package cn.silince.silincemall.search.controller;

import cn.silince.common.exception.BizCodeEnume;
import cn.silince.common.to.es.SkuEsModel;
import cn.silince.common.utils.R;
import cn.silince.silincemall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-18 17:00
 **/
@RestController
@Slf4j
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Resource
    private ProductSaveService productSaveService;

    /**
     * @description: 上架商品
     */
    @RequestMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuEsModels);
        } catch (IOException e) {
            log.error("ElasticSaveController商品上架错误:{} ", e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }

        if (!b) {
            return R.ok();
        } else {
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMsg());
        }


    }


}
