package com.jishi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.dto.DishDto;
import com.jishi.dto.SetmealDto;
import com.jishi.entity.Dish;
import com.jishi.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 23049
* @description 针对表【setmeal(套餐)】的数据库操作Service
* @createDate 2023-01-03 21:59:24
*/
public interface SetmealService extends IService<Setmeal> {


    public R addSetmealWithSetmealDish(SetmealDto setmealDto);

    public  R<Page<SetmealDto>> setmealPageSelect(Integer page, Integer pageSize, String name);

    public  R deleteSetmeal(List<Long> ids);

    public  R<List<Setmeal>>  selectSetmelByCategoryId(Long categoryId,Integer status);

    public  R  stopSale(List<Long> ids);

    public R startSale(List<Long> ids);

    public  R update(SetmealDto setmealDto);

    public R<SetmealDto> updateShow(Long id);

}
