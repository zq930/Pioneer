package com.pioneer.framework.web.exception;

import cn.hutool.http.HttpStatus;
import com.pioneer.common.core.domain.AjaxResult;
import com.pioneer.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Objects;

/**
 * 全局异常处理器
 *
 * @author hlm
 * @date 2021-08-09 17:49:59
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public AjaxResult businessException(CustomException e) {
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public AjaxResult handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(HttpStatus.HTTP_NOT_FOUND, "路径不存在，请检查路径是否正确");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage());
        return AjaxResult.error(HttpStatus.HTTP_FORBIDDEN, "没有权限，请联系管理员授权");
    }

    @ExceptionHandler(AccountExpiredException.class)
    public AjaxResult handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public AjaxResult handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e) {
        log.error(e.getMessage(), e);
        return AjaxResult.error("系统错误，请联系管理员");
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult validatedBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        return AjaxResult.error(message);
    }
}
