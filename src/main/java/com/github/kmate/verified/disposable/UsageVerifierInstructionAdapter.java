package com.github.kmate.verified.disposable;

import static org.objectweb.asm.Opcodes.ACC_BRIDGE;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

/**
 * Inserts a static call to the appropriate method of class
 * {@link UsageVerifier} before each field access of {@link Disposable} objects,
 * except for those which are done in the same disposable class. Every
 * non-static and non-private regular method of disposable objects will also be
 * extended with a similar check on its entry.
 * <p>
 * For the sake of clarity, the following methods will <b>not</b> be modified on
 * a disposable object:
 * <ul>
 * <li>constructors
 * <li>class initializer {@code <clinit>} and instance initializer {@code 
 * <init>}
 * <li>{@link #getClass()}
 * <li>{@link Disposable#isDisposed()}
 * <li>any static, private, synthetic and bridge methods
 * </ul>
 * 
 * @see UsageVerifierTransformer
 */
class UsageVerifierInstructionAdapter extends InstructionAdapter {

	private static final String USAGE_VERIFIER_CLASS_NAME = UsageVerifier.class.getName().replace('.', '/');
	private static final String VERIFIER_SIGNATURE = "(L" + ClassUtils.DISPOSABLE_CLASS_NAME + ";Ljava/lang/String;)V";

	private static final Set<String> UNCHECKED_METHODS = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList("<init>", "<clinit>", "getClass", "isDisposed")));

	private static final int HAS_NO_ACCESS = ACC_STATIC + ACC_PRIVATE + ACC_SYNTHETIC + ACC_BRIDGE;

	private final int access;
	private final String methodName;
	private final ClassReader reader;
	private final ClassLoader loader;
	private final LocalVariablesSorter variables;

	UsageVerifierInstructionAdapter(int access, String methodName, ClassReader reader, ClassLoader loader,
			LocalVariablesSorter variables) {
		super(Opcodes.ASM5, variables);
		this.access = access;
		this.methodName = methodName;
		this.reader = reader;
		this.loader = loader;
		this.variables = variables;
	}

	@Override
	public void visitCode() {
		if (shouldInstrumentMethod()) {
			visitVarInsn(Opcodes.ALOAD, 0);
			aconst(methodName);
			invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyMethodInvocation", VERIFIER_SIGNATURE, false);
		}
		super.visitCode();
	}

	private boolean shouldInstrumentMethod() {
		return hasPublicAccess() && !isUncheckedMethod() && ClassUtils.isDisposableClass(reader.getClassName(), loader);
	}

	private boolean hasPublicAccess() {
		return 0 == (access & HAS_NO_ACCESS);
	}

	private boolean isUncheckedMethod() {
		return isConstructor() || UNCHECKED_METHODS.contains(methodName);
	}

	private boolean isConstructor() {
		return reader.getClassName().equals(methodName);
	}

	@Override
	public void getfield(String owner, String name, String desc) {
		if (isDisposableExternalClass(owner)) {
			dup();
			aconst(name);
			invokestatic(USAGE_VERIFIER_CLASS_NAME, "verifyFieldRead", VERIFIER_SIGNATURE, false);
		}
		super.getfield(owner, name, desc);
	}

	@Override
	public void putfield(String owner, String name, String desc) {
		if (isDisposableExternalClass(owner)) {
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

	private boolean isDisposableExternalClass(String candidate) {
		if (reader.getClassName().equals(candidate)) {
			// Do not instrument the owner of the member,
			// as only external accesses have to be verified.
			return false;
		}
		return ClassUtils.isDisposableClass(candidate, loader);
	}
}