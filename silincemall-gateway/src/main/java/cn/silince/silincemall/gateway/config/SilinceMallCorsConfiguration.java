package cn.silince.silincemall.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @program: SilinceMall
 * @description: 解决跨域问题
 * @author: Silince
 * @create: 2021-02-05 19:16
 **/
@Configuration
public class SilinceMallCorsConfiguration {

    @Bean
    public CorsWebFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //1 配置跨域
        corsConfiguration.addAllowedHeader("*"); //允许所有头可以进行跨域
        corsConfiguration.addAllowedMethod("*"); //允许所有请求方式可以进行跨域
        corsConfiguration.addAllowedOrigin("*"); //允许任意请求来源可以进行跨域
        corsConfiguration.setAllowCredentials(true); //允许携带Cookie进行跨域

        source.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsWebFilter(source);
    }
}
