package com.zhuhe.hotel.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhuhe.hotel.common.R;
import com.zhuhe.hotel.entity.Employee;
import com.zhuhe.hotel.service.EmployService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployController {

    @Autowired
    private EmployService employService;

    /**
     * 员工登录
     * @param employee  前端的数据封装成相应的对象
     * @param request   将登录成功的对象放入session中
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        //1.首先将密码进行进行加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.通过mybatis-plus查询数据
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employService.getOne(queryWrapper);
        //3.如果没有数据，就返回失败
        if (null == emp){
            return R.error("登录失败");
        }
        //4.如果有这个人，就要看一下密码对不对
        if (!password.equals(emp.getPassword())){
            return R.error("密码错误");
        }
        //5.如果密码也对，就看看这个员工的状态
        if (emp.getStatus() == 0){
            return R.error("账号已被禁用");
        }
        //6.登录成功，将员工id存入Session并返回成功的结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request){
        //清除session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
//        log.info("新增员工"+employee.toString());
        //这个没有给密码的选项，所有统一给定一个初始密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /**这里的操作就全部当道配置类中，进行自动的装配*/
//        //初始化入职时间和修改人
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //初始化创建人和修改人(即当前的管理员，通过session获取)
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        //然后把对象存起来
        employService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 分页操作，这个理的Page对象是Mybatis-Plus对象分装好的，
     * 用于显示分页的信息
     * 底层是基于mybatis-plus的分页插件实现的
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造器
        Page pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);//添加一个条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //条件添加完毕，就可以执行查询了
        employService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据员工id修改信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpServletRequest request){
        /**这里的方法同样也放到MyMetaObjectHandler类中*/
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        long id = Thread.currentThread().getId();
        log.info("线程id："+id);
        employService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 编辑员工的第一步首先显示当前员工的信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable String id){
//        log.info("根据id查数据库");
        Employee employee = employService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到该信息");
    }
}
