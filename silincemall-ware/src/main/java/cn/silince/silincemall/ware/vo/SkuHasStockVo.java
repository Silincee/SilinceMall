package cn.silince.silincemall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-18 16:11
 **/
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;
}
