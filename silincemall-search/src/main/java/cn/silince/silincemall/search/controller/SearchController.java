package cn.silince.silincemall.search.controller;

import cn.silince.silincemall.search.service.MallSearchService;
import cn.silince.silincemall.search.vo.SearchParam;
import cn.silince.silincemall.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @program: SilinceMall
 * @description: 主页三级分类页面跳转
 * @author: Silince
 * @create: 2021-02-20 21:39
 **/
@Controller
public class SearchController {

    @Resource
    private MallSearchService mallSearchService;

    /**
     * @description: springMVC 会自动将页面提交过来的所有请求查询参数封装成指定的对象
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request){

        String queryString = request.getQueryString();
        searchParam.set_queryString(queryString);
        // 1 根据传递来的页面的查询参数，去es中检索商品
        SearchResult result = mallSearchService.search(searchParam);
        model.addAttribute("result",result);
        return "list";
    }

}
