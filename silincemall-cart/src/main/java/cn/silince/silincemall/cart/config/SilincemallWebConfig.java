package cn.silince.silincemall.cart.config;

import cn.silince.silincemall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-26 03:27
 **/
@Configuration
public class SilincemallWebConfig implements WebMvcConfigurer {

    /**
    * @description: 添加拦截器
    */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // CartInterceptor拦截器拦截当前服务的所有请求
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
