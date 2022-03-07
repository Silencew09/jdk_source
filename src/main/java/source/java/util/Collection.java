/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.util;

import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The root interface in the <i>collection hierarchy</i>.  A collection
 * represents a group of objects, known as its <i>elements</i>.  Some
 * collections allow duplicate elements and others do not.  Some are ordered
 * and others unordered.  The JDK does not provide any <i>direct</i>
 * implementations of this interface: it provides implementations of more
 * specific subinterfaces like <tt>Set</tt> and <tt>List</tt>.  This interface
 * is typically used to pass collections around and manipulate them where
 * maximum generality is desired.
 * 1.集合层次结构中的根接口。一个集合代表一组对象，称为它的元素。一些集合允许重复元素，而另一些则不允许。
 * 有些是有序的，有些是无序的。 JDK 不提供此接口的任何direct实现：它提供了更具体的子接口的实现，
 * 例如Set和List。此接口通常用于传递集合并在需要最大通用性的地方操作它们
 * <p><i>Bags</i> or <i>multisets</i> (unordered collections that may contain
 * duplicate elements) should implement this interface directly.
 * 2.Bags或multisets（可能包含重复元素的无序集合）应该直接实现这个接口
 * <p>All general-purpose <tt>Collection</tt> implementation classes (which
 * typically implement <tt>Collection</tt> indirectly through one of its
 * subinterfaces) should provide two "standard" constructors: a void (no
 * arguments) constructor, which creates an empty collection, and a
 * constructor with a single argument of type <tt>Collection</tt>, which
 * creates a new collection with the same elements as its argument.  In
 * effect, the latter constructor allows the user to copy any collection,
 * producing an equivalent collection of the desired implementation type.
 * There is no way to enforce this convention (as interfaces cannot contain
 * constructors) but all of the general-purpose <tt>Collection</tt>
 * implementations in the Java platform libraries comply.
 * 3.所有通用Collection 实现类（通常通过其子接口之一间接实现Collection）都应该提供两个“标准”构造函数：
 * 一个 void（无参数）构造函数，它创建一个空集合，以及一个带有Collection类型参数的构造函数，
 * 它创建一个与参数相同的元素的新集合。
 * 实际上，后一个构造函数允许用户复制任何集合，生成所需实现类型的等效集合。
 * 没有办法强制执行此约定（因为接口不能包含构造函数），但 Java 平台库中的所有通用Collection实现都符合
 * <p>The "destructive" methods contained in this interface, that is, the
 * methods that modify the collection on which they operate, are specified to
 * throw <tt>UnsupportedOperationException</tt> if this collection does not
 * support the operation.  If this is the case, these methods may, but are not
 * required to, throw an <tt>UnsupportedOperationException</tt> if the
 * invocation would have no effect on the collection.  For example, invoking
 * the {@link #addAll(Collection)} method on an unmodifiable collection may,
 * but is not required to, throw the exception if the collection to be added
 * is empty.
 * 4.此接口中包含的“破坏性”方法，即修改它们操作的集合的方法，如果此集合不支持该操作，
 * 则指定为抛出UnsupportedOperationException。在这种情况下，如果调用对集合没有影响，
 * 则这些方法可能（但不是必需）抛出UnsupportedOperationException。
 * 例如，如果要添加的集合为空，则在不可修改的集合上调用addAll(Collection)方法可能（但不是必须）抛出异常
 * <p><a name="optional-restrictions">
 * Some collection implementations have restrictions on the elements that
 * they may contain.</a>  For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements.  Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * <tt>NullPointerException</tt> or <tt>ClassCastException</tt>.  Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter.  More generally, attempting an
 * operation on an ineligible element whose completion would not result in
 * the insertion of an ineligible element into the collection may throw an
 * exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface.
 * 5.一些集合实现对它们可能包含的元素有限制。例如，一些实现禁止空元素，而一些实现对其元素的类型有限制。
 * 尝试添加不合格的元素会引发未经检查的异常，通常是NullPointerException或ClassCastException。
 * 尝试查询不合格元素的存在可能会引发异常，或者可能只是返回 false；一些实现会表现出前一种行为，
 * 而另一些会表现出后者。更一般地，尝试对不合格元素执行操作，其完成不会导致将不合格元素插入到集合中，
 * 这可能会引发异常，也可能会成功，具体取决于实现的选择。在此接口的规范中，此类异常被标记为“可选”
 * <p>It is up to each collection to determine its own synchronization
 * policy.  In the absence of a stronger guarantee by the
 * implementation, undefined behavior may result from the invocation
 * of any method on a collection that is being mutated by another
 * thread; this includes direct invocations, passing the collection to
 * a method that might perform invocations, and using an existing
 * iterator to examine the collection.
 * 6.由每个集合来确定自己的同步策略。在没有更强大的实现保证的情况下，
 * 未定义的行为可能是由于在另一个线程正在改变的集合上调用任何方法；
 * 这包括直接调用、将集合传递给可能执行调用的方法以及使用现有迭代器检查集合。
 * <p>Many methods in Collections Framework interfaces are defined in
 * terms of the {@link Object#equals(Object) equals} method.  For example,
 * the specification for the {@link #contains(Object) contains(Object o)}
 * method says: "returns <tt>true</tt> if and only if this collection
 * contains at least one element <tt>e</tt> such that
 * <tt>(o==null ? e==null : o.equals(e))</tt>."  This specification should
 * <i>not</i> be construed to imply that invoking <tt>Collection.contains</tt>
 * with a non-null argument <tt>o</tt> will cause <tt>o.equals(e)</tt> to be
 * invoked for any element <tt>e</tt>.  Implementations are free to implement
 * optimizations whereby the <tt>equals</tt> invocation is avoided, for
 * example, by first comparing the hash codes of the two elements.  (The
 * {@link Object#hashCode()} specification guarantees that two objects with
 * unequal hash codes cannot be equal.)  More generally, implementations of
 * the various Collections Framework interfaces are free to take advantage of
 * the specified behavior of underlying {@link Object} methods wherever the
 * implementor deems it appropriate.
 * 7.Collections Framework 接口中的许多方法都是根据 Object.equals(Object)方法定义的。
 * 例如，contains(Object)方法的规范说：“返回true当且仅当此集合包含至少一个元素e这样
 * (o==null ? e==null : o.equals(e))。”本规范应not被解释为暗示使用非空参数o调用Collection.contains
 * 将导致 o.equals(e)为任何元素e调用。实现可以自由地实现优化，从而避免equals调用，
 * 例如，通过首先比较两个元素的哈希码
 * （Object.hashCode()规范保证哈希码不相等的两个对象不能相等。
 * 更一般地说，各种集合框架接口的实现可以自由地利用底层Object方法的指定行为实施者认为合适的任何地方
 * <p>Some collection operations which perform recursive traversal of the
 * collection may fail with an exception for self-referential instances where
 * the collection directly or indirectly contains itself. This includes the
 * {@code clone()}, {@code equals()}, {@code hashCode()} and {@code toString()}
 * methods. Implementations may optionally handle the self-referential scenario,
 * however most current implementations do not do so.
 * 8.一些对集合执行递归遍历的集合操作可能会失败，但自引用实例除外，其中集合直接或间接包含自身。
 * 这包括clone()、equals()、hashCode()和 toString()方法。实现可以选择性地处理自引用场景，但是大多数当前的实现都没有这样做
 * <p>This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @implSpec
 * The default method implementations (inherited or otherwise) do not apply any
 * synchronization protocol.  If a {@code Collection} implementation has a
 * specific synchronization protocol, then it must override default
 * implementations to apply that protocol.
 * 默认方法实现（继承或以其他方式）不应用任何同步协议。如果 Collection实现具有特定的同步协议，则它必须覆盖默认实现以应用该协议。
 * @param <E> the type of elements in this collection
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see     Set
 * @see     List
 * @see     Map
 * @see     SortedSet
 * @see     SortedMap
 * @see     HashSet
 * @see     TreeSet
 * @see     ArrayList
 * @see     LinkedList
 * @see     Vector
 * @see     Collections
 * @see     Arrays
 * @see     AbstractCollection
 * @since 1.2
 */

public interface Collection<E> extends Iterable<E> {
    // Query Operations

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * 返回此集合中的元素数。如果此集合包含多个Integer.MAX_VALUE元素，则返回Integer.MAX_VALUE
     * @return the number of elements in this collection
     */
    int size();

    /**
     * Returns <tt>true</tt> if this collection contains no elements.
     * 如果此集合不包含元素，则返回true。
     * @return <tt>true</tt> if this collection contains no elements
     */
    boolean isEmpty();

    /**
     * Returns <tt>true</tt> if this collection contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this collection
     * contains at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     * 如果此集合包含指定的元素，则返回true。更正式地说，当且仅当此集合包含至少一个元素e使得(o==null ? e==null : o.equals(e ))
     * @param o element whose presence in this collection is to be tested
     * @return <tt>true</tt> if this collection contains the specified
     *         element
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this collection
     *         (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements
     *         (<a href="#optional-restrictions">optional</a>)
     */
    boolean contains(Object o);

    /**
     * Returns an iterator over the elements in this collection.  There are no
     * guarantees concerning the order in which the elements are returned
     * (unless this collection is an instance of some class that provides a
     * guarantee).
     * 返回此集合中元素的迭代器。没有关于元素返回顺序的保证（除非这个集合是提供保证的某个类的实例）
     * @return an <tt>Iterator</tt> over the elements in this collection
     */
    Iterator<E> iterator();

    /**
     * Returns an array containing all of the elements in this collection.
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     * 1.返回一个包含此集合中所有元素的数组。如果此集合对其迭代器返回其元素的顺序做出任何保证，则此方法必须以相同的顺序返回元素
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.
     * 2.返回的数组将是“安全的”，因为此集合不维护对它的引用。
     * （换句话说，即使此集合由数组支持，此方法也必须分配一个新数组）。因此调用者可以自由修改返回的数组
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     * 3.此方法充当基于数组和基于集合的 API 之间的桥梁
     * @return an array containing all of the elements in this collection
     */
    Object[] toArray();

    /**
     * Returns an array containing all of the elements in this collection;
     * the runtime type of the returned array is that of the specified array.
     * If the collection fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this collection.
     * 1.返回一个包含此集合中所有元素的数组；返回数组的运行时类型是指定数组的类型。
     * 如果集合适合指定的数组，则在其中返回。否则，将使用指定数组的运行时类型和此集合的大小分配一个新数组
     * <p>If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element
     * in the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does
     * not contain any <tt>null</tt> elements.)
     * 2.如果此集合适合指定的数组并有剩余空间（即，该数组的元素比此集合多），则紧跟该集合末尾的数组中的元素将设置为null。
     * （如果调用者知道此集合不包含任何null元素，则这在确定此集合的长度时很有用only。）
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     * 3.如果此集合对其迭代器返回其元素的顺序做出任何保证，则此方法必须以相同的顺序返回元素。
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     * 4.与toArray()方法一样，此方法充当基于数组和基于集合的 API 之间的桥梁。
     * 此外，该方法允许精确控制输出数组的运行时类型，并且在某些情况下可用于节省分配成本。
     * <p>Suppose <tt>x</tt> is a collection known to contain only strings.
     * The following code can be used to dump the collection into a newly
     * allocated array of <tt>String</tt>:
     *
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     * 5.假设x是一个已知只包含字符串的集合。以下代码可用于将集合转储到新分配的String数组中：
     * String[] y = x.toArray(new String[0]);
     * 注意 toArray(new Object[0])与toArray()的功能相同
     * @param <T> the runtime type of the array to contain the collection
     * @param a the array into which the elements of this collection are to be
     *        stored, if it is big enough; otherwise, a new array of the same
     *        runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this collection
     * @throws NullPointerException if the specified array is null
     */
    <T> T[] toArray(T[] a);

    // Modification Operations

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns <tt>true</tt> if this collection changed as a
     * result of the call.  (Returns <tt>false</tt> if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     * 1.确保此集合包含指定的元素（可选操作）。如果此集合因调用而更改，则返回true。
     * （如果此集合不允许重复并且已经包含指定的元素，则返回false。）
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add <tt>null</tt> elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     * 2.支持此操作的集合可能会对可以添加到此集合中的元素设置限制。特别是，一些集合会拒绝添加null元素，
     * 而其他集合会对可能添加的元素类型施加限制。集合类应在其文档中明确指定对可以添加哪些元素的任何限制。
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning <tt>false</tt>).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     * 3.如果集合由于任何原因而拒绝添加特定元素，而不是因为它已经包含该元素，则它必须抛出异常（而不是返回 <tt>false<tt>）。
     * 这保留了在此调用返回后集合始终包含指定元素的不变性
     * @param e element whose presence in this collection is to be ensured
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *         is not supported by this collection
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this collection
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements
     * @throws IllegalArgumentException if some property of the element
     *         prevents it from being added to this collection
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to insertion restrictions
     */
    boolean add(E e);

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if
     * this collection contains one or more such elements.  Returns
     * <tt>true</tt> if this collection contained the specified element (or
     * equivalently, if this collection changed as a result of the call).
     * 从此集合中移除指定元素的单个实例（如果存在）（可选操作）。
     * 更正式地，删除元素e使得(o==null ? e==null : o.equals(e))，如果这个集合包含一个或多个这样的元素。
     * 如果此集合包含指定的元素（或等效地，如果此集合因调用而更改），则返回true。
     * @param o element to be removed from this collection, if present
     * @return <tt>true</tt> if an element was removed as a result of this call
     * @throws ClassCastException if the type of the specified element
     *         is incompatible with this collection
     *         (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *         collection does not permit null elements
     *         (<a href="#optional-restrictions">optional</a>)
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *         is not supported by this collection
     */
    boolean remove(Object o);


    // Bulk Operations

    /**
     * Returns <tt>true</tt> if this collection contains all of the elements
     * in the specified collection.
     * 如果此集合包含指定集合中的所有元素，则返回true
     * @param  c collection to be checked for containment in this collection
     * @return <tt>true</tt> if this collection contains all of the elements
     *         in the specified collection
     * @throws ClassCastException if the types of one or more elements
     *         in the specified collection are incompatible with this
     *         collection
     *         (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection contains one
     *         or more null elements and this collection does not permit null
     *         elements
     *         (<a href="#optional-restrictions">optional</a>),
     *         or if the specified collection is null.
     * @see    #contains(Object)
     */
    boolean containsAll(Collection<?> c);

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is
     * nonempty.)
     * 将指定集合中的所有元素添加到此集合（可选操作）。
     * 如果在操作进行时修改了指定的集合，则此操作的行为未定义。
     * （这意味着如果指定的集合是这个集合，并且这个集合是非空的，那么这个调用的行为是未定义的。）
     * @param c collection containing elements to be added to this collection
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *         is not supported by this collection
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this collection
     * @throws NullPointerException if the specified collection contains a
     *         null element and this collection does not permit null elements,
     *         or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this
     *         collection
     * @throws IllegalStateException if not all the elements can be added at
     *         this time due to insertion restrictions
     * @see #add(Object)
     */
    boolean addAll(Collection<? extends E> c);

    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     * 删除也包含在指定集合中的所有此集合的元素（可选操作）。此调用返回后，此集合将不包含与指定集合相同的元素
     * @param c collection containing elements to be removed from this collection
     * @return <tt>true</tt> if this collection changed as a result of the
     *         call
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
     *         is not supported by this collection
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection
     *         (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not support
     *         null elements
     *         (<a href="#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean removeAll(Collection<?> c);

    /**
     * Removes all of the elements of this collection that satisfy the given
     * predicate.  Errors or runtime exceptions thrown during iteration or by
     * the predicate are relayed to the caller.
     * 1.删除此集合中满足给定谓词的所有元素。在迭代期间或由谓词引发的错误或运行时异常被中继到调用者
     * @implSpec
     * The default implementation traverses all elements of the collection using
     * its {@link #iterator}.  Each matching element is removed using
     * {@link Iterator#remove()}.  If the collection's iterator does not
     * support removal then an {@code UnsupportedOperationException} will be
     * thrown on the first matching element.
     * 2.默认实现使用其迭代器遍历集合的所有元素。使用Iterator.remove()删除每个匹配的元素。
     * 如果集合的迭代器不支持删除，则将在第一个匹配元素上抛出UnsupportedOperationException
     * @param filter a predicate which returns {@code true} for elements to be
     *        removed
     * @return {@code true} if any elements were removed
     * @throws NullPointerException if the specified filter is null
     * @throws UnsupportedOperationException if elements cannot be removed
     *         from this collection.  Implementations may throw this exception if a
     *         matching element cannot be removed or if, in general, removal is not
     *         supported.
     * @since 1.8
     */
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     * 仅保留此集合中包含在指定集合中的元素（可选操作）。换句话说，从该集合中删除所有未包含在指定集合中的元素。
     * @param c collection containing elements to be retained in this collection
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
     *         is not supported by this collection
     * @throws ClassCastException if the types of one or more elements
     *         in this collection are incompatible with the specified
     *         collection
     *         (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException if this collection contains one or more
     *         null elements and the specified collection does not permit null
     *         elements
     *         (<a href="#optional-restrictions">optional</a>),
     *         or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    boolean retainAll(Collection<?> c);

    /**
     * Removes all of the elements from this collection (optional operation).
     * The collection will be empty after this method returns.
     * 从此集合中删除所有元素（可选操作）。此方法返回后，集合将为空
     * @throws UnsupportedOperationException if the <tt>clear</tt> operation
     *         is not supported by this collection
     */
    void clear();


    // Comparison and hashing

    /**
     * Compares the specified object with this collection for equality. <p>
     * 1.比较指定的对象与此集合是否相等
     * While the <tt>Collection</tt> interface adds no stipulations to the
     * general contract for the <tt>Object.equals</tt>, programmers who
     * implement the <tt>Collection</tt> interface "directly" (in other words,
     * create a class that is a <tt>Collection</tt> but is not a <tt>Set</tt>
     * or a <tt>List</tt>) must exercise care if they choose to override the
     * <tt>Object.equals</tt>.  It is not necessary to do so, and the simplest
     * course of action is to rely on <tt>Object</tt>'s implementation, but
     * the implementor may wish to implement a "value comparison" in place of
     * the default "reference comparison."  (The <tt>List</tt> and
     * <tt>Set</tt> interfaces mandate such value comparisons.)<p>
     * 2.虽然Collection接口没有为Object.equals的一般契约添加任何规定，
     * 但实现Collection接口的程序员“直接”（换句话说，创建一个类是Collection但不是Set或List)
     * 如果他们选择覆盖Object.equals必须小心.没有必要这样做，最简单的做法是依赖Object的实现，
     * 但实现者可能希望实现“值比较”来代替默认的“引用比较”。 ” （List和Set接口要求进行这样的值比较。）
     * The general contract for the <tt>Object.equals</tt> method states that
     * equals must be symmetric (in other words, <tt>a.equals(b)</tt> if and
     * only if <tt>b.equals(a)</tt>).  The contracts for <tt>List.equals</tt>
     * and <tt>Set.equals</tt> state that lists are only equal to other lists,
     * and sets to other sets.  Thus, a custom <tt>equals</tt> method for a
     * collection class that implements neither the <tt>List</tt> nor
     * <tt>Set</tt> interface must return <tt>false</tt> when this collection
     * is compared to any list or set.  (By the same logic, it is not possible
     * to write a class that correctly implements both the <tt>Set</tt> and
     * <tt>List</tt> interfaces.)
     * 3.Object.equals方法的一般契约规定，equals 必须是对称的（换句话说，a.equals(b)当且仅当b.equals(a ))。
     * List.equals和Set.equals的契约声明列表仅等于其他列表，并设置为其他集合。
     * 因此，当比较此集合时，既不实现List也没有实现 Set接口的集合类的自定义equals 方法必须返回 <tt>false<tt>到任何列表或集合。 （根据相同的逻辑，不可能编写一个正确实现 <tt>Set<tt> 和 <tt>List<tt> 接口的类。）
     * @param o object to be compared for equality with this collection
     * @return <tt>true</tt> if the specified object is equal to this
     * collection
     *
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     * @see List#equals(Object)
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this collection.  While the
     * <tt>Collection</tt> interface adds no stipulations to the general
     * contract for the <tt>Object.hashCode</tt> method, programmers should
     * take note that any class that overrides the <tt>Object.equals</tt>
     * method must also override the <tt>Object.hashCode</tt> method in order
     * to satisfy the general contract for the <tt>Object.hashCode</tt> method.
     * In particular, <tt>c1.equals(c2)</tt> implies that
     * <tt>c1.hashCode()==c2.hashCode()</tt>.
     * 返回此集合的哈希码值。虽然Collection接口没有为Object.hashCode方法的一般约定添加任何规定，
     * 但程序员应注意任何重写Object.equals方法的类还必须重写Object.hashCode方法以满足Object.hashCode方法的一般约定。
     * 特别是，c1.equals(c2)意味着 c1.hashCode()==c2.hashCode()
     * @return the hash code value for this collection
     *
     * @see Object#hashCode()
     * @see Object#equals(Object)
     */
    int hashCode();

    /**
     * Creates a {@link Spliterator} over the elements in this collection.
     * 1.在此集合中的元素上创建一个Spliterator
     * Implementations should document characteristic values reported by the
     * spliterator.  Such characteristic values are not required to be reported
     * if the spliterator reports {@link Spliterator#SIZED} and this collection
     * contains no elements.
     * 2.实现应该记录分离器报告的特征值。如果拆分器报告SpliteratorSIZED并且此集合不包含任何元素，则不需要报告此类特征值
     * <p>The default implementation should be overridden by subclasses that
     * can return a more efficient spliterator.  In order to
     * preserve expected laziness behavior for the {@link #stream()} and
     * {@link #parallelStream()}} methods, spliterators should either have the
     * characteristic of {@code IMMUTABLE} or {@code CONCURRENT}, or be
     * <em><a href="Spliterator.html#binding">late-binding</a></em>.
     * If none of these is practical, the overriding class should describe the
     * spliterator's documented policy of binding and structural interference,
     * and should override the {@link #stream()} and {@link #parallelStream()}
     * methods to create streams using a {@code Supplier} of the spliterator,
     * as in:
     * <pre>{@code
     *     Stream<E> s = StreamSupport.stream(() -> spliterator(), spliteratorCharacteristics)
     * }</pre>
     * <p>These requirements ensure that streams produced by the
     * {@link #stream()} and {@link #parallelStream()} methods will reflect the
     * contents of the collection as of initiation of the terminal stream
     * operation.
     * 3.默认实现应该被可以返回更有效拆分器的子类覆盖。为了保留stream()和parallelStream()方法的预期惰性行为，
     * 拆分器应该具有IMMUTABLE或CONCURRENT的特性，或者是后期绑定。如果这些都不可行，
     * 则覆盖类应描述拆分器的绑定和结构干扰的记录策略，并应覆盖stream()和parallelStream()方法以使用创建流拆分器的供应商，
     * 如：Stream<E> s = StreamSupport.stream(() -> spliterator(), spliteratorCharacteristics)
     * 这些要求确保由stream()和 parallelStream()方法将反映集合的内容作为终端流操作的启动。
     * @implSpec
     * The default implementation creates a
     * <em><a href="Spliterator.html#binding">late-binding</a></em> spliterator
     * from the collections's {@code Iterator}.  The spliterator inherits the
     * <em>fail-fast</em> properties of the collection's iterator.
     * <p>
     * The created {@code Spliterator} reports {@link Spliterator#SIZED}.
     * 4.默认实现从集合的Iterator创建一个late-binding拆分器。拆分器继承了集合迭代器的fail-fast属性。
     * 创建的Spliterator报告SpliteratorSIZED
     * @implNote
     * The created {@code Spliterator} additionally reports
     * {@link Spliterator#SUBSIZED}.
     * 5.创建的Spliterator还报告SpliteratorSUBSIZED
     * <p>If a spliterator covers no elements then the reporting of additional
     * characteristic values, beyond that of {@code SIZED} and {@code SUBSIZED},
     * does not aid clients to control, specialize or simplify computation.
     * However, this does enable shared use of an immutable and empty
     * spliterator instance (see {@link Spliterators#emptySpliterator()}) for
     * empty collections, and enables clients to determine if such a spliterator
     * covers no elements.
     * 6.如果拆分器不包含任何元素，那么报告的附加特征值，除了SIZED和SUBSIZED之外，并不能帮助客户控制、专门化或简化计算。
     * 但是，这确实允许对空集合共享使用不可变和空的拆分器实例（请参阅Spliterators.emptySpliterator()），
     * 并使客户端能够确定这样的拆分器是否不包含任何元素。
     * @return a {@code Spliterator} over the elements in this collection
     * @since 1.8
     */
    @Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, 0);
    }

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     * 1.返回以此集合为源的顺序Stream。
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     * 2.当 spliterator()方法无法返回IMMUTABLE、 CONCURRENT或late-binding的拆分器时，应覆盖此方法。
     * （有关详细信息，请参阅 spliterator()。）
     * @implSpec
     * The default implementation creates a sequential {@code Stream} from the
     * collection's {@code Spliterator}.
     * 3.默认实现从集合的 Spliterator创建一个顺序 Stream。
     * @return a sequential {@code Stream} over the elements in this collection
     * @since 1.8
     */
    default Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Returns a possibly parallel {@code Stream} with this collection as its
     * source.  It is allowable for this method to return a sequential stream.
     * 1.返回一个可能并行的Stream ，并以此集合为源。此方法允许返回顺序流
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     * 2.当 spliterator()方法无法返回IMMUTABLE、CONCURRENT或late-binding的拆分器时，应覆盖此方法。
     * （有关详细信息，请参阅 spliterator()。）
     * @implSpec
     * The default implementation creates a parallel {@code Stream} from the
     * collection's {@code Spliterator}.
     * 3.默认实现从集合的Spliterator创建并行 Stream
     * @return a possibly parallel {@code Stream} over the elements in this
     * collection
     * @since 1.8
     */
    default Stream<E> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }
}
