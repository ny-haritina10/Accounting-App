package mg.module.accounting.services.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import mg.module.accounting.api.ApiResponse;
import mg.module.accounting.dto.LoginRequest;
import mg.module.accounting.dto.LoginResponse;
import mg.module.accounting.models.User;
import mg.module.accounting.repositories.UserRepository;
import mg.module.accounting.utils.JwtUtil;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        // find user by username
        User user = userRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                        "Invalid credentials", null));

        // verify password
        if (!passwordEncoder.matches(loginRequest.getUserPassword(), user.getUserPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                    "Invalid credentials", null);
        }

        // generate JWT token
        String token = jwtUtil.generateToken(user.getUserName());
        LoginResponse loginResponse = new LoginResponse(token, user.getUserName());

        return ApiResponse.success(loginResponse, "Login successful");
    }
}