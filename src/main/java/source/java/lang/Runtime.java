/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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
import java.util.StringTokenizer;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

/**
 * Every Java application has a single instance of class
 * <code>Runtime</code> that allows the application to interface with
 * the environment in which the application is running. The current
 * runtime can be obtained from the <code>getRuntime</code> method.
 * 1.每个 Java 应用程序都有一个Runtime类的单个实例，
 * 它允许应用程序与应用程序运行的环境进行交互。当前运行时可以从getRuntime方法获得
 * <p>
 * An application cannot create its own instance of this class.
 *
 * @author  unascribed
 * @see     java.lang.Runtime#getRuntime()
 * @since   JDK1.0
 */

public class Runtime {
    private static Runtime currentRuntime = new Runtime();

    /**
     * Returns the runtime object associated with the current Java application.
     * Most of the methods of class <code>Runtime</code> are instance
     * methods and must be invoked with respect to the current runtime object.
     * 1.返回与当前 Java 应用程序关联的运行时对象。 Runtime类的大多数方法都是实例方法，必须针对当前运行时对象调用。
     * @return  the <code>Runtime</code> object associated with the current
     *          Java application.
     */
    public static Runtime getRuntime() {
        return currentRuntime;
    }

    /** Don't let anyone else instantiate this class */
    //不要让其他人实例化这个类
    private Runtime() {}

    /**
     * Terminates the currently running Java virtual machine by initiating its
     * shutdown sequence.  This method never returns normally.  The argument
     * serves as a status code; by convention, a nonzero status code indicates
     * abnormal termination.
     * 1.通过启动其关闭序列来终止当前运行的 Java 虚拟机。此方法永远不会正常返回。
     * 参数用作状态码；按照惯例，非零状态代码表示异常终止。
     * <p> The virtual machine's shutdown sequence consists of two phases.  In
     * the first phase all registered {@link #addShutdownHook shutdown hooks},
     * if any, are started in some unspecified order and allowed to run
     * concurrently until they finish.  In the second phase all uninvoked
     * finalizers are run if {@link #runFinalizersOnExit finalization-on-exit}
     * has been enabled.  Once this is done the virtual machine {@link #halt
     * halts}.
     * 2.虚拟机的关闭顺序包括两个阶段。在第一阶段，所有注册的addShutdownHook 关闭钩子
     * （如果有）都以某种未指定的顺序启动，并允许并发运行直到它们完成。在第二阶段，
     * 如果runFinalizersOnExit finalization-on-exit已启用，则所有未调用的终结器都会运行。
     * 完成此操作后，虚拟机暂停。
     * <p> If this method is invoked after the virtual machine has begun its
     * shutdown sequence then if shutdown hooks are being run this method will
     * block indefinitely.  If shutdown hooks have already been run and on-exit
     * finalization has been enabled then this method halts the virtual machine
     * with the given status code if the status is nonzero; otherwise, it
     * blocks indefinitely.
     * 3.如果在虚拟机开始其关闭序列后调用此方法，则如果正在运行关闭挂钩，则此方法将无限期阻塞。
     * 如果关闭钩子已经运行并且退出完成已经启用，那么如果状态非零，则此方法将停止具有给定状态代码的虚拟机；
     * 否则，它会无限期地阻塞
     * <p> The <tt>{@link System#exit(int) System.exit}</tt> method is the
     * conventional and convenient means of invoking this method. <p>
     * 4. System.exit(int)方法是调用此方法的常规且方便的方法
     * @param  status
     *         Termination status.  By convention, a nonzero status code
     *         indicates abnormal termination.
     *
     * @throws SecurityException
     *         If a security manager is present and its <tt>{@link
     *         SecurityManager#checkExit checkExit}</tt> method does not permit
     *         exiting with the specified status
     *
     * @see java.lang.SecurityException
     * @see java.lang.SecurityManager#checkExit(int)
     * @see #addShutdownHook
     * @see #removeShutdownHook
     * @see #runFinalizersOnExit
     * @see #halt(int)
     */
    public void exit(int status) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkExit(status);
        }
        Shutdown.exit(status);
    }

    /**
     * Registers a new virtual-machine shutdown hook.
     * 1.注册一个新的虚拟机关闭钩子
     * <p> The Java virtual machine <i>shuts down</i> in response to two kinds
     * of events:
     *
     *   <ul>
     *
     *   <li> The program <i>exits</i> normally, when the last non-daemon
     *   thread exits or when the <tt>{@link #exit exit}</tt> (equivalently,
     *   {@link System#exit(int) System.exit}) method is invoked, or
     *
     *   <li> The virtual machine is <i>terminated</i> in response to a
     *   user interrupt, such as typing <tt>^C</tt>, or a system-wide event,
     *   such as user logoff or system shutdown.
     * 2.程序退出正常，当最后一个非守护线程退出时或者当exit （相当于，System.exit(int) ）方法被调用，
     * 或虚拟机被终止以响应用户中断，例如键入^C，或系统范围的事件，例如用户注销或系统关掉。
     *   </ul>
     *
     * <p> A <i>shutdown hook</i> is simply an initialized but unstarted
     * thread.  When the virtual machine begins its shutdown sequence it will
     * start all registered shutdown hooks in some unspecified order and let
     * them run concurrently.  When all the hooks have finished it will then
     * run all uninvoked finalizers if finalization-on-exit has been enabled.
     * Finally, the virtual machine will halt.  Note that daemon threads will
     * continue to run during the shutdown sequence, as will non-daemon threads
     * if shutdown was initiated by invoking the <tt>{@link #exit exit}</tt>
     * method.
     * 3.shutdown hook只是一个初始化但未启动的线程。当虚拟机开始其关闭序列时，
     * 它将以某种未指定的顺序启动所有已注册的关闭挂钩，并让它们同时运行。当所有钩子都完成后，
     * 如果启用了退出时终结，它将运行所有未调用的终结器。最后，虚拟机将停止。请注意，
     * 守护线程将在关闭序列期间继续运行，如果关闭是通过调用 exit 方法启动的，那么非守护线程也将继续运行。
     * <p> Once the shutdown sequence has begun it can be stopped only by
     * invoking the <tt>{@link #halt halt}</tt> method, which forcibly
     * terminates the virtual machine.
     * 4.一旦关闭序列开始，它只能通过调用halt方法来停止，该方法强制终止虚拟机。
     * <p> Once the shutdown sequence has begun it is impossible to register a
     * new shutdown hook or de-register a previously-registered hook.
     * Attempting either of these operations will cause an
     * <tt>{@link IllegalStateException}</tt> to be thrown.
     * 5.一旦关闭序列开始，就不可能注册新的关闭挂钩或取消注册先前注册的挂钩。
     * 尝试这些操作中的任何一个都会导致抛出 IllegalStateException。
     * <p> Shutdown hooks run at a delicate time in the life cycle of a virtual
     * machine and should therefore be coded defensively.  They should, in
     * particular, be written to be thread-safe and to avoid deadlocks insofar
     * as possible.  They should also not rely blindly upon services that may
     * have registered their own shutdown hooks and therefore may themselves in
     * the process of shutting down.  Attempts to use other thread-based
     * services such as the AWT event-dispatch thread, for example, may lead to
     * deadlocks.
     * 6.关闭钩子在虚拟机生命周期的一个微妙时刻运行，因此应该进行防御性编码。特别是，它们应该被编写为线程安全的，
     * 并尽可能避免死锁。他们也不应该盲目依赖可能已经注册了自己的关闭钩子的服务，因此他们自己可能会在关闭过程中。
     * 例如，尝试使用其他基于线程的服务（例如 AWT 事件分派线程）可能会导致死锁。
     * <p> Shutdown hooks should also finish their work quickly.  When a
     * program invokes <tt>{@link #exit exit}</tt> the expectation is
     * that the virtual machine will promptly shut down and exit.  When the
     * virtual machine is terminated due to user logoff or system shutdown the
     * underlying operating system may only allow a fixed amount of time in
     * which to shut down and exit.  It is therefore inadvisable to attempt any
     * user interaction or to perform a long-running computation in a shutdown
     * hook.
     * 7.关闭钩子也应该快速完成它们的工作。当程序调用 exit时，期望虚拟机将立即关闭并退出。
     * 当虚拟机由于用户注销或系统关闭而终止时，底层操作系统可能只允许关闭和退出的固定时间量。
     * 因此，不建议尝试任何用户交互或在关闭挂钩中执行长时间运行的计算
     * <p> Uncaught exceptions are handled in shutdown hooks just as in any
     * other thread, by invoking the <tt>{@link ThreadGroup#uncaughtException
     * uncaughtException}</tt> method of the thread's <tt>{@link
     * ThreadGroup}</tt> object.  The default implementation of this method
     * prints the exception's stack trace to <tt>{@link System#err}</tt> and
     * terminates the thread; it does not cause the virtual machine to exit or
     * halt.
     * 8.通过调用线程的 ThreadGroup对象的ThreadGroup.uncaughtException方法，在关闭钩子中处理未捕获的异常，
     * 就像在任何其他线程中一样。此方法的默认实现将异常的堆栈跟踪打印到 System.err并终止线程；它不会导致虚拟机退出或停止
     * <p> In rare circumstances the virtual machine may <i>abort</i>, that is,
     * stop running without shutting down cleanly.  This occurs when the
     * virtual machine is terminated externally, for example with the
     * <tt>SIGKILL</tt> signal on Unix or the <tt>TerminateProcess</tt> call on
     * Microsoft Windows.  The virtual machine may also abort if a native
     * method goes awry by, for example, corrupting internal data structures or
     * attempting to access nonexistent memory.  If the virtual machine aborts
     * then no guarantee can be made about whether or not any shutdown hooks
     * will be run. <p>
     * 9.在极少数情况下，虚拟机可能中止，即停止运行而不完全关闭。当虚拟机从外部终止时会发生这种情况，
     * 例如在 Unix 上使用 SIGKILL信号或在 Microsoft Windows 上使用 TerminateProcess调用。
     * 如果本地方法出错，例如破坏内部数据结构或尝试访问不存在的内存，虚拟机也可能中止。
     * 如果虚拟机中止，则无法保证是否会运行任何关闭挂钩。
     * @param   hook
     *          An initialized but unstarted <tt>{@link Thread}</tt> object
     *
     * @throws  IllegalArgumentException
     *          If the specified hook has already been registered,
     *          or if it can be determined that the hook is already running or
     *          has already been run
     *
     * @throws  IllegalStateException
     *          If the virtual machine is already in the process
     *          of shutting down
     *
     * @throws  SecurityException
     *          If a security manager is present and it denies
     *          <tt>{@link RuntimePermission}("shutdownHooks")</tt>
     *
     * @see #removeShutdownHook
     * @see #halt(int)
     * @see #exit(int)
     * @since 1.3
     */
    public void addShutdownHook(Thread hook) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("shutdownHooks"));
        }
        ApplicationShutdownHooks.add(hook);
    }

    /**
     * De-registers a previously-registered virtual-machine shutdown hook. <p>
     * 取消注册先前注册的虚拟机关闭挂钩
     * @param hook the hook to remove
     * @return <tt>true</tt> if the specified hook had previously been
     * registered and was successfully de-registered, <tt>false</tt>
     * otherwise.
     *
     * @throws  IllegalStateException
     *          If the virtual machine is already in the process of shutting
     *          down
     *
     * @throws  SecurityException
     *          If a security manager is present and it denies
     *          <tt>{@link RuntimePermission}("shutdownHooks")</tt>
     *
     * @see #addShutdownHook
     * @see #exit(int)
     * @since 1.3
     */
    public boolean removeShutdownHook(Thread hook) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("shutdownHooks"));
        }
        return ApplicationShutdownHooks.remove(hook);
    }

    /**
     * Forcibly terminates the currently running Java virtual machine.  This
     * method never returns normally.
     * 1.强制终止当前运行的 Java 虚拟机。此方法永远不会正常返回。
     * <p> This method should be used with extreme caution.  Unlike the
     * <tt>{@link #exit exit}</tt> method, this method does not cause shutdown
     * hooks to be started and does not run uninvoked finalizers if
     * finalization-on-exit has been enabled.  If the shutdown sequence has
     * already been initiated then this method does not wait for any running
     * shutdown hooks or finalizers to finish their work. <p>
     *2.这种方法应该非常谨慎地使用。与 exit方法不同，此方法不会导致关闭挂钩启动，
     * 并且如果启用了退出时终结，则不会运行未调用的终结器。如果关闭序列已经启动，
     * 则此方法不会等待任何正在运行的关闭挂钩或终结器完成其工作。
     * @param  status
     *         Termination status.  By convention, a nonzero status code
     *         indicates abnormal termination.  If the <tt>{@link Runtime#exit
     *         exit}</tt> (equivalently, <tt>{@link System#exit(int)
     *         System.exit}</tt>) method has already been invoked then this
     *         status code will override the status code passed to that method.
     *
     * @throws SecurityException
     *         If a security manager is present and its <tt>{@link
     *         SecurityManager#checkExit checkExit}</tt> method does not permit
     *         an exit with the specified status
     *
     * @see #exit
     * @see #addShutdownHook
     * @see #removeShutdownHook
     * @since 1.3
     */
    public void halt(int status) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkExit(status);
        }
        Shutdown.halt(status);
    }

    /**
     * Enable or disable finalization on exit; doing so specifies that the
     * finalizers of all objects that have finalizers that have not yet been
     * automatically invoked are to be run before the Java runtime exits.
     * By default, finalization on exit is disabled.
     * 1.退出时启用或禁用终结；这样做指定所有具有尚未自动调用的终结器的对象的终结器将在 Java 运行时退出之前运行。
     * 默认情况下，退出时的终结是禁用的
     * <p>If there is a security manager,
     * its <code>checkExit</code> method is first called
     * with 0 as its argument to ensure the exit is allowed.
     * This could result in a SecurityException.
     * 2.如果存在安全管理器，则首先调用其 checkExit方法，并使用 0 作为其参数以确保允许退出。这可能会导致 SecurityException。
     * @param value true to enable finalization on exit, false to disable
     * @deprecated  This method is inherently unsafe.  It may result in
     *      finalizers being called on live objects while other threads are
     *      concurrently manipulating those objects, resulting in erratic
     *      behavior or deadlock.
     *
     * @throws  SecurityException
     *        if a security manager exists and its <code>checkExit</code>
     *        method doesn't allow the exit.
     *
     * @see     java.lang.Runtime#exit(int)
     * @see     java.lang.Runtime#gc()
     * @see     java.lang.SecurityManager#checkExit(int)
     * @since   JDK1.1
     */
    @Deprecated
    public static void runFinalizersOnExit(boolean value) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            try {
                security.checkExit(0);
            } catch (SecurityException e) {
                throw new SecurityException("runFinalizersOnExit");
            }
        }
        Shutdown.setRunFinalizersOnExit(value);
    }

    /**
     * Executes the specified string command in a separate process.
     * 1.在单独的进程中执行指定的字符串命令。
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(command)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String, String[], File) exec}(command, null, null)</tt>.
     * 2.这是一种方便的方法。exec(command)形式的调用与调用exec(String, String[], File) exec}(command, null, null)。
     * @param   command   a specified system command.
     *
     * @return  A new {@link Process} object for managing the subprocess
     *
     * @throws  SecurityException
     *          If a security manager exists and its
     *          {@link SecurityManager#checkExec checkExec}
     *          method doesn't allow creation of the subprocess
     *
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @throws  NullPointerException
     *          If <code>command</code> is <code>null</code>
     *
     * @throws  IllegalArgumentException
     *          If <code>command</code> is empty
     *
     * @see     #exec(String[], String[], File)
     * @see     ProcessBuilder
     */
    public Process exec(String command) throws IOException {
        return exec(command, null, null);
    }

    /**
     * Executes the specified string command in a separate process with the
     * specified environment.
     * 1.在具有指定环境的单独进程中执行指定的字符串命令。
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(command, envp)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String, String[], File) exec}(command, envp, null)</tt>.
     * 2.这是一种方便的方法。exec(command, envp)形式的调用与调用 exec(String, String[], File) exec}(command, envp, null )。
     * @param   command   a specified system command.
     *
     * @param   envp      array of strings, each element of which
     *                    has environment variable settings in the format
     *                    <i>name</i>=<i>value</i>, or
     *                    <tt>null</tt> if the subprocess should inherit
     *                    the environment of the current process.
     *
     * @return  A new {@link Process} object for managing the subprocess
     *
     * @throws  SecurityException
     *          If a security manager exists and its
     *          {@link SecurityManager#checkExec checkExec}
     *          method doesn't allow creation of the subprocess
     *
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @throws  NullPointerException
     *          If <code>command</code> is <code>null</code>,
     *          or one of the elements of <code>envp</code> is <code>null</code>
     *
     * @throws  IllegalArgumentException
     *          If <code>command</code> is empty
     *
     * @see     #exec(String[], String[], File)
     * @see     ProcessBuilder
     */
    public Process exec(String command, String[] envp) throws IOException {
        return exec(command, envp, null);
    }

    /**
     * Executes the specified string command in a separate process with the
     * specified environment and working directory.
     * 1.在具有指定环境和工作目录的单独进程中执行指定的字符串命令
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(command, envp, dir)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String[], String[], File) exec}(cmdarray, envp, dir)</tt>,
     * where <code>cmdarray</code> is an array of all the tokens in
     * <code>command</code>.
     * 2.这是一种方便的方法。exec(command, envp, dir)形式的调用与调用 exec(String[], String[], File)
     * exec(cmdarray , envp, dir)，其中cmdarray是 command中所有标记的数组
     *
     * <p>More precisely, the <code>command</code> string is broken
     * into tokens using a {@link StringTokenizer} created by the call
     * <code>new {@link StringTokenizer}(command)</code> with no
     * further modification of the character categories.  The tokens
     * produced by the tokenizer are then placed in the new string
     * array <code>cmdarray</code>, in the same order.
     * 3.更准确地说，command字符串使用由调用new StringTokenizer(command)创建的StringTokenizer分解为标记，
     * 而没有进一步修改字符类别.然后将标记器生成的标记以相同的顺序放置在新的字符串数组cmdarray中。
     * @param   command   a specified system command.
     *
     * @param   envp      array of strings, each element of which
     *                    has environment variable settings in the format
     *                    <i>name</i>=<i>value</i>, or
     *                    <tt>null</tt> if the subprocess should inherit
     *                    the environment of the current process.
     *
     * @param   dir       the working directory of the subprocess, or
     *                    <tt>null</tt> if the subprocess should inherit
     *                    the working directory of the current process.
     *
     * @return  A new {@link Process} object for managing the subprocess
     *
     * @throws  SecurityException
     *          If a security manager exists and its
     *          {@link SecurityManager#checkExec checkExec}
     *          method doesn't allow creation of the subprocess
     *
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @throws  NullPointerException
     *          If <code>command</code> is <code>null</code>,
     *          or one of the elements of <code>envp</code> is <code>null</code>
     *
     * @throws  IllegalArgumentException
     *          If <code>command</code> is empty
     *
     * @see     ProcessBuilder
     * @since 1.3
     */
    public Process exec(String command, String[] envp, File dir)
        throws IOException {
        if (command.length() == 0)
            throw new IllegalArgumentException("Empty command");

        StringTokenizer st = new StringTokenizer(command);
        String[] cmdarray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            cmdarray[i] = st.nextToken();
        return exec(cmdarray, envp, dir);
    }

    /**
     * Executes the specified command and arguments in a separate process.
     * 1.在单独的进程中执行指定的命令和参数
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(cmdarray)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String[], String[], File) exec}(cmdarray, null, null)</tt>.
     *
     * @param   cmdarray  array containing the command to call and
     *                    its arguments.
     * 2.这是一种方便的方法。 exec(cmdarray)形式的调用与调用 exec(String[], String[], File)
     * @return  A new {@link Process} object for managing the subprocess
     *
     * @throws  SecurityException
     *          If a security manager exists and its
     *          {@link SecurityManager#checkExec checkExec}
     *          method doesn't allow creation of the subprocess
     *
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @throws  NullPointerException
     *          If <code>cmdarray</code> is <code>null</code>,
     *          or one of the elements of <code>cmdarray</code> is <code>null</code>
     *
     * @throws  IndexOutOfBoundsException
     *          If <code>cmdarray</code> is an empty array
     *          (has length <code>0</code>)
     *
     * @see     ProcessBuilder
     */
    public Process exec(String cmdarray[]) throws IOException {
        return exec(cmdarray, null, null);
    }

    /**
     * Executes the specified command and arguments in a separate process
     * with the specified environment.
     * 1.在具有指定环境的单独进程中执行指定的命令和参数。
     * <p>This is a convenience method.  An invocation of the form
     * <tt>exec(cmdarray, envp)</tt>
     * behaves in exactly the same way as the invocation
     * <tt>{@link #exec(String[], String[], File) exec}(cmdarray, envp, null)</tt>.
     * 2.这是一种方便的方法。 <tt>exec(cmdarray, envp)形式的调用与调用 exec(String[], String[], File)的行为完全相同, 空)<tt>。
     * @param   cmdarray  array containing the command to call and
     *                    its arguments.
     *
     * @param   envp      array of strings, each element of which
     *                    has environment variable settings in the format
     *                    <i>name</i>=<i>value</i>, or
     *                    <tt>null</tt> if the subprocess should inherit
     *                    the environment of the current process.
     *
     * @return  A new {@link Process} object for managing the subprocess
     *
     * @throws  SecurityException
     *          If a security manager exists and its
     *          {@link SecurityManager#checkExec checkExec}
     *          method doesn't allow creation of the subprocess
     *
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @throws  NullPointerException
     *          If <code>cmdarray</code> is <code>null</code>,
     *          or one of the elements of <code>cmdarray</code> is <code>null</code>,
     *          or one of the elements of <code>envp</code> is <code>null</code>
     *
     * @throws  IndexOutOfBoundsException
     *          If <code>cmdarray</code> is an empty array
     *          (has length <code>0</code>)
     *
     * @see     ProcessBuilder
     */
    public Process exec(String[] cmdarray, String[] envp) throws IOException {
        return exec(cmdarray, envp, null);
    }


    /**
     * Executes the specified command and arguments in a separate process with
     * the specified environment and working directory.
     * 1.在具有指定环境和工作目录的单独进程中执行指定的命令和参数
     * <p>Given an array of strings <code>cmdarray</code>, representing the
     * tokens of a command line, and an array of strings <code>envp</code>,
     * representing "environment" variable settings, this method creates
     * a new process in which to execute the specified command.
     * 2.给定一个字符串数组cmdarray，代表命令行的标记，以及一个字符串数组envp，代表“环境”变量设置，
     * 这个方法创建了一个新的进程，在其中执行指定的命令
     * <p>This method checks that <code>cmdarray</code> is a valid operating
     * system command.  Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty list of
     * non-null strings.
     * 3.此方法检查cmdarray是否是有效的操作系统命令。哪些命令有效取决于系统，但至少命令必须是非空字符串的非空列表。
     * <p>If <tt>envp</tt> is <tt>null</tt>, the subprocess inherits the
     * environment settings of the current process.
     * 4.如果envp为null，子进程继承当前进程的环境设置
     * <p>A minimal set of system dependent environment variables may
     * be required to start a process on some operating systems.
     * As a result, the subprocess may inherit additional environment variable
     * settings beyond those in the specified environment.
     * 5.在某些操作系统上启动进程可能需要一组最小的系统相关环境变量。因此，
     * 子进程可能会继承指定环境中的环境变量设置之外的其他环境变量设置。
     * <p>{@link ProcessBuilder#start()} is now the preferred way to
     * start a process with a modified environment.
     * 6.ProcessBuilder.start()现在是使用修改后的环境启动进程的首选方式。
     * <p>The working directory of the new subprocess is specified by <tt>dir</tt>.
     * If <tt>dir</tt> is <tt>null</tt>, the subprocess inherits the
     * current working directory of the current process.
     * 7.新子进程的工作目录由dir指定。如果dir为null，子进程继承当前进程的当前工作目录
     * <p>If a security manager exists, its
     * {@link SecurityManager#checkExec checkExec}
     * method is invoked with the first component of the array
     * <code>cmdarray</code> as its argument. This may result in a
     * {@link SecurityException} being thrown.
     * 8.如果存在安全管理器，则使用数组cmdarray的第一个组件作为其参数调用其  SecurityManager.checkExec方法。
     * 这可能会导致抛出 SecurityException
     * <p>Starting an operating system process is highly system-dependent.
     * Among the many things that can go wrong are:
     * <ul>
     * <li>The operating system program file was not found.
     * <li>Access to the program file was denied.
     * <li>The working directory does not exist.
     * </ul>
     * 9.启动操作系统进程高度依赖于系统。可能出错的许多事情包括：
     * 找不到操作系统程序文件。
     * 对程序文件的访问被拒绝。
     * 工作目录不存在。
     * <p>In such cases an exception will be thrown.  The exact nature
     * of the exception is system-dependent, but it will always be a
     * subclass of {@link IOException}.
     * 10在这种情况下，将抛出异常。异常的确切性质取决于系统，但它始终是IOException的子类。
     *
     * @param   cmdarray  array containing the command to call and
     *                    its arguments.
     *
     * @param   envp      array of strings, each element of which
     *                    has environment variable settings in the format
     *                    <i>name</i>=<i>value</i>, or
     *                    <tt>null</tt> if the subprocess should inherit
     *                    the environment of the current process.
     *
     * @param   dir       the working directory of the subprocess, or
     *                    <tt>null</tt> if the subprocess should inherit
     *                    the working directory of the current process.
     *
     * @return  A new {@link Process} object for managing the subprocess
     *
     * @throws  SecurityException
     *          If a security manager exists and its
     *          {@link SecurityManager#checkExec checkExec}
     *          method doesn't allow creation of the subprocess
     *
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @throws  NullPointerException
     *          If <code>cmdarray</code> is <code>null</code>,
     *          or one of the elements of <code>cmdarray</code> is <code>null</code>,
     *          or one of the elements of <code>envp</code> is <code>null</code>
     *
     * @throws  IndexOutOfBoundsException
     *          If <code>cmdarray</code> is an empty array
     *          (has length <code>0</code>)
     *
     * @see     ProcessBuilder
     * @since 1.3
     */
    public Process exec(String[] cmdarray, String[] envp, File dir)
        throws IOException {
        return new ProcessBuilder(cmdarray)
            .environment(envp)
            .directory(dir)
            .start();
    }

    /**
     * Returns the number of processors available to the Java virtual machine.
     * 1.返回 Java 虚拟机可用的处理器数
     * <p> This value may change during a particular invocation of the virtual
     * machine.  Applications that are sensitive to the number of available
     * processors should therefore occasionally poll this property and adjust
     * their resource usage appropriately. </p>
     * 2.该值可能会在虚拟机的特定调用期间发生变化。因此，
     * 对可用处理器数量敏感的应用程序应偶尔轮询此属性并适当调整其资源使用。
     * @return  the maximum number of processors available to the virtual
     *          machine; never smaller than one
     * @since 1.4
     */
    public native int availableProcessors();

    /**
     * Returns the amount of free memory in the Java Virtual Machine.
     * Calling the
     * <code>gc</code> method may result in increasing the value returned
     * by <code>freeMemory.</code>
     * 返回 Java 虚拟机中的可用内存量。调用gc方法可能会导致freeMemory.返回的值增加
     * @return  an approximation to the total amount of memory currently
     *          available for future allocated objects, measured in bytes.
     */
    public native long freeMemory();

    /**
     * Returns the total amount of memory in the Java virtual machine.
     * The value returned by this method may vary over time, depending on
     * the host environment.
     * 1.返回 Java 虚拟机中的总内存量。此方法返回的值可能会随时间变化，具体取决于主机环
     * <p>
     * Note that the amount of memory required to hold an object of any
     * given type may be implementation-dependent.
     * 2.请注意，保存任何给定类型的对象所需的内存量可能取决于实现
     * @return  the total amount of memory currently available for current
     *          and future objects, measured in bytes.
     */
    public native long totalMemory();

    /**
     * Returns the maximum amount of memory that the Java virtual machine will
     * attempt to use.  If there is no inherent limit then the value {@link
     * java.lang.Long#MAX_VALUE} will be returned.
     * 1.返回 Java 虚拟机将尝试使用的最大内存量。如果没有固有限制，则将返回值java.lang.LongMAX_VALUE。
     * @return  the maximum amount of memory that the virtual machine will
     *          attempt to use, measured in bytes
     * @since 1.4
     */
    public native long maxMemory();

    /**
     * Runs the garbage collector.
     * Calling this method suggests that the Java virtual machine expend
     * effort toward recycling unused objects in order to make the memory
     * they currently occupy available for quick reuse. When control
     * returns from the method call, the virtual machine has made
     * its best effort to recycle all discarded objects.
     * 1.运行垃圾收集器。调用此方法表明 Java 虚拟机将努力回收未使用的对象，
     * 以使它们当前占用的内存可用于快速重用。当控制从方法调用返回时，虚拟机已尽最大努力回收所有丢弃的对象
     * <p>
     * The name <code>gc</code> stands for "garbage
     * collector". The virtual machine performs this recycling
     * process automatically as needed, in a separate thread, even if the
     * <code>gc</code> method is not invoked explicitly.
     * 2.名称gc代表“垃圾收集器”。虚拟机根据需要在单独的线程中自动执行此回收过程，即使gc方法未显式调用
     * <p>
     * The method {@link System#gc()} is the conventional and convenient
     * means of invoking this method.
     * 方法System.gc()是调用此方法的常规且方便的方法。
     */
    public native void gc();

    /* Wormhole for calling java.lang.ref.Finalizer.runFinalization */
    //调用 java.lang.ref.Finalizer.runFinalization 的虫洞
    private static native void runFinalization0();

    /**
     * Runs the finalization methods of any objects pending finalization.
     * Calling this method suggests that the Java virtual machine expend
     * effort toward running the <code>finalize</code> methods of objects
     * that have been found to be discarded but whose <code>finalize</code>
     * methods have not yet been run. When control returns from the
     * method call, the virtual machine has made a best effort to
     * complete all outstanding finalizations.
     * 1.运行任何挂起终结的对象的终结方法。调用此方法表明 Java 虚拟机将努力运行已发现
     * 已被丢弃但其 finalize方法尚未运行的对象的finalize方法。当控制从方法调用返回时，
     * 虚拟机已尽最大努力完成所有未完成的完成
     * <p>
     * The virtual machine performs the finalization process
     * automatically as needed, in a separate thread, if the
     * <code>runFinalization</code> method is not invoked explicitly.
     * 2.如果runFinalization方法没有被显式调用，虚拟机会根据需要在单独的线程中自动执行终结过程。
     * <p>
     * The method {@link System#runFinalization()} is the conventional
     * and convenient means of invoking this method.
     * 3方法System.runFinalization()是调用此方法的常规且方便的方法
     * @see     java.lang.Object#finalize()
     */
    public void runFinalization() {
        runFinalization0();
    }

    /**
     * Enables/Disables tracing of instructions.
     * If the <code>boolean</code> argument is <code>true</code>, this
     * method suggests that the Java virtual machine emit debugging
     * information for each instruction in the virtual machine as it
     * is executed. The format of this information, and the file or other
     * output stream to which it is emitted, depends on the host environment.
     * The virtual machine may ignore this request if it does not support
     * this feature. The destination of the trace output is system
     * dependent.
     * 1.EnablesDisables 跟踪指令。如果boolean参数为true，
     * 则此方法建议 Java 虚拟机在执行时为虚拟机中的每条指令发出调试信息。此信息的格式，
     * 以及它发送到的文件或其他输出流，取决于主机环境。如果虚拟机不支持此功能，它可能会忽略此请求。
     * 跟踪输出的目的地取决于系统。
     * <p>
     * If the <code>boolean</code> argument is <code>false</code>, this
     * method causes the virtual machine to stop performing the
     * detailed instruction trace it is performing.
     * 如果boolean参数为false，此方法会导致虚拟机停止执行它正在执行的详细指令跟踪。
     * @param   on   <code>true</code> to enable instruction tracing;
     *               <code>false</code> to disable this feature.
     */
    public native void traceInstructions(boolean on);

    /**
     * Enables/Disables tracing of method calls.
     * If the <code>boolean</code> argument is <code>true</code>, this
     * method suggests that the Java virtual machine emit debugging
     * information for each method in the virtual machine as it is
     * called. The format of this information, and the file or other output
     * stream to which it is emitted, depends on the host environment. The
     * virtual machine may ignore this request if it does not support
     * this feature.
     * 1.EnablesDisables 跟踪方法调用。如果boolean参数为true，则此方法建议 Java 虚拟机在调用时
     * 为虚拟机中的每个方法发出调试信息。此信息的格式，以及它发送到的文件或其他输出流，取决于主机环境。
     * 如果虚拟机不支持此功能，它可能会忽略此请求
     * <p>
     * Calling this method with argument false suggests that the
     * virtual machine cease emitting per-call debugging information.
     * 2.使用参数 false 调用此方法表明虚拟机停止发出每次调用调试信息。
     *
     * @param   on   <code>true</code> to enable instruction tracing;
     *               <code>false</code> to disable this feature.
     */
    public native void traceMethodCalls(boolean on);

    /**
     * Loads the native library specified by the filename argument.  The filename
     * argument must be an absolute path name.
     * (for example
     * <code>Runtime.getRuntime().load("/home/avh/lib/libX11.so");</code>).
     * 1.加载由文件名参数指定的本机库。文件名参数必须是绝对路径名。
     * （例如Runtime.getRuntime().load("homeavhliblibX11.so");）
     * If the filename argument, when stripped of any platform-specific library
     * prefix, path, and file extension, indicates a library whose name is,
     * for example, L, and a native library called L is statically linked
     * with the VM, then the JNI_OnLoad_L function exported by the library
     * is invoked rather than attempting to load a dynamic library.
     * A filename matching the argument does not have to exist in the file
     * system. See the JNI Specification for more details.
     * 2.如果 filename 参数在去除任何特定于平台的库前缀、路径和文件扩展名后表示名称为 L 的库，
     * 并且名为 L 的本机库与 VM 静态链接，则 JNI_OnLoad_L 函数由库导出被调用而不是尝试加载动态库。
     * 文件系统中不必存在与参数匹配的文件名。有关更多详细信息，请参阅 JNI 规范。
     *
     * Otherwise, the filename argument is mapped to a native library image in
     * an implementation-dependent manner.
     * 3.否则，文件名参数将以依赖于实现的方式映射到本机库映像。
     * <p>
     * First, if there is a security manager, its <code>checkLink</code>
     * method is called with the <code>filename</code> as its argument.
     * This may result in a security exception.
     * 4.首先，如果有一个安全管理器，它的checkLink方法将被调用，并将filename作为它的参数。这可能会导致安全异常
     * <p>
     * This is similar to the method {@link #loadLibrary(String)}, but it
     * accepts a general file name as an argument rather than just a library
     * name, allowing any file of native code to be loaded.
     * 5.这类似于loadLibrary(String)方法，但它接受通用文件名作为参数而不仅仅是库名，允许加载任何本地代码文件。
     * <p>
     * The method {@link System#load(String)} is the conventional and
     * convenient means of invoking this method.
     * 6.方法System.load(String)是调用此方法的常规且方便的方法
     *
     * @param      filename   the file to load.
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkLink</code> method doesn't allow
     *             loading of the specified dynamic library
     * @exception  UnsatisfiedLinkError  if either the filename is not an
     *             absolute path name, the native library is not statically
     *             linked with the VM, or the library cannot be mapped to
     *             a native library image by the host system.
     * @exception  NullPointerException if <code>filename</code> is
     *             <code>null</code>
     * @see        java.lang.Runtime#getRuntime()
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     */
    @CallerSensitive
    public void load(String filename) {
        load0(Reflection.getCallerClass(), filename);
    }

    synchronized void load0(Class<?> fromClass, String filename) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkLink(filename);
        }
        if (!(new File(filename).isAbsolute())) {
            throw new UnsatisfiedLinkError(
                "Expecting an absolute path of the library: " + filename);
        }
        ClassLoader.loadLibrary(fromClass, filename, true);
    }

    /**
     * Loads the native library specified by the <code>libname</code>
     * argument.  The <code>libname</code> argument must not contain any platform
     * specific prefix, file extension or path. If a native library
     * called <code>libname</code> is statically linked with the VM, then the
     * JNI_OnLoad_<code>libname</code> function exported by the library is invoked.
     * See the JNI Specification for more details.
     * 1.加载由libname参数指定的本机库。libname参数不得包含任何特定于平台的前缀、文件扩展名或路径。
     * 如果名为libname的本机库与 VM 静态链接，则调用该库导出的 JNI_OnLoad_libname函数。有关更多详细信息，请参阅 JNI 规范。
     * Otherwise, the libname argument is loaded from a system library
     * location and mapped to a native library image in an implementation-
     * dependent manner.
     * 2.否则，libname 参数将从系统库位置加载并以依赖于实现的方式映射到本机库映像。
     * <p>
     * First, if there is a security manager, its <code>checkLink</code>
     * method is called with the <code>libname</code> as its argument.
     * This may result in a security exception.
     * 3.首先，如果有一个安全管理器，它的checkLink方法被调用，libname作为它的参数。这可能会导致安全异常。
     * <p>
     * The method {@link System#loadLibrary(String)} is the conventional
     * and convenient means of invoking this method. If native
     * methods are to be used in the implementation of a class, a standard
     * strategy is to put the native code in a library file (call it
     * <code>LibFile</code>) and then to put a static initializer:
     * <blockquote><pre>
     * static { System.loadLibrary("LibFile"); }
     * </pre></blockquote>
     * 4.方法 System.loadLibrary(String)是调用此方法的常规且方便的方法。如果要在类的实现中使用本机方法，
     * 标准策略是将本机代码放入库文件中（称为 LibFile），然后放入静态初始化程序：static { System.loadLibrary("LibFile"); }
     * within the class declaration. When the class is loaded and
     * initialized, the necessary native code implementation for the native
     * methods will then be loaded as well.
     * 5.在类声明中。当类被加载和初始化时，本地方法的必要本地代码实现也将被加载
     * <p>
     * If this method is called more than once with the same library
     * name, the second and subsequent calls are ignored.
     * 6.如果使用相同的库名多次调用此方法，则忽略第二次和后续调用
     * @param      libname   the name of the library.
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkLink</code> method doesn't allow
     *             loading of the specified dynamic library
     * @exception  UnsatisfiedLinkError if either the libname argument
     *             contains a file path, the native library is not statically
     *             linked with the VM,  or the library cannot be mapped to a
     *             native library image by the host system.
     * @exception  NullPointerException if <code>libname</code> is
     *             <code>null</code>
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     */
    @CallerSensitive
    public void loadLibrary(String libname) {
        loadLibrary0(Reflection.getCallerClass(), libname);
    }

    synchronized void loadLibrary0(Class<?> fromClass, String libname) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkLink(libname);
        }
        if (libname.indexOf((int)File.separatorChar) != -1) {
            throw new UnsatisfiedLinkError(
    "Directory separator should not appear in library name: " + libname);
        }
        ClassLoader.loadLibrary(fromClass, libname, false);
    }

    /**
     * Creates a localized version of an input stream. This method takes
     * an <code>InputStream</code> and returns an <code>InputStream</code>
     * equivalent to the argument in all respects except that it is
     * localized: as characters in the local character set are read from
     * the stream, they are automatically converted from the local
     * character set to Unicode.
     * 1.创建输入流的本地化版本。此方法接受一个InputStream并返回一个 InputStream等价于所有方面的参数，
     * 除了它是本地化的：当本地字符集中的字符从流中读取时，它们会自动从本地字符集转换为 Unicode。
     * <p>
     * If the argument is already a localized stream, it may be returned
     * as the result.
     * 2.如果参数已经是一个本地化的流，它可能会作为结果返回。
     * @param      in InputStream to localize
     * @return     a localized input stream
     * @see        java.io.InputStream
     * @see        java.io.BufferedReader#BufferedReader(java.io.Reader)
     * @see        java.io.InputStreamReader#InputStreamReader(java.io.InputStream)
     * @deprecated As of JDK&nbsp;1.1, the preferred way to translate a byte
     * stream in the local encoding into a character stream in Unicode is via
     * the <code>InputStreamReader</code> and <code>BufferedReader</code>
     * classes.
     */
    @Deprecated
    public InputStream getLocalizedInputStream(InputStream in) {
        return in;
    }

    /**
     * Creates a localized version of an output stream. This method
     * takes an <code>OutputStream</code> and returns an
     * <code>OutputStream</code> equivalent to the argument in all respects
     * except that it is localized: as Unicode characters are written to
     * the stream, they are automatically converted to the local
     * character set.
     * <p>
     * If the argument is already a localized stream, it may be returned
     * as the result.
     *
     * @deprecated As of JDK&nbsp;1.1, the preferred way to translate a
     * Unicode character stream into a byte stream in the local encoding is via
     * the <code>OutputStreamWriter</code>, <code>BufferedWriter</code>, and
     * <code>PrintWriter</code> classes.
     *
     * @param      out OutputStream to localize
     * @return     a localized output stream
     * @see        java.io.OutputStream
     * @see        java.io.BufferedWriter#BufferedWriter(java.io.Writer)
     * @see        java.io.OutputStreamWriter#OutputStreamWriter(java.io.OutputStream)
     * @see        java.io.PrintWriter#PrintWriter(java.io.OutputStream)
     */
    @Deprecated
    public OutputStream getLocalizedOutputStream(OutputStream out) {
        return out;
    }

}
