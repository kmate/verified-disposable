package com.github.kmate.verified.disposable;

/**
 * Common interface for disposable objects. It does not determine how the
 * objects get into a disposed state, just provides a query to examine it. The
 * disposition of an object could be done in any preferred way, and must not be
 * final either.
 * <p>
 * Every time a field is accessed or a method is invoked on a {@link Disposable}
 * object, the runtime calls its {@link #isDisposed()} method to check whether
 * the given instance is marked as disposed. In these cases a specific
 * descendant of the run-time exception {@link UsageException} will be thrown to
 * indicate invalid usage of a disposed object.
 * <p>
 * The run-time verification must be initialized before any client class of a
 * disposable class is loaded, for details see {@link Agent}.
 * 
 * @see Agent
 * @see UsageException
 */
public interface Disposable {

	/**
	 * @return {@code true} when the object is marked as disposed, {@code false}
	 *         otherwise
	 */
	boolean isDisposed();
}
