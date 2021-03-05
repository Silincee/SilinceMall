package cn.silince.silincemall.product.service;

import cn.silince.silincemall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.silince.common.utils.PageUtils;
import cn.silince.silincemall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author silince
 * @email zhangjianlincn@outlook.com
 * @date 2021-02-04 17:00:18
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
    * @description: 查出所有分类以及子分类,以树形结构组装起来
     * @param: []
    * @return: java.util.List<cn.silince.silincemall.product.entity.CategoryEntity>
    * @author: Silince
    * @date: 2/5/21
    */
    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    /**
    * @description: 找到catelogId的完整路径
    * @param: [catelogId]
    * @return: java.lang.Long[] [父/子/孙]
    * @author: Silince
    * @date: 2/10/21
    */
    Long[] findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    /**
    * @description: 查出所有的一级分类
    */
    List<CategoryEntity> getLevel1Categorys();

    /**
    * @description: 查出所有的分类，按照要求组织成json
    */
    Map<String, List<Catelog2Vo>> getCatalogJson();
}

