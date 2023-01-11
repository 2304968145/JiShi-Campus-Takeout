package com.jishi.service;

import com.jishi.common.R;
import com.jishi.entity.AddressBook;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

/**
* @author 23049
* @description 针对表【address_book(地址管理)】的数据库操作Service
* @createDate 2023-01-06 22:26:32
*/
public interface AddressBookService extends IService<AddressBook> {

    public R addOrUpdateAddress(AddressBook addressBook);

    public  R<List<AddressBook>>  addressList();

    public  R setDeaultAddr(Map map);

    public  R<AddressBook>  defaultAddr();
    public  R<AddressBook>  updateShow(Long addrId);

    public R delAddr(Long addrID);



}
