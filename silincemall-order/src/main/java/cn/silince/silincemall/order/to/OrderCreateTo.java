package cn.silince.silincemall.order.to;

import cn.silince.silincemall.order.entity.OrderEntity;
import cn.silince.silincemall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-02-28 15:40
 **/
@Data
public class OrderCreateTo {

    private OrderEntity order;
    private List<OrderItemEntity> orderItems;
    private BigDecimal payPrice; //订单计算的应付价格
    private BigDecimal fare; // 运费
}
