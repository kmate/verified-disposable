package com.github.kmate.verified.disposable;

public class UsageException extends RuntimeException {

	private static final long serialVersionUID = -1820625258037355800L;

	private final Disposable target;
	private final String memberName;

	public UsageException(String message, Disposable target, String memberName) {
		super(message);
		this.target = target;
		this.memberName = memberName;
	}

	public Disposable getTargetObject() {
		return target;
	}

	public String getMemberName() {
		return memberName;
	}
}
