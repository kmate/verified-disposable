package com.github.kmate.verified.disposable;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * Uses {@link UsageVerifierClassVisitor} to transform all non-system classes.
 * 
 * @see ClassUtils#isSystemClass(String)
 */
class UsageVerifierTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		if (ClassUtils.isSystemClass(className)) {
			return classfileBuffer;
		}

		ClassReader reader = new ClassReader(classfileBuffer);
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		ClassVisitor visitor = new UsageVerifierClassVisitor(reader, writer, loader);
		reader.accept(visitor, 0);
		return writer.toByteArray();
	}
}
