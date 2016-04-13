package com.github.kmate.verified.disposable;

public class DisposableTestObject extends TestObject implements Disposable {

	private boolean disposed = false;

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	public void dispose() {
		disposed = true;
	}
}
