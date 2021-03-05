package cn.silince.silincemall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient // 开启服务注册发现 nacos
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // 排除与数据源有关的配置，因为common中引入了mybatis
public class SilincemallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SilincemallGatewayApplication.class, args);
    }

}
