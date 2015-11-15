package com.github.kmate.verified.disposable.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.kmate.verified.disposable.FieldAccessException;

public class FieldAccessNameMatcher extends TypeSafeMatcher<FieldAccessException> {

	public static FieldAccessNameMatcher hasFieldName(String fieldName) {
		return new FieldAccessNameMatcher(fieldName);
	}

	private final String expectedFieldName;
	private String foundFieldName;

	private FieldAccessNameMatcher(String expectedFieldName) {
		this.expectedFieldName = expectedFieldName;
	}

	@Override
	protected boolean matchesSafely(FieldAccessException e) {
		foundFieldName = e.getFieldName();
		return expectedFieldName.equals(foundFieldName);
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(foundFieldName).appendText(" was found instead of ").appendValue(expectedFieldName);
	}
}
