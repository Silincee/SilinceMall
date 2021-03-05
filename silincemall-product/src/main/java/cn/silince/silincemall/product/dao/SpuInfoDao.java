package cn.silince.silincemall.product.dao;

import cn.silince.silincemall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 17:00:18
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /** 
    * @description: 修改商品上架状态
    */ 
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code")int code);
}
