package com.jishi.filter;


import com.alibaba.fastjson.JSON;
import com.jishi.common.R;
import com.jishi.common.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//启动类加上@ServletComponentScan
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter  implements Filter {

    //Spring提供的路径匹配器，支持通配符
    public  static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


        @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


            Long employeeId = (Long) request.getSession().getAttribute("employee");

            Long userId = (Long) request.getSession().getAttribute("user");
            //先判断用户有无登录，登录了直接放行

        if(employeeId!=null)
        {
            //存入线程存储中，方便MP自动填充调用id
            ThreadUtil.setCurrentId(employeeId);
            filterChain.doFilter(request,response);
            log.info("用户已经登陆，直接放行");
            return;
        }

        //应该再写一个过滤器类，否则这样直接写用户登录后也可以直接访问后台了
            if(userId!=null)
            {
                //存入线程存储中，方便MP自动填充调用id
                ThreadUtil.setCurrentId(userId);
                filterChain.doFilter(request,response);
                log.info("用户已经登陆，直接放行");
                return;
            }


        //Uri相对路径,url绝对路径
        String uri = request.getRequestURI();
        //访问login.html    相对路径就是/backend/page/login/login.html

        //设置放行路径，静态资源都放行没关系，
        // 动态资源被拦截，数据不会展现出来
            //动态资源放行前后端登录的相关访问
         String[]  strs = { "/backend/**",
                           "/front/**",
                            "/employee/login",
                           "/employee/logout",
                            "user/sendMsg",
                            "user/login"
                            };

        for (String str : strs) {
            //第一个参数放行路径（带通配符），第二个参数访问路径
            if (PATH_MATCHER.match(str,uri));
            {       //匹配上放行
               filterChain.doFilter(request,response);
                System.out.println(str);
                System.out.println(uri);
                log.info("路径匹配成功，直接放行");
               return;

            }
        }

       //没匹配上，跳转到登录页面
        //前端设置有响应拦截器，接收到指定msg自动跳转Login.html
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("用户未登录且路径匹配错误，通知前端跳转");

    }


}
