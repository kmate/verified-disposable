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

	/**
	 * This method is also used to verify invocations with
	 * <em>invokeinterface</em>. In that case, it is unknown whether the current
	 * target also implements {@link Disposable}. This is why <em>target</em> is
	 * an arbitrary Object.
	 * 
	 * @param target
	 *            target of the invocation, maybe a Disposable
	 * @param methodName
	 *            name of the method invoked
	 */
	public static void verifyMethodInvocation(Object target, String methodName) {
		if (!(target instanceof Disposable)) {
			return;
		}

		Disposable disposable = (Disposable) target;
		if (disposable.isDisposed()) {
			throw new MethodInvocationException(disposable, methodName);
		}
	}
}
