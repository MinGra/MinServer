package com.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.http.handler.RequestHandler;

/**
 * 这个和Context关联,jetty中还挺复杂的，我这简单些，能实现request.getDispatcher(String url).forward(Request request, Response response);就结束。。
 * @author MYD
 *
 */
public class Dispatcher implements RequestDispatcher{
	private Logger logger = LoggerFactory.getLogger(Dispatcher.class);
	private String url;
	
	public Dispatcher(String url) {
		this.url = url;
		logger.debug("已根据url：" + url + "创建Dispatcher");
	}
	
	@Override
	public void forward(Request request, Response response) {
		logger.debug("Dispatcher成功进入forward方法");
		new RequestHandler(request.getAsynchronousSocketChannel()).handleRequest(request, response, this.url);
	}

//	@Override
//	public void include(Request var1, Response var2) {
//		// TODO Auto-generated method stub
//		
//	}

}
