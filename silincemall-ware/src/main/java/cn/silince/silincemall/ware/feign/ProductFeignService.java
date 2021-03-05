package cn.silince.silincemall.ware.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("silincemall-product")
public interface ProductFeignService {

    /**
     * 1) 让所有请求过网关：
     *  1 @FeignClient("silincemall-gateway") 给silincemall-gateway所在机器发请求
     *  2 /api/product/skuinfo/info/{skuId}
     *
     * 2）直接让后台指定服务处理： @FeignClient("silincemall-product") /product/skuinfo/info/{skuId}
     *
    */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);
}
