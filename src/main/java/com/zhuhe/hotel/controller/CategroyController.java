package com.zhuhe.hotel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.Category;
import com.zhuhe.hotel.service.CategroyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategroyController {
    @Autowired
    private CategroyService categroyService;

    @PostMapping
    private R<String> save(@RequestBody Category category){
        categroyService.save(category);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    private R<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //条件过滤器
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(Category::getSort);
        //进行分页查询
        categroyService.page(pageInfo,qw);
        return R.success(pageInfo);
    }

    /**
     * 删除菜品
     * @param id
     * @return
     */
    @DeleteMapping
    private R<String> delete(@RequestParam(name = "ids") Long id){
        categroyService.remove(id);
        return R.success("删除成功");
    }

    @PutMapping
    private R<String> Update(@RequestBody Category category){
        categroyService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 这个可以用于下拉框的显示,同时前台页面右面的菜品分类
     * 显示也是用到他
     * @param category
     * @return
     */
    @GetMapping("/list")
    private R<List> list(Category category){
        LambdaQueryWrapper<Category> wq = new LambdaQueryWrapper<>();
        if (category.getType()!=null){
            wq.eq(Category::getType, category.getType());
            wq.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);
        }
        List<Category> list = categroyService.list(wq);

        return R.success(list);
    }
}
