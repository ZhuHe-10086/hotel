package com.zhuhe.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuhe.hotel.Dto.SetmealDto;
import com.zhuhe.hotel.entity.Dish;
import com.zhuhe.hotel.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增页面的方法保存套餐表和菜品的关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    void  removeWithDish(List<Long> ids);
}
