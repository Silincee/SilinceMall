package cn.silince.silincemall.product.vo;

import cn.silince.silincemall.product.entity.SkuImagesEntity;
import cn.silince.silincemall.product.entity.SkuInfoEntity;
import cn.silince.silincemall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: SilinceMall
 * @description: sku商品详情页Vo
 * @author: Silince
 * @create: 2021-02-23 12:40
 **/
@Data
public class SkuItemVo {
    // 1 sku基本信息获取 pms_sku_info
    private SkuInfoEntity info;

    // 2 sku的图片信息 pms_sku_images
    private List<SkuImagesEntity> images;

    // 3 获取spu的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    // 4 获取spu的介绍
    private SpuInfoDescEntity desp;

    // 5 获取spu的规格参数信息
    private  List<SpuItemAttrGroupVo> groupAttrs;

    // 6 是否有货
    private boolean hasStock=true;

    // 秒杀信息
    SeckillInfo seckillInfo;


}
