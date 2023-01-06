package com.jishi.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.entity.Category;
import com.jishi.service.CategoryService;
import com.jishi.service.DishService;
import com.jishi.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService service;


    @PostMapping
    public R add(@RequestBody Category category){
        return service.add(category);
    }

    @GetMapping("/page")
    public R<Page<Category>> pageSelect(int page, int pageSize){

        return  service.pageSelect(page,pageSize);
    }

    @DeleteMapping
    //这里只是删除单个套餐功能，但是不知道为什么前端传了ids进来
    //所以进行了一下转换
    public  R  delete(@RequestParam(value = "ids") Long id){

        return  service.delete(id);
    }

    @PutMapping
    public  R   updateCategory(@RequestBody Category category){

        return service.updateCategory(category);
    }

    @GetMapping("/list")
    public R<List<Category>> categorySelect(Integer type){

            return service.categorySelect(type);
    }

}
