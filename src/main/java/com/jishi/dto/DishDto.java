package com.jishi.dto;


import com.jishi.entity.Dish;
import com.jishi.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    //私有属性也会被继承，子类无法直接访问，但能通过父类的Public方法访问
    private List<DishFlavor> flavors = new ArrayList<>();

    //用于Dish页面分页查询展示菜品种类（dish表中
    // 只有分类id字段，所以在Dto中额外添加种类名字
    // 同时在后端查询数据库为其赋值）
    private String categoryName;

    private Integer copies;
}
