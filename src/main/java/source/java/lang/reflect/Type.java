/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * Type is the common superinterface for all types in the Java
 * programming language. These include raw types, parameterized types,
 * array types, type variables and primitive types.
 * Type 是 Java 编程语言中所有类型的通用超级接口。
 * 这些包括原始类型、参数化类型、数组类型、类型变量和原始类型
 * @since 1.5
 */
public interface Type {
    /**
     * Returns a string describing this type, including information
     * about any type parameters.
     * 返回描述此类型的字符串，包括有关任何类型参数的信息
     * @implSpec The default implementation calls {@code toString}.
     *
     * @return a string describing this type
     * @since 1.8
     */
    default String getTypeName() {
        return toString();
    }
}
