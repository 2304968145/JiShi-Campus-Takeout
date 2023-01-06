package com.jishi.mapper;

import com.jishi.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【user(用户信息)】的数据库操作Mapper
* @createDate 2023-01-05 19:28:27
* @Entity com.jishi.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




