package com.jishi.mapper;

import com.jishi.entity.DishFlavor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Mapper
* @createDate 2023-01-04 13:43:15
* @Entity com.jishi.entity.DishFlavor
*/

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {

}




