package com.github.kmate.verified.disposable.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.kmate.verified.disposable.MethodInvocationException;

public class MethodInvocationNameMatcher extends TypeSafeMatcher<MethodInvocationException> {

	public static MethodInvocationNameMatcher hasMethodName(String methodName) {
		return new MethodInvocationNameMatcher(methodName);
	}

	private final String expectedMethodName;
	private String foundMethodName;

	private MethodInvocationNameMatcher(String expectedMethodName) {
		this.expectedMethodName = expectedMethodName;
	}

	@Override
	protected boolean matchesSafely(MethodInvocationException e) {
		foundMethodName = e.getMethodName();
		return expectedMethodName.equals(foundMethodName);
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(foundMethodName).appendText(" was found instead of ").appendValue(expectedMethodName);
	}
}
