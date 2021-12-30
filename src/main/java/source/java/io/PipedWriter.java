/*
 * Copyright (c) 1996, 2006, Oracle and/or its affiliates. All rights reserved.
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
 * Piped character-output streams.
 * 管道字符输出流。
 * @author      Mark Reinhold
 * @since       JDK1.1
 */

public class PipedWriter extends Writer {

    /* REMIND: identification of the read and write sides needs to be
       more sophisticated.  Either using thread groups (but what about
       pipes within a thread?) or using finalization (but it may be a
       long time until the next GC). */
    private PipedReader sink;

    /* This flag records the open status of this particular writer. It
     * is independent of the status flags defined in PipedReader. It is
     * used to do a sanity check on connect.
     */
    private boolean closed = false;

    /**
     * Creates a piped writer connected to the specified piped
     * reader. Data characters written to this stream will then be
     * available as input from <code>snk</code>.
     * 创建连接到指定管道读取器的管道写入器。 然后写入此流的数据字符将可用作来自snk输入
     * @param      snk   The piped reader to connect to.
     * @exception  IOException  if an I/O error occurs.
     */
    public PipedWriter(PipedReader snk)  throws IOException {
        connect(snk);
    }

    /**
     * Creates a piped writer that is not yet connected to a
     * piped reader. It must be connected to a piped reader,
     * either by the receiver or the sender, before being used.
     * 创建一个尚未连接到管道读取器的管道写入器。 在使用之前，它必须由接收器或发送器连接到管道阅读器
     * @see     java.io.PipedReader#connect(java.io.PipedWriter)
     * @see     java.io.PipedWriter#connect(java.io.PipedReader)
     */
    public PipedWriter() {
    }

    /**
     * Connects this piped writer to a receiver. If this object
     * is already connected to some other piped reader, an
     * <code>IOException</code> is thrown.
     * <p>
     * If <code>snk</code> is an unconnected piped reader and
     * <code>src</code> is an unconnected piped writer, they may
     * be connected by either the call:
     * <blockquote><pre>
     * src.connect(snk)</pre></blockquote>
     * or the call:
     * <blockquote><pre>
     * snk.connect(src)</pre></blockquote>
     * The two calls have the same effect.
     * 将此管道写入器连接到接收器。 如果此对象已连接到某个其他管道读取器，则会抛出IOException 。
     * 如果snk是未连接的管道读取器而src是未连接的管道写入器，则它们可以通过以下任一调用连接：
     *        src.connect(snk)
     * 或电话：
     *        snk.connect(src)
     * 这两个调用具有相同的效果。
     * @param      snk   the piped reader to connect to.
     * @exception  IOException  if an I/O error occurs.
     */
    public synchronized void connect(PipedReader snk) throws IOException {
        if (snk == null) {
            throw new NullPointerException();
        } else if (sink != null || snk.connected) {
            throw new IOException("Already connected");
        } else if (snk.closedByReader || closed) {
            throw new IOException("Pipe closed");
        }

        sink = snk;
        snk.in = -1;
        snk.out = 0;
        snk.connected = true;
    }

    /**
     * Writes the specified <code>char</code> to the piped output stream.
     * If a thread was reading data characters from the connected piped input
     * stream, but the thread is no longer alive, then an
     * <code>IOException</code> is thrown.
     * <p>
     * Implements the <code>write</code> method of <code>Writer</code>.
     * 将指定的char写入管道输出流。 如果线程正在从连接的管道输入流中读取数据字符，但该线程不再处于活动状态，则会抛出IOException 。
     * 实现Writer的write方法
     * @param      c   the <code>char</code> to be written.
     * @exception  IOException  if the pipe is
     *          <a href=PipedOutputStream.html#BROKEN> <code>broken</code></a>,
     *          {@link #connect(java.io.PipedReader) unconnected}, closed
     *          or an I/O error occurs.
     */
    public void write(int c)  throws IOException {
        if (sink == null) {
            throw new IOException("Pipe not connected");
        }
        sink.receive(c);
    }

    /**
     * Writes <code>len</code> characters from the specified character array
     * starting at offset <code>off</code> to this piped output stream.
     * This method blocks until all the characters are written to the output
     * stream.
     * If a thread was reading data characters from the connected piped input
     * stream, but the thread is no longer alive, then an
     * <code>IOException</code> is thrown.
     * 将指定字符数组中的len字符从偏移量off开始写入此管道输出流。
     * 此方法会阻塞，直到所有字符都写入输出流。
     * 如果线程正在从连接的管道输入流中读取数据字符，但该线程不再处于活动状态，则会抛出IOException 。
     * @param      cbuf  the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of characters to write.
     * @exception  IOException  if the pipe is
     *          <a href=PipedOutputStream.html#BROKEN> <code>broken</code></a>,
     *          {@link #connect(java.io.PipedReader) unconnected}, closed
     *          or an I/O error occurs.
     */
    public void write(char cbuf[], int off, int len) throws IOException {
        if (sink == null) {
            throw new IOException("Pipe not connected");
        } else if ((off | len | (off + len) | (cbuf.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        }
        sink.receive(cbuf, off, len);
    }

    /**
     * Flushes this output stream and forces any buffered output characters
     * to be written out.
     * This will notify any readers that characters are waiting in the pipe.
     * 刷新此输出流并强制写出任何缓冲的输出字符。 这将通知任何读者字符正在管道中等待
     * @exception  IOException  if the pipe is closed, or an I/O error occurs.
     */
    public synchronized void flush() throws IOException {
        if (sink != null) {
            if (sink.closedByReader || closed) {
                throw new IOException("Pipe closed");
            }
            synchronized (sink) {
                sink.notifyAll();
            }
        }
    }

    /**
     * Closes this piped output stream and releases any system resources
     * associated with this stream. This stream may no longer be used for
     * writing characters.
     * 关闭此管道输出流并释放与此流关联的任何系统资源。 此流可能不再用于写入字符
     * @exception  IOException  if an I/O error occurs.
     */
    public void close()  throws IOException {
        closed = true;
        if (sink != null) {
            sink.receivedLast();
        }
    }
}
