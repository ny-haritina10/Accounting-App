package mg.module.accounting.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.CreateUserRequest;
import mg.module.accounting.dto.RoleAssignmentRequest;
import mg.module.accounting.dto.UserRoleResponse;
import mg.module.accounting.models.User;
import mg.module.accounting.services.admin.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @PostMapping("/roles/assign")
    public ResponseEntity<ApiResponse<String>> assignRoles(@RequestBody RoleAssignmentRequest request) {
        return ResponseEntity.ok(adminService.assignRoles(request));
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse<List<UserRoleResponse>>> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserRoles(userId));
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    public ResponseEntity<ApiResponse<String>> removeRole(@PathVariable Long userId, @PathVariable Long roleId) {
        return ResponseEntity.ok(adminService.removeRole(userId, roleId));
    }
}