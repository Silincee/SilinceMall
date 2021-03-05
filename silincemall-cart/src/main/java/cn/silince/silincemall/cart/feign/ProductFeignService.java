package cn.silince.silincemall.cart.feign;

import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("silincemall-product")
public interface ProductFeignService {

    /**
    * @description: 根据skuId查询商品详细信息
    */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * @description: 根据skuId得到所有销售属性值
     */
    @GetMapping("/product/skusaleattrvalue/stringlist/{skuId}")
    public List<String> getSkuSaleAttrValuesAsStringList(@PathVariable("skuId") Long skuId);

    /**
     * @description: 根据skuId获取商品的价格
     */
    @GetMapping("/product/skuinfo/{skuId}/price")
    public R getPrice(@PathVariable("skuId")Long skuId);
}
