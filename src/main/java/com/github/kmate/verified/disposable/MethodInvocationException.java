package com.github.kmate.verified.disposable;

public class MethodInvocationException extends UsageException {

	private static final long serialVersionUID = -8557728960127249090L;

	private final String methodName;

	public MethodInvocationException(Disposable target, String methodName) {
		super(createMessage(methodName), target);
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	private static String createMessage(String methodName) {
		return "Invoking method of a disposed object:" + methodName;
	}
}
