package com.jishi.Controller;


import com.jishi.common.R;
import com.jishi.entity.ShoppingCart;
import com.jishi.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService service;


    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingItem, HttpServletRequest request){

       return service.add(shoppingItem,request);
    }

    @PostMapping("/sub")
    public R<List<ShoppingCart>> sub(@RequestBody ShoppingCart shoppingItem, HttpServletRequest request){

        return service.sub(shoppingItem,request);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> shoppingCartList(HttpServletRequest request){

        return  service.shoppingCartList(request);
    }

    @DeleteMapping("/clean")
    public R remove(HttpServletRequest request){

        return service.remove(request);
    }

}
