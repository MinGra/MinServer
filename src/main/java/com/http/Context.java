package com.http;

public interface Context {
	
	public void setAttribute(String attributeName,Object value);
	public Object getAttribute(String attributeName);

}
