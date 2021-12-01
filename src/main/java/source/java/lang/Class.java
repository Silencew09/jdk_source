/*
 * Copyright (c) 1994, 2014, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Member;
import java.lang.reflect.Field;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.AnnotatedType;
import java.lang.ref.SoftReference;
import java.io.InputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.ConstantPool;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import sun.reflect.generics.factory.CoreReflectionFactory;
import sun.reflect.generics.factory.GenericsFactory;
import sun.reflect.generics.repository.ClassRepository;
import sun.reflect.generics.repository.MethodRepository;
import sun.reflect.generics.repository.ConstructorRepository;
import sun.reflect.generics.scope.ClassScope;
import sun.security.util.SecurityConstants;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import sun.reflect.annotation.*;
import sun.reflect.misc.ReflectUtil;

/**
 * Instances of the class {@code Class} represent classes and
 * interfaces in a running Java application.  An enum is a kind of
 * class and an annotation is a kind of interface.  Every array also
 * belongs to a class that is reflected as a {@code Class} object
 * that is shared by all arrays with the same element type and number
 * of dimensions.  The primitive Java types ({@code boolean},
 * {@code byte}, {@code char}, {@code short},
 * {@code int}, {@code long}, {@code float}, and
 * {@code double}), and the keyword {@code void} are also
 * represented as {@code Class} objects.
 *  1.Class类的实例表示正在运行的 Java 应用程序中的类和接口
 *  2.枚举是一种类,注解是一种接口
 *  3.每个数组也属于一个类，该类反映为一个Class对象，该对象由具有相同元素类型和维数的所有数组共享
 *  4.Java 原始类型boolean、byte、char、short、int、long、float ,double，关键字 void也表示为Class对象
 * <p> {@code Class} has no public constructor. Instead {@code Class}
 * objects are constructed automatically by the Java Virtual Machine as classes
 * are loaded and by calls to the {@code defineClass} method in the class
 * loader.
 *5.Class没有公共构造函数。相反，Class对象在类加载时由 Java 虚拟机自动构造，
 * 并通过调用类加载器中的 defineClass方法
 * <p> The following example uses a {@code Class} object to print the
 * class name of an object:
 *
 * <blockquote><pre>
 *     void printClassName(Object obj) {
 *         System.out.println("The class of " + obj +
 *                            " is " + obj.getClass().getName());
 *     }
 * </pre></blockquote>
 *
 * <p> It is also possible to get the {@code Class} object for a named
 * type (or for void) using a class literal.  See Section 15.8.2 of
 * <cite>The Java&trade; Language Specification</cite>.
 * For example:
 *
 * <blockquote>
 *     {@code System.out.println("The name of class Foo is: "+Foo.class.getName());}
 * </blockquote>
 *
 * @param <T> the type of the class modeled by this {@code Class}
 * object.  For example, the type of {@code String.class} is {@code
 * Class<String>}.  Use {@code Class<?>} if the class being modeled is
 * unknown.
 *  6.此 Class对象建模的类的类型。例如，String.class的类型是 Class<String>。如果正在建模的类未知，请使用Class<?>
 * @author  unascribed
 * @see     java.lang.ClassLoader#defineClass(byte[], int, int)
 * @since   JDK1.0
 */
public final class Class<T> implements java.io.Serializable,
                              GenericDeclaration,
                              Type,
                              AnnotatedElement {
    //表示注解的十六进制
    private static final int ANNOTATION= 0x00002000;
    //枚举的十六进制
    private static final int ENUM      = 0x00004000;
    //合成类的十六进制表示
    private static final int SYNTHETIC = 0x00001000;

    private static native void registerNatives();
    static {
        registerNatives();
    }

    /*
     * Private constructor. Only the Java Virtual Machine creates Class objects.
     * This constructor is not used and prevents the default constructor being
     * generated.
     * 1.私有构造函数。
     * 2.只有 Java 虚拟机创建 Class 对象。未使用此构造函数并阻止生成默认构造函数
     */
    private Class(ClassLoader loader) {
        // Initialize final field for classLoader.  The initialization value of non-null
        // prevents future JIT optimizations from assuming this final field is null.
        //1.初始化 classLoader 的 final 字段。非空的初始化值
        //2.防止未来的 JIT 优化假设这个最终字段为空
        classLoader = loader;
    }

    /**
     * Converts the object to a string. The string representation is the
     * string "class" or "interface", followed by a space, and then by the
     * fully qualified name of the class in the format returned by
     * {@code getName}.  If this {@code Class} object represents a
     * primitive type, this method returns the name of the primitive type.  If
     * this {@code Class} object represents void this method returns
     * "void".
     *  1.将对象转换为字符串。
     *  2.字符串表示是字符串“class”或“interface”，后跟一个空格，然后是getName()返回格式的类的完全限定名称。
     *  如果此 Class对象表示原始类型，则此方法返回原始类型的名称。
     *  如果此 Class对象表示 void，则此方法返回“void”
     * @return a string representation of this class object.
     */
    public String toString() {
        return (isInterface() ? "interface " : (isPrimitive() ? "" : "class "))
            + getName();
    }

    /**
     * Returns a string describing this {@code Class}, including
     * information about modifiers and type parameters.
     *  1.返回描述此 Class的字符串，包括有关修饰符和类型参数的信息
     * The string is formatted as a list of type modifiers, if any,
     * followed by the kind of type (empty string for primitive types
     * and {@code class}, {@code enum}, {@code interface}, or
     * <code>&#64;</code>{@code interface}, as appropriate), followed
     * by the type's name, followed by an angle-bracketed
     * comma-separated list of the type's type parameters, if any.
     * 2.该字符串的格式为类型修饰符列表（如果有），
     * 后跟类型（原始类型的空字符串和class、enum、interface 或&64 interface，视情况而定），
     * 后跟类型的名称，后跟用尖括号括起的逗号分隔的类型类型参数列表（如果有）
     *
     * A space is used to separate modifiers from one another and to
     * separate any modifiers from the kind of type. The modifiers
     * occur in canonical order. If there are no type parameters, the
     * type parameter list is elided.
     * 3.空格用于将修饰符彼此分开，并将任何修饰符与类型的种类分开。
     * 修饰符按规范顺序出现。如果没有类型参数，则省略类型参数列表
     *
     * <p>Note that since information about the runtime representation
     * of a type is being generated, modifiers not present on the
     * originating source code or illegal on the originating source
     * code may be present.
     * 4.请注意，由于正在生成有关类型的运行时表示的信息，
     * 因此可能存在原始源代码中不存在的修饰符或原始源代码中的非法修饰符
     *
     * @return a string describing this {@code Class}, including
     * information about modifiers and type parameters
     *
     * @since 1.8
     */
    public String toGenericString() {
        //判断是否是私有的
        if (isPrimitive()) {
            return toString();
        } else {
            StringBuilder sb = new StringBuilder();

            // Class modifiers are a superset of interface modifiers
            //是否有有修饰符
            int modifiers = getModifiers() & Modifier.classModifiers();
            if (modifiers != 0) {
                sb.append(Modifier.toString(modifiers));
                sb.append(' ');
            }

            if (isAnnotation()) {
                sb.append('@');
            }
            if (isInterface()) { // Note: all annotation types are interfaces
                sb.append("interface");
            } else {
                if (isEnum())
                    sb.append("enum");
                else
                    sb.append("class");
            }
            sb.append(' ');
            sb.append(getName());
            //获取参数类型
            TypeVariable<?>[] typeparms = getTypeParameters();
            if (typeparms.length > 0) {
                boolean first = true;
                sb.append('<');
                for(TypeVariable<?> typeparm: typeparms) {
                    if (!first)
                        sb.append(',');
                    //获取参数名称
                    sb.append(typeparm.getTypeName());
                    first = false;
                }
                sb.append('>');
            }

            return sb.toString();
        }
    }

    /**
     * Returns the {@code Class} object associated with the class or
     * interface with the given string name.  Invoking this method is
     * equivalent to:
     *  1.返回与具有给定字符串名称的类或接口关联的Class对象
     *  2.调用这个方法相当于调用Class.forName(className, true, currentLoader),
     *  其中 =currentLoader=表示当前类的定义类加载器
     * <blockquote>
     *  {@code Class.forName(className, true, currentLoader)}
     * </blockquote>
     *
     * where {@code currentLoader} denotes the defining class loader of
     * the current class.
     *
     * <p> For example, the following code fragment returns the
     * runtime {@code Class} descriptor for the class named
     * {@code java.lang.Thread}:
     *
     * <blockquote>
     *   {@code Class t = Class.forName("java.lang.Thread")}
     * </blockquote>
     * <p>
     * A call to {@code forName("X")} causes the class named
     * {@code X} to be initialized.
     *
     * @param      className   the fully qualified name of the desired class.
     * @return     the {@code Class} object for the class with the
     *             specified name.
     * @exception LinkageError if the linkage fails
     * @exception ExceptionInInitializerError if the initialization provoked
     *            by this method fails
     * @exception ClassNotFoundException if the class cannot be located
     */
    @CallerSensitive
    public static Class<?> forName(String className)
                throws ClassNotFoundException {
        Class<?> caller = Reflection.getCallerClass();
        return forName0(className, true, ClassLoader.getClassLoader(caller), caller);
    }


    /**
     * Returns the {@code Class} object associated with the class or
     * interface with the given string name, using the given class loader.
     * Given the fully qualified name for a class or interface (in the same
     * format returned by {@code getName}) this method attempts to
     * locate, load, and link the class or interface.  The specified class
     * loader is used to load the class or interface.  If the parameter
     * {@code loader} is null, the class is loaded through the bootstrap
     * class loader.  The class is initialized only if the
     * {@code initialize} parameter is {@code true} and if it has
     * not been initialized earlier.
     * 1.使用给定的类加载器返回与具有给定字符串名称的类或接口关联的 Class对象。
     * 给定类或接口的完全限定名称getName返回的格式相同，此方法尝试定位、加载和链接类或接口。
     * 指定的类加载器用于加载类或接口。如果参数 loader为空，则通过引导类加载器加载该类。
     * 仅当 initialize参数为true并且之前尚未初始化该类时才初始化该类
     *
     * <p> If {@code name} denotes a primitive type or void, an attempt
     * will be made to locate a user-defined class in the unnamed package whose
     * name is {@code name}. Therefore, this method cannot be used to
     * obtain any of the {@code Class} objects representing primitive
     * types or void.
     * 2.如果 name表示原始类型或 void，将尝试在名称为name的未命名包中定位用户定义的类。
     * 因此，此方法不能用于获取任何表示原始类型或 void 的Class对象
     *
     * <p> If {@code name} denotes an array class, the component type of
     * the array class is loaded but not initialized.
     *3.如果 name表示数组类，则加载数组类的组件类型但不会初始化
     * <p> For example, in an instance method the expression:
     *
     * <blockquote>
     *  {@code Class.forName("Foo")}
     * </blockquote>
     *
     * is equivalent to:
     *
     * <blockquote>
     *  {@code Class.forName("Foo", true, this.getClass().getClassLoader())}
     * </blockquote>
     *
     * Note that this method throws errors related to loading, linking or
     * initializing as specified in Sections 12.2, 12.3 and 12.4 of <em>The
     * Java Language Specification</em>.
     * Note that this method does not check whether the requested class
     * is accessible to its caller.
     *4.请注意，此方法会引发与 Java 语言规范的第 12.2、12.3 和 12.4 节中指定的加载、链接或初始化相关的错误。
     * 请注意，此方法不会检查其调用者是否可以访问所请求的类
     * <p> If the {@code loader} is {@code null}, and a security
     * manager is present, and the caller's class loader is not null, then this
     * method calls the security manager's {@code checkPermission} method
     * with a {@code RuntimePermission("getClassLoader")} permission to
     * ensure it's ok to access the bootstrap class loader.
     * 5.如果 loader为 null，并且存在安全管理器，并且调用者的类加载器不为 null，
     * 则此方法使用RuntimePermission() 调用安全管理器的 checkPermission 方法"getClassLoader" 权限以确保可以访问引导类加载器
     * @param name       fully qualified name of the desired class 所需类的完全限定名称
     * @param initialize if {@code true} the class will be initialized.
     *                   See Section 12.4 of <em>The Java Language Specification</em>.
     * @param loader     class loader from which the class must be loaded
     * @return           class object representing the desired class
     *
     * @exception LinkageError if the linkage fails
     * @exception ExceptionInInitializerError if the initialization provoked
     *            by this method fails
     * @exception ClassNotFoundException if the class cannot be located by
     *            the specified class loader
     *
     * @see       java.lang.Class#forName(String)
     * @see       java.lang.ClassLoader
     * @since     1.2
     */
    @CallerSensitive
    public static Class<?> forName(String name, boolean initialize,
                                   ClassLoader loader)
        throws ClassNotFoundException
    {
        Class<?> caller = null;
        //获取安全管理器
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // Reflective call to get caller class is only needed if a security manager
            // is present.  Avoid the overhead of making this call otherwise.
            //仅当存在安全管理器时才需要反射调用以获取调用者类。避免进行此调用的开销
            caller = Reflection.getCallerClass();
            if (sun.misc.VM.isSystemDomainLoader(loader)) {
                ClassLoader ccl = ClassLoader.getClassLoader(caller);
                if (!sun.misc.VM.isSystemDomainLoader(ccl)) {
                    sm.checkPermission(
                        SecurityConstants.GET_CLASSLOADER_PERMISSION);
                }
            }
        }
        return forName0(name, initialize, loader, caller);
    }

    /** Called after security check for system loader access checks have been made. */
    //在对系统加载程序访问检查进行安全检查后调用
    private static native Class<?> forName0(String name, boolean initialize,
                                            ClassLoader loader,
                                            Class<?> caller)
        throws ClassNotFoundException;

    /**
     * Creates a new instance of the class represented by this {@code Class}
     * object.  The class is instantiated as if by a {@code new}
     * expression with an empty argument list.  The class is initialized if it
     * has not already been initialized.
     *  1.创建此Class对象表示的类的新实例。该类被实例化为一个带有空参数列表的 new表达式。
     *  如果该类尚未初始化，则该类将会初始化
     * <p>Note that this method propagates any exception thrown by the
     * nullary constructor, including a checked exception.  Use of
     * this method effectively bypasses the compile-time exception
     * checking that would otherwise be performed by the compiler.
     * 2.请注意，此方法会传播空构造函数抛出的任何异常，包括已检查的异常。
     * 使用此方法可有效绕过编译器否则会执行的编译时异常检查
     * The {@link
     * java.lang.reflect.Constructor#newInstance(java.lang.Object...)
     * Constructor.newInstance} method avoids this problem by wrapping
     * any exception thrown by the constructor in a (checked) {@link
     * java.lang.reflect.InvocationTargetException}.
     *
     * @return  a newly allocated instance of the class represented by this
     *          object.
     * @throws  IllegalAccessException  if the class or its nullary
     *          constructor is not accessible.
     * @throws  InstantiationException
     *          if this {@code Class} represents an abstract class,
     *          an interface, an array class, a primitive type, or void;
     *          or if the class has no nullary constructor;
     *          or if the instantiation fails for some other reason.
     * @throws  ExceptionInInitializerError if the initialization
     *          provoked by this method fails.
     * @throws  SecurityException
     *          If a security manager, <i>s</i>, is present and
     *          the caller's class loader is not the same as or an
     *          ancestor of the class loader for the current class and
     *          invocation of {@link SecurityManager#checkPackageAccess
     *          s.checkPackageAccess()} denies access to the package
     *          of this class.
     */
    @CallerSensitive
    public T newInstance()
        throws InstantiationException, IllegalAccessException
    {
        if (System.getSecurityManager() != null) {
            //检查成员访问权限
            checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), false);
        }

        // NOTE: the following code may not be strictly correct under
        // the current Java memory model.
        //以下代码在当前Java内存模型下可能不严格正确
        // Constructor lookup
        if (cachedConstructor == null) {
            if (this == Class.class) {
                throw new IllegalAccessException(
                    "Can not call newInstance() on the Class for java.lang.Class"
                );
            }
            try {
                Class<?>[] empty = {};
                final Constructor<T> c = getConstructor0(empty, Member.DECLARED);
                // Disable accessibility checks on the constructor
                // since we have to do the security check here anyway
                // (the stack depth is wrong for the Constructor's
                // security check to work)
                //禁用对构造函数的可访问性检查，
                // 因为无论如何我们都必须在此处进行安全检查（堆栈深度错误，构造函数的安全检查无法正常工作）
                java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<Void>() {
                        public Void run() {
                                c.setAccessible(true);
                                return null;
                            }
                        });
                cachedConstructor = c;
            } catch (NoSuchMethodException e) {
                throw (InstantiationException)
                    new InstantiationException(getName()).initCause(e);
            }
        }
        Constructor<T> tmpConstructor = cachedConstructor;
        // Security check (same as in java.lang.reflect.Constructor)
        int modifiers = tmpConstructor.getModifiers();

        //检查成员访问权限
        if (!Reflection.quickCheckMemberAccess(this, modifiers)) {
            Class<?> caller = Reflection.getCallerClass();
            if (newInstanceCallerCache != caller) {
                Reflection.ensureMemberAccess(caller, this, null, modifiers);
                newInstanceCallerCache = caller;
            }
        }
        // Run constructor
        try {
            return tmpConstructor.newInstance((Object[])null);
        } catch (InvocationTargetException e) {
            Unsafe.getUnsafe().throwException(e.getTargetException());
            // Not reached
            return null;
        }
    }
    //缓存构造器
    private volatile transient Constructor<T> cachedConstructor;
    //新的实例调用者缓存
    private volatile transient Class<?>       newInstanceCallerCache;


    /**
     * Determines if the specified {@code Object} is assignment-compatible
     * with the object represented by this {@code Class}.  This method is
     * the dynamic equivalent of the Java language {@code instanceof}
     * operator. The method returns {@code true} if the specified
     * {@code Object} argument is non-null and can be cast to the
     * reference type represented by this {@code Class} object without
     * raising a {@code ClassCastException.} It returns {@code false}
     * otherwise.
     *  1.确定指定的 Object是否与此Class表示的对象赋值兼容。
     *  此方法是 Java 语言 instanceof运算符的动态等效项。
     *  如果指定的Object数为非 null，并且可以强制转换为此 Class对象表示的引用类型而不引发 ClassCastException，
     *  则该方法返回 true。否则它返回 false
     * <p> Specifically, if this {@code Class} object represents a
     * declared class, this method returns {@code true} if the specified
     * {@code Object} argument is an instance of the represented class (or
     * of any of its subclasses); it returns {@code false} otherwise. If
     * this {@code Class} object represents an array class, this method
     * returns {@code true} if the specified {@code Object} argument
     * can be converted to an object of the array class by an identity
     * conversion or by a widening reference conversion; it returns
     * {@code false} otherwise. If this {@code Class} object
     * represents an interface, this method returns {@code true} if the
     * class or any superclass of the specified {@code Object} argument
     * implements this interface; it returns {@code false} otherwise. If
     * this {@code Class} object represents a primitive type, this method
     * returns {@code false}.
     *2.具体来说，如果这个Class对象表示一个声明的类，
     * 如果指定的Object参数是所表示的类（或其任何子类）的实例，则此方法返回true,否则返回 false
     * 3.如果此Class对象表示一个数组类，如果指定的 Object参数可以通过恒等转换或扩展引用转换转换为数组类的对象，
     * 则此方法返回 true;否则返回false
     * 4.如果此Class对象表示一个接口，则如果指定的 Object参数的类或任何超类实现此接口，
     * 则此方法返回 true；否则返回 false
     * 5.如果此 Class对象表示原始类型，false。
     * @param   obj the object to check
     * @return  true if {@code obj} is an instance of this class
     *
     * @since JDK1.1
     */
    public native boolean isInstance(Object obj);


    /**
     * Determines if the class or interface represented by this
     * {@code Class} object is either the same as, or is a superclass or
     * superinterface of, the class or interface represented by the specified
     * {@code Class} parameter. It returns {@code true} if so;
     * otherwise it returns {@code false}. If this {@code Class}
     * object represents a primitive type, this method returns
     * {@code true} if the specified {@code Class} parameter is
     * exactly this {@code Class} object; otherwise it returns
     * {@code false}.
     * 1.确定此 Class对象表示的类或接口是否与指定的 Class参数表示的类或接口相同，
     * 或者是其超类或超接口
     * <p> Specifically, this method tests whether the type represented by the
     * specified {@code Class} parameter can be converted to the type
     * represented by this {@code Class} object via an identity conversion
     * or via a widening reference conversion. See <em>The Java Language
     * Specification</em>, sections 5.1.1 and 5.1.4 , for details.
     *2.具体来说，该方法测试指定的 Class参数表示的类型是否可以通过标识转换或通过扩展引用转换,转换为此 Class对象表示的类型
     * @param cls the {@code Class} object to be checked
     * @return the {@code boolean} value indicating whether objects of the
     * type {@code cls} can be assigned to objects of this class
     * @exception NullPointerException if the specified Class parameter is
     *            null.
     * @since JDK1.1
     */
    public native boolean isAssignableFrom(Class<?> cls);


    /**
     * Determines if the specified {@code Class} object represents an
     * interface type.
     *确定指定的 Class对象是否表示接口类型
     * @return  {@code true} if this object represents an interface;
     *          {@code false} otherwise.
     */
    public native boolean isInterface();


    /**
     * Determines if this {@code Class} object represents an array class.
     *确定此Class对象是否表示数组类
     * @return  {@code true} if this object represents an array class;
     *          {@code false} otherwise.
     * @since   JDK1.1
     */
    public native boolean isArray();


    /**
     * Determines if the specified {@code Class} object represents a
     * primitive type.
     * 1.确定指定的 Class对象是否表示原始类型
     * <p> There are nine predefined {@code Class} objects to represent
     * the eight primitive types and void.  These are created by the Java
     * Virtual Machine, and have the same names as the primitive types that
     * they represent, namely {@code boolean}, {@code byte},
     * {@code char}, {@code short}, {@code int},
     * {@code long}, {@code float}, and {@code double}.
     * 2.有九个预定义的Class对象来表示八种原始类型和 void。
     * 它们由 Java 虚拟机创建，并与它们所代表的原始类型具有相同的名称，即 boolean、byte、char、
     * short、int、long、float 和double
     * <p> These objects may only be accessed via the following public static
     * final variables, and are the only {@code Class} objects for which
     * this method returns {@code true}.
     *3.这些对象只能通过以下公共静态最终变量访问，并且是此方法为其返回 true的唯一 Class对象
     * @return true if and only if this class represents a primitive type
     *
     * @see     java.lang.Boolean#TYPE
     * @see     java.lang.Character#TYPE
     * @see     java.lang.Byte#TYPE
     * @see     java.lang.Short#TYPE
     * @see     java.lang.Integer#TYPE
     * @see     java.lang.Long#TYPE
     * @see     java.lang.Float#TYPE
     * @see     java.lang.Double#TYPE
     * @see     java.lang.Void#TYPE
     * @since JDK1.1
     */
    public native boolean isPrimitive();

    /**
     * Returns true if this {@code Class} object represents an annotation
     * type.  Note that if this method returns true, {@link #isInterface()}
     * would also return true, as all annotation types are also interfaces.
     *如果此 Class对象表示注解类型，则返回 true。
     * 请注意，如果此方法返回 true，isInterface()也会返回 true，因为所有注解类型也是接口
     * @return {@code true} if this class object represents an annotation
     *      type; {@code false} otherwise
     * @since 1.5
     */
    public boolean isAnnotation() {
        return (getModifiers() & ANNOTATION) != 0;
    }

    /**
     * 判断此类是否是合成类
     * Returns {@code true} if this class is a synthetic class;
     * returns {@code false} otherwise.
     * @return {@code true} if and only if this class is a synthetic class as
     *         defined by the Java Language Specification.
     * @jls 13.1 The Form of a Binary
     * @since 1.5
     */
    public boolean isSynthetic() {
        return (getModifiers() & SYNTHETIC) != 0;
    }

    /**
     * Returns the  name of the entity (class, interface, array class,
     * primitive type, or void) represented by this {@code Class} object,
     * as a {@code String}.
     * 1.返回此 Class对象表示的实体（类、接口、数组类、原始类型或 void）的名称，作为 String
     * <p> If this class object represents a reference type that is not an
     * array type then the binary name of the class is returned, as specified
     * by
     * <cite>The Java&trade; Language Specification</cite>.
     * 2.如果此类对象表示不是数组类型的引用类型，则返回该类的二进制名称，如 Java语言规范所指定
     * <p> If this class object represents a primitive type or void, then the
     * name returned is a {@code String} equal to the Java language
     * keyword corresponding to the primitive type or void.
     *3.如果这个类对象表示一个原始类型或void，则返回的名称是一个String，等于原始类型或void对应的Java语言关键字
     * <p> If this class object represents a class of arrays, then the internal
     * form of the name consists of the name of the element type preceded by
     * one or more '{@code [}' characters representing the depth of the array
     * nesting.  The encoding of element type names is as follows:
     *
     * <blockquote><table summary="Element types and encodings">
     * <tr><th> Element Type <th> &nbsp;&nbsp;&nbsp; <th> Encoding
     * <tr><td> boolean      <td> &nbsp;&nbsp;&nbsp; <td align=center> Z
     * <tr><td> byte         <td> &nbsp;&nbsp;&nbsp; <td align=center> B
     * <tr><td> char         <td> &nbsp;&nbsp;&nbsp; <td align=center> C
     * <tr><td> class or interface
     *                       <td> &nbsp;&nbsp;&nbsp; <td align=center> L<i>classname</i>;
     * <tr><td> double       <td> &nbsp;&nbsp;&nbsp; <td align=center> D
     * <tr><td> float        <td> &nbsp;&nbsp;&nbsp; <td align=center> F
     * <tr><td> int          <td> &nbsp;&nbsp;&nbsp; <td align=center> I
     * <tr><td> long         <td> &nbsp;&nbsp;&nbsp; <td align=center> J
     * <tr><td> short        <td> &nbsp;&nbsp;&nbsp; <td align=center> S
     * </table></blockquote>
     *
     * <p> The class or interface name <i>classname</i> is the binary name of
     * the class specified above.
     *
     * <p> Examples:
     * <blockquote><pre>
     * String.class.getName()
     *     returns "java.lang.String"
     * byte.class.getName()
     *     returns "byte"
     * (new Object[3]).getClass().getName()
     *     returns "[Ljava.lang.Object;"
     * (new int[3][4][5][6][7][8][9]).getClass().getName()
     *     returns "[[[[[[[I"
     * </pre></blockquote>
     *
     * @return  the name of the class or interface
     *          represented by this object.
     */
    public String getName() {
        String name = this.name;
        if (name == null)
            this.name = name = getName0();
        return name;
    }

    // cache the name to reduce the number of calls into the VM
    //缓存名字以减少调用VM的次数
    private transient String name;
    private native String getName0();

    /**
     * Returns the class loader for the class.  Some implementations may use
     * null to represent the bootstrap class loader. This method will return
     * null in such implementations if this class was loaded by the bootstrap
     * class loader.
     * 1.返回类的类加载器。一些实现可能使用 null 来表示引导类加载器。
     * 如果此类由引导类加载器加载，则此方法将在此类实现中返回 null
     * <p> If a security manager is present, and the caller's class loader is
     * not null and the caller's class loader is not the same as or an ancestor of
     * the class loader for the class whose class loader is requested, then
     * this method calls the security manager's {@code checkPermission}
     * method with a {@code RuntimePermission("getClassLoader")}
     * permission to ensure it's ok to access the class loader for the class.
     * 2.如果存在安全管理器，并且调用者的类加载器不为 null，
     * 并且调用者的类加载器与其类加载器被请求的类的类加载器不相同或不是该类加载器的祖先，
     * 则此方法调用安全管理器的 RuntimePermission("getClassLoader")权限的代码 checkPermission 方法，
     * 以确保可以访问该类的类加载器
     * <p>If this object
     * represents a primitive type or void, null is returned.
     *
     * @return  the class loader that loaded the class or interface
     *          represented by this object.
     * @throws SecurityException
     *    if a security manager exists and its
     *    {@code checkPermission} method denies
     *    access to the class loader for the class.
     * @see java.lang.ClassLoader
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    @CallerSensitive
    public ClassLoader getClassLoader() {
        ClassLoader cl = getClassLoader0();
        if (cl == null)
            return null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ClassLoader.checkClassLoaderPermission(cl, Reflection.getCallerClass());
        }
        return cl;
    }

    // Package-private to allow ClassLoader access
    ClassLoader getClassLoader0() { return classLoader; }

    // Initialized in JVM not by private constructor
    // This field is filtered from reflection access, i.e. getDeclaredField
    // will throw NoSuchFieldException
    //在JVM初始化而不是私有的构造函数
    //该字段从反射访问中过滤，即 getDeclaredField 将抛出 NoSuchFieldException
    private final ClassLoader classLoader;

    /**
     * Returns an array of {@code TypeVariable} objects that represent the
     * type variables declared by the generic declaration represented by this
     * {@code GenericDeclaration} object, in declaration order.  Returns an
     * array of length 0 if the underlying generic declaration declares no type
     * variables.
     * 返回一个 TypeVariable对象数组，这些对象按声明顺序
     * 表示由此 GenericDeclaration对象表示的泛型声明所声明的类型变量。
     * 如果底层泛型声明没有声明类型变量，则返回长度为 0 的数组
     * @return an array of {@code TypeVariable} objects that represent
     *     the type variables declared by this generic declaration
     * @throws java.lang.reflect.GenericSignatureFormatError if the generic
     *     signature of this generic declaration does not conform to
     *     the format specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     * @since 1.5
     */
    @SuppressWarnings("unchecked")
    public TypeVariable<Class<T>>[] getTypeParameters() {
        ClassRepository info = getGenericInfo();
        if (info != null)
            return (TypeVariable<Class<T>>[])info.getTypeParameters();
        else
            return (TypeVariable<Class<T>>[])new TypeVariable<?>[0];
    }


    /**
     * Returns the {@code Class} representing the superclass of the entity
     * (class, interface, primitive type or void) represented by this
     * {@code Class}.  If this {@code Class} represents either the
     * {@code Object} class, an interface, a primitive type, or void, then
     * null is returned.  If this object represents an array class then the
     * {@code Class} object representing the {@code Object} class is
     * returned.
     * 返回表示此 Class表示的实体（类、接口、原始类型或 void）的超类的 Class。
     * 如果此 Class表示Object类、接口、原始类型或 void，则返回 null。
     * 如果此对象表示数组类，则返回表示 Object类的  Class对象
     *
     * @return the superclass of the class represented by this object.
     */
    public native Class<? super T> getSuperclass();


    /**
     * Returns the {@code Type} representing the direct superclass of
     * the entity (class, interface, primitive type or void) represented by
     * this {@code Class}.
     * 返回 Type表示实体（类、接口、原始类型或 void）的直接超类，由该 Class表示
     * <p>If the superclass is a parameterized type, the {@code Type}
     * object returned must accurately reflect the actual type
     * parameters used in the source code. The parameterized type
     * representing the superclass is created if it had not been
     * created before. See the declaration of {@link
     * java.lang.reflect.ParameterizedType ParameterizedType} for the
     * semantics of the creation process for parameterized types.  If
     * this {@code Class} represents either the {@code Object}
     * class, an interface, a primitive type, or void, then null is
     * returned.  If this object represents an array class then the
     * {@code Class} object representing the {@code Object} class is
     * returned.
     *  如果超类是参数化类型，则返回的 Type对象必须准确反映源代码中使用的实际类型参数。
     *  如果之前没有创建过表示超类的参数化类型，则会创建它。有关参数化类型的创建过程的语义，
     *  请参阅 java.lang.reflect.ParameterizedType ParameterizedType的声明。
     *  如果此Class表示  Object类、接口、原始类型或 void，则返回 null。
     *  如果此对象表示数组类，则返回表示 Object类的 Class对象
     * @throws java.lang.reflect.GenericSignatureFormatError if the generic
     *     class signature does not conform to the format specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException if the generic superclass
     *     refers to a non-existent type declaration
     * @throws java.lang.reflect.MalformedParameterizedTypeException if the
     *     generic superclass refers to a parameterized type that cannot be
     *     instantiated  for any reason
     * @return the superclass of the class represented by this object
     * @since 1.5
     */
    public Type getGenericSuperclass() {
        ClassRepository info = getGenericInfo();
        if (info == null) {
            return getSuperclass();
        }

        // Historical irregularity:
        // Generic signature marks interfaces with superclass = Object
        // but this API returns null for interfaces
        if (isInterface()) {
            return null;
        }

        return info.getSuperclass();
    }

    /**
     * Gets the package for this class.  The class loader of this class is used
     * to find the package.  If the class was loaded by the bootstrap class
     * loader the set of packages loaded from CLASSPATH is searched to find the
     * package of the class. Null is returned if no package object was created
     * by the class loader of this class.
     * 1.获取此类的包。此类的类加载器用于查找包。
     * 如果该类是由引导类加载器加载的，则搜索从 CLASSPATH 加载的包集以查找该类的包。
     * 如果此类的类加载器未创建包对象，则返回 Null
     * <p> Packages have attributes for versions and specifications only if the
     * information was defined in the manifests that accompany the classes, and
     * if the class loader created the package instance with the attributes
     * from the manifest.
     * 2.仅当信息在类随附的清单中定义，并且类加载器使用清单中的属性创建包实例时，
     * 包才具有版本和规范的属性
     * @return the package of the class, or null if no package
     *         information is available from the archive or codebase.
     */
    public Package getPackage() {
        return Package.getPackage(this);
    }


    /**
     * Determines the interfaces implemented by the class or interface
     * represented by this object.
     * 1.确定该对象所代表的类或接口所实现的接口
     * <p> If this object represents a class, the return value is an array
     * containing objects representing all interfaces implemented by the
     * class. The order of the interface objects in the array corresponds to
     * the order of the interface names in the {@code implements} clause
     * of the declaration of the class represented by this object. For
     * example, given the declaration:
     * 2.如果此对象代表一个类，则返回值是一个数组，其中包含代表该类实现的所有接口的对象。
     * 数组中接口对象的顺序与该对象表示的类的声明的implements子句中接口名称的顺序相对应
     * <blockquote>
     * {@code class Shimmer implements FloorWax, DessertTopping { ... }}
     * </blockquote>
     * suppose the value of {@code s} is an instance of
     * {@code Shimmer}; the value of the expression:
     * <blockquote>
     * {@code s.getClass().getInterfaces()[0]}
     * </blockquote>
     * is the {@code Class} object that represents interface
     * {@code FloorWax}; and the value of:
     * <blockquote>
     * {@code s.getClass().getInterfaces()[1]}
     * </blockquote>
     * is the {@code Class} object that represents interface
     * {@code DessertTopping}.
     *
     * <p> If this object represents an interface, the array contains objects
     * representing all interfaces extended by the interface. The order of the
     * interface objects in the array corresponds to the order of the interface
     * names in the {@code extends} clause of the declaration of the
     * interface represented by this object.
     * 2.如果此对象表示一个接口，则该数组包含表示该接口扩展的所有接口的对象。
     * 数组中接口对象的顺序与该对象所代表的接口声明的extends子句中接口名称的顺序相对应
     * <p> If this object represents a class or interface that implements no
     * interfaces, the method returns an array of length 0.
     *3.如果此对象表示一个没有实现接口的类或接口，则该方法返回一个长度为 0 的数组
     * <p> If this object represents a primitive type or void, the method
     * returns an array of length 0.
     * 4.如果此对象表示原始类型或 void，则该方法返回长度为 0 的数组
     * <p> If this {@code Class} object represents an array type, the
     * interfaces {@code Cloneable} and {@code java.io.Serializable} are
     * returned in that order.
     *
     * @return an array of interfaces implemented by this class.
     */
    public Class<?>[] getInterfaces() {
        ReflectionData<T> rd = reflectionData();
        if (rd == null) {
            // no cloning required
            return getInterfaces0();
        } else {
            Class<?>[] interfaces = rd.interfaces;
            if (interfaces == null) {
                interfaces = getInterfaces0();
                rd.interfaces = interfaces;
            }
            // defensively copy before handing over to user code
            //在移交给用户代码之前防御性地复制
            return interfaces.clone();
        }
    }

    private native Class<?>[] getInterfaces0();

    /**
     * Returns the {@code Type}s representing the interfaces
     * directly implemented by the class or interface represented by
     * this object.
     * 1.返回表示该对象所表示的类或接口直接实现的接口的Type
     * <p>If a superinterface is a parameterized type, the
     * {@code Type} object returned for it must accurately reflect
     * the actual type parameters used in the source code. The
     * parameterized type representing each superinterface is created
     * if it had not been created before. See the declaration of
     * {@link java.lang.reflect.ParameterizedType ParameterizedType}
     * for the semantics of the creation process for parameterized
     * types.
     * 2.如果超接口是参数化类型，则为其返回的 Type对象必须准确反映源代码中使用的实际类型参数。
     * 如果之前未创建，则创建代表每个超级接口的参数化类型。
     * 参数化类型的创建过程语义见java.lang.reflect.ParameterizedType ParameterizedType的声明
     * <p> If this object represents a class, the return value is an
     * array containing objects representing all interfaces
     * implemented by the class. The order of the interface objects in
     * the array corresponds to the order of the interface names in
     * the {@code implements} clause of the declaration of the class
     * represented by this object.  In the case of an array class, the
     * interfaces {@code Cloneable} and {@code Serializable} are
     * returned in that order.
     * 3.如果此对象代表一个类，则返回值是一个数组，其中包含代表该类实现的所有接口的对象。
     * 数组中接口对象的顺序与该对象表示的类的声明的implements子句中接口名称的顺序相对应。
     * 在数组类的情况下，接口 Cloneable和 Serializable按该顺序返回
     * <p>If this object represents an interface, the array contains
     * objects representing all interfaces directly extended by the
     * interface.  The order of the interface objects in the array
     * corresponds to the order of the interface names in the
     * {@code extends} clause of the declaration of the interface
     * represented by this object.
     * 6.如果此对象代表一个接口，则该数组包含代表该接口直接扩展的所有接口的对象。
     * 数组中接口对象的顺序与该对象所代表的接口声明的 extends子句中接口名称的顺序相对应
     * <p>If this object represents a class or interface that
     * implements no interfaces, the method returns an array of length
     * 0.
     * 7.如果此对象表示未实现任何接口的类或接口，则该方法返回长度为 0 的数组
     * <p>If this object represents a primitive type or void, the
     * method returns an array of length 0.
     *8.如果此对象表示原始类型或 void，则该方法返回长度为 0 的数组
     * @throws java.lang.reflect.GenericSignatureFormatError
     *     if the generic class signature does not conform to the format
     *     specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException if any of the generic
     *     superinterfaces refers to a non-existent type declaration
     * @throws java.lang.reflect.MalformedParameterizedTypeException
     *     if any of the generic superinterfaces refer to a parameterized
     *     type that cannot be instantiated for any reason
     * @return an array of interfaces implemented by this class
     * @since 1.5
     */
    public Type[] getGenericInterfaces() {
        ClassRepository info = getGenericInfo();
        return (info == null) ?  getInterfaces() : info.getSuperInterfaces();
    }


    /**
     * Returns the {@code Class} representing the component type of an
     * array.  If this class does not represent an array class this method
     * returns null.
     * 1.返回表示数组组件类型的 Class。如果此类不代表数组类，则此方法返回 null
     * @return the {@code Class} representing the component type of this
     * class if this class is an array
     * @see     java.lang.reflect.Array
     * @since JDK1.1
     */
    public native Class<?> getComponentType();


    /**
     * Returns the Java language modifiers for this class or interface, encoded
     * in an integer. The modifiers consist of the Java Virtual Machine's
     * constants for {@code public}, {@code protected},
     * {@code private}, {@code final}, {@code static},
     * {@code abstract} and {@code interface}; they should be decoded
     * using the methods of class {@code Modifier}.
     * 1.返回此类或接口的 Java 语言修饰符，以整数编码。
     * 修饰符包括 public、protected、private、final、static、abstract 和interface的 Java 虚拟机常量;
     * 它们应该使用类  Modifier的方法进行解码
     *
     * <p> If the underlying class is an array class, then its
     * {@code public}, {@code private} and {@code protected}
     * modifiers are the same as those of its component type.  If this
     * {@code Class} represents a primitive type or void, its
     * {@code public} modifier is always {@code true}, and its
     * {@code protected} and {@code private} modifiers are always
     * {@code false}. If this object represents an array class, a
     * primitive type or void, then its {@code final} modifier is always
     * {@code true} and its interface modifier is always
     * {@code false}. The values of its other modifiers are not determined
     * by this specification.
     * 如果底层类是数组类，则其 public、private和protected修饰符与其组件类型的修饰符相同。
     * 如果此 Class表示原始类型或 void，则其 public修饰符始终为true，
     * 其 protected和 private修饰符始终为 false.
     * 如果此对象表示数组类、原始类型或 void，则其final修饰符始终为 true，
     * 其接口修饰符始终为 false。它的其他修饰符的值不是由本规范决定的
     * <p> The modifier encodings are defined in <em>The Java Virtual Machine
     * Specification</em>, table 4.1.
     *
     * @return the {@code int} representing the modifiers for this class
     * @see     java.lang.reflect.Modifier
     * @since JDK1.1
     */
    public native int getModifiers();


    /**
     * Gets the signers of this class.
     * 1.获取该类的签名者
     * @return  the signers of this class, or null if there are no signers.  In
     *          particular, this method returns null if this object represents
     *          a primitive type or void.
     * 2.此类的签名者，如果没有签名者，则为 null。特别是，如果此对象表示原始类型或 void，则此方法返回 null
     * @since   JDK1.1
     */
    public native Object[] getSigners();


    /**
     * Set the signers of this class.
     * 设置这个类的签名者
     */
    native void setSigners(Object[] signers);


    /**
     * If this {@code Class} object represents a local or anonymous
     * class within a method, returns a {@link
     * java.lang.reflect.Method Method} object representing the
     * immediately enclosing method of the underlying class. Returns
     * {@code null} otherwise.
     * 1.如果此Class对象表示方法内的本地或匿名类，则返回表示底层类的直接封闭方法的
     * java.lang.reflect.Method Method对象。否则返回null
     *
     * In particular, this method returns {@code null} if the underlying
     * class is a local or anonymous class immediately enclosed by a type
     * declaration, instance initializer or static initializer.
     * 2.特别是，如果底层类是本地类或匿名类，则此方法返回 null，
     * 该类立即被类型声明、实例初始化程序或静态初始化程序包围
     * @return the immediately enclosing method of the underlying class, if
     *     that class is a local or anonymous class; otherwise {@code null}.
     *
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and any of the
     *         following conditions is met:
     *
     *         <ul>
     *
     *         <li> the caller's class loader is not the same as the
     *         class loader of the enclosing class and invocation of
     *         {@link SecurityManager#checkPermission
     *         s.checkPermission} method with
     *         {@code RuntimePermission("accessDeclaredMembers")}
     *         denies access to the methods within the enclosing class
     *
     *         <li> the caller's class loader is not the same as or an
     *         ancestor of the class loader for the enclosing class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of the enclosing class
     *
     *         </ul>
     * @since 1.5
     */
    @CallerSensitive
    public Method getEnclosingMethod() throws SecurityException {
        EnclosingMethodInfo enclosingInfo = getEnclosingMethodInfo();

        if (enclosingInfo == null)
            return null;
        else {
            if (!enclosingInfo.isMethod())
                return null;

            MethodRepository typeInfo = MethodRepository.make(enclosingInfo.getDescriptor(),
                                                              getFactory());
            Class<?>   returnType       = toClass(typeInfo.getReturnType());
            Type []    parameterTypes   = typeInfo.getParameterTypes();
            Class<?>[] parameterClasses = new Class<?>[parameterTypes.length];

            // Convert Types to Classes; returned types *should*
            // be class objects since the methodDescriptor's used
            // don't have generics information
            for(int i = 0; i < parameterClasses.length; i++)
                parameterClasses[i] = toClass(parameterTypes[i]);

            // Perform access check
            Class<?> enclosingCandidate = enclosingInfo.getEnclosingClass();
            enclosingCandidate.checkMemberAccess(Member.DECLARED,
                                                 Reflection.getCallerClass(), true);
            /*
             * Loop over all declared methods; match method name,
             * number of and type of parameters, *and* return
             * type.  Matching return type is also necessary
             * because of covariant returns, etc.
             * 循环所有声明的方法；匹配方法名称、参数数量和类型以及返回类型。
             * 由于协变返回等原因，还需要匹配返回类型
             */
            for(Method m: enclosingCandidate.getDeclaredMethods()) {
                if (m.getName().equals(enclosingInfo.getName()) ) {
                    Class<?>[] candidateParamClasses = m.getParameterTypes();
                    if (candidateParamClasses.length == parameterClasses.length) {
                        boolean matches = true;
                        for(int i = 0; i < candidateParamClasses.length; i++) {
                            if (!candidateParamClasses[i].equals(parameterClasses[i])) {
                                matches = false;
                                break;
                            }
                        }

                        if (matches) { // finally, check return type
                            if (m.getReturnType().equals(returnType) )
                                return m;
                        }
                    }
                }
            }

            throw new InternalError("Enclosing method not found");
        }
    }

    private native Object[] getEnclosingMethod0();

    private EnclosingMethodInfo getEnclosingMethodInfo() {
        Object[] enclosingInfo = getEnclosingMethod0();
        if (enclosingInfo == null)
            return null;
        else {
            return new EnclosingMethodInfo(enclosingInfo);
        }
    }

    //封闭的方法信息
    private final static class EnclosingMethodInfo {
        //封闭的Class对象
        private Class<?> enclosingClass;
        //名称
        private String name;
        //描述符
        private String descriptor;

        private EnclosingMethodInfo(Object[] enclosingInfo) {
            if (enclosingInfo.length != 3)
                throw new InternalError("Malformed enclosing method information");
            try {
                // The array is expected to have three elements:
                //该数组预计具有三个元素：
                // the immediately enclosing class
                //直接封闭的类
                enclosingClass = (Class<?>) enclosingInfo[0];
                assert(enclosingClass != null);

                // the immediately enclosing method or constructor's
                // name (can be null).
                //直接封闭的方法或构造函数的名称（可以为 null）
                name            = (String)   enclosingInfo[1];

                // the immediately enclosing method or constructor's
                // descriptor (null iff name is).
                //直接封闭的方法或构造函数的描述符（如果名称为空，则为空
                descriptor      = (String)   enclosingInfo[2];
                assert((name != null && descriptor != null) || name == descriptor);
            } catch (ClassCastException cce) {
                throw new InternalError("Invalid type in enclosing method information", cce);
            }
        }

        boolean isPartial() {
            return enclosingClass == null || name == null || descriptor == null;
        }

        boolean isConstructor() { return !isPartial() && "<init>".equals(name); }

        boolean isMethod() { return !isPartial() && !isConstructor() && !"<clinit>".equals(name); }

        Class<?> getEnclosingClass() { return enclosingClass; }

        String getName() { return name; }

        String getDescriptor() { return descriptor; }

    }

    private static Class<?> toClass(Type o) {
        if (o instanceof GenericArrayType)
            return Array.newInstance(toClass(((GenericArrayType)o).getGenericComponentType()),
                                     0)
                .getClass();
        return (Class<?>)o;
     }

    /**
     * If this {@code Class} object represents a local or anonymous
     * class within a constructor, returns a {@link
     * java.lang.reflect.Constructor Constructor} object representing
     * the immediately enclosing constructor of the underlying
     * class. Returns {@code null} otherwise.  In particular, this
     * method returns {@code null} if the underlying class is a local
     * or anonymous class immediately enclosed by a type declaration,
     * instance initializer or static initializer.
     *  1.如果此 Class对象表示构造函数中的本地或匿名类，
     *  则返回表示底层类的直接封闭构造函数的 java.lang.reflect.Constructor Constructor对象。
     *  否则返回 null。特别是，如果底层类是本地类或匿名类，则此方法返回 null，
     *  该类立即被类型声明、实例初始化程序或静态初始化程序包围
     * @return the immediately enclosing constructor of the underlying class, if
     *     that class is a local or anonymous class; otherwise {@code null}.
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and any of the
     *         following conditions is met:
     *
     *         <ul>
     *
     *         <li> the caller's class loader is not the same as the
     *         class loader of the enclosing class and invocation of
     *         {@link SecurityManager#checkPermission
     *         s.checkPermission} method with
     *         {@code RuntimePermission("accessDeclaredMembers")}
     *         denies access to the constructors within the enclosing class
     *
     *         <li> the caller's class loader is not the same as or an
     *         ancestor of the class loader for the enclosing class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of the enclosing class
     *
     *         </ul>
     * @since 1.5
     */
    @CallerSensitive
    public Constructor<?> getEnclosingConstructor() throws SecurityException {
        EnclosingMethodInfo enclosingInfo = getEnclosingMethodInfo();

        if (enclosingInfo == null)
            return null;
        else {
            if (!enclosingInfo.isConstructor())
                return null;

            ConstructorRepository typeInfo = ConstructorRepository.make(enclosingInfo.getDescriptor(),
                                                                        getFactory());
            Type []    parameterTypes   = typeInfo.getParameterTypes();
            Class<?>[] parameterClasses = new Class<?>[parameterTypes.length];

            // Convert Types to Classes; returned types *should*
            // be class objects since the methodDescriptor's used
            // don't have generics information
            //将类型转换为类；返回的类型应该是类对象，因为使用的 methodDescriptor 没有泛型信息
            for(int i = 0; i < parameterClasses.length; i++)
                parameterClasses[i] = toClass(parameterTypes[i]);

            // Perform access check
            //执行访问检查
            Class<?> enclosingCandidate = enclosingInfo.getEnclosingClass();
            enclosingCandidate.checkMemberAccess(Member.DECLARED,
                                                 Reflection.getCallerClass(), true);
            /*
             * Loop over all declared constructors; match number
             * of and type of parameters.
             * 循环所有声明的构造函数；匹配参数的数量和类型
             */
            for(Constructor<?> c: enclosingCandidate.getDeclaredConstructors()) {
                Class<?>[] candidateParamClasses = c.getParameterTypes();
                if (candidateParamClasses.length == parameterClasses.length) {
                    boolean matches = true;
                    for(int i = 0; i < candidateParamClasses.length; i++) {
                        if (!candidateParamClasses[i].equals(parameterClasses[i])) {
                            matches = false;
                            break;
                        }
                    }

                    if (matches)
                        return c;
                }
            }

            throw new InternalError("Enclosing constructor not found");
        }
    }


    /**
     * If the class or interface represented by this {@code Class} object
     * is a member of another class, returns the {@code Class} object
     * representing the class in which it was declared.  This method returns
     * null if this class or interface is not a member of any other class.  If
     * this {@code Class} object represents an array class, a primitive
     * type, or void,then this method returns null.
     * 1.如果此Class对象表示的类或接口是另一个类的成员，则返回表示声明它的类的Class对象。
     * 如果此类或接口不是任何其他类的成员，则此方法返回 null。如果此Class对象表示数组类、原始类型或 void，
     * 则此方法返回 null
     * @return the declaring class for this class
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and the caller's
     *         class loader is not the same as or an ancestor of the class
     *         loader for the declaring class and invocation of {@link
     *         SecurityManager#checkPackageAccess s.checkPackageAccess()}
     *         denies access to the package of the declaring class
     * @since JDK1.1
     */
    @CallerSensitive
    public Class<?> getDeclaringClass() throws SecurityException {
        final Class<?> candidate = getDeclaringClass0();

        if (candidate != null)
            candidate.checkPackageAccess(
                    ClassLoader.getClassLoader(Reflection.getCallerClass()), true);
        return candidate;
    }

    private native Class<?> getDeclaringClass0();


    /**
     * Returns the immediately enclosing class of the underlying
     * class.  If the underlying class is a top level class this
     * method returns {@code null}.
     * 返回底层类的直接封闭类。如果底层类是顶级类，则此方法返回 {@code null}
     * @return the immediately enclosing class of the underlying class
     * @exception  SecurityException
     *             If a security manager, <i>s</i>, is present and the caller's
     *             class loader is not the same as or an ancestor of the class
     *             loader for the enclosing class and invocation of {@link
     *             SecurityManager#checkPackageAccess s.checkPackageAccess()}
     *             denies access to the package of the enclosing class
     * @since 1.5
     */
    @CallerSensitive
    public Class<?> getEnclosingClass() throws SecurityException {
        // There are five kinds of classes (or interfaces):
        // a) Top level classes
        // b) Nested classes (static member classes)
        // c) Inner classes (non-static member classes)
        // d) Local classes (named classes declared within a method)
        // e) Anonymous classes


        // JVM Spec 4.8.6: A class must have an EnclosingMethod
        // attribute if and only if it is a local class or an
        // anonymous class.
        //有五种类（或接口）：
        // a）顶级类
        // b）嵌套类（静态成员类）
        // c）内部类（非静态成员类）
        // d）局部类（在方法中声明的命名类）
        // e)匿名类
        // JVM 规范 4.8.6：
        // 当且仅当一个类是本地类或匿名类时，该类必须具有 EnclosureMethod 属性
        EnclosingMethodInfo enclosingInfo = getEnclosingMethodInfo();
        Class<?> enclosingCandidate;

        if (enclosingInfo == null) {
            // This is a top level or a nested class or an inner class (a, b, or c)
            //这是顶级或嵌套类或内部类（a、b 或 c)
            enclosingCandidate = getDeclaringClass();
        } else {
            Class<?> enclosingClass = enclosingInfo.getEnclosingClass();
            // This is a local class or an anonymous class (d or e)
            //他是本地类或匿名类（d 或 e）
            if (enclosingClass == this || enclosingClass == null)
                throw new InternalError("Malformed enclosing method information");
            else
                enclosingCandidate = enclosingClass;
        }

        if (enclosingCandidate != null)
            enclosingCandidate.checkPackageAccess(
                    ClassLoader.getClassLoader(Reflection.getCallerClass()), true);
        return enclosingCandidate;
    }

    /**
     * Returns the simple name of the underlying class as given in the
     * source code. Returns an empty string if the underlying class is
     * anonymous.
     * 返回源代码中给出的基础类的简单名称。如果底层类是匿名的，则返回一个空字符串
     * <p>The simple name of an array is the simple name of the
     * component type with "[]" appended.  In particular the simple
     * name of an array whose component type is anonymous is "[]".
     * 数组的简单名称是附加“[]”的组件类型的简单名称。特别是组件类型为匿名的数组的简单名称是“[]”
     * @return the simple name of the underlying class
     * @since 1.5
     */
    public String getSimpleName() {
        //首先判断是否是数组
        if (isArray())
            //返回表示数组组件类型的 Class
            return getComponentType().getSimpleName()+"[]";

        //获取底层简单名称
        String simpleName = getSimpleBinaryName();
        if (simpleName == null) { // top level class
            //如果为null,则表示顶层类
            simpleName = getName();
            //去掉包名
            return simpleName.substring(simpleName.lastIndexOf(".")+1); // strip the package name
        }
        // According to JLS3 "Binary Compatibility" (13.1) the binary
        // name of non-package classes (not top level) is the binary
        // name of the immediately enclosing class followed by a '$' followed by:
        // (for nested and inner classes): the simple name.
        // (for local classes): 1 or more digits followed by the simple name.
        // (for anonymous classes): 1 or more digits.
        //根据 JLS3“二进制兼容性”（13.1），
        // 非包类（不是顶级）的二进制名称是紧接其后的类的二进制名称后跟一个 '$'：
        // （对于嵌套类和内部类）：简单的姓名。 （
        // 对于本地类）：1 个或多个数字后跟简单名称。
        // （对于匿名类）：1 个或多个数字

        // Since getSimpleBinaryName() will strip the binary name of
        // the immediatly enclosing class, we are now looking at a
        // string that matches the regular expression "\$[0-9]*"
        // followed by a simple name (considering the simple of an
        // anonymous class to be the empty string).
        //由于 getSimpleBinaryName() 将剥离直接封闭类的二进制名称，
        // 我们现在查看匹配正则表达式“$[0-9]*”的字符串，后跟一个简单名称（考虑匿名类的简单名称为空字符串
        // Remove leading "\$[0-9]*" from the name
        int length = simpleName.length();
        if (length < 1 || simpleName.charAt(0) != '$')
            throw new InternalError("Malformed class name");
        int index = 1;
        while (index < length && isAsciiDigit(simpleName.charAt(index)))
            index++;
        // Eventually, this is the empty string iff this is an anonymous class
       // 最终，如果这是一个匿名类，这是空字符串
        return simpleName.substring(index);
    }

    /**
     * Return an informative string for the name of this type.
     *  返回此类型名称的信息字符串
     * @return an informative string for the name of this type
     * @since 1.8
     */
    public String getTypeName() {
        //判断是否是数组
        if (isArray()) {
            try {
                Class<?> cl = this;
                //记录是几维数组
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]");
                }
                return sb.toString();
            } catch (Throwable e) { /*FALLTHRU*/ }
        }
        return getName();
    }

    /**
     * Character.isDigit answers {@code true} to some non-ascii
     * digits.  This one does not.
     */
    private static boolean isAsciiDigit(char c) {
        return '0' <= c && c <= '9';
    }

    /**
     * Returns the canonical name of the underlying class as
     * defined by the Java Language Specification.  Returns null if
     * the underlying class does not have a canonical name (i.e., if
     * it is a local or anonymous class or an array whose component
     * type does not have a canonical name).
     * 返回 Java 语言规范定义的基础类的规范名称。
     * 如果底层类没有规范名称（即，如果它是本地或匿名类或组件类型没有规范名称的数组），则返回 nul
     * @return the canonical name of the underlying class if it exists, and
     * {@code null} otherwise.
     * @since 1.5
     */
    public String getCanonicalName() {
        if (isArray()) {
            //获取数组的类型,然后在获取基础名称
            String canonicalName = getComponentType().getCanonicalName();
            if (canonicalName != null)
                return canonicalName + "[]";
            else
                return null;
        }
        //本地或匿名类 返回null
        if (isLocalOrAnonymousClass())
            return null;
        //获取封闭类
        Class<?> enclosingClass = getEnclosingClass();

        //没有就是顶层类
        if (enclosingClass == null) { // top level class
            //直接获取顶层类名称
            return getName();
        } else {

            String enclosingName = enclosingClass.getCanonicalName();
            if (enclosingName == null)
                return null;
            return enclosingName + "." + getSimpleName();
        }
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * is an anonymous class.
     *  当且仅当底层类是匿名类时才返回 true
     * @return {@code true} if and only if this class is an anonymous class.
     * @since 1.5
     */
    public boolean isAnonymousClass() {
        return "".equals(getSimpleName());
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * is a local class.
     *  当且仅当底层类是本地类时才返回 true
     * @return {@code true} if and only if this class is a local class.
     * @since 1.5
     */
    public boolean isLocalClass() {
        return isLocalOrAnonymousClass() && !isAnonymousClass();
    }

    /**
     * Returns {@code true} if and only if the underlying class
     * is a member class.
     *当且仅当基础类是成员类时才返回  true
     * @return {@code true} if and only if this class is a member class.
     * @since 1.5
     */
    public boolean isMemberClass() {
        return getSimpleBinaryName() != null && !isLocalOrAnonymousClass();
    }

    /**
     * Returns the "simple binary name" of the underlying class, i.e.,
     * the binary name without the leading enclosing class name.
     * Returns {@code null} if the underlying class is a top level
     * class.
     * 返回底层类的“简单二进制名称”，即没有前导类名称的二进制名称。
     * 如果底层类是顶级类，则返回 {@code null}
     */
    private String getSimpleBinaryName() {
        Class<?> enclosingClass = getEnclosingClass();
        if (enclosingClass == null) // top level class
            return null;
        // Otherwise, strip the enclosing class' name
        try {
            return getName().substring(enclosingClass.getName().length());
        } catch (IndexOutOfBoundsException ex) {
            throw new InternalError("Malformed class name", ex);
        }
    }

    /**
     * Returns {@code true} if this is a local class or an anonymous
     * class.  Returns {@code false} otherwise.
     * 如果这是本地类或匿名类，则返回 true。否则返回 false
     */
    private boolean isLocalOrAnonymousClass() {
        // JVM Spec 4.8.6: A class must have an EnclosingMethod
        // attribute if and only if it is a local class or an
        // anonymous class.
        //JVM 规范 4.8.6：当且仅当类是本地类或匿名类时，类必须具有 EnclosureMethod 属性
        return getEnclosingMethodInfo() != null;
    }

    /**
     * Returns an array containing {@code Class} objects representing all
     * the public classes and interfaces that are members of the class
     * represented by this {@code Class} object.  This includes public
     * class and interface members inherited from superclasses and public class
     * and interface members declared by the class.  This method returns an
     * array of length 0 if this {@code Class} object has no public member
     * classes or interfaces.  This method also returns an array of length 0 if
     * this {@code Class} object represents a primitive type, an array
     * class, or void.
     * 返回一个包含 Class对象的数组，这些对象表示作为此Class对象表示的类的成员的所有公共类和接口。
     * 这包括从超类继承的公共类和接口成员以及由该类声明的公共类和接口成员。
     * 如果此 Class对象没有公共成员类或接口，则此方法返回长度为 0 的数组。
     * 如果此Class对象表示原始类型、数组类或 void，则此方法还返回长度为 0 的数组
     *
     * @return the array of {@code Class} objects representing the public
     *         members of this class
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @since JDK1.1
     */
    @CallerSensitive
    public Class<?>[] getClasses() {
        //检查成员的访问权限
        checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), false);

        // Privileged so this implementation can look at DECLARED classes,
        // something the caller might not have privilege to do.  The code here
        // is allowed to look at DECLARED classes because (1) it does not hand
        // out anything other than public members and (2) public member access
        // has already been ok'd by the SecurityManager.
        //因此此实现可以查看已声明的类，调用者可能无权执行此操作。
        // 允许这里的代码查看 DECLARED 类，
        // 因为 (1) 它不分发公共成员以外的任何东西，并且
        // (2) SecurityManager 已经确定公共成员访问权限

        return java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction<Class<?>[]>() {
                public Class<?>[] run() {
                    List<Class<?>> list = new ArrayList<>();
                    Class<?> currentClass = Class.this;
                    while (currentClass != null) {
                        Class<?>[] members = currentClass.getDeclaredClasses();
                        for (int i = 0; i < members.length; i++) {
                            if (Modifier.isPublic(members[i].getModifiers())) {
                                list.add(members[i]);
                            }
                        }
                        currentClass = currentClass.getSuperclass();
                    }
                    return list.toArray(new Class<?>[0]);
                }
            });
    }


    /**
     * Returns an array containing {@code Field} objects reflecting all
     * the accessible public fields of the class or interface represented by
     * this {@code Class} object.
     * 1.返回一个包含 Field对象的数组，这些对象反映了此 Class对象表示的类或接口的所有可访问公共字段
     *
     * <p> If this {@code Class} object represents a class or interface with no
     * no accessible public fields, then this method returns an array of length
     * 0.
     * 2.如果此 Class对象表示没有可访问公共字段的类或接口，则此方法返回长度为 0 的数组
     * <p> If this {@code Class} object represents a class, then this method
     * returns the public fields of the class and of all its superclasses.
     * 3.如果此 Class对象表示一个类，则此方法返回该类及其所有超类的公共字段
     * <p> If this {@code Class} object represents an interface, then this
     * method returns the fields of the interface and of all its
     * superinterfaces.
     * 4.如果此 Class对象表示一个接口，则此方法返回该接口及其所有超接口的字段
     *
     * <p> If this {@code Class} object represents an array type, a primitive
     * type, or void, then this method returns an array of length 0.
     * 5.如果此 Class对象表示数组类型、原始类型或 void，则此方法返回长度为 0 的数组
     * <p> The elements in the returned array are not sorted and are not in any
     * particular order.
     * 6.返回的数组中的元素没有排序，也没有任何特定的顺序
     * @return the array of {@code Field} objects representing the
     *         public fields
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @since JDK1.1
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     */
    @CallerSensitive
    public Field[] getFields() throws SecurityException {
        checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), true);
        return copyFields(privateGetPublicFields(null));
    }


    /**
     * Returns an array containing {@code Method} objects reflecting all the
     * public methods of the class or interface represented by this {@code
     * Class} object, including those declared by the class or interface and
     * those inherited from superclasses and superinterfaces.
     * 1.返回一个包含 Method对象的数组，这些对象反映了此Class对象表示的类或接口的所有公共方法，
     * 包括由类或接口声明的方法以及从超类和超接口继承的方法
     * <p> If this {@code Class} object represents a type that has multiple
     * public methods with the same name and parameter types, but different
     * return types, then the returned array has a {@code Method} object for
     * each such method.
     * 2.如果此Class对象表示具有多个具有相同名称和参数类型但返回类型不同的公共方法的类型，
     * 则返回的数组对于每个此类方法都有一个 Method对象
     * <p> If this {@code Class} object represents a type with a class
     * initialization method {@code <clinit>}, then the returned array does
     * <em>not</em> have a corresponding {@code Method} object.
     * 3.如果此 Class对象表示具有类初始化方法 <clinit> 的类型，
     * 则返回的数组不具有相应的 Method对象
     * <p> If this {@code Class} object represents an array type, then the
     * returned array has a {@code Method} object for each of the public
     * methods inherited by the array type from {@code Object}. It does not
     * contain a {@code Method} object for {@code clone()}.
     * 4.如果此 Class对象表示一个数组类型，则返回的数组具有一个 Method对象，
     * 用于该数组类型从Object继承的每个公共方法。它不包含 clone()的 Method对象
     * <p> If this {@code Class} object represents an interface then the
     * returned array does not contain any implicitly declared methods from
     * {@code Object}. Therefore, if no methods are explicitly declared in
     * this interface or any of its superinterfaces then the returned array
     * has length 0. (Note that a {@code Class} object which represents a class
     * always has public methods, inherited from {@code Object}.)
     * 5.如果此 Class对象表示一个接口，则返回的数组不包含来自 Object 的任何隐式声明的方法。
     * 因此，如果在此接口或其任何超接口中没有显式声明方法，则返回的数组长度为 0。
     * （请注意，表示类的 Class对象始终具有从 Object继承的公共方法.)
     *
     * <p> If this {@code Class} object represents a primitive type or void,
     * then the returned array has length 0.
     * 6.如果此Class对象表示原始类型或 void，则返回的数组长度为 0
     * <p> Static methods declared in superinterfaces of the class or interface
     * represented by this {@code Class} object are not considered members of
     * the class or interface.
     * 7.在此Class对象表示的类或接口的超接口中声明的静态方法不被视为类或接口的成员
     * <p> The elements in the returned array are not sorted and are not in any
     * particular order.
     * 8.返回的数组中的元素没有排序，也没有任何特定的顺序
     * @return the array of {@code Method} objects representing the
     *         public methods of this class
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */
    @CallerSensitive
    public Method[] getMethods() throws SecurityException {
        checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), true);
        return copyMethods(privateGetPublicMethods());
    }


    /**
     * Returns an array containing {@code Constructor} objects reflecting
     * all the public constructors of the class represented by this
     * {@code Class} object.  An array of length 0 is returned if the
     * class has no public constructors, or if the class is an array class, or
     * if the class reflects a primitive type or void.
     * 1.返回一个包含 Constructor对象的数组，该对象反映了此 Class对象表示的类的所有公共构造函数。
     * 如果该类没有公共构造函数，或者如果该类是一个数组类，或者如果该类反映一个基本类型或 void，则返回长度为 0 的数组
     * Note that while this method returns an array of {@code
     * Constructor<T>} objects (that is an array of constructors from
     * this class), the return type of this method is {@code
     * Constructor<?>[]} and <em>not</em> {@code Constructor<T>[]} as
     * might be expected.  This less informative return type is
     * necessary since after being returned from this method, the
     * array could be modified to hold {@code Constructor} objects for
     * different classes, which would violate the type guarantees of
     * {@code Constructor<T>[]}.
     * 2.请注意，虽然此方法返回一个 Constructor<T>对象数组（即来自此类的构造函数数组），
     * 但此方法的返回类型是 Constructor<?>[]和 不是Constructor<T>[]
     * 正如预期的那样。这种信息较少的返回类型是必要的，因为从该方法返回后，
     * 可以修改数组以保存不同类的Constructor象，这将违反 Constructor<T>[]的类型保证
     * @return the array of {@code Constructor} objects representing the
     *         public constructors of this class
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @since JDK1.1
     */
    @CallerSensitive
    public Constructor<?>[] getConstructors() throws SecurityException {
        checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), true);
        return copyConstructors(privateGetDeclaredConstructors(true));
    }


    /**
     * Returns a {@code Field} object that reflects the specified public member
     * field of the class or interface represented by this {@code Class}
     * object. The {@code name} parameter is a {@code String} specifying the
     * simple name of the desired field.
     * 1.返回一个 Field对象，该对象反映此Class对象表示的类或接口的指定公共成员字段。
     * name参数是一个String，指定所需字段的简单名称
     * <p> The field to be reflected is determined by the algorithm that
     * follows.  Let C be the class or interface represented by this object:
     *
     * <OL>
     * <LI> If C declares a public field with the name specified, that is the
     *      field to be reflected.</LI>
     * <LI> If no field was found in step 1 above, this algorithm is applied
     *      recursively to each direct superinterface of C. The direct
     *      superinterfaces are searched in the order they were declared.</LI>
     * <LI> If no field was found in steps 1 and 2 above, and C has a
     *      superclass S, then this algorithm is invoked recursively upon S.
     *      If C has no superclass, then a {@code NoSuchFieldException}
     *      is thrown.</LI>
     * </OL>
     *
     * <p> If this {@code Class} object represents an array type, then this
     * method does not find the {@code length} field of the array type.
     * 如果这个Class对象代表的是一个数组类型，那么这个方法就找不到该数组类型的length字段
     * @param name the field name
     * @return the {@code Field} object of this class specified by
     *         {@code name}
     * @throws NoSuchFieldException if a field with the specified name is
     *         not found.
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @since JDK1.1
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     */
    @CallerSensitive
    public Field getField(String name)
        throws NoSuchFieldException, SecurityException {
        checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), true);
        Field field = getField0(name);
        if (field == null) {
            throw new NoSuchFieldException(name);
        }
        return field;
    }


    /**
     * Returns a {@code Method} object that reflects the specified public
     * member method of the class or interface represented by this
     * {@code Class} object. The {@code name} parameter is a
     * {@code String} specifying the simple name of the desired method. The
     * {@code parameterTypes} parameter is an array of {@code Class}
     * objects that identify the method's formal parameter types, in declared
     * order. If {@code parameterTypes} is {@code null}, it is
     * treated as if it were an empty array.
     * 1.返回一个 Method对象，该对象反映此Class对象表示的类或接口的指定公共成员方法。
     * name参数是一个 String，指定所需方法的简单名称。parameterTypes参数是一个 Class对象数组，
     * 这些对象按声明的顺序标识方法的形式参数类型。如果 parameterTypes为 null，则将其视为空数组
     *
     * <p> If the {@code name} is "{@code <init>}" or "{@code <clinit>}" a
     * {@code NoSuchMethodException} is raised. Otherwise, the method to
     * be reflected is determined by the algorithm that follows.  Let C be the
     * class or interface represented by this object:
     * <OL>
     * <LI> C is searched for a <I>matching method</I>, as defined below. If a
     *      matching method is found, it is reflected.</LI>
     * <LI> If no matching method is found by step 1 then:
     *   <OL TYPE="a">
     *   <LI> If C is a class other than {@code Object}, then this algorithm is
     *        invoked recursively on the superclass of C.</LI>
     *   <LI> If C is the class {@code Object}, or if C is an interface, then
     *        the superinterfaces of C (if any) are searched for a matching
     *        method. If any such method is found, it is reflected.</LI>
     *   </OL></LI>
     * </OL>
     *
     * <p> To find a matching method in a class or interface C:&nbsp; If C
     * declares exactly one public method with the specified name and exactly
     * the same formal parameter types, that is the method reflected. If more
     * than one such method is found in C, and one of these methods has a
     * return type that is more specific than any of the others, that method is
     * reflected; otherwise one of the methods is chosen arbitrarily.
     *  1.在类或接口 C 中查找匹配的方法：如果 C 仅声明了一个具有指定名称和完全相同的形式参数类型的公共方法，
     *  则该方法就是反射的方法。如果在 C 中找到多个这样的方法，
     *  并且这些方法之一的返回类型比其他任何一个方法都更具体，则反映该方法；否则任意选择其中一种方法
     * <p>Note that there may be more than one matching method in a
     * class because while the Java language forbids a class to
     * declare multiple methods with the same signature but different
     * return types, the Java virtual machine does not.  This
     * increased flexibility in the virtual machine can be used to
     * implement various language features.  For example, covariant
     * returns can be implemented with {@linkplain
     * java.lang.reflect.Method#isBridge bridge methods}; the bridge
     * method and the method being overridden would have the same
     * signature but different return types.
     * 2.请注意，一个类中可能有多个匹配的方法，因为 Java 语言禁止一个类声明多个具有相同签名但返回类型不同的方法，
     * 而 Java 虚拟机则没有。虚拟机中这种增加的灵活性可用于实现各种语言功能。
     * 例如，协变返回可以用java.lang.reflect.MethodisBridge 桥接方法来实现；
     * 桥接方法和被覆盖的方法将具有相同的签名但不同的返回类型
     *
     * <p> If this {@code Class} object represents an array type, then this
     * method does not find the {@code clone()} method.
     * 如果此 Class对象表示数组类型，则此方法找不到 clone()方法
     * <p> Static methods declared in superinterfaces of the class or interface
     * represented by this {@code Class} object are not considered members of
     * the class or interface.
     * 在此 Class对象表示的类或接口的超接口中声明的静态方法不被视为类或接口的成员
     * @param name the name of the method
     * @param parameterTypes the list of parameters
     * @return the {@code Method} object that matches the specified
     *         {@code name} and {@code parameterTypes}
     * @throws NoSuchMethodException if a matching method is not found
     *         or if the name is "&lt;init&gt;"or "&lt;clinit&gt;".
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */
    @CallerSensitive
    public Method getMethod(String name, Class<?>... parameterTypes)
        throws NoSuchMethodException, SecurityException {
        checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), true);
        Method method = getMethod0(name, parameterTypes, true);
        if (method == null) {
            throw new NoSuchMethodException(getName() + "." + name + argumentTypesToString(parameterTypes));
        }
        return method;
    }


    /**
     * Returns a {@code Constructor} object that reflects the specified
     * public constructor of the class represented by this {@code Class}
     * object. The {@code parameterTypes} parameter is an array of
     * {@code Class} objects that identify the constructor's formal
     * parameter types, in declared order.
     * 1.返回一个 Constructor对象，该对象反映此 Class对象表示的类的指定公共构造函数。
     * parameterTypes参数是一个Class对象数组，按声明顺序标识构造函数的形参类型
     * If this {@code Class} object represents an inner class
     * declared in a non-static context, the formal parameter types
     * include the explicit enclosing instance as the first parameter.
     * 2.如果此 Class对象表示在非静态上下文中声明的内部类，则形参类型包括显式封闭实例作为第一个参数
     * <p> The constructor to reflect is the public constructor of the class
     * represented by this {@code Class} object whose formal parameter
     * types match those specified by {@code parameterTypes}.
     * 3.要反映的构造函数是此 Class对象表示的类的公共构造函数，其形式参数类型与parameterTypes指定的类型匹配
     * @param parameterTypes the parameter array
     * @return the {@code Constructor} object of the public constructor that
     *         matches the specified {@code parameterTypes}
     * @throws NoSuchMethodException if a matching method is not found.
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and
     *         the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class.
     *
     * @since JDK1.1
     */
    @CallerSensitive
    public Constructor<T> getConstructor(Class<?>... parameterTypes)
        throws NoSuchMethodException, SecurityException {
        checkMemberAccess(Member.PUBLIC, Reflection.getCallerClass(), true);
        return getConstructor0(parameterTypes, Member.PUBLIC);
    }


    /**
     * Returns an array of {@code Class} objects reflecting all the
     * classes and interfaces declared as members of the class represented by
     * this {@code Class} object. This includes public, protected, default
     * (package) access, and private classes and interfaces declared by the
     * class, but excludes inherited classes and interfaces.  This method
     * returns an array of length 0 if the class declares no classes or
     * interfaces as members, or if this {@code Class} object represents a
     * primitive type, an array class, or void.
     * 1.返回一个 Class对象数组，这些对象反映了声明为由这个Class对象表示的类的成员的所有类和接口。
     * 这包括公共、受保护、默认（包）访问以及类声明的私有类和接口，但不包括继承的类和接口。
     * 如果该类没有声明任何类或接口作为成员，或者如果此Class对象表示原始类型、数组类或 void，
     * 则此方法返回长度为 0 的数组
     * @return the array of {@code Class} objects representing all the
     *         declared members of this class
     * @throws SecurityException
     *         If a security manager, <i>s</i>, is present and any of the
     *         following conditions is met:
     *
     *         <ul>
     *
     *         <li> the caller's class loader is not the same as the
     *         class loader of this class and invocation of
     *         {@link SecurityManager#checkPermission
     *         s.checkPermission} method with
     *         {@code RuntimePermission("accessDeclaredMembers")}
     *         denies access to the declared classes within this class
     *
     *         <li> the caller's class loader is not the same as or an
     *         ancestor of the class loader for the current class and
     *         invocation of {@link SecurityManager#checkPackageAccess
     *         s.checkPackageAccess()} denies access to the package
     *         of this class
     *
     *         </ul>
     *
     * @since JDK1.1
     */
    @CallerSensitive
    public Class<?>[] getDeclaredClasses() throws SecurityException {
        checkMemberAccess(Member.DECLARED, Reflection.getCallerClass(), false);
        return getDeclaredClasses0();
    }


    /**
     * Returns an array of {@code Field} objects reflecting all the fields
     * declared by the class or interface represented by this
     * {@code Class} object. This includes public, protected, default
     * (package) access, and private fields, but excludes inherited fields.
     * 1.返回一个 Field对象数组，这些对象反映了由这个 Class对象表示的类或接口声明的所有字段。
     * 这包括公共、受保护、默认（包）访问和私有字段，但不包括继承的字段
     * <p> If this {@code Class} object represents a class or interface with no
     * declared fields, then this method returns an array of length 0.
     * 2.如果此 {@code Class} 对象表示没有声明字段的类或接口，则此方法返回长度为 0 的数组
     * <p> If this {@code Class} object represents an array type, a primitive
     * type, or void, then this method returns an array of length 0.
     * 3.如果此 {@code Class} 对象表示数组类型、原始类型或 void，则此方法返回长度为 0 的数组
     * <p> The elements in the returned array are not sorted and are not in any
     * particular order.
     * 4.返回的数组中的元素没有排序，也没有任何特定的顺序
     * @return  the array of {@code Field} objects representing all the
     *          declared fields of this class
     * @throws  SecurityException
     *          If a security manager, <i>s</i>, is present and any of the
     *          following conditions is met:
     *
     *          <ul>
     *
     *          <li> the caller's class loader is not the same as the
     *          class loader of this class and invocation of
     *          {@link SecurityManager#checkPermission
     *          s.checkPermission} method with
     *          {@code RuntimePermission("accessDeclaredMembers")}
     *          denies access to the declared fields within this class
     *
     *          <li> the caller's class loader is not the same as or an
     *          ancestor of the class loader for the current class and
     *          invocation of {@link SecurityManager#checkPackageAccess
     *          s.checkPackageAccess()} denies access to the package
     *          of this class
     *
     *          </ul>
     *
     * @since JDK1.1
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     */
    @CallerSensitive
    public Field[] getDeclaredFields() throws SecurityException {
        checkMemberAccess(Member.DECLARED, Reflection.getCallerClass(), true);
        return copyFields(privateGetDeclaredFields(false));
    }


    /**
     *
     * Returns an array containing {@code Method} objects reflecting all the
     * declared methods of the class or interface represented by this {@code
     * Class} object, including public, protected, default (package)
     * access, and private methods, but excluding inherited methods.
     * 1.返回一个包含 Method对象的数组，该对象反映了此Class对象表示的类或接口的所有声明方法，
     * 包括公共、受保护、默认（包）访问和私有方法，但不包括继承的方法
     * <p> If this {@code Class} object represents a type that has multiple
     * declared methods with the same name and parameter types, but different
     * return types, then the returned array has a {@code Method} object for
     * each such method.
     * 2.如果这个 Class对象表示一个类型，该类型具有多个具有相同名称和参数类型但返回类型不同的声明方法，
     * 则返回的数组对于每个这样的方法都有一个 Method对象
     * <p> If this {@code Class} object represents a type that has a class
     * initialization method {@code <clinit>}, then the returned array does
     * <em>not</em> have a corresponding {@code Method} object.
     * 3.如果此 Class对象表示具有类初始化方法 <clinit>的类型，
     * 则返回的数组<em>不<em>具有相应的Method对象
     * <p> If this {@code Class} object represents a class or interface with no
     * declared methods, then the returned array has length 0.
     * 4.如果此 Class对象表示没有声明方法的类或接口，则返回的数组长度为 0
     * <p> If this {@code Class} object represents an array type, a primitive
     * type, or void, then the returned array has length 0.
     * 5.如果此Class对象表示数组类型、原始类型或 void，则返回的数组长度为 0
     * <p> The elements in the returned array are not sorted and are not in any
     * particular order.
     *6.返回的数组中的元素没有排序，也没有任何特定的顺序
     * @return  the array of {@code Method} objects representing all the
     *          declared methods of this class
     * @throws  SecurityException
     *          If a security manager, <i>s</i>, is present and any of the
     *          following conditions is met:
     *
     *          <ul>
     *
     *          <li> the caller's class loader is not the same as the
     *          class loader of this class and invocation of
     *          {@link SecurityManager#checkPermission
     *          s.checkPermission} method with
     *          {@code RuntimePermission("accessDeclaredMembers")}
     *          denies access to the declared methods within this class
     *
     *          <li> the caller's class loader is not the same as or an
     *          ancestor of the class loader for the current class and
     *          invocation of {@link SecurityManager#checkPackageAccess
     *          s.checkPackageAccess()} denies access to the package
     *          of this class
     *
     *          </ul>
     *
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */
    @CallerSensitive
    public Method[] getDeclaredMethods() throws SecurityException {
        checkMemberAccess(Member.DECLARED, Reflection.getCallerClass(), true);
        return copyMethods(privateGetDeclaredMethods(false));
    }


    /**
     * Returns an array of {@code Constructor} objects reflecting all the
     * constructors declared by the class represented by this
     * {@code Class} object. These are public, protected, default
     * (package) access, and private constructors.  The elements in the array
     * returned are not sorted and are not in any particular order.  If the
     * class has a default constructor, it is included in the returned array.
     * This method returns an array of length 0 if this {@code Class}
     * object represents an interface, a primitive type, an array class, or
     * void.
     * 1.返回一个Constructor对象数组，这些对象反映了由这个Class对象表示的类声明的所有构造函数。
     * 2.它们是公共、受保护、默认（包）访问和私有构造函数。
     * 3.返回的数组中的元素没有排序，也没有任何特定的顺序。
     * 4.如果该类具有默认构造函数，则它包含在返回的数组中。
     * 5.如果此 Class对象表示接口、原始类型、数组类或 void，则此方法返回长度为 0 的数组
     *
     * <p> See <em>The Java Language Specification</em>, section 8.2.
     *
     * @return  the array of {@code Constructor} objects representing all the
     *          declared constructors of this class
     * @throws  SecurityException
     *          If a security manager, <i>s</i>, is present and any of the
     *          following conditions is met:
     *
     *          <ul>
     *
     *          <li> the caller's class loader is not the same as the
     *          class loader of this class and invocation of
     *          {@link SecurityManager#checkPermission
     *          s.checkPermission} method with
     *          {@code RuntimePermission("accessDeclaredMembers")}
     *          denies access to the declared constructors within this class
     *
     *          <li> the caller's class loader is not the same as or an
     *          ancestor of the class loader for the current class and
     *          invocation of {@link SecurityManager#checkPackageAccess
     *          s.checkPackageAccess()} denies access to the package
     *          of this class
     *
     *          </ul>
     *
     * @since JDK1.1
     */
    @CallerSensitive
    public Constructor<?>[] getDeclaredConstructors() throws SecurityException {
        checkMemberAccess(Member.DECLARED, Reflection.getCallerClass(), true);
        return copyConstructors(privateGetDeclaredConstructors(false));
    }


    /**
     * Returns a {@code Field} object that reflects the specified declared
     * field of the class or interface represented by this {@code Class}
     * object. The {@code name} parameter is a {@code String} that specifies
     * the simple name of the desired field.
     *  1.返回一个Field对象，该对象反映此Class对象表示的类或接口的指定声明字段。
     *  name参数是一个String，用于指定所需字段的简单名称
     * <p> If this {@code Class} object represents an array type, then this
     * method does not find the {@code length} field of the array type.
     * 2.如果这个Class对象代表的是一个数组类型，那么这个方法就找不到该数组类型的length字段
     * @param name the name of the field
     * @return  the {@code Field} object for the specified field in this
     *          class
     * @throws  NoSuchFieldException if a field with the specified name is
     *          not found.
     * @throws  NullPointerException if {@code name} is {@code null}
     * @throws  SecurityException
     *          If a security manager, <i>s</i>, is present and any of the
     *          following conditions is met:
     *
     *          <ul>
     *
     *          <li> the caller's class loader is not the same as the
     *          class loader of this class and invocation of
     *          {@link SecurityManager#checkPermission
     *          s.checkPermission} method with
     *          {@code RuntimePermission("accessDeclaredMembers")}
     *          denies access to the declared field
     *
     *          <li> the caller's class loader is not the same as or an
     *          ancestor of the class loader for the current class and
     *          invocation of {@link SecurityManager#checkPackageAccess
     *          s.checkPackageAccess()} denies access to the package
     *          of this class
     *
     *          </ul>
     *
     * @since JDK1.1
     * @jls 8.2 Class Members
     * @jls 8.3 Field Declarations
     */
    @CallerSensitive
    public Field getDeclaredField(String name)
        throws NoSuchFieldException, SecurityException {
        checkMemberAccess(Member.DECLARED, Reflection.getCallerClass(), true);
        Field field = searchFields(privateGetDeclaredFields(false), name);
        if (field == null) {
            throw new NoSuchFieldException(name);
        }
        return field;
    }


    /**
     * Returns a {@code Method} object that reflects the specified
     * declared method of the class or interface represented by this
     * {@code Class} object. The {@code name} parameter is a
     * {@code String} that specifies the simple name of the desired
     * method, and the {@code parameterTypes} parameter is an array of
     * {@code Class} objects that identify the method's formal parameter
     * types, in declared order.  If more than one method with the same
     * parameter types is declared in a class, and one of these methods has a
     * return type that is more specific than any of the others, that method is
     * returned; otherwise one of the methods is chosen arbitrarily.  If the
     * name is "&lt;init&gt;"or "&lt;clinit&gt;" a {@code NoSuchMethodException}
     * is raised.
     * 1.返回一个Method对象，该对象反映了此Class对象表示的类或接口的指定声明方法。
     * 2.name参数是一个String，用于指定所需方法的简单名称，而 parameterTypes参数是一个Class对象数组，
     * 用于标识方法的形参类型，按照声明的顺序。
     * 3.如果在一个类中声明了多个具有相同参数类型的方法，并且其中一个方法的返回类型比其他任何一个方法都更具体，
     * 则返回该方法；否则任意选择其中一种方法。
     * 4.如果名称是“<init>”或“<clinit>”，则会引发 NoSuchMethodException
     * <p> If this {@code Class} object represents an array type, then this
     * method does not find the {@code clone()} method.
     *
     * @param name the name of the method
     * @param parameterTypes the parameter array
     * @return  the {@code Method} object for the method of this class
     *          matching the specified name and parameters
     * @throws  NoSuchMethodException if a matching method is not found.
     * @throws  NullPointerException if {@code name} is {@code null}
     * @throws  SecurityException
     *          If a security manager, <i>s</i>, is present and any of the
     *          following conditions is met:
     *
     *          <ul>
     *
     *          <li> the caller's class loader is not the same as the
     *          class loader of this class and invocation of
     *          {@link SecurityManager#checkPermission
     *          s.checkPermission} method with
     *          {@code RuntimePermission("accessDeclaredMembers")}
     *          denies access to the declared method
     *
     *          <li> the caller's class loader is not the same as or an
     *          ancestor of the class loader for the current class and
     *          invocation of {@link SecurityManager#checkPackageAccess
     *          s.checkPackageAccess()} denies access to the package
     *          of this class
     *
     *          </ul>
     *
     * @jls 8.2 Class Members
     * @jls 8.4 Method Declarations
     * @since JDK1.1
     */
    @CallerSensitive
    public Method getDeclaredMethod(String name, Class<?>... parameterTypes)
        throws NoSuchMethodException, SecurityException {
        checkMemberAccess(Member.DECLARED, Reflection.getCallerClass(), true);
        Method method = searchMethods(privateGetDeclaredMethods(false), name, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException(getName() + "." + name + argumentTypesToString(parameterTypes));
        }
        return method;
    }


    /**
     * Returns a {@code Constructor} object that reflects the specified
     * constructor of the class or interface represented by this
     * {@code Class} object.  The {@code parameterTypes} parameter is
     * an array of {@code Class} objects that identify the constructor's
     * formal parameter types, in declared order.
     * 1.返回一个 Constructor对象，该对象反映了此 Class对象表示的类或接口的指定构造函数。
     * parameterTypes参数是一个 Class对象数组，这些对象按声明的顺序标识构造函数的形参类型
     * If this {@code Class} object represents an inner class
     * declared in a non-static context, the formal parameter types
     * include the explicit enclosing instance as the first parameter.
     * 2.如果此Class对象表示在非静态上下文中声明的内部类，则形式参数类型包括显式封闭实例作为第一个参数
     * @param parameterTypes the parameter array
     * @return  The {@code Constructor} object for the constructor with the
     *          specified parameter list
     * @throws  NoSuchMethodException if a matching method is not found.
     * @throws  SecurityException
     *          If a security manager, <i>s</i>, is present and any of the
     *          following conditions is met:
     *
     *          <ul>
     *
     *          <li> the caller's class loader is not the same as the
     *          class loader of this class and invocation of
     *          {@link SecurityManager#checkPermission
     *          s.checkPermission} method with
     *          {@code RuntimePermission("accessDeclaredMembers")}
     *          denies access to the declared constructor
     *
     *          <li> the caller's class loader is not the same as or an
     *          ancestor of the class loader for the current class and
     *          invocation of {@link SecurityManager#checkPackageAccess
     *          s.checkPackageAccess()} denies access to the package
     *          of this class
     *
     *          </ul>
     *
     * @since JDK1.1
     */
    @CallerSensitive
    public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes)
        throws NoSuchMethodException, SecurityException {
        checkMemberAccess(Member.DECLARED, Reflection.getCallerClass(), true);
        return getConstructor0(parameterTypes, Member.DECLARED);
    }

    /**
     * Finds a resource with a given name.  The rules for searching resources
     * associated with a given class are implemented by the defining
     * {@linkplain ClassLoader class loader} of the class.  This method
     * delegates to this object's class loader.  If this object was loaded by
     * the bootstrap class loader, the method delegates to {@link
     * ClassLoader#getSystemResourceAsStream}.
     * 1.查找具有给定名称的资源。搜索与给定类相关联的资源的规则由类的定义ClassLoader类加载器实现。
     * 此方法委托给此对象的类加载器。如果此对象由引导类加载器加载，
     * 则该方法委托给 ClassLoader.getSystemResourceAsStream
     * <p> Before delegation, an absolute resource name is constructed from the
     * given resource name using this algorithm:
     *
     * <ul>
     *
     * <li> If the {@code name} begins with a {@code '/'}
     * (<tt>'&#92;u002f'</tt>), then the absolute name of the resource is the
     * portion of the {@code name} following the {@code '/'}.
     *
     * <li> Otherwise, the absolute name is of the following form:
     *
     * <blockquote>
     *   {@code modified_package_name/name}
     * </blockquote>
     *
     * <p> Where the {@code modified_package_name} is the package name of this
     * object with {@code '/'} substituted for {@code '.'}
     * (<tt>'&#92;u002e'</tt>).
     *
     * </ul>
     *
     * @param  name name of the desired resource
     * @return      A {@link java.io.InputStream} object or {@code null} if
     *              no resource with this name is found
     * @throws  NullPointerException If {@code name} is {@code null}
     * @since  JDK1.1
     */
     public InputStream getResourceAsStream(String name) {
        name = resolveName(name);
        ClassLoader cl = getClassLoader0();
        if (cl==null) {
            // A system class.
            return ClassLoader.getSystemResourceAsStream(name);
        }
        return cl.getResourceAsStream(name);
    }

    /**
     * Finds a resource with a given name.  The rules for searching resources
     * associated with a given class are implemented by the defining
     * {@linkplain ClassLoader class loader} of the class.  This method
     * delegates to this object's class loader.  If this object was loaded by
     * the bootstrap class loader, the method delegates to {@link
     * ClassLoader#getSystemResource}.
     * 1.查找具有给定名称的资源。搜索与给定类相关联的资源的规则由类的定义 ClassLoader类加载器实现。
     * 此方法委托给此对象的类加载器。如果此对象由引导类加载器加载，则该方法委托给ClassLoadergetSystemResource
     * <p> Before delegation, an absolute resource name is constructed from the
     * given resource name using this algorithm:
     *
     * <ul>
     *
     * <li> If the {@code name} begins with a {@code '/'}
     * (<tt>'&#92;u002f'</tt>), then the absolute name of the resource is the
     * portion of the {@code name} following the {@code '/'}.
     *
     * <li> Otherwise, the absolute name is of the following form:
     *
     * <blockquote>
     *   {@code modified_package_name/name}
     * </blockquote>
     *
     * <p> Where the {@code modified_package_name} is the package name of this
     * object with {@code '/'} substituted for {@code '.'}
     * (<tt>'&#92;u002e'</tt>).
     *
     * </ul>
     *
     * @param  name name of the desired resource
     * @return      A  {@link java.net.URL} object or {@code null} if no
     *              resource with this name is found
     * @since  JDK1.1
     */
    public java.net.URL getResource(String name) {
        name = resolveName(name);
        ClassLoader cl = getClassLoader0();
        if (cl==null) {
            // A system class.
            return ClassLoader.getSystemResource(name);
        }
        return cl.getResource(name);
    }



    /** protection domain returned when the internal domain is null */
    //内域为空时返回保护域
    private static java.security.ProtectionDomain allPermDomain;


    /**
     * Returns the {@code ProtectionDomain} of this class.  If there is a
     * security manager installed, this method first calls the security
     * manager's {@code checkPermission} method with a
     * {@code RuntimePermission("getProtectionDomain")} permission to
     * ensure it's ok to get the
     * {@code ProtectionDomain}.
     * 1.返回此类的 ProtectionDomain。
     * 如果安装了安全管理器，此方法首先调用安全管理器的checkPermission方法，
     * 并带有 RuntimePermission("getProtectionDomain") 权限，以确保可以获取ProtectionDomain
     * @return the ProtectionDomain of this class
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        {@code checkPermission} method doesn't allow
     *        getting the ProtectionDomain.
     *
     * @see java.security.ProtectionDomain
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     * @since 1.2
     */
    public java.security.ProtectionDomain getProtectionDomain() {
        //判断是否存在安全管理器
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SecurityConstants.GET_PD_PERMISSION);
        }
        //获取保护域
        java.security.ProtectionDomain pd = getProtectionDomain0();
        if (pd == null) {
            if (allPermDomain == null) {
                java.security.Permissions perms =
                    new java.security.Permissions();
                perms.add(SecurityConstants.ALL_PERMISSION);
                allPermDomain =
                    new java.security.ProtectionDomain(null, perms);
            }
            pd = allPermDomain;
        }
        return pd;
    }


    /**
     * Returns the ProtectionDomain of this class.
     */
    private native java.security.ProtectionDomain getProtectionDomain0();

    /*
     * Return the Virtual Machine's Class object for the named
     * primitive type.
     */
    static native Class<?> getPrimitiveClass(String name);

    /*
     * Check if client is allowed to access members.  If access is denied,
     * throw a SecurityException.
     *1.检查是否允许客户端访问成员。如果访问被拒绝，则抛出 SecurityException
     * This method also enforces package access.
     *
     * <p> Default policy: allow all clients access with normal Java access
     * control.
     */
    private void checkMemberAccess(int which, Class<?> caller, boolean checkProxyInterfaces) {
        final SecurityManager s = System.getSecurityManager();
        if (s != null) {
            /* Default policy allows access to all {@link Member#PUBLIC} members,
             * as well as access to classes that have the same class loader as the caller.
             * In all other cases, it requires RuntimePermission("accessDeclaredMembers")
             * permission.
             * 默认策略允许访问所有 Member PUBLIC成员，以及访问与调用者具有相同类加载器的类。
             * 在所有其他情况下，它需要 RuntimePermission("accessDeclaredMembers") 权限
             */
            final ClassLoader ccl = ClassLoader.getClassLoader(caller);
            final ClassLoader cl = getClassLoader0();
            //判断一些非public的访问权限
            if (which != Member.PUBLIC) {
                if (ccl != cl) {
                    s.checkPermission(SecurityConstants.CHECK_MEMBER_ACCESS_PERMISSION);
                }
            }
            //检查访问包的权限
            this.checkPackageAccess(ccl, checkProxyInterfaces);
        }
    }

    /*
     * Checks if a client loaded in ClassLoader ccl is allowed to access this
     * class under the current package access policy. If access is denied,
     * throw a SecurityException.
     * 检查是否允许加载在 ClassLoader ccl 中的客户端在当前包访问策略下访问此类。
     * 如果访问被拒绝，则抛出 SecurityException
     */
    private void checkPackageAccess(final ClassLoader ccl, boolean checkProxyInterfaces) {
        final SecurityManager s = System.getSecurityManager();
        if (s != null) {
            final ClassLoader cl = getClassLoader0();

            //
            if (ReflectUtil.needsPackageAccessCheck(ccl, cl)) {
                String name = this.getName();
                int i = name.lastIndexOf('.');
                if (i != -1) {
                    // skip the package access check on a proxy class in default proxy package
                    //跳过对默认代理包中的代理类的包访问检查
                    String pkg = name.substring(0, i);
                    if (!Proxy.isProxyClass(this) || ReflectUtil.isNonPublicProxyClass(this)) {
                        s.checkPackageAccess(pkg);
                    }
                }
            }
            // check package access on the proxy interfaces
            //检查代理接口上的包访问
            if (checkProxyInterfaces && Proxy.isProxyClass(this)) {
                ReflectUtil.checkProxyPackageAccess(ccl, this.getInterfaces());
            }
        }
    }

    /**
     * Add a package name prefix if the name is not absolute Remove leading "/"
     * if name is absolute
     * 如果名称不是绝对的，则添加包名称前缀 如果名称是绝对的，则删除前导“/”
     */

    private String resolveName(String name) {
        if (name == null) {
            return name;
        }
        if (!name.startsWith("/")) {
            Class<?> c = this;
            while (c.isArray()) {
                c = c.getComponentType();
            }
            String baseName = c.getName();
            int index = baseName.lastIndexOf('.');
            if (index != -1) {
                name = baseName.substring(0, index).replace('.', '/')
                    +"/"+name;
            }
        } else {
            name = name.substring(1);
        }
        return name;
    }

    /**
     * Atomic operations support.
     * 支持原子操作
     */
    private static class Atomic {
        // initialize Unsafe machinery here, since we need to call Class.class instance method
        // and have to avoid calling it in the static initializer of the Class class...
        //在这里初始化 Unsafe 机器，
        // 因为我们需要调用 Class.class 实例方法并且必须避免在 Class 类的静态初始化器中调用它......
        private static final Unsafe unsafe = Unsafe.getUnsafe();
        // offset of Class.reflectionData instance field
        //Class.reflectionData 实例字段的偏移量
        private static final long reflectionDataOffset;
        // offset of Class.annotationType instance field
        //Class.annotationType 实例字段的偏移量
        private static final long annotationTypeOffset;
        // offset of Class.annotationData instance field
        //Class.annotationData 实例字段的偏移量
        private static final long annotationDataOffset;

        static {
            //获得Class的信息
            Field[] fields = Class.class.getDeclaredFields0(false); // bypass caches
            //查找出反射数据偏移量
            reflectionDataOffset = objectFieldOffset(fields, "reflectionData");
            //查找出注解类型偏移量
            annotationTypeOffset = objectFieldOffset(fields, "annotationType");
            //查找出注解数据偏移量
            annotationDataOffset = objectFieldOffset(fields, "annotationData");
        }

        private static long objectFieldOffset(Field[] fields, String fieldName) {
            //遍历查找是否存在
            Field field = searchFields(fields, fieldName);
            if (field == null) {
                throw new Error("No " + fieldName + " field found in java.lang.Class");
            }
            //存在之后直接通过unsafe来调用查找
            return unsafe.objectFieldOffset(field);
        }

        //新老反射数据是否可以交换
        static <T> boolean casReflectionData(Class<?> clazz,
                                             SoftReference<ReflectionData<T>> oldData,
                                             SoftReference<ReflectionData<T>> newData) {

            return unsafe.compareAndSwapObject(clazz, reflectionDataOffset, oldData, newData);
        }

        // //新老注解类型是否可以交换
        static <T> boolean casAnnotationType(Class<?> clazz,
                                             AnnotationType oldType,
                                             AnnotationType newType) {
            return unsafe.compareAndSwapObject(clazz, annotationTypeOffset, oldType, newType);
        }

        //新老注解数据是否可以交换
        static <T> boolean casAnnotationData(Class<?> clazz,
                                             AnnotationData oldData,
                                             AnnotationData newData) {
            return unsafe.compareAndSwapObject(clazz, annotationDataOffset, oldData, newData);
        }
    }

    /**
     * Reflection support.
     */

    // Caches for certain reflective results
    //是否使用缓存
    private static boolean useCaches = true;

    // reflection data that might get invalidated when JVM TI RedefineClasses() is called
    //调用 JVM TI RedefineClasses() 时可能会失效的反射数据
    private static class ReflectionData<T> {
        volatile Field[] declaredFields;
        volatile Field[] publicFields;
        volatile Method[] declaredMethods;
        volatile Method[] publicMethods;
        volatile Constructor<T>[] declaredConstructors;
        volatile Constructor<T>[] publicConstructors;
        // Intermediate results for getFields and getMethods
       // getFields 和 getMethods 的中间结果
        volatile Field[] declaredPublicFields;
        volatile Method[] declaredPublicMethods;
        volatile Class<?>[] interfaces;

        // Value of classRedefinedCount when we created this ReflectionData instance
        //当我们创建这个 ReflectionData 实例时 classRedefinedCount 的值
        final int redefinedCount;

        ReflectionData(int redefinedCount) {
            this.redefinedCount = redefinedCount;
        }
    }


    private volatile transient SoftReference<ReflectionData<T>> reflectionData;

    // Incremented by the VM on each call to JVM TI RedefineClasses()
    // that redefines this class or a superclass.
    //VM 在每次调用重新定义此类或超类的 JVM TI RedefineClasses() 时递增
    private volatile transient int classRedefinedCount = 0;

    // Lazily create and cache ReflectionData
    //懒惰地创建和缓存 ReflectionData
    private ReflectionData<T> reflectionData() {
        SoftReference<ReflectionData<T>> reflectionData = this.reflectionData;
        int classRedefinedCount = this.classRedefinedCount;
        ReflectionData<T> rd;
        if (useCaches &&
            reflectionData != null &&
            (rd = reflectionData.get()) != null &&
            rd.redefinedCount == classRedefinedCount) {
            return rd;
        }
        // else no SoftReference or cleared SoftReference or stale ReflectionData
        // -> create and replace new instance
        //否则没有 SoftReference 或清除 SoftReference 或陈旧的 ReflectionData -> 创建和替换新实例
        return newReflectionData(reflectionData, classRedefinedCount);
    }

    //将旧的反射数据替换成新的反射数据
    private ReflectionData<T> newReflectionData(SoftReference<ReflectionData<T>> oldReflectionData,
                                                int classRedefinedCount) {
        if (!useCaches) return null;

        while (true) {
            ReflectionData<T> rd = new ReflectionData<>(classRedefinedCount);
            // try to CAS it...
            if (Atomic.casReflectionData(this, oldReflectionData, new SoftReference<>(rd))) {
                return rd;
            }
            // else retry
            oldReflectionData = this.reflectionData;
            classRedefinedCount = this.classRedefinedCount;
            if (oldReflectionData != null &&
                (rd = oldReflectionData.get()) != null &&
                rd.redefinedCount == classRedefinedCount) {
                return rd;
            }
        }
    }

    // Generic signature handling
    private native String getGenericSignature0();

    // Generic info repository; lazily initialized
    //通用信息存储库；延迟初始化
    private volatile transient ClassRepository genericInfo;

    // accessor for factory
    private GenericsFactory getFactory() {
        // create scope and factory
        //创建范围和工厂
        return CoreReflectionFactory.make(this, ClassScope.make(this));
    }

    // accessor for generic info repository;
    // generic info is lazily initialized
    //通用信息存储库的访问器；通用信息被延迟初始化
    private ClassRepository getGenericInfo() {
        ClassRepository genericInfo = this.genericInfo;
        if (genericInfo == null) {
            String signature = getGenericSignature0();
            if (signature == null) {
                genericInfo = ClassRepository.NONE;
            } else {
                genericInfo = ClassRepository.make(signature, getFactory());
            }
            this.genericInfo = genericInfo;
        }
        return (genericInfo != ClassRepository.NONE) ? genericInfo : null;
    }

    // Annotations handling
    native byte[] getRawAnnotations();
    // Since 1.8
    native byte[] getRawTypeAnnotations();
    //获取可执行类型注释字节
    static byte[] getExecutableTypeAnnotationBytes(Executable ex) {
        return getReflectionFactory().getExecutableTypeAnnotationBytes(ex);
    }

    native ConstantPool getConstantPool();

    //
    //
    // java.lang.reflect.Field handling
    //
    //

    // Returns an array of "root" fields. These Field objects must NOT
    // be propagated to the outside world, but must instead be copied
    // via ReflectionFactory.copyField.
    //返回一组“根”字段。这些 Field 对象不能传播到外部世界，而必须通过 ReflectionFactory.copyField 复制
    private Field[] privateGetDeclaredFields(boolean publicOnly) {
        checkInitted();
        Field[] res;
        ReflectionData<T> rd = reflectionData();
        if (rd != null) {
            res = publicOnly ? rd.declaredPublicFields : rd.declaredFields;
            if (res != null) return res;
        }
        // No cached value available; request value from VM
        //没有可用的缓存值；从 VM 请求值
        res = Reflection.filterFields(this, getDeclaredFields0(publicOnly));
        if (rd != null) {
            if (publicOnly) {
                rd.declaredPublicFields = res;
            } else {
                rd.declaredFields = res;
            }
        }
        return res;
    }

    // Returns an array of "root" fields. These Field objects must NOT
    // be propagated to the outside world, but must instead be copied
    // via ReflectionFactory.copyField.
    //返回一组“根”字段。这些 Field 对象不得传播到外部世界，而必须通过 ReflectionFactory.copyField 进行复制
    private Field[] privateGetPublicFields(Set<Class<?>> traversedInterfaces) {
        checkInitted();
        Field[] res;
        ReflectionData<T> rd = reflectionData();
        if (rd != null) {
            res = rd.publicFields;
            if (res != null) return res;
        }

        // No cached value available; compute value recursively.
        // Traverse in correct order for getField().
        //没有可用的缓存值；递归计算值。以正确的顺序遍历 getField()
        List<Field> fields = new ArrayList<>();
        if (traversedInterfaces == null) {
            traversedInterfaces = new HashSet<>();
        }

        // Local fields
        Field[] tmp = privateGetDeclaredFields(true);
        addAll(fields, tmp);

        // Direct superinterfaces, recursively
        for (Class<?> c : getInterfaces()) {
            if (!traversedInterfaces.contains(c)) {
                traversedInterfaces.add(c);
                addAll(fields, c.privateGetPublicFields(traversedInterfaces));
            }
        }

        // Direct superclass, recursively
        if (!isInterface()) {
            Class<?> c = getSuperclass();
            if (c != null) {
                addAll(fields, c.privateGetPublicFields(traversedInterfaces));
            }
        }

        res = new Field[fields.size()];
        fields.toArray(res);
        if (rd != null) {
            rd.publicFields = res;
        }
        return res;
    }

    private static void addAll(Collection<Field> c, Field[] o) {
        for (int i = 0; i < o.length; i++) {
            c.add(o[i]);
        }
    }


    //
    //
    // java.lang.reflect.Constructor handling
    //
    //

    // Returns an array of "root" constructors. These Constructor
    // objects must NOT be propagated to the outside world, but must
    // instead be copied via ReflectionFactory.copyConstructor.
    //返回一个“根”构造函数数组。这些构造函数对象不能传播到外部世界，
    // 而必须通过 ReflectionFactory.copyConstructor 复制
    private Constructor<T>[] privateGetDeclaredConstructors(boolean publicOnly) {
        checkInitted();
        Constructor<T>[] res;
        ReflectionData<T> rd = reflectionData();
        if (rd != null) {
            res = publicOnly ? rd.publicConstructors : rd.declaredConstructors;
            if (res != null) return res;
        }
        // No cached value available; request value from VM
        if (isInterface()) {
            @SuppressWarnings("unchecked")
            Constructor<T>[] temporaryRes = (Constructor<T>[]) new Constructor<?>[0];
            res = temporaryRes;
        } else {
            res = getDeclaredConstructors0(publicOnly);
        }
        if (rd != null) {
            if (publicOnly) {
                rd.publicConstructors = res;
            } else {
                rd.declaredConstructors = res;
            }
        }
        return res;
    }

    //
    //
    // java.lang.reflect.Method handling
    //
    //

    // Returns an array of "root" methods. These Method objects must NOT
    // be propagated to the outside world, but must instead be copied
    // via ReflectionFactory.copyMethod.
    //返回一组“根”方法。这些 Method 对象不能传播到外部世界，
    // 而必须通过 ReflectionFactory.copyMethod 复制
    private Method[] privateGetDeclaredMethods(boolean publicOnly) {
        checkInitted();
        Method[] res;
        ReflectionData<T> rd = reflectionData();
        if (rd != null) {
            res = publicOnly ? rd.declaredPublicMethods : rd.declaredMethods;
            if (res != null) return res;
        }
        // No cached value available; request value from VM
        res = Reflection.filterMethods(this, getDeclaredMethods0(publicOnly));
        if (rd != null) {
            if (publicOnly) {
                rd.declaredPublicMethods = res;
            } else {
                rd.declaredMethods = res;
            }
        }
        return res;
    }

    static class MethodArray {
        // Don't add or remove methods except by add() or remove() calls.
        //除了 add() 或 remove() 调用之外，不要添加或删除方法
        private Method[] methods;
        private int length;
        private int defaults;
        //默认方法数组容量为20
        MethodArray() {
            this(20);
        }

        MethodArray(int initialSize) {
            if (initialSize < 2)
                throw new IllegalArgumentException("Size should be 2 or more");

            methods = new Method[initialSize];
            length = 0;
            defaults = 0;
        }


        boolean hasDefaults() {
            return defaults != 0;
        }

        void add(Method m) {

            //如果长度等于数组长度,扩容两倍
            if (length == methods.length) {
                methods = Arrays.copyOf(methods, 2 * methods.length);
            }
            //没有直接往后加,长度length+1
            methods[length++] = m;
            //方法不为空,并且方法是默认的
            //defaults++
            if (m != null && m.isDefault())
                defaults++;
        }

        //把方法中的数组全部加进去
        void addAll(Method[] ma) {
            for (int i = 0; i < ma.length; i++) {
                add(ma[i]);
            }
        }

        //将MethodArray中全部加进去
        void addAll(MethodArray ma) {
            for (int i = 0; i < ma.length(); i++) {
                add(ma.get(i));
            }
        }

        //如果当前方法不存在就加入
        void addIfNotPresent(Method newMethod) {
            for (int i = 0; i < length; i++) {
                Method m = methods[i];
                if (m == newMethod || (m != null && m.equals(newMethod))) {
                    return;
                }
            }
            add(newMethod);
        }

        //如果当前方法中存在没有的就加入
        void addAllIfNotPresent(MethodArray newMethods) {
            for (int i = 0; i < newMethods.length(); i++) {
                Method m = newMethods.get(i);
                if (m != null) {
                    addIfNotPresent(m);
                }
            }
        }

        /* Add Methods declared in an interface to this MethodArray.
         * Static methods declared in interfaces are not inherited.
         * //将接口中声明的方法添加到此 MethodArray。接口中声明的静态方法不会被继承
         */
        void addInterfaceMethods(Method[] methods) {
            for (Method candidate : methods) {
                //不是静态方法就加入
                if (!Modifier.isStatic(candidate.getModifiers())) {
                    add(candidate);
                }
            }
        }

        int length() {
            return length;
        }

        Method get(int i) {
            return methods[i];
        }

        //返回第一个不为空的方法.如果没有则返回null
        Method getFirst() {
            for (Method m : methods)
                if (m != null)
                    return m;
            return null;
        }

        //通过名字和描述符来删除
        void removeByNameAndDescriptor(Method toRemove) {
            for (int i = 0; i < length; i++) {
                Method m = methods[i];
                if (m != null && matchesNameAndDescriptor(m, toRemove)) {
                    remove(i);
                }
            }
        }

        //删除不为null并且是默认的方法
        private void remove(int i) {
            if (methods[i] != null && methods[i].isDefault())
                defaults--;
            methods[i] = null;
        }

        //通过名字和描述符来匹配
        private boolean matchesNameAndDescriptor(Method m1, Method m2) {
            //返回类型,参数类型,方法签名
            //名字必须要interned
            return m1.getReturnType() == m2.getReturnType() &&
                   m1.getName() == m2.getName() && // name is guaranteed to be interned
                   arrayContentsEq(m1.getParameterTypes(),
                           m2.getParameterTypes());
        }

        //将数组中的null去掉
        void compactAndTrim() {
            int newPos = 0;
            // Get rid of null slots
            for (int pos = 0; pos < length; pos++) {
                Method m = methods[pos];
                if (m != null) {
                    if (pos != newPos) {
                        methods[newPos] = m;
                    }
                    newPos++;
                }
            }
            if (newPos != methods.length) {
                methods = Arrays.copyOf(methods, newPos);
            }
        }

        /* Removes all Methods from this MethodArray that have a more specific
         * default Method in this MethodArray.
         * 从此 MethodArray 中删除所有在此 MethodArray 中具有更具体默认方法的方法
         *
         * Users of MethodArray are responsible for pruning Methods that have
         * a more specific <em>concrete</em> Method.
         * MethodArray 的用户负责修剪具有更具体方法的方法
         */
        void removeLessSpecifics() {
            if (!hasDefaults())
                return;

            for (int i = 0; i < length; i++) {
                Method m = get(i);
                if  (m == null || !m.isDefault())
                    continue;

                for (int j  = 0; j < length; j++) {
                    if (i == j)
                        continue;

                    Method candidate = get(j);
                    if (candidate == null)
                        continue;

                    if (!matchesNameAndDescriptor(m, candidate))
                        continue;

                    if (hasMoreSpecificClass(m, candidate))
                        remove(j);
                }
            }
        }

        Method[] getArray() {
            return methods;
        }

        // Returns true if m1 is more specific than m2
        //如果 m1 比 m2 更具体，则返回 true
        static boolean hasMoreSpecificClass(Method m1, Method m2) {
            Class<?> m1Class = m1.getDeclaringClass();
            Class<?> m2Class = m2.getDeclaringClass();
            return m1Class != m2Class && m2Class.isAssignableFrom(m1Class);
        }
    }


    // Returns an array of "root" methods. These Method objects must NOT
    // be propagated to the outside world, but must instead be copied
    // via ReflectionFactory.copyMethod.
    //返回一组“根”方法。这些 Method 对象不能传播到外部世界，而必须通过 ReflectionFactory.copyMethod 复制
    private Method[] privateGetPublicMethods() {
        checkInitted();
        Method[] res;
        ReflectionData<T> rd = reflectionData();
        if (rd != null) {
            res = rd.publicMethods;
            if (res != null) return res;
        }

        // No cached value available; compute value recursively.
        // Start by fetching public declared methods
        MethodArray methods = new MethodArray();
        {
            Method[] tmp = privateGetDeclaredMethods(true);
            methods.addAll(tmp);
        }
        // Now recur over superclass and direct superinterfaces.
        // Go over superinterfaces first so we can more easily filter
        // out concrete implementations inherited from superclasses at
        // the end.
        MethodArray inheritedMethods = new MethodArray();
        for (Class<?> i : getInterfaces()) {
            inheritedMethods.addInterfaceMethods(i.privateGetPublicMethods());
        }
        if (!isInterface()) {
            Class<?> c = getSuperclass();
            if (c != null) {
                MethodArray supers = new MethodArray();
                supers.addAll(c.privateGetPublicMethods());
                // Filter out concrete implementations of any
                // interface methods
                for (int i = 0; i < supers.length(); i++) {
                    Method m = supers.get(i);
                    if (m != null &&
                            !Modifier.isAbstract(m.getModifiers()) &&
                            !m.isDefault()) {
                        inheritedMethods.removeByNameAndDescriptor(m);
                    }
                }
                // Insert superclass's inherited methods before
                // superinterfaces' to satisfy getMethod's search
                // order
                supers.addAll(inheritedMethods);
                inheritedMethods = supers;
            }
        }
        // Filter out all local methods from inherited ones
        for (int i = 0; i < methods.length(); i++) {
            Method m = methods.get(i);
            inheritedMethods.removeByNameAndDescriptor(m);
        }
        methods.addAllIfNotPresent(inheritedMethods);
        methods.removeLessSpecifics();
        methods.compactAndTrim();
        res = methods.getArray();
        if (rd != null) {
            rd.publicMethods = res;
        }
        return res;
    }


    //
    // Helpers for fetchers of one field, method, or constructor
    //一个字段、方法或构造函数的获取器的助手
    //

    private static Field searchFields(Field[] fields, String name) {

        String internedName = name.intern();

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getName() == internedName) {
                return getReflectionFactory().copyField(fields[i]);
            }
        }
        return null;
    }

    private Field getField0(String name) throws NoSuchFieldException {
        // Note: the intent is that the search algorithm this routine
        // uses be equivalent to the ordering imposed by
        // privateGetPublicFields(). It fetches only the declared
        // public fields for each class, however, to reduce the number
        // of Field objects which have to be created for the common
        // case where the field being requested is declared in the
        // class which is being queried.
        //注意：目的是该例程使用的搜索算法等效于 privateGetPublicFields() 强加的排序。
        // 然而，它只获取每个类的声明的公共字段，以减少必须为常见情况创建的 Field 对象的数量，
        // 其中所请求的字段在被查询的类中声明
        Field res;
        // Search declared public fields
        //搜索声明的公共字段
        if ((res = searchFields(privateGetDeclaredFields(true), name)) != null) {
            return res;
        }
        // Direct superinterfaces, recursively
        Class<?>[] interfaces = getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class<?> c = interfaces[i];
            if ((res = c.getField0(name)) != null) {
                return res;
            }
        }
        // Direct superclass, recursively
        if (!isInterface()) {
            Class<?> c = getSuperclass();
            if (c != null) {
                if ((res = c.getField0(name)) != null) {
                    return res;
                }
            }
        }
        return null;
    }

    //查找方法名
    private static Method searchMethods(Method[] methods,
                                        String name,
                                        Class<?>[] parameterTypes)
    {
        Method res = null;
        String internedName = name.intern();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (m.getName() == internedName
                && arrayContentsEq(parameterTypes, m.getParameterTypes())
                && (res == null
                    || res.getReturnType().isAssignableFrom(m.getReturnType())))
                res = m;
        }

        return (res == null ? res : getReflectionFactory().copyMethod(res));
    }

    private Method getMethod0(String name, Class<?>[] parameterTypes, boolean includeStaticMethods) {
        MethodArray interfaceCandidates = new MethodArray(2);
        Method res =  privateGetMethodRecursive(name, parameterTypes, includeStaticMethods, interfaceCandidates);
        if (res != null)
            return res;

        // Not found on class or superclass directly
        interfaceCandidates.removeLessSpecifics();
        return interfaceCandidates.getFirst(); // may be null
    }

    private Method privateGetMethodRecursive(String name,
            Class<?>[] parameterTypes,
            boolean includeStaticMethods,
            MethodArray allInterfaceCandidates) {
        // Note: the intent is that the search algorithm this routine
        // uses be equivalent to the ordering imposed by
        // privateGetPublicMethods(). It fetches only the declared
        // public methods for each class, however, to reduce the
        // number of Method objects which have to be created for the
        // common case where the method being requested is declared in
        // the class which is being queried.
        //1.注意：目的是该例程使用的搜索算法等效于 privateGetPublicMethods() 强加的排序。
        // 然而，它只获取每个类的声明的公共方法，以减少必须为常见情况创建的 Method 对象的数量，
        // 其中所请求的方法是在被查询的类中声明的

        // Due to default methods, unless a method is found on a superclass,
        // methods declared in any superinterface needs to be considered.
        // Collect all candidates declared in superinterfaces in {@code
        // allInterfaceCandidates} and select the most specific if no match on
        // a superclass is found.
        //2.由于默认方法，除非在超类上找到方法，否则需要考虑在任何超接口中声明的方法。
        // 收集在 allInterfaceCandidates中的超接口中声明的所有候选，如果未找到超类的匹配项则选择最具体的候选

        // Must _not_ return root methods
        Method res;
        // Search declared public methods
        if ((res = searchMethods(privateGetDeclaredMethods(true),
                                 name,
                                 parameterTypes)) != null) {
            if (includeStaticMethods || !Modifier.isStatic(res.getModifiers()))
                return res;
        }
        // Search superclass's methods
        if (!isInterface()) {
            Class<? super T> c = getSuperclass();
            if (c != null) {
                if ((res = c.getMethod0(name, parameterTypes, true)) != null) {
                    return res;
                }
            }
        }
        // Search superinterfaces' methods
        Class<?>[] interfaces = getInterfaces();
        for (Class<?> c : interfaces)
            if ((res = c.getMethod0(name, parameterTypes, false)) != null)
                allInterfaceCandidates.add(res);
        // Not found
        return null;
    }

    private Constructor<T> getConstructor0(Class<?>[] parameterTypes,
                                        int which) throws NoSuchMethodException
    {
        Constructor<T>[] constructors = privateGetDeclaredConstructors((which == Member.PUBLIC));
        for (Constructor<T> constructor : constructors) {
            if (arrayContentsEq(parameterTypes,
                                constructor.getParameterTypes())) {
                return getReflectionFactory().copyConstructor(constructor);
            }
        }
        throw new NoSuchMethodException(getName() + ".<init>" + argumentTypesToString(parameterTypes));
    }

    //
    // Other helpers and base implementation
    //其他助手和基础实现
    //

    private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if (a1 == null) {
            return a2 == null || a2.length == 0;
        }

        if (a2 == null) {
            return a1.length == 0;
        }

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }

        return true;
    }

    private static Field[] copyFields(Field[] arg) {
        Field[] out = new Field[arg.length];
        ReflectionFactory fact = getReflectionFactory();
        for (int i = 0; i < arg.length; i++) {
            out[i] = fact.copyField(arg[i]);
        }
        return out;
    }

    private static Method[] copyMethods(Method[] arg) {
        Method[] out = new Method[arg.length];
        ReflectionFactory fact = getReflectionFactory();
        for (int i = 0; i < arg.length; i++) {
            out[i] = fact.copyMethod(arg[i]);
        }
        return out;
    }

    private static <U> Constructor<U>[] copyConstructors(Constructor<U>[] arg) {
        Constructor<U>[] out = arg.clone();
        ReflectionFactory fact = getReflectionFactory();
        for (int i = 0; i < out.length; i++) {
            out[i] = fact.copyConstructor(out[i]);
        }
        return out;
    }

    private native Field[]       getDeclaredFields0(boolean publicOnly);
    private native Method[]      getDeclaredMethods0(boolean publicOnly);
    private native Constructor<T>[] getDeclaredConstructors0(boolean publicOnly);
    private native Class<?>[]   getDeclaredClasses0();

    private static String        argumentTypesToString(Class<?>[] argTypes) {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (argTypes != null) {
            for (int i = 0; i < argTypes.length; i++) {
                if (i > 0) {
                    buf.append(", ");
                }
                Class<?> c = argTypes[i];
                buf.append((c == null) ? "null" : c.getName());
            }
        }
        buf.append(")");
        return buf.toString();
    }

    /** use serialVersionUID from JDK 1.1 for interoperability */
    private static final long serialVersionUID = 3206093459760846163L;


    /**
     * Class Class is special cased within the Serialization Stream Protocol.
     *Class类是序列化流协议中的特例
     * A Class instance is written initially into an ObjectOutputStream in the
     * following format:
     * <pre>
     *      {@code TC_CLASS} ClassDescriptor
     *      A ClassDescriptor is a special cased serialization of
     *      a {@code java.io.ObjectStreamClass} instance.
     * </pre>
     * A new handle is generated for the initial time the class descriptor
     * is written into the stream. Future references to the class descriptor
     * are written as references to the initial class descriptor instance.
     *在类描述符写入流的初始时间生成一个新句柄。未来对类描述符的引用被写为对初始类描述符实例的引用
     * @see java.io.ObjectStreamClass
     */
    private static final ObjectStreamField[] serialPersistentFields =
        new ObjectStreamField[0];


    /**
     * Returns the assertion status that would be assigned to this
     * class if it were to be initialized at the time this method is invoked.
     * If this class has had its assertion status set, the most recent
     * setting will be returned; otherwise, if any package default assertion
     * status pertains to this class, the most recent setting for the most
     * specific pertinent package default assertion status is returned;
     * otherwise, if this class is not a system class (i.e., it has a
     * class loader) its class loader's default assertion status is returned;
     * otherwise, the system class default assertion status is returned.
     * 1.如果在调用此方法时对其进行初始化，则返回将分配给此类的断言状态。
     * 如果该类已设置其断言状态，则将返回最近的设置；
     * 否则，如果任何包默认断言状态属于此类，则返回最具体的相关包默认断言状态的最新设置；
     * 否则，如果该类不是系统类（即，它有一个类加载器），则返回其类加载器的默认断言状态；
     * 否则，返回系统类默认断言状态
     * <p>
     * Few programmers will have any need for this method; it is provided
     * for the benefit of the JRE itself.  (It allows a class to determine at
     * the time that it is initialized whether assertions should be enabled.)
     * Note that this method is not guaranteed to return the actual
     * assertion status that was (or will be) associated with the specified
     * class when it was (or will be) initialized.
     *
     * @return the desired assertion status of the specified class.
     * @see    java.lang.ClassLoader#setClassAssertionStatus
     * @see    java.lang.ClassLoader#setPackageAssertionStatus
     * @see    java.lang.ClassLoader#setDefaultAssertionStatus
     * @since  1.4
     */
    public boolean desiredAssertionStatus() {
        ClassLoader loader = getClassLoader();
        // If the loader is null this is a system class, so ask the VM
        //如果 loader 为 null，则这是一个系统类，因此请询问 VM
        if (loader == null)
            return desiredAssertionStatus0(this);

        // If the classloader has been initialized with the assertion
        // directives, ask it. Otherwise, ask the VM.
        //If the classloader has been initialized with the assertion directives, ask it. Otherwise, ask the VM.
        synchronized(loader.assertionLock) {
            if (loader.classAssertionStatus != null) {
                return loader.desiredAssertionStatus(getName());
            }
        }
        return desiredAssertionStatus0(this);
    }

    // Retrieves the desired assertion status of this class from the VM
    private static native boolean desiredAssertionStatus0(Class<?> clazz);

    /**
     * Returns true if and only if this class was declared as an enum in the
     * source code.
     * //当且仅当此类在源代码中被声明为枚举时才返回 true
     *
     * @return true if and only if this class was declared as an enum in the
     *     source code
     * @since 1.5
     */
    public boolean isEnum() {
        // An enum must both directly extend java.lang.Enum and have
        // the ENUM bit set; classes for specialized enum constants
        // don't do the former.
        //枚举必须直接扩展 java.lang.Enum 并设置 ENUM 位；专门的枚举常量的类不做前者
        return (this.getModifiers() & ENUM) != 0 &&
        this.getSuperclass() == java.lang.Enum.class;
    }

    // Fetches the factory for reflective objects
    //获取反射对象的工厂
    private static ReflectionFactory getReflectionFactory() {
        if (reflectionFactory == null) {
            reflectionFactory =
                java.security.AccessController.doPrivileged
                    (new sun.reflect.ReflectionFactory.GetReflectionFactoryAction());
        }
        return reflectionFactory;
    }
    //反射工厂类
    private static ReflectionFactory reflectionFactory;

    // To be able to query system properties as soon as they're available
    //能够在系统属性可用时立即查询
    private static boolean initted = false;
    private static void checkInitted() {
        if (initted) return;
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    // Tests to ensure the system properties table is fully
                    // initialized. This is needed because reflection code is
                    // called very early in the initialization process (before
                    // command-line arguments have been parsed and therefore
                    // these user-settable properties installed.) We assume that
                    // if System.out is non-null then the System class has been
                    // fully initialized and that the bulk of the startup code
                    // has been run.
                    //测试以确保系统属性表已完全初始化。
                    // 这是必需的，因为反射代码在初始化过程中很早就被调用
                    // （在命令行参数被解析之前，因此这些用户可设置的属性被安装之前。）
                    // 我们假设如果 System.out 是非空的，那么 System 类已经被完全初始化并且大部分启动代码已经运行

                    if (System.out == null) {
                        // java.lang.System not yet fully initialized
                        //java.lang.System 尚未完全初始化
                        return null;
                    }

                    // Doesn't use Boolean.getBoolean to avoid class init.
                    //不使用 Boolean.getBoolean 来避免类初始化
                    String val =
                        System.getProperty("sun.reflect.noCaches");
                    if (val != null && val.equals("true")) {
                        useCaches = false;
                    }

                    initted = true;
                    return null;
                }
            });
    }

    /**
     * Returns the elements of this enum class or null if this
     * Class object does not represent an enum type.
     * //如果此 Class 对象不表示枚举类型，则返回此枚举类的元素或 null
     *
     * @return an array containing the values comprising the enum class
     *     represented by this Class object in the order they're
     *     declared, or null if this Class object does not
     *     represent an enum type
     * @since 1.5
     */
    public T[] getEnumConstants() {
        T[] values = getEnumConstantsShared();
        return (values != null) ? values.clone() : null;
    }

    /**
     * Returns the elements of this enum class or null if this
     * Class object does not represent an enum type;
     * identical to getEnumConstants except that the result is
     * uncloned, cached, and shared by all callers.
     * 如果此 Class 对象不表示枚举类型，则返回此枚举类的元素或 null；
     * 与 getEnumConstants 相同，不同之处在于结果被所有调用者取消克隆、缓存和共享
     */
    T[] getEnumConstantsShared() {
        if (enumConstants == null) {
            if (!isEnum()) return null;
            try {
                final Method values = getMethod("values");
                java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<Void>() {
                        public Void run() {
                                values.setAccessible(true);
                                return null;
                            }
                        });
                @SuppressWarnings("unchecked")
                T[] temporaryConstants = (T[])values.invoke(null);
                enumConstants = temporaryConstants;
            }
            // These can happen when users concoct enum-like classes
            // that don't comply with the enum spec.
            //当用户编造不符合枚举规范的类似枚举的类时，就会发生这些情况
            catch (InvocationTargetException | NoSuchMethodException |
                   IllegalAccessException ex) { return null; }
        }
        return enumConstants;
    }
    //枚举常量
    private volatile transient T[] enumConstants = null;

    /**
     * Returns a map from simple name to enum constant.  This package-private
     * method is used internally by Enum to implement
     * {@code public static <T extends Enum<T>> T valueOf(Class<T>, String)}
     * efficiently.  Note that the map is returned by this method is
     * created lazily on first use.  Typically it won't ever get created.
     * 返回从简单名称到枚举常量的映射。
     * Enum 在内部使用此包私有方法来有效地实现 (public static <T extends Enum<T>> T valueOf(Class<T>, String))。
     * 请注意，此方法返回的目录是在第一次使用时延迟创建的。通常它永远不会被创建
     */
    Map<String, T> enumConstantDirectory() {
        if (enumConstantDirectory == null) {
            T[] universe = getEnumConstantsShared();
            if (universe == null)
                throw new IllegalArgumentException(
                    getName() + " is not an enum type");
            Map<String, T> m = new HashMap<>(2 * universe.length);
            for (T constant : universe)
                m.put(((Enum<?>)constant).name(), constant);
            enumConstantDirectory = m;
        }
        return enumConstantDirectory;
    }
    //枚举常量目录
    private volatile transient Map<String, T> enumConstantDirectory = null;

    /**
     * Casts an object to the class or interface represented
     * by this {@code Class} object.
     * //将对象强制转换为由该 {@code Class} 对象表示的类或接口
     *
     * @param obj the object to be cast
     * @return the object after casting, or null if obj is null
     *
     * @throws ClassCastException if the object is not
     * null and is not assignable to the type T.
     *
     * @since 1.5
     */
    @SuppressWarnings("unchecked")
    public T cast(Object obj) {
        if (obj != null && !isInstance(obj))
            throw new ClassCastException(cannotCastMsg(obj));
        return (T) obj;
    }

    private String cannotCastMsg(Object obj) {
        return "Cannot cast " + obj.getClass().getName() + " to " + getName();
    }

    /**
     * Casts this {@code Class} object to represent a subclass of the class
     * represented by the specified class object.  Checks that the cast
     * is valid, and throws a {@code ClassCastException} if it is not.  If
     * this method succeeds, it always returns a reference to this class object.
     *  1.强制转换此Class对象以表示由指定类对象表示的类的子类。
     *  检查强制转换是否有效，如果无效则抛出ClassCastException。
     *  如果此方法成功，则始终返回对此类对象的引用（向上转型）
     * <p>This method is useful when a client needs to "narrow" the type of
     * a {@code Class} object to pass it to an API that restricts the
     * {@code Class} objects that it is willing to accept.  A cast would
     * generate a compile-time warning, as the correctness of the cast
     * could not be checked at runtime (because generic types are implemented
     * by erasure).
     *
     * @param <U> the type to cast this class object to
     * @param clazz the class of the type to cast this class object to
     * @return this {@code Class} object, cast to represent a subclass of
     *    the specified class object.
     * @throws ClassCastException if this {@code Class} object does not
     *    represent a subclass of the specified class (here "subclass" includes
     *    the class itself).
     * @since 1.5
     */
    @SuppressWarnings("unchecked")
    public <U> Class<? extends U> asSubclass(Class<U> clazz) {
        //判断是否向上转型成功
        if (clazz.isAssignableFrom(this))
            return (Class<? extends U>) this;
        else
            throw new ClassCastException(this.toString());
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     * 如果这样的注解是存在的，则返回此元素的指定类型的注解，否则为 null
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        Objects.requireNonNull(annotationClass);

        return (A) annotationData().annotations.get(annotationClass);
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     * 判断当前类型的注解是否存在
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return GenericDeclaration.super.isAnnotationPresent(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     * 返回与此元素关联的注解
     */
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationClass) {
        Objects.requireNonNull(annotationClass);

        AnnotationData annotationData = annotationData();
        return AnnotationSupport.getAssociatedAnnotations(annotationData.declaredAnnotations,
                                                          this,
                                                          annotationClass);
    }

    /**
     * @since 1.5
     * 获取所有注解
     */
    public Annotation[] getAnnotations() {
        return AnnotationParser.toArray(annotationData().annotations);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     * //获取声明的注解
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass) {
        Objects.requireNonNull(annotationClass);

        return (A) annotationData().declaredAnnotations.get(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     * //通过类型获得特定的声明注解
     */
    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationClass) {
        Objects.requireNonNull(annotationClass);

        return AnnotationSupport.getDirectlyAndIndirectlyPresent(annotationData().declaredAnnotations,
                                                                 annotationClass);
    }

    /**
     * @since 1.5
     * 获取声明的注解
     */
    public Annotation[] getDeclaredAnnotations()  {
        return AnnotationParser.toArray(annotationData().declaredAnnotations);
    }

    // annotation data that might get invalidated when JVM TI RedefineClasses() is called
    //调用 JVM TI RedefineClasses() 时可能会失效的注解数据
    private static class AnnotationData {
        //注解
        final Map<Class<? extends Annotation>, Annotation> annotations;
        //声明的注解
        final Map<Class<? extends Annotation>, Annotation> declaredAnnotations;

        // Value of classRedefinedCount when we created this AnnotationData instance
        //当我们创建这个 AnnotationData 实例时 classRedefinedCount 的值
        final int redefinedCount;

        AnnotationData(Map<Class<? extends Annotation>, Annotation> annotations,
                       Map<Class<? extends Annotation>, Annotation> declaredAnnotations,
                       int redefinedCount) {
            this.annotations = annotations;
            this.declaredAnnotations = declaredAnnotations;
            this.redefinedCount = redefinedCount;
        }
    }

    // Annotations cache
    @SuppressWarnings("UnusedDeclaration")
    //注解缓存
    private volatile transient AnnotationData annotationData;

    private AnnotationData annotationData() {
        while (true) { // retry loop
            AnnotationData annotationData = this.annotationData;
            int classRedefinedCount = this.classRedefinedCount;
            if (annotationData != null &&
                annotationData.redefinedCount == classRedefinedCount) {
                return annotationData;
            }
            // null or stale annotationData -> optimistically create new instance
            AnnotationData newAnnotationData = createAnnotationData(classRedefinedCount);
            // try to install it
            if (Atomic.casAnnotationData(this, annotationData, newAnnotationData)) {
                // successfully installed new AnnotationData
                return newAnnotationData;
            }
        }
    }

    private AnnotationData createAnnotationData(int classRedefinedCount) {
        Map<Class<? extends Annotation>, Annotation> declaredAnnotations =
            AnnotationParser.parseAnnotations(getRawAnnotations(), getConstantPool(), this);
        Class<?> superClass = getSuperclass();
        Map<Class<? extends Annotation>, Annotation> annotations = null;
        if (superClass != null) {
            Map<Class<? extends Annotation>, Annotation> superAnnotations =
                superClass.annotationData().annotations;
            for (Map.Entry<Class<? extends Annotation>, Annotation> e : superAnnotations.entrySet()) {
                Class<? extends Annotation> annotationClass = e.getKey();
                if (AnnotationType.getInstance(annotationClass).isInherited()) {
                    if (annotations == null) { // lazy construction
                        annotations = new LinkedHashMap<>((Math.max(
                                declaredAnnotations.size(),
                                Math.min(12, declaredAnnotations.size() + superAnnotations.size())
                            ) * 4 + 2) / 3
                        );
                    }
                    annotations.put(annotationClass, e.getValue());
                }
            }
        }
        if (annotations == null) {
            // no inherited annotations -> share the Map with declaredAnnotations
            annotations = declaredAnnotations;
        } else {
            // at least one inherited annotation -> declared may override inherited
            annotations.putAll(declaredAnnotations);
        }
        return new AnnotationData(annotations, declaredAnnotations, classRedefinedCount);
    }

    // Annotation types cache their internal (AnnotationType) form

    @SuppressWarnings("UnusedDeclaration")
    //注解类型缓存其内部 (AnnotationType) 形式
    private volatile transient AnnotationType annotationType;

    boolean casAnnotationType(AnnotationType oldType, AnnotationType newType) {
        return Atomic.casAnnotationType(this, oldType, newType);
    }

    AnnotationType getAnnotationType() {
        return annotationType;
    }

    Map<Class<? extends Annotation>, Annotation> getDeclaredAnnotationMap() {
        return annotationData().declaredAnnotations;
    }

    /* Backing store of user-defined values pertaining to this class.
     * Maintained by the ClassValue class.
     */
    //与此类有关的用户定义值的后备存储。由 ClassValue 类维护
    transient ClassValue.ClassValueMap classValueMap;

    /**
     * Returns an {@code AnnotatedType} object that represents the use of a
     * type to specify the superclass of the entity represented by this {@code
     * Class} object. (The <em>use</em> of type Foo to specify the superclass
     * in '...  extends Foo' is distinct from the <em>declaration</em> of type
     * Foo.)
     *  1.返回一个 AnnotatedType对象，该对象表示使用一种类型来指定此 Class对象表示的实体的超类。
     *  （Foo 类型的 <em>use<em> 用于指定 '... extends Foo' 中的超类与 Foo 类型的 <em>declaration<em> 不同。）
     * <p> If this {@code Class} object represents a type whose declaration
     * does not explicitly indicate an annotated superclass, then the return
     * value is an {@code AnnotatedType} object representing an element with no
     * annotations.
     * 2.如果此 Class对象表示其声明未明确指示带注解的超类的类型，
     * 则返回值是表示没有注解的元素的 AnnotatedType对象
     * <p> If this {@code Class} represents either the {@code Object} class, an
     * interface type, an array type, a primitive type, or void, the return
     * value is {@code null}.
     * 3.如果此 Class表示 Object类、接口类型、数组类型、原始类型或 void，则返回值为null
     * @return an object representing the superclass
     * @since 1.8
     */
    public AnnotatedType getAnnotatedSuperclass() {
        if (this == Object.class ||
                isInterface() ||
                isArray() ||
                isPrimitive() ||
                this == Void.TYPE) {
            return null;
        }

        return TypeAnnotationParser.buildAnnotatedSuperclass(getRawTypeAnnotations(), getConstantPool(), this);
    }

    /**
     * Returns an array of {@code AnnotatedType} objects that represent the use
     * of types to specify superinterfaces of the entity represented by this
     * {@code Class} object. (The <em>use</em> of type Foo to specify a
     * superinterface in '... implements Foo' is distinct from the
     * <em>declaration</em> of type Foo.)
     * 1.返回一个 AnnotatedType对象数组，这些对象表示使用类型来指定此 Class对象表示的实体的超接口。
     * （Foo 类型的 <em>use<em> 在 '... implements Foo' 中指定一个超接口与 Foo 类型的 <em>declaration<em> 不同。）
     * <p> If this {@code Class} object represents a class, the return value is
     * an array containing objects representing the uses of interface types to
     * specify interfaces implemented by the class. The order of the objects in
     * the array corresponds to the order of the interface types used in the
     * 'implements' clause of the declaration of this {@code Class} object.
     * 2.如果此Class对象表示一个类，则返回值是一个包含表示使用接口类型的对象的数组，以指定该类实现的接口。
     * 数组中对象的顺序与此 Class对象声明的“实现”子句中使用的接口类型的顺序相对应
     * <p> If this {@code Class} object represents an interface, the return
     * value is an array containing objects representing the uses of interface
     * types to specify interfaces directly extended by the interface. The
     * order of the objects in the array corresponds to the order of the
     * interface types used in the 'extends' clause of the declaration of this
     * {@code Class} object.
     * 3.如果此 Class对象表示一个接口，则返回值是一个数组，其中包含表示使用接口类型指定接口直接扩展的接口的对象。
     * 数组中对象的顺序与此 Class对象声明的“扩展”子句中使用的接口类型的顺序相对应
     * <p> If this {@code Class} object represents a class or interface whose
     * declaration does not explicitly indicate any annotated superinterfaces,
     * the return value is an array of length 0.
     * 4.如果这个Class对象代表一个类或接口，其声明没有明确指示任何带注释的超接口，则返回值为长度为 0 的数组
     * <p> If this {@code Class} object represents either the {@code Object}
     * class, an array type, a primitive type, or void, the return value is an
     * array of length 0.
     * 5.如果此 Class对象表示 Object类、数组类型、原始类型或 void，则返回值是长度为 0 的数组
     * @return an array representing the superinterfaces
     * @since 1.8
     */
    public AnnotatedType[] getAnnotatedInterfaces() {
         return TypeAnnotationParser.buildAnnotatedInterfaces(getRawTypeAnnotations(), getConstantPool(), this);
    }
}
