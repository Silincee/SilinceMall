package cn.silince.silincemall.seckill.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("silincemall-product")
public interface ProductFeignService {

    /**
    * @description: 查询商品的详细信息
    */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R skuInfo(@PathVariable("skuId") Long skuId);
}
