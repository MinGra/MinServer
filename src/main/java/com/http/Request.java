package com.http;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Enumeration;
import java.util.Set;
 
public interface Request {
	public Enumeration<String> getAttributeNames();	//获取request域中所有的Attribute名称		来自ServletRequest接口
	
	public Object getAttribute(String attributeName);	//获取request域中指定名称的Attribute	来自ServletRequest接口
	public void setAttribute(String attributeName , Object value);	//request域中写Attribute	来自ServletRequest接口
	public String getMethod();	//获取请求方式，post等	来自HttpServletRequest
	public String getRequestURI();	//获取URI	来自HttpServletRequest
	public String getProtocol();	//获取版本协议	来自ServletRequest接口
	public String getParameter(String attributeName);	//获取参数，来自ServletRequest接口
	public Context getContext();	//获取Context，来自ServletRequest接口的getServletCotext
	public Set<String> getHeaderNames();
	public Object getHeader(String key);
	public AsynchronousSocketChannel getAsynchronousSocketChannel();
	public RequestDispatcher getRequestDispatcher(String uriInContext);
	public Session getSession();
	public Cookie[] getCookies();
}
