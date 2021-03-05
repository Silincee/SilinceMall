package cn.silince.silincemall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-24 15:21
 **/
@ConfigurationProperties(prefix = "silincemall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
