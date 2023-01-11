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
import com.jishi.service.DishService;
import com.jishi.service.SetmealDishService;
import com.jishi.service.SetmealService;
import com.jishi.mapper.SetmealMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Autowired
    private DishService dishService;

    @Transactional
    @Override
    //新增套餐功能
    @CacheEvict(value = "setmea_detail_list",allEntries = true)
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

            //套餐分页查询
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
    //删除套餐后，直接清空所有套餐缓存（清除单个不知道怎么判断，传过来的是数组)
    @CacheEvict(value = "setmea_detail_list",allEntries = true)
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
    @Cacheable(value = "setmea_detail_list",key = "'categoryId_'+#categoryId")
    public R<List<Setmeal>> selectSetmelByCategoryId(Long categoryId, Integer status) {


        List<Setmeal> setmealList = this.list(new LambdaQueryWrapper<Setmeal>()
                .eq(categoryId != null, Setmeal::getCategoryId, categoryId)
                .eq(status != null, Setmeal::getStatus, status));

        return R.success(setmealList);
    }


    //（批量）停售套餐
    @CacheEvict(value = "setmea_detail_list",allEntries = true)
    @Override
    public R stopSale(List<Long> ids) {

        if (ids.isEmpty())
        {
            return R.error("请先选择要停售的套餐！");
        }


        List<Setmeal> setmealList = ids.stream().map(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(0);
            return setmeal;
        }).collect(Collectors.toList());

        this.updateBatchById(setmealList);

        return R.success(null);
    }

    //（批量）起售套餐
    @CacheEvict(value = "setmea_detail_list",allEntries = true)
    @Override
    public R startSale(List<Long> ids) {

        if (ids.isEmpty())
        {
            return R.error("请先选择要起售的套餐！");
        }
        //起售之前应该先判断套餐包含的菜品是否存在且处于起售状态
        //先查询ids中包含的菜品id集合
        List<Long> dishIds = setmealDishService.listObjs(new LambdaQueryWrapper<SetmealDish>()
                        .select(SetmealDish::getDishId)
                        .in(SetmealDish::getSetmealId, ids))
                .stream().distinct().map(id -> {
                    //进行强转
                    return Long.valueOf(id.toString());
                }).collect(Collectors.toList());
        //查询dish表中dish数量和集合大小比对，如果不一致说明有菜品状态不正常
        int dishCount = dishService.count(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getStatus, 1)
                .in(Dish::getId, dishIds));
        if (dishCount!=dishIds.size())
        {
            return R.error("启用的套餐中有菜品状态不正常，请检查！");
        }
        //正式启用套餐
        List<Setmeal> setmealList = ids.stream().map(id -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(id);
            setmeal.setStatus(1);
            return setmeal;
        }).collect(Collectors.toList());

        this.updateBatchById(setmealList);

        //删除缓存

        return R.success(null);
    }


    //更新套餐信息
    @Override
    @Transactional
    @CacheEvict(value = "setmea_detail_list",allEntries = true)
    public R update(SetmealDto setmealDto) {


            //框架会自动识别数据进行插入
            this.saveOrUpdate(setmealDto);
            Long setmealId = setmealDto.getId();

            //这里先把所有套餐详情删除再进行添加（这样做会丢失修改时间，不这样做会很麻烦，需要
            //先查询数据库中所有对应套餐的详情，全部删除，


            //删除所有对应套餐详情
            setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>()
                    .eq(SetmealDish::getSetmealId,setmealId));


            // 使用stream流方法给每个套餐（菜品）详情赋上共同的套餐id
            List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes().stream().map(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
                return setmealDish;
            }).collect(Collectors.toList());

            setmealDishService.saveBatch(setmealDishList);

            return R.success(null);

    }

    @Override
    public R<SetmealDto> updateShow(Long id) {

        Setmeal setmeal = this.getById(id);

        List<SetmealDish> setmealDishList = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>()
                .eq(SetmealDish::getSetmealId, id));

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(setmealDishList);

        return R.success(setmealDto);

    }


}




