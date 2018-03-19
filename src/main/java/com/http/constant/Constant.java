package com.http.constant;

import java.io.File;

import com.http.handler.RequestHandler;
import com.http.utils.XMLUtil;

public class Constant {
	private Constant() {
		throw new AssertionError("不允许实例化com.http.constant.Constents");
	}
	public static final String ROOT_PATH = new File(RequestHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent();
	
	public static final String FAVICON_URI = "favicon.ico";
	
	public static final String NOTFOUND_PATH = "/defaultFiles/404.html";
	public static final String FAVICO_PATH = "/favicon.ico";
	public static final String SERVER_XML_PATH = "/server.xml";
	
	public static final String SERVER_NAME = "MinServer";
	
}
