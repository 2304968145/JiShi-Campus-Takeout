package com.jishi.service;

import com.jishi.common.R;
import com.jishi.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
* @author 23049
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2023-01-05 19:28:27
*/
public interface UserService extends IService<User> {

        public R<String> smsCode(Map<String,String> map,HttpServletRequest request);

        public R login(Map<String,Object> map,HttpServletRequest request);

        public  void  register(String phoneNumber,HttpServletRequest request);

}
