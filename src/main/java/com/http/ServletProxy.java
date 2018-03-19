package com.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.http.constant.HttpHeader;
import com.http.constant.ResponseStatusMap;
import com.http.utils.CommonUtil;

public class ServletProxy  implements InvocationHandler {
	private Object servlet;
	private Logger logger = LoggerFactory.getLogger(ServletProxy.class);
	public ServletProxy(Object servlet) {
		this.servlet = servlet;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 当代理对象调用真实对象的方法时，其会自动的跳转到代理对象关联的handler对象的invoke方法来进行调用
		method.invoke(servlet, args);
		// 在代理真实对象后添加一些自己的操作
		HttpRequest request = (HttpRequest)args[0];
		HttpResponse response = (HttpResponse)args[1];
		
		byte[] entityByteArray = ((ByteArrayOutputStream)(response.getOutputStream())).toByteArray();
//		ByteBuffer responseBuffer = ByteBuffer.wrap(byteArray);
		
		response.setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(entityByteArray.length));
		StringBuilder sb = new StringBuilder();
		// 构造状态行
		sb.append(request.getProtocol()).append(" ").append(response.getStatus()).append(" ")
				.append(ResponseStatusMap.MAP.get(response.getStatus())).append("\r\n");
		//构造响应头
		for (HttpHeader key : response.getHeaderMap().keySet()) {
			sb.append(key).append(": ").append(response.getHeaderMap().get(key)).append("\r\n");
		}
		//空行
		sb.append("\r\n");
		byte[] statusAndHeadByteArray = sb.toString().getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(CommonUtil.mergeByteArray(statusAndHeadByteArray, entityByteArray));
		logger.debug(new String(statusAndHeadByteArray,"UTF-8"));
		writeByteBuffer2Channel(response.getAsynchronousSocketChannel(), buffer, "servlet响应写出失败");
		logger.info("servlet响应已写出");
		
		return null;
	}
	
	private void writeByteBuffer2Channel(AsynchronousSocketChannel channel, ByteBuffer buffer, String errorMsg) {
//		buffer.flip(); 直接wrap来的位置是对的，flap之后就错了
		channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer buffer) {
				// 如果没完，继续
				if (buffer.hasRemaining()) {
					channel.write(buffer, buffer, this);
				} else {
					try {
						channel.close();
					} catch (IOException e) {
						logger.error("关闭AsynchronousSocketChannel失败");
						e.printStackTrace();
					}
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer buffer) {
				logger.error(errorMsg);
			}
		});
	}

}
