package com.jishi.mapper;

import com.jishi.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2023-01-06 22:12:24
* @Entity com.jishi.entity.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




