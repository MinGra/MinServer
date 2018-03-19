package com.http;

public class HttpServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;
	
	private transient Config config;
	@Override
	public void init(Config config) {
		this.config = config;
//		this.service(Request request, Response response);
	}
	
	public void service(HttpRequest request, HttpResponse response) {
		//通过请求方式选择是doGET方法还是doPOST方法
		String method = request.getMethod();
		if(method.equals("GET")) {
			this.doGet(request,response);
		} else if (method.equals("POST")) {
			this.doPost(request,response);
		}
//		new ResponseHandler().write(response);
	}

	public void doGet(HttpRequest request , HttpResponse response) {
		String protocol = request.getProtocol();
        if (protocol.endsWith("1.1")) {
        	response.setStatus(405);
        } else {
        	response.setStatus(400);
        }
	}

	public void doPost(HttpRequest request , HttpResponse response) {
		String protocol = request.getProtocol();
        if (protocol.endsWith("1.1")) {
        	response.setStatus(405);
        } else {
        	response.setStatus(400);
        }
	}

	@Override
	public void destory() {
	}

	public Context getHttpContext(){
		return this.config.getContext();
	}

//	@Override
	public void service(Request request, Response response) throws ServletException {
		if (request instanceof HttpRequest && response instanceof HttpResponse) {
            HttpRequest request0 = (HttpRequest)request;
            HttpResponse response0 = (HttpResponse)response;
            this.service(request0, response0);
        } else {
            throw new ServletException("non-HTTP request or response");
        }
	}
}
