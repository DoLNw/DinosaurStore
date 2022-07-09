package com.jcwang.store.search.controller;

import com.jcwang.store.search.service.StoreSearchService;
import com.jcwang.store.search.vo.SearchParam;
import com.jcwang.store.search.vo.SearchResult;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {

    @Autowired
    StoreSearchService storeSearchService;

    /**
     * 自动将提交过来的所有请求参数封装成指定的对象
     * @param searchParam
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request) {

        searchParam.set_queryString(request.getQueryString());

        //1、根据传递来的页面的查询参数，去es中检索商品
        SearchResult result = storeSearchService.search(searchParam);

        model.addAttribute("result", result);

        return "list";
    }
}
