package cn.silince.silincemall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cn.silince.silincemall.member.feign")
@SpringBootApplication
@EnableRedisHttpSession
public class SilincemallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(SilincemallMemberApplication.class, args);
    }

}
