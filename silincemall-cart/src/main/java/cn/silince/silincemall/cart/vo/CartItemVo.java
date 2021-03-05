package cn.silince.silincemall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: SilinceMall
 * @description: 购物项
 * @author: Silince
 * @create: 2021-02-25 23:41
 **/
public class CartItemVo {
    
    private Long skuId; // 商品id
    private Boolean check=true; // 是否被选中，默认true
    private String title; // 商品标题
    private String image; // 商品图片
    private List<String> skuAttr; // 商品套餐信息
    private BigDecimal price; // 价格
    private Integer count;// 数量
    private BigDecimal totalPrice; // 总价 需要自己提供get方法

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /** 
    * @description: 计算当前项总价
    */ 
    public BigDecimal getTotalPrice() {
        
        return this.price.multiply(new BigDecimal(""+this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
