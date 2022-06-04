package com.zhuhe.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhuhe.hotel.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
