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

package java.lang;
import java.util.*;

/**
 * This interface imposes a total ordering on the objects of each class that
 * implements it.  This ordering is referred to as the class's <i>natural
 * ordering</i>, and the class's <tt>compareTo</tt> method is referred to as
 * its <i>natural comparison method</i>.<p>
 * 1.该接口对实现它的每个类的对象强加了总排序。这种排序被称为类的自然排序，
 * 而类的compareTo方法被称为它的自然比较方法
 * Lists (and arrays) of objects that implement this interface can be sorted
 * automatically by {@link Collections#sort(List) Collections.sort} (and
 * {@link Arrays#sort(Object[]) Arrays.sort}).  Objects that implement this
 * interface can be used as keys in a {@linkplain SortedMap sorted map} or as
 * elements in a {@linkplain SortedSet sorted set}, without the need to
 * specify a {@linkplain Comparator comparator}.<p>
 * 2.实现此接口的对象列表（和数组）可以通过Collections.sort(List)Arrays.sort(Object[])自动排序
 * 3.实现此接口的对象可以用作 SortedMap中的键或用作SortedSet排序集中的元素，而无需指定Comparator比较器
 * The natural ordering for a class <tt>C</tt> is said to be <i>consistent
 * with equals</i> if and only if <tt>e1.compareTo(e2) == 0</tt> has
 * the same boolean value as <tt>e1.equals(e2)</tt> for every
 * <tt>e1</tt> and <tt>e2</tt> of class <tt>C</tt>.  Note that <tt>null</tt>
 * is not an instance of any class, and <tt>e.compareTo(null)</tt> should
 * throw a <tt>NullPointerException</tt> even though <tt>e.equals(null)</tt>
 * returns <tt>false</tt>.<p>
 * 4.当且仅当e1.compareTo(e2) == 0具有相同的布尔值时，C类的自然顺序被称为与 equals一致对于e1
 * 和 e2类的 e1.equals(e2)。注意 null不是任何类的实例，并且e.compareTo(null)应该抛出一个NullPointerException
 * 即使 e.equals(null ) 返回 false
 * It is strongly recommended (though not required) that natural orderings be
 * consistent with equals.  This is so because sorted sets (and sorted maps)
 * without explicit comparators behave "strangely" when they are used with
 * elements (or keys) whose natural ordering is inconsistent with equals.  In
 * particular, such a sorted set (or sorted map) violates the general contract
 * for set (or map), which is defined in terms of the <tt>equals</tt>
 * method.<p>
 * 5.强烈建议（虽然不是必需的）自然顺序与 equals 一致。
 * 之所以如此，是因为没有显式比较器的排序集（和排序映射）
 * 在与自然顺序与 equals 不一致的元素（或键）一起使用时表现得“奇怪”。
 * 特别是，这样的排序集合（或排序映射）违反了集合（或映射）的一般契约，
 * 它是根据equals方法定义的
 * For example, if one adds two keys <tt>a</tt> and <tt>b</tt> such that
 * {@code (!a.equals(b) && a.compareTo(b) == 0)} to a sorted
 * set that does not use an explicit comparator, the second <tt>add</tt>
 * operation returns false (and the size of the sorted set does not increase)
 * because <tt>a</tt> and <tt>b</tt> are equivalent from the sorted set's
 * perspective.<p>
 * 6.例如，如果添加两个键 a和b使得(!a.equals(b) && a.compareTo(b) == 0) 到 a不使用显式比较器的排序集，
 * 第二个add 操作返回 false（并且排序集的大小不会增加），因为 a和b从有序集合的角度来看是等价的。
 * Virtually all Java core classes that implement <tt>Comparable</tt> have natural
 * orderings that are consistent with equals.  One exception is
 * <tt>java.math.BigDecimal</tt>, whose natural ordering equates
 * <tt>BigDecimal</tt> objects with equal values and different precisions
 * (such as 4.0 and 4.00).<p>
 * 7.几乎所有实现Comparable的 Java 核心类都具有与 equals 一致的自然顺序。
 * 一个例外是 java.math.BigDecimal，它的自然排序等同于BigDecimal具有相同值和不同精度的对象（例如 4.0 和 4.00）
 * For the mathematically inclined, the <i>relation</i> that defines
 * the natural ordering on a given class C is:<pre>
 *       {(x, y) such that x.compareTo(y) &lt;= 0}.
 * </pre> The <i>quotient</i> for this total order is: <pre>
 *       {(x, y) such that x.compareTo(y) == 0}.
 * </pre>
 *
 * It follows immediately from the contract for <tt>compareTo</tt> that the
 * quotient is an <i>equivalence relation</i> on <tt>C</tt>, and that the
 * natural ordering is a <i>total order</i> on <tt>C</tt>.  When we say that a
 * class's natural ordering is <i>consistent with equals</i>, we mean that the
 * quotient for the natural ordering is the equivalence relation defined by
 * the class's {@link Object#equals(Object) equals(Object)} method:<pre>
 *     {(x, y) such that x.equals(y)}. </pre><p>
 * 8.从compareTo的契约可以看出，商是C上的 等价关系，并且自然排序是全序 在C上。
 * 当我们说一个类的自然排序与equals一致时，我们的意思是自然排序的商是类的Object.equals(Object)方法定义的等价关系：
 *(x, y) 使得 x.equals(y)
 * This interface is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <T> the type of objects that this object may be compared to
 *
 * @author  Josh Bloch
 * @see java.util.Comparator
 * @since 1.2
 */
public interface Comparable<T> {
    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * 1.将此对象与指定的对象进行比较以进行排序,当此对象小于、等于或大于指定对象时，返回负整数、零或正整数
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * 2.实现者必须确保 sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
     * 对于所有 x和 y。 （这意味着 x.compareTo(y)必须抛出异常，如果y.compareTo(x)抛出异常
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * 3.实现者还必须确保关系是可传递的：(x.compareTo(y)>0 , y.compareTo(z)<0)
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * 4.最后，实现者必须确保 x.compareTo(y)==0意味着 sgn(x.compareTo(z)) == sgn(y.compareTo(z)) , 对于所有 z
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * 5.强烈推荐，但 没有严格要求(x.compareTo(y)==0) == (x.equals(y))。
     * 一般而言，任何实现Comparable 接口并违反此条件的类都应清楚表明这一事实。
     * 推荐的语言是“注意：这个类有一个与equals不一致的自然顺序
     *
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     * 6.在前面的描述中，符号sgn(expression) 表示数学signum 函数，
     * 它被定义为返回-1、0或 1根据 expression 的值是否为负、零或正
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     *
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException if the specified object's type prevents it
     *         from being compared to this object.
     */
    public int compareTo(T o);
}
