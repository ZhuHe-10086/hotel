package com.zhuhe.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuhe.hotel.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
