package cn.silince.silincemall.product.vo;

import lombok.Data;
import sun.plugin.dom.core.Attr;

/**
 * @program: SilinceMall
 * @description: 属性响应数据
 * @author: Silince
 * @create: 2021-02-10 22:30
 **/
@Data
public class AttrRespVo extends AttrVo {

    /**
    * @description: 所属分类名字
    */
    private String catelogName;

    /**
    * @description: 所属分组名字
    */
    private String groupName;
    
    /** 
    * @description: 分类的完整路径
    */
    private Long[] catelogPath;
}
