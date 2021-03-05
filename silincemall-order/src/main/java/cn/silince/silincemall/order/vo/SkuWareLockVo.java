package cn.silince.silincemall.order.vo;

import lombok.Data;

import java.util.List;

/**
 * @program: SilinceMall
 * @description: sku库存锁定vo
 * @author: Silince
 * @create: 2021-02-28 19:45
 **/
@Data
public class SkuWareLockVo {

    private String orderSn; // 订单号
    private List<OrderItemVo> locks; //需要锁住的所有库存信息
}
