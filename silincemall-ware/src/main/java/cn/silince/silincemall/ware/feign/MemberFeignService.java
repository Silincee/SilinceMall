package cn.silince.silincemall.ware.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-28 00:24
 **/
@FeignClient("silincemall-member")
public interface MemberFeignService {

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    public R addrInfo(@PathVariable("id") Long id);
}
