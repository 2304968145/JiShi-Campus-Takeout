package com.jishi.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jishi.common.R;
import com.jishi.entity.Orders;
import com.jishi.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;


    @PostMapping("/submit")
    public R submit(@RequestBody Orders orders){

       return ordersService.submit(orders);
    }

    @GetMapping("/page")
    public R<Page<Orders>> pageSelcet(Integer page, Integer pageSize, Integer number,
                                      @DateTimeFormat(pattern = "yyyy/MM//dd HH:mm:ss") LocalDateTime beginTime,
                                      @DateTimeFormat(pattern = "yyyy/MM//dd HH:mm:ss")LocalDateTime endTime){

            return   ordersService.pageSelcet(page,pageSize,number,beginTime,endTime);
    }

    //查询对应用户的最新一条订单
    @GetMapping("/userPage")
    public  R<Page<Orders>>  lastOneOrder(Integer page,Integer pageSize){

            return ordersService.lastOneOrder(page,pageSize);
    }



}
