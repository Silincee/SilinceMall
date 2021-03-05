package cn.silince.silincemall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 18:52:19
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

