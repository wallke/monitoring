package com.xwtech.jobs.controller.global;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

/**
 * 统一异常处理
 * Created by zq on 16/7/26.
 */
@ControllerAdvice
public class GlobalExceptionController {


    final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 服务器其他异常
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleCustomException(Exception ex,
                                                              HttpServletRequest request) {

        logger.error("请求地址:",request.getRequestURI());
        logger.error("发生异常",ex);
        ResponseEntity<String> responseEntity
                = new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        return responseEntity;

    }


    /**
     * 参数验证异常
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<String> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        logger.error("请求地址:",request.getRequestURI());
        logger.error("校验异常",ex);

        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = "Invalid Request:";

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage +=
                    MessageFormat.format("{}-{};",fieldError.getField(),fieldError.getDefaultMessage());
        }
        logger.error("校验异常",errorMessage);
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);


        return responseEntity;
    }

    /**
     * HttpMessage 序列化异常
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<String> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        logger.error("请求地址:",request.getRequestURI());
        logger.error("发生异常",ex);

        ResponseEntity<String> responseEntity
                = new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        return responseEntity;
    }

}
