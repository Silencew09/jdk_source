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
import sun.misc.FpUtils;
import sun.misc.DoubleConsts;

/**
 * The {@code Double} class wraps a value of the primitive type
 * {@code double} in an object. An object of type
 * {@code Double} contains a single field whose type is
 * {@code double}.
 * 1.Double类将原始类型 double的值包装在一个对象中。Double类型的对象包含一个类型为 double的字段
 * <p>In addition, this class provides several methods for converting a
 * {@code double} to a {@code String} and a
 * {@code String} to a {@code double}, as well as other
 * constants and methods useful when dealing with a
 * {@code double}.
 * 2.此外，该类提供了几种将 double转换为 String和将 String转换为 double的方法，以及在处理double
 * @author  Lee Boynton
 * @author  Arthur van Hoff
 * @author  Joseph D. Darcy
 * @since JDK1.0
 */
public final class Double extends Number implements Comparable<Double> {
    /**
     * A constant holding the positive infinity of type
     * 保持类型的正无穷大的常数
     * {@code double}. It is equal to the value returned by
     * {@code Double.longBitsToDouble(0x7ff0000000000000L)}.
     */
    public static final double POSITIVE_INFINITY = 1.0 / 0.0;

    /**
     * A constant holding the negative infinity of type
     * //保持类型的负无穷大的常数
     * {@code double}. It is equal to the value returned by
     * {@code Double.longBitsToDouble(0xfff0000000000000L)}.
     */
    public static final double NEGATIVE_INFINITY = -1.0 / 0.0;

    /**
     * A constant holding a Not-a-Number (NaN) value of type
     * 保存类型为非数字 (NaN) 值的常量
     * {@code double}. It is equivalent to the value returned by
     * {@code Double.longBitsToDouble(0x7ff8000000000000L)}.
     */
    public static final double NaN = 0.0d / 0.0;

    /**
     * A constant holding the largest positive finite value of type
     * 保持最大正有限值类型的常数
     * {@code double},
     * (2-2<sup>-52</sup>)&middot;2<sup>1023</sup>.  It is equal to
     * the hexadecimal floating-point literal
     * {@code 0x1.fffffffffffffP+1023} and also equal to
     * {@code Double.longBitsToDouble(0x7fefffffffffffffL)}.
     */
    public static final double MAX_VALUE = 0x1.fffffffffffffP+1023; // 1.7976931348623157e+308

    /**
     * A constant holding the smallest positive normal value of type
     * Double.MIN_NORMAL是您可以表示的最小可能数
     * {@code double}, 2<sup>-1022</sup>.  It is equal to the
     * hexadecimal floating-point literal {@code 0x1.0p-1022} and also
     * equal to {@code Double.longBitsToDouble(0x0010000000000000L)}.
     *
     * @since 1.6
     */
    public static final double MIN_NORMAL = 0x1.0p-1022; // 2.2250738585072014E-308

    /**
     * A constant holding the smallest positive nonzero value of type
     * Double.MIN_VALUE基本上是您可以在没有此约束的情况下表示的最小数字
     * {@code double}, 2<sup>-1074</sup>. It is equal to the
     * hexadecimal floating-point literal
     * {@code 0x0.0000000000001P-1022} and also equal to
     * {@code Double.longBitsToDouble(0x1L)}.
     */
    public static final double MIN_VALUE = 0x0.0000000000001P-1022; // 4.9e-324

    /**
     * Maximum exponent a finite {@code double} variable may have.
     * 有限 double变量可能具有的最大指数。
     * It is equal to the value returned by
     * {@code Math.getExponent(Double.MAX_VALUE)}.
     *
     * @since 1.6
     */
    public static final int MAX_EXPONENT = 1023;

    /**
     * Minimum exponent a normalized {@code double} variable may
     * have.  It is equal to the value returned by
     * {@code Math.getExponent(Double.MIN_NORMAL)}.
     * 标准化double变量可能具有的最小指数。它等于返回的值
     * @since 1.6
     */
    public static final int MIN_EXPONENT = -1022;

    /**
     * The number of bits used to represent a {@code double} value.
     * 用于表示double值的位数
     * @since 1.5
     */
    public static final int SIZE = 64;

    /**
     * The number of bytes used to represent a {@code double} value.
     * 用于表示 double值的字节数
     * @since 1.8
     */
    public static final int BYTES = SIZE / Byte.SIZE;

    /**
     * The {@code Class} instance representing the primitive type
     * {@code double}.
     * 表示原始类型 double的Class实例
     * @since JDK1.1
     */
    @SuppressWarnings("unchecked")
    public static final Class<Double>   TYPE = (Class<Double>) Class.getPrimitiveClass("double");

    /**
     * Returns a string representation of the {@code double}
     * argument. All characters mentioned below are ASCII characters.
     * 1.返回 double参数的字符串表示形式。下面提到的所有字符都是ASCII字符
     * <ul>
     * <li>If the argument is NaN, the result is the string
     *     "{@code NaN}".
     * 2.如果参数为NaN，则结果为字符串“NaN”,否则，结果是一个字符串，表示参数的符号和大小（绝对值）。
     * 如果符号为负，则结果的第一个字符为'-'；如果符号为正，则结果中不出现符号字符。
     * <li>Otherwise, the result is a string that represents the sign and
     * magnitude (absolute value) of the argument. If the sign is negative,
     * the first character of the result is '{@code -}'
     * ({@code '\u005Cu002D'}); if the sign is positive, no sign character
     * appears in the result. As for the magnitude <i>m</i>:
     * <ul>
     * <li>If <i>m</i> is infinity, it is represented by the characters
     * {@code "Infinity"}; thus, positive infinity produces the result
     * {@code "Infinity"} and negative infinity produces the result
     * {@code "-Infinity"}.
     *
     * <li>If <i>m</i> is zero, it is represented by the characters
     * {@code "0.0"}; thus, negative zero produces the result
     * {@code "-0.0"} and positive zero produces the result
     * {@code "0.0"}.
     * 3.至于幅度m：
     *      1)如果m为无穷大，则用字符"Infinity"表示；
     *      因此，正无穷产生结果 "Infinity"，负无穷产生结果 "-Infinity"。
     *      如果m为零，则用字符"0.0"表示；因此，负零产生结果"-0.0"，正零产生结果"0.0"
     * <li>If <i>m</i> is greater than or equal to 10<sup>-3</sup> but less
     * than 10<sup>7</sup>, then it is represented as the integer part of
     * <i>m</i>, in decimal form with no leading zeroes, followed by
     * '{@code .}' ({@code '\u005Cu002E'}), followed by one or
     * more decimal digits representing the fractional part of <i>m</i>.
     *      2)如果m大于等于10<sup>-3<sup>但小于10<sup>7<sup>，
     *      则表示为m，十进制形式，没有前导零，后跟'.'，后跟一个或多个十进制数字，代表m的小数部分
     * <li>If <i>m</i> is less than 10<sup>-3</sup> or greater than or
     * equal to 10<sup>7</sup>, then it is represented in so-called
     * "computerized scientific notation." Let <i>n</i> be the unique
     * integer such that 10<sup><i>n</i></sup> &le; <i>m</i> {@literal <}
     * 10<sup><i>n</i>+1</sup>; then let <i>a</i> be the
     * mathematically exact quotient of <i>m</i> and
     * 10<sup><i>n</i></sup> so that 1 &le; <i>a</i> {@literal <} 10. The
     * magnitude is then represented as the integer part of <i>a</i>,
     * as a single decimal digit, followed by '{@code .}'
     * ({@code '\u005Cu002E'}), followed by decimal digits
     * representing the fractional part of <i>a</i>, followed by the
     * letter '{@code E}' ({@code '\u005Cu0045'}), followed
     * by a representation of <i>n</i> as a decimal integer, as
     * produced by the method {@link Integer#toString(int)}.
     *      3)如果m小于10<sup>-3<sup>或大于或等于10<sup>7<sup>，则用所谓的“计算机科学记数法”表示。
     *      令 n为唯一整数，满足 10<sup>n<sup> ≤ m {@literal <} 10<sup><i>n< i>+1<sup>;
     *      然后让 a成为 m和 10<sup><i>n<i><sup> 的数学上精确商，使得 1 ≤a { <} 10.
     *      然后将幅度表示为 a的整数部分，作为单个十进制数字，后跟 '.'，
     *      然后是由十进制数字表示 a的小数部分，后跟字母 'E'，后跟 n的表示作为十进制整数，
     *      由方法 Integer.toString(int)产生
     * </ul>
     * </ul>
     * How many digits must be printed for the fractional part of
     * <i>m</i> or <i>a</i>? There must be at least one digit to represent
     * the fractional part, and beyond that as many, but only as many, more
     * digits as are needed to uniquely distinguish the argument value from
     * adjacent values of type {@code double}. That is, suppose that
     * <i>x</i> is the exact mathematical value represented by the decimal
     * representation produced by this method for a finite nonzero argument
     * <i>d</i>. Then <i>d</i> must be the {@code double} value nearest
     * to <i>x</i>; or if two {@code double} values are equally close
     * to <i>x</i>, then <i>d</i> must be one of them and the least
     * significant bit of the significand of <i>d</i> must be {@code 0}.
     *      4).m或 a的小数部分必须打印多少位数字？必须至少有一个数字来表示小数部分，
     *      并且超过这个数字，但只能是唯一地将参数值与double类型的相邻值区分开来所需的数字。
     *      也就是说，假设x是由此方法为有限非零参数 d生成的十进制表示所表示的精确数学值。
     *      那么d必须是最接近x的double值；或者如果两个double值同样接近x，
     *      则 d必须是其中之一，并且是 d的有效数的最低有效位必须是0
     * <p>To create localized string representations of a floating-point
     * value, use subclasses of {@link java.text.NumberFormat}.
     * 4.要创建浮点值的本地化字符串表示，请使用java.text.NumberFormat的子类
     * @param   d   the {@code double} to be converted.
     * @return a string representation of the argument.
     */
    public static String toString(double d) {
        return FloatingDecimal.toJavaFormatString(d);
    }

    /**
     * Returns a hexadecimal string representation of the
     * {@code double} argument. All characters mentioned below
     * are ASCII characters.
     * 1.返回 double参数的十六进制字符串表示形式。
     * 2.下面提到的所有字符都是ASCII字符:
     *      1)如果参数是 NaN，则结果是字符串 "NaN"
     *      2)否则，结果是一个字符串，表示参数的符号和大小。
     *      如果符号为负，则结果的第一个字符为'-' ；如果符号为正，则结果中不出现符号字符
     * <ul>
     * <li>If the argument is NaN, the result is the string
     *     "{@code NaN}".
     * <li>Otherwise, the result is a string that represents the sign
     * and magnitude of the argument. If the sign is negative, the
     * first character of the result is '{@code -}'
     * ({@code '\u005Cu002D'}); if the sign is positive, no sign
     * character appears in the result. As for the magnitude <i>m</i>:
     *  3.至于幅度m:
     *      1)如果m是无穷大，则用字符串 "Infinity"表示；因此，正无穷产生结果"Infinity"，
     *      负无穷产生结果"-Infinity"
     * <ul>
     * <li>If <i>m</i> is infinity, it is represented by the string
     * {@code "Infinity"}; thus, positive infinity produces the
     * result {@code "Infinity"} and negative infinity produces
     * the result {@code "-Infinity"}.
     *      2)如果 m为零，则由字符串 "0x0.0p0"表示；因此，负零产生结果 "-0x0.0p0"，
     *      正零产生结果 "0x0.0p0"
     * <li>If <i>m</i> is zero, it is represented by the string
     * {@code "0x0.0p0"}; thus, negative zero produces the result
     * {@code "-0x0.0p0"} and positive zero produces the result
     * {@code "0x0.0p0"}.
     *      3)如果m是具有标准化表示的double 值，则子字符串用于表示有效数和指数字段。
     *      有效数由字符 "0x1."表示，后跟有效数其余部分的小写十六进制表示形式。
     *      除非所有数字都为零，否则将删除十六进制表示中的尾随零，在这种情况下使用单个零。
     *      接下来，指数由"p"后跟无偏指数的十进制字符串表示，就像通过对指数值调用
     *      Integer.toString(int)产生的一样
     * <li>If <i>m</i> is a {@code double} value with a
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
     * <li>If <i>m</i> is a {@code double} value with a subnormal
     * representation, the significand is represented by the
     * characters {@code "0x0."} followed by a
     * hexadecimal representation of the rest of the significand as a
     * fraction.  Trailing zeros in the hexadecimal representation are
     * removed. Next, the exponent is represented by
     * {@code "p-1022"}.  Note that there must be at
     * least one nonzero digit in a subnormal significand.
     *      4)如果m 是具有次正规表示的 double 值，则有效数由字符 "0x0."表示，
     *      后跟有效数其余部分的十六进制表示作为分数.十六进制表示中的尾随零被删除。
     *      接下来，指数由 "p-1022"表示。请注意，次正规有效数中必须至少有一个非零数字
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
     * <tr><td>{@code Double.MAX_VALUE}</td>
     *     <td>{@code 0x1.fffffffffffffp1023}</td>
     * <tr><td>{@code Minimum Normal Value}</td>
     *     <td>{@code 0x1.0p-1022}</td>
     * <tr><td>{@code Maximum Subnormal Value}</td>
     *     <td>{@code 0x0.fffffffffffffp-1022}</td>
     * <tr><td>{@code Double.MIN_VALUE}</td>
     *     <td>{@code 0x0.0000000000001p-1022}</td>
     * </table>
     * @param   d   the {@code double} to be converted.
     * @return a hex string representation of the argument.
     * @since 1.5
     * @author Joseph D. Darcy
     */
    public static String toHexString(double d) {
        /*
         * Modeled after the "a" conversion specifier in C99, section
         * 7.19.6.1; however, the output of this method is more
         * tightly specified.
         */
        if (!isFinite(d) )
            // For infinity and NaN, use the decimal output.
            //对于无穷大和 NaN，使用十进制输出
            return Double.toString(d);
        else {
            // Initialized to maximum size of output.
            //初始化为最大输出大小
            StringBuilder answer = new StringBuilder(24);

            if (Math.copySign(1.0, d) == -1.0)    // value is negative,
                answer.append("-");                  // so append sign info

            answer.append("0x");

            d = Math.abs(d);

            if(d == 0.0) {
                answer.append("0.0p0");
            } else {
                boolean subnormal = (d < DoubleConsts.MIN_NORMAL);

                // Isolate significand bits and OR in a high-order bit
                // so that the string representation has a known
                // length.
                //隔离高位中的有效位和 OR，以便字符串表示具有已知长度
                long signifBits = (Double.doubleToLongBits(d)
                                   & DoubleConsts.SIGNIF_BIT_MASK) |
                    0x1000000000000000L;

                // Subnormal values have a 0 implicit bit; normal
                // values have a 1 implicit bit.
                //次正规值有一个 0 隐含位；正常值有 1 个隐式位
                answer.append(subnormal ? "0." : "1.");

                // Isolate the low-order 13 digits of the hex
                // representation.  If all the digits are zero,
                // replace with a single 0; otherwise, remove all
                // trailing zeros.
                //隔离十六进制表示的低 13 位数字。
                // 如果所有数字都为零，则替换为单个 0；否则，删除所有尾随零
                String signif = Long.toHexString(signifBits).substring(3,16);
                answer.append(signif.equals("0000000000000") ? // 13 zeros
                              "0":
                              signif.replaceFirst("0{1,12}$", ""));

                answer.append('p');
                // If the value is subnormal, use the E_min exponent
                // value for double; otherwise, extract and report d's
                // exponent (the representation of a subnormal uses
                // E_min -1).
                //如果该值低于正常值，则对 double 使用 E_min 指数值；
                // 否则，提取并报告 d 的指数（次正规的表示使用 E_min -1）
                answer.append(subnormal ?
                              DoubleConsts.MIN_EXPONENT:
                              Math.getExponent(d));
            }
            return answer.toString();
        }
    }

    /**
     * Returns a {@code Double} object holding the
     * {@code double} value represented by the argument string
     * {@code s}.
     * 1.返回一个Double对象，其中包含由参数字符串s表示的double值
     * <p>If {@code s} is {@code null}, then a
     * {@code NullPointerException} is thrown.
     * 2.如果 s为 null，则抛出NullPointerException。
     * <p>Leading and trailing whitespace characters in {@code s}
     * are ignored.  Whitespace is removed as if by the {@link
     * String#trim} method; that is, both ASCII space and control
     * characters are removed. The rest of {@code s} should
     * constitute a <i>FloatValue</i> as described by the lexical
     * syntax rules:
     * 3.s中的前导和尾随空白字符将被忽略。像 String.trim方法一样删除空格；
     * 也就是说，ASCII 空格和控制字符都被删除了,其余的 s应该构成一个FloatValue，
     *
     *
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
     * 3.其中Sign、FloatingPointLiteral、HexNumeral、HexDigits、SignedInteger
     * 和 FloatTypeSuffix为在Java™ 语言规范的词法结构部分中定义，除了数字之间不接受下划线
     * If {@code s} does not have the form of
     * a <i>FloatValue</i>, then a {@code NumberFormatException}
     * is thrown. Otherwise, {@code s} is regarded as
     * representing an exact decimal value in the usual
     * "computerized scientific notation" or as an exact
     * hexadecimal value; this exact numerical value is then
     * conceptually converted to an "infinitely precise"
     * binary value that is then rounded to type {@code double}
     * by the usual round-to-nearest rule of IEEE 754 floating-point
     * arithmetic, which includes preserving the sign of a zero
     * value.
     * 4.如果 s不具有 FloatValue的形式，则抛出 NumberFormatException
     * 5.否则，s被视为表示通常的“计算机化科学记数法”中的精确十进制值或精确的十六进制值；
     * 然后，这个精确的数值在概念上被转换为一个“无限精确”的二进制值，
     * 然后通过 IEEE 754 浮点运算的通常舍入到最近的规则舍入到类型 double，其中包括保留一个零值
     *
     * Note that the round-to-nearest rule also implies overflow and
     * underflow behaviour; if the exact value of {@code s} is large
     * enough in magnitude (greater than or equal to ({@link
     * #MAX_VALUE} + {@link Math#ulp(double) ulp(MAX_VALUE)}/2),
     * rounding to {@code double} will result in an infinity and if the
     * exact value of {@code s} is small enough in magnitude (less
     * than or equal to {@link #MIN_VALUE}/2), rounding to float will
     * result in a zero.
     * 6.请注意，舍入到最近的规则还暗示上溢和下溢行为
     * 7.如果 s的确切值在数量级上足够大（大于或等于 MAX_VALUE+ Math.ulp(double)），
     * 四舍五入到 double将导致无穷大，
     * 8.如果 s 的确切值在数量级上足够小（小于或等于 MIN_VALUE，四舍五入为浮点数将导致零
     * Finally, after rounding a {@code Double} object representing
     * this {@code double} value is returned.
     * 9.最后，在四舍五入后返回表示此 double值的 Double对象
     * <p> To interpret localized string representations of a
     * floating-point value, use subclasses of {@link
     * java.text.NumberFormat}.
     * 10.要解释浮点值的本地化字符串表示，请使用java.text.NumberFormat的子类
     * <p>Note that trailing format specifiers, specifiers that
     * determine the type of a floating-point literal
     * ({@code 1.0f} is a {@code float} value;
     * {@code 1.0d} is a {@code double} value), do
     * not influence the results of this method.  In other
     * words, the numerical value of the input string is converted
     * directly to the target floating-point type.  The two-step
     * sequence of conversions, string to {@code float} followed
     * by {@code float} to {@code double}, is <em>not</em>
     * equivalent to converting a string directly to
     * {@code double}. For example, the {@code float}
     * literal {@code 0.1f} is equal to the {@code double}
     * value {@code 0.10000000149011612}; the {@code float}
     * literal {@code 0.1f} represents a different numerical
     * value than the {@code double} literal
     * {@code 0.1}. (The numerical value 0.1 cannot be exactly
     * represented in a binary floating-point number.)
     * 11.请注意尾随格式说明符，确定浮点文字类型的说明符（1.0f是一个  float 值；
     * 1.0d是一个 double值），请执行不影响本方法的结果
     * 12.换句话说，输入字符串的数值直接转换为目标浮点类型
     * 13.两步转换序列，字符串到 float，然后是 float到double，不等效于将字符串直接转换为 double
     * 14.例如，float文字 0.1f 等于 double值 0.10000000149011612；float文字 0.1f表示与 double文字 0.1不同的数值。
     * （数值 0.1 不能用二进制浮点数精确表示。
     * <p>To avoid calling this method on an invalid string and having
     * a {@code NumberFormatException} be thrown, the regular
     * expression below can be used to screen the input string:
     * 15.为了避免在无效字符串上调用此方法并抛出NumberFormatException，
     * 可以使用下面的正则表达式来筛选输入字符串
     * <pre>{@code
     *  final String Digits     = "(\\p{Digit}+)";
     *  final String HexDigits  = "(\\p{XDigit}+)";
     *  // an exponent is 'e' or 'E' followed by an optionally
     *  // signed decimal integer.
     *  final String Exp        = "[eE][+-]?"+Digits;
     *  final String fpRegex    =
     *      ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
     *       "[+-]?(" + // Optional sign character
     *       "NaN|" +           // "NaN" string
     *       "Infinity|" +      // "Infinity" string
     *
     *       // A decimal floating-point string representing a finite positive
     *       // number without a leading sign has at most five basic pieces:
     *       // Digits . Digits ExponentPart FloatTypeSuffix
     *       //
     *       // Since this method allows integer-only strings as input
     *       // in addition to strings of floating-point literals, the
     *       // two sub-patterns below are simplifications of the grammar
     *       // productions from section 3.10.2 of
     *       // The Java Language Specification.
     *
     *       // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
     *       "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+
     *
     *       // . Digits ExponentPart_opt FloatTypeSuffix_opt
     *       "(\\.("+Digits+")("+Exp+")?)|"+
     *
     *       // Hexadecimal strings
     *       "((" +
     *        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
     *        "(0[xX]" + HexDigits + "(\\.)?)|" +
     *
     *        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
     *        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +
     *
     *        ")[pP][+-]?" + Digits + "))" +
     *       "[fFdD]?))" +
     *       "[\\x00-\\x20]*");// Optional trailing "whitespace"
     *
     *  if (Pattern.matches(fpRegex, myString))
     *      Double.valueOf(myString); // Will not throw NumberFormatException
     *  else {
     *      // Perform suitable alternative action
     *  }
     * }</pre>
     *
     * @param      s   the string to be parsed.
     * @return     a {@code Double} object holding the value
     *             represented by the {@code String} argument.
     * @throws     NumberFormatException  if the string does not contain a
     *             parsable number.
     */
    public static Double valueOf(String s) throws NumberFormatException {
        return new Double(parseDouble(s));
    }

    /**
     * Returns a {@code Double} instance representing the specified
     * {@code double} value.
     * If a new {@code Double} instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Double(double)}, as this method is likely to yield
     * significantly better space and time performance by caching
     * frequently requested values.
     * 1.返回表示指定的 double值的 Double实例。
     * 2.如果不需要新的Double实例，则通常应优先使用此方法而不是构造函数 Double(double)，
     * 因为此方法可能会通过缓存频繁请求的内容来显着提高空间和时间性能值
     * @param  d a double value.
     * @return a {@code Double} instance representing {@code d}.
     * @since  1.5
     */
    public static Double valueOf(double d) {
        return new Double(d);
    }

    /**
     * Returns a new {@code double} initialized to the value
     * represented by the specified {@code String}, as performed
     * by the {@code valueOf} method of class
     * {@code Double}.
     * 返回一个新的 double，初始化为由指定的 String表示的值，
     * 由 Double类的 valueOf法执行
     * @param  s   the string to be parsed.
     * @return the {@code double} value represented by the string
     *         argument.
     * @throws NullPointerException  if the string is null
     * @throws NumberFormatException if the string does not contain
     *         a parsable {@code double}.
     * @see    java.lang.Double#valueOf(String)
     * @since 1.2
     */
    public static double parseDouble(String s) throws NumberFormatException {
        return FloatingDecimal.parseDouble(s);
    }

    /**
     * Returns {@code true} if the specified number is a
     * Not-a-Number (NaN) value, {@code false} otherwise.
     * 1.如果指定的数字是非数字 (NaN) 值，则返回 true，否则返回false
     * @param   v   the value to be tested.
     * @return  {@code true} if the value of the argument is NaN;
     *          {@code false} otherwise.
     */
    public static boolean isNaN(double v) {
        return (v != v);
    }

    /**
     * Returns {@code true} if the specified number is infinitely
     * large in magnitude, {@code false} otherwise.
     * 如果指定数字的大小无限大，则返回 true，否则返回 false
     * @param   v   the value to be tested.
     * @return  {@code true} if the value of the argument is positive
     *          infinity or negative infinity; {@code false} otherwise.
     */
    public static boolean isInfinite(double v) {
        return (v == POSITIVE_INFINITY) || (v == NEGATIVE_INFINITY);
    }

    /**
     * Returns {@code true} if the argument is a finite floating-point
     * value; returns {@code false} otherwise (for NaN and infinity
     * arguments).
     * 如果参数是有限浮点值，则返回true；否则返回false对于 NaN 和无穷大参数
     * @param d the {@code double} value to be tested
     * @return {@code true} if the argument is a finite
     * floating-point value, {@code false} otherwise.
     * @since 1.8
     */
    public static boolean isFinite(double d) {
        return Math.abs(d) <= DoubleConsts.MAX_VALUE;
    }

    /**
     * The value of the Double.
     * Double 的值
     * @serial
     */
    private final double value;

    /**
     * Constructs a newly allocated {@code Double} object that
     * represents the primitive {@code double} argument.
     * 构造一个新分配的 Double对象，表示原始double参数
     * @param   value   the value to be represented by the {@code Double}.
     */
    public Double(double value) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated {@code Double} object that
     * represents the floating-point value of type {@code double}
     * represented by the string. The string is converted to a
     * {@code double} value as if by the {@code valueOf} method.
     * 1.构造一个新分配的 Double对象，该对象表示由字符串表示的double类型的浮点值。
     * 字符串被转换为 double值，就像通过 valueOf方法一样
     * @param  s  a string to be converted to a {@code Double}.
     * @throws    NumberFormatException  if the string does not contain a
     *            parsable number.
     * @see       java.lang.Double#valueOf(java.lang.String)
     */
    public Double(String s) throws NumberFormatException {
        value = parseDouble(s);
    }

    /**
     * Returns {@code true} if this {@code Double} value is
     * a Not-a-Number (NaN), {@code false} otherwise.
     *
     * @return  {@code true} if the value represented by this object is
     *          NaN; {@code false} otherwise.
     */
    public boolean isNaN() {
        return isNaN(value);
    }

    /**
     * Returns {@code true} if this {@code Double} value is
     * infinitely large in magnitude, {@code false} otherwise.
     *
     * @return  {@code true} if the value represented by this object is
     *          positive infinity or negative infinity;
     *          {@code false} otherwise.
     */
    public boolean isInfinite() {
        return isInfinite(value);
    }

    /**
     * Returns a string representation of this {@code Double} object.
     * The primitive {@code double} value represented by this
     * object is converted to a string exactly as if by the method
     * {@code toString} of one argument.
     *
     * @return  a {@code String} representation of this object.
     * @see java.lang.Double#toString(double)
     */
    public String toString() {
        return toString(value);
    }

    /**
     * Returns the value of this {@code Double} as a {@code byte}
     * after a narrowing primitive conversion.
     * 在缩小原始转换后将此 Double的值作为byte返回
     * @return  the {@code double} value represented by this object
     *          converted to type {@code byte}
     * @jls 5.1.3 Narrowing Primitive Conversions
     * @since JDK1.1
     */
    public byte byteValue() {
        return (byte)value;
    }

    /**
     * Returns the value of this {@code Double} as a {@code short}
     * after a narrowing primitive conversion.
     * 在缩小原始转换后，将此 Double的值作为short返回。
     * @return  the {@code double} value represented by this object
     *          converted to type {@code short}
     * @jls 5.1.3 Narrowing Primitive Conversions
     * @since JDK1.1
     */
    public short shortValue() {
        return (short)value;
    }

    /**
     * Returns the value of this {@code Double} as an {@code int}
     * after a narrowing primitive conversion.
     * @jls 5.1.3 Narrowing Primitive Conversions
     * 在缩小原始转换后，将此 Double的值作为 int返回。
     * @return  the {@code double} value represented by this object
     *          converted to type {@code int}
     */
    public int intValue() {
        return (int)value;
    }

    /**
     * Returns the value of this {@code Double} as a {@code long}
     * after a narrowing primitive conversion.
     * 在缩小原始转换后将此 Double的值作为 long返回
     * @return  the {@code double} value represented by this object
     *          converted to type {@code long}
     * @jls 5.1.3 Narrowing Primitive Conversions
     */
    public long longValue() {
        return (long)value;
    }

    /**
     * Returns the value of this {@code Double} as a {@code float}
     * after a narrowing primitive conversion.
     * 在缩小原始转换后将此 Double的值作为 float返回
     * @return  the {@code double} value represented by this object
     *          converted to type {@code float}
     * @jls 5.1.3 Narrowing Primitive Conversions
     * @since JDK1.0
     */
    public float floatValue() {
        return (float)value;
    }

    /**
     * Returns the {@code double} value of this {@code Double} object.
     * 返回此  Double对象的 double 值
     * @return the {@code double} value represented by this object
     */
    public double doubleValue() {
        return value;
    }

    /**
     * Returns a hash code for this {@code Double} object. The
     * result is the exclusive OR of the two halves of the
     * {@code long} integer bit representation, exactly as
     * produced by the method {@link #doubleToLongBits(double)}, of
     * the primitive {@code double} value represented by this
     * {@code Double} object. That is, the hash code is the value
     * of the expression:
     * 1.返回此Double对象的哈希码
     * 2.结果是 long整数位表示的两半的异或，与方法 doubleToLongBits(double)
     * 产生的完全一样，由这个 表示的原始double值代码 Double对象(int)(v^(v>>>32))
     * <blockquote>
     *  {@code (int)(v^(v>>>32))}
     * </blockquote>
     *
     * where {@code v} is defined by:
     *
     * <blockquote>
     *  {@code long v = Double.doubleToLongBits(this.doubleValue());}
     * </blockquote>
     *
     * @return  a {@code hash code} value for this object.
     */
    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    /**
     * Returns a hash code for a {@code double} value; compatible with
     * {@code Double.hashCode()}.
     * 1.返回双精度值的哈希码；与 Double.hashCode() 兼容
     * @param value the value to hash
     * @return a hash code value for a {@code double} value.
     * @since 1.8
     */
    public static int hashCode(double value) {
        long bits = doubleToLongBits(value);
        return (int)(bits ^ (bits >>> 32));
    }

    /**
     * Compares this object against the specified object.  The result
     * is {@code true} if and only if the argument is not
     * {@code null} and is a {@code Double} object that
     * represents a {@code double} that has the same value as the
     * {@code double} represented by this object. For this
     * purpose, two {@code double} values are considered to be
     * the same if and only if the method {@link
     * #doubleToLongBits(double)} returns the identical
     * {@code long} value when applied to each.
     *
     * <p>Note that in most cases, for two instances of class
     * {@code Double}, {@code d1} and {@code d2}, the
     * value of {@code d1.equals(d2)} is {@code true} if and
     * only if
     *
     * <blockquote>
     *  {@code d1.doubleValue() == d2.doubleValue()}
     * </blockquote>
     *
     * <p>also has the value {@code true}. However, there are two
     * exceptions:
     * <ul>
     * <li>If {@code d1} and {@code d2} both represent
     *     {@code Double.NaN}, then the {@code equals} method
     *     returns {@code true}, even though
     *     {@code Double.NaN==Double.NaN} has the value
     *     {@code false}.
     * <li>If {@code d1} represents {@code +0.0} while
     *     {@code d2} represents {@code -0.0}, or vice versa,
     *     the {@code equal} test has the value {@code false},
     *     even though {@code +0.0==-0.0} has the value {@code true}.
     * </ul>
     * This definition allows hash tables to operate properly.
     * @param   obj   the object to compare with.
     * @return  {@code true} if the objects are the same;
     *          {@code false} otherwise.
     * @see java.lang.Double#doubleToLongBits(double)
     */
    public boolean equals(Object obj) {
        return (obj instanceof Double)
               && (doubleToLongBits(((Double)obj).value) ==
                      doubleToLongBits(value));
    }

    /**
     * Returns a representation of the specified floating-point value
     * according to the IEEE 754 floating-point "double
     * format" bit layout.
     * 1.根据 IEEE 754 浮点“双格式”位布局返回指定浮点值的表示
     * <p>Bit 63 (the bit that is selected by the mask
     * {@code 0x8000000000000000L}) represents the sign of the
     * floating-point number. Bits
     * 62-52 (the bits that are selected by the mask
     * {@code 0x7ff0000000000000L}) represent the exponent. Bits 51-0
     * (the bits that are selected by the mask
     * {@code 0x000fffffffffffffL}) represent the significand
     * (sometimes called the mantissa) of the floating-point number.
     * 2.第 63 位（由掩码  0x8000000000000000L选择的位）表示浮点数的符号
     * 3.第 62-52 位（掩码0x7ff0000000000000L 选择的位）代表指数
     * 4.位 51-0（由掩码 0x000fffffffffffffL 选择的位）表示浮点数的有效数（有时称为尾数）
     * <p>If the argument is positive infinity, the result is
     * {@code 0x7ff0000000000000L}.
     * 5.如果参数为正无穷大，则结果为 0x7ff0000000000000L
     * <p>If the argument is negative infinity, the result is
     * {@code 0xfff0000000000000L}.
     * 6.如果参数为负无穷大，则结果为 0xfff0000000000000L
     * <p>If the argument is NaN, the result is
     * {@code 0x7ff8000000000000L}.
     * 7.如果参数为 NaN，则结果为 0x7ff8000000000000L
     * <p>In all cases, the result is a {@code long} integer that, when
     * given to the {@link #longBitsToDouble(long)} method, will produce a
     * floating-point value the same as the argument to
     * {@code doubleToLongBits} (except all NaN values are
     * collapsed to a single "canonical" NaN value).
     * 8.在所有情况下，结果都是一个 long整数，当给longBitsToDouble(long)方法时，
     * 将产生一个与 doubleToLongBits的参数相同的浮点值（除了所有 NaN 值都折叠为单个“规范”NaN 值）
     * @param   value   a {@code double} precision floating-point number.
     * @return the bits that represent the floating-point number.
     */
    public static long doubleToLongBits(double value) {
        long result = doubleToRawLongBits(value);
        // Check for NaN based on values of bit fields, maximum
        // exponent and nonzero significand.
        //根据位字段的值、最大指数和非零有效数检查 NaN
        if ( ((result & DoubleConsts.EXP_BIT_MASK) ==
              DoubleConsts.EXP_BIT_MASK) &&
             (result & DoubleConsts.SIGNIF_BIT_MASK) != 0L)
            result = 0x7ff8000000000000L;
        return result;
    }

    /**
     * Returns a representation of the specified floating-point value
     * according to the IEEE 754 floating-point "double
     * format" bit layout, preserving Not-a-Number (NaN) values.
     * 1.根据 IEEE 754 浮点“双格式”位布局返回指定浮点值的表示，保留非数字 (NaN) 值
     * <p>Bit 63 (the bit that is selected by the mask
     * {@code 0x8000000000000000L}) represents the sign of the
     * floating-point number. Bits
     * 62-52 (the bits that are selected by the mask
     * {@code 0x7ff0000000000000L}) represent the exponent. Bits 51-0
     * (the bits that are selected by the mask
     * {@code 0x000fffffffffffffL}) represent the significand
     * (sometimes called the mantissa) of the floating-point number.
     * 2.第63位（掩码选中的位0x8000000000000000L）表示浮点数的符号
     * 3.第 62-52 位（掩码0x7ff0000000000000L选择的位）代表指数
     * 4.位 51-0（由掩码 0x000fffffffffffffL选择的位）表示浮点数的有效数（有时称为尾数）
     * <p>If the argument is positive infinity, the result is
     * {@code 0x7ff0000000000000L}.
     * 5.如果参数为正无穷大，则结果为 0x7ff0000000000000L
     * <p>If the argument is negative infinity, the result is
     * {@code 0xfff0000000000000L}.
     * 6.如果参数为负无穷大，则结果为0xfff0000000000000L
     * <p>If the argument is NaN, the result is the {@code long}
     * integer representing the actual NaN value.  Unlike the
     * {@code doubleToLongBits} method,
     * {@code doubleToRawLongBits} does not collapse all the bit
     * patterns encoding a NaN to a single "canonical" NaN
     * value.
     * 7.如果参数是 NaN，则结果是表示实际 NaN 值的 long 整数
     * 8.与 doubleToLongBits方法不同， doubleToRawLongBits会将编码 NaN 的所有位模式折叠为单个“规范”NaN 值
     * <p>In all cases, the result is a {@code long} integer that,
     * when given to the {@link #longBitsToDouble(long)} method, will
     * produce a floating-point value the same as the argument to
     * {@code doubleToRawLongBits}.
     * 9.在所有情况下，结果都是一个long 整数，当将其提供给longBitsToDouble(long)方法时，将产生与 doubleToRawLongBits的参数相同的浮点值
     * @param   value   a {@code double} precision floating-point number.
     * @return the bits that represent the floating-point number.
     * @since 1.3
     */
    public static native long doubleToRawLongBits(double value);

    /**
     * Returns the {@code double} value corresponding to a given
     * bit representation.
     * The argument is considered to be a representation of a
     * floating-point value according to the IEEE 754 floating-point
     * "double format" bit layout.
     *
     * <p>If the argument is {@code 0x7ff0000000000000L}, the result
     * is positive infinity.
     *
     * <p>If the argument is {@code 0xfff0000000000000L}, the result
     * is negative infinity.
     *
     * <p>If the argument is any value in the range
     * {@code 0x7ff0000000000001L} through
     * {@code 0x7fffffffffffffffL} or in the range
     * {@code 0xfff0000000000001L} through
     * {@code 0xffffffffffffffffL}, the result is a NaN.  No IEEE
     * 754 floating-point operation provided by Java can distinguish
     * between two NaN values of the same type with different bit
     * patterns.  Distinct values of NaN are only distinguishable by
     * use of the {@code Double.doubleToRawLongBits} method.
     *
     * <p>In all other cases, let <i>s</i>, <i>e</i>, and <i>m</i> be three
     * values that can be computed from the argument:
     *
     * <blockquote><pre>{@code
     * int s = ((bits >> 63) == 0) ? 1 : -1;
     * int e = (int)((bits >> 52) & 0x7ffL);
     * long m = (e == 0) ?
     *                 (bits & 0xfffffffffffffL) << 1 :
     *                 (bits & 0xfffffffffffffL) | 0x10000000000000L;
     * }</pre></blockquote>
     *
     * Then the floating-point result equals the value of the mathematical
     * expression <i>s</i>&middot;<i>m</i>&middot;2<sup><i>e</i>-1075</sup>.
     *
     * <p>Note that this method may not be able to return a
     * {@code double} NaN with exactly same bit pattern as the
     * {@code long} argument.  IEEE 754 distinguishes between two
     * kinds of NaNs, quiet NaNs and <i>signaling NaNs</i>.  The
     * differences between the two kinds of NaN are generally not
     * visible in Java.  Arithmetic operations on signaling NaNs turn
     * them into quiet NaNs with a different, but often similar, bit
     * pattern.  However, on some processors merely copying a
     * signaling NaN also performs that conversion.  In particular,
     * copying a signaling NaN to return it to the calling method
     * may perform this conversion.  So {@code longBitsToDouble}
     * may not be able to return a {@code double} with a
     * signaling NaN bit pattern.  Consequently, for some
     * {@code long} values,
     * {@code doubleToRawLongBits(longBitsToDouble(start))} may
     * <i>not</i> equal {@code start}.  Moreover, which
     * particular bit patterns represent signaling NaNs is platform
     * dependent; although all NaN bit patterns, quiet or signaling,
     * must be in the NaN range identified above.
     *
     * @param   bits   any {@code long} integer.
     * @return  the {@code double} floating-point value with the same
     *          bit pattern.
     */
    public static native double longBitsToDouble(long bits);

    /**
     * Compares two {@code Double} objects numerically.  There
     * are two ways in which comparisons performed by this method
     * differ from those performed by the Java language numerical
     * comparison operators ({@code <, <=, ==, >=, >})
     * when applied to primitive {@code double} values:
     * <ul><li>
     *          {@code Double.NaN} is considered by this method
     *          to be equal to itself and greater than all other
     *          {@code double} values (including
     *          {@code Double.POSITIVE_INFINITY}).
     * <li>
     *          {@code 0.0d} is considered by this method to be greater
     *          than {@code -0.0d}.
     * </ul>
     * This ensures that the <i>natural ordering</i> of
     * {@code Double} objects imposed by this method is <i>consistent
     * with equals</i>.
     *
     * @param   anotherDouble   the {@code Double} to be compared.
     * @return  the value {@code 0} if {@code anotherDouble} is
     *          numerically equal to this {@code Double}; a value
     *          less than {@code 0} if this {@code Double}
     *          is numerically less than {@code anotherDouble};
     *          and a value greater than {@code 0} if this
     *          {@code Double} is numerically greater than
     *          {@code anotherDouble}.
     *
     * @since   1.2
     */
    public int compareTo(Double anotherDouble) {
        return Double.compare(value, anotherDouble.value);
    }

    /**
     * Compares the two specified {@code double} values. The sign
     * of the integer value returned is the same as that of the
     * integer that would be returned by the call:
     * <pre>
     *    new Double(d1).compareTo(new Double(d2))
     * </pre>
     *
     * @param   d1        the first {@code double} to compare
     * @param   d2        the second {@code double} to compare
     * @return  the value {@code 0} if {@code d1} is
     *          numerically equal to {@code d2}; a value less than
     *          {@code 0} if {@code d1} is numerically less than
     *          {@code d2}; and a value greater than {@code 0}
     *          if {@code d1} is numerically greater than
     *          {@code d2}.
     * @since 1.4
     */
    public static int compare(double d1, double d2) {
        if (d1 < d2)
            return -1;           // Neither val is NaN, thisVal is smaller
        if (d1 > d2)
            return 1;            // Neither val is NaN, thisVal is larger

        // Cannot use doubleToRawLongBits because of possibility of NaNs.
        long thisBits    = Double.doubleToLongBits(d1);
        long anotherBits = Double.doubleToLongBits(d2);

        return (thisBits == anotherBits ?  0 : // Values are equal
                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                 1));                          // (0.0, -0.0) or (NaN, !NaN)
    }

    /**
     * Adds two {@code double} values together as per the + operator.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the sum of {@code a} and {@code b}
     * @jls 4.2.4 Floating-Point Operations
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    public static double sum(double a, double b) {
        return a + b;
    }

    /**
     * Returns the greater of two {@code double} values
     * as if by calling {@link Math#max(double, double) Math.max}.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the greater of {@code a} and {@code b}
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    /**
     * Returns the smaller of two {@code double} values
     * as if by calling {@link Math#min(double, double) Math.min}.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the smaller of {@code a} and {@code b}.
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = -9172774392245257468L;
}
