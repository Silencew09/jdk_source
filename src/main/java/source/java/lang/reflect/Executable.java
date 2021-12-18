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

import java.lang.annotation.*;
import java.util.Map;
import java.util.Objects;
import sun.reflect.annotation.AnnotationParser;
import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.TypeAnnotationParser;
import sun.reflect.annotation.TypeAnnotation;
import sun.reflect.generics.repository.ConstructorRepository;

/**
 * A shared superclass for the common functionality of {@link Method}
 * and {@link Constructor}.
 * Method和 Constructor通用功能的共享超类。
 *
 * @since 1.8
 */
public abstract class Executable extends AccessibleObject
    implements Member, GenericDeclaration {
    /*
     * Only grant package-visibility to the constructor.
     * 只向构造函数授予包可见性
     */
    Executable() {}

    /**
     * Accessor method to allow code sharing
     * 允许代码共享的访问器方法
     */
    abstract byte[] getAnnotationBytes();

    /**
     * Accessor method to allow code sharing
     * 允许代码共享的访问器方法
     */
    abstract Executable getRoot();

    /**
     * Does the Executable have generic information.
     * 可执行文件是否具有遗传信息
     */
    abstract boolean hasGenericInformation();

    abstract ConstructorRepository getGenericInfo();

    boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        /* Avoid unnecessary cloning */
        //避免不必要的克隆
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    //解析参数注解
    Annotation[][] parseParameterAnnotations(byte[] parameterAnnotations) {
        return AnnotationParser.parseParameterAnnotations(
               parameterAnnotations,
               sun.misc.SharedSecrets.getJavaLangAccess().
               getConstantPool(getDeclaringClass()),
               getDeclaringClass());
    }

    void separateWithCommas(Class<?>[] types, StringBuilder sb) {
        for (int j = 0; j < types.length; j++) {
            sb.append(types[j].getTypeName());
            if (j < (types.length - 1))
                sb.append(",");
        }

    }

    //如果非零打印修饰符
    void printModifiersIfNonzero(StringBuilder sb, int mask, boolean isDefault) {
        int mod = getModifiers() & mask;

        if (mod != 0 && !isDefault) {
            sb.append(Modifier.toString(mod)).append(' ');
        } else {
            int access_mod = mod & Modifier.ACCESS_MODIFIERS;
            if (access_mod != 0)
                sb.append(Modifier.toString(access_mod)).append(' ');
            if (isDefault)
                sb.append("default ");
            mod = (mod & ~Modifier.ACCESS_MODIFIERS);
            if (mod != 0)
                sb.append(Modifier.toString(mod)).append(' ');
        }
    }

    String sharedToString(int modifierMask,
                          boolean isDefault,
                          Class<?>[] parameterTypes,
                          Class<?>[] exceptionTypes) {
        try {
            StringBuilder sb = new StringBuilder();

            printModifiersIfNonzero(sb, modifierMask, isDefault);
            specificToStringHeader(sb);

            sb.append('(');
            separateWithCommas(parameterTypes, sb);
            sb.append(')');
            if (exceptionTypes.length > 0) {
                sb.append(" throws ");
                separateWithCommas(exceptionTypes, sb);
            }
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    /**
     * Generate toString header information specific to a method or
     * constructor.
     * 生成特定于方法或构造函数的 toString 标头信息
     */
    abstract void specificToStringHeader(StringBuilder sb);

    String sharedToGenericString(int modifierMask, boolean isDefault) {
        try {
            StringBuilder sb = new StringBuilder();

            printModifiersIfNonzero(sb, modifierMask, isDefault);

            TypeVariable<?>[] typeparms = getTypeParameters();
            if (typeparms.length > 0) {
                boolean first = true;
                sb.append('<');
                for(TypeVariable<?> typeparm: typeparms) {
                    if (!first)
                        sb.append(',');
                    // Class objects can't occur here; no need to test
                    // and call Class.getName().
                    sb.append(typeparm.toString());
                    first = false;
                }
                sb.append("> ");
            }

            specificToGenericStringHeader(sb);

            sb.append('(');
            Type[] params = getGenericParameterTypes();
            for (int j = 0; j < params.length; j++) {
                String param = params[j].getTypeName();
                if (isVarArgs() && (j == params.length - 1)) // replace T[] with T...
                    param = param.replaceFirst("\\[\\]$", "...");
                sb.append(param);
                if (j < (params.length - 1))
                    sb.append(',');
            }
            sb.append(')');
            Type[] exceptions = getGenericExceptionTypes();
            if (exceptions.length > 0) {
                sb.append(" throws ");
                for (int k = 0; k < exceptions.length; k++) {
                    sb.append((exceptions[k] instanceof Class)?
                              ((Class)exceptions[k]).getName():
                              exceptions[k].toString());
                    if (k < (exceptions.length - 1))
                        sb.append(',');
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    /**
     * Generate toGenericString header information specific to a
     * method or constructor.
     * 生成特定于方法或构造函数的 toGenericString 标头信息。
     */
    abstract void specificToGenericStringHeader(StringBuilder sb);

    /**
     * Returns the {@code Class} object representing the class or interface
     * that declares the executable represented by this object.
     * 返回Class对象，该对象表示声明此对象表示的可执行文件的类或接口。
     */
    public abstract Class<?> getDeclaringClass();

    /**
     * Returns the name of the executable represented by this object.
     * 返回由此对象表示的可执行文件的名称
     */
    public abstract String getName();

    /**
     * Returns the Java language {@linkplain Modifier modifiers} for
     * the executable represented by this object.
     * 返回此对象表示的可执行文件的 Java 语言Modifier。
     */
    public abstract int getModifiers();

    /**
     * Returns an array of {@code TypeVariable} objects that represent the
     * type variables declared by the generic declaration represented by this
     * {@code GenericDeclaration} object, in declaration order.  Returns an
     * array of length 0 if the underlying generic declaration declares no type
     * variables.
     * 返回一个TypeVariable对象数组，这些对象按声明顺序表示由此GenericDeclaration
     * 对象表示的泛型声明所声明的类型变量。如果底层泛型声明没有声明类型变量，则返回长度为 0 的数组
     * @return an array of {@code TypeVariable} objects that represent
     *     the type variables declared by this generic declaration
     * @throws GenericSignatureFormatError if the generic
     *     signature of this generic declaration does not conform to
     *     the format specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     */
    public abstract TypeVariable<?>[] getTypeParameters();

    /**
     * Returns an array of {@code Class} objects that represent the formal
     * parameter types, in declaration order, of the executable
     * represented by this object.  Returns an array of length
     * 0 if the underlying executable takes no parameters.
     * 返回一个Class对象数组，这些对象按声明顺序表示由此对象表示的可执行文件的形式参数类型。
     * 如果底层可执行文件不带参数，则返回长度为 0 的数组
     * @return the parameter types for the executable this object
     * represents
     */
    public abstract Class<?>[] getParameterTypes();

    /**
     * Returns the number of formal parameters (whether explicitly
     * declared or implicitly declared or neither) for the executable
     * represented by this object.
     * 1.返回由此对象表示的可执行文件的形式参数的数量（无论是显式声明还是隐式声明或两者都不是)
     *
     * @return The number of formal parameters for the executable this
     * object represents
     */
    public int getParameterCount() {
        throw new AbstractMethodError();
    }

    /**
     * Returns an array of {@code Type} objects that represent the formal
     * parameter types, in declaration order, of the executable represented by
     * this object. Returns an array of length 0 if the
     * underlying executable takes no parameters.
     * 1.返回一个Type对象数组，这些对象按声明顺序表示由此对象表示的可执行文件的形式参数类型。
     * 如果底层可执行文件不带参数，则返回长度为 0 的数组。
     * <p>If a formal parameter type is a parameterized type,
     * the {@code Type} object returned for it must accurately reflect
     * the actual type parameters used in the source code.
     * 2.如果形式参数类型是参数化类型，则为其返回的Type对象必须准确反映源代码中使用的实际类型参数。
     * <p>If a formal parameter type is a type variable or a parameterized
     * type, it is created. Otherwise, it is resolved.
     * 3.如果形式参数类型是类型变量或参数化类型，则创建它。否则就解决了
     * @return an array of {@code Type}s that represent the formal
     *     parameter types of the underlying executable, in declaration order
     * @throws GenericSignatureFormatError
     *     if the generic method signature does not conform to the format
     *     specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException if any of the parameter
     *     types of the underlying executable refers to a non-existent type
     *     declaration
     * @throws MalformedParameterizedTypeException if any of
     *     the underlying executable's parameter types refer to a parameterized
     *     type that cannot be instantiated for any reason
     */
    public Type[] getGenericParameterTypes() {
        if (hasGenericInformation())
            return getGenericInfo().getParameterTypes();
        else
            return getParameterTypes();
    }

    /**
     * Behaves like {@code getGenericParameterTypes}, but returns type
     * information for all parameters, including synthetic parameters.
     * 行为类似于getGenericParameterTypes，但返回所有参数的类型信息，包括合成参数
     */
    Type[] getAllGenericParameterTypes() {
        final boolean genericInfo = hasGenericInformation();

        // Easy case: we don't have generic parameter information.  In
        // this case, we just return the result of
        // getParameterTypes().
        //简单的情况：我们没有通用参数信息。在这种情况下，
        // 我们只返回 getParameterTypes() 的结果
        if (!genericInfo) {
            return getParameterTypes();
        } else {
            final boolean realParamData = hasRealParameterData();
            final Type[] genericParamTypes = getGenericParameterTypes();
            final Type[] nonGenericParamTypes = getParameterTypes();
            final Type[] out = new Type[nonGenericParamTypes.length];
            final Parameter[] params = getParameters();
            int fromidx = 0;
            // If we have real parameter data, then we use the
            // synthetic and mandate flags to our advantage.
            //如果我们有真实的参数数据，那么我们会使用合成和授权标志来发挥我们的优势
            if (realParamData) {
                for (int i = 0; i < out.length; i++) {
                    final Parameter param = params[i];
                    if (param.isSynthetic() || param.isImplicit()) {
                        // If we hit a synthetic or mandated parameter,
                        // use the non generic parameter info.
                        //如果我们命中合成或强制参数，请使用非通用参数信息。
                        out[i] = nonGenericParamTypes[i];
                    } else {
                        // Otherwise, use the generic parameter info.
                        //否则，使用通用参数信息。
                        out[i] = genericParamTypes[fromidx];
                        fromidx++;
                    }
                }
            } else {
                // Otherwise, use the non-generic parameter data.
                // Without method parameter reflection data, we have
                // no way to figure out which parameters are
                // synthetic/mandated, thus, no way to match up the
                // indexes.
                //否则，使用非通用参数数据。没有方法参数反射数据，我们无法判断哪些参数是合成授权的，因此无法匹配索引
                return genericParamTypes.length == nonGenericParamTypes.length ?
                    genericParamTypes : nonGenericParamTypes;
            }
            return out;
        }
    }

    /**
     * Returns an array of {@code Parameter} objects that represent
     * all the parameters to the underlying executable represented by
     * this object.  Returns an array of length 0 if the executable
     * has no parameters.
     * 1.返回一个Parameter对象数组，这些对象表示此对象表示的底层可执行文件的所有参数。
     * 如果可执行文件没有参数，则返回长度为 0 的数组
     * <p>The parameters of the underlying executable do not necessarily
     * have unique names, or names that are legal identifiers in the
     * Java programming language (JLS 3.8).
     * 2.底层可执行文件的参数不一定具有唯一的名称，或者是 Java 编程语言 (JLS 3.8) 中合法标识符的名称
     * @throws MalformedParametersException if the class file contains
     * a MethodParameters attribute that is improperly formatted.
     * @return an array of {@code Parameter} objects representing all
     * the parameters to the executable this object represents.
     */
    public Parameter[] getParameters() {
        // TODO: This may eventually need to be guarded by security
        // mechanisms similar to those in Field, Method, etc.
        //
        // Need to copy the cached array to prevent users from messing
        // with it.  Since parameters are immutable, we can
        // shallow-copy.
        //这可能最终需要由类似于 Field、Method 等中的安全机制来保护。
        // 需要复制缓存的数组以防止用户对其进行混淆。由于参数是不可变的，我们可以进行浅拷贝
        return privateGetParameters().clone();
    }

    private Parameter[] synthesizeAllParams() {
        final int realparams = getParameterCount();
        final Parameter[] out = new Parameter[realparams];
        for (int i = 0; i < realparams; i++)
            // TODO: is there a way to synthetically derive the
            // modifiers?  Probably not in the general case, since
            // we'd have no way of knowing about them, but there
            // may be specific cases.
            //有没有办法综合导出修饰符？
            // 可能不是在一般情况下，因为我们无法了解它们，但可能存在特定情况
            out[i] = new Parameter("arg" + i, 0, this, i);
        return out;
    }

    private void verifyParameters(final Parameter[] parameters) {
        final int mask = Modifier.FINAL | Modifier.SYNTHETIC | Modifier.MANDATED;

        if (getParameterTypes().length != parameters.length)
            throw new MalformedParametersException("Wrong number of parameters in MethodParameters attribute");

        for (Parameter parameter : parameters) {
            final String name = parameter.getRealName();
            final int mods = parameter.getModifiers();

            if (name != null) {
                if (name.isEmpty() || name.indexOf('.') != -1 ||
                    name.indexOf(';') != -1 || name.indexOf('[') != -1 ||
                    name.indexOf('/') != -1) {
                    throw new MalformedParametersException("Invalid parameter name \"" + name + "\"");
                }
            }

            if (mods != (mods & mask)) {
                throw new MalformedParametersException("Invalid parameter modifiers");
            }
        }
    }

    private Parameter[] privateGetParameters() {
        // Use tmp to avoid multiple writes to a volatile.
        //使用 tmp 避免多次写入 volatile
        Parameter[] tmp = parameters;

        if (tmp == null) {

            // Otherwise, go to the JVM to get them
            //否则，去JVM获取它们
            try {
                tmp = getParameters0();
            } catch(IllegalArgumentException e) {
                // Rethrow ClassFormatErrors
                //重新抛出 ClassFormatErrors
                throw new MalformedParametersException("Invalid constant pool index");
            }

            // If we get back nothing, then synthesize parameters
            //如果我们什么都没有返回，则合成参数
            if (tmp == null) {
                hasRealParameterData = false;
                tmp = synthesizeAllParams();
            } else {
                hasRealParameterData = true;
                verifyParameters(tmp);
            }

            parameters = tmp;
        }

        return tmp;
    }

    //有实参数据
    boolean hasRealParameterData() {
        // If this somehow gets called before parameters gets
        // initialized, force it into existence.
        //如果在参数初始化之前以某种方式调用了它，则强制它存在
        if (parameters == null) {
            privateGetParameters();
        }
        return hasRealParameterData;
    }

    private transient volatile boolean hasRealParameterData;
    private transient volatile Parameter[] parameters;

    private native Parameter[] getParameters0();
    native byte[] getTypeAnnotationBytes0();

    // Needed by reflectaccess 反射访问需要
    byte[] getTypeAnnotationBytes() {
        return getTypeAnnotationBytes0();
    }

    /**
     * Returns an array of {@code Class} objects that represent the
     * types of exceptions declared to be thrown by the underlying
     * executable represented by this object.  Returns an array of
     * length 0 if the executable declares no exceptions in its {@code
     * throws} clause.
     * 返回一个Class对象数组，这些对象表示声明为由该对象表示的底层可执行文件抛出的异常类型。
     * 如果可执行文件在其throws子句中声明没有异常，则返回长度为 0 的数组。
     * @return the exception types declared as being thrown by the
     * executable this object represents
     */
    public abstract Class<?>[] getExceptionTypes();

    /**
     * Returns an array of {@code Type} objects that represent the
     * exceptions declared to be thrown by this executable object.
     * Returns an array of length 0 if the underlying executable declares
     * no exceptions in its {@code throws} clause.
     * 1.返回一个Type对象数组，这些对象表示此可执行对象声明要抛出的异常。
     * 如果底层可执行文件在其throws子句中声明没有异常，则返回长度为 0 的数组。
     * <p>If an exception type is a type variable or a parameterized
     * type, it is created. Otherwise, it is resolved.
     * 2.如果异常类型是类型变量或参数化类型，则创建它。否则就解决了
     * @return an array of Types that represent the exception types
     *     thrown by the underlying executable
     * @throws GenericSignatureFormatError
     *     if the generic method signature does not conform to the format
     *     specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException if the underlying executable's
     *     {@code throws} clause refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if
     *     the underlying executable's {@code throws} clause refers to a
     *     parameterized type that cannot be instantiated for any reason
     */
    public Type[] getGenericExceptionTypes() {
        Type[] result;
        if (hasGenericInformation() &&
            ((result = getGenericInfo().getExceptionTypes()).length > 0))
            return result;
        else
            return getExceptionTypes();
    }

    /**
     * Returns a string describing this {@code Executable}, including
     * any type parameters.
     * 返回描述此Executable的字符串，包括任何类型参数
     * @return a string describing this {@code Executable}, including
     * any type parameters
     */
    public abstract String toGenericString();

    /**
     * Returns {@code true} if this executable was declared to take a
     * variable number of arguments; returns {@code false} otherwise.
     * 如果此可执行文件被声明为采用可变数量的参数，则返回true；否则返回false。
     * @return {@code true} if an only if this executable was declared
     * to take a variable number of arguments.
     */
    public boolean isVarArgs()  {
        return (getModifiers() & Modifier.VARARGS) != 0;
    }

    /**
     * Returns {@code true} if this executable is a synthetic
     * construct; returns {@code false} otherwise.
     * 如果此可执行文件是合成构造，则返回true；否则返回false。
     * @return true if and only if this executable is a synthetic
     * construct as defined by
     * <cite>The Java&trade; Language Specification</cite>.
     * @jls 13.1 The Form of a Binary
     */
    public boolean isSynthetic() {
        return Modifier.isSynthetic(getModifiers());
    }

    /**
     * Returns an array of arrays of {@code Annotation}s that
     * represent the annotations on the formal parameters, in
     * declaration order, of the {@code Executable} represented by
     * this object.  Synthetic and mandated parameters (see
     * explanation below), such as the outer "this" parameter to an
     * inner class constructor will be represented in the returned
     * array.  If the executable has no parameters (meaning no formal,
     * no synthetic, and no mandated parameters), a zero-length array
     * will be returned.  If the {@code Executable} has one or more
     * parameters, a nested array of length zero is returned for each
     * parameter with no annotations. The annotation objects contained
     * in the returned arrays are serializable.  The caller of this
     * method is free to modify the returned arrays; it will have no
     * effect on the arrays returned to other callers.
     * 1.返回Annotation数组的数组，这些数组按声明顺序表示此对象表示的Executable的形式参数上的注释。
     * 合成和强制参数（参见下面的解释），例如内部类构造函数的外部“this”参数将在返回的数组中表示。
     * 如果可执行文件没有参数（意味着没有正式、没有合成和强制参数），则将返回一个零长度数组。
     * 如果Executable有一个或多个参数，则为每个没有注释的参数返回一个长度为零的嵌套数组。
     * 返回的数组中包含的注释对象是可序列化的。该方法的调用者可以自由修改返回的数组；
     * 它不会影响返回给其他调用者的数组
     * A compiler may add extra parameters that are implicitly
     * declared in source ("mandated"), as well as parameters that
     * are neither implicitly nor explicitly declared in source
     * ("synthetic") to the parameter list for a method.  See {@link
     * java.lang.reflect.Parameter} for more information.
     * 2.编译器可以将在源代码中隐式声明（“强制”）的额外参数，以及在源代码（“合成”）
     * 中既没有隐式声明也没有显式声明的参数添加到方法的参数列表中。
     * 有关更多信息，请参阅java.lang.reflect.Parameter
     * @see java.lang.reflect.Parameter
     * @see java.lang.reflect.Parameter#getAnnotations
     * @return an array of arrays that represent the annotations on
     *    the formal and implicit parameters, in declaration order, of
     *    the executable represented by this object
     */
    public abstract Annotation[][] getParameterAnnotations();

    //共享获取参数注解
    Annotation[][] sharedGetParameterAnnotations(Class<?>[] parameterTypes,
                                                 byte[] parameterAnnotations) {
        int numParameters = parameterTypes.length;
        if (parameterAnnotations == null)
            return new Annotation[numParameters][0];

        Annotation[][] result = parseParameterAnnotations(parameterAnnotations);

        if (result.length != numParameters)
            handleParameterNumberMismatch(result.length, numParameters);
        return result;
    }

    //处理参数号不匹配
    abstract void handleParameterNumberMismatch(int resultLength, int numParameters);

    /**
     * {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
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
    public Annotation[] getDeclaredAnnotations()  {
        return AnnotationParser.toArray(declaredAnnotations());
    }

    private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;

    //声明的注解
    private synchronized  Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
        if (declaredAnnotations == null) {
            Executable root = getRoot();
            if (root != null) {
                declaredAnnotations = root.declaredAnnotations();
            } else {
                declaredAnnotations = AnnotationParser.parseAnnotations(
                    getAnnotationBytes(),
                    sun.misc.SharedSecrets.getJavaLangAccess().
                    getConstantPool(getDeclaringClass()),
                    getDeclaringClass());
            }
        }
        return declaredAnnotations;
    }

    /**
     * Returns an {@code AnnotatedType} object that represents the use of a type to
     * specify the return type of the method/constructor represented by this
     * Executable.
     * 1.返回一个AnnotatedType对象，该对象表示使用一种类型来指定由此 Executable
     * 表示的 method/constructor 的返回类型
     * If this {@code Executable} object represents a constructor, the {@code
     * AnnotatedType} object represents the type of the constructed object.
     * 2.如果此Executable对象表示构造函数，则AnnotatedType对象表示构造对象的类型。
     * If this {@code Executable} object represents a method, the {@code
     * AnnotatedType} object represents the use of a type to specify the return
     * type of the method.
     * 3.如果此Executable对象表示一个方法，则AnnotatedType对象表示使用一个类型来指定该方法的返回类型
     * @return an object representing the return type of the method
     * or constructor represented by this {@code Executable}
     */
    public abstract AnnotatedType getAnnotatedReturnType();

    /* Helper for subclasses of Executable.
     * 1.Executable 子类的助手
     * Returns an AnnotatedType object that represents the use of a type to
     * specify the return type of the method/constructor represented by this
     * Executable.
     * 2.返回一个 AnnotatedType 对象，该对象表示使用一种类型来指定由此 Executable
     * 表示的 method/constructor 的返回类型
     */
    AnnotatedType getAnnotatedReturnType0(Type returnType) {
        return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(),
                sun.misc.SharedSecrets.getJavaLangAccess().
                        getConstantPool(getDeclaringClass()),
                this,
                getDeclaringClass(),
                returnType,
                TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
    }

    /**
     * Returns an {@code AnnotatedType} object that represents the use of a
     * type to specify the receiver type of the method/constructor represented
     * by this Executable object. The receiver type of a method/constructor is
     * available only if the method/constructor has a <em>receiver
     * parameter</em> (JLS 8.4.1).
     * 1.返回一个AnnotatedType对象，该对象表示使用一种类型来指定由此 Executable
     * 对象表示的方法构造函数的接收器类型。方法构造器的接收器类型仅在方法构造器具有
     * 接收器参数(JLS 8.4.1) 时才可用
     * If this {@code Executable} object represents a constructor or instance
     * method that does not have a receiver parameter, or has a receiver
     * parameter with no annotations on its type, then the return value is an
     * {@code AnnotatedType} object representing an element with no
     * annotations.
     * 2.如果此Executable对象表示没有接收器参数的构造函数或实例方法，
     * 或者其类型没有注释的接收器参数，则返回值是一个AnnotatedType对象，表示具有没有注释
     * If this {@code Executable} object represents a static method, then the
     * return value is null.
     * 3.如果此Executable对象表示静态方法，则返回值为 null。
     * @return an object representing the receiver type of the method or
     * constructor represented by this {@code Executable}
     */
    public AnnotatedType getAnnotatedReceiverType() {
        if (Modifier.isStatic(this.getModifiers()))
            return null;
        return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(),
                sun.misc.SharedSecrets.getJavaLangAccess().
                        getConstantPool(getDeclaringClass()),
                this,
                getDeclaringClass(),
                getDeclaringClass(),
                TypeAnnotation.TypeAnnotationTarget.METHOD_RECEIVER);
    }

    /**
     * Returns an array of {@code AnnotatedType} objects that represent the use
     * of types to specify formal parameter types of the method/constructor
     * represented by this Executable. The order of the objects in the array
     * corresponds to the order of the formal parameter types in the
     * declaration of the method/constructor.
     * 1.返回一个 AnnotatedType对象数组，这些对象表示使用类型来指定此 Executable
     * 表示的方法构造函数的形式参数类型。数组中对象的顺序对应于方法构造函数声明中形参类型的顺序。
     * Returns an array of length 0 if the method/constructor declares no
     * parameters.
     * 2.如果 method/constructor 没有声明参数，则返回长度为 0 的数组。
     * @return an array of objects representing the types of the
     * formal parameters of the method or constructor represented by this
     * {@code Executable}
     */
    public AnnotatedType[] getAnnotatedParameterTypes() {
        return TypeAnnotationParser.buildAnnotatedTypes(getTypeAnnotationBytes0(),
                sun.misc.SharedSecrets.getJavaLangAccess().
                        getConstantPool(getDeclaringClass()),
                this,
                getDeclaringClass(),
                getAllGenericParameterTypes(),
                TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER);
    }

    /**
     * Returns an array of {@code AnnotatedType} objects that represent the use
     * of types to specify the declared exceptions of the method/constructor
     * represented by this Executable. The order of the objects in the array
     * corresponds to the order of the exception types in the declaration of
     * the method/constructor.
     * 1.返回一个AnnotatedType对象数组，这些对象表示使用类型来指定由此 Executable 表示的
     * method/constructor 的声明异常。数组中对象的顺序对应于方法构造函数声明中异常类型的顺序
     * Returns an array of length 0 if the method/constructor declares no
     * exceptions.
     * 2.如果 methodconstructor 没有声明异常，则返回长度为 0 的数组。
     * @return an array of objects representing the declared
     * exceptions of the method or constructor represented by this {@code
     * Executable}
     */
    public AnnotatedType[] getAnnotatedExceptionTypes() {
        return TypeAnnotationParser.buildAnnotatedTypes(getTypeAnnotationBytes0(),
                sun.misc.SharedSecrets.getJavaLangAccess().
                        getConstantPool(getDeclaringClass()),
                this,
                getDeclaringClass(),
                getGenericExceptionTypes(),
                TypeAnnotation.TypeAnnotationTarget.THROWS);
    }

}
