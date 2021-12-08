/*
 * Copyright (c) 1994, 2012, Oracle and/or its affiliates. All rights reserved.
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

/**
 * Class {@code Object} is the root of the class hierarchy.
 * Every class has {@code Object} as a superclass. All objects,
 * including arrays, implement the methods of this class.
 *类 Object是类层次结构的根。每个类都有 Object作为超类。所有对象，包括数组，都实现了这个类的方法
 * @author  unascribed
 * @see     java.lang.Class
 * @since   JDK1.0
 */
public class Object {

    private static native void registerNatives();
    static {
        registerNatives();
    }

    /**
     * Returns the runtime class of this {@code Object}. The returned
     * {@code Class} object is the object that is locked by {@code
     * static synchronized} methods of the represented class.
     * 1.返回此 Object的运行时类。返回的Class对象是被代表类的static synchronized方法锁定的对象。
     * <p><b>The actual result type is {@code Class<? extends |X|>}
     * where {@code |X|} is the erasure of the static type of the
     * expression on which {@code getClass} is called.</b> For
     * example, no cast is required in this code fragment:</p>
     * 2.实际的结果类型是Class<? extends |X|>其中  |X| 是对调用 getClass的表达式的静态类型的擦除
     * 3.例如，此代码片段中不需要强制转换：
     * 数 n = 0;
     * Class<? extends Number> c = n.getClass();
     * <p>
     * {@code Number n = 0;                             }<br>
     * {@code Class<? extends Number> c = n.getClass(); }
     * </p>
     *
     * @return The {@code Class} object that represents the runtime
     *         class of this object.
     * @jls 15.8.2 Class Literals
     */
    public final native Class<?> getClass();

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.
     * 1.返回对象的哈希码值。支持此方法是为了有利于散列表，例如 java.util.HashMap提供的散列表。
     * <p>
     * The general contract of {@code hashCode} is:
     * 2.hashCode的总合约是：
     *  1)每当在 Java 应用程序执行期间在同一对象上多次调用它时，hashCode
     *  方法必须始终返回相同的整数，前提是在对象的 equals比较中使用的信息没有被修改。
     *  该整数不需要从应用程序的一次执行到同一应用程序的另一次执行保持一致。
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     *     an execution of a Java application, the {@code hashCode} method
     *     must consistently return the same integer, provided no information
     *     used in {@code equals} comparisons on the object is modified.
     *     This integer need not remain consistent from one execution of an
     *     application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     *     method, then calling the {@code hashCode} method on each of
     *     the two objects must produce the same integer result.
     *    2)如果两个对象根据equals(Object)方法相等，则对两个对象中的每一个调用hashCode方法必须产生相同的整数结果。
     * <li>It is <em>not</em> required that if two objects are unequal
     *     according to the {@link java.lang.Object#equals(java.lang.Object)}
     *     method, then calling the {@code hashCode} method on each of the
     *     two objects must produce distinct integer results.  However, the
     *     programmer should be aware that producing distinct integer results
     *     for unequal objects may improve the performance of hash tables.
     * </ul>
     *     3)如果根据 java.lang.Object.equals(java.lang.Object)方法，不要求两个对象不相等，
     *     则在每个对象上调用hashCode方法,两个对象必须产生不同的整数结果。
     *     但是，程序员应该意识到为不相等的对象生成不同的整数结果可能会提高哈希表的性能。
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java&trade; programming language.)
     * 3.尽管合理实用，类Object定义的 hashCode 方法确实为不同的对象返回不同的整数。
     * （这通常是通过将对象的内部地址转换为整数来实现的，但是 Java™ 编程语言不需要这种实现技术。）
     * @return  a hash code value for this object.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see     java.lang.System#identityHashCode
     */
    public native int hashCode();

    /**
     * Indicates whether some other object is "equal to" this one.
     * 1.指示其他某个对象是否“等于”这个对象
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * 2.equals方法在非空对象引用上实现等价关系:
     *   1)它是反身的的,对于任何非空引用值 x，x.equals(x)应该返回 true。
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     *     {@code x}, {@code x.equals(x)} should return
     *     {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     *     {@code x} and {@code y}, {@code x.equals(y)}
     *     should return {@code true} if and only if
     *     {@code y.equals(x)} returns {@code true}.
     *     2)对称：对于任何非空引用值x和y，当且仅当x.equals(y)应该返回true,y.equals(x)返回true。
     * <li>It is <i>transitive</i>: for any non-null reference values
     *     {@code x}, {@code y}, and {@code z}, if
     *     {@code x.equals(y)} returns {@code true} and
     *     {@code y.equals(z)} returns {@code true}, then
     *     {@code x.equals(z)} should return {@code true}.
     *     3)传递：对于任何非空引用值x、y和 z，如果 x.equals(y)返回 true和y.equals(z)返回true，
     *     那么x.equals(z)应该返回 true。
     * <li>It is <i>consistent</i>: for any non-null reference values
     *     {@code x} and {@code y}, multiple invocations of
     *     {@code x.equals(y)} consistently return {@code true}
     *     or consistently return {@code false}, provided no
     *     information used in {@code equals} comparisons on the
     *     objects is modified.
     *     4)一致：对于任何非空引用值x和y，多次调用x.equals(y)始终返回true或始终返回 false，
     *     前提是未修改对象的equals比较中使用的信息
     * <li>For any non-null reference value {@code x},
     *     {@code x.equals(null)} should return {@code false}.
     *     3.对于任何非空引用值x，x.equals(null) 应该返回 false
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     *  4.Object类的equals方法在对象上实现了最具辨别力的可能等价关系；也就是说，
     *  对于任何非空引用值x和y，当且仅当x和 y引用同一个对象时，此方法才返回true （ x == y的值为 true）
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     * 5.请注意，每当此方法被覆盖时，通常都需要覆盖hashCode方法，以维护hashCode方法的一般约定，
     * 即相等的对象必须具有相同的哈希码。
     * @param   obj   the reference object with which to compare.
     * @return  {@code true} if this object is the same as the obj
     *          argument; {@code false} otherwise.
     * @see     #hashCode()
     * @see     java.util.HashMap
     */
    public boolean equals(Object obj) {
        return (this == obj);
    }

    /**
     * Creates and returns a copy of this object.  The precise meaning
     * of "copy" may depend on the class of the object.
     * 1.创建并返回此对象的副本。 “复制”的确切含义可能取决于对象的类别
     * The general
     * intent is that, for any object {@code x}, the expression:
     * 2.一般意图是，对于任何对象x，表达式:x.clone() != x将是真的，并且表达式：
     * x.clone().getClass() == x.getClass()将是 true，但这些不是绝对的要求
     *
     * <blockquote>
     * <pre>
     * x.clone() != x</pre></blockquote>
     * will be true, and that the expression:
     * <blockquote>
     * <pre>
     * x.clone().getClass() == x.getClass()</pre></blockquote>
     * will be {@code true}, but these are not absolute requirements.
     * While it is typically the case that:
     * <blockquote>
     * <pre>
     * x.clone().equals(x)</pre></blockquote>
     * will be {@code true}, this is not an absolute requirement.
     * <p>
     * By convention, the returned object should be obtained by calling
     * {@code super.clone}.  If a class and all of its superclasses (except
     * {@code Object}) obey this convention, it will be the case that
     * {@code x.clone().getClass() == x.getClass()}.
     * 3.按照惯例，返回的对象应该通过调用super.clone来获取。如果一个类和它的所有超类（Object除外）都遵守这个约定，
     * 那么 x.clone().getClass() == x.getClass()就是这种情况。
     * <p>
     * By convention, the object returned by this method should be independent
     * of this object (which is being cloned).  To achieve this independence,
     * it may be necessary to modify one or more fields of the object returned
     * by {@code super.clone} before returning it.  Typically, this means
     * copying any mutable objects that comprise the internal "deep structure"
     * of the object being cloned and replacing the references to these
     * objects with references to the copies.  If a class contains only
     * primitive fields or references to immutable objects, then it is usually
     * the case that no fields in the object returned by {@code super.clone}
     * need to be modified.
     * 4.按照惯例，这个方法返回的对象应该独立于这个对象（它被克隆）。为了实现这种独立性，
     * 可能需要在返回之前修改super.clone返回的对象的一个或多个字段。
     * 通常，这意味着复制包含被克隆对象的内部“深层结构”的任何可变对象，
     * 并将对这些对象的引用替换为对副本的引用。如果一个类只包含原始字段或对不可变对象的引用，
     * 那么通常情况下，super.clone返回的对象中没有字段需要修改
     * <p>
     * The method {@code clone} for class {@code Object} performs a
     * specific cloning operation. First, if the class of this object does
     * not implement the interface {@code Cloneable}, then a
     * {@code CloneNotSupportedException} is thrown. Note that all arrays
     * are considered to implement the interface {@code Cloneable} and that
     * the return type of the {@code clone} method of an array type {@code T[]}
     * is {@code T[]} where T is any reference or primitive type.
     * Otherwise, this method creates a new instance of the class of this
     * object and initializes all its fields with exactly the contents of
     * the corresponding fields of this object, as if by assignment; the
     * contents of the fields are not themselves cloned. Thus, this method
     * performs a "shallow copy" of this object, not a "deep copy" operation.
     * 5.Object类的方法clone执行特定的克隆操作。首先，如果这个对象的类没有实现接口Cloneable，
     * 则抛出一个CloneNotSupportedException。请注意，所有数组都被认为实现了接口Cloneable，并且数组类型 T[]
     * 的clone方法的返回类型是 T[]，其中 T 是任何引用或原始类型。否则，此方法将创建此对象的类的新实例，
     * 并使用此对象的相应字段的内容来初始化其所有字段，就像通过赋值一样；字段的内容本身不会被克隆。
     * 因此，此方法执行此对象的“浅拷贝”，而不是“深拷贝”操作。
     * <p>
     * The class {@code Object} does not itself implement the interface
     * {@code Cloneable}, so calling the {@code clone} method on an object
     * whose class is {@code Object} will result in throwing an
     * exception at run time.
     * 6.Object类本身并没有实现Cloneable接口，因此在类为Object的对象上调用clone方法会导致在运行时抛出异常
     * @return     a clone of this instance.
     * @throws  CloneNotSupportedException  if the object's class does not
     *               support the {@code Cloneable} interface. Subclasses
     *               that override the {@code clone} method can also
     *               throw this exception to indicate that an instance cannot
     *               be cloned.
     * @see java.lang.Cloneable
     */
    protected native Object clone() throws CloneNotSupportedException;

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * 1.返回对象的字符串表示形式。通常，toString方法返回一个“文本表示”此对象的字符串。
     * 结果应该是一个简洁但信息丰富的表示，易于人们阅读。
     * It is recommended that all subclasses override this method.
     * 2.建议所有子类都覆盖此方法
     * 3.Object类的 toString方法返回一个字符串，该字符串由对象是其实例的类的名称、at-sign 字符 `@'
     * 和无符号的十六进制表示组成对象的哈希码。
     * 4.换句话说，这个方法返回一个等于以下值的字符串：getClass().getName() + '@' + Integer.toHexString(hashCode())
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * Wakes up a single thread that is waiting on this object's
     * monitor. If any threads are waiting on this object, one of them
     * is chosen to be awakened. The choice is arbitrary and occurs at
     * the discretion of the implementation. A thread waits on an object's
     * monitor by calling one of the {@code wait} methods.
     * 1.唤醒在此对象的监视器上等待的单个线程。如果有任何线程正在等待这个对象，则选择其中一个线程被唤醒。
     * 选择是任意的，并由实现自行决定。线程通过调用wait方法之一在对象的监视器上等待。
     * <p>
     * The awakened thread will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened thread will
     * compete in the usual manner with any other threads that might be
     * actively competing to synchronize on this object; for example, the
     * awakened thread enjoys no reliable privilege or disadvantage in being
     * the next thread to lock this object.
     * 2.被唤醒的线程将无法继续，直到当前线程放弃对该对象的锁定。
     * 被唤醒的线程将以通常的方式与可能正在积极竞争以同步该对象的任何其他线程进行竞争；
     * 例如，被唤醒的线程在成为下一个锁定该对象的线程时没有可靠的特权或劣势。
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. A thread becomes the owner of the
     * object's monitor in one of three ways:
     * 3.此方法只能由作为此对象监视器的所有者的线程调用。
     * 线程通过以下三种方式之一成为对象监视器的所有者：
     *  1)通过执行该对象的同步实例方法。
     *  2)通过执行在对象上同步的 synchronized语句的主体。
     *  3)对于Class类型的对象，通过执行该类的同步静态方法
     * <ul>
     * <li>By executing a synchronized instance method of that object.
     * <li>By executing the body of a {@code synchronized} statement
     *     that synchronizes on the object.
     * <li>For objects of type {@code Class,} by executing a
     *     synchronized static method of that class.
     * </ul>
     * <p>
     * Only one thread at a time can own an object's monitor.
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notifyAll()
     * @see        java.lang.Object#wait()
     */
    public final native void notify();

    /**
     * Wakes up all threads that are waiting on this object's monitor. A
     * thread waits on an object's monitor by calling one of the
     * {@code wait} methods.
     * 1.唤醒在此对象监视器上等待的所有线程。线程通过调用 wait方法之一在对象的监视器上等待
     * <p>
     * The awakened threads will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened threads
     * will compete in the usual manner with any other threads that might
     * be actively competing to synchronize on this object; for example,
     * the awakened threads enjoy no reliable privilege or disadvantage in
     * being the next thread to lock this object.
     * 2.被唤醒的线程将无法继续，直到当前线程放弃对该对象的锁定。
     * 被唤醒的线程将以通常的方式与可能正在积极竞争以同步此对象的任何其他线程进行竞争；
     * 例如，被唤醒的线程在成为下一个锁定该对象的线程时不享有可靠的特权或劣势。
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     * 3.此方法只能由作为此对象监视器的所有者的线程调用。有关线程可以成为监视器所有者的方式的描述，请参阅 notify 方法。
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#wait()
     */
    public final native void notifyAll();

    /**
     * Causes the current thread to wait until either another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or a
     * specified amount of time has elapsed.
     * 1.导致当前线程等待，直到另一个线程为此对象调用 java.lang.Objectnotify()方法或
     * java.lang.ObjectnotifyAll()方法，或者指定的时间已经过去.
     * <p>
     * The current thread must own this object's monitor.
     * 2.当前线程必须拥有此对象的监视器。
     * <p>
     * This method causes the current thread (call it <var>T</var>) to
     * place itself in the wait set for this object and then to relinquish
     * any and all synchronization claims on this object.
     * 3.此方法使当前线程（称为T）将自己置于此对象的等待集中，然后放弃对该对象的任何和所有同步声明。
     *
     * Thread <var>T</var>
     * becomes disabled for thread scheduling purposes and lies dormant
     * until one of four things happens:
     * 4.线程T出于线程调度目的而被禁用并处于休眠状态，直到发生以下四种情况之一：
     *  1)某些其他线程为此对象调用 notify方法，并且线程 T恰好被任意选择为要唤醒的线程
     * <ul>
     * <li>Some other thread invokes the {@code notify} method for this
     * object and thread <var>T</var> happens to be arbitrarily chosen as
     * the thread to be awakened.
     * <li>Some other thread invokes the {@code notifyAll} method for this
     * object.
     * 2)其他一些线程为此对象调用 notifyAll 方法。
     * <li>Some other thread {@linkplain Thread#interrupt() interrupts}
     * thread <var>T</var>.
     * 3)其他一些线程 Threadinterrupt() 中断线程 T
     * <li>The specified amount of real time has elapsed, more or less.  If
     * {@code timeout} is zero, however, then real time is not taken into
     * consideration and the thread simply waits until notified.
     * 4)或多或少已经过了指定的实时时间。但是，如果 timeout为零，则不考虑实时时间，线程只是等待直到收到通知
     * </ul>
     * The thread <var>T</var> is then removed from the wait set for this
     * object and re-enabled for thread scheduling. It then competes in the
     * usual manner with other threads for the right to synchronize on the
     * object; once it has gained control of the object, all its
     * synchronization claims on the object are restored to the status quo
     * ante - that is, to the situation as of the time that the {@code wait}
     * method was invoked. Thread <var>T</var> then returns from the
     * invocation of the {@code wait} method. Thus, on return from the
     * {@code wait} method, the synchronization state of the object and of
     * thread {@code T} is exactly as it was when the {@code wait} method
     * was invoked.
     * 5.线程 T 然后从这个对象的等待集中移除，并重新启用线程调度。然后它以通常的方式与其他线程竞争在对象上同步的权利；
     * 一旦它获得了对象的控制权，它对对象的所有同步声明都将恢复到之前的状态——也就是说，恢复到调用 wait方法时的情况。
     * 线程 T然后从 wait方法的调用中返回。因此，从 wait方法返回时，对象和线程T的同步状态与调用wait法时完全相同
     * <p>
     * A thread can also wake up without being notified, interrupted, or
     * timing out, a so-called <i>spurious wakeup</i>.  While this will rarely
     * occur in practice, applications must guard against it by testing for
     * the condition that should have caused the thread to be awakened, and
     * continuing to wait if the condition is not satisfied.
     * 6.线程也可以在没有被通知、中断或超时的情况下唤醒，即所谓的虚假唤醒。
     * 虽然这在实践中很少发生，但应用程序必须通过测试应该导致线程被唤醒的条件来防止它，如果条件不满足则继续等待
     *
     * In other words,
     * waits should always occur in loops, like this one:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * (For more information on this topic, see Section 3.2.3 in Doug Lea's
     * "Concurrent Programming in Java (Second Edition)" (Addison-Wesley,
     * 2000), or Item 50 in Joshua Bloch's "Effective Java Programming
     * Language Guide" (Addison-Wesley, 2001).
     * ps:有关此主题的更多信息，请参阅 Doug Lea 的“Java 中的并发编程（第二版）
     * <p>If the current thread is {@linkplain java.lang.Thread#interrupt()
     * interrupted} by any thread before or while it is waiting, then an
     * {@code InterruptedException} is thrown.  This exception is not
     * thrown until the lock status of this object has been restored as
     * described above.
     * 7.如果当前线程在等待之前或期间被任何线程java.lang.Threadinterrupt() 中断，
     * 则抛出InterruptedException。在如上所述恢复此对象的锁定状态之前，不会抛出此异常
     * <p>
     * Note that the {@code wait} method, as it places the current thread
     * into the wait set for this object, unlocks only this object; any
     * other objects on which the current thread may be synchronized remain
     * locked while the thread waits.
     * 8.请注意，wait方法将当前线程放入此对象的等待集中，因此仅解锁此对象；当前线程可能同步的任何其他对象在线程等待时保持锁定
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     * 9.此方法只能由作为此对象监视器的所有者的线程调用。有关线程可以成为监视器所有者的方式的描述，请参阅 notify方法。
     * @param      timeout   the maximum time to wait in milliseconds.
     * @throws  IllegalArgumentException      if the value of timeout is
     *               negative.
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final native void wait(long timeout) throws InterruptedException;

    /**
     * Causes the current thread to wait until another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or
     * some other thread interrupts the current thread, or a certain
     * amount of real time has elapsed.
     * 1.使当前线程等待，直到另一个线程为此对象调用 java.lang.Objectnotify()方法或 java.lang.ObjectnotifyAll() 方法，
     * 或者其他一些线程中断当前线程，或者经过了一定的实时时间。
     * <p>
     * This method is similar to the {@code wait} method of one
     * argument, but it allows finer control over the amount of time to
     * wait for a notification before giving up.
     * 2.此方法类似于一个参数的 wait 方法，但它允许更好地控制在放弃之前等待通知的时间量。
     * 实时量，以纳秒为单位，由下式给出：1000000timeout+nanos
     * The amount of real time,
     * measured in nanoseconds, is given by:
     * <blockquote>
     * <pre>
     * 1000000*timeout+nanos</pre></blockquote>
     * <p>
     * In all other respects, this method does the same thing as the
     * method {@link #wait(long)} of one argument. In particular,
     * {@code wait(0, 0)} means the same thing as {@code wait(0)}.
     * 3.在所有其他方面，此方法与一个参数的方法  wait(long)执行相同的操作。
     * 特别是，wait(0, 0)与 wait(0) 的含义相同
     * <p>
     * The current thread must own this object's monitor. The thread
     * releases ownership of this monitor and waits until either of the
     * following two conditions has occurred:
     * <ul>
     * <li>Another thread notifies threads waiting on this object's monitor
     *     to wake up either through a call to the {@code notify} method
     *     or the {@code notifyAll} method.
     * <li>The timeout period, specified by {@code timeout}
     *     milliseconds plus {@code nanos} nanoseconds arguments, has
     *     elapsed.
     * </ul>
     * <p>
     *     4.当前线程必须拥有此对象的监视器。线程释放此监视器的所有权并等待，直到发生以下两个条件之一：
     *     另一个线程通知在此对象监视器上等待的线程通过调用 notify法或notifyAll方法。
     *     由 timeout毫秒加上nanos纳秒参数指定的超时时间已经过去。
     * The thread then waits until it can re-obtain ownership of the
     * monitor and resumes execution.
     * 5.然后线程等待直到它可以重新获得监视器的所有权并恢复执行。
     * <p>
     * As in the one argument version, interrupts and spurious wakeups are
     * possible, and this method should always be used in a loop:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout, nanos);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     * 6.此方法只能由作为此对象监视器的所有者的线程调用。有关线程可以成为监视器所有者的方式的描述，请参阅 notify方法。
     * @param      timeout   the maximum time to wait in milliseconds.
     * @param      nanos      additional time, in nanoseconds range
     *                       0-999999.
     * @throws  IllegalArgumentException      if the value of timeout is
     *                      negative or the value of nanos is
     *                      not in the range 0-999999.
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     */
    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException(
                                "nanosecond timeout value out of range");
        }

        if (nanos > 0) {
            timeout++;
        }

        wait(timeout);
    }

    /**
     * Causes the current thread to wait until another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object.
     * In other words, this method behaves exactly as if it simply
     * performs the call {@code wait(0)}.
     * 1.使当前线程等待，直到另一个线程为此对象调用java.lang.Object.notify()方法或 java.lang.Object.notifyAll()方法。
     * 换句话说，此方法的行为就像它只是执行调用wait(0)一样。
     * <p>
     * The current thread must own this object's monitor. The thread
     * releases ownership of this monitor and waits until another thread
     * notifies threads waiting on this object's monitor to wake up
     * either through a call to the {@code notify} method or the
     * {@code notifyAll} method. The thread then waits until it can
     * re-obtain ownership of the monitor and resumes execution.
     * 2.当前线程必须拥有此对象的监视器。线程释放此监视器的所有权并等待，
     * 直到另一个线程通知在此对象监视器上等待的线程通过调用 notify方法或notifyAll方法唤醒。
     * 然后线程等待直到它可以重新获得监视器的所有权并恢复执行。
     * <p>
     * As in the one argument version, interrupts and spurious wakeups are
     * possible, and this method should always be used in a loop:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait();
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     * 3.此方法只能由作为此对象监视器的所有者的线程调用。有关线程可以成为监视器所有者的方式的描述，请参阅 notify方法。
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final void wait() throws InterruptedException {
        wait(0);
    }

    /**
     * Called by the garbage collector on an object when garbage collection
     * determines that there are no more references to the object.
     * A subclass overrides the {@code finalize} method to dispose of
     * system resources or to perform other cleanup.
     * 1.当垃圾收集确定不再有对对象的引用时，由垃圾收集器在对象上调用。子类覆盖finalize方法来处理系统资源或执行其他清理。
     * <p>
     * The general contract of {@code finalize} is that it is invoked
     * if and when the Java&trade; virtual
     * machine has determined that there is no longer any
     * means by which this object can be accessed by any thread that has
     * not yet died, except as a result of an action taken by the
     * finalization of some other object or class which is ready to be
     * finalized. The {@code finalize} method may take any action, including
     * making this object available again to other threads; the usual purpose
     * of {@code finalize}, however, is to perform cleanup actions before
     * the object is irrevocably discarded. For example, the finalize method
     * for an object that represents an input/output connection might perform
     * explicit I/O transactions to break the connection before the object is
     * permanently discarded.
     * 2.finalize的一般约定是，当 Java™ 虚拟机确定不再有任何方法可以让任何尚未死的线程访问此对象时调用它，
     * 除非作为某个其他准备好被终结的对象或类的终结所采取的行动的结果。 finalize方法可以执行任何操作，
     * 包括使该对象再次可供其他线程使用；然而，finalize的通常目的是在对象被不可撤销地丢弃之前执行清理操作。
     * 例如，表示输入输出连接的对象的 finalize 方法可能会执行显式 IO 事务以在对象被永久丢弃之前断开连接
     * <p>
     * The {@code finalize} method of class {@code Object} performs no
     * special action; it simply returns normally. Subclasses of
     * {@code Object} may override this definition.
     * 3.Object类的 finalize方法不执行任何特殊操作；它只是正常返回。 Object的子类可能会覆盖此定义。
     * <p>
     * The Java programming language does not guarantee which thread will
     * invoke the {@code finalize} method for any given object. It is
     * guaranteed, however, that the thread that invokes finalize will not
     * be holding any user-visible synchronization locks when finalize is
     * invoked. If an uncaught exception is thrown by the finalize method,
     * the exception is ignored and finalization of that object terminates.
     * 4.Java 编程语言不保证哪个线程将调用任何给定对象的finalize方法。
     * 但是，可以保证调用 finalize 的线程在调用 finalize 时不会持有任何用户可见的同步锁。
     * 如果 finalize 方法抛出未捕获的异常，则忽略该异常并终止该对象的终结。
     * <p>
     * After the {@code finalize} method has been invoked for an object, no
     * further action is taken until the Java virtual machine has again
     * determined that there is no longer any means by which this object can
     * be accessed by any thread that has not yet died, including possible
     * actions by other objects or classes which are ready to be finalized,
     * at which point the object may be discarded.
     * 5.在为一个对象调用了finalize方法后，不会采取进一步的行动，
     * 直到 Java 虚拟机再次确定没有任何方法可以让任何尚未死亡的线程访问该对象，
     * 包括准备好完成的其他对象或类的可能操作，此时该对象可能会被丢弃。
     * <p>
     * The {@code finalize} method is never invoked more than once by a Java
     * virtual machine for any given object.
     * 6.finalize方法永远不会被 Java 虚拟机对任何给定对象调用多次
     * <p>
     * Any exception thrown by the {@code finalize} method causes
     * the finalization of this object to be halted, but is otherwise
     * ignored.
     * 7.finalize方法抛出的任何异常都会导致该对象的终结被暂停，但会被忽略。
     * @throws Throwable the {@code Exception} raised by this method
     * @see java.lang.ref.WeakReference
     * @see java.lang.ref.PhantomReference
     * @jls 12.6 Finalization of Class Instances
     */
    protected void finalize() throws Throwable { }
}
