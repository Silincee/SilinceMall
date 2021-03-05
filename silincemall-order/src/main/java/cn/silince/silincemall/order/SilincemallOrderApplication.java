package cn.silince.silincemall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableRabbit
@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableFeignClients
@EnableAspectJAutoProxy(exposeProxy=true)
public class SilincemallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SilincemallOrderApplication.class, args);
    }

}
