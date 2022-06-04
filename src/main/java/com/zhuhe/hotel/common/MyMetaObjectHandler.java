package com.zhuhe.hotel.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 元数据对象处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
//        log.info("公共字段创建自动填充");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContest.getId());
        metaObject.setValue("updateUser", BaseContest.getId());
        log.info(metaObject.toString());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        log.info("公共字段修改时自动填充");
        long id = Thread.currentThread().getId();
        log.info("线程id："+id);
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContest.getId());
        log.info(metaObject.toString());
    }
}
