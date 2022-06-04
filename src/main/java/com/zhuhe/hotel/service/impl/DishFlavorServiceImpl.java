package com.zhuhe.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuhe.hotel.Dto.DishDto;
import com.zhuhe.hotel.entity.DishFlavor;
import com.zhuhe.hotel.mapper.DishFlavorMapper;
import com.zhuhe.hotel.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

}
