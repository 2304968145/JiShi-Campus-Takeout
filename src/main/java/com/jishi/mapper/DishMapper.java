package com.jishi.mapper;

import com.jishi.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【dish(菜品管理)】的数据库操作Mapper
* @createDate 2023-01-03 21:59:37
* @Entity com.jishi.entity.Dish
*/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}




