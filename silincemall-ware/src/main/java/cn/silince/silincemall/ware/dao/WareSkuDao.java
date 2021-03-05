package cn.silince.silincemall.ware.dao;

import cn.silince.silincemall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 19:31:06
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

    /**
     * @description: 查询当前sku的总库存量
     */
    Long getSkuStock(@Param("skuId") Long skuId);

    /**
    * @description: 查询该存在该商品的所有仓库
    */
    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId, @Param("num")Integer num);


    void unlockStock(@Param("skuId")Long skuId,@Param("wareId") Long wareId,@Param("num") Integer num);
}
