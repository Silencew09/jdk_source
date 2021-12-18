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

import sun.reflect.CallerSensitive;
import sun.reflect.ConstructorAccessor;
import sun.reflect.Reflection;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.scope.ConstructorScope;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;

/**
 * {@code Constructor} provides information about, and access to, a single
 * constructor for a class.
 * 1.Constructor提供有关类的单个构造函数的信息和对其的访问
 *
 * <p>{@code Constructor} permits widening conversions to occur when matching the
 * actual parameters to newInstance() with the underlying
 * constructor's formal parameters, but throws an
 * {@code IllegalArgumentException} if a narrowing conversion would occur.
 * 2.Constructor允许在将实际参数与 newInstance() 与底层构造函数的形式参数匹配时发生扩大转换，
 * 但如果发生缩小转换，则会抛出IllegalArgumentException
 * @param <T> the class in which the constructor is declared
 *
 * @see Member
 * @see java.lang.Class
 * @see java.lang.Class#getConstructors()
 * @see java.lang.Class#getConstructor(Class[])
 * @see java.lang.Class#getDeclaredConstructors()
 *
 * @author      Kenneth Russell
 * @author      Nakul Saraiya
 */
public final class Constructor<T> extends Executable {
    private Class<T>            clazz;
    private int                 slot;
    private Class<?>[]          parameterTypes;
    private Class<?>[]          exceptionTypes;
                                //修饰符
    private int                 modifiers;
    // Generics and annotations support
    //泛型和注解支持
    private transient String    signature;
    // generic info repository; lazily initialized
    private transient ConstructorRepository genericInfo;
    private byte[]              annotations;
    private byte[]              parameterAnnotations;

    // Generics infrastructure
    // Accessor for factory
    private GenericsFactory getFactory() {
        // create scope and factory
        return CoreReflectionFactory.make(this, ConstructorScope.make(this));
    }

    // Accessor for generic info repository
    @Override
    ConstructorRepository getGenericInfo() {
        // lazily initialize repository if necessary
        if (genericInfo == null) {
            // create and cache generic info repository
            genericInfo =
                ConstructorRepository.make(getSignature(),
                                           getFactory());
        }
        return genericInfo; //return cached repository
    }

    private volatile ConstructorAccessor constructorAccessor;
    // For sharing of ConstructorAccessors. This branching structure
    // is currently only two levels deep (i.e., one root Constructor
    // and potentially many Constructor objects pointing to it.)
    // 用于共享 ConstructorAccessors。这个分支结构目前只有两层深
    // （即，一个根构造函数和潜在的许多指向它的构造函数对象。）
    // If this branching structure would ever contain cycles, deadlocks can
    // occur in annotation code.
    //如果此分支结构包含循环，则注解代码中可能会发生死锁
    private Constructor<T>      root;

    /**
     * Used by Excecutable for annotation sharing.
     */
    @Override
    Executable getRoot() {
        return root;
    }

    /**
     * Package-private constructor used by ReflectAccess to enable
     * instantiation of these objects in Java code from the java.lang
     * package via sun.reflect.LangReflectAccess.
     * ReflectAccess 使用的包私有构造函数，通过 sun.reflect.LangReflectAccess
     * 从 java.lang 包启用 Java 代码中这些对象的实例化
     */
    Constructor(Class<T> declaringClass,
                Class<?>[] parameterTypes,
                Class<?>[] checkedExceptions,
                int modifiers,
                int slot,
                String signature,
                byte[] annotations,
                byte[] parameterAnnotations) {
        this.clazz = declaringClass;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = checkedExceptions;
        this.modifiers = modifiers;
        this.slot = slot;
        this.signature = signature;
        this.annotations = annotations;
        this.parameterAnnotations = parameterAnnotations;
    }

    /**
     * Package-private routine (exposed to java.lang.Class via
     * ReflectAccess) which returns a copy of this Constructor. The copy's
     * "root" field points to this Constructor.
     * 包私有例程（通过 ReflectAccess 暴露给 java.lang.Class）
     * 返回此构造函数的副本。副本的“根”字段指向此构造函数
     */
    Constructor<T> copy() {
        // This routine enables sharing of ConstructorAccessor objects
        // among Constructor objects which refer to the same underlying
        // method in the VM. (All of this contortion is only necessary
        // because of the "accessibility" bit in AccessibleObject,
        // which implicitly requires that new java.lang.reflect
        // objects be fabricated for each reflective call on Class
        // objects.)
        //此例程允许在引用 VM 中相同底层方法的构造函数对象之间共享构造函数访问器对象。
        // （所有这些扭曲都是必要的，因为 AccessibleObject 中的“可访问性”位，它隐含地要求为 Class 对象的
        // 每个反射调用制造新的 java.lang.reflect 对象。）
        if (this.root != null)
            throw new IllegalArgumentException("Can not copy a non-root Constructor");

        Constructor<T> res = new Constructor<>(clazz,
                                               parameterTypes,
                                               exceptionTypes, modifiers, slot,
                                               signature,
                                               annotations,
                                               parameterAnnotations);
        res.root = this;
        // Might as well eagerly propagate this if already present
        //如果已经存在，不妨急切地传播它
        res.constructorAccessor = constructorAccessor;
        return res;
    }

    @Override
    boolean hasGenericInformation() {
        return (getSignature() != null);
    }

    @Override
    byte[] getAnnotationBytes() {
        return annotations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getDeclaringClass() {
        return clazz;
    }

    /**
     * Returns the name of this constructor, as a string.  This is
     * the binary name of the constructor's declaring class.
     * 以字符串形式返回此构造函数的名称。这是构造函数声明类的二进制名称
     */
    @Override
    public String getName() {
        return getDeclaringClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getModifiers() {
        return modifiers;
    }

    /**
     * {@inheritDoc}
     * @throws GenericSignatureFormatError {@inheritDoc}
     * @since 1.5
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public TypeVariable<Constructor<T>>[] getTypeParameters() {
      if (getSignature() != null) {
        return (TypeVariable<Constructor<T>>[])getGenericInfo().getTypeParameters();
      } else
          return (TypeVariable<Constructor<T>>[])new TypeVariable[0];
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes.clone();
    }

    /**
     * {@inheritDoc}
     * @since 1.8
     */
    public int getParameterCount() { return parameterTypes.length; }

    /**
     * {@inheritDoc}
     * @throws GenericSignatureFormatError {@inheritDoc}
     * @throws TypeNotPresentException {@inheritDoc}
     * @throws MalformedParameterizedTypeException {@inheritDoc}
     * @since 1.5
     */
    @Override
    public Type[] getGenericParameterTypes() {
        return super.getGenericParameterTypes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?>[] getExceptionTypes() {
        return exceptionTypes.clone();
    }


    /**
     * {@inheritDoc}
     * @throws GenericSignatureFormatError {@inheritDoc}
     * @throws TypeNotPresentException {@inheritDoc}
     * @throws MalformedParameterizedTypeException {@inheritDoc}
     * @since 1.5
     */
    @Override
    public Type[] getGenericExceptionTypes() {
        return super.getGenericExceptionTypes();
    }

    /**
     * Compares this {@code Constructor} against the specified object.
     * Returns true if the objects are the same.  Two {@code Constructor} objects are
     * the same if they were declared by the same class and have the
     * same formal parameter types.
     * 将此Constructor与指定的对象进行比较。如果对象相同，则返回 true。
     * 如果两个Constructor对象由同一个类声明并且具有相同的形参类型，则它们是相同的
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Constructor) {
            Constructor<?> other = (Constructor<?>)obj;
            if (getDeclaringClass() == other.getDeclaringClass()) {
                return equalParamTypes(parameterTypes, other.parameterTypes);
            }
        }
        return false;
    }

    /**
     * Returns a hashcode for this {@code Constructor}. The hashcode is
     * the same as the hashcode for the underlying constructor's
     * declaring class name.
     * 返回此Constructor的哈希码。哈希码与底层构造函数的声明类名的哈希码相同
     */
    public int hashCode() {
        return getDeclaringClass().getName().hashCode();
    }

    /**
     * Returns a string describing this {@code Constructor}.  The string is
     * formatted as the constructor access modifiers, if any,
     * followed by the fully-qualified name of the declaring class,
     * followed by a parenthesized, comma-separated list of the
     * constructor's formal parameter types.
     * 1.返回描述此Constructor的字符串。该字符串的格式为构造函数访问修饰符（如果有），
     * 后跟声明类的完全限定名称，后跟带括号的、逗号分隔的构造函数形式参数类型列表。
     * For example:
     * <pre>
     *    public java.util.Hashtable(int,float)
     * </pre>
     * 2.例如：public java.util.Hashtable(int,float)
     *
     * <p>The only possible modifiers for constructors are the access
     * modifiers {@code public}, {@code protected} or
     * {@code private}.  Only one of these may appear, or none if the
     * constructor has default (package) access.
     * 3.构造函数唯一可能的修饰符是访问修饰符public、protected或private。
     * 如果构造函数具有默认（包）访问权限，则可能只出现其中之一，或者不出现
     * @return a string describing this {@code Constructor}
     * @jls 8.8.3. Constructor Modifiers
     */
    public String toString() {
        return sharedToString(Modifier.constructorModifiers(),
                              false,
                              parameterTypes,
                              exceptionTypes);
    }

    @Override
    void specificToStringHeader(StringBuilder sb) {
        sb.append(getDeclaringClass().getTypeName());
    }

    /**
     * Returns a string describing this {@code Constructor},
     * including type parameters.  The string is formatted as the
     * constructor access modifiers, if any, followed by an
     * angle-bracketed comma separated list of the constructor's type
     * parameters, if any, followed by the fully-qualified name of the
     * declaring class, followed by a parenthesized, comma-separated
     * list of the constructor's generic formal parameter types.
     * 1.返回描述此Constructor的字符串，包括类型参数。该字符串的格式为构造函数访问修饰符（如果有），
     * 后跟一个用尖括号括起的逗号分隔的构造函数类型参数列表（如果有），后跟声明类的完全限定名称，
     * 后跟一个带括号的逗号-构造函数的通用形式参数类型的分隔列表
     * If this constructor was declared to take a variable number of
     * arguments, instead of denoting the last parameter as
     * "<tt><i>Type</i>[]</tt>", it is denoted as
     * "<tt><i>Type</i>...</tt>".
     * 2.如果此构造函数被声明为采用可变数量的参数，
     * 而不是将最后一个参数表示为“Type[]”，而是表示为“Type ...”。
     * A space is used to separate access modifiers from one another
     * and from the type parameters or return type.  If there are no
     * type parameters, the type parameter list is elided; if the type
     * parameter list is present, a space separates the list from the
     * class name.  If the constructor is declared to throw
     * exceptions, the parameter list is followed by a space, followed
     * by the word "{@code throws}" followed by a
     * comma-separated list of the thrown exception types.
     * 3.空格用于将访问修饰符彼此分隔开，并与类型参数或返回类型分隔开。如果没有类型参数，
     * 则省略类型参数列表；如果类型参数列表存在，一个空格将列表与类名分开。如果构造函数声明为抛出异常，
     * 则参数列表后跟一个空格，后跟单词“throws”，后跟以逗号分隔的抛出异常类型列表
     * <p>The only possible modifiers for constructors are the access
     * modifiers {@code public}, {@code protected} or
     * {@code private}.  Only one of these may appear, or none if the
     * constructor has default (package) access.
     * 4.构造函数唯一可能的修饰符是访问修饰符 public、protected或 private。
     * 如果构造函数具有默认（包）访问权限，则可能只出现其中之一，或者不出现。
     * @return a string describing this {@code Constructor},
     * include type parameters
     *
     * @since 1.5
     * @jls 8.8.3. Constructor Modifiers
     */
    @Override
    public String toGenericString() {
        return sharedToGenericString(Modifier.constructorModifiers(), false);
    }

    @Override
    void specificToGenericStringHeader(StringBuilder sb) {
        specificToStringHeader(sb);
    }

    /**
     * Uses the constructor represented by this {@code Constructor} object to
     * create and initialize a new instance of the constructor's
     * declaring class, with the specified initialization parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to method invocation conversions as necessary.
     * 1.使用此Constructor对象表示的构造函数，使用指定的初始化参数创建和初始化构造函数声明类的新实例。
     * 单个参数会自动解包以匹配原始形式参数，并且原始参数和引用参数都根据需要进行方法调用转换。
     * <p>If the number of formal parameters required by the underlying constructor
     * is 0, the supplied {@code initargs} array may be of length 0 or null.
     * 2.如果底层构造函数所需的形式参数数量为 0，则提供的initargs数组的长度可能为 0 或 null
     * <p>If the constructor's declaring class is an inner class in a
     * non-static context, the first argument to the constructor needs
     * to be the enclosing instance; see section 15.9.3 of
     * <cite>The Java&trade; Language Specification</cite>.
     * 3.如果构造函数的声明类是非静态上下文中的内部类，则构造函数的第一个参数需要是封闭实例；
     * 请参阅 Java™ 语言规范的第 15.9.3 节。
     * <p>If the required access and argument checks succeed and the
     * instantiation will proceed, the constructor's declaring class
     * is initialized if it has not already been initialized.
     * 4.如果所需的访问和参数检查成功并且实例化将继续进行，并且构造函数的声明类尚未初始化，则会对其进行初始化
     * <p>If the constructor completes normally, returns the newly
     * created and initialized instance.
     * 5.如果构造函数正常完成，则返回新创建和初始化的实例
     * @param initargs array of objects to be passed as arguments to
     * the constructor call; values of primitive types are wrapped in
     * a wrapper object of the appropriate type (e.g. a {@code float}
     * in a {@link java.lang.Float Float})
     *
     * @return a new object created by calling the constructor
     * this object represents
     *
     * @exception IllegalAccessException    if this {@code Constructor} object
     *              is enforcing Java language access control and the underlying
     *              constructor is inaccessible.
     * @exception IllegalArgumentException  if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion; if
     *              this constructor pertains to an enum type.
     * @exception InstantiationException    if the class that declares the
     *              underlying constructor represents an abstract class.
     * @exception InvocationTargetException if the underlying constructor
     *              throws an exception.
     * @exception ExceptionInInitializerError if the initialization provoked
     *              by this method fails.
     */
    @CallerSensitive
    public T newInstance(Object ... initargs)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        if (!override) {
            if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
                Class<?> caller = Reflection.getCallerClass();
                checkAccess(caller, clazz, null, modifiers);
            }
        }
        if ((clazz.getModifiers() & Modifier.ENUM) != 0)
            throw new IllegalArgumentException("Cannot reflectively create enum objects");
        ConstructorAccessor ca = constructorAccessor;   // read volatile
        if (ca == null) {
            ca = acquireConstructorAccessor();
        }
        @SuppressWarnings("unchecked")
        T inst = (T) ca.newInstance(initargs);
        return inst;
    }

    /**
     * {@inheritDoc}
     * @since 1.5
     */
    @Override
    public boolean isVarArgs() {
        return super.isVarArgs();
    }

    /**
     * {@inheritDoc}
     * @jls 13.1 The Form of a Binary
     * @since 1.5
     */
    @Override
    public boolean isSynthetic() {
        return super.isSynthetic();
    }

    // NOTE that there is no synchronization used here. It is correct
    // (though not efficient) to generate more than one
    // ConstructorAccessor for a given Constructor. However, avoiding
    // synchronization will probably make the implementation more
    // scalable.
    //注意这里没有使用同步。为给定的构造函数生成多个构造函数访问器是正确的（虽然效率不高）。但是，避免同步可能会使实现更具可扩展性
    private ConstructorAccessor acquireConstructorAccessor() {
        // First check to see if one has been created yet, and take it
        // if so.
        //首先检查是否已创建，如果已创建，则使用它
        ConstructorAccessor tmp = null;
        if (root != null) tmp = root.getConstructorAccessor();
        if (tmp != null) {
            constructorAccessor = tmp;
        } else {
            // Otherwise fabricate one and propagate it up to the root
            //否则制作一个并将其传播到根
            tmp = reflectionFactory.newConstructorAccessor(this);
            setConstructorAccessor(tmp);
        }

        return tmp;
    }

    // Returns ConstructorAccessor for this Constructor object, not
    // looking up the chain to the root
    //返回此 Constructor 对象的 ConstructorAccessor，而不是查找到根的链
    ConstructorAccessor getConstructorAccessor() {
        return constructorAccessor;
    }

    // Sets the ConstructorAccessor for this Constructor object and
    // (recursively) its root
    //设置此 Constructor 对象的 ConstructorAccessor 和（递归地）其根
    void setConstructorAccessor(ConstructorAccessor accessor) {
        constructorAccessor = accessor;
        // Propagate up
        if (root != null) {
            root.setConstructorAccessor(accessor);
        }
    }

    int getSlot() {
        return slot;
    }

    String getSignature() {
        return signature;
    }

    byte[] getRawAnnotations() {
        return annotations;
    }

    byte[] getRawParameterAnnotations() {
        return parameterAnnotations;
    }


    /**
     * {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     * @since 1.5
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return super.getAnnotation(annotationClass);
    }

    /**
     * {@inheritDoc}
     * @since 1.5
     */
    public Annotation[] getDeclaredAnnotations()  {
        return super.getDeclaredAnnotations();
    }

    /**
     * {@inheritDoc}
     * @since 1.5
     */
    @Override
    public Annotation[][] getParameterAnnotations() {
        return sharedGetParameterAnnotations(parameterTypes, parameterAnnotations);
    }

    @Override
    void handleParameterNumberMismatch(int resultLength, int numParameters) {
        Class<?> declaringClass = getDeclaringClass();
        if (declaringClass.isEnum() ||
            declaringClass.isAnonymousClass() ||
            declaringClass.isLocalClass() )
            return ; // Can't do reliable parameter counting 无法进行可靠的参数计数
        else {
            if (!declaringClass.isMemberClass() || // top-level
                // Check for the enclosing instance parameter for
                // non-static member classes
                (declaringClass.isMemberClass() &&
                 ((declaringClass.getModifiers() & Modifier.STATIC) == 0)  &&
                 resultLength + 1 != numParameters) ) {
                throw new AnnotationFormatError(
                          "Parameter annotations don't match number of parameters");
            }
        }
    }

    /**
     * {@inheritDoc}
     * @since 1.8
     */
    @Override
    public AnnotatedType getAnnotatedReturnType() {
        return getAnnotatedReturnType0(getDeclaringClass());
    }

    /**
     * {@inheritDoc}
     * @since 1.8
     */
    @Override
    public AnnotatedType getAnnotatedReceiverType() {
        if (getDeclaringClass().getEnclosingClass() == null)
            return super.getAnnotatedReceiverType();

        return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(),
                sun.misc.SharedSecrets.getJavaLangAccess().
                        getConstantPool(getDeclaringClass()),
                this,
                getDeclaringClass(),
                getDeclaringClass().getEnclosingClass(),
                TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
    }
}
