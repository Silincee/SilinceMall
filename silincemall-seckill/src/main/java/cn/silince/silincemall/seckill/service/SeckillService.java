package cn.silince.silincemall.seckill.service;

import cn.silince.silincemall.seckill.to.SecKillRedisTo;

import java.util.List;

/**
 * @program: SilinceMall
 * @description:
 * @author: Silince
 * @create: 2021-03-03 17:07
 **/
public interface SeckillService {
    /**
     * @description: 每天晚上3点；上架最近三天需要秒杀的商品。
     *                  当天00:00:00  - 23:59:59
     *                  明天00:00:00  - 23:59:59
     *                  后天00:00:00  - 23:59:59
     */
    void uploadSeckullSkuLatest3Days();

    /**
     * @description: 返回当前时间可以参与秒杀的商品
     */
    List<SecKillRedisTo> getCurrentSeckillSkus();

    /**
     * @description:  获取某个sku商品的秒杀预告信息
     */
    SecKillRedisTo getSkuSeckillInfo(Long skuId);

    /**
     * @description: 秒杀商品 创建订单号
     */
    String kill(String killId, String key, Integer num);
}
