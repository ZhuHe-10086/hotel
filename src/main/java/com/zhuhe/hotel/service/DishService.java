package com.zhuhe.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuhe.hotel.Dto.DishDto;
import com.zhuhe.hotel.entity.Dish;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品，同时插入口味数据
    //需要同时操作两张表，一个是dish，一个是dish_flavor
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFalvor(long id);

    void updateWithFlavor(DishDto dishDto);

    /**
     * 批量删除
     * @param ids
     */
    void removeWitFlavor(List<String> ids);
}
