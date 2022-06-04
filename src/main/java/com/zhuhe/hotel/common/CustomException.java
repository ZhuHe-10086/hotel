package com.zhuhe.hotel.common;

/**
 * 自定义异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String massage){
        super(massage);
    }
}
