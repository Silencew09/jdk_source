/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * (C) Copyright Taligent, Inc. 1996-1998 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996-1998 - All Rights Reserved
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.BuddhistCalendar;
import sun.util.calendar.ZoneInfo;
import sun.util.locale.provider.CalendarDataUtility;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.spi.CalendarProvider;

/**
 * The <code>Calendar</code> class is an abstract class that provides methods
 * for converting between a specific instant in time and a set of {@link
 * #fields calendar fields} such as <code>YEAR</code>, <code>MONTH</code>,
 * <code>DAY_OF_MONTH</code>, <code>HOUR</code>, and so on, and for
 * manipulating the calendar fields, such as getting the date of the next
 * week. An instant in time can be represented by a millisecond value that is
 * an offset from the <a name="Epoch"><em>Epoch</em></a>, January 1, 1970
 * 00:00:00.000 GMT (Gregorian).
 * 1.Calendar类是一个抽象类，它提供了在特定时刻和一组 fields日历字段（例如YEAR、MONTH）之间进行转换的方法、
 * DAY_OF_MONTH、HOUR等，以及用于操作日历字段，例如获取下一周的日期。时间瞬间可以用毫秒值表示，
 * 该值是 <a name="Epoch">Epoch<a>, 1970 年 1 月 1 日 00:00:00.000 GMT（格里高利）的偏移量
 * <p>The class also provides additional fields and methods for
 * implementing a concrete calendar system outside the package. Those
 * fields and methods are defined as <code>protected</code>.
 * 2.该类还提供了额外的字段和方法，用于在包外实现具体的日历系统。这些字段和方法被定义为protected。
 * <p>
 * Like other locale-sensitive classes, <code>Calendar</code> provides a
 * class method, <code>getInstance</code>, for getting a generally useful
 * object of this type. <code>Calendar</code>'s <code>getInstance</code> method
 * returns a <code>Calendar</code> object whose
 * calendar fields have been initialized with the current date and time:
 * <blockquote>
 * <pre>
 *     Calendar rightNow = Calendar.getInstance();
 * </pre>
 * </blockquote>
 * 3.与其他语言环境敏感类一样，Calendar提供了一个类方法getInstance，用于获取这种类型的通用对象。
 * Calendar的getInstance方法返回一个Calendar对象，其日历字段已用当前日期和时间初始化：
 * Calendar rightNow = Calendar .getInstance();
 * <p>A <code>Calendar</code> object can produce all the calendar field values
 * needed to implement the date-time formatting for a particular language and
 * calendar style (for example, Japanese-Gregorian, Japanese-Traditional).
 * <code>Calendar</code> defines the range of values returned by
 * certain calendar fields, as well as their meaning.  For example,
 * the first month of the calendar system has value <code>MONTH ==
 * JANUARY</code> for all calendars.  Other values are defined by the
 * concrete subclass, such as <code>ERA</code>.  See individual field
 * documentation and subclass documentation for details.
 * 4.Calendar对象可以生成实现特定语言和日历样式（例如，Japanese-Gregorian、Japanese-Traditional）的日期时间格式
 * 所需的所有日历字段值。Calendar定义了某些日历字段返回的值的范围及其含义,
 * 例如，日历系统的第一个月对于所有日历都有值MONTH == JANUARY。
 * 其他值由具体子类定义，例如ERA。有关详细信息，请参阅各个字段文档和子类文档
 * <h3>Getting and Setting Calendar Field Values</h3>
 * 获取和设置日历字段值
 * <p>The calendar field values can be set by calling the <code>set</code>
 * methods. Any field values set in a <code>Calendar</code> will not be
 * interpreted until it needs to calculate its time value (milliseconds from
 * the Epoch) or values of the calendar fields. Calling the
 * <code>get</code>, <code>getTimeInMillis</code>, <code>getTime</code>,
 * <code>add</code> and <code>roll</code> involves such calculation.
 * 5.日历字段值可以通过调用set方法来设置。在Calendar中设置的任何字段值都不会被解释，
 * 直到需要计算其时间值（距纪元的毫秒数）或日历字段的值。调用get、getTimeInMillis、getTime、add和roll涉及这样的计算
 * <h4>Leniency</h4>
 *
 * <p><code>Calendar</code> has two modes for interpreting the calendar
 * fields, <em>lenient</em> and <em>non-lenient</em>.  When a
 * <code>Calendar</code> is in lenient mode, it accepts a wider range of
 * calendar field values than it produces.  When a <code>Calendar</code>
 * recomputes calendar field values for return by <code>get()</code>, all of
 * the calendar fields are normalized. For example, a lenient
 * <code>GregorianCalendar</code> interprets <code>MONTH == JANUARY</code>,
 * <code>DAY_OF_MONTH == 32</code> as February 1.
 6.Calendar有两种解释日历字段的模式，lenient和non-lenient。当Calendar处于宽松模式时，
 它接受比它产生的更广泛的日历字段值。当Calendar重新计算get()返回的日历字段值时，
 所有日历字段都被标准化。例如，宽松的GregorianCalendar将MONTH == JANUARY,
 DAY_OF_MONTH == 32解释为二月 1
 * <p>When a <code>Calendar</code> is in non-lenient mode, it throws an
 * exception if there is any inconsistency in its calendar fields. For
 * example, a <code>GregorianCalendar</code> always produces
 * <code>DAY_OF_MONTH</code> values between 1 and the length of the month. A
 * non-lenient <code>GregorianCalendar</code> throws an exception upon
 * calculating its time or calendar field values if any out-of-range field
 * value has been set.
 * 7.当Calendar处于非宽松模式时，如果其日历字段中存在任何不一致，则会引发异常。
 * 例如，GregorianCalendar总是产生介于 1 和月份长度之间的DAY_OF_MONTH值。
 * 如果设置了任何超出范围的字段值，则非宽松GregorianCalendar在计算其时间或日历字段值时会引发异常
 * <h4><a name="first_week">First Week</a></h4>
 *
 * <code>Calendar</code> defines a locale-specific seven day week using two
 * parameters: the first day of the week and the minimal days in first week
 * (from 1 to 7).  These numbers are taken from the locale resource data when a
 * <code>Calendar</code> is constructed.  They may also be specified explicitly
 * through the methods for setting their values.
 * 8.Calendar使用两个参数定义了特定于语言环境的每周 7 天：
 * 一周的第一天和第一周的最少天数（从 1 到 7）。这些数字是在构造Calendar时从语言环境资源数据中获取的。
 * 它们也可以通过设置它们的值的方法显式指定
 * <p>When setting or getting the <code>WEEK_OF_MONTH</code> or
 * <code>WEEK_OF_YEAR</code> fields, <code>Calendar</code> must determine the
 * first week of the month or year as a reference point.  The first week of a
 * month or year is defined as the earliest seven day period beginning on
 * <code>getFirstDayOfWeek()</code> and containing at least
 * <code>getMinimalDaysInFirstWeek()</code> days of that month or year.  Weeks
 * numbered ..., -1, 0 precede the first week; weeks numbered 2, 3,... follow
 * it.  Note that the normalized numbering returned by <code>get()</code> may be
 * different.  For example, a specific <code>Calendar</code> subclass may
 * designate the week before week 1 of a year as week <code><i>n</i></code> of
 * the previous year.
 * 9.在设置或获取WEEK_OF_MONTH或WEEK_OF_YEAR字段时，Calendar必须确定月份或年份的第一周作为参考点。
 * 一个月或一年的第一周定义为从getFirstDayOfWeek()开始并至少包含该月或年的getMinimalDaysInFirstWeek()天的最早 7 天。
 * 周数 ..., -1, 0 在第一周之前；周数为 2、3、... 跟着它。请注意，get()返回的规范化编号可能不同。
 * 例如，特定的Calendar子类可以将一年的第 1 周之前的一周指定为上一年的第n 周
 * <h4>Calendar Fields Resolution</h4>
 * 日历字段解析
 * When computing a date and time from the calendar fields, there
 * may be insufficient information for the computation (such as only
 * year and month with no day of month), or there may be inconsistent
 * information (such as Tuesday, July 15, 1996 (Gregorian) -- July 15,
 * 1996 is actually a Monday). <code>Calendar</code> will resolve
 * calendar field values to determine the date and time in the
 * following way.
 * 10.从日历字段计算日期和时间时，计算信息可能不足（例如只有年和月而没有月份的日期），
 * 或者可能存在不一致的信息（例如 1996 年 7 月 15 日，星期二（格里高利） ) -- 1996 年 7 月 15 日实际上是星期一)。
 * Calendar将通过以下方式解析日历字段值以确定日期和时间。
 * <p><a name="resolution">If there is any conflict in calendar field values,
 * <code>Calendar</code> gives priorities to calendar fields that have been set
 * more recently.</a> The following are the default combinations of the
 * calendar fields. The most recent combination, as determined by the
 * most recently set single field, will be used.
 * 11.如果日历字段值存在任何冲突，Calendar将优先考虑最近设置的日历字段。
 * 以下是日历字段的默认组合。将使用由最近设置的单个字段确定的最近组合。
 * <p><a name="date_resolution">For the date fields</a>:
 * <blockquote>
 * <pre> //对于日期字段
 * YEAR + MONTH + DAY_OF_MONTH
 * YEAR + MONTH + WEEK_OF_MONTH + DAY_OF_WEEK
 * YEAR + MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
 * YEAR + DAY_OF_YEAR
 * YEAR + DAY_OF_WEEK + WEEK_OF_YEAR
 * </pre></blockquote>
 *
 * <a name="time_resolution">For the time of day fields</a>:
 * <blockquote>
 * <pre>
 * HOUR_OF_DAY
 * AM_PM + HOUR
 * </pre></blockquote>
 *
 * <p>If there are any calendar fields whose values haven't been set in the selected
 * field combination, <code>Calendar</code> uses their default values. The default
 * value of each field may vary by concrete calendar systems. For example, in
 * <code>GregorianCalendar</code>, the default of a field is the same as that
 * of the start of the Epoch: i.e., <code>YEAR = 1970</code>, <code>MONTH =
 * JANUARY</code>, <code>DAY_OF_MONTH = 1</code>, etc.
 * 12.如果有任何日历字段的值尚未在所选字段组合中设置，Calendar将使用其默认值,
 * 每个字段的默认值可能因具体日历系统而异,例如，在GregorianCalendar中，
 * 一个字段的默认值与Epoch开始时相同：即YEAR = 1970,MONTH = JANUARY,DAY_OF_MONTH = 1等
 * <p>
 * <strong>Note:</strong> There are certain possible ambiguities in
 * interpretation of certain singular times, which are resolved in the
 * following ways:
 * 13.注：某些奇异时间的解释可能存在一定的歧义，可通过以下方式解决：
 * <ol>
 *     <li> 23:59 is the last minute of the day and 00:00 is the first
 *          minute of the next day. Thus, 23:59 on Dec 31, 1999 &lt; 00:00 on
 *          Jan 1, 2000 &lt; 00:01 on Jan 1, 2000.
 *23:59 是当天的最后一分钟，00:00 是第二天的第一分钟。
 * 因此，1999 年 12 月 31 日的 23:59 < 2000 年 1 月 1 日的 00:00 < 2000 年 1 月 1 日的 00:01
 *     <li> Although historically not precise, midnight also belongs to "am",
 *          and noon belongs to "pm", so on the same day,
 *          12:00 am (midnight) &lt; 12:01 am, and 12:00 pm (noon) &lt; 12:01 pm
 *          虽然历史上并不精确，但午夜也属于“am”，中午属于“pm”，
 *          所以在同一天，12:00 am（午夜）< 12:01 am，12:00 pm（中午）< 12 :01 下午
 * </ol>
 *
 * <p>
 * The date or time format strings are not part of the definition of a
 * calendar, as those must be modifiable or overridable by the user at
 * runtime. Use {@link DateFormat}
 * to format dates.
 * 14.日期或时间格式字符串不是日历定义的一部分，因为它们必须在运行时可由用户修改或覆盖。使用DateFormat格式化日期。
 * <h4>Field Manipulation</h4>
 *
 * The calendar fields can be changed using three methods:
 * <code>set()</code>, <code>add()</code>, and <code>roll()</code>.
 * 15.可以使用三种方法更改日历字段：set()、add()和roll()
 * <p><strong><code>set(f, value)</code></strong> changes calendar field
 * <code>f</code> to <code>value</code>.  In addition, it sets an
 * internal member variable to indicate that calendar field <code>f</code> has
 * been changed. Although calendar field <code>f</code> is changed immediately,
 * the calendar's time value in milliseconds is not recomputed until the next call to
 * <code>get()</code>, <code>getTime()</code>, <code>getTimeInMillis()</code>,
 * <code>add()</code>, or <code>roll()</code> is made. Thus, multiple calls to
 * <code>set()</code> do not trigger multiple, unnecessary
 * computations. As a result of changing a calendar field using
 * <code>set()</code>, other calendar fields may also change, depending on the
 * calendar field, the calendar field value, and the calendar system. In addition,
 * <code>get(f)</code> will not necessarily return <code>value</code> set by
 * the call to the <code>set</code> method
 * after the calendar fields have been recomputed. The specifics are determined by
 * the concrete calendar class.</p>
 * 16.set(f, value)将日历字段f更改为value。此外，它还设置了一个内部成员变量来指示日历字段f已更改。
 * 尽管日历字段f会立即更改，但不会重新计算以毫秒为单位的日历时间值，
 * 直到下一次调用get(),getTime(), getTimeInMillis()、add()或roll()已生成。
 * 因此，多次调用set()不会触发多次不必要的计算。作为使用set()更改日历字段的结果，
 * 其他日历字段也可能会更改，具体取决于日历字段、日历字段值和日历系统。
 * 此外，在重新计算日历字段后，get(f)不一定返回通过调用set方法设置的value。具体由具体的日历类决定
 * <p><em>Example</em>: Consider a <code>GregorianCalendar</code>
 * originally set to August 31, 1999. Calling <code>set(Calendar.MONTH,
 * Calendar.SEPTEMBER)</code> sets the date to September 31,
 * 1999. This is a temporary internal representation that resolves to
 * October 1, 1999 if <code>getTime()</code>is then called. However, a
 * call to <code>set(Calendar.DAY_OF_MONTH, 30)</code> before the call to
 * <code>getTime()</code> sets the date to September 30, 1999, since
 * no recomputation occurs after <code>set()</code> itself.</p>
 * 17.示例：考虑一个GregorianCalendar最初设置为 1999 年 8 月 31 日。
 * 调用set(Calendar.MONTH, Calendar.SEPTEMBER)将日期设置为 1999 年 9 月 31 日。
 * 这是如果getTime()被调用，则解析为 1999 年 10 月 1 日的临时内部表示。
 * 但是，在调用getTime()之前调用 set(Calendar.DAY_OF_MONTH, 30)
 * 会将日期设置为 1999 年 9 月 30 日，因为在set 之后不会发生重新计算()本身
 * <p><strong><code>add(f, delta)</code></strong> adds <code>delta</code>
 * to field <code>f</code>.  This is equivalent to calling <code>set(f,
 * get(f) + delta)</code> with two adjustments:</p>
 * 18.add(f, delta)将delta添加到字段f。这等效于调用set(f, get(f) + delta)进行两个调整：
 * <blockquote>
 *   <p><strong>Add rule 1</strong>. The value of field <code>f</code>
 *   after the call minus the value of field <code>f</code> before the
 *   call is <code>delta</code>, modulo any overflow that has occurred in
 *   field <code>f</code>. Overflow occurs when a field value exceeds its
 *   range and, as a result, the next larger field is incremented or
 *   decremented and the field value is adjusted back into its range.</p>
 *  1)添加规则 1。调用后字段 f的值减去调用前字段f的值是delta，以字段f中发生的任何溢出为模。
 *  当字段值超出其范围时会发生溢出，因此，下一个较大的字段会增加或减少，并且字段值会调整回其范围
 *   <p><strong>Add rule 2</strong>. If a smaller field is expected to be
 *   invariant, but it is impossible for it to be equal to its
 *   prior value because of changes in its minimum or maximum after field
 *   <code>f</code> is changed or other constraints, such as time zone
 *   offset changes, then its value is adjusted to be as close
 *   as possible to its expected value. A smaller field represents a
 *   smaller unit of time. <code>HOUR</code> is a smaller field than
 *   <code>DAY_OF_MONTH</code>. No adjustment is made to smaller fields
 *   that are not expected to be invariant. The calendar system
 *   determines what fields are expected to be invariant.</p>
 * </blockquote>
 *  2)添加规则 2。如果期望较小的字段是不变的，但由于字段f更改后其最小值或最大值的变化或其他限制（例如时区）
 *  而无法使其等于其先前值offset 发生变化，然后将其值调整为尽可能接近其期望值
 *  较小的字段代表较小的时间单位。HOUR是比DAY_OF_MONTH更小的字段。
 *  不会对预计不会保持不变的较小字段进行调整。日历系统确定哪些字段应该是不变的
 * <p>In addition, unlike <code>set()</code>, <code>add()</code> forces
 * an immediate recomputation of the calendar's milliseconds and all
 * fields.</p>
 * 19.此外，与set()不同，add()强制立即重新计算日历的毫秒数和所有字段
 * <p><em>Example</em>: Consider a <code>GregorianCalendar</code>
 * originally set to August 31, 1999. Calling <code>add(Calendar.MONTH,
 * 13)</code> sets the calendar to September 30, 2000. <strong>Add rule
 * 1</strong> sets the <code>MONTH</code> field to September, since
 * adding 13 months to August gives September of the next year. Since
 * <code>DAY_OF_MONTH</code> cannot be 31 in September in a
 * <code>GregorianCalendar</code>, <strong>add rule 2</strong> sets the
 * <code>DAY_OF_MONTH</code> to 30, the closest possible value. Although
 * it is a smaller field, <code>DAY_OF_WEEK</code> is not adjusted by
 * rule 2, since it is expected to change when the month changes in a
 * <code>GregorianCalendar</code>.</p>
 * 20.示例：考虑最初设置为 1999 年 8 月 31 日的GregorianCalendar。
 * 调用 add(Calendar.MONTH, 13)将日历设置为 2000 年 9 月 30 日。
 * 添加规则 1将 MONTH字段设置为 9 月，因为将 13 个月添加到 8 月会得到下一年的 9 月。
 * 由于DAY_OF_MONTH 在 GregorianCalendar中的 9 月不能为 31，
 * 添加规则 2将DAY_OF_MONTH设置为 30，即最接近的可能值。虽然它是一个较小的字段，但是 DAY_OF_WEEK
 * 没有被规则 2 调整，因为它预计会随着GregorianCalendar 中的月份变化而变化
 * <p><strong><code>roll(f, delta)</code></strong> adds
 * <code>delta</code> to field <code>f</code> without changing larger
 * fields. This is equivalent to calling <code>add(f, delta)</code> with
 * the following adjustment:</p>
 * 21.roll(f, delta)将delta添加到字段f而不更改更大的字段。这等效于调用add(f, delta)并进行以下调整
 * <blockquote>
 *   <p><strong>Roll rule</strong>. Larger fields are unchanged after the
 *   call. A larger field represents a larger unit of
 *   time. <code>DAY_OF_MONTH</code> is a larger field than
 *   <code>HOUR</code>.</p>
 *   滚动规则。调用后较大的字段不变。较大的字段表示较大的时间单位。DAY_OF_MONTH是比HOUR更大的字段
 * </blockquote>
 *
 * <p><em>Example</em>: See {@link java.util.GregorianCalendar#roll(int, int)}.
 *
 * <p><strong>Usage model</strong>. To motivate the behavior of
 * <code>add()</code> and <code>roll()</code>, consider a user interface
 * component with increment and decrement buttons for the month, day, and
 * year, and an underlying <code>GregorianCalendar</code>. If the
 * interface reads January 31, 1999 and the user presses the month
 * increment button, what should it read? If the underlying
 * implementation uses <code>set()</code>, it might read March 3, 1999. A
 * better result would be February 28, 1999. Furthermore, if the user
 * presses the month increment button again, it should read March 31,
 * 1999, not March 28, 1999. By saving the original date and using either
 * <code>add()</code> or <code>roll()</code>, depending on whether larger
 * fields should be affected, the user interface can behave as most users
 * will intuitively expect.</p>
 * 22.使用模型。为了激发add()和roll()的行为，考虑一个带有月、日和年递增和递减按钮的用户界面组件，
 * 以及一个底层公历。如果界面显示为 1999 年 1 月 31 日，用户按下月份递增按钮，
 * 它应该显示什么？如果底层实现使用set()，它可能会读到 1999 年 3 月 3 日。
 * 更好的结果是 1999 年 2 月 28 日。此外，如果用户再次按下月份递增按钮，
 * 它应该会读到 3 月 31 日, 1999，而不是 1999 年 3 月 28 日。
 * 通过保存原始日期并使用add()或roll()，根据是否应影响较大的字段，用户界面可以表现得像大多数用户直觉上期望的那样
 * @see          java.lang.System#currentTimeMillis()
 * @see          Date
 * @see          GregorianCalendar
 * @see          TimeZone
 * @see          java.text.DateFormat
 * @author Mark Davis, David Goldsmith, Chen-Lieh Huang, Alan Liu
 * @since JDK1.1
 */
public abstract class Calendar implements Serializable, Cloneable, Comparable<Calendar> {

    // Data flow in Calendar
    //日历中的数据流
    // ---------------------

    // The current time is represented in two ways by Calendar: as UTC
    // milliseconds from the epoch (1 January 1970 0:00 UTC), and as local
    // fields such as MONTH, HOUR, AM_PM, etc.  It is possible to compute the
    // millis from the fields, and vice versa.  The data needed to do this
    // conversion is encapsulated by a TimeZone object owned by the Calendar.
    // The data provided by the TimeZone object may also be overridden if the
    // user sets the ZONE_OFFSET and/or DST_OFFSET fields directly. The class
    // keeps track of what information was most recently set by the caller, and
    // uses that to compute any other information as needed.
   // 1.当前时间由 Calendar 以两种方式表示：作为从纪元（1970 年 1 月 1 日 0:00 UTC）开始的 UTC 毫秒，
    // 以及作为本地字段，例如 MONTH、HOUR、AM_PM 等。可以从字段，反之亦然。
    // 进行此转换所需的数据由 Calendar 拥有的 TimeZone 对象封装。
    // 如果用户直接设置 ZONE_OFFSET 和/或 DST_OFFSET 字段，则 TimeZone 对象提供的数据也可能被覆盖。
    // 该类会跟踪调用者最近设置的信息，并根据需要使用它来计算任何其他信息
    // If the user sets the fields using set(), the data flow is as follows.
    // This is implemented by the Calendar subclass's computeTime() method.
    // During this process, certain fields may be ignored.  The disambiguation
    // algorithm for resolving which fields to pay attention to is described
    // in the class documentation.
    //2.如果用户使用 set() 设置字段，则数据流如下。这是由 Calendar 子类的 computeTime() 方法实现的。
    // 在此过程中，某些字段可能会被忽略。类文档中描述了解决哪些字段需要注意的消歧算法

    //   local fields (YEAR, MONTH, DATE, HOUR, MINUTE, etc.)
    //           |
    //           | Using Calendar-specific algorithm
    //           V
    //   local standard millis
    //           |
    //           | Using TimeZone or user-set ZONE_OFFSET / DST_OFFSET
    //           V
    //   UTC millis (in time data member)

    // If the user sets the UTC millis using setTime() or setTimeInMillis(),
    // the data flow is as follows.  This is implemented by the Calendar
    // subclass's computeFields() method.
    //3.如果用户使用 setTime() 或 setTimeInMillis() 设置 UTC 毫秒，则数据流如下。
    // 这是由 Calendar 子类的 computeFields() 方法实现的
    //   UTC millis (in time data member)
    //           |
    //           | Using TimeZone getOffset()
    //           V
    //   local standard millis
    //           |
    //           | Using Calendar-specific algorithm
    //           V
    //   local fields (YEAR, MONTH, DATE, HOUR, MINUTE, etc.)

    // In general, a round trip from fields, through local and UTC millis, and
    // back out to fields is made when necessary.  This is implemented by the
    // complete() method.  Resolving a partial set of fields into a UTC millis
    // value allows all remaining fields to be generated from that value.  If
    // the Calendar is lenient, the fields are also renormalized to standard
    // ranges when they are regenerated.
    //4.通常，在必要时进行从字段到本地和 UTC 毫秒的往返行程，然后返回到字段。这是由 complete() 方法实现的。
    // 将部分字段集解析为 UTC 毫秒值允许从该值生成所有剩余字段。
    // 如果 Calendar 宽松，则字段在重新生成时也会重新规范化为标准范围
    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * era, e.g., AD or BC in the Julian calendar. This is a calendar-specific
     * value; see subclass documentation.
     *get和set的字段编号表示时代，例如儒略历中的公元或公元前。这是一个特定于日历的值；请参阅子类文档
     * @see GregorianCalendar#AD
     * @see GregorianCalendar#BC
     */
    public final static int ERA = 0;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * year. This is a calendar-specific value; see subclass documentation.
     *get和set的字段编号表示年份。这是一个特定于日历的值；请参阅子类文档。
     */
    public final static int YEAR = 1;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * month. This is a calendar-specific value. The first month of
     * the year in the Gregorian and Julian calendars is
     * <code>JANUARY</code> which is 0; the last depends on the number
     * of months in a year.
     * get和set的字段编号，表示月份。这是特定于日历的值。
     * 公历和儒略历中一年的第一个月是 JANUARY，即 0；最后一个取决于一年中的月数
     * @see #JANUARY
     * @see #FEBRUARY
     * @see #MARCH
     * @see #APRIL
     * @see #MAY
     * @see #JUNE
     * @see #JULY
     * @see #AUGUST
     * @see #SEPTEMBER
     * @see #OCTOBER
     * @see #NOVEMBER
     * @see #DECEMBER
     * @see #UNDECIMBER
     */
    public final static int MONTH = 2;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * week number within the current year.  The first week of the year, as
     * defined by <code>getFirstDayOfWeek()</code> and
     * <code>getMinimalDaysInFirstWeek()</code>, has value 1.  Subclasses define
     * the value of <code>WEEK_OF_YEAR</code> for days before the first week of
     * the year.
     * get和set的字段编号表示当前年份中的周数。
     * 一年中的第一周，由getFirstDayOfWeek()和getMinimalDaysInFirstWeek()定义，值为 1。
     * 子类定义WEEK_OF_YEAR的值一年的第一周
     * @see #getFirstDayOfWeek
     * @see #getMinimalDaysInFirstWeek
     */
    public final static int WEEK_OF_YEAR = 3;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * week number within the current month.  The first week of the month, as
     * defined by <code>getFirstDayOfWeek()</code> and
     * <code>getMinimalDaysInFirstWeek()</code>, has value 1.  Subclasses define
     * the value of <code>WEEK_OF_MONTH</code> for days before the first week of
     * the month.
     * get和set的字段编号表示当月的周数。每月的第一周，
     * 由getFirstDayOfWeek()和getMinimalDaysInFirstWeek()定义，值为 1。
     * 子类定义WEEK_OF_MONTH在本月的第一周
     * @see #getFirstDayOfWeek
     * @see #getMinimalDaysInFirstWeek
     */
    public final static int WEEK_OF_MONTH = 4;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * day of the month. This is a synonym for <code>DAY_OF_MONTH</code>.
     * The first day of the month has value 1.
     * get和set的字段编号，指示月份中的哪一天。这是DAY_OF_MONTH的同义词。该月的第一天的值为 1。
     * @see #DAY_OF_MONTH
     */
    public final static int DATE = 5;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * day of the month. This is a synonym for <code>DATE</code>.
     * The first day of the month has value 1.
     * get和set的字段编号，指示月份中的哪一天。这是DATE的同义词。该月的第一天的值为 1
     * @see #DATE
     */
    public final static int DAY_OF_MONTH = 5;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the day
     * number within the current year.  The first day of the year has value 1.
     * get和set的字段编号表示当前年份中的天数。一年中的第一天的值为 1
     */
    public final static int DAY_OF_YEAR = 6;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the day
     * of the week.  This field takes values <code>SUNDAY</code>,
     * <code>MONDAY</code>, <code>TUESDAY</code>, <code>WEDNESDAY</code>,
     * <code>THURSDAY</code>, <code>FRIDAY</code>, and <code>SATURDAY</code>.
     * get和set的字段编号表示星期几。此字段采用值SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,
     * FRIDAY, 和SATURDAY
     * @see #SUNDAY
     * @see #MONDAY
     * @see #TUESDAY
     * @see #WEDNESDAY
     * @see #THURSDAY
     * @see #FRIDAY
     * @see #SATURDAY
     */
    public final static int DAY_OF_WEEK = 7;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * ordinal number of the day of the week within the current month. Together
     * with the <code>DAY_OF_WEEK</code> field, this uniquely specifies a day
     * within a month.  Unlike <code>WEEK_OF_MONTH</code> and
     * <code>WEEK_OF_YEAR</code>, this field's value does <em>not</em> depend on
     * <code>getFirstDayOfWeek()</code> or
     * <code>getMinimalDaysInFirstWeek()</code>.  <code>DAY_OF_MONTH 1</code>
     * through <code>7</code> always correspond to <code>DAY_OF_WEEK_IN_MONTH
     * 1</code>; <code>8</code> through <code>14</code> correspond to
     * <code>DAY_OF_WEEK_IN_MONTH 2</code>, and so on.
     * <code>DAY_OF_WEEK_IN_MONTH 0</code> indicates the week before
     * <code>DAY_OF_WEEK_IN_MONTH 1</code>.  Negative values count back from the
     * end of the month, so the last Sunday of a month is specified as
     * <code>DAY_OF_WEEK = SUNDAY, DAY_OF_WEEK_IN_MONTH = -1</code>.  Because
     * negative values count backward they will usually be aligned differently
     * within the month than positive values.  For example, if a month has 31
     * days, <code>DAY_OF_WEEK_IN_MONTH -1</code> will overlap
     * <code>DAY_OF_WEEK_IN_MONTH 5</code> and the end of <code>4</code>.
     * get和set的字段编号，指示当前月份中星期几的序号。与DAY_OF_WEEK字段一起，
     * 它唯一指定了一个月内的一天。与WEEK_OF_MONTH和WEEK_OF_YEAR不同，
     * 该字段的值不取决于getFirstDayOfWeek()或getMinimalDaysInFirstWeek()。
     * DAY_OF_MONTH 1到7总是对应于DAY_OF_WEEK_IN_MONTH 1； 8到 14对应DAY_OF_WEEK_IN_MONTH 2，
     * 依此类推。DAY_OF_WEEK_IN_MONTH 0表示DAY_OF_WEEK_IN_MONTH 1前一周。
     * 负值从月末开始倒计时，因此一个月的最后一个星期日指定为DAY_OF_WEEK = SUNDAY,
     * DAY_OF_WEEK_IN_MONTH = -1。由于负值向后计数，因此它们在一个月内的对齐方式通常与正值不同。
     * 例如，如果一个月有 31 天，DAY_OF_WEEK_IN_MONTH -1将与DAY_OF_WEEK_IN_MONTH 5
     * 和4的结尾重叠
     * @see #DAY_OF_WEEK
     * @see #WEEK_OF_MONTH
     */
    public final static int DAY_OF_WEEK_IN_MONTH = 8;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating
     * whether the <code>HOUR</code> is before or after noon.
     * E.g., at 10:04:15.250 PM the <code>AM_PM</code> is <code>PM</code>.
     * get和set的字段编号，指示HOUR是在中午之前还是之后。
     * 例如，在 10:04:15.250 PM，AM_PM是PM
     * @see #AM
     * @see #PM
     * @see #HOUR
     */
    public final static int AM_PM = 9;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * hour of the morning or afternoon. <code>HOUR</code> is used for the
     * 12-hour clock (0 - 11). Noon and midnight are represented by 0, not by 12.
     * E.g., at 10:04:15.250 PM the <code>HOUR</code> is 10.
     * get和set的字段编号指示上午或下午的时间。HOUR用于 12 小时制 (0 - 11)。
     * 中午和午夜由 0 表示，而不是 12。例如，在 10:04:15.250 PM，HOUR是 10
     * @see #AM_PM
     * @see #HOUR_OF_DAY
     */
    public final static int HOUR = 10;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * hour of the day. <code>HOUR_OF_DAY</code> is used for the 24-hour clock.
     * E.g., at 10:04:15.250 PM the <code>HOUR_OF_DAY</code> is 22.
     * get和set的字段编号指示一天中的小时。HOUR_OF_DAY用于 24 小时制。
     * 例如，在晚上 10:04:15.250，HOUR_OF_DAY是 22
     * @see #HOUR
     */
    public final static int HOUR_OF_DAY = 11;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * minute within the hour.
     * E.g., at 10:04:15.250 PM the <code>MINUTE</code> is 4.
     *get和set的字段编号表示一小时内的分钟。例如，在晚上 10:04:15.250，MINUTE是 4
     */
    public final static int MINUTE = 12;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * second within the minute.
     * E.g., at 10:04:15.250 PM the <code>SECOND</code> is 15.
     *get和set的字段编号表示一分钟内的秒。例如，在晚上 10:04:15.250，SECOND是 15。
     */
    public final static int SECOND = 13;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * millisecond within the second.
     * E.g., at 10:04:15.250 PM the <code>MILLISECOND</code> is 250.
     * get和set的字段编号指示秒内的毫秒数。例如，在晚上 10:04:15.250，MILLISECOND是 250
     */
    public final static int MILLISECOND = 14;

    /**
     * Field number for <code>get</code> and <code>set</code>
     * indicating the raw offset from GMT in milliseconds.
     * <p>
     * This field reflects the correct GMT offset value of the time
     * zone of this <code>Calendar</code> if the
     * <code>TimeZone</code> implementation subclass supports
     * historical GMT offset changes.
     * get和set的字段编号，指示与 GMT 的原始偏移量（以毫秒为单位）。
     * 如果TimeZone实现子类支持历史 GMT 偏移更改，则此字段反映此Calendar时区的正确 GMT 偏移值
     */
    public final static int ZONE_OFFSET = 15;

    /**
     * Field number for <code>get</code> and <code>set</code> indicating the
     * daylight saving offset in milliseconds.
     * 1.get和set的字段编号指示以毫秒为单位的夏令时偏移量。
     * <p>
     * This field reflects the correct daylight saving offset value of
     * the time zone of this <code>Calendar</code> if the
     * <code>TimeZone</code> implementation subclass supports
     * historical Daylight Saving Time schedule changes.
     * 2.如果TimeZone实现子类支持历史夏令时时间表更改，则此字段反映此Calendar时区的正确夏令时偏移值
     */
    public final static int DST_OFFSET = 16;

    /**
     * The number of distinct fields recognized by <code>get</code> and <code>set</code>.
     * Field numbers range from <code>0..FIELD_COUNT-1</code>.
     * get和set识别的不同字段的数量。字段编号范围从0..FIELD_COUNT-1
     */
    public final static int FIELD_COUNT = 17;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Sunday.
     * DAY_OF_WEEK字段的值表示星期日
     */
    public final static int SUNDAY = 1;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Monday.
     *DAY_OF_WEEK字段的值表示星期一。
     */
    public final static int MONDAY = 2;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Tuesday.
     * DAY_OF_WEEK字段的值表示星期二
     */
    public final static int TUESDAY = 3;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Wednesday.
     *DAY_OF_WEEK字段的值表示星期三
     */
    public final static int WEDNESDAY = 4;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Thursday.
     * DAY_OF_WEEK字段的值表示星期四。
     */
    public final static int THURSDAY = 5;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Friday.
     * DAY_OF_WEEK字段的值表示星期五
     */
    public final static int FRIDAY = 6;

    /**
     * Value of the {@link #DAY_OF_WEEK} field indicating
     * Saturday.
     * DAY_OF_WEEK字段的值表示星期六
     */
    public final static int SATURDAY = 7;

    /**
     * Value of the {@link #MONTH} field indicating the
     * first month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第一个月
     */
    public final static int JANUARY = 0;

    /**
     * Value of the {@link #MONTH} field indicating the
     * second month of the year in the Gregorian and Julian calendars.
     *  MONTH字段的值指示公历和儒略历中一年中的第二个月
     */
    public final static int FEBRUARY = 1;

    /**
     * Value of the {@link #MONTH} field indicating the
     * third month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第三个月
     */
    public final static int MARCH = 2;

    /**
     * Value of the {@link #MONTH} field indicating the
     * fourth month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第四个月
     */
    public final static int APRIL = 3;

    /**
     * Value of the {@link #MONTH} field indicating the
     * fifth month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第五个月
     */
    public final static int MAY = 4;

    /**
     * Value of the {@link #MONTH} field indicating the
     * sixth month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第六个月
     */
    public final static int JUNE = 5;

    /**
     * Value of the {@link #MONTH} field indicating the
     * seventh month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第七个月
     */
    public final static int JULY = 6;

    /**
     * Value of the {@link #MONTH} field indicating the
     * eighth month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第八个月
     */
    public final static int AUGUST = 7;

    /**
     * Value of the {@link #MONTH} field indicating the
     * ninth month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第九个月
     */
    public final static int SEPTEMBER = 8;

    /**
     * Value of the {@link #MONTH} field indicating the
     * tenth month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第十个月
     */
    public final static int OCTOBER = 9;

    /**
     * Value of the {@link #MONTH} field indicating the
     * eleventh month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第十一个月
     */
    public final static int NOVEMBER = 10;

    /**
     * Value of the {@link #MONTH} field indicating the
     * twelfth month of the year in the Gregorian and Julian calendars.
     * MONTH字段的值指示公历和儒略历中一年中的第十二个月
     */
    public final static int DECEMBER = 11;

    /**
     * Value of the {@link #MONTH} field indicating the
     * thirteenth month of the year. Although <code>GregorianCalendar</code>
     * does not use this value, lunar calendars do.
     * MONTH字段的值表示一年中的第十三个月。尽管GregorianCalendar不使用此值，但农历会使用
     */
    public final static int UNDECIMBER = 12;

    /**
     * Value of the {@link #AM_PM} field indicating the
     * period of the day from midnight to just before noon.
     * AM_PM字段的值指示一天中从午夜到中午之前的时间段
     */
    public final static int AM = 0;

    /**
     * Value of the {@link #AM_PM} field indicating the
     * period of the day from noon to just before midnight.
     * AM_PM字段的值指示一天中从中午到午夜之前的时间段
     */
    public final static int PM = 1;

    /**
     * A style specifier for {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} indicating names in all styles, such as
     * "January" and "Jan".
     * getDisplayNames(int, int, Locale)的样式说明符指示所有样式中的名称，例如“January”和“Jan”
     * @see #SHORT_FORMAT
     * @see #LONG_FORMAT
     * @see #SHORT_STANDALONE
     * @see #LONG_STANDALONE
     * @see #SHORT
     * @see #LONG
     * @since 1.6
     */
    public static final int ALL_STYLES = 0;

    static final int STANDALONE_MASK = 0x8000;

    /**
     * A style specifier for {@link #getDisplayName(int, int, Locale)
     * getDisplayName} and {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} equivalent to {@link #SHORT_FORMAT}.
     * getDisplayName(int, int, Locale)和 getDisplayNames(int, int, Locale)
     * 等价于  SHORT_FORMAT 的样式说明符
     * @see #SHORT_STANDALONE
     * @see #LONG
     * @since 1.6
     */
    public static final int SHORT = 1;

    /**
     * A style specifier for {@link #getDisplayName(int, int, Locale)
     * getDisplayName} and {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} equivalent to {@link #LONG_FORMAT}.
     * getDisplayName(int, int, Locale)和 getDisplayNames(int, int, Locale) 等
     * 价于LONG_FORMAT的样式说明符
     * @see #LONG_STANDALONE
     * @see #SHORT
     * @since 1.6
     */
    public static final int LONG = 2;

    /**
     * A style specifier for {@link #getDisplayName(int, int, Locale)
     * getDisplayName} and {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} indicating a narrow name used for format. Narrow names
     * are typically single character strings, such as "M" for Monday.
     * getDisplayName(int, int, Locale)和 getDisplayNames(int, int, Locale)
     * 的样式说明符指示用于格式的窄名称。窄名称通常是单个字符串，例如“M”代表星期一
     * @see #NARROW_STANDALONE
     * @see #SHORT_FORMAT
     * @see #LONG_FORMAT
     * @since 1.8
     */
    public static final int NARROW_FORMAT = 4;

    /**
     * A style specifier for {@link #getDisplayName(int, int, Locale)
     * getDisplayName} and {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} indicating a narrow name independently. Narrow names
     * are typically single character strings, such as "M" for Monday.
     * getDisplayName(int, int, Locale)和 getDisplayNames(int, int, Locale)
     * 的样式说明符独立指示窄名称。窄名称通常是单个字符串，例如“M”代表星期一。
     * @see #NARROW_FORMAT
     * @see #SHORT_STANDALONE
     * @see #LONG_STANDALONE
     * @since 1.8
     */
    public static final int NARROW_STANDALONE = NARROW_FORMAT | STANDALONE_MASK;

    /**
     * A style specifier for {@link #getDisplayName(int, int, Locale)
     * getDisplayName} and {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} indicating a short name used for format.
     * getDisplayName(int, int, Locale)和 getDisplayNames(int, int, Locale)
     * 的样式说明符指示用于格式的短名称
     * @see #SHORT_STANDALONE
     * @see #LONG_FORMAT
     * @see #LONG_STANDALONE
     * @since 1.8
     */
    public static final int SHORT_FORMAT = 1;

    /**
     * A style specifier for {@link #getDisplayName(int, int, Locale)
     * getDisplayName} and {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} indicating a long name used for format.
     * getDisplayName(int, int, Locale)和getDisplayNames(int, int, Locale) 的样式说明符指示用于格式的长名称
     * @see #LONG_STANDALONE
     * @see #SHORT_FORMAT
     * @see #SHORT_STANDALONE
     * @since 1.8
     */
    public static final int LONG_FORMAT = 2;

    /**
     * A style specifier for {@link #getDisplayName(int, int, Locale)
     * getDisplayName} and {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} indicating a short name used independently,
     * such as a month abbreviation as calendar headers.
     *getDisplayName(int, int, Locale)和 getDisplayNames(int, int, Locale)
     * 的样式说明符，指示独立使用的短名称，例如作为日历标题的月份缩写
     * @see #SHORT_FORMAT
     * @see #LONG_FORMAT
     * @see #LONG_STANDALONE
     * @since 1.8
     */
    public static final int SHORT_STANDALONE = SHORT | STANDALONE_MASK;

    /**
     * A style specifier for {@link #getDisplayName(int, int, Locale)
     * getDisplayName} and {@link #getDisplayNames(int, int, Locale)
     * getDisplayNames} indicating a long name used independently,
     * such as a month name as calendar headers.
     * getDisplayName(int, int, Locale)和 getDisplayNames(int, int, Locale)
     * 的样式说明符指示独立使用的长名称，例如作为日历标题的月份名称
     * @see #LONG_FORMAT
     * @see #SHORT_FORMAT
     * @see #SHORT_STANDALONE
     * @since 1.8
     */
    public static final int LONG_STANDALONE = LONG | STANDALONE_MASK;

    // Internal notes:
    // Calendar contains two kinds of time representations: current "time" in
    // milliseconds, and a set of calendar "fields" representing the current time.
    // The two representations are usually in sync, but can get out of sync
    // as follows.
    // 1. Initially, no fields are set, and the time is invalid.
    // 2. If the time is set, all fields are computed and in sync.
    // 3. If a single field is set, the time is invalid.
    // Recomputation of the time and fields happens when the object needs
    // to return a result to the user, or use a result for a computation.
    //内部说明： Calendar 包含两种时间表示：以毫秒为单位的当前“时间”，以及表示当前时间的一组日历“字段”。
    // 这两种表示通常是同步的，但也可能不同步，
    // 如下所示。 1. 最初没有设置字段，时间无效。
    // 2. 如果设置了时间，则所有字段都被计算并同步。
    // 3.如果设置了单个字段，则时间无效。当对象需要将结果返回给用户或使用结果进行计算时，会重新计算时间和字段

    /**
     * The calendar field values for the currently set time for this calendar.
     * This is an array of <code>FIELD_COUNT</code> integers, with index values
     * <code>ERA</code> through <code>DST_OFFSET</code>.
     * 此日历的当前设置时间的日历字段值。这是一个FIELD_COUNT整数数组，
     * 索引值是ERA到 DST_OFFSET
     * @serial
     */
    @SuppressWarnings("ProtectedField")
    protected int           fields[];

    /**
     * The flags which tell if a specified calendar field for the calendar is set.
     * A new object has no fields set.  After the first call to a method
     * which generates the fields, they all remain set after that.
     * This is an array of <code>FIELD_COUNT</code> booleans, with index values
     * <code>ERA</code> through <code>DST_OFFSET</code>.
     * 指示是否设置了日历的指定日历字段的标志。新对象没有设置字段。在第一次调用生成字段的方法之后，
     * 它们都保持设置。这是一个FIELD_COUNT布尔值数组，索引值ERA到DST_OFFSET
     * @serial
     */
    @SuppressWarnings("ProtectedField")
    protected boolean       isSet[];

    /**
     * Pseudo-time-stamps which specify when each field was set. There
     * are two special values, UNSET and COMPUTED. Values from
     * MINIMUM_USER_SET to Integer.MAX_VALUE are legal user set values.
     * 伪时间戳，指定何时设置每个字段。有两个特殊值，UNSET 和 COMPUTED。
     * 从 MINIMUM_USER_SET 到 Integer.MAX_VALUE 的值是合法的用户设置值。
     */
    transient private int   stamp[];

    /**
     * The currently set time for this calendar, expressed in milliseconds after
     * January 1, 1970, 0:00:00 GMT.
     * 此日历的当前设置时间，以 1970 年 1 月 1 日格林威治标准时间 0:00:00 之后的毫秒数表示
     * @see #isTimeSet
     * @serial
     */
    @SuppressWarnings("ProtectedField")
    protected long          time;

    /**
     * True if then the value of <code>time</code> is valid.
     * The time is made invalid by a change to an item of <code>field[]</code>.
     * 如果time的值有效，则为 True。通过更改field[]的项目使时间无效
     * @see #time
     * @serial
     */
    @SuppressWarnings("ProtectedField")
    protected boolean       isTimeSet;

    /**
     * True if <code>fields[]</code> are in sync with the currently set time.
     * If false, then the next attempt to get the value of a field will
     * force a recomputation of all fields from the current value of
     * <code>time</code>.
     * 如果fields[]与当前设置的时间同步，则为真。如果为 false，
     * 则下一次获取字段值的尝试将强制从time的当前值重新计算所有字段
     * @serial
     */
    @SuppressWarnings("ProtectedField")
    protected boolean       areFieldsSet;

    /**
     * True if all fields have been set.
     * 如果已设置所有字段，则为真
     * @serial
     */
    transient boolean       areAllFieldsSet;

    /**
     * <code>True</code> if this calendar allows out-of-range field values during computation
     * of <code>time</code> from <code>fields[]</code>.
     * True如果此日历在从fields[]计算time期间允许超出范围的字段值
     * @see #setLenient
     * @see #isLenient
     * @serial
     */
    private boolean         lenient = true;

    /**
     * The <code>TimeZone</code> used by this calendar. <code>Calendar</code>
     * uses the time zone data to translate between locale and GMT time.
     * 此日历使用的TimeZone。Calendar使用时区数据在区域设置和 GMT 时间之间进行转换
     * @serial
     */
    private TimeZone        zone;

    /**
     * <code>True</code> if zone references to a shared TimeZone object.
     *True如果区域引用共享的 TimeZone 对象
     */
    transient private boolean sharedZone = false;

    /**
     * The first day of the week, with possible values <code>SUNDAY</code>,
     * <code>MONDAY</code>, etc.  This is a locale-dependent value.
     * 一周的第一天，可能的值为 SUNDAY、MONDAY等。这是一个与语言环境相关的值
     * @serial
     */
    private int             firstDayOfWeek;

    /**
     * The number of days required for the first week in a month or year,
     * with possible values from 1 to 7.  This is a locale-dependent value.
     * 一个月或一年中第一周所需的天数，可能的值从 1 到 7。这是一个与区域设置相关的值
     * @serial
     */
    private int             minimalDaysInFirstWeek;

    /**
     * Cache to hold the firstDayOfWeek and minimalDaysInFirstWeek
     * of a Locale.
     * 缓存以保存语言环境的 firstDayOfWeek 和 minimumDaysInFirstWeek
     */
    private static final ConcurrentMap<Locale, int[]> cachedLocaleData
        = new ConcurrentHashMap<>(3);

    // Special values of stamp[]
    /**
     * The corresponding fields[] has no value.
     * 相应的 fields[] 没有值
     */
    private static final int        UNSET = 0;

    /**
     * The value of the corresponding fields[] has been calculated internally.
     * 对应的 fields[] 的值已经在内部计算出来了。
     */
    private static final int        COMPUTED = 1;

    /**
     * The value of the corresponding fields[] has been set externally. Stamp
     * values which are greater than 1 represents the (pseudo) time when the
     * corresponding fields[] value was set.
     * 相应字段[] 的值已在外部设置。大于 1 的标记值表示设置相应字段 [] 值时的（伪）时间
     */
    private static final int        MINIMUM_USER_STAMP = 2;

    /**
     * The mask value that represents all of the fields.
     * 代表所有字段的掩码值。
     */
    static final int ALL_FIELDS = (1 << FIELD_COUNT) - 1;

    /**
     * The next available value for <code>stamp[]</code>, an internal array.
     * This actually should not be written out to the stream, and will probably
     * be removed from the stream in the near future.  In the meantime,
     * a value of <code>MINIMUM_USER_STAMP</code> should be used.
     * stamp[]的下一个可用值，一个内部数组。这实际上不应该写出到流中，
     * 并且可能会在不久的将来从流中删除。
     * 同时，应该使用MINIMUM_USER_STAMP的值。
     * @serial
     */
    private int             nextStamp = MINIMUM_USER_STAMP;

    // the internal serial version which says which version was written
    // - 0 (default) for version up to JDK 1.1.5
    // - 1 for version from JDK 1.1.6, which writes a correct 'time' value
    //     as well as compatible values for other fields.  This is a
    //     transitional format.
    // - 2 (not implemented yet) a future version, in which fields[],
    //     areFieldsSet, and isTimeSet become transient, and isSet[] is
    //     removed. In JDK 1.1.6 we write a format compatible with version 2.
    //内部串行版本，说明写入的版本
    // - 0（默认）代表 JDK 1.1.5 之前的版本
    // - 1 代表 JDK 1.1.6 的版本，它写入正确的“时间”值以及其他字段的兼容值。这是一种过渡格式。
    // - 2（尚未实现）未来版本，其中 fields[]、areFieldsSet 和 isTimeSet 变为瞬态，并删除 isSet[]。
    // 在 JDK 1.1.6 中，我们编写了与版本 2 兼容的格式。
    static final int        currentSerialVersion = 1;

    /**
     * The version of the serialized data on the stream.  Possible values:
     * <dl>
     * <dt><b>0</b> or not present on stream</dt>
     * <dd>
     * JDK 1.1.5 or earlier.
     * </dd>
     * <dt><b>1</b></dt>
     * <dd>
     * JDK 1.1.6 or later.  Writes a correct 'time' value
     * as well as compatible values for other fields.  This is a
     * transitional format.
     * </dd>
     * </dl>
     * When streaming out this class, the most recent format
     * and the highest allowable <code>serialVersionOnStream</code>
     * is written.
     * 流中序列化数据的版本。 可能的值：
     * 0或不存在于流中
     * JDK 1.1.5 或更早版本。
     * 1
     * JDK 1.1.6 或更高版本。 写入正确的“时间”值以及其他字段的兼容值。 这是一种过渡格式。
     * 流式输出此类时，将写入最新的格式和允许的最高serialVersionOnStream 。
     * @serial
     * @since JDK1.1.6
     */
    private int             serialVersionOnStream = currentSerialVersion;

    // Proclaim serialization compatibility with JDK 1.1
    static final long       serialVersionUID = -1807547505821590642L;

    // Mask values for calendar fields
    @SuppressWarnings("PointlessBitwiseExpression")
    final static int ERA_MASK           = (1 << ERA);
    final static int YEAR_MASK          = (1 << YEAR);
    final static int MONTH_MASK         = (1 << MONTH);
    final static int WEEK_OF_YEAR_MASK  = (1 << WEEK_OF_YEAR);
    final static int WEEK_OF_MONTH_MASK = (1 << WEEK_OF_MONTH);
    final static int DAY_OF_MONTH_MASK  = (1 << DAY_OF_MONTH);
    final static int DATE_MASK          = DAY_OF_MONTH_MASK;
    final static int DAY_OF_YEAR_MASK   = (1 << DAY_OF_YEAR);
    final static int DAY_OF_WEEK_MASK   = (1 << DAY_OF_WEEK);
    final static int DAY_OF_WEEK_IN_MONTH_MASK  = (1 << DAY_OF_WEEK_IN_MONTH);
    final static int AM_PM_MASK         = (1 << AM_PM);
    final static int HOUR_MASK          = (1 << HOUR);
    final static int HOUR_OF_DAY_MASK   = (1 << HOUR_OF_DAY);
    final static int MINUTE_MASK        = (1 << MINUTE);
    final static int SECOND_MASK        = (1 << SECOND);
    final static int MILLISECOND_MASK   = (1 << MILLISECOND);
    final static int ZONE_OFFSET_MASK   = (1 << ZONE_OFFSET);
    final static int DST_OFFSET_MASK    = (1 << DST_OFFSET);

    /**
     * {@code Calendar.Builder} is used for creating a {@code Calendar} from
     * various date-time parameters.
     * 1.Calendar.Builder用于从各种日期时间参数创建Calendar
     * <p>There are two ways to set a {@code Calendar} to a date-time value. One
     * is to set the instant parameter to a millisecond offset from the <a
     * href="Calendar.html#Epoch">Epoch</a>. The other is to set individual
     * field parameters, such as {@link Calendar#YEAR YEAR}, to their desired
     * values. These two ways can't be mixed. Trying to set both the instant and
     * individual fields will cause an {@link IllegalStateException} to be
     * thrown. However, it is permitted to override previous values of the
     * instant or field parameters.
     * 2.有两种方法可以将Calendar设置为日期时间值。
     * 一种是将 Instant 参数设置为距Epoch的毫秒偏移量。
     * 另一种方法是将各个字段参数（例如Calendar.YEAR设置为所需的值。这两种方式不能混用。
     * 尝试同时设置即时字段和单个字段将导致抛出IllegalStateException。然而，允许覆盖瞬时或场参数的先前值
     * <p>If no enough field parameters are given for determining date and/or
     * time, calendar specific default values are used when building a
     * {@code Calendar}. For example, if the {@link Calendar#YEAR YEAR} value
     * isn't given for the Gregorian calendar, 1970 will be used. If there are
     * any conflicts among field parameters, the <a
     * href="Calendar.html#resolution"> resolution rules</a> are applied.
     * Therefore, the order of field setting matters.
     * 3.如果没有提供足够的字段参数来确定日期和/或时间，则在构建Calendar时将使用日历特定的默认值。
     * 例如，如果没有为公历指定Calendar.YEAR值，则将使用 1970。如果字段参数之间存在任何冲突，
     * 则应用<a href="Calendar.htmlresolution">解析规则。因此，字段设置的顺序很重要
     * <p>In addition to the date-time parameters,
     * the {@linkplain #setLocale(Locale) locale},
     * {@linkplain #setTimeZone(TimeZone) time zone},
     * {@linkplain #setWeekDefinition(int, int) week definition}, and
     * {@linkplain #setLenient(boolean) leniency mode} parameters can be set.
     * 4.除了日期时间参数，setLocale(Locale)、setTimeZone(TimeZone)、setWeekDefinition(int, int) 周定义
     * 和setLenient( boolean) leniency mode} 参数可以设置。
     * <p><b>Examples</b>
     * <p>The following are sample usages. Sample code assumes that the
     * {@code Calendar} constants are statically imported.
     * 5.以下是示例用法。示例代码假定Calendar常量是静态导入的。
     * <p>The following code produces a {@code Calendar} with date 2012-12-31
     * (Gregorian) because Monday is the first day of a week with the <a
     * href="GregorianCalendar.html#iso8601_compatible_setting"> ISO 8601
     * compatible week parameters</a>.
     * 6.以下代码生成日期为 2012-12-31 (Gregorian) 的Calendar，
     * 因为星期一是具有ISO 8601 兼容周参数的一周的第一天
     * <pre>
     *   Calendar cal = new Calendar.Builder().setCalendarType("iso8601")
     *                        .setWeekDate(2013, 1, MONDAY).build();</pre>
     * <p>The following code produces a Japanese {@code Calendar} with date
     * 1989-01-08 (Gregorian), assuming that the default {@link Calendar#ERA ERA}
     * is <em>Heisei</em> that started on that day.
     * 7.以下代码生成日期为 1989-01-08（公历）的日语Calendar，假设默认Calendar.ERA是从那天开始的Heisei
     * <pre>
     *   Calendar cal = new Calendar.Builder().setCalendarType("japanese")
     *                        .setFields(YEAR, 1, DAY_OF_YEAR, 1).build();</pre>
     *
     * @since 1.8
     * @see Calendar#getInstance(TimeZone, Locale)
     * @see Calendar#fields
     */
    public static class Builder {
        private static final int NFIELDS = FIELD_COUNT + 1; // +1 for WEEK_YEAR
        private static final int WEEK_YEAR = FIELD_COUNT;

        private long instant;
        // Calendar.stamp[] (lower half) and Calendar.fields[] (upper half) combined
        private int[] fields;
        // Pseudo timestamp starting from MINIMUM_USER_STAMP.
        // (COMPUTED is used to indicate that the instant has been set.)
        private int nextStamp;
        // maxFieldIndex keeps the max index of fields which have been set.
        // (WEEK_YEAR is never included.)
        private int maxFieldIndex;
        private String type;
        private TimeZone zone;
        private boolean lenient = true;
        private Locale locale;
        private int firstDayOfWeek, minimalDaysInFirstWeek;

        /**
         * Constructs a {@code Calendar.Builder}.
         */
        public Builder() {
        }

        /**
         * Sets the instant parameter to the given {@code instant} value that is
         * a millisecond offset from <a href="Calendar.html#Epoch">the
         * Epoch</a>.
         * 将 Instant 参数设置为给定的instant值，该值是 <a href="Calendar.htmlEpoch">Epoch<a> 的毫秒偏移量
         * @param instant a millisecond offset from the Epoch
         * @return this {@code Calendar.Builder}
         * @throws IllegalStateException if any of the field parameters have
         *                               already been set
         * @see Calendar#setTime(Date)
         * @see Calendar#setTimeInMillis(long)
         * @see Calendar#time
         */
        public Builder setInstant(long instant) {
            if (fields != null) {
                throw new IllegalStateException();
            }
            this.instant = instant;
            nextStamp = COMPUTED;
            return this;
        }

        /**
         * Sets the instant parameter to the {@code instant} value given by a
         * {@link Date}. This method is equivalent to a call to
         * {@link #setInstant(long) setInstant(instant.getTime())}.
         * 将 Instant 参数设置为Date给出的instant值。
         * 此方法等效于调用setInstant(instant.getTime())。
         * @param instant a {@code Date} representing a millisecond offset from
         *                the Epoch
         * @return this {@code Calendar.Builder}
         * @throws NullPointerException  if {@code instant} is {@code null}
         * @throws IllegalStateException if any of the field parameters have
         *                               already been set
         * @see Calendar#setTime(Date)
         * @see Calendar#setTimeInMillis(long)
         * @see Calendar#time
         */
        public Builder setInstant(Date instant) {
            return setInstant(instant.getTime()); // NPE if instant == null
        }

        /**
         * Sets the {@code field} parameter to the given {@code value}.
         * {@code field} is an index to the {@link Calendar#fields}, such as
         * {@link Calendar#DAY_OF_MONTH DAY_OF_MONTH}. Field value validation is
         * not performed in this method. Any out of range values are either
         * normalized in lenient mode or detected as an invalid value in
         * non-lenient mode when building a {@code Calendar}.
         * 将field参数设置为给定的value。field是Calendar.fields的索引，
         * 例如Calendar.DAY_OF_MONTH。此方法不执行字段值验证。在构建Calendar时，
         * 任何超出范围的值要么在宽松模式下标准化，要么在非宽松模式下检测为无效值。
         * @param field an index to the {@code Calendar} fields
         * @param value the field value
         * @return this {@code Calendar.Builder}
         * @throws IllegalArgumentException if {@code field} is invalid
         * @throws IllegalStateException if the instant value has already been set,
         *                      or if fields have been set too many
         *                      (approximately {@link Integer#MAX_VALUE}) times.
         * @see Calendar#set(int, int)
         */
        public Builder set(int field, int value) {
            // Note: WEEK_YEAR can't be set with this method.
            if (field < 0 || field >= FIELD_COUNT) {
                throw new IllegalArgumentException("field is invalid");
            }
            if (isInstantSet()) {
                throw new IllegalStateException("instant has been set");
            }
            allocateFields();
            internalSet(field, value);
            return this;
        }

        /**
         * Sets field parameters to their values given by
         * {@code fieldValuePairs} that are pairs of a field and its value.
         * For example,
         * <pre>
         *   setFeilds(Calendar.YEAR, 2013,
         *             Calendar.MONTH, Calendar.DECEMBER,
         *             Calendar.DAY_OF_MONTH, 23);</pre>
         * is equivalent to the sequence of the following
         * {@link #set(int, int) set} calls:
         * <pre>
         *   set(Calendar.YEAR, 2013)
         *   .set(Calendar.MONTH, Calendar.DECEMBER)
         *   .set(Calendar.DAY_OF_MONTH, 23);</pre>
         *
         * @param fieldValuePairs field-value pairs
         * @return this {@code Calendar.Builder}
         * @throws NullPointerException if {@code fieldValuePairs} is {@code null}
         * @throws IllegalArgumentException if any of fields are invalid,
         *             or if {@code fieldValuePairs.length} is an odd number.
         * @throws IllegalStateException    if the instant value has been set,
         *             or if fields have been set too many (approximately
         *             {@link Integer#MAX_VALUE}) times.
         */
        public Builder setFields(int... fieldValuePairs) {
            int len = fieldValuePairs.length;
            if ((len % 2) != 0) {
                throw new IllegalArgumentException();
            }
            if (isInstantSet()) {
                throw new IllegalStateException("instant has been set");
            }
            if ((nextStamp + len / 2) < 0) {
                throw new IllegalStateException("stamp counter overflow");
            }
            allocateFields();
            for (int i = 0; i < len; ) {
                int field = fieldValuePairs[i++];
                // Note: WEEK_YEAR can't be set with this method.
                if (field < 0 || field >= FIELD_COUNT) {
                    throw new IllegalArgumentException("field is invalid");
                }
                internalSet(field, fieldValuePairs[i++]);
            }
            return this;
        }

        /**
         * Sets the date field parameters to the values given by {@code year},
         * {@code month}, and {@code dayOfMonth}. This method is equivalent to
         * a call to:
         * <pre>
         *   setFields(Calendar.YEAR, year,
         *             Calendar.MONTH, month,
         *             Calendar.DAY_OF_MONTH, dayOfMonth);</pre>
         * 将日期字段参数设置为 year、month和dayOfMonth给出的值。此方法等效于调用：
         * @param year       the {@link Calendar#YEAR YEAR} value
         * @param month      the {@link Calendar#MONTH MONTH} value
         *                   (the month numbering is <em>0-based</em>).
         * @param dayOfMonth the {@link Calendar#DAY_OF_MONTH DAY_OF_MONTH} value
         * @return this {@code Calendar.Builder}
         */
        public Builder setDate(int year, int month, int dayOfMonth) {
            return setFields(YEAR, year, MONTH, month, DAY_OF_MONTH, dayOfMonth);
        }

        /**
         * Sets the time of day field parameters to the values given by
         * {@code hourOfDay}, {@code minute}, and {@code second}. This method is
         * equivalent to a call to:
         * <pre>
         *   setTimeOfDay(hourOfDay, minute, second, 0);</pre>
         * 将时间字段参数设置为hourOfDay 、 minute和second给定的值。 此方法等效于调用：
         *              setTimeOfDay(hourOfDay, minute, second, 0)
         * @param hourOfDay the {@link Calendar#HOUR_OF_DAY HOUR_OF_DAY} value
         *                  (24-hour clock)
         * @param minute    the {@link Calendar#MINUTE MINUTE} value
         * @param second    the {@link Calendar#SECOND SECOND} value
         * @return this {@code Calendar.Builder}
         */
        public Builder setTimeOfDay(int hourOfDay, int minute, int second) {
            return setTimeOfDay(hourOfDay, minute, second, 0);
        }

        /**
         * Sets the time of day field parameters to the values given by
         * {@code hourOfDay}, {@code minute}, {@code second}, and
         * {@code millis}. This method is equivalent to a call to:
         * <pre>
         *   setFields(Calendar.HOUR_OF_DAY, hourOfDay,
         *             Calendar.MINUTE, minute,
         *             Calendar.SECOND, second,
         *             Calendar.MILLISECOND, millis);</pre>
         *
         将时间字段参数设置为hourOfDay 、 minute 、 second和millis给定的值。 此方法等效于调用：
         setFields(Calendar.HOUR_OF_DAY, hourOfDay,
         Calendar.MINUTE, minute,
         Calendar.SECOND, second,
         Calendar.MILLISECOND, millis);
         * @param hourOfDay the {@link Calendar#HOUR_OF_DAY HOUR_OF_DAY} value
         *                  (24-hour clock)
         * @param minute    the {@link Calendar#MINUTE MINUTE} value
         * @param second    the {@link Calendar#SECOND SECOND} value
         * @param millis    the {@link Calendar#MILLISECOND MILLISECOND} value
         * @return this {@code Calendar.Builder}
         */
        public Builder setTimeOfDay(int hourOfDay, int minute, int second, int millis) {
            return setFields(HOUR_OF_DAY, hourOfDay, MINUTE, minute,
                             SECOND, second, MILLISECOND, millis);
        }

        /**
         * Sets the week-based date parameters to the values with the given
         * date specifiers - week year, week of year, and day of week.
         *
         * <p>If the specified calendar doesn't support week dates, the
         * {@link #build() build} method will throw an {@link IllegalArgumentException}.
         * 将基于周的日期参数设置为具有给定日期说明符的值 - 周年、一年中的一周和一周中的某一天。
         * 如果指定的日历不支持星期日期，build()方法将抛出一个IllegalArgumentException
         * @param weekYear   the week year
         * @param weekOfYear the week number based on {@code weekYear}
         * @param dayOfWeek  the day of week value: one of the constants
         *     for the {@link Calendar#DAY_OF_WEEK DAY_OF_WEEK} field:
         *     {@link Calendar#SUNDAY SUNDAY}, ..., {@link Calendar#SATURDAY SATURDAY}.
         * @return this {@code Calendar.Builder}
         * @see Calendar#setWeekDate(int, int, int)
         * @see Calendar#isWeekDateSupported()
         */
        public Builder setWeekDate(int weekYear, int weekOfYear, int dayOfWeek) {
            allocateFields();
            internalSet(WEEK_YEAR, weekYear);
            internalSet(WEEK_OF_YEAR, weekOfYear);
            internalSet(DAY_OF_WEEK, dayOfWeek);
            return this;
        }

        /**
         * Sets the time zone parameter to the given {@code zone}. If no time
         * zone parameter is given to this {@code Caledar.Builder}, the
         * {@linkplain TimeZone#getDefault() default
         * <code>TimeZone</code>} will be used in the {@link #build() build}
         * method.
         * 将时区参数设置为给定的zone。如果没有为此Caledar.Builder提供时区参数，
         * 则TimeZone.getDefault() 默认TimeZone将用于build()方法。
         * @param zone the {@link TimeZone}
         * @return this {@code Calendar.Builder}
         * @throws NullPointerException if {@code zone} is {@code null}
         * @see Calendar#setTimeZone(TimeZone)
         */
        public Builder setTimeZone(TimeZone zone) {
            if (zone == null) {
                throw new NullPointerException();
            }
            this.zone = zone;
            return this;
        }

        /**
         * Sets the lenient mode parameter to the value given by {@code lenient}.
         * If no lenient parameter is given to this {@code Calendar.Builder},
         * lenient mode will be used in the {@link #build() build} method.
         * 将 lenient 模式参数设置为lenient给定的值。
         * 如果没有为此Calendar.Builder提供 lenient 参数，则在build()方法中将使用 lenient 模式
         * @param lenient {@code true} for lenient mode;
         *                {@code false} for non-lenient mode
         * @return this {@code Calendar.Builder}
         * @see Calendar#setLenient(boolean)
         */
        public Builder setLenient(boolean lenient) {
            this.lenient = lenient;
            return this;
        }

        /**
         * Sets the calendar type parameter to the given {@code type}. The
         * calendar type given by this method has precedence over any explicit
         * or implicit calendar type given by the
         * {@linkplain #setLocale(Locale) locale}.
         *
         * <p>In addition to the available calendar types returned by the
         * {@link Calendar#getAvailableCalendarTypes() Calendar.getAvailableCalendarTypes}
         * method, {@code "gregorian"} and {@code "iso8601"} as aliases of
         * {@code "gregory"} can be used with this method.
         *
         将日历类型参数设置为给定的type 。 此方法给出的日历类型优先于locale给出的任何显式或隐式日历类型。
         除了Calendar.getAvailableCalendarTypes方法返回的可用日历类型之外，
         "gregorian"和"iso8601"作为"gregory"别名也可以与此方法一起使用
         * @param type the calendar type
         * @return this {@code Calendar.Builder}
         * @throws NullPointerException if {@code type} is {@code null}
         * @throws IllegalArgumentException if {@code type} is unknown
         * @throws IllegalStateException if another calendar type has already been set
         * @see Calendar#getCalendarType()
         * @see Calendar#getAvailableCalendarTypes()
         */
        public Builder setCalendarType(String type) {
            if (type.equals("gregorian")) { // NPE if type == null
                type = "gregory";
            }
            if (!Calendar.getAvailableCalendarTypes().contains(type)
                    && !type.equals("iso8601")) {
                throw new IllegalArgumentException("unknown calendar type: " + type);
            }
            if (this.type == null) {
                this.type = type;
            } else {
                if (!this.type.equals(type)) {
                    throw new IllegalStateException("calendar type override");
                }
            }
            return this;
        }

        /**
         * Sets the locale parameter to the given {@code locale}. If no locale
         * is given to this {@code Calendar.Builder}, the {@linkplain
         * Locale#getDefault(Locale.Category) default <code>Locale</code>}
         * for {@link Locale.Category#FORMAT} will be used.
         * 1.将 locale 参数设置为给定的locale。如果没有为此Calendar.Builder指定语言环境，
         * 则将使用Locale.CategoryFORMAT的 Locale.getDefault(Locale.Category) 默认Locale。
         * <p>If no calendar type is explicitly given by a call to the
         * {@link #setCalendarType(String) setCalendarType} method,
         * the {@code Locale} value is used to determine what type of
         * {@code Calendar} to be built.
         * 2.如果调用setCalendarType(String)方法未明确指定日历类型，
         * 则使用Locale值来确定要构建的Calendar类型。
         * <p>If no week definition parameters are explicitly given by a call to
         * the {@link #setWeekDefinition(int,int) setWeekDefinition} method, the
         * {@code Locale}'s default values are used.
         * 3.如果调用setWeekDefinition(int,int)方法没有明确给出周定义参数，则使用Locale的默认值
         * @param locale the {@link Locale}
         * @throws NullPointerException if {@code locale} is {@code null}
         * @return this {@code Calendar.Builder}
         * @see Calendar#getInstance(Locale)
         */
        public Builder setLocale(Locale locale) {
            if (locale == null) {
                throw new NullPointerException();
            }
            this.locale = locale;
            return this;
        }

        /**
         * Sets the week definition parameters to the values given by
         * {@code firstDayOfWeek} and {@code minimalDaysInFirstWeek} that are
         * used to determine the <a href="Calendar.html#First_Week">first
         * week</a> of a year. The parameters given by this method have
         * precedence over the default values given by the
         * {@linkplain #setLocale(Locale) locale}.
         * 将周定义参数设置为firstDayOfWeek和minimumDaysInFirstWeek给出的值，
         * 用于确定一年的<a href="Calendar.htmlFirst_Week">第一周<a>。
         * 此方法给出的参数优先于setLocale(Locale)给出的默认值
         * @param firstDayOfWeek the first day of a week; one of
         *                       {@link Calendar#SUNDAY} to {@link Calendar#SATURDAY}
         * @param minimalDaysInFirstWeek the minimal number of days in the first
         *                               week (1..7)
         * @return this {@code Calendar.Builder}
         * @throws IllegalArgumentException if {@code firstDayOfWeek} or
         *                                  {@code minimalDaysInFirstWeek} is invalid
         * @see Calendar#getFirstDayOfWeek()
         * @see Calendar#getMinimalDaysInFirstWeek()
         */
        public Builder setWeekDefinition(int firstDayOfWeek, int minimalDaysInFirstWeek) {
            if (!isValidWeekParameter(firstDayOfWeek)
                    || !isValidWeekParameter(minimalDaysInFirstWeek)) {
                throw new IllegalArgumentException();
            }
            this.firstDayOfWeek = firstDayOfWeek;
            this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
            return this;
        }

        /**
         * Returns a {@code Calendar} built from the parameters set by the
         * setter methods. The calendar type given by the {@link #setCalendarType(String)
         * setCalendarType} method or the {@linkplain #setLocale(Locale) locale} is
         * used to determine what {@code Calendar} to be created. If no explicit
         * calendar type is given, the locale's default calendar is created.
         *
         * <p>If the calendar type is {@code "iso8601"}, the
         * {@linkplain GregorianCalendar#setGregorianChange(Date) Gregorian change date}
         * of a {@link GregorianCalendar} is set to {@code Date(Long.MIN_VALUE)}
         * to be the <em>proleptic</em> Gregorian calendar. Its week definition
         * parameters are also set to be <a
         * href="GregorianCalendar.html#iso8601_compatible_setting">compatible
         * with the ISO 8601 standard</a>. Note that the
         * {@link GregorianCalendar#getCalendarType() getCalendarType} method of
         * a {@code GregorianCalendar} created with {@code "iso8601"} returns
         * {@code "gregory"}.
         *
         * <p>The default values are used for locale and time zone if these
         * parameters haven't been given explicitly.
         *
         * <p>Any out of range field values are either normalized in lenient
         * mode or detected as an invalid value in non-lenient mode.
         *
         * @return a {@code Calendar} built with parameters of this {@code
         *         Calendar.Builder}
         * @throws IllegalArgumentException if the calendar type is unknown, or
         *             if any invalid field values are given in non-lenient mode, or
         *             if a week date is given for the calendar type that doesn't
         *             support week dates.
         * @see Calendar#getInstance(TimeZone, Locale)
         * @see Locale#getDefault(Locale.Category)
         * @see TimeZone#getDefault()
         */
        public Calendar build() {
            if (locale == null) {
                locale = Locale.getDefault();
            }
            if (zone == null) {
                zone = TimeZone.getDefault();
            }
            Calendar cal;
            if (type == null) {
                type = locale.getUnicodeLocaleType("ca");
            }
            if (type == null) {
                if (locale.getCountry() == "TH"
                    && locale.getLanguage() == "th") {
                    type = "buddhist";
                } else {
                    type = "gregory";
                }
            }
            switch (type) {
            case "gregory":
                cal = new GregorianCalendar(zone, locale, true);
                break;
            case "iso8601":
                GregorianCalendar gcal = new GregorianCalendar(zone, locale, true);
                // make gcal a proleptic Gregorian
                gcal.setGregorianChange(new Date(Long.MIN_VALUE));
                // and week definition to be compatible with ISO 8601
                setWeekDefinition(MONDAY, 4);
                cal = gcal;
                break;
            case "buddhist":
                cal = new BuddhistCalendar(zone, locale);
                cal.clear();
                break;
            case "japanese":
                cal = new JapaneseImperialCalendar(zone, locale, true);
                break;
            default:
                throw new IllegalArgumentException("unknown calendar type: " + type);
            }
            cal.setLenient(lenient);
            if (firstDayOfWeek != 0) {
                cal.setFirstDayOfWeek(firstDayOfWeek);
                cal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
            }
            if (isInstantSet()) {
                cal.setTimeInMillis(instant);
                cal.complete();
                return cal;
            }

            if (fields != null) {
                boolean weekDate = isSet(WEEK_YEAR)
                                       && fields[WEEK_YEAR] > fields[YEAR];
                if (weekDate && !cal.isWeekDateSupported()) {
                    throw new IllegalArgumentException("week date is unsupported by " + type);
                }

                // Set the fields from the min stamp to the max stamp so that
                // the fields resolution works in the Calendar.
                for (int stamp = MINIMUM_USER_STAMP; stamp < nextStamp; stamp++) {
                    for (int index = 0; index <= maxFieldIndex; index++) {
                        if (fields[index] == stamp) {
                            cal.set(index, fields[NFIELDS + index]);
                            break;
                        }
                    }
                }

                if (weekDate) {
                    int weekOfYear = isSet(WEEK_OF_YEAR) ? fields[NFIELDS + WEEK_OF_YEAR] : 1;
                    int dayOfWeek = isSet(DAY_OF_WEEK)
                                    ? fields[NFIELDS + DAY_OF_WEEK] : cal.getFirstDayOfWeek();
                    cal.setWeekDate(fields[NFIELDS + WEEK_YEAR], weekOfYear, dayOfWeek);
                }
                cal.complete();
            }

            return cal;
        }

        private void allocateFields() {
            if (fields == null) {
                fields = new int[NFIELDS * 2];
                nextStamp = MINIMUM_USER_STAMP;
                maxFieldIndex = -1;
            }
        }

        private void internalSet(int field, int value) {
            fields[field] = nextStamp++;
            if (nextStamp < 0) {
                throw new IllegalStateException("stamp counter overflow");
            }
            fields[NFIELDS + field] = value;
            if (field > maxFieldIndex && field < WEEK_YEAR) {
                maxFieldIndex = field;
            }
        }

        private boolean isInstantSet() {
            return nextStamp == COMPUTED;
        }

        private boolean isSet(int index) {
            return fields != null && fields[index] > UNSET;
        }

        private boolean isValidWeekParameter(int value) {
            return value > 0 && value <= 7;
        }
    }

    /**
     * Constructs a Calendar with the default time zone
     * and the default {@link java.util.Locale.Category#FORMAT FORMAT}
     * locale.
     * 使用默认时区和默认java.util.Locale.CategoryFORMAT语言环境构造日历。
     * @see     TimeZone#getDefault
     */
    protected Calendar()
    {
        this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
        sharedZone = true;
    }

    /**
     * Constructs a calendar with the specified time zone and locale.
     * 构造具有指定时区和区域设置的日历
     * @param zone the time zone to use
     * @param aLocale the locale for the week data
     */
    protected Calendar(TimeZone zone, Locale aLocale)
    {
        fields = new int[FIELD_COUNT];
        isSet = new boolean[FIELD_COUNT];
        stamp = new int[FIELD_COUNT];

        this.zone = zone;
        setWeekCountData(aLocale);
    }

    /**
     * Gets a calendar using the default time zone and locale. The
     * <code>Calendar</code> returned is based on the current time
     * in the default time zone with the default
     * {@link Locale.Category#FORMAT FORMAT} locale.
     * 使用默认时区和区域设置获取日历。返回的Calendar基于具有默认Locale.CategoryFORMAT语言环境的默认时区中的当前时间。
     * @return a Calendar.
     */
    public static Calendar getInstance()
    {
        return createCalendar(TimeZone.getDefault(), Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * Gets a calendar using the specified time zone and default locale.
     * The <code>Calendar</code> returned is based on the current time
     * in the given time zone with the default
     * {@link Locale.Category#FORMAT FORMAT} locale.
     * 使用指定的时区和默认区域设置获取日历。返回的Calendar基于给定时区中的当前时间，
     * 使用默认的Locale.CategoryFORMAT语言环境
     * @param zone the time zone to use
     * @return a Calendar.
     */
    public static Calendar getInstance(TimeZone zone)
    {
        return createCalendar(zone, Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * Gets a calendar using the default time zone and specified locale.
     * The <code>Calendar</code> returned is based on the current time
     * in the default time zone with the given locale.
     * 使用默认时区和指定区域设置获取日历。返回的Calendar基于具有给定语言环境的默认时区中的当前时间。
     * @param aLocale the locale for the week data
     * @return a Calendar.
     */
    public static Calendar getInstance(Locale aLocale)
    {
        return createCalendar(TimeZone.getDefault(), aLocale);
    }

    /**
     * Gets a calendar with the specified time zone and locale.
     * The <code>Calendar</code> returned is based on the current time
     * in the given time zone with the given locale.
     * 获取具有指定时区和区域设置的日历。返回的Calendar基于给定时区和给定语言环境中的当前时间
     * @param zone the time zone to use
     * @param aLocale the locale for the week data
     * @return a Calendar.
     */
    public static Calendar getInstance(TimeZone zone,
                                       Locale aLocale)
    {
        return createCalendar(zone, aLocale);
    }

    private static Calendar createCalendar(TimeZone zone,
                                           Locale aLocale)
    {
        CalendarProvider provider =
            LocaleProviderAdapter.getAdapter(CalendarProvider.class, aLocale)
                                 .getCalendarProvider();
        if (provider != null) {
            try {
                return provider.getInstance(zone, aLocale);
            } catch (IllegalArgumentException iae) {
                // fall back to the default instantiation
            }
        }

        Calendar cal = null;

        if (aLocale.hasExtensions()) {
            String caltype = aLocale.getUnicodeLocaleType("ca");
            if (caltype != null) {
                switch (caltype) {
                case "buddhist":
                cal = new BuddhistCalendar(zone, aLocale);
                    break;
                case "japanese":
                    cal = new JapaneseImperialCalendar(zone, aLocale);
                    break;
                case "gregory":
                    cal = new GregorianCalendar(zone, aLocale);
                    break;
                }
            }
        }
        if (cal == null) {
            // If no known calendar type is explicitly specified,
            // perform the traditional way to create a Calendar:
            // create a BuddhistCalendar for th_TH locale,
            // a JapaneseImperialCalendar for ja_JP_JP locale, or
            // a GregorianCalendar for any other locales.
            // NOTE: The language, country and variant strings are interned.
            //如果没有明确指定已知的日历类型，则执行传统方式来创建日历：为 th_TH 语言环境创建一个佛教日历，
            // 为 ja_JP_JP 语言环境创建一个 JapaneseImperialCalendar，
            // 或者为任何其他语言环境创建一个 GregorianCalendar。注意：语言、国家和变体字符串是实习的
            if (aLocale.getLanguage() == "th" && aLocale.getCountry() == "TH") {
                cal = new BuddhistCalendar(zone, aLocale);
            } else if (aLocale.getVariant() == "JP" && aLocale.getLanguage() == "ja"
                       && aLocale.getCountry() == "JP") {
                cal = new JapaneseImperialCalendar(zone, aLocale);
            } else {
                cal = new GregorianCalendar(zone, aLocale);
            }
        }
        return cal;
    }

    /**
     * Returns an array of all locales for which the <code>getInstance</code>
     * methods of this class can return localized instances.
     * The array returned must contain at least a <code>Locale</code>
     * instance equal to {@link java.util.Locale#US Locale.US}.
     *返回所有语言环境的数组，此类的getInstance方法可以为其返回本地化实例。
     * 返回的数组必须至少包含一个等于 java.util.LocaleUS的Locale实例。
     * @return An array of locales for which localized
     *         <code>Calendar</code> instances are available.
     */
    public static synchronized Locale[] getAvailableLocales()
    {
        return DateFormat.getAvailableLocales();
    }

    /**
     * Converts the current calendar field values in {@link #fields fields[]}
     * to the millisecond time value
     * {@link #time}.
     * 将fields[]中的当前日历字段值转换为毫秒时间值
     * @see #complete()
     * @see #computeFields()
     */
    protected abstract void computeTime();

    /**
     * Converts the current millisecond time value {@link #time}
     * to calendar field values in {@link #fields fields[]}.
     * This allows you to sync up the calendar field values with
     * a new time that is set for the calendar.  The time is <em>not</em>
     * recomputed first; to recompute the time, then the fields, call the
     * {@link #complete()} method.
     *将当前毫秒时间值time转换为fields[]中的日历字段值。
     * 这允许您将日历字段值与为日历设置的新时间同步。
     * 时间不先重新计算；要重新计算时间，然后是字段，请调用complete()方法。
     * @see #computeTime()
     */
    protected abstract void computeFields();

    /**
     * Returns a <code>Date</code> object representing this
     * <code>Calendar</code>'s time value (millisecond offset from the <a
     * href="#Epoch">Epoch</a>").
     * 返回一个Date对象，表示此Calendar的时间值（从Epoch开始的毫秒偏移量）。
     * @return a <code>Date</code> representing the time value.
     * @see #setTime(Date)
     * @see #getTimeInMillis()
     */
    public final Date getTime() {
        return new Date(getTimeInMillis());
    }

    /**
     * Sets this Calendar's time with the given <code>Date</code>.
     * <p>
     * Note: Calling <code>setTime()</code> with
     * <code>Date(Long.MAX_VALUE)</code> or <code>Date(Long.MIN_VALUE)</code>
     * may yield incorrect field values from <code>get()</code>.
     * 使用给定的Date设置此日历的时间。
     * 注意：使用Date(Long.MAX_VALUE)或Date(Long.MIN_VALUE)调用setTime()
     * 可能会从产生不正确的字段值获取（）
     * @param date the given Date.
     * @see #getTime()
     * @see #setTimeInMillis(long)
     */
    public final void setTime(Date date) {
        setTimeInMillis(date.getTime());
    }

    /**
     * Returns this Calendar's time value in milliseconds.
     * 以毫秒为单位返回此日历的时间值
     * @return the current time as UTC milliseconds from the epoch.
     * @see #getTime()
     * @see #setTimeInMillis(long)
     */
    public long getTimeInMillis() {
        if (!isTimeSet) {
            updateTime();
        }
        return time;
    }

    /**
     * Sets this Calendar's current time from the given long value.
     * 根据给定的 long 值设置此日历的当前时间
     * @param millis the new time in UTC milliseconds from the epoch.
     * @see #setTime(Date)
     * @see #getTimeInMillis()
     */
    public void setTimeInMillis(long millis) {
        // If we don't need to recalculate the calendar field values,
        // do nothing.
        //如果我们不需要重新计算日历字段值，则什么都不做
        if (time == millis && isTimeSet && areFieldsSet && areAllFieldsSet
            && (zone instanceof ZoneInfo) && !((ZoneInfo)zone).isDirty()) {
            return;
        }
        time = millis;
        isTimeSet = true;
        areFieldsSet = false;
        computeFields();
        areAllFieldsSet = areFieldsSet = true;
    }

    /**
     * Returns the value of the given calendar field. In lenient mode,
     * all calendar fields are normalized. In non-lenient mode, all
     * calendar fields are validated and this method throws an
     * exception if any calendar fields have out-of-range values. The
     * normalization and validation are handled by the
     * {@link #complete()} method, which process is calendar
     * system dependent.
     * 返回给定日历字段的值。在宽松模式下，所有日历字段都被标准化。
     * 在非宽松模式下，所有日历字段都经过验证，如果任何日历字段的值超出范围，则此方法将引发异常。
     * 规范化和验证由complete()方法处理，该过程取决于日历系统。
     * @param field the given calendar field.
     * @return the value for the given calendar field.
     * @throws ArrayIndexOutOfBoundsException if the specified field is out of range
     *             (<code>field &lt; 0 || field &gt;= FIELD_COUNT</code>).
     * @see #set(int,int)
     * @see #complete()
     */
    public int get(int field)
    {
        complete();
        return internalGet(field);
    }

    /**
     * Returns the value of the given calendar field. This method does
     * not involve normalization or validation of the field value.
     * 返回给定日历字段的值。此方法不涉及字段值的规范化或验证
     * @param field the given calendar field.
     * @return the value for the given calendar field.
     * @see #get(int)
     */
    protected final int internalGet(int field)
    {
        return fields[field];
    }

    /**
     * Sets the value of the given calendar field. This method does
     * not affect any setting state of the field in this
     * <code>Calendar</code> instance.
     * 设置给定日历字段的值。此方法不影响此Calendar实例中字段的任何设置状态
     * @throws IndexOutOfBoundsException if the specified field is out of range
     *             (<code>field &lt; 0 || field &gt;= FIELD_COUNT</code>).
     * @see #areFieldsSet
     * @see #isTimeSet
     * @see #areAllFieldsSet
     * @see #set(int,int)
     */
    final void internalSet(int field, int value)
    {
        fields[field] = value;
    }

    /**
     * Sets the given calendar field to the given value. The value is not
     * interpreted by this method regardless of the leniency mode.
     * 将给定的日历字段设置为给定的值。无论宽松模式如何，此方法都不会解释该值
     * @param field the given calendar field.
     * @param value the value to be set for the given calendar field.
     * @throws ArrayIndexOutOfBoundsException if the specified field is out of range
     *             (<code>field &lt; 0 || field &gt;= FIELD_COUNT</code>).
     * in non-lenient mode.
     * @see #set(int,int,int)
     * @see #set(int,int,int,int,int)
     * @see #set(int,int,int,int,int,int)
     * @see #get(int)
     */
    public void set(int field, int value)
    {
        // If the fields are partially normalized, calculate all the
        // fields before changing any fields.
        if (areFieldsSet && !areAllFieldsSet) {
            computeFields();
        }
        internalSet(field, value);
        isTimeSet = false;
        areFieldsSet = false;
        isSet[field] = true;
        stamp[field] = nextStamp++;
        if (nextStamp == Integer.MAX_VALUE) {
            adjustStamp();
        }
    }

    /**
     * Sets the values for the calendar fields <code>YEAR</code>,
     * <code>MONTH</code>, and <code>DAY_OF_MONTH</code>.
     * Previous values of other calendar fields are retained.  If this is not desired,
     * call {@link #clear()} first.
     * 设置日历字段YEAR、MONTH和DAY_OF_MONTH的值。保留其他日历字段的先前值。如果不需要，请先调用clear()
     * @param year the value used to set the <code>YEAR</code> calendar field.
     * @param month the value used to set the <code>MONTH</code> calendar field.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the <code>DAY_OF_MONTH</code> calendar field.
     * @see #set(int,int)
     * @see #set(int,int,int,int,int)
     * @see #set(int,int,int,int,int,int)
     */
    public final void set(int year, int month, int date)
    {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
    }

    /**
     * Sets the values for the calendar fields <code>YEAR</code>,
     * <code>MONTH</code>, <code>DAY_OF_MONTH</code>,
     * <code>HOUR_OF_DAY</code>, and <code>MINUTE</code>.
     * Previous values of other fields are retained.  If this is not desired,
     * call {@link #clear()} first.
     * 设置日历字段YEAR、MONTH、DAY_OF_MONTH、HOUR_OF_DAY和MINUTE的值。
     * 保留其他字段的先前值。如果不需要，请先调用clear()。
     * @param year the value used to set the <code>YEAR</code> calendar field.
     * @param month the value used to set the <code>MONTH</code> calendar field.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the <code>DAY_OF_MONTH</code> calendar field.
     * @param hourOfDay the value used to set the <code>HOUR_OF_DAY</code> calendar field.
     * @param minute the value used to set the <code>MINUTE</code> calendar field.
     * @see #set(int,int)
     * @see #set(int,int,int)
     * @see #set(int,int,int,int,int,int)
     */
    public final void set(int year, int month, int date, int hourOfDay, int minute)
    {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    /**
     * Sets the values for the fields <code>YEAR</code>, <code>MONTH</code>,
     * <code>DAY_OF_MONTH</code>, <code>HOUR_OF_DAY</code>, <code>MINUTE</code>, and
     * <code>SECOND</code>.
     * Previous values of other fields are retained.  If this is not desired,
     * call {@link #clear()} first.
     * 设置字段YEAR、MONTH、DAY_OF_MONTH、HOUR_OF_DAY、MINUTE和SECOND。
     * 保留其他字段的先前值。如果不需要，请先调用clear()
     * @param year the value used to set the <code>YEAR</code> calendar field.
     * @param month the value used to set the <code>MONTH</code> calendar field.
     * Month value is 0-based. e.g., 0 for January.
     * @param date the value used to set the <code>DAY_OF_MONTH</code> calendar field.
     * @param hourOfDay the value used to set the <code>HOUR_OF_DAY</code> calendar field.
     * @param minute the value used to set the <code>MINUTE</code> calendar field.
     * @param second the value used to set the <code>SECOND</code> calendar field.
     * @see #set(int,int)
     * @see #set(int,int,int)
     * @see #set(int,int,int,int,int)
     */
    public final void set(int year, int month, int date, int hourOfDay, int minute,
                          int second)
    {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    /**
     * Sets all the calendar field values and the time value
     * (millisecond offset from the <a href="#Epoch">Epoch</a>) of
     * this <code>Calendar</code> undefined. This means that {@link
     * #isSet(int) isSet()} will return <code>false</code> for all the
     * calendar fields, and the date and time calculations will treat
     * the fields as if they had never been set. A
     * <code>Calendar</code> implementation class may use its specific
     * default field values for date/time calculations. For example,
     * <code>GregorianCalendar</code> uses 1970 if the
     * <code>YEAR</code> field value is undefined.
     * 设置此Calendar的所有日历字段值和时间值（从Epoch开始的毫秒偏移量）未定义。
     * 这意味着isSet(int)将为所有日历字段返回false，并且日期和时间计算会将这些字段视为从未设置过。
     * Calendar实现类可以使用其特定的默认字段值进行日期时间计算。
     * 例如，如果YEAR字段值未定义，则GregorianCalendar使用 1970。
     * @see #clear(int)
     */
    public final void clear()
    {
        for (int i = 0; i < fields.length; ) {
            stamp[i] = fields[i] = 0; // UNSET == 0
            isSet[i++] = false;
        }
        areAllFieldsSet = areFieldsSet = false;
        isTimeSet = false;
    }

    /**
     * Sets the given calendar field value and the time value
     * (millisecond offset from the <a href="#Epoch">Epoch</a>) of
     * this <code>Calendar</code> undefined. This means that {@link
     * #isSet(int) isSet(field)} will return <code>false</code>, and
     * the date and time calculations will treat the field as if it
     * had never been set. A <code>Calendar</code> implementation
     * class may use the field's specific default value for date and
     * time calculations.
     * 1.设置给定的日历字段值和此Calendar的时间值（从Epoch开始的毫秒偏移量）未定义。
     * 这意味着isSet(int)将返回false，并且日期和时间计算会将字段视为从未设置过。
     * Calendar实现类可以使用字段的特定默认值进行日期和时间计算
     * <p>The {@link #HOUR_OF_DAY}, {@link #HOUR} and {@link #AM_PM}
     * fields are handled independently and the <a
     * href="#time_resolution">the resolution rule for the time of
     * day</a> is applied. Clearing one of the fields doesn't reset
     * the hour of day value of this <code>Calendar</code>. Use {@link
     * #set(int,int) set(Calendar.HOUR_OF_DAY, 0)} to reset the hour
     * value.
     * 2.HOUR_OF_DAY、HOUR和AM_PM字段是独立处理的，并且应用了当天时间的解析规则。
     * 清除其中一个字段不会重置此Calendar的小时值。使用set(Calendar.HOUR_OF_DAY, 0)重置小时值
     * @param field the calendar field to be cleared.
     * @see #clear()
     */
    public final void clear(int field)
    {
        fields[field] = 0;
        stamp[field] = UNSET;
        isSet[field] = false;

        areAllFieldsSet = areFieldsSet = false;
        isTimeSet = false;
    }

    /**
     * Determines if the given calendar field has a value set,
     * including cases that the value has been set by internal fields
     * calculations triggered by a <code>get</code> method call.
     * 确定给定的日历字段是否具有值集，包括该值已由get方法调用触发的内部字段计算设置的情况
     * @param field the calendar field to test
     * @return <code>true</code> if the given calendar field has a value set;
     * <code>false</code> otherwise.
     */
    public final boolean isSet(int field)
    {
        return stamp[field] != UNSET;
    }

    /**
     * Returns the string representation of the calendar
     * <code>field</code> value in the given <code>style</code> and
     * <code>locale</code>.  If no string representation is
     * applicable, <code>null</code> is returned. This method calls
     * {@link Calendar#get(int) get(field)} to get the calendar
     * <code>field</code> value if the string representation is
     * applicable to the given calendar <code>field</code>.
     * 1.返回给定style和locale中日历field值的字符串表示形式。如果没有适用的字符串表示，
     * 则返回null。如果字符串表示适用于给定的日历field，则此方法调用Calendar.get(int)以获取日历field值
     * <p>For example, if this <code>Calendar</code> is a
     * <code>GregorianCalendar</code> and its date is 2005-01-01, then
     * the string representation of the {@link #MONTH} field would be
     * "January" in the long style in an English locale or "Jan" in
     * the short style. However, no string representation would be
     * available for the {@link #DAY_OF_MONTH} field, and this method
     * would return <code>null</code>
     * 2.例如，如果这个Calendar是一个GregorianCalendar并且它的日期是 2005-01-01，
     * 那么MONTH字段的字符串表示在英语语言环境中的长样式或短样式中的“Jan”。
     * 但是，没有字符串表示可用于 DAY_OF_MONTH字段，并且此方法将返回null
     * <p>The default implementation supports the calendar fields for
     * which a {@link DateFormatSymbols} has names in the given
     * <code>locale</code>.
     * 3.默认实现支持DateFormatSymbols在给定locale中具有名称的日历字段
     * @param field
     *        the calendar field for which the string representation
     *        is returned
     * @param style
     *        the style applied to the string representation; one of {@link
     *        #SHORT_FORMAT} ({@link #SHORT}), {@link #SHORT_STANDALONE},
     *        {@link #LONG_FORMAT} ({@link #LONG}), {@link #LONG_STANDALONE},
     *        {@link #NARROW_FORMAT}, or {@link #NARROW_STANDALONE}.
     * @param locale
     *        the locale for the string representation
     *        (any calendar types specified by {@code locale} are ignored)
     * @return the string representation of the given
     *        {@code field} in the given {@code style}, or
     *        {@code null} if no string representation is
     *        applicable.
     * @exception IllegalArgumentException
     *        if {@code field} or {@code style} is invalid,
     *        or if this {@code Calendar} is non-lenient and any
     *        of the calendar fields have invalid values
     * @exception NullPointerException
     *        if {@code locale} is null
     * @since 1.6
     */
    public String getDisplayName(int field, int style, Locale locale) {
        if (!checkDisplayNameParams(field, style, SHORT, NARROW_FORMAT, locale,
                            ERA_MASK|MONTH_MASK|DAY_OF_WEEK_MASK|AM_PM_MASK)) {
            return null;
        }

        String calendarType = getCalendarType();
        int fieldValue = get(field);
        // the standalone and narrow styles are supported only through CalendarDataProviders.
        //仅通过 CalendarDataProviders 支持独立和窄样式
        if (isStandaloneStyle(style) || isNarrowFormatStyle(style)) {
            String val = CalendarDataUtility.retrieveFieldValueName(calendarType,
                                                                    field, fieldValue,
                                                                    style, locale);
            // Perform fallback here to follow the CLDR rules
            if (val == null) {
                if (isNarrowFormatStyle(style)) {
                    val = CalendarDataUtility.retrieveFieldValueName(calendarType,
                                                                     field, fieldValue,
                                                                     toStandaloneStyle(style),
                                                                     locale);
                } else if (isStandaloneStyle(style)) {
                    val = CalendarDataUtility.retrieveFieldValueName(calendarType,
                                                                     field, fieldValue,
                                                                     getBaseStyle(style),
                                                                     locale);
                }
            }
            return val;
        }

        DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
        String[] strings = getFieldStrings(field, style, symbols);
        if (strings != null) {
            if (fieldValue < strings.length) {
                return strings[fieldValue];
            }
        }
        return null;
    }

    /**
     * Returns a {@code Map} containing all names of the calendar
     * {@code field} in the given {@code style} and
     * {@code locale} and their corresponding field values. For
     * example, if this {@code Calendar} is a {@link
     * GregorianCalendar}, the returned map would contain "Jan" to
     * {@link #JANUARY}, "Feb" to {@link #FEBRUARY}, and so on, in the
     * {@linkplain #SHORT short} style in an English locale.
     * 1.返回一个Map，其中包含给定style和locale中日历field的所有名称及其相应的字段值。
     * 例如，如果此Calendar是GregorianCalendar，则返回的映射将包含“Jan”到 JANUARY、“Feb”到 FEBRUARY，
     * 依此类推，在 SHORT short 英语语言环境中的样式。
     * <p>Narrow names may not be unique due to use of single characters,
     * such as "S" for Sunday and Saturday. In that case narrow names are not
     * included in the returned {@code Map}.
     * 2.由于使用单个字符，例如“S”代表星期日和星期六，窄名称可能不是唯一的。在这种情况下，返回的Map中不包含窄名称。
     * <p>The values of other calendar fields may be taken into
     * account to determine a set of display names. For example, if
     * this {@code Calendar} is a lunisolar calendar system and
     * the year value given by the {@link #YEAR} field has a leap
     * month, this method would return month names containing the leap
     * month name, and month names are mapped to their values specific
     * for the year.
     * 3.可以考虑其他日历字段的值来确定一组显示名称。
     * 例如，如果此Calendar是阴阳历系统，并且YEAR字段给出的年份值具有闰月，则此方法将返回包含闰月名称的月份名称，并映射月份名称到其特定年份的值
     * <p>The default implementation supports display names contained in
     * a {@link DateFormatSymbols}. For example, if {@code field}
     * is {@link #MONTH} and {@code style} is {@link
     * #ALL_STYLES}, this method returns a {@code Map} containing
     * all strings returned by {@link DateFormatSymbols#getShortMonths()}
     * and {@link DateFormatSymbols#getMonths()}.
     * 4.默认实现支持包含在DateFormatSymbols中的显示名称。例如，如果field为MONTH且style为ALL_STYLES，
     * 则此方法返回一个Map，其中包含DateFormatSymbolsgetShortMonths()返回的所有字符串和DateFormatSymbolsgetMonths()。
     * @param field
     *        the calendar field for which the display names are returned
     * @param style
     *        the style applied to the string representation; one of {@link
     *        #SHORT_FORMAT} ({@link #SHORT}), {@link #SHORT_STANDALONE},
     *        {@link #LONG_FORMAT} ({@link #LONG}), {@link #LONG_STANDALONE},
     *        {@link #NARROW_FORMAT}, or {@link #NARROW_STANDALONE}
     * @param locale
     *        the locale for the display names
     * @return a {@code Map} containing all display names in
     *        {@code style} and {@code locale} and their
     *        field values, or {@code null} if no display names
     *        are defined for {@code field}
     * @exception IllegalArgumentException
     *        if {@code field} or {@code style} is invalid,
     *        or if this {@code Calendar} is non-lenient and any
     *        of the calendar fields have invalid values
     * @exception NullPointerException
     *        if {@code locale} is null
     * @since 1.6
     */
    public Map<String, Integer> getDisplayNames(int field, int style, Locale locale) {
        if (!checkDisplayNameParams(field, style, ALL_STYLES, NARROW_FORMAT, locale,
                                    ERA_MASK|MONTH_MASK|DAY_OF_WEEK_MASK|AM_PM_MASK)) {
            return null;
        }

        String calendarType = getCalendarType();
        if (style == ALL_STYLES || isStandaloneStyle(style) || isNarrowFormatStyle(style)) {
            Map<String, Integer> map;
            map = CalendarDataUtility.retrieveFieldValueNames(calendarType, field, style, locale);

            // Perform fallback here to follow the CLDR rules
            if (map == null) {
                if (isNarrowFormatStyle(style)) {
                    map = CalendarDataUtility.retrieveFieldValueNames(calendarType, field,
                                                                      toStandaloneStyle(style), locale);
                } else if (style != ALL_STYLES) {
                    map = CalendarDataUtility.retrieveFieldValueNames(calendarType, field,
                                                                      getBaseStyle(style), locale);
                }
            }
            return map;
        }

        // SHORT or LONG
        return getDisplayNamesImpl(field, style, locale);
    }

    private Map<String,Integer> getDisplayNamesImpl(int field, int style, Locale locale) {
        DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
        String[] strings = getFieldStrings(field, style, symbols);
        if (strings != null) {
            Map<String,Integer> names = new HashMap<>();
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].length() == 0) {
                    continue;
                }
                names.put(strings[i], i);
            }
            return names;
        }
        return null;
    }

    boolean checkDisplayNameParams(int field, int style, int minStyle, int maxStyle,
                                   Locale locale, int fieldMask) {
        int baseStyle = getBaseStyle(style); // Ignore the standalone mask
        if (field < 0 || field >= fields.length ||
            baseStyle < minStyle || baseStyle > maxStyle) {
            throw new IllegalArgumentException();
        }
        if (locale == null) {
            throw new NullPointerException();
        }
        return isFieldSet(fieldMask, field);
    }

    private String[] getFieldStrings(int field, int style, DateFormatSymbols symbols) {
        int baseStyle = getBaseStyle(style); // ignore the standalone mask

        // DateFormatSymbols doesn't support any narrow names.
        if (baseStyle == NARROW_FORMAT) {
            return null;
        }

        String[] strings = null;
        switch (field) {
        case ERA:
            strings = symbols.getEras();
            break;

        case MONTH:
            strings = (baseStyle == LONG) ? symbols.getMonths() : symbols.getShortMonths();
            break;

        case DAY_OF_WEEK:
            strings = (baseStyle == LONG) ? symbols.getWeekdays() : symbols.getShortWeekdays();
            break;

        case AM_PM:
            strings = symbols.getAmPmStrings();
            break;
        }
        return strings;
    }

    /**
     * Fills in any unset fields in the calendar fields. First, the {@link
     * #computeTime()} method is called if the time value (millisecond offset
     * from the <a href="#Epoch">Epoch</a>) has not been calculated from
     * calendar field values. Then, the {@link #computeFields()} method is
     * called to calculate all calendar field values.
     * 填写日历字段中任何未设置的字段。首先，如果尚未根据日历字段值计算时间值（与Epoch的毫秒偏移量），
     * 则调用computeTime()方法。然后，调用computeFields()方法计算所有日历字段值
     */
    protected void complete()
    {
        if (!isTimeSet) {
            updateTime();
        }
        if (!areFieldsSet || !areAllFieldsSet) {
            computeFields(); // fills in unset fields
            areAllFieldsSet = areFieldsSet = true;
        }
    }

    /**
     * Returns whether the value of the specified calendar field has been set
     * externally by calling one of the setter methods rather than by the
     * internal time calculation.
     * 返回是否通过调用 setter 方法之一而不是通过内部时间计算在外部设置了指定日历字段的值
     * @return <code>true</code> if the field has been set externally,
     * <code>false</code> otherwise.
     * @exception IndexOutOfBoundsException if the specified
     *                <code>field</code> is out of range
     *               (<code>field &lt; 0 || field &gt;= FIELD_COUNT</code>).
     * @see #selectFields()
     * @see #setFieldsComputed(int)
     */
    final boolean isExternallySet(int field) {
        return stamp[field] >= MINIMUM_USER_STAMP;
    }

    /**
     * Returns a field mask (bit mask) indicating all calendar fields that
     * have the state of externally or internally set.
     * 返回一个字段掩码（位掩码），指示所有具有外部或内部设置状态的日历字段
     * @return a bit mask indicating set state fields
     */
    final int getSetStateFields() {
        int mask = 0;
        for (int i = 0; i < fields.length; i++) {
            if (stamp[i] != UNSET) {
                mask |= 1 << i;
            }
        }
        return mask;
    }

    /**
     * Sets the state of the specified calendar fields to
     * <em>computed</em>. This state means that the specified calendar fields
     * have valid values that have been set by internal time calculation
     * rather than by calling one of the setter methods.
     * 将指定日历字段的状态设置为computed。
     * 这种状态意味着指定的日历字段具有通过内部时间计算而不是通过调用 setter 方法之一设置的有效值。
     * @param fieldMask the field to be marked as computed.
     * @exception IndexOutOfBoundsException if the specified
     *                <code>field</code> is out of range
     *               (<code>field &lt; 0 || field &gt;= FIELD_COUNT</code>).
     * @see #isExternallySet(int)
     * @see #selectFields()
     */
    final void setFieldsComputed(int fieldMask) {
        if (fieldMask == ALL_FIELDS) {
            for (int i = 0; i < fields.length; i++) {
                stamp[i] = COMPUTED;
                isSet[i] = true;
            }
            areFieldsSet = areAllFieldsSet = true;
        } else {
            for (int i = 0; i < fields.length; i++) {
                if ((fieldMask & 1) == 1) {
                    stamp[i] = COMPUTED;
                    isSet[i] = true;
                } else {
                    if (areAllFieldsSet && !isSet[i]) {
                        areAllFieldsSet = false;
                    }
                }
                fieldMask >>>= 1;
            }
        }
    }

    /**
     * Sets the state of the calendar fields that are <em>not</em> specified
     * by <code>fieldMask</code> to <em>unset</em>. If <code>fieldMask</code>
     * specifies all the calendar fields, then the state of this
     * <code>Calendar</code> becomes that all the calendar fields are in sync
     * with the time value (millisecond offset from the Epoch).
     * 将not由fieldMask指定的日历字段的状态设置为unset。
     * 如果fieldMask指定所有日历字段，则此Calendar的状态变为所有日历字段与时间值同步（距 Epoch 的毫秒偏移量）
     * @param fieldMask the field mask indicating which calendar fields are in
     * sync with the time value.
     * @exception IndexOutOfBoundsException if the specified
     *                <code>field</code> is out of range
     *               (<code>field &lt; 0 || field &gt;= FIELD_COUNT</code>).
     * @see #isExternallySet(int)
     * @see #selectFields()
     */
    final void setFieldsNormalized(int fieldMask) {
        if (fieldMask != ALL_FIELDS) {
            for (int i = 0; i < fields.length; i++) {
                if ((fieldMask & 1) == 0) {
                    stamp[i] = fields[i] = 0; // UNSET == 0
                    isSet[i] = false;
                }
                fieldMask >>= 1;
            }
        }

        // Some or all of the fields are in sync with the
        // milliseconds, but the stamp values are not normalized yet.
        //部分或全部字段与毫秒同步，但标记值尚未标准化。
        areFieldsSet = true;
        areAllFieldsSet = false;
    }

    /**
     * Returns whether the calendar fields are partially in sync with the time
     * value or fully in sync but not stamp values are not normalized yet.
     * //返回日历字段是与时间值部分同步还是完全同步，但标记值尚未标准化
     */
    final boolean isPartiallyNormalized() {
        return areFieldsSet && !areAllFieldsSet;
    }

    /**
     * Returns whether the calendar fields are fully in sync with the time
     * value.
     * 返回日历字段是否与时间值完全同步。
     */
    final boolean isFullyNormalized() {
        return areFieldsSet && areAllFieldsSet;
    }

    /**
     * Marks this Calendar as not sync'd.
     * 将此日历标记为未同步
     */
    final void setUnnormalized() {
        areFieldsSet = areAllFieldsSet = false;
    }

    /**
     * Returns whether the specified <code>field</code> is on in the
     * <code>fieldMask</code>.
     * 返回指定的field在fieldMask中是否打开
     */
    static boolean isFieldSet(int fieldMask, int field) {
        return (fieldMask & (1 << field)) != 0;
    }

    /**
     * Returns a field mask indicating which calendar field values
     * to be used to calculate the time value. The calendar fields are
     * returned as a bit mask, each bit of which corresponds to a field, i.e.,
     * the mask value of <code>field</code> is <code>(1 &lt;&lt;
     * field)</code>. For example, 0x26 represents the <code>YEAR</code>,
     * <code>MONTH</code>, and <code>DAY_OF_MONTH</code> fields (i.e., 0x26 is
     * equal to
     * <code>(1&lt;&lt;YEAR)|(1&lt;&lt;MONTH)|(1&lt;&lt;DAY_OF_MONTH))</code>.
     *
     * <p>This method supports the calendar fields resolution as described in
     * the class description. If the bit mask for a given field is on and its
     * field has not been set (i.e., <code>isSet(field)</code> is
     * <code>false</code>), then the default value of the field has to be
     * used, which case means that the field has been selected because the
     * selected combination involves the field.
     *
     * @return a bit mask of selected fields
     * @see #isExternallySet(int)
     */
    final int selectFields() {
        // This implementation has been taken from the GregorianCalendar class.

        // The YEAR field must always be used regardless of its SET
        // state because YEAR is a mandatory field to determine the date
        // and the default value (EPOCH_YEAR) may change through the
        // normalization process.
        int fieldMask = YEAR_MASK;

        if (stamp[ERA] != UNSET) {
            fieldMask |= ERA_MASK;
        }
        // Find the most recent group of fields specifying the day within
        // the year.  These may be any of the following combinations:
        //   MONTH + DAY_OF_MONTH
        //   MONTH + WEEK_OF_MONTH + DAY_OF_WEEK
        //   MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
        //   DAY_OF_YEAR
        //   WEEK_OF_YEAR + DAY_OF_WEEK
        // We look for the most recent of the fields in each group to determine
        // the age of the group.  For groups involving a week-related field such
        // as WEEK_OF_MONTH, DAY_OF_WEEK_IN_MONTH, or WEEK_OF_YEAR, both the
        // week-related field and the DAY_OF_WEEK must be set for the group as a
        // whole to be considered.  (See bug 4153860 - liu 7/24/98.)
        int dowStamp = stamp[DAY_OF_WEEK];
        int monthStamp = stamp[MONTH];
        int domStamp = stamp[DAY_OF_MONTH];
        int womStamp = aggregateStamp(stamp[WEEK_OF_MONTH], dowStamp);
        int dowimStamp = aggregateStamp(stamp[DAY_OF_WEEK_IN_MONTH], dowStamp);
        int doyStamp = stamp[DAY_OF_YEAR];
        int woyStamp = aggregateStamp(stamp[WEEK_OF_YEAR], dowStamp);

        int bestStamp = domStamp;
        if (womStamp > bestStamp) {
            bestStamp = womStamp;
        }
        if (dowimStamp > bestStamp) {
            bestStamp = dowimStamp;
        }
        if (doyStamp > bestStamp) {
            bestStamp = doyStamp;
        }
        if (woyStamp > bestStamp) {
            bestStamp = woyStamp;
        }

        /* No complete combination exists.  Look for WEEK_OF_MONTH,
         * DAY_OF_WEEK_IN_MONTH, or WEEK_OF_YEAR alone.  Treat DAY_OF_WEEK alone
         * as DAY_OF_WEEK_IN_MONTH.
         */
        if (bestStamp == UNSET) {
            womStamp = stamp[WEEK_OF_MONTH];
            dowimStamp = Math.max(stamp[DAY_OF_WEEK_IN_MONTH], dowStamp);
            woyStamp = stamp[WEEK_OF_YEAR];
            bestStamp = Math.max(Math.max(womStamp, dowimStamp), woyStamp);

            /* Treat MONTH alone or no fields at all as DAY_OF_MONTH.  This may
             * result in bestStamp = domStamp = UNSET if no fields are set,
             * which indicates DAY_OF_MONTH.
             */
            if (bestStamp == UNSET) {
                bestStamp = domStamp = monthStamp;
            }
        }

        if (bestStamp == domStamp ||
           (bestStamp == womStamp && stamp[WEEK_OF_MONTH] >= stamp[WEEK_OF_YEAR]) ||
           (bestStamp == dowimStamp && stamp[DAY_OF_WEEK_IN_MONTH] >= stamp[WEEK_OF_YEAR])) {
            fieldMask |= MONTH_MASK;
            if (bestStamp == domStamp) {
                fieldMask |= DAY_OF_MONTH_MASK;
            } else {
                assert (bestStamp == womStamp || bestStamp == dowimStamp);
                if (dowStamp != UNSET) {
                    fieldMask |= DAY_OF_WEEK_MASK;
                }
                if (womStamp == dowimStamp) {
                    // When they are equal, give the priority to
                    // WEEK_OF_MONTH for compatibility.
                    if (stamp[WEEK_OF_MONTH] >= stamp[DAY_OF_WEEK_IN_MONTH]) {
                        fieldMask |= WEEK_OF_MONTH_MASK;
                    } else {
                        fieldMask |= DAY_OF_WEEK_IN_MONTH_MASK;
                    }
                } else {
                    if (bestStamp == womStamp) {
                        fieldMask |= WEEK_OF_MONTH_MASK;
                    } else {
                        assert (bestStamp == dowimStamp);
                        if (stamp[DAY_OF_WEEK_IN_MONTH] != UNSET) {
                            fieldMask |= DAY_OF_WEEK_IN_MONTH_MASK;
                        }
                    }
                }
            }
        } else {
            assert (bestStamp == doyStamp || bestStamp == woyStamp ||
                    bestStamp == UNSET);
            if (bestStamp == doyStamp) {
                fieldMask |= DAY_OF_YEAR_MASK;
            } else {
                assert (bestStamp == woyStamp);
                if (dowStamp != UNSET) {
                    fieldMask |= DAY_OF_WEEK_MASK;
                }
                fieldMask |= WEEK_OF_YEAR_MASK;
            }
        }

        // Find the best set of fields specifying the time of day.  There
        // are only two possibilities here; the HOUR_OF_DAY or the
        // AM_PM and the HOUR.
        int hourOfDayStamp = stamp[HOUR_OF_DAY];
        int hourStamp = aggregateStamp(stamp[HOUR], stamp[AM_PM]);
        bestStamp = (hourStamp > hourOfDayStamp) ? hourStamp : hourOfDayStamp;

        // if bestStamp is still UNSET, then take HOUR or AM_PM. (See 4846659)
        if (bestStamp == UNSET) {
            bestStamp = Math.max(stamp[HOUR], stamp[AM_PM]);
        }

        // Hours
        if (bestStamp != UNSET) {
            if (bestStamp == hourOfDayStamp) {
                fieldMask |= HOUR_OF_DAY_MASK;
            } else {
                fieldMask |= HOUR_MASK;
                if (stamp[AM_PM] != UNSET) {
                    fieldMask |= AM_PM_MASK;
                }
            }
        }
        if (stamp[MINUTE] != UNSET) {
            fieldMask |= MINUTE_MASK;
        }
        if (stamp[SECOND] != UNSET) {
            fieldMask |= SECOND_MASK;
        }
        if (stamp[MILLISECOND] != UNSET) {
            fieldMask |= MILLISECOND_MASK;
        }
        if (stamp[ZONE_OFFSET] >= MINIMUM_USER_STAMP) {
                fieldMask |= ZONE_OFFSET_MASK;
        }
        if (stamp[DST_OFFSET] >= MINIMUM_USER_STAMP) {
            fieldMask |= DST_OFFSET_MASK;
        }

        return fieldMask;
    }

    int getBaseStyle(int style) {
        return style & ~STANDALONE_MASK;
    }

    private int toStandaloneStyle(int style) {
        return style | STANDALONE_MASK;
    }

    private boolean isStandaloneStyle(int style) {
        return (style & STANDALONE_MASK) != 0;
    }

    private boolean isNarrowStyle(int style) {
        return style == NARROW_FORMAT || style == NARROW_STANDALONE;
    }

    private boolean isNarrowFormatStyle(int style) {
        return style == NARROW_FORMAT;
    }

    /**
     * Returns the pseudo-time-stamp for two fields, given their
     * individual pseudo-time-stamps.  If either of the fields
     * is unset, then the aggregate is unset.  Otherwise, the
     * aggregate is the later of the two stamps.
     * 给定它们各自的伪时间戳，返回两个字段的伪时间戳。如果任一字段未设置，则聚合未设置。否则，合计是两个邮票中较晚的一个
     */
    private static int aggregateStamp(int stamp_a, int stamp_b) {
        if (stamp_a == UNSET || stamp_b == UNSET) {
            return UNSET;
        }
        return (stamp_a > stamp_b) ? stamp_a : stamp_b;
    }

    /**
     * Returns an unmodifiable {@code Set} containing all calendar types
     * supported by {@code Calendar} in the runtime environment. The available
     * calendar types can be used for the <a
     * href="Locale.html#def_locale_extension">Unicode locale extensions</a>.
     * The {@code Set} returned contains at least {@code "gregory"}. The
     * calendar types don't include aliases, such as {@code "gregorian"} for
     * {@code "gregory"}.
     * 返回一个不可修改的Set，其中包含运行时环境中Calendar支持的所有日历类型。
     * 可用的日历类型可用于Unicode 语言环境扩展。
     * 返回的Set至少包含"gregory"。日历类型不包括别名，例如"gregory"的 "gregorian"
     * @return an unmodifiable {@code Set} containing all available calendar types
     * @since 1.8
     * @see #getCalendarType()
     * @see Calendar.Builder#setCalendarType(String)
     * @see Locale#getUnicodeLocaleType(String)
     */
    public static Set<String> getAvailableCalendarTypes() {
        return AvailableCalendarTypes.SET;
    }

    private static class AvailableCalendarTypes {
        private static final Set<String> SET;
        static {
            Set<String> set = new HashSet<>(3);
            set.add("gregory");
            set.add("buddhist");
            set.add("japanese");
            SET = Collections.unmodifiableSet(set);
        }
        private AvailableCalendarTypes() {
        }
    }

    /**
     * Returns the calendar type of this {@code Calendar}. Calendar types are
     * defined by the <em>Unicode Locale Data Markup Language (LDML)</em>
     * specification.
     * 1.返回此Calendar的日历类型。日历类型由Unicode 区域设置数据标记语言 (LDML)规范定义。
     * <p>The default implementation of this method returns the class name of
     * this {@code Calendar} instance. Any subclasses that implement
     * LDML-defined calendar systems should override this method to return
     * appropriate calendar types.
     * 2.此方法的默认实现返回此Calendar实例的类名。任何实现 LDML 定义的日历系统的子类都应覆盖此方法以返回适当的日历类型
     * @return the LDML-defined calendar type or the class name of this
     *         {@code Calendar} instance
     * @since 1.8
     * @see <a href="Locale.html#def_extensions">Locale extensions</a>
     * @see Locale.Builder#setLocale(Locale)
     * @see Locale.Builder#setUnicodeLocaleKeyword(String, String)
     */
    public String getCalendarType() {
        return this.getClass().getName();
    }

    /**
     * Compares this <code>Calendar</code> to the specified
     * <code>Object</code>.  The result is <code>true</code> if and only if
     * the argument is a <code>Calendar</code> object of the same calendar
     * system that represents the same time value (millisecond offset from the
     * <a href="#Epoch">Epoch</a>) under the same
     * <code>Calendar</code> parameters as this object.
     * 1.将此Calendar与指定的Object进行比较。
     * 结果是true当且仅当参数是表示相同时间值的同一日历系统的Calendar对象（从 Epoch的毫秒偏移量）)
     * 在与此对象相同的Calendar参数下
     * <p>The <code>Calendar</code> parameters are the values represented
     * by the <code>isLenient</code>, <code>getFirstDayOfWeek</code>,
     * <code>getMinimalDaysInFirstWeek</code> and <code>getTimeZone</code>
     * methods. If there is any difference in those parameters
     * between the two <code>Calendar</code>s, this method returns
     * <code>false</code>.
     * 2.Calendar参数是由isLenient、getFirstDayOfWeek、getMinimalDaysInFirstWeek和getTimeZone方法表示的值。
     * 如果两个Calendar之间的那些参数有任何差异，则此方法返回false
     * <p>Use the {@link #compareTo(Calendar) compareTo} method to
     * compare only the time values.
     *
     * @param obj the object to compare with.
     * @return <code>true</code> if this object is equal to <code>obj</code>;
     * <code>false</code> otherwise.
     */
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        try {
            Calendar that = (Calendar)obj;
            return compareTo(getMillisOf(that)) == 0 &&
                lenient == that.lenient &&
                firstDayOfWeek == that.firstDayOfWeek &&
                minimalDaysInFirstWeek == that.minimalDaysInFirstWeek &&
                zone.equals(that.zone);
        } catch (Exception e) {
            // Note: GregorianCalendar.computeTime throws
            // IllegalArgumentException if the ERA value is invalid
            // even it's in lenient mode.
        }
        return false;
    }

    /**
     * Returns a hash code for this calendar.
     * 返回此日历的哈希码
     * @return a hash code value for this object.
     * @since 1.2
     */
    @Override
    public int hashCode() {
        // 'otheritems' represents the hash code for the previous versions.
        int otheritems = (lenient ? 1 : 0)
            | (firstDayOfWeek << 1)
            | (minimalDaysInFirstWeek << 4)
            | (zone.hashCode() << 7);
        long t = getMillisOf(this);
        return (int) t ^ (int)(t >> 32) ^ otheritems;
    }

    /**
     * Returns whether this <code>Calendar</code> represents a time
     * before the time represented by the specified
     * <code>Object</code>. This method is equivalent to:
     * <pre>{@code
     *         compareTo(when) < 0
     * }</pre>
     * if and only if <code>when</code> is a <code>Calendar</code>
     * instance. Otherwise, the method returns <code>false</code>.
     * 1.返回此 Calendar是否表示指定Object表示的时间之前的时间。
     * 此方法等效于：compareTo(when) < 0 当且仅当when是Calendar 实例。否则，该方法返回false。
     * @param when the <code>Object</code> to be compared
     * @return <code>true</code> if the time of this
     * <code>Calendar</code> is before the time represented by
     * <code>when</code>; <code>false</code> otherwise.
     * @see     #compareTo(Calendar)
     */
    public boolean before(Object when) {
        return when instanceof Calendar
            && compareTo((Calendar)when) < 0;
    }

    /**
     * Returns whether this <code>Calendar</code> represents a time
     * after the time represented by the specified
     * <code>Object</code>. This method is equivalent to:
     * <pre>{@code
     *         compareTo(when) > 0
     * }</pre>
     * if and only if <code>when</code> is a <code>Calendar</code>
     * instance. Otherwise, the method returns <code>false</code>.
     * 返回此Calendar是否表示指定Object表示的时间之后的时间。
     * 此方法等效于：compareTo(when) > 0 当且仅当when是Calendar实例。否则，该方法返回false
     * @param when the <code>Object</code> to be compared
     * @return <code>true</code> if the time of this <code>Calendar</code> is
     * after the time represented by <code>when</code>; <code>false</code>
     * otherwise.
     * @see     #compareTo(Calendar)
     */
    public boolean after(Object when) {
        return when instanceof Calendar
            && compareTo((Calendar)when) > 0;
    }

    /**
     * Compares the time values (millisecond offsets from the <a
     * href="#Epoch">Epoch</a>) represented by two
     * <code>Calendar</code> objects.
     * 1.比较由两个Calendar对象表示的时间值（与Epoch 的毫秒偏移量）
     * @param anotherCalendar the <code>Calendar</code> to be compared.
     * @return the value <code>0</code> if the time represented by the argument
     * is equal to the time represented by this <code>Calendar</code>; a value
     * less than <code>0</code> if the time of this <code>Calendar</code> is
     * before the time represented by the argument; and a value greater than
     * <code>0</code> if the time of this <code>Calendar</code> is after the
     * time represented by the argument.
     * @exception NullPointerException if the specified <code>Calendar</code> is
     *            <code>null</code>.
     * @exception IllegalArgumentException if the time value of the
     * specified <code>Calendar</code> object can't be obtained due to
     * any invalid calendar values.
     * @since   1.5
     */
    @Override
    public int compareTo(Calendar anotherCalendar) {
        return compareTo(getMillisOf(anotherCalendar));
    }

    /**
     * Adds or subtracts the specified amount of time to the given calendar field,
     * based on the calendar's rules. For example, to subtract 5 days from
     * the current time of the calendar, you can achieve it by calling:
     * <p><code>add(Calendar.DAY_OF_MONTH, -5)</code>.
     * 根据日历的规则，向给定的日历字段添加或减去指定的时间量。
     * 例如，要从日历的当前时间减去5天，可以通过调用add(Calendar.DAY_OF_MONTH, -5)来实现。
     * @param field the calendar field.
     * @param amount the amount of date or time to be added to the field.
     * @see #roll(int,int)
     * @see #set(int,int)
     */
    abstract public void add(int field, int amount);

    /**
     * Adds or subtracts (up/down) a single unit of time on the given time
     * field without changing larger fields. For example, to roll the current
     * date up by one day, you can achieve it by calling:
     * <p>roll(Calendar.DATE, true).
     * When rolling on the year or Calendar.YEAR field, it will roll the year
     * value in the range between 1 and the value returned by calling
     * <code>getMaximum(Calendar.YEAR)</code>.
     * When rolling on the month or Calendar.MONTH field, other fields like
     * date might conflict and, need to be changed. For instance,
     * rolling the month on the date 01/31/96 will result in 02/29/96.
     * When rolling on the hour-in-day or Calendar.HOUR_OF_DAY field, it will
     * roll the hour value in the range between 0 and 23, which is zero-based.
     * 在给定的时间字段上增加或减少（上下）一个时间单位而不改变更大的字段。
     * 例如，要将当前日期向上滚动一天，您可以通过调用：<p>roll(Calendar.DATE, true) 来实现。
     * 在年份或 Calendar.YEAR 字段上滚动时，它将在 1 和调用 <code>getMaximum(Calendar.YEAR)
     * 返回的值之间的范围内滚动年份值。在月份或 Calendar.MONTH 字段上滚动时，日期等其他字段可能会发生冲突，需要更改。
     * 例如，在日期 013196 上滚动月份将导致 022996。在一天中的小时或 Calendar.HOUR_OF_DAY 字段上滚动时，
     * 它将在 0 到 23 之间的范围内滚动小时值，这是从零开始的。
     * @param field the time field.
     * @param up indicates if the value of the specified time field is to be
     * rolled up or rolled down. Use true if rolling up, false otherwise.
     * @see Calendar#add(int,int)
     * @see Calendar#set(int,int)
     */
    abstract public void roll(int field, boolean up);

    /**
     * Adds the specified (signed) amount to the specified calendar field
     * without changing larger fields.  A negative amount means to roll
     * down.
     * 1.将指定（签名）数量添加到指定日历字段而不更改更大的字段。负数意味着向下滚动。
     * <p>NOTE:  This default implementation on <code>Calendar</code> just repeatedly calls the
     * version of {@link #roll(int,boolean) roll()} that rolls by one unit.  This may not
     * always do the right thing.  For example, if the <code>DAY_OF_MONTH</code> field is 31,
     * rolling through February will leave it set to 28.  The <code>GregorianCalendar</code>
     * version of this function takes care of this problem.  Other subclasses
     * should also provide overrides of this function that do the right thing.
     * 2.Calendar上的这个默认实现只是重复调用滚动一个单位的roll(int,boolean) roll()版本。
     * 这可能并不总是正确的做法。例如，如果DAY_OF_MONTH字段为 31，滚动到二月会将其设置为 28。
     * 此函数的 GregorianCalendar版本解决了这个问题。其他子类也应该提供这个函数的覆盖来做正确的事情
     * @param field the calendar field.
     * @param amount the signed amount to add to the calendar <code>field</code>.
     * @since 1.2
     * @see #roll(int,boolean)
     * @see #add(int,int)
     * @see #set(int,int)
     */
    public void roll(int field, int amount)
    {
        while (amount > 0) {
            roll(field, true);
            amount--;
        }
        while (amount < 0) {
            roll(field, false);
            amount++;
        }
    }

    /**
     * Sets the time zone with the given time zone value.
     * 使用给定的时区值设置时区
     * @param value the given time zone.
     */
    public void setTimeZone(TimeZone value)
    {
        zone = value;
        sharedZone = false;
        /* Recompute the fields from the time using the new zone.  This also
         * works if isTimeSet is false (after a call to set()).  In that case
         * the time will be computed from the fields using the new zone, then
         * the fields will get recomputed from that.  Consider the sequence of
         * calls: cal.setTimeZone(EST); cal.set(HOUR, 1); cal.setTimeZone(PST).
         * Is cal set to 1 o'clock EST or 1 o'clock PST?  Answer: PST.  More
         * generally, a call to setTimeZone() affects calls to set() BEFORE AND
         * AFTER it up to the next call to complete().
         */
        areAllFieldsSet = areFieldsSet = false;
    }

    /**
     * Gets the time zone.
     *
     * @return the time zone object associated with this calendar.
     */
    public TimeZone getTimeZone()
    {
        // If the TimeZone object is shared by other Calendar instances, then
        // create a clone.
        if (sharedZone) {
            zone = (TimeZone) zone.clone();
            sharedZone = false;
        }
        return zone;
    }

    /**
     * Returns the time zone (without cloning).
     */
    TimeZone getZone() {
        return zone;
    }

    /**
     * Sets the sharedZone flag to <code>shared</code>.
     * 将 sharedZone 标志设置为shared
     */
    void setZoneShared(boolean shared) {
        sharedZone = shared;
    }

    /**
     * Specifies whether or not date/time interpretation is to be lenient.  With
     * lenient interpretation, a date such as "February 942, 1996" will be
     * treated as being equivalent to the 941st day after February 1, 1996.
     * With strict (non-lenient) interpretation, such dates will cause an exception to be
     * thrown. The default is lenient.
     *指定日期时间解释是否宽松。对于宽松解释，诸如“February 942, 1996”之类的日期将被视为
     * 等同于 1996 年 2 月 1 日之后的第 941 天。对于严格（非宽松）解释，此类日期将导致抛出异常。默认是宽松的
     * @param lenient <code>true</code> if the lenient mode is to be turned
     * on; <code>false</code> if it is to be turned off.
     * @see #isLenient()
     * @see java.text.DateFormat#setLenient
     */
    public void setLenient(boolean lenient)
    {
        this.lenient = lenient;
    }

    /**
     * Tells whether date/time interpretation is to be lenient.
     * 告诉日期时间解释是否宽松
     * @return <code>true</code> if the interpretation mode of this calendar is lenient;
     * <code>false</code> otherwise.
     * @see #setLenient(boolean)
     */
    public boolean isLenient()
    {
        return lenient;
    }

    /**
     * Sets what the first day of the week is; e.g., <code>SUNDAY</code> in the U.S.,
     * <code>MONDAY</code> in France.
     * 设置一周的第一天是什么；例如，<code>SUNDAY<code> 在美国
     * @param value the given first day of the week.
     * @see #getFirstDayOfWeek()
     * @see #getMinimalDaysInFirstWeek()
     */
    public void setFirstDayOfWeek(int value)
    {
        if (firstDayOfWeek == value) {
            return;
        }
        firstDayOfWeek = value;
        invalidateWeekFields();
    }

    /**
     * Gets what the first day of the week is; e.g., <code>SUNDAY</code> in the U.S.,
     * <code>MONDAY</code> in France.
     * 获取一周的第一天是什么；例如，SUNDAY在美国,MONDAY=在法国。
     * @return the first day of the week.
     * @see #setFirstDayOfWeek(int)
     * @see #getMinimalDaysInFirstWeek()
     */
    public int getFirstDayOfWeek()
    {
        return firstDayOfWeek;
    }

    /**
     * Sets what the minimal days required in the first week of the year are;
     * For example, if the first week is defined as one that contains the first
     * day of the first month of a year, call this method with value 1. If it
     * must be a full week, use value 7.
     * 设置一年中第一周所需的最少天数；例如，如果第一周定义为包含一年第一个月的第一天，则使用值 1 调用此方法。如果它必须是完整的一周，则使用值 7。
     * @param value the given minimal days required in the first week
     * of the year.
     * @see #getMinimalDaysInFirstWeek()
     */
    public void setMinimalDaysInFirstWeek(int value)
    {
        if (minimalDaysInFirstWeek == value) {
            return;
        }
        minimalDaysInFirstWeek = value;
        invalidateWeekFields();
    }

    /**
     * Gets what the minimal days required in the first week of the year are;
     * e.g., if the first week is defined as one that contains the first day
     * of the first month of a year, this method returns 1. If
     * the minimal days required must be a full week, this method
     * returns 7.
     * 获取一年中第一周所需的最少天数；例如，如果第一周定义为包含一年第一个月的第一天，则此方法返回 1。如果所需的最少天数必须是整周，则此方法返回 7
     * @return the minimal days required in the first week of the year.
     * @see #setMinimalDaysInFirstWeek(int)
     */
    public int getMinimalDaysInFirstWeek()
    {
        return minimalDaysInFirstWeek;
    }

    /**
     * Returns whether this {@code Calendar} supports week dates.
     * 1.返回此Calendar是否支持周日期
     * <p>The default implementation of this method returns {@code false}.
     * 2.此方法的默认实现返回false。
     * @return {@code true} if this {@code Calendar} supports week dates;
     *         {@code false} otherwise.
     * @see #getWeekYear()
     * @see #setWeekDate(int,int,int)
     * @see #getWeeksInWeekYear()
     * @since 1.7
     */
    public boolean isWeekDateSupported() {
        return false;
    }

    /**
     * Returns the week year represented by this {@code Calendar}. The
     * week year is in sync with the week cycle. The {@linkplain
     * #getFirstDayOfWeek() first day of the first week} is the first
     * day of the week year.
     * 1.返回此Calendar表示的周年。周年与周周期同步。 getFirstDayOfWeek() 第一周的第一天}是一年中的第一天。
     * <p>The default implementation of this method throws an
     * {@link UnsupportedOperationException}.
     * 2.此方法的默认实现会引发 UnsupportedOperationException
     * @return the week year of this {@code Calendar}
     * @exception UnsupportedOperationException
     *            if any week year numbering isn't supported
     *            in this {@code Calendar}.
     * @see #isWeekDateSupported()
     * @see #getFirstDayOfWeek()
     * @see #getMinimalDaysInFirstWeek()
     * @since 1.7
     */
    public int getWeekYear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the date of this {@code Calendar} with the the given date
     * specifiers - week year, week of year, and day of week.
     * 1.使用给定的日期说明符设置此 Calendar的日期 - 周年、一年中的一周和一周中的某天
     * <p>Unlike the {@code set} method, all of the calendar fields
     * and {@code time} values are calculated upon return.
     * 2.与set方法不同，所有日历字段和time值都是在返回时计算的
     * <p>If {@code weekOfYear} is out of the valid week-of-year range
     * in {@code weekYear}, the {@code weekYear} and {@code
     * weekOfYear} values are adjusted in lenient mode, or an {@code
     * IllegalArgumentException} is thrown in non-lenient mode.
     * 3.如果weekOfYear超出weekYear中的有效周数范围，则weekYear和weekOfYear值在宽松模式下进行调整，
     * 或者 IllegalArgumentException以非宽松模式抛出
     * <p>The default implementation of this method throws an
     * {@code UnsupportedOperationException}.
     * 4.此方法的默认实现会引发 UnsupportedOperationException。
     * @param weekYear   the week year
     * @param weekOfYear the week number based on {@code weekYear}
     * @param dayOfWeek  the day of week value: one of the constants
     *                   for the {@link #DAY_OF_WEEK} field: {@link
     *                   #SUNDAY}, ..., {@link #SATURDAY}.
     * @exception IllegalArgumentException
     *            if any of the given date specifiers is invalid
     *            or any of the calendar fields are inconsistent
     *            with the given date specifiers in non-lenient mode
     * @exception UnsupportedOperationException
     *            if any week year numbering isn't supported in this
     *            {@code Calendar}.
     * @see #isWeekDateSupported()
     * @see #getFirstDayOfWeek()
     * @see #getMinimalDaysInFirstWeek()
     * @since 1.7
     */
    public void setWeekDate(int weekYear, int weekOfYear, int dayOfWeek) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number of weeks in the week year represented by this
     * {@code Calendar}.
     * 1.返回此Calendar 表示的一周年中的周数。
     * <p>The default implementation of this method throws an
     * {@code UnsupportedOperationException}.
     * 3.此方法的默认实现会引发UnsupportedOperationException。
     * @return the number of weeks in the week year.
     * @exception UnsupportedOperationException
     *            if any week year numbering isn't supported in this
     *            {@code Calendar}.
     * @see #WEEK_OF_YEAR
     * @see #isWeekDateSupported()
     * @see #getWeekYear()
     * @see #getActualMaximum(int)
     * @since 1.7
     */
    public int getWeeksInWeekYear() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the minimum value for the given calendar field of this
     * <code>Calendar</code> instance. The minimum value is defined as
     * the smallest value returned by the {@link #get(int) get} method
     * for any possible time value.  The minimum value depends on
     * calendar system specific parameters of the instance.
     * 返回此Calendar实例的给定日历字段的最小值。
     * 最小值定义为 get(int)方法为任何可能的时间值返回的最小值。最小值取决于实例的日历系统特定参数
     * @param field the calendar field.
     * @return the minimum value for the given calendar field.
     * @see #getMaximum(int)
     * @see #getGreatestMinimum(int)
     * @see #getLeastMaximum(int)
     * @see #getActualMinimum(int)
     * @see #getActualMaximum(int)
     */
    abstract public int getMinimum(int field);

    /**
     * Returns the maximum value for the given calendar field of this
     * <code>Calendar</code> instance. The maximum value is defined as
     * the largest value returned by the {@link #get(int) get} method
     * for any possible time value. The maximum value depends on
     * calendar system specific parameters of the instance.
     * 返回此Calendar实例的给定日历字段的最大值。
     * 最大值定义为get(int)方法为任何可能的时间值返回的最大值。最大值取决于实例的日历系统特定参数
     * @param field the calendar field.
     * @return the maximum value for the given calendar field.
     * @see #getMinimum(int)
     * @see #getGreatestMinimum(int)
     * @see #getLeastMaximum(int)
     * @see #getActualMinimum(int)
     * @see #getActualMaximum(int)
     */
    abstract public int getMaximum(int field);

    /**
     * Returns the highest minimum value for the given calendar field
     * of this <code>Calendar</code> instance. The highest minimum
     * value is defined as the largest value returned by {@link
     * #getActualMinimum(int)} for any possible time value. The
     * greatest minimum value depends on calendar system specific
     * parameters of the instance.
     * 返回此Calendar实例的给定日历字段的最高最小值。
     * 最高最小值定义为getActualMinimum(int)为任何可能的时间值返回的最大值。
     * 最大最小值取决于实例的日历系统特定参数
     * @param field the calendar field.
     * @return the highest minimum value for the given calendar field.
     * @see #getMinimum(int)
     * @see #getMaximum(int)
     * @see #getLeastMaximum(int)
     * @see #getActualMinimum(int)
     * @see #getActualMaximum(int)
     */
    abstract public int getGreatestMinimum(int field);

    /**
     * Returns the lowest maximum value for the given calendar field
     * of this <code>Calendar</code> instance. The lowest maximum
     * value is defined as the smallest value returned by {@link
     * #getActualMaximum(int)} for any possible time value. The least
     * maximum value depends on calendar system specific parameters of
     * the instance. For example, a <code>Calendar</code> for the
     * Gregorian calendar system returns 28 for the
     * <code>DAY_OF_MONTH</code> field, because the 28th is the last
     * day of the shortest month of this calendar, February in a
     * common year.
     * 返回此Calendar实例的给定日历字段的最低最大值。
     * 最低最大值定义为getActualMaximum(int)为任何可能的时间值返回的最小值。
     * 最小最大值取决于实例的日历系统特定参数。
     * 例如，公历系统的 Calendar为DAY_OF_MONTH字段返回 28，因为 28 日是该日历最短月份的最后一天，即普通年份的二月
     * @param field the calendar field.
     * @return the lowest maximum value for the given calendar field.
     * @see #getMinimum(int)
     * @see #getMaximum(int)
     * @see #getGreatestMinimum(int)
     * @see #getActualMinimum(int)
     * @see #getActualMaximum(int)
     */
    abstract public int getLeastMaximum(int field);

    /**
     * Returns the minimum value that the specified calendar field
     * could have, given the time value of this <code>Calendar</code>.
     * 1.给定此Calendar的时间值，返回指定日历字段可能具有的最小值
     * <p>The default implementation of this method uses an iterative
     * algorithm to determine the actual minimum value for the
     * calendar field. Subclasses should, if possible, override this
     * with a more efficient implementation - in many cases, they can
     * simply return <code>getMinimum()</code>.
     *2.此方法的默认实现使用迭代算法来确定日历字段的实际最小值。
     * 如果可能，子类应该使用更有效的实现来覆盖它 - 在许多情况下，它们可以简单地返回getMinimum()
     * @param field the calendar field
     * @return the minimum of the given calendar field for the time
     * value of this <code>Calendar</code>
     * @see #getMinimum(int)
     * @see #getMaximum(int)
     * @see #getGreatestMinimum(int)
     * @see #getLeastMaximum(int)
     * @see #getActualMaximum(int)
     * @since 1.2
     */
    public int getActualMinimum(int field) {
        int fieldValue = getGreatestMinimum(field);
        int endValue = getMinimum(field);

        // if we know that the minimum value is always the same, just return it
        //如果我们知道最小值总是相同的，就返回它
        if (fieldValue == endValue) {
            return fieldValue;
        }

        // clone the calendar so we don't mess with the real one, and set it to
        // accept anything for the field values
        //克隆日历，这样我们就不会弄乱真实的日历，并将其设置为接受字段值的任何内容
        Calendar work = (Calendar)this.clone();
        work.setLenient(true);

        // now try each value from getLeastMaximum() to getMaximum() one by one until
        // we get a value that normalizes to another value.  The last value that
        // normalizes to itself is the actual minimum for the current date
        //现在一个一个地尝试从 getLeastMaximum() 到 getMaximum() 的每个值，
        // 直到我们得到一个标准化为另一个值的值。归一化为自身的最后一个值是当前日期的实际最小值
        int result = fieldValue;

        do {
            work.set(field, fieldValue);
            if (work.get(field) != fieldValue) {
                break;
            } else {
                result = fieldValue;
                fieldValue--;
            }
        } while (fieldValue >= endValue);

        return result;
    }

    /**
     * Returns the maximum value that the specified calendar field
     * could have, given the time value of this
     * <code>Calendar</code>. For example, the actual maximum value of
     * the <code>MONTH</code> field is 12 in some years, and 13 in
     * other years in the Hebrew calendar system.
     * 1.给定此Calendar 的时间值，返回指定日历字段可能具有的最大值。
     * 例如，在希伯来日历系统中，MONTH字段的实际最大值在某些年份为 12，而在其他年份为 13
     * <p>The default implementation of this method uses an iterative
     * algorithm to determine the actual maximum value for the
     * calendar field. Subclasses should, if possible, override this
     * with a more efficient implementation.
     * 2.此方法的默认实现使用迭代算法来确定日历字段的实际最大值。如果可能，子类应该使用更有效的实现来覆盖它。
     * @param field the calendar field
     * @return the maximum of the given calendar field for the time
     * value of this <code>Calendar</code>
     * @see #getMinimum(int)
     * @see #getMaximum(int)
     * @see #getGreatestMinimum(int)
     * @see #getLeastMaximum(int)
     * @see #getActualMinimum(int)
     * @since 1.2
     */
    public int getActualMaximum(int field) {
        int fieldValue = getLeastMaximum(field);
        int endValue = getMaximum(field);

        // if we know that the maximum value is always the same, just return it.
        if (fieldValue == endValue) {
            return fieldValue;
        }

        // clone the calendar so we don't mess with the real one, and set it to
        // accept anything for the field values.
        Calendar work = (Calendar)this.clone();
        work.setLenient(true);

        // if we're counting weeks, set the day of the week to Sunday.  We know the
        // last week of a month or year will contain the first day of the week.
        if (field == WEEK_OF_YEAR || field == WEEK_OF_MONTH) {
            work.set(DAY_OF_WEEK, firstDayOfWeek);
        }

        // now try each value from getLeastMaximum() to getMaximum() one by one until
        // we get a value that normalizes to another value.  The last value that
        // normalizes to itself is the actual maximum for the current date
        int result = fieldValue;

        do {
            work.set(field, fieldValue);
            if (work.get(field) != fieldValue) {
                break;
            } else {
                result = fieldValue;
                fieldValue++;
            }
        } while (fieldValue <= endValue);

        return result;
    }

    /**
     * Creates and returns a copy of this object.
     * 创建并返回此对象的副本。
     * @return a copy of this object.
     */
    @Override
    public Object clone()
    {
        try {
            Calendar other = (Calendar) super.clone();

            other.fields = new int[FIELD_COUNT];
            other.isSet = new boolean[FIELD_COUNT];
            other.stamp = new int[FIELD_COUNT];
            for (int i = 0; i < FIELD_COUNT; i++) {
                other.fields[i] = fields[i];
                other.stamp[i] = stamp[i];
                other.isSet[i] = isSet[i];
            }
            other.zone = (TimeZone) zone.clone();
            return other;
        }
        catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }

    private static final String[] FIELD_NAME = {
        "ERA", "YEAR", "MONTH", "WEEK_OF_YEAR", "WEEK_OF_MONTH", "DAY_OF_MONTH",
        "DAY_OF_YEAR", "DAY_OF_WEEK", "DAY_OF_WEEK_IN_MONTH", "AM_PM", "HOUR",
        "HOUR_OF_DAY", "MINUTE", "SECOND", "MILLISECOND", "ZONE_OFFSET",
        "DST_OFFSET"
    };

    /**
     * Returns the name of the specified calendar field.
     * 返回指定日历字段的名称。
     * @param field the calendar field
     * @return the calendar field name
     * @exception IndexOutOfBoundsException if <code>field</code> is negative,
     * equal to or greater then <code>FIELD_COUNT</code>.
     */
    static String getFieldName(int field) {
        return FIELD_NAME[field];
    }

    /**
     * Return a string representation of this calendar. This method
     * is intended to be used only for debugging purposes, and the
     * format of the returned string may vary between implementations.
     * The returned string may be empty but may not be <code>null</code>.
     * 返回此日历的字符串表示形式。此方法仅用于调试目的，返回字符串的格式可能因实现而异。返回的字符串可能为空，但可能不是null。
     * @return  a string representation of this calendar.
     */
    @Override
    public String toString() {
        // NOTE: BuddhistCalendar.toString() interprets the string
        // produced by this method so that the Gregorian year number
        // is substituted by its B.E. year value. It relies on
        // "...,YEAR=<year>,..." or "...,YEAR=?,...".
        StringBuilder buffer = new StringBuilder(800);
        buffer.append(getClass().getName()).append('[');
        appendValue(buffer, "time", isTimeSet, time);
        buffer.append(",areFieldsSet=").append(areFieldsSet);
        buffer.append(",areAllFieldsSet=").append(areAllFieldsSet);
        buffer.append(",lenient=").append(lenient);
        buffer.append(",zone=").append(zone);
        appendValue(buffer, ",firstDayOfWeek", true, (long) firstDayOfWeek);
        appendValue(buffer, ",minimalDaysInFirstWeek", true, (long) minimalDaysInFirstWeek);
        for (int i = 0; i < FIELD_COUNT; ++i) {
            buffer.append(',');
            appendValue(buffer, FIELD_NAME[i], isSet(i), (long) fields[i]);
        }
        buffer.append(']');
        return buffer.toString();
    }

    // =======================privates===============================

    private static void appendValue(StringBuilder sb, String item, boolean valid, long value) {
        sb.append(item).append('=');
        if (valid) {
            sb.append(value);
        } else {
            sb.append('?');
        }
    }

    /**
     * Both firstDayOfWeek and minimalDaysInFirstWeek are locale-dependent.
     * They are used to figure out the week count for a specific date for
     * a given locale. These must be set when a Calendar is constructed.
     * firstDayOfWeek 和 minimumDaysInFirstWeek 都依赖于语言环境。
     * 它们用于计算给定语言环境的特定日期的周数。这些必须在构造 Calendar 时设置
     * @param desiredLocale the given locale.
     */
    private void setWeekCountData(Locale desiredLocale)
    {
        /* try to get the Locale data from the cache */
        int[] data = cachedLocaleData.get(desiredLocale);
        if (data == null) {  /* cache miss */
            data = new int[2];
            data[0] = CalendarDataUtility.retrieveFirstDayOfWeek(desiredLocale);
            data[1] = CalendarDataUtility.retrieveMinimalDaysInFirstWeek(desiredLocale);
            cachedLocaleData.putIfAbsent(desiredLocale, data);
        }
        firstDayOfWeek = data[0];
        minimalDaysInFirstWeek = data[1];
    }

    /**
     * Recomputes the time and updates the status fields isTimeSet
     * and areFieldsSet.  Callers should check isTimeSet and only
     * call this method if isTimeSet is false.
     * 重新计算时间并更新状态字段 isTimeSet 和 areFieldsSet。调用者应检查 isTimeSet 并且仅在 isTimeSet 为 false 时才调用此方法
     */
    private void updateTime() {
        computeTime();
        // The areFieldsSet and areAllFieldsSet values are no longer
        // controlled here (as of 1.5).
        isTimeSet = true;
    }

    private int compareTo(long t) {
        long thisTime = getMillisOf(this);
        return (thisTime > t) ? 1 : (thisTime == t) ? 0 : -1;
    }

    private static long getMillisOf(Calendar calendar) {
        if (calendar.isTimeSet) {
            return calendar.time;
        }
        Calendar cal = (Calendar) calendar.clone();
        cal.setLenient(true);
        return cal.getTimeInMillis();
    }

    /**
     * Adjusts the stamp[] values before nextStamp overflow. nextStamp
     * is set to the next stamp value upon the return.
     * 在nextStamp 溢出之前调整stamp[] 值。 nextStamp 设置为返回时的下一个标记值。
     */
    private void adjustStamp() {
        int max = MINIMUM_USER_STAMP;
        int newStamp = MINIMUM_USER_STAMP;

        for (;;) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < stamp.length; i++) {
                int v = stamp[i];
                if (v >= newStamp && min > v) {
                    min = v;
                }
                if (max < v) {
                    max = v;
                }
            }
            if (max != min && min == Integer.MAX_VALUE) {
                break;
            }
            for (int i = 0; i < stamp.length; i++) {
                if (stamp[i] == min) {
                    stamp[i] = newStamp;
                }
            }
            newStamp++;
            if (min == max) {
                break;
            }
        }
        nextStamp = newStamp;
    }

    /**
     * Sets the WEEK_OF_MONTH and WEEK_OF_YEAR fields to new values with the
     * new parameter value if they have been calculated internally.
     * 将 WEEK_OF_MONTH 和 WEEK_OF_YEAR 字段设置为具有新参数值的新值（如果它们已在内部计算）
     */
    private void invalidateWeekFields()
    {
        if (stamp[WEEK_OF_MONTH] != COMPUTED &&
            stamp[WEEK_OF_YEAR] != COMPUTED) {
            return;
        }

        // We have to check the new values of these fields after changing
        // firstDayOfWeek and/or minimalDaysInFirstWeek. If the field values
        // have been changed, then set the new values. (4822110)
        Calendar cal = (Calendar) clone();
        cal.setLenient(true);
        cal.clear(WEEK_OF_MONTH);
        cal.clear(WEEK_OF_YEAR);

        if (stamp[WEEK_OF_MONTH] == COMPUTED) {
            int weekOfMonth = cal.get(WEEK_OF_MONTH);
            if (fields[WEEK_OF_MONTH] != weekOfMonth) {
                fields[WEEK_OF_MONTH] = weekOfMonth;
            }
        }

        if (stamp[WEEK_OF_YEAR] == COMPUTED) {
            int weekOfYear = cal.get(WEEK_OF_YEAR);
            if (fields[WEEK_OF_YEAR] != weekOfYear) {
                fields[WEEK_OF_YEAR] = weekOfYear;
            }
        }
    }

    /**
     * Save the state of this object to a stream (i.e., serialize it).
     * 1.将此对象的状态保存到流中（即序列化它）
     * Ideally, <code>Calendar</code> would only write out its state data and
     * the current time, and not write any field data out, such as
     * <code>fields[]</code>, <code>isTimeSet</code>, <code>areFieldsSet</code>,
     * and <code>isSet[]</code>.  <code>nextStamp</code> also should not be part
     * of the persistent state. Unfortunately, this didn't happen before JDK 1.1
     * shipped. To be compatible with JDK 1.1, we will always have to write out
     * the field values and state flags.  However, <code>nextStamp</code> can be
     * removed from the serialization stream; this will probably happen in the
     * near future.
     * 2.理想情况下，Calendar只会写出它的状态数据和当前时间，而不会写出任何字段数据，
     * 例如fields[],isTimeSet,areFieldsSet和isSet[]。
     * nextStamp也不应该是持久状态的一部分。不幸的是，这在 JDK 1.1 发布之前没有发生。为了与 JDK 1.1 兼容，
     * 我们将始终必须写出字段值和状态标志。但是，nextStamp可以从序列化流中移除；这可能会在不久的将来发生
     */
    private synchronized void writeObject(ObjectOutputStream stream)
         throws IOException
    {
        // Try to compute the time correctly, for the future (stream
        // version 2) in which we don't write out fields[] or isSet[].
        if (!isTimeSet) {
            try {
                updateTime();
            }
            catch (IllegalArgumentException e) {}
        }

        // If this Calendar has a ZoneInfo, save it and set a
        // SimpleTimeZone equivalent (as a single DST schedule) for
        // backward compatibility.
        TimeZone savedZone = null;
        if (zone instanceof ZoneInfo) {
            SimpleTimeZone stz = ((ZoneInfo)zone).getLastRuleInstance();
            if (stz == null) {
                stz = new SimpleTimeZone(zone.getRawOffset(), zone.getID());
            }
            savedZone = zone;
            zone = stz;
        }

        // Write out the 1.1 FCS object.
        stream.defaultWriteObject();

        // Write out the ZoneInfo object
        // 4802409: we write out even if it is null, a temporary workaround
        // the real fix for bug 4844924 in corba-iiop
        stream.writeObject(savedZone);
        if (savedZone != null) {
            zone = savedZone;
        }
    }

    private static class CalendarAccessControlContext {
        private static final AccessControlContext INSTANCE;
        static {
            RuntimePermission perm = new RuntimePermission("accessClassInPackage.sun.util.calendar");
            PermissionCollection perms = perm.newPermissionCollection();
            perms.add(perm);
            INSTANCE = new AccessControlContext(new ProtectionDomain[] {
                                                    new ProtectionDomain(null, perms)
                                                });
        }
        private CalendarAccessControlContext() {
        }
    }

    /**
     * Reconstitutes this object from a stream (i.e., deserialize it).
     * 从流中重建这个对象（即反序列化它）
     */
    private void readObject(ObjectInputStream stream)
         throws IOException, ClassNotFoundException
    {
        final ObjectInputStream input = stream;
        input.defaultReadObject();

        stamp = new int[FIELD_COUNT];

        // Starting with version 2 (not implemented yet), we expect that
        // fields[], isSet[], isTimeSet, and areFieldsSet may not be
        // streamed out anymore.  We expect 'time' to be correct.
        if (serialVersionOnStream >= 2)
        {
            isTimeSet = true;
            if (fields == null) {
                fields = new int[FIELD_COUNT];
            }
            if (isSet == null) {
                isSet = new boolean[FIELD_COUNT];
            }
        }
        else if (serialVersionOnStream >= 0)
        {
            for (int i=0; i<FIELD_COUNT; ++i) {
                stamp[i] = isSet[i] ? COMPUTED : UNSET;
            }
        }

        serialVersionOnStream = currentSerialVersion;

        // If there's a ZoneInfo object, use it for zone.
        ZoneInfo zi = null;
        try {
            zi = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<ZoneInfo>() {
                        @Override
                        public ZoneInfo run() throws Exception {
                            return (ZoneInfo) input.readObject();
                        }
                    },
                    CalendarAccessControlContext.INSTANCE);
        } catch (PrivilegedActionException pae) {
            Exception e = pae.getException();
            if (!(e instanceof OptionalDataException)) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else if (e instanceof IOException) {
                    throw (IOException) e;
                } else if (e instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException) e;
                }
                throw new RuntimeException(e);
            }
        }
        if (zi != null) {
            zone = zi;
        }

        // If the deserialized object has a SimpleTimeZone, try to
        // replace it with a ZoneInfo equivalent (as of 1.4) in order
        // to be compatible with the SimpleTimeZone-based
        // implementation as much as possible.
        if (zone instanceof SimpleTimeZone) {
            String id = zone.getID();
            TimeZone tz = TimeZone.getTimeZone(id);
            if (tz != null && tz.hasSameRules(zone) && tz.getID().equals(id)) {
                zone = tz;
            }
        }
    }

    /**
     * Converts this object to an {@link Instant}.
     * <p>
     * The conversion creates an {@code Instant} that represents the
     * same point on the time-line as this {@code Calendar}.
     *
     * @return the instant representing the same point on the time-line
     * @since 1.8
     */
    public final Instant toInstant() {
        return Instant.ofEpochMilli(getTimeInMillis());
    }
}
