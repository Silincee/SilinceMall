package cn.silince.silincemall.product.service;

import cn.silince.silincemall.product.entity.SpuInfoDescEntity;
import cn.silince.silincemall.product.vo.SpuSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 17:00:18
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);


    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);

    /**
     * @description: 根据skuId返回spu信息
     */
    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

