package com.jishi.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.entity.Employee;
import com.jishi.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    //登录功能
    @PostMapping("/login")
    private R login(HttpServletRequest request, @RequestBody Employee employee) {

        return service.login(request, employee);

    }

    @PostMapping("/logout")
    private R<String> logout(HttpServletRequest request){

        return  service.logOut(request);
    }



    //添加后台界面用户（这里管理员和后台用户权限没区分开，都在一张表中）
    @PostMapping
    private   R<String>  add(HttpServletRequest request,@RequestBody Employee employee){

           return service.add(request,employee);
    }

    @GetMapping("/page")
    public R<Page<Employee>> pageSelect(int page, int pageSize, String name){

        return  service.pageSelect(page,pageSize,name);
    };

    @PutMapping
    public  R update(HttpServletRequest request,@RequestBody Employee employee){

        return  service.update(request,employee);
    }

    //通过id查询用于回显修改界面数据，前端点击保存直接调用update方法
    @GetMapping("/{id}")
    public  R getById(@PathVariable Long id){

        Employee employee = service.getById(id);
        if (employee==null)
            return R.error("用户不存在！");

        return R.success(employee);
    }
}
