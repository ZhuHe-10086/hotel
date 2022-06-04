package com.zhuhe.hotel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhuhe.hotel.common.BaseContest;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.OrderDetail;
import com.zhuhe.hotel.entity.Orders;
import com.zhuhe.hotel.service.OrderDetailService;
import com.zhuhe.hotel.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    public OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        ordersService.submit(orders);
        return R.success("支付成功");
    }

    @GetMapping("/page")
    public R<Page> Page(int page, int pageSize, Long number,
                                String beginTime,String endTime){
        Page pageInfo = new Page(page, pageSize);
        LambdaQueryWrapper<Orders> oqw = new LambdaQueryWrapper<>();
        oqw.eq(number!=null,Orders::getPhone,number);
        oqw.between(beginTime!=null,Orders::getOrderTime,beginTime,endTime);
        ordersService.page(pageInfo,oqw);
        return R.success(pageInfo);
    }
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize){
        Page pageInfo = new Page(page,pageSize);
        //先通过userid获得订单的id,再通过订单的id得到订单详情的id
        QueryWrapper<Orders> oqw = new QueryWrapper<>();
        oqw.select("max(order_time)","id").eq("user_id",BaseContest.getId());
        Orders order = ordersService.getOne(oqw);
        LambdaQueryWrapper<OrderDetail> qw = new LambdaQueryWrapper<>();
        qw.eq(OrderDetail::getOrderId,order.getId());
        orderDetailService.page(pageInfo,qw);
        return R.success(pageInfo);
    }
}
