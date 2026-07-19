package com.easyphoto.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结构。
 * code：0 成功，非 0 表示业务/系统错误。
 */
@Data
public class Result<T> implements Serializable {

    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "ok", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(0, "ok", null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
