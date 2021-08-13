package com.pioneer.common.exception;

/**
 * 自定义异常
 *
 * @author hlm
 * @date 2021-08-09 17:45:27
 */
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = -5198969658351291908L;

    private final String message;

    public CustomException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
