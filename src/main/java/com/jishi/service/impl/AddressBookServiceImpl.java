package com.jishi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.entity.AddressBook;
import com.jishi.service.AddressBookService;
import com.jishi.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author 23049
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2023-01-06 22:26:32
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




