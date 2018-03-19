package com.http;

import java.io.Serializable;


public abstract class GenericServlet implements Servlet, Serializable {
	private static final long serialVersionUID = 1L;
	private transient Config config;
	@Override
	public void init(Config config) {
		this.config = config;
	}
	@Override
	public abstract void service(Request request, Response response) throws ServletException;
	@Override
	public void destory(){
		
	};
}
