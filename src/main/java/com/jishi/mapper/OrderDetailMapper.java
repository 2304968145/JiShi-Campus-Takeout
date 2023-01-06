package com.jishi.mapper;

import com.jishi.entity.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2023-01-06 22:12:28
* @Entity com.jishi.entity.OrderDetail
*/
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

}




