package mg.module.accounting.services.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
                .orElse(null);
        if (user == null) {
            return ApiResponse.error("Invalid credentials", "INVALID_CREDENTIALS");
        }

        // verify password
        if (!passwordEncoder.matches(loginRequest.getUserPassword(), user.getUserPassword())) {
            return ApiResponse.error("Invalid credentials", "INVALID_CREDENTIALS");
        }

        // generate JWT token
        String token = jwtUtil.generateToken(user.getUserName());
        LoginResponse loginResponse = new LoginResponse(token, user.getUserName());

        return ApiResponse.success(loginResponse, "Login successful");
    }

    public ApiResponse<String> logout(String token) {
        if (!jwtUtil.validateToken(token)) {
            return ApiResponse.error("Invalid token", "INVALID_TOKEN");
        }

        return ApiResponse.success(null, "Logout successful");
    }
}