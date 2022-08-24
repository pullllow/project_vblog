package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Chang Qi
 * @date 2022/8/20 14:35
 * @description 跨域问题
 * @Version V1.0
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {


        // addMapping 设置允许跨域的路径
        // allowedOriginPatterns 设置允许跨域请求的域名
        // allowedMethods 设置允许方法
        // allowCredentials 是否允许证书
        // allowedHeaders 设置允许的Header属性
        // maxAge 准备响应前的缓存持续的最大时间（以秒为单位）。
        registry.addMapping("/**")
                //.allowedOrigins("*")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }

}
