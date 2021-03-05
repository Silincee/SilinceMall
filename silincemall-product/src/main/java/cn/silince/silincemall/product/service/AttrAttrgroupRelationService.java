package cn.silince.silincemall.product.service;

import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.product.entity.AttrAttrgroupRelationEntity;
import cn.silince.silincemall.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 17:00:18
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatch(List<AttrGroupRelationVo> vos);
}

