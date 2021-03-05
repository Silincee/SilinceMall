package cn.silince.silincemall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SilincemallThirdPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SilincemallThirdPartyApplication.class, args);
    }

}
