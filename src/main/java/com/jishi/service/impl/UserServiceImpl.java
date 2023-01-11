package com.jishi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.common.R;
import com.jishi.common.ThreadUtil;
import com.jishi.common.utils.ValidateCodeUtils;
import com.jishi.entity.User;
import com.jishi.service.UserService;
import com.jishi.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
* @author 23049
* @description 针对表【user(用户信息)】的数据库操作Service实现
* @createDate 2023-01-05 19:28:27
*/

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{


    @Autowired
    private StringRedisTemplate redisTemplate;

    //这里利用工具生成四位数验证码，本来应该利用腾讯云发送给
    //用户手机，但是无法备案，因此先在控制台查看验证码进行测试
    //项目优化，使用redis进行存储验证码
    @Override
    public R<String> smsCode(Map<String,String> map,HttpServletRequest request) {


        //利用工具类直接生成四位数验证码
        Integer smsCode = ValidateCodeUtils.generateValidateCode(4);
        //把验证码存入redis中存储（手机号做key,code做value）,有效期设置为5分钟
        redisTemplate.opsForValue().set(map.get("phone"),String.valueOf(smsCode),5, TimeUnit.MINUTES);

        //这里应该是调用腾讯云SMS Api传入map.get("phone")向指定手机号发送验证码
        System.out.println(smsCode);

        return R.success(null);
    }

    //手机号登录方法，如果没有登录直接帮助用户注册
    @Override
    public R login(Map<String,Object> map, HttpServletRequest request) {



        String smsCode =(String)map.get("code");

        String phoneNumber = (String) map.get("phone");



        if(smsCode==null||phoneNumber==null)
            return R.error("请将手机号和验证码输入完整！");


        if (!smsCode.equals(redisTemplate.opsForValue().get(phoneNumber)))
                return  R.error("验证码输入错误，请重新输入！");


        //如果账户不存在，直接帮用户注册
       if(this.count(new LambdaQueryWrapper<User>()
               .eq(User::getPhone,phoneNumber))==0)
       {
           //调用注册方法
           this.register(phoneNumber,request);
           redisTemplate.delete(phoneNumber);
           return  R.success(null);
       }
       else
       {
           //如果账户存在，把userId存入Session中
           Long userId = this.getOne(new LambdaQueryWrapper<User>()
                   .eq(User::getPhone, phoneNumber)).getId();
           request.getSession().setAttribute("user",userId);
           redisTemplate.delete(phoneNumber);
           return R.success(null);
       }

    }

    //注册用户方法
    @Override
    public void register(String phoneNumber,HttpServletRequest request) {

        User user = new User();
        user.setPhone(phoneNumber);
        this.save(user);
        request.getSession().setAttribute("user",user.getId());
    }


    //用户登出方法
    @Override
    public R loginout(HttpServletRequest request) {

        request.getSession().removeAttribute("user");
        return R.success(null);
    }
}




