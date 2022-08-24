package com.example.config;
/*
 *  @author changqi
 *  @date 2022/8/18 22:14
 *  @description
 *  @Version V1.0
 */

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.mapper")
public class MyBatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //设置请求的页面大于最后页后操作， true回到首页， false继续请求 默认false
        //paginationInterceptor.setOverflow(false);
        //设置最大单页限制数量，默认500条，-1不受限制
        //paginationInterceptor.setMaxLimit
        return paginationInterceptor;
    }

}
