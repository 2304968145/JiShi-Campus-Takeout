package com.jishi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.entity.DishFlavor;
import com.jishi.service.DishFlavorService;
import com.jishi.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author 23049
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2023-01-04 13:43:15
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




