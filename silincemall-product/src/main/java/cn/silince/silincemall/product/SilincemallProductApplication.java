package cn.silince.silincemall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableCaching
@MapperScan("cn.silince.silincemall.product.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.silince.silincemall.product.feign")
@EnableRedisHttpSession // spring session
public class SilincemallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SilincemallProductApplication.class, args);
    }

}
