package com.github.kmate.verified.disposable;

public class DisposableTestObject implements Disposable {

	private boolean disposed = false;

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	public void dispose() {
		disposed = true;
	}

	public int testField;

	public void testMethod(int testParam) {
	}
}
