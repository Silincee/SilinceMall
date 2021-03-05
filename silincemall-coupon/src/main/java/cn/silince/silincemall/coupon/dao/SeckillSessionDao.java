package cn.silince.silincemall.coupon.dao;

import cn.silince.silincemall.coupon.entity.SeckillSessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 秒杀活动场次
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 18:52:19
 */
@Mapper
public interface SeckillSessionDao extends BaseMapper<SeckillSessionEntity> {
	
}
