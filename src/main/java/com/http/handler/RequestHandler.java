package com.http.handler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.http.Config;
import com.http.Context;
import com.http.ServletProxy;
import com.http.classloader.LibraryLoader;
import com.http.HttpConfig;
import com.http.HttpContext;
import com.http.HttpRequest;
import com.http.HttpResponse;
import com.http.Request;
import com.http.Response;
import com.http.Servlet;
import com.http.constant.Constant;
import com.http.constant.HttpHeader;
import com.http.constant.ResponseStatusMap;
import com.http.utils.CommonUtil;

/**
 * 此类用于处理一次请求后删除
 * 
 * @author MYD
 *
 */
// public class RequestHandler implements Runnable {
public class RequestHandler {
	private Logger logger = LoggerFactory.getLogger(RequestHandler.class);

	// 标志位，表示当前是否找到了状态
	private int status = STATUS_NOT_FOUND;
	// 状态变量，关于请求是servlet，静态资源，图标，未找到
	private static final int STATUS_IS_SERVLET = 0;
	private static final int STATUS_IS_FAVICON = 1;
	private static final int STATUS_IS_STATIC = 2;
	private static final int STATUS_NOT_FOUND = 3;
	// http请求字符串
//	private String requestStr;
	// 针对uri选择不同的处理器
	private Servlet servlet;

	private AsynchronousSocketChannel channel;

	public RequestHandler(AsynchronousSocketChannel channel) {
		this.channel = channel;
//		this.requestStr = ;
	}
	
	

	// @Override
	// public void run() {
	public void handleRequest(Request request, Response response, String uri) {
		LibraryLoader.getInstance(); // 确保Jar包中的类已被加载，这个实现有点傻的
		
		Config config = new HttpConfig(request.getContext());
		if (uri.equals(Constant.FAVICON_URI)) {
			status = STATUS_IS_FAVICON;
		} else {
			// 得到ServletMap(uri,servlet)
			servlet = ClassMapper.getInstance().getServletMap().get(uri);
			if (servlet != null) {
				status = STATUS_IS_SERVLET;
			} else {
				File staticFile = new File(Constant.ROOT_PATH + "/webapps/" + uri);
				if (staticFile.exists() && staticFile.isFile()) {
					status = STATUS_IS_STATIC;
				} else {
					status = STATUS_NOT_FOUND;
				}
			}
		}
		Path filename = null; // case的命名空间是一样的。
		switch (status) {
		case STATUS_IS_SERVLET:
			servlet.init(config);
			// 动态代理servlet
			logger.debug(servlet.getClass().getName());
			InvocationHandler handler = new ServletProxy(servlet);
			// 参数 被代理类的加载器 被代理类实现的接口数组，数组中要有cast之后的接口，不然会unchecked
			// exception(Runtime Exception) 和 InvocationHandler
			logger.debug("被代理类名称：" + servlet.getClass().getSuperclass().getSuperclass().getName());
			logger.debug("被代理类实现接口数：" + servlet.getClass().getSuperclass().getSuperclass().getInterfaces().length);
			logger.debug("handler.getClass().getName()" + handler.getClass().getName());
			Servlet proxyedServlet = (Servlet) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
					servlet.getClass().getSuperclass().getSuperclass().getInterfaces(), handler);
			try {
				proxyedServlet.service(request, response);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("代理类service方法抛异常");
			}
			break;
		case STATUS_IS_FAVICON:
			File faviconFile = new File(Constant.ROOT_PATH + Constant.FAVICO_PATH);
			filename = faviconFile.toPath();
			try (FileChannel faviconFileChannel = FileChannel.open(filename)) {
				long length = faviconFileChannel.size();
				MappedByteBuffer fileBuffer = faviconFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, length);
				byte[] entityByteArray = new byte[fileBuffer.remaining()];
				fileBuffer.get(entityByteArray, 0, entityByteArray.length);

				StringBuilder sb = new StringBuilder();
				response.setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(entityByteArray.length));
				// 构造状态行
				sb.append(request.getProtocol()).append(" ").append(response.getStatus()).append(" ")
						.append(ResponseStatusMap.MAP.get(response.getStatus())).append("\r\n");
				// 构造响应头
				for (HttpHeader key : response.getHeaderMap().keySet()) {
					sb.append(key).append(": ").append(response.getHeaderMap().get(key)).append("\r\n");
				}
				// 空行
				sb.append("\r\n");
				byte[] statusAndHeadByteArray = sb.toString().getBytes();

				byte[] responseByteArray = CommonUtil.mergeByteArray(statusAndHeadByteArray, entityByteArray);
				logger.debug("响应内容为：\r\n" + new String(responseByteArray, "UTF-8"));
				logger.debug("响应长度为：\r\n" + responseByteArray.length);
				ByteBuffer buffer = ByteBuffer.wrap(responseByteArray, 0, responseByteArray.length);
				writeByteBuffer2Channel(channel, buffer, "向客户端返回静态资源:"
						+ new File(RequestHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile())
								.getParent()
						+ "/defaultFiles/404.html" + "失败");
			} catch (IOException e) {
				logger.error("读取静态资源:" + "webapps" + uri + "出现出现IO异常");
			}
			logger.info("静态资源:" + "webapps" + uri + "不存在或不是有效文件，显示404");
			break;
		case STATUS_IS_STATIC:
			File staticFile = new File(Constant.ROOT_PATH + "/webapps/" + uri);
			if (staticFile.exists() && staticFile.isFile()) {

				filename = staticFile.toPath();
				try (FileChannel staticFileChannel = FileChannel.open(filename)) {
					long length = staticFileChannel.size();
					MappedByteBuffer fileBuffer = staticFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, length);
					byte[] entityByteArray = new byte[fileBuffer.remaining()];
					fileBuffer.get(entityByteArray, 0, entityByteArray.length);

					response.setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(entityByteArray.length));
					StringBuilder sb = new StringBuilder();
					// 构造状态行
					sb.append(request.getProtocol()).append(" ").append(response.getStatus()).append(" ")
							.append(ResponseStatusMap.MAP.get(response.getStatus())).append("\r\n");

					// 构造响应头
					for (HttpHeader key : response.getHeaderMap().keySet()) {
						sb.append(key).append(": ").append(response.getHeaderMap().get(key)).append("\r\n");
					}
					// 空行
					sb.append("\r\n");
					// 此处sb全英文
					// 输出响应头和空行
					byte[] statusAndHeadByteArray = sb.toString().getBytes();

					byte[] responseByteArray = CommonUtil.mergeByteArray(statusAndHeadByteArray, entityByteArray);
					logger.debug("响应长度为：\r\n" + entityByteArray.length);
					logger.debug("响应内容为：\r\n" + new String(responseByteArray, "UTF-8"));
					ByteBuffer buffer = ByteBuffer.wrap(responseByteArray, 0, responseByteArray.length);
					writeByteBuffer2Channel(channel, buffer, "向客户端返回静态资源:" + "webapps" + uri + "失败");
				} catch (IOException e) {
					logger.error("读取静态资源:" + "webapps" + uri + "出现出现IO异常");
				}
			}
			break;
		case STATUS_NOT_FOUND:
			File notFoundFile = new File(Constant.ROOT_PATH + Constant.NOTFOUND_PATH);
			filename = notFoundFile.toPath();
			try (FileChannel notFoundFileChannel = FileChannel.open(filename)) {
				long length = notFoundFileChannel.size();
				MappedByteBuffer fileBuffer = notFoundFileChannel.map(FileChannel.MapMode.READ_ONLY, 0, length);
				byte[] entityByteArray = new byte[fileBuffer.remaining()];
				fileBuffer.get(entityByteArray, 0, entityByteArray.length);

				StringBuilder sb = new StringBuilder();
				response.setStatus(404);
				response.setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(entityByteArray.length));
				// 构造状态行
				sb.append(request.getProtocol()).append(" ").append(response.getStatus()).append(" ")
						.append(ResponseStatusMap.MAP.get(response.getStatus())).append("\r\n");
				// 构造响应头
				for (HttpHeader key : response.getHeaderMap().keySet()) {
					sb.append(key).append(": ").append(response.getHeaderMap().get(key)).append("\r\n");
				}
				// 空行
				sb.append("\r\n");
				byte[] statusAndHeadByteArray = sb.toString().getBytes();

				byte[] responseByteArray = CommonUtil.mergeByteArray(statusAndHeadByteArray, entityByteArray);
				logger.debug("响应内容为：\r\n" + new String(responseByteArray, "UTF-8"));
				logger.debug("响应长度为：\r\n" + responseByteArray.length);
				ByteBuffer buffer = ByteBuffer.wrap(responseByteArray, 0, responseByteArray.length);
				writeByteBuffer2Channel(channel, buffer, "向客户端返回静态资源:"
						+ new File(RequestHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile())
								.getParent()
						+ "/defaultFiles/404.html" + "失败");
			} catch (IOException e) {
				logger.error("读取静态资源:" + "webapps" + uri + "出现出现IO异常");
			}
			logger.info("静态资源:" + "webapps" + uri + "不存在或不是有效文件，显示404");
			break;

		default:
			logger.error("RequestHandler 无法处理的request");
		}

	}

	public void handleRequest(String requestStr) {
		Context context = HttpContext.getContext();
		logger.info("收到请求：\r\n" + requestStr + "\r\n请求结束");

		Request request = new HttpRequest(requestStr, context, channel);
		Response response = new HttpResponse(channel, request.getContext());
		String uri = request.getRequestURI(); // 得到uri
		logger.info("得到了uri " + uri);

		handleRequest(request, response, uri);

	}

	private void writeByteBuffer2Channel(AsynchronousSocketChannel channel, ByteBuffer buffer, String errorMsg) {
		channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer buffer) {
				// 如果没完，继续
				if (buffer.hasRemaining()) {
					logger.debug("响应还剩" + buffer.remaining());
					channel.write(buffer, buffer, this);
				} else {
					logger.debug("响应已输出");
					// 关通道，不会拒绝请求，是用线程池出的bug
					try {
						channel.close();
					} catch (IOException e) {
						logger.info("channel关闭失败");
						e.printStackTrace();
					}
				}
				// buffer.compact();
			}

			@Override
			public void failed(Throwable exc, ByteBuffer buffer) {
				logger.error(errorMsg);
			}
		});
	}

}
