package com.github.kmate.verified.disposable;

public class FieldAccessException extends UsageException {

	private static final long serialVersionUID = -3603967466195116288L;

	private final boolean isWrite;

	public FieldAccessException(Disposable target, String fieldName, boolean isWrite) {
		super(createMessage(fieldName, isWrite), target, fieldName);
		this.isWrite = isWrite;
	}

	public String getFieldName() {
		return getMemberName();
	}

	public boolean isWrite() {
		return isWrite;
	}

	private static String createMessage(String fieldName, boolean isWrite) {
		return (isWrite ? "Writing" : "Reading") + " field of a disposed object: " + fieldName;
	}
}
