package cn.silince.silincemall.product.feign;

import cn.silince.common.utils.R;
import cn.silince.silincemall.product.feign.fallback.SeckillFeignServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value="silincemall-seckill",fallback = SeckillFeignServiceFallback.class)
public interface SeckillFeignService {
    /**
     * @description:  获取某个sku商品的秒杀预告信息
     */
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
