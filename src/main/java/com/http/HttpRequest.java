package com.http;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.http.constant.HttpHeader;
/**
 * 一个线程一个request，没有线程安全问题
 * @author MYD
 *
 */
public class HttpRequest implements Request {
	private Logger logger = LoggerFactory.getLogger(HttpRequest.class);
	private String method;	//请求方法名
	private String uri;	//uri
	private String protocol;	//HTTP版本号
	private Map<String, Object> headers = new HashMap<>();	//请求头 例如 Content-Length: 25
	private Map<String, List<String>> parameters = new HashMap<>();	//参数
	private Map<String, Object> attrbutes = new HashMap<>();
	private Map<String, String> cookies = new HashMap<>();
	private Context context = null;	//此request所在的Context
	private AsynchronousSocketChannel channel;	//此request所在的AsynchronousSocketChannel
	
	public HttpRequest(String requestStr, Context context, AsynchronousSocketChannel channel) {
		this.context = context;
		this.channel = channel;
		init(requestStr); //HttpRequest被创建后需要初始化一些参数
	}

	private void init(String requestStr) {
		//将请求分行
		String[] headers = requestStr.split("\r\n");
		//获取请求方式
		logger.debug("请求行：\r\n" + headers[0] +"\r\n请求行结束");
		String[] requestLine = headers[0].split(" ");
		logger.debug("请求行分割" + requestLine.length);
		method = requestLine[0];
		uri = requestLine[1];
		protocol = requestLine[2];
		if(uri.contains("?")) {
			String params = uri.substring(uri.indexOf("?") + 1, uri.length());
			uri = uri.substring(0, uri.indexOf("?"));
			initParameters(params);
		}
		
		//设置请求头 
		initRequestHeaders(headers);
		
		//获取cookie
		initRequestCookies(this.headers);
	}
	
	/**
	 * 获取request参数
	 * @param strParams HTTP请求头中关于参数的部分，其实这个POST和GET都可以带
	 */
	private void initParameters(String strParams) {
		String[] params = strParams.split("&");
		for (String string : params) {
			string = string.trim();
			String key = string.substring(0, string.indexOf("="));
			String value = string.substring(string.indexOf("=") + 1);
			if(parameters.get(key)==null) {
				ArrayList<String> list = new ArrayList<>();
				list.add(value);
				parameters.put(key, list);
			} else {
				parameters.get(key).add(value);
			}
			
		}
	}
	
	/**
	 * 设置请求头参数Map
	 */
	private void initRequestHeaders(String[] strs) {
		//去掉第一行
		for(int i = 1; i < strs.length; i++) {
			String key = strs[i].substring(0, strs[i].indexOf(":"));
			String value = strs[i].substring(strs[i].indexOf(":") + 1);
			headers.put(key, value);
		}
	}
	
	private void initRequestCookies(Map<String, Object> headers) {
		Object cookieObj = headers.get(HttpHeader.COOKIE);
		if(cookieObj==null) {
			return;
		}
		String cookieStr = ((String)cookieObj).trim();
		String[] cookieStringArray = cookieStr.split(";");
		for(String s : cookieStringArray) {
			String[] keyValue = s.split("=");
			if(2 == keyValue.length) {
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();
				cookies.put(key, value);
			}
		}
	}
	
	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getRequestURI() {
		return uri;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public Set<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public Object getHeader(String key) {
		return headers.get(key);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(this.parameters.keySet());
	}

	@Override
	public String getParameter(String attributeName) {
		return parameters.get(attributeName).get(0);
	}

	@Override
	public void setAttribute(String attributeName, Object value) {
		this.attrbutes.put(attributeName, value);
		
	}

	@Override
	public Object getAttribute(String attributeName) {
		return this.attrbutes.get(attributeName);
	}

	@Override
	public Context getContext() {
		return this.context;
	}
	
	@Override
	public AsynchronousSocketChannel getAsynchronousSocketChannel() {
		return this.channel;
	}
}
