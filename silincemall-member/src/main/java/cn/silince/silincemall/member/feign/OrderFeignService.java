package cn.silince.silincemall.member.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-03-03 12:01
 **/
@FeignClient("silincemall-order")
public interface OrderFeignService {

    /**
     * 分页查询当前登陆用户的所有订单
     */
    @PostMapping("/order/order/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params);
}
