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
import  java.io.*;
import  java.util.*;

/**
 * The {@code Throwable} class is the superclass of all errors and
 * exceptions in the Java language. Only objects that are instances of this
 * class (or one of its subclasses) are thrown by the Java Virtual Machine or
 * can be thrown by the Java {@code throw} statement. Similarly, only
 * this class or one of its subclasses can be the argument type in a
 * {@code catch} clause.
 * 1.Throwable类是 Java 语言中所有错误和异常的超类。只有属于此类（或其子类之一）的实例的对象
 * 才会被 Java 虚拟机抛出或可以被 Java throw语句抛出。
 * 类似地，只有此类或其子类之一可以是  catch子句中的参数类型
 * For the purposes of compile-time checking of exceptions, {@code
 * Throwable} and any subclass of {@code Throwable} that is not also a
 * subclass of either {@link RuntimeException} or {@link Error} are
 * regarded as checked exceptions.
 * 2.出于编译时异常检查的目的，Throwable和 Throwable的任何子类
 * （不是RuntimeException或  Error的子类）都被视为已检查异常
 * <p>Instances of two subclasses, {@link java.lang.Error} and
 * {@link java.lang.Exception}, are conventionally used to indicate
 * that exceptional situations have occurred. Typically, these instances
 * are freshly created in the context of the exceptional situation so
 * as to include relevant information (such as stack trace data).
 * 3.两个子类的实例，java.lang.Error和 java.lang.Exception，通常用于指示发生了异常情况。
 * 通常，这些实例是在异常情况的上下文中新创建的，以便包含相关信息（例如堆栈跟踪数据）
 * <p>A throwable contains a snapshot of the execution stack of its
 * thread at the time it was created. It can also contain a message
 * string that gives more information about the error. Over time, a
 * throwable can {@linkplain Throwable#addSuppressed suppress} other
 * throwables from being propagated.  Finally, the throwable can also
 * contain a <i>cause</i>: another throwable that caused this
 * throwable to be constructed.  The recording of this causal information
 * is referred to as the <i>chained exception</i> facility, as the
 * cause can, itself, have a cause, and so on, leading to a "chain" of
 * exceptions, each caused by another.
 * 4.throwable 包含创建时其线程的执行堆栈的快照,它还可以包含一个消息字符串，提供有关错误的更多信息
 * 5.随着时间的推移，一个throwable可以Throwable.addSuppressed抑制其他throwable被传播
 * 6.最后，throwable 还可以包含一个原因：另一个导致该 throwable 被构造的 throwable
 * 7.这种因果信息的记录被称为链式异常设施，因为原因本身可以有一个原因，等等，导致异常的“链”，每个异常都由另一个引起
 *
 * <p>One reason that a throwable may have a cause is that the class that
 * throws it is built atop a lower layered abstraction, and an operation on
 * the upper layer fails due to a failure in the lower layer.  It would be bad
 * design to let the throwable thrown by the lower layer propagate outward, as
 * it is generally unrelated to the abstraction provided by the upper layer.
 * Further, doing so would tie the API of the upper layer to the details of
 * its implementation, assuming the lower layer's exception was a checked
 * exception.  Throwing a "wrapped exception" (i.e., an exception containing a
 * cause) allows the upper layer to communicate the details of the failure to
 * its caller without incurring either of these shortcomings.  It preserves
 * the flexibility to change the implementation of the upper layer without
 * changing its API (in particular, the set of exceptions thrown by its
 * methods).
 * 8.throwable可能有原因的一个原因是抛出它的类构建在较低层的抽象之上，并且由于较低层的失败而导致上层的操作失败
 * 9.让下层抛出的throwable向外传播是不好的设计，因为它通常与上层提供的抽象无关。
 * 10.此外，这样做会将上层的 API 与其实现的细节联系起来，假设下层的异常是已检查的异常
 * 11.抛出“包装异常”（即包含原因的异常）允许上层将失败的详细信息传达给其调用者，而不会导致这些缺点中的任何一个
 * 12.它保留了更改上层实现的灵活性，而无需更改其 API（特别是其方法抛出的异常集）
 * <p>A second reason that a throwable may have a cause is that the method
 * that throws it must conform to a general-purpose interface that does not
 * permit the method to throw the cause directly.  For example, suppose
 * a persistent collection conforms to the {@link java.util.Collection
 * Collection} interface, and that its persistence is implemented atop
 * {@code java.io}.  Suppose the internals of the {@code add} method
 * can throw an {@link java.io.IOException IOException}.  The implementation
 * can communicate the details of the {@code IOException} to its caller
 * while conforming to the {@code Collection} interface by wrapping the
 * {@code IOException} in an appropriate unchecked exception.  (The
 * specification for the persistent collection should indicate that it is
 * capable of throwing such exceptions.)
 *13.造成throwable的第二个原因可能是，抛出它的方法必须符合不允许该方法直接抛出原因的通用接口
 * 14.例如，假设一个持久化集合符合java.util.Collection接口，并且它的持久化是在java.io之上实现的。
 * 通过将 IOException包装在适当的未经检查的异常中，实现可以将 IOException的详细信息传达给其调用者，
 * 同时符合 Collection接口。 （持久集合的规范应该表明它能够抛出这样的异常。）
 * <p>A cause can be associated with a throwable in two ways: via a
 * constructor that takes the cause as an argument, or via the
 * {@link #initCause(Throwable)} method.  New throwable classes that
 * wish to allow causes to be associated with them should provide constructors
 * that take a cause and delegate (perhaps indirectly) to one of the
 * {@code Throwable} constructors that takes a cause.
 * 15.原因可以通过两种方式与 throwable 相关联：通过将原因作为参数的构造函数，或通过initCause(Throwable)方法
 * 16.希望允许原因与它们相关联的新可抛出类,应提供采用原因的构造函数并委托（可能间接地）给采用原因的 Throwable构造函数之一
 * Because the {@code initCause} method is public, it allows a cause to be
 * associated with any throwable, even a "legacy throwable" whose
 * implementation predates the addition of the exception chaining mechanism to
 * {@code Throwable}.
 * 17.因为 initCause方法是公开的，它允许将原因与任何可抛出的相关联，
 * 甚至是“遗留的可抛出”，其实现早于将异常链机制添加到 Throwable。
 * <p>By convention, class {@code Throwable} and its subclasses have two
 * constructors, one that takes no arguments and one that takes a
 * {@code String} argument that can be used to produce a detail message.
 * Further, those subclasses that might likely have a cause associated with
 * them should have two more constructors, one that takes a
 * {@code Throwable} (the cause), and one that takes a
 * {@code String} (the detail message) and a {@code Throwable} (the
 * cause).
 * 18.按照惯例，类Throwable及其子类有两个构造函数，一个不接受参数，另一个接受可用于生成详细消息的String参数
 * 19.此外，那些可能有相关原因的子类应该有两个以上的构造函数，其中一个采用 {@code Throwable}（原因），
 * 其二是一个采用 String(详细消息）和一个Throwable原因）
 * @author  unascribed
 * @author  Josh Bloch (Added exception chaining and programmatic access to
 *          stack trace in 1.4.)
 * @jls 11.2 Compile-Time Checking of Exceptions
 * @since JDK1.0
 */
public class Throwable implements Serializable {
    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -3042686055658047285L;

    /**
     * Native code saves some indication of the stack backtrace in this slot.
     * 本机代码在此槽中保存了一些堆栈回溯的指示
     */
    private transient Object backtrace;

    /**
     * Specific details about the Throwable.  For example, for
     * {@code FileNotFoundException}, this contains the name of
     * the file that could not be found.
     *关于 Throwable 的具体细节。例如，对于 FileNotFoundException，这包含无法找到的文件的名称。
     * @serial
     */
    private String detailMessage;


    /**
     * Holder class to defer initializing sentinel objects only used
     * for serialization.
     *  持有者类推迟初始化仅用于序列化的哨兵对象
     */
    private static class SentinelHolder {
        /**
         * {@linkplain #setStackTrace(StackTraceElement[]) Setting the
         * stack trace} to a one-element array containing this sentinel
         * value indicates future attempts to set the stack trace will be
         * ignored.  The sentinal is equal to the result of calling:<br>
         * {@code new StackTraceElement("", "", null, Integer.MIN_VALUE)}
         * 1.setStackTrace(StackTraceElement[]将堆栈跟踪设置为包含此标记值的单元素数组，
         * 表示将来设置堆栈跟踪的尝试将被忽略
         * 2.哨兵等于调用结果：new StackTraceElement("", "", null, Integer.MIN_VALUE)
         */
        public static final StackTraceElement STACK_TRACE_ELEMENT_SENTINEL =
            new StackTraceElement("", "", null, Integer.MIN_VALUE);

        /**
         * Sentinel value used in the serial form to indicate an immutable
         * stack trace.
         * 以串行形式使用的 Sentinel 值来指示不可变的堆栈跟踪
         */
        public static final StackTraceElement[] STACK_TRACE_SENTINEL =
            new StackTraceElement[] {STACK_TRACE_ELEMENT_SENTINEL};
    }

    /**
     * A shared value for an empty stack.
     * 空堆栈的共享值
     */
    private static final StackTraceElement[] UNASSIGNED_STACK = new StackTraceElement[0];

    /*
     * To allow Throwable objects to be made immutable and safely
     * reused by the JVM, such as OutOfMemoryErrors, fields of
     * Throwable that are writable in response to user actions, cause,
     * stackTrace, and suppressedExceptions obey the following
     * protocol:
     * 1.为了让 Throwable 对象成为不可变的并且被 JVM 安全地重用，
     * 例如 OutOfMemoryErrors、响应用户操作的可写 Throwable 字段、
     * cause、stackTrace 和抑制异常遵循以下协议
     *
     * 1) The fields are initialized to a non-null sentinel value
     * which indicates the value has logically not been set.
     * 1).这些字段被初始化为一个非空的哨兵值，这表明该值在逻辑上没有被设置
     * 2) Writing a null to the field indicates further writes
     * are forbidden
     * 2) 向该字段写入空值表示禁止进一步写入
     * 3) The sentinel value may be replaced with another non-null
     * value.
     * 3)哨兵值可以替换为另一个非空值
     * For example, implementations of the HotSpot JVM have
     * preallocated OutOfMemoryError objects to provide for better
     * diagnosability of that situation.  These objects are created
     * without calling the constructor for that class and the fields
     * in question are initialized to null.  To support this
     * capability, any new fields added to Throwable that require
     * being initialized to a non-null value require a coordinated JVM
     * change.
     * 2.例如，HotSpot JVM 的实现已经预先分配了 OutOfMemoryError 对象，以便更好地诊断这种情况。
     * 3.这些对象是在不调用该类的构造函数的情况下创建的，并且相关字段被初始化为 null
     * 4.为了支持此功能，添加到 Throwable 的任何新字段需要初始化为非空值，都需要协调 JVM 更改
     */

    /**
     * The throwable that caused this throwable to get thrown, or null if this
     * throwable was not caused by another throwable, or if the causative
     * throwable is unknown.  If this field is equal to this throwable itself,
     * it indicates that the cause of this throwable has not yet been
     * initialized.
     * 导致该 throwable 被抛出的 throwable，如果该 throwable 不是由另一个 throwable 引起的，
     * 或者如果导致该 throwable 的原因未知，则为 null。如果这个字段等于这个throwable本身，
     * 说明这个throwable的原因还没有被初始化
     * @serial
     * @since 1.4
     */
    private Throwable cause = this;

    /**
     * The stack trace, as returned by {@link #getStackTrace()}.
     * 1.getStackTrace()返回的堆栈跟踪
     * The field is initialized to a zero-length array.  A {@code
     * null} value of this field indicates subsequent calls to {@link
     * #setStackTrace(StackTraceElement[])} and {@link
     * #fillInStackTrace()} will be be no-ops.
     * 2.该字段被初始化为零长度数组。此字段的 null值表示对setStackTrace(StackTraceElement[])
     * 和 fillInStackTrace()的后续调用将是空操作
     * @serial
     * @since 1.4
     */
    private StackTraceElement[] stackTrace = UNASSIGNED_STACK;

    // Setting this static field introduces an acceptable
    // initialization dependency on a few java.util classes.
    //设置此静态字段会引入对一些 java.util 类的可接受的初始化依赖性
    private static final List<Throwable> SUPPRESSED_SENTINEL =
        Collections.unmodifiableList(new ArrayList<Throwable>(0));

    /**
     * The list of suppressed exceptions, as returned by {@link
     * #getSuppressed()}.  The list is initialized to a zero-element
     * unmodifiable sentinel list.  When a serialized Throwable is
     * read in, if the {@code suppressedExceptions} field points to a
     * zero-element list, the field is reset to the sentinel value.
     * 1.被抑制的异常列表，由getSuppressed()返回
     * 2.该列表被初始化为一个零元素不可修改的哨兵列表
     * 3.当读入序列化的 Throwable 时，如果 suppressExceptions字段指向零元素列表，则该字段将重置为哨兵值
     * @serial
     * @since 1.7
     */
    private List<Throwable> suppressedExceptions = SUPPRESSED_SENTINEL;

    /** Message for trying to suppress a null exception. */
    //尝试抑制空异常的消息
    private static final String NULL_CAUSE_MESSAGE = "Cannot suppress a null exception.";

    /** Message for trying to suppress oneself. */
    //试图压抑自己的消息
    private static final String SELF_SUPPRESSION_MESSAGE = "Self-suppression not permitted";

    /** Caption  for labeling causative exception stack traces */
    //用于标记导致异常堆栈跟踪的标题
    private static final String CAUSE_CAPTION = "Caused by: ";

    /** Caption for labeling suppressed exception stack traces */
    //用于标记被抑制的异常堆栈跟踪的标题
    private static final String SUPPRESSED_CAPTION = "Suppressed: ";

    /**
     * Constructs a new throwable with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * 1.使用 null作为详细消息构造一个新的 throwable。
     * 原因未初始化，随后可能会通过调用 initCause进行初始化
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     * 2.调用 fillInStackTrace()方法对新创建的 throwable 中的堆栈跟踪数据进行初始化
     */
    public Throwable() {
        fillInStackTrace();
    }

    /**
     * Constructs a new throwable with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     * 1.使用指定的详细消息构造一个新的 throwable。原因未初始化，随后可能会通过调用 initCause进行初始化。
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     * 2.调用 fillInStackTrace()方法来初始化新创建的 throwable 中的堆栈跟踪数据
     * @param   message   the detail message. The detail message is saved for
     *          later retrieval by the {@link #getMessage()} method.
     */
    public Throwable(String message) {
        fillInStackTrace();
        detailMessage = message;
    }

    /**
     * Constructs a new throwable with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this throwable's detail message.
     * 1.使用指定的详细消息和原因构造一个新的 throwable。
     * 请注意，与 cause相关联的详细消息不会<自动合并到此 throwable 的详细消息中。
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     * 2.调用 fillInStackTrace()方法来初始化新创建的 throwable 中的堆栈跟踪数据
     * @param  message the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public Throwable(String message, Throwable cause) {
        fillInStackTrace();
        detailMessage = message;
        this.cause = cause;
    }

    /**
     * Constructs a new throwable with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     * This constructor is useful for throwables that are little more than
     * wrappers for other throwables (for example, {@link
     * java.security.PrivilegedActionException}).
     * 1.使用指定的原因和 (cause==null ? null :cause.toString())的详细消息
     * 构造一个新的 throwable（通常包含cause的类和详细消息）
     * 2.此构造函数对于仅是其他 throwable 的包装器的 throwable 很有用,
     * 例如，java.security.PrivilegedActionException
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     * 3.调用 fillInStackTrace() 方法来初始化新创建的 throwable 中的堆栈跟踪数据
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    public Throwable(Throwable cause) {
        fillInStackTrace();
        detailMessage = (cause==null ? null : cause.toString());
        this.cause = cause;
    }

    /**
     * Constructs a new throwable with the specified detail message,
     * cause, {@linkplain #addSuppressed suppression} enabled or
     * disabled, and writable stack trace enabled or disabled.  If
     * suppression is disabled, {@link #getSuppressed} for this object
     * will return a zero-length array and calls to {@link
     * #addSuppressed} that would otherwise append an exception to the
     * suppressed list will have no effect.  If the writable stack
     * trace is false, this constructor will not call {@link
     * #fillInStackTrace()}, a {@code null} will be written to the
     * {@code stackTrace} field, and subsequent calls to {@code
     * fillInStackTrace} and {@link
     * #setStackTrace(StackTraceElement[])} will not set the stack
     * trace.  If the writable stack trace is false, {@link
     * #getStackTrace} will return a zero length array.
     * 1.使用指定的详细消息、原因、启用或禁用 addSuppressed 抑制以及启用或禁用可写堆栈跟踪构造一个新的 throwable。
     * 2.如果禁用了抑制，则此对象的 getSuppressed将返回一个零长度数组，并且调用 addSuppressed
     * 否则会将异常附加到抑制列表将无效
     * 3.如果可写堆栈跟踪为 false，则此构造函数不会调用fillInStackTrace()，
     * null将写入  stackTrace字段，随后调用fillInStackTrace 和 setStackTrace(StackTraceElement[])不会设置堆栈跟踪。
     * <p>Note that the other constructors of {@code Throwable} treat
     * suppression as being enabled and the stack trace as being
     * writable.  Subclasses of {@code Throwable} should document any
     * conditions under which suppression is disabled and document
     * conditions under which the stack trace is not writable.
     * Disabling of suppression should only occur in exceptional
     * circumstances where special requirements exist, such as a
     * virtual machine reusing exception objects under low-memory
     * situations.  Circumstances where a given exception object is
     * repeatedly caught and rethrown, such as to implement control
     * flow between two sub-systems, is another situation where
     * immutable throwable objects would be appropriate.
     * 4.请注意，Throwable的其他构造函数将抑制视为已启用，堆栈跟踪视为可写。
     * 5.Throwable的子类应该记录禁用抑制的任何条件以及记录堆栈跟踪不可写的条件。
     * 6.禁用抑制应该只发生在存在特殊要求的异常情况下，例如虚拟机在低内存情况下重用异常对象
     * 7.给定异常对象被重复捕获和重新抛出的情况，例如在两个子系统之间实现控制流，是另一种适合不可变抛出对象的情况。
     * @param  message the detail message.
     * @param cause the cause.  (A {@code null} value is permitted,
     * and indicates that the cause is nonexistent or unknown.)
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be
     *                           writable
     *
     * @see OutOfMemoryError
     * @see NullPointerException
     * @see ArithmeticException
     * @since 1.7
     */
    protected Throwable(String message, Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace) {
        if (writableStackTrace) {
            fillInStackTrace();
        } else {
            stackTrace = null;
        }
        detailMessage = message;
        this.cause = cause;
        if (!enableSuppression)
            suppressedExceptions = null;
    }

    /**
     * Returns the detail message string of this throwable.
     * 返回此 throwable 的详细消息字符串
     * @return  the detail message string of this {@code Throwable} instance
     *          (which may be {@code null}).
     */
    public String getMessage() {
        return detailMessage;
    }

    /**
     * Creates a localized description of this throwable.
     * Subclasses may override this method in order to produce a
     * locale-specific message.  For subclasses that do not override this
     * method, the default implementation returns the same result as
     * {@code getMessage()}.
     * 1.创建此 throwable 的本地化描述,子类可以覆盖此方法以生成特定于语言环境的消息。
     * 2.对于不覆盖此方法的子类，默认实现返回与getMessage()相同的结果。
     * @return  The localized description of this throwable.
     * @since   JDK1.1
     */
    public String getLocalizedMessage() {
        return getMessage();
    }

    /**
     * Returns the cause of this throwable or {@code null} if the
     * cause is nonexistent or unknown.  (The cause is the throwable that
     * caused this throwable to get thrown.)
     * 1.如果原因不存在或未知，则返回此 throwable 的原因或null。
     * （原因是导致这个 throwable 被抛出的 throwable。）
     * <p>This implementation returns the cause that was supplied via one of
     * the constructors requiring a {@code Throwable}, or that was set after
     * creation with the {@link #initCause(Throwable)} method.  While it is
     * typically unnecessary to override this method, a subclass can override
     * it to return a cause set by some other means.  This is appropriate for
     * a "legacy chained throwable" that predates the addition of chained
     * exceptions to {@code Throwable}.  Note that it is <i>not</i>
     * necessary to override any of the {@code PrintStackTrace} methods,
     * all of which invoke the {@code getCause} method to determine the
     * cause of a throwable.
     * 2.此实现返回通过需要 Throwable的构造函数之一提供的原因，或者在使用 initCause(Throwable)方法创建后设置的原因
     * 3.虽然通常没有必要覆盖此方法，但子类可以覆盖它以返回通过其他方式设置的原因
     * 4.这适用于在将链式异常添加到 Throwable之前的“遗留链式可抛出”
     * 5.请注意，不需要覆盖任何PrintStackTrace方法，所有这些方法都会调用getCause方法来确定 throwable 的原因。
     * @return  the cause of this throwable or {@code null} if the
     *          cause is nonexistent or unknown.
     * @since 1.4
     */
    public synchronized Throwable getCause() {
        return (cause==this ? null : cause);
    }

    /**
     * Initializes the <i>cause</i> of this throwable to the specified value.
     * (The cause is the throwable that caused this throwable to get thrown.)
     * 1.将此 throwable 的 cause初始化为指定值。（原因是导致这个 throwable 被抛出的 throwable。）
     * <p>This method can be called at most once.  It is generally called from
     * within the constructor, or immediately after creating the
     * throwable.  If this throwable was created
     * with {@link #Throwable(Throwable)} or
     * {@link #Throwable(String,Throwable)}, this method cannot be called
     * even once.
     *2.该方法最多可以调用一次。它通常在构造函数中调用，或者在创建 throwable 之后立即调用。
     * 3.如果这个 throwable 是用Throwable(Throwable)或 Throwable(String,Throwable) 创建的，
     * 这个方法甚至不能被调用一次
     * <p>An example of using this method on a legacy throwable type
     * without other support for setting the cause is:
     *
     * <pre>
     * try {
     *     lowLevelOp();
     * } catch (LowLevelException le) {
     *     throw (HighLevelException)
     *           new HighLevelException().initCause(le); // Legacy constructor
     * }
     * </pre>
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @return  a reference to this {@code Throwable} instance.
     * @throws IllegalArgumentException if {@code cause} is this
     *         throwable.  (A throwable cannot be its own cause.)
     * @throws IllegalStateException if this throwable was
     *         created with {@link #Throwable(Throwable)} or
     *         {@link #Throwable(String,Throwable)}, or this method has already
     *         been called on this throwable.
     * @since  1.4
     */
    public synchronized Throwable initCause(Throwable cause) {
        if (this.cause != this)
            throw new IllegalStateException("Can't overwrite cause with " +
                                            Objects.toString(cause, "a null"), this);
        if (cause == this)
            throw new IllegalArgumentException("Self-causation not permitted", this);
        this.cause = cause;
        return this;
    }

    /**
     * Returns a short description of this throwable.
     * The result is the concatenation of:
     * <ul>
     * <li> the {@linkplain Class#getName() name} of the class of this object
     * <li> ": " (a colon and a space)
     * <li> the result of invoking this object's {@link #getLocalizedMessage}
     *      method
     * </ul>
     * If {@code getLocalizedMessage} returns {@code null}, then just
     * the class name is returned.
     * 返回此 throwable 的简短描述。结果是:
     * 此对象的类的Class.getName() ": "（一个冒号和一个空格）
     * 调用此对象的  getLocalizedMessage方法的结果
     * 如果 getLocalizedMessage返回 null，则只返回类名。
     * @return a string representation of this throwable.
     */
    public String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    /**
     * Prints this throwable and its backtrace to the
     * standard error stream. This method prints a stack trace for this
     * {@code Throwable} object on the error output stream that is
     * the value of the field {@code System.err}. The first line of
     * output contains the result of the {@link #toString()} method for
     * this object.  Remaining lines represent data previously recorded by
     * the method {@link #fillInStackTrace()}. The format of this
     * information depends on the implementation, but the following
     * example may be regarded as typical:
     * 1.将此 throwable 及其回溯打印到标准错误流
     * 2.此方法在错误输出流上打印此 Throwable对象的堆栈跟踪，即字段System.err的值。
     * 3.输出的第一行包含此对象的 toString()方法的结果,剩余的行表示之前由方法fillInStackTrace()记录的数据。
     *
     * <blockquote><pre>
     * java.lang.NullPointerException
     *         at MyClass.mash(MyClass.java:9)
     *         at MyClass.crunch(MyClass.java:6)
     *         at MyClass.main(MyClass.java:3)
     * </pre></blockquote>
     * This example was produced by running the program:
     * <pre>
     * class MyClass {
     *     public static void main(String[] args) {
     *         crunch(null);
     *     }
     *     static void crunch(int[] a) {
     *         mash(a);
     *     }
     *     static void mash(int[] b) {
     *         System.out.println(b[0]);
     *     }
     * }
     * </pre>
     * The backtrace for a throwable with an initialized, non-null cause
     * should generally include the backtrace for the cause.  The format
     * of this information depends on the implementation, but the following
     * example may be regarded as typical:
     * 具有初始化的非空原因的 throwable 的回溯通常应包括原因的回溯
     * <pre>
     * HighLevelException: MidLevelException: LowLevelException
     *         at Junk.a(Junk.java:13)
     *         at Junk.main(Junk.java:4)
     * Caused by: MidLevelException: LowLevelException
     *         at Junk.c(Junk.java:23)
     *         at Junk.b(Junk.java:17)
     *         at Junk.a(Junk.java:11)
     *         ... 1 more
     * Caused by: LowLevelException
     *         at Junk.e(Junk.java:30)
     *         at Junk.d(Junk.java:27)
     *         at Junk.c(Junk.java:21)
     *         ... 3 more
     * </pre>
     * Note the presence of lines containing the characters {@code "..."}.
     * These lines indicate that the remainder of the stack trace for this
     * exception matches the indicated number of frames from the bottom of the
     * stack trace of the exception that was caused by this exception (the
     * "enclosing" exception).  This shorthand can greatly reduce the length
     * of the output in the common case where a wrapped exception is thrown
     * from same method as the "causative exception" is caught.  The above
     * example was produced by running the program:
     * 1.请注意包含字符"..."的行的存在。这些行表示此异常的堆栈跟踪的其余部分
     * 与由此异常（“封闭”异常）引起的异常的堆栈跟踪底部的指示帧数相匹配
     * 2.在从与捕获“原因异常”相同的方法抛出包装异常的常见情况下，这种速记可以大大减少输出的长度
     * <pre>
     * public class Junk {
     *     public static void main(String args[]) {
     *         try {
     *             a();
     *         } catch(HighLevelException e) {
     *             e.printStackTrace();
     *         }
     *     }
     *     static void a() throws HighLevelException {
     *         try {
     *             b();
     *         } catch(MidLevelException e) {
     *             throw new HighLevelException(e);
     *         }
     *     }
     *     static void b() throws MidLevelException {
     *         c();
     *     }
     *     static void c() throws MidLevelException {
     *         try {
     *             d();
     *         } catch(LowLevelException e) {
     *             throw new MidLevelException(e);
     *         }
     *     }
     *     static void d() throws LowLevelException {
     *        e();
     *     }
     *     static void e() throws LowLevelException {
     *         throw new LowLevelException();
     *     }
     * }
     *
     * class HighLevelException extends Exception {
     *     HighLevelException(Throwable cause) { super(cause); }
     * }
     *
     * class MidLevelException extends Exception {
     *     MidLevelException(Throwable cause)  { super(cause); }
     * }
     *
     * class LowLevelException extends Exception {
     * }
     * </pre>
     * As of release 7, the platform supports the notion of
     * <i>suppressed exceptions</i> (in conjunction with the {@code
     * try}-with-resources statement). Any exceptions that were
     * suppressed in order to deliver an exception are printed out
     * beneath the stack trace.  The format of this information
     * depends on the implementation, but the following example may be
     * regarded as typical:
     * 1.从第 7 版开始，该平台支持抑制异常的概念（与  try-with-resources 语句结合使用）
     * <pre>
     * Exception in thread "main" java.lang.Exception: Something happened
     *  at Foo.bar(Foo.java:10)
     *  at Foo.main(Foo.java:5)
     *  Suppressed: Resource$CloseFailException: Resource ID = 0
     *          at Resource.close(Resource.java:26)
     *          at Foo.bar(Foo.java:9)
     *          ... 1 more
     * </pre>
     * Note that the "... n more" notation is used on suppressed exceptions
     * just at it is used on causes. Unlike causes, suppressed exceptions are
     * indented beyond their "containing exceptions."
     * 1.请注意，“... n more”符号用于抑制的异常，只是用于原因。与原因不同，被抑制的异常缩进超出了它们的“包含异常”。
     * <p>An exception can have both a cause and one or more suppressed
     * exceptions:
     * 2.一个异常可以有一个原因和一个或多个被抑制的异常：
     * <pre>
     * Exception in thread "main" java.lang.Exception: Main block
     *  at Foo3.main(Foo3.java:7)
     *  Suppressed: Resource$CloseFailException: Resource ID = 2
     *          at Resource.close(Resource.java:26)
     *          at Foo3.main(Foo3.java:5)
     *  Suppressed: Resource$CloseFailException: Resource ID = 1
     *          at Resource.close(Resource.java:26)
     *          at Foo3.main(Foo3.java:5)
     * Caused by: java.lang.Exception: I did it
     *  at Foo3.main(Foo3.java:8)
     * </pre>
     * Likewise, a suppressed exception can have a cause:
     * <pre>
     * Exception in thread "main" java.lang.Exception: Main block
     *  at Foo4.main(Foo4.java:6)
     *  Suppressed: Resource2$CloseFailException: Resource ID = 1
     *          at Resource2.close(Resource2.java:20)
     *          at Foo4.main(Foo4.java:5)
     *  Caused by: java.lang.Exception: Rats, you caught me
     *          at Resource2$CloseFailException.&lt;init&gt;(Resource2.java:45)
     *          ... 2 more
     * </pre>
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints this throwable and its backtrace to the specified print stream.
     * 将此 throwable 及其回溯打印到指定的打印流
     * @param s {@code PrintStream} to use for output
     */
    public void printStackTrace(PrintStream s) {
        printStackTrace(new WrappedPrintStream(s));
    }

    private void printStackTrace(PrintStreamOrWriter s) {
        // Guard against malicious overrides of Throwable.equals by
        // using a Set with identity equality semantics.
        //通过使用具有身份相等语义的 Set 来防止 Throwable.equals 的恶意覆盖
        Set<Throwable> dejaVu =
            Collections.newSetFromMap(new IdentityHashMap<Throwable, Boolean>());
        dejaVu.add(this);

        synchronized (s.lock()) {
            // Print our stack trace
            s.println(this);
            StackTraceElement[] trace = getOurStackTrace();
            for (StackTraceElement traceElement : trace)
                s.println("\tat " + traceElement);

            // Print suppressed exceptions, if any
            for (Throwable se : getSuppressed())
                se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION, "\t", dejaVu);

            // Print cause, if any
            Throwable ourCause = getCause();
            if (ourCause != null)
                ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, "", dejaVu);
        }
    }

    /**
     * Print our stack trace as an enclosed exception for the specified
     * stack trace.
     * 将我们的堆栈跟踪打印为指定堆栈跟踪的封闭异常。
     */
    private void printEnclosedStackTrace(PrintStreamOrWriter s,
                                         StackTraceElement[] enclosingTrace,
                                         String caption,
                                         String prefix,
                                         Set<Throwable> dejaVu) {
        assert Thread.holdsLock(s.lock());
        if (dejaVu.contains(this)) {
            s.println("\t[CIRCULAR REFERENCE:" + this + "]");
        } else {
            dejaVu.add(this);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = getOurStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >=0 && trace[m].equals(enclosingTrace[n])) {
                m--; n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // Print our stack trace
            s.println(prefix + caption + this);
            for (int i = 0; i <= m; i++)
                s.println(prefix + "\tat " + trace[i]);
            if (framesInCommon != 0)
                s.println(prefix + "\t... " + framesInCommon + " more");

            // Print suppressed exceptions, if any
            for (Throwable se : getSuppressed())
                se.printEnclosedStackTrace(s, trace, SUPPRESSED_CAPTION,
                                           prefix +"\t", dejaVu);

            // Print cause, if any
            Throwable ourCause = getCause();
            if (ourCause != null)
                ourCause.printEnclosedStackTrace(s, trace, CAUSE_CAPTION, prefix, dejaVu);
        }
    }

    /**
     * Prints this throwable and its backtrace to the specified
     * print writer.
     *
     * @param s {@code PrintWriter} to use for output
     * @since   JDK1.1
     */
    public void printStackTrace(PrintWriter s) {
        printStackTrace(new WrappedPrintWriter(s));
    }

    /**
     * Wrapper class for PrintStream and PrintWriter to enable a single
     * implementation of printStackTrace.
     * PrintStream 和 PrintWriter 的包装类以启用 printStackTrace 的单个实现
     */
    private abstract static class PrintStreamOrWriter {
        /** Returns the object to be locked when using this StreamOrWriter */
        //返回使用此 StreamOrWriter 时要锁定的对象
        abstract Object lock();

        /** Prints the specified string as a line on this StreamOrWriter */
        //在此 StreamOrWriter 上将指定的字符串打印为一行
        abstract void println(Object o);
    }

    private static class WrappedPrintStream extends PrintStreamOrWriter {
        private final PrintStream printStream;

        WrappedPrintStream(PrintStream printStream) {
            this.printStream = printStream;
        }

        Object lock() {
            return printStream;
        }

        void println(Object o) {
            printStream.println(o);
        }
    }

    private static class WrappedPrintWriter extends PrintStreamOrWriter {
        private final PrintWriter printWriter;

        WrappedPrintWriter(PrintWriter printWriter) {
            this.printWriter = printWriter;
        }

        Object lock() {
            return printWriter;
        }

        void println(Object o) {
            printWriter.println(o);
        }
    }

    /**
     * Fills in the execution stack trace. This method records within this
     * {@code Throwable} object information about the current state of
     * the stack frames for the current thread.
     * 1.填充执行堆栈跟踪。此方法在此 Throwable对象中记录有关当前线程的堆栈帧的当前状态的信息
     * <p>If the stack trace of this {@code Throwable} {@linkplain
     * Throwable#Throwable(String, Throwable, boolean, boolean) is not
     * writable}, calling this method has no effect.
     * 2.如果这个Throwable的堆栈跟踪Throwable(String, Throwable, boolean, boolean) 不可写，
     * 调用这个方法没有效果
     * @return  a reference to this {@code Throwable} instance.
     * @see     java.lang.Throwable#printStackTrace()
     */
    public synchronized Throwable fillInStackTrace() {
        if (stackTrace != null ||
            backtrace != null /* Out of protocol state  超出协议状态*/ ) {
            fillInStackTrace(0);
            stackTrace = UNASSIGNED_STACK;
        }
        return this;
    }

    private native Throwable fillInStackTrace(int dummy);

    /**
     * Provides programmatic access to the stack trace information printed by
     * {@link #printStackTrace()}.  Returns an array of stack trace elements,
     * each representing one stack frame.  The zeroth element of the array
     * (assuming the array's length is non-zero) represents the top of the
     * stack, which is the last method invocation in the sequence.  Typically,
     * this is the point at which this throwable was created and thrown.
     * The last element of the array (assuming the array's length is non-zero)
     * represents the bottom of the stack, which is the first method invocation
     * in the sequence.
     * 1.提供对 printStackTrace()打印的堆栈跟踪信息的编程访问
     * 2.返回一组堆栈跟踪元素，每个元素代表一个堆栈帧
     * 3.数组的第零个元素（假设数组的长度不为零）表示堆栈的顶部，这是序列中的最后一个方法调用
     * 4.通常，这是创建和抛出此 throwable 的点。数组的最后一个元素（假设数组的长度不为零）表示堆栈的底部，
     * 这是序列中的第一个方法调用。
     * <p>Some virtual machines may, under some circumstances, omit one
     * or more stack frames from the stack trace.  In the extreme case,
     * a virtual machine that has no stack trace information concerning
     * this throwable is permitted to return a zero-length array from this
     * method.  Generally speaking, the array returned by this method will
     * contain one element for every frame that would be printed by
     * {@code printStackTrace}.  Writes to the returned array do not
     * affect future calls to this method.
     * 5.在某些情况下，某些虚拟机可能会从堆栈跟踪中省略一个或多个堆栈帧
     * 6.在极端情况下，允许没有与此 throwable 有关的堆栈跟踪信息的虚拟机从此方法返回零长度数组
     * 7.一般而言，此方法返回的数组将包含每个由 printStackTrace打印的帧的元素。写入返回的数组不会影响以后对此方法的调用
     * @return an array of stack trace elements representing the stack trace
     *         pertaining to this throwable.
     * @since  1.4
     */
    public StackTraceElement[] getStackTrace() {
        return getOurStackTrace().clone();
    }

    private synchronized StackTraceElement[] getOurStackTrace() {
        // Initialize stack trace field with information from
        // backtrace if this is the first call to this method
        //如果这是对该方法的第一次调用，则使用来自回溯的信息初始化堆栈跟踪字段
        if (stackTrace == UNASSIGNED_STACK ||
            (stackTrace == null && backtrace != null) /* Out of protocol state */) {
            int depth = getStackTraceDepth();
            stackTrace = new StackTraceElement[depth];
            for (int i=0; i < depth; i++)
                stackTrace[i] = getStackTraceElement(i);
        } else if (stackTrace == null) {
            return UNASSIGNED_STACK;
        }
        return stackTrace;
    }

    /**
     * Sets the stack trace elements that will be returned by
     * {@link #getStackTrace()} and printed by {@link #printStackTrace()}
     * and related methods.
     * 1.设置将由 getStackTrace()返回并由 printStackTrace()和相关方法打印的堆栈跟踪元素
     * This method, which is designed for use by RPC frameworks and other
     * advanced systems, allows the client to override the default
     * stack trace that is either generated by {@link #fillInStackTrace()}
     * when a throwable is constructed or deserialized when a throwable is
     * read from a serialization stream.
     * 2.此方法是为 RPC 框架和其他高级系统设计的，允许客户端覆盖默认堆栈跟踪，
     * 该堆栈跟踪由 fillInStackTrace()在构造 throwable 时生成或在从 throwable 读取时反序列化序列化流
     *
     * <p>If the stack trace of this {@code Throwable} {@linkplain
     * Throwable#Throwable(String, Throwable, boolean, boolean) is not
     * writable}, calling this method has no effect other than
     * validating its argument.
     * 3.<p>如果此 Throwable(String, Throwable, boolean, boolean) 的堆栈跟踪不可写，
     * 则调用此方法除了验证其参数外没有任何效果
     * @param   stackTrace the stack trace elements to be associated with
     * this {@code Throwable}.  The specified array is copied by this
     * call; changes in the specified array after the method invocation
     * returns will have no affect on this {@code Throwable}'s stack
     * trace.
     *
     * @throws NullPointerException if {@code stackTrace} is
     *         {@code null} or if any of the elements of
     *         {@code stackTrace} are {@code null}
     *
     * @since  1.4
     */
    public void setStackTrace(StackTraceElement[] stackTrace) {
        // Validate argument
        StackTraceElement[] defensiveCopy = stackTrace.clone();
        for (int i = 0; i < defensiveCopy.length; i++) {
            if (defensiveCopy[i] == null)
                throw new NullPointerException("stackTrace[" + i + "]");
        }

        synchronized (this) {
            if (this.stackTrace == null && // Immutable stack
                backtrace == null) // Test for out of protocol state
                return;
            this.stackTrace = defensiveCopy;
        }
    }

    /**
     * Returns the number of elements in the stack trace (or 0 if the stack
     * trace is unavailable).
     * 返回堆栈跟踪中的元素数（如果堆栈跟踪不可用，则返回 0）
     * package-protection for use by SharedSecrets.
     */
    native int getStackTraceDepth();

    /**
     * Returns the specified element of the stack trace.
     * 返回堆栈跟踪的指定元素
     * package-protection for use by SharedSecrets.
     * SharedSecrets 使用的包保护。
     * @param index index of the element to return.
     * @throws IndexOutOfBoundsException if {@code index < 0 ||
     *         index >= getStackTraceDepth() }
     */
    native StackTraceElement getStackTraceElement(int index);

    /**
     * Reads a {@code Throwable} from a stream, enforcing
     * well-formedness constraints on fields.  Null entries and
     * self-pointers are not allowed in the list of {@code
     * suppressedExceptions}.  Null entries are not allowed for stack
     * trace elements.  A null stack trace in the serial form results
     * in a zero-length stack element array. A single-element stack
     * trace whose entry is equal to {@code new StackTraceElement("",
     * "", null, Integer.MIN_VALUE)} results in a {@code null} {@code
     * stackTrace} field.
     * 1.从流中读取Throwable，对字段强制执行格式良好的约束。
     * 2.空条目和自指针在suppressedExceptions列表中是不允许的,堆栈跟踪元素不允许使用空条目。
     * 3.串行形式的空堆栈跟踪导致零长度堆栈元素数组。条目等于new StackTraceElement("", "", null, Integer.MIN_VALUE)
     * 的单元素堆栈跟踪会导致 null stackTrace字段
     * Note that there are no constraints on the value the {@code
     * cause} field can hold; both {@code null} and {@code this} are
     * valid values for the field.
     * 4.请注意，cause字段可以容纳的值没有限制； null和  this都是该字段的有效值。
     */
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException {
        s.defaultReadObject();     // read in all fields
        if (suppressedExceptions != null) {
            List<Throwable> suppressed = null;
            if (suppressedExceptions.isEmpty()) {
                // Use the sentinel for a zero-length list
                suppressed = SUPPRESSED_SENTINEL;
            } else { // Copy Throwables to new list
                suppressed = new ArrayList<>(1);
                for (Throwable t : suppressedExceptions) {
                    // Enforce constraints on suppressed exceptions in
                    // case of corrupt or malicious stream.
                    if (t == null)
                        throw new NullPointerException(NULL_CAUSE_MESSAGE);
                    if (t == this)
                        throw new IllegalArgumentException(SELF_SUPPRESSION_MESSAGE);
                    suppressed.add(t);
                }
            }
            suppressedExceptions = suppressed;
        } // else a null suppressedExceptions field remains null
        //否则空的抑制异常字段仍然为空

        /*
         * For zero-length stack traces, use a clone of
         * UNASSIGNED_STACK rather than UNASSIGNED_STACK itself to
         * allow identity comparison against UNASSIGNED_STACK in
         * getOurStackTrace.  The identity of UNASSIGNED_STACK in
         * stackTrace indicates to the getOurStackTrace method that
         * the stackTrace needs to be constructed from the information
         * in backtrace.
         * 1.对于零长度堆栈跟踪，使用 UNASSIGNED_STACK 的克隆而不是 UNASSIGNED_STACK 本身
         * 以允许在 getOurStackTrace 中与 UNASSIGNED_STACK 进行身份比较
         */
        if (stackTrace != null) {
            if (stackTrace.length == 0) {
                stackTrace = UNASSIGNED_STACK.clone();
            }  else if (stackTrace.length == 1 &&
                        // Check for the marker of an immutable stack trace
                     //检查不可变堆栈跟踪的标记
                        SentinelHolder.STACK_TRACE_ELEMENT_SENTINEL.equals(stackTrace[0])) {
                stackTrace = null;
            } else { // Verify stack trace elements are non-null.
                //验证堆栈跟踪元素是否为非空。
                for(StackTraceElement ste : stackTrace) {
                    if (ste == null)
                        throw new NullPointerException("null StackTraceElement in serial stream. ");
                }
            }
        } else {
            // A null stackTrace field in the serial form can result
            // from an exception serialized without that field in
            // older JDK releases; treat such exceptions as having
            // empty stack traces.
            //序列形式中的空 stackTrace 字段可能是由于在较旧的 JDK 版本中没有该字段而序列化的异常；
            // 将此类异常视为具有空堆栈跟踪
            stackTrace = UNASSIGNED_STACK.clone();
        }
    }

    /**
     * Write a {@code Throwable} object to a stream.
     * 1.将 Throwable对象写入流。
     * A {@code null} stack trace field is represented in the serial
     * form as a one-element array whose element is equal to {@code
     * new StackTraceElement("", "", null, Integer.MIN_VALUE)}.
     * null堆栈跟踪字段以串行形式表示为一个元素等于
     * new StackTraceElement("", "", null, Integer.MIN_VALUE)的单元素数组。
     */
    private synchronized void writeObject(ObjectOutputStream s)
        throws IOException {
        // Ensure that the stackTrace field is initialized to a
        // non-null value, if appropriate.  As of JDK 7, a null stack
        // trace field is a valid value indicating the stack trace
        // should not be set.
        //如果合适，请确保将 stackTrace 字段初始化为非空值。
        //从 JDK 7 开始，空堆栈跟踪字段是一个有效值，指示不应设置堆栈跟踪
        getOurStackTrace();

        StackTraceElement[] oldStackTrace = stackTrace;
        try {
            if (stackTrace == null)
                stackTrace = SentinelHolder.STACK_TRACE_SENTINEL;
            s.defaultWriteObject();
        } finally {
            stackTrace = oldStackTrace;
        }
    }

    /**
     * Appends the specified exception to the exceptions that were
     * suppressed in order to deliver this exception. This method is
     * thread-safe and typically called (automatically and implicitly)
     * by the {@code try}-with-resources statement.
     * 1.将指定的异常附加到为传递此异常而被抑制的异常。此方法是线程安全的，
     * 通常由 try-with-resources 语句调用（自动和隐式）
     * <p>The suppression behavior is enabled <em>unless</em> disabled
     * {@linkplain #Throwable(String, Throwable, boolean, boolean) via
     * a constructor}.  When suppression is disabled, this method does
     * nothing other than to validate its argument.
     * 2.通过构造函数启用抑制行为除非禁用 Throwable(String, Throwable, boolean, boolean)。
     * 当抑制被禁用时，这个方法除了验证它的参数之外什么都不做
     * <p>Note that when one exception {@linkplain
     * #initCause(Throwable) causes} another exception, the first
     * exception is usually caught and then the second exception is
     * thrown in response.  In other words, there is a causal
     * connection between the two exceptions.
     * 3.请注意，当一个异常initCause(Throwable) 导致另一个异常时，通常会捕获第一个异常，
     * 然后抛出第二个异常作为响应。换句话说，这两个例外之间存在因果关系
     * In contrast, there are situations where two independent
     * exceptions can be thrown in sibling code blocks, in particular
     * in the {@code try} block of a {@code try}-with-resources
     * statement and the compiler-generated {@code finally} block
     * which closes the resource.
     * 4.相比之下，在兄弟代码块中可能会抛出两个独立的异常，
     * 特别是在  try-with-resources 语句的 try块和编译器生成的 finally关闭资源的块
     * In these situations, only one of the thrown exceptions can be
     * propagated.  In the {@code try}-with-resources statement, when
     * there are two such exceptions, the exception originating from
     * the {@code try} block is propagated and the exception from the
     * {@code finally} block is added to the list of exceptions
     * suppressed by the exception from the {@code try} block.  As an
     * exception unwinds the stack, it can accumulate multiple
     * suppressed exceptions.
     * 5.在这些情况下，只能传播抛出的异常之一。
     * 6.在 try-with-resources 语句中，当有两个这样的异常时，
     * 来自try块的异常被传播，来自 finally 块的异常被添加到被  try块中的异常抑制的异常。
     * 当异常展开堆栈时，它可以累积多个被抑制的异常
     * <p>An exception may have suppressed exceptions while also being
     * caused by another exception.  Whether or not an exception has a
     * cause is semantically known at the time of its creation, unlike
     * whether or not an exception will suppress other exceptions
     * which is typically only determined after an exception is
     * thrown.
     * 7.一个异常可能抑制了异常，同时也由另一个异常引起。
     * 异常是否有原因在其创建时在语义上是已知的，这与异常是否会抑制其他异常不同，后者通常仅在抛出异常后确定。
     * <p>Note that programmer written code is also able to take
     * advantage of calling this method in situations where there are
     * multiple sibling exceptions and only one can be propagated.
     * 8.请注意，在存在多个同级异常并且只能传播一个异常的情况下，程序员编写的代码也可以利用调用此方法
     * @param exception the exception to be added to the list of
     *        suppressed exceptions
     * @throws IllegalArgumentException if {@code exception} is this
     *         throwable; a throwable cannot suppress itself.
     * @throws NullPointerException if {@code exception} is {@code null}
     * @since 1.7
     */
    public final synchronized void addSuppressed(Throwable exception) {
        if (exception == this)
            throw new IllegalArgumentException(SELF_SUPPRESSION_MESSAGE, exception);

        if (exception == null)
            throw new NullPointerException(NULL_CAUSE_MESSAGE);

        if (suppressedExceptions == null) // Suppressed exceptions not recorded
            //未记录禁止的异常
            return;

        if (suppressedExceptions == SUPPRESSED_SENTINEL)
            suppressedExceptions = new ArrayList<>(1);

        suppressedExceptions.add(exception);
    }

    //空的可抛数组
    private static final Throwable[] EMPTY_THROWABLE_ARRAY = new Throwable[0];

    /**
     * Returns an array containing all of the exceptions that were
     * suppressed, typically by the {@code try}-with-resources
     * statement, in order to deliver this exception.
     * 1.返回一个包含所有被抑制的异常的数组，通常是通过 try-with-resources 语句来传递这个异常。
     * If no exceptions were suppressed or {@linkplain
     * #Throwable(String, Throwable, boolean, boolean) suppression is
     * disabled}, an empty array is returned.  This method is
     * thread-safe.  Writes to the returned array do not affect future
     * calls to this method.
     * 2.如果没有异常被抑制或 Throwable(String, Throwable, boolean, boolean) 抑制被禁用，则返回一个空数组。
     * 此方法是线程安全的。写入返回的数组不会影响以后对此方法的调用。
     * @return an array containing all of the exceptions that were
     *         suppressed to deliver this exception.
     * @since 1.7
     */
    public final synchronized Throwable[] getSuppressed() {
        if (suppressedExceptions == SUPPRESSED_SENTINEL ||
            suppressedExceptions == null)
            return EMPTY_THROWABLE_ARRAY;
        else
            return suppressedExceptions.toArray(EMPTY_THROWABLE_ARRAY);
    }
}
