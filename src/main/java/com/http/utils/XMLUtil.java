package com.http.utils;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XMLUtil {
	
	private static Logger logger = LoggerFactory.getLogger(XMLUtil.class);
	private static SAXReader reader = new SAXReader();
	
	private XMLUtil() {
		throw new AssertionError("不允许实例化工具类");
	}

	public static Element getRootElement(String xmlPath) {
		Document document = null;;
		try {
			document = reader.read(new File(xmlPath));
		} catch (DocumentException e) {
			logger.error("找不到指定的xml文件的路径" + xmlPath + "！");
			return null;
		}
		return document.getRootElement();
	}
	
	/*
	 * 得到该节点下的子节点集合
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getElements(Element element) {
		return element.elements();
	}
	
	/*
	 * 得到节点下的指定节点
	 */
	public static Element getElement(Element element, String name) {
		Element childElement = element.element(name);
		if(childElement == null) {
			logger.error(element.getName() + "节点下没有子节点" + name);
			return null;
		}
		return childElement;
	}
	
	/*
	 * 得到节点的内容
	 */
	public static String getElementText(Element element) {
		return element.getText();
	}
}
