package cn.silince.silincemall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-03-03 22:33
 **/
@Data
public class SeckillInfo {
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;


    private Long startTime; // 当前商品的开始时间
    private Long endTime;// 当前商品的结束时间
    private String randomCode; // 秒杀随机码
}
