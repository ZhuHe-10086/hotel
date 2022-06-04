package com.zhuhe.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuhe.hotel.entity.OrderDetail;
import com.zhuhe.hotel.mapper.OrderDetailMapper;
import com.zhuhe.hotel.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
