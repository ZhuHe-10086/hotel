package com.zhuhe.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuhe.hotel.entity.Orders;


public interface OrdersService extends IService<Orders> {
    //下单
    void submit(Orders orders);
}
