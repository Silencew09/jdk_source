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
 * The {@code DataInput} interface provides
 * for reading bytes from a binary stream and
 * reconstructing from them data in any of
 * the Java primitive types. There is also
 * a
 * facility for reconstructing a {@code String}
 * from data in
 * <a href="#modified-utf-8">modified UTF-8</a>
 * format.
 * 1.DataInput接口提供了从二进制流中读取字节并从中重建任何 Java 原始类型数据的功能。
 * 还有一个工具可以从 <a href="modified-utf-8">修改后的 UTF-8格式的数据中重建String
 * <p>
 * It is generally true of all the reading
 * routines in this interface that if end of
 * file is reached before the desired number
 * of bytes has been read, an {@code EOFException}
 * (which is a kind of {@code IOException})
 * is thrown. If any byte cannot be read for
 * any reason other than end of file, an {@code IOException}
 * other than {@code EOFException} is
 * thrown. In particular, an {@code IOException}
 * may be thrown if the input stream has been
 * closed.
 * 2.对于此接口中的所有读取例程，如果在读取所需字节数之前到达文件末尾，
 * 则通常会抛出EOFException（这是一种 IOException）
 * 如果由于文件结尾以外的任何原因无法读取任何字节，则抛出EOFException以外的IOException。
 * 特别是，如果输入流已关闭，则可能会抛出IOException。
 * <h3><a name="modified-utf-8">Modified UTF-8</a></h3>
 * <p>
 * Implementations of the DataInput and DataOutput interfaces represent
 * Unicode strings in a format that is a slight modification of UTF-8.
 * (For information regarding the standard UTF-8 format, see section
 * <i>3.9 Unicode Encoding Forms</i> of <i>The Unicode Standard, Version
 * 4.0</i>).
 * 3.DataInput 和 DataOutput 接口的实现以对 UTF-8 稍作修改的格式表示 Unicode 字符串。
 * （有关标准 UTF-8 格式的信息，请参阅The Unicode Standard, Version 4.0的
 * 3.9 Unicode Encoding Forms部分）
 * Note that in the following table, the most significant bit appears in the
 * far left-hand column.
 * 4.请注意，在下表中，最高有效位出现在最左侧的列中
 * <blockquote>
 *   <table border="1" cellspacing="0" cellpadding="8"
 *          summary="Bit values and bytes">
 *     <tr>
 *       <th colspan="9"><span style="font-weight:normal">
 *         All characters in the range {@code '\u005Cu0001'} to
 *         {@code '\u005Cu007F'} are represented by a single byte:</span></th>
 *     </tr>
 *     <tr>
 *       <td></td>
 *       <th colspan="8" id="bit_a">Bit Values</th>
 *     </tr>
 *     <tr>
 *       <th id="byte1_a">Byte 1</th>
 *       <td><center>0</center>
 *       <td colspan="7"><center>bits 6-0</center>
 *     </tr>
 *     <tr>
 *       <th colspan="9"><span style="font-weight:normal">
 *         The null character {@code '\u005Cu0000'} and characters
 *         in the range {@code '\u005Cu0080'} to {@code '\u005Cu07FF'} are
 *         represented by a pair of bytes:</span></th>
 *     </tr>
 *     <tr>
 *       <td></td>
 *       <th colspan="8" id="bit_b">Bit Values</th>
 *     </tr>
 *     <tr>
 *       <th id="byte1_b">Byte 1</th>
 *       <td><center>1</center>
 *       <td><center>1</center>
 *       <td><center>0</center>
 *       <td colspan="5"><center>bits 10-6</center>
 *     </tr>
 *     <tr>
 *       <th id="byte2_a">Byte 2</th>
 *       <td><center>1</center>
 *       <td><center>0</center>
 *       <td colspan="6"><center>bits 5-0</center>
 *     </tr>
 *     <tr>
 *       <th colspan="9"><span style="font-weight:normal">
 *         {@code char} values in the range {@code '\u005Cu0800'}
 *         to {@code '\u005CuFFFF'} are represented by three bytes:</span></th>
 *     </tr>
 *     <tr>
 *       <td></td>
 *       <th colspan="8"id="bit_c">Bit Values</th>
 *     </tr>
 *     <tr>
 *       <th id="byte1_c">Byte 1</th>
 *       <td><center>1</center>
 *       <td><center>1</center>
 *       <td><center>1</center>
 *       <td><center>0</center>
 *       <td colspan="4"><center>bits 15-12</center>
 *     </tr>
 *     <tr>
 *       <th id="byte2_b">Byte 2</th>
 *       <td><center>1</center>
 *       <td><center>0</center>
 *       <td colspan="6"><center>bits 11-6</center>
 *     </tr>
 *     <tr>
 *       <th id="byte3">Byte 3</th>
 *       <td><center>1</center>
 *       <td><center>0</center>
 *       <td colspan="6"><center>bits 5-0</center>
 *     </tr>
 *   </table>
 * </blockquote>
 * <p>
 * The differences between this format and the
 * standard UTF-8 format are the following:
 * <ul>
 * <li>The null byte {@code '\u005Cu0000'} is encoded in 2-byte format
 *     rather than 1-byte, so that the encoded strings never have
 *     embedded nulls.
 * <li>Only the 1-byte, 2-byte, and 3-byte formats are used.
 * <li><a href="../lang/Character.html#unicode">Supplementary characters</a>
 *     are represented in the form of surrogate pairs.
 * 5.这种格式与标准 UTF-8 格式的区别如下：空字节 {@code '\u005Cu0000'} 被编码为 2 字节格式而不是 1 字节，
 * 这样编码的字符串从来没有嵌入空值。仅使用 1 字节、2 字节和 3 字节格式。
 * <a href="..langCharacter.htmlunicode">补充字符以代理对的形式表示
 * </ul>
 * @author  Frank Yellin
 * @see     java.io.DataInputStream
 * @see     java.io.DataOutput
 * @since   JDK1.0
 */
public
interface DataInput {
    /**
     * Reads some bytes from an input
     * stream and stores them into the buffer
     * array {@code b}. The number of bytes
     * read is equal
     * to the length of {@code b}.
     * 1.从输入流中读取一些字节并将它们存储到缓冲区数组b中。读取的字节数等于b的长度。
     * <p>
     * This method blocks until one of the
     * following conditions occurs:
     * <ul>
     * <li>{@code b.length}
     * bytes of input data are available, in which
     * case a normal return is made.
     *
     * <li>End of
     * file is detected, in which case an {@code EOFException}
     * is thrown.
     * 2.此方法会阻塞，直到出现以下情况之一：
     * b.length字节的输入数据可用，在这种情况下进行正常返回。
     * 检测到文件结尾，在这种情况下会抛出EOFException。
     * 发生 IO 错误，在这种情况下抛出 EOFException以外的IOException
     * <li>An I/O error occurs, in
     * which case an {@code IOException} other
     * than {@code EOFException} is thrown.
     * </ul>
     * <p>
     * 如果b为null，则抛出NullPointerException。如果b.length为零，则不读取任何字节。
     * 否则，读取的第一个字节存储到元素b[0]中，下一个存储到b[1]中，依此类推
     * If {@code b} is {@code null},
     * a {@code NullPointerException} is thrown.
     * If {@code b.length} is zero, then
     * no bytes are read. Otherwise, the first
     * byte read is stored into element {@code b[0]},
     * the next one into {@code b[1]}, and
     * so on.
     * If an exception is thrown from
     * this method, then it may be that some but
     * not all bytes of {@code b} have been
     * updated with data from the input stream.
     * 如果此方法抛出异常，则可能是b的部分而非全部字节已使用输入流中的数据进行更新
     * @param     b   the buffer into which the data is read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    void readFully(byte b[]) throws IOException;

    /**
     *
     * Reads {@code len}
     * bytes from
     * an input stream.
     * 1.从输入流中读取len字节
     * <p>
     * This method
     * blocks until one of the following conditions
     * occurs:
     * 2.此方法会阻塞，直到出现以下情况之一：
     * <ul>
     * <li>{@code len} bytes
     * of input data are available, in which case
     * a normal return is made.
     * 1)len字节的输入数据可用，在这种情况下进行正常返回
     * <li>End of file
     * is detected, in which case an {@code EOFException}
     * is thrown.
     * 2)检测到文件结尾，在这种情况下会抛出EOFException
     * <li>An I/O error occurs, in
     * which case an {@code IOException} other
     * than {@code EOFException} is thrown.
     * 3)发生 IO 错误，在这种情况下抛出EOFException以外的IOException
     * </ul>
     * <p>
     * If {@code b} is {@code null},
     * a {@code NullPointerException} is thrown.
     * If {@code off} is negative, or {@code len}
     * is negative, or {@code off+len} is
     * greater than the length of the array {@code b},
     * then an {@code IndexOutOfBoundsException}
     * is thrown.
     * 如果 b为null，则抛出NullPointerException。如果off为负数，或 len为负数，或off+len大于数组
     * b的长度，则抛出IndexOutOfBoundsException。
     * If {@code len} is zero,
     * then no bytes are read. Otherwise, the first
     * byte read is stored into element {@code b[off]},
     * the next one into {@code b[off+1]},
     * and so on. The number of bytes read is,
     * at most, equal to {@code len}.
     * 如果len为零，则不读取任何字节。否则，读取的第一个字节存储到元素b[off]中，
     * 下一个存储到b[off+1]中，依此类推。读取的字节数最多等于len
     * @param     b   the buffer into which the data is read.
     * @param off  an int specifying the offset into the data.
     * @param len  an int specifying the number of bytes to read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    void readFully(byte b[], int off, int len) throws IOException;

    /**
     * Makes an attempt to skip over
     * {@code n} bytes
     * of data from the input
     * stream, discarding the skipped bytes. However,
     * it may skip
     * over some smaller number of
     * bytes, possibly zero. This may result from
     * any of a
     * number of conditions; reaching
     * end of file before {@code n} bytes
     * have been skipped is
     * only one possibility.
     * This method never throws an {@code EOFException}.
     * The actual
     * number of bytes skipped is returned.
     * 尝试从输入流中跳过n字节的数据，丢弃跳过的字节。但是，它可能会跳过一些较小的字节数，
     * 可能为零。这可能由多种情况中的任何一种引起；在跳过n个字节之前到达文件末尾只是一种可能性。
     * 此方法从不抛出 EOFException。返回实际跳过的字节数。
     * @param      n   the number of bytes to be skipped.
     * @return     the number of bytes actually skipped.
     * @exception  IOException   if an I/O error occurs.
     */
    int skipBytes(int n) throws IOException;

    /**
     * Reads one input byte and returns
     * {@code true} if that byte is nonzero,
     * {@code false} if that byte is zero.
     * This method is suitable for reading
     * the byte written by the {@code writeBoolean}
     * method of interface {@code DataOutput}.
     *读取一个输入字节，如果该字节非零则返回true，
     * 如果该字节为零则返回false。该方法适用于读取接口DataOutput的writeBoolean方法写入的字节。
     * @return     the {@code boolean} value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    boolean readBoolean() throws IOException;

    /**
     * Reads and returns one input byte.
     * The byte is treated as a signed value in
     * the range {@code -128} through {@code 127},
     * inclusive.
     * This method is suitable for
     * reading the byte written by the {@code writeByte}
     * method of interface {@code DataOutput}.
     * 读取并返回一个输入字节。该字节被视为-128到127（含）范围内的有符号值。
     * 该方法适用于读取接口DataOutput的writeByte方法写入的字节
     * @return     the 8-bit value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    byte readByte() throws IOException;

    /**
     * Reads one input byte, zero-extends
     * it to type {@code int}, and returns
     * the result, which is therefore in the range
     * {@code 0}
     * through {@code 255}.
     * This method is suitable for reading
     * the byte written by the {@code writeByte}
     * method of interface {@code DataOutput}
     * if the argument to {@code writeByte}
     * was intended to be a value in the range
     * {@code 0} through {@code 255}.
     * 读取一个输入字节，将其零扩展为 int类型，并返回结果，
     * 因此该结果在0到255的范围内。如果writeByte的参数是0到255
     * @return     the unsigned 8-bit value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    int readUnsignedByte() throws IOException;

    /**
     * Reads two input bytes and returns
     * a {@code short} value. Let {@code a}
     * be the first byte read and {@code b}
     * be the second byte. The value
     * returned
     * is:
     * <pre>{@code (short)((a << 8) | (b & 0xff))
     * }</pre>
     * This method
     * is suitable for reading the bytes written
     * by the {@code writeShort} method of
     * interface {@code DataOutput}.
     * 读取两个输入字节并返回一个short值。让a是读取的第一个字节，b是第二个字节。
     * 返回值是： (short)((a << 8) | (b & 0xff)) }该方法适用于读取writeShort
     * 方法写入的字节接口DataOutput。
     * @return     the 16-bit value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    short readShort() throws IOException;

    /**
     * Reads two input bytes and returns
     * an {@code int} value in the range {@code 0}
     * through {@code 65535}. Let {@code a}
     * be the first byte read and
     * {@code b}
     * be the second byte. The value returned is:
     * <pre>{@code (((a & 0xff) << 8) | (b & 0xff))
     * }</pre>
     * This method is suitable for reading the bytes
     * written by the {@code writeShort} method
     * of interface {@code DataOutput}  if
     * the argument to {@code writeShort}
     * was intended to be a value in the range
     * {@code 0} through {@code 65535}.
     * 读取两个输入字节并返回0到65535范围内的int值。让a是读取的第一个字节，b是第二个字节。
     * 返回值是： (((a & 0xff) << 8) | (b & 0xff)) 该方法适用于读取writeShort
     * 方法写入的字节接口DataOutput如果writeShort的参数是0到65535范围内的值
     * @return     the unsigned 16-bit value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    int readUnsignedShort() throws IOException;

    /**
     * Reads two input bytes and returns a {@code char} value.
     * Let {@code a}
     * be the first byte read and {@code b}
     * be the second byte. The value
     * returned is:
     * <pre>{@code (char)((a << 8) | (b & 0xff))
     * }</pre>
     * This method
     * is suitable for reading bytes written by
     * the {@code writeChar} method of interface
     * {@code DataOutput}.
     * 读取两个输入字节并返回一个char值。让a是读取的第一个字节，b是第二个字节。
     * 返回值为：(char)((a << 8) | (b & 0xff)) }该方法适用于读取接口的writeChar
     * 方法写入的字节数据输出。
     * @return     the {@code char} value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    char readChar() throws IOException;

    /**
     * Reads four input bytes and returns an
     * {@code int} value. Let {@code a-d}
     * be the first through fourth bytes read. The value returned is:
     * <pre>{@code
     * (((a & 0xff) << 24) | ((b & 0xff) << 16) |
     *  ((c & 0xff) <<  8) | (d & 0xff))
     * }</pre>
     * This method is suitable
     * for reading bytes written by the {@code writeInt}
     * method of interface {@code DataOutput}.
     * 读取四个输入字节并返回一个int值。让a-d是读取的第一个到第四个字节。
     * 返回的值是：(((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff) )
     * 该方法适用于读取接口DataOutput的writeInt方法写入的字节
     * @return     the {@code int} value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    int readInt() throws IOException;

    /**
     * Reads eight input bytes and returns
     * a {@code long} value. Let {@code a-h}
     * be the first through eighth bytes read.
     * The value returned is:
     * 读取八个输入字节并返回一个long值。让a-h是读取的第一个到第八个字节。返回的值为：
     * <pre>{@code
     * (((long)(a & 0xff) << 56) |
     *  ((long)(b & 0xff) << 48) |
     *  ((long)(c & 0xff) << 40) |
     *  ((long)(d & 0xff) << 32) |
     *  ((long)(e & 0xff) << 24) |
     *  ((long)(f & 0xff) << 16) |
     *  ((long)(g & 0xff) <<  8) |
     *  ((long)(h & 0xff)))
     * }</pre>
     * <p>
     * This method is suitable
     * for reading bytes written by the {@code writeLong}
     * method of interface {@code DataOutput}.
     * 该方法适用于读取接口DataOutput的writeLong方法写入的字节
     * @return     the {@code long} value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    long readLong() throws IOException;

    /**
     * Reads four input bytes and returns
     * a {@code float} value. It does this
     * by first constructing an {@code int}
     * value in exactly the manner
     * of the {@code readInt}
     * method, then converting this {@code int}
     * value to a {@code float} in
     * exactly the manner of the method {@code Float.intBitsToFloat}.
     * This method is suitable for reading
     * bytes written by the {@code writeFloat}
     * method of interface {@code DataOutput}.
     * 读取四个输入字节并返回一个float值。它首先以readInt方法的方式构造一个int值，
     * 然后以该方法int的方式将这个int值转换为float,Float.intBitsToFloat。
     * 该方法适用于读取接口DataOutput的writeFloat方法写入的字节
     * @return     the {@code float} value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    float readFloat() throws IOException;

    /**
     * Reads eight input bytes and returns
     * a {@code double} value. It does this
     * by first constructing a {@code long}
     * value in exactly the manner
     * of the {@code readLong}
     * method, then converting this {@code long}
     * value to a {@code double} in exactly
     * the manner of the method {@code Double.longBitsToDouble}.
     * This method is suitable for reading
     * bytes written by the {@code writeDouble}
     * method of interface {@code DataOutput}.
     * 读取八个输入字节并返回一个double值。它首先以readLong方法的方式构造一个long值，
     * 然后以readLong方法的方式将这个long值转换为double值。Double.longBitsToDouble。
     * 该方法适用于读取接口DataOutput的writeDouble方法写入的字节
     * @return     the {@code double} value read.
     * @exception  EOFException  if this stream reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    double readDouble() throws IOException;

    /**
     * Reads the next line of text from the input stream.
     * It reads successive bytes, converting
     * each byte separately into a character,
     * until it encounters a line terminator or
     * end of
     * file; the characters read are then
     * returned as a {@code String}. Note
     * that because this
     * method processes bytes,
     * it does not support input of the full Unicode
     * character set.
     * 1.从输入流中读取下一行文本。它读取连续的字节，将每个字节分别转换为一个字符，
     * 直到遇到行终止符或文件结尾；然后将读取的字符作为String返回。请注意，由于此方法处理字节，
     * 因此不支持完整 Unicode 字符集的输入
     * <p>
     * If end of file is encountered
     * before even one byte can be read, then {@code null}
     * is returned. Otherwise, each byte that is
     * read is converted to type {@code char}
     * by zero-extension. If the character {@code '\n'}
     * is encountered, it is discarded and reading
     * ceases. If the character {@code '\r'}
     * is encountered, it is discarded and, if
     * the following byte converts &#32;to the
     * character {@code '\n'}, then that is
     * discarded also; reading then ceases. If
     * end of file is encountered before either
     * of the characters {@code '\n'} and
     * {@code '\r'} is encountered, reading
     * ceases. Once reading has ceased, a {@code String}
     * is returned that contains all the characters
     * read and not discarded, taken in order.
     * Note that every character in this string
     * will have a value less than {@code \u005Cu0100},
     * that is, {@code (char)256}.
     * 如果在读取一个字节之前遇到文件结尾，则返回null。否则，读取的每个字节都将通过零扩展转换为char类型。
     * 如果遇到字符'\n'，则将其丢弃并停止读取。如果遇到字符'\r'，则将其丢弃，如果接下来的字节将 &32;
     * 转换为字符 '\n'，则也将其丢弃；阅读然后停止。如果在字符'\n'和'\r'之前遇到文件结尾，
     * 则停止读取。读取停止后，将返回一个String，其中包含按顺序读取且未丢弃的所有字符。
     * 请注意，此字符串中的每个字符都将具有小于\u005Cu0100的值，即(char)256
     * @return the next line of text from the input stream,
     *         or {@code null} if the end of file is
     *         encountered before a byte can be read.
     * @exception  IOException  if an I/O error occurs.
     */
    String readLine() throws IOException;

    /**
     * Reads in a string that has been encoded using a
     * <a href="#modified-utf-8">modified UTF-8</a>
     * format.
     * 1.读取已使用 <a href="modified-utf-8">修改的 UTF-8格式编码的字符串
     * The general contract of {@code readUTF}
     * is that it reads a representation of a Unicode
     * character string encoded in modified
     * UTF-8 format; this string of characters
     * is then returned as a {@code String}.
     * 2.readUTF的一般约定是它读取以修改后的 UTF-8 格式编码的 Unicode 字符串的表示；
     * 这个字符串然后作为String返回。
     * <p>
     * First, two bytes are read and used to
     * construct an unsigned 16-bit integer in
     * exactly the manner of the {@code readUnsignedShort}
     * method . This integer value is called the
     * <i>UTF length</i> and specifies the number
     * of additional bytes to be read. These bytes
     * are then converted to characters by considering
     * them in groups. The length of each group
     * is computed from the value of the first
     * byte of the group. The byte following a
     * group, if any, is the first byte of the
     * next group.
     * 3.首先，完全按照readUnsignedShort方法的方式读取两个字节并用于构造一个无符号的 16 位整数。
     * 该整数值称为UTF长度，并指定要读取的附加字节数。然后通过将它们分组考虑将这些字节转换为字符。
     * 每个组的长度是根据组的第一个字节的值计算的。组后面的字节（如果有）是下一组的第一个字节
     * <p>
     * If the first byte of a group
     * matches the bit pattern {@code 0xxxxxxx}
     * (where {@code x} means "may be {@code 0}
     * or {@code 1}"), then the group consists
     * of just that byte. The byte is zero-extended
     * to form a character.
     * 4.如果组的第一个字节与位模式0xxxxxxx匹配（其中x表示“可能是0或1”），
     * 则该组仅由该字节组成。该字节被零扩展以形成一个字符
     * <p>
     * If the first byte
     * of a group matches the bit pattern {@code 110xxxxx},
     * then the group consists of that byte {@code a}
     * and a second byte {@code b}. If there
     * is no byte {@code b} (because byte
     * {@code a} was the last of the bytes
     * to be read), or if byte {@code b} does
     * not match the bit pattern {@code 10xxxxxx},
     * then a {@code UTFDataFormatException}
     * is thrown. Otherwise, the group is converted
     * to the character:
     * <pre>{@code (char)(((a & 0x1F) << 6) | (b & 0x3F))
     * }</pre>
     * 5.如果组的第一个字节与位模式110xxxxx匹配，则该组由该字节a和第二个字节b组成。
     * 如果没有字节b（因为字节a是要读取的最后一个字节），或者如果字节b与位模式10xxxxxx不匹配，
     * 则抛出一个UTFDataFormatException。否则，组将转换为字符：(char)(((a & 0x1F) << 6) | (b & 0x3F))
     * If the first byte of a group
     * matches the bit pattern {@code 1110xxxx},
     * then the group consists of that byte {@code a}
     * and two more bytes {@code b} and {@code c}.
     * If there is no byte {@code c} (because
     * byte {@code a} was one of the last
     * two of the bytes to be read), or either
     * byte {@code b} or byte {@code c}
     * does not match the bit pattern {@code 10xxxxxx},
     * then a {@code UTFDataFormatException}
     * is thrown. Otherwise, the group is converted
     * to the character:
     * <pre>{@code
     * (char)(((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F))
     * }</pre>
     * If the first byte of a group matches the
     * pattern {@code 1111xxxx} or the pattern
     * {@code 10xxxxxx}, then a {@code UTFDataFormatException}
     * is thrown.
     * 6.如果组的第一个字节与位模式1110xxxx匹配，则该组由该字节a和另外两个字节b和c组成。
     * 如果没有字节c（因为字节a是要读取的最后两个字节之一），或者字节b或字节c不匹配位模式
     * 10xxxxxx，然后抛出UTFDataFormatException。
     * 否则，组将转换为字符：(char)(((a & 0x0F) << 12) | ((b & 0x3F) << 6) | (c & 0x3F)) }
     * 如果组的第一个字节与模式1111xxxx或模式10xxxxxx匹配，则抛出UTFDataFormatException。
     * <p>
     * If end of file is encountered
     * at any time during this entire process,
     * then an {@code EOFException} is thrown.
     * 7.如果在整个过程中的任何时候遇到文件结尾，则抛出EOFException。
     * <p>
     * After every group has been converted to
     * a character by this process, the characters
     * are gathered, in the same order in which
     * their corresponding groups were read from
     * the input stream, to form a {@code String},
     * which is returned.
     * 8.通过此过程将每个组转换为字符后，按照从输入流中读取相应组的相同顺序，将字符收集起来，形成一个String，然后返回
     * <p>
     * The {@code writeUTF}
     * method of interface {@code DataOutput}
     * may be used to write data that is suitable
     * for reading by this method.
     * 9.DataOutput接口的writeUTF方法可用于写入适合该方法读取的数据
     * @return     a Unicode string.
     * @exception  EOFException            if this stream reaches the end
     *               before reading all the bytes.
     * @exception  IOException             if an I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent a
     *               valid modified UTF-8 encoding of a string.
     */
    String readUTF() throws IOException;
}
