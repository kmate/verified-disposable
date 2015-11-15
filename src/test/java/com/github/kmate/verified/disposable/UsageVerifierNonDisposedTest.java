package com.github.kmate.verified.disposable;

import org.junit.Before;
import org.junit.Test;

public class UsageVerifierNonDisposedTest extends UsageVerifier {

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
		verifyFieldRead(target, "testField");
	}

	@Test
	public void testWritingField() {
		verifyFieldWrite(target, "testField");
	}

	@Test
	public void testInvokingMethod() {
		verifyMethodInvocation(target, "testMethod");
	}
}
