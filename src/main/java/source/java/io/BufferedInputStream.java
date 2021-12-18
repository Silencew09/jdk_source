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
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * A <code>BufferedInputStream</code> adds
 * functionality to another input stream-namely,
 * the ability to buffer the input and to
 * support the <code>mark</code> and <code>reset</code>
 * methods. When  the <code>BufferedInputStream</code>
 * is created, an internal buffer array is
 * created. As bytes  from the stream are read
 * or skipped, the internal buffer is refilled
 * as necessary  from the contained input stream,
 * many bytes at a time. The <code>mark</code>
 * operation  remembers a point in the input
 * stream and the <code>reset</code> operation
 * causes all the  bytes read since the most
 * recent <code>mark</code> operation to be
 * reread before new bytes are  taken from
 * the contained input stream.
 * BufferedInputStream向另一个输入流添加功能，即缓冲输入和支持mark和reset方法的能力。
 * 创建BufferedInputStream时，会创建一个内部缓冲区数组。当读取或跳过流中的字节时，
 * 内部缓冲区会根据需要从包含的输入流中重新填充，一次很多字节。mark操作记住输入流中的一个点，
 * reset操作导致在新字节被读取之前重新读取自最近的mark操作以来读取的所有字节取自包含的输入流。
 * @author  Arthur van Hoff
 * @since   JDK1.0
 */
public
class BufferedInputStream extends FilterInputStream {

    //默认缓冲区大小
    private static int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     * 要分配的数组的最大大小。一些 VM 在数组中保留一些头字。
     * 尝试分配更大的数组可能会导致 OutOfMemoryError：请求的数组大小超出 VM 限制
     */
    private static int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    /**
     * The internal buffer array where the data is stored. When necessary,
     * it may be replaced by another array of
     * a different size.
     * 存储数据的内部缓冲区数组。必要时，它可能会被另一个不同大小的数组替换
     */
    protected volatile byte buf[];

    /**
     * Atomic updater to provide compareAndSet for buf. This is
     * necessary because closes can be asynchronous. We use nullness
     * of buf[] as primary indicator that this stream is closed. (The
     * "in" field is also nulled out on close.)
     * 原子更新程序为 buf 提供 compareAndSet。这是必要的，因为关闭可以是异步的。
     * 我们使用 buf[] 的空值作为该流已关闭的主要指示符。 （“in”字段在关闭时也会被清零。）
     */
    private static final
        AtomicReferenceFieldUpdater<BufferedInputStream, byte[]> bufUpdater =
        AtomicReferenceFieldUpdater.newUpdater
        (BufferedInputStream.class,  byte[].class, "buf");

    /**
     * The index one greater than the index of the last valid byte in
     * the buffer.
     * This value is always
     * in the range <code>0</code> through <code>buf.length</code>;
     * elements <code>buf[0]</code>  through <code>buf[count-1]
     * </code>contain buffered input data obtained
     * from the underlying  input stream.
     * 比缓冲区中最后一个有效字节的索引大 1 的索引。该值始终在0到buf.length范围内；
     * 元素buf[0]到buf[count-1]包含从底层输入流获得的缓冲输入数据。
     */
    protected int count;

    /**
     * The current position in the buffer. This is the index of the next
     * character to be read from the <code>buf</code> array.
     * 1.缓冲区中的当前位置。这是要从buf数组中读取的下一个字符的索引
     * <p>
     * This value is always in the range <code>0</code>
     * through <code>count</code>. If it is less
     * than <code>count</code>, then  <code>buf[pos]</code>
     * is the next byte to be supplied as input;
     * if it is equal to <code>count</code>, then
     * the  next <code>read</code> or <code>skip</code>
     * operation will require more bytes to be
     * read from the contained  input stream.
     * 2.该值始终在0到count的范围内。如果小于count，则buf[pos]是下一个要作为输入提供的字节；
     * 如果它等于count，则下一个read或skip操作将需要从包含的输入流中读取更多字节
     * @see     java.io.BufferedInputStream#buf
     */
    protected int pos;

    /**
     * The value of the <code>pos</code> field at the time the last
     * <code>mark</code> method was called.
     * 1.在最后一个mark方法被调用时pos字段的值
     * <p>
     * This value is always
     * in the range <code>-1</code> through <code>pos</code>.
     * If there is no marked position in  the input
     * stream, this field is <code>-1</code>. If
     * there is a marked position in the input
     * stream,  then <code>buf[markpos]</code>
     * is the first byte to be supplied as input
     * after a <code>reset</code> operation. If
     * <code>markpos</code> is not <code>-1</code>,
     * then all bytes from positions <code>buf[markpos]</code>
     * through  <code>buf[pos-1]</code> must remain
     * in the buffer array (though they may be
     * moved to  another place in the buffer array,
     * with suitable adjustments to the values
     * of <code>count</code>,  <code>pos</code>,
     * and <code>markpos</code>); they may not
     * be discarded unless and until the difference
     * between <code>pos</code> and <code>markpos</code>
     * exceeds <code>marklimit</code>.
     * 2.该值始终在-1到pos的范围内。如果输入流中没有标记位置，则该字段为-1。
     * 如果输入流中有标记位置，则buf[markpos]是reset操作后要作为输入提供的第一个字节。
     * 如果markpos不是-1，则从位置buf[markpos]到buf[pos-1]的所有字节必须保留在缓冲区数组
     * （尽管它们可能会移动到缓冲区数组中的另一个位置，并对count、pos和markpos的值进行适当调整）；
     * 除非pos和markpos之间的差异超过marklimit，否则它们可能不会被丢弃
     * @see     java.io.BufferedInputStream#mark(int)
     * @see     java.io.BufferedInputStream#pos
     */
    protected int markpos = -1;

    /**
     * The maximum read ahead allowed after a call to the
     * <code>mark</code> method before subsequent calls to the
     * <code>reset</code> method fail.
     * Whenever the difference between <code>pos</code>
     * and <code>markpos</code> exceeds <code>marklimit</code>,
     * then the  mark may be dropped by setting
     * <code>markpos</code> to <code>-1</code>.
     * 在调用mark方法之后，在后续调用reset方法失败之前允许的最大预读。
     * 每当pos和markpos之间的差异超过marklimit时，可以通过将markpos设置为-1<来删除标记代码
     * @see     java.io.BufferedInputStream#mark(int)
     * @see     java.io.BufferedInputStream#reset()
     */
    protected int marklimit;

    /**
     * Check to make sure that underlying input stream has not been
     * nulled out due to close; if not return it;
     * 检查以确保底层输入流没有因关闭而被清零；如果没有返回；
     */
    private InputStream getInIfOpen() throws IOException {
        InputStream input = in;
        if (input == null)
            throw new IOException("Stream closed");
        return input;
    }

    /**
     * Check to make sure that buffer has not been nulled out due to
     * close; if not return it;
     * 检查以确保缓冲区没有因关闭而被清空；如果没有返回
     */
    private byte[] getBufIfOpen() throws IOException {
        byte[] buffer = buf;
        if (buffer == null)
            throw new IOException("Stream closed");
        return buffer;
    }

    /**
     * Creates a <code>BufferedInputStream</code>
     * and saves its  argument, the input stream
     * <code>in</code>, for later use. An internal
     * buffer array is created and  stored in <code>buf</code>.
     * 创建一个BufferedInputStream并保存它的参数，输入流in，供以后使用。
     * 一个内部缓冲区数组被创建并存储在buf中。
     * @param   in   the underlying input stream.
     */
    public BufferedInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates a <code>BufferedInputStream</code>
     * with the specified buffer size,
     * and saves its  argument, the input stream
     * <code>in</code>, for later use.  An internal
     * buffer array of length  <code>size</code>
     * is created and stored in <code>buf</code>.
     * 使用指定的缓冲区大小创建一个BufferedInputStream，并保存它的参数，
     * 输入流in，供以后使用。长度为size的内部缓冲区数组被创建并存储在buf中。
     * @param   in     the underlying input stream.
     * @param   size   the buffer size.
     * @exception IllegalArgumentException if {@code size <= 0}.
     */
    public BufferedInputStream(InputStream in, int size) {
        super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        buf = new byte[size];
    }

    /**
     * Fills the buffer with more data, taking into account
     * shuffling and other tricks for dealing with marks.
     * Assumes that it is being called by a synchronized method.
     * This method also assumes that all data has already been read in,
     * hence pos > count.
     * 用更多数据填充缓冲区，考虑到处理标记的混洗和其他技巧。假设它正在被同步方法调用。
     * 此方法还假设所有数据都已读入，因此 pos > count。
     */
    private void fill() throws IOException {
        byte[] buffer = getBufIfOpen();
        if (markpos < 0)
            pos = 0;            /* no mark: throw away the buffer  无标记：丢弃缓冲区*/
        else if (pos >= buffer.length)  /* no room left in buffer  缓冲区中没有剩余空间*/
            if (markpos > 0) {  /* can throw away early part of the buffer */
                int sz = pos - markpos;
                System.arraycopy(buffer, markpos, buffer, 0, sz);
                pos = sz;
                markpos = 0;
            } else if (buffer.length >= marklimit) {
                markpos = -1;   /* buffer got too big, invalidate mark */
                pos = 0;        /* drop buffer contents
                缓冲区太大，使标记 pos = 0 无效；删除缓冲区内容
                */
            } else if (buffer.length >= MAX_BUFFER_SIZE) {
                throw new OutOfMemoryError("Required array size too large");
            } else {            /* grow buffer */
                int nsz = (pos <= MAX_BUFFER_SIZE - pos) ?
                        pos * 2 : MAX_BUFFER_SIZE;
                if (nsz > marklimit)
                    nsz = marklimit;
                byte nbuf[] = new byte[nsz];
                System.arraycopy(buffer, 0, nbuf, 0, pos);
                if (!bufUpdater.compareAndSet(this, buffer, nbuf)) {
                    // Can't replace buf if there was an async close.
                    // Note: This would need to be changed if fill()
                    // is ever made accessible to multiple threads.
                    // But for now, the only way CAS can fail is via close.
                    // assert buf == null;
                    //如果有异步关闭，则无法替换 buf。注意：如果 fill() 可被多个线程访问，
                    // 则需要更改此设置。但就目前而言，CAS 失败的唯一方法是通过 close。断言 buf == null
                    throw new IOException("Stream closed");
                }
                buffer = nbuf;
            }
        count = pos;
        int n = getInIfOpen().read(buffer, pos, buffer.length - pos);
        if (n > 0)
            count = n + pos;
    }

    /**
     * See
     * the general contract of the <code>read</code>
     * method of <code>InputStream</code>.
     * 参见InputStream的read方法的通用约定。
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if this input stream has been closed by
     *                          invoking its {@link #close()} method,
     *                          or an I/O error occurs.
     * @see        java.io.FilterInputStream#in
     */
    public synchronized int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count)
                return -1;
        }
        return getBufIfOpen()[pos++] & 0xff;
    }

    /**
     * Read characters into a portion of an array, reading from the underlying
     * stream at most once if necessary.
     * 将字符读入数组的一部分，如有必要，最多从底层流中读取一次。
     */
    private int read1(byte[] b, int off, int len) throws IOException {
        int avail = count - pos;
        if (avail <= 0) {
            /* If the requested length is at least as large as the buffer, and
               if there is no mark/reset activity, do not bother to copy the
               bytes into the local buffer.  In this way buffered streams will
               cascade harmlessly. */
            //如果请求的长度至少与缓冲区一样大，并且没有标记重置活动，
            // 则不必费心将字节复制到本地缓冲区中。这样缓冲的流将无害地级联。
            if (len >= getBufIfOpen().length && markpos < 0) {
                return getInIfOpen().read(b, off, len);
            }
            fill();
            avail = count - pos;
            if (avail <= 0) return -1;
        }
        int cnt = (avail < len) ? avail : len;
        System.arraycopy(getBufIfOpen(), pos, b, off, cnt);
        pos += cnt;
        return cnt;
    }

    /**
     * Reads bytes from this byte-input stream into the specified byte array,
     * starting at the given offset.
     * 1.从此字节输入流中读取字节到指定的字节数组中，从给定的偏移量开始。
     * <p> This method implements the general contract of the corresponding
     * <code>{@link InputStream#read(byte[], int, int) read}</code> method of
     * the <code>{@link InputStream}</code> class.  As an additional
     * convenience, it attempts to read as many bytes as possible by repeatedly
     * invoking the <code>read</code> method of the underlying stream.
     * 2.该方法实现了InputStream类对应的InputStream.read(byte[], int, int) read方法的通用约定。
     * 作为额外的便利，它尝试通过重复调用底层流的read方法来读取尽可能多的字节。
     * This
     * iterated <code>read</code> continues until one of the following
     * conditions becomes true: <ul>
     * 3.这个迭代的read一直持续到以下条件之一变为真:
     *   <li> The specified number of bytes have been read,
     *  1)已读取指定数量的字节，
     *   <li> The <code>read</code> method of the underlying stream returns
     *   <code>-1</code>, indicating end-of-file, or
     *  2)底层流的read方法返回-1，表示文件结束，或者底层流的available方法返回零，
     *  表示进一步的输入请求将被阻止
     *   <li> The <code>available</code> method of the underlying stream
     *   returns zero, indicating that further input requests would block.
     *  3)如果底层流上的第一个read返回-1以指示文件结束，则此方法返回-1。否则此方法返回实际读取的字节数。
     * </ul> If the first <code>read</code> on the underlying stream returns
     * <code>-1</code> to indicate end-of-file then this method returns
     * <code>-1</code>.  Otherwise this method returns the number of bytes
     * actually read.
     *
     * <p> Subclasses of this class are encouraged, but not required, to
     * attempt to read as many bytes as possible in the same fashion.
     *  4.鼓励但不要求此类的子类尝试以相同的方式读取尽可能多的字节。
     * @param      b     destination buffer.
     * @param      off   offset at which to start storing bytes.
     * @param      len   maximum number of bytes to read.
     * @return     the number of bytes read, or <code>-1</code> if the end of
     *             the stream has been reached.
     * @exception  IOException  if this input stream has been closed by
     *                          invoking its {@link #close()} method,
     *                          or an I/O error occurs.
     */
    public synchronized int read(byte b[], int off, int len)
        throws IOException
    {
        getBufIfOpen(); // Check for closed stream 检查关闭的流
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int n = 0;
        for (;;) {
            int nread = read1(b, off + n, len - n);
            if (nread <= 0)
                return (n == 0) ? nread : n;
            n += nread;
            if (n >= len)
                return n;
            // if not closed but no bytes available, return
            //如果未关闭但没有可用字节，则返回
            InputStream input = in;
            if (input != null && input.available() <= 0)
                return n;
        }
    }

    /**
     * See the general contract of the <code>skip</code>
     * method of <code>InputStream</code>.
     * 参见InputStream的skip方法的通用约定
     * @exception  IOException  if the stream does not support seek,
     *                          or if this input stream has been closed by
     *                          invoking its {@link #close()} method, or an
     *                          I/O error occurs.
     */
    public synchronized long skip(long n) throws IOException {
        getBufIfOpen(); // Check for closed stream
        if (n <= 0) {
            return 0;
        }
        long avail = count - pos;

        if (avail <= 0) {
            // If no mark position set then don't keep in buffer
            //如果没有设置标记位置，则不要保留在缓冲区中
            if (markpos <0)
                return getInIfOpen().skip(n);

            // Fill in buffer to save bytes for reset
            //填充缓冲区以保存用于重置的字节
            fill();
            avail = count - pos;
            if (avail <= 0)
                return 0;
        }

        long skipped = (avail < n) ? avail : n;
        pos += skipped;
        return skipped;
    }

    /**
     * Returns an estimate of the number of bytes that can be read (or
     * skipped over) from this input stream without blocking by the next
     * invocation of a method for this input stream. The next invocation might be
     * the same thread or another thread.  A single read or skip of this
     * many bytes will not block, but may read or skip fewer bytes.
     * 1.返回可以从此输入流读取（或跳过）的字节数的估计值，而不会因下一次调用此输入流的方法而阻塞。
     * 下一次调用可能是同一个线程或另一个线程。单次读取或跳过这么多字节不会阻塞，但可能读取或跳过更少的字节
     * <p>
     * This method returns the sum of the number of bytes remaining to be read in
     * the buffer (<code>count&nbsp;- pos</code>) and the result of calling the
     * {@link java.io.FilterInputStream#in in}.available().
     * 2.此方法返回缓冲区中剩余要读取的字节数 (count - pos) 和调用java.io.FilterInputStream.in
     * .available() 的结果之和。
     * @return     an estimate of the number of bytes that can be read (or skipped
     *             over) from this input stream without blocking.
     * @exception  IOException  if this input stream has been closed by
     *                          invoking its {@link #close()} method,
     *                          or an I/O error occurs.
     */
    public synchronized int available() throws IOException {
        int n = count - pos;
        int avail = getInIfOpen().available();
        return n > (Integer.MAX_VALUE - avail)
                    ? Integer.MAX_VALUE
                    : n + avail;
    }

    /**
     * See the general contract of the <code>mark</code>
     * method of <code>InputStream</code>.
     * 参见InputStream<code>的mark方法的通用约定
     * @param   readlimit   the maximum limit of bytes that can be read before
     *                      the mark position becomes invalid.
     * @see     java.io.BufferedInputStream#reset()
     */
    public synchronized void mark(int readlimit) {
        marklimit = readlimit;
        markpos = pos;
    }

    /**
     * See the general contract of the <code>reset</code>
     * method of <code>InputStream</code>.
     * 1.参见InputStream的reset方法的通用约定
     * <p>
     * If <code>markpos</code> is <code>-1</code>
     * (no mark has been set or the mark has been
     * invalidated), an <code>IOException</code>
     * is thrown. Otherwise, <code>pos</code> is
     * set equal to <code>markpos</code>.
     * 2.如果markpos是-1（未设置标记或标记已失效），则抛出IOException。
     * 否则，pos设置为等于markpos
     * @exception  IOException  if this stream has not been marked or,
     *                  if the mark has been invalidated, or the stream
     *                  has been closed by invoking its {@link #close()}
     *                  method, or an I/O error occurs.
     * @see        java.io.BufferedInputStream#mark(int)
     */
    public synchronized void reset() throws IOException {
        getBufIfOpen(); // Cause exception if closed
        if (markpos < 0)
            throw new IOException("Resetting to invalid mark");
        pos = markpos;
    }

    /**
     * Tests if this input stream supports the <code>mark</code>
     * and <code>reset</code> methods. The <code>markSupported</code>
     * method of <code>BufferedInputStream</code> returns
     * <code>true</code>.
     * 测试此输入流是否支持mark和reset方法。BufferedInputStream的markSupported方法返回true。
     * @return  a <code>boolean</code> indicating if this stream type supports
     *          the <code>mark</code> and <code>reset</code> methods.
     * @see     java.io.InputStream#mark(int)
     * @see     java.io.InputStream#reset()
     */
    public boolean markSupported() {
        return true;
    }

    /**
     * Closes this input stream and releases any system resources
     * associated with the stream.
     * Once the stream has been closed, further read(), available(), reset(),
     * or skip() invocations will throw an IOException.
     * Closing a previously closed stream has no effect.
     * 关闭此输入流并释放与该流关联的所有系统资源。一旦流关闭，
     * 进一步的 read()、available()、reset() 或 skip()
     * 调用将抛出 IOException。关闭先前关闭的流没有任何效果
     * @exception  IOException  if an I/O error occurs.
     */
    public void close() throws IOException {
        byte[] buffer;
        while ( (buffer = buf) != null) {
            if (bufUpdater.compareAndSet(this, buffer, null)) {
                InputStream input = in;
                in = null;
                if (input != null)
                    input.close();
                return;
            }
            // Else retry in case a new buf was CASed in fill()
            //否则重试以防在 fill() 中 CASed 新的 buf
        }
    }
}
