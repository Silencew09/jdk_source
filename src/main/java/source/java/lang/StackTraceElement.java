/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Objects;

/**
 * An element in a stack trace, as returned by {@link
 * Throwable#getStackTrace()}.  Each element represents a single stack frame.
 * All stack frames except for the one at the top of the stack represent
 * a method invocation.  The frame at the top of the stack represents the
 * execution point at which the stack trace was generated.  Typically,
 * this is the point at which the throwable corresponding to the stack trace
 * was created.
 * 1.堆栈跟踪中的元素，由 Throwable.getStackTrace()返回。每个元素代表一个堆栈帧。
 * 除了堆栈顶部的堆栈帧之外的所有堆栈帧都表示一个方法调用。堆栈顶部的帧表示生成堆栈跟踪的执行点。
 * 通常，这是创建与堆栈跟踪对应的 throwable 的点。
 * @since  1.4
 * @author Josh Bloch
 */
public final class StackTraceElement implements java.io.Serializable {
    // Normally initialized by VM (public constructor added in 1.5)
    //通常由 VM 初始化（1.5 中添加的公共构造函数）
    private String declaringClass;
    private String methodName;
    private String fileName;
    private int    lineNumber;

    /**
     * Creates a stack trace element representing the specified execution
     * point.
     * 创建表示指定执行点的堆栈跟踪元素
     * @param declaringClass the fully qualified name of the class containing
     *        the execution point represented by the stack trace element
     *                       包含堆栈跟踪元素表示的执行点的类的完全限定名称
     * @param methodName the name of the method containing the execution point
     *        represented by the stack trace element
     *                   包含堆栈跟踪元素表示的执行点的方法的名称
     * @param fileName the name of the file containing the execution point
     *         represented by the stack trace element, or {@code null} if
     *         this information is unavailable
     *                 包含由堆栈跟踪元素表示的执行点的文件的名称，如果此信息不可用，则为 {@code null}
     * @param lineNumber the line number of the source line containing the
     *         execution point represented by this stack trace element, or
     *         a negative number if this information is unavailable. A value
     *         of -2 indicates that the method containing the execution point
     *         is a native method
     *                   包含此堆栈跟踪元素表示的执行点的源行的行号，如果此信息不可用，则为负数。值为 -2 表示包含执行点的方法是本机方法
     * @throws NullPointerException if {@code declaringClass} or
     *         {@code methodName} is null
     * @since 1.5
     */
    public StackTraceElement(String declaringClass, String methodName,
                             String fileName, int lineNumber) {
        this.declaringClass = Objects.requireNonNull(declaringClass, "Declaring class is null");
        this.methodName     = Objects.requireNonNull(methodName, "Method name is null");
        this.fileName       = fileName;
        this.lineNumber     = lineNumber;
    }

    /**
     * Returns the name of the source file containing the execution point
     * represented by this stack trace element.  Generally, this corresponds
     * to the {@code SourceFile} attribute of the relevant {@code class}
     * file (as per <i>The Java Virtual Machine Specification</i>, Section
     * 4.7.7).  In some systems, the name may refer to some source code unit
     * other than a file, such as an entry in source repository.
     * 返回包含此堆栈跟踪元素表示的执行点的源文件的名称。通常，这对应于相关 class文件的 SourceFile属性
     * （根据 Java 虚拟机规范，第 4.7.7 节）。在某些系统中，该名称可能指代某个源代码单元而不是文件，例如源存储库中的条目
     * @return the name of the file containing the execution point
     *         represented by this stack trace element, or {@code null} if
     *         this information is unavailable.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the line number of the source line containing the execution
     * point represented by this stack trace element.  Generally, this is
     * derived from the {@code LineNumberTable} attribute of the relevant
     * {@code class} file (as per <i>The Java Virtual Machine
     * Specification</i>, Section 4.7.8).
     * 2.返回包含此堆栈跟踪元素表示的执行点的源行的行号。通常，这是从相关 class文件的LineNumberTable属性派生的
     * （根据 Java 虚拟机规范，第 4.7.8 节）
     * @return the line number of the source line containing the execution
     *         point represented by this stack trace element, or a negative
     *         number if this information is unavailable.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Returns the fully qualified name of the class containing the
     * execution point represented by this stack trace element.
     * 返回包含此堆栈跟踪元素表示的执行点的类的完全限定名称
     * @return the fully qualified name of the {@code Class} containing
     *         the execution point represented by this stack trace element.
     */
    public String getClassName() {
        return declaringClass;
    }

    /**
     * Returns the name of the method containing the execution point
     * represented by this stack trace element.  If the execution point is
     * contained in an instance or class initializer, this method will return
     * the appropriate <i>special method name</i>, {@code <init>} or
     * {@code <clinit>}, as per Section 3.9 of <i>The Java Virtual
     * Machine Specification</i>.
     * 返回包含此堆栈跟踪元素表示的执行点的方法的名称。如果执行点包含在实例或类初始值设定项中，
     * 则此方法将返回适当的特殊方法名称、<init>或 <clinit>，如第 3.9 节Java 虚拟机规范。
     * @return the name of the method containing the execution point
     *         represented by this stack trace element.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Returns true if the method containing the execution point
     * represented by this stack trace element is a native method.
     * 如果包含此堆栈跟踪元素表示的执行点的方法是本机方法，则返回 true
     * @return {@code true} if the method containing the execution point
     *         represented by this stack trace element is a native method.
     */
    public boolean isNativeMethod() {
        return lineNumber == -2;
    }

    /**
     * Returns a string representation of this stack trace element.  The
     * format of this string depends on the implementation, but the following
     * examples may be regarded as typical:
     * 1.返回此堆栈跟踪元素的字符串表示形式。此字符串的格式取决于实现，但以下示例可能被视为典型：
     *
     * <ul>
     * <li>
     *   {@code "MyClass.mash(MyClass.java:9)"} - Here, {@code "MyClass"}
     *   is the <i>fully-qualified name</i> of the class containing the
     *   execution point represented by this stack trace element,
     *   {@code "mash"} is the name of the method containing the execution
     *   point, {@code "MyClass.java"} is the source file containing the
     *   execution point, and {@code "9"} is the line number of the source
     *   line containing the execution point.
     *   1) "MyClass.mash(MyClass.java:9)"
     *   - 此处， "MyClass"是包含此堆栈跟踪表示的执行点的类的完全限定名称元素，
     *    "mash"是包含执行点的方法名称， "MyClass.java"是包含执行点的源文件， "9"是行号包含执行点的源代码行。
     * <li>
     *   {@code "MyClass.mash(MyClass.java)"} - As above, but the line
     *   number is unavailable.
     *   2)"MyClass.mash(MyClass.java)"} - 同上，但行号不可用。
     * <li>
     *   {@code "MyClass.mash(Unknown Source)"} - As above, but neither
     *   the file name nor the line  number are available.
     *   3)"MyClass.mash(Unknown Source)" - 同上，但文件名和行号都不可用。
     * <li>
     *   {@code "MyClass.mash(Native Method)"} - As above, but neither
     *   the file name nor the line  number are available, and the method
     *   containing the execution point is known to be a native method.
     *   4)"MyClass.mash(Native Method)"- 同上，但文件名和行号都不可用，包含执行点的方法已知为本地方法。
     * </ul>
     * @see    Throwable#printStackTrace()
     */
    public String toString() {
        return getClassName() + "." + methodName +
            (isNativeMethod() ? "(Native Method)" :
             (fileName != null && lineNumber >= 0 ?
              "(" + fileName + ":" + lineNumber + ")" :
              (fileName != null ?  "("+fileName+")" : "(Unknown Source)")));
    }

    /**
     * Returns true if the specified object is another
     * {@code StackTraceElement} instance representing the same execution
     * point as this instance.
     * 1.如果指定的对象是另一个StackTraceElement实例，表示与此实例相同的执行点，则返回 true。
     * Two stack trace elements {@code a} and
     * {@code b} are equal if and only if:
     * <pre>{@code
     *     equals(a.getFileName(), b.getFileName()) &&
     *     a.getLineNumber() == b.getLineNumber()) &&
     *     equals(a.getClassName(), b.getClassName()) &&
     *     equals(a.getMethodName(), b.getMethodName())
     * }</pre>
     * where {@code equals} has the semantics of {@link
     * java.util.Objects#equals(Object, Object) Objects.equals}.
     * 2.两个堆栈跟踪元素a和  b相等当且仅当：
     * equals(a.getFileName(), b.getFileName())
     * && a.getLineNumber() == b.getLineNumber())
     * && equals(a.getClassName(), b.getClassName())
     * && equals(a.getMethodName(), b.getMethodName()) }
     * 其中 equals的语义为java.util.Objects.equals(Object, Object)。
     * @param  obj the object to be compared with this stack trace element.
     * @return true if the specified object is another
     *         {@code StackTraceElement} instance representing the same
     *         execution point as this instance.
     */
    public boolean equals(Object obj) {
        if (obj==this)
            return true;
        if (!(obj instanceof StackTraceElement))
            return false;
        StackTraceElement e = (StackTraceElement)obj;
        return e.declaringClass.equals(declaringClass) &&
            e.lineNumber == lineNumber &&
            Objects.equals(methodName, e.methodName) &&
            Objects.equals(fileName, e.fileName);
    }

    /**
     * Returns a hash code value for this stack trace element.
     * //返回此堆栈跟踪元素的哈希码值。
     */
    public int hashCode() {
        int result = 31*declaringClass.hashCode() + methodName.hashCode();
        result = 31*result + Objects.hashCode(fileName);
        result = 31*result + lineNumber;
        return result;
    }

    private static final long serialVersionUID = 6992337162326171013L;
}
