package com.jishi.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.jishi.common.R;
import com.jishi.entity.Employee;
import com.jishi.mapper.EmployeeMapper;
import com.jishi.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Date;

import static com.jishi.common.R.error;

/**
* @author 23049
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2023-01-02 00:19:03
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService {


    @Autowired
    EmployeeMapper mapper;


    //登录功能
    @Override
    public R login(HttpServletRequest request, Employee employee) {

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();

        //Spring提供的md5加密方法
        String passMd5 = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());

        wrapper.eq(Employee::getUsername,employee.getUsername())
                .eq(Employee::getPassword,passMd5);

        Employee one = mapper.selectOne(wrapper);

        if (one==null)
            return  R.error("账号名或密码错误，请重新输入！");

        //数据库记录的status状态
        if(one.getStatus()!=1)
            return  R.error("账号已被禁用，请联系管理员！");

        //存储到Sessison中
        request.getSession().setAttribute("employee",one.getId());
        return  R.success(one);

    }

    //账户主动退出功能
    @Override
    public R<String> logOut(HttpServletRequest request) {

        //账号退出清除服务器session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    //添加用户
    @Override
    public R<String> add(HttpServletRequest request, Employee employee) {

        //由MP公共字段自动插入完成
  /*      employee.setCreateTime(new Date());
        employee.setUpdateTime(new Date());*/
        //设置初始默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //获取当前操作人id
       Long  adminId=(Long) request.getSession().getAttribute("employee");

      /*  employee.setCreateUser(adminId);
        employee.setUpdateUser(adminId);*/


        mapper.insert(employee);

        return  R.success("新增员工成功");
    }

    //分页查询功能
    @Override
    public  R<Page<Employee>> pageSelect(int page,int pageSize,String name){

        //设置查询页码和分页大小
        Page<Employee> pageInfo = new Page<>(page,pageSize);

        //设置查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name)
                .orderByDesc(Employee::getCreateTime);

        mapper.selectPage(pageInfo,queryWrapper);

        //查询后会自动赋值给pageinfo对象，无需返回值
        return  R.success(pageInfo);


    }

    //更新用户信息和启用/禁用账号功能集成到这一个方法中
    @Override
    public R update(HttpServletRequest request, Employee employee) {


       /* employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employee.setUpdateTime(new Date());*/

        //MP会根据值的null自动生成语句只修改非null数据
        mapper.updateById(employee);
        return R.success("修改成功！");
    }
}




