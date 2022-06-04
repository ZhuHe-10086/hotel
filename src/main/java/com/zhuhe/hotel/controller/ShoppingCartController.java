package com.zhuhe.hotel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zhuhe.hotel.common.BaseContest;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.ShoppingCart;
import com.zhuhe.hotel.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 加入购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart cart = shoppingCartService.addAndSub(shoppingCart, true);
        return R.success(cart);
    }

    /**
     * 从购物出车中减一份
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        ShoppingCart cart = shoppingCartService.addAndSub(shoppingCart, false);
        return R.success(cart);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List> list(){
        LambdaQueryWrapper<ShoppingCart> sqw = new LambdaQueryWrapper<>();
        sqw.eq(ShoppingCart::getUserId,BaseContest.getId())
                .ne(ShoppingCart::getNumber,0)
                .orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(sqw);
        if (list == null){
            return R.error("kong");
        }
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> removeAll(){
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId,BaseContest.getId());
        shoppingCartService.remove(qw);
        return R.success("已清空");
    }
}
