package com.jishi.mapper;

import com.jishi.entity.SetmealDish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【setmeal_dish(套餐菜品关系)】的数据库操作Mapper
* @createDate 2023-01-05 10:51:37
* @Entity com.jishi.entity.SetmealDish
*/

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {

}




