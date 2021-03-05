package cn.silince.silincemall.search.vo;

import cn.silince.common.to.es.SkuEsModel;
import lombok.Data;
import sun.plugin.dom.core.Attr;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: SilinceMall
 * @description: 检索返回的结果vo
 * @author: Silince
 * @create: 2021-02-20 22:32
 **/
@Data
public class SearchResult {
    private List<SkuEsModel> products; // 查询到的所有商品信息

    /**
    * @description: 以下是分页信息
    */
    private Long total; // 总记录数
    private Integer PageNum; // 当前页
    private Integer totalPages; // 总页数
    private List<Integer> pageNavs; // 导航页码

    private List<BrandVo> brands; // 当前查询到的结果，所有涉及到的所有品牌
    private List<CatalogVo> catalogs; // 当前查询到的结果，所有涉及到的所有分类
    private List<AttrVo> attrs; // 当前查询到的结果，所有涉及到的所有属性

    //==============以上是返回给页面的所有信息============================

    // 面包屑导航数据
    private List<NavVo> navs = new ArrayList<>();
    private List<Long> attrIds = new ArrayList<>();

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }


    @Data
    public static class  BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class  AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class  CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
}
