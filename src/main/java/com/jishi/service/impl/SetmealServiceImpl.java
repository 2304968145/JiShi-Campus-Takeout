package com.jishi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.common.Exception.BussinessException;
import com.jishi.common.R;
import com.jishi.dto.DishDto;
import com.jishi.dto.SetmealDto;
import com.jishi.entity.Dish;
import com.jishi.entity.DishFlavor;
import com.jishi.entity.Setmeal;
import com.jishi.entity.SetmealDish;
import com.jishi.service.CategoryService;
import com.jishi.service.SetmealDishService;
import com.jishi.service.SetmealService;
import com.jishi.mapper.SetmealMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 23049
* @description 针对表【setmeal(套餐)】的数据库操作Service实现
* @createDate 2023-01-03 21:59:24
*/
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
    implements SetmealService{

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    @Transactional
    @Override
    //新增套餐功能
    public R addSetmealWithSetmealDish(SetmealDto setmealDto) {


        //框架自动识别
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();



        //使用stream流方法给每个flover赋上对应的dish_id
        List<SetmealDish> setmealDishList = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            return setmealDish;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishList);
        return R.success(null);

    }

    //套餐管理页面的分页查询，原理同菜品页面的分页查询
    @Override
    public R<Page<SetmealDto>> setmealPageSelect(Integer page, Integer pageSize, String name) {

            //Dish分页查询
            Page<Setmeal> pageInfo = new Page<>(page,pageSize);
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(name!=null,Setmeal::getName,name)
                    .orderByDesc(Setmeal::getCreateTime);

            Page<Setmeal> setmealPage = this.page(pageInfo,queryWrapper);
            Page<SetmealDto> setmealDtoPage = new Page<>();

            //对象拷贝工具,忽略records属性的拷贝
            BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

            List<SetmealDto> records = setmealPage.getRecords().stream().map(setmeal -> {

                String categoryName="";
                //通过setmeal的分类ID查询对应的种类名称赋值给CategryName字段（前端需要）
                if(setmeal.getCategoryId()!=null)
                    categoryName = categoryService.getById(setmeal.getCategoryId())
                            .getName();

                //通过对象拷贝实现了父类向子类的强转
                SetmealDto setmealDto = new SetmealDto();
                BeanUtils.copyProperties(setmeal,setmealDto);
                setmealDto.setCategoryName(categoryName);

                return setmealDto;
            }).collect(Collectors.toList());

         setmealDtoPage.setRecords(records);

            return  R.success(setmealDtoPage);
        }

    @Override
    @Transactional
    //套餐管理界面中删除对应套餐功能
    public R deleteSetmeal(List<Long> ids) {


         if (ids.isEmpty())
            return R.error("请先选中要删除的套餐！");


        //先进行判断，如果套餐处于出售状态，则不允许删除
        if (this.count(new LambdaQueryWrapper<Setmeal>()
                .in(Setmeal::getId,ids)
                .eq(Setmeal::getStatus,1))>0)

            throw new BussinessException("删除选项中中存在未停售的套餐，禁止删除！");

        //正式进行删除操作
        //删除Setmeal表中数据
        this.removeByIds(ids);

        //删除setmealDish表中相关联的菜品数据
        setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>()
                .in(SetmealDish::getSetmealId,ids));


        return R.success("删除成功！");
    }

    //用户页面查询不同套餐内容的具体信息（如商务套餐）
    @Override
    public R<List<Setmeal>> selectSetmelByCategoryId(Long categoryId, Integer status) {


        List<Setmeal> setmealList = this.list(new LambdaQueryWrapper<Setmeal>()
                .eq(categoryId != null, Setmeal::getCategoryId, categoryId)
                .eq(status != null, Setmeal::getStatus, status));

        return R.success(setmealList);
    }

}




