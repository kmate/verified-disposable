package com.github.kmate.verified.disposable.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.kmate.verified.disposable.FieldAccessException;

public class FieldAccessModeMatcher extends TypeSafeMatcher<FieldAccessException> {

	public static FieldAccessModeMatcher isRead() {
		return new FieldAccessModeMatcher(false);
	}

	public static FieldAccessModeMatcher isWrite() {
		return new FieldAccessModeMatcher(true);
	}

	private final boolean expectedIsWrite;
	private boolean foundIsWrite;

	private FieldAccessModeMatcher(boolean expectedIsWrite) {
		this.expectedIsWrite = expectedIsWrite;
	}

	@Override
	protected boolean matchesSafely(FieldAccessException e) {
		foundIsWrite = e.isWrite();
		return expectedIsWrite == foundIsWrite;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(foundIsWrite).appendText(" was found instead of ").appendValue(expectedIsWrite);
	}
}
