package com.jishi.Controller;

import com.jishi.common.R;
import com.jishi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/sendMsg")
    public R<String> smsCode(@RequestBody Map<String,String> map, HttpServletRequest request){


        return userService.smsCode(map,request);
    }

    @PostMapping("/login")
    public R login(@RequestBody Map<String,Object> map, HttpServletRequest request){

        return  userService.login(map,request);
    }
}
