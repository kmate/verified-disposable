package com.github.kmate.verified.disposable;

import java.lang.instrument.Instrumentation;

import net.nicoulaj.instrument.AutoInstrumentation;

/**
 * Initializes the run-time verification system. When the instrumentation agent
 * is active, it registers a {@linkplain UsageVerifierTransformer class
 * transformer}, which will examine all classes loaded thereafter. The
 * transformation inserts a static call to the appropriate method of class
 * {@link UsageVerifier} before each field access of {@link Disposable} objects,
 * except for those which are done in the same class. Each non-static and
 * non-private regular method of disposable objects will also be extended with a
 * similar check on its entry, except {@link Disposable#isDisposed()}.
 * <p>
 * It is important to initialize the agent before any client classes of
 * disposable classes are loaded. This could be done in the following two ways:
 * <li>A static call to {@link #initialize()}. In this case, the Java program
 * must be started with VM argument {@code -XX:+StartAttachListener}.
 * <li>Using this class as a Java agent with the {@code -javaagent} VM argument.
 */
public class Agent {

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		addTransformer(instrumentation);
	}

	public static void agentmain(String agentArgs, Instrumentation instrumentation) {
		addTransformer(instrumentation);
	}

	/**
	 * Initialize the instrumentation agent in the current running process. This
	 * must be called before any client classes of disposable classes are
	 * loaded. The Java program must be started with VM argument
	 * {@code -XX:+StartAttachListener}, otherwise this method will throw a
	 * runtime exception.
	 */
	public static void initialize() {
		Instrumentation instrumentation = AutoInstrumentation.getInstrumentation();
		addTransformer(instrumentation);
	}

	private static void addTransformer(Instrumentation instrumentation) {
		instrumentation.addTransformer(new UsageVerifierTransformer());
	}
}
