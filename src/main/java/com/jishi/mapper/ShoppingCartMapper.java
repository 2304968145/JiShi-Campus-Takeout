package com.jishi.mapper;

import com.jishi.entity.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【shopping_cart(购物车)】的数据库操作Mapper
* @createDate 2023-01-06 16:02:13
* @Entity com.jishi.entity.ShoppingCart
*/

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {

}




