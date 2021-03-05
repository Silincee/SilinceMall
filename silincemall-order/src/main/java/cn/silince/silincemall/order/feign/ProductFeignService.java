package cn.silince.silincemall.order.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-28 17:04
 **/
@FeignClient("silincemall-product")
public interface ProductFeignService {

    /**
     * @description: 根据skuId返回spu信息
     */
    @PostMapping("/product/spuinfo/skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId);
}
