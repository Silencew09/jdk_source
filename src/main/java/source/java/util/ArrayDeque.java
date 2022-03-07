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
 * Written by Josh Bloch of Google Inc. and released to the public domain,
 * as explained at http://creativecommons.org/publicdomain/zero/1.0/.
 */

package java.util;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Resizable-array implementation of the {@link Deque} interface.  Array
 * deques have no capacity restrictions; they grow as necessary to support
 * usage.  They are not thread-safe; in the absence of external
 * synchronization, they do not support concurrent access by multiple threads.
 * Null elements are prohibited.  This class is likely to be faster than
 * {@link Stack} when used as a stack, and faster than {@link LinkedList}
 * when used as a queue.
 * 1.Deque接口的可调整大小的数组实现。数组双端队列没有容量限制；它们根据需要增长以支持使用。
 * 它们不是线程安全的；在没有外部同步的情况下，它们不支持多线程并发访问。
 * 禁止空元素。此类用作堆栈时可能比Stack更快，用作队列时比LinkedList更快
 * <p>Most {@code ArrayDeque} operations run in amortized constant time.
 * Exceptions include {@link #remove(Object) remove}, {@link
 * #removeFirstOccurrence removeFirstOccurrence}, {@link #removeLastOccurrence
 * removeLastOccurrence}, {@link #contains contains}, {@link #iterator
 * iterator.remove()}, and the bulk operations, all of which run in linear
 * time.
 * 2.大多数ArrayDeque操作都在分摊常数时间内运行。例外包括remove(Object)、removeFirstOccurrence
 * 、removeLastOccurrence 、contains、iterator.remove()和批量操作，所有其中以线性时间运行
 * <p>The iterators returned by this class's {@code iterator} method are
 * <i>fail-fast</i>: If the deque is modified at any time after the iterator
 * is created, in any way except through the iterator's own {@code remove}
 * method, the iterator will generally throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 * 3.此类的iterator方法返回的迭代器是fail-fast：如果在迭代器创建后的任何时间修改 deque，
 * 以任何方式修改，除非通过迭代器自己的remove方法，迭代器通常会抛出一个ConcurrentModificationException。
 * 因此，面对并发修改，迭代器快速而干净地失败，而不是冒着在未来不确定的时间出现任意、非确定性行为的风险
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 * 4.请注意，无法保证迭代器的快速失败行为，因为一般而言，在存在非同步并发修改的情况下不可能做出任何硬保证。
 * 快速失败的迭代器会尽最大努力抛出ConcurrentModificationException。
 * 因此，编写一个依赖此异常来确保其正确性的程序是错误的：迭代器的快速失败行为应该仅用于检测错误
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link
 * Iterator} interfaces.
 * 5.此类及其迭代器实现了Collection和Iterator接口的所有optional方法
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch and Doug Lea
 * @since   1.6
 * @param <E> the type of elements held in this collection
 */
public class ArrayDeque<E> extends AbstractCollection<E>
                           implements Deque<E>, Cloneable, Serializable
{
    /**
     * The array in which the elements of the deque are stored.
     * The capacity of the deque is the length of this array, which is
     * always a power of two. The array is never allowed to become
     * full, except transiently within an addX method where it is
     * resized (see doubleCapacity) immediately upon becoming full,
     * thus avoiding head and tail wrapping around to equal each
     * other.  We also guarantee that all array cells not holding
     * deque elements are always null.
     * 存储双端队列元素的数组。 deque 的容量是这个数组的长度，它总是 2 的幂。
     * 数组永远不允许变满，除非在 addX 方法中暂时变满，在变满后立即调整大小（请参阅 doubleCapacity），
     * 从而避免头部和尾部环绕使彼此相等。我们还保证所有不包含双端队列元素的数组单元始终为空
     */
    transient Object[] elements; // non-private to simplify nested class access

    /**
     * The index of the element at the head of the deque (which is the
     * element that would be removed by remove() or pop()); or an
     * arbitrary number equal to tail if the deque is empty.
     * deque 头部元素的索引（即会被 remove() 或 pop() 移除的元素）；如果双端队列为空，则为等于 tail 的任意数字
     */
    transient int head;

    /**
     * The index at which the next element would be added to the tail
     * of the deque (via addLast(E), add(E), or push(E)).
     * 将下一个元素添加到双端队列尾部的索引（通过 addLast(E)、add(E) 或 push(E)）
     */
    transient int tail;

    /**
     * The minimum capacity that we'll use for a newly created deque.
     * Must be a power of 2.
     * 我们将用于新创建的双端队列的最小容量。必须是 2 的幂。
     */
    private static final int MIN_INITIAL_CAPACITY = 8;

    // ******  Array allocation and resizing utilities ******
    //数组分配和调整大小实用程序

    /**
     * Allocates empty array to hold the given number of elements.
     * 分配空数组以保存给定数量的元素
     * @param numElements  the number of elements to hold
     */
    private void allocateElements(int numElements) {
        int initialCapacity = MIN_INITIAL_CAPACITY;
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        //找到两个的最佳幂来保存元素。测试“<=”，因为数组没有保持满
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>>  1);
            initialCapacity |= (initialCapacity >>>  2);
            initialCapacity |= (initialCapacity >>>  4);
            initialCapacity |= (initialCapacity >>>  8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0)   // Too many elements, must back off 元素太多，必须退缩
                initialCapacity >>>= 1;// Good luck allocating 2 ^ 30 elements 祝你好运分配 2 ^ 30 个元素
        }
        elements = new Object[initialCapacity];
    }

    /**
     * Doubles the capacity of this deque.  Call only when full, i.e.,
     * when head and tail have wrapped around to become equal.
     * 将此双端队列的容量加倍。仅在满时调用，即当头部和尾部缠绕变得相等时
     */
    private void doubleCapacity() {
        assert head == tail;
        int p = head;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p p 右侧的元素数
        int newCapacity = n << 1;
        if (newCapacity < 0)
            throw new IllegalStateException("Sorry, deque too big");
        Object[] a = new Object[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = a;
        head = 0;
        tail = n;
    }

    /**
     * Copies the elements from our element array into the specified array,
     * in order (from first to last element in the deque).  It is assumed
     * that the array is large enough to hold all elements in the deque.
     * 按顺序（从双端队列中的第一个元素到最后一个元素）将元素从我们的元素数组复制到指定的数组中。
     * 假设数组足够大以容纳双端队列中的所有元素
     * @return its argument
     */
    private <T> T[] copyElements(T[] a) {
        if (head < tail) {
            System.arraycopy(elements, head, a, 0, size());
        } else if (head > tail) {
            int headPortionLen = elements.length - head;
            System.arraycopy(elements, head, a, 0, headPortionLen);
            System.arraycopy(elements, 0, a, headPortionLen, tail);
        }
        return a;
    }

    /**
     * Constructs an empty array deque with an initial capacity
     * sufficient to hold 16 elements.
     * 构造一个空数组 deque，其初始容量足以容纳 16 个元素
     */
    public ArrayDeque() {
        elements = new Object[16];
    }

    /**
     * Constructs an empty array deque with an initial capacity
     * sufficient to hold the specified number of elements.
     * 构造一个空数组 deque，其初始容量足以容纳指定数量的元素
     * @param numElements  lower bound on initial capacity of the deque
     */
    public ArrayDeque(int numElements) {
        allocateElements(numElements);
    }

    /**
     * Constructs a deque containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.  (The first element returned by the collection's
     * iterator becomes the first element, or <i>front</i> of the
     * deque.)
     * 构造一个包含指定集合的元素的双端队列，按照集合的迭代器返回的顺序。
     * （集合的迭代器返回的第一个元素成为第一个元素，或双端队列的front。）
     * @param c the collection whose elements are to be placed into the deque
     * @throws NullPointerException if the specified collection is null
     */
    public ArrayDeque(Collection<? extends E> c) {
        allocateElements(c.size());
        addAll(c);
    }

    // The main insertion and extraction methods are addFirst,
    // addLast, pollFirst, pollLast. The other methods are defined in
    // terms of these.
    //主要的插入和提取方法有addFirst、addLast、pollFirst、pollLast。其他方法是根据这些定义的

    /**
     * Inserts the specified element at the front of this deque.
     * 在此双端队列的前面插入指定的元素
     * @param e the element to add
     * @throws NullPointerException if the specified element is null
     */
    public void addFirst(E e) {
        if (e == null)
            throw new NullPointerException();
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail)
            doubleCapacity();
    }

    /**
     * Inserts the specified element at the end of this deque.
     * 在此双端队列的末尾插入指定的元素
     * <p>This method is equivalent to {@link #add}.
     *
     * @param e the element to add
     * @throws NullPointerException if the specified element is null
     */
    public void addLast(E e) {
        if (e == null)
            throw new NullPointerException();
        elements[tail] = e;
        if ( (tail = (tail + 1) & (elements.length - 1)) == head)
            doubleCapacity();
    }

    /**
     * Inserts the specified element at the front of this deque.
     * 在此双端队列的前面插入指定的元素
     * @param e the element to add
     * @return {@code true} (as specified by {@link Deque#offerFirst})
     * @throws NullPointerException if the specified element is null
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this deque.
     * 在此双端队列的末尾插入指定的元素
     * @param e the element to add
     * @return {@code true} (as specified by {@link Deque#offerLast})
     * @throws NullPointerException if the specified element is null
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E removeFirst() {
        E x = pollFirst();
        if (x == null)
            throw new NoSuchElementException();
        return x;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E removeLast() {
        E x = pollLast();
        if (x == null)
            throw new NoSuchElementException();
        return x;
    }

    public E pollFirst() {
        int h = head;
        @SuppressWarnings("unchecked")
        E result = (E) elements[h];
        // Element is null if deque empty
        if (result == null)
            return null;
        elements[h] = null;     // Must null out slot
        head = (h + 1) & (elements.length - 1);
        return result;
    }

    public E pollLast() {
        int t = (tail - 1) & (elements.length - 1);
        @SuppressWarnings("unchecked")
        E result = (E) elements[t];
        if (result == null)
            return null;
        elements[t] = null;
        tail = t;
        return result;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E getFirst() {
        @SuppressWarnings("unchecked")
        E result = (E) elements[head];
        if (result == null)
            throw new NoSuchElementException();
        return result;
    }

    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E getLast() {
        @SuppressWarnings("unchecked")
        E result = (E) elements[(tail - 1) & (elements.length - 1)];
        if (result == null)
            throw new NoSuchElementException();
        return result;
    }

    @SuppressWarnings("unchecked")
    public E peekFirst() {
        // elements[head] is null if deque empty
        return (E) elements[head];
    }

    @SuppressWarnings("unchecked")
    public E peekLast() {
        return (E) elements[(tail - 1) & (elements.length - 1)];
    }

    /**
     * Removes the first occurrence of the specified element in this
     * deque (when traversing the deque from head to tail).
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the first element {@code e} such that
     * {@code o.equals(e)} (if such an element exists).
     * Returns {@code true} if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     * 删除此双端队列中第一次出现的指定元素（从头到尾遍历双端队列时）。
     * 如果 deque 不包含该元素，则它保持不变。
     * 更正式地，删除第一个元素e使得o.equals(e)（如果存在这样的元素）。
     * 如果此双端队列包含指定的元素（或等效地，如果此双端队列因调用而更改），则返回true
     * @param o element to be removed from this deque, if present
     * @return {@code true} if the deque contained the specified element
     */
    public boolean removeFirstOccurrence(Object o) {
        if (o == null)
            return false;
        int mask = elements.length - 1;
        int i = head;
        Object x;
        while ( (x = elements[i]) != null) {
            if (o.equals(x)) {
                delete(i);
                return true;
            }
            i = (i + 1) & mask;
        }
        return false;
    }

    /**
     * Removes the last occurrence of the specified element in this
     * deque (when traversing the deque from head to tail).
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the last element {@code e} such that
     * {@code o.equals(e)} (if such an element exists).
     * Returns {@code true} if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     * 删除此双端队列中最后一次出现的指定元素（从头到尾遍历双端队列时）。
     * 如果 deque 不包含该元素，则它保持不变。更正式地，删除最后一个元素e使得o.equals(e)（如果存在这样的元素）。
     * 如果此双端队列包含指定的元素（或等效地，如果此双端队列因调用而更改），则返回true
     * @param o element to be removed from this deque, if present
     * @return {@code true} if the deque contained the specified element
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null)
            return false;
        int mask = elements.length - 1;
        int i = (tail - 1) & mask;
        Object x;
        while ( (x = elements[i]) != null) {
            if (o.equals(x)) {
                delete(i);
                return true;
            }
            i = (i - 1) & mask;
        }
        return false;
    }

    // *** Queue methods ***

    /**
     * Inserts the specified element at the end of this deque.
     * 在此双端队列的末尾插入指定的元素。
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e the element to add
     * @return {@code true} (as specified by {@link Collection#add})
     * @throws NullPointerException if the specified element is null
     */
    public boolean add(E e) {
        addLast(e);
        return true;
    }

    /**
     * Inserts the specified element at the end of this deque.
     * 在此双端队列的末尾插入指定的元素
     * <p>This method is equivalent to {@link #offerLast}.
     * 此方法等效于offerLast
     * @param e the element to add
     * @return {@code true} (as specified by {@link Queue#offer})
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        return offerLast(e);
    }

    /**
     * Retrieves and removes the head of the queue represented by this deque.
     * 1.检索并删除此双端队列表示的队列的头部
     * This method differs from {@link #poll poll} only in that it throws an
     * exception if this deque is empty.
     * 2.此方法与poll的不同之处仅在于，如果此双端队列为空，则会引发异常
     * <p>This method is equivalent to {@link #removeFirst}.
     * 3.这个方法相当于removeFirst。
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E remove() {
        return removeFirst();
    }

    /**
     * Retrieves and removes the head of the queue represented by this deque
     * (in other words, the first element of this deque), or returns
     * {@code null} if this deque is empty.
     * 1.检索并移除此双端队列所代表的队列的头部（换句话说，此双端队列的第一个元素），
     * 如果此双端队列为空，则返回null
     * <p>This method is equivalent to {@link #pollFirst}.
     * 2.这个方法相当于pollFirst
     * @return the head of the queue represented by this deque, or
     *         {@code null} if this deque is empty
     */
    public E poll() {
        return pollFirst();
    }

    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque.  This method differs from {@link #peek peek} only in
     * that it throws an exception if this deque is empty.
     * 1.检索但不删除此双端队列表示的队列的头部。此方法与peek的不同之处仅在于，
     * 如果此双端队列为空，则会引发异常
     * <p>This method is equivalent to {@link #getFirst}.
     * 2.此方法等效于getFirst
     * @return the head of the queue represented by this deque
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E element() {
        return getFirst();
    }

    /**
     * Retrieves, but does not remove, the head of the queue represented by
     * this deque, or returns {@code null} if this deque is empty.
     * 1.检索但不删除此双端队列表示的队列的头部，如果此双端队列为空，则返回 null。
     * <p>This method is equivalent to {@link #peekFirst}.
     * 2.此方法等效于peekFirst。
     * @return the head of the queue represented by this deque, or
     *         {@code null} if this deque is empty
     */
    public E peek() {
        return peekFirst();
    }

    // *** Stack methods ***
    //堆栈方法

    /**
     * Pushes an element onto the stack represented by this deque.  In other
     * words, inserts the element at the front of this deque.
     * 1.将一个元素推送到此双端队列表示的堆栈上。换句话说，在这个双端队列的前面插入元素
     * <p>This method is equivalent to {@link #addFirst}.
     * 2.此方法等效于addFirst
     * @param e the element to push
     * @throws NullPointerException if the specified element is null
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * Pops an element from the stack represented by this deque.  In other
     * words, removes and returns the first element of this deque.
     * 1.从此双端队列表示的堆栈中弹出一个元素。换句话说，删除并返回此双端队列的第一个元素
     * <p>This method is equivalent to {@link #removeFirst()}.
     * 2.此方法等效于removeFirst()。
     * @return the element at the front of this deque (which is the top
     *         of the stack represented by this deque)
     * @throws NoSuchElementException {@inheritDoc}
     */
    public E pop() {
        return removeFirst();
    }

    private void checkInvariants() {
        assert elements[tail] == null;
        assert head == tail ? elements[head] == null :
            (elements[head] != null &&
             elements[(tail - 1) & (elements.length - 1)] != null);
        assert elements[(head - 1) & (elements.length - 1)] == null;
    }

    /**
     * Removes the element at the specified position in the elements array,
     * adjusting head and tail as necessary.  This can result in motion of
     * elements backwards or forwards in the array.
     * 1.删除元素数组中指定位置的元素，根据需要调整头部和尾部。这会导致元素在数组中向后或向前移动
     * <p>This method is called delete rather than remove to emphasize
     * that its semantics differ from those of {@link List#remove(int)}.
     * 2.这个方法被称为delete而不是remove，以强调它的语义不同于List.remove(int)
     * @return true if elements moved backwards
     */
    private boolean delete(int i) {
        checkInvariants();
        final Object[] elements = this.elements;
        final int mask = elements.length - 1;
        final int h = head;
        final int t = tail;
        final int front = (i - h) & mask;
        final int back  = (t - i) & mask;

        // Invariant: head <= i < tail mod circularity
        if (front >= ((t - h) & mask))
            throw new ConcurrentModificationException();

        // Optimize for least element motion
        if (front < back) {
            if (h <= i) {
                System.arraycopy(elements, h, elements, h + 1, front);
            } else { // Wrap around
                System.arraycopy(elements, 0, elements, 1, i);
                elements[0] = elements[mask];
                System.arraycopy(elements, h, elements, h + 1, mask - h);
            }
            elements[h] = null;
            head = (h + 1) & mask;
            return false;
        } else {
            if (i < t) { // Copy the null tail as well
                System.arraycopy(elements, i + 1, elements, i, back);
                tail = t - 1;
            } else { // Wrap around
                System.arraycopy(elements, i + 1, elements, i, mask - i);
                elements[mask] = elements[0];
                System.arraycopy(elements, 1, elements, 0, t);
                tail = (t - 1) & mask;
            }
            return true;
        }
    }

    // *** Collection Methods ***

    /**
     * Returns the number of elements in this deque.
     * 返回此双端队列中的元素数
     * @return the number of elements in this deque
     */
    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    /**
     * Returns {@code true} if this deque contains no elements.
     * 如果此双端队列不包含任何元素，则返回true
     * @return {@code true} if this deque contains no elements
     */
    public boolean isEmpty() {
        return head == tail;
    }

    /**
     * Returns an iterator over the elements in this deque.  The elements
     * will be ordered from first (head) to last (tail).  This is the same
     * order that elements would be dequeued (via successive calls to
     * {@link #remove} or popped (via successive calls to {@link #pop}).
     * 返回此双端队列中元素的迭代器。
     * 元素将从第一个（头）到最后一个（尾）排序
     * 。这与元素出列（通过连续调用remove或弹出（通过连续调用 pop）的顺序相同
     * @return an iterator over the elements in this deque
     */
    public Iterator<E> iterator() {
        return new DeqIterator();
    }

    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    private class DeqIterator implements Iterator<E> {
        /**
         * Index of element to be returned by subsequent call to next.
         * 后续调用 next 将返回的元素索引
         */
        private int cursor = head;

        /**
         * Tail recorded at construction (also in remove), to stop
         * iterator and also to check for comodification.
         * 在构建时记录尾部（也在移除时记录），以停止迭代器并检查协同修改
         */
        private int fence = tail;

        /**
         * Index of element returned by most recent call to next.
         * Reset to -1 if element is deleted by a call to remove.
         * 最近调用 next 返回的元素索引。如果通过调用 remove 删除元素，则重置为 -1。
         */
        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public E next() {
            if (cursor == fence)
                throw new NoSuchElementException();
            @SuppressWarnings("unchecked")
            E result = (E) elements[cursor];
            // This check doesn't catch all possible comodifications,
            // but does catch the ones that corrupt traversal
            //此检查不会捕获所有可能的共同修饰，但会捕获破坏遍历的那些
            if (tail != fence || result == null)
                throw new ConcurrentModificationException();
            lastRet = cursor;
            cursor = (cursor + 1) & (elements.length - 1);
            return result;
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (delete(lastRet)) { // if left-shifted, undo increment in next()
                cursor = (cursor - 1) & (elements.length - 1);
                fence = tail;
            }
            lastRet = -1;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            Object[] a = elements;
            int m = a.length - 1, f = fence, i = cursor;
            cursor = f;
            while (i != f) {
                @SuppressWarnings("unchecked") E e = (E)a[i];
                i = (i + 1) & m;
                if (e == null)
                    throw new ConcurrentModificationException();
                action.accept(e);
            }
        }
    }

    private class DescendingIterator implements Iterator<E> {
        /*
         * This class is nearly a mirror-image of DeqIterator, using
         * tail instead of head for initial cursor, and head instead of
         * tail for fence.
         * 这个类几乎是 DeqIterator 的镜像，初始光标使用尾部而不是头部，栅栏使用头部而不是尾部
         */
        private int cursor = tail;
        private int fence = head;
        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public E next() {
            if (cursor == fence)
                throw new NoSuchElementException();
            cursor = (cursor - 1) & (elements.length - 1);
            @SuppressWarnings("unchecked")
            E result = (E) elements[cursor];
            if (head != fence || result == null)
                throw new ConcurrentModificationException();
            lastRet = cursor;
            return result;
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            if (!delete(lastRet)) {
                cursor = (cursor + 1) & (elements.length - 1);
                fence = head;
            }
            lastRet = -1;
        }
    }

    /**
     * Returns {@code true} if this deque contains the specified element.
     * More formally, returns {@code true} if and only if this deque contains
     * at least one element {@code e} such that {@code o.equals(e)}.
     * 如果此双端队列包含指定的元素，则返回 true。
     * 更正式地说，当且仅当此双端队列包含至少一个元素 e使得o.equals(e)时才返回true
     * @param o object to be checked for containment in this deque
     * @return {@code true} if this deque contains the specified element
     */
    public boolean contains(Object o) {
        if (o == null)
            return false;
        int mask = elements.length - 1;
        int i = head;
        Object x;
        while ( (x = elements[i]) != null) {
            if (o.equals(x))
                return true;
            i = (i + 1) & mask;
        }
        return false;
    }

    /**
     * Removes a single instance of the specified element from this deque.
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the first element {@code e} such that
     * {@code o.equals(e)} (if such an element exists).
     * Returns {@code true} if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     * 从此双端队列中移除指定元素的单个实例。如果 deque 不包含该元素，则它保持不变。
     * 更正式地，删除第一个元素e使得o.equals(e)（如果存在这样的元素）。
     * 如果此双端队列包含指定的元素（或等效地，如果此双端队列因调用而更改），则返回 true
     * <p>This method is equivalent to {@link #removeFirstOccurrence(Object)}.
     *
     * @param o element to be removed from this deque, if present
     * @return {@code true} if this deque contained the specified element
     */
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    /**
     * Removes all of the elements from this deque.
     * The deque will be empty after this call returns.
     * 从此双端队列中删除所有元素。此调用返回后，双端队列将为空
     */
    public void clear() {
        int h = head;
        int t = tail;
        if (h != t) { // clear all cells
            head = tail = 0;
            int i = h;
            int mask = elements.length - 1;
            do {
                elements[i] = null;
                i = (i + 1) & mask;
            } while (i != t);
        }
    }

    /**
     * Returns an array containing all of the elements in this deque
     * in proper sequence (from first to last element).
     * 1.以适当的顺序（从第一个元素到最后一个元素）返回一个包含此双端队列中所有元素的数组
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this deque.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     * 2.返回的数组将是“安全的”，因为此双端队列不维护对它的引用。
     * （换句话说，此方法必须分配一个新数组）。因此调用者可以自由地修改返回的数组
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     * 3.此方法充当基于数组和基于集合的 API 之间的桥梁。
     * @return an array containing all of the elements in this deque
     */
    public Object[] toArray() {
        return copyElements(new Object[size()]);
    }

    /**
     * Returns an array containing all of the elements in this deque in
     * proper sequence (from first to last element); the runtime type of the
     * returned array is that of the specified array.  If the deque fits in
     * the specified array, it is returned therein.  Otherwise, a new array
     * is allocated with the runtime type of the specified array and the
     * size of this deque.
     * 1.以适当的顺序（从第一个元素到最后一个元素）返回一个包含此双端队列中所有元素的数组；
     * 返回数组的运行时类型是指定数组的类型。如果双端队列适合指定的数组，则在其中返回。
     * 否则，将使用指定数组的运行时类型和此双端队列的大小分配一个新数组
     * <p>If this deque fits in the specified array with room to spare
     * (i.e., the array has more elements than this deque), the element in
     * the array immediately following the end of the deque is set to
     * {@code null}.
     * 2.如果此双端队列适合指定的数组并有空闲空间（即，该数组的元素比此双端队列多），
     * 则紧跟该双端队列末尾的数组中的元素将设置为 null
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     * 3.与toArray()方法一样，此方法充当基于数组和基于集合的 API 之间的桥梁。
     * 此外，该方法允许精确控制输出数组的运行时类型，并且在某些情况下可用于节省分配成本
     * <p>Suppose {@code x} is a deque known to contain only strings.
     * The following code can be used to dump the deque into a newly
     * allocated array of {@code String}:
     *
     *  <pre> {@code String[] y = x.toArray(new String[0]);}</pre>
     * 4.假设x是一个已知只包含字符串的双端队列。以下代码可用于将双端队列转储到新分配的String数组中：
     * String[] y = x.toArray(new String[0]);
     * Note that {@code toArray(new Object[0])} is identical in function to
     * {@code toArray()}.
     * 5.请注意，toArray(new Object[0])在功能上与  toArray() 相同
     * @param a the array into which the elements of the deque are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this deque
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this deque
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        copyElements(a);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // *** Object methods ***

    /**
     * Returns a copy of this deque.
     * 返回此双端队列的副本
     * @return a copy of this deque
     */
    public ArrayDeque<E> clone() {
        try {
            @SuppressWarnings("unchecked")
            ArrayDeque<E> result = (ArrayDeque<E>) super.clone();
            result.elements = Arrays.copyOf(elements, elements.length);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    private static final long serialVersionUID = 2340985798034038923L;

    /**
     * Saves this deque to a stream (that is, serializes it).
     * 将此双端队列保存到流中（即序列化它）
     * @serialData The current size ({@code int}) of the deque,
     * followed by all of its elements (each an object reference) in
     * first-to-last order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size());

        // Write out elements in order.
        //按顺序写出元素
        int mask = elements.length - 1;
        for (int i = head; i != tail; i = (i + 1) & mask)
            s.writeObject(elements[i]);
    }

    /**
     * Reconstitutes this deque from a stream (that is, deserializes it).
     * 从流中重建这个双端队列（即反序列化它）
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();

        // Read in size and allocate array
        int size = s.readInt();
        allocateElements(size);
        head = 0;
        tail = size;

        // Read in all elements in the proper order.
        //按正确顺序读入所有元素。
        for (int i = 0; i < size; i++)
            elements[i] = s.readObject();
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * deque.
     * 1.在此双端队列中的元素上创建 <a href="Spliterator.htmlbinding">late-binding和
     * fail-fast Spliterator
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#SUBSIZED}, {@link Spliterator#ORDERED}, and
     * {@link Spliterator#NONNULL}.  Overriding implementations should document
     * the reporting of additional characteristic values.
     * 2.Spliterator报告SpliteratorSIZED、SpliteratorSUBSIZED、SpliteratorORDERED和
     * SpliteratorNONNULL。覆盖实现应该记录附加特征值的报告
     * @return a {@code Spliterator} over the elements in this deque
     * @since 1.8
     */
    public Spliterator<E> spliterator() {
        return new DeqSpliterator<E>(this, -1, -1);
    }

    static final class DeqSpliterator<E> implements Spliterator<E> {
        private final ArrayDeque<E> deq;
        private int fence;  // -1 until first use
        private int index;  // current index, modified on traverse/split

        /** Creates new spliterator covering the given array and range */
        //创建覆盖给定数组和范围的新拆分器
        DeqSpliterator(ArrayDeque<E> deq, int origin, int fence) {
            this.deq = deq;
            this.index = origin;
            this.fence = fence;
        }

        private int getFence() { // force initialization
            int t;
            if ((t = fence) < 0) {
                t = fence = deq.tail;
                index = deq.head;
            }
            return t;
        }

        public DeqSpliterator<E> trySplit() {
            int t = getFence(), h = index, n = deq.elements.length;
            if (h != t && ((h + 1) & (n - 1)) != t) {
                if (h > t)
                    t += n;
                int m = ((h + t) >>> 1) & (n - 1);
                return new DeqSpliterator<>(deq, h, index = m);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super E> consumer) {
            if (consumer == null)
                throw new NullPointerException();
            Object[] a = deq.elements;
            int m = a.length - 1, f = getFence(), i = index;
            index = f;
            while (i != f) {
                @SuppressWarnings("unchecked") E e = (E)a[i];
                i = (i + 1) & m;
                if (e == null)
                    throw new ConcurrentModificationException();
                consumer.accept(e);
            }
        }

        public boolean tryAdvance(Consumer<? super E> consumer) {
            if (consumer == null)
                throw new NullPointerException();
            Object[] a = deq.elements;
            int m = a.length - 1, f = getFence(), i = index;
            if (i != fence) {
                @SuppressWarnings("unchecked") E e = (E)a[i];
                index = (i + 1) & m;
                if (e == null)
                    throw new ConcurrentModificationException();
                consumer.accept(e);
                return true;
            }
            return false;
        }

        public long estimateSize() {
            int n = getFence() - index;
            if (n < 0)
                n += deq.elements.length;
            return (long) n;
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED |
                Spliterator.NONNULL | Spliterator.SUBSIZED;
        }
    }

}
