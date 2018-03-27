package com.http;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.AsynchronousSocketChannel;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.http.constant.Constant;
import com.http.constant.HttpHeader;

public class HttpResponse implements Response {
	
	private int code = 200;	//响应状态码，默认为500，服务器异常
	
//	private String contentType = "text/html";	//内容类型  defalut 为text/html
//	private String serverName = "MinServer";
	private Map<HttpHeader, String> headers = new HashMap<>();	//这样只能存一个Cookie
	//默认32字节容量
	private OutputStream outputStream = new ByteArrayOutputStream();	//jetty使用自定义的ServletOutputStream
	private PrintWriter writer = new PrintWriter(outputStream);
	private AsynchronousSocketChannel channel;
	private Context context;
	//构造器
	public HttpResponse(AsynchronousSocketChannel channel, Context context) {
		
		this.channel = channel;
		this.context = context;
		
		headers.put(HttpHeader.SERVER, Constant.SERVER_NAME);
		headers.put(HttpHeader.CONTENT_TYPE, "text/html");
		headers.put(HttpHeader.DATE, LocalDate.now().toString());
	}
	
	@Override
	public String getContentType() {
		return headers.get(HttpHeader.CONTENT_TYPE);
	}
	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}
	@Override
	public PrintWriter getWriter() {
		return writer;
	}
	@Override
	public void setContentType(String contentType) {
		headers.put(HttpHeader.CONTENT_TYPE, contentType);
	}
	
	@Override
	public void setStatus(int sc) {
		this.code =sc;
	}
	
	@Override
	public int getStatus() {
		return code;
	}
	
	@Override
	public void setHeader(HttpHeader header, String value) {
		this.headers.put(header, value);
	}
	
	@Override
	public String getHeader(String name) {
        return this.headers.get(HttpHeader.getHttpHeaderByStr(name));
    }
	public Map<HttpHeader, String> getHeaderMap() {
		return this.headers;
	}
	
	@Override
	public AsynchronousSocketChannel getAsynchronousSocketChannel() {
		return this.channel;
	}
	
	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public void addCookie(Cookie cookie) {
		if(cookie.getPath() == null) {
			cookie.setPath("/");
		}
	    
	    //	    System.out.println(dateString);
	    StringBuilder builder = new StringBuilder();
	    builder.append(cookie.getName()).append("=").append(cookie.getValue());
	    if(cookie.getMaxAge() >= 0) {
//			Expires=Tue, 27-Mar-2018 03:21:37 GMT
	    	SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		    String dateString = sdf.format(new Date(new Date().getTime() + cookie.getMaxAge()*1000));
	    	builder.append("; Expires=").append(dateString);
	    }
		builder.append("; Max-Age=").append(cookie.getMaxAge()).append("; Path=").append(cookie.getPath());
	    headers.put(HttpHeader.SET_COOKIE, builder.toString());
	    
//		StringBuilder builder = new StringBuilder();
//		builder.append(cookie.getName()).append("=").append(cookie.getValue())
//		.append(" ;Version=1")
//		.append("; Max-Age=").append(cookie.getMaxAge()).append("; Path=").append(cookie.getPath()).append(";");
//		headers.put(HttpHeader.SET_COOKIE2, builder.toString());
	}

	@Override
	public void sendRedirect(String uri) {
		code =302;
		headers.put(HttpHeader.LOCATION, uri);
		
	}
}
