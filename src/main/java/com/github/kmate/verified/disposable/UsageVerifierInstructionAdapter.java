package com.github.kmate.verified.disposable;

import java.util.Stack;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.commons.Method;

final class UsageVerifierInstructionAdapter extends InstructionAdapter {

	private static final String USAGE_VERIFIER_CLASS_NAME = UsageVerifier.class.getName().replace('.', '/');
	private static final String VERIFIER_SIGNATURE = "(L" + ClassUtils.DISPOSABLE_CLASS_NAME + ";Ljava/lang/String;)V";

	private final LocalVariablesSorter variables;
	private final ClassReader reader;
	private final ClassLoader loader;

	UsageVerifierInstructionAdapter(ClassReader reader, LocalVariablesSorter variables, ClassLoader loader) {
		super(Opcodes.ASM5, variables);
		this.variables = variables;
		this.reader = reader;
		this.loader = loader;
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
			store(ownerLocal, ownerType);
			load(ownerLocal, ownerType);
			load(newValueLocal, fieldType);
			load(ownerLocal, ownerType);
			aconst(name);
			invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyFieldWrite", VERIFIER_SIGNATURE, false);
		}
		super.putfield(owner, name, desc);
	}

	@Override
	public void invokevirtual(String owner, String name, String desc, boolean itf) {
		insertMethodVerifierWhenDisposable(owner, name, desc);
		super.invokevirtual(owner, name, desc, itf);
	}

	@Override
	public void invokeinterface(String owner, String name, String desc) {
		insertMethodVerifierWhenDisposable(owner, name, desc);
		super.invokeinterface(owner, name, desc);
	}

	@Override
	public void invokespecial(String owner, String name, String desc, boolean itf) {
		// Special handling for superclass, private, and instance initialization
		// method invocations. It is not needed to override the default behavior
		// and add verification here.
		super.invokespecial(owner, name, desc, itf);
	}

	@Override
	public void invokedynamic(String name, String desc, Handle bsm, Object[] bsmArgs) {
		insertMethodVerifierWhenDisposable(bsm.getOwner(), name, desc);
		super.invokedynamic(name, desc, bsm, bsmArgs);
	}

	private boolean isDisposableClass(String candidate) {
		if (reader.getClassName().equals(candidate)) {
			// Do not instrument the owner of the member,
			// as only external accesses have to be verified.
			return false;
		}
		return ClassUtils.isDisposableClass(candidate, loader);
	}

	private void insertMethodVerifierWhenDisposable(String owner, String name, String desc) {
		if (isDisposableClass(owner)) {
			insertMethodVerifier(owner, name, desc);
		}
	}

	private void insertMethodVerifier(String owner, String name, String desc) {
		Method method = new Method(name, desc);
		Type[] argTypes = method.getArgumentTypes();
		int numArgs = argTypes.length;
		Stack<Integer> argLocals = new Stack<Integer>();
		for (int i = numArgs - 1; i >= 0; --i) {
			Type argType = argTypes[i];
			int argLocal = variables.newLocal(argType);
			argLocals.add(argLocal);
			store(argLocal, argType);
		}

		Type ownerType = Type.getType("L" + owner + ";");
		int ownerLocal = variables.newLocal(ownerType);
		store(ownerLocal, ownerType);
		load(ownerLocal, ownerType);

		for (int i = 0; i < numArgs; ++i) {
			Type argType = argTypes[i];
			int argLocal = argLocals.pop();
			load(argLocal, argType);
		}

		load(ownerLocal, ownerType);
		visitLdcInsn(name);
		invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyMethodInvocation", VERIFIER_SIGNATURE, false);
	}
}