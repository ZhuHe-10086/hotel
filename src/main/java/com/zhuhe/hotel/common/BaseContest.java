package com.zhuhe.hotel.common;

/**
 * 基于ThreadLocal封装的一个工具类，用于用户保存和获取当前的登录的id
 */
public class BaseContest {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setId(Long id){
        threadLocal.set(id);
    }

    public static Long getId(){
        return threadLocal.get();
    }
}
