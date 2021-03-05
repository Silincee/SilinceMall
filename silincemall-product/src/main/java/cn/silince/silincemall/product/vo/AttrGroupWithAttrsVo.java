package cn.silince.silincemall.product.vo;

import cn.silince.silincemall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

/**
 * @program: SilinceMall
 * @description: 商品系统17-获取分类下所有分组和关联属性
 * @author: Silince
 * @create: 2021-02-13 17:08
 **/
@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
    * @description: 用于封装属性的集合
    */
    private List<AttrEntity> attrs;
}
