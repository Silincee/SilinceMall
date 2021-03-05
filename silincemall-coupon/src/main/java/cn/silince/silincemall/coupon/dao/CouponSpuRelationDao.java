package cn.silince.silincemall.coupon.dao;

import cn.silince.silincemall.coupon.entity.CouponSpuRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券与产品关联
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 18:52:18
 */
@Mapper
public interface CouponSpuRelationDao extends BaseMapper<CouponSpuRelationEntity> {
	
}
