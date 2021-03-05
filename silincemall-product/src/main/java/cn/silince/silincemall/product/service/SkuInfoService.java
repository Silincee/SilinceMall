package cn.silince.silincemall.product.service;

import cn.silince.silincemall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-10 20:10:33
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkuBySpuId(Long spuId);

    /**
    * @description: 返回商品详情也sku详细信息,封装到 SkuItemVo 中
    * @param: [skuId]  sku的id
    * @return: cn.silince.silincemall.product.vo.SkuItemVo
    * @author: Silince
    * @date: 2/23/21
    */
    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;
}

