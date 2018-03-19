package com.http;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;

import com.http.constant.HttpHeader;

public interface Response {
	public String getContentType();	//来自ServletResponse
	public OutputStream getOutputStream();	//来自ServletResponse
	public PrintWriter getWriter();	//来自ServletResponse
	public void setContentType(String contentType);	//来自ServletResponse
	
	//自建
	public void setStatus(int sc);
	public int getStatus();
	public void setHeader(HttpHeader header, String value);
	public String getHeader(String name);
	public Map<HttpHeader, String> getHeaderMap();
	public AsynchronousSocketChannel getAsynchronousSocketChannel();
	public Context getContext();
}
