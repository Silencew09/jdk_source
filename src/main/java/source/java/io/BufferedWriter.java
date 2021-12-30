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

package java.io;


/**
 * Writes text to a character-output stream, buffering characters so as to
 * provide for the efficient writing of single characters, arrays, and strings.
 * 1.将文本写入字符输出流，缓冲字符以提供单个字符、数组和字符串的高效写入
 * <p> The buffer size may be specified, or the default size may be accepted.
 * The default is large enough for most purposes.
 * 2.可以指定缓冲区大小，也可以接受默认大小。对于大多数用途，默认值足够大
 * <p> A newLine() method is provided, which uses the platform's own notion of
 * line separator as defined by the system property <tt>line.separator</tt>.
 * Not all platforms use the newline character ('\n') to terminate lines.
 * Calling this method to terminate each output line is therefore preferred to
 * writing a newline character directly.
 * 3.提供了一个 newLine() 方法，它使用平台自己的由系统属性line.separator定义的行分隔符概念。
 * 并非所有平台都使用换行符 ('\n') 来终止行。因此，调用此方法来终止每个输出行比直接写入换行符更可取
 * <p> In general, a Writer sends its output immediately to the underlying
 * character or byte stream.  Unless prompt output is required, it is advisable
 * to wrap a BufferedWriter around any Writer whose write() operations may be
 * costly, such as FileWriters and OutputStreamWriters.  For example,
 *
 * <pre>
 * PrintWriter out
 *   = new PrintWriter(new BufferedWriter(new FileWriter("foo.out")));
 * </pre>
 * 4.通常，Writer 立即将其输出发送到底层字符或字节流。除非需要提示输出，否则建议将 BufferedWriter
 * 包裹在任何 write() 操作可能开销较大的 Writer 周围，例如 FileWriters 和 OutputStreamWriters。
 * 例如，PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("foo.out")));
 * 将 PrintWriter 的输出缓冲到文件中。如果没有缓冲，每次调用 print() 方法都会导致字符转换为字节，
 * 然后立即写入文件，这可能非常低效
 * will buffer the PrintWriter's output to the file.  Without buffering, each
 * invocation of a print() method would cause characters to be converted into
 * bytes that would then be written immediately to the file, which can be very
 * inefficient.
 *
 * @see PrintWriter
 * @see FileWriter
 * @see OutputStreamWriter
 * @see java.nio.file.Files#newBufferedWriter
 *
 * @author      Mark Reinhold
 * @since       JDK1.1
 */

public class BufferedWriter extends Writer {

    private Writer out;

    private char cb[];
    private int nChars, nextChar;

    private static int defaultCharBufferSize = 8192;

    /**
     * Line separator string.  This is the value of the line.separator
     * property at the moment that the stream was created.
     * 行分隔符字符串。这是创建流时 line.separator 属性的值
     */
    private String lineSeparator;

    /**
     * Creates a buffered character-output stream that uses a default-sized
     * output buffer.
     * 创建一个使用默认大小的输出缓冲区的缓冲字符输出流
     *
     * @param  out  A Writer
     */
    public BufferedWriter(Writer out) {
        this(out, defaultCharBufferSize);
    }

    /**
     * Creates a new buffered character-output stream that uses an output
     * buffer of the given size.
     * 创建一个新的缓冲字符输出流，它使用给定大小的输出缓冲区
     * @param  out  A Writer
     * @param  sz   Output-buffer size, a positive integer
     *
     * @exception  IllegalArgumentException  If {@code sz <= 0}
     */
    public BufferedWriter(Writer out, int sz) {
        super(out);
        if (sz <= 0)
            throw new IllegalArgumentException("Buffer size <= 0");
        this.out = out;
        cb = new char[sz];
        nChars = sz;
        nextChar = 0;

        lineSeparator = java.security.AccessController.doPrivileged(
            new sun.security.action.GetPropertyAction("line.separator"));
    }

    /** Checks to make sure that the stream has not been closed */
    //检查以确保流尚未关闭
    private void ensureOpen() throws IOException {
        if (out == null)
            throw new IOException("Stream closed");
    }

    /**
     * Flushes the output buffer to the underlying character stream, without
     * flushing the stream itself.  This method is non-private only so that it
     * may be invoked by PrintStream.
     * 将输出缓冲区刷新到底层字符流，而不刷新流本身。此方法是非私有的，因此它可以被 PrintStream 调用。
     */
    void flushBuffer() throws IOException {
        synchronized (lock) {
            ensureOpen();
            if (nextChar == 0)
                return;
            out.write(cb, 0, nextChar);
            nextChar = 0;
        }
    }

    /**
     * Writes a single character.
     * 写入单个字符
     * @exception  IOException  If an I/O error occurs
     */
    public void write(int c) throws IOException {
        synchronized (lock) {
            ensureOpen();
            if (nextChar >= nChars)
                flushBuffer();
            cb[nextChar++] = (char) c;
        }
    }

    /**
     * Our own little min method, to avoid loading java.lang.Math if we've run
     * out of file descriptors and we're trying to print a stack trace.
     * 我们自己的小 min 方法，以避免在我们用完文件描述符并且尝试打印堆栈跟踪时加载 java.lang.Math
     */
    private int min(int a, int b) {
        if (a < b) return a;
        return b;
    }

    /**
     * Writes a portion of an array of characters.
     * 1.写入字符数组的一部分
     * <p> Ordinarily this method stores characters from the given array into
     * this stream's buffer, flushing the buffer to the underlying stream as
     * needed.  If the requested length is at least as large as the buffer,
     * however, then this method will flush the buffer and write the characters
     * directly to the underlying stream.  Thus redundant
     * <code>BufferedWriter</code>s will not copy data unnecessarily.
     * 2.通常，此方法将给定数组中的字符存储到此流的缓冲区中，并根据需要将缓冲区刷新到底层流。
     * 但是，如果请求的长度至少与缓冲区一样大，则此方法将刷新缓冲区并将字符直接写入底层流。
     * 因此冗余的BufferedWriter不会不必要地复制数据
     * @param  cbuf  A character array
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(char cbuf[], int off, int len) throws IOException {
        synchronized (lock) {
            ensureOpen();
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }

            if (len >= nChars) {
                /* If the request length exceeds the size of the output buffer,
                   flush the buffer and then write the data directly.  In this
                   way buffered streams will cascade harmlessly. */
                //如果请求长度超过输出缓冲区的大小，则刷新缓冲区，然后直接写入数据。这样缓冲的流将无害地级联
                flushBuffer();
                out.write(cbuf, off, len);
                return;
            }

            int b = off, t = off + len;
            while (b < t) {
                int d = min(nChars - nextChar, t - b);
                System.arraycopy(cbuf, b, cb, nextChar, d);
                b += d;
                nextChar += d;
                if (nextChar >= nChars)
                    flushBuffer();
            }
        }
    }

    /**
     * Writes a portion of a String.
     * 1.写入字符串的一部分。
     * <p> If the value of the <tt>len</tt> parameter is negative then no
     * characters are written.  This is contrary to the specification of this
     * method in the {@linkplain java.io.Writer#write(java.lang.String,int,int)
     * superclass}, which requires that an {@link IndexOutOfBoundsException} be
     * thrown.
     * 2.如果len参数的值为负，则不写入任何字符。这与java.io.Writerwrite(java.lang.String,int,int) 超类
     * 中此方法的规范相反，该规范要求抛出 IndexOutOfBoundsException
     * @param  s     String to be written
     * @param  off   Offset from which to start reading characters
     * @param  len   Number of characters to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(String s, int off, int len) throws IOException {
        synchronized (lock) {
            ensureOpen();

            int b = off, t = off + len;
            while (b < t) {
                int d = min(nChars - nextChar, t - b);
                s.getChars(b, b + d, cb, nextChar);
                b += d;
                nextChar += d;
                if (nextChar >= nChars)
                    flushBuffer();
            }
        }
    }

    /**
     * Writes a line separator.  The line separator string is defined by the
     * system property <tt>line.separator</tt>, and is not necessarily a single
     * newline ('\n') character.
     * 写一个行分隔符。行分隔符字符串由系统属性line.separator定义，不一定是单个换行符 ('\n')
     * @exception  IOException  If an I/O error occurs
     */
    public void newLine() throws IOException {
        write(lineSeparator);
    }

    /**
     * Flushes the stream.
     * 冲洗流。
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void flush() throws IOException {
        synchronized (lock) {
            flushBuffer();
            out.flush();
        }
    }

    @SuppressWarnings("try")
    public void close() throws IOException {
        synchronized (lock) {
            if (out == null) {
                return;
            }
            try (Writer w = out) {
                flushBuffer();
            } finally {
                out = null;
                cb = null;
            }
        }
    }
}
