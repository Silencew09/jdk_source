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
 * {@code AnnotatedWildcardType} represents the potentially annotated use of a
 * wildcard type argument, whose upper or lower bounds may themselves represent
 * annotated uses of types.
 *AnnotatedWildcardType表示通配符类型参数的潜在注释使用，其上限或下限本身可能代表类型的注释使用
 * @since 1.8
 */
public interface AnnotatedWildcardType extends AnnotatedType {

    /**
     * Returns the potentially annotated lower bounds of this wildcard type.
     * 返回此通配符类型的潜在注释下限
     * @return the potentially annotated lower bounds of this wildcard type
     */
    AnnotatedType[] getAnnotatedLowerBounds();

    /**
     * Returns the potentially annotated upper bounds of this wildcard type.
     * 返回此通配符类型的潜在注释上限
     * @return the potentially annotated upper bounds of this wildcard type
     */
    AnnotatedType[] getAnnotatedUpperBounds();
}
