package com.zhuhe.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuhe.hotel.Dto.DishDto;
import com.zhuhe.hotel.entity.Dish;
import com.zhuhe.hotel.entity.DishFlavor;
import com.zhuhe.hotel.mapper.DishMapper;
import com.zhuhe.hotel.service.DishFlavorService;
import com.zhuhe.hotel.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品同时新增口味数据
     * @param dishDto
     */
    @Override
    @Transactional()
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        //这个顺序不能错，因为这个id之后当这个save之后才会生成
        Long id = dishDto.getId();
        //这样写是有问题的，要知道前端传来的dishDto中的Flavors属性只key和value，
        // 没有菜品的id，菜品的id再外面的dishDto上
//        dishFlavorService.saveBatch(dishDto.getFlavors());
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor:flavors){
            flavor.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public DishDto getByIdWithFalvor(long id) {
        Dish dish = this.getById(id);
        LambdaQueryWrapper<DishFlavor> wq = new LambdaQueryWrapper<>();
        wq.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(wq);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新基本信息
        this.updateById(dishDto);
        //还有flavor需要操作，这里需要先删除所有的口味
        LambdaQueryWrapper<DishFlavor> wq = new LambdaQueryWrapper<>();
        wq.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wq);
        //然后重新添加
        List<DishFlavor> flavors = dishDto.getFlavors();
        for(DishFlavor flavor:flavors){
            flavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void removeWitFlavor(List<String> ids) {
        //先删除dish表
        this.removeByIds(ids);
        //然后再删除口味表
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(qw);
    }
}
