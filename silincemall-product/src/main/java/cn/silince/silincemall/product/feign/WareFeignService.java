package cn.silince.silincemall.product.feign;

import cn.silince.common.utils.R;
import cn.silince.silincemall.product.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("silincemall-ware")
public interface WareFeignService {

    /**
    *  1. R设计的时候可以加上泛型 ⭐️当前使用这种方式
     * 2。 直接返回我们想要的结果
     * 3。 自己封装解析结果
    */
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);
}
