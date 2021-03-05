package cn.silince.silincemall.coupon.dao;

import cn.silince.silincemall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 18:52:19
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
