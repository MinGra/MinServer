package com.http.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.http.HttpServlet;
import com.http.classloader.MinServletClassLoader;
import com.http.utils.XMLUtil;

public class ClassMapper {

	// 访问路径对应控制类 map<uri,class>
	private static Map<String, HttpServlet> servletMap = new HashMap<>();
	private static volatile ClassMapper instance = null;
	private static Logger logger = LoggerFactory.getLogger(ClassMapper.class);

	private ClassMapper() {
	}

	public static ClassMapper getInstance() {

		if (instance == null) {
			synchronized (ClassMapper.class) {
				if (instance == null) {
					instance = new ClassMapper();
					// 得到web.xml的根路径
					Element rootElement = XMLUtil.getRootElement("webapps/WEB-INF/web.xml");
					// 得到servlet的集合
					List<Element> elements = XMLUtil.getElements(rootElement);
					for (Element element : elements) {
						if (element.getName().equals("servlet")) {
							Element urlPattenEle = XMLUtil.getElement(element, "url-patten");
							// 得到urlPatten(uri)和对应的处理类
							String urlPatten = XMLUtil.getElementText(urlPattenEle);
							Element servletClass = XMLUtil.getElement(element, "servlet-class");
							// 得到servlet的全限定名
							String classPath = XMLUtil.getElementText(servletClass);
							Class<?> clazz = null;
							// 通过反射得到servlet实例存在map里
							MinServletClassLoader clod = new MinServletClassLoader();
							// 调用 通过字节流生产java类
							try {
								clazz = clod.findClass(classPath);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
								logger.error("web.xml中，类：" + classPath + "未找到");
							}
							HttpServlet servlet = null;
							try {
								servlet = (HttpServlet) clazz.newInstance();
							} catch (InstantiationException | IllegalAccessException e1) {
								e1.printStackTrace();
								logger.error("通过反射实例化web.xml中，类：" + classPath + "失败");
							}
							instance.getServletMap().put(urlPatten, servlet);
							logger.info("成功添加了URI： " + urlPatten + "到servlet： " + classPath + "的映射");
						}
					}
				}
			}
		}
		return instance;
	}

	public Map<String, HttpServlet> getServletMap() {
		return servletMap;
	}
}
