package com.github.kmate.verified.disposable;

import static com.github.kmate.verified.disposable.matcher.FieldAccessModeMatcher.isRead;
import static com.github.kmate.verified.disposable.matcher.FieldAccessModeMatcher.isWrite;
import static com.github.kmate.verified.disposable.matcher.FieldAccessNameMatcher.hasFieldName;
import static com.github.kmate.verified.disposable.matcher.MethodInvocationNameMatcher.hasMethodName;
import static com.github.kmate.verified.disposable.matcher.UsageTargetMatcher.hasTarget;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UsageVerifierDisposedTest {

	private static final String TEST_FIELD_NAME = "testField";
	private static final String TEST_METHOD_NAME = "testMethod";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private AlwaysDisposed target;

	private static class AlwaysDisposed implements Disposable {

		@Override
		public boolean isDisposed() {
			return true;
		}
	};

	@Before
	public void setUp() {
		target = new AlwaysDisposed();
	}

	@Test
	public void testReadingFieldThrowsException() {
		thrown.expect(FieldAccessException.class);
		thrown.expect(hasTarget(target));
		thrown.expect(hasFieldName(TEST_FIELD_NAME));
		thrown.expect(isRead());
		UsageVerifier.verifyFieldRead(target, TEST_FIELD_NAME);
	}

	@Test
	public void testWritingFieldThrowsException() {
		thrown.expect(FieldAccessException.class);
		thrown.expect(hasTarget(target));
		thrown.expect(hasFieldName(TEST_FIELD_NAME));
		thrown.expect(isWrite());
		UsageVerifier.verifyFieldWrite(target, TEST_FIELD_NAME);
	}

	@Test
	public void testInvokingMethodThrowsException() {
		thrown.expect(MethodInvocationException.class);
		thrown.expect(hasTarget(target));
		thrown.expect(hasMethodName(TEST_METHOD_NAME));
		UsageVerifier.verifyMethodInvocation(target, TEST_METHOD_NAME);
	}
}
