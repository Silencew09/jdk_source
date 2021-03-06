/*
 * Copyright (c) 1994, 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A <code>SequenceInputStream</code> represents
 * the logical concatenation of other input
 * streams. It starts out with an ordered
 * collection of input streams and reads from
 * the first one until end of file is reached,
 * whereupon it reads from the second one,
 * and so on, until end of file is reached
 * on the last of the contained input streams.
 * SequenceInputStream表示其他输入流的逻辑串联。
 * 它从输入流的有序集合开始，从第一个流读取，直到到达文件末尾，然后从第二个读取，
 * 依此类推，直到在最后一个包含的输入流上到达文件末尾。
 * @author  Author van Hoff
 * @since   JDK1.0
 */
public
class SequenceInputStream extends InputStream {
    Enumeration<? extends InputStream> e;
    InputStream in;

    /**
     * Initializes a newly created <code>SequenceInputStream</code>
     * by remembering the argument, which must
     * be an <code>Enumeration</code>  that produces
     * objects whose run-time type is <code>InputStream</code>.
     * The input streams that are  produced by
     * the enumeration will be read, in order,
     * to provide the bytes to be read  from this
     * <code>SequenceInputStream</code>. After
     * each input stream from the enumeration
     * is exhausted, it is closed by calling its
     * <code>close</code> method.
     * 通过记住参数来初始化新创建的SequenceInputStream ，该参数必须是一个Enumeration ，
     * 它生成运行时类型为InputStream 。 将读取枚举生成的输入流，以便提供要从此SequenceInputStream读取的字节。
     * 枚举中的每个输入流用完后，通过调用其close方法将其close 。
     * @param   e   an enumeration of input streams.
     * @see     java.util.Enumeration
     */
    public SequenceInputStream(Enumeration<? extends InputStream> e) {
        this.e = e;
        try {
            nextStream();
        } catch (IOException ex) {
            // This should never happen
            throw new Error("panic");
        }
    }

    /**
     * Initializes a newly
     * created <code>SequenceInputStream</code>
     * by remembering the two arguments, which
     * will be read in order, first <code>s1</code>
     * and then <code>s2</code>, to provide the
     * bytes to be read from this <code>SequenceInputStream</code>.
     * 通过记住两个参数来初始化新创建的SequenceInputStream ，这两个参数将按顺序读取，
     * 首先是s1 ，然后是s2 ，以提供要从此SequenceInputStream读取的字节
     * @param   s1   the first input stream to read.
     * @param   s2   the second input stream to read.
     */
    public SequenceInputStream(InputStream s1, InputStream s2) {
        Vector<InputStream> v = new Vector<>(2);

        v.addElement(s1);
        v.addElement(s2);
        e = v.elements();
        try {
            nextStream();
        } catch (IOException ex) {
            // This should never happen
            throw new Error("panic");
        }
    }

    /**
     *  Continues reading in the next stream if an EOF is reached.
     *  如果到达 EOF，则在下一个流中继续读取
     */
    final void nextStream() throws IOException {
        if (in != null) {
            in.close();
        }

        if (e.hasMoreElements()) {
            in = (InputStream) e.nextElement();
            if (in == null)
                throw new NullPointerException();
        }
        else in = null;

    }

    /**
     * Returns an estimate of the number of bytes that can be read (or
     * skipped over) from the current underlying input stream without
     * blocking by the next invocation of a method for the current
     * underlying input stream. The next invocation might be
     * the same thread or another thread.  A single read or skip of this
     * many bytes will not block, but may read or skip fewer bytes.
     * <p>
     * This method simply calls {@code available} of the current underlying
     * input stream and returns the result.
     * 返回可以从当前底层输入流读取（或跳过）的字节数的估计值，而不会被当前底层输入流的方法的下一次调用阻塞。
     * 下一次调用可能是同一个线程或另一个线程。 单次读取或跳过这么多字节不会阻塞，但可能读取或跳过更少的字节。
     * 此方法简单地调用当前底层输入流的available并返回结果。
     * @return an estimate of the number of bytes that can be read (or
     *         skipped over) from the current underlying input stream
     *         without blocking or {@code 0} if this input stream
     *         has been closed by invoking its {@link #close()} method
     * @exception  IOException  if an I/O error occurs.
     *
     * @since   JDK1.1
     */
    public int available() throws IOException {
        if (in == null) {
            return 0; // no way to signal EOF from available()
        }
        return in.available();
    }

    /**
     * Reads the next byte of data from this input stream. The byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the
     * stream has been reached, the value <code>-1</code> is returned.
     * This method blocks until input data is available, the end of the
     * stream is detected, or an exception is thrown.
     * <p>
     * This method
     * tries to read one character from the current substream. If it
     * reaches the end of the stream, it calls the <code>close</code>
     * method of the current substream and begins reading from the next
     * substream.
     * 从此输入流中读取下一个数据字节。 该字节以0到255范围内的int形式返回。
     * 如果由于已到达流末尾而没有可用字节，则返回值-1 。
     * 此方法会阻塞，直到输入数据可用、检测到流结束或抛出异常为止。
     * 此方法尝试从当前子流中读取一个字符。 如果到达流的末尾，则调用当前子流的close方法并开始从下一个子流读取。
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
    public int read() throws IOException {
        while (in != null) {
            int c = in.read();
            if (c != -1) {
                return c;
            }
            nextStream();
        }
        return -1;
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream
     * into an array of bytes.  If <code>len</code> is not zero, the method
     * blocks until at least 1 byte of input is available; otherwise, no
     * bytes are read and <code>0</code> is returned.
     * <p>
     * The <code>read</code> method of <code>SequenceInputStream</code>
     * tries to read the data from the current substream. If it fails to
     * read any characters because the substream has reached the end of
     * the stream, it calls the <code>close</code> method of the current
     * substream and begins reading from the next substream.
     * 从此输入流中读取最多len个字节的数据到一个字节数组中。
     * 如果len不为零，则该方法将阻塞，直到至少有 1 个字节的输入可用； 否则，不读取任何字节并返回0 。
     * SequenceInputStream的read方法尝试从当前子流中读取数据。
     * 如果由于子流已到达流的末尾而无法读取任何字符，则调用当前子流的close方法并从下一个子流开始读取
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in array <code>b</code>
     *                   at which the data is written.
     * @param      len   the maximum number of bytes read.
     * @return     int   the number of bytes read.
     * @exception  NullPointerException If <code>b</code> is <code>null</code>.
     * @exception  IndexOutOfBoundsException If <code>off</code> is negative,
     * <code>len</code> is negative, or <code>len</code> is greater than
     * <code>b.length - off</code>
     * @exception  IOException  if an I/O error occurs.
     */
    public int read(byte b[], int off, int len) throws IOException {
        if (in == null) {
            return -1;
        } else if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        do {
            int n = in.read(b, off, len);
            if (n > 0) {
                return n;
            }
            nextStream();
        } while (in != null);
        return -1;
    }

    /**
     * Closes this input stream and releases any system resources
     * associated with the stream.
     * A closed <code>SequenceInputStream</code>
     * cannot  perform input operations and cannot
     * be reopened.
     * <p>
     * If this stream was created
     * from an enumeration, all remaining elements
     * are requested from the enumeration and closed
     * before the <code>close</code> method returns.
     * 关闭此输入流并释放与该流关联的所有系统资源。 关闭的SequenceInputStream无法执行输入操作，也无法重新打开。
     * 如果此流是从枚举创建的，则从枚举中请求所有剩余元素并在close方法返回之前close
     * @exception  IOException  if an I/O error occurs.
     */
    public void close() throws IOException {
        do {
            nextStream();
        } while (in != null);
    }
}
