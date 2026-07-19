package com.easyphoto.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，统一返回 Result 结构。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException e) {
        return ResponseEntity.status(e.getStatus())
                .body(Result.error(e.getStatus(), e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Void>> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.error(HttpStatus.FORBIDDEN.value(), "禁止访问"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException e) {
        String msg = "参数校验失败";
        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError != null) {
            msg = fieldError.getDefaultMessage();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Result.error(HttpStatus.BAD_REQUEST.value(), msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleOther(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "服务器内部错误: " + e.getMessage()));
    }
}
