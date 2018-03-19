package com.http.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.dom4j.Element;

import com.http.utils.XMLUtil;
/**
 * MinServer入口
 * @author MYD
 *
 */
public class Main {
	
	//MinServer入口
	public static void main(String[] args) {
		int port = 12345;
		Element e = XMLUtil.getRootElement("server.xml").element("port");
		if(e != null) {
			port = Integer.parseInt(e.getText());
		}
		// ThreadPoolExecutor pool = new ThreadPoolExecutor(CORE_POOL_SIZE,
		// MAX_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT,
		// new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_SIZE),
		// REJECTED_EXECUTION_HANDLER);
		ThreadPoolExecutor serverSocketChannelPool = new ThreadPoolExecutor(100, 500, 5, TimeUnit.MICROSECONDS,
				new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.AbortPolicy());
		Server server = new Server(port, serverSocketChannelPool);
		ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 5, TimeUnit.SECONDS,
						new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.AbortPolicy());
				// DiscardOldestPolicy是ThreadPoolExecutor的一个静态子类
				pool.execute(server);
//		new Thread(server).start();
	}
}
