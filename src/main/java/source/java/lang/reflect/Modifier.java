/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.lang.reflect;

import java.security.AccessController;
import sun.reflect.LangReflectAccess;
import sun.reflect.ReflectionFactory;

/**
 * The Modifier class provides {@code static} methods and
 * constants to decode class and member access modifiers.  The sets of
 * modifiers are represented as integers with distinct bit positions
 * representing different modifiers.  The values for the constants
 * representing the modifiers are taken from the tables in sections 4.1, 4.4, 4.5, and 4.7 of
 * <cite>The Java&trade; Virtual Machine Specification</cite>.
 *
 * @see Class#getModifiers()
 * @see Member#getModifiers()
 *
 * @author Nakul Saraiya
 * @author Kenneth Russell
 */
public class Modifier {

    /*
     * Bootstrapping protocol between java.lang and java.lang.reflect
     *  packages
     * java.lang 和 java.lang.reflect 包之间的引导协议
     */
    static {
        sun.reflect.ReflectionFactory factory =
            AccessController.doPrivileged(
                new ReflectionFactory.GetReflectionFactoryAction());
        factory.setLangReflectAccess(new java.lang.reflect.ReflectAccess());
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code public} modifier, {@code false} otherwise.
     * 如果整数参数包含public修饰符，则返回true，否则返回false
     *
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code public} modifier; {@code false} otherwise.
     */
    public static boolean isPublic(int mod) {
        return (mod & PUBLIC) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code private} modifier, {@code false} otherwise.
     * 如果整数参数包含private修饰符，则返回true，否则返回false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code private} modifier; {@code false} otherwise.
     */
    public static boolean isPrivate(int mod) {
        return (mod & PRIVATE) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code protected} modifier, {@code false} otherwise.
     * 如果整数参数包含protected修饰符，则返回true，否则返回false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code protected} modifier; {@code false} otherwise.
     */
    public static boolean isProtected(int mod) {
        return (mod & PROTECTED) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code static} modifier, {@code false} otherwise.
     * 如果整数参数包含static修饰符，则返回true，否则返回false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code static} modifier; {@code false} otherwise.
     */
    public static boolean isStatic(int mod) {
        return (mod & STATIC) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code final} modifier, {@code false} otherwise.
     * 如果整数参数包含final修饰符，则返回true，否则返回 false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code final} modifier; {@code false} otherwise.
     */
    public static boolean isFinal(int mod) {
        return (mod & FINAL) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code synchronized} modifier, {@code false} otherwise.
     * 如果整数参数包含synchronized修饰符，则返回true，否则返回false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code synchronized} modifier; {@code false} otherwise.
     */
    public static boolean isSynchronized(int mod) {
        return (mod & SYNCHRONIZED) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code volatile} modifier, {@code false} otherwise.
     * 如果整数参数包含volatile修饰符，则返回true，否则返回 false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code volatile} modifier; {@code false} otherwise.
     */
    public static boolean isVolatile(int mod) {
        return (mod & VOLATILE) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code transient} modifier, {@code false} otherwise.
     * 如果整数参数包含Transient修饰符，则返回true，否则返回false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code transient} modifier; {@code false} otherwise.
     */
    public static boolean isTransient(int mod) {
        return (mod & TRANSIENT) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code native} modifier, {@code false} otherwise.
     * 如果整数参数包含native修饰符，则返回 true，否则返回 false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code native} modifier; {@code false} otherwise.
     */
    public static boolean isNative(int mod) {
        return (mod & NATIVE) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code interface} modifier, {@code false} otherwise.
     * 如果整数参数包含 interface修饰符，则返回 true，否则返回 false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code interface} modifier; {@code false} otherwise.
     */
    public static boolean isInterface(int mod) {
        return (mod & INTERFACE) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code abstract} modifier, {@code false} otherwise.
     * 如果整数参数包含 abstract修饰符，则返回 true，否则返回 false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code abstract} modifier; {@code false} otherwise.
     */
    public static boolean isAbstract(int mod) {
        return (mod & ABSTRACT) != 0;
    }

    /**
     * Return {@code true} if the integer argument includes the
     * {@code strictfp} modifier, {@code false} otherwise.
     * 如果整数参数包含strictfp修饰符，则返回true，否则返回false
     * @param   mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code strictfp} modifier; {@code false} otherwise.
     */
    public static boolean isStrict(int mod) {
        return (mod & STRICT) != 0;
    }

    /**
     * Return a string describing the access modifier flags in
     * the specified modifier.
     * 1.返回描述指定修饰符中的访问修饰符标志的字符串
     * For example:
     * <blockquote><pre>
     *    public final synchronized strictfp
     * </pre></blockquote>
     * The modifier names are returned in an order consistent with the
     * suggested modifier orderings given in sections 8.1.1, 8.3.1, 8.4.3, 8.8.3, and 9.1.1 of
     * <cite>The Java&trade; Language Specification</cite>.
     * 2.修饰符名称的返回顺序与Java™ 语言规范<cite> 的第 8.1.1、8.3.1、8.4.3、8.8.3 和
     * 9.1.1 节中给出的建议修饰符顺序一致
     * 3.此方法使用的完整修饰符排序是：
     * public protected private abstract static final Transient volatile synchronized native strictfp interface
     * The full modifier ordering used by this method is:
     * <blockquote> {@code
     * public protected private abstract static final transient
     * volatile synchronized native strictfp
     * interface } </blockquote>
     * The {@code interface} modifier discussed in this class is
     * not a true modifier in the Java language and it appears after
     * all other modifiers listed by this method.  This method may
     * return a string of modifiers that are not valid modifiers of a
     * Java entity; in other words, no checking is done on the
     * possible validity of the combination of modifiers represented
     * by the input.
     * 4.此类中讨论的interface修饰符在 Java 语言中并不是真正的修饰符，它出现在此方法列出的所有其他修饰符之后。
     * 此方法可能返回一串修饰符，这些修饰符不是 Java 实体的有效修饰符；换句话说，不对输入所表示的修饰符组合的可能有效性进行检查。
     * Note that to perform such checking for a known kind of entity,
     * such as a constructor or method, first AND the argument of
     * {@code toString} with the appropriate mask from a method like
     * {@link #constructorModifiers} or {@link #methodModifiers}.
     * 5.请注意，要对已知类型的实体（例如构造函数或方法）执行此类检查，首先将toString的参数与来自
     * constructorModifiers或 methodModifiers等方法的适当掩码进行 AND 运算
     * @param   mod a set of modifiers
     * @return  a string representation of the set of modifiers
     * represented by {@code mod}
     */
    public static String toString(int mod) {
        StringBuilder sb = new StringBuilder();
        int len;

        if ((mod & PUBLIC) != 0)        sb.append("public ");
        if ((mod & PROTECTED) != 0)     sb.append("protected ");
        if ((mod & PRIVATE) != 0)       sb.append("private ");

        /* Canonical order */
        if ((mod & ABSTRACT) != 0)      sb.append("abstract ");
        if ((mod & STATIC) != 0)        sb.append("static ");
        if ((mod & FINAL) != 0)         sb.append("final ");
        if ((mod & TRANSIENT) != 0)     sb.append("transient ");
        if ((mod & VOLATILE) != 0)      sb.append("volatile ");
        if ((mod & SYNCHRONIZED) != 0)  sb.append("synchronized ");
        if ((mod & NATIVE) != 0)        sb.append("native ");
        if ((mod & STRICT) != 0)        sb.append("strictfp ");
        if ((mod & INTERFACE) != 0)     sb.append("interface ");

        if ((len = sb.length()) > 0)    /* trim trailing space */
            return sb.toString().substring(0, len-1);
        return "";
    }

    /*
     * Access modifier flag constants from tables 4.1, 4.4, 4.5, and 4.7 of
     * <cite>The Java&trade; Virtual Machine Specification</cite>
     * 来自The Java™ Virtual Machine Specification表 4.1、4.4、4.5 和 4.7 的访问修饰符标志常量
     */

    /**
     * The {@code int} value representing the {@code public}
     * modifier.
     * 表示public修饰符的int值。
     */
    public static final int PUBLIC           = 0x00000001;

    /**
     * The {@code int} value representing the {@code private}
     * modifier.
     * 表示private修饰符的int值。
     */
    public static final int PRIVATE          = 0x00000002;

    /**
     * The {@code int} value representing the {@code protected}
     * modifier.
     * 表示protected修饰符的int值
     */
    public static final int PROTECTED        = 0x00000004;

    /**
     * The {@code int} value representing the {@code static}
     * modifier.
     * 表示static修饰符的int值。
     */
    public static final int STATIC           = 0x00000008;

    /**
     * The {@code int} value representing the {@code final}
     * modifier.
     * 表示final修饰符的int值
     */
    public static final int FINAL            = 0x00000010;

    /**
     * The {@code int} value representing the {@code synchronized}
     * modifier.
     * int值表示synchronized修饰符
     */
    public static final int SYNCHRONIZED     = 0x00000020;

    /**
     * The {@code int} value representing the {@code volatile}
     * modifier.
     * 表示volatile修饰符的int值
     */
    public static final int VOLATILE         = 0x00000040;

    /**
     * The {@code int} value representing the {@code transient}
     * modifier.
     * int值表示Transient修饰符。
     */
    public static final int TRANSIENT        = 0x00000080;

    /**
     * The {@code int} value representing the {@code native}
     * modifier.
     * 表示 native修饰符的int值
     */
    public static final int NATIVE           = 0x00000100;

    /**
     * The {@code int} value representing the {@code interface}
     * modifier.
     * int值表示interface修饰符
     */
    public static final int INTERFACE        = 0x00000200;

    /**
     * The {@code int} value representing the {@code abstract}
     * modifier.
     * 表示abstract修饰符的int值。
     */
    public static final int ABSTRACT         = 0x00000400;

    /**
     * The {@code int} value representing the {@code strictfp}
     * modifier.
     * 表示strictfp修饰符的int值
     */
    public static final int STRICT           = 0x00000800;

    // Bits not (yet) exposed in the public API either because they
    // have different meanings for fields and methods and there is no
    // way to distinguish between the two in this class, or because
    // they are not Java programming language keywords
    //未（尚未）在公共 API 中公开的位要么是因为它们对字段和方法
    // 具有不同的含义并且在此类中无法区分这两者，要么是因为它们不是 Java 编程语言关键字
    static final int BRIDGE    = 0x00000040;
    static final int VARARGS   = 0x00000080;
    static final int SYNTHETIC = 0x00001000;
    static final int ANNOTATION  = 0x00002000;
    static final int ENUM      = 0x00004000;
    static final int MANDATED  = 0x00008000;
    static boolean isSynthetic(int mod) {
      return (mod & SYNTHETIC) != 0;
    }

    static boolean isMandated(int mod) {
      return (mod & MANDATED) != 0;
    }

    // Note on the FOO_MODIFIERS fields and fooModifiers() methods:
    // the sets of modifiers are not guaranteed to be constants
    // across time and Java SE releases. Therefore, it would not be
    // appropriate to expose an external interface to this information
    // that would allow the values to be treated as Java-level
    // constants since the values could be constant folded and updates
    // to the sets of modifiers missed. Thus, the fooModifiers()
    // methods return an unchanging values for a given release, but a
    // value that can potentially change over time.
    //注意 FOO_MODIFIERS 字段和 fooModifiers() 方法：
    // 不能保证修饰符集是跨时间和 Java SE 版本的常量。
    // 因此，将允许将值视为 Java 级常量的信息公开外部接口是不合适的，因为这些值可能是常量折叠的，
    // 并且会丢失对修饰符集的更新。因此， fooModifiers() 方法为给定的版本返回一个不变的值，
    // 但一个可能会随着时间而改变的值。

    /**
     * The Java source modifiers that can be applied to a class.
     * @jls 8.1.1 Class Modifiers
     * 可以应用于类的 Java 源代码修饰符。 @jls 8.1.1 类修饰符
     */
    private static final int CLASS_MODIFIERS =
        Modifier.PUBLIC         | Modifier.PROTECTED    | Modifier.PRIVATE |
        Modifier.ABSTRACT       | Modifier.STATIC       | Modifier.FINAL   |
        Modifier.STRICT;

    /**
     * The Java source modifiers that can be applied to an interface.
     * 可以应用于接口的 Java 源代码修饰符。
     * @jls 9.1.1 Interface Modifiers
     */
    private static final int INTERFACE_MODIFIERS =
        Modifier.PUBLIC         | Modifier.PROTECTED    | Modifier.PRIVATE |
        Modifier.ABSTRACT       | Modifier.STATIC       | Modifier.STRICT;


    /**
     * The Java source modifiers that can be applied to a constructor.
     * 可以应用于构造函数的 Java 源代码修饰符
     * @jls 8.8.3 Constructor Modifiers
     */
    private static final int CONSTRUCTOR_MODIFIERS =
        Modifier.PUBLIC         | Modifier.PROTECTED    | Modifier.PRIVATE;

    /**
     * The Java source modifiers that can be applied to a method.
     * 可应用于方法的 Java 源代码修饰符。
     * @jls8.4.3  Method Modifiers
     */
    private static final int METHOD_MODIFIERS =
        Modifier.PUBLIC         | Modifier.PROTECTED    | Modifier.PRIVATE |
        Modifier.ABSTRACT       | Modifier.STATIC       | Modifier.FINAL   |
        Modifier.SYNCHRONIZED   | Modifier.NATIVE       | Modifier.STRICT;

    /**
     * The Java source modifiers that can be applied to a field.
     * 可应用于字段的 Java 源代码修饰符。
     * @jls 8.3.1  Field Modifiers
     */
    private static final int FIELD_MODIFIERS =
        Modifier.PUBLIC         | Modifier.PROTECTED    | Modifier.PRIVATE |
        Modifier.STATIC         | Modifier.FINAL        | Modifier.TRANSIENT |
        Modifier.VOLATILE;

    /**
     * The Java source modifiers that can be applied to a method or constructor parameter.
     * 可应用于方法或构造函数参数的 Java 源代码修饰符。
     * @jls 8.4.1 Formal Parameters
     */
    private static final int PARAMETER_MODIFIERS =
        Modifier.FINAL;

    /**
     *
     */
    static final int ACCESS_MODIFIERS =
        Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

    /**
     * Return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a class.
     * 返回一个int值或将可应用于类的源语言修饰符组合在一起
     * @return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a class.
     *
     * @jls 8.1.1 Class Modifiers
     * @since 1.7
     */
    public static int classModifiers() {
        return CLASS_MODIFIERS;
    }

    /**
     * Return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to an interface.
     * 返回一个int值或将可应用于接口的源语言修饰符组合在一起
     * @return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to an interface.
     *
     * @jls 9.1.1 Interface Modifiers
     * @since 1.7
     */
    public static int interfaceModifiers() {
        return INTERFACE_MODIFIERS;
    }

    /**
     * Return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a constructor.
     * 返回一个int值或将可应用于构造函数的源语言修饰符组合在一起
     * @return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a constructor.
     *
     * @jls 8.8.3 Constructor Modifiers
     * @since 1.7
     */
    public static int constructorModifiers() {
        return CONSTRUCTOR_MODIFIERS;
    }

    /**
     * Return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a method.
     * 返回一个 int值或将可应用于方法的源语言修饰符组合在一起
     * @return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a method.
     *
     * @jls 8.4.3 Method Modifiers
     * @since 1.7
     */
    public static int methodModifiers() {
        return METHOD_MODIFIERS;
    }

    /**
     * Return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a field.
     * 返回一个int值或将可应用于字段的源语言修饰符组合在一起
     * @return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a field.
     *
     * @jls 8.3.1 Field Modifiers
     * @since 1.7
     */
    public static int fieldModifiers() {
        return FIELD_MODIFIERS;
    }

    /**
     * Return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a parameter.
     * 返回一个 int值或将可应用于参数的源语言修饰符组合在一起。
     * @return an {@code int} value OR-ing together the source language
     * modifiers that can be applied to a parameter.
     *
     * @jls 8.4.1 Formal Parameters
     * @since 1.8
     */
    public static int parameterModifiers() {
        return PARAMETER_MODIFIERS;
    }
}
