package com.jishi.service;

import com.jishi.common.R;
import com.jishi.entity.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 23049
* @description 针对表【shopping_cart(购物车)】的数据库操作Service
* @createDate 2023-01-06 16:02:13
*/
public interface ShoppingCartService extends IService<ShoppingCart> {

    public R<ShoppingCart> add(ShoppingCart shoppingItem, HttpServletRequest request);

    public R<List<ShoppingCart>> sub(ShoppingCart shoppingItem, HttpServletRequest request);

    public R<List<ShoppingCart>>  shoppingCartList(HttpServletRequest request);

    public R remove(HttpServletRequest request);



}
