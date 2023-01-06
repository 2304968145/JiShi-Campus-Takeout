package com.jishi.common;

import com.jishi.common.Exception.BussinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class GloblExceptionHandle {


    //捕获用户重名异常进行处理（直接捕获对应异常，自己不设置异常这也是一种小思路）
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public  R<String> SqlExceptionHandle(SQLIntegrityConstraintViolationException ex) {

          String str= ex.getMessage();
        //Duplicate entry是从异常信息中截取到的关键字
        if (str.contains("Duplicate entry")){

            String[] strings = str.split(" ");

            //从SQL重复数据异常的异常信息提取到重复的值，统一处理所有数据库重复异常
            return R.error( strings[2].substring(1,strings[2].length()-1)+"已存在");

        }

        ex.printStackTrace();
        return R.error("数据库错误异常！由Spring统一异常处理器处理");
    }

    @ExceptionHandler
    public R BussinessExceptionHandl(BussinessException bussinessException){

        bussinessException.printStackTrace();
        return  R.error(bussinessException.getMessage());
    }

    @ExceptionHandler
    public R exception(Exception ex){

        ex.printStackTrace();
        return  R.error("您的网络缓慢，请稍后再试！");
    }


}
