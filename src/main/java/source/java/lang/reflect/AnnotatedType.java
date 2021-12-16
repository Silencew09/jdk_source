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

package java.lang.reflect;

/**
 * {@code AnnotatedType} represents the potentially annotated use of a type in
 * the program currently running in this VM. The use may be of any type in the
 * Java programming language, including an array type, a parameterized type, a
 * type variable, or a wildcard type.
 * AnnotatedType表示当前在此 VM 中运行的程序中的类型的潜在注释使用。
 * 使用可以是 Java 编程语言中的任何类型，包括数组类型、参数化类型、类型变量或通配符类型
 * @since 1.8
 */
public interface AnnotatedType extends AnnotatedElement {

    /**
     * Returns the underlying type that this annotated type represents.
     * 返回此注释类型表示的基础类型
     * @return the type this annotated type represents
     */
    public Type getType();
}
