package cn.silince.silincemall.seckill.to;

import cn.silince.silincemall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: SilinceMall
 * @description: redis用的商品详细信息
 * @author: Silince
 * @create: 2021-03-03 19:34
 **/
@Data
public class SecKillRedisTo {
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
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    /**
    * @description: sku的详细信息
    */
    private SkuInfoVo skuInfo;

    private Long startTime; // 当前商品的开始时间
    private Long endTime;// 当前商品的结束时间
    private String randomCode; // 秒杀随机码
}
