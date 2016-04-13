package com.github.kmate.verified.disposable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Inserts a static call to the appropriate method of class
 * {@link UsageVerifier} before each field access of {@link Disposable} objects,
 * except for those which are done in the same disposable class. Any call using
 * <em>invokeinterface</em> or <em>invokevirtual</em> on {@link Disposable}
 * objects will also be checked before execution at the call site.
 * 
 * @see UsageVerifierTransformer
 */
class UsageVerifierInstructionAdapter extends InstructionAdapter {

	private static final String USAGE_VERIFIER_CLASS_NAME = UsageVerifier.class.getName().replace('.', '/');
	private static final String FIELD_VERIFIER_TYPE = "(L" + ClassUtils.DISPOSABLE_CLASS_NAME + ";Ljava/lang/String;)V";
	private static final String METHOD_VERIFIER_TYPE = "(Ljava/lang/Object;Ljava/lang/String;)V";

	private final ClassReader reader;
	private final ClassLoader loader;
	private final LocalVariablesSorter variables;

	UsageVerifierInstructionAdapter(ClassReader reader, ClassLoader loader, LocalVariablesSorter variables) {
		super(Opcodes.ASM5, variables);
		this.reader = reader;
		this.loader = loader;
		this.variables = variables;
	}

	@Override
	public void getfield(String owner, String name, String desc) {
		if (isDisposableExternalClass(owner)) {
			dup();
			aconst(name);
			invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyFieldRead", FIELD_VERIFIER_TYPE, false);
		}
		super.getfield(owner, name, desc);
	}

	@Override
	public void putfield(String owner, String name, String desc) {
		if (isDisposableExternalClass(owner)) {
			// Original stack: owner,newValue
			// Modified stack: owner,newValue,owner,name
			Type fieldType = Type.getType(desc);
			Type ownerType = Type.getType("L" + owner + ";");
			int newValueLocal = variables.newLocal(fieldType);
			int ownerLocal = variables.newLocal(ownerType);
			// saving new value from stack top
			store(newValueLocal, fieldType);
			// duplicating owner
			dup();
			// saving one of the owners
			store(ownerLocal, ownerType);
			// restoring new value
			load(newValueLocal, fieldType);
			// restoring owner
			load(ownerLocal, ownerType);
			// add field name to stack top
			aconst(name);
			invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyFieldWrite", FIELD_VERIFIER_TYPE, false);
		}
		super.putfield(owner, name, desc);
	}

	private boolean isDisposableExternalClass(String candidate) {
		if (reader.getClassName().equals(candidate)) {
			// Do not instrument the owner of the member,
			// as only external accesses have to be verified.
			return false;
		}
		return ClassUtils.isDisposableClass(candidate, loader);
	}

	@Override
	public void invokevirtual(String owner, String name, String desc, boolean itf) {
		if (isDisposableExternalClass(owner)) {
			addMethodVerifierInvocation(owner, name, desc);
		}
		super.invokevirtual(owner, name, desc, itf);
	}

	@Override
	public void invokeinterface(String owner, String name, String desc) {
		addMethodVerifierInvocation(owner, name, desc);
		super.invokeinterface(owner, name, desc);
	}

	private void addMethodVerifierInvocation(String owner, String name, String desc) {
		// Original stack: owner,arg0,..,argN
		// Modified stack: owner,arg0,..,argN,owner,name
		Type methodType = Type.getType(desc);
		Type[] argTypes = methodType.getArgumentTypes();
		int[] argLocals = new int[argTypes.length];
		int argN = 0;
		// save all arguments from stack top
		for (Type argType : argTypes) {
			argLocals[argN] = variables.newLocal(argType);
			store(argLocals[argN++], argType);
		}
		Type ownerType = Type.getType("L" + owner + ";");
		int ownerLocal = variables.newLocal(ownerType);
		// duplicate owner on stack top
		dup();
		// save one of the owners
		store(ownerLocal, ownerType);
		argN = 0;
		// restore all arguments
		for (int argLocal : argLocals) {
			load(argLocal, argTypes[argN]);
		}
		// push owner and method name on stack
		load(ownerLocal, ownerType);
		aconst(name);
		invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyMethodInvocation", METHOD_VERIFIER_TYPE, false);
	}
}
