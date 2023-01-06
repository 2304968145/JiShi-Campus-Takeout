package com.jishi.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;


//MP公共资源赋值设置
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {

        metaObject.setValue("createTime", LocalDateTime.now());
        //从线程存储中取出
        metaObject.setValue("createUser",ThreadUtil.getCurrentId());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",ThreadUtil.getCurrentId());

    }

    @Override
    public void updateFill(MetaObject metaObject) {

        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",ThreadUtil.getCurrentId());

    }
}
