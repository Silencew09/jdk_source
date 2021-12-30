/*
 * Copyright (c) 1994, 2010, Oracle and/or its affiliates. All rights reserved.
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
 * A <code>FilterInputStream</code> contains
 * some other input stream, which it uses as
 * its  basic source of data, possibly transforming
 * the data along the way or providing  additional
 * functionality. The class <code>FilterInputStream</code>
 * itself simply overrides all  methods of
 * <code>InputStream</code> with versions that
 * pass all requests to the contained  input
 * stream. Subclasses of <code>FilterInputStream</code>
 * may further override some of  these methods
 * and may also provide additional methods
 * and fields.
 * FilterInputStream包含一些其他输入流，它用作其基本数据源，可能沿途转换数据或提供附加功能。
 * 类 FilterInputStream本身简单地覆盖了InputStream的所有方法，并使用将所有请求传递到包含的输入流的版本。
 * FilterInputStream的子类可能会进一步覆盖这些方法中的一些，也可能提供额外的方法和字段。
 * @author  Jonathan Payne
 * @since   JDK1.0
 */
public
class FilterInputStream extends InputStream {
    /**
     * The input stream to be filtered.
     * 要过滤的输入流
     */
    protected volatile InputStream in;

    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     * 通过将参数in分配给字段this.in来创建一个FilterInputStream以便记住它以备后用。
     * @param   in   the underlying input stream, or <code>null</code> if
     *          this instance is to be created without an underlying stream.
     */
    protected FilterInputStream(InputStream in) {
        this.in = in;
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an <code>int</code> in the range
     * <code>0</code> to <code>255</code>. If no byte is available
     * because the end of the stream has been reached, the value
     * <code>-1</code> is returned. This method blocks until input data
     * is available, the end of the stream is detected, or an exception
     * is thrown.
     * 1.从此输入流中读取下一个数据字节。值字节作为int在 0到255范围内返回。
     * 如果由于已到达流的末尾而没有可用字节，则返回值 -1。此方法会阻塞，
     * 直到输入数据可用、检测到流结束或抛出异常为止。
     * <p>
     * This method
     * simply performs <code>in.read()</code> and returns the result.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public int read() throws IOException {
        return in.read();
    }

    /**
     * Reads up to <code>byte.length</code> bytes of data from this
     * input stream into an array of bytes. This method blocks until some
     * input is available.
     * 1.从此输入流中读取最多byte.length个字节的数据到一个字节数组中。此方法会阻塞，直到某些输入可用
     * <p>
     * This method simply performs the call
     * <code>read(b, 0, b.length)</code> and returns
     * the  result. It is important that it does
     * <i>not</i> do <code>in.read(b)</code> instead;
     * certain subclasses of  <code>FilterInputStream</code>
     * depend on the implementation strategy actually
     * used.
     * 2.该方法简单地执行调用read(b, 0, b.length)并返回结果。
     * 重要的是它not做in.read(b)代替；FilterInputStream的某些子类取决于实际使用的实现策略。
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#read(byte[], int, int)
     */
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream
     * into an array of bytes. If <code>len</code> is not zero, the method
     * blocks until some input is available; otherwise, no
     * bytes are read and <code>0</code> is returned.
     * 1.从此输入流中读取最多len个字节的数据到一个字节数组中。如果len不为零，
     * 则该方法会阻塞，直到某些输入可用；否则，不读取任何字节并返回0
     * <p>
     * This method simply performs <code>in.read(b, off, len)</code>
     * and returns the result.
     * 2.该方法简单地执行in.read(b, off, len)并返回结果
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in the destination array <code>b</code>
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  NullPointerException If <code>b</code> is <code>null</code>.
     * @exception  IndexOutOfBoundsException If <code>off</code> is negative,
     * <code>len</code> is negative, or <code>len</code> is greater than
     * <code>b.length - off</code>
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public int read(byte b[], int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from the
     * input stream. The <code>skip</code> method may, for a variety of
     * reasons, end up skipping over some smaller number of bytes,
     * possibly <code>0</code>. The actual number of bytes skipped is
     * returned.
     * 1.从输入流中跳过并丢弃n字节的数据。由于各种原因，skip方法最终可能会跳过一些较小的字节数，
     * 可能是0。返回实际跳过的字节数
     * <p>
     * This method simply performs <code>in.skip(n)</code>.
     * 2.这个方法只是简单地执行in.skip(n)。
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if the stream does not support seek,
     *                          or if some other I/O error occurs.
     */
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or
     * skipped over) from this input stream without blocking by the next
     * caller of a method for this input stream. The next caller might be
     * the same thread or another thread.  A single read or skip of this
     * many bytes will not block, but may read or skip fewer bytes.
     * 1.返回可以从此输入流读取（或跳过）而不会被此输入流的方法的下一个调用者阻塞的估计字节数。
     * 下一个调用者可能是同一个线程或另一个线程。单次读取或跳过这么多字节不会阻塞，但可能读取或跳过更少的字节
     * <p>
     * This method returns the result of {@link #in in}.available().
     * 2.此方法返回in.available() 的结果。
     * @return     an estimate of the number of bytes that can be read (or skipped
     *             over) from this input stream without blocking.
     * @exception  IOException  if an I/O error occurs.
     */
    public int available() throws IOException {
        return in.available();
    }

    /**
     * Closes this input stream and releases any system resources
     * associated with the stream.
     * This
     * method simply performs <code>in.close()</code>.
     * 关闭此输入流并释放与该流关联的所有系统资源。这个方法只是执行in.close()
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public void close() throws IOException {
        in.close();
    }

    /**
     * Marks the current position in this input stream. A subsequent
     * call to the <code>reset</code> method repositions this stream at
     * the last marked position so that subsequent reads re-read the same bytes.
     * 1.标记此输入流中的当前位置。对reset方法的后续调用将此流重新定位在最后标记的位置，
     * 以便后续读取重新读取相同的字节
     * <p>
     * The <code>readlimit</code> argument tells this input stream to
     * allow that many bytes to be read before the mark position gets
     * invalidated.
     * 2.read.limit参数告诉这个输入流允许在标记位置失效之前读取那么多字节
     * <p>
     * This method simply performs <code>in.mark(readlimit)</code>.
     * 3.这个方法只是简单地执行in.mark(read.limit)。
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     java.io.FilterInputStream#in
     * @see     java.io.FilterInputStream#reset()
     */
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    /**
     * Repositions this stream to the position at the time the
     * <code>mark</code> method was last called on this input stream.
     * 1.将此流重新定位到上次在此输入流上调用mark方法时的位置。
     * <p>
     * This method
     * simply performs <code>in.reset()</code>.
     * 2.此方法仅执行in.reset()
     * <p>
     * Stream marks are intended to be used in
     * situations where you need to read ahead a little to see what's in
     * the stream. Often this is most easily done by invoking some
     * general parser. If the stream is of the type handled by the
     * parse, it just chugs along happily. If the stream is not of
     * that type, the parser should toss an exception when it fails.
     * If this happens within readlimit bytes, it allows the outer
     * code to reset the stream and try another parser.
     * 3.流标记旨在用于您需要提前阅读以了解流中的内容的情况。
     * 通常，这最容易通过调用一些通用解析器来完成。如果流是由解析处理的类型，
     * 它就会愉快地前进。如果流不是那种类型，解析器应该在它失败时抛出一个异常。
     * 如果这发生在 read.limit 字节内，它允许外部代码重置流并尝试另一个解析器
     * @exception  IOException  if the stream has not been marked or if the
     *               mark has been invalidated.
     * @see        java.io.FilterInputStream#in
     * @see        java.io.FilterInputStream#mark(int)
     */
    public synchronized void reset() throws IOException {
        in.reset();
    }

    /**
     * Tests if this input stream supports the <code>mark</code>
     * and <code>reset</code> methods.
     * This method
     * simply performs <code>in.markSupported()</code>.
     * 测试此输入流是否支持mark和reset方法。此方法仅执行in.markSupported()
     * @return  <code>true</code> if this stream type supports the
     *          <code>mark</code> and <code>reset</code> method;
     *          <code>false</code> otherwise.
     * @see     java.io.FilterInputStream#in
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     */
    public boolean markSupported() {
        return in.markSupported();
    }
}
