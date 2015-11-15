package com.github.kmate.verified.disposable.test;

import com.github.kmate.verified.disposable.Disposable;

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
