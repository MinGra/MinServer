package com.http.utils;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.http.classloader.MinServletClassLoader;

public class LibraryLoader {
	private static Logger logger = LoggerFactory.getLogger(LibraryLoader.class);
	public LibraryLoader() {
		throw new AssertionError("不允许实例化工具类");
	}
	
	public static void loadJars() {
		String path = new File(
				MinServletClassLoader.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent()
				+ "/webapps/WEB-INF/lib/";
		logger.debug("jar包路径：" + path);
		File parent = new File(path);
		File[] jarFiles = null;
		if (parent.exists() && parent.isDirectory()) {
			// 获取Jar包文件列表
			jarFiles = parent.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (!pathname.getName().endsWith(".jar"))
						return false;
					else
						return true;
				}
			});
			logger.debug("有" + (jarFiles == null ? 0 : jarFiles.length) + "个jar包");
			if (jarFiles != null) {
				// 从URLClassLoader类中获取类所在文件夹的方法
				Method method = null;
				try {
					method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
					method.setAccessible(true); // 设置方法的访问权限
					URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
					for (File file : jarFiles) {
						URL url;
						try {
							url = file.toURI().toURL();
							try {
								logger.debug("加载" + url.toString());
								method.invoke(classLoader, url);
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
								e.printStackTrace();
								logger.error("反射调用addURL异常");
							}
						} catch (MalformedURLException e) {
							e.printStackTrace();
							logger.error("Jar包toURL格式错误");
						}
					}
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					logger.error("获取URLClassLoader的addURL方法失败");
				}
			}
		}
	}
}
