package com.http.server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.http.handler.RequestHandler;

public class AsynchronousSocketChannelHandler implements CompletionHandler<Integer, ByteBuffer> {
	private AsynchronousSocketChannel channel;
	private Logger logger = LoggerFactory.getLogger(Server.class);

	public AsynchronousSocketChannelHandler(AsynchronousSocketChannel channel) {
		this.channel = channel;
	}

	@Override
	public void completed(Integer result, ByteBuffer buffer) {
		// TODO Auto-generated method stub
		// 收完消息首先转向，不然Buffer会炸
		buffer.flip();
		//经常会收到些长度为0的request
		if(buffer.remaining()>0) {
			logger.debug("请求长度" + buffer.remaining());
			//获取buffer中的内容
			byte[] message = new byte[buffer.remaining()];  
			buffer.get(message);
			try {
				String requestStr = new String(message, "UTF-8");
				logger.debug("请求内容" + requestStr);
				RequestHandler requestHandler = new RequestHandler(channel);
				// FixedThreadPool和 SingleThread
				// 允许的请求队列长度为 Integer.MAX_VALUE，可 能会堆积大量的请求，从而导致 OOM。
				// CachedThreadPool和ScheduledThreadPool
				// 允许的创建线程数量为 Integer.MAX_VALUE，可能会创建大量的线程，从而导致 OOM。
				// ExecutorService pool = new ScheduledThreadPoolExecutor(3);

				// ThreadPoolExecutor完全参数的构造器
				// public ThreadPoolExecutor(int corePoolSize, //池中线程数
				// int maximumPoolSize, //池中最大线程数
				// long keepAliveTime, //当池中线程数超过内核支持的线程数时，等待的线程的存活时间
				// TimeUnit unit, //keepAliveTime的时间单位
				// BlockingQueue<Runnable> workQueue, //在线程运行前存线程的队列
				// ThreadFactory threadFactory, //生成新工程的工厂
				// RejectedExecutionHandler handler)
				// //句柄，处理器处理程序在执行被阻止时使用，因为已达到线程边界和队列容量

//				ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 5, TimeUnit.SECONDS,
//						new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.AbortPolicy());
//				// DiscardOldestPolicy是ThreadPoolExecutor的一个静态子类
//				//这个线程池应该只有一个线程
//				pool.execute(requestHandler);
				requestHandler.handleRequest(requestStr);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				logger.error("不支持UTF-8编码");
			}
		}
			
			
	}

	@Override
	public void failed(Throwable exc, ByteBuffer buffer) {
		exc.printStackTrace();
		logger.error("AsynchronousSocketChannel获取成功，但是AsynchronousSocketChannel读取失败");
	}

}
