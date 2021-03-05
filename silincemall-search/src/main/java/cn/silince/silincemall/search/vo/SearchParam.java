package cn.silince.silincemall.search.vo;

import lombok.Data;
import sun.plugin.dom.core.Attr;

import java.util.List;

/**
 * @program: SilinceMall
 * @description: 封装页面所有可能传递过来的检索条件vo
 * @author: Silince
 * @create: 2021-02-20 22:08
 **/
@Data
public class SearchParam {

    private String keyword; // 页面传递过来的全文匹配关键字
    private Long catelog3Id; // 三级分类id

    /**
    * @description: 排序条件
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
    */
    private String sort;

    /**
    * @description: 过滤条件
     * hasStock、skuPrice区间、brandId、catalogId、attrs
     * hasStock=0/1
     * skuPrice=1_500/_500/500_
     * brandId=1
     * attrs=2_5寸:6寸
    */
    private Integer hasStock; // 是否只显示有货 0-无 1-有
    private String skuPrice; // 价格区间
    private List<Long> brandId; // 品牌id,可以多选
    private List<String> attrs; // 属性，可以多选
    private Integer pageNum=1; // 页码


    private String _queryString; // 原生的所有查询条件
}
