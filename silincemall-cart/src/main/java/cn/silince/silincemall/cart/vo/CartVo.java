package cn.silince.silincemall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: SilinceMall
 * @description: 购物车 所有项都要手动计算 除了items
 * @author: Silince
 * @create: 2021-02-25 23:41
 **/
public class CartVo {

    private List<CartItemVo> items; // 商品项集合
    private Integer countNum; // 商品数
    private Integer countType; // 商品类型数
    private BigDecimal totalAmount; // 商品总价
    private BigDecimal reduce=new BigDecimal("0.00"); // 减免价格

    public List<CartItemVo> getItems() {
        return items;
    }

    public void setItems(List<CartItemVo> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count=0;
        if (this.items!=null&&items.size()>0){
            for (CartItemVo item : items) {
                count+=item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {

        int count=0;
        if (this.items!=null&&items.size()>0){
            for (CartItemVo item : items) {
                count++;
            }
        }
        return count;
    }


    public BigDecimal getTotalAmount() {

        BigDecimal amount = new BigDecimal("0");
        if (this.items!=null&&items.size()>0){
            for (CartItemVo item : items) {
               if (item.getCheck()){
                   BigDecimal totalPrice = item.getTotalPrice();
                   amount=amount.add(totalPrice);
               }
            }
        }
        return amount.subtract(this.getReduce());
    }


    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduces) {
        this.reduce = reduces;
    }
}
