package cn.silince.silincemall.product.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-20 19:20
 **/

@EnableConfigurationProperties(CacheProperties.class)    // 开启属性配置的绑定功能
@Configuration
@EnableCaching
public class MyCacheConfig {

    @Autowired
    private CacheProperties cacheProperties;

    /**
    *  配置文件中的东西没有用上？
     *  1 原来和配置文件绑定的配置累是这样子的
     *  @ConfigurationProperties(prefix = "spring.cache")
     *  public class CacheProperties {...}
     *  2 要让他生效需要加上注解 @EnableConfigurationProperties(CacheProperties.class)
     *  然后自动注入即可
    */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(){

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        config = config.serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));

        // ⚠️ 使用了该配置类就不会使用配置文件中的内容了
        // 将配置文件 org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration 中的所有配置都生效
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }


        return config;
    }
}
