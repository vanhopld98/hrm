package vn.com.humanresourcesmanagement.common.exception;

import java.io.Serializable;

@SuppressWarnings("all")
public class AuthenticationException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private Integer statusCode;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AuthenticationException() {
        super("Thông tin truy cập không hợp lệ, vui lòng đăng nhập lại.");
    }

    public Integer getStatusCode() {
        return statusCode;
    }

}
