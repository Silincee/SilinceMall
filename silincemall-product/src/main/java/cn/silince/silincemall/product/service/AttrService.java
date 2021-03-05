package cn.silince.silincemall.product.service;

import cn.silince.silincemall.product.vo.AttrGroupRelationVo;
import cn.silince.silincemall.product.vo.AttrRespVo;
import cn.silince.silincemall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-10 20:10:33
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId,String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /** 
    * @description: 过滤出attrIds集合中可以被检索的attrId集合 
    */ 
    List<Long> selectSearchAttrsIds(List<Long> attrIds);
}

