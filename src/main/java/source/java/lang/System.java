/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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
import java.lang.reflect.Executable;
import java.lang.annotation.Annotation;
import java.security.AccessControlContext;
import java.util.Properties;
import java.util.PropertyPermission;
import java.util.StringTokenizer;
import java.util.Map;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.AllPermission;
import java.nio.channels.Channel;
import java.nio.channels.spi.SelectorProvider;
import sun.nio.ch.Interruptible;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.SecurityConstants;
import sun.reflect.annotation.AnnotationType;

/**
 * The <code>System</code> class contains several useful class fields
 * and methods. It cannot be instantiated.
 * 1.System 类包含几个有用的类字段和方法。它不能被实例化。
 * <p>Among the facilities provided by the <code>System</code> class
 * are standard input, standard output, and error output streams;
 * access to externally defined properties and environment
 * variables; a means of loading files and libraries; and a utility
 * method for quickly copying a portion of an array.
 * 2.System类提供的工具包括标准输入、标准输出和错误输出流；访问外部定义的属性和环境变量；
 * 一种加载文件和库的方法；以及一种用于快速复制数组一部分的实用方法
 * @author  unascribed
 * @since   JDK1.0
 */
public final class System {

    /* register the natives via the static initializer.
     *
     * VM will invoke the initializeSystemClass method to complete
     * the initialization for this class separated from clinit.
     * Note that to use properties set by the VM, see the constraints
     * described in the initializeSystemClass method.
     */
    private static native void registerNatives();
    static {
        registerNatives();
    }

    /** Don't let anyone instantiate this class */
    //不要让任何人实例化这个类
    private System() {
    }

    /**
     * The "standard" input stream. This stream is already
     * open and ready to supply input data. Typically this stream
     * corresponds to keyboard input or another input source specified by
     * the host environment or user.
     * “标准”输入流。此流已打开并准备好提供输入数据。通常，此流对应于键盘输入或主机环境或用户指定的另一个输入源。
     */
    public final static InputStream in = null;

    /**
     * The "standard" output stream. This stream is already
     * open and ready to accept output data. Typically this stream
     * corresponds to display output or another output destination
     * specified by the host environment or user.
     * 1.“标准”输出流。此流已打开并准备好接受输出数据。通常，此流对应于主机环境或用户指定的显示输出或另一个输出目的地
     * <p>
     * For simple stand-alone Java applications, a typical way to write
     * a line of output data is:
     * <blockquote><pre>
     *     System.out.println(data)
     * </pre></blockquote>
     * <p>
     * See the <code>println</code> methods in class <code>PrintStream</code>.
     * 2.对于简单的独立 Java 应用程序，写入一行输出数据的典型方式是：
     * System.out.println(data) 参见println类 PrintStream中的方法。
     * @see     java.io.PrintStream#println()
     * @see     java.io.PrintStream#println(boolean)
     * @see     java.io.PrintStream#println(char)
     * @see     java.io.PrintStream#println(char[])
     * @see     java.io.PrintStream#println(double)
     * @see     java.io.PrintStream#println(float)
     * @see     java.io.PrintStream#println(int)
     * @see     java.io.PrintStream#println(long)
     * @see     java.io.PrintStream#println(java.lang.Object)
     * @see     java.io.PrintStream#println(java.lang.String)
     */
    public final static PrintStream out = null;

    /**
     * The "standard" error output stream. This stream is already
     * open and ready to accept output data.
     * 1.“标准”错误输出流。此流已打开并准备好接受输出数据
     * <p>
     * Typically this stream corresponds to display output or another
     * output destination specified by the host environment or user. By
     * convention, this output stream is used to display error messages
     * or other information that should come to the immediate attention
     * of a user even if the principal output stream, the value of the
     * variable <code>out</code>, has been redirected to a file or other
     * destination that is typically not continuously monitored.
     * 2.通常，此流对应于主机环境或用户指定的显示输出或另一个输出目的地。
     * 按照惯例，此输出流用于显示错误消息或其他应引起用户立即注意的信息，
     * 即使主要输出流（变量 out的值已重定向到文件或其他通常不会被持续监控的目标。
     */
    public final static PrintStream err = null;

    /* The security manager for the system.
    系统的安全管理器
     */
    private static volatile SecurityManager security = null;

    /**
     * Reassigns the "standard" input stream.
     * 1.重新分配“标准”输入流。
     * <p>First, if there is a security manager, its <code>checkPermission</code>
     * method is called with a <code>RuntimePermission("setIO")</code> permission
     *  to see if it's ok to reassign the "standard" input stream.
     * <p>
     * 2.首先，如果有一个安全管理器，它的checkPermission方法被调用，
     * 并带有RuntimePermission("setIO")权限，看看是否可以重新分配“标准”输入流
     * @param in the new standard input stream.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <code>checkPermission</code> method doesn't allow
     *        reassigning of the standard input stream.
     *
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     *
     * @since   JDK1.1
     */
    public static void setIn(InputStream in) {
        checkIO();
        setIn0(in);
    }

    /**
     * Reassigns the "standard" output stream.
     * 1.重新分配“标准”输出流。
     * <p>First, if there is a security manager, its <code>checkPermission</code>
     * method is called with a <code>RuntimePermission("setIO")</code> permission
     *  to see if it's ok to reassign the "standard" output stream.
     * 2.>首先，如果有一个安全管理器，它的checkPermission方法被调用，
     * 并带有RuntimePermission("setIO")权限，看看是否可以重新分配“标准”输出流。
     * @param out the new standard output stream
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <code>checkPermission</code> method doesn't allow
     *        reassigning of the standard output stream.
     *
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     *
     * @since   JDK1.1
     */
    public static void setOut(PrintStream out) {
        checkIO();
        setOut0(out);
    }

    /**
     * Reassigns the "standard" error output stream.
     * 1.重新分配“标准”错误输出流。
     * <p>First, if there is a security manager, its <code>checkPermission</code>
     * method is called with a <code>RuntimePermission("setIO")</code> permission
     *  to see if it's ok to reassign the "standard" error output stream.
     *2.>首先，如果有安全管理器，它的checkPermission方法被调用，并带有RuntimePermission("setIO")权限，
     * 看是否可以重新分配“标准”错误输出流.
     * @param err the new standard error output stream.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <code>checkPermission</code> method doesn't allow
     *        reassigning of the standard error output stream.
     *
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     *
     * @since   JDK1.1
     */
    public static void setErr(PrintStream err) {
        checkIO();
        setErr0(err);
    }

    private static volatile Console cons = null;
    /**
     * Returns the unique {@link java.io.Console Console} object associated
     * with the current Java virtual machine, if any.
     * 1.返回与当前 Java 虚拟机关联的唯一 java.io.Console 对象（如果有）
     * @return  The system console, if any, otherwise <tt>null</tt>.
     *
     * @since   1.6
     */
     public static Console console() {
         if (cons == null) {
             synchronized (System.class) {
                 cons = sun.misc.SharedSecrets.getJavaIOAccess().console();
             }
         }
         return cons;
     }

    /**
     * Returns the channel inherited from the entity that created this
     * Java virtual machine.
     * 1.返回从创建此 Java 虚拟机的实体继承的通道
     * <p> This method returns the channel obtained by invoking the
     * {@link java.nio.channels.spi.SelectorProvider#inheritedChannel
     * inheritedChannel} method of the system-wide default
     * {@link java.nio.channels.spi.SelectorProvider} object. </p>
     * 2.该方法返回通过调用系统范围默认java.nio.channels.spi.SelectorProvider对象的
     * java.nio.channels.spi.SelectorProvider.inheritedChannel}方法获得的通道。
     * <p> In addition to the network-oriented channels described in
     * {@link java.nio.channels.spi.SelectorProvider#inheritedChannel
     * inheritedChannel}, this method may return other kinds of
     * channels in the future.
     * 3.除了 java.nio.channels.spi.SelectorProvider.inheritedChannel中描述的面向网络的通道外，
     * 该方法未来可能会返回其他类型的通道
     * @return  The inherited channel, if any, otherwise <tt>null</tt>.
     *
     * @throws  IOException
     *          If an I/O error occurs
     *
     * @throws  SecurityException
     *          If a security manager is present and it does not
     *          permit access to the channel.
     *
     * @since 1.5
     */
    public static Channel inheritedChannel() throws IOException {
        return SelectorProvider.provider().inheritedChannel();
    }

    private static void checkIO() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("setIO"));
        }
    }

    private static native void setIn0(InputStream in);
    private static native void setOut0(PrintStream out);
    private static native void setErr0(PrintStream err);

    /**
     * Sets the System security.
     * 1.设置系统安全
     * <p> If there is a security manager already installed, this method first
     * calls the security manager's <code>checkPermission</code> method
     * with a <code>RuntimePermission("setSecurityManager")</code>
     * permission to ensure it's ok to replace the existing
     * security manager.
     * This may result in throwing a <code>SecurityException</code>.
     * 2.如果已经安装了安全管理器，此方法首先使用RuntimePermission("setSecurityManager")
     * 权限调用安全管理器的checkPermission方法，以确保可以替换现有的安全管理器。
     * 这可能会导致抛出SecurityException
     * <p> Otherwise, the argument is established as the current
     * security manager. If the argument is <code>null</code> and no
     * security manager has been established, then no action is taken and
     * the method simply returns.
     * 3.否则，该参数被建立为当前的安全管理器。如果参数是null并且没有建立安全管理器，
     * 那么不采取任何行动并且方法简单地返回。
     * @param      s   the security manager.
     * @exception  SecurityException  if the security manager has already
     *             been set and its <code>checkPermission</code> method
     *             doesn't allow it to be replaced.
     * @see #getSecurityManager
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    public static
    void setSecurityManager(final SecurityManager s) {
        try {
            s.checkPackageAccess("java.lang");
        } catch (Exception e) {
            // no-op
        }
        setSecurityManager0(s);
    }

    private static synchronized
    void setSecurityManager0(final SecurityManager s) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            // ask the currently installed security manager if we
            // can replace it.
            //询问当前安装的安全管理器是否可以更换它
            sm.checkPermission(new RuntimePermission
                                     ("setSecurityManager"));
        }

        if ((s != null) && (s.getClass().getClassLoader() != null)) {
            // New security manager class is not on bootstrap classpath.
            // Cause policy to get initialized before we install the new
            // security manager, in order to prevent infinite loops when
            // trying to initialize the policy (which usually involves
            // accessing some security and/or system properties, which in turn
            // calls the installed security manager's checkPermission method
            // which will loop infinitely if there is a non-system class
            // (in this case: the new security manager class) on the stack).
            //新的安全管理器类不在引导类路径上。在我们安装新的安全管理器之前使策略被初始化，
            // 以防止尝试初始化策略时的无限循环（这通常涉及访问一些安全和/或系统属性，
            // 然后调用已安装的安全管理器的 checkPermission 方法，
            // 该方法将无限循环如果堆栈中存在非系统类（在本例中：新的安全管理器类）
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    s.getClass().getProtectionDomain().implies
                        (SecurityConstants.ALL_PERMISSION);
                    return null;
                }
            });
        }

        security = s;
    }

    /**
     * Gets the system security interface.
     * 获取系统安全接口。
     * @return  if a security manager has already been established for the
     *          current application, then that security manager is returned;
     *          otherwise, <code>null</code> is returned.
     * @see     #setSecurityManager
     */
    public static SecurityManager getSecurityManager() {
        return security;
    }

    /**
     * Returns the current time in milliseconds.  Note that
     * while the unit of time of the return value is a millisecond,
     * the granularity of the value depends on the underlying
     * operating system and may be larger.  For example, many
     * operating systems measure time in units of tens of
     * milliseconds.
     * 1.以毫秒为单位返回当前时间。请注意，虽然返回值的时间单位是毫秒，
     * 但值的粒度取决于底层操作系统，可能会更大。例如，许多操作系统以几十毫秒为单位测量时间。
     * <p> See the description of the class <code>Date</code> for
     * a discussion of slight discrepancies that may arise between
     * "computer time" and coordinated universal time (UTC).
     * 2.有关“计算机时间”和协调世界时 (UTC) 之间可能出现的细微差异的讨论，请参阅类Date的说明。
     * @return  the difference, measured in milliseconds, between
     *          the current time and midnight, January 1, 1970 UTC.
     * @see     java.util.Date
     */
    public static native long currentTimeMillis();

    /**
     * Returns the current value of the running Java Virtual Machine's
     * high-resolution time source, in nanoseconds.
     * 1.返回正在运行的 Java 虚拟机的高分辨率时间源的当前值，以纳秒为单位。
     * <p>This method can only be used to measure elapsed time and is
     * not related to any other notion of system or wall-clock time.
     * The value returned represents nanoseconds since some fixed but
     * arbitrary <i>origin</i> time (perhaps in the future, so values
     * may be negative).  The same origin is used by all invocations of
     * this method in an instance of a Java virtual machine; other
     * virtual machine instances are likely to use a different origin.
     * 2.此方法只能用于测量经过的时间，与系统或挂钟时间的任何其他概念无关。
     * 返回的值表示自某些固定但任意的origin时间以来的纳秒（可能在未来，因此值可能为负）。
     * 在 Java 虚拟机的实例中，此方法的所有调用都使用相同的来源；其他虚拟机实例可能使用不同的来源。
     * <p>This method provides nanosecond precision, but not necessarily
     * nanosecond resolution (that is, how frequently the value changes)
     * - no guarantees are made except that the resolution is at least as
     * good as that of {@link #currentTimeMillis()}.
     * 3.此方法提供纳秒精度，但不一定提供纳秒分辨率（即值更改的频率）-
     * 除了分辨率至少与  currentTimeMillis()一样好外，不做任何保证。
     * <p>Differences in successive calls that span greater than
     * approximately 292 years (2<sup>63</sup> nanoseconds) will not
     * correctly compute elapsed time due to numerical overflow.
     * 4.由于数值溢出，跨越大约 292 年（2<sup>63<sup> 纳秒）的连续调用的差异将无法正确计算经过的时间。
     * <p>The values returned by this method become meaningful only when
     * the difference between two such values, obtained within the same
     * instance of a Java virtual machine, is computed.
     * 5.仅当计算在 Java 虚拟机的同一实例中获得的两个此类值之间的差异时，此方法返回的值才有意
     * <p> For example, to measure how long some code takes to execute:
     *  <pre> {@code
     * long startTime = System.nanoTime();
     * // ... the code being measured ...
     * long estimatedTime = System.nanoTime() - startTime;}</pre>
     *
     * <p>To compare two nanoTime values
     *  <pre> {@code
     * long t0 = System.nanoTime();
     * ...
     * long t1 = System.nanoTime();}</pre>
     *
     * one should use {@code t1 - t0 < 0}, not {@code t1 < t0},
     * because of the possibility of numerical overflow.
     *
     * @return the current value of the running Java Virtual Machine's
     *         high-resolution time source, in nanoseconds
     * @since 1.5
     */
    public static native long nanoTime();

    /**
     * Copies an array from the specified source array, beginning at the
     * specified position, to the specified position of the destination array.
     * A subsequence of array components are copied from the source
     * array referenced by <code>src</code> to the destination array
     * referenced by <code>dest</code>. The number of components copied is
     * equal to the <code>length</code> argument. The components at
     * positions <code>srcPos</code> through
     * <code>srcPos+length-1</code> in the source array are copied into
     * positions <code>destPos</code> through
     * <code>destPos+length-1</code>, respectively, of the destination
     * array.
     * 1.从指定的源数组中复制一个数组，从指定位置开始，到目标数组的指定位置。
     * 数组组件的子序列从src引用的源数组复制到dest引用的目标数组。
     * 复制的组件数等于length 参数。将源数组中位置srcPos 到srcPos+length-1的组件复制到位置destPos
     * 到destPos+length-1，分别是目标数组的。
     * <p>
     * If the <code>src</code> and <code>dest</code> arguments refer to the
     * same array object, then the copying is performed as if the
     * components at positions <code>srcPos</code> through
     * <code>srcPos+length-1</code> were first copied to a temporary
     * array with <code>length</code> components and then the contents of
     * the temporary array were copied into positions
     * <code>destPos</code> through <code>destPos+length-1</code> of the
     * destination array.
     * 2.如果src和dest参数引用同一个数组对象，则复制执行就好像位置srcPos到srcPos+length-1 的组件一样
     * 首先被复制到一个带有 length分量的临时数组，然后临时数组的内容被复制到destPos到 destPos+length-1目标数组。
     * <p>
     * If <code>dest</code> is <code>null</code>, then a
     * <code>NullPointerException</code> is thrown.
     * 3如果 dest 是null，则抛出NullPointerException
     * <p>
     * If <code>src</code> is <code>null</code>, then a
     * <code>NullPointerException</code> is thrown and the destination
     * array is not modified.
     * 4.如果 src是null，则抛出NullPointerException并且不修改目标数组。
     * <p>
     * Otherwise, if any of the following is true, an
     * <code>ArrayStoreException</code> is thrown and the destination is
     * not modified:
     * 5.否则，如果以下任一情况为真，则抛出ArrayStoreException并且不修改目标:
     *  1)src参数引用一个不是数组的对象。
     *  2)dest参数引用一个不是数组的对象。
     *  3)src参数和dest参数是指组件类型为不同原始类型的数组。
     *  4)src参数是指具有原始组件类型的数组，而dest参数是指具有参考组件类型的数组。
     *  5)src参数是指具有参考组件类型的数组，而dest参数是指具有原始组件类型的数组。
     * <ul>
     * <li>The <code>src</code> argument refers to an object that is not an
     *     array.
     * <li>The <code>dest</code> argument refers to an object that is not an
     *     array.
     * <li>The <code>src</code> argument and <code>dest</code> argument refer
     *     to arrays whose component types are different primitive types.
     * <li>The <code>src</code> argument refers to an array with a primitive
     *    component type and the <code>dest</code> argument refers to an array
     *     with a reference component type.
     * <li>The <code>src</code> argument refers to an array with a reference
     *    component type and the <code>dest</code> argument refers to an array
     *     with a primitive component type.
     * </ul>
     * <p>
     * Otherwise, if any of the following is true, an
     * <code>IndexOutOfBoundsException</code> is
     * thrown and the destination is not modified:
     * 6.否则，如果以下任一情况为真，则抛出IndexOutOfBoundsException并且不修改目标：
     *  1)srcPos参数是否定的。
     *  2)destPos参数是否定的。
     *  3)length参数为负数。
     *  4)srcPos+length大于 src.length，即源数组的长度。
     *  5)destPos+length大于目标数组的长度 dest.length。
     * <ul>
     * <li>The <code>srcPos</code> argument is negative.
     * <li>The <code>destPos</code> argument is negative.
     * <li>The <code>length</code> argument is negative.
     * <li><code>srcPos+length</code> is greater than
     *     <code>src.length</code>, the length of the source array.
     * <li><code>destPos+length</code> is greater than
     *     <code>dest.length</code>, the length of the destination array.
     * </ul>
     * <p>
     * Otherwise, if any actual component of the source array from
     * position <code>srcPos</code> through
     * <code>srcPos+length-1</code> cannot be converted to the component
     * type of the destination array by assignment conversion, an
     * <code>ArrayStoreException</code> is thrown. In this case, let
     * <b><i>k</i></b> be the smallest nonnegative integer less than
     * length such that <code>src[srcPos+</code><i>k</i><code>]</code>
     * cannot be converted to the component type of the destination
     * array; when the exception is thrown, source array components from
     * positions <code>srcPos</code> through
     * <code>srcPos+</code><i>k</i><code>-1</code>
     * will already have been copied to destination array positions
     * <code>destPos</code> through
     * <code>destPos+</code><i>k</I><code>-1</code> and no other
     * positions of the destination array will have been modified.
     * (Because of the restrictions already itemized, this
     * paragraph effectively applies only to the situation where both
     * arrays have component types that are reference types.)
     * 7.否则，如果源数组从位置srcPos到srcPos+length-1的任何实际组件不能通过赋值转换转换为目标数组的组件类型，
     * 则ArrayStoreException被抛出。在这种情况下，让k是小于长度的最小非负整数，使得src[srcPos+k]无法转换
     * 为目标数组的组件类型；当抛出异常时，从位置srcPos到 srcPos+k-1的源数组组件将已经被复制到
     * 目标数组位置destPos到 destPos+k-1 并且目标数组的其他位置不会被修改。
     * （由于已经逐项列出了限制，本段仅适用于两个数组都具有引用类型的组件类型的情况。）
     * @param      src      the source array.
     * @param      srcPos   starting position in the source array.
     * @param      dest     the destination array.
     * @param      destPos  starting position in the destination data.
     * @param      length   the number of array elements to be copied.
     * @exception  IndexOutOfBoundsException  if copying would cause
     *               access of data outside array bounds.
     * @exception  ArrayStoreException  if an element in the <code>src</code>
     *               array could not be stored into the <code>dest</code> array
     *               because of a type mismatch.
     * @exception  NullPointerException if either <code>src</code> or
     *               <code>dest</code> is <code>null</code>.
     */
    public static native void arraycopy(Object src,  int  srcPos,
                                        Object dest, int destPos,
                                        int length);

    /**
     * Returns the same hash code for the given object as
     * would be returned by the default method hashCode(),
     * whether or not the given object's class overrides
     * hashCode().
     * The hash code for the null reference is zero.
     * 1.为给定对象返回与默认方法 hashCode() 返回的相同的哈希码，无论给定对象的类是否覆盖 hashCode()。
     * 2.空引用的哈希码为零。
     * @param x object for which the hashCode is to be calculated
     * @return  the hashCode
     * @since   JDK1.1
     */
    public static native int identityHashCode(Object x);

    /**
     * System properties. The following properties are guaranteed to be defined:
     * 系统属性。
     * <dl>
     * <dt>java.version         <dd>Java version number Java版本号
     * <dt>java.vendor          <dd>Java vendor specific string Java 供应商特定字符串
     * <dt>java.vendor.url      <dd>Java vendor URL Java 供应商 URL
     * <dt>java.home            <dd>Java installation directory Java安装目录
     * <dt>java.class.version   <dd>Java class version number Java 类版本号
     * <dt>java.class.path      <dd>Java classpath Java 类路径
     * <dt>os.name              <dd>Operating System Name 操作系统名称
     * <dt>os.arch              <dd>Operating System Architecture 操作系统架构
     * <dt>os.version           <dd>Operating System Version 操作系统版本
     * <dt>file.separator       <dd>File separator ("/" on Unix)
     * <dt>path.separator       <dd>Path separator (":" on Unix)
     * <dt>line.separator       <dd>Line separator ("\n" on Unix)
     * <dt>user.name            <dd>User account name 用户帐号名称
     * <dt>user.home            <dd>User home directory 用户主目录
     * <dt>user.dir             <dd>User's current working directory 用户当前工作目
     * </dl>
     */

    private static Properties props;
    private static native Properties initProperties(Properties props);

    /**
     * Determines the current system properties.
     * 1.确定当前系统属性
     * <p>
     * First, if there is a security manager, its
     * <code>checkPropertiesAccess</code> method is called with no
     * arguments. This may result in a security exception.
     * 2.首先，如果有一个安全管理器，它的 checkPropertiesAccess方法被调用而没有参数。这可能会导致安全异常。
     * <p>
     * 3.getProperty(String)方法使用的当前系统属性集作为Properties对象返回。如果当前没有一组系统属性，
     * 则首先创建并初始化一组系统属性。
     * The current set of system properties for use by the
     * {@link #getProperty(String)} method is returned as a
     * <code>Properties</code> object. If there is no current set of
     * system properties, a set of system properties is first created and
     * initialized. This set of system properties always includes values
     * for the following keys:
     * <table summary="Shows property keys and associated values">
     * <tr><th>Key</th>
     *     <th>Description of Associated Value</th></tr>
     * <tr><td><code>java.version</code></td>
     *     <td>Java Runtime Environment version</td></tr>
     * <tr><td><code>java.vendor</code></td>
     *     <td>Java Runtime Environment vendor</td></tr>
     * <tr><td><code>java.vendor.url</code></td>
     *     <td>Java vendor URL</td></tr>
     * <tr><td><code>java.home</code></td>
     *     <td>Java installation directory</td></tr>
     * <tr><td><code>java.vm.specification.version</code></td>
     *     <td>Java Virtual Machine specification version</td></tr>
     * <tr><td><code>java.vm.specification.vendor</code></td>
     *     <td>Java Virtual Machine specification vendor</td></tr>
     * <tr><td><code>java.vm.specification.name</code></td>
     *     <td>Java Virtual Machine specification name</td></tr>
     * <tr><td><code>java.vm.version</code></td>
     *     <td>Java Virtual Machine implementation version</td></tr>
     * <tr><td><code>java.vm.vendor</code></td>
     *     <td>Java Virtual Machine implementation vendor</td></tr>
     * <tr><td><code>java.vm.name</code></td>
     *     <td>Java Virtual Machine implementation name</td></tr>
     * <tr><td><code>java.specification.version</code></td>
     *     <td>Java Runtime Environment specification  version</td></tr>
     * <tr><td><code>java.specification.vendor</code></td>
     *     <td>Java Runtime Environment specification  vendor</td></tr>
     * <tr><td><code>java.specification.name</code></td>
     *     <td>Java Runtime Environment specification  name</td></tr>
     * <tr><td><code>java.class.version</code></td>
     *     <td>Java class format version number</td></tr>
     * <tr><td><code>java.class.path</code></td>
     *     <td>Java class path</td></tr>
     * <tr><td><code>java.library.path</code></td>
     *     <td>List of paths to search when loading libraries</td></tr>
     * <tr><td><code>java.io.tmpdir</code></td>
     *     <td>Default temp file path</td></tr>
     * <tr><td><code>java.compiler</code></td>
     *     <td>Name of JIT compiler to use</td></tr>
     * <tr><td><code>java.ext.dirs</code></td>
     *     <td>Path of extension directory or directories
     *         <b>Deprecated.</b> <i>This property, and the mechanism
     *            which implements it, may be removed in a future
     *            release.</i> </td></tr>
     * <tr><td><code>os.name</code></td>
     *     <td>Operating system name</td></tr>
     * <tr><td><code>os.arch</code></td>
     *     <td>Operating system architecture</td></tr>
     * <tr><td><code>os.version</code></td>
     *     <td>Operating system version</td></tr>
     * <tr><td><code>file.separator</code></td>
     *     <td>File separator ("/" on UNIX)</td></tr>
     * <tr><td><code>path.separator</code></td>
     *     <td>Path separator (":" on UNIX)</td></tr>
     * <tr><td><code>line.separator</code></td>
     *     <td>Line separator ("\n" on UNIX)</td></tr>
     * <tr><td><code>user.name</code></td>
     *     <td>User's account name</td></tr>
     * <tr><td><code>user.home</code></td>
     *     <td>User's home directory</td></tr>
     * <tr><td><code>user.dir</code></td>
     *     <td>User's current working directory</td></tr>
     * </table>
     * <p>
     * Multiple paths in a system property value are separated by the path
     * separator character of the platform.
     * 4.系统属性值中的多个路径由平台的路径分隔符分隔
     * <p>
     * Note that even if the security manager does not permit the
     * <code>getProperties</code> operation, it may choose to permit the
     * {@link #getProperty(String)} operation.
     * 5.请注意，即使安全管理器不允许getProperties操作，它也可以选择允许getProperty(String)操作。
     * @return     the system properties
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertiesAccess</code> method doesn't allow access
     *              to the system properties.
     * @see        #setProperties
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertiesAccess()
     * @see        java.util.Properties
     */
    public static Properties getProperties() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertiesAccess();
        }

        return props;
    }

    /**
     * Returns the system-dependent line separator string.  It always
     * returns the same value - the initial value of the {@linkplain
     * #getProperty(String) system property} {@code line.separator}.
     * 1.返回系统相关的行分隔符字符串。它总是返回相同的值 - getProperty(String) 系统属性line.separator的初始值
     * <p>On UNIX systems, it returns {@code "\n"}; on Microsoft
     * Windows systems it returns {@code "\r\n"}.
     * 2.在 UNIX 系统上，它返回"\n";在 Microsoft Windows 系统上，它返回"\r\n"
     * @return the system-dependent line separator string
     * @since 1.7
     */
    public static String lineSeparator() {
        return lineSeparator;
    }

    private static String lineSeparator;

    /**
     * Sets the system properties to the <code>Properties</code>
     * argument.
     * 1.将系统属性设置为Properties参数
     * <p>
     * First, if there is a security manager, its
     * <code>checkPropertiesAccess</code> method is called with no
     * arguments. This may result in a security exception.
     * 2.首先，如果有一个安全管理器，它的checkPropertiesAccess方法被调用而没有参数。这可能会导致安全异常
     * <p>
     * The argument becomes the current set of system properties for use
     * by the {@link #getProperty(String)} method. If the argument is
     * <code>null</code>, then the current set of system properties is
     * forgotten.
     * 3.该参数成为getProperty(String)方法使用的当前系统属性集。如果参数为null，则当前的系统属性集将被遗忘
     * @param      props   the new system properties.
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertiesAccess</code> method doesn't allow access
     *              to the system properties.
     * @see        #getProperties
     * @see        java.util.Properties
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertiesAccess()
     */
    public static void setProperties(Properties props) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertiesAccess();
        }
        if (props == null) {
            props = new Properties();
            initProperties(props);
        }
        System.props = props;
    }

    /**
     * Gets the system property indicated by the specified key.
     * 1.获取由指定键指示的系统属性
     * <p>
     * First, if there is a security manager, its
     * <code>checkPropertyAccess</code> method is called with the key as
     * its argument. This may result in a SecurityException.
     * 2.首先，如果有一个安全管理器，它的checkPropertyAccess方法将被调用，
     * 并将密钥作为它的参数。这可能会导致 SecurityException。
     * <p>
     * If there is no current set of system properties, a set of system
     * properties is first created and initialized in the same manner as
     * for the <code>getProperties</code> method.
     * 3.如果当前没有一组系统属性，则首先以与getProperties方法相同的方式创建和初始化一组系统属性。
     * @param      key   the name of the system property.
     * @return     the string value of the system property,
     *             or <code>null</code> if there is no property with that key.
     *
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertyAccess</code> method doesn't allow
     *              access to the specified system property.
     * @exception  NullPointerException if <code>key</code> is
     *             <code>null</code>.
     * @exception  IllegalArgumentException if <code>key</code> is empty.
     * @see        #setProperty
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see        java.lang.System#getProperties()
     */
    public static String getProperty(String key) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertyAccess(key);
        }

        return props.getProperty(key);
    }

    /**
     * Gets the system property indicated by the specified key.
     * 1.获取由指定键指示的系统属性
     * <p>
     * First, if there is a security manager, its
     * <code>checkPropertyAccess</code> method is called with the
     * <code>key</code> as its argument.
     * 2.首先，如果有一个安全管理器，它的checkPropertyAccess方法被调用，并使用key作为它的参数。
     * <p>
     * If there is no current set of system properties, a set of system
     * properties is first created and initialized in the same manner as
     * for the <code>getProperties</code> method.
     * 3.如果当前没有一组系统属性，则首先以与getProperties方法相同的方式创建和初始化一组系统属性。
     * @param      key   the name of the system property.
     * @param      def   a default value.
     * @return     the string value of the system property,
     *             or the default value if there is no property with that key.
     *
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertyAccess</code> method doesn't allow
     *             access to the specified system property.
     * @exception  NullPointerException if <code>key</code> is
     *             <code>null</code>.
     * @exception  IllegalArgumentException if <code>key</code> is empty.
     * @see        #setProperty
     * @see        java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
     * @see        java.lang.System#getProperties()
     */
    public static String getProperty(String key, String def) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPropertyAccess(key);
        }

        return props.getProperty(key, def);
    }

    /**
     * Sets the system property indicated by the specified key.
     * 1.设置由指定键指示的系统属性
     * <p>
     * First, if a security manager exists, its
     * <code>SecurityManager.checkPermission</code> method
     * is called with a <code>PropertyPermission(key, "write")</code>
     * permission. This may result in a SecurityException being thrown.
     * If no exception is thrown, the specified property is set to the given
     * value.
     * 2.首先，如果存在安全管理器，则使用PropertyPermission(key, "write")权限调用其
     * SecurityManager.checkPermission方法。这可能会导致抛出 SecurityException。
     * 如果没有抛出异常，则将指定的属性设置为给定的值
     * <p>
     *
     * @param      key   the name of the system property.
     * @param      value the value of the system property.
     * @return     the previous value of the system property,
     *             or <code>null</code> if it did not have one.
     *
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPermission</code> method doesn't allow
     *             setting of the specified property.
     * @exception  NullPointerException if <code>key</code> or
     *             <code>value</code> is <code>null</code>.
     * @exception  IllegalArgumentException if <code>key</code> is empty.
     * @see        #getProperty
     * @see        java.lang.System#getProperty(java.lang.String)
     * @see        java.lang.System#getProperty(java.lang.String, java.lang.String)
     * @see        java.util.PropertyPermission
     * @see        SecurityManager#checkPermission
     * @since      1.2
     */
    public static String setProperty(String key, String value) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key,
                SecurityConstants.PROPERTY_WRITE_ACTION));
        }

        return (String) props.setProperty(key, value);
    }

    /**
     * Removes the system property indicated by the specified key.
     * 1.删除由指定键指示的系统属性
     * <p>
     * First, if a security manager exists, its
     * <code>SecurityManager.checkPermission</code> method
     * is called with a <code>PropertyPermission(key, "write")</code>
     * permission. This may result in a SecurityException being thrown.
     * If no exception is thrown, the specified property is removed.
     * <p>
     * 2.首先，如果存在安全管理器，则使用 PropertyPermission(key, "write")
     * 权限调用其SecurityManager.checkPermission方法。这可能会导致抛出 SecurityException。
     * 如果没有抛出异常，则删除指定的属性
     *
     * @param      key   the name of the system property to be removed.
     * @return     the previous string value of the system property,
     *             or <code>null</code> if there was no property with that key.
     *
     * @exception  SecurityException  if a security manager exists and its
     *             <code>checkPropertyAccess</code> method doesn't allow
     *              access to the specified system property.
     * @exception  NullPointerException if <code>key</code> is
     *             <code>null</code>.
     * @exception  IllegalArgumentException if <code>key</code> is empty.
     * @see        #getProperty
     * @see        #setProperty
     * @see        java.util.Properties
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkPropertiesAccess()
     * @since 1.5
     */
    public static String clearProperty(String key) {
        checkKey(key);
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key, "write"));
        }

        return (String) props.remove(key);
    }

    private static void checkKey(String key) {
        if (key == null) {
            throw new NullPointerException("key can't be null");
        }
        if (key.equals("")) {
            throw new IllegalArgumentException("key can't be empty");
        }
    }

    /**
     * Gets the value of the specified environment variable. An
     * environment variable is a system-dependent external named
     * value.
     * 1.获取指定环境变量的值。环境变量是依赖于系统的外部命名值。
     * <p>If a security manager exists, its
     * {@link SecurityManager#checkPermission checkPermission}
     * method is called with a
     * <code>{@link RuntimePermission}("getenv."+name)</code>
     * permission.  This may result in a {@link SecurityException}
     * being thrown.  If no exception is thrown the value of the
     * variable <code>name</code> is returned.
     * 2.如果存在安全管理器，则使用 RuntimePermission("getenv."+name)权限调用其
     *  SecurityManager.checkPermission方法。这可能会导致抛出 SecurityException。
     *  如果没有抛出异常，则返回变量 name的值。
     * <p><a name="EnvironmentVSSystemProperties"><i>System
     * properties</i> and <i>environment variables</i></a> are both
     * conceptually mappings between names and values.  Both
     * mechanisms can be used to pass user-defined information to a
     * Java process.  Environment variables have a more global effect,
     * because they are visible to all descendants of the process
     * which defines them, not just the immediate Java subprocess.
     * They can have subtly different semantics, such as case
     * insensitivity, on different operating systems.  For these
     * reasons, environment variables are more likely to have
     * unintended side effects.  It is best to use system properties
     * where possible.  Environment variables should be used when a
     * global effect is desired, or when an external system interface
     * requires an environment variable (such as <code>PATH</code>).
     * 3.<a name="EnvironmentVSSystemProperties">系统属性和环境变量都是名称和值之间的概念映射。
     * 这两种机制都可用于将用户定义的信息传递给 Java 进程。环境变量具有更全局的影响，
     * 因为它们对定义它们的进程的所有后代可见，而不仅仅是直接的 Java 子进程。
     * 它们在不同的操作系统上可以有细微的不同语义，例如不区分大小写。
     * 由于这些原因，环境变量更有可能产生意想不到的副作用。
     * 最好尽可能使用系统属性。当需要全局效果时，或当外部系统接口需要环境变量（例如PATH）时，
     * 应使用环境变量
     * <p>On UNIX systems the alphabetic case of <code>name</code> is
     * typically significant, while on Microsoft Windows systems it is
     * typically not.  For example, the expression
     * <code>System.getenv("FOO").equals(System.getenv("foo"))</code>
     * is likely to be true on Microsoft Windows.
     * 4.在 UNIX 系统上，name的字母大小写通常很重要，而在 Microsoft Windows 系统上通常不重要。
     * 例如，表达式 System.getenv("FOO").equals(System.getenv("foo"))
     * 在 Microsoft Windows 上很可能为真
     * @param  name the name of the environment variable
     * @return the string value of the variable, or <code>null</code>
     *         if the variable is not defined in the system environment
     * @throws NullPointerException if <code>name</code> is <code>null</code>
     * @throws SecurityException
     *         if a security manager exists and its
     *         {@link SecurityManager#checkPermission checkPermission}
     *         method doesn't allow access to the environment variable
     *         <code>name</code>
     * @see    #getenv()
     * @see    ProcessBuilder#environment()
     */
    public static String getenv(String name) {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getenv."+name));
        }

        return ProcessEnvironment.getenv(name);
    }


    /**
     * Returns an unmodifiable string map view of the current system environment.
     * The environment is a system-dependent mapping from names to
     * values which is passed from parent to child processes.
     * 1.返回当前系统环境的不可修改的字符串映射视图。环境是从名称到从父进程传递到子进程的值的依赖于系统的映射。
     * <p>If the system does not support environment variables, an
     * empty map is returned.
     * 2.如果系统不支持环境变量，则返回一个空映射
     * <p>The returned map will never contain null keys or values.
     * Attempting to query the presence of a null key or value will
     * throw a {@link NullPointerException}.  Attempting to query
     * the presence of a key or value which is not of type
     * {@link String} will throw a {@link ClassCastException}.
     * 3.返回的映射永远不会包含空键或值。尝试查询空键或值是否存在将抛出NullPointerException。
     * 尝试查询不是  String类型的键或值的存在将抛出ClassCastException。
     * <p>The returned map and its collection views may not obey the
     * general contract of the {@link Object#equals} and
     * {@link Object#hashCode} methods.
     * 4.返回的地图及其集合视图可能不遵守Object.equals 和 Object.hashCode方法的一般约定。
     * <p>The returned map is typically case-sensitive on all platforms.
     * 5.返回的地图在所有平台上通常区分大小写
     * <p>If a security manager exists, its
     * {@link SecurityManager#checkPermission checkPermission}
     * method is called with a
     * <code>{@link RuntimePermission}("getenv.*")</code>
     * permission.  This may result in a {@link SecurityException} being
     * thrown.
     * 6.如果存在安全管理器，则使用 RuntimePermission("getenv.")权限调用其
     * SecurityManager.checkPermission方法。这可能会导致抛出SecurityException。
     * <p>When passing information to a Java subprocess,
     * <a href=#EnvironmentVSSystemProperties>system properties</a>
     * are generally preferred over environment variables.
     * 7.将信息传递给 Java 子进程时，<a href=EnvironmentVSSystemProperties>系统属性通常比环境变量更受欢迎
     * @return the environment as a map of variable names to values
     * @throws SecurityException
     *         if a security manager exists and its
     *         {@link SecurityManager#checkPermission checkPermission}
     *         method doesn't allow access to the process environment
     * @see    #getenv(String)
     * @see    ProcessBuilder#environment()
     * @since  1.5
     */
    public static java.util.Map<String,String> getenv() {
        SecurityManager sm = getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getenv.*"));
        }

        return ProcessEnvironment.getenv();
    }

    /**
     * Terminates the currently running Java Virtual Machine. The
     * argument serves as a status code; by convention, a nonzero status
     * code indicates abnormal termination.
     * 1.终止当前运行的 Java 虚拟机。参数用作状态码；按照惯例，非零状态代码表示异常终止
     * <p>
     * This method calls the <code>exit</code> method in class
     * <code>Runtime</code>. This method never returns normally.
     * 2.此方法调用 Runtime类中的exit 方法。此方法永远不会正常返回
     * <p>
     * The call <code>System.exit(n)</code> is effectively equivalent to
     * the call:
     * <blockquote><pre>
     * Runtime.getRuntime().exit(n)
     * </pre></blockquote>
     *
     * @param      status   exit status.
     * @throws  SecurityException
     *        if a security manager exists and its <code>checkExit</code>
     *        method doesn't allow exit with the specified status.
     * @see        java.lang.Runtime#exit(int)
     */
    public static void exit(int status) {
        Runtime.getRuntime().exit(status);
    }

    /**
     * Runs the garbage collector.
     * 1.运行垃圾收集器。
     * <p>
     * Calling the <code>gc</code> method suggests that the Java Virtual
     * Machine expend effort toward recycling unused objects in order to
     * make the memory they currently occupy available for quick reuse.
     * When control returns from the method call, the Java Virtual
     * Machine has made a best effort to reclaim space from all discarded
     * objects.
     * 2.调用gc方法表明 Java 虚拟机花费精力来回收未使用的对象，以使它们当前占用的内存可用于快速重用。
     * 当控制从方法调用返回时，Java 虚拟机已尽最大努力从所有丢弃的对象中回收空间
     * <p>
     * The call <code>System.gc()</code> is effectively equivalent to the
     * call:
     * <blockquote><pre>
     * Runtime.getRuntime().gc()
     * </pre></blockquote>
     *
     * @see     java.lang.Runtime#gc()
     */
    public static void gc() {
        Runtime.getRuntime().gc();
    }

    /**
     * Runs the finalization methods of any objects pending finalization.
     * 1.运行任何挂起终结的对象的终结方法
     * <p>
     * Calling this method suggests that the Java Virtual Machine expend
     * effort toward running the <code>finalize</code> methods of objects
     * that have been found to be discarded but whose <code>finalize</code>
     * methods have not yet been run. When control returns from the
     * method call, the Java Virtual Machine has made a best effort to
     * complete all outstanding finalizations.
     * 2.调用此方法表明 Java 虚拟机花费精力来运行已发现被丢弃但其 finalize方法尚未运行的对象的 finalize方法。
     * 当控制权从方法调用返回时，Java 虚拟机已尽最大努力完成所有未完成的终结。
     * <p>
     * The call <code>System.runFinalization()</code> is effectively
     * equivalent to the call:
     * <blockquote><pre>
     * Runtime.getRuntime().runFinalization()
     * </pre></blockquote>
     * 3.调用System.runFinalization()等效于调用： Runtime.getRuntime().runFinalization()
     * @see     java.lang.Runtime#runFinalization()
     */
    public static void runFinalization() {
        Runtime.getRuntime().runFinalization();
    }

    /**
     * Enable or disable finalization on exit; doing so specifies that the
     * finalizers of all objects that have finalizers that have not yet been
     * automatically invoked are to be run before the Java runtime exits.
     * By default, finalization on exit is disabled.
     * 1.退出时启用或禁用终结；这样做指定所有具有尚未自动调用的终结器的对象的终结器
     * 将在 Java 运行时退出之前运行。默认情况下，退出时的终结是禁用的。
     * <p>If there is a security manager,
     * its <code>checkExit</code> method is first called
     * with 0 as its argument to ensure the exit is allowed.
     * This could result in a SecurityException.
     * 2.如果存在安全管理器，则首先调用其checkExit方法，
     * 并使用 0 作为其参数以确保允许退出。这可能会导致 SecurityException
     * @deprecated  This method is inherently unsafe.  It may result in
     *      finalizers being called on live objects while other threads are
     *      concurrently manipulating those objects, resulting in erratic
     *      behavior or deadlock.
     * @param value indicating enabling or disabling of finalization
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
        Runtime.runFinalizersOnExit(value);
    }

    /**
     * Loads the native library specified by the filename argument.  The filename
     * argument must be an absolute path name.
     * 1.加载由文件名参数指定的本机库。文件名参数必须是绝对路径名。
     * If the filename argument, when stripped of any platform-specific library
     * prefix, path, and file extension, indicates a library whose name is,
     * for example, L, and a native library called L is statically linked
     * with the VM, then the JNI_OnLoad_L function exported by the library
     * is invoked rather than attempting to load a dynamic library.
     * A filename matching the argument does not have to exist in the
     * file system.
     * See the JNI Specification for more details.
     * 2.如果 filename 参数在去除任何特定于平台的库前缀、路径和文件扩展名后表示名称为 L 的库，
     * 并且名为 L 的本机库与 VM 静态链接，则 JNI_OnLoad_L 函数由库导出被调用而不是尝试加载动态库。
     * 文件系统中不必存在与参数匹配的文件名。
     * 3.有关更多详细信息，请参阅 JNI 规范。
     * Otherwise, the filename argument is mapped to a native library image in
     * an implementation-dependent manner.
     * 4.否则，文件名参数将以依赖于实现的方式映射到本机库映像
     * <p>
     * The call <code>System.load(name)</code> is effectively equivalent
     * to the call:
     * <blockquote><pre>
     * Runtime.getRuntime().load(name)
     * </pre></blockquote>
     * 5.调用System.load(name)等效于调用： Runtime.getRuntime().load(name)
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
     * @see        java.lang.Runtime#load(java.lang.String)
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     */
    @CallerSensitive
    public static void load(String filename) {
        Runtime.getRuntime().load0(Reflection.getCallerClass(), filename);
    }

    /**
     * Loads the native library specified by the <code>libname</code>
     * argument.  The <code>libname</code> argument must not contain any platform
     * specific prefix, file extension or path. If a native library
     * called <code>libname</code> is statically linked with the VM, then the
     * JNI_OnLoad_<code>libname</code> function exported by the library is invoked.
     * See the JNI Specification for more details.
     * 1.加载由 libname参数指定的本机库。libname参数不得包含任何特定于平台的前缀、文件扩展名或路径。
     * 如果名为libname的本机库与 VM 静态链接，则调用该库导出的 JNI_OnLoad_libname函数。
     * 有关更多详细信息，请参阅 JNI 规范。
     * Otherwise, the libname argument is loaded from a system library
     * location and mapped to a native library image in an implementation-
     * dependent manner.
     * 2.否则，libname 参数将从系统库位置加载并以依赖于实现的方式映射到本机库映像
     * <p>
     * The call <code>System.loadLibrary(name)</code> is effectively
     * equivalent to the call
     * <blockquote><pre>
     * Runtime.getRuntime().loadLibrary(name)
     * </pre></blockquote>
     * 3.调用 System.loadLibrary(name)等效于调用Runtime.getRuntime().loadLibrary(name)
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
     * @see        java.lang.Runtime#loadLibrary(java.lang.String)
     * @see        java.lang.SecurityManager#checkLink(java.lang.String)
     */
    @CallerSensitive
    public static void loadLibrary(String libname) {
        Runtime.getRuntime().loadLibrary0(Reflection.getCallerClass(), libname);
    }

    /**
     * Maps a library name into a platform-specific string representing
     * a native library.
     * 1将库名称映射到表示本机库的特定于平台的字符串。
     * @param      libname the name of the library.
     * @return     a platform-dependent native library name.
     * @exception  NullPointerException if <code>libname</code> is
     *             <code>null</code>
     * @see        java.lang.System#loadLibrary(java.lang.String)
     * @see        java.lang.ClassLoader#findLibrary(java.lang.String)
     * @since      1.2
     */
    public static native String mapLibraryName(String libname);

    /**
     * Create PrintStream for stdout/err based on encoding.
     * 根据编码为 stdout/err 创建 PrintStream。
     */
    private static PrintStream newPrintStream(FileOutputStream fos, String enc) {
       if (enc != null) {
            try {
                return new PrintStream(new BufferedOutputStream(fos, 128), true, enc);
            } catch (UnsupportedEncodingException uee) {}
        }
        return new PrintStream(new BufferedOutputStream(fos, 128), true);
    }


    /**
     * Initialize the system class.  Called after thread initialization.
     * //初始化系统类。在线程初始化后调用。
     */
    private static void initializeSystemClass() {

        // VM might invoke JNU_NewStringPlatform() to set those encoding
        // sensitive properties (user.home, user.name, boot.class.path, etc.)
        // during "props" initialization, in which it may need access, via
        // System.getProperty(), to the related system encoding property that
        // have been initialized (put into "props") at early stage of the
        // initialization. So make sure the "props" is available at the
        // very beginning of the initialization and all system properties to
        // be put into it directly.
        //VM 可能会在“道具”初始化期间调用 JNU_NewStringPlatform() 来设置那些编码敏感属性
        // （user.home、user.name、boot.class.path 等），其中它可能需要通过 System.getProperty()
        // 进行访问，到在初始化早期已经初始化（放入“props”）的相关系统编码属性。
        // 因此，请确保“道具”在初始化的一开始就可用，并且所有系统属性都可以直接放入其中
        props = new Properties();
        initProperties(props);  // initialized by the VM

        // There are certain system configurations that may be controlled by
        // VM options such as the maximum amount of direct memory and
        // Integer cache size used to support the object identity semantics
        // of autoboxing.  Typically, the library will obtain these values
        // from the properties set by the VM.  If the properties are for
        // internal implementation use only, these properties should be
        // removed from the system properties.
        // 1.某些系统配置可能由 VM 选项控制，例如用于支持自动装箱对象标识语义的最大直接内存量和整数缓存大小。
        // 通常，库将从 VM 设置的属性中获取这些值。如果属性仅供内部实现使用，则应从系统属性中删除这些属性。
        // See java.lang.Integer.IntegerCache and the
        // sun.misc.VM.saveAndRemoveProperties method for example.
        // 2.例如，参见 java.lang.Integer.IntegerCache 和 sun.misc.VM.saveAndRemoveProperties 方法。
        // Save a private copy of the system properties object that
        // can only be accessed by the internal implementation.  Remove
        // certain system properties that are not intended for public access.
        //3.保存只能由内部实现访问的系统属性对象的私有副本。删除某些不供公共访问的系统属性
        sun.misc.VM.saveAndRemoveProperties(props);


        lineSeparator = props.getProperty("line.separator");
        sun.misc.Version.init();

        FileInputStream fdIn = new FileInputStream(FileDescriptor.in);
        FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
        FileOutputStream fdErr = new FileOutputStream(FileDescriptor.err);
        setIn0(new BufferedInputStream(fdIn));
        setOut0(newPrintStream(fdOut, props.getProperty("sun.stdout.encoding")));
        setErr0(newPrintStream(fdErr, props.getProperty("sun.stderr.encoding")));

        // Load the zip library now in order to keep java.util.zip.ZipFile
        // from trying to use itself to load this library later.
        //现在加载 zip 库，以防止 java.util.zip.ZipFile 稍后尝试使用自身加载此库
        loadLibrary("zip");

        // Setup Java signal handlers for HUP, TERM, and INT (where available).
        //为 HUP、TERM 和 INT（如果可用）设置 Java 信号处理程序
        Terminator.setup();

        // Initialize any miscellenous operating system settings that need to be
        // set for the class libraries. Currently this is no-op everywhere except
        // for Windows where the process-wide error mode is set before the java.io
        // classes are used.
        //初始化需要为类库设置的任何杂项操作系统设置。
        // 目前，除了在使用 java.io 类之前设置进程范围错误模式的 Windows 之外，这在任何地方都是无操作的。
        sun.misc.VM.initializeOSEnvironment();

        // The main thread is not added to its thread group in the same
        // way as other threads; we must do it ourselves here.
        //主线程没有像其他线程一样加入到它的线程组中；我们必须在这里自己做。
        Thread current = Thread.currentThread();
        current.getThreadGroup().add(current);

        // register shared secrets
        //注册共享机密
        setJavaLangAccess();

        // Subsystems that are invoked during initialization can invoke
        // sun.misc.VM.isBooted() in order to avoid doing things that should
        // wait until the application class loader has been set up.
        // IMPORTANT: Ensure that this remains the last initialization action!
        sun.misc.VM.booted();
    }

    private static void setJavaLangAccess() {
        // Allow privileged classes outside of java.lang
        //允许 java.lang 之外的特权类
        sun.misc.SharedSecrets.setJavaLangAccess(new sun.misc.JavaLangAccess(){
            public sun.reflect.ConstantPool getConstantPool(Class<?> klass) {
                return klass.getConstantPool();
            }
            public boolean casAnnotationType(Class<?> klass, AnnotationType oldType, AnnotationType newType) {
                return klass.casAnnotationType(oldType, newType);
            }
            public AnnotationType getAnnotationType(Class<?> klass) {
                return klass.getAnnotationType();
            }
            public Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap(Class<?> klass) {
                return klass.getDeclaredAnnotationMap();
            }
            public byte[] getRawClassAnnotations(Class<?> klass) {
                return klass.getRawAnnotations();
            }
            public byte[] getRawClassTypeAnnotations(Class<?> klass) {
                return klass.getRawTypeAnnotations();
            }
            public byte[] getRawExecutableTypeAnnotations(Executable executable) {
                return Class.getExecutableTypeAnnotationBytes(executable);
            }
            public <E extends Enum<E>>
                    E[] getEnumConstantsShared(Class<E> klass) {
                return klass.getEnumConstantsShared();
            }
            public void blockedOn(Thread t, Interruptible b) {
                t.blockedOn(b);
            }
            public void registerShutdownHook(int slot, boolean registerShutdownInProgress, Runnable hook) {
                Shutdown.add(slot, registerShutdownInProgress, hook);
            }
            public int getStackTraceDepth(Throwable t) {
                return t.getStackTraceDepth();
            }
            public StackTraceElement getStackTraceElement(Throwable t, int i) {
                return t.getStackTraceElement(i);
            }
            public String newStringUnsafe(char[] chars) {
                return new String(chars, true);
            }
            public Thread newThreadWithAcc(Runnable target, AccessControlContext acc) {
                return new Thread(target, acc);
            }
            public void invokeFinalize(Object o) throws Throwable {
                o.finalize();
            }
        });
    }
}
