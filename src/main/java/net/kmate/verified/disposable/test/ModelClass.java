package net.kmate.verified.disposable.test;

import net.kmate.verified.disposable.Disposable;

public class ModelClass implements Disposable {

	private boolean disposed = false;

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	public void dispose() {
		disposed = true;
	}
}
