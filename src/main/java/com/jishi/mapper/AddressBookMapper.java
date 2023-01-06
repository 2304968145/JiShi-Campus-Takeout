package com.jishi.mapper;

import com.jishi.entity.AddressBook;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 23049
* @description 针对表【address_book(地址管理)】的数据库操作Mapper
* @createDate 2023-01-06 22:26:32
* @Entity com.jishi.entity.AddressBook
*/
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

}




