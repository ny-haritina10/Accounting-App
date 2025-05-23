package mg.module.accounting.api;

public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private String message;
    private String errorCode;

    // constructor for successful response
    public ApiResponse(T data, String message) {
        this.success = true;
        this.data = data;
        this.message = message;
        this.errorCode = null;
    }

    // constructor for error response
    public ApiResponse(String message, String errorCode) {
        this.success = false;
        this.data = null;
        this.message = message;
        this.errorCode = errorCode;
    }

    // static factory methods for convenience
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(message, errorCode);
    }

    // getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}