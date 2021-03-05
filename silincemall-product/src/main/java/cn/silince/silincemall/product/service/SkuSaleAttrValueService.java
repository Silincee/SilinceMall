package cn.silince.silincemall.product.service;

import cn.silince.silincemall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-10 20:10:33
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /** 
    * @description: 根据spu查询当前spu包含的sku的所有属性组合 
    */ 
    List<SkuItemSaleAttrVo> getSaleAttrBySpuId(Long spuId);

    /** 
    * @description: 根据skuId得到所有销售属性值 
    * @param: [skuId] 
    * @return: java.util.List<java.lang.String> 
    * @author: Silince 
    * @date: 2/26/21 
    */
    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

