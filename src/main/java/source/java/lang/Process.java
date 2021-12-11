/*
 * Copyright (c) 1995, 2012, Oracle and/or its affiliates. All rights reserved.
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

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * The {@link ProcessBuilder#start()} and
 * {@link Runtime#exec(String[],String[],File) Runtime.exec}
 * methods create a native process and return an instance of a
 * subclass of {@code Process} that can be used to control the process
 * and obtain information about it.  The class {@code Process}
 * provides methods for performing input from the process, performing
 * output to the process, waiting for the process to complete,
 * checking the exit status of the process, and destroying (killing)
 * the process.
 * 1. ProcessBuilder.start()和 Runtime.exec(String[],String[],File)
 * 方法创建本机进程并返回可以使用的Process子类的实例控制过程并获取有关它的信息。
 * Process类提供了执行进程输入、执行输出到进程、等待进程完成、检查进程退出状态以及销毁（杀死）进程的方法
 * <p>The methods that create processes may not work well for special
 * processes on certain native platforms, such as native windowing
 * processes, daemon processes, Win16/DOS processes on Microsoft
 * Windows, or shell scripts.
 * 2.创建进程的方法可能不适用于某些原生平台上的特殊进程，
 * 例如原生窗口进程、守护进程、Microsoft Windows 上的 Win16DOS 进程或 shell 脚本。
 * <p>By default, the created subprocess does not have its own terminal
 * or console.  All its standard I/O (i.e. stdin, stdout, stderr)
 * operations will be redirected to the parent process, where they can
 * be accessed via the streams obtained using the methods
 * {@link #getOutputStream()},
 * {@link #getInputStream()}, and
 * {@link #getErrorStream()}.
 * The parent process uses these streams to feed input to and get output
 * from the subprocess.  Because some native platforms only provide
 * limited buffer size for standard input and output streams, failure
 * to promptly write the input stream or read the output stream of
 * the subprocess may cause the subprocess to block, or even deadlock.
 * 3.默认情况下，创建的子进程没有自己的终端或控制台。
 * 它的所有标准 IO（即 stdin、stdout、stderr）操作都将被重定向到父进程，
 * 在那里可以通过使用 getOutputStream()、getInputStream()和 getErrorStream()。
 * 父进程使用这些流向子进程提供输入和从子进程获取输出。由于一些原生平台只为标准输入输出流提供有限的缓冲区大小，
 * 如果不能及时写入输入流或读取子进程的输出流，可能会导致子进程阻塞，甚至死锁
 * <p>Where desired, <a href="ProcessBuilder.html#redirect-input">
 * subprocess I/O can also be redirected</a>
 * using methods of the {@link ProcessBuilder} class.
 * 4.如果需要，子进程 IO 也可以使用ProcessBuilder类的方法重定向
 * <p>The subprocess is not killed when there are no more references to
 * the {@code Process} object, but rather the subprocess
 * continues executing asynchronously.
 * 5.当没有更多对 Process对象的引用时，子进程不会被终止，而是子进程继续异步执行。
 * <p>There is no requirement that a process represented by a {@code
 * Process} object execute asynchronously or concurrently with respect
 * to the Java process that owns the {@code Process} object.
 * 6.不要求由Process对象表示的进程与拥有Process对象的 Java 进程异步或并发执行
 * <p>As of 1.5, {@link ProcessBuilder#start()} is the preferred way
 * to create a {@code Process}.
*7.从 1.5 开始，ProcessBuilder.start()是创建 Process的首选方式
 * @since   JDK1.0
 */
public abstract class Process {
    /**
     * Returns the output stream connected to the normal input of the
     * subprocess.  Output to the stream is piped into the standard
     * input of the process represented by this {@code Process} object.
     * 1.返回连接到子进程的正常输入的输出流。流的输出通过管道传输到此 Process对象表示的进程的标准输入中
     * <p>If the standard input of the subprocess has been redirected using
     * {@link ProcessBuilder#redirectInput(Redirect)
     * ProcessBuilder.redirectInput}
     * then this method will return a
     * <a href="ProcessBuilder.html#redirect-input">null output stream</a>.
     * 2.如果子流程的标准输入已使用 ProcessBuilder.redirectInput(Redirect)重定向，
     * 则此方法将返回空输出流。
     * <p>Implementation note: It is a good idea for the returned
     * output stream to be buffered.
     * 3.实现说明：将返回的输出流缓冲是一个好主意。
     * @return the output stream connected to the normal input of the
     *         subprocess
     */
    public abstract OutputStream getOutputStream();

    /**
     * Returns the input stream connected to the normal output of the
     * subprocess.  The stream obtains data piped from the standard
     * output of the process represented by this {@code Process} object.
     * 1.返回连接到子进程正常输出的输入流。该流从由该Process对象表示的进程的标准输出中获取管道数据。
     * <p>If the standard output of the subprocess has been redirected using
     * {@link ProcessBuilder#redirectOutput(Redirect)
     * ProcessBuilder.redirectOutput}
     * then this method will return a
     * <a href="ProcessBuilder.html#redirect-output">null input stream</a>.
     * 2.如果子流程的标准输出已使用ProcessBuilder.redirectOutput(Redirect)重定向，
     * 则此方法将返回 空输入流。
     * <p>Otherwise, if the standard error of the subprocess has been
     * redirected using
     * {@link ProcessBuilder#redirectErrorStream(boolean)
     * ProcessBuilder.redirectErrorStream}
     * then the input stream returned by this method will receive the
     * merged standard output and the standard error of the subprocess.
     * 3否则，如果子进程的标准错误已经使用 ProcessBuilder.redirectErrorStream(boolean) 重定向，
     * 则此方法返回的输入流将接收合并的标准输出和子进程的标准错误。
     * <p>Implementation note: It is a good idea for the returned
     * input stream to be buffered.
     * 4.实现说明：对返回的输入流进行缓冲是个好主意
     * @return the input stream connected to the normal output of the
     *         subprocess
     */
    public abstract InputStream getInputStream();

    /**
     * Returns the input stream connected to the error output of the
     * subprocess.  The stream obtains data piped from the error output
     * of the process represented by this {@code Process} object.
     * 1.返回连接到子进程错误输出的输入流。该流从该 Process对象表示的进程的错误输出中获取管道数据。
     * <p>If the standard error of the subprocess has been redirected using
     * {@link ProcessBuilder#redirectError(Redirect)
     * ProcessBuilder.redirectError} or
     * {@link ProcessBuilder#redirectErrorStream(boolean)
     * ProcessBuilder.redirectErrorStream}
     * then this method will return a
     * <a href="ProcessBuilder.html#redirect-output">null input stream</a>.
     * 2.如果子流程的标准错误已使用ProcessBuilder.redirectError(Redirect)
     * 或 ProcessBuilder.redirectErrorStream(boolean)重定向，则此方法将返回 空输入流。
     * <p>Implementation note: It is a good idea for the returned
     * input stream to be buffered.
     * 3.实现说明：对返回的输入流进行缓冲是个好主意
     * @return the input stream connected to the error output of
     *         the subprocess
     */
    public abstract InputStream getErrorStream();

    /**
     * Causes the current thread to wait, if necessary, until the
     * process represented by this {@code Process} object has
     * terminated.  This method returns immediately if the subprocess
     * has already terminated.  If the subprocess has not yet
     * terminated, the calling thread will be blocked until the
     * subprocess exits.
     * 1.如有必要，使当前线程等待，直到此Process对象表示的进程终止。如果子进程已经终止，
     * 则此方法立即返回。如果子进程尚未终止，调用线程将被阻塞，直到子进程退出
     * @return the exit value of the subprocess represented by this
     *         {@code Process} object.  By convention, the value
     *         {@code 0} indicates normal termination.
     * @throws InterruptedException if the current thread is
     *         {@linkplain Thread#interrupt() interrupted} by another
     *         thread while it is waiting, then the wait is ended and
     *         an {@link InterruptedException} is thrown.
     */
    public abstract int waitFor() throws InterruptedException;

    /**
     * Causes the current thread to wait, if necessary, until the
     * subprocess represented by this {@code Process} object has
     * terminated, or the specified waiting time elapses.
     * 1.使当前线程在必要时等待，直到此Process对象表示的子进程终止，或指定的等待时间过去
     * <p>If the subprocess has already terminated then this method returns
     * immediately with the value {@code true}.  If the process has not
     * terminated and the timeout value is less than, or equal to, zero, then
     * this method returns immediately with the value {@code false}.
     * 2.如果子进程已经终止，则此方法立即返回值true。如果进程尚未终止且超时值小于或等于零，则此方法立即返回值false
     * <p>The default implementation of this methods polls the {@code exitValue}
     * to check if the process has terminated. Concrete implementations of this
     * class are strongly encouraged to override this method with a more
     * efficient implementation.
     * 3.此方法的默认实现轮询 exitValue以检查进程是否已终止。强烈鼓励此类的具体实现以更有效的实现覆盖此方法。
     * @param timeout the maximum time to wait
     * @param unit the time unit of the {@code timeout} argument
     * @return {@code true} if the subprocess has exited and {@code false} if
     *         the waiting time elapsed before the subprocess has exited.
     * @throws InterruptedException if the current thread is interrupted
     *         while waiting.
     * @throws NullPointerException if unit is null
     * @since 1.8
     */
    public boolean waitFor(long timeout, TimeUnit unit)
        throws InterruptedException
    {
        long startTime = System.nanoTime();
        long rem = unit.toNanos(timeout);

        do {
            try {
                exitValue();
                return true;
            } catch(IllegalThreadStateException ex) {
                if (rem > 0)
                    Thread.sleep(
                        Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
            }
            rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
        } while (rem > 0);
        return false;
    }

    /**
     * Returns the exit value for the subprocess.
     * 返回子进程的退出值
     * @return the exit value of the subprocess represented by this
     *         {@code Process} object.  By convention, the value
     *         {@code 0} indicates normal termination.
     * @throws IllegalThreadStateException if the subprocess represented
     *         by this {@code Process} object has not yet terminated
     */
    public abstract int exitValue();

    /**
     * Kills the subprocess. Whether the subprocess represented by this
     * {@code Process} object is forcibly terminated or not is
     * implementation dependent.
     * 杀死子进程。此Process对象表示的子进程是否被强制终止取决于实现。
     */
    public abstract void destroy();

    /**
     * Kills the subprocess. The subprocess represented by this
     * {@code Process} object is forcibly terminated.
     * 1.杀死子进程。这个 Process对象所代表的子进程被强行终止
     * <p>The default implementation of this method invokes {@link #destroy}
     * and so may not forcibly terminate the process. Concrete implementations
     * of this class are strongly encouraged to override this method with a
     * compliant implementation.  Invoking this method on {@code Process}
     * objects returned by {@link ProcessBuilder#start} and
     * {@link Runtime#exec} will forcibly terminate the process.
     * 2.此方法的默认实现调用destroy，因此可能不会强制终止进程。强烈鼓励此类的具体实现使用兼容实现覆盖此方法。
     * 在  ProcessBuilder.start和 Runtime.exec返回的Process对象上调用此方法将强制终止进程。
     * <p>Note: The subprocess may not terminate immediately.
     * i.e. {@code isAlive()} may return true for a brief period
     * after {@code destroyForcibly()} is called. This method
     * may be chained to {@code waitFor()} if needed.
     * 3.注意：子进程可能不会立即终止。即isAlive()可能会在调用destroyForcibly()后的短时间内返回 true。
     * 如果需要，此方法可以链接到 waitFor()。
     * @return the {@code Process} object representing the
     *         subprocess to be forcibly destroyed.
     * @since 1.8
     */
    public Process destroyForcibly() {
        destroy();
        return this;
    }

    /**
     * Tests whether the subprocess represented by this {@code Process} is
     * alive.
     * 测试此 Process表示的子进程是否处于活动状态
     * @return {@code true} if the subprocess represented by this
     *         {@code Process} object has not yet terminated.
     * @since 1.8
     */
    public boolean isAlive() {
        try {
            exitValue();
            return false;
        } catch(IllegalThreadStateException e) {
            return true;
        }
    }
}
