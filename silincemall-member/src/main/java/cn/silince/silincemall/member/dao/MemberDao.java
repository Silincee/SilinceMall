package cn.silince.silincemall.member.dao;

import cn.silince.silincemall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:03:11
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
