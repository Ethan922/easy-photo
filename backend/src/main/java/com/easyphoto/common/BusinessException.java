package com.easyphoto.common;

/**
 * 业务异常。携带 HTTP 状态码用于全局处理。
 */
public class BusinessException extends RuntimeException {

    private final int status;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
