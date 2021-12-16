/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
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

import java.security.AccessController;
import sun.reflect.Reflection;
import sun.reflect.ReflectionFactory;
import java.lang.annotation.Annotation;

/**
 * The AccessibleObject class is the base class for Field, Method and
 * Constructor objects.  It provides the ability to flag a reflected
 * object as suppressing default Java language access control checks
 * when it is used.  The access checks--for public, default (package)
 * access, protected, and private members--are performed when Fields,
 * Methods or Constructors are used to set or get fields, to invoke
 * methods, or to create and initialize new instances of classes,
 * respectively.
 * 1.AccessibleObject 类是 Field、Method 和 Constructor 对象的基类。
 * 它提供了在使用时将反射对象标记为禁止默认 Java 语言访问控制检查的能力。
 * 当字段、方法或构造函数用于设置或获取字段、调用方法或创建和初始化类的新实例时，
 * 将分别执行访问检查——公共、默认（包）访问、受保护和私有成员
 *
 * <p>Setting the {@code accessible} flag in a reflected object
 * permits sophisticated applications with sufficient privilege, such
 * as Java Object Serialization or other persistence mechanisms, to
 * manipulate objects in a manner that would normally be prohibited.
 * 2.在反射对象中设置accessibility标志允许具有足够特权的复杂应用程序（
 * 例如 Java 对象序列化或其他持久性机制）以通常被禁止的方式操作对象。
 * <p>By default, a reflected object is <em>not</em> accessible.
 * 3.默认情况下，反射对象不可访问
 * @see Field
 * @see Method
 * @see Constructor
 * @see ReflectPermission
 *
 * @since 1.2
 */
public class AccessibleObject implements AnnotatedElement {

    /**
     * The Permission object that is used to check whether a client
     * has sufficient privilege to defeat Java language access
     * control checks.
     * Permission 对象，用于检查客户端是否有足够的权限来阻止 Java 语言访问控制检查。
     */
    static final private java.security.Permission ACCESS_PERMISSION =
        new ReflectPermission("suppressAccessChecks");

    /**
     * Convenience method to set the {@code accessible} flag for an
     * array of objects with a single security check (for efficiency).
     * 1.使用单个安全检查为一组对象设置可访问标志的便捷方法（为了效率)
     * <p>First, if there is a security manager, its
     * {@code checkPermission} method is called with a
     * {@code ReflectPermission("suppressAccessChecks")} permission.
     * 2.首先，如果有一个安全管理器，它的checkPermission方法被调用，
     * 并带有ReflectPermission("suppressAccessChecks")权限。
     * <p>A {@code SecurityException} is raised if {@code flag} is
     * {@code true} but accessibility of any of the elements of the input
     * {@code array} may not be changed (for example, if the element
     * object is a {@link Constructor} object for the class {@link
     * java.lang.Class}).  In the event of such a SecurityException, the
     * accessibility of objects is set to {@code flag} for array elements
     * upto (and excluding) the element for which the exception occurred; the
     * accessibility of elements beyond (and including) the element for which
     * the exception occurred is unchanged.
     * 3.如果 flag为true，但可能不会更改输入array的任何元素的可访问性
     * （例如，如果元素对象是 {类java.lang.Class的 Constructor对象）。
     * 在发生此类 SecurityException 的情况下，对象的可访问性被设置为 {@code flag}，
     * 直到（并排除）发生异常的元素的数组元素；超出（包括）发生异常的元素的元素的可访问性不变
     * @param array the array of AccessibleObjects
     * @param flag  the new value for the {@code accessible} flag
     *              in each object
     * @throws SecurityException if the request is denied.
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    public static void setAccessible(AccessibleObject[] array, boolean flag)
        throws SecurityException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) sm.checkPermission(ACCESS_PERMISSION);
        for (int i = 0; i < array.length; i++) {
            setAccessible0(array[i], flag);
        }
    }

    /**
     * Set the {@code accessible} flag for this object to
     * the indicated boolean value.  A value of {@code true} indicates that
     * the reflected object should suppress Java language access
     * checking when it is used.  A value of {@code false} indicates
     * that the reflected object should enforce Java language access checks.
     * 1.将此对象的可访问标志设置为指示的布尔值.true值表示反射对象在使用时应禁止 Java 语言访问检查。
     * false值表示反射对象应该强制执行 Java 语言访问检查。
     * <p>First, if there is a security manager, its
     * {@code checkPermission} method is called with a
     * {@code ReflectPermission("suppressAccessChecks")} permission.
     * 2.首先，如果有一个安全管理器，它的checkPermission方法被调用，
     * 并带有ReflectPermission("suppressAccessChecks")权限
     * <p>A {@code SecurityException} is raised if {@code flag} is
     * {@code true} but accessibility of this object may not be changed
     * (for example, if this element object is a {@link Constructor} object for
     * the class {@link java.lang.Class}).
     * 3.如果flag为true，但可能不会更改此对象的可访问性（例如，如果此元素对象是  Constructor对象），
     * 则会引发SecurityException链接 java.lang.Class}）
     * <p>A {@code SecurityException} is raised if this object is a {@link
     * java.lang.reflect.Constructor} object for the class
     * {@code java.lang.Class}, and {@code flag} is true.
     * 4.如果此对象是类java.lang.Class的java.lang.reflect.Constructor对象，并且flag为真，
     * 则会引发SecurityException。
     * @param flag the new value for the {@code accessible} flag
     * @throws SecurityException if the request is denied.
     * @see SecurityManager#checkPermission
     * @see java.lang.RuntimePermission
     */
    public void setAccessible(boolean flag) throws SecurityException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) sm.checkPermission(ACCESS_PERMISSION);
        setAccessible0(this, flag);
    }

    /* Check that you aren't exposing java.lang.Class.<init> or sensitive
       fields in java.lang.Class. */
    //检查您没有暴露 java.lang.Class.<init> 或 java.lang.Class 中的敏感字段
    private static void setAccessible0(AccessibleObject obj, boolean flag)
        throws SecurityException
    {
        if (obj instanceof Constructor && flag == true) {
            Constructor<?> c = (Constructor<?>)obj;
            if (c.getDeclaringClass() == Class.class) {
                throw new SecurityException("Cannot make a java.lang.Class" +
                                            " constructor accessible");
            }
        }
        obj.override = flag;
    }

    /**
     * Get the value of the {@code accessible} flag for this object.
     * 获取此对象的可访问标志的值。
     * @return the value of the object's {@code accessible} flag
     */
    public boolean isAccessible() {
        return override;
    }

    /**
     * Constructor: only used by the Java Virtual Machine.
     * 构造函数：仅由 Java 虚拟机使用
     */
    protected AccessibleObject() {}

    // Indicates whether language-level access checks are overridden
    // by this object. Initializes to "false". This field is used by
    // Field, Method, and Constructor.
    // 1.指示此对象是否覆盖语言级别的访问检查。初始化为“假”。该字段由字段、方法和构造函数使用
    // NOTE: for security purposes, this field must not be visible
    // outside this package.
    //2.注意：出于安全考虑，此字段不得在此包外可见
    boolean override;

    // Reflection factory used by subclasses for creating field,
    // method, and constructor accessors. Note that this is called
    // very early in the bootstrapping process.
    //子类用于创建字段、方法和构造函数访问器的反射工厂。请注意，这在引导过程中很早就被调用了。
    static final ReflectionFactory reflectionFactory =
        AccessController.doPrivileged(
            new sun.reflect.ReflectionFactory.GetReflectionFactoryAction());

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        throw new AssertionError("All subclasses should override this method");
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     * @since 1.5
     */
    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return AnnotatedElement.super.isAnnotationPresent(annotationClass);
    }

   /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        throw new AssertionError("All subclasses should override this method");
    }

    /**
     * @since 1.5
     */
    public Annotation[] getAnnotations() {
        return getDeclaredAnnotations();
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        // Only annotations on classes are inherited, for all other
        // objects getDeclaredAnnotation is the same as
        // getAnnotation.
        //仅继承类上的注释，对于所有其他对象，getDeclaredAnnotation 与 getAnnotation 相同
        return getAnnotation(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     * @since 1.8
     */
    @Override
    public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        // Only annotations on classes are inherited, for all other
        // objects getDeclaredAnnotationsByType is the same as
        // getAnnotationsByType.
        //仅继承类上的注释，对于所有其他对象，getDeclaredAnnotationsByType 与 getAnnotationsByType 相同。
        return getAnnotationsByType(annotationClass);
    }

    /**
     * @since 1.5
     */
    public Annotation[] getDeclaredAnnotations()  {
        throw new AssertionError("All subclasses should override this method");
    }


    // Shared access checking logic.
    // 1.共享访问检查逻辑。
    // For non-public members or members in package-private classes,
    // it is necessary to perform somewhat expensive security checks.
    // If the security check succeeds for a given class, it will
    // always succeed (it is not affected by the granting or revoking
    // of permissions); we speed up the check in the common case by
    // remembering the last Class for which the check succeeded.
    //2.对于非公共成员或包私有类中的成员，有必要执行一些昂贵的安全检查。
    // 如果对给定类的安全检查成功，则始终成功（不受授予或撤销权限的影响）；
    // 在一般情况下，我们通过记住检查成功的最后一个类来加快检查速度。
    // The simple security check for Constructor is to see if
    // the caller has already been seen, verified, and cached.
    // (See also Class.newInstance(), which uses a similar method.)
    // 3.Constructor 的简单安全检查是查看调用者是否已经被看到、验证和缓存。
    // （另请参见 Class.newInstance()，它使用了类似的方法。）
    // A more complicated security check cache is needed for Method and Field
    // The cache can be either null (empty cache), a 2-array of {caller,target},
    // or a caller (with target implicitly equal to this.clazz).
    // In the 2-array case, the target is always different from the clazz.
    //4.Method 和 Field 需要更复杂的安全检查缓存。缓存可以是 null（空缓存）、{caller,target} 的 2-array
    // 或调用者（目标隐式等于 this.clazz）。在 2-array 的情况下，目标总是与 clazz 不同。
    volatile Object securityCheckCache;

    void checkAccess(Class<?> caller, Class<?> clazz, Object obj, int modifiers)
        throws IllegalAccessException
    {
        if (caller == clazz) {  // quick check
            return;             // ACCESS IS OK 快速检查返回；访问正常
        }
        Object cache = securityCheckCache;  // read volatile
        Class<?> targetClass = clazz;
        if (obj != null
            && Modifier.isProtected(modifiers)
            && ((targetClass = obj.getClass()) != clazz)) {
            // Must match a 2-list of { caller, targetClass }.
            //必须匹配 { caller, targetClass } 的 2-list
            if (cache instanceof Class[]) {
                Class<?>[] cache2 = (Class<?>[]) cache;
                if (cache2[1] == targetClass &&
                    cache2[0] == caller) {
                    return;     // ACCESS IS OK
                }
                // (Test cache[1] first since range check for [1]
                // subsumes range check for [0].)
                //（首先测试缓存 [1]，因为 [1] 的范围检查包含了 [0] 的范围检查。）
            }
        } else if (cache == caller) {
            // Non-protected case (or obj.class == this.clazz).
            return;             // ACCESS IS OK
        }

        // If no return, fall through to the slow path.
        //如果没有返回，则进入缓慢的路径。
        slowCheckMemberAccess(caller, clazz, obj, modifiers, targetClass);
    }

    // Keep all this slow stuff out of line:
    void slowCheckMemberAccess(Class<?> caller, Class<?> clazz, Object obj, int modifiers,
                               Class<?> targetClass)
        throws IllegalAccessException
    {
        Reflection.ensureMemberAccess(caller, clazz, obj, modifiers);

        // Success: Update the cache.
        //成功：更新缓存。
        Object cache = ((targetClass == clazz)
                        ? caller
                        : new Class<?>[] { caller, targetClass });

        // Note:  The two cache elements are not volatile,
        // but they are effectively final.  The Java memory model
        // guarantees that the initializing stores for the cache
        // elements will occur before the volatile write.
        //注意：这两个缓存元素不是易失性的，但它们实际上是最终的。
        // Java 内存模型保证缓存元素的初始化存储将在易失性写入之前发生。
        securityCheckCache = cache;         // write volatile
    }
}
