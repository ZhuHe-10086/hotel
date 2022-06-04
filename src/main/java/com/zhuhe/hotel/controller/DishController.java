package com.zhuhe.hotel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhuhe.hotel.Dto.DishDto;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.Category;
import com.zhuhe.hotel.entity.Dish;
import com.zhuhe.hotel.entity.DishFlavor;
import com.zhuhe.hotel.service.CategroyService;
import com.zhuhe.hotel.service.DishFlavorService;
import com.zhuhe.hotel.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategroyService categroyService;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     *新增菜品
     * 这里的参数类需要，实体类中，没有完全能接收的类型
     * 所有创建了一个DTO类型的数据，专门用来接收复合性数据
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("成功");
    }

    /**
     * 菜品信息的分页
     * @param page
     * @param pageSize
     * @param name  查询的参数名
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageDto = new Page<>();//创建这个是因为前端需要一个分类名字的属性，而后端没有，这时
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper();
        qw.like(name!=null,Dish::getName,name);
        qw.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,qw);
        //对象拷贝方法
        BeanUtils.copyProperties(pageInfo,pageDto,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> listDishDto = new ArrayList<>();
        for(Dish dish:records){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);
            Long categoryId = dish.getCategoryId();//获取原本数据的id
            Category category = categroyService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            listDishDto.add(dishDto);
        }
        pageDto.setRecords(listDishDto);
        return R.success(pageDto);
    }

    /**
     * 修改的回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable long id){
        DishDto dishDto = dishService.getByIdWithFalvor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("成功");
    }

    /**
     * 修改启用禁售状态,要兼容批量操作
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable int status,@RequestParam List<Long> ids){
//        log.info(""+status+ids);
        LambdaUpdateWrapper<Dish> qw = new LambdaUpdateWrapper<>();
        qw.in(Dish::getId,ids).set(Dish::getStatus,status);
        dishService.update(qw);
        return R.success("修改成功");
    }

    /**
     * 兼容批量删除，需要操作两张表，一张菜品表，一张口味表
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> remove(@RequestParam List<String> ids){
        dishService.removeWitFlavor(ids);
        return R.success("删除成功");
    }

    /**
     * 套餐管理的添加套餐，展示数据，
     * 同时也应用到前端页面的展示（所以需要改造一下编程dishDto类型）
     * @param dish
     * @return
     */
//    @GetMapping("list")
//    public R<List> list(Dish dish){
//        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
//        if (dish.getCategoryId() != null){
//            qw.eq(Dish::getCategoryId,dish.getCategoryId()).eq(Dish::getStatus,1);
//            qw.orderByDesc(Dish::getSort);
//        }
//        List<Dish> list = dishService.list(qw);
//        return R.success(list);
//    }
    @GetMapping("list")
    public R<List> list(Dish dish){
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        if (dish.getCategoryId() != null){
            qw.eq(Dish::getCategoryId,dish.getCategoryId()).eq(Dish::getStatus,1);
            qw.orderByDesc(Dish::getSort);
        }
        List<Dish> list = dishService.list(qw);
        List<DishDto> dishDtos  = new ArrayList<>();
        for(Dish di:list){
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(di,dishDto);
            LambdaQueryWrapper<DishFlavor> dqw = new LambdaQueryWrapper<>();
            dqw.eq(DishFlavor::getDishId,di.getId());
            List<DishFlavor> flavors = dishFlavorService.list(dqw);
            dishDto.setFlavors(flavors);
            dishDtos.add(dishDto);
        }
        return R.success(dishDtos);
    }

}
