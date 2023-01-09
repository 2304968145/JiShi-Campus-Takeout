package com.jishi.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.common.R;
import com.jishi.dto.DishDto;
import com.jishi.entity.Dish;
import com.jishi.entity.DishFlavor;
import com.jishi.service.CategoryService;
import com.jishi.service.DishFlavorService;
import com.jishi.service.DishService;
import com.jishi.mapper.DishMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 23049
* @description 针对表【dish(菜品管理)】的数据库操作Service实现
* @createDate 2023-01-03 21:59:37
*/
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService{

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private  DishMapper dishMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



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
        Long dishID = dishDto.getId();


        //使用stream流方法给每个flover赋上对应的dish_id
        flavors.stream().map(dishFlavor -> {
            dishFlavor.setDishId(dishID);
            return dishFlavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

        //删除redis对应缓存
        stringRedisTemplate.delete(redisId);
        return R.success(null);

    }

}




