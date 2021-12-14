/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.lang;

/**
 * An instance of {@code ThreadDeath} is thrown in the victim thread
 * when the (deprecated) {@link Thread#stop()} method is invoked.
 * 1.当调用（不推荐使用的） Thread.stop()方法时，会在受害者线程中抛出一个 ThreadDeath实例。
 * <p>An application should catch instances of this class only if it
 * must clean up after being terminated asynchronously.  If
 * {@code ThreadDeath} is caught by a method, it is important that it
 * be rethrown so that the thread actually dies.
 * 2.仅当应用程序在异步终止后必须进行清理时，它才应捕获此类的实例。如果  ThreadDeath被方法捕获，
 * 重要的是将其重新抛出，以便线程实际死亡
 * <p>The {@linkplain ThreadGroup#uncaughtException top-level error
 * handler} does not print out a message if {@code ThreadDeath} is
 * never caught.
 * 3.如果 ThreadDeath从未被捕获，则 ThreadGroup.uncaughtException 顶级错误处理程序不会打印出消息。
 * <p>The class {@code ThreadDeath} is specifically a subclass of
 * {@code Error} rather than {@code Exception}, even though it is a
 * "normal occurrence", because many applications catch all
 * occurrences of {@code Exception} and then discard the exception.
 * 4.ThreadDeath类特别是 Error而不是 Exception的子类，即使它是“正常发生”，
 * 因为许多应用程序捕获所有出现的 Exception然后丢弃例外
 * @since   JDK1.0
 */

public class ThreadDeath extends Error {
    private static final long serialVersionUID = -4417128565033088268L;
}
