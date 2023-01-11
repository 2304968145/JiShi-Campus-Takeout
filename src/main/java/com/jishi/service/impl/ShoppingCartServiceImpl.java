package com.jishi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.common.R;
import com.jishi.entity.ShoppingCart;
import com.jishi.service.ShoppingCartService;
import com.jishi.mapper.ShoppingCartMapper;
import com.sun.org.apache.xpath.internal.objects.XNull;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
* @author 23049
* @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
* @createDate 2023-01-06 16:02:13
*/
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
    implements ShoppingCartService{


    //添加购物车方法，每次添加商品到购物车执行此方法
    @Override
    public R<ShoppingCart> add(ShoppingCart shoppingItem, HttpServletRequest request) {

        //先判断购物车表中是否存在同样的商品
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //先判断是套餐还是单个菜品（不放mp判断是黑客防止传两个空值过来）
        //其实TM的出错反正也会抛异常，统一异常处理不久完了吗
        if(shoppingItem.getDishId()!=null)
        {
            //这里的口味判断放这里是因为前端没有提供套餐口味
            //所以套餐口味一直为null，但是Mysq null不能用eq判断，会被认为是不同数据
              queryWrapper.eq(ShoppingCart::getDishId,shoppingItem.getDishId());
                    //该行不同口味当作不同内容item删除，因为前端设计如此，如果改动变化太大
                    //因此认为不同口味属于同一item
                  //    .eq(ShoppingCart::getDishFlavor,shoppingItem.getDishFlavor());
        }else if (shoppingItem.getSetmealId()!=null)
        {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingItem.getSetmealId());
        }else {
            return  R.error("请不要进行非法操作！");
        }


        Long userId = (Long) request.getSession().getAttribute("user");
        //查询该用户是否存在此类商品
        ShoppingCart item = this.getOne(queryWrapper.eq(ShoppingCart::getUserId,userId));


        //如果不存在直接插入该条商品到数据库
        if(item==null){
            shoppingItem.setUserId(userId);
            shoppingItem.setCreateTime(LocalDateTime.now());
            this.save(shoppingItem);
            return R.success(shoppingItem);
        }else
        {
            //如果存在则数量+1
            item.setNumber(item.getNumber()+1);
            this.updateById(item);
        }

        return R.success(item);
    }

    //减少购物车商品数量方法
    @Override
    public R<List<ShoppingCart>> sub(ShoppingCart shoppingItem, HttpServletRequest request) {

        Long userId = (Long) request.getSession().getAttribute("user");

        //判断需要减少的的是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (shoppingItem.getDishId()!=null) {
            queryWrapper.eq(ShoppingCart::getDishId,shoppingItem.getDishId());
        } else if (shoppingItem.getSetmealId()!=null) {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingItem.getSetmealId());
        }else
        {
            return R.error("请按规则操作！");
        }

        //查询到购物车对应商品条目
        ShoppingCart item = this.getOne(queryWrapper.eq(ShoppingCart::getUserId, userId));
        //判断物品数据，如果>1，则-1，如果=1，直接删除记录
        if (item.getNumber()==1)
        {
            this.removeById(item.getId());

        }else
        {
            item.setNumber(item.getNumber()-1);
            this.updateById(item);
        }

        return R.success(this.list(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId,userId)));

    }

    //查询用户购物车所有内容
    @Override
    public R<List<ShoppingCart>> shoppingCartList(HttpServletRequest request) {

        List<ShoppingCart> shoppingCartList = this.list(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, (Long)request.getSession().getAttribute("user"))
                .orderByDesc(ShoppingCart::getCreateTime));

        return  R.success(shoppingCartList);
    }

    //清空购物车
    @Override
    public R remove(HttpServletRequest request) {

        this.remove(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId,(Long)request.getSession().getAttribute("user")));
        return R.success(null);
    }
}




