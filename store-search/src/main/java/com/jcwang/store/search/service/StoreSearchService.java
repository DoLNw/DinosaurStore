package com.jcwang.store.search.service;

import com.jcwang.store.search.vo.SearchParam;
import com.jcwang.store.search.vo.SearchResult;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.stereotype.Service;

@Service
public interface StoreSearchService {

    /**
     * @param searchParam 检索参数
     * @return 检索结果
     */
    SearchResult search(SearchParam searchParam);
}
