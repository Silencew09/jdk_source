/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * A programmer assertion that the body of the annotated method or
 * constructor does not perform potentially unsafe operations on its
 * varargs parameter.  Applying this annotation to a method or
 * constructor suppresses unchecked warnings about a
 * <i>non-reifiable</i> variable arity (vararg) type and suppresses
 * unchecked warnings about parameterized array creation at call
 * sites.
 * 1.程序员断言带注释的方法或构造函数的主体不会对其 varargs 参数执行潜在的不安全操作。
 * 将此注释应用于方法或构造函数会抑制关于 non-reifiable变量参数 (vararg) 类型的未经检查的警告，
 * 并禁止关于在调用点创建参数化数组的未经检查的警告
 * <p> In addition to the usage restrictions imposed by its {@link
 * Target @Target} meta-annotation, compilers are required to implement
 * additional usage restrictions on this annotation type; it is a
 * compile-time error if a method or constructor declaration is
 * annotated with a {@code @SafeVarargs} annotation, and either:
 * <ul>
 * <li>  the declaration is a fixed arity method or constructor
 *
 * <li> the declaration is a variable arity method that is neither
 * {@code static} nor {@code final}.
 *
 * </ul>
 * 2.除了其@Target元注解施加的使用限制外，编译器还需要对该注解类型实施额外的使用限制；
 * 如果方法或构造函数声明用 @SafeVarargs注释进行注释，并且：
 * 声明是固定的arity 方法或构造函数声明是一个既不是static也不是final的可变数量方法。
 * <p> Compilers are encouraged to issue warnings when this annotation
 * type is applied to a method or constructor declaration where:
 * 3.鼓励编译器在将此注释类型应用于方法或构造函数声明时发出警告，其中:
 * <ul> 变量arity参数有一个具体的元素类型，包括原始类型，Object和String。
 * （对于可具体化的元素类型，此注释类型抑制的未经检查的警告已经不会发生。）
 *
 * <li> The variable arity parameter has a reifiable element type,
 * which includes primitive types, {@code Object}, and {@code String}.
 * (The unchecked warnings this annotation type suppresses already do
 * not occur for a reifiable element type.)
 *
 * <li> The body of the method or constructor declaration performs
 * potentially unsafe operations, such as an assignment to an element
 * of the variable arity parameter's array that generates an unchecked
 * warning.  Some unsafe operations do not trigger an unchecked
 * warning.  For example, the aliasing in
 * 4.方法的主体或构造函数声明执行潜在的不安全操作，例如对变量 arity 参数数组的元素进行赋值，
 * 从而生成未经检查的警告。一些不安全的操作不会触发未经检查的警告
 *
 * <blockquote><pre>
 * &#64;SafeVarargs // Not actually safe!
 * static void m(List&lt;String&gt;... stringLists) {
 *   Object[] array = stringLists;
 *   List&lt;Integer&gt; tmpList = Arrays.asList(42);
 *   array[0] = tmpList; // Semantically invalid, but compiles without warnings
 *   String s = stringLists[0].get(0); // Oh no, ClassCastException at runtime!
 * }
 * </pre></blockquote>
 *
 * leads to a {@code ClassCastException} at runtime.
 *
 * <p>Future versions of the platform may mandate compiler errors for
 * such unsafe operations.
 * 该平台的未来版本可能会要求此类不安全操作出现编译器错误。
 * </ul>
 *
 * @since 1.7
 * @jls 4.7 Reifiable Types
 * @jls 8.4.1 Formal Parameters
 * @jls 9.6.3.7 @SafeVarargs
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface SafeVarargs {}
