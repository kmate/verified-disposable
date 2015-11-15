package com.github.kmate.verified.disposable;

public interface DisposableObjectUser {

	Disposable disposeAndGetTarget();

	void readField();

	void writeField();

	void invokeVirtualMethod();

	void invokeInterfaceMethod();
}
