package cn.silince.silincemall.coupon.dao;

import cn.silince.silincemall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 18:52:19
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {
	
}
