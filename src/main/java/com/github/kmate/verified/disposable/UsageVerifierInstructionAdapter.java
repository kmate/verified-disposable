package com.github.kmate.verified.disposable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

final class UsageVerifierInstructionAdapter extends InstructionAdapter {

	private static final String USAGE_VERIFIER_CLASS_NAME = UsageVerifier.class.getName().replace('.', '/');
	private static final String VERIFIER_SIGNATURE = "(L" + ClassUtils.DISPOSABLE_CLASS_NAME + ";Ljava/lang/String;)V";

	private final int access;
	private final String name;
	private final ClassReader reader;
	private final ClassLoader loader;
	private final LocalVariablesSorter variables;

	UsageVerifierInstructionAdapter(int access, String name, ClassReader reader, ClassLoader loader,
			LocalVariablesSorter variables) {
		super(Opcodes.ASM5, variables);
		this.access = access;
		this.name = name;
		this.reader = reader;
		this.loader = loader;
		this.variables = variables;
	}

	@Override
	public void visitCode() {
		// FIXME: watch access modifiers
		if (!"<init>".equals(name) && !"isDisposed".equals(name)
				&& ClassUtils.isDisposableClass(reader.getClassName(), loader)) {
			visitVarInsn(Opcodes.ALOAD, 0);
			aconst(name);
			invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyMethodInvocation", VERIFIER_SIGNATURE, false);
		}
		super.visitCode();
	}

	@Override
	public void getfield(String owner, String name, String desc) {
		if (isDisposableClass(owner)) {
			dup();
			aconst(name);
			invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyFieldRead", VERIFIER_SIGNATURE, false);
		}
		super.getfield(owner, name, desc);
	}

	@Override
	public void putfield(String owner, String name, String desc) {
		if (isDisposableClass(owner)) {
			Type fieldType = Type.getType(desc);
			Type ownerType = Type.getType("L" + owner + ";");
			int newValueLocal = variables.newLocal(fieldType);
			int ownerLocal = variables.newLocal(ownerType);
			store(newValueLocal, fieldType);
			dup();
			store(ownerLocal, ownerType);
			load(newValueLocal, fieldType);
			load(ownerLocal, ownerType);
			aconst(name);
			invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyFieldWrite", VERIFIER_SIGNATURE, false);
		}
		super.putfield(owner, name, desc);
	}

	private boolean isDisposableClass(String candidate) {
		if (reader.getClassName().equals(candidate)) {
			// Do not instrument the owner of the member,
			// as only external accesses have to be verified.
			return false;
		}
		return ClassUtils.isDisposableClass(candidate, loader);
	}
}