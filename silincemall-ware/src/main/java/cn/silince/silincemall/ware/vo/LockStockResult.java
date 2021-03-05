package cn.silince.silincemall.ware.vo;

import lombok.Data;

/**
 * @program: SilinceMall
 * @description: 库存锁定结果
 * @author: Silince
 * @create: 2021-02-28 19:49
 **/
@Data
public class LockStockResult {

    private Long skuId;
    private Integer num;
    private Boolean locked;
}
