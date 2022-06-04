package com.zhuhe.hotel.config;

import com.zhuhe.hotel.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


//这个配置类直接就可以
@Slf4j
@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    /**
     *设置静态页面映射资源
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        log.info("配置静态资源");
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展mvc的消息转换器（不会）
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建消息转化器对象
        MappingJackson2HttpMessageConverter mess = new MappingJackson2HttpMessageConverter();
        //设置对象转化器，底层使用Json将java转成json串格式
        mess.setObjectMapper(new JacksonObjectMapper());
        //将上面消息转化器对象追加到mvc框架集合中
        super.extendMessageConverters(converters);
        converters.add(0,mess);//0是一个索引，目的是前提转换器
    }
}
