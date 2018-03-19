package com.http.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable {
	public static final int SERVER_PORT = 12345;
	public static final int CORE_POOL_SIZE = 1;
	public static final int MAX_POOL_SIZE = 1;
	public static final long KEEP_ALIVE_TIME = 1;
	public static final TimeUnit TIME_UNIT = TimeUnit.MICROSECONDS;
	public static final int BLOCKING_QUEUE_SIZE = 1;
	public static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.DiscardOldestPolicy();
	public static final int BYTE_BUFFER_SIZE = 20480;

	private static Logger logger = LoggerFactory.getLogger(Server.class);
	// 计数器，计算有多少连接到此服务器的连接
	public AtomicInteger atomicInteger = new AtomicInteger(0);

	private int port;
	private AsynchronousServerSocketChannel channel;
	private ExecutorService threadPool;

	public Server(int port, ExecutorService threadPool) {
		this.port = port;
		this.threadPool = threadPool;
		this.init();
	}

	public Server() {
		this.port = SERVER_PORT;
		this.threadPool = null;
		this.init();
	}

	private void init() {
		
		// 完整参数的open
		// public static AsynchronousServerSocketChannel
		// open(AsynchronousChannelGroup group)
		// throws IOException
		// 此处使用默认AsynchronousChannelGroup
		try {
			AsynchronousChannelGroup acg = null;
			if(null != threadPool ) {
				acg = AsynchronousChannelGroup.withThreadPool(threadPool);
			} 
			channel = AsynchronousServerSocketChannel.open(acg);
			try {
				channel.bind(new InetSocketAddress(this.port));
				logger.info("服务器已启动，端口号：" + port);
			} catch (IOException e) {
				logger.error("AsynchronousServerSocketChannel绑定端口" + this.port + "失败");
				e.printStackTrace();
			}
		} catch (IOException e) {
			logger.error("AsynchronousServerSocketChannel开启失败");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// 这个写法抛java.nio.channels.AcceptPendingException
		// while (true) {
		if (null != channel) {
			channel.accept(channel,
					new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {

						@Override
						public void completed(AsynchronousSocketChannel result,
								AsynchronousServerSocketChannel channel) {
							channel.accept(channel, this);
							// 记录已处理的请求数
							System.out.println("已处理的请求数：" + (atomicInteger.incrementAndGet() + 1));
							// 创建新的Buffer
							ByteBuffer buffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);

							// 函数原型
							// public final <A> void read(ByteBuffer dst,
							// A attachment,
							// CompletionHandler<Integer,? super A> handler)
							result.read(buffer, buffer, new AsynchronousSocketChannelHandler(result));
							
						}

						@Override
						public void failed(Throwable exc, AsynchronousServerSocketChannel channel) {
							logger.error("AsynchronousSocketChannel获取失败");
						}

					});
		} else {
			logger.error("channel null了，调试下");
		}
		// }
	}

}
