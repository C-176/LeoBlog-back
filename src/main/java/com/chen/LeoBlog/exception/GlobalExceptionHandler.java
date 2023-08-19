package com.chen.LeoBlog.exception;

import com.chen.LeoBlog.base.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * validation参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultInfo<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x ->
                errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String message = errorMsg.toString();
        log.info("validation parameters error！The reason is:{}", message);
        return ResultInfo.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), message.substring(0, message.length() - 1));
    }

    /**
     * validation参数校验异常
     */
    @ExceptionHandler(value = BindException.class)
    public ResultInfo<?> bindExceptionHandler(BindException e) {
        StringBuilder errorMsg = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(x -> errorMsg.append(x.getField()).append(x.getDefaultMessage()).append(","));
        String message = errorMsg.toString();
        log.info("validation parameters error！The reason is:{}", message);
        return ResultInfo.fail(CommonErrorEnum.PARAM_VALID.getErrorCode(), message.substring(0, message.length() - 1));
    }

    /**
     * 处理空指针的异常
     */
    @ExceptionHandler(value = NullPointerException.class)
    public ResultInfo<?> exceptionHandler(NullPointerException e) {
        log.error("null point exception！The reason is: ", e);
        return ResultInfo.fail(CommonErrorEnum.SYSTEM_ERROR);
    }

    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResultInfo<?> systemExceptionHandler(Exception e) {
        log.error("system exception！The reason is：{}", e.getMessage(), e);
        return ResultInfo.fail(HttpErrorEnum.UNAUTHORIZED);
    }

    /**
     * 自定义校验异常（如参数校验等）
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResultInfo<?> businessExceptionHandler(BusinessException e) {
        log.info("business exception！The reason is：{}", e.getMessage(), e);
        return ResultInfo.fail(e.getErrorCode(), e.getMessage());
    }

    /**
     * http请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultInfo<?> handleException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return ResultInfo.fail(CommonErrorEnum.BAD_REQUEST);
    }

    /**
     * 限流异常
     */
    @ExceptionHandler(value = FrequencyControlException.class)
    public ResultInfo<?> frequencyControlExceptionHandler(FrequencyControlException e) {
        log.info("frequencyControl exception！The reason is：{}", e.getMessage(), e);
        return ResultInfo.fail(e.getErrorCode(), e.getMessage());
    }
}
