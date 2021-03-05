package cn.silince.silincemall.order.vo;

import lombok.Data;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-18 16:11
 **/
@Data
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;
}
