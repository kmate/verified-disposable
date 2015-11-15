package com.github.kmate.verified.disposable.matcher;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.github.kmate.verified.disposable.Disposable;
import com.github.kmate.verified.disposable.UsageException;

public class UsageTargetMatcher extends TypeSafeMatcher<UsageException> {

	public static UsageTargetMatcher hasTarget(Disposable expectedTarget) {
		return new UsageTargetMatcher(expectedTarget);
	}

	private final Disposable expectedTarget;
	private Disposable foundTarget;

	private UsageTargetMatcher(Disposable expectedTarget) {
		this.expectedTarget = expectedTarget;
	}

	@Override
	protected boolean matchesSafely(UsageException e) {
		foundTarget = e.getTargetObject();
		return expectedTarget == foundTarget;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(foundTarget).appendText(" was found instead of ").appendValue(expectedTarget);
	}
}
