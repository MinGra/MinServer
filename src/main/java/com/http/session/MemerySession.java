package com.http.session;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.http.Context;
import com.http.Request;
import com.http.Session;
import com.http.storage.SessionQueue;
import com.http.utils.JSessionIdGeenerator;

public class MemerySession implements Session {
	private String JSESSIONID;
	private long createTime;
	private long lastAccessTime;
	private Map<String, Object> attributes = new HashMap<>();
	private Context context;
	
	public MemerySession(Request request) {
		this.createTime = new Date().getTime();
		this.JSESSIONID = JSessionIdGeenerator.JSessionId();
		this.lastAccessTime = this.createTime;
		this.context = request.getContext();
		SessionQueue.putSession(this);
	}
	@Override
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public long getCreationTime() {
		return createTime;
	}

	@Override
	public String getId() {
		return JSESSIONID;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessTime;
	}

	@Override
	public Context getContext() {
		return context;
	}

	@Override
	public void access() {
		this.lastAccessTime = new Date().getTime();
	}
}
