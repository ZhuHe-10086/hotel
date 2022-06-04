package com.zhuhe.hotel.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhuhe.hotel.entity.Category;

public interface CategroyService extends IService<Category> {

    public void remove(Long id);
}
