package com.github.kmate.verified.disposable;

public class MethodInvocationException extends UsageException {

	private static final long serialVersionUID = -5939162641776497628L;

	public MethodInvocationException(Disposable target, String methodName) {
		super(createMessage(methodName), target, methodName);
	}

	public String getMethodName() {
		return getMemberName();
	}

	private static String createMessage(String methodName) {
		return "Invoking method of a disposed object:" + methodName;
	}
}
