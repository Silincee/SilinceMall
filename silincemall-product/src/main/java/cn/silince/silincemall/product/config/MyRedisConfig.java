package cn.silince.silincemall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-20 15:18
 **/
@Configuration
public class MyRedisConfig {

    /**
    * @description: 所有对Redisson的使用都是通过RedissonClient对象
    */
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://47.97.191.157:6379");
        return Redisson.create(config);
    }
}
