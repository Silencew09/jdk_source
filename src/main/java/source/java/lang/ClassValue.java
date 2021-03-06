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

import java.lang.ClassValue.ClassValueMap;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.ClassValue.ClassValueMap.probeHomeLocation;
import static java.lang.ClassValue.ClassValueMap.probeBackupLocations;

/**
 * Lazily associate a computed value with (potentially) every type.
 * For example, if a dynamic language needs to construct a message dispatch
 * table for each class encountered at a message send call site,
 * it can use a {@code ClassValue} to cache information needed to
 * perform the message send quickly, for each class encountered.
 * 延迟地将计算值与（可能）每种类型相关联。
 * 例如，如果动态语言需要为在消息发送调用站点遇到的每个类构建一个消息调度表，
 * 它可以使用 ClassValue来缓存为遇到的每个类快速执行消息发送所需的信息。
 * @author John Rose, JSR 292 EG
 * @since 1.7
 */
public abstract class ClassValue<T> {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     * 唯一的构造函数。 （对于子类构造函数的调用，通常是隐式的。）
     */
    protected ClassValue() {
    }

    /**
     * Computes the given class's derived value for this {@code ClassValue}.
     * 1.计算此 {@code ClassValue} 的给定类的派生值
     * <p>
     * This method will be invoked within the first thread that accesses
     * the value with the {@link #get get} method.
     * 2.此方法将在使用 get 方法访问该值的第一个线程中调用
     * <p>
     * Normally, this method is invoked at most once per class,
     * but it may be invoked again if there has been a call to
     * {@link #remove remove}.
     * 3.通常，每个类最多调用一次此方法，但如果已调用 remove,则可能会再次调用它
     * <p>
     * If this method throws an exception, the corresponding call to {@code get}
     * will terminate abnormally with that exception, and no class value will be recorded.
     * 4.如果此方法抛出异常，相应的get调用将因该异常异常终止，并且不会记录任何类值
     * @param type the type whose class value must be computed
     * @return the newly computed value associated with this {@code ClassValue}, for the given class or interface
     * @see #get
     * @see #remove
     */
    protected abstract T computeValue(Class<?> type);

    /**
     * Returns the value for the given class.
     * If no value has yet been computed, it is obtained by
     * an invocation of the {@link #computeValue computeValue} method.
     * 1.返回给定类的值。如果尚未计算任何值，则通过调用computeValue方法获得
     * <p>
     * The actual installation of the value on the class
     * is performed atomically.
     * 2.类上值的实际安装是原子执行的。
     * At that point, if several racing threads have
     * computed values, one is chosen, and returned to
     * all the racing threads.
     * 3.在这一点上，如果多个竞速线程有计算值，则选择一个，并将其返回给所有竞速线程
     * <p>
     * The {@code type} parameter is typically a class, but it may be any type,
     * such as an interface, a primitive type (like {@code int.class}), or {@code void.class}.
     * <p>
     * 4.参数通常是一个类，但它可以是任何类型，例如接口、原始类型（如 int.class）或void.class
     * In the absence of {@code remove} calls, a class value has a simple
     * state diagram:  uninitialized and initialized.
     * When {@code remove} calls are made,
     * the rules for value observation are more complex.
     * See the documentation for {@link #remove remove} for more information.
     * 5.type参数通常是一个类，但它可以是任何类型，例如接口、原始类型（如 int.class或 void.class
     * @param type the type whose class value must be computed or retrieved
     * @return the current value associated with this {@code ClassValue}, for the given class or interface
     * @throws NullPointerException if the argument is null
     * @see #remove
     * @see #computeValue
     */
    public T get(Class<?> type) {
        // non-racing this.hashCodeForCache : final int
        Entry<?>[] cache;
        Entry<T> e = probeHomeLocation(cache = getCacheCarefully(type), this);
        // racing e : current value <=> stale value from current cache or from stale cache
        //当前值 <=> 来自当前缓存或陈旧缓存的陈旧值
        // invariant:  e is null or an Entry with readable Entry.version and Entry.value
        //e 为 null 或具有可读 Entry.version 和 Entry.value 的条目
        if (match(e))
            // invariant:  No false positive matches.  False negatives are OK if rare.
            // The key fact that makes this work: if this.version == e.version,
            // then this thread has a right to observe (final) e.value.
            return e.value();
        // The fast path can fail for any of these reasons:
        // 1. no entry has been computed yet
        // 2. hash code collision (before or after reduction mod cache.length)
        // 3. an entry has been removed (either on this type or another)
        // 4. the GC has somehow managed to delete e.version and clear the reference
        //快速路径可能因以下任何原因而失败：
        // 1. 尚未计算任何条目
        // 2. 哈希码冲突（减少 mod cache.length 之前或之后）
        // 3. 条目已被删除（在此类型或其他类型上）
        // 4 . GC 以某种方式设法删除了 e.version 并清除了引用
        return getFromBackup(cache, type);
    }

    /**
     * Removes the associated value for the given class.
     * If this value is subsequently {@linkplain #get read} for the same class,
     * its value will be reinitialized by invoking its {@link #computeValue computeValue} method.
     * This may result in an additional invocation of the
     * {@code computeValue} method for the given class.
     * <p>
     * 1.删除给定类的关联值。如果此值随后为同一类 get/read，
     * 则其值将通过调用其 computeValue 方法重新初始化。
     * 这可能会导致对给定类的computeValue方法的额外调用
     * In order to explain the interaction between {@code get} and {@code remove} calls,
     * we must model the state transitions of a class value to take into account
     * the alternation between uninitialized and initialized states.
     * To do this, number these states sequentially from zero, and note that
     * uninitialized (or removed) states are numbered with even numbers,
     * while initialized (or re-initialized) states have odd numbers.
     * 2.为了解释 get和 remove调用之间的交互，我们必须对类值的状态转换进行建模，
     * 以考虑未初始化和初始化状态之间的交替。为此，请从零开始对这些状态进行顺序编号，
     * 并注意未初始化（或删除）状态使用偶数编号，而初始化（或重新初始化）状态使用奇数编号
     * <p>
     * When a thread {@code T} removes a class value in state {@code 2N},
     * nothing happens, since the class value is already uninitialized.
     * Otherwise, the state is advanced atomically to {@code 2N+1}.
     * 3.当线程 T在状态 2N中删除类值时，什么也不会发生，因为类值已经未初始化。
     * 否则，状态将自动推进到2N+1
     * <p>
     * When a thread {@code T} queries a class value in state {@code 2N},
     * the thread first attempts to initialize the class value to state {@code 2N+1}
     * by invoking {@code computeValue} and installing the resulting value.
     * 4.当线程T查询状态2N的类值时，该线程首先尝试通过调用computeValue并安装结果值来将类值初始化为状态2N+1
     * <p>
     * When {@code T} attempts to install the newly computed value,
     * if the state is still at {@code 2N}, the class value will be initialized
     * with the computed value, advancing it to state {@code 2N+1}.
     * 5.当 T尝试安装新计算的值时，如果状态仍为2N，则类值将使用计算值进行初始化，将其推进到状态2N+1
     * <p>
     * Otherwise, whether the new state is even or odd,
     * {@code T} will discard the newly computed value
     * and retry the {@code get} operation.
     * 6.否则，无论新状态是偶数还是奇数，T都会丢弃新计算的值并重试 get 操作
     * <p>
     * Discarding and retrying is an important proviso,
     * since otherwise {@code T} could potentially install
     * a disastrously stale value.
     * 7.丢弃和重试是一个重要的条件，否则 T可能会安装一个灾难性的陈旧值
     * For example:
     * <ul>
     * <li>{@code T} calls {@code CV.get(C)} and sees state {@code 2N}
     * <li>{@code T} quickly computes a time-dependent value {@code V0} and gets ready to install it
     * <li>{@code T} is hit by an unlucky paging or scheduling event, and goes to sleep for a long time
     * <li>...meanwhile, {@code T2} also calls {@code CV.get(C)} and sees state {@code 2N}
     * <li>{@code T2} quickly computes a similar time-dependent value {@code V1} and installs it on {@code CV.get(C)}
     * <li>{@code T2} (or a third thread) then calls {@code CV.remove(C)}, undoing {@code T2}'s work
     * <li> the previous actions of {@code T2} are repeated several times
     * <li> also, the relevant computed values change over time: {@code V1}, {@code V2}, ...
     * <li>...meanwhile, {@code T} wakes up and attempts to install {@code V0}; <em>this must fail</em>
     * </ul>
     * We can assume in the above scenario that {@code CV.computeValue} uses locks to properly
     * observe the time-dependent states as it computes {@code V1}, etc.
     * This does not remove the threat of a stale value, since there is a window of time
     * between the return of {@code computeValue} in {@code T} and the installation
     * of the the new value.  No user synchronization is possible during this time.
     * 8.我们可以假设在上面的场景中CV.computeValue在计算 V1等时使用锁来正确观察依赖于时间的状态。
     * 这并没有消除过时值的威胁，因为有在 T中返回 computeValue和安装新值之间的时间窗口。在此期间无法进行用户同步
     * @param type the type whose class value must be removed
     * @throws NullPointerException if the argument is null
     */
    public void remove(Class<?> type) {
        ClassValueMap map = getMap(type);
        map.removeEntry(this);
    }

    // Possible functionality for JSR 292 MR 1
    /*public*/ void put(Class<?> type, T value) {
        ClassValueMap map = getMap(type);
        map.changeEntry(this, value);
    }

    /// --------
    /// Implementation...
    /// --------

    /** Return the cache, if it exists, else a dummy empty cache. */
    //返回缓存，如果存在，否则返回一个虚拟的空缓存
    private static Entry<?>[] getCacheCarefully(Class<?> type) {
        // racing type.classValueMap{.cacheArray} : null => new Entry[X] <=> new Entry[Y]
        ClassValueMap map = type.classValueMap;
        if (map == null)  return EMPTY_CACHE;
        Entry<?>[] cache = map.getCache();
        return cache;
        // invariant:  returned value is safe to dereference and check for an Entry
        //不变：返回值可以安全地取消引用并检查条目
    }

    /** Initial, one-element, empty cache used by all Class instances.  Must never be filled. */
    //所有 Class 实例使用的初始、单元素、空缓存。绝对不能填满
    private static final Entry<?>[] EMPTY_CACHE = { null };

    /**
     * Slow tail of ClassValue.get to retry at nearby locations in the cache,
     * or take a slow lock and check the hash table.
     * Called only if the first probe was empty or a collision.
     * This is a separate method, so compilers can process it independently.
     * ClassValue.get 的慢尾在缓存中的附近位置重试，或者采取慢速锁定并检查哈希表。
     * 仅在第一个探针为空或发生碰撞时调用。这是一个单独的方法，因此编译器可以独立处理它
     */
    private T getFromBackup(Entry<?>[] cache, Class<?> type) {
        Entry<T> e = probeBackupLocations(cache, this);
        if (e != null)
            return e.value();
        return getFromHashMap(type);
    }

    // Hack to suppress warnings on the (T) cast, which is a no-op.
    @SuppressWarnings("unchecked")
    Entry<T> castEntry(Entry<?> e) { return (Entry<T>) e; }

    /** Called when the fast path of get fails, and cache reprobe also fails.
     * //get 的快速路径失败时调用，缓存 reprobe 也失败时调用
     */
    private T getFromHashMap(Class<?> type) {
        // The fail-safe recovery is to fall back to the underlying classValueMap.
        //故障安全恢复是回退到底层 classValueMap
        ClassValueMap map = getMap(type);
        for (;;) {
            Entry<T> e = map.startEntry(this);
            if (!e.isPromise())
                return e.value();
            try {
                // Try to make a real entry for the promised version.
                //尝试为承诺的版本做一个真实的条目
                e = makeEntry(e.version(), computeValue(type));
            } finally {
                // Whether computeValue throws or returns normally,
                // be sure to remove the empty entry.
                //无论computeValue 是正常抛出还是返回，请务必删除空条目
                e = map.finishEntry(this, e);
            }
            if (e != null)
                return e.value();
            // else try again, in case a racing thread called remove (so e == null)
        }
    }

    /** Check that e is non-null, matches this ClassValue, and is live. */
    //检查 e 是否为非空、是否与此 ClassValue 匹配且是否有效
    boolean match(Entry<?> e) {
        // racing e.version : null (blank) => unique Version token => null (GC-ed version)
        // non-racing this.version : v1 => v2 => ... (updates are read faithfully from volatile)
        return (e != null && e.get() == this.version);
        // invariant:  No false positives on version match.  Null is OK for false negative.
        // invariant:  If version matches, then e.value is readable (final set in Entry.<init>)
    }

    /** Internal hash code for accessing Class.classValueMap.cacheArray. */
    //用于访问 Class.classValueMap.cacheArray 的内部哈希码。
    final int hashCodeForCache = nextHashCode.getAndAdd(HASH_INCREMENT) & HASH_MASK;

    /** Value stream for hashCodeForCache.  See similar structure in ThreadLocal. */
    //hashCodeForCache 的值流。请参阅 ThreadLocal 中的类似结构
    private static final AtomicInteger nextHashCode = new AtomicInteger();

    /** Good for power-of-two tables.  See similar structure in ThreadLocal. */
    //适用于 2 的幂表。请参阅 ThreadLocal 中的类似结构
    private static final int HASH_INCREMENT = 0x61c88647;

    /** Mask a hash code to be positive but not too large, to prevent wraparound. */
    //将哈希码屏蔽为正数但不要太大，以防止回绕。
    static final int HASH_MASK = (-1 >>> 2);

    /**
     * Private key for retrieval of this object from ClassValueMap.
     * //用于从 ClassValueMap 检索此对象的私钥
     */
    static class Identity {
    }
    /**
     * This ClassValue's identity, expressed as an opaque object.
     * The main object {@code ClassValue.this} is incorrect since
     * subclasses may override {@code ClassValue.equals}, which
     * could confuse keys in the ClassValueMap.
     * //这个 ClassValue 的标识，表示为一个不透明的对象。
     * 主对象 ClassValue.this不正确，因为子类可能会覆盖 ClassValue.equals，
     * 这可能会混淆 ClassValueMap 中的键
     */
    final Identity identity = new Identity();

    /**
     * Current version for retrieving this class value from the cache.
     * Any number of computeValue calls can be cached in association with one version.
     * But the version changes when a remove (on any type) is executed.
     * A version change invalidates all cache entries for the affected ClassValue,
     * by marking them as stale.  Stale cache entries do not force another call
     * to computeValue, but they do require a synchronized visit to a backing map.
     * 1.用于从缓存中检索此类值的当前版本
     * 2.可以与一个版本相关联地缓存任意数量的 computeValue 调用，但是当执行删除（在任何类型上）时，版本会发生变化。
     * 3.通过将受影响的 ClassValue 的所有缓存条目标记为过时，版本更改使它们无效
     * 4.陈旧的缓存条目不会强制再次调用计算值，但它们确实需要对支持映射的同步访问
     * <p>
     * All user-visible state changes on the ClassValue take place under
     * a lock inside the synchronized methods of ClassValueMap.
     * Readers (of ClassValue.get) are notified of such state changes
     * when this.version is bumped to a new token.
     * This variable must be volatile so that an unsynchronized reader
     * will receive the notification without delay.
     * 5.ClassValue 上所有用户可见的状态更改都在 ClassValueMap 的同步方法内的锁定下发生
     * 6.当 this.version 遇到一个新的令牌时，（ClassValue.get 的）读者会收到这种状态变化的通知
     * 7.这个变量必须是可变的，以便未同步的读者可以毫不延迟地收到通知
     * <p>
     * If version were not volatile, one thread T1 could persistently hold onto
     * a stale value this.value == V1, while while another thread T2 advances
     * (under a lock) to this.value == V2.  This will typically be harmless,
     * but if T1 and T2 interact causally via some other channel, such that
     * T1's further actions are constrained (in the JMM) to happen after
     * the V2 event, then T1's observation of V1 will be an error.
     * 8.如果版本不是易失性的，一个线程 T1 可以持久地保持一个陈旧的值 this.value == V1，
     * 而另一个线程 T2 前进（在锁定下）到 this.value == V2。这通常是无害的，
     * 但如果 T1 和 T2 通过某个其他渠道因果交互，
     * 使得 T1 的进一步行动被限制（在 JMM 中）发生在 V2 事件之后，
     * 那么 T1 对 V1 的观察将是错误的
     * <p>
     * The practical effect of making this.version be volatile is that it cannot
     * be hoisted out of a loop (by an optimizing JIT) or otherwise cached.
     * Some machines may also require a barrier instruction to execute
     * before this.version.
     * 9.使 this.version 可变的实际效果是它不能被提升出循环（通过优化 JIT）或以其他方式缓存。
     * 有些机器可能还需要在此版本之前执行屏障指令
     */
    private volatile Version<T> version = new Version<>(this);
    Version<T> version() { return version; }
    void bumpVersion() { version = new Version<>(this); }
    static class Version<T> {
        private final ClassValue<T> classValue;
        private final Entry<T> promise = new Entry<>(this);
        Version(ClassValue<T> classValue) { this.classValue = classValue; }
        ClassValue<T> classValue() { return classValue; }
        Entry<T> promise() { return promise; }
        boolean isLive() { return classValue.version() == this; }
    }

    /** One binding of a value to a class via a ClassValue.
     * 1.通过 ClassValue 将一个值绑定到一个类。
     *  States are:<ul>
     *  <li> promise if value == Entry.this
     *  <li> else dead if version == null
     *  <li> else stale if version != classValue.version
     *  <li> else live </ul>
     *  2.状态是：
     *  允许时value = Entry.this
     *  死亡时value= null
     *  陈旧时version!=classValueversion
     *  否则就是活跃状态
     *  Promises are never put into the cache; they only live in the
     *  backing map while a computeValue call is in flight.
     *  Once an entry goes stale, it can be reset at any time
     *  into the dead state.
     *  Promises 永远不会放入缓存；它们只存在于支持映射中，而计算值调用正在进行中。
     *  一旦条目变得陈旧，它可以随时重置为死状态
     */
    static class Entry<T> extends WeakReference<Version<T>> {
        //通常是 T 型，但有时 (Entry)this
        final Object value;  // usually of type T, but sometimes (Entry)this
        Entry(Version<T> version, T value) {
            super(version);
            //对于常规条目，值的类型为 T
            this.value = value;  // for a regular entry, value is of type T
        }
        private void assertNotPromise() { assert(!isPromise()); }
        /** For creating a promise. */
        //promise状态
        Entry(Version<T> version) {
            super(version);
            this.value = this;  // for a promise, value is not of type T, but Entry!
        }
        /** Fetch the value.  This entry must not be a promise. */
        //取值。此条目不能是Promise
        @SuppressWarnings("unchecked")  // if !isPromise, type is T
        T value() { assertNotPromise(); return (T) value; }
        boolean isPromise() { return value == this; }
        Version<T> version() { return get(); }
        ClassValue<T> classValueOrNull() {
            Version<T> v = version();
            return (v == null) ? null : v.classValue();
        }
        boolean isLive() {
            Version<T> v = version();
            if (v == null)  return false;
            if (v.isLive())  return true;
            clear();
            return false;
        }
        Entry<T> refreshVersion(Version<T> v2) {
            assertNotPromise();
            @SuppressWarnings("unchecked")  // if !isPromise, type is T
            Entry<T> e2 = new Entry<>(v2, (T) value);
            clear();
            // value = null -- caller must drop
            return e2;
        }
        //dead状态
        static final Entry<?> DEAD_ENTRY = new Entry<>(null, null);
    }

    /** Return the backing map associated with this type. */
    //返回与此类型关联的支持映射
    private static ClassValueMap getMap(Class<?> type) {
        // racing type.classValueMap : null (blank) => unique ClassValueMap
        // if a null is observed, a map is created (lazily, synchronously, uniquely)
        // all further access to that map is synchronized
        //Race type.classValueMap : null (blank) => unique ClassValueMap
        // 如果观察到 null，则创建一个地图（延迟、同步、唯一）对该地图的所有进一步访问都是同步的
        ClassValueMap map = type.classValueMap;
        if (map != null)  return map;
        return initializeMap(type);
    }

    //避免死锁的私有对象
    private static final Object CRITICAL_SECTION = new Object();
    private static ClassValueMap initializeMap(Class<?> type) {
        ClassValueMap map;
        synchronized (CRITICAL_SECTION) {  // private object to avoid deadlocks
            // happens about once per type
            if ((map = type.classValueMap) == null)
                type.classValueMap = map = new ClassValueMap(type);
        }
            return map;
        }

    static <T> Entry<T> makeEntry(Version<T> explicitVersion, T value) {
        // Note that explicitVersion might be different from this.version.
        //请注意，explicitVersion 可能与 this.version 不同
        return new Entry<>(explicitVersion, value);

        // As soon as the Entry is put into the cache, the value will be
        // reachable via a data race (as defined by the Java Memory Model).
        // This race is benign, assuming the value object itself can be
        // read safely by multiple threads.  This is up to the user.
        //
        // The entry and version fields themselves can be safely read via
        // a race because they are either final or have controlled states.
        // If the pointer from the entry to the version is still null,
        // or if the version goes immediately dead and is nulled out,
        // the reader will take the slow path and retry under a lock.
    }

    // The following class could also be top level and non-public:
    //以下类也可以是顶级和非公开的：

    /** A backing map for all ClassValues, relative a single given type.
     *  Gives a fully serialized "true state" for each pair (ClassValue cv, Class type).
     *  Also manages an unserialized fast-path cache.
     *  所有 ClassValues 的支持映射，相对于单个给定类型。为
     *  每对（ClassValue cv，Class 类型）提供一个完全序列化的“真实状态”。还管理未序列化的快速路径缓存
     */
    static class ClassValueMap extends WeakHashMap<ClassValue.Identity, Entry<?>> {
        private final Class<?> type;
        private Entry<?>[] cacheArray;
        private int cacheLoad, cacheLoadLimit;

        /** Number of entries initially allocated to each type when first used with any ClassValue.
         *  It would be pointless to make this much smaller than the Class and ClassValueMap objects themselves.
         *  Must be a power of 2.
         *  首次与任何 ClassValue 一起使用时，最初分配给每种类型的条目数。
         *  使它比 Class 和 ClassValueMap 对象本身小得多是没有意义的。必须是 2 的幂
         */
        private static final int INITIAL_ENTRIES = 32;

        /** Build a backing map for ClassValues, relative the given type.
         *  Also, create an empty cache array and install it on the class.
         *  为 ClassValues 构建一个支持映射，相对于给定的类型。
         *  此外，创建一个空的缓存数组并将其安装在类上
         */
        ClassValueMap(Class<?> type) {
            this.type = type;
            sizeCache(INITIAL_ENTRIES);
        }

        Entry<?>[] getCache() { return cacheArray; }

        /** Initiate a query.  Store a promise (placeholder) if there is no value yet. */
        //发起查询。如果还没有值，则存储promise（占位符)
        synchronized
        <T> Entry<T> startEntry(ClassValue<T> classValue) {
            @SuppressWarnings("unchecked")  // one map has entries for all value types <T>
            Entry<T> e = (Entry<T>) get(classValue.identity);
            Version<T> v = classValue.version();
            if (e == null) {
                e = v.promise();
                // The presence of a promise means that a value is pending for v.
                // Eventually, finishEntry will overwrite the promise.
                //promise的存在意味着 v 的值正在等待。最终，finishEntry 将覆盖promise
                put(classValue.identity, e);
                // Note that the promise is never entered into the cache!
                //请注意，promise永远不会进入缓存
                return e;
            } else if (e.isPromise()) {
                // Somebody else has asked the same question.
                // Let the races begin!
                if (e.version() != v) {
                    e = v.promise();
                    put(classValue.identity, e);
                }
                return e;
            } else {
                // there is already a completed entry here; report it
                //这里已经有一个完整的条目；举报
                if (e.version() != v) {
                    // There is a stale but valid entry here; make it fresh again.
                    // Once an entry is in the hash table, we don't care what its version is.
                    //这里有一个陈旧但有效的条目；让它再次新鲜。
                    // 一旦一个条目在哈希表中，我们就不关心它的版本是什么
                    e = e.refreshVersion(v);
                    put(classValue.identity, e);
                }
                // Add to the cache, to enable the fast path, next time.
                //添加到缓存中，下次以启用快速路径，
                checkCacheLoad();
                addToCache(classValue, e);
                return e;
            }
        }

        /** Finish a query.  Overwrite a matching placeholder.  Drop stale incoming values. */
        //完成一个查询。覆盖匹配的占位符。删除陈旧的传入值
        synchronized
        <T> Entry<T> finishEntry(ClassValue<T> classValue, Entry<T> e) {
            @SuppressWarnings("unchecked")  // one map has entries for all value types <T>
            Entry<T> e0 = (Entry<T>) get(classValue.identity);
            if (e == e0) {
                // We can get here during exception processing, unwinding from computeValue.
                //我们可以在异常处理期间到达这里，从computeValue 展开
                assert(e.isPromise());
                remove(classValue.identity);
                return null;
            } else if (e0 != null && e0.isPromise() && e0.version() == e.version()) {
                // If e0 matches the intended entry, there has not been a remove call
                // between the previous startEntry and now.  So now overwrite e0.
                //如果 e0 与预期条目匹配，则前一个 startEntry 和现在之间没有删除调用。所以现在覆盖e0
                Version<T> v = classValue.version();
                if (e.version() != v)
                    e = e.refreshVersion(v);
                put(classValue.identity, e);
                // Add to the cache, to enable the fast path, next time.
                //添加到缓存中，以启用快速路径，下次
                checkCacheLoad();
                addToCache(classValue, e);
                return e;
            } else {
                // Some sort of mismatch; caller must try again.
                return null;
            }
        }

        /** Remove an entry. */
        synchronized
        void removeEntry(ClassValue<?> classValue) {
            Entry<?> e = remove(classValue.identity);
            if (e == null) {
                // Uninitialized, and no pending calls to computeValue.  No change.
            } else if (e.isPromise()) {
                // State is uninitialized, with a pending call to finishEntry.
                // Since remove is a no-op in such a state, keep the promise
                // by putting it back into the map.
                put(classValue.identity, e);
            } else {
                // In an initialized state.  Bump forward, and de-initialize.
                classValue.bumpVersion();
                // Make all cache elements for this guy go stale.
                removeStaleEntries(classValue);
            }
        }

        /** Change the value for an entry. */
        synchronized
        <T> void changeEntry(ClassValue<T> classValue, T value) {
            @SuppressWarnings("unchecked")  // one map has entries for all value types <T>
            Entry<T> e0 = (Entry<T>) get(classValue.identity);
            Version<T> version = classValue.version();
            if (e0 != null) {
                if (e0.version() == version && e0.value() == value)
                    // no value change => no version change needed
                    return;
                classValue.bumpVersion();
                removeStaleEntries(classValue);
            }
            Entry<T> e = makeEntry(version, value);
            put(classValue.identity, e);
            // Add to the cache, to enable the fast path, next time.
            checkCacheLoad();
            addToCache(classValue, e);
        }

        /// --------
        /// Cache management.
        /// --------

        // Statics do not need synchronization.

        /** Load the cache entry at the given (hashed) location. */
        static Entry<?> loadFromCache(Entry<?>[] cache, int i) {
            // non-racing cache.length : constant
            // racing cache[i & (mask)] : null <=> Entry
            return cache[i & (cache.length-1)];
            // invariant:  returned value is null or well-constructed (ready to match)
        }

        /** Look in the cache, at the home location for the given ClassValue. */
        static <T> Entry<T> probeHomeLocation(Entry<?>[] cache, ClassValue<T> classValue) {
            return classValue.castEntry(loadFromCache(cache, classValue.hashCodeForCache));
        }

        /** Given that first probe was a collision, retry at nearby locations. */
        //鉴于第一次探测是碰撞，请在附近位置重试
        static <T> Entry<T> probeBackupLocations(Entry<?>[] cache, ClassValue<T> classValue) {
            if (PROBE_LIMIT <= 0)  return null;
            // Probe the cache carefully, in a range of slots.
            //在一系列插槽中仔细探测缓存
            int mask = (cache.length-1);
            int home = (classValue.hashCodeForCache & mask);
            Entry<?> e2 = cache[home];  // victim, if we find the real guy
            if (e2 == null) {
                return null;   // if nobody is at home, no need to search nearby
            }
            // assume !classValue.match(e2), but do not assert, because of races
            int pos2 = -1;
            for (int i = home + 1; i < home + PROBE_LIMIT; i++) {
                Entry<?> e = cache[i & mask];
                if (e == null) {
                    break;   // only search within non-null runs
                }
                if (classValue.match(e)) {
                    // relocate colliding entry e2 (from cache[home]) to first empty slot
                    cache[home] = e;
                    if (pos2 >= 0) {
                        cache[i & mask] = Entry.DEAD_ENTRY;
                    } else {
                        pos2 = i;
                    }
                    cache[pos2 & mask] = ((entryDislocation(cache, pos2, e2) < PROBE_LIMIT)
                                          ? e2                  // put e2 here if it fits
                                          : Entry.DEAD_ENTRY);
                    return classValue.castEntry(e);
                }
                // Remember first empty slot, if any:
                if (!e.isLive() && pos2 < 0)  pos2 = i;
            }
            return null;
        }

        /** How far out of place is e? */
        //e 离位多远？
        private static int entryDislocation(Entry<?>[] cache, int pos, Entry<?> e) {
            ClassValue<?> cv = e.classValueOrNull();
            if (cv == null)  return 0;  // entry is not live!
            int mask = (cache.length-1);
            return (pos - cv.hashCodeForCache) & mask;
        }

        /// --------
        /// Below this line all functions are private, and assume synchronized access.
        /// --------

        private void sizeCache(int length) {
            assert((length & (length-1)) == 0);  // must be power of 2
            cacheLoad = 0;
            cacheLoadLimit = (int) ((double) length * CACHE_LOAD_LIMIT / 100);
            cacheArray = new Entry<?>[length];
        }

        /** Make sure the cache load stays below its limit, if possible. */
        //如果可能，请确保缓存负载保持在其限制以下
        private void checkCacheLoad() {
            if (cacheLoad >= cacheLoadLimit) {
                reduceCacheLoad();
            }
        }
        private void reduceCacheLoad() {
            removeStaleEntries();
            if (cacheLoad < cacheLoadLimit)
                return;  // win
            Entry<?>[] oldCache = getCache();
            if (oldCache.length > HASH_MASK)
                return;  // lose
            sizeCache(oldCache.length * 2);
            for (Entry<?> e : oldCache) {
                if (e != null && e.isLive()) {
                    addToCache(e);
                }
            }
        }

        /** Remove stale entries in the given range.
         *  Should be executed under a Map lock.
         *  删除给定范围内的陈旧条目。应该在地图锁下执行
         */
        private void removeStaleEntries(Entry<?>[] cache, int begin, int count) {
            if (PROBE_LIMIT <= 0)  return;
            int mask = (cache.length-1);
            int removed = 0;
            for (int i = begin; i < begin + count; i++) {
                Entry<?> e = cache[i & mask];
                if (e == null || e.isLive())
                    continue;  // skip null and live entries
                Entry<?> replacement = null;
                if (PROBE_LIMIT > 1) {
                    // avoid breaking up a non-null run
                    //避免中断非空运行
                    replacement = findReplacement(cache, i);
                }
                cache[i & mask] = replacement;
                if (replacement == null)  removed += 1;
            }
            cacheLoad = Math.max(0, cacheLoad - removed);
        }

        /** Clearing a cache slot risks disconnecting following entries
         *  from the head of a non-null run, which would allow them
         *  to be found via reprobes.  Find an entry after cache[begin]
         *  to plug into the hole, or return null if none is needed.
         *  清除缓存槽有可能将后续条目与非空运行的头部断开连接，
         *  这将允许通过重新探测找到它们。在 cache[begin] 之后查找一个条目以插入该漏洞，如果不需要则返回 null
         */
        private Entry<?> findReplacement(Entry<?>[] cache, int home1) {
            Entry<?> replacement = null;
            int haveReplacement = -1, replacementPos = 0;
            int mask = (cache.length-1);
            for (int i2 = home1 + 1; i2 < home1 + PROBE_LIMIT; i2++) {
                Entry<?> e2 = cache[i2 & mask];
                if (e2 == null)  break;  // End of non-null run.
                if (!e2.isLive())  continue;  // Doomed anyway.
                int dis2 = entryDislocation(cache, i2, e2);
                if (dis2 == 0)  continue;  // e2 already optimally placed
                int home2 = i2 - dis2;
                if (home2 <= home1) {
                    // e2 can replace entry at cache[home1]
                    if (home2 == home1) {
                        // Put e2 exactly where he belongs.
                        haveReplacement = 1;
                        replacementPos = i2;
                        replacement = e2;
                    } else if (haveReplacement <= 0) {
                        haveReplacement = 0;
                        replacementPos = i2;
                        replacement = e2;
                    }
                    // And keep going, so we can favor larger dislocations.
                }
            }
            if (haveReplacement >= 0) {
                if (cache[(replacementPos+1) & mask] != null) {
                    // Be conservative, to avoid breaking up a non-null run.
                    cache[replacementPos & mask] = (Entry<?>) Entry.DEAD_ENTRY;
                } else {
                    cache[replacementPos & mask] = null;
                    cacheLoad -= 1;
                }
            }
            return replacement;
        }

        /** Remove stale entries in the range near classValue. */
        //删除 classValue 附近范围内的陈旧条目
        private void removeStaleEntries(ClassValue<?> classValue) {
            removeStaleEntries(getCache(), classValue.hashCodeForCache, PROBE_LIMIT);
        }

        /** Remove all stale entries, everywhere. */
        //删除所有陈旧条目，无处不在
        private void removeStaleEntries() {
            Entry<?>[] cache = getCache();
            removeStaleEntries(cache, 0, cache.length + PROBE_LIMIT - 1);
        }

        /** Add the given entry to the cache, in its home location, unless it is out of date. */
        //将给定条目添加到缓存中，在其主位置，除非它已过期
        private <T> void addToCache(Entry<T> e) {
            ClassValue<T> classValue = e.classValueOrNull();
            if (classValue != null)
                addToCache(classValue, e);
        }

        /** Add the given entry to the cache, in its home location. */
        //将给定的条目添加到缓存中，在其主位置
        private <T> void addToCache(ClassValue<T> classValue, Entry<T> e) {
            if (PROBE_LIMIT <= 0)  return;  // do not fill cache
            // Add e to the cache.
            Entry<?>[] cache = getCache();
            int mask = (cache.length-1);
            int home = classValue.hashCodeForCache & mask;
            Entry<?> e2 = placeInCache(cache, home, e, false);
            if (e2 == null)  return;  // done
            if (PROBE_LIMIT > 1) {
                // try to move e2 somewhere else in his probe range
                int dis2 = entryDislocation(cache, home, e2);
                int home2 = home - dis2;
                for (int i2 = home2; i2 < home2 + PROBE_LIMIT; i2++) {
                    if (placeInCache(cache, i2 & mask, e2, true) == null) {
                        return;
                    }
                }
            }
            // Note:  At this point, e2 is just dropped from the cache.
        }

        /** Store the given entry.  Update cacheLoad, and return any live victim.
         *  'Gently' means return self rather than dislocating a live victim.
         *  存储给定的条目。更新 cacheLoad，并返回任何活着的受害者。 “轻轻”意味着回归自我，而不是让一个活生生的受害者脱臼
         */
        private Entry<?> placeInCache(Entry<?>[] cache, int pos, Entry<?> e, boolean gently) {
            Entry<?> e2 = overwrittenEntry(cache[pos]);
            if (gently && e2 != null) {
                // do not overwrite a live entry
                return e;
            } else {
                cache[pos] = e;
                return e2;
            }
        }

        /** Note an entry that is about to be overwritten.
         *  If it is not live, quietly replace it by null.
         *  If it is an actual null, increment cacheLoad,
         *  because the caller is going to store something
         *  in its place.
         *  请注意即将被覆盖的条目。如果它不是活的，则悄悄地将其替换为空值。
         *  如果它是一个实际的空值，增加 cacheLoad，因为调用者将在它的位置存储一些东西
         */
        private <T> Entry<T> overwrittenEntry(Entry<T> e2) {
            if (e2 == null)  cacheLoad += 1;
            else if (e2.isLive())  return e2;
            return null;
        }

        /** Percent loading of cache before resize. */
        //调整大小前缓存的加载百分比
        private static final int CACHE_LOAD_LIMIT = 67;  // 0..100
        /** Maximum number of probes to attempt. */
        //尝试的最大探测数
        private static final int PROBE_LIMIT      =  6;       // 1..
        // N.B.  Set PROBE_LIMIT=0 to disable all fast paths.
    }
}
