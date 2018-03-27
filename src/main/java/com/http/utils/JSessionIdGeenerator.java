package com.http.utils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于生成sessionid
 * @author MYD
 *
 */
public class JSessionIdGeenerator {
	private static AtomicInteger id = new AtomicInteger(10000);
	private JSessionIdGeenerator() {
		throw new AssertionError("不允许实例化工具类");
	}
	public static String JSessionId() {
		return Long.toString(new Date().getTime()) + id.addAndGet(1);
	}
}
