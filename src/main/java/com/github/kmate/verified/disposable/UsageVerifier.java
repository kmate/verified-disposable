package com.github.kmate.verified.disposable;

/**
 * Implements verification methods. Each of them just throws a specific
 * {@link UsageException} when the target object is marked as disposed.
 * 
 * @see Disposable#isDisposed()
 * @see Agent
 */
public abstract class UsageVerifier {

	public static void verifyFieldRead(Disposable target, String fieldName) {
		if (target.isDisposed()) {
			throw new FieldAccessException(target, fieldName, false);
		}
	}

	public static void verifyFieldWrite(Disposable target, String fieldName) {
		if (target.isDisposed()) {
			throw new FieldAccessException(target, fieldName, true);
		}
	}

	public static void verifyMethodInvocation(Disposable target, String methodName) {
		if (target.isDisposed()) {
			throw new MethodInvocationException(target, methodName);
		}
	}
}
