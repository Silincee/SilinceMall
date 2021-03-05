package cn.silince.silincemall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.coupon.entity.SeckillSkuRelationEntity;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 18:52:19
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

