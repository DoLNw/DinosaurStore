package com.jcwang.store.search;

import com.alibaba.fastjson.JSON;
import com.jcwang.store.search.config.StoreElasticSearchConfig;
import lombok.Data;
import org.apache.catalina.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreSearchApplicationTests {

	/**
	 * 一
	 * {
	 *     skuId: 1
	 *     skuTitle: 华为1
	 *     price: 998
	 *     saleCount: 99
	 *     attrs: [
	 *     		{尺寸: 5寸}
	 *     		{CPU: 高通}
	 *     		{分辨率: 全高清}
	 *     ]
	 * }
	 * 冗余，浪费空间节省时间
	 *
	 * 二
	 * sku具体的手机名称价格等索引和attr规格索引分别存储，因为每个商品的规格可能会重复
	 * 首先一个环境，网页中它的属性里面一定要有东西，所以会根据spu查询到底
	 * 分开存储之后，一个手机sku，可以搭配的attr，spu有很多，要都传过去，可能会网络阻塞时间更久。虽然空间更少了
	 * 时间换空间
	 */

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Test
	public void SearchData() throws IOException {
		// 创建检索请求
		SearchRequest searchRequest = new SearchRequest();
		// 指定索引
		searchRequest.indices("bank");
		// 指定DSL，检索条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		// 构造检索条件
//		sourceBuilder.query();
//		sourceBuilder.from();
//		sourceBuilder.aggregation();
		sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
		System.out.println(sourceBuilder.toString());
		searchRequest.source(sourceBuilder);

		// 执行索引
		SearchResponse response = restHighLevelClient.search(searchRequest, StoreElasticSearchConfig.COMMON_OPTIONS);

		// 分析结果
		System.out.println(response);
	}

	/**
	 * 测试存储数据
	 */
	@Test
	public void indexDate() throws IOException {
		IndexRequest indexRequest = new IndexRequest("user");
		indexRequest.id("1");
		User user = new User();
		user.setUserName("我是谁");
		user.setGender("M");
		user.setAge(18);
		String jsonString = JSON.toJSONString(user);
		indexRequest.source(jsonString, XContentType.JSON); // 要保存的内容

		IndexResponse indexResponse = restHighLevelClient.index(indexRequest, StoreElasticSearchConfig.COMMON_OPTIONS);

		System.out.println(indexResponse);
	}

	@Data
	class User {
		private String userName;
		private String gender;
		private Integer age;
	}


	@Test
	public void contextLoads() {

	}

}
