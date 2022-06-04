package com.zhuhe.hotel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuhe.hotel.common.BaseContest;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.ShoppingCart;
import com.zhuhe.hotel.mapper.ShoppingCartMapper;
import com.zhuhe.hotel.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    @Transactional
    public ShoppingCart addAndSub(ShoppingCart shoppingCart,Boolean flag) {
        //首先需要设置用户的id,指定用户订单
        Long userId = BaseContest.getId();
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> sqw = new LambdaQueryWrapper<>();
        sqw.eq(ShoppingCart::getUserId,userId);
        //然后判断修改购物车的是菜品还是套餐
        if (shoppingCart.getDishId() != null){//说明是菜
            sqw.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {//说明是套餐
            sqw.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询然后查询
        ShoppingCart one = this.getOne(sqw);
        if (flag){
            //如果是新增
            if (one == null){ //这个东东不存在
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
                this.save(shoppingCart);
                one = shoppingCart;
            }
//            else if (one != null && one.getNumber() == 0){
//                shoppingCart.setId(one.getId());
//                shoppingCart.setNumber(1);
//                shoppingCart.setCreateTime(LocalDateTime.now());
//                this.updateById(shoppingCart);
//                one = shoppingCart;
//            }
            else {
                one.setNumber(one.getNumber()+1);
                this.updateById(one);
            }
        }else {
            if (one == null || one.getNumber() == 0){ //这个东东,不存在数量则么减少
                return null;
            }else {
                if (one.getNumber()-1 == 0){
                    this.removeById(one);
                    one.setNumber(0);//要是不设置成0这个前端显示就会出错
                }else {
                    one.setNumber(one.getNumber()-1);
                    this.updateById(one);
                }
            }
        }
        return one;
    }
}
