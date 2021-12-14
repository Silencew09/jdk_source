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

package java.lang;

/**
 * The {@code Short} class wraps a value of primitive type {@code
 * short} in an object.  An object of type {@code Short} contains a
 * single field whose type is {@code short}.
 * 1.Short类将原始类型 short的值包装在一个对象中。 Short类型的对象包含一个类型为short的字段。
 * <p>In addition, this class provides several methods for converting
 * a {@code short} to a {@code String} and a {@code String} to a
 * {@code short}, as well as other constants and methods useful when
 * dealing with a {@code short}.
 * 2.此外，该类提供了几种将 short转换为String和将 String转换为short的方法，以及其他在处理short时有用的常量和方法。
 * @author  Nakul Saraiya
 * @author  Joseph D. Darcy
 * @see     java.lang.Number
 * @since   JDK1.1
 */
public final class Short extends Number implements Comparable<Short> {

    /**
     * A constant holding the minimum value a {@code short} can
     * have, -2<sup>15</sup>.
     * 保持short可以具有的最小值的常量，-2<sup>15<sup
     */
    public static final short   MIN_VALUE = -32768;

    /**
     * A constant holding the maximum value a {@code short} can
     * have, 2<sup>15</sup>-1.
     * 保持 short 可以具有的最大值的常量，2<sup>15<sup>-1
     */
    public static final short   MAX_VALUE = 32767;

    /**
     * The {@code Class} instance representing the primitive type
     * {@code short}.
     * 表示原始类型short的Class实例。
     */
    @SuppressWarnings("unchecked")
    public static final Class<Short>    TYPE = (Class<Short>) Class.getPrimitiveClass("short");

    /**
     * Returns a new {@code String} object representing the
     * specified {@code short}. The radix is assumed to be 10.
     * 返回一个新的 String对象，表示指定的 short。假设基数为 10。
     * @param s the {@code short} to be converted
     * @return the string representation of the specified {@code short}
     * @see java.lang.Integer#toString(int)
     */
    public static String toString(short s) {
        return Integer.toString((int)s, 10);
    }

    /**
     * Parses the string argument as a signed {@code short} in the
     * radix specified by the second argument. The characters in the
     * string must all be digits, of the specified radix (as
     * determined by whether {@link java.lang.Character#digit(char,
     * int)} returns a nonnegative value) except that the first
     * character may be an ASCII minus sign {@code '-'}
     * ({@code '\u005Cu002D'}) to indicate a negative value or an
     * ASCII plus sign {@code '+'} ({@code '\u005Cu002B'}) to
     * indicate a positive value.  The resulting {@code short} value
     * is returned.
     * 1.将字符串参数解析为第二个参数指定的基数中的带符号short。
     * 字符串中的字符必须都是指定基数的数字（由 java.lang.Character.digit(char, int)是否返回非负值决定），
     * 但第一个字符可以是 ASCII 减号 '-'表示负值或 ASCII 加号  '+'表示正值。返回结果 short值。
     * <p>An exception of type {@code NumberFormatException} is
     * thrown if any of the following situations occurs:
     * <ul>
     * <li> The first argument is {@code null} or is a string of
     * length zero.
     * 2.如果发生以下任何一种情况，则会抛出 NumberFormatException类型的异常：第一个参数是null或长度为零的字符串。
     * <li> The radix is either smaller than {@link
     * java.lang.Character#MIN_RADIX} or larger than {@link
     * java.lang.Character#MAX_RADIX}.
     * 3.基数小于 java.lang.CharacterMIN_RADIX或大于java.lang.CharacterMAX_RADIX
     * <li> Any character of the string is not a digit of the
     * specified radix, except that the first character may be a minus
     * sign {@code '-'} ({@code '\u005Cu002D'}) or plus sign
     * {@code '+'} ({@code '\u005Cu002B'}) provided that the
     * string is longer than length 1.
     * 4.字符串中的任何字符都不是指定基数的数字，除了第一个字符可以是减号'-'或加号'+' 前提是字符串的长度大于长度 1。
     * <li> The value represented by the string is not a value of type
     * {@code short}.
     * 5.字符串表示的值不是 short类型的值。
     * </ul>
     *
     * @param s         the {@code String} containing the
     *                  {@code short} representation to be parsed
     * @param radix     the radix to be used while parsing {@code s}
     * @return          the {@code short} represented by the string
     *                  argument in the specified radix.
     * @throws          NumberFormatException If the {@code String}
     *                  does not contain a parsable {@code short}.
     */
    public static short parseShort(String s, int radix)
        throws NumberFormatException {
        int i = Integer.parseInt(s, radix);
        if (i < MIN_VALUE || i > MAX_VALUE)
            throw new NumberFormatException(
                "Value out of range. Value:\"" + s + "\" Radix:" + radix);
        return (short)i;
    }

    /**
     * Parses the string argument as a signed decimal {@code
     * short}. The characters in the string must all be decimal
     * digits, except that the first character may be an ASCII minus
     * sign {@code '-'} ({@code '\u005Cu002D'}) to indicate a
     * negative value or an ASCII plus sign {@code '+'}
     * ({@code '\u005Cu002B'}) to indicate a positive value.  The
     * resulting {@code short} value is returned, exactly as if the
     * argument and the radix 10 were given as arguments to the {@link
     * #parseShort(java.lang.String, int)} method.
     * 1.将字符串参数解析为有符号十进制 short。字符串中的字符必须都是十进制数字，
     * 除了第一个字符可以是ASCII减号'-'表示负值或ASCII加号'+'表示正值。返回结果short值，
     * 就像参数和基数 10 作为参数提供给 parseShort(java.lang.String, int)方法一样
     * @param s a {@code String} containing the {@code short}
     *          representation to be parsed
     * @return  the {@code short} value represented by the
     *          argument in decimal.
     * @throws  NumberFormatException If the string does not
     *          contain a parsable {@code short}.
     */
    public static short parseShort(String s) throws NumberFormatException {
        return parseShort(s, 10);
    }

    /**
     * Returns a {@code Short} object holding the value
     * extracted from the specified {@code String} when parsed
     * with the radix given by the second argument. The first argument
     * is interpreted as representing a signed {@code short} in
     * the radix specified by the second argument, exactly as if the
     * argument were given to the {@link #parseShort(java.lang.String,
     * int)} method. The result is a {@code Short} object that
     * represents the {@code short} value specified by the string.
     * 1.返回一个Short对象，其中包含使用第二个参数给出的基数解析时从指定的String中提取的值。
     * 第一个参数被解释为表示由第二个参数指定的基数中的有符号short，
     * 就像参数被提供给parseShort(java.lang.String, int)方法一样。结果是一个Short对象，
     * 表示由字符串指定的short值。
     * <p>In other words, this method returns a {@code Short} object
     * equal to the value of:
     *
     * <blockquote>
     *  {@code new Short(Short.parseShort(s, radix))}
     * </blockquote>
     * 2.换句话说，此方法返回一个Short对象，其值等于：new Short(Short.parseShort(s, radix))
     * @param s         the string to be parsed
     * @param radix     the radix to be used in interpreting {@code s}
     * @return          a {@code Short} object holding the value
     *                  represented by the string argument in the
     *                  specified radix.
     * @throws          NumberFormatException If the {@code String} does
     *                  not contain a parsable {@code short}.
     */
    public static Short valueOf(String s, int radix)
        throws NumberFormatException {
        return valueOf(parseShort(s, radix));
    }

    /**
     * Returns a {@code Short} object holding the
     * value given by the specified {@code String}. The argument
     * is interpreted as representing a signed decimal
     * {@code short}, exactly as if the argument were given to
     * the {@link #parseShort(java.lang.String)} method. The result is
     * a {@code Short} object that represents the
     * {@code short} value specified by the string.
     * 1.返回一个Short对象，其中包含由指定的String给出的值。参数被解释为表示一个带符号的十进制short，
     * 就像参数被提供给parseShort(java.lang.String)方法一样。结果是一个Short对象，表示由字符串指定的short值。
     * <p>In other words, this method returns a {@code Short} object
     * equal to the value of:
     *
     * <blockquote>
     *  {@code new Short(Short.parseShort(s))}
     * </blockquote>
     * 2.换句话说，此方法返回一个Short对象，其值等于：new Short(Short.parseShort(s))
     * @param s the string to be parsed
     * @return  a {@code Short} object holding the value
     *          represented by the string argument
     * @throws  NumberFormatException If the {@code String} does
     *          not contain a parsable {@code short}.
     */
    public static Short valueOf(String s) throws NumberFormatException {
        return valueOf(s, 10);
    }

    private static class ShortCache {
        private ShortCache(){}

        static final Short cache[] = new Short[-(-128) + 127 + 1];

        static {
            for(int i = 0; i < cache.length; i++)
                cache[i] = new Short((short)(i - 128));
        }
    }

    /**
     * Returns a {@code Short} instance representing the specified
     * {@code short} value.
     * If a new {@code Short} instance is not required, this method
     * should generally be used in preference to the constructor
     * {@link #Short(short)}, as this method is likely to yield
     * significantly better space and time performance by caching
     * frequently requested values.
     * 1.返回表示指定的short值的Short实例
     *  2.如果不需要新的Short实例，则通常应优先使用此方法而不是构造函数Short(short)，
     *  因为此方法可能会通过缓存频繁请求的内容来显着提高空间和时间性能值。
     * This method will always cache values in the range -128 to 127,
     * inclusive, and may cache other values outside of this range.
     * 3.此方法将始终缓存 -128 到 127（含）范围内的值，并且可能缓存此范围之外的其他值
     * @param  s a short value.
     * @return a {@code Short} instance representing {@code s}.
     * @since  1.5
     */
    public static Short valueOf(short s) {
        final int offset = 128;
        int sAsInt = s;
        if (sAsInt >= -128 && sAsInt <= 127) { // must cache
            return ShortCache.cache[sAsInt + offset];
        }
        return new Short(s);
    }

    /**
     * Decodes a {@code String} into a {@code Short}.
     * Accepts decimal, hexadecimal, and octal numbers given by
     * the following grammar:
     * 1.将 String解码为 Short。接受语法给出的十进制、十六进制和八进制数
     * <blockquote>
     * <dl>
     * <dt><i>DecodableString:</i>
     * <dd><i>Sign<sub>opt</sub> DecimalNumeral</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0x} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0X} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code #} <i>HexDigits</i>
     * <dd><i>Sign<sub>opt</sub></i> {@code 0} <i>OctalDigits</i>
     *
     * <dt><i>Sign:</i>
     * <dd>{@code -}
     * <dd>{@code +}
     * </dl>
     * </blockquote>
     *
     * <i>DecimalNumeral</i>, <i>HexDigits</i>, and <i>OctalDigits</i>
     * are as defined in section 3.10.1 of
     * <cite>The Java&trade; Language Specification</cite>,
     * except that underscores are not accepted between digits.
     * 2.DecimalNumeral,HexDigits 和 <i>OctalDigits在Java™ 语言规范的 3.10.1 节中定义，但数字之间不接受下划线
     * <p>The sequence of characters following an optional
     * sign and/or radix specifier ("{@code 0x}", "{@code 0X}",
     * "{@code #}", or leading zero) is parsed as by the {@code
     * Short.parseShort} method with the indicated radix (10, 16, or
     * 8).  This sequence of characters must represent a positive
     * value or a {@link NumberFormatException} will be thrown.  The
     * result is negated if first character of the specified {@code
     * String} is the minus sign.  No whitespace characters are
     * permitted in the {@code String}.
     * 3.可选符号和/或基数说明符（“0x”、“0X”、“#”或前导零）之后的字符序列被解析为Short.parseShort
     * 具有指定基数（10、16 或 8）的方法。此字符序列必须表示正值，否则将抛出NumberFormatException。
     * 如果指定的 String的第一个字符是减号，则结果否定。String中不允许出现空白字符
     * @param     nm the {@code String} to decode.
     * @return    a {@code Short} object holding the {@code short}
     *            value represented by {@code nm}
     * @throws    NumberFormatException  if the {@code String} does not
     *            contain a parsable {@code short}.
     * @see java.lang.Short#parseShort(java.lang.String, int)
     */
    public static Short decode(String nm) throws NumberFormatException {
        int i = Integer.decode(nm);
        if (i < MIN_VALUE || i > MAX_VALUE)
            throw new NumberFormatException(
                    "Value " + i + " out of range from input " + nm);
        return valueOf((short)i);
    }

    /**
     * The value of the {@code Short}.
     * Short的值。
     * @serial
     */
    private final short value;

    /**
     * Constructs a newly allocated {@code Short} object that
     * represents the specified {@code short} value.
     * 构造一个新分配的 Short对象，表示指定的short值。
     * @param value     the value to be represented by the
     *                  {@code Short}.
     */
    public Short(short value) {
        this.value = value;
    }

    /**
     * Constructs a newly allocated {@code Short} object that
     * represents the {@code short} value indicated by the
     * {@code String} parameter. The string is converted to a
     * {@code short} value in exactly the manner used by the
     * {@code parseShort} method for radix 10.
     * 构造一个新分配的Short对象，该对象表示由String参数指示的short值。
     * 字符串按照 parseShort方法用于基数 10 的方式完全转换为short值。
     * @param s the {@code String} to be converted to a
     *          {@code Short}
     * @throws  NumberFormatException If the {@code String}
     *          does not contain a parsable {@code short}.
     * @see     java.lang.Short#parseShort(java.lang.String, int)
     */
    public Short(String s) throws NumberFormatException {
        this.value = parseShort(s, 10);
    }

    /**
     * Returns the value of this {@code Short} as a {@code byte} after
     * a narrowing primitive conversion.
     * 1.在缩小原始转换后，将此 Short的值作为byte返回
     * @jls 5.1.3 Narrowing Primitive Conversions
     */
    public byte byteValue() {
        return (byte)value;
    }

    /**
     * Returns the value of this {@code Short} as a
     * {@code short}.
     * 将此Short的值作为short返回。
     */
    public short shortValue() {
        return value;
    }

    /**
     * Returns the value of this {@code Short} as an {@code int} after
     * a widening primitive conversion.
     * 在扩展原始转换后，将此Short的值作为 int返回。
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public int intValue() {
        return (int)value;
    }

    /**
     * Returns the value of this {@code Short} as a {@code long} after
     * a widening primitive conversion.
     * 在扩展原始转换后，将此 Short的值作为 long返回。
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public long longValue() {
        return (long)value;
    }

    /**
     * Returns the value of this {@code Short} as a {@code float}
     * after a widening primitive conversion.
     * 在扩展原始转换后，将此Short的值作为 float返回。
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public float floatValue() {
        return (float)value;
    }

    /**
     * Returns the value of this {@code Short} as a {@code double}
     * after a widening primitive conversion.
     * 在扩展原始转换后，将此Short的值作为double返回。
     * @jls 5.1.2 Widening Primitive Conversions
     */
    public double doubleValue() {
        return (double)value;
    }

    /**
     * Returns a {@code String} object representing this
     * {@code Short}'s value.  The value is converted to signed
     * decimal representation and returned as a string, exactly as if
     * the {@code short} value were given as an argument to the
     * {@link java.lang.Short#toString(short)} method.
     * 1.返回表示此 Short值的String对象。该值被转换为有符号十进制表示并作为字符串返回，
     * 就像short值作为参数提供给java.lang.Short.toString(short)方法一样。
     * @return  a string representation of the value of this object in
     *          base&nbsp;10.
     */
    public String toString() {
        return Integer.toString((int)value);
    }

    /**
     * Returns a hash code for this {@code Short}; equal to the result
     * of invoking {@code intValue()}.
     * 返回此Short的哈希码；等于调用 intValue()的结果
     * @return a hash code value for this {@code Short}
     */
    @Override
    public int hashCode() {
        return Short.hashCode(value);
    }

    /**
     * Returns a hash code for a {@code short} value; compatible with
     * {@code Short.hashCode()}.
     * 返回 short值的哈希码；与 Short.hashCode()兼容。
     * @param value the value to hash
     * @return a hash code value for a {@code short} value.
     * @since 1.8
     */
    public static int hashCode(short value) {
        return (int)value;
    }

    /**
     * Compares this object to the specified object.  The result is
     * {@code true} if and only if the argument is not
     * {@code null} and is a {@code Short} object that
     * contains the same {@code short} value as this object.
     * 将此对象与指定的对象进行比较。结果是 true当且仅当参数不是 null并且
     * 是包含与此对象相同的 short值的 Short对象。
     * @param obj       the object to compare with
     * @return          {@code true} if the objects are the same;
     *                  {@code false} otherwise.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Short) {
            return value == ((Short)obj).shortValue();
        }
        return false;
    }

    /**
     * Compares two {@code Short} objects numerically.
     * 以数字方式比较两个Short对象
     * @param   anotherShort   the {@code Short} to be compared.
     * @return  the value {@code 0} if this {@code Short} is
     *          equal to the argument {@code Short}; a value less than
     *          {@code 0} if this {@code Short} is numerically less
     *          than the argument {@code Short}; and a value greater than
     *           {@code 0} if this {@code Short} is numerically
     *           greater than the argument {@code Short} (signed
     *           comparison).
     * @since   1.2
     */
    public int compareTo(Short anotherShort) {
        return compare(this.value, anotherShort.value);
    }

    /**
     * Compares two {@code short} values numerically.
     * The value returned is identical to what would be returned by:
     * <pre>
     *    Short.valueOf(x).compareTo(Short.valueOf(y))
     * </pre>
     * 以数字方式比较两个short值。返回的值与以下返回的值相同：Short.valueOf(x).compareTo(Short.valueOf(y))
     * @param  x the first {@code short} to compare
     * @param  y the second {@code short} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code x < y}; and
     *         a value greater than {@code 0} if {@code x > y}
     * @since 1.7
     */
    public static int compare(short x, short y) {
        return x - y;
    }

    /**
     * The number of bits used to represent a {@code short} value in two's
     * complement binary form.
     * 用于以二进制补码形式表示 short值的位数。
     * @since 1.5
     */
    public static final int SIZE = 16;

    /**
     * The number of bytes used to represent a {@code short} value in two's
     * complement binary form.
     * 用于以二进制补码形式表示short值的字节数
     * @since 1.8
     */
    public static final int BYTES = SIZE / Byte.SIZE;

    /**
     * Returns the value obtained by reversing the order of the bytes in the
     * two's complement representation of the specified {@code short} value.
     * 1.返回通过反转指定short值的二进制补码表示中的字节顺序而获得的值
     * @param i the value whose bytes are to be reversed
     * @return the value obtained by reversing (or, equivalently, swapping)
     *     the bytes in the specified {@code short} value.
     * @since 1.5
     */
    public static short reverseBytes(short i) {
        return (short) (((i & 0xFF00) >> 8) | (i << 8));
    }


    /**
     * Converts the argument to an {@code int} by an unsigned
     * conversion.  In an unsigned conversion to an {@code int}, the
     * high-order 16 bits of the {@code int} are zero and the
     * low-order 16 bits are equal to the bits of the {@code short} argument.
     * 1.通过无符号转换将参数转换为 int。在到int的无符号转换中，int的高 16 位为零，
     * 低 16 位等于 short参数的位。
     * Consequently, zero and positive {@code short} values are mapped
     * to a numerically equal {@code int} value and negative {@code
     * short} values are mapped to an {@code int} value equal to the
     * input plus 2<sup>16</sup>.
     * 2.因此，零和正 short值被映射到一个数值上相等的 int值，而负 short值被映射
     * 到一个int值等于输入加上 2<sup> 16<sup>
     * @param  x the value to convert to an unsigned {@code int}
     * @return the argument converted to {@code int} by an unsigned
     *         conversion
     * @since 1.8
     */
    public static int toUnsignedInt(short x) {
        return ((int) x) & 0xffff;
    }

    /**
     * Converts the argument to a {@code long} by an unsigned
     * conversion.  In an unsigned conversion to a {@code long}, the
     * high-order 48 bits of the {@code long} are zero and the
     * low-order 16 bits are equal to the bits of the {@code short} argument.
     * 1.通过无符号转换将参数转换为 long。在到 long的无符号转换中，long的高 48 位为零，
     * 低 16 位等于  short参数的位。
     * Consequently, zero and positive {@code short} values are mapped
     * to a numerically equal {@code long} value and negative {@code
     * short} values are mapped to a {@code long} value equal to the
     * input plus 2<sup>16</sup>.
     * 2.因此，零和正 short值被映射到一个数值相等的long值，负 short值被映射
     * 到一个 long值等于输入加上 2<sup> 16<sup>。
     * @param  x the value to convert to an unsigned {@code long}
     * @return the argument converted to {@code long} by an unsigned
     *         conversion
     * @since 1.8
     */
    public static long toUnsignedLong(short x) {
        return ((long) x) & 0xffffL;
    }

    /** use serialVersionUID from JDK 1.1. for interoperability */
    private static final long serialVersionUID = 7515723908773894738L;
}
