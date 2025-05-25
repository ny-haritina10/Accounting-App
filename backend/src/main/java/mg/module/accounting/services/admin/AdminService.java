package mg.module.accounting.services.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.CreateUserRequest;
import mg.module.accounting.dto.RoleAssignmentRequest;
import mg.module.accounting.dto.UserRoleResponse;
import mg.module.accounting.models.Role;
import mg.module.accounting.models.User;
import mg.module.accounting.models.UserRole;
import mg.module.accounting.repositories.RoleRepository;
import mg.module.accounting.repositories.UserRepository;
import mg.module.accounting.repositories.UserRoleRepository;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public ApiResponse<User> createUser(CreateUserRequest request) {
        // check if username exists
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists", null);
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        userRepository.save(user);

        return ApiResponse.success(user, "User created successfully");
    }

    @Transactional
    public ApiResponse<String> assignRoles(RoleAssignmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", null));

        // verify all roles exist
        for (Long roleId : request.getRoleIds()) {
            if (!roleRepository.existsById(roleId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role ID " + roleId + " not found", null);
            }
        }

        userRoleRepository.deleteByIdUser(request.getUserId());

        // assign new roles
        for (Long roleId : request.getRoleIds()) {
            UserRole userRole = new UserRole();
            userRole.setIdUser(user.getId());
            userRole.setIdRole(roleId);
            userRoleRepository.save(userRole);
        }

        return ApiResponse.success(null, "Roles assigned successfully");
    }

    public ApiResponse<List<UserRoleResponse>> getUserRoles(Long userId) {
        // verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", null));

        List<UserRole> userRoles = userRoleRepository.findByIdUser(userId);
        List<UserRoleResponse> response = userRoles.stream().map(userRole -> {
            UserRoleResponse userRoleResponse = new UserRoleResponse();
            userRoleResponse.setId(userRole.getId());
            userRoleResponse.setUserId(userRole.getIdUser());
            userRoleResponse.setUserName(user.getUserName());
            userRoleResponse.setRoleId(userRole.getIdRole());
            Role role = roleRepository.findById(userRole.getIdRole())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found", null));
            userRoleResponse.setRoleLabel(role.getLabel());
            userRoleResponse.setAssignedAt(userRole.getAssignedAt());
            return userRoleResponse;
        }).collect(Collectors.toList());

        return ApiResponse.success(response, "User roles retrieved successfully");
    }

    @Transactional
    public ApiResponse<String> removeRole(Long userId, Long roleId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", null);
        }

        if (!roleRepository.existsById(roleId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found", null);
        }

        userRoleRepository.deleteByIdUserAndIdRole(userId, roleId);
        return ApiResponse.success(null, "Role removed successfully");
    }
}