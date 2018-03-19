package com.http;

public interface RequestDispatcher {
	//jetty中，这个接口还定义了一些常量，这里不写
//	void forward(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;
	void forward(Request var1, Response var2);
//  void include(ServletRequest var1, ServletResponse var2) throws ServletException, IOException;
//	void include(Request var1, Response var2); //不实现了，都没用过
}
