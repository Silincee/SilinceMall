package cn.silince.silincemall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: SilinceMall
 * @description: Feign拦截器
 * @author: Silince
 * @create: 2021-02-27 22:41
 **/
@Configuration
public class SilinceFeignConfig {

    @Bean("requestInterceptor") // 定义拦截器的名字
    public RequestInterceptor requestInterceptor(){

        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate requestTemplate) {

                // 1 使用 RequestContextHolder 拿到刚进来的这个请求的所有属性(底层用的还是ThreadLocal)
                ServletRequestAttributes attributes
                        = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
              if (attributes!=null){ // 没有attributes(无需登陆信息)时不需要重新添加请求头
                  HttpServletRequest request = attributes.getRequest(); // 老请求
                  if (request!=null){
                      // 2 给新请求同步请求头数据 Cookie
                      requestTemplate.header("Cookie",request.getHeader("Cookie"));
                  }

                  System.out.println("feign远程调用之前先进行RequestInterceptor.apply");
              }

            }
        };
    }
}
