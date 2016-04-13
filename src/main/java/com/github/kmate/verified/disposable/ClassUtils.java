package com.github.kmate.verified.disposable;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;

/**
 * Various utilities for detecting {@link Disposable} and system classes.
 */
abstract class ClassUtils {

	public static final String DISPOSABLE_CLASS_NAME = Disposable.class.getName().replace('.', '/');

	/**
	 * Determines whether a class is a system class. All classes in the
	 * {@code java.*}, {@code javax.*} and {@code sun.*} packages are treated as
	 * system classes. These classes will never be transformed by
	 * {@link UsageVerifierTransformer}.
	 * 
	 * @param className
	 *            the name of the class in the internal form of fully qualified
	 *            class and interface names as defined in The Java Virtual
	 *            Machine Specification
	 * @return {@code true} when the given class name identifies a system class,
	 *         {@code false} otherwise
	 */
	public static boolean isSystemClass(String className) {
		return className.startsWith("java/") || className.startsWith("javax/") || className.startsWith("sun/");
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
