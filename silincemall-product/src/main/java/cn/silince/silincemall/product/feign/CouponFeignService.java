package cn.silince.silincemall.product.feign;

import cn.silince.common.to.SkuReductionTo;
import cn.silince.common.to.SpuBoundsTo;
import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-14 23:37
 **/
@FeignClient("silincemall-coupon") // 注册中心中想要调用的服务名
public interface CouponFeignService {


    /**
    * 1. CouponFeignService.saveSpuBounds(spuBoundsTo);
     *  1) @RequestBody 将这个对象转为json
     *  2）找到silincemall-coupon服务，给/coupon/spubounds/save发送请求
     *     将上一步转的json放在请求体位置，发送请求
     *  3）对方服务收到请求，请求体里有json数据
     *   (@RequestBody SpuBoundsEntity spuBounds);将请求体的json转为SpuBoundsEntity；
     *
     * 因此 只要json数据模型是兼容的，双方服务无需使用同一个to
    */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
