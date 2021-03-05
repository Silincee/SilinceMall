package cn.silince.silincemall.ware.service;

import cn.silince.common.to.mq.OrderTo;
import cn.silince.common.to.mq.StockLockedTo;
import cn.silince.silincemall.ware.vo.LockStockResult;
import cn.silince.silincemall.ware.vo.SkuHasStockVo;
import cn.silince.silincemall.ware.vo.SkuWareLockVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:31:06
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    /**
     * @description: 根据skuIds返回所有sku的库存状态(skuId,hasStock)
     */
    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    /**
     * @description: 为订单锁库存
     */
    Boolean orderLockStock(SkuWareLockVo vo);

    /** 
    * @description: 解锁库存 -- 自身的消息
    */ 
    void unlockStock(StockLockedTo to);

    /**
     * @description: 解锁库存 -- 来自订单关闭的消息
     */
    void unlockStock(OrderTo orderTo);
}

