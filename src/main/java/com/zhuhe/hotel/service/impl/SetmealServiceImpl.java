package com.zhuhe.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuhe.hotel.Dto.SetmealDto;
import com.zhuhe.hotel.common.CustomException;
import com.zhuhe.hotel.entity.Setmeal;
import com.zhuhe.hotel.entity.SetmealDish;
import com.zhuhe.hotel.mapper.SetmealMapper;
import com.zhuhe.hotel.service.SetmealDishService;
import com.zhuhe.hotel.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional //保证事务的一致性
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional //保证事务的一致性
    public void removeWithDish(List<Long> ids) {
        //首先查询套餐的状态，如果是启用则不能删除
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId,ids).eq(Setmeal::getStatus,0);
        int count = this.count(qw);
        //如果不能删除，直接抛出异常，
        if (count == 0){//说明一个都没有查出
            throw new CustomException("不能删除，改套餐正在售卖");
        }
        //如果可以删除，先删除套餐表Setmeal
        this.remove(qw);
        //然后根据套餐表的id查套餐的菜品表SetmealDish，并进行删除
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(wrapper);
    }
}
