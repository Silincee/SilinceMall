package cn.silince.silincemall.member.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("silincemall-coupon") // 注册中心中想要调用的服务名
public interface CouponFeignService {

    /**
     * @description: 返回当前会员所有优惠券
     */
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
