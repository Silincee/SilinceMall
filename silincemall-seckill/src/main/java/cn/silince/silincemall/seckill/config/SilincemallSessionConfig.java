package cn.silince.silincemall.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @program: SilinceMall
 * @description: 解决子域session共享问题
 * @author: Silince
 * @create: 2021-02-25 16:10
 **/
@Configuration
@EnableRedisHttpSession
public class SilincemallSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SILINCESESSION");
        serializer.setDomainName("silincemall.com"); // 放到大父域名
        return serializer;
    }

    /** 
    * @description: 默认序列化器 
    */ 
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
