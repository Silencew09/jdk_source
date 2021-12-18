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
import sun.reflect.MethodAccessor;
import sun.reflect.Reflection;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.scope.MethodScope;
import sun.reflect.annotation.AnnotationType;
import sun.reflect.annotation.AnnotationParser;
import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.nio.ByteBuffer;

/**
 * A {@code Method} provides information about, and access to, a single method
 * on a class or interface.  The reflected method may be a class method
 * or an instance method (including an abstract method).
 * 1.Method提供有关类或接口上的单个方法的信息和访问权限。反映的方法可以是类方法或实例方法（包括抽象方法）
 * <p>A {@code Method} permits widening conversions to occur when matching the
 * actual parameters to invoke with the underlying method's formal
 * parameters, but it throws an {@code IllegalArgumentException} if a
 * narrowing conversion would occur.
 * 2.Method允许在将要调用的实际参数与基础方法的形式参数匹配时发生扩大转换，
 * 但如果发生缩小转换，它会抛出 IllegalArgumentException
 * @see Member
 * @see java.lang.Class
 * @see java.lang.Class#getMethods()
 * @see java.lang.Class#getMethod(String, Class[])
 * @see java.lang.Class#getDeclaredMethods()
 * @see java.lang.Class#getDeclaredMethod(String, Class[])
 *
 * @author Kenneth Russell
 * @author Nakul Saraiya
 */
public final class Method extends Executable {
    private Class<?>            clazz;
    private int                 slot;
    // This is guaranteed to be interned by the VM in the 1.4
    // reflection implementation
    //这保证在 1.4 反射实现中由 VM interned
    private String              name;
    private Class<?>            returnType;
    private Class<?>[]          parameterTypes;
    private Class<?>[]          exceptionTypes;
    private int                 modifiers;
    // Generics and annotations support
    //泛型和注解支持
    private transient String              signature;
    // generic info repository; lazily initialized
    //通用信息存储库；延迟初始化
    private transient MethodRepository genericInfo;
    private byte[]              annotations;
    private byte[]              parameterAnnotations;
    private byte[]              annotationDefault;
    private volatile MethodAccessor methodAccessor;
    // For sharing of MethodAccessors. This branching structure is
    // currently only two levels deep (i.e., one root Method and
    // potentially many Method objects pointing to it.)
    //用于共享 MethodAccessors。这个分支结构目前只有两层深
    // （即，一个根 Method 和可能指向它的许多 Method 对象。）
    //
    // If this branching structure would ever contain cycles, deadlocks can
    // occur in annotation code.
    //如果此分支结构包含循环，则注释代码中可能会发生死锁。
    private Method              root;

    // Generics infrastructure
    //泛型基础设施
    private String getGenericSignature() {return signature;}

    // Accessor for factory
    //工厂配件
    private GenericsFactory getFactory() {
        // create scope and factory
        //创建范围和工厂
        return CoreReflectionFactory.make(this, MethodScope.make(this));
    }

    // Accessor for generic info repository
    //通用信息存储库的访问器
    @Override
    MethodRepository getGenericInfo() {
        // lazily initialize repository if necessary
        //如有必要，延迟初始化存储库
        if (genericInfo == null) {
            // create and cache generic info repository
            //创建和缓存通用信息存储库
            genericInfo = MethodRepository.make(getGenericSignature(),
                                                getFactory());
        }
        return genericInfo; //return cached repository 返回缓存的存储库
    }

    /**
     * Package-private constructor used by ReflectAccess to enable
     * instantiation of these objects in Java code from the java.lang
     * package via sun.reflect.LangReflectAccess.
     * ReflectAccess 使用的包私有构造函数，通过 sun.reflect.LangReflectAccess
     * 从 java.lang 包启用 Java 代码中这些对象的实例化
     */
    Method(Class<?> declaringClass,
           String name,
           Class<?>[] parameterTypes,
           Class<?> returnType,
           Class<?>[] checkedExceptions,
           int modifiers,
           int slot,
           String signature,
           byte[] annotations,
           byte[] parameterAnnotations,
           byte[] annotationDefault) {
        this.clazz = declaringClass;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.exceptionTypes = checkedExceptions;
        this.modifiers = modifiers;
        this.slot = slot;
        this.signature = signature;
        this.annotations = annotations;
        this.parameterAnnotations = parameterAnnotations;
        this.annotationDefault = annotationDefault;
    }

    /**
     * Package-private routine (exposed to java.lang.Class via
     * ReflectAccess) which returns a copy of this Method. The copy's
     * "root" field points to this Method.
     * 包私有例程（通过 ReflectAccess 暴露给 java.lang.Class）返回此方法的副本。副本的“根”字段指向此方法。
     */
    Method copy() {
        // This routine enables sharing of MethodAccessor objects
        // among Method objects which refer to the same underlying
        // method in the VM. (All of this contortion is only necessary
        // because of the "accessibility" bit in AccessibleObject,
        // which implicitly requires that new java.lang.reflect
        // objects be fabricated for each reflective call on Class
        // objects.)
        //此例程允许在引用 VM 中相同底层方法的 Method 对象之间共享 MethodAccessor 对象。
        // （所有这些扭曲都是必要的，因为 AccessibleObject 中的“可访问性”位，它隐含地要求为 Class
        // 对象的每个反射调用制造新的 java.lang.reflect 对象。）
        if (this.root != null)
            throw new IllegalArgumentException("Can not copy a non-root Method");

        Method res = new Method(clazz, name, parameterTypes, returnType,
                                exceptionTypes, modifiers, slot, signature,
                                annotations, parameterAnnotations, annotationDefault);
        res.root = this;
        // Might as well eagerly propagate this if already present
        //如果已经存在，不妨急切地传播它
        res.methodAccessor = methodAccessor;
        return res;
    }

    /**
     * Used by Excecutable for annotation sharing.
     * 由 Executable 用于注释共享
     */
    @Override
    Executable getRoot() {
        return root;
    }

    @Override
    boolean hasGenericInformation() {
        return (getGenericSignature() != null);
    }

    @Override
    byte[] getAnnotationBytes() {
        return annotations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getDeclaringClass() {
        return clazz;
    }

    /**
     * Returns the name of the method represented by this {@code Method}
     * object, as a {@code String}.
     * 返回此Method对象表示的方法的名称，作为String
     */
    @Override
    public String getName() {
        return name;
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
    public TypeVariable<Method>[] getTypeParameters() {
        if (getGenericSignature() != null)
            return (TypeVariable<Method>[])getGenericInfo().getTypeParameters();
        else
            return (TypeVariable<Method>[])new TypeVariable[0];
    }

    /**
     * Returns a {@code Class} object that represents the formal return type
     * of the method represented by this {@code Method} object.
     * 返回一个Class对象，该对象表示此Method对象表示的方法的正式返回类型
     * @return the return type for the method this object represents
     */
    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * Returns a {@code Type} object that represents the formal return
     * type of the method represented by this {@code Method} object.
     * 1.返回一个Type对象，该对象表示此Method对象表示的方法的正式返回类型
     * <p>If the return type is a parameterized type,
     * the {@code Type} object returned must accurately reflect
     * the actual type parameters used in the source code.
     * 2.如果返回类型是参数化类型，则返回的Type对象必须准确反映源代码中使用的实际类型参数
     *
     * <p>If the return type is a type variable or a parameterized type, it
     * is created. Otherwise, it is resolved.
     * 3.如果返回类型是类型变量或参数化类型，则创建它。否则就解决了。
     * @return  a {@code Type} object that represents the formal return
     *     type of the underlying  method
     * @throws GenericSignatureFormatError
     *     if the generic method signature does not conform to the format
     *     specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException if the underlying method's
     *     return type refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if the
     *     underlying method's return typed refers to a parameterized
     *     type that cannot be instantiated for any reason
     * @since 1.5
     */
    public Type getGenericReturnType() {
      if (getGenericSignature() != null) {
        return getGenericInfo().getReturnType();
      } else { return getReturnType();}
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
     * Compares this {@code Method} against the specified object.  Returns
     * true if the objects are the same.  Two {@code Methods} are the same if
     * they were declared by the same class and have the same name
     * and formal parameter types and return type.
     * 将此Method与指定的对象进行比较。如果对象相同，则返回 true。
     * 如果两个Methods是由同一个类声明并且具有相同的名称和形参类型和返回类型，则它们是相同的
     */
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Method) {
            Method other = (Method)obj;
            if ((getDeclaringClass() == other.getDeclaringClass())
                && (getName() == other.getName())) {
                if (!returnType.equals(other.getReturnType()))
                    return false;
                return equalParamTypes(parameterTypes, other.parameterTypes);
            }
        }
        return false;
    }

    /**
     * Returns a hashcode for this {@code Method}.  The hashcode is computed
     * as the exclusive-or of the hashcodes for the underlying
     * method's declaring class name and the method's name.
     * 返回此Method的哈希码。哈希码计算为底层方法的声明类名称和方法名称的哈希码的异或
     */
    public int hashCode() {
        return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
    }

    /**
     * Returns a string describing this {@code Method}.  The string is
     * formatted as the method access modifiers, if any, followed by
     * the method return type, followed by a space, followed by the
     * class declaring the method, followed by a period, followed by
     * the method name, followed by a parenthesized, comma-separated
     * list of the method's formal parameter types. If the method
     * throws checked exceptions, the parameter list is followed by a
     * space, followed by the word throws followed by a
     * comma-separated list of the thrown exception types.
     * 1.返回描述此Method的字符串。字符串被格式化为方法访问修饰符（如果有），后跟方法返回类型，
     * 后跟一个空格，后跟声明方法的类，后跟一个句点，后跟方法名称，后跟一个括号，逗号- 方法的形式参数类型的分隔列表。
     * 如果该方法抛出已检查的异常，则参数列表后跟一个空格，后跟单词 throws，后跟以逗号分隔的抛出异常类型列表
     * For example:
     * <pre>
     *    public boolean java.lang.Object.equals(java.lang.Object)
     * </pre>
     *
     * <p>The access modifiers are placed in canonical order as
     * specified by "The Java Language Specification".  This is
     * {@code public}, {@code protected} or {@code private} first,
     * and then other modifiers in the following order:
     * {@code abstract}, {@code default}, {@code static}, {@code final},
     * {@code synchronized}, {@code native}, {@code strictfp}.
     * 2.访问修饰符按照“Java 语言规范”指定的规范顺序放置。首先是public、protected或private，
     * 然后是其他修饰符，顺序如下：abstract、default、static、final、synchronized、native、strictfp
     * @return a string describing this {@code Method}
     *
     * @jls 8.4.3 Method Modifiers
     */
    public String toString() {
        return sharedToString(Modifier.methodModifiers(),
                              isDefault(),
                              parameterTypes,
                              exceptionTypes);
    }

    @Override
    void specificToStringHeader(StringBuilder sb) {
        sb.append(getReturnType().getTypeName()).append(' ');
        sb.append(getDeclaringClass().getTypeName()).append('.');
        sb.append(getName());
    }

    /**
     * Returns a string describing this {@code Method}, including
     * type parameters.  The string is formatted as the method access
     * modifiers, if any, followed by an angle-bracketed
     * comma-separated list of the method's type parameters, if any,
     * followed by the method's generic return type, followed by a
     * space, followed by the class declaring the method, followed by
     * a period, followed by the method name, followed by a
     * parenthesized, comma-separated list of the method's generic
     * formal parameter types.
     * 1.返回描述此Method的字符串，包括类型参数。字符串被格式化为方法访问修饰符（如果有），
     * 后跟一个用尖括号括起来的逗号分隔的方法类型参数列表（如果有），然后是方法的通用返回类型，后跟一个空格，
     * 后跟声明的类方法，后跟一个句点，后跟方法名称，后跟括号中、逗号分隔的方法通用形式参数类型列表
     * If this method was declared to take a variable number of
     * arguments, instead of denoting the last parameter as
     * "<tt><i>Type</i>[]</tt>", it is denoted as
     * "<tt><i>Type</i>...</tt>".
     * 2.如果此方法被声明为采用可变数量的参数，而不是将最后一个参数表示为“Type[]”，而是表示为“Type ...”
     * A space is used to separate access modifiers from one another
     * and from the type parameters or return type.  If there are no
     * type parameters, the type parameter list is elided; if the type
     * parameter list is present, a space separates the list from the
     * class name.  If the method is declared to throw exceptions, the
     * parameter list is followed by a space, followed by the word
     * throws followed by a comma-separated list of the generic thrown
     * exception types.
     * 3.空格用于将访问修饰符彼此分隔开，并与类型参数或返回类型分隔开。如果没有类型参数，
     * 则省略类型参数列表；如果类型参数列表存在，一个空格将列表与类名分开。如果该方法声明为抛出异常，
     * 则参数列表后跟一个空格，后跟单词 throws，后跟以逗号分隔的泛型抛出异常类型列表
     * <p>The access modifiers are placed in canonical order as
     * specified by "The Java Language Specification".  This is
     * {@code public}, {@code protected} or {@code private} first,
     * and then other modifiers in the following order:
     * {@code abstract}, {@code default}, {@code static}, {@code final},
     * {@code synchronized}, {@code native}, {@code strictfp}.
     * 4.访问修饰符按照“Java 语言规范”指定的规范顺序放置。首先是public、protected或private，
     * 然后是其他修饰符，顺序如下abstract、default、static、final、synchronized、native、strictfp
     * @return a string describing this {@code Method},
     * include type parameters
     *
     * @since 1.5
     *
     * @jls 8.4.3 Method Modifiers
     */
    @Override
    public String toGenericString() {
        return sharedToGenericString(Modifier.methodModifiers(), isDefault());
    }

    @Override
    void specificToGenericStringHeader(StringBuilder sb) {
        Type genRetType = getGenericReturnType();
        sb.append(genRetType.getTypeName()).append(' ');
        sb.append(getDeclaringClass().getTypeName()).append('.');
        sb.append(getName());
    }

    /**
     * Invokes the underlying method represented by this {@code Method}
     * object, on the specified object with the specified parameters.
     * Individual parameters are automatically unwrapped to match
     * primitive formal parameters, and both primitive and reference
     * parameters are subject to method invocation conversions as
     * necessary.
     * 1.在具有指定参数的指定对象上调用此Method对象表示的基础方法。
     * 单个参数会自动解包以匹配原始形式参数，并且原始参数和引用参数都根据需要进行方法调用转换
     * <p>If the underlying method is static, then the specified {@code obj}
     * argument is ignored. It may be null.
     * 2.如果底层方法是静态的，则忽略指定的obj参数。它可能为空
     * <p>If the number of formal parameters required by the underlying method is
     * 0, the supplied {@code args} array may be of length 0 or null.
     * 3.如果底层方法所需的形参数量为 0，则提供的args数组的长度可能为 0 或 null
     * <p>If the underlying method is an instance method, it is invoked
     * using dynamic method lookup as documented in The Java Language
     * Specification, Second Edition, section 15.12.4.4; in particular,
     * overriding based on the runtime type of the target object will occur.
     * 4.如果底层方法是实例方法，则使用动态方法查找调用它，如 The Java Language Specification,
     * Second Edition, section 15.12.4.4 中所述；特别是，将发生基于目标对象的运行时类型的覆盖
     * <p>If the underlying method is static, the class that declared
     * the method is initialized if it has not already been initialized.
     * 5.如果底层方法是静态的，则声明该方法的类在尚未初始化的情况下会被初始化。
     * <p>If the method completes normally, the value it returns is
     * returned to the caller of invoke; if the value has a primitive
     * type, it is first appropriately wrapped in an object. However,
     * if the value has the type of an array of a primitive type, the
     * elements of the array are <i>not</i> wrapped in objects; in
     * other words, an array of primitive type is returned.  If the
     * underlying method return type is void, the invocation returns
     * null.
     * 6.如果方法正常完成，则返回值返回给invoke的调用者；如果该值具有原始类型，
     * 则首先将其适当地包装在一个对象中。但是，如果该值具有原始类型数组的类型，
     * 则该数组的元素不包裹在对象中；换句话说，返回一个原始类型的数组。
     * 如果基础方法返回类型为 void，则调用返回 nul
     * @param obj  the object the underlying method is invoked from
     * @param args the arguments used for the method call
     * @return the result of dispatching the method represented by
     * this object on {@code obj} with parameters
     * {@code args}
     *
     * @exception IllegalAccessException    if this {@code Method} object
     *              is enforcing Java language access control and the underlying
     *              method is inaccessible.
     * @exception IllegalArgumentException  if the method is an
     *              instance method and the specified object argument
     *              is not an instance of the class or interface
     *              declaring the underlying method (or of a subclass
     *              or implementor thereof); if the number of actual
     *              and formal parameters differ; if an unwrapping
     *              conversion for primitive arguments fails; or if,
     *              after possible unwrapping, a parameter value
     *              cannot be converted to the corresponding formal
     *              parameter type by a method invocation conversion.
     * @exception InvocationTargetException if the underlying method
     *              throws an exception.
     * @exception NullPointerException      if the specified object is null
     *              and the method is an instance method.
     * @exception ExceptionInInitializerError if the initialization
     * provoked by this method fails.
     */
    @CallerSensitive
    public Object invoke(Object obj, Object... args)
        throws IllegalAccessException, IllegalArgumentException,
           InvocationTargetException
    {
        if (!override) {
            if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
                Class<?> caller = Reflection.getCallerClass();
                checkAccess(caller, clazz, obj, modifiers);
            }
        }
        MethodAccessor ma = methodAccessor;             // read volatile
        if (ma == null) {
            ma = acquireMethodAccessor();
        }
        return ma.invoke(obj, args);
    }

    /**
     * Returns {@code true} if this method is a bridge
     * method; returns {@code false} otherwise.
     * 如果此方法是桥接方法，则返回true；否则返回 false。
     * @return true if and only if this method is a bridge
     * method as defined by the Java Language Specification.
     * @since 1.5
     */
    public boolean isBridge() {
        return (getModifiers() & Modifier.BRIDGE) != 0;
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

    /**
     * Returns {@code true} if this method is a default
     * method; returns {@code false} otherwise.
     * 1.如果此方法是默认方法，则返回true；否则返回false
     * A default method is a public non-abstract instance method, that
     * is, a non-static method with a body, declared in an interface
     * type.
     * 2.默认方法是公共非抽象实例方法，即具有主体的非静态方法，在接口类型中声明。
     * @return true if and only if this method is a default
     * method as defined by the Java Language Specification.
     * @since 1.8
     */
    public boolean isDefault() {
        // Default methods are public non-abstract instance methods
        // declared in an interface.
        //默认方法是在接口中声明的公共非抽象实例方法
        return ((getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) ==
                Modifier.PUBLIC) && getDeclaringClass().isInterface();
    }

    // NOTE that there is no synchronization used here. It is correct
    // (though not efficient) to generate more than one MethodAccessor
    // for a given Method. However, avoiding synchronization will
    // probably make the implementation more scalable.
    //注意这里没有使用同步。为给定的方法生成多个 MethodAccessor 是正确的（虽然效率不高）。
    // 但是，避免同步可能会使实现更具可扩展性
    private MethodAccessor acquireMethodAccessor() {
        // First check to see if one has been created yet, and take it
        // if so 首先检查是否已经创建，如果创建则取走
        MethodAccessor tmp = null;
        if (root != null) tmp = root.getMethodAccessor();
        if (tmp != null) {
            methodAccessor = tmp;
        } else {
            // Otherwise fabricate one and propagate it up to the root
            //否则制作一个并将其传播到根
            tmp = reflectionFactory.newMethodAccessor(this);
            setMethodAccessor(tmp);
        }

        return tmp;
    }

    // Returns MethodAccessor for this Method object, not looking up
    // the chain to the root
    //返回此 Method 对象的 MethodAccessor，而不是查找到根的链
    MethodAccessor getMethodAccessor() {
        return methodAccessor;
    }

    // Sets the MethodAccessor for this Method object and
    // (recursively) its root
    //设置此 Method 对象的 MethodAccessor 和（递归地）其根
    void setMethodAccessor(MethodAccessor accessor) {
        methodAccessor = accessor;
        // Propagate up
        //向上传播
        if (root != null) {
            root.setMethodAccessor(accessor);
        }
    }

    /**
     * Returns the default value for the annotation member represented by
     * this {@code Method} instance.  If the member is of a primitive type,
     * an instance of the corresponding wrapper type is returned. Returns
     * null if no default is associated with the member, or if the method
     * instance does not represent a declared member of an annotation type.
     * 1.返回此Method实例表示的注释成员的默认值。如果成员是原始类型，则返回相应包装器类型的实例。
     * 如果没有与成员关联的默认值，或者方法实例不表示注释类型的声明成员，则返回 null。
     * @return the default value for the annotation member represented
     *     by this {@code Method} instance.
     * @throws TypeNotPresentException if the annotation is of type
     *     {@link Class} and no definition can be found for the
     *     default class value.
     * @since  1.5
     */
    public Object getDefaultValue() {
        if  (annotationDefault == null)
            return null;
        Class<?> memberType = AnnotationType.invocationHandlerReturnType(
            getReturnType());
        Object result = AnnotationParser.parseMemberValue(
            memberType, ByteBuffer.wrap(annotationDefault),
            sun.misc.SharedSecrets.getJavaLangAccess().
                getConstantPool(getDeclaringClass()),
            getDeclaringClass());
        if (result instanceof sun.reflect.annotation.ExceptionProxy)
            throw new AnnotationFormatError("Invalid default: " + this);
        return result;
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

    /**
     * {@inheritDoc}
     * @since 1.8
     */
    @Override
    public AnnotatedType getAnnotatedReturnType() {
        return getAnnotatedReturnType0(getGenericReturnType());
    }

    @Override
    void handleParameterNumberMismatch(int resultLength, int numParameters) {
        throw new AnnotationFormatError("Parameter annotations don't match number of parameters");
    }
}
