package com.zhuhe.hotel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhuhe.hotel.Dto.SetmealDto;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.Category;
import com.zhuhe.hotel.entity.Setmeal;
import com.zhuhe.hotel.service.CategroyService;
import com.zhuhe.hotel.service.SetmealDishService;
import com.zhuhe.hotel.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategroyService categroyService;

    @Autowired
    private SetmealDishService setmealDishService;



    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    /**
     * 分页查询
     * @param page      第xx页
     * @param pageSize  每个个数
     * @param name      查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        //需要改造一下page的数据类型，但又邀请后端数据库能够正常请求
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(name!=null,Setmeal::getName,name);
        qw.orderByDesc(Setmeal::getPrice).orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,qw);
        //将pageInfo数据放入dtoPage中,但是不能泡杯records,因为类型不匹配，不能拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //索引单独取出
        List<Setmeal> setmeals = pageInfo.getRecords();
        List<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal setmeal: setmeals){
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            Category category = categroyService.getById(setmeal.getCategoryId());
            if (category!=null){
                setmealDto.setCategoryName(category.getName());//分类名称放入dto
            }
            setmealDtos.add(setmealDto);
        }
        dtoPage.setRecords(setmealDtos);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐（可批量删除）
     * 删除套餐需要操作两张表，一张setmeal,一张setmealDish
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable int status,@RequestParam List<Long> ids){
        LambdaUpdateWrapper<Setmeal> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.in(Setmeal::getId,ids).set(Setmeal::getStatus,status);
        setmealService.update(queryWrapper);
        return R.success("修改成功");
    }

    /**
     * 用于前台套餐的展示
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        qw.eq(setmeal.getStatus() !=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> setmeals = setmealService.list(qw);
        return R.success(setmeals);
    }
}
