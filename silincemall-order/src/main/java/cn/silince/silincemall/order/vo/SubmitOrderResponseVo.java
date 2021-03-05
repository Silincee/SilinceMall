package cn.silince.silincemall.order.vo;

import cn.silince.silincemall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @program: SilinceMall
 * @description: 下单操作返回数据
 * @author: Silince
 * @create: 2021-02-28 15:07
 **/
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code; // 0-success 错误状态码
}
