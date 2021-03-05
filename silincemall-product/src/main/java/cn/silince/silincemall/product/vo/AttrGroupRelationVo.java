package cn.silince.silincemall.product.vo;

import lombok.Data;

/**
 * @program: SilinceMall
 * @description: 12、删除属性与分组的关联关系
 * @author: Silince
 * @create: 2021-02-12 22:49
 **/
@Data
public class AttrGroupRelationVo {

    /**
    * @description: 属性id
    */
    private Long attrId;

    /**
     * @description: 属性分组id
     */
    private Long attrGroupId;
}
