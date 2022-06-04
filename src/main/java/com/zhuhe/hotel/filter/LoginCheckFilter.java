package com.zhuhe.hotel.filter;

import com.alibaba.fastjson.JSON;
import com.zhuhe.hotel.common.BaseContest;
import com.zhuhe.hotel.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
//这个注解是创建一个拦截类，然后拦截/*
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //专门用于路径匹配的，支持通配符
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    //创建一个数组进行判断哪些需要放行
    private static String[] URLS = new String[]{
            "/employee/login",   //遇到这个当然需要放行
            "/employee/logout",
            "/backend/**",//这些都是静态页面，不处理，放行
            "/front/**",
            "/common/**",
            "/user/login"
    };
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //首先获取本次请求的URI
        String requestURI = request.getRequestURI();
        //调用check方法，看是否匹配成功（即是否需要判断），成功就放行，
        boolean res = check(requestURI);
        if (res){
            filterChain.doFilter(request,response);
//            log.info("匹配成功"+request.getRequestURI());
            return;
        }
        log.info("拦截到："+request.getRequestURI());
        //需要判断并成功的操作，即判断登录状态(后台员工管理)
        Object employee = request.getSession().getAttribute("employee");
        if (null != employee){
            /**这里添加ThreaLocalhost*/
            BaseContest.setId((Long) employee);
            //这说明已经登陆了，可以放行
            filterChain.doFilter(request,response);
            return;
        }
        //需要判断并成功的操作，即判断登录状态（前台用户管理）
        Object user = request.getSession().getAttribute("user");
        if (null != user ){
            /**这里添加ThreaLocalhost*/
            BaseContest.setId((Long) user);
            //这说明已经登陆了，可以放行
            filterChain.doFilter(request,response);
            return;
        }
        //需要判断并失败的操作，则通过流对象写回客户端
        log.info("非法登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    //专门抽出这个方法用于匹配字符串
    private boolean check(String url){
        for (String s : URLS) {
            boolean match = PATH_MATCHER.match(s, url);//如何返回true表示匹配成功
            if (match) return true;
        }
        return false;
    }
}
