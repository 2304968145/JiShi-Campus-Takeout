package com.jishi.service;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jishi.common.R;
import com.jishi.entity.Employee;

import javax.servlet.http.HttpServletRequest;

/**
* @author 23049
* @description 针对表【employee(员工信息)】的数据库操作Service
* @createDate 2023-01-02 00:19:03
*/
public interface EmployeeService extends IService<Employee> {


    public R login(HttpServletRequest request,Employee employee);

    public  R<String> logOut(HttpServletRequest request);

    public  R<String>  add(HttpServletRequest request,Employee employee);
    public  R<Page<Employee>> pageSelect(int page, int pageSize, String name);

    public  R update(HttpServletRequest request,Employee employee);
}
