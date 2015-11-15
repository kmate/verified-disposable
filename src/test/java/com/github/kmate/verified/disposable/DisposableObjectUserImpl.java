package com.github.kmate.verified.disposable;

public class DisposableObjectUserImpl implements DisposableObjectUser {

	private final DisposableTestObject target = new DisposableTestObject();

	@Override
	public Disposable disposeAndGetTarget() {
		target.dispose();
		return target;
	}

	@Override
	@SuppressWarnings("unused")
	public void readField() {
		int value = target.testField;
	}

	@Override
	public void writeField() {
		target.testField = 0;
	}

	@Override
	public void invokeVirtualMethod() {
		target.testMethod(0);
	}

	@Override
	public void invokeInterfaceMethod() {
		TestMethod interfaceTarget = target;
		interfaceTarget.testMethod(0);
	}
}
