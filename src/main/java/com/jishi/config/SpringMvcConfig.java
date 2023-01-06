package com.jishi.config;

import com.jishi.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class SpringMvcConfig extends WebMvcConfigurationSupport {

    //静态资源放行
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        log.info("静态文件已放行");
       //将所有访问backend开头的任何目录重定向访问backend（内含index）,替代static目录
        //不打上index居然不会自动访问
        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");

        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");

    }

    //添加扩展SpringMVC的默认消息转换器
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置转换器使用Jackson进行java对象序列化
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //添加该转换器到mvc转换器集合中，并把顺序设置为第一个
        converters.add(0,messageConverter);

    }
}
