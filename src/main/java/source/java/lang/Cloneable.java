/*
 * Copyright (c) 1995, 2004, Oracle and/or its affiliates. All rights reserved.
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
 * A class implements the <code>Cloneable</code> interface to
 * indicate to the {@link java.lang.Object#clone()} method that it
 * is legal for that method to make a
 * field-for-field copy of instances of that class.
 * 1.一个类实现了Cloneable接口，以向 java.lang.Object.clone()方法指示
 * 该方法制作该类实例的字段对字段副本是合法的
 * <p>
 * Invoking Object's clone method on an instance that does not implement the
 * <code>Cloneable</code> interface results in the exception
 * <code>CloneNotSupportedException</code> being thrown.
 * 2.在未实现 Cloneable接口的实例上调用 Object 的 clone 方法
 * 会导致抛出异常 CloneNotSupportedException
 * <p>
 * By convention, classes that implement this interface should override
 * <tt>Object.clone</tt> (which is protected) with a public method.
 * See {@link java.lang.Object#clone()} for details on overriding this
 * method.
 * 3.按照惯例，实现此接口的类应该使用公共方法覆盖Object.clone受保护的）。
 * 有关覆盖此方法的详细信息，请参阅 java.lang.Object.clone()
 * <p>
 * Note that this interface does <i>not</i> contain the <tt>clone</tt> method.
 * Therefore, it is not possible to clone an object merely by virtue of the
 * fact that it implements this interface.  Even if the clone method is invoked
 * reflectively, there is no guarantee that it will succeed.
 * 4.请注意，此接口不包含clone方法。因此，不能仅仅凭借对象实现了这个接口就克隆一个对象。
 * 即使以反射方式调用 clone 方法，也不能保证它会成功
 * @author  unascribed
 * @see     java.lang.CloneNotSupportedException
 * @see     java.lang.Object#clone()
 * @since   JDK1.0
 */
public interface Cloneable {
}
