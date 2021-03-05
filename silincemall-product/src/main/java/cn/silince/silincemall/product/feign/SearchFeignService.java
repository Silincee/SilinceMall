package cn.silince.silincemall.product.feign;

import cn.silince.common.to.es.SkuEsModel;
import cn.silince.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-18 17:23
 **/
@FeignClient("silincemall-search")
public interface SearchFeignService {
    /**
     * @description: 上架商品
     */
    @RequestMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
