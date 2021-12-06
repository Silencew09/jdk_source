/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.*;

/**
 * An informative annotation type used to indicate that an interface
 * type declaration is intended to be a <i>functional interface</i> as
 * defined by the Java Language Specification.
 * 1.用于指示接口类型声明旨在成为 Java 语言规范定义的功能接口的信息性注释类型
 * Conceptually, a functional interface has exactly one abstract
 * method.  Since {@linkplain java.lang.reflect.Method#isDefault()
 * default methods} have an implementation, they are not abstract.  If
 * an interface declares an abstract method overriding one of the
 * public methods of {@code java.lang.Object}, that also does
 * <em>not</em> count toward the interface's abstract method count
 * since any implementation of the interface will have an
 * implementation from {@code java.lang.Object} or elsewhere.
 * 2.从概念上讲，函数式接口只有一个抽象方法。由于 java.lang.reflect.Method.isDefault() 默认方法有一个实现，
 * 它们不是抽象的。如果接口声明了一个抽象方法覆盖了java.lang.Object的公共方法之一，那也不计入接口的抽象方法计数，
 * 因为接口的任何实现都将有一个从java.lang.Object或其他地方实现。
 * <p>Note that instances of functional interfaces can be created with
 * lambda expressions, method references, or constructor references.
 * 3.请注意，可以使用 lambda 表达式、方法引用或构造函数引用创建函数式接口的实例。
 * <p>If a type is annotated with this annotation type, compilers are
 * required to generate an error message unless:
 * 4.如果使用此注解类型对注解类型进行注解，则编译器需要生成错误消息，除非
 * <ul> 1）类型是接口类型，而不是注解类型、枚举或类
 *      2）带注解的类型满足功能接口的要求
 * <li> The type is an interface type and not an annotation type, enum, or class.
 * <li> The annotated type satisfies the requirements of a functional interface.
 * </ul>
 *
 * <p>However, the compiler will treat any interface meeting the
 * definition of a functional interface as a functional interface
 * regardless of whether or not a {@code FunctionalInterface}
 * annotation is present on the interface declaration.
 * 5.但是，无论接口声明中是否存在FunctionalInterface注解，编译器都会将满足函数式接口定义的任何接口视为函数式接口
 * @jls 4.3.2. The Class Object
 * @jls 9.8 Functional Interfaces
 * @jls 9.4.3 Interface Method Body
 * @since 1.8
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FunctionalInterface {}
