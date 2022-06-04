package com.zhuhe.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuhe.hotel.entity.ShoppingCart;

import java.util.List;


public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     *增加和删除方法菜品的方法
     * @param shoppingCart
     * @param flag  判断到底是增加还是删除 true增加
     * @return
     */
    ShoppingCart addAndSub(ShoppingCart shoppingCart,Boolean flag);
}
