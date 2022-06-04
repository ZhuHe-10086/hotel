package com.zhuhe.hotel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.User;
import com.zhuhe.hotel.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param map
     * @param request
     * @return
     */
    @PostMapping("/login")
    private R<User> login(@RequestBody Map map, HttpServletRequest request){
        log.info(map.toString());
        //获取手机好
        String phone = (String) map.get("phone");
        //查这个手机号是否存在
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        qw.eq(phone != null,User::getPhone,map.get("phone"));
        User user  = userService.getOne(qw);
        //如果手机号不存在，就给他存起来，相当于注册
        if (user  == null){
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        request.getSession().setAttribute("user",user.getId());
        return R.success(user);
    }
}
