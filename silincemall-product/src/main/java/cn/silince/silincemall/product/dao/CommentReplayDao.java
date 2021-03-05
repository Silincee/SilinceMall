package cn.silince.silincemall.product.dao;

import cn.silince.silincemall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 17:00:18
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {
	
}
