package com.jishi.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jishi.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【employee(员工信息)】的数据库操作Mapper
* @createDate 2023-01-02 00:19:03
* @Entity .entity.Employee
*/

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}




