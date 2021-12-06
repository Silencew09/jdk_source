/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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

import sun.misc.FloatingDecimal;
import sun.misc.FloatConsts;
import sun.misc.DoubleConsts;

/**
 * The {@code Float} class wraps a value of primitive type
 * {@code float} in an object. An object of type
 * {@code Float} contains a single field whose type is
 * {@code float}.
 * 1.Float类将原始类型 float的值包装在一个对象中。Float类型的对象包含一个类型为float的字段
 * <p>In addition, this class provides several methods for converting a
 * {@code float} to a {@code String} and a
 * {@code String} to a {@code float}, as well as other
 * constants and methods useful when dealing with a
 * {@code float}.
 * 2.此外，该类提供了几种将float转换为String和将 String转换为 float的方法，以及在处理{float}
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @author  Joseph D. Darcy
 * @since JDK1.0
 */
public final class Float extends Number implements Comparable<Float> {
    /**
     * A constant holding the positive infinity of type
     * {@code float}. It is equal to the value returned by
     * {@code Float.intBitsToFloat(0x7f800000)}.
     * 保持float类型的正无穷大的常量。它等于 Float.intBitsToFloat(0x7f800000)返回的值
     */
    public static final float POSITIVE_INFINITY = 1.0f / 0.0f;

    /**
     * A constant holding the negative infinity of type
     * {@code float}. It is equal to the value returned by
     * {@code Float.intBitsToFloat(0xff800000)}.
     * 一个常量，持有float类型的负无穷大。它等于Float.intBitsToFloat(0xff800000)返回的值
     */
    public static final float NEGATIVE_INFINITY = -1.0f / 0.0f;

    /**
     * A constant holding a Not-a-Number (NaN) value of type
     * {@code float}.  It is equivalent to the value returned by
     * {@code Float.intBitsToFloat(0x7fc00000)}.
     * 持有float类型的非数字 (NaN) 值的常量。相当于Float.intBitsToFloat(0x7fc00000)返回的值
     */
    public static final float NaN = 0.0f / 0.0f;

    /**
     * A constant holding the largest positive finite value of type
     * {@code float}, (2-2<sup>-23</sup>)&middot;2<sup>127</sup>.
     * It is equal to the hexadecimal floating-point literal
     * {@code 0x1.fffffeP+127f} and also equal to
     * {@code Float.intBitsToFloat(0x7f7fffff)}.
     * 一个常量，持有float类型的最大正有限值。它等于十六进制浮点文字 0x1.fffffeP+127f
     * 也等于  Float.intBitsToFloat(0x7f7fffff)
     */
    public static final float MAX_VALUE = 0x1.fffffeP+127f; // 3.4028235e+38f

    /**
     * A constant holding the smallest positive normal value of type
     * {@code float}, 2<sup>-126</sup>.  It is equal to the
     * hexadecimal floating-point literal {@code 0x1.0p-126f} and also
     * equal to {@code Float.intBitsToFloat(0x00800000)}.
     *一个常量，持有float类型的最小正正常值.它等于十六进制浮点文字 0x1.0p-126f
     * 也等于Float.intBitsToFloat(0x00800000)
     * @since 1.6
     */
    public static final float MIN_NORMAL = 0x1.0p-126f; // 1.17549435E-38f

    /**
     * A constant holding the smallest positive nonzero value of type
     * {@code float}, 2<sup>-149</sup>. It is equal to the
     * hexadecimal floating-point literal {@code 0x0.000002P-126f}
     * and also equal to {@code Float.intBitsToFloat(0x1)}.
     * 一个常量，包含 float类型的最小正非零值。它等于十六进制浮点文字0x0.000002P-126f
     * 也等于 Float.intBitsToFloat(0x1)
     */
    public static final float MIN_VALUE = 0x0.000002P-126f; // 1.4e-45f

    /**
     * Maximum exponent a finite {@code float} variable may have.  It
     * is equal to the value returned by {@code
     * Math.getExponent(Float.MAX_VALUE)}.
     * 有限 float变量可能具有的最大指数。它等于 Math.getExponent(Float.MAX_VALUE)返回的值
     * @since 1.6
     */
    public static final int MAX_EXPONENT = 127;

    /**
     * Minimum exponent a normalized {@code float} variable may have.
     * It is equal to the value returned by {@code
     * Math.getExponent(Float.MIN_NORMAL)}.
     * 标准化 float变量可能具有的最小指数。它等于 Math.getExponent(Float.MIN_NORMAL)返回的值
     * @since 1.6
     */
    public static final int MIN_EXPONENT = -126;

    /**
     * The number of bits used to represent a {@code float} value.
     * 用于表示 float值的位数
     * @since 1.5
     */
    public static final int SIZE = 32;

    /**
     * The number of bytes used to represent a {@code float} value.
     * 用于表示 float值的字节数
     * @since 1.8
     */
    public static final int BYTES = SIZE / Byte.SIZE;

    /**
     * The {@code Class} instance representing the primitive type
     * {@code float}.
     * 表示原始类型float的  Class 实例
     * @since JDK1.1
     */
    @SuppressWarnings("unchecked")
    public static final Class<Float> TYPE = (Class<Float>) Class.getPrimitiveClass("float");

    /**
     * Returns a string representation of the {@code float}
     * argument. All characters mentioned below are ASCII characters.
     * <ul>
     *     1.返回 float参数的字符串表示形式
     *     2.下面提到的所有字符都是ASCII字符:
     *      1)如果参数是 NaN，则结果是字符串 "NaN"
     *      2)否则，结果是一个字符串，表示参数的符号和大小（绝对值）。
     *      如果符号为负，则结果的第一个字符为'-'；如果符号为正，则结果中不出现符号字符。
     *
     *     3.至于幅度m:
     * <li>If the argument is NaN, the result is the string
     * "{@code NaN}".
     * <li>Otherwise, the result is a string that represents the sign and
     *     magnitude (absolute value) of the argument. If the sign is
     *     negative, the first character of the result is
     *     '{@code -}' ({@code '\u005Cu002D'}); if the sign is
     *     positive, no sign character appears in the result. As for
     *     the magnitude <i>m</i>:
     * <ul>
     *     如果m为无穷大，则用字符"Infinity"表示；因此，正无穷产生结果"Infinity"，负无穷产生结果"-Infinity"
     * <li>If <i>m</i> is infinity, it is represented by the characters
     *     {@code "Infinity"}; thus, positive infinity produces
     *     the result {@code "Infinity"} and negative infinity
     *     produces the result {@code "-Infinity"}.
     * <li>If <i>m</i> is zero, it is represented by the characters
     *     {@code "0.0"}; thus, negative zero produces the result
     *     {@code "-0.0"} and positive zero produces the result
     *     {@code "0.0"}.
     *     如果m为零，则用字符"0.0"表示；因此，负零产生结果"-0.0"，正零产生结果"0.0"
     * <li> If <i>m</i> is greater than or equal to 10<sup>-3</sup> but
     *      less than 10<sup>7</sup>, then it is represented as the
     *      integer part of <i>m</i>, in decimal form with no leading
     *      zeroes, followed by '{@code .}'
     *      ({@code '\u005Cu002E'}), followed by one or more
     *      decimal digits representing the fractional part of
     *      <i>m</i>.
     *      如果m大于等于10的-3次方,但小于10的7次方，则表示为m的整数部分，十进制形式，
     *      没有前导零，后跟'.'，后跟一个或多个十进制数字，代表m的小数部分
     * <li> If <i>m</i> is less than 10<sup>-3</sup> or greater than or
     *      equal to 10<sup>7</sup>, then it is represented in
     *      so-called "computerized scientific notation." Let <i>n</i>
     *      be the unique integer such that 10<sup><i>n</i> </sup>&le;
     *      <i>m</i> {@literal <} 10<sup><i>n</i>+1</sup>; then let <i>a</i>
     *      be the mathematically exact quotient of <i>m</i> and
     *      10<sup><i>n</i></sup> so that 1 &le; <i>a</i> {@literal <} 10.
     *      The magnitude is then represented as the integer part of
     *      <i>a</i>, as a single decimal digit, followed by
     *      '{@code .}' ({@code '\u005Cu002E'}), followed by
     *      decimal digits representing the fractional part of
     *      <i>a</i>, followed by the letter '{@code E}'
     *      ({@code '\u005Cu0045'}), followed by a representation
     *      of <i>n</i> as a decimal integer, as produced by the
     *      method {@link java.lang.Integer#toString(int)}.
     *  如果m小于10<sup>-3<sup>或大于或等于10<sup>7<sup>，则用所谓的“计算机科学记数法”表示。
     *  令n为唯一整数，使得 10<sup>n <sup>≤ m <10<sup>n+1<sup>;
     *  然后让 a成为m和 10<sup>n<sup> 的数学上精确商，使得 1 ≤ a <10.
     *  然后将幅度表示为 a的整数部分，作为单个十进制数字，后跟 '.'，
     *  然后是由代表a 小数部分的十进制数字，后跟字母 'E' ，接着是n的表示作为十进制整数，由方法java.lang.IntegertoString(int)产生
     * </ul>
     * </ul>
     * How many digits must be printed for the fractional part of
     * <i>m</i> or <i>a</i>? There must be at least one digit
     * to represent the fractional part, and beyond that as many, but
     * only as many, more digits as are needed to uniquely distinguish
     * the argument value from adjacent values of type
     * {@code float}. That is, suppose that <i>x</i> is the
     * exact mathematical value represented by the decimal
     * representation produced by this method for a finite nonzero
     * argument <i>f</i>. Then <i>f</i> must be the {@code float}
     * value nearest to <i>x</i>; or, if two {@code float} values are
     * equally close to <i>x</i>, then <i>f</i> must be one of
     * them and the least significant bit of the significand of
     * <i>f</i> must be {@code 0}.
     * m或 a的小数部分必须打印多少位数字？必须至少有一个数字来表示小数部分，
     * 并且超过这个数字，但只能是唯一地将参数值与float类型的相邻值区分开来所需的数字。
     * 也就是说，假设 x是由此方法为有限非零参数 f 生成的十进制表示所表示的精确数学值。
     * 那么f必须是最接近x的float值；或者，如果两个float值同样接近 x，
     * 则 f必须是其中之一，并且是 f的有效数的最低有效位必须是0
     * <p>To create localized string representations of a floating-point
     * value, use subclasses of {@link java.text.NumberFormat}.
     * 要创建浮点值的本地化字符串表示，请使用  java.text.NumberFormat的子类
     * @param   f   the float to be converted.
     * @return a string representation of the argument.
     */
    public static String toString(float f) {
        return FloatingDecimal.toJavaFormatString(f);
    }

    /**
     * Returns a hexadecimal string representation of the
     * {@code float} argument. All characters mentioned below are
     * ASCII characters.
     * 返回 {@code float} 参数的十六进制字符串表示形式。
     * <ul>
     * <li>If the argument is NaN, the result is the string
     *     "{@code NaN}".
     * <li>Otherwise, the result is a string that represents the sign and
     * magnitude (absolute value) of the argument. If the sign is negative,
     * the first character of the result is '{@code -}'
     * ({@code '\u005Cu002D'}); if the sign is positive, no sign character
     * appears in the result. As for the magnitude <i>m</i>:
     *
     * <ul>
     * <li>If <i>m</i> is infinity, it is represented by the string
     * {@code "Infinity"}; thus, positive infinity produces the
     * result {@code "Infinity"} and negative infinity produces
     * the result {@code "-Infinity"}.
     *
     * <li>If <i>m</i> is zero, it is represented by the string
     * {@code "0x0.0p0"}; thus, negative zero produces the result
     * {@code "-0x0.0p0"} and positive zero produces the result
     * {@code "0x0.0p0"}.
     *
     * <li>If <i>m</i> is a {@code float} value with a
     * normalized representation, substrings are used to represent the
     * significand and exponent fields.  The significand is
     * represented by the characters {@code "0x1."}
     * followed by a lowercase hexadecimal representation of the rest
     * of the significand as a fraction.  Trailing zeros in the
     * hexadecimal representation are removed unless all the digits
     * are zero, in which case a single zero is used. Next, the
     * exponent is represented by {@code "p"} followed
     * by a decimal string of the unbiased exponent as if produced by
     * a call to {@link Integer#toString(int) Integer.toString} on the
     * exponent value.
     *
     * <li>If <i>m</i> is a {@code float} value with a subnormal
     * representation, the significand is represented by the
     * characters {@code "0x0."} followed by a
     * hexadecimal representation of the rest of the significand as a
     * fraction.  Trailing zeros in the hexadecimal representation are
     * removed. Next, the exponent is represented by
     * {@code "p-126"}.  Note that there must be at
     * least one nonzero digit in a subnormal significand.
     *
     * </ul>
     *
     * </ul>
     *
     * <table border>
     * <caption>Examples</caption>
     * <tr><th>Floating-point Value</th><th>Hexadecimal String</th>
     * <tr><td>{@code 1.0}</td> <td>{@code 0x1.0p0}</td>
     * <tr><td>{@code -1.0}</td>        <td>{@code -0x1.0p0}</td>
     * <tr><td>{@code 2.0}</td> <td>{@code 0x1.0p1}</td>
     * <tr><td>{@code 3.0}</td> <td>{@code 0x1.8p1}</td>
     * <tr><td>{@code 0.5}</td> <td>{@code 0x1.0p-1}</td>
     * <tr><td>{@code 0.25}</td>        <td>{@code 0x1.0p-2}</td>
     * <tr><td>{@code Float.MAX_VALUE}</td>
     *     <td>{@code 0x1.fffffep127}</td>
     * <tr><td>{@code Minimum Normal Value}</td>
     *     <td>{@code 0x1.0p-126}</td>
     * <tr><td>{@code Maximum Subnormal Value}</td>
     *     <td>{@code 0x0.fffffep-126}</td>
     * <tr><td>{@code Float.MIN_VALUE}</td>
     *     <td>{@code 0x0.000002p-126}</td>
     * </table>
     * @param   f   the {@code float} to be converted.
     * @return a hex string representation of the argument.
     * @since 1.5
     * @author Joseph D. Darcy
     */
    public static String toHexString(float f) {
        if (Math.abs(f) < FloatConsts.MIN_NORMAL
            &&  f != 0.0f ) {// float subnormal
            // Adjust exponent to create subnormal double, then
            // replace subnormal double exponent with subnormal float
            // exponent
            //float subnormal 调整指数以创建 subnormal double，然后用 subnormal float 指数替换 subnormal double 指数
            String s = Double.toHexString(Math.scalb((double)f,
                                                     /* -1022+126 */
                                                     DoubleConsts.MIN_EXPONENT-
                                                     FloatConsts.MIN_EXPONENT));
            return s.replaceFirst("p-1022$", "p-126");
        }
        else // double string will be the same as float string
            return Double.toHexString(f);
    }

    /**
     * Returns a {@code Float} object holding the
     * {@code float} value represented by the argument string
     * {@code s}.
     * 1.返回一个 Float对象，其中包含由参数字符串s表示的 float值
     * <p>If {@code s} is {@code null}, then a
     * {@code NullPointerException} is thrown.
     *2.如果 s为 null，则抛出NullPointerException
     * <p>Leading and trailing whitespace characters in {@code s}
     * are ignored.  Whitespace is removed as if by the {@link
     * String#trim} method; that is, both ASCII space and control
     * characters are removed. The rest of {@code s} should
     * constitute a <i>FloatValue</i> as described by the lexical
     * syntax rules:
     * 3.s中的前导和尾随空白字符将被忽略。像Stringtrim方法一样删除空格；也就是说，ASCII 空格和控制字符都被删除了,
     *  s 的其余部分应构成一个FloatValue ，如词法语法规则所述
     * <blockquote>
     * <dl>
     * <dt><i>FloatValue:</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code NaN}
     * <dd><i>Sign<sub>opt</sub></i> {@code Infinity}
     * <dd><i>Sign<sub>opt</sub> FloatingPointLiteral</i>
     * <dd><i>Sign<sub>opt</sub> HexFloatingPointLiteral</i>
     * <dd><i>SignedInteger</i>
     * </dl>
     *
     * <dl>
     * <dt><i>HexFloatingPointLiteral</i>:
     * <dd> <i>HexSignificand BinaryExponent FloatTypeSuffix<sub>opt</sub></i>
     * </dl>
     *
     * <dl>
     * <dt><i>HexSignificand:</i>
     * <dd><i>HexNumeral</i>
     * <dd><i>HexNumeral</i> {@code .}
     * <dd>{@code 0x} <i>HexDigits<sub>opt</sub>
     *     </i>{@code .}<i> HexDigits</i>
     * <dd>{@code 0X}<i> HexDigits<sub>opt</sub>
     *     </i>{@code .} <i>HexDigits</i>
     * </dl>
     *
     * <dl>
     * <dt><i>BinaryExponent:</i>
     * <dd><i>BinaryExponentIndicator SignedInteger</i>
     * </dl>
     *
     * <dl>
     * <dt><i>BinaryExponentIndicator:</i>
     * <dd>{@code p}
     * <dd>{@code P}
     * </dl>
     *
     * </blockquote>
     *
     * where <i>Sign</i>, <i>FloatingPointLiteral</i>,
     * <i>HexNumeral</i>, <i>HexDigits</i>, <i>SignedInteger</i> and
     * <i>FloatTypeSuffix</i> are as defined in the lexical structure
     * sections of
     * <cite>The Java&trade; Language Specification</cite>,
     * except that underscores are not accepted between digits.
     * 4.其中Sign、FloatingPointLiteral、HexNumeral、HexDigits、SignedInteger
     * 和 FloatTypeSuffix为在<Java™ 语言规范<的词法结构部分中定义，除了数字之间不接受下划线。
     * If {@code s} does not have the form of
     * a <i>FloatValue</i>, then a {@code NumberFormatException}
     * is thrown. Otherwise, {@code s} is regarded as
     * representing an exact decimal value in the usual
     * "computerized scientific notation" or as an exact
     * hexadecimal value; this exact numerical value is then
     * conceptually converted to an "infinitely precise"
     * binary value that is then rounded to type {@code float}
     * by the usual round-to-nearest rule of IEEE 754 floating-point
     * arithmetic, which includes preserving the sign of a zero
     * value.
     * 5.如果s不具有 FloatValue的形式，则抛出NumberFormatException。
     * 否则， s被视为表示通常的“计算机化科学记数法”中的精确十进制值或精确的十六进制值；
     * 然后，这个精确的数值在概念上被转换为一个“无限精确”的二进制值，
     * 然后通过 IEEE 754 浮点运算的通常舍入到最近的规则舍入到类型 float，其中包括保留一个零值
     * Note that the round-to-nearest rule also implies overflow and
     * underflow behaviour; if the exact value of {@code s} is large
     * enough in magnitude (greater than or equal to ({@link
     * #MAX_VALUE} + {@link Math#ulp(float) ulp(MAX_VALUE)}/2),
     * rounding to {@code float} will result in an infinity and if the
     * exact value of {@code s} is small enough in magnitude (less
     * than or equal to {@link #MIN_VALUE}/2), rounding to float will
     * result in a zero.
     * 6.请注意，舍入到最近的规则还暗示上溢和下溢行为；如果 s
     * 的准确值在数量级上足够大（大于或等于 MAX_VALUE +Math.ulp(float) ），
     * 四舍五入到float将导致无穷大，如果s的确切值在数量上足够小（小于或等于MIN_VALUE），
     * 四舍五入到浮点数将导致零。
     * Finally, after rounding a {@code Float} object representing
     * this {@code float} value is returned.
     * 7.最后，在舍入表示此float值的Float对象后返回
     * <p>To interpret localized string representations of a
     * floating-point value, use subclasses of {@link
     * java.text.NumberFormat}.
     * 8.要解释浮点值的本地化字符串表示，请使用java.text.NumberFormat的子类
     * <p>Note that trailing format specifiers, specifiers that
     * determine the type of a floating-point literal
     * ({@code 1.0f} is a {@code float} value;
     * {@code 1.0d} is a {@code double} value), do
     * <em>not</em> influence the results of this method.  In other
     * words, the numerical value of the input string is converted
     * directly to the target floating-point type.  In general, the
     * two-step sequence of conversions, string to {@code double}
     * followed by {@code double} to {@code float}, is
     * <em>not</em> equivalent to converting a string directly to
     * {@code float}.  For example, if first converted to an
     * intermediate {@code double} and then to
     * {@code float}, the string<br>
     * {@code "1.00000017881393421514957253748434595763683319091796875001d"}<br>
     * results in the {@code float} value
     * {@code 1.0000002f}; if the string is converted directly to
     * {@code float}, <code>1.000000<b>1</b>f</code> results.
     * 9.请注意尾随格式说明符，确定浮点文字类型的说明符1.0f是一个float值；
     * {1.0d} 是一个  double值），请执行不影响这个方法的结果。换句话说，
     * 输入字符串的数值直接转换为目标浮点类型。一般来说，字符串到 double
     * 后跟 double到float的两步转换序列不相当于将字符串直接转换为 {Double}。
     * 例如，如果首先转换为中间double，然后转换为 float，
     * 则字符串"1.00000017881393421514957253748434595763683319091796875001d"值
     * 导致浮点数代码 1.0000002f;如果字符串直接转换为 float，则结果为1.0000001f
     * <p>To avoid calling this method on an invalid string and having
     * a {@code NumberFormatException} be thrown, the documentation
     * for {@link Double#valueOf Double.valueOf} lists a regular
     * expression which can be used to screen the input.
     * 10.为了避免在无效字符串上调用此方法并抛出NumberFormatException，
     * Double.valueOf的文档列出了可用于筛选输入的正则表达式
     * @param   s   the string to be parsed.
     * @return  a {@code Float} object holding the value
     *          represented by the {@code String} argument.
     * @throws  NumberFormatException  if the string does not contain a
     *          parsable number.
     */
    public static Float valueOf(String s) throws NumberFormatException {
        return new Float(parseFloat(s));
    }

    /**
     * Returns a {@code Float} instance representing the specified
     * {@code float} value.
     * If a new {@code Float} instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Float(float)}, as this method is likely to yield
     * significantly better space and time performance by caching
     * frequently requested values.
     * 1.返回一个 Float实例，表示指定的 float值。
     * 2.如果不需要新的 Float实例，则通常应优先使用此方法而不是构造函数Float(float)，
     * 因为此方法可能会通过缓存频繁请求的内容来显着提高空间和时间性能价值观
     * @param  f a float value.
     * @return a {@code Float} instance representing {@code f}.
     * @since  1.5
     */
    public static Float valueOf(float f) {
        return new Float(f);
    }

    /**
     * Returns a new {@code float} initialized to the value
     * represented by the specified {@code String}, as performed
     * by the {@code valueOf} method of class {@code Float}.
     * 返回一个新的 float，初始化为由指定的String表示的值，由 Float类的 valueOf方法执行
     * @param  s the string to be parsed.
     * @return the {@code float} value represented by the string
     *         argument.
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string does not contain a
     *               parsable {@code float}.
     * @see    java.lang.Float#valueOf(String)
     * @since 1.2
     */
    public static float parseFloat(String s) throws NumberFormatException {
        return FloatingDecimal.parseFloat(s);
    }

    /**
     * Returns {@code true} if the specified number is a
     * Not-a-Number (NaN) value, {@code false} otherwise.
     * 如果指定的数字是非数字 (NaN)值，则返回 true，否则返回 false
     * @param   v   the value to be tested.
     * @return  {@code true} if the argument is NaN;
     *          {@code false} otherwise.
     */
    public static boolean isNaN(float v) {
        return (v != v);
    }

    /**
     * Returns {@code true} if the specified number is infinitely
     * large in magnitude, {@code false} otherwise.
     * 如果指定数字的大小无限大，则返回true，否则返回false。
     * @param   v   the value to be tested.
     * @return  {@code true} if the argument is positive infinity or
     *          negative infinity; {@code false} otherwise.
     */
    public static boolean isInfinite(float v) {
        return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }


    /**
     * Returns {@code true} if the argument is a finite floating-point
     * value; returns {@code false} otherwise (for NaN and infinity
     * arguments).
     * 如果参数是有限浮点值，则返回 true；否则返回 false,对于 NaN 和无穷大参数）
     * @param f the {@code float} value to be tested
     * @return {@code true} if the argument is a finite
     * floating-point value, {@code false} otherwise.
     * @since 1.8
     */
     public static boolean isFinite(float f) {
        return Math.abs(f) <= FloatConsts.MAX_VALUE;
    }

    /**
     * The value of the Float.
     *浮点数的值
     * @serial
     */
    private final float value;

    /**
     * Constructs a newly allocated {@code Float} object that
     * represents the primitive {@code float} argument.
     * 构造一个新分配的Float对象，表示原始float参数
     * @param   value   the value to be represented by the {@code Float}.
     */
    public Float(float value) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated {@code Float} object that
     * represents the argument converted to type {@code float}.
     * 构造一个新分配的Float 对象，该对象表示转换为 float类型的参数
     * @param   value   the value to be represented by the {@code Float}.
     */
    public Float(double value) {
        this.value = (float)value;
    }

    /**
     * Constructs a newly allocated {@code Float} object that
     * represents the floating-point value of type {@code float}
     * represented by the string. The string is converted to a
     * {@code float} value as if by the {@code valueOf} method.
     * 构造一个新分配的Float对象，该对象表示由字符串表示的float类型的浮点值。
     * 字符串被转换为 float值，就像通过 valueOf方法一样。
     * @param      s   a string to be converted to a {@code Float}.
     * @throws  NumberFormatException  if the string does not contain a
     *               parsable number.
     * @see        java.lang.Float#valueOf(java.lang.String)
     */
    public Float(String s) throws NumberFormatException {
        value = parseFloat(s);
    }

    /**
     * Returns {@code true} if this {@code Float} value is a
     * Not-a-Number (NaN), {@code false} otherwise.
     * 如果此Float 值是非数字 (NaN)，则返回true，否则返回false
     * @return  {@code true} if the value represented by this object is
     *          NaN; {@code false} otherwise.
     */
    public boolean isNaN() {
        return isNaN(value);
    }

    /**
     * Returns {@code true} if this {@code Float} value is
     * infinitely large in magnitude, {@code false} otherwise.
     * 如果此 Float值的大小无限大，则返回 true，否则返回false。
     * @return  {@code true} if the value represented by this object is
     *          positive infinity or negative infinity;
     *          {@code false} otherwise.
     */
    public boolean isInfinite() {
        return isInfinite(value);
    }

    /**
     * Returns a string representation of this {@code Float} object.
     * The primitive {@code float} value represented by this object
     * is converted to a {@code String} exactly as if by the method
     * {@code toString} of one argument.
     * 返回此Float对象的字符串表示形式。由该对象表示的原始float值被完全转换为 String，
     * 就像通过一个参数的方法 toString 一样。
     * @return  a {@code String} representation of this object.
     * @see java.lang.Float#toString(float)
     */
    public String toString() {
        return Float.toString(value);
    }

    /**
     * Returns the value of this {@code Float} as a {@code byte} after
     * a narrowing primitive conversion.
     * 在缩小原始转换后，将此 Float的值作为 byte返回
     * @return  the {@code float} value represented by this object
     *          converted to type {@code byte}
     * @jls 5.1.3 Narrowing Primitive Conversions
     */
    public byte byteValue() {
        return (byte)value;
    }

    /**
     * Returns the value of this {@code Float} as a {@code short}
     * after a narrowing primitive conversion.
     * 在缩小原始转换后，将此Float的值作为short返回
     * @return  the {@code float} value represented by this object
     *          converted to type {@code short}
     * @jls 5.1.3 Narrowing Primitive Conversions
     * @since JDK1.1
     */
    public short shortValue() {
        return (short)value;
    }

    /**
     * Returns the value of this {@code Float} as an {@code int} after
     * a narrowing primitive conversion.
     * 在缩小原始转换后，将此Float的值作为int返回。
     * @return  the {@code float} value represented by this object
     *          converted to type {@code int}
     * @jls 5.1.3 Narrowing Primitive Conversions
     */
    public int intValue() {
        return (int)value;
    }

    /**
     * Returns value of this {@code Float} as a {@code long} after a
     * narrowing primitive conversion.
     * 在缩小原始转换后，将此 Float的值作为 long返回。
     * @return  the {@code float} value represented by this object
     *          converted to type {@code long}
     * @jls 5.1.3 Narrowing Primitive Conversions
     */
    public long longValue() {
        return (long)value;
    }

    /**
     * Returns the {@code float} value of this {@code Float} object.
     * 返回此 Float对象的 float值
     * @return the {@code float} value represented by this object
     */
    public float floatValue() {
        return value;
    }

    /**
     * Returns the value of this {@code Float} as a {@code double}
     * after a widening primitive conversion.
     * 在扩展原始转换后，将此 Float的值作为 double返回。
     * @return the {@code float} value represented by this
     *         object converted to type {@code double}
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public double doubleValue() {
        return (double)value;
    }

    /**
     * Returns a hash code for this {@code Float} object. The
     * result is the integer bit representation, exactly as produced
     * by the method {@link #floatToIntBits(float)}, of the primitive
     * {@code float} value represented by this {@code Float}
     * object.
     * 返回此 Float对象的哈希码。结果是该 Float对象表示的原始float值的整数位表示，
     * 与方法 floatToIntBits(float)完全相同。
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

    /**
     * Returns a hash code for a {@code float} value; compatible with
     * {@code Float.hashCode()}.
     * 返回float 值的哈希码；与 Float.hashCode()兼容
     * @param value the value to hash
     * @return a hash code value for a {@code float} value.
     * @since 1.8
     */
    public static int hashCode(float value) {
        return floatToIntBits(value);
    }

    /**

     * Compares this object against the specified object.  The result
     * is {@code true} if and only if the argument is not
     * {@code null} and is a {@code Float} object that
     * represents a {@code float} with the same value as the
     * {@code float} represented by this object. For this
     * purpose, two {@code float} values are considered to be the
     * same if and only if the method {@link #floatToIntBits(float)}
     * returns the identical {@code int} value when applied to
     * each.
     * 1.将此对象与指定的对象进行比较。结果是 true当且仅当参数不是 null并且是一个 Float对象，
     * 该对象表示与 float具有相同值的 float通过这个对象。为此，当且仅当方法floatToIntBits(float)
     * 在应用于每个时返回相同的int值时，两个  float值被认为是相同的
     * <p>Note that in most cases, for two instances of class
     * {@code Float}, {@code f1} and {@code f2}, the value
     * of {@code f1.equals(f2)} is {@code true} if and only if
     *
     * <blockquote><pre>
     *   f1.floatValue() == f2.floatValue()
     * </pre></blockquote>
     *2.注意，在大多数情况下，对于 f1和 f2类的两个实例，f1.equals(f2)的值是true
     * 如果和仅当f1.floatValue() == f2.floatValue()
     * <p>also has the value {@code true}. However, there are two exceptions:
     * <ul>
     *     3.但是，有两个例外
     * <li>If {@code f1} and {@code f2} both represent
     *     {@code Float.NaN}, then the {@code equals} method returns
     *     {@code true}, even though {@code Float.NaN==Float.NaN}
     *     has the value {@code false}.
     *     如果 f1和 f2都表示 Float.NaN，那么equals方法返回true，
     *     即使 Float.NaN==Float.NaN的值为 false。
     * <li>If {@code f1} represents {@code +0.0f} while
     *     {@code f2} represents {@code -0.0f}, or vice
     *     versa, the {@code equal} test has the value
     *     {@code false}, even though {@code 0.0f==-0.0f}
     *     has the value {@code true}.
     *     如果f1代表+0.0f而f2代表-0.0f，反之亦然，equal测试的值为 false，
     *     即使尽管 0.0f==-0.0f的值为 true
     * </ul>
     *
     * This definition allows hash tables to operate properly.
     * 此定义允许哈希表正常运行
     * @param obj the object to be compared
     * @return  {@code true} if the objects are the same;
     *          {@code false} otherwise.
     * @see java.lang.Float#floatToIntBits(float)
     */
    public boolean equals(Object obj) {
        return (obj instanceof Float)
               && (floatToIntBits(((Float)obj).value) == floatToIntBits(value));
    }

    /**
     * Returns a representation of the specified floating-point value
     * according to the IEEE 754 floating-point "single format" bit
     * layout.
     * 1.根据 IEEE 754 浮点“单一格式”位布局返回指定浮点值的表示形式
     * <p>Bit 31 (the bit that is selected by the mask
     * {@code 0x80000000}) represents the sign of the floating-point
     * number.
     * 2.第 31 位（掩码 0x80000000选择的位）表示浮点数的符号
     * Bits 30-23 (the bits that are selected by the mask
     * {@code 0x7f800000}) represent the exponent.
     * 3.位 30-23（由掩码 0x7f800000选择的位）代表指数。
     * Bits 22-0 (the bits that are selected by the mask
     * {@code 0x007fffff}) represent the significand (sometimes called
     * the mantissa) of the floating-point number.
     * 4.位 22-0（由掩码 0x007fffff选择的位）表示浮点数的有效数（有时称为尾数）
     * <p>If the argument is positive infinity, the result is
     * {@code 0x7f800000}.
     * 5.如果参数为正无穷大，则结果为0x7f800000。
     * <p>If the argument is negative infinity, the result is
     * {@code 0xff800000}.
     * 6.如果参数为负无穷大，则结果为 0xff800000
     * <p>If the argument is NaN, the result is {@code 0x7fc00000}.
     * 7.如果参数为 NaN，则结果为 0x7fc00000
     * <p>In all cases, the result is an integer that, when given to the
     * {@link #intBitsToFloat(int)} method, will produce a floating-point
     * value the same as the argument to {@code floatToIntBits}
     * (except all NaN values are collapsed to a single
     * "canonical" NaN value).
     * 8.在所有情况下，结果都是一个整数，当给 intBitsToFloat(int)方法时，
     * 将产生一个与floatToIntBits的参数相同的浮点值（除了所有 NaN 值被折叠到单个“规范”NaN 值）
     * @param   value   a floating-point number.
     * @return the bits that represent the floating-point number.
     */
    public static int floatToIntBits(float value) {
        int result = floatToRawIntBits(value);
        // Check for NaN based on values of bit fields, maximum
        // exponent and nonzero significand.
        //根据位字段的值、最大指数和非零有效数检查 NaN。
        if ( ((result & FloatConsts.EXP_BIT_MASK) ==
              FloatConsts.EXP_BIT_MASK) &&
             (result & FloatConsts.SIGNIF_BIT_MASK) != 0)
            result = 0x7fc00000;
        return result;
    }

    /**
     * Returns a representation of the specified floating-point value
     * according to the IEEE 754 floating-point "single format" bit
     * layout, preserving Not-a-Number (NaN) values.
     *根据 IEEE 754 浮点“单一格式”位布局返回指定浮点值的表示形式，保留非数字 (NaN) 值
     * <p>Bit 31 (the bit that is selected by the mask
     * {@code 0x80000000}) represents the sign of the floating-point
     * number.
     * Bits 30-23 (the bits that are selected by the mask
     * {@code 0x7f800000}) represent the exponent.
     * Bits 22-0 (the bits that are selected by the mask
     * {@code 0x007fffff}) represent the significand (sometimes called
     * the mantissa) of the floating-point number.
     *
     * <p>If the argument is positive infinity, the result is
     * {@code 0x7f800000}.
     *
     * <p>If the argument is negative infinity, the result is
     * {@code 0xff800000}.
     *
     * <p>If the argument is NaN, the result is the integer representing
     * the actual NaN value.  Unlike the {@code floatToIntBits}
     * method, {@code floatToRawIntBits} does not collapse all the
     * bit patterns encoding a NaN to a single "canonical"
     * NaN value.
     *
     * <p>In all cases, the result is an integer that, when given to the
     * {@link #intBitsToFloat(int)} method, will produce a
     * floating-point value the same as the argument to
     * {@code floatToRawIntBits}.
     *
     * @param   value   a floating-point number.
     * @return the bits that represent the floating-point number.
     * @since 1.3
     */
    public static native int floatToRawIntBits(float value);

    /**
     * Returns the {@code float} value corresponding to a given
     * bit representation.
     * The argument is considered to be a representation of a
     * floating-point value according to the IEEE 754 floating-point
     * "single format" bit layout.
     *
     * <p>If the argument is {@code 0x7f800000}, the result is positive
     * infinity.
     *
     * <p>If the argument is {@code 0xff800000}, the result is negative
     * infinity.
     *
     * <p>If the argument is any value in the range
     * {@code 0x7f800001} through {@code 0x7fffffff} or in
     * the range {@code 0xff800001} through
     * {@code 0xffffffff}, the result is a NaN.  No IEEE 754
     * floating-point operation provided by Java can distinguish
     * between two NaN values of the same type with different bit
     * patterns.  Distinct values of NaN are only distinguishable by
     * use of the {@code Float.floatToRawIntBits} method.
     *
     * <p>In all other cases, let <i>s</i>, <i>e</i>, and <i>m</i> be three
     * values that can be computed from the argument:
     *
     * <blockquote><pre>{@code
     * int s = ((bits >> 31) == 0) ? 1 : -1;
     * int e = ((bits >> 23) & 0xff);
     * int m = (e == 0) ?
     *                 (bits & 0x7fffff) << 1 :
     *                 (bits & 0x7fffff) | 0x800000;
     * }</pre></blockquote>
     *
     * Then the floating-point result equals the value of the mathematical
     * expression <i>s</i>&middot;<i>m</i>&middot;2<sup><i>e</i>-150</sup>.
     *
     * <p>Note that this method may not be able to return a
     * {@code float} NaN with exactly same bit pattern as the
     * {@code int} argument.  IEEE 754 distinguishes between two
     * kinds of NaNs, quiet NaNs and <i>signaling NaNs</i>.  The
     * differences between the two kinds of NaN are generally not
     * visible in Java.  Arithmetic operations on signaling NaNs turn
     * them into quiet NaNs with a different, but often similar, bit
     * pattern.  However, on some processors merely copying a
     * signaling NaN also performs that conversion.  In particular,
     * copying a signaling NaN to return it to the calling method may
     * perform this conversion.  So {@code intBitsToFloat} may
     * not be able to return a {@code float} with a signaling NaN
     * bit pattern.  Consequently, for some {@code int} values,
     * {@code floatToRawIntBits(intBitsToFloat(start))} may
     * <i>not</i> equal {@code start}.  Moreover, which
     * particular bit patterns represent signaling NaNs is platform
     * dependent; although all NaN bit patterns, quiet or signaling,
     * must be in the NaN range identified above.
     *
     * @param   bits   an integer.
     * @return  the {@code float} floating-point value with the same bit
     *          pattern.
     */
    public static native float intBitsToFloat(int bits);

    /**
     * Compares two {@code Float} objects numerically.  There are
     * two ways in which comparisons performed by this method differ
     * from those performed by the Java language numerical comparison
     * operators ({@code <, <=, ==, >=, >}) when
     * applied to primitive {@code float} values:
     * 1.以数字方式比较两个Float对象。此方法执行的比较与 Java 语言数值比较运算符 ( <, <=, ==, >=, >)
     * 执行的比较有两种不同之处
     * 2.当应用于原始float 值时:
     *   此方法认为Float.NaN等于自身并大于所有其他float值（包括 Float.POSITIVE_INFINITY
     *   此方法认为 0.0f大于 -0.0f
     * <ul><li>
     *          {@code Float.NaN} is considered by this method to
     *          be equal to itself and greater than all other
     *          {@code float} values
     *          (including {@code Float.POSITIVE_INFINITY}).
     * <li>
     *          {@code 0.0f} is considered by this method to be greater
     *          than {@code -0.0f}.
     * </ul>
     * 3.这确保了此方法强加的Float对象的 自然顺序与 equals一致
     * This ensures that the <i>natural ordering</i> of {@code Float}
     * objects imposed by this method is <i>consistent with equals</i>.
     *
     * @param   anotherFloat   the {@code Float} to be compared.
     * @return  the value {@code 0} if {@code anotherFloat} is
     *          numerically equal to this {@code Float}; a value
     *          less than {@code 0} if this {@code Float}
     *          is numerically less than {@code anotherFloat};
     *          and a value greater than {@code 0} if this
     *          {@code Float} is numerically greater than
     *          {@code anotherFloat}.
     *
     * @since   1.2
     * @see Comparable#compareTo(Object)
     */
    public int compareTo(Float anotherFloat) {
        return Float.compare(value, anotherFloat.value);
    }

    /**
     * Compares the two specified {@code float} values. The sign
     * of the integer value returned is the same as that of the
     * integer that would be returned by the call:
     * <pre>
     *    new Float(f1).compareTo(new Float(f2))
     * </pre>
     *
     * @param   f1        the first {@code float} to compare.
     * @param   f2        the second {@code float} to compare.
     * @return  the value {@code 0} if {@code f1} is
     *          numerically equal to {@code f2}; a value less than
     *          {@code 0} if {@code f1} is numerically less than
     *          {@code f2}; and a value greater than {@code 0}
     *          if {@code f1} is numerically greater than
     *          {@code f2}.
     * @since 1.4
     */
    public static int compare(float f1, float f2) {
        if (f1 < f2)
            return -1;           // Neither val is NaN, thisVal is smaller
        if (f1 > f2)
            return 1;            // Neither val is NaN, thisVal is larger

        // Cannot use floatToRawIntBits because of possibility of NaNs.
        //由于可能存在 NaN，因此无法使用 floatToRawIntBits。
        int thisBits    = Float.floatToIntBits(f1);
        int anotherBits = Float.floatToIntBits(f2);

        return (thisBits == anotherBits ?  0 : // Values are equal
                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                 1));                          // (0.0, -0.0) or (NaN, !NaN)
    }

    /**
     * Adds two {@code float} values together as per the + operator.
     * 根据 + 运算符将两个 {@code float} 值相加。
     * @param a the first operand
     * @param b the second operand
     * @return the sum of {@code a} and {@code b}
     * @jls 4.2.4 Floating-Point Operations
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    public static float sum(float a, float b) {
        return a + b;
    }

    /**
     * Returns the greater of two {@code float} values
     * as if by calling {@link Math#max(float, float) Math.max}.
     * 返回两个 float值中的较大者，就像通过调用  Math.max(float, float) Math.max一样
     * @param a the first operand
     * @param b the second operand
     * @return the greater of {@code a} and {@code b}
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    public static float max(float a, float b) {
        return Math.max(a, b);
    }

    /**
     * Returns the smaller of two {@code float} values
     * as if by calling {@link Math#min(float, float) Math.min}.
     * 返回两个float值中较小的一个，就像调用 Math.min(float, float) Math.min
     * @param a the first operand
     * @param b the second operand
     * @return the smaller of {@code a} and {@code b}
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    public static float min(float a, float b) {
        return Math.min(a, b);
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -2671257302660747028L;
}
