/*
 * Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * An object that may hold resources (such as file or socket handles)
 * until it is closed. The {@link #close()} method of an {@code AutoCloseable}
 * object is called automatically when exiting a {@code
 * try}-with-resources block for which the object has been declared in
 * the resource specification header. This construction ensures prompt
 * release, avoiding resource exhaustion exceptions and errors that
 * may otherwise occur.
 *  1.这个接口是作用于一些掌握某些资源的对象(比如一些文件或者建立连接),而这个接口可以在这些对象
 *  退出之前迅速的释放这些资源,避免出现一些异常和错误
 *
 * @apiNote
 * <p>It is possible, and in fact common, for a base class to
 * implement AutoCloseable even though not all of its subclasses or
 * instances will hold releasable resources.  For code that must operate
 * in complete generality, or when it is known that the {@code AutoCloseable}
 * instance requires resource release, it is recommended to use {@code
 * try}-with-resources constructions. However, when using facilities such as
 * {@link java.util.stream.Stream} that support both I/O-based and
 * non-I/O-based forms, {@code try}-with-resources blocks are in
 * general unnecessary when using non-I/O-based forms.
 *  2.即使一个基类下的部分子类和实例并不持有某些可释放的资源,实现该接口也并不意外,意思就是多实现AutoCloseable也没有什么问题
 *  3.对于一些需要去释放资源的对象,推荐使用try-catch-resource的形式来释放资源,但是也有一些特殊情形,比如Stream中的Nio
 *  并不是必须的
 * @author Josh Bloch
 * @since 1.7
 */
public interface AutoCloseable {
    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * 1.这个方法就是通过try-with-resource结构来自动管理释放资源
     *
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     * 2.这个方法是会抛出异常,但是可以通过更具体的实现类来抛出更具体的异常(为了更方便的排查问题)
     * ,当天也可以选择不抛出异常,如果操作不会失败的话
     *
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     * 3,需要实施者注意的是关闭操作可能失败的情况,因此强烈建议在抛出异常之前,
     * 放弃底层的资源并将其标记会关闭.这个方法不太可能被执行多次,因此要确保这些资源被
     * 及时的释放.此外它减少了一些资源包装或被包装时可能出现的问题
     *
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * 4.强烈建议实现者,不要在这个接口抛出InterruptedException异常
     *
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * 5.此异常(InterruptedException)会和线程的中断状态交互,可能会导致运行时不正常的行为出现
     *
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     * 6.更一般的说,如果它导致一些异常被压制,AutoCloseable接口就不应该抛出InterruptedException
     * 7.因此,说来说去就是尽量不要抛出InterruptedException,避免不必要的麻烦
     *
     * <p>Note that unlike the {@link java.io.Closeable#close close}
     * method of {@link java.io.Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * 8.这个接口的方法和Closeable中的close方法不一样,这个AutoCloseable的方法没有被要求幂等
     * 换句话说,多次调用这个方法可能会出现一些副作用,但是Closeable的close方法多次调用没有影响
     *
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     * 9.因此强烈建议,实现者们,让这个方法具有幂等性
     *
     * @throws Exception if this resource cannot be closed
     */
    void close() throws Exception;
}
