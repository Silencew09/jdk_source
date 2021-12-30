/*
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

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util;

/**
 * This class provides skeletal implementations of some {@link Queue}
 * operations. The implementations in this class are appropriate when
 * the base implementation does <em>not</em> allow <tt>null</tt>
 * elements.  Methods {@link #add add}, {@link #remove remove}, and
 * {@link #element element} are based on {@link #offer offer}, {@link
 * #poll poll}, and {@link #peek peek}, respectively, but throw
 * exceptions instead of indicating failure via <tt>false</tt> or
 * <tt>null</tt> returns.
 * 1.此类提供了一些Queue操作的骨架实现。当基本实现不允许null元素时，此类中的实现是合适的。
 * 方法add、remove和element分别基于offer、poll和peek，
 * 但是抛出异常而不是通过false或null返回指示失败
 * <p>A <tt>Queue</tt> implementation that extends this class must
 * minimally define a method {@link Queue#offer} which does not permit
 * insertion of <tt>null</tt> elements, along with methods {@link
 * Queue#peek}, {@link Queue#poll}, {@link Collection#size}, and
 * {@link Collection#iterator}.  Typically, additional methods will be
 * overridden as well.  If these requirements cannot be met, consider
 * instead subclassing {@link AbstractCollection}.
 * 2.扩展此类的Queue实现必须至少定义一个方法Queue.offer，该方法不允许插入null元素，
 * 以及方法Queue.peek、Queue.poll、Collection.size和Collection.iterator。
 * 通常，其他方法也会被覆盖。如果无法满足这些要求，请考虑子类化AbstractCollection
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public abstract class AbstractQueue<E>
    extends AbstractCollection<E>
    implements Queue<E> {

    /**
     * Constructor for use by subclasses.
     * 供子类使用的构造函数
     */
    protected AbstractQueue() {
    }

    /**
     * Inserts the specified element into this queue if it is possible to do so
     * immediately without violating capacity restrictions, returning
     * <tt>true</tt> upon success and throwing an <tt>IllegalStateException</tt>
     * if no space is currently available.
     * 1.如果可以在不违反容量限制的情况下立即将指定元素插入此队列，
     * 则在成功时返回true并在当前没有可用空间时抛出IllegalStateException
     * <p>This implementation returns <tt>true</tt> if <tt>offer</tt> succeeds,
     * else throws an <tt>IllegalStateException</tt>.
     * 如果offer成功，此实现返回true，否则抛出IllegalStateException
     * @param e the element to add
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     * @throws IllegalStateException if the element cannot be added at this
     *         time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element
     *         prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null and
     *         this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     *         prevents it from being added to this queue
     */
    public boolean add(E e) {
        if (offer(e))
            return true;
        else
            throw new IllegalStateException("Queue full");
    }

    /**
     * Retrieves and removes the head of this queue.  This method differs
     * from {@link #poll poll} only in that it throws an exception if this
     * queue is empty.
     * 1.检索并删除此队列的头部。此方法与poll的不同之处仅在于，如果此队列为空，它会引发异常
     * <p>This implementation returns the result of <tt>poll</tt>
     * unless the queue is empty.
     *2.此实现返回 poll的结果，除非队列为空
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    public E remove() {
        E x = poll();
        if (x != null)
            return x;
        else
            throw new NoSuchElementException();
    }

    /**
     * Retrieves, but does not remove, the head of this queue.  This method
     * differs from {@link #peek peek} only in that it throws an exception if
     * this queue is empty.
     * 1.检索但不删除此队列的头部。此方法与peek的不同之处仅在于，如果此队列为空，它会引发异常
     * <p>This implementation returns the result of <tt>peek</tt>
     * unless the queue is empty.
     * 2.除非队列为空，否则此实现将返回peek的结果
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    public E element() {
        E x = peek();
        if (x != null)
            return x;
        else
            throw new NoSuchElementException();
    }

    /**
     * Removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     * 1.从此队列中移除所有元素。此调用返回后，队列将为空
     * <p>This implementation repeatedly invokes {@link #poll poll} until it
     * returns <tt>null</tt>.
     * 2.这个实现重复调用poll直到它返回null
     */
    public void clear() {
        while (poll() != null)
            ;
    }

    /**
     * Adds all of the elements in the specified collection to this
     * queue.  Attempts to addAll of a queue to itself result in
     * <tt>IllegalArgumentException</tt>. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     * 1.将指定集合中的所有元素添加到此队列。尝试将队列的所有内容添加到自身会导致
     * IllegalArgumentException。此外，如果在操作进行时修改了指定的集合，则此操作的行为是未定义的
     * <p>This implementation iterates over the specified collection,
     * and adds each element returned by the iterator to this
     * queue, in turn.  A runtime exception encountered while
     * trying to add an element (including, in particular, a
     * <tt>null</tt> element) may result in only some of the elements
     * having been successfully added when the associated exception is
     * thrown.
     * 2.此实现迭代指定的集合，并将迭代器返回的每个元素依次添加到此队列中。
     * 尝试添加元素（尤其包括null元素）时遇到的运行时异常可能会导致在引发相关异常时仅成功添加了某些元素
     * @param c collection containing elements to be added to this queue
     * @return <tt>true</tt> if this queue changed as a result of the call
     * @throws ClassCastException if the class of an element of the specified
     *         collection prevents it from being added to this queue
     * @throws NullPointerException if the specified collection contains a
     *         null element and this queue does not permit null elements,
     *         or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     *         specified collection prevents it from being added to this
     *         queue, or if the specified collection is this queue
     * @throws IllegalStateException if not all the elements can be added at
     *         this time due to insertion restrictions
     * @see #add(Object)
     */
    public boolean addAll(Collection<? extends E> c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
    }

}
