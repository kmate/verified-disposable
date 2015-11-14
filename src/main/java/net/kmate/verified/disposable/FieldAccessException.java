package net.kmate.verified.disposable;

public class FieldAccessException extends UsageException {

	private static final long serialVersionUID = -8557728960127249090L;

	private final String fieldName;
	private final boolean isWrite;

	public FieldAccessException(Disposable target, String fieldName, boolean isWrite) {
		super(createMessage(fieldName, isWrite), target);
		this.fieldName = fieldName;
		this.isWrite = isWrite;
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isWrite() {
		return isWrite;
	}

	public boolean isRead() {
		return !isWrite;
	}

	private static String createMessage(String fieldName, boolean isWrite) {
		return (isWrite ? "Writing" : "Reading") + " field of a disposed object: " + fieldName;
	}
}
