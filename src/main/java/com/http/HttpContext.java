package com.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context实现类
 * @author MYD
 *
 */
public class HttpContext implements Context {

	private static volatile HttpContext instance;
	private static Map<String, Object> attributeMap = new ConcurrentHashMap<String, Object>();

	// 构造方法
	private HttpContext() {
	}

	// 双锁实现单例
	public static Context getContext() {
		if (instance == null) {
			synchronized (HttpContext.class) {
				if (instance == null) {
					instance = new HttpContext();
				}
			}
		}
		return instance;
	}

	@Override
	public void setAttribute(String attributeName, Object value) {
		attributeMap.put(attributeName, value);
	}

	@Override
	public Object getAttribute(String attributeName) {
		return attributeMap.get(attributeName);
	}
}
