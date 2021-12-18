/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import sun.reflect.annotation.AnnotationSupport;

/**
 * Information about method parameters.
 * 1.有关方法参数的信息
 * A {@code Parameter} provides information about method parameters,
 * including its name and modifiers.  It also provides an alternate
 * means of obtaining attributes for the parameter.
 * 2.Parameter提供有关方法参数的信息，包括其名称和修饰符。
 * 它还提供了一种获取参数属性的替代方法
 * @since 1.8
 */
public final class Parameter implements AnnotatedElement {

    private final String name;
    private final int modifiers;
    private final Executable executable;
    private final int index;

    /**
     * Package-private constructor for {@code Parameter}.
     * 1.Parameter的包私有构造函数
     * If method parameter data is present in the classfile, then the
     * JVM creates {@code Parameter} objects directly.  If it is
     * absent, however, then {@code Executable} uses this constructor
     * to synthesize them.
     * 2.如果类文件中存在方法参数数据，则 JVM 会直接创建Parameter对象。
     * 但是，如果它不存在，则 Executable使用此构造函数来合成它们
     * @param name The name of the parameter.
     * @param modifiers The modifier flags for the parameter.
     * @param executable The executable which defines this parameter.
     * @param index The index of the parameter.
     */
    Parameter(String name,
              int modifiers,
              Executable executable,
              int index) {
        this.name = name;
        this.modifiers = modifiers;
        this.executable = executable;
        this.index = index;
    }

    /**
     * Compares based on the executable and the index.
     * 基于可执行文件和索引进行比较。
     * @param obj The object to compare.
     * @return Whether or not this is equal to the argument.
     */
    public boolean equals(Object obj) {
        if(obj instanceof Parameter) {
            Parameter other = (Parameter)obj;
            return (other.executable.equals(executable) &&
                    other.index == index);
        }
        return false;
    }

    /**
     * Returns a hash code based on the executable's hash code and the
     * index.
     * 根据可执行文件的哈希码和索引返回哈希码。
     *
     * @return A hash code based on the executable's hash code.
     */
    public int hashCode() {
        return executable.hashCode() ^ index;
    }

    /**
     * Returns true if the parameter has a name according to the class
     * file; returns false otherwise. Whether a parameter has a name
     * is determined by the {@literal MethodParameters} attribute of
     * the method which declares the parameter.
     * 如果参数根据类文件具有名称，则返回true；否则返回 false。
     * 参数是否具有名称由声明参数的方法的MethodParameters属性决定
     * @return true if and only if the parameter has a name according
     * to the class file.
     */
    public boolean isNamePresent() {
        return executable.hasRealParameterData() && name != null;
    }

    /**
     * Returns a string describing this parameter.  The format is the
     * modifiers for the parameter, if any, in canonical order as
     * recommended by <cite>The Java&trade; Language
     * Specification</cite>, followed by the fully- qualified type of
     * the parameter (excluding the last [] if the parameter is
     * variable arity), followed by "..." if the parameter is variable
     * arity, followed by a space, followed by the name of the
     * parameter.
     * 返回描述此参数的字符串。格式是参数的修饰符（如果有），按照 <cite>Java™ 语言规范<cite> 推荐的规范顺序，
     * 后跟参数的完全限定类型（不包括最后一个 []，如果参数是变量 arity)，如果参数是变量 arity，则后跟“...”，
     * 后跟一个空格，后跟参数的名称。
     * @return A string representation of the parameter and associated
     * information.
     */
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Type type = getParameterizedType();
        final String typename = type.getTypeName();

        sb.append(Modifier.toString(getModifiers()));

        if(0 != modifiers)
            sb.append(' ');

        if(isVarArgs())
            sb.append(typename.replaceFirst("\\[\\]$", "..."));
        else
            sb.append(typename);

        sb.append(' ');
        sb.append(getName());

        return sb.toString();
    }

    /**
     * Return the {@code Executable} which declares this parameter.
     * 返回声明此参数的Executable。
     * @return The {@code Executable} declaring this parameter.
     */
    public Executable getDeclaringExecutable() {
        return executable;
    }

    /**
     * Get the modifier flags for this the parameter represented by
     * this {@code Parameter} object.
     * 获取此Parameter对象表示的参数的修饰符标志。
     * @return The modifier flags for this parameter.
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Returns the name of the parameter.  If the parameter's name is
     * {@linkplain #isNamePresent() present}, then this method returns
     * the name provided by the class file. Otherwise, this method
     * synthesizes a name of the form argN, where N is the index of
     * the parameter in the descriptor of the method which declares
     * the parameter.
     * 1.返回参数的名称。如果参数的名称是isNamePresent()，则此方法返回类文件提供的名称。
     * 否则，该方法合成一个形式为 argN 的名称，其中 N 是参数在声明参数的方法的描述符中的索引
     * @return The name of the parameter, either provided by the class
     *         file or synthesized if the class file does not provide
     *         a name.
     */
    public String getName() {
        // Note: empty strings as paramete names are now outlawed.
        // The .equals("") is for compatibility with current JVM
        // behavior.  It may be removed at some point.
        //注意：空字符串作为参数名称现在是非法的。 .equals("") 是为了与当前的 JVM 行为兼容。它可能会在某个时候被删除
        if(name == null || name.equals(""))
            return "arg" + index;
        else
            return name;
    }

    // Package-private accessor to the real name field.
    //对实名字段的包私有访问器
    String getRealName() {
        return name;
    }

    /**
     * Returns a {@code Type} object that identifies the parameterized
     * type for the parameter represented by this {@code Parameter}
     * object.
     * 返回一个Type对象，该对象标识此Parameter对象表示的参数的参数化类型
     *
     * @return a {@code Type} object identifying the parameterized
     * type of the parameter represented by this object
     */
    public Type getParameterizedType() {
        Type tmp = parameterTypeCache;
        if (null == tmp) {
            tmp = executable.getAllGenericParameterTypes()[index];
            parameterTypeCache = tmp;
        }

        return tmp;
    }

    private transient volatile Type parameterTypeCache = null;

    /**
     * Returns a {@code Class} object that identifies the
     * declared type for the parameter represented by this
     * {@code Parameter} object.
     * 返回一个 Class对象，该对象标识此 Parameter对象表示的参数的声明类型
     * @return a {@code Class} object identifying the declared
     * type of the parameter represented by this object
     */
    public Class<?> getType() {
        Class<?> tmp = parameterClassCache;
        if (null == tmp) {
            tmp = executable.getParameterTypes()[index];
            parameterClassCache = tmp;
        }
        return tmp;
    }

    /**
     * Returns an AnnotatedType object that represents the use of a type to
     * specify the type of the formal parameter represented by this Parameter.
     * 返回一个 AnnotatedType 对象，该对象表示使用一种类型来指定由此 Parameter 表示的形式参数的类型
     * @return an {@code AnnotatedType} object representing the use of a type
     *         to specify the type of the formal parameter represented by this
     *         Parameter
     */
    public AnnotatedType getAnnotatedType() {
        // no caching for now
        return executable.getAnnotatedParameterTypes()[index];
    }

    private transient volatile Class<?> parameterClassCache = null;

    /**
     * Returns {@code true} if this parameter is implicitly declared
     * in source code; returns {@code false} otherwise.
     * 如果此参数在源代码中隐式声明，则返回true；否则返回false
     * @return true if and only if this parameter is implicitly
     * declared as defined by <cite>The Java&trade; Language
     * Specification</cite>.
     */
    public boolean isImplicit() {
        return Modifier.isMandated(getModifiers());
    }

    /**
     * Returns {@code true} if this parameter is neither implicitly
     * nor explicitly declared in source code; returns {@code false}
     * otherwise.
     * 如果此参数在源代码中既没有隐式也没有显式声明，则返回true；否则返回false
     *
     * @jls 13.1 The Form of a Binary
     * @return true if and only if this parameter is a synthetic
     * construct as defined by
     * <cite>The Java&trade; Language Specification</cite>.
     */
    public boolean isSynthetic() {
        return Modifier.isSynthetic(getModifiers());
    }

    /**
     * Returns {@code true} if this parameter represents a variable
     * argument list; returns {@code false} otherwise.
     * 如果此参数表示可变参数列表，则返回true；否则返回false。
     * @return {@code true} if an only if this parameter represents a
     * variable argument list.
     */
    public boolean isVarArgs() {
        return executable.isVarArgs() &&
            index == executable.getParameterCount() - 1;
    }


    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);
        return annotationClass.cast(declaredAnnotations().get(annotationClass));
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);

        return AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), annotationClass);
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[] getDeclaredAnnotations() {
        return executable.getParameterAnnotations()[index];
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        // Only annotations on classes are inherited, for all other
        // objects getDeclaredAnnotation is the same as
        // getAnnotation.
        //仅继承类上的注释，对于所有其他对象，getDeclaredAnnotation 与 getAnnotation 相同
        return getAnnotation(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        // Only annotations on classes are inherited, for all other
        // objects getDeclaredAnnotations is the same as
        // getAnnotations.
        //仅继承类上的注释，对于所有其他对象，getDeclaredAnnotations 与 getAnnotations 相同。
        return getAnnotationsByType(annotationClass);
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[] getAnnotations() {
        return getDeclaredAnnotations();
    }

    private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;

    private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
        if(null == declaredAnnotations) {
            declaredAnnotations =
                new HashMap<Class<? extends Annotation>, Annotation>();
            Annotation[] ann = getDeclaredAnnotations();
            for(int i = 0; i < ann.length; i++)
                declaredAnnotations.put(ann[i].annotationType(), ann[i]);
        }
        return declaredAnnotations;
   }

}
