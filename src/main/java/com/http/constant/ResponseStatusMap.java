package com.http.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * 不可更改的Map，存有HTTP响应状态码与说明
 * @author MYD
 *
 */
public class ResponseStatusMap {
	public static final Map<Integer, String> MAP = new HashMap<>();
	static {
		MAP.put(200, "OK");
		MAP.put(302, "Move temporarily");	//重定向的时候用
		MAP.put(404, "Not Found");
		MAP.put(500, "Internal Server Error");
		Collections.unmodifiableMap(MAP);
	}
	
	private ResponseStatusMap() {
		throw new AssertionError("不允许实例化com.http.constant.ResponseStatusMap");
	}
}
