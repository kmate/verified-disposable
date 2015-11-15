package com.github.kmate.verified.disposable;

import static com.github.kmate.verified.disposable.matcher.FieldAccessModeMatcher.isRead;
import static com.github.kmate.verified.disposable.matcher.FieldAccessModeMatcher.isWrite;
import static com.github.kmate.verified.disposable.matcher.FieldAccessNameMatcher.hasFieldName;
import static com.github.kmate.verified.disposable.matcher.MethodInvocationNameMatcher.hasMethodName;
import static com.github.kmate.verified.disposable.matcher.UsageTargetMatcher.hasTarget;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UsageVerifierTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private DisposableObjectUser user;

	@BeforeClass
	public static void setUpBeforeClass() {
		Agent.initialize();
	}

	@Before
	public void setUp() {
		user = new DisposableObjectUserImpl();
	}

	@Test
	public void testReadingField() {
		user.readField();
	}

	@Test
	public void testWritingField() {
		user.writeField();
	}

	@Test
	public void testInvokingMethod() {
		user.invokeVirtualMethod();
		user.invokeInterfaceMethod();
		user.invokeVirtualMethod();
	}

	@Test
	public void testReadingFieldThrowsException() {
		thrown.expect(FieldAccessException.class);
		thrown.expect(hasTarget(user.disposeAndGetTarget()));
		thrown.expect(hasFieldName("testField"));
		thrown.expect(isRead());
		user.readField();
	}

	@Test
	public void testWritingFieldThrowsException() {
		thrown.expect(FieldAccessException.class);
		thrown.expect(hasTarget(user.disposeAndGetTarget()));
		thrown.expect(hasFieldName("testField"));
		thrown.expect(isWrite());
		user.writeField();
	}

	@Test
	public void testInvokingVirtualMethodThrowsException() {
		expectMethodInvocationException();
		user.invokeVirtualMethod();
	}

	@Test
	public void testInvokingInterfaceMethodThrowsException() {
		expectMethodInvocationException();
		user.invokeInterfaceMethod();
	}

	private void expectMethodInvocationException() {
		thrown.expect(MethodInvocationException.class);
		thrown.expect(hasTarget(user.disposeAndGetTarget()));
		thrown.expect(hasMethodName("testMethod"));
	}
}
