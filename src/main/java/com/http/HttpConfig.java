package com.http;

public class HttpConfig implements Config{
	private Context context;
	public HttpConfig(Context context) {
		this.context = context;
	}
	@Override
	public Context getContext() {
		return this.context;
	}

}
