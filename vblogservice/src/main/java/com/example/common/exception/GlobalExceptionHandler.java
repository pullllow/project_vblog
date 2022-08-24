package com.example.common.exception;

import com.example.common.lang.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

/**
 * @author Chang Qi
 * @date 2022/8/20 9:29
 * @description
 * @Version V1.0
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     *  捕获shiro异常
     *
     * @param e
     * @return com.example.common.lang.RestResponse
     *
     **/
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public RestResponse handle401(ShiroException e) {
        log.error("Shiro 捕获异常：-------------------->{}",e.getMessage());
        return RestResponse.fail(401,e.getMessage(),null);
    }

    /**
     * 处理Assert异常
     *
     * @param e
     * @return com.example.common.lang.RestResponse
     *
     **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public RestResponse handle(IllegalArgumentException e) {
        log.error("Assert 异常：-------------------->{}",e.getMessage());
        return RestResponse.fail(e.getMessage());
    }

    /**
     * @Validated 校验错误异常处理
     *
     * @param e
     * @return com.example.common.lang.RestResponse
     *
     **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestResponse handle(MethodArgumentNotValidException e) {
        log.error("运行时异常：-------------------->{}",e.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        return RestResponse.fail(objectError.getDefaultMessage());
    }

    /**
     *
     *
     * @param e
     * @return com.example.common.lang.RestResponse
     *
     **/
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public RestResponse handler(RuntimeException e) throws IOException {
        log.error("运行时异常:-------------->",e);
        return RestResponse.fail(e.getMessage());
    }









}
