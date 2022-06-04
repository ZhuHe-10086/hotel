package com.zhuhe.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuhe.hotel.common.CustomException;
import com.zhuhe.hotel.entity.Category;
import com.zhuhe.hotel.entity.Dish;
import com.zhuhe.hotel.entity.Setmeal;
import com.zhuhe.hotel.mapper.CateGroyMapper;
import com.zhuhe.hotel.service.CategroyService;
import com.zhuhe.hotel.service.DishService;
import com.zhuhe.hotel.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategroyServiceImpl extends ServiceImpl<CateGroyMapper, Category> implements CategroyService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //需要判断该分类下是否有菜品
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(Dish::getCategoryId,id);
        int count = dishService.count(qw);
        if (count>0){//说明关联了菜品
//            log.error("删除异常，其下有菜品");
            throw new CustomException("删除异常，其下有菜品");
        }

        LambdaQueryWrapper<Setmeal> qw2 = new LambdaQueryWrapper<>();
        qw2.eq(Setmeal::getCategoryId,id);
        count = setmealService.count(qw2);
        if (count>0){//说明关联了套餐
//            log.error("删除异常，其下有套餐");
            throw new CustomException("删除异常，其下有套餐");
        }
        //需要判断该分类下是否有套餐

    }
}
