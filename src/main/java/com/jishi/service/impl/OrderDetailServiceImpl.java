package com.jishi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.entity.OrderDetail;
import com.jishi.service.OrderDetailService;
import com.jishi.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author 23049
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2023-01-06 22:12:28
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




