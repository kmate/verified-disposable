package net.kmate.verified.disposable.test;

public class TestUser {

	@SuppressWarnings("unused")
	public void testValidOperations() {
		TestClass tc = new TestClass();
		int readCategory1 = tc.testFieldCategory1;
		long readCategory2 = tc.testFieldCategory2;
		tc.testFieldCategory1 = 0;
		tc.testFieldCategory2 = 0;
		tc.testMethod(20, 2);
	}

	@SuppressWarnings("unused")
	public void testDisposedReadCategory1() {
		TestClass tc = new TestClass();
		tc.dispose();
		int disposedRead = tc.testFieldCategory1;
	}

	@SuppressWarnings("unused")
	public void testDisposedReadCategory2() {
		TestClass tc = new TestClass();
		tc.dispose();
		long disposedRead = tc.testFieldCategory2;
	}

	public void testDisposedWriteCategory1() {
		TestClass tc = new TestClass();
		tc.dispose();
		tc.testFieldCategory1 = 0;
	}

	public void testDisposedWriteCategory2() {
		TestClass tc = new TestClass();
		tc.dispose();
		tc.testFieldCategory2 = 0;
	}

	public void testDisposedInvoke() {
		TestClass tc = new TestClass();
		tc.dispose();
		tc.testMethod(20, 2);
	}
}
