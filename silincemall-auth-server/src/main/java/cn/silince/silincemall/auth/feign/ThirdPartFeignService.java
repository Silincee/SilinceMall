package cn.silince.silincemall.auth.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: SilinceMall
 * @description: 第三方服务远程调用接口
 * @author: Silince
 * @create: 2021-02-24 19:23
 **/
@FeignClient("silincemall-third-party")
public interface ThirdPartFeignService {

    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
