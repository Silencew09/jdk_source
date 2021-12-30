/*
 * Copyright (c) 1997, 2006, Oracle and/or its affiliates. All rights reserved.
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

/**
 * This class provides a skeletal implementation of the <tt>List</tt>
 * interface to minimize the effort required to implement this interface
 * backed by a "sequential access" data store (such as a linked list).  For
 * random access data (such as an array), <tt>AbstractList</tt> should be used
 * in preference to this class.<p>
 * 1.此类提供了List接口的骨架实现，以最大限度地减少实现由“顺序访问”数据存储（例如链表）支持的此接口所需的工作。
 * 对于随机访问数据（例如数组），应该优先使用AbstractList而非此类。
 * This class is the opposite of the <tt>AbstractList</tt> class in the sense
 * that it implements the "random access" methods (<tt>get(int index)</tt>,
 * <tt>set(int index, E element)</tt>, <tt>add(int index, E element)</tt> and
 * <tt>remove(int index)</tt>) on top of the list's list iterator, instead of
 * the other way around.<p>
 * 2.此类与AbstractList类相反，因为它实现了“随机访问”方法 (get(int index), set(int index, E element ),
 * add(int index, E element)和remove(int index)) 在列表的列表迭代器之上，而不是相反。
 * To implement a list the programmer needs only to extend this class and
 * provide implementations for the <tt>listIterator</tt> and <tt>size</tt>
 * methods.  For an unmodifiable list, the programmer need only implement the
 * list iterator's <tt>hasNext</tt>, <tt>next</tt>, <tt>hasPrevious</tt>,
 * <tt>previous</tt> and <tt>index</tt> methods.<p>
 * 3.要实现一个列表，程序员只需要扩展这个类并提供listIterator和size方法的实现。
 * 对于不可修改的列表，程序员只需实现列表迭代器的hasNext、next、hasPrevious、previous和index方法。
 * For a modifiable list the programmer should additionally implement the list
 * iterator's <tt>set</tt> method.  For a variable-size list the programmer
 * should additionally implement the list iterator's <tt>remove</tt> and
 * <tt>add</tt> methods.<p>
 * 4.对于可修改的列表，程序员应该另外实现列表迭代器的set方法。对于可变大小的列表，
 * 程序员应该另外实现列表迭代器的remove和add方法。
 * The programmer should generally provide a void (no argument) and collection
 * constructor, as per the recommendation in the <tt>Collection</tt> interface
 * specification.<p>
 * 5.根据Collection接口规范中的建议，程序员通常应提供void（无参数）和集合构造函数
 * This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @author  Josh Bloch
 * @author  Neal Gafter
 * @see Collection
 * @see List
 * @see AbstractList
 * @see AbstractCollection
 * @since 1.2
 */

public abstract class AbstractSequentialList<E> extends AbstractList<E> {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     * 唯一的构造函数。 （对于子类构造函数的调用，通常是隐式的。）
     */
    protected AbstractSequentialList() {
    }

    /**
     * Returns the element at the specified position in this list.
     * 1.返回此列表中指定位置的元素
     * <p>This implementation first gets a list iterator pointing to the
     * indexed element (with <tt>listIterator(index)</tt>).  Then, it gets
     * the element using <tt>ListIterator.next</tt> and returns it.
     * 2.此实现首先获取一个指向索引元素的列表迭代器（使用listIterator(index)）。
     * 然后，它使用ListIterator.next获取元素并返回它
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        try {
            return listIterator(index).next();
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element (optional operation).
     * 1.用指定的元素替换此列表中指定位置的元素（可选操作）
     * <p>This implementation first gets a list iterator pointing to the
     * indexed element (with <tt>listIterator(index)</tt>).  Then, it gets
     * the current element using <tt>ListIterator.next</tt> and replaces it
     * with <tt>ListIterator.set</tt>.
     * 2.此实现首先获取一个指向索引元素的列表迭代器（使用listIterator(index)）。
     * 然后，它使用ListIterator.next获取当前元素并将其替换为ListIterator.set
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the list iterator does not
     * implement the <tt>set</tt> operation.
     * 3.请注意，如果列表迭代器未实现set操作，则此实现将抛出 UnsupportedOperationException
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    public E set(int index, E element) {
        try {
            ListIterator<E> e = listIterator(index);
            E oldVal = e.next();
            e.set(element);
            return oldVal;
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }

    /**
     * Inserts the specified element at the specified position in this list
     * (optional operation).  Shifts the element currently at that position
     * (if any) and any subsequent elements to the right (adds one to their
     * indices).
     * 1.在此列表中的指定位置插入指定元素（可选操作）。将当前在该位置的元素（如果有）和任何后续元素向右移动（向它们的索引添加一个）
     * <p>This implementation first gets a list iterator pointing to the
     * indexed element (with <tt>listIterator(index)</tt>).  Then, it
     * inserts the specified element with <tt>ListIterator.add</tt>.
     * 2.此实现首先获取一个指向索引元素的列表迭代器（使用listIterator(index)）。
     * 然后，它使用ListIterator.add插入指定的元素
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the list iterator does not
     * implement the <tt>add</tt> operation.
     * 3.请注意，如果列表迭代器未实现add操作，则此实现将抛出UnsupportedOperationException。
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    public void add(int index, E element) {
        try {
            listIterator(index).add(element);
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }

    /**
     * Removes the element at the specified position in this list (optional
     * operation).  Shifts any subsequent elements to the left (subtracts one
     * from their indices).  Returns the element that was removed from the
     * list.
     * 1.移除此列表中指定位置的元素（可选操作）。将任何后续元素向左移动（从它们的索引中减去一个）。返回从列表中删除的元素
     * <p>This implementation first gets a list iterator pointing to the
     * indexed element (with <tt>listIterator(index)</tt>).  Then, it removes
     * the element with <tt>ListIterator.remove</tt>.
     * 2.此实现首先获取一个指向索引元素的列表迭代器（使用listIterator(index)）。
     * 然后，它使用ListIterator.remove 删除元素
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the list iterator does not
     * implement the <tt>remove</tt> operation.
     * 3.请注意，如果列表迭代器未实现remove操作，则此实现将抛出UnsupportedOperationException
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    public E remove(int index) {
        try {
            ListIterator<E> e = listIterator(index);
            E outCast = e.next();
            e.remove();
            return outCast;
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }


    // Bulk Operations
    //批量操作

    /**
     * Inserts all of the elements in the specified collection into this
     * list at the specified position (optional operation).  Shifts the
     * element currently at that position (if any) and any subsequent
     * elements to the right (increases their indices).  The new elements
     * will appear in this list in the order that they are returned by the
     * specified collection's iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the
     * operation is in progress.  (Note that this will occur if the specified
     * collection is this list, and it's nonempty.)
     * 1.将指定集合中的所有元素插入此列表的指定位置（可选操作）。
     * 将当前在该位置的元素（如果有）和任何后续元素向右移动（增加它们的索引）。
     * 新元素将按照指定集合的迭代器返回的顺序出现在此列表中。如果在操作进行时修改了指定的集合，则此操作的行为未定义。
     * （请注意，如果指定的集合是这个列表，并且它是非空的，则会发生这种情况。
     * <p>This implementation gets an iterator over the specified collection and
     * a list iterator over this list pointing to the indexed element (with
     * <tt>listIterator(index)</tt>).  Then, it iterates over the specified
     * collection, inserting the elements obtained from the iterator into this
     * list, one at a time, using <tt>ListIterator.add</tt> followed by
     * <tt>ListIterator.next</tt> (to skip over the added element).
     * 2.此实现获取指定集合上的迭代器和此列表上指向索引元素的列表迭代器（使用listIterator(index)）。
     * 然后，它遍历指定的集合，将从迭代器获得的元素插入到这个列表中，一次一个，使用ListIterator.add
     * 后跟ListIterator.next（跳过添加的元素）
     * <p>Note that this implementation will throw an
     * <tt>UnsupportedOperationException</tt> if the list iterator returned by
     * the <tt>listIterator</tt> method does not implement the <tt>add</tt>
     * operation.
     * 3.请注意，如果listIterator方法返回的列表迭代器没有实现add操作，
     * 则此实现将抛出UnsupportedOperationException
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     * @throws IndexOutOfBoundsException     {@inheritDoc}
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        try {
            boolean modified = false;
            ListIterator<E> e1 = listIterator(index);
            Iterator<? extends E> e2 = c.iterator();
            while (e2.hasNext()) {
                e1.add(e2.next());
                modified = true;
            }
            return modified;
        } catch (NoSuchElementException exc) {
            throw new IndexOutOfBoundsException("Index: "+index);
        }
    }


    // Iterators

    /**
     * Returns an iterator over the elements in this list (in proper
     * sequence).<p>
     * 1.返回此列表中元素的迭代器（以适当的顺序）
     * This implementation merely returns a list iterator over the list.
     * 2.此实现仅返回列表上的列表迭代器
     * @return an iterator over the elements in this list (in proper sequence)
     */
    public Iterator<E> iterator() {
        return listIterator();
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     * 返回此列表中元素的列表迭代器（按适当的顺序）
     * @param  index index of first element to be returned from the list
     *         iterator (by a call to the <code>next</code> method)
     * @return a list iterator over the elements in this list (in proper
     *         sequence)
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public abstract ListIterator<E> listIterator(int index);
}
