package com.jishi.mapper;

import com.jishi.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author 23049
* @description 针对表【dish(菜品管理)】的数据库操作Mapper
* @createDate 2023-01-03 21:59:37
* @Entity com.jishi.entity.Dish
*/
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    //查询包含该DishId集合的起售状态的套餐的总数
    @Select(value = "select count(*) from setmeal  where id in(select distinct setmeal_id from " +
            "setmeal_dish where dish_id in #{ids}) and status =1")
    public  int  selectSetmealStatusCount(List<Long> ids);

    //当集合大小为1时调用   查询包含该DishId集合的起售状态的套餐的总数
    //动态Sql判断集合大小不会写
    @Select(value = "select count(*) from setmeal  where id in(select distinct setmeal_id from " +
            "setmeal_dish where dish_id = #{ids}) and status =1")
    public  int  selectSetmealStatusSingleCount(List<Long> ids);

}




