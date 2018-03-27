package com.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

public class Cookie implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private String name;
    private String value;
//    private String comment;
//    private String domain;
    private int maxAge = -1;
    private String path;
    
    Cookie(String name, String value) {
    	this.name = name;
    	this.value = value;
    }
    //序列化实现深度复制
    @Override
	public Cookie clone() {
		
		try {
			// save the object to a byte array
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bout);
			out.writeObject(this);
			out.close();

			// read a clone of the object from the byte array
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bin);
			Cookie ret = (Cookie) in.readObject();
			in.close();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public int getMaxAge() {
		return maxAge;
	}
	
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
//	public String toHttpHeader() {
////	 TODO 
//		StringBuilder builder = new StringBuilder();
//		builder.append("Set-Cookie2: ").append(name).append("=").append(value)
//		.append(" ;Version=1").append("; Max-Age=").append(maxAge).append("path=").append(path).append(";");
//		return builder.toString();
//	}
}
