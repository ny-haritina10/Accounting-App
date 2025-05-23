package mg.module.accounting.dto;

public class LoginResponse {
    
    private String token;
    private String userName;

    public LoginResponse(String token, String userName) {
        this.token = token;
        this.userName = userName;
    }

    // getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}