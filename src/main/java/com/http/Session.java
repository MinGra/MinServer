package com.http;

public interface Session {
	//截取官方文档上的一部分功能
	public void setAttribute(String name, Object value);
    public Object getAttribute(String name);
    public long getCreationTime();		//创建时间
    public String getId();				//JSESSIONID
    public long getLastAccessedTime();	//最近登录时间
//    public int getMaxInactiveInterval();	//Session保存多久，我这写死20分钟，这方法就不写了
	public Context getContext();		//该Session所在的Context，不同Context之间的Session不能互相访问
	public void access();
}
