package cn.silince.silincemall.auth.congfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: SilinceMall
 * @description: 发送一个请求直接跳转到一个页面
 * @author: Silince
 * @create: 2021-02-24 17:27
 **/
@Configuration
public class SilincemallWebConfig implements WebMvcConfigurer {

    /**
    * @description: 视图映射
    */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/reg.html").setViewName("reg");
    }
}
