package cn.silince.silincemall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: SilinceMall
 * @description: 购物项
 * @author: Silince
 * @create: 2021-02-27 17:34
 **/
@Data
public class OrderItemVo {
    private Long skuId; // 商品id
    private Boolean check; // 是否被选中，默认true
    private String title; // 商品标题
    private String image; // 商品图片
    private List<String> skuAttr; // 商品套餐信息
    private BigDecimal price; // 价格
    private Integer count;// 数量
    private BigDecimal totalPrice; // 总价 需要自己提供get方法

    private BigDecimal weight; // 商品重量
}
