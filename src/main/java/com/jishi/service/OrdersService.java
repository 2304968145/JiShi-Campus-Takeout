package com.jishi.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;

/**
* @author 23049
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2023-01-06 22:12:24
*/
public interface OrdersService extends IService<Orders> {


    public R<Page<Orders>> pageSelcet(Integer page, Integer pageSize,Integer number, LocalDateTime begin, LocalDateTime end);

    public R submit(Orders orders);

    public  R<Page<Orders>>  lastOneOrder(Integer page,Integer pageSize);
}
