package cn.silince.silincemall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-28 22:12
 **/
@Data
public class SkuWareHasStock {
    private Long skuId;
    private Integer num;
    private List<Long> wareId;
}
