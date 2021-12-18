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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Cache mapping pairs of {@code (key, sub-key) -> value}. Keys and values are
 * weakly but sub-keys are strongly referenced.  Keys are passed directly to
 * {@link #get} method which also takes a {@code parameter}. Sub-keys are
 * calculated from keys and parameters using the {@code subKeyFactory} function
 * passed to the constructor. Values are calculated from keys and parameters
 * using the {@code valueFactory} function passed to the constructor.
 * Keys can be {@code null} and are compared by identity while sub-keys returned by
 * {@code subKeyFactory} or values returned by {@code valueFactory}
 * can not be null. Sub-keys are compared using their {@link #equals} method.
 * Entries are expunged from cache lazily on each invocation to {@link #get},
 * {@link #containsValue} or {@link #size} methods when the WeakReferences to
 * keys are cleared. Cleared WeakReferences to individual values don't cause
 * expunging, but such entries are logically treated as non-existent and
 * trigger re-evaluation of {@code valueFactory} on request for their
 * key/subKey.
 * 缓存 (key, sub-key) -> value的映射对。键和值是弱引用，但子键被强引用。
 * 密钥直接传递给 get方法，该方法也采用 (parameter)参数。子键是使用传递给构造函数的 subKeyFactory
 * 函数从键和参数计算出来的。使用传递给构造函数的valueFactory函数根据键和参数计算值。
 * 键可以为 null并按标识进行比较，而 subKeyFactory返回的子键或 valueFactory返回的值不能为 null。
 * 子键使用它们的equals方法进行比较。当清除键的 WeakReferences 时，条目会在每次调用get、containsValue
 * 或size方法时从缓存中延迟删除。清除对单个值的 WeakReferences 不会导致清除，但此类条目在逻辑上被视为不存在，
 * 并会在请求其 keysubKey 时触发对valueFactory的重新评估
 * @author Peter Levart
 * @param <K> type of keys
 * @param <P> type of parameters
 * @param <V> type of values
 */
final class WeakCache<K, P, V> {

    private final ReferenceQueue<K> refQueue
        = new ReferenceQueue<>();
    // the key type is Object for supporting null key
    //键类型是支持空键的对象
    private final ConcurrentMap<Object, ConcurrentMap<Object, Supplier<V>>> map
        = new ConcurrentHashMap<>();
    private final ConcurrentMap<Supplier<V>, Boolean> reverseMap
        = new ConcurrentHashMap<>();
    private final BiFunction<K, P, ?> subKeyFactory;
    private final BiFunction<K, P, V> valueFactory;

    /**
     * Construct an instance of {@code WeakCache}
     * 构造一个WeakCache的实例
     * @param subKeyFactory a function mapping a pair of
     *                      {@code (key, parameter) -> sub-key}
     * @param valueFactory  a function mapping a pair of
     *                      {@code (key, parameter) -> value}
     * @throws NullPointerException if {@code subKeyFactory} or
     *                              {@code valueFactory} is null.
     */
    public WeakCache(BiFunction<K, P, ?> subKeyFactory,
                     BiFunction<K, P, V> valueFactory) {
        this.subKeyFactory = Objects.requireNonNull(subKeyFactory);
        this.valueFactory = Objects.requireNonNull(valueFactory);
    }

    /**
     * Look-up the value through the cache. This always evaluates the
     * {@code subKeyFactory} function and optionally evaluates
     * {@code valueFactory} function if there is no entry in the cache for given
     * pair of (key, subKey) or the entry has already been cleared.
     * 通过缓存查找值。这总是评估subKeyFactory函数，如果给定的 (key, subKey)
     * 对缓存中没有条目或者条目已经被清除，则可选地评估valueFactory函数
     * @param key       possibly null key
     * @param parameter parameter used together with key to create sub-key and
     *                  value (should not be null)
     * @return the cached value (never null)
     * @throws NullPointerException if {@code parameter} passed in or
     *                              {@code sub-key} calculated by
     *                              {@code subKeyFactory} or {@code value}
     *                              calculated by {@code valueFactory} is null.
     */
    public V get(K key, P parameter) {
        Objects.requireNonNull(parameter);

        expungeStaleEntries();

        Object cacheKey = CacheKey.valueOf(key, refQueue);

        // lazily install the 2nd level valuesMap for the particular cacheKey
        //为特定的 cacheKey 懒惰地安装第二级 valuesMap
        ConcurrentMap<Object, Supplier<V>> valuesMap = map.get(cacheKey);
        if (valuesMap == null) {
            ConcurrentMap<Object, Supplier<V>> oldValuesMap
                = map.putIfAbsent(cacheKey,
                                  valuesMap = new ConcurrentHashMap<>());
            if (oldValuesMap != null) {
                valuesMap = oldValuesMap;
            }
        }

        // create subKey and retrieve the possible Supplier<V> stored by that
        // subKey from valuesMap
        //创建 subKey 并从 valuesMap 中检索该 subKey 存储的可能的 Supplier
        Object subKey = Objects.requireNonNull(subKeyFactory.apply(key, parameter));
        Supplier<V> supplier = valuesMap.get(subKey);
        Factory factory = null;

        while (true) {
            if (supplier != null) {
                // supplier might be a Factory or a CacheValue<V> instance
                //供应商可能是 Factory 或 CacheValue实例
                V value = supplier.get();
                if (value != null) {
                    return value;
                }
            }
            // else no supplier in cache
            // or a supplier that returned null (could be a cleared CacheValue
            // or a Factory that wasn't successful in installing the CacheValue)
            //否则缓存中没有供应商或返回 null 的供应商（可能是清除的 CacheValue 或未成功安装 CacheValue 的工厂）

            // lazily construct a Factory
            //懒惰地构建一个工厂
            if (factory == null) {
                factory = new Factory(key, parameter, subKey, valuesMap);
            }

            if (supplier == null) {
                supplier = valuesMap.putIfAbsent(subKey, factory);
                if (supplier == null) {
                    // successfully installed Factory
                    supplier = factory;
                }
                // else retry with winning supplier
            } else {
                if (valuesMap.replace(subKey, supplier, factory)) {
                    // successfully replaced
                    // cleared CacheEntry / unsuccessful Factory
                    // with our Factory
                    supplier = factory;
                } else {
                    // retry with current supplier
                    supplier = valuesMap.get(subKey);
                }
            }
        }
    }

    /**
     * Checks whether the specified non-null value is already present in this
     * {@code WeakCache}. The check is made using identity comparison regardless
     * of whether value's class overrides {@link Object#equals} or not.
     * 检查此WeakCache中是否已存在指定的非空值。无论值的类是否覆盖Object.equals，都会使用身份比较进行检查
     * @param value the non-null value to check
     * @return true if given {@code value} is already cached
     * @throws NullPointerException if value is null
     */
    public boolean containsValue(V value) {
        Objects.requireNonNull(value);

        expungeStaleEntries();
        return reverseMap.containsKey(new LookupValue<>(value));
    }

    /**
     * Returns the current number of cached entries that
     * can decrease over time when keys/values are GC-ed.
     * 返回当前缓存条目的数量，当键值被 GC 处理时，这些条目会随着时间的推移而减少
     */
    public int size() {
        expungeStaleEntries();
        return reverseMap.size();
    }

    private void expungeStaleEntries() {
        CacheKey<K> cacheKey;
        while ((cacheKey = (CacheKey<K>)refQueue.poll()) != null) {
            cacheKey.expungeFrom(map, reverseMap);
        }
    }

    /**
     * A factory {@link Supplier} that implements the lazy synchronized
     * construction of the value and installment of it into the cache.
     * 一个工厂 Supplier，它实现了值的延迟同步构造并将其安装到缓存中
     */
    private final class Factory implements Supplier<V> {

        private final K key;
        private final P parameter;
        private final Object subKey;
        private final ConcurrentMap<Object, Supplier<V>> valuesMap;

        Factory(K key, P parameter, Object subKey,
                ConcurrentMap<Object, Supplier<V>> valuesMap) {
            this.key = key;
            this.parameter = parameter;
            this.subKey = subKey;
            this.valuesMap = valuesMap;
        }

        @Override
        public synchronized V get() { // serialize access 序列化访问
            // re-check
            Supplier<V> supplier = valuesMap.get(subKey);
            if (supplier != this) {
                // something changed while we were waiting:
                // might be that we were replaced by a CacheValue
                // or were removed because of failure ->
                // return null to signal WeakCache.get() to retry
                // the loop
                //在我们等待时发生了一些变化：可能是我们被 CacheValue 替换了或者因为失败而被移除
                // -> 返回 null 以指示 WeakCache.get() 重试循环
                return null;
            }
            // else still us (supplier == this) 否则还是我们（供应商==这个）

            // create new value
            V value = null;
            try {
                value = Objects.requireNonNull(valueFactory.apply(key, parameter));
            } finally {
                if (value == null) { // remove us on failure 失败时删除我们
                    valuesMap.remove(subKey, this);
                }
            }
            // the only path to reach here is with non-null value 到达这里的唯一路径是非空值
            assert value != null;

            // wrap value with CacheValue (WeakReference) 用 CacheValue (WeakReference) 包装值
            CacheValue<V> cacheValue = new CacheValue<>(value);

            // try replacing us with CacheValue (this should always succeed)
            //尝试用 CacheValue 替换我们（这应该总是成功
            if (valuesMap.replace(subKey, this, cacheValue)) {
                // put also in reverseMap
                reverseMap.put(cacheValue, Boolean.TRUE);
            } else {
                throw new AssertionError("Should not reach here");
            }

            // successfully replaced us with new CacheValue -> return the value
            // wrapped by it
            //成功地用新的 CacheValue 替换了我们 -> 返回由它包装的值
            return value;
        }
    }

    /**
     * Common type of value suppliers that are holding a referent.
     * The {@link #equals} and {@link #hashCode} of implementations is defined
     * to compare the referent by identity.
     * 持有参照物的常见价值供应商类型。实现的equals和hashCode被定义为通过身份比较所指对象。
     */
    private interface Value<V> extends Supplier<V> {}

    /**
     * An optimized {@link Value} used to look-up the value in
     * {@link WeakCache#containsValue} method so that we are not
     * constructing the whole {@link CacheValue} just to look-up the referent.
     * 优化的 Value用于在WeakCache.containsValue方法中查找值，
     * 这样我们就不会构建整个 CacheValue只是为了查找所指对象。
     */
    private static final class LookupValue<V> implements Value<V> {
        private final V value;

        LookupValue(V value) {
            this.value = value;
        }

        @Override
        public V get() {
            return value;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(value); // compare by identity 按身份比较
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this ||
                   obj instanceof Value &&
                   this.value == ((Value<?>) obj).get();  // compare by identity
        }
    }

    /**
     * A {@link Value} that weakly references the referent.
     * 弱引用所指对象的 Value。
     */
    private static final class CacheValue<V>
        extends WeakReference<V> implements Value<V>
    {
        private final int hash;

        CacheValue(V value) {
            super(value);
            this.hash = System.identityHashCode(value); // compare by identity
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            V value;
            return obj == this ||
                   obj instanceof Value &&
                   // cleared CacheValue is only equal to itself 清除的 CacheValue 只等于它自己
                   (value = get()) != null &&
                   value == ((Value<?>) obj).get(); // compare by identity
        }
    }

    /**
     * CacheKey containing a weakly referenced {@code key}. It registers
     * itself with the {@code refQueue} so that it can be used to expunge
     * the entry when the {@link WeakReference} is cleared.
     * CacheKey 包含弱引用的key。它将自己注册到refQueue，
     * 以便在WeakReference被清除时可以使用它来清除条目
     */
    private static final class CacheKey<K> extends WeakReference<K> {

        // a replacement for null keys
        //空键的替代品
        private static final Object NULL_KEY = new Object();

        static <K> Object valueOf(K key, ReferenceQueue<K> refQueue) {
            return key == null
                   // null key means we can't weakly reference it,
                   // so we use a NULL_KEY singleton as cache key
                    //null 键意味着我们不能弱引用它，所以我们使用 NULL_KEY 单例作为缓存键
                   ? NULL_KEY
                   // non-null key requires wrapping with a WeakReference
                    //非空键需要用 WeakReference 包装
                   : new CacheKey<>(key, refQueue);
        }

        private final int hash;

        private CacheKey(K key, ReferenceQueue<K> refQueue) {
            super(key, refQueue);
            this.hash = System.identityHashCode(key);  // compare by identity
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            K key;
            return obj == this ||
                   obj != null &&
                   obj.getClass() == this.getClass() &&
                   // cleared CacheKey is only equal to itself
                   (key = this.get()) != null &&
                   // compare key by identity
                   key == ((CacheKey<K>) obj).get();
        }

        void expungeFrom(ConcurrentMap<?, ? extends ConcurrentMap<?, ?>> map,
                         ConcurrentMap<?, Boolean> reverseMap) {
            // removing just by key is always safe here because after a CacheKey
            // is cleared and enqueue-ed it is only equal to itself
            // (see equals method)...
            //仅通过键删除在这里总是安全的，因为在清除 CacheKey 并入队后，它仅等于自身（请参阅 equals 方法）...
            ConcurrentMap<?, ?> valuesMap = map.remove(this);
            // remove also from reverseMap if needed
            //如果需要，也从 reverseMap 中删除
            if (valuesMap != null) {
                for (Object cacheValue : valuesMap.values()) {
                    reverseMap.remove(cacheValue);
                }
            }
        }
    }
}
