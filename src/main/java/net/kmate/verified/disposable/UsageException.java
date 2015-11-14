package net.kmate.verified.disposable;

public class UsageException extends RuntimeException {

	private static final long serialVersionUID = -1820625258037355800L;

	private final Disposable target;

	public UsageException(String message, Disposable target) {
		super(message);
		this.target = target;
	}

	public Disposable getTargetObject() {
		return target;
	}
}
