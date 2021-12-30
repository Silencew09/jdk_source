/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.io;

/**
 * The <code>DataOutput</code> interface provides
 * for converting data from any of the Java
 * primitive types to a series of bytes and
 * writing these bytes to a binary stream.
 * There is  also a facility for converting
 * a <code>String</code> into
 * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
 * format and writing the resulting series
 * of bytes.
 * 1.DataOutput接口提供了将数据从任何 Java 基本类型转换为一系列字节
 * 并将这些字节写入二进制流的功能。还有一种工具可以将String转换为
 * <a href="DataInput.htmlmodified-utf-8">修改后的 UTF-8格式并写入生成的一系列字节
 * <p>
 * For all the methods in this interface that
 * write bytes, it is generally true that if
 * a byte cannot be written for any reason,
 * an <code>IOException</code> is thrown.
 * 2.对于这个接口中所有写字节的方法，一般情况下，
 * 如果一个字节由于某种原因不能写，则抛出一个IOException
 * @author  Frank Yellin
 * @see     java.io.DataInput
 * @see     java.io.DataOutputStream
 * @since   JDK1.0
 */
public
interface DataOutput {
    /**
     * Writes to the output stream the eight
     * low-order bits of the argument <code>b</code>.
     * The 24 high-order  bits of <code>b</code>
     * are ignored.
     * 将参数b的 8 个低位写入输出流。 b的 24 个高位被忽略
     * @param      b   the byte to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void write(int b) throws IOException;

    /**
     * Writes to the output stream all the bytes in array <code>b</code>.
     * If <code>b</code> is <code>null</code>,
     * a <code>NullPointerException</code> is thrown.
     * If <code>b.length</code> is zero, then
     * no bytes are written. Otherwise, the byte
     * <code>b[0]</code> is written first, then
     * <code>b[1]</code>, and so on; the last byte
     * written is <code>b[b.length-1]</code>.
     * 将数组b中的所有字节写入输出流。如果b是null，则抛出NullPointerException。
     * 如果b.length为零，则不写入字节。否则，先写入字节b[0]，然后写入b[1]，
     * 依此类推；最后写入的字节是b[b.length-1]
     * @param      b   the data.
     * @throws     IOException  if an I/O error occurs.
     */
    void write(byte b[]) throws IOException;

    /**
     * Writes <code>len</code> bytes from array
     * <code>b</code>, in order,  to
     * the output stream.  If <code>b</code>
     * is <code>null</code>, a <code>NullPointerException</code>
     * is thrown.  If <code>off</code> is negative,
     * or <code>len</code> is negative, or <code>off+len</code>
     * is greater than the length of the array
     * <code>b</code>, then an <code>IndexOutOfBoundsException</code>
     * is thrown.  If <code>len</code> is zero,
     * then no bytes are written. Otherwise, the
     * byte <code>b[off]</code> is written first,
     * then <code>b[off+1]</code>, and so on; the
     * last byte written is <code>b[off+len-1]</code>.
     * 按顺序将len字节从数组b写入输出流。如果b是null，则抛出NullPointerException。
     * 如果off为负数，或len为负数，或off+len大于数组b的长度，则IndexOutOfBoundsException被抛出。
     * 如果len为零，则不写入字节。否则，先写入字节b[off]，然后写入b[off+1]，
     * 依此类推；最后写入的字节是b[off+len-1]
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @throws     IOException  if an I/O error occurs.
     */
    void write(byte b[], int off, int len) throws IOException;

    /**
     * Writes a <code>boolean</code> value to this output stream.
     * If the argument <code>v</code>
     * is <code>true</code>, the value <code>(byte)1</code>
     * is written; if <code>v</code> is <code>false</code>,
     * the  value <code>(byte)0</code> is written.
     * The byte written by this method may
     * be read by the <code>readBoolean</code>
     * method of interface <code>DataInput</code>,
     * which will then return a <code>boolean</code>
     * equal to <code>v</code>.
     * 将boolean值写入此输出流。如果参数v为true，则写入值(byte)1；如果v是false，
     * 则写入值(byte)0。该方法写入的字节可以被接口DataInput的readBoolean方法读取，
     * 然后返回一个boolean等于v
     * @param      v   the boolean to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeBoolean(boolean v) throws IOException;

    /**
     * Writes to the output stream the eight low-
     * order bits of the argument <code>v</code>.
     * The 24 high-order bits of <code>v</code>
     * are ignored. (This means  that <code>writeByte</code>
     * does exactly the same thing as <code>write</code>
     * for an integer argument.) The byte written
     * by this method may be read by the <code>readByte</code>
     * method of interface <code>DataInput</code>,
     * which will then return a <code>byte</code>
     * equal to <code>(byte)v</code>.
     * 将参数v的 8 个低位写入输出流。v 的 24 个高位被忽略。
     * （这意味着writeByte与write对于整数参数的作用完全相同。）
     * 此方法写入的字节可以通过readByte的方法读取interface DataInput，
     * 然后返回一个byte等于(byte)v
     * @param      v   the byte value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeByte(int v) throws IOException;

    /**
     * Writes two bytes to the output
     * stream to represent the value of the argument.
     * The byte values to be written, in the  order
     * shown, are:
     * <pre>{@code
     * (byte)(0xff & (v >> 8))
     * (byte)(0xff & v)
     * }</pre> <p>
     * The bytes written by this method may be
     * read by the <code>readShort</code> method
     * of interface <code>DataInput</code> , which
     * will then return a <code>short</code> equal
     * to <code>(short)v</code>.
     * 将两个字节写入输出流以表示参数的值。按所示顺序写入的字节值是：
     * (byte)(0xff & (v >> 8)) (byte)(0xff & v)
     * 字节由该方法写入的内容可以被接口DataInput的readShort方法读取，
     * 然后返回一个short等于(short)v
     * @param      v   the <code>short</code> value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeShort(int v) throws IOException;

    /**
     * Writes a <code>char</code> value, which
     * is comprised of two bytes, to the
     * output stream.
     * The byte values to be written, in the  order
     * shown, are:
     * <pre>{@code
     * (byte)(0xff & (v >> 8))
     * (byte)(0xff & v)
     * }</pre><p>
     * The bytes written by this method may be
     * read by the <code>readChar</code> method
     * of interface <code>DataInput</code> , which
     * will then return a <code>char</code> equal
     * to <code>(char)v</code>.
     * 将包含两个字节的char值写入输出流。按所示顺序写入的字节值是：
     * (byte)(0xff & (v >> 8)) (byte)(0xff & v)字节由该方法写入的内容可以被接口
     * DataInput的readChar方法读取，然后返回一个char等于(char)v
     * @param      v   the <code>char</code> value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeChar(int v) throws IOException;

    /**
     * Writes an <code>int</code> value, which is
     * comprised of four bytes, to the output stream.
     * The byte values to be written, in the  order
     * shown, are:
     * <pre>{@code
     * (byte)(0xff & (v >> 24))
     * (byte)(0xff & (v >> 16))
     * (byte)(0xff & (v >>  8))
     * (byte)(0xff & v)
     * }</pre><p>
     * The bytes written by this method may be read
     * by the <code>readInt</code> method of interface
     * <code>DataInput</code> , which will then
     * return an <code>int</code> equal to <code>v</code>.
     * 将int值（由四个字节组成）写入输出流。按所示顺序写入的字节值是：
     * (byte)(0xff & (v >> 24)) (byte)(0xff & (v >> 16)) (byte)( 0xff & (v >> 8)) (byte)(0xff & v)
     * 该方法写入的字节可以通过接口DataInput的readInt方法读取，然后将返回一个int等于v
     * @param      v   the <code>int</code> value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeInt(int v) throws IOException;

    /**
     * Writes a <code>long</code> value, which is
     * comprised of eight bytes, to the output stream.
     * The byte values to be written, in the  order
     * shown, are:
     * <pre>{@code
     * (byte)(0xff & (v >> 56))
     * (byte)(0xff & (v >> 48))
     * (byte)(0xff & (v >> 40))
     * (byte)(0xff & (v >> 32))
     * (byte)(0xff & (v >> 24))
     * (byte)(0xff & (v >> 16))
     * (byte)(0xff & (v >>  8))
     * (byte)(0xff & v)
     * }</pre><p>
     * The bytes written by this method may be
     * read by the <code>readLong</code> method
     * of interface <code>DataInput</code> , which
     * will then return a <code>long</code> equal
     * to <code>v</code>.
     * 将一个long值（由八个字节组成）写入输出流。按所示顺序写入的字节值是：
     * (byte)(0xff & (v >> 56)) (byte)(0xff & (v >> 48))
     * (byte)( 0xff & (v >> 40)) (byte)(0xff & (v >> 32))
     * (byte)(0xff & (v >> 24)) (byte)(0xff & (v >> 16))
     * (byte )(0xff & (v >> 8)) (byte)(0xff & v) }
     * 该方法写入的字节可以通过接口的readLong方法读取DataInput ，
     * 然后将返回一个long等于v
     * @param      v   the <code>long</code> value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeLong(long v) throws IOException;

    /**
     * Writes a <code>float</code> value,
     * which is comprised of four bytes, to the output stream.
     * It does this as if it first converts this
     * <code>float</code> value to an <code>int</code>
     * in exactly the manner of the <code>Float.floatToIntBits</code>
     * method  and then writes the <code>int</code>
     * value in exactly the manner of the  <code>writeInt</code>
     * method.  The bytes written by this method
     * may be read by the <code>readFloat</code>
     * method of interface <code>DataInput</code>,
     * which will then return a <code>float</code>
     * equal to <code>v</code>.
     * 将float值（由四个字节组成）写入输出流。它这样做就好像它首先以Float.floatToIntBits
     * 方法的方式首先将此float值转换为int，然后写入int值与writeInt方法的方式完全相同。
     * 该方法写入的字节可以被接口DataInput的readFloat方法读取，然后返回一个float等于v
     * @param      v   the <code>float</code> value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeFloat(float v) throws IOException;

    /**
     * Writes a <code>double</code> value,
     * which is comprised of eight bytes, to the output stream.
     * It does this as if it first converts this
     * <code>double</code> value to a <code>long</code>
     * in exactly the manner of the <code>Double.doubleToLongBits</code>
     * method  and then writes the <code>long</code>
     * value in exactly the manner of the  <code>writeLong</code>
     * method. The bytes written by this method
     * may be read by the <code>readDouble</code>
     * method of interface <code>DataInput</code>,
     * which will then return a <code>double</code>
     * equal to <code>v</code>.
     * 将一个double值（由八个字节组成）写入输出流。它这样做就好像它首先以Double.doubleToLongBits
     * 方法的方式首先将此double值转换为long然后写入long值与writeLong方法的方式完全相同。
     * 该方法写入的字节可以被接口DataInput的readDouble方法读取，然后返回一个double等于v
     * @param      v   the <code>double</code> value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeDouble(double v) throws IOException;

    /**
     * Writes a string to the output stream.
     * For every character in the string
     * <code>s</code>,  taken in order, one byte
     * is written to the output stream.  If
     * <code>s</code> is <code>null</code>, a <code>NullPointerException</code>
     * is thrown.<p>  If <code>s.length</code>
     * is zero, then no bytes are written. Otherwise,
     * the character <code>s[0]</code> is written
     * first, then <code>s[1]</code>, and so on;
     * the last character written is <code>s[s.length-1]</code>.
     * For each character, one byte is written,
     * the low-order byte, in exactly the manner
     * of the <code>writeByte</code> method . The
     * high-order eight bits of each character
     * in the string are ignored.
     * 将字符串写入输出流。对于字符串s中的每个字符，按顺序，一个字节被写入输出流。
     * 如果s是null，则抛出NullPointerException。如果s.length为零，
     * 则不写入任何字节。否则，先写字符s[0]，然后是s[1]，依此类推；最后写入的字符是
     * s[s.length-1]。对于每个字符，以与 writeByte方法完全相同的方式写入一个字节，
     * 即低位字节。字符串中每个字符的高八位被忽略
     * @param      s   the string of bytes to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeBytes(String s) throws IOException;

    /**
     * Writes every character in the string <code>s</code>,
     * to the output stream, in order,
     * two bytes per character. If <code>s</code>
     * is <code>null</code>, a <code>NullPointerException</code>
     * is thrown.  If <code>s.length</code>
     * is zero, then no characters are written.
     * Otherwise, the character <code>s[0]</code>
     * is written first, then <code>s[1]</code>,
     * and so on; the last character written is
     * <code>s[s.length-1]</code>. For each character,
     * two bytes are actually written, high-order
     * byte first, in exactly the manner of the
     * <code>writeChar</code> method.
     * 将字符串s中的每个字符按顺序写入输出流，每个字符两个字节。如果s是null，
     * 则抛出NullPointerException。如果s.length为零，则不写入任何字符。
     * 否则，先写字符s[0]，然后是s[1]，依此类推；最后写入的字符是s[s.length-1]。
     * 对于每个字符，实际写入两个字节，高位字节在前，与writeChar方法的方式完全相同
     * @param      s   the string value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeChars(String s) throws IOException;

    /**
     * Writes two bytes of length information
     * to the output stream, followed
     * by the
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * representation
     * of  every character in the string <code>s</code>.
     * If <code>s</code> is <code>null</code>,
     * a <code>NullPointerException</code> is thrown.
     * Each character in the string <code>s</code>
     * is converted to a group of one, two, or
     * three bytes, depending on the value of the
     * character.<p>
     * If a character <code>c</code>
     * is in the range <code>&#92;u0001</code> through
     * <code>&#92;u007f</code>, it is represented
     * by one byte:
     * <pre>(byte)c </pre>  <p>
     * If a character <code>c</code> is <code>&#92;u0000</code>
     * or is in the range <code>&#92;u0080</code>
     * through <code>&#92;u07ff</code>, then it is
     * represented by two bytes, to be written
     * in the order shown: <pre>{@code
     * (byte)(0xc0 | (0x1f & (c >> 6)))
     * (byte)(0x80 | (0x3f & c))
     * }</pre> <p> If a character
     * <code>c</code> is in the range <code>&#92;u0800</code>
     * through <code>uffff</code>, then it is
     * represented by three bytes, to be written
     * in the order shown: <pre>{@code
     * (byte)(0xe0 | (0x0f & (c >> 12)))
     * (byte)(0x80 | (0x3f & (c >>  6)))
     * (byte)(0x80 | (0x3f & c))
     * }</pre>  <p> First,
     * the total number of bytes needed to represent
     * all the characters of <code>s</code> is
     * calculated. If this number is larger than
     * <code>65535</code>, then a <code>UTFDataFormatException</code>
     * is thrown. Otherwise, this length is written
     * to the output stream in exactly the manner
     * of the <code>writeShort</code> method;
     * after this, the one-, two-, or three-byte
     * representation of each character in the
     * string <code>s</code> is written.<p>  The
     * bytes written by this method may be read
     * by the <code>readUTF</code> method of interface
     * <code>DataInput</code> , which will then
     * return a <code>String</code> equal to <code>s</code>.
     * 将两个字节的长度信息写入输出流，然后是字符串s中每个字符的
     * <a href="DataInput.htmlmodified-utf-8">修改后的 UTF-8表示。
     * 如果s是null，则抛出NullPointerException。字符串s中的每个字符都被转换为一组一个、
     * 两个或三个字节，具体取决于字符的值。如果字符c在范围&92;u0001到&92;u007f，
     * 用一个字节表示：(byte)c 如果一个字符c是&92;u0000或者在&92;u0080到&92;u07ff范围内，
     * 那么用两个字节表示，要按顺序写显示：(byte)(0xc0 | (0x1f & (c >> 6))) (byte)(0x80 | (0x3f & c))
     * 如果一个字符c在&92;u0800到uffff的范围内，则用三个字节表示，按顺序写成：
     * (byte )(0xe0 | (0x0f & (c >> 12))) (byte)(0x80 | (0x3f & (c >> 6))) (byte)(0x80 | (0x3f & c))
     * 首先计算表示s的所有字符所需的总字节数。如果此数字大于 65535，则抛出UTFDataFormatException。
     * 否则，这个长度将完全按照writeShort方法的方式写入输出流；在此之后，写入字符串s中每个字符的一、二或三字节表示。
     * 此方法写入的字节可由readUTF读取接口DataInput的方法，然后将返回一个String等于s。
     * @param      s   the string value to be written.
     * @throws     IOException  if an I/O error occurs.
     */
    void writeUTF(String s) throws IOException;
}
