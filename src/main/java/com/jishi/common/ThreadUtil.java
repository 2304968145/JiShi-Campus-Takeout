package com.jishi.common;

//设置一个工具类用来调用ThreadLocal，其实感觉直接定义静态在Filter也行
//获取到用户ID
public class ThreadUtil {

    public static  ThreadLocal threadLocal = new ThreadLocal();

    public  static  void  setCurrentId(Long currentId){
        threadLocal.set(currentId);
    }

    public  static  Long getCurrentId(){

        return (Long) threadLocal.get();
    }

}
