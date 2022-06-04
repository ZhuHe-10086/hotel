package com.zhuhe.hotel.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

//表示有类上添加了RestController等注解，就会被拦截到这里
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 这个是如果拦截的类中有这个异常，就在这里处理
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        String message = exception.getMessage();
        log.error(message);
//        return R.error("保存失败，员工可能已存在");这种太草率了，需要更加细致
        if(message.contains("Duplicate entry")){
            //说明这个员工已经存在了
            String[] split = message.split("'");
            return R.error(split[1]+"已存在");
        }
        return R.error("未知错误，请重试");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception){
        String message = exception.getMessage();
        return R.error(message);
    }

}
