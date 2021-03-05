package cn.silince.silincemall.product.web;

import cn.silince.silincemall.product.service.impl.SkuInfoServiceImpl;
import cn.silince.silincemall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * @program: SilinceMall
 * @description: 商品详情
 * @author: Silince
 * @create: 2021-02-23 12:33
 **/
@Controller
public class ItemController {

    @Resource
    private SkuInfoServiceImpl skuInfoService;

    /**
    * @description: 展示当前sku的详情
    */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        System.out.println("准备查询 "+skuId+"的详情");
        SkuItemVo vo =skuInfoService.item(skuId);

        model.addAttribute("item",vo);

        return "item";
    }
}
