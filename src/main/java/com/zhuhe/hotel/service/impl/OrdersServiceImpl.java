package com.zhuhe.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuhe.hotel.common.BaseContest;
import com.zhuhe.hotel.common.CustomException;
import com.zhuhe.hotel.entity.*;
import com.zhuhe.hotel.mapper.OrdersMapper;
import com.zhuhe.hotel.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    public ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        //首先获得当前用户的id
        Long userId = BaseContest.getId();
        //根据这个用户id去购物车表查具体的商品
        LambdaQueryWrapper<ShoppingCart> sqw = new LambdaQueryWrapper<>();
        sqw.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(sqw);
        if (shoppingCarts == null || shoppingCarts.size() == 0){
            throw new CustomException("购物车为空，无法下单");
        }
        //首先需要一张订单表，这里需要放订单数据（前端传来的数据只有地址表id和备注）
        //查询user表中的信息，把user表中的用户名和手机号当道订单表中
        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        //组装成Order对象,然后存入表中
        orders = assemble1(orders, user,addressBook);
        int money = assembleGetMoney(shoppingCarts).intValue();
        orders.setAmount(money);
        this.save(orders);//然后把组装好的需要的信息放入订单表
        List<OrderDetail> orderDetails = assemble2(orders, shoppingCarts, addressBook);

        orderDetailService.saveBatch(orderDetails);//然后把组装好的信息放入订单详情表中

        //全部保存完毕，清空购物车
        shoppingCartService.remove(sqw);
        return;
    }

    //组合订单对象的方法,太多了单独提出来
    private Orders assemble1(Orders orders,User user,AddressBook addressBook){
        long orderid = IdWorker.getId();//随机生成一个订单号
        orders.setUserId(user.getId());//添加一个用户id
        orders.setUserName(user.getName());//添加一个用户名
        orders.setNumber(String.valueOf(orderid));//把创建的一个订单号放入
        orders.setOrderTime(LocalDateTime.now());//下单时间
        orders.setCheckoutTime(LocalDateTime.now());//结账时间
        orders.setStatus(4);//直接设置成已完成
        orders.setPhone(addressBook.getPhone());//还有一个手机号未添加，这里加上
        orders.setConsignee(addressBook.getConsignee());//设置收货人
        orders.setAddress(addressBook.getDetail());//设置收货地址
        return orders;
    }
    //组合订单详情数据的
    private List<OrderDetail> assemble2(Orders orders,List<ShoppingCart> shoppingCarts,AddressBook addressBook){
        List<OrderDetail> list = new ArrayList<>();
//        Long i = 1L;
        for (ShoppingCart shoppingCart:shoppingCarts){
            OrderDetail orderDetail = new OrderDetail();
//            orderDetail.setId(i++);
            orderDetail.setName(shoppingCart.getName());//设置菜品名字
            orderDetail.setImage(shoppingCart.getImage());//设置菜品名字
            orderDetail.setOrderId(orders.getId());//获取订单名字
            orderDetail.setDishFlavor(shoppingCart.getDishFlavor());//设置口味
            orderDetail.setNumber(shoppingCart.getNumber());//设置菜品数量
            orderDetail.setAmount(shoppingCart.getAmount());//单分金额
            //设置菜品的id或套餐id
            if (shoppingCart.getDishId() == null){
                orderDetail.setSetmealId(shoppingCart.getSetmealId());
            }else {
                orderDetail.setDishId(shoppingCart.getDishId());
            }
            //把对象放到集合中
            list.add(orderDetail);
        }
        return list;
    }

    //这个是计算用户能用多少钱的
    private AtomicInteger assembleGetMoney(List<ShoppingCart> shoppingCarts){
        AtomicInteger money = new AtomicInteger(0);
        for (ShoppingCart shoppingCart :shoppingCarts){
            BigDecimal multiply = shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber()));
            money.addAndGet(multiply.intValue());
        }
        return money;
    }
}
