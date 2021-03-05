package cn.silince.silincemall.seckill.config;

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
public class MyRedissonConfig {

    /**
    * @description: 所有对Redisson的使用都是通过RedissonClient对象
    */
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
        // 1 创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://47.97.191.157:6379");
        // 2 根据Config创建出RedissonClient实例
        return Redisson.create(config);
    }
}
