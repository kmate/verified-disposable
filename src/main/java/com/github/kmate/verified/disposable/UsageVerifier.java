package com.github.kmate.verified.disposable;

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
