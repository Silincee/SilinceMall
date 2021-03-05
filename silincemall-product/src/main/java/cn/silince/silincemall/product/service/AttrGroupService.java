package cn.silince.silincemall.product.service;

import cn.silince.silincemall.product.vo.Attr;
import cn.silince.silincemall.product.vo.AttrGroupWithAttrsVo;
import cn.silince.silincemall.product.vo.SkuItemVo;
import cn.silince.silincemall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 17:00:18
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /** 
    * @description: 获取分类属性分组
    */ 
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);

    /** 
    * @description: 根据spuId获取该spu的所有属性分组 以及 当前分组下所有属性对应的值 
    */ 
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

