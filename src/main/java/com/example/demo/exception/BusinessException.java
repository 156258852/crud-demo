package com.example.demo.exception;

/**
 * 业务异常 - 用于业务逻辑验证失败等场景
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}