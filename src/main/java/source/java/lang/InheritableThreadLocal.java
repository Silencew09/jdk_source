/*
 * Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved.
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
import java.lang.ref.*;

/**
 * This class extends <tt>ThreadLocal</tt> to provide inheritance of values
 * from parent thread to child thread: when a child thread is created, the
 * child receives initial values for all inheritable thread-local variables
 * for which the parent has values.  Normally the child's values will be
 * identical to the parent's; however, the child's value can be made an
 * arbitrary function of the parent's by overriding the <tt>childValue</tt>
 * method in this class.
 * 1.此类扩展ThreadLocal以提供从父线程到子线程的值的继承
 * 2.创建子线程时，子线程会接收父线程具有值的所有可继承线程局部变量的初始值
 * 3.通常孩子的价值观将与父母的相同；但是，通过覆盖此类中的childValue方法，可以将子值设为父值的任意函数。
 * <p>Inheritable thread-local variables are used in preference to
 * ordinary thread-local variables when the per-thread-attribute being
 * maintained in the variable (e.g., User ID, Transaction ID) must be
 * automatically transmitted to any child threads that are created.
 * 4.当在变量中维护的每线程属性（例如，用户 ID、事务 ID）必须自动传输到任何创建的子线程时，可优先使用可继承的线程局部变量而不是普通的线程局部变量。
 * @author  Josh Bloch and Doug Lea
 * @see     ThreadLocal
 * @since   1.2
 */

public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    /**
     * Computes the child's initial value for this inheritable thread-local
     * variable as a function of the parent's value at the time the child
     * thread is created.  This method is called from within the parent
     * thread before the child is started.
     * 1.计算此可继承线程局部变量的子级初始值，作为创建子线程时父级值的函数
     * 在启动子线程之前，从父线程中调用此方法
     * <p>
     * This method merely returns its input argument, and should be overridden
     * if a different behavior is desired.
     * 2.此方法仅返回其输入参数，如果需要不同的行为，则应覆盖该方法。
     * @param parentValue the parent thread's value
     * @return the child thread's initial value
     */
    protected T childValue(T parentValue) {
        return parentValue;
    }

    /**
     * Get the map associated with a ThreadLocal.
     * 获取与 ThreadLocal 关联的映射
     * @param t the current thread
     */
    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }

    /**
     * Create the map associated with a ThreadLocal.
     * 创建与 ThreadLocal 关联的映射
     * @param t the current thread
     * @param firstValue value for the initial entry of the table.
     */
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}
