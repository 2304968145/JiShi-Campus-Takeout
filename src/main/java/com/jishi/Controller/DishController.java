package com.jishi.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.dto.DishDto;
import com.jishi.dto.SetmealDto;
import com.jishi.entity.Dish;
import com.jishi.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
//操作Dish表，同时DishFlover也在这里操作
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public R addDishWithFlover(@RequestBody DishDto dishDto){

        return  dishService.addDishWithFlavor(dishDto);

    }

    @DeleteMapping
    public R deleteDish(@RequestParam List<Long> ids){

        return dishService.deleteDish(ids);
    }

    @GetMapping("/page")
    public  R<Page<DishDto>> dishSelect(Integer page, Integer pageSize, String name){

      return   dishService.dishSelect(page,pageSize,name);
    }



    @PutMapping
    public R updateDishWithFlavor(@RequestBody DishDto dishDto){

        return dishService.updateDishWithFlavor(dishDto);
    }

    @GetMapping("/{dishId}")
    public R<DishDto> updateShow(@PathVariable  Long dishId){

        return  dishService.updateShow(dishId);
    }

    @GetMapping("/list")
    public R<List<DishDto>> selectDishByCategory( Long categoryId,String name){

        return  dishService.selectDishByCategory(categoryId,name);
    }

    @PostMapping("/status/0")
    public  R  stopSale(@RequestParam List<Long> ids){
        return dishService.stopSale(ids);
    }

    @PostMapping("/status/1")
    public R startSale(@RequestParam List<Long> ids){
        return  dishService.startSale(ids);
    }

}
