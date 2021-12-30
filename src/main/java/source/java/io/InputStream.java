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

package java.io;

/**
 * This abstract class is the superclass of all classes representing
 * an input stream of bytes.
 * 1.这个抽象类是表示字节输入流的所有类的超类
 * <p> Applications that need to define a subclass of <code>InputStream</code>
 * must always provide a method that returns the next byte of input.
 *2.需要定义InputStream子类的应用程序
 * 必须总是提供一个方法来返回输入的下一个字节。
 * @author  Arthur van Hoff
 * @see     java.io.BufferedInputStream
 * @see     java.io.ByteArrayInputStream
 * @see     java.io.DataInputStream
 * @see     java.io.FilterInputStream
 * @see     java.io.InputStream#read()
 * @see     java.io.OutputStream
 * @see     java.io.PushbackInputStream
 * @since   JDK1.0
 */
public abstract class InputStream implements Closeable {

    // MAX_SKIP_BUFFER_SIZE is used to determine the maximum buffer size to
    // use when skipping.
    //MAX_SKIP_BUFFER_SIZE用于确定跳过时
    //使用的最大缓冲区大小。
    private static final int MAX_SKIP_BUFFER_SIZE = 2048;

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     * 1.从输入流中读取下一个字节的数据。 值字节以0到255范围内的int形式返回。
     * 如果由于已到达流末尾而没有可用字节，则返回值-1 。 此方法会阻塞，直到输入数据可用、检测到流结束或抛出异常为止。
     * 子类必须提供此方法的实现
     * <p> A subclass must provide an implementation of this method.
     * 2. 子类必须提供此方法的实现
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public abstract int read() throws IOException;

    /**
     * Reads some number of bytes from the input stream and stores them into
     * the buffer array <code>b</code>. The number of bytes actually read is
     * returned as an integer.  This method blocks until input data is
     * available, end of file is detected, or an exception is thrown.
     *
     * <p> If the length of <code>b</code> is zero, then no bytes are read and
     * <code>0</code> is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at the
     * end of the file, the value <code>-1</code> is returned; otherwise, at
     * least one byte is read and stored into <code>b</code>.
     *
     * <p> The first byte read is stored into element <code>b[0]</code>, the
     * next one into <code>b[1]</code>, and so on. The number of bytes read is,
     * at most, equal to the length of <code>b</code>. Let <i>k</i> be the
     * number of bytes actually read; these bytes will be stored in elements
     * <code>b[0]</code> through <code>b[</code><i>k</i><code>-1]</code>,
     * leaving elements <code>b[</code><i>k</i><code>]</code> through
     * <code>b[b.length-1]</code> unaffected.
     *
     * <p> The <code>read(b)</code> method for class <code>InputStream</code>
     * has the same effect as: <pre><code> read(b, 0, b.length) </code></pre>
     * 从输入流中读取一定数量的字节并将它们存储到缓冲区数组b 。
     * 实际读取的字节数作为整数返回。 此方法会阻塞，直到输入数据可用、检测到文件结尾或抛出异常。
     * 如果b的长度为零，则不读取字节并返回0 ； 否则，将尝试读取至少一个字节。
     * 如果由于流位于文件末尾而没有可用字节，则返回值-1 ； 否则，至少读取一个字节并将其存储到b 。
     * 读取的第一个字节存储到元素b[0] ，下一个存储到b[1] ，依此类推。
     * 读取的字节数最多等于b的长度。 令k为实际读取的字节数； 这些字节将存储在元素b[0]到b[ k -1] ，
     * 而元素b[ k ]到b[b.length-1]不受影响。
     * 类InputStream的read(b)方法具有与以下相同的效果：
     *  read(b, 0, b.length)
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  If the first byte cannot be read for any reason
     * other than the end of the file, if the input stream has been closed, or
     * if some other I/O error occurs.
     * @exception  NullPointerException  if <code>b</code> is <code>null</code>.
     * @see        java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads up to <code>len</code> bytes of data from the input stream into
     * an array of bytes.  An attempt is made to read as many as
     * <code>len</code> bytes, but a smaller number may be read.
     * The number of bytes actually read is returned as an integer.
     *
     * <p> This method blocks until input data is available, end of file is
     * detected, or an exception is thrown.
     *
     * <p> If <code>len</code> is zero, then no bytes are read and
     * <code>0</code> is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at end of
     * file, the value <code>-1</code> is returned; otherwise, at least one
     * byte is read and stored into <code>b</code>.
     *
     * <p> The first byte read is stored into element <code>b[off]</code>, the
     * next one into <code>b[off+1]</code>, and so on. The number of bytes read
     * is, at most, equal to <code>len</code>. Let <i>k</i> be the number of
     * bytes actually read; these bytes will be stored in elements
     * <code>b[off]</code> through <code>b[off+</code><i>k</i><code>-1]</code>,
     * leaving elements <code>b[off+</code><i>k</i><code>]</code> through
     * <code>b[off+len-1]</code> unaffected.
     *
     * <p> In every case, elements <code>b[0]</code> through
     * <code>b[off]</code> and elements <code>b[off+len]</code> through
     * <code>b[b.length-1]</code> are unaffected.
     *
     * <p> The <code>read(b,</code> <code>off,</code> <code>len)</code> method
     * for class <code>InputStream</code> simply calls the method
     * <code>read()</code> repeatedly. If the first such call results in an
     * <code>IOException</code>, that exception is returned from the call to
     * the <code>read(b,</code> <code>off,</code> <code>len)</code> method.  If
     * any subsequent call to <code>read()</code> results in a
     * <code>IOException</code>, the exception is caught and treated as if it
     * were end of file; the bytes read up to that point are stored into
     * <code>b</code> and the number of bytes read before the exception
     * occurred is returned. The default implementation of this method blocks
     * until the requested amount of input data <code>len</code> has been read,
     * end of file is detected, or an exception is thrown. Subclasses are encouraged
     * to provide a more efficient implementation of this method.
     * 从输入流中读取最多len个字节的数据到一个字节数组中。 尝试读取多达len个字节，但可能会读取较小的数字。
     * 实际读取的字节数作为整数返回。
     * 此方法会阻塞，直到输入数据可用、检测到文件结尾或抛出异常。
     * 如果len为零，则不读取字节并返回0 ； 否则，将尝试读取至少一个字节。
     * 如果由于流位于文件末尾而没有可用字节，则返回值-1 ； 否则，至少读取一个字节并将其存储到b 。
     * 读取的第一个字节存储到元素b[off] ，下一个存储到b[off+1] ，依此类推。
     * 读取的字节数最多等于len 。 令k为实际读取的字节数； 这些字节将存储在元素b[off]到b[off+ k -1] ，
     * 而元素b[off+ k ]到b[off+len-1]不受影响。
     * 在每种情况下，元素b[0]到b[off]和元素b[off+len]到b[b.length-1]不受影响。
     * InputStream类的read(b, off, len)方法只是重复调用read()方法。
     * 如果第一次这样的调用导致IOException ，则该异常将从对read(b, off, len)方法的调用返回。
     * 如果对read()任何后续调用导致IOException ，则会捕获该异常并将其视为文件结尾；
     * 到该点读取的字节存储到b并返回发生异常之前读取的字节数。
     * 此方法的默认实现会阻塞，直到读取了请求的输入数据量len 、检测到文件结尾或抛出异常。
     * 鼓励子类提供此方法的更有效实现
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in array <code>b</code>
     *                   at which the data is written.
     * @param      len   the maximum number of bytes to read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException If the first byte cannot be read for any reason
     * other than end of file, or if the input stream has been closed, or if
     * some other I/O error occurs.
     * @exception  NullPointerException If <code>b</code> is <code>null</code>.
     * @exception  IndexOutOfBoundsException If <code>off</code> is negative,
     * <code>len</code> is negative, or <code>len</code> is greater than
     * <code>b.length - off</code>
     * @see        java.io.InputStream#read()
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte)c;
            }
        } catch (IOException ee) {
        }
        return i;
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from this input
     * stream. The <code>skip</code> method may, for a variety of reasons, end
     * up skipping over some smaller number of bytes, possibly <code>0</code>.
     * This may result from any of a number of conditions; reaching end of file
     * before <code>n</code> bytes have been skipped is only one possibility.
     * The actual number of bytes skipped is returned. If {@code n} is
     * negative, the {@code skip} method for class {@code InputStream} always
     * returns 0, and no bytes are skipped. Subclasses may handle the negative
     * value differently.
     *
     * <p> The <code>skip</code> method of this class creates a
     * byte array and then repeatedly reads into it until <code>n</code> bytes
     * have been read or the end of the stream has been reached. Subclasses are
     * encouraged to provide a more efficient implementation of this method.
     * For instance, the implementation may depend on the ability to seek.
     * 1.跳过并丢弃此输入流中的n字节数据。
     * 由于各种原因， skip方法最终可能会跳过一些较小数量的字节，可能是0 。
     * 这可能由多种情况中的任何一种引起； 在跳过n个字节之前到达文件末尾只是一种可能性。
     * 返回实际跳过的字节数。 如果n为负，则InputStream类的skip方法始终返回 0，并且不会跳过任何字节。
     * 子类可能会以不同的方式处理负值。
     * 此类的skip方法创建一个字节数组，然后重复读入其中，直到已读取n个字节或已到达流的末尾。
     * 鼓励子类提供此方法的更有效实现。 例如，实现可能取决于搜索的能力
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if the stream does not support seek,
     *                          or if some other I/O error occurs.
     */
    public long skip(long n) throws IOException {

        long remaining = n;
        int nr;

        if (n <= 0) {
            return 0;
        }

        int size = (int)Math.min(MAX_SKIP_BUFFER_SIZE, remaining);
        byte[] skipBuffer = new byte[size];
        while (remaining > 0) {
            nr = read(skipBuffer, 0, (int)Math.min(size, remaining));
            if (nr < 0) {
                break;
            }
            remaining -= nr;
        }

        return n - remaining;
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or
     * skipped over) from this input stream without blocking by the next
     * invocation of a method for this input stream. The next invocation
     * might be the same thread or another thread.  A single read or skip of this
     * many bytes will not block, but may read or skip fewer bytes.
     *
     * <p> Note that while some implementations of {@code InputStream} will return
     * the total number of bytes in the stream, many will not.  It is
     * never correct to use the return value of this method to allocate
     * a buffer intended to hold all data in this stream.
     *
     * <p> A subclass' implementation of this method may choose to throw an
     * {@link IOException} if this input stream has been closed by
     * invoking the {@link #close()} method.
     *
     * <p> The {@code available} method for class {@code InputStream} always
     * returns {@code 0}.
     *
     * <p> This method should be overridden by subclasses.
     * 返回可以从此输入流读取（或跳过）的字节数的估计值，而不会因下一次调用此输入流的方法而阻塞。
     * 下一次调用可能是同一个线程或另一个线程。
     * 单次读取或跳过这么多字节不会阻塞，但可能读取或跳过更少的字节。
     * 请注意，虽然InputStream某些实现会返回流中的总字节数，但许多不会。
     * 使用此方法的返回值来分配用于保存此流中所有数据的缓冲区永远是不正确的。
     * 如果此输入流已通过调用close()方法关闭，则此方法的子类实现可能会选择抛出IOException 。
     * InputStream类的available方法始终返回0 。
     * 这个方法应该被子类覆盖
     * @return     an estimate of the number of bytes that can be read (or skipped
     *             over) from this input stream without blocking or {@code 0} when
     *             it reaches the end of the input stream.
     * @exception  IOException if an I/O error occurs.
     */
    public int available() throws IOException {
        return 0;
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     *
     * <p> The <code>close</code> method of <code>InputStream</code> does
     * nothing.
     * 关闭此输入流并释放与该流关联的所有系统资源。
     * InputStream的close方法什么也不做。
     * @exception  IOException  if an I/O error occurs.
     */
    public void close() throws IOException {}

    /**
     * Marks the current position in this input stream. A subsequent call to
     * the <code>reset</code> method repositions this stream at the last marked
     * position so that subsequent reads re-read the same bytes.
     *
     * <p> The <code>readlimit</code> arguments tells this input stream to
     * allow that many bytes to be read before the mark position gets
     * invalidated.
     *
     * <p> The general contract of <code>mark</code> is that, if the method
     * <code>markSupported</code> returns <code>true</code>, the stream somehow
     * remembers all the bytes read after the call to <code>mark</code> and
     * stands ready to supply those same bytes again if and whenever the method
     * <code>reset</code> is called.  However, the stream is not required to
     * remember any data at all if more than <code>readlimit</code> bytes are
     * read from the stream before <code>reset</code> is called.
     *
     * <p> Marking a closed stream should not have any effect on the stream.
     *
     * <p> The <code>mark</code> method of <code>InputStream</code> does
     * nothing.
     *标记此输入流中的当前位置。 对reset方法的后续调用将此流重新定位在最后标记的位置，
     * 以便后续读取重新读取相同的字节。
     * read.limit参数告诉此输入流允许在标记位置无效之前读取许多字节。
     * mark的一般约定是，如果方法markSupported返回true ，则流以某种方式记住调用mark后读取的所有字节，
     * 并随时准备在调用方法reset时再次提供相同的字节。
     * 但是，如果在调用reset之前从流中读取了超过read.limit个字节，则该流根本不需要记住任何数据。
     * 标记关闭的流不应对流产生任何影响。
     * InputStream的mark方法什么也不做。
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     java.io.InputStream#reset()
     */
    public synchronized void mark(int readlimit) {}

    /**
     * Repositions this stream to the position at the time the
     * <code>mark</code> method was last called on this input stream.
     *
     * <p> The general contract of <code>reset</code> is:
     *
     * <ul>
     * <li> If the method <code>markSupported</code> returns
     * <code>true</code>, then:
     *
     *     <ul><li> If the method <code>mark</code> has not been called since
     *     the stream was created, or the number of bytes read from the stream
     *     since <code>mark</code> was last called is larger than the argument
     *     to <code>mark</code> at that last call, then an
     *     <code>IOException</code> might be thrown.
     *
     *     <li> If such an <code>IOException</code> is not thrown, then the
     *     stream is reset to a state such that all the bytes read since the
     *     most recent call to <code>mark</code> (or since the start of the
     *     file, if <code>mark</code> has not been called) will be resupplied
     *     to subsequent callers of the <code>read</code> method, followed by
     *     any bytes that otherwise would have been the next input data as of
     *     the time of the call to <code>reset</code>. </ul>
     *
     * <li> If the method <code>markSupported</code> returns
     * <code>false</code>, then:
     *
     *     <ul><li> The call to <code>reset</code> may throw an
     *     <code>IOException</code>.
     *
     *     <li> If an <code>IOException</code> is not thrown, then the stream
     *     is reset to a fixed state that depends on the particular type of the
     *     input stream and how it was created. The bytes that will be supplied
     *     to subsequent callers of the <code>read</code> method depend on the
     *     particular type of the input stream. </ul></ul>
     *
     * <p>The method <code>reset</code> for class <code>InputStream</code>
     * does nothing except throw an <code>IOException</code>.
     * 将此流重新定位到上次在此输入流上调用mark方法时的位置。
     * reset的总合约为：
     * 如果方法markSupported返回true ，则：
     * 如果自创建流后尚未调用方法mark ，或者自上次调用mark以来从流中读取的字节数大于上次调用时mark的参数，
     * 则可能会抛出IOException 。
     * 如果没有抛出这样的IOException ，那么流将被重置为这样一种状态，即自最近一次调用mark
     * （或自文件开始，如果尚未调用mark ）以来读取的所有字节将重新提供给后续read方法的调用者，
     * 后跟在调用reset时本应是下一个输入数据的任何字节。
     * 如果方法markSupported返回false ，则：
     * 对reset的调用可能会抛出IOException 。
     * 如果未抛出IOException ，则该流将重置为固定状态，该状态取决于输入流的特定类型及其创建方式。
     * 将提供给read方法的后续调用者的字节取决于输入流的特定类型。
     * InputStream类的reset方法除了抛出一个IOException之外什么都不做。
     * @exception  IOException  if this stream has not been marked or if the
     *               mark has been invalidated.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.IOException
     */
    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    /**
     * Tests if this input stream supports the <code>mark</code> and
     * <code>reset</code> methods. Whether or not <code>mark</code> and
     * <code>reset</code> are supported is an invariant property of a
     * particular input stream instance. The <code>markSupported</code> method
     * of <code>InputStream</code> returns <code>false</code>.
     * 测试此输入流是否支持mark和reset方法。
     * 是否支持mark和reset是特定输入流实例的不变属性。
     * InputStream的markSupported方法返回false
     * @return  <code>true</code> if this stream instance supports the mark
     *          and reset methods; <code>false</code> otherwise.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     */
    public boolean markSupported() {
        return false;
    }

}
