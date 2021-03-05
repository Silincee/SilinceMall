package cn.silince.silincemall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.coupon.entity.SeckillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 18:52:19
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

