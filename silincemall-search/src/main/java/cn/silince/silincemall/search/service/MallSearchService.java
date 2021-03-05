package cn.silince.silincemall.search.service;

import cn.silince.silincemall.search.vo.SearchParam;
import cn.silince.silincemall.search.vo.SearchResult;

public interface MallSearchService {
    
    /** 
    * @description: 返回检索的结果
    * @param: [searchParam] 检索的所有参数
    * @return: search.vo.SearchResult 里面包含页面需要的所有信息
    * @author: Silince 
    * @date: 2/20/21 
    */
    SearchResult search(SearchParam searchParam);
}
