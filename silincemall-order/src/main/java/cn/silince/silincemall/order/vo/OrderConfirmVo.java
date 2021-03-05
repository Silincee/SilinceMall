package cn.silince.silincemall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @program: SilinceMall
 * @description: 订单确认页需要用到的数据
 * @author: Silince
 * @create: 2021-02-27 17:29
 **/

public class OrderConfirmVo {

    @Getter
    @Setter
    private List<MemberAddressVo> address;// 收货地址列表
    @Getter
    @Setter
    private List<OrderItemVo> items; // 所有选中的购物项
    @Getter
    @Setter
    private Integer integration; // 会员积分信息
    @Getter
    @Setter
    private String orderToken; // 防重令牌，防止重复提交
    @Getter
    @Setter
    Map<Long,Boolean> stocks; // 库存


    // 订单总额
    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items!=null){
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    // 应付价格
    public BigDecimal getPayPrice() {
        return this.getTotal();
    }

    public Integer getCount(){
        Integer i = 0;
        if (items!=null){
            for (OrderItemVo item : items) {
                i+=item.getCount();
            }
        }
        return i;
    }
}
