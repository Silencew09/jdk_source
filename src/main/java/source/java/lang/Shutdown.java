/*
 * Copyright (c) 1999, 2005, Oracle and/or its affiliates. All rights reserved.
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
 * Package-private utility class containing data structures and logic
 * governing the virtual-machine shutdown sequence.
 * 1.包私有实用程序类包含控制虚拟机关闭顺序的数据结构和逻辑
 * @author   Mark Reinhold
 * @since    1.3
 */

class Shutdown {

    /* Shutdown state */
    //关机状态
    private static final int RUNNING = 0;
    private static final int HOOKS = 1;
    private static final int FINALIZERS = 2;
    private static int state = RUNNING;

    /* Should we run all finalizers upon exit? */
    //我们应该在退出时运行所有终结器吗？
    private static boolean runFinalizersOnExit = false;

    // The system shutdown hooks are registered with a predefined slot.
    // The list of shutdown hooks is as follows:
    // (0) Console restore hook
    // (1) Application hooks
    // (2) DeleteOnExit hook
    //系统关闭挂钩使用预定义的插槽注册。关闭钩子列表如下：
    // (0) 控制台恢复钩子
    // (1) 应用程序钩子
    // (2) DeleteOnExit 钩子
    private static final int MAX_SYSTEM_HOOKS = 10;
    private static final Runnable[] hooks = new Runnable[MAX_SYSTEM_HOOKS];

    // the index of the currently running shutdown hook to the hooks array
    //当前运行的关闭挂钩到 hooks 数组的索引
    private static int currentRunningHook = 0;

    /* The preceding static fields are protected by this lock */
    //前面的静态字段受此锁保护
    private static class Lock { };
    private static Object lock = new Lock();

    /* Lock object for the native halt method */
    //本机停止方法的锁定对象
    private static Object haltLock = new Lock();

    /* Invoked by Runtime.runFinalizersOnExit */
    //由 Runtime.runFinalizersOnExit 调用
    static void setRunFinalizersOnExit(boolean run) {
        synchronized (lock) {
            runFinalizersOnExit = run;
        }
    }


    /**
     * Add a new shutdown hook.  Checks the shutdown state and the hook itself,
     * but does not do any security checks.
     * 1.添加一个新的关闭钩子。检查关闭状态和钩子本身，但不进行任何安全检查。
     * The registerShutdownInProgress parameter should be false except
     * registering the DeleteOnExitHook since the first file may
     * be added to the delete on exit list by the application shutdown
     * hooks.
     * 2.除了注册 DeleteOnExitHook 之外， registerShutdownInProgress 参数应该为 false ，
     * 因为第一个文件可能会被应用程序关闭挂钩添加到退出列表中
     * @params slot  the slot in the shutdown hook array, whose element
     *               will be invoked in order during shutdown
     * @params registerShutdownInProgress true to allow the hook
     *               to be registered even if the shutdown is in progress.
     * @params hook  the hook to be registered
     *
     * @throw IllegalStateException
     *        if registerShutdownInProgress is false and shutdown is in progress; or
     *        if registerShutdownInProgress is true and the shutdown process
     *           already passes the given slot
     */
    static void add(int slot, boolean registerShutdownInProgress, Runnable hook) {
        synchronized (lock) {
            if (hooks[slot] != null)
                throw new InternalError("Shutdown hook at slot " + slot + " already registered");

            if (!registerShutdownInProgress) {
                if (state > RUNNING)
                    throw new IllegalStateException("Shutdown in progress");
            } else {
                if (state > HOOKS || (state == HOOKS && slot <= currentRunningHook))
                    throw new IllegalStateException("Shutdown in progress");
            }

            hooks[slot] = hook;
        }
    }

    /* Run all registered shutdown hooks
    //运行所有注册的关闭钩子
     */
    private static void runHooks() {
        for (int i=0; i < MAX_SYSTEM_HOOKS; i++) {
            try {
                Runnable hook;
                synchronized (lock) {
                    // acquire the lock to make sure the hook registered during
                    // shutdown is visible here.
                    //获取锁以确保在关闭期间注册的钩子在此处可见。
                    currentRunningHook = i;
                    hook = hooks[i];
                }
                if (hook != null) hook.run();
            } catch(Throwable t) {
                if (t instanceof ThreadDeath) {
                    ThreadDeath td = (ThreadDeath)t;
                    throw td;
                }
            }
        }
    }

    /* The halt method is synchronized on the halt lock
     * to avoid corruption of the delete-on-shutdown file list.
     * It invokes the true native halt method.
     * halt 方法在halt 锁上同步以避免delete-on-shutdown 文件列表损坏。它调用真正的本机停止方法。
     */
    static void halt(int status) {
        synchronized (haltLock) {
            halt0(status);
        }
    }

    static native void halt0(int status);

    /* Wormhole for invoking java.lang.ref.Finalizer.runAllFinalizers */
    //用于调用 java.lang.ref.Finalizer.runAllFinalizers 的虫洞
    private static native void runAllFinalizers();


    /* The actual shutdown sequence is defined here.
     * 1.实际关闭顺序在此处定义
     * If it weren't for runFinalizersOnExit, this would be simple -- we'd just
     * run the hooks and then halt.  Instead we need to keep track of whether
     * we're running hooks or finalizers.  In the latter case a finalizer could
     * invoke exit(1) to cause immediate termination, while in the former case
     * any further invocations of exit(n), for any n, simply stall.  Note that
     * if on-exit finalizers are enabled they're run iff the shutdown is
     * initiated by an exit(0); they're never run on exit(n) for n != 0 or in
     * response to SIGINT, SIGTERM, etc.
     * 2.如果不是 runFinalizersOnExit，这将很简单——我们只需运行钩子然后停止。
     * 相反，我们需要跟踪我们是在运行钩子还是终结器。在后一种情况下，终结器可以调用 exit(1) 以导致立即终止，
     * 而在前一种情况下，对于任何 n，任何进一步调用 exit(n) 都只是停止。请注意，如果启用了出口终结器，
     * 它们将在关闭是由 exit(0) 启动的情况下运行；它们永远不会在 exit(n) 上运行 n != 0 或响应 SIGINT、SIGTERM 等。
     */
    private static void sequence() {
        synchronized (lock) {
            /* Guard against the possibility of a daemon thread invoking exit
             * after DestroyJavaVM initiates the shutdown sequence
             * 防止在 DestroyJavaVM 启动关闭序列后守护线程调用退出的可能性
             */
            if (state != HOOKS) return;
        }
        runHooks();
        boolean rfoe;
        synchronized (lock) {
            state = FINALIZERS;
            rfoe = runFinalizersOnExit;
        }
        if (rfoe) runAllFinalizers();
    }


    /* Invoked by Runtime.exit, which does all the security checks.
     * Also invoked by handlers for system-provided termination events,
     * which should pass a nonzero status code.
     * 由执行所有安全检查的 Runtime.exit 调用。也由系统提供的终止事件的处理程序调用，
     * 该事件应传递非零状态代码。
     */
    static void exit(int status) {
        boolean runMoreFinalizers = false;
        synchronized (lock) {
            if (status != 0) runFinalizersOnExit = false;
            switch (state) {
            case RUNNING:       /* Initiate shutdown */
                state = HOOKS;
                break;
            case HOOKS:         /* Stall and halt */
                break;
            case FINALIZERS:
                if (status != 0) {
                    /* Halt immediately on nonzero status */
                    //在非零状态下立即停止
                    halt(status);
                } else {
                    /* Compatibility with old behavior:
                     * Run more finalizers and then halt
                     * //与旧行为的兼容性：运行更多终结器然后停止
                     */
                    runMoreFinalizers = runFinalizersOnExit;
                }
                break;
            }
        }
        if (runMoreFinalizers) {
            runAllFinalizers();
            halt(status);
        }
        synchronized (Shutdown.class) {
            /* Synchronize on the class object, causing any other thread
             * that attempts to initiate shutdown to stall indefinitely
             * //在类对象上同步，导致任何其他尝试启动关闭的线程无限期停顿
             */
            sequence();
            halt(status);
        }
    }


    /* Invoked by the JNI DestroyJavaVM procedure when the last non-daemon
     * thread has finished.  Unlike the exit method, this method does not
     * actually halt the VM.
     * /当最后一个非守护线程完成时由 JNI DestroyJavaVM 过程调用。与 exit 方法不同，此方法实际上并不停止 VM。
     */
    static void shutdown() {
        synchronized (lock) {
            switch (state) {
            case RUNNING:       /* Initiate shutdown */
                state = HOOKS;
                break;
            case HOOKS:         /* Stall and then return */
            case FINALIZERS:
                break;
            }
        }
        synchronized (Shutdown.class) {
            sequence();
        }
    }

}
