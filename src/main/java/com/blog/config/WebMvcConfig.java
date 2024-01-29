package com.blog.config;

import com.blog.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.io.File;
import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    private AppConfig appConfig;

    /**
     * 静态资源处理
     **/
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {

        String webPath = appConfig.getResPhysicalPath();

        File file = new File(webPath + "/logistics/");
        if (!file.exists()) {
            file.mkdirs();

        }
        log.info("开始进行静态资源映射...");
        log.info("静态资源处理：" + webPath + "/logistics/");

        registry.addResourceHandler("/logistics/**").addResourceLocations("file:" + webPath + "/logistics/");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转化器，底层使用jackson将java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合当中(index设置为0，表示设置在第一个位置，避免被其它转换器接收，从而达不到想要的功能)
        converters.add(0, messageConverter);
    }
}