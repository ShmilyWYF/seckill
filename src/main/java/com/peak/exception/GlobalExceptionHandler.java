package com.peak.exception;

import com.peak.httpUiltr.HttpEnum;
import com.peak.httpUiltr.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseResult<Exception> ExceptionHandler(Exception e){
        if(e instanceof GlobalException ex){           //捕获运行时的异常
            if(ex.getMsg()!=null){
                return ResponseResult.failed(ex.getHttpEnum(),ex.getMsg());
            }
            return ResponseResult.failed(ex.getHttpEnum());
        }else if(e instanceof BindException ex){       //捕获校验绑定参数抛出的异常
            return ResponseResult.failed(HttpEnum.ERROR_600,ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        }
        return ResponseResult.failed(HttpEnum.ERROR_500,e.toString()+"全局异常处理抛出的异常");
    }
}


