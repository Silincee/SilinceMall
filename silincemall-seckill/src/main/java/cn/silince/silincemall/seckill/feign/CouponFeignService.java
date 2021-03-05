package cn.silince.silincemall.seckill.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("silincemall-coupon")
public interface CouponFeignService {
    /**
     * @description: 查询最近三天的秒杀活动
     */
    @GetMapping("/coupon/seckillsession/lates3DaySession")
    public R getLates3DaySession();
}
