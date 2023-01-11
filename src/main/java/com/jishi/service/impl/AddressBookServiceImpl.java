package com.jishi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jishi.common.R;
import com.jishi.common.ThreadUtil;
import com.jishi.entity.AddressBook;
import com.jishi.service.AddressBookService;
import com.jishi.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
* @author 23049
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2023-01-06 22:26:32
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

    //添加或修改用户收货地址
    @Override
    public R addOrUpdateAddress(AddressBook addressBook) {

        //id为null说明是新增地址
        if (addressBook.getUserId()==null)
        addressBook.setUserId(ThreadUtil.getCurrentId());
        //判断是否为用户第一个地址,如果是自动设为默认地址
        int count =this.count(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getUserId,addressBook.getUserId()));
        if (count==0)
            addressBook.setIsDefault(1);

        this.saveOrUpdate(addressBook);

        return R.success(null);

    }

    //返回用户对应的所有地址
    @Override
    public R<List<AddressBook>> addressList() {
        //获取用户id
        Long userID = ThreadUtil.getCurrentId();
        List<AddressBook> list = this.list(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getUserId, userID));

        return R.success(list);
    }

    //设置用户默认下单地址
    @Override
    public R setDeaultAddr(Map map) {

        AddressBook updateAddr = new AddressBook();
        Long addrId = Long.valueOf(map.get("id").toString());
        //先将该用户所有地址默认字段设为0
        updateAddr.setIsDefault(0);
        this.update(updateAddr,new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getUserId,ThreadUtil.getCurrentId()));

        //再将指定id地址设为1
        updateAddr.setIsDefault(1);
        this.update(updateAddr,new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getUserId,ThreadUtil.getCurrentId())
                .eq(AddressBook::getId,addrId));


        return R.success(null);
    }

    //返回用户的默认地址
    @Override
    public R<AddressBook> defaultAddr() {

        //获取用户对应的默认地址
        AddressBook addressBook = this.getOne(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getUserId, ThreadUtil.getCurrentId())
                .eq(AddressBook::getIsDefault, 1));

        //如果用户不存在任何地址，提示前端先添加地址
        if (addressBook==null)
            return R.error("默认地址不存在！");

            return R.success(addressBook);
    }

    //修改地址回显数据功能
    @Override
    public R<AddressBook> updateShow(Long addrId) {

        AddressBook one = this.getOne(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getId, addrId));

        return R.success(one);
    }

    //删除对应Id的地址
    @Override
    public R delAddr(Long addrID) {

        this.remove(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getId,addrID));

        return R.success(null);
    }
}




