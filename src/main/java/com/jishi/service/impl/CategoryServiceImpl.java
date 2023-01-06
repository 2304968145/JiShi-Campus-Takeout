package com.jishi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.common.R;
import com.jishi.entity.Category;
import com.jishi.entity.Dish;
import com.jishi.entity.Setmeal;
import com.jishi.mapper.DishMapper;
import com.jishi.mapper.SetmealMapper;
import com.jishi.service.CategoryService;
import com.jishi.mapper.CategoryMapper;
import com.jishi.service.DishService;
import com.jishi.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;

/**
* @author 23049
* @description 针对表【category(菜品及套餐分类)】的数据库操作Service实现
* @createDate 2023-01-03 19:48:00
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{


    //新增套餐功能
    @Autowired
    private  CategoryMapper mapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    public R add(@RequestBody Category category){

        mapper.insert(category);

        return  R.success(category);
    }

    //套餐分类分页查询功能
    public R<Page<Category>> pageSelect(int page,int pageSize){

        Page<Category> pageSelect = new Page<>(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        mapper.selectPage(pageSelect,queryWrapper);


        return R.success(pageSelect);
    }

    //删除套餐,删除前先检查菜品和套餐中是否还有属于此分类的产品，如果有，禁止删除
    public  R  delete(Long id){

        //这里老师不直接返回R.error而是抛出一个自定义业务异常，在统一异常处理抛出R.error，麻烦很多
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
       if( dishMapper.selectCount( dishWrapper.eq(Dish::getCategoryId,id))>0)
           return R.error("该分类中还含有存在的菜品，禁止删除！");

        LambdaQueryWrapper<Setmeal> setMealWrapper = new LambdaQueryWrapper<>();
        if (setmealMapper.selectCount(setMealWrapper.eq(Setmeal::getCategoryId,id))>0)
            return R.error("该分类中还含有存在的套餐，禁止删除！");

        mapper.deleteById(id);

        return  R.success(null);

    }

    //修改分类信息
    public  R   updateCategory(Category category){
        mapper.updateById(category);
        return R.success(null);

    }


    public R<List<Category>> categorySelect(Integer type){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null,Category::getType,type)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime);

              return   R.success(mapper.selectList(queryWrapper)) ;
    }

}




