package com.zhuhe.hotel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zhuhe.hotel.common.BaseContest;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.AddressBook;
import com.zhuhe.hotel.entity.User;
import com.zhuhe.hotel.service.AddressBookService;
import com.zhuhe.hotel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;
    /**
     * 新增地址
     * @param addressBook
     */
    @PostMapping()
    public R<AddressBook> save(@RequestBody AddressBook addressBook){
        Long userId = BaseContest.getId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 回显修改页面
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getAddressBook(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null){
            return R.error("未找到该地址");
        }
        return R.success(addressBook);
    }

    @GetMapping("/list")
    public R<List> list(AddressBook addressBook){
        Long userId = BaseContest.getId();
        addressBook.setUserId(userId);
        LambdaQueryWrapper<AddressBook> qw = new LambdaQueryWrapper<>();
        qw.eq(userId != null,AddressBook::getUserId,addressBook.getUserId())
                .orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBooks = addressBookService.list(qw);
        return R.success(addressBooks);
    }

    /**
     * 修改提交
     * @param addressBook
     * @return
     */
    @PutMapping()
    public R<String> update(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }

    /**
     * 修改默认地址，
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        //首先全部给他改成0
        LambdaUpdateWrapper<AddressBook> uq = new LambdaUpdateWrapper<>();
        uq.set(AddressBook::getIsDefault,0)
                .eq(AddressBook::getUserId,BaseContest.getId());
        addressBookService.update(uq);
        //单独修改需要操作的属性
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 在结算的时候可以自动返给默认地址
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        Long uerId = BaseContest.getId();
        //然后再查相应的默认地址
        LambdaQueryWrapper<AddressBook> aqw = new LambdaQueryWrapper<>();
        aqw.eq(AddressBook::getUserId,uerId)
                .eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(aqw);
        if (addressBook == null){
            return R.error("没有找到该对象");
        }
        return R.success(addressBook);
    }
}
