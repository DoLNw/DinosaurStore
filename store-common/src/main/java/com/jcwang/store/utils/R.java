/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.jcwang.store.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	/**
	 * 远程调用的，拿到R，再取出来麻烦，所以可以加个范型
	 */
//	private T data;
//	public T getData() {
//		return data;
//	}
//	public void setData(T data) {
//		this.data = data;
//	}
	
	public R() {
		put("code", 0);
		put("msg", "success");
	}

	/**
	 * 链式编程
 	 */
	public R setData(Object data) {
		put("data", data);
		return this;
	}

	public <T> T getData(String name, TypeReference<T> typeReference) {
		T data = JSON.parseObject(JSON.toJSONString(get(name)), typeReference);
		return data;
	}

	/**
	 * 利用fastjson进行逆转
	 * @param typeReference
	 * @return
	 * @param <T>
	 */
	public <T> T getData(TypeReference<T> typeReference) {
		T data = JSON.parseObject(JSON.toJSONString(get("data")), typeReference);
		return data;
	}

	public int getCode() {
		return (int) get("code");
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
