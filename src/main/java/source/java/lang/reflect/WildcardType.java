/*
 * Copyright (c) 2003, 2004, Oracle and/or its affiliates. All rights reserved.
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
 * WildcardType represents a wildcard type expression, such as
 * {@code ?}, {@code ? extends Number}, or {@code ? super Integer}.
 * WildcardType 表示通配符类型表达式，例如?, ? extends Number或 ?super Integer
 * @since 1.5
 */
public interface WildcardType extends Type {
    /**
     * Returns an array of {@code Type} objects representing the  upper
     * bound(s) of this type variable.  Note that if no upper bound is
     * explicitly declared, the upper bound is {@code Object}.
     * 1.返回一个Type对象数组，表示此类型变量的上限。请注意，如果没有明确声明上限，则上限为 Object
     * <p>For each upper bound B :
     * <ul>
     *  <li>if B is a parameterized type or a type variable, it is created,
     *  (see {@link java.lang.reflect.ParameterizedType ParameterizedType}
     *  for the details of the creation process for parameterized types).
     *  <li>Otherwise, B is resolved.
     * </ul>
     * 2.对于每个上界 B :如果 B 是参数化类型或类型变量，则创建它，
     * （创建细节见 java.lang.reflect.ParameterizedType参数化类型的过程）。否则，B 被解析。
     * @return an array of Types representing the upper bound(s) of this
     *     type variable
     * @throws TypeNotPresentException if any of the
     *     bounds refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if any of the
     *     bounds refer to a parameterized type that cannot be instantiated
     *     for any reason
     */
    Type[] getUpperBounds();

    /**
     * Returns an array of {@code Type} objects representing the
     * lower bound(s) of this type variable.  Note that if no lower bound is
     * explicitly declared, the lower bound is the type of {@code null}.
     * In this case, a zero length array is returned.
     * 1.返回表示此类型变量下限的Type对象数组。请注意，如果没有明确声明下限，则下限是null的类型。
     * 在这种情况下，返回零长度数组
     * <p>For each lower bound B :
     * <ul>
     *   <li>if B is a parameterized type or a type variable, it is created,
     *  (see {@link java.lang.reflect.ParameterizedType ParameterizedType}
     *  for the details of the creation process for parameterized types).
     *   <li>Otherwise, B is resolved.
     * </ul>
     * 2.对于每个下界 B : <ul> <li>如果 B 是参数化类型或类型变量，则创建它，
     * （参见 java.lang.reflect.ParameterizedType参数化的创建过程的详细信息类型）。
     * 否则，B 被解析。
     * @return an array of Types representing the lower bound(s) of this
     *     type variable
     * @throws TypeNotPresentException if any of the
     *     bounds refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if any of the
     *     bounds refer to a parameterized type that cannot be instantiated
     *     for any reason
     */
    Type[] getLowerBounds();
    // one or many? Up to language spec; currently only one, but this API
    // allows for generalization. 一个还是多个？达到语言规范；目前只有一个，但这个 API 允许泛化。
}
