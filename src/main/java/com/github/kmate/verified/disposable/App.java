package com.github.kmate.verified.disposable;

import java.lang.instrument.Instrumentation;

import com.github.kmate.verified.disposable.test.TestUser;

import net.nicoulaj.instrument.AutoInstrumentation;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		Instrumentation i = AutoInstrumentation.getInstrumentation();
		i.addTransformer(new UsageVerifierTransformer());

		new TestUser().testValidOperations();

		try {
			new TestUser().testDisposedReadCategory1();
		} catch (FieldAccessException e) {
			System.out.println(e);
		}

		try {
			new TestUser().testDisposedReadCategory2();
		} catch (FieldAccessException e) {
			System.out.println(e);
		}

		try {
			new TestUser().testDisposedWriteCategory1();
		} catch (FieldAccessException e) {
			System.out.println(e);
		}

		try {
			new TestUser().testDisposedWriteCategory2();
		} catch (FieldAccessException e) {
			System.out.println(e);
		}

		try {
			new TestUser().testDisposedInvoke();
		} catch (MethodInvocationException e) {
			System.out.println(e);
		}
	}
}
