package com.github.kmate.verified.disposable;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;

public class ClassUtils {

	public static final String DISPOSABLE_CLASS_NAME = Disposable.class.getName().replace('.', '/');

	public static boolean isSystemClass(String className) {
		return className.startsWith("java/") || className.startsWith("javax/");
	}

	public static boolean isDisposableClass(String className, ClassLoader loader) {
		try {
			InputStream classStream = loader.getResourceAsStream(className.replace('.', '/') + ".class");
			ClassReader reader = new ClassReader(classStream);
			return isDisposableClass(reader, loader);
		} catch (IOException e) {
			throw new IllegalArgumentException("Unable to load class data: " + className);
		}
	}

	private static boolean isDisposableClass(ClassReader reader, ClassLoader loader) {
		if (isSystemClass(reader.getClassName())) {
			return false;
		}

		for (String interfaceName : reader.getInterfaces()) {
			if (DISPOSABLE_CLASS_NAME.equals(interfaceName) || isDisposableClass(interfaceName, loader)) {
				return true;
			}
		}

		return isDisposableClass(reader.getSuperName(), loader);
	}
}
