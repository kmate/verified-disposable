package com.github.kmate.verified.disposable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class UsageVerifierClassVisitor extends ClassVisitor {

	private final ClassReader reader;
	private final ClassLoader loader;

	public UsageVerifierClassVisitor(ClassReader reader, ClassVisitor writer, ClassLoader loader) {
		super(Opcodes.ASM5, writer);
		this.reader = reader;
		this.loader = loader;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		MethodVisitor writer = super.visitMethod(access, name, desc, signature, exceptions);
		return new UsageVerifierInstructionAdapter(reader, new LocalVariablesSorter(access, desc, writer), loader);
	}
}
