package com.jishi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.common.Exception.BussinessException;
import com.jishi.common.R;
import com.jishi.dto.DishDto;
import com.jishi.entity.Dish;
import com.jishi.entity.DishFlavor;
import com.jishi.entity.Setmeal;
import com.jishi.entity.SetmealDish;
import com.jishi.service.*;
import com.jishi.mapper.DishMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
* @author 23049
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2023-01-03 21:59:37
*/
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private  DishMapper dishMapper;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Transactional
    @Override
    //开启事务，还需要在启动类加上EnableTransactionManagement
    //修改菜品，需要同步删除redis对应缓存
    public R updateDishWithFlavor(DishDto dishDto) {

        //缓存查询种类菜品的redis Id,这里应该和存入时的redisKey保持一致
        String  redisId = "dish_select_by"+"_categoryid_"+dishDto.getCategoryId();

        //框架会自动识别数据进行插入
        this.saveOrUpdate(dishDto);
        Long dishId = dishDto.getId();

        //这里先把所有口味删除再进行添加（这样做会丢失修改时间，不这样做会很麻烦，需要
        //先查询数据库中所有对应菜品的口味，把这次前端未传入的口味类型全部删除，
        // 传入的口味类型进行update）

        //删除所有口味
        dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId,dishId));


        // 使用stream流方法给每个flover赋上对应的dish_id
        List<DishFlavor> dishFlavors = dishDto.getFlavors().stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(dishFlavors);

        stringRedisTemplate.delete(redisId);

        return R.success(null);
    }

    //菜品页面的分页查询，其中因为前端需要dish种类，但是
    //dish表中只存在dishId，所以在DishDto中创建了对应的
    //CategoryName对象，所以这里查询出来的Page<Dish>需要进行处理
    @Override
    public  R<Page<DishDto>> dishSelect(Integer page,Integer pageSize,String name){

        //Dish分页查询
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getCreateTime);

        Page<Dish> dishPage = dishMapper.selectPage(pageInfo, queryWrapper);

        Page<DishDto> dishDtoPage = new Page<>();

        //对象拷贝工具,忽略records属性的拷贝
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<DishDto> records = dishPage.getRecords().stream().map(dish -> {

            String categoryName="";
            //通过dishId查询对应的种类名称赋值给CategryName字段（前端需要）
            if(dish.getCategoryId()!=null)
             categoryName = categoryService.getById(dish.getCategoryId())
                    .getName();

            //通过对象拷贝实现了父类向子类的强转
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(records);

        return  R.success(dishDtoPage);
    }

    //提供给套餐管理页面新增套餐里的查询分类管理（川菜等等）对应菜品功能
    //提供给用户点餐页面展示不同菜品分类的菜品
    //使用redis进行优化
    @Override
    public R<List<DishDto>> selectDishByCategory(Long categoryId,String name) {

        //构造一个存入redis的实际含义id
        String  redisId = "dish_select_by"+"_categoryid_"+categoryId;

        //获取redis JsON字符串并进行强转
        List<DishDto> dishDtoList= (List<DishDto>)JSON.parse(stringRedisTemplate.opsForValue()
                .get(redisId));

        //如果redis中有缓存数据，直接返回
        if (dishDtoList!=null)
            return R.success(dishDtoList);


        //如果不存在缓存数据，查询数据库，并将数据存入redis
        List<Dish> dishLish = this.list(new LambdaQueryWrapper<Dish>()
                .eq(categoryId != null, Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus,1)
                .like(name!=null,Dish::getName,name)
                .orderByAsc(Dish::getSort)
        );

        dishDtoList = dishLish.stream().map(dish -> {

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            //查询口味数据并赋值给DTO，用于给用户点餐时选择口味
            List<DishFlavor> flavorsList = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>()
                    .eq(DishFlavor::getDishId, dish.getId()));

            dishDto.setFlavors(flavorsList);

            return dishDto;
        }).collect(Collectors.toList());

        //将查询到的数据存入redis缓存,设置一小时超时时间
        stringRedisTemplate.opsForValue().set(redisId,JSON.toJSONString(dishDtoList),1, TimeUnit.HOURS);
        return R.success(dishDtoList);
    }

    @Override
    public R deleteDish(List<Long> ids) {


        if (ids.isEmpty())
            return R.error("请先选中要删除的菜品！");

        //先进行判断，如果套餐菜品处于出售状态，则不允许删除
        if (this.count(new LambdaQueryWrapper<Dish>()
                .in(Dish::getId,ids)
                .eq(Dish::getStatus,1))>0)
        {
            throw new BussinessException("删除选项中中存在未停售的菜品，请先停售菜品！");
        }
        //先进行判断如果有套餐包含该菜品处，需要先删除套餐
        if (setmealDishService.count(new LambdaQueryWrapper<SetmealDish>()
                .in(SetmealDish::getDishId,ids)
        )>0)
        {
            throw new BussinessException("删除选项中存在包含该菜品的套餐，请先删除或修改套餐！");
        }

        //查询dishId对应的CategoryId
        //清空redis中相关缓存
         List<Object> list = this.listObjs(new LambdaQueryWrapper<Dish>()
                .select(Dish::getCategoryId)
                .in(Dish::getId, ids)
        );

        //这里再进行删除正式进行删除操作，否则会导致上一步查询不到结果
        //删除Dish表中数据
        this.removeByIds(ids);

        //得到去重后的Category Set集合
        Set<String> idsSet = list.stream().map(id -> {
                    //将Object类型转换为String
                    //拼接缓存前缀
             System.out.println("dish_select_by"+"_categoryid_"+id.toString());
                    return "dish_select_by"+"_categoryid_"+id.toString();
                }
        ).collect(Collectors.toSet());

        //清空redis对应菜品种类缓存
        stringRedisTemplate.delete(idsSet);

        return R.success(null);
    }

    //批量停售菜品
    @Override
    public R stopSale(List<Long> ids) {

        if (ids.isEmpty())
        {
            return R.error("请先选择要停售的菜品！");
        }
        //先查询是否存在含有该菜品的套餐正在售卖
        List<Object> objects = setmealDishService.listObjs(new LambdaQueryWrapper<SetmealDish>()
                .select(SetmealDish::getSetmealId)
                .in(SetmealDish::getDishId, ids));

        if (objects.size()!=0)
        {
            List<Long> setmealIds = objects.stream().map(o -> {

                Long id =  Long.valueOf(o.toString());
                return id;
            }).collect(Collectors.toList());

            //查询删除菜品是否有包含该菜品的套餐正在售卖
            int count = setmealService.count(new LambdaQueryWrapper<Setmeal>()
                    .eq(Setmeal::getStatus, 1)
                    .in(setmealIds.size()>1,Setmeal::getId, setmealIds)
                    .eq(ids.size()==1,Setmeal::getId,setmealIds));

            if (count>0)
                return R.error("含有删除菜品的套餐正在售卖！禁止删除！");

        }
        //正式开始删除
        //修改菜品状态
        List<Dish> dishList = ids.stream().map(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(0);
            return dish;
        }).collect(Collectors.toList());

        this.updateBatchById(dishList);


        //查询dishId对应的菜系categoryID，将其缓存清空
        this.listObjs(new LambdaQueryWrapper<Dish>()
                        .select(Dish::getCategoryId)
                        .in(Dish::getId,ids))
                .stream()
                .distinct().forEach(categoryId->{
                    //清空缓存
                    stringRedisTemplate.delete("dish_select_by" + "_categoryid_" + categoryId);
                });

        return R.success(null);
    }

    //（批量起售菜品）
    @Override
    public R startSale(List<Long> ids) {

        if (ids.isEmpty())
        {
            return R.error("请先选择要起售的菜品！");
        }


        List<Dish> dishList = ids.stream().map(id -> {
            Dish dish = new Dish();
            dish.setId(id);
            dish.setStatus(1);
            return dish;
        }).collect(Collectors.toList());

        this.updateBatchById(dishList);
        //查询dishId对应的categoryID，将其缓存清空
        this.listObjs(new LambdaQueryWrapper<Dish>()
                .select(Dish::getCategoryId)
                .in(Dish::getId,ids))
                .stream()
                .distinct().forEach(categoryId->{
                    //清空缓存
                 stringRedisTemplate.delete("dish_select_by" + "_categoryid_" + categoryId);
                });

        return R.success(null);

    }

    //修改菜品数据回显功能
    @Override
    public R<DishDto> updateShow(Long dishId) {

        Dish dish = this.getById(dishId);
        LambdaQueryWrapper<DishFlavor> dishFloverWrapper = new LambdaQueryWrapper<>();
        //查询所有对应的口味数据
        dishFloverWrapper.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> dishFlavors = dishFlavorService.list(dishFloverWrapper);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(dishFlavors);


        return R.success(dishDto);
    }


    @Transactional
    @Override
    //新增菜品功能
    //新增菜品后，查询种类对应的菜品功能redis缓存应该被清除
    public R addDishWithFlavor(DishDto dishDto) {

        //缓存查询种类菜品的redis Id,这里应该和存入时的redisKey保持一致
        String  redisId = "dish_select_by"+"_categoryid_"+dishDto.getCategoryId();

        this.save(dishDto);
        Long dishId = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();



        //使用stream流方法给每个flover赋上对应的dish_id
        flavors.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishId);
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

        //删除redis对应缓存
        stringRedisTemplate.delete(redisId);
        return R.success(null);

    }

}




