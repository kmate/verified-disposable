# verified-disposable [![Build Status](https://travis-ci.org/kmate/verified-disposable.svg?branch=master)](https://travis-ci.org/kmate/verified-disposable) [![Coverage Status](https://coveralls.io/repos/kmate/verified-disposable/badge.svg?branch=master&service=github)](https://coveralls.io/github/kmate/verified-disposable?branch=master) [![Dependency Status](https://www.versioneye.com/user/projects/5648fd9dcc00b0001c000001/badge.svg?style=flat)](https://www.versioneye.com/user/projects/5648fd9dcc00b0001c000001)
Disposable objects in Java with run-time usage verification.

## Purpose

This library provides run-time verification of instance field access and method invocation on objects which could be marked as disposed. Every time a field is accessed or a method is invoked on a `Disposable` object, the runtime calls its `isDisposed()` method to check whether the given instance is marked as disposed. In these cases a specific descendant of the run-time exception `UsageException` will be thrown to indicate invalid usage of a disposed object. Reading or writing a field will trigger `FieldAccessException`, while invocation of a method will rise `MethodInvocationException`. All of these exceptions are containing a reference of the target object and the name of the field or method used. Additionally, `FieldAccessException` also contains a flag which indicates whether a read or a write was performed on the given field. The `Disposable` interface does not determine how an object instance could be disposed, it is just provides a query for the verification about the state of a particular instance.

## Implementation

The implementation is based on [Java Instrumentation](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html) and on the [ASM](http://asm.ow2.org/) bytecode manipulation framework. When the instrumentation agent implemented by the `Agent` class is active, it registers a class transformer, which will manipulate all classes loaded thereafter. The transformation inserts a static call to the appropriate method of class `UsageVerifier` before each field access of `Disposable` objects, except for those which are done in the same `Disposable` class. Every non-static and non-private regular method of `Disposable` objects will also be extended with a similar check on its entry.

## Usage

### Creating disposable objects

Simply implement the `Disposable` interface. Note that the disposition of an object could be done in any preferred way, and must not be final either.

```java
import com.github.kmate.verified.disposable.Disposable;

public class TestSubject implements Disposable {

	@Override
	public boolean isDisposed() {
		return disposed;
	}	

	private boolean disposed;

	public void dispose() {
		disposed = true;
	}

	public int someField;

	public void someMethod() {
		System.out.println(someField);
	}
}
```

### Registration of the verifier agent

One could either use the `-javaagent` switch for the JVM, or call `Agent.initialize()`. The second aproach will work as expected only when it is called before any of the `Disposable` classes or their users are loaded. It may also require the `-XX:+StartAttachListener` command line option to be passed for the JVM.

### Using the disposable instances

Instances of `Disposable` classes can be used simply as normal Java objects. The only difference is that method invocation or field access will throw a specific runtime exception when their `isDisposed()` method returns true.

```java
TestSubject object = new TestSubject();

// regular field and method access
object.someField = object.someField + 42;
object.someMethod();

// isDisposed() will return true below this point
object.dispose();

// each of these lines will throw a specific UsageException
int valueRead = object.someField;
object.someField = 0;
object.someMethod();
```
