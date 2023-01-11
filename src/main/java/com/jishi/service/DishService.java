package com.jishi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.dto.DishDto;
import com.jishi.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 23049
* @description 针对表【dish(菜品管理)】的数据库操作Service
* @createDate 2023-01-03 21:59:37
*/
public interface DishService extends IService<Dish> {


    public R updateDishWithFlavor(DishDto dishDto);

    //修改菜品数据回显功能
    R<DishDto> updateShow(Long dishId);

    public R addDishWithFlavor(DishDto dishDto);

    public  R<Page<DishDto>> dishSelect(Integer page, Integer pageSize, String name);

    public  R<List<DishDto>> selectDishByCategory(Long categoryId,String name);

    public  R deleteDish(List<Long> ids);

    public  R  stopSale(List<Long> ids);

    public R startSale(List<Long> ids);

}
