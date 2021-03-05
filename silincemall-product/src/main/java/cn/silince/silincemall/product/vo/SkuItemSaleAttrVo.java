package cn.silince.silincemall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-23 22:27
 **/
@Data
@ToString
public  class SkuItemSaleAttrVo{
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
