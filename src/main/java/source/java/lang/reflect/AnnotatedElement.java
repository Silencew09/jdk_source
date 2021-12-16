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

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.Repeatable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;

/**
 * Represents an annotated element of the program currently running in this
 * VM.  This interface allows annotations to be read reflectively.  All
 * annotations returned by methods in this interface are immutable and
 * serializable. The arrays returned by methods of this interface may be modified
 * by callers without affecting the arrays returned to other callers.
 *
 * 1.表示当前在此 VM 中运行的程序的带注释元素。
 * 该接口允许以反射方式读取注释。此接口中的方法返回的所有注释都是不可变和可序列化的。
 * 此接口的方法返回的数组可以被调用者修改，而不会影响返回给其他调用者的数组
 * <p>The {@link #getAnnotationsByType(Class)} and {@link
 * #getDeclaredAnnotationsByType(Class)} methods support multiple
 * annotations of the same type on an element. If the argument to
 * either method is a repeatable annotation type (JLS 9.6), then the
 * method will "look through" a container annotation (JLS 9.7), if
 * present, and return any annotations inside the container. Container
 * annotations may be generated at compile-time to wrap multiple
 * annotations of the argument type.
 * 2. getAnnotationsByType(Class)和 getDeclaredAnnotationsByType(Class)
 * 方法支持一个元素上的多个相同类型的注释。如果任一方法的参数是可重复的注释类型 (JLS 9.6)，
 * 则该方法将“查看”容器注释 (JLS 9.7)（如果存在），并返回容器内的任何注释。
 * 可以在编译时生成容器注释以包装参数类型的多个注释
 * <p>The terms <em>directly present</em>, <em>indirectly present</em>,
 * <em>present</em>, and <em>associated</em> are used throughout this
 * interface to describe precisely which annotations are returned by
 * methods:
 * 3.术语直接呈现、间接呈现、呈现和相关在整个接口中使用，以精确描述方法返回哪些注释：
 * <ul>
 * 1)如果 E具有 RuntimeVisibleAnnotations或 RuntimeVisibleParameterAnnotations
 * 或RuntimeVisibleTypeAnnotations属性，该属性包含 A。
 * <li> An annotation <i>A</i> is <em>directly present</em> on an
 * element <i>E</i> if <i>E</i> has a {@code
 * RuntimeVisibleAnnotations} or {@code
 * RuntimeVisibleParameterAnnotations} or {@code
 * RuntimeVisibleTypeAnnotations} attribute, and the attribute
 * contains <i>A</i>.
 * 2)如果E具有RuntimeVisibleAnnotations或RuntimeVisibleParameterAnnotations或
 * RuntimeVisibleTypeAnnotations属性，并且A的类型是可重复的，并且该属性只包含一个注解，
 * 其值元素包含A并且其类型为的包含注解类型A的类型。
 * <li>An annotation <i>A</i> is <em>indirectly present</em> on an
 * element <i>E</i> if <i>E</i> has a {@code RuntimeVisibleAnnotations} or
 * {@code RuntimeVisibleParameterAnnotations} or {@code RuntimeVisibleTypeAnnotations}
 * attribute, and <i>A</i> 's type is repeatable, and the attribute contains
 * exactly one annotation whose value element contains <i>A</i> and whose
 * type is the containing annotation type of <i>A</i> 's type.
 *
 * <li>An annotation <i>A</i> is present on an element <i>E</i> if either:
 *
 * <ul>
 *
 * <li><i>A</i> is directly present on <i>E</i>; or
 *
 * <li>No annotation of <i>A</i> 's type is directly present on
 * <i>E</i>, and <i>E</i> is a class, and <i>A</i> 's type is
 * inheritable, and <i>A</i> is present on the superclass of <i>E</i>.
 * 3)注解A存在于元素E上，如果：A直接存在于E;或 A的类型的注解不直接出现在E上，并且E是一个类，而A' s 类型是可继承的，
 * 并且A出现在E的超类中。
 * </ul>
 * 4)注释A与元素E关联，如果：A直接或间接存在于E;或 A的类型没有直接或间接的注解出现在E上，
 * E是一个类，A的类型是可继承的，并且A与E的超类相关联
 * <li>An annotation <i>A</i> is <em>associated</em> with an element <i>E</i>
 * if either:
 *
 * <ul>
 *
 * <li><i>A</i> is directly or indirectly present on <i>E</i>; or
 *
 * <li>No annotation of <i>A</i> 's type is directly or indirectly
 * present on <i>E</i>, and <i>E</i> is a class, and <i>A</i>'s type
 * is inheritable, and <i>A</i> is associated with the superclass of
 * <i>E</i>.
 *
 * </ul>
 *
 * </ul>
 *
 * <p>The table below summarizes which kind of annotation presence
 * different methods in this interface examine.
 * 4.下表总结了在该接口中检查哪种注解存在不同的方法
 * <table border>
 * <caption>Overview of kind of presence detected by different AnnotatedElement methods</caption>
 * <tr><th colspan=2></th><th colspan=4>Kind of Presence</th>
 * <tr><th colspan=2>Method</th><th>Directly Present</th><th>Indirectly Present</th><th>Present</th><th>Associated</th>
 * <tr><td align=right>{@code T}</td><td>{@link #getAnnotation(Class) getAnnotation(Class&lt;T&gt;)}
 * <td></td><td></td><td>X</td><td></td>
 * </tr>
 * <tr><td align=right>{@code Annotation[]}</td><td>{@link #getAnnotations getAnnotations()}
 * <td></td><td></td><td>X</td><td></td>
 * </tr>
 * <tr><td align=right>{@code T[]}</td><td>{@link #getAnnotationsByType(Class) getAnnotationsByType(Class&lt;T&gt;)}
 * <td></td><td></td><td></td><td>X</td>
 * </tr>
 * <tr><td align=right>{@code T}</td><td>{@link #getDeclaredAnnotation(Class) getDeclaredAnnotation(Class&lt;T&gt;)}
 * <td>X</td><td></td><td></td><td></td>
 * </tr>
 * <tr><td align=right>{@code Annotation[]}</td><td>{@link #getDeclaredAnnotations getDeclaredAnnotations()}
 * <td>X</td><td></td><td></td><td></td>
 * </tr>
 * <tr><td align=right>{@code T[]}</td><td>{@link #getDeclaredAnnotationsByType(Class) getDeclaredAnnotationsByType(Class&lt;T&gt;)}
 * <td>X</td><td>X</td><td></td><td></td>
 * </tr>
 * </table>
 *
 * <p>For an invocation of {@code get[Declared]AnnotationsByType( Class <
 * T >)}, the order of annotations which are directly or indirectly
 * present on an element <i>E</i> is computed as if indirectly present
 * annotations on <i>E</i> are directly present on <i>E</i> in place
 * of their container annotation, in the order in which they appear in
 * the value element of the container annotation.
 * 5. 对于get[Declared]AnnotationsByType( Class < T >)的调用，直接或间接存在于
 * 元素E上的注释的顺序被计算为好像间接存在于 上的注释E直接出现在E上，代替它们的容器注解，
 * 按照它们在容器注解的值元素中出现的顺序。
 * <p>There are several compatibility concerns to keep in mind if an
 * annotation type <i>T</i> is originally <em>not</em> repeatable and
 * later modified to be repeatable.
 * 6.如果注释类型T最初是not可重复的，后来修改为可重复的，则需要记住几个兼容性问题。
 * The containing annotation type for <i>T</i> is <i>TC</i>.
 * 7.T的包含注释类型是TC,将T修改为可重复是与T的现有用途和TC的现有用途兼容的源代码和二进制文件。
 * <ul>
 *
 * <li>Modifying <i>T</i> to be repeatable is source and binary
 * compatible with existing uses of <i>T</i> and with existing uses
 * of <i>TC</i>.
 *
 * That is, for source compatibility, source code with annotations of
 * type <i>T</i> or of type <i>TC</i> will still compile. For binary
 * compatibility, class files with annotations of type <i>T</i> or of
 * type <i>TC</i> (or with other kinds of uses of type <i>T</i> or of
 * type <i>TC</i>) will link against the modified version of <i>T</i>
 * if they linked against the earlier version.
 * 8.也就是说，为了源代码兼容性，带有T类型或 TC类型注释的源代码仍将编译。
 * 为了二进制兼容性，具有T类型或TC类型注释的类文件（或具有T类型或TC 类型的其他用途)
 * 将链接到 T的修改版本，如果它们链接到早期版本。
 * (An annotation type <i>TC</i> may informally serve as an acting
 * containing annotation type before <i>T</i> is modified to be
 * formally repeatable. Alternatively, when <i>T</i> is made
 * repeatable, <i>TC</i> can be introduced as a new type.)
 * 9.（注释类型 TC可以在T被修改为形式上可重复之前非正式地充当包含注释类型的动作。
 * 或者，当 T被设为可重复时，TC可以作为新类型引入。）
 * <li>If an annotation type <i>TC</i> is present on an element, and
 * <i>T</i> is modified to be repeatable with <i>TC</i> as its
 * containing annotation type then:
 * 10.如果元素上存在注释类型TC，并且T被修改为可重复使用TC作为其包含的注释类型，则：
 * <ul>
 *     1)对T的更改在行为上兼容get[Declared]Annotation(Class<T>)（使用 <i>T<i> 或 <i>TC 的参数调用<i>)
 *     和get[Declared]Annotations()方法，因为方法的结果不会因为TC成为T的包含注释类型而改变。
 *      2)对T的更改会更改使用T参数调用的get[Declared]AnnotationsByType(Class<T>)方法的结果，
 *      因为这些方法现在将识别TC类型的注解作为T的容器注解，并将“浏览”它以公开T类型的注解
 * <li>The change to <i>T</i> is behaviorally compatible with respect
 * to the {@code get[Declared]Annotation(Class<T>)} (called with an
 * argument of <i>T</i> or <i>TC</i>) and {@code
 * get[Declared]Annotations()} methods because the results of the
 * methods will not change due to <i>TC</i> becoming the containing
 * annotation type for <i>T</i>.
 *
 * <li>The change to <i>T</i> changes the results of the {@code
 * get[Declared]AnnotationsByType(Class<T>)} methods called with an
 * argument of <i>T</i>, because those methods will now recognize an
 * annotation of type <i>TC</i> as a container annotation for <i>T</i>
 * and will "look through" it to expose annotations of type <i>T</i>.
 *
 * </ul>
 *
 * <li>If an annotation of type <i>T</i> is present on an
 * element and <i>T</i> is made repeatable and more annotations of
 * type <i>T</i> are added to the element:
 * 11.如果元素上存在T类型的注释并且T是可重复的，并且更多T类型的注释被添加到元素中：
 *  1)添加T类型的注释既是源代码兼容的，也是二进制兼容的
 * <ul>
 *
 * <li> The addition of the annotations of type <i>T</i> is both
 * source compatible and binary compatible.
 *
 * <li>The addition of the annotations of type <i>T</i> changes the results
 * of the {@code get[Declared]Annotation(Class<T>)} methods and {@code
 * get[Declared]Annotations()} methods, because those methods will now
 * only see a container annotation on the element and not see an
 * annotation of type <i>T</i>.
 * 12.添加T类型的注解会改变 get[Declared]Annotation(Class<T>)方法和 get[Declared]Annotations()方法的结果，
 * 因为这些方法现在只会看到元素上的容器注释，而看不到T类型的注释。
 * <li>The addition of the annotations of type <i>T</i> changes the
 * results of the {@code get[Declared]AnnotationsByType(Class<T>)}
 * methods, because their results will expose the additional
 * annotations of type <i>T</i> whereas previously they exposed only a
 * single annotation of type <i>T</i>.
 * 13.添加T类型的注解会改变get[Declared]AnnotationsByType(Class<T>)方法的结果，
 * 因为它们的结果会暴露T类型的附加注解而以前他们只公开了一个T类型的注释
 * </ul>
 *
 * </ul>
 *
 * <p>If an annotation returned by a method in this interface contains
 * (directly or indirectly) a {@link Class}-valued member referring to
 * a class that is not accessible in this VM, attempting to read the class
 * by calling the relevant Class-returning method on the returned annotation
 * will result in a {@link TypeNotPresentException}.
 * 14.如果此接口中的方法返回的注解包含（直接或间接）一个 Class值成员，该成员引用此 VM 中不可访问的类，
 * 则尝试通过调用相关的类返回方法来读取该类在返回的注释上将导致 TypeNotPresentException
 * <p>Similarly, attempting to read an enum-valued member will result in
 * a {@link EnumConstantNotPresentException} if the enum constant in the
 * annotation is no longer present in the enum type.
 * 15.同样，如果注释中的枚举常量不再存在于枚举类型中，则尝试读取枚举值成员将导致EnumConstantNotPresentException。
 * <p>If an annotation type <i>T</i> is (meta-)annotated with an
 * {@code @Repeatable} annotation whose value element indicates a type
 * <i>TC</i>, but <i>TC</i> does not declare a {@code value()} method
 * with a return type of <i>T</i>{@code []}, then an exception of type
 * {@link java.lang.annotation.AnnotationFormatError} is thrown.
 * 16.如果注释类型T被（元）注释了Repeatable ，其值元素指示类型TC，但TC没有声明一个返回类型为
 * T[]的value()方法，然后抛出类型为java.lang.annotation.AnnotationFormatError的异常
 * <p>Finally, attempting to read a member whose definition has evolved
 * incompatibly will result in a {@link
 * java.lang.annotation.AnnotationTypeMismatchException} or an
 * {@link java.lang.annotation.IncompleteAnnotationException}.
 * 17.最后，尝试读取定义不兼容的成员将导致java.lang.annotation.AnnotationTypeMismatchException或
 * java.lang.annotation.IncompleteAnnotationException
 * @see java.lang.EnumConstantNotPresentException
 * @see java.lang.TypeNotPresentException
 * @see AnnotationFormatError
 * @see java.lang.annotation.AnnotationTypeMismatchException
 * @see java.lang.annotation.IncompleteAnnotationException
 * @since 1.5
 * @author Josh Bloch
 */
public interface AnnotatedElement {
    /**
     * Returns true if an annotation for the specified type
     * is <em>present</em> on this element, else false.  This method
     * is designed primarily for convenient access to marker annotations.
     * 1.如果指定类型的注解在此元素上存在 ，则返回 true，否则返回 false。此方法主要是为了方便访问标记注解而设计的
     * <p>The truth value returned by this method is equivalent to:
     * {@code getAnnotation(annotationClass) != null}
     * 2.该方法返回的真值等价于：getAnnotation(annotationClass) != null
     * <p>The body of the default method is specified to be the code
     * above.
     * 3.默认方法的主体被指定为上面的代码。
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return true if an annotation for the specified annotation
     *     type is present on this element, else false
     * @throws NullPointerException if the given annotation class is null
     * @since 1.5
     */
    default boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getAnnotation(annotationClass) != null;
    }

   /**
     * Returns this element's annotation for the specified type if
     * such an annotation is <em>present</em>, else null.
     *  如果这样的注解是存在的，则返回此元素的指定类型的注解，否则为 null
     * @param <T> the type of the annotation to query for and return if present
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return this element's annotation for the specified annotation type if
     *     present on this element, else null
     * @throws NullPointerException if the given annotation class is null
     * @since 1.5
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    /**
     * Returns annotations that are <em>present</em> on this element.
     * 1.返回此元素上present的注释。
     * If there are no annotations <em>present</em> on this element, the return
     * value is an array of length 0.
     *
     * The caller of this method is free to modify the returned array; it will
     * have no effect on the arrays returned to other callers.
     *
     * @return annotations present on this element
     * @since 1.5
     */
    Annotation[] getAnnotations();

    /**
     * Returns annotations that are <em>associated</em> with this element.
     * 1.返回与此元素关联的注解
     * If there are no annotations <em>associated</em> with this element, the return
     * value is an array of length 0.
     * 2.如果没有与该元素关联的注释，则返回值是一个长度为 0 的数组
     * The difference between this method and {@link #getAnnotation(Class)}
     * is that this method detects if its argument is a <em>repeatable
     * annotation type</em> (JLS 9.6), and if so, attempts to find one or
     * more annotations of that type by "looking through" a container
     * annotation.
     * 3.此方法与getAnnotation(Class)的区别在于，此方法检测其参数是否为可重复注释类型(JLS 9.6)，
     * 如果是，则尝试查找通过“查看”容器注释来键入
     * The caller of this method is free to modify the returned array; it will
     * have no effect on the arrays returned to other callers.
     * 4.该方法的调用者可以自由修改返回的数组；它不会影响返回给其他调用者的数组
     * @implSpec The default implementation first calls {@link
     * #getDeclaredAnnotationsByType(Class)} passing {@code
     * annotationClass} as the argument. If the returned array has
     * length greater than zero, the array is returned. If the returned
     * array is zero-length and this {@code AnnotatedElement} is a
     * class and the argument type is an inheritable annotation type,
     * and the superclass of this {@code AnnotatedElement} is non-null,
     * then the returned result is the result of calling {@link
     * #getAnnotationsByType(Class)} on the superclass with {@code
     * annotationClass} as the argument. Otherwise, a zero-length
     * array is returned.
     * 5.默认实现首先调用 getDeclaredAnnotationsByType(Class)传递annotationClass作为参数。
     * 如果返回的数组长度大于零，则返回该数组。如果返回的数组长度为零且此AnnotatedElement为类且参数类型为可继承的注解类型，
     * 且此AnnotatedElement的超类为非空，则返回结果为结果以 annotationClass作为参数
     * 在超类上调用 getAnnotationsByType(Class)。否则，返回零长度数组
     * @param <T> the type of the annotation to query for and return if present
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return all this element's annotations for the specified annotation type if
     *     associated with this element, else an array of length zero
     * @throws NullPointerException if the given annotation class is null
     * @since 1.8
     */
    default <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
         /*
          * Definition of associated: directly or indirectly present OR
          * neither directly nor indirectly present AND the element is
          * a Class, the annotation type is inheritable, and the
          * annotation type is associated with the superclass of the
          * element.
          * 关联的定义：直接或间接存在 OR 既不直接也不间接存在 AND 元素是一个类，
          * 注解类型是可继承的，注解类型与元素的超类相关联
          */
         T[] result = getDeclaredAnnotationsByType(annotationClass);

         if (result.length == 0 && // Neither directly nor indirectly present
             this instanceof Class && // the element is a class
             AnnotationType.getInstance(annotationClass).isInherited()) { // Inheritable
             Class<?> superClass = ((Class<?>) this).getSuperclass();
             if (superClass != null) {
                 // Determine if the annotation is associated with the
                 // superclass
                 result = superClass.getAnnotationsByType(annotationClass);
             }
         }

         return result;
     }

    /**
     * Returns this element's annotation for the specified type if
     * such an annotation is <em>directly present</em>, else null.
     *1.如果这样的注解（直接存在），则返回此元素对指定类型的注解，否则为 null
     * This method ignores inherited annotations. (Returns null if no
     * annotations are directly present on this element.)
     *2.此方法忽略继承的注释。 （如果此元素上没有直接存在注释，则返回 null。）
     * @implSpec The default implementation first performs a null check
     * and then loops over the results of {@link
     * #getDeclaredAnnotations} returning the first annotation whose
     * annotation type matches the argument type.
     * 3.默认实现首先执行空检查，然后循环遍历getDeclaredAnnotations的结果，返回其注释类型与参数类型匹配的第一个注释。
     * @param <T> the type of the annotation to query for and return if directly present
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return this element's annotation for the specified annotation type if
     *     directly present on this element, else null
     * @throws NullPointerException if the given annotation class is null
     * @since 1.8
     */
    default <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
         Objects.requireNonNull(annotationClass);
         // Loop over all directly-present annotations looking for a matching one
        //遍历所有直接存在的注释以寻找匹配的注释
         for (Annotation annotation : getDeclaredAnnotations()) {
             if (annotationClass.equals(annotation.annotationType())) {
                 // More robust to do a dynamic cast at runtime instead
                 // of compile-time only.
                 //在运行时进行动态转换而不是仅在编译时进行更健壮
                 return annotationClass.cast(annotation);
             }
         }
         return null;
     }

    /**
     * Returns this element's annotation(s) for the specified type if
     * such annotations are either <em>directly present</em> or
     * <em>indirectly present</em>. This method ignores inherited
     * annotations.
     * 1.如果此类注释直接存在或间接存在，则返回此元素的指定类型的注释。此方法忽略继承的注释
     * If there are no specified annotations directly or indirectly
     * present on this element, the return value is an array of length
     * 0.
     * 2.如果此元素上没有直接或间接指定的注解，则返回值是长度为 0 的数组
     * The difference between this method and {@link
     * #getDeclaredAnnotation(Class)} is that this method detects if its
     * argument is a <em>repeatable annotation type</em> (JLS 9.6), and if so,
     * attempts to find one or more annotations of that type by "looking
     * through" a container annotation if one is present.
     * 3.此方法与 getDeclaredAnnotation(Class)的区别在于，此方法检测其参数是否为可重复注释类型(JLS 9.6)，
     * 如果是，则尝试查找通过“查看”容器注释（如果存在）来键入该类型
     * The caller of this method is free to modify the returned array; it will
     * have no effect on the arrays returned to other callers.
     * 4.该方法的调用者可以自由修改返回的数组；它不会影响返回给其他调用者的数组
     * @implSpec The default implementation may call {@link
     * #getDeclaredAnnotation(Class)} one or more times to find a
     * directly present annotation and, if the annotation type is
     * repeatable, to find a container annotation. If annotations of
     * the annotation type {@code annotationClass} are found to be both
     * directly and indirectly present, then {@link
     * #getDeclaredAnnotations()} will get called to determine the
     * order of the elements in the returned array.
     * 5.默认实现可能会调用getDeclaredAnnotation(Class)一次或多次以查找直接存在的注解，
     * 如果注解类型可重复，则查找容器注解。如果发现注释类型 annotationClass的注释直接和间接存在，
     * 则将调用getDeclaredAnnotations()以确定返回数组中元素的顺序
     * <p>Alternatively, the default implementation may call {@link
     * #getDeclaredAnnotations()} a single time and the returned array
     * examined for both directly and indirectly present
     * annotations. The results of calling {@link
     * #getDeclaredAnnotations()} are assumed to be consistent with the
     * results of calling {@link #getDeclaredAnnotation(Class)}.
     * 6.或者，默认实现可能会调用 getDeclaredAnnotations()一次，并检查返回的数组是否直接和间接存在注释。
     * 假设调用getDeclaredAnnotations() 的结果与调用getDeclaredAnnotation(Class)的结果一致。
     * @param <T> the type of the annotation to query for and return
     * if directly or indirectly present
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return all this element's annotations for the specified annotation type if
     *     directly or indirectly present on this element, else an array of length zero
     * @throws NullPointerException if the given annotation class is null
     * @since 1.8
     */
    default <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);
        return AnnotationSupport.
            getDirectlyAndIndirectlyPresent(Arrays.stream(getDeclaredAnnotations()).
                                            collect(Collectors.toMap(Annotation::annotationType,
                                                                     Function.identity(),
                                                                     ((first,second) -> first),
                                                                     LinkedHashMap::new)),
                                            annotationClass);
    }

    /**
     * Returns annotations that are <em>directly present</em> on this element.
     * This method ignores inherited annotations.
     * 1.返回直接存在于该元素上的注释。此方法忽略继承的注释。
     * If there are no annotations <em>directly present</em> on this element,
     * the return value is an array of length 0.
     * 2.如果该元素上没有直接存在的注释，则返回值是一个长度为 0 的数组
     * The caller of this method is free to modify the returned array; it will
     * have no effect on the arrays returned to other callers.
     * 3.该方法的调用者可以自由修改返回的数组；它不会影响返回给其他调用者的数组
     * @return annotations directly present on this element
     * @since 1.5
     */
    Annotation[] getDeclaredAnnotations();
}
