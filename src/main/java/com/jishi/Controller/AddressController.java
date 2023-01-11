package com.jishi.Controller;


import com.jishi.common.R;
import com.jishi.entity.AddressBook;
import com.jishi.entity.Orders;
import com.jishi.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/addressBook")
public class AddressController {

    @Autowired
    private AddressBookService service;


    @PostMapping
    public R addAddress(@RequestBody AddressBook addressBook)
    {
        return  service.addOrUpdateAddress(addressBook);
    }


    @GetMapping("/list")
    public R<List<AddressBook>> addressList(){
    return service.addressList();
    }


    @PutMapping("/default")
    public R setDeaultAddr(@RequestBody Map map){

        return  service.setDeaultAddr(map);
    }

    @GetMapping("/{addrId}")
    public R<AddressBook> updateShow(@PathVariable Long addrId){

       return service .updateShow(addrId);
    }

    @GetMapping("/default")
    public  R<AddressBook> defaultAddr(){

        return service.defaultAddr();
    }

    @DeleteMapping
    public R delAddr(Long ids){

        return service.delAddr(ids);
    }

    @PutMapping
    public  R  updateAddr(@RequestBody AddressBook addressBook){
       return service.addOrUpdateAddress(addressBook);
    }

}
