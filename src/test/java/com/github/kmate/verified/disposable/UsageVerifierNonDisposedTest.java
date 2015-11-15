package com.github.kmate.verified.disposable;

import org.junit.Before;
import org.junit.Test;

public class UsageVerifierNonDisposedTest {

	private NeverDisposed target;

	private static class NeverDisposed implements Disposable {

		@Override
		public boolean isDisposed() {
			return false;
		}
	};

	@Before
	public void setUp() {
		target = new NeverDisposed();
	}

	@Test
	public void testReadingField() {
		UsageVerifier.verifyFieldRead(target, "testField");
	}

	@Test
	public void testWritingField() {
		UsageVerifier.verifyFieldWrite(target, "testField");
	}

	@Test
	public void testInvokingMethod() {
		UsageVerifier.verifyMethodInvocation(target, "testMethod");
	}
}
