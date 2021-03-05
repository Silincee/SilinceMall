package cn.silince.silincemall.ware.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("silincemall-order")
public interface OrderFeignService {

    /**
     * @description: 根据订单号查询订单详细信息
     */
    @GetMapping("/order/order/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
