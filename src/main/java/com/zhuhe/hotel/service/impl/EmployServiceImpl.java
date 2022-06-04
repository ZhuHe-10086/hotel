package com.zhuhe.hotel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhuhe.hotel.entity.Employee;
import com.zhuhe.hotel.mapper.EmployeeMapper;
import com.zhuhe.hotel.service.EmployService;
import org.springframework.stereotype.Service;

@Service
public class EmployServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployService {
}
