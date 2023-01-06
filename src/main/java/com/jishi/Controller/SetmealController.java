package com.jishi.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.dto.SetmealDto;
import com.jishi.entity.Setmeal;
import com.jishi.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    public R addSetmealWithSetmealDish(@RequestBody SetmealDto setmealDto){

        return  setmealService.addSetmealWithSetmealDish(setmealDto);
    }


    @GetMapping("/page")
    public R<Page<SetmealDto>> setmealPageSelect(Integer page, Integer pageSize, String name){

        return  setmealService.setmealPageSelect(page,pageSize,name);
    }

    @DeleteMapping
    public R deleteSetmeal(@RequestParam List<Long> ids){

       return setmealService.deleteSetmeal(ids);
    }

    @GetMapping("/list")
    public R<List<Setmeal>> selectSetmelByCategoryId(Long categoryId, Integer status){

        return  setmealService.selectSetmelByCategoryId(categoryId,status);
    }
}
