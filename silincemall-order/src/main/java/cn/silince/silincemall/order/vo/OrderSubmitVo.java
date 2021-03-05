package cn.silince.silincemall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: SilinceMall
 * @description: 封装订单提交的数据 无需提交需要购买的商品，要去购物车再查一遍最新的
 * @author: Silince
 * @create: 2021-02-28 14:43
 **/
@Data
public class OrderSubmitVo {

    private Long addrId; // 收货地址的id
    private Integer payType; // 支付方式
    private String orderToken; // 防重令牌
    private BigDecimal payPrice; // 应付价格 用于验价
    private String note; // 订单备注
    // 用户相关信息，直接去session取出登陆的用户

}
