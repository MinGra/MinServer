package com.http.storage;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.http.Session;
import com.http.constant.Constant;

public class SessionQueue {
	private static Logger logger = LoggerFactory.getLogger(SessionQueue.class);
	//用linked hashmap 实现最近最少使用的排序
	//函数原型
//	/**
//     * Constructs an empty <tt>LinkedHashMap</tt> instance with the
//     * specified initial capacity, load factor and ordering mode.
//     *
//     * @param  initialCapacity the initial capacity
//     * @param  loadFactor      the load factor
//     * @param  accessOrder     the ordering mode - <tt>true</tt> for
//     *         access-order, <tt>false</tt> for insertion-order
//     * @throws IllegalArgumentException if the initial capacity is negative
//     *         or the load factor is nonpositive
//     */
//    public LinkedHashMap(int initialCapacity,
//                         float loadFactor,
//                         boolean accessOrder) {
//        super(initialCapacity, loadFactor);
//        this.accessOrder = accessOrder;
//    }
	private static LinkedHashMap<String, Session> sessionMap = new LinkedHashMap<>(50, 0.75f, true);
	
	/**
	 * 访问Session 同时删除过期Session
	 * @param sessionId
	 * @return
	 */
	public static synchronized Session getSessionById(String sessionId) {
		boolean isDeleted = true;
		//删除最早的过期Session
		Iterator<Map.Entry<String, Session>> iteratorOldest = sessionMap.entrySet().iterator();
		while(iteratorOldest.hasNext() && isDeleted) {
			Map.Entry<String,Session> oldestEntity = iteratorOldest.next();
			long nowTime = new Date().getTime();
			if (oldestEntity.getValue().getLastAccessedTime() + Constant.MILLISECOND_IN_20_MINUTES < nowTime) {
				sessionMap.remove(oldestEntity.getKey());
			} else {
				isDeleted = false;
			}
			//更新Session时间，获取session
			
			Session session = sessionMap.get(sessionId);
			if(session != null) {
				session.access();
			}
			return session;
		}
		return null;
		
		
		
	}
	/**
	 * 插入Session
	 * @param session
	 */
	public static synchronized void putSession(Session session) {
		if(getSessionById(session.getId()) == null) {
			sessionMap.put(session.getId(), session);
			logger.debug("session id：" + session.getId() + "已插入");
		}
		getSessionById(session.getId());
		logger.debug("session queue 长度：" + sessionMap.size());
	}
	
}
