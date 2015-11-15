package com.github.kmate.verified.disposable;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ClassUtilsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private final ClassLoader loader = getClass().getClassLoader();

	@Test
	public void testObjectIsSystemClass() {
		assertTrue(ClassUtils.isSystemClass("java/lang/Object"));
	}

	@Test
	public void testNamingContextIsSystemClass() {
		assertTrue(ClassUtils.isSystemClass("javax/naming/Context"));
	}

	@Test
	public void testDisposableCheckOnMissingClass() {
		final String invalidClassName = "an invalid class name";
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage(containsString(invalidClassName));
		ClassUtils.isDisposableClass(invalidClassName, loader);
	}

	@Test
	public void testObjectIsNotDisposable() {
		assertFalse(ClassUtils.isDisposableClass(Object.class.getName(), loader));
	}

	@Test
	public void testNonDisposable() {
		assertFalse(ClassUtils.isDisposableClass(NonDisposable.class.getName(), loader));
	}

	private static class NonDisposable {
	}

	@Test
	public void testDirectImplementationOfDisposable() {
		assertTrue(ClassUtils.isDisposableClass(DirectImplementor.class.getName(), loader));
	}

	private static class DirectImplementor implements Disposable {

		@Override
		public boolean isDisposed() {
			return false;
		}
	}

	@Test
	public void testInheritedClassImplementationOfDisposable() {
		assertTrue(ClassUtils.isDisposableClass(InheritedClassImplementor.class.getName(), loader));
	}

	private static class InheritedClassImplementor extends DirectImplementor {
	}

	@Test
	public void testInheritedInterfaceImplementationOfDisposable() {
		assertTrue(ClassUtils.isDisposableClass(InheritedInterfaceImplementor.class.getName(), loader));
	}

	private static interface InheritedDisposable extends Disposable {
	}

	private static class InheritedInterfaceImplementor implements InheritedDisposable {

		@Override
		public boolean isDisposed() {
			return false;
		}
	}
}
