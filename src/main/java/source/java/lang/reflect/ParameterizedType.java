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
 * ParameterizedType represents a parameterized type such as
 * Collection&lt;String&gt;.
 * 1.ParameterizedType 表示参数化类型，例如 Collection<String>
 * <p>A parameterized type is created the first time it is needed by a
 * reflective method, as specified in this package. When a
 * parameterized type p is created, the generic type declaration that
 * p instantiates is resolved, and all type arguments of p are created
 * recursively. See {@link java.lang.reflect.TypeVariable
 * TypeVariable} for details on the creation process for type
 * variables. Repeated creation of a parameterized type has no effect.
 * 2.参数化类型在反射方法第一次需要时创建，如本包中所指定。创建参数化类型 p 时，
 * 解析 p 实例化的泛型类型声明，并递归创建 p 的所有类型参数。有关类型变量创建过程的详细信息，
 * 请参阅java.lang.reflect.TypeVariable。重复创建参数化类型无效
 * <p>Instances of classes that implement this interface must implement
 * an equals() method that equates any two instances that share the
 * same generic type declaration and have equal type parameters.
 *3.实现此接口的类的实例必须实现一个 equals() 方法，该方法等同于共享相同泛型类型声明并具有相同类型参数的任何两个实例。
 * @since 1.5
 */
public interface ParameterizedType extends Type {
    /**
     * Returns an array of {@code Type} objects representing the actual type
     * arguments to this type.
     * 1.返回一个Type对象数组，表示该类型的实际类型参数。
     * <p>Note that in some cases, the returned array be empty. This can occur
     * if this type represents a non-parameterized type nested within
     * a parameterized type.
     * 2.请注意，在某些情况下，返回的数组为空。如果此类型表示嵌套在参数化类型中的非参数化类型，则会发生这种情况。
     * @return an array of {@code Type} objects representing the actual type
     *     arguments to this type
     * @throws TypeNotPresentException if any of the
     *     actual type arguments refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if any of the
     *     actual type parameters refer to a parameterized type that cannot
     *     be instantiated for any reason
     * @since 1.5
     */
    Type[] getActualTypeArguments();

    /**
     * Returns the {@code Type} object representing the class or interface
     * that declared this type.
     * 1.返回表示声明此类型的类或接口的 Type对象。
     * @return the {@code Type} object representing the class or interface
     *     that declared this type
     * @since 1.5
     */
    Type getRawType();

    /**
     * Returns a {@code Type} object representing the type that this type
     * is a member of.  For example, if this type is {@code O<T>.I<S>},
     * return a representation of {@code O<T>}.
     * 1.返回一个 Type对象，表示该类型所属的类型。例如，如果此类型为O.I}，则返回O的表示
     * <p>If this type is a top-level type, {@code null} is returned.
     * 2.如果此类型是顶级类型，则返回null
     * @return a {@code Type} object representing the type that
     *     this type is a member of. If this type is a top-level type,
     *     {@code null} is returned
     * @throws TypeNotPresentException if the owner type
     *     refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if the owner type
     *     refers to a parameterized type that cannot be instantiated
     *     for any reason
     * @since 1.5
     */
    Type getOwnerType();
}
