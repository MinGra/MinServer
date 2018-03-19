package com.http.classloader;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinServletClassLoader extends ClassLoader {

	private static String servletClasspath = new File(
			MinServletClassLoader.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getParent()
			+ "/webapps/WEB-INF/classes/";
	private static Logger logger = LoggerFactory.getLogger(MinServletClassLoader.class);
	public static MinServletClassLoader instance = new MinServletClassLoader();

	public MinServletClassLoader() {
		super();
	}

	/**
	 * @param name 待加载servlet的全限定名
	 * @return 全限定名对应的Class对象
	 */
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] classByteArray = null;
		// 读取类文件内容
		Path filename = Paths.get(servletClasspath + name.replace('.', '/') + ".class");
		try (FileChannel channel = FileChannel.open(filename)) {
			long length = channel.size();
			MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, length);

			classByteArray = new byte[buffer.remaining()];
			buffer.get(classByteArray, 0, classByteArray.length); // 将Buffer转为数组
		} catch (IOException e) {
			logger.error("读取servlet：" + name + "时出现IO异常");
		}
		return defineClass(name, classByteArray, 0, classByteArray.length);
	}
}
