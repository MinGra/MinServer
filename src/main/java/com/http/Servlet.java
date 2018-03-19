package com.http;

public interface Servlet {
	public void init(Config config);
	public void service(Request request, Response response) throws ServletException;
	public void destory();
}
