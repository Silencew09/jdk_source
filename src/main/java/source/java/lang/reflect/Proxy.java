/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import sun.misc.ProxyGenerator;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

/**
 * {@code Proxy} provides static methods for creating dynamic proxy
 * classes and instances, and it is also the superclass of all
 * dynamic proxy classes created by those methods.
 * 1.Proxy提供了创建动态代理类和实例的静态方法，它也是由这些方法创建的所有动态代理类的超类
 * <p>To create a proxy for some interface {@code Foo}:
 * <pre>
 *     InvocationHandler handler = new MyInvocationHandler(...);
 *     Class&lt;?&gt; proxyClass = Proxy.getProxyClass(Foo.class.getClassLoader(), Foo.class);
 *     Foo f = (Foo) proxyClass.getConstructor(InvocationHandler.class).
 *                     newInstance(handler);
 * </pre>
 * or more simply:
 * <pre>
 *     Foo f = (Foo) Proxy.newProxyInstance(Foo.class.getClassLoader(),
 *                                          new Class&lt;?&gt;[] { Foo.class },
 *                                          handler);
 * </pre>
 *
 * <p>A <i>dynamic proxy class</i> (simply referred to as a <i>proxy
 * class</i> below) is a class that implements a list of interfaces
 * specified at runtime when the class is created, with behavior as
 * described below.
 * 2.动态代理类（以下简称为代理类）是一个类，它实现了类创建时在运行时指定的接口列表，其行为为如下面所描述的。
 * A <i>proxy interface</i> is such an interface that is implemented
 * by a proxy class.
 * 3.代理接口就是这样一个由代理类实现的接口
 * A <i>proxy instance</i> is an instance of a proxy class.
 *
 * Each proxy instance has an associated <i>invocation handler</i>
 * object, which implements the interface {@link InvocationHandler}.
 * A method invocation on a proxy instance through one of its proxy
 * interfaces will be dispatched to the {@link InvocationHandler#invoke
 * invoke} method of the instance's invocation handler, passing the proxy
 * instance, a {@code java.lang.reflect.Method} object identifying
 * the method that was invoked, and an array of type {@code Object}
 * containing the arguments.  The invocation handler processes the
 * encoded method invocation as appropriate and the result that it
 * returns will be returned as the result of the method invocation on
 * the proxy instance.
 * 4.每个代理实例都有一个关联的调用处理程序对象，它实现了接口InvocationHandler。
 * 通过代理接口之一对代理实例的方法调用将被分派到实例调用处理程序的InvocationHandler.invoke方法，
 * 传递代理实例，一个java.lang.reflect.Method对象标识被调用的方法，以及包含参数的Object类型的数组。
 * 调用处理程序适当地处理编码的方法调用，它返回的结果将作为代理实例上方法调用的结果返回
 * <p>A proxy class has the following properties:
 * 5.代理类具有以下属性：
 *  1)如果所有代理接口都是公共的，代理类是公共的、最终的，而不是抽象的
 * <ul>
 * <li>Proxy classes are <em>public, final, and not abstract</em> if
 * all proxy interfaces are public.</li>
 *
 * <li>Proxy classes are <em>non-public, final, and not abstract</em> if
 * any of the proxy interfaces is non-public.</li>
 * 2)如果任何代理接口是非公共的，则代理类是非公共的、最终的，而不是抽象的
 * <li>The unqualified name of a proxy class is unspecified.  The space
 * of class names that begin with the string {@code "$Proxy"}
 * should be, however, reserved for proxy classes.
 * 3)未指定代理类的非限定名称。但是，以字符串 "Proxy"开头的类名空间应该保留给代理类
 * <li>A proxy class extends {@code java.lang.reflect.Proxy}.
 * 4)代理类扩展了java.lang.reflect.Proxy
 * <li>A proxy class implements exactly the interfaces specified at its
 * creation, in the same order.
 *  5)代理类以相同的顺序实现在其创建时指定的接口
 * <li>If a proxy class implements a non-public interface, then it will
 * be defined in the same package as that interface.  Otherwise, the
 * package of a proxy class is also unspecified.  Note that package
 * sealing will not prevent a proxy class from being successfully defined
 * in a particular package at runtime, and neither will classes already
 * defined by the same class loader and the same package with particular
 * signers.
 * 6)如果代理类实现了非公共接口，那么它将与该接口定义在同一个包中。
 * 否则，代理类的包也是未指定的。请注意，包密封不会阻止代理类在运行时在特定包中成功定义，
 * 并且已由相同类加载器和具有特定签名者的相同包定义的类也不会
 * <li>Since a proxy class implements all of the interfaces specified at
 * its creation, invoking {@code getInterfaces} on its
 * {@code Class} object will return an array containing the same
 * list of interfaces (in the order specified at its creation), invoking
 * {@code getMethods} on its {@code Class} object will return
 * an array of {@code Method} objects that include all of the
 * methods in those interfaces, and invoking {@code getMethod} will
 * find methods in the proxy interfaces as would be expected.
 * 7)由于代理类实现了在其创建时指定的所有接口，因此在其Class对象上调用getInterfaces
 * 将返回一个包含相同接口列表的数组（按照其创建时指定的顺序），调用其Class对象上的getMethods
 * 将返回一个Method对象数组，其中包括这些接口中的所有方法，并且调用 getMethod将在代理接口中找到方法预期的
 * <li>The {@link Proxy#isProxyClass Proxy.isProxyClass} method will
 * return true if it is passed a proxy class-- a class returned by
 * {@code Proxy.getProxyClass} or the class of an object returned by
 * {@code Proxy.newProxyInstance}-- and false otherwise.
 * 8)Proxy.isProxyClass方法如果传入一个代理类——一个由Proxy.getProxyClass返回的类
 * 或由Proxy.newProxyInstance返回的一个对象的类，将返回 true——否则为假。
 * <li>The {@code java.security.ProtectionDomain} of a proxy class
 * is the same as that of system classes loaded by the bootstrap class
 * loader, such as {@code java.lang.Object}, because the code for a
 * proxy class is generated by trusted system code.  This protection
 * domain will typically be granted
 * {@code java.security.AllPermission}.
 * 9)代理类的java.security.ProtectionDomain与bootstrap类加载器加载的系统类相同，
 * 比如java.lang.Object，因为生成了代理类的代码通过受信任的系统代码。
 * 通常会授予此保护域java.security.AllPermission
 * <li>Each proxy class has one public constructor that takes one argument,
 * an implementation of the interface {@link InvocationHandler}, to set
 * the invocation handler for a proxy instance.  Rather than having to use
 * the reflection API to access the public constructor, a proxy instance
 * can be also be created by calling the {@link Proxy#newProxyInstance
 * Proxy.newProxyInstance} method, which combines the actions of calling
 * {@link Proxy#getProxyClass Proxy.getProxyClass} with invoking the
 * constructor with an invocation handler.
 * 10)每个代理类都有一个公共构造函数，它接受一个参数，即接口InvocationHandler的实现，
 * 以设置代理实例的调用处理程序。不必使用反射 API 来访问公共构造函数，还可以通过调用
 * Proxy.newProxyInstance方法创建代理实例，该方法将调用Proxy.getProxyClass的动作与使用调用处理程序调用构造函数
 * </ul>
 *
 * <p>A proxy instance has the following properties:
 *  6.代理实例具有以下属性：
 * <ul>
 * <li>Given a proxy instance {@code proxy} and one of the
 * interfaces implemented by its proxy class {@code Foo}, the
 * following expression will return true:
 *  1)给定一个代理实例proxy及其代理类Foo实现的接口之一，以下表达式将返回 true：
 *    a) proxy instanceof Foo
 *    b)并且下面的转换操作将会成功（而不是抛出一个ClassCastException）(Foo) proxy
 * <pre>
 *     {@code proxy instanceof Foo}
 * </pre>
 * and the following cast operation will succeed (rather than throwing
 * a {@code ClassCastException}):
 * <pre>
 *     {@code (Foo) proxy}
 * </pre>
 *
 * <li>Each proxy instance has an associated invocation handler, the one
 * that was passed to its constructor.  The static
 * {@link Proxy#getInvocationHandler Proxy.getInvocationHandler} method
 * will return the invocation handler associated with the proxy instance
 * passed as its argument.
 * 2)每个代理实例都有一个关联的调用处理程序，该处理程序已传递给其构造函数。静态Proxy.getInvocationHandler
 * 方法将返回与作为其参数传递的代理实例关联的调用处理程序
 * <li>An interface method invocation on a proxy instance will be
 * encoded and dispatched to the invocation handler's {@link
 * InvocationHandler#invoke invoke} method as described in the
 * documentation for that method.
 * 3)代理实例上的接口方法调用将被编码并分派到调用处理程序的InvocationHandler.invoke方法，如该方法的文档中所述。
 * <li>An invocation of the {@code hashCode},
 * {@code equals}, or {@code toString} methods declared in
 * {@code java.lang.Object} on a proxy instance will be encoded and
 * dispatched to the invocation handler's {@code invoke} method in
 * the same manner as interface method invocations are encoded and
 * dispatched, as described above.  The declaring class of the
 * {@code Method} object passed to {@code invoke} will be
 * {@code java.lang.Object}.  Other public methods of a proxy
 * instance inherited from {@code java.lang.Object} are not
 * overridden by a proxy class, so invocations of those methods behave
 * like they do for instances of {@code java.lang.Object}.
 *  4)在代理实例的 java.lang.Object中声明的hashCode、equals或 toString方法的调用将
 *  被编码并分派到调用处理程序的invoke方法以与接口方法调用相同的方式进行编码和调度，如上所述。
 *  传递给 invoke的Method对象的声明类将是java.lang.Object。从java.lang.Object继承的代理实例的
 *  其他公共方法不会被代理类覆盖，因此这些方法的调用行为就像它们对java.lang.Object的实例所做的一样。
 * </ul>
 *
 * <h3>Methods Duplicated in Multiple Proxy Interfaces</h3>
 *  7.在多个代理接口中重复的方法
 * <p>When two or more interfaces of a proxy class contain a method with
 * the same name and parameter signature, the order of the proxy class's
 * interfaces becomes significant.  When such a <i>duplicate method</i>
 * is invoked on a proxy instance, the {@code Method} object passed
 * to the invocation handler will not necessarily be the one whose
 * declaring class is assignable from the reference type of the interface
 * that the proxy's method was invoked through.  This limitation exists
 * because the corresponding method implementation in the generated proxy
 * class cannot determine which interface it was invoked through.
 * Therefore, when a duplicate method is invoked on a proxy instance,
 * the {@code Method} object for the method in the foremost interface
 * that contains the method (either directly or inherited through a
 * superinterface) in the proxy class's list of interfaces is passed to
 * the invocation handler's {@code invoke} method, regardless of the
 * reference type through which the method invocation occurred.
 *  1)当代理类的两个或多个接口包含具有相同名称和参数签名的方法时，代理类的接口顺序就变得很重要。
 *  当在代理实例上调用这样的重复方法时，传递给调用处理程序的Method对象不一定是其声明类
 *  可从接口的引用类型分配的对象代理的方法是通过调用的。之所以存在这个限制，是因为生成的代理类中相应的方法实现无
 *  法确定它是通过哪个接口调用的。因此，当在代理实例上调用重复方法时，包含代理类接口列表中的方法
 *  （直接或通过超接口继承）的最前面接口中方法的Method对象将传递给调用处理程序的invoke方法，
 *  无论方法调用通过何种引用类型发生。
 * <p>If a proxy interface contains a method with the same name and
 * parameter signature as the {@code hashCode}, {@code equals},
 * or {@code toString} methods of {@code java.lang.Object},
 * when such a method is invoked on a proxy instance, the
 * {@code Method} object passed to the invocation handler will have
 * {@code java.lang.Object} as its declaring class.  In other words,
 * the public, non-final methods of {@code java.lang.Object}
 * logically precede all of the proxy interfaces for the determination of
 * which {@code Method} object to pass to the invocation handler.
 * 2)如果代理接口包含与java.lang.Object的hashCode、equals或toString方法具有相同名称和参数签名的方法，
 * 当这样的方法在代理实例上调用时，传递给调用处理程序的Method对象将具有java.lang.Object作为其声明类。
 * 换句话说，java.lang.Object的公共非最终方法在逻辑上位于所有代理接口之前，用于确定将哪个Method对象传递给调用处理程序。
 * <p>Note also that when a duplicate method is dispatched to an
 * invocation handler, the {@code invoke} method may only throw
 * checked exception types that are assignable to one of the exception
 * types in the {@code throws} clause of the method in all of
 * the proxy interfaces that it can be invoked through.  If the
 * {@code invoke} method throws a checked exception that is not
 * assignable to any of the exception types declared by the method in one
 * of the proxy interfaces that it can be invoked through, then an
 * unchecked {@code UndeclaredThrowableException} will be thrown by
 * the invocation on the proxy instance.  This restriction means that not
 * all of the exception types returned by invoking
 * {@code getExceptionTypes} on the {@code Method} object
 * passed to the {@code invoke} method can necessarily be thrown
 * successfully by the {@code invoke} method.
 * 9.另请注意，当重复方法被分派到调用处理程序时，invoke方法可能只抛出可分配给中方法的 throws子句中的
 * 异常类型之一的已检查异常类型可以通过它调用的代理接口。如果invoke方法抛出一个检查异常，
 * 该异常不能分配给该方法在其可以通过调用的代理接口之一中声明的任何异常类型，
 * 那么将抛出一个未检查的 UndeclaredThrowableException通过对代理实例的调用。
 * 此限制意味着并非所有通过在传递给invoke方法的Method对象上调用getExceptionTypes
 * 返回的异常类型都一定能被invoke方法成功抛出
 * @author      Peter Jones
 * @see         InvocationHandler
 * @since       1.3
 */
public class Proxy implements java.io.Serializable {

    private static final long serialVersionUID = -2222568056686623797L;

    /** parameter types of a proxy class constructor */
    //代理类构造函数的参数类型
    private static final Class<?>[] constructorParams =
        { InvocationHandler.class };

    /**
     * a cache of proxy classes
     * 代理类的缓存
     */
    private static final WeakCache<ClassLoader, Class<?>[], Class<?>>
        proxyClassCache = new WeakCache<>(new KeyFactory(), new ProxyClassFactory());

    /**
     * the invocation handler for this proxy instance.
     * 此代理实例的调用处理程序。
     * @serial
     */
    protected InvocationHandler h;

    /**
     * Prohibits instantiation.
     * 禁止实例化。
     */
    private Proxy() {
    }

    /**
     * Constructs a new {@code Proxy} instance from a subclass
     * (typically, a dynamic proxy class) with the specified value
     * for its invocation handler.
     * 从子类（通常是动态代理类）构造一个新的Proxy实例，并为其调用处理程序指定值
     *
     * @param  h the invocation handler for this proxy instance
     *
     * @throws NullPointerException if the given invocation handler, {@code h},
     *         is {@code null}.
     */
    protected Proxy(InvocationHandler h) {
        Objects.requireNonNull(h);
        this.h = h;
    }

    /**
     * Returns the {@code java.lang.Class} object for a proxy class
     * given a class loader and an array of interfaces.  The proxy class
     * will be defined by the specified class loader and will implement
     * all of the supplied interfaces.  If any of the given interfaces
     * is non-public, the proxy class will be non-public. If a proxy class
     * for the same permutation of interfaces has already been defined by the
     * class loader, then the existing proxy class will be returned; otherwise,
     * a proxy class for those interfaces will be generated dynamically
     * and defined by the class loader.
     * 1.返回给定类加载器和接口数组的代理类的java.lang.Class对象。
     * 代理类将由指定的类加载器定义，并将实现所有提供的接口。如果任何给定的接口是非公共的，
     * 则代理类将是非公共的。如果类加载器已经定义了相同接口排列的代理类，则返回现有的代理类；
     * 否则，这些接口的代理类将动态生成并由类加载器定义。
     * <p>There are several restrictions on the parameters that may be
     * passed to {@code Proxy.getProxyClass}:
     * 2.可能传递给 Proxy.getProxyClass的参数有几个限制：
     * <ul>
     * <li>All of the {@code Class} objects in the
     * {@code interfaces} array must represent interfaces, not
     * classes or primitive types.
     *  1)interfaces数组中的所有Class对象都必须表示接口，而不是类或原始类型。
     * <li>No two elements in the {@code interfaces} array may
     * refer to identical {@code Class} objects.
     *  2)interfaces数组中的任何两个元素都不能引用相同的Class对象。
     * <li>All of the interface types must be visible by name through the
     * specified class loader.  In other words, for class loader
     * {@code cl} and every interface {@code i}, the following
     * expression must be true:
     * 3)所有接口类型都必须通过指定的类加载器按名称可见。
     * 换句话说，对于类加载器cl和每个接口i，以下表达式必须为真：
     * Class.forName(i.getName(), false, cl) == i
     * <pre>
     *     Class.forName(i.getName(), false, cl) == i
     * </pre>
     *
     * <li>All non-public interfaces must be in the same package;
     * otherwise, it would not be possible for the proxy class to
     * implement all of the interfaces, regardless of what package it is
     * defined in.
     * 4)所有非公共接口必须在同一个包中；否则，代理类不可能实现所有接口，无论它是在哪个包中定义的
     * <li>For any set of member methods of the specified interfaces
     * that have the same signature:
     * 5)对于具有相同签名的指定接口的任何成员方法集：
     *      a)如果任何方法的返回类型是原始类型或 void，则所有方法都必须具有相同的返回类型。
     *      b)否则，其中一个方法必须具有可分配给其余方法的所有返回类型的返回类型
     * <ul>
     * <li>If the return type of any of the methods is a primitive
     * type or void, then all of the methods must have that same
     * return type.
     * <li>Otherwise, one of the methods must have a return type that
     * is assignable to all of the return types of the rest of the
     * methods.
     * </ul>
     *
     * <li>The resulting proxy class must not exceed any limits imposed
     * on classes by the virtual machine.  For example, the VM may limit
     * the number of interfaces that a class may implement to 65535; in
     * that case, the size of the {@code interfaces} array must not
     * exceed 65535.
     * </ul>
     * 6)生成的代理类不得超过虚拟机对类施加的任何限制。
     * 例如，VM 可能会限制一个类可以实现的接口数量为 65535；
     * 在这种情况下，interfaces数组的大小不得超过 65535
     * <p>If any of these restrictions are violated,
     * {@code Proxy.getProxyClass} will throw an
     * {@code IllegalArgumentException}.  If the {@code interfaces}
     * array argument or any of its elements are {@code null}, a
     * {@code NullPointerException} will be thrown.
     * 7)如果违反任何这些限制，Proxy.getProxyClass将抛出一个IllegalArgumentException。
     * 如果 interfaces数组参数或其任何元素为 null，则会抛出NullPointerException
     * <p>Note that the order of the specified proxy interfaces is
     * significant: two requests for a proxy class with the same combination
     * of interfaces but in a different order will result in two distinct
     * proxy classes.
     * 8)请注意，指定代理接口的顺序很重要：对具有相同接口组合但顺序不同的代理类的两个请求将导致两个不同的代理类
     * @param   loader the class loader to define the proxy class
     * @param   interfaces the list of interfaces for the proxy class
     *          to implement
     * @return  a proxy class that is defined in the specified class loader
     *          and that implements the specified interfaces
     * @throws  IllegalArgumentException if any of the restrictions on the
     *          parameters that may be passed to {@code getProxyClass}
     *          are violated
     * @throws  SecurityException if a security manager, <em>s</em>, is present
     *          and any of the following conditions is met:
     *          <ul>
     *             <li> the given {@code loader} is {@code null} and
     *             the caller's class loader is not {@code null} and the
     *             invocation of {@link SecurityManager#checkPermission
     *             s.checkPermission} with
     *             {@code RuntimePermission("getClassLoader")} permission
     *             denies access.</li>
     *             <li> for each proxy interface, {@code intf},
     *             the caller's class loader is not the same as or an
     *             ancestor of the class loader for {@code intf} and
     *             invocation of {@link SecurityManager#checkPackageAccess
     *             s.checkPackageAccess()} denies access to {@code intf}.</li>
     *          </ul>

     * @throws  NullPointerException if the {@code interfaces} array
     *          argument or any of its elements are {@code null}
     */
    @CallerSensitive
    public static Class<?> getProxyClass(ClassLoader loader,
                                         Class<?>... interfaces)
        throws IllegalArgumentException
    {
        final Class<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }

        return getProxyClass0(loader, intfs);
    }

    /*
     * Check permissions required to create a Proxy class.
     * 1.检查创建代理类所需的权限。
     * To define a proxy class, it performs the access checks as in
     * Class.forName (VM will invoke ClassLoader.checkPackageAccess):
     * 1. "getClassLoader" permission check if loader == null
     * 2. checkPackageAccess on the interfaces it implements
     * 2.要定义代理类，它执行 Class.forName 中的访问检查（VM 将调用 ClassLoader.checkPackageAccess）：
     *  1). “getClassLoader”权限检查如果 loader == null
     * 2). checkPackageAccess 在它实现的接口上
     * To get a constructor and new instance of a proxy class, it performs
     * the package access check on the interfaces it implements
     * as in Class.getConstructor.
     * 3.要获取代理类的构造函数和新实例，它会在其实现的接口上执行包访问检查，就像在 Class.getConstructor 中一样。
     * If an interface is non-public, the proxy class must be defined by
     * the defining loader of the interface.  If the caller's class loader
     * is not the same as the defining loader of the interface, the VM
     * will throw IllegalAccessError when the generated proxy class is
     * being defined via the defineClass0 method.
     * 4.如果接口是非公共的，则代理类必须由接口的定义加载器定义。如果调用者的类加载器与接口的定义加载器不同，
     * 当通过defineClass0方法定义生成的代理类时，VM将抛出IllegalAccessError
     */
    private static void checkProxyAccess(Class<?> caller,
                                         ClassLoader loader,
                                         Class<?>... interfaces)
    {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ClassLoader ccl = caller.getClassLoader();
            if (VM.isSystemDomainLoader(loader) && !VM.isSystemDomainLoader(ccl)) {
                sm.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION);
            }
            ReflectUtil.checkProxyPackageAccess(ccl, interfaces);
        }
    }

    /**
     * Generate a proxy class.  Must call the checkProxyAccess method
     * to perform permission checks before calling this.
     * 生成代理类。在调用之前必须调用 checkProxyAccess 方法来执行权限检查。
     */
    private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
        //如果由实现给定接口的给定加载器定义的代理类存在，这将简单地返回缓存副本；否则，它将通过 ProxyClassFactory 创建代理类
        return proxyClassCache.get(loader, interfaces);
    }

    /*
     * a key used for proxy class with 0 implemented interfaces
     * 用于具有 0 个已实现接口的代理类的键
     */
    private static final Object key0 = new Object();

    /*
     * Key1 and Key2 are optimized for the common use of dynamic proxies
     * that implement 1 or 2 interfaces.
     * Key1 和 Key2 针对实现 1 或 2 个接口的动态代理的常见用途进行了优化。
     */

    /*
     * a key used for proxy class with 1 implemented interface
     * 用于具有 1 个已实现接口的代理类的密钥
     */
    private static final class Key1 extends WeakReference<Class<?>> {
        private final int hash;

        Key1(Class<?> intf) {
            super(intf);
            this.hash = intf.hashCode();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            Class<?> intf;
            return this == obj ||
                   obj != null &&
                   obj.getClass() == Key1.class &&
                   (intf = get()) != null &&
                   intf == ((Key1) obj).get();
        }
    }

    /*
     * a key used for proxy class with 2 implemented interfaces
     * 用于具有 2 个已实现接口的代理类的密钥
     */
    private static final class Key2 extends WeakReference<Class<?>> {
        private final int hash;
        private final WeakReference<Class<?>> ref2;

        Key2(Class<?> intf1, Class<?> intf2) {
            super(intf1);
            hash = 31 * intf1.hashCode() + intf2.hashCode();
            ref2 = new WeakReference<Class<?>>(intf2);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            Class<?> intf1, intf2;
            return this == obj ||
                   obj != null &&
                   obj.getClass() == Key2.class &&
                   (intf1 = get()) != null &&
                   intf1 == ((Key2) obj).get() &&
                   (intf2 = ref2.get()) != null &&
                   intf2 == ((Key2) obj).ref2.get();
        }
    }

    /*
     * a key used for proxy class with any number of implemented interfaces
     * (used here for 3 or more only)
     * 用于具有任意数量已实现接口的代理类的密钥（此处仅用于 3 个或更多）
     */
    private static final class KeyX {
        private final int hash;
        private final WeakReference<Class<?>>[] refs;

        @SuppressWarnings("unchecked")
        KeyX(Class<?>[] interfaces) {
            hash = Arrays.hashCode(interfaces);
            refs = (WeakReference<Class<?>>[])new WeakReference<?>[interfaces.length];
            for (int i = 0; i < interfaces.length; i++) {
                refs[i] = new WeakReference<>(interfaces[i]);
            }
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj ||
                   obj != null &&
                   obj.getClass() == KeyX.class &&
                   equals(refs, ((KeyX) obj).refs);
        }

        private static boolean equals(WeakReference<Class<?>>[] refs1,
                                      WeakReference<Class<?>>[] refs2) {
            if (refs1.length != refs2.length) {
                return false;
            }
            for (int i = 0; i < refs1.length; i++) {
                Class<?> intf = refs1[i].get();
                if (intf == null || intf != refs2[i].get()) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * A function that maps an array of interfaces to an optimal key where
     * Class objects representing interfaces are weakly referenced.
     * 将接口数组映射到最佳键的函数，其中表示接口的类对象被弱引用。
     */
    private static final class KeyFactory
        implements BiFunction<ClassLoader, Class<?>[], Object>
    {
        @Override
        public Object apply(ClassLoader classLoader, Class<?>[] interfaces) {
            switch (interfaces.length) {
                case 1: return new Key1(interfaces[0]); // the most frequent
                case 2: return new Key2(interfaces[0], interfaces[1]);
                case 0: return key0;
                default: return new KeyX(interfaces);
            }
        }
    }

    /**
     * A factory function that generates, defines and returns the proxy class given
     * the ClassLoader and array of interfaces.
     * 一个工厂函数，它生成、定义和返回给定 ClassLoader 和接口数组的代理类
     */
    private static final class ProxyClassFactory
        implements BiFunction<ClassLoader, Class<?>[], Class<?>>
    {
        // prefix for all proxy class names
        //所有代理类名称的前缀
        private static final String proxyClassNamePrefix = "$Proxy";

        // next number to use for generation of unique proxy class names
        //用于生成唯一代理类名称的下一个数字
        private static final AtomicLong nextUniqueNumber = new AtomicLong();

        @Override
        public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {

            Map<Class<?>, Boolean> interfaceSet = new IdentityHashMap<>(interfaces.length);
            for (Class<?> intf : interfaces) {
                /*
                 * Verify that the class loader resolves the name of this
                 * interface to the same Class object.
                 * 验证类加载器是否将此接口的名称解析为相同的 Class 对象
                 */
                Class<?> interfaceClass = null;
                try {
                    interfaceClass = Class.forName(intf.getName(), false, loader);
                } catch (ClassNotFoundException e) {
                }
                if (interfaceClass != intf) {
                    throw new IllegalArgumentException(
                        intf + " is not visible from class loader");
                }
                /*
                 * Verify that the Class object actually represents an
                 * interface.
                 * 验证 Class 对象实际上表示一个接口。
                 */
                if (!interfaceClass.isInterface()) {
                    throw new IllegalArgumentException(
                        interfaceClass.getName() + " is not an interface");
                }
                /*
                 * Verify that this interface is not a duplicate.
                 * 验证此接口不是重复的
                 */
                if (interfaceSet.put(interfaceClass, Boolean.TRUE) != null) {
                    throw new IllegalArgumentException(
                        "repeated interface: " + interfaceClass.getName());
                }
            }

            String proxyPkg = null;     // package to define proxy class in 包中定义代理类
            int accessFlags = Modifier.PUBLIC | Modifier.FINAL;

            /*
             * Record the package of a non-public proxy interface so that the
             * proxy class will be defined in the same package.  Verify that
             * all non-public proxy interfaces are in the same package.
             * 记录一个非公共代理接口的包，这样代理类就会被定义在同一个包中。
             * 验证所有非公共代理接口都在同一个包中
             */
            for (Class<?> intf : interfaces) {
                int flags = intf.getModifiers();
                if (!Modifier.isPublic(flags)) {
                    accessFlags = Modifier.FINAL;
                    String name = intf.getName();
                    int n = name.lastIndexOf('.');
                    String pkg = ((n == -1) ? "" : name.substring(0, n + 1));
                    if (proxyPkg == null) {
                        proxyPkg = pkg;
                    } else if (!pkg.equals(proxyPkg)) {
                        throw new IllegalArgumentException(
                            "non-public interfaces from different packages");
                    }
                }
            }

            if (proxyPkg == null) {
                // if no non-public proxy interfaces, use com.sun.proxy package
                //如果没有非公共代理接口，使用 com.sun.proxy 包
                proxyPkg = ReflectUtil.PROXY_PACKAGE + ".";
            }

            /*
             * Choose a name for the proxy class to generate.
             * 为要生成的代理类选择一个名称。
             */
            long num = nextUniqueNumber.getAndIncrement();
            String proxyName = proxyPkg + proxyClassNamePrefix + num;

            /*
             * Generate the specified proxy class.
             * 生成指定的代理类
             */
            byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
                proxyName, interfaces, accessFlags);
            try {
                return defineClass0(loader, proxyName,
                                    proxyClassFile, 0, proxyClassFile.length);
            } catch (ClassFormatError e) {
                /*
                 * A ClassFormatError here means that (barring bugs in the
                 * proxy class generation code) there was some other
                 * invalid aspect of the arguments supplied to the proxy
                 * class creation (such as virtual machine limitations
                 * exceeded).
                 * 这里的 ClassFormatError 意味着（除了代理类生成代码中的错误）
                 * 提供给代理类创建的参数存在其他一些无效方面（例如超出虚拟机限制）
                 */
                throw new IllegalArgumentException(e.toString());
            }
        }
    }

    /**
     * Returns an instance of a proxy class for the specified interfaces
     * that dispatches method invocations to the specified invocation
     * handler.
     * 返回指定接口的代理类的实例，该接口将方法调用分派到指定的调用处理程序。
     * <p>{@code Proxy.newProxyInstance} throws
     * {@code IllegalArgumentException} for the same reasons that
     * {@code Proxy.getProxyClass} does.
     *
     * @param   loader the class loader to define the proxy class
     * @param   interfaces the list of interfaces for the proxy class
     *          to implement
     * @param   h the invocation handler to dispatch method invocations to
     * @return  a proxy instance with the specified invocation handler of a
     *          proxy class that is defined by the specified class loader
     *          and that implements the specified interfaces
     * @throws  IllegalArgumentException if any of the restrictions on the
     *          parameters that may be passed to {@code getProxyClass}
     *          are violated
     * @throws  SecurityException if a security manager, <em>s</em>, is present
     *          and any of the following conditions is met:
     *          <ul>
     *          <li> the given {@code loader} is {@code null} and
     *               the caller's class loader is not {@code null} and the
     *               invocation of {@link SecurityManager#checkPermission
     *               s.checkPermission} with
     *               {@code RuntimePermission("getClassLoader")} permission
     *               denies access;</li>
     *          <li> for each proxy interface, {@code intf},
     *               the caller's class loader is not the same as or an
     *               ancestor of the class loader for {@code intf} and
     *               invocation of {@link SecurityManager#checkPackageAccess
     *               s.checkPackageAccess()} denies access to {@code intf};</li>
     *          <li> any of the given proxy interfaces is non-public and the
     *               caller class is not in the same {@linkplain Package runtime package}
     *               as the non-public interface and the invocation of
     *               {@link SecurityManager#checkPermission s.checkPermission} with
     *               {@code ReflectPermission("newProxyInPackage.{package name}")}
     *               permission denies access.</li>
     *          </ul>
     * @throws  NullPointerException if the {@code interfaces} array
     *          argument or any of its elements are {@code null}, or
     *          if the invocation handler, {@code h}, is
     *          {@code null}
     */
    @CallerSensitive
    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        Objects.requireNonNull(h);

        final Class<?>[] intfs = interfaces.clone();
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }

        /*
         * Look up or generate the designated proxy class.
         * 查找或生成指定的代理类。
         */
        Class<?> cl = getProxyClass0(loader, intfs);

        /*
         * Invoke its constructor with the designated invocation handler.
         * 使用指定的调用处理程序调用其构造函数。
         */
        try {
            if (sm != null) {
                checkNewProxyPermission(Reflection.getCallerClass(), cl);
            }

            final Constructor<?> cons = cl.getConstructor(constructorParams);
            final InvocationHandler ih = h;
            if (!Modifier.isPublic(cl.getModifiers())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        cons.setAccessible(true);
                        return null;
                    }
                });
            }
            return cons.newInstance(new Object[]{h});
        } catch (IllegalAccessException|InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString(), e);
        }
    }

    private static void checkNewProxyPermission(Class<?> caller, Class<?> proxyClass) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            if (ReflectUtil.isNonPublicProxyClass(proxyClass)) {
                ClassLoader ccl = caller.getClassLoader();
                ClassLoader pcl = proxyClass.getClassLoader();

                // do permission check if the caller is in a different runtime package
                // of the proxy class
                //如果调用者在代理类的不同运行时包中，则进行权限检查
                int n = proxyClass.getName().lastIndexOf('.');
                String pkg = (n == -1) ? "" : proxyClass.getName().substring(0, n);

                n = caller.getName().lastIndexOf('.');
                String callerPkg = (n == -1) ? "" : caller.getName().substring(0, n);

                if (pcl != ccl || !pkg.equals(callerPkg)) {
                    sm.checkPermission(new ReflectPermission("newProxyInPackage." + pkg));
                }
            }
        }
    }

    /**
     * Returns true if and only if the specified class was dynamically
     * generated to be a proxy class using the {@code getProxyClass}
     * method or the {@code newProxyInstance} method.
     * 1.当且仅当使用 getProxyClass方法或 newProxyInstance方法将指定的类动态生成为代理类时，才返回 true。
     * <p>The reliability of this method is important for the ability
     * to use it to make security decisions, so its implementation should
     * not just test if the class in question extends {@code Proxy}.
     * 2.此方法的可靠性对于使用它做出安全决策的能力很重要，因此它的实现不应该只是测试所讨论的类是否扩展了Proxy。
     * @param   cl the class to test
     * @return  {@code true} if the class is a proxy class and
     *          {@code false} otherwise
     * @throws  NullPointerException if {@code cl} is {@code null}
     */
    public static boolean isProxyClass(Class<?> cl) {
        return Proxy.class.isAssignableFrom(cl) && proxyClassCache.containsValue(cl);
    }

    /**
     * Returns the invocation handler for the specified proxy instance.
     * 1.返回指定代理实例的调用处理程序。
     * @param   proxy the proxy instance to return the invocation handler for
     * @return  the invocation handler for the proxy instance
     * @throws  IllegalArgumentException if the argument is not a
     *          proxy instance
     * @throws  SecurityException if a security manager, <em>s</em>, is present
     *          and the caller's class loader is not the same as or an
     *          ancestor of the class loader for the invocation handler
     *          and invocation of {@link SecurityManager#checkPackageAccess
     *          s.checkPackageAccess()} denies access to the invocation
     *          handler's class.
     */
    @CallerSensitive
    public static InvocationHandler getInvocationHandler(Object proxy)
        throws IllegalArgumentException
    {
        /*
         * Verify that the object is actually a proxy instance.
         * 验证对象实际上是代理实例。
         */
        if (!isProxyClass(proxy.getClass())) {
            throw new IllegalArgumentException("not a proxy instance");
        }

        final Proxy p = (Proxy) proxy;
        final InvocationHandler ih = p.h;
        if (System.getSecurityManager() != null) {
            Class<?> ihClass = ih.getClass();
            Class<?> caller = Reflection.getCallerClass();
            if (ReflectUtil.needsPackageAccessCheck(caller.getClassLoader(),
                                                    ihClass.getClassLoader()))
            {
                ReflectUtil.checkPackageAccess(ihClass);
            }
        }

        return ih;
    }

    private static native Class<?> defineClass0(ClassLoader loader, String name,
                                                byte[] b, int off, int len);
}
