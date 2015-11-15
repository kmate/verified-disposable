package com.github.kmate.verified.disposable;

import java.lang.instrument.Instrumentation;

import net.nicoulaj.instrument.AutoInstrumentation;

public class Agent {

	public static void premain(String agentArgs, Instrumentation instrumentation) {
		addTransformer(instrumentation);
	}

	public static void agentmain(String agentArgs, Instrumentation instrumentation) {
		addTransformer(instrumentation);
	}

	public static void initialize() {
		Instrumentation instrumentation = AutoInstrumentation.getInstrumentation();
		addTransformer(instrumentation);
	}

	private static void addTransformer(Instrumentation instrumentation) {
		instrumentation.addTransformer(new UsageVerifierTransformer());
	}
}
