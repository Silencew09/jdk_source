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

/**
 * Member is an interface that reflects identifying information about
 * a single member (a field or a method) or a constructor.
 * 成员是反映有关单个成员（字段或方法）或构造函数的标识信息的接口
 * @see java.lang.Class
 * @see Field
 * @see Method
 * @see Constructor
 *
 * @author Nakul Saraiya
 */
public
interface Member {

    /**
     * Identifies the set of all public members of a class or interface,
     * including inherited members.
     * 标识类或接口的所有公共成员的集合，包括继承的成员
     */
    public static final int PUBLIC = 0;

    /**
     * Identifies the set of declared members of a class or interface.
     * Inherited members are not included.
     * 标识类或接口的声明成员集。不包括继承成员
     */
    public static final int DECLARED = 1;

    /**
     * Returns the Class object representing the class or interface
     * that declares the member or constructor represented by this Member.
     * 返回表示类或接口的 Class 对象，该类或接口声明了此 Member 表示的成员或构造函数
     * @return an object representing the declaring class of the
     * underlying member
     */
    public Class<?> getDeclaringClass();

    /**
     * Returns the simple name of the underlying member or constructor
     * represented by this Member.
     * 返回由该成员表示的底层成员或构造函数的简单名称
     * @return the simple name of the underlying member
     */
    public String getName();

    /**
     * Returns the Java language modifiers for the member or
     * constructor represented by this Member, as an integer.  The
     * Modifier class should be used to decode the modifiers in
     * the integer.
     * 以整数形式返回此 Member 表示的成员或构造函数的 Java 语言修饰符。 Modifier 类应该用于解码整数中的修饰符。
     * @return the Java language modifiers for the underlying member
     * @see Modifier
     */
    public int getModifiers();

    /**
     * Returns {@code true} if this member was introduced by
     * the compiler; returns {@code false} otherwise.
     * 如果该成员是由编译器引入的，则返回true；否则返回false。
     * @return true if and only if this member was introduced by
     * the compiler.
     * @jls 13.1 The Form of a Binary
     * @since 1.5
     */
    public boolean isSynthetic();
}
