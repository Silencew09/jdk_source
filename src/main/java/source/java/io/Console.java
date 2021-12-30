/*
 * Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.util.*;
import java.nio.charset.Charset;
import sun.nio.cs.StreamDecoder;
import sun.nio.cs.StreamEncoder;

/**
 * Methods to access the character-based console device, if any, associated
 * with the current Java virtual machine.
 * 1.访问与当前 Java 虚拟机关联的基于字符的控制台设备（如果有）的方法
 * <p> Whether a virtual machine has a console is dependent upon the
 * underlying platform and also upon the manner in which the virtual
 * machine is invoked.  If the virtual machine is started from an
 * interactive command line without redirecting the standard input and
 * output streams then its console will exist and will typically be
 * connected to the keyboard and display from which the virtual machine
 * was launched.  If the virtual machine is started automatically, for
 * example by a background job scheduler, then it will typically not
 * have a console.
 * 2.虚拟机是否有控制台取决于底层平台以及调用虚拟机的方式。如果虚拟机从交互式命令行启动而不重定向标准输入和输出流，
 * 那么它的控制台将存在并且通常将连接到启动虚拟机的键盘和显示器。如果虚拟机是自动启动的，
 * 例如由后台作业调度程序启动，那么它通常没有控制台
 * <p>
 * If this virtual machine has a console then it is represented by a
 * unique instance of this class which can be obtained by invoking the
 * {@link java.lang.System#console()} method.  If no console device is
 * available then an invocation of that method will return <tt>null</tt>.
 * 3.如果此虚拟机具有控制台，则它由此类的唯一实例表示，可以通过调用 java.lang.System.console()方法获得该实例。
 * 如果没有可用的控制台设备，则调用该方法将返回 null
 * <p>
 * Read and write operations are synchronized to guarantee the atomic
 * completion of critical operations; therefore invoking methods
 * {@link #readLine()}, {@link #readPassword()}, {@link #format format()},
 * {@link #printf printf()} as well as the read, format and write operations
 * on the objects returned by {@link #reader()} and {@link #writer()} may
 * block in multithreaded scenarios.
 * 4.读写操作同步，保证关键操作原子完成；因此调用方法 readLine(),readPassword(), format(),
 *  printf()以及对返回对象的读取、格式化和写入操作by reader()和 writer()可能会在多线程场景中阻塞
 * <p>
 * Invoking <tt>close()</tt> on the objects returned by the {@link #reader()}
 * and the {@link #writer()} will not close the underlying stream of those
 * objects.
 * 5.在 reader()和writer()返回的对象上调用close()不会关闭这些对象的底层流。
 * <p>
 * The console-read methods return <tt>null</tt> when the end of the
 * console input stream is reached, for example by typing control-D on
 * Unix or control-Z on Windows.  Subsequent read operations will succeed
 * if additional characters are later entered on the console's input
 * device.
 * 6.当到达控制台输入流的末尾时，控制台读取方法返回null，例如通过在 Unix 上键入
 * control-D 或在 Windows 上键入 control-Z。如果稍后在控制台的输入设备上输入其他字符，则后续读取操作将成功
 * <p>
 * Unless otherwise specified, passing a <tt>null</tt> argument to any method
 * in this class will cause a {@link NullPointerException} to be thrown.
 * 7.除非另有说明，否则将null参数传递给此类中的任何方法将导致抛出NullPointerException
 * <p>
 * <b>Security note:</b>
 * If an application needs to read a password or other secure data, it should
 * use {@link #readPassword()} or {@link #readPassword(String, Object...)} and
 * manually zero the returned character array after processing to minimize the
 * lifetime of sensitive data in memory.
 * 8.安全注意事项：如果应用程序需要读取密码或其他安全数据，则应使用readPassword()或
 * readPassword(String, Object...)并手动将返回的字符归零处理后的数组以最小化内存中敏感数据的生命周期
 * <blockquote><pre>{@code
 * Console cons;
 * char[] passwd;
 * if ((cons = System.console()) != null &&
 *     (passwd = cons.readPassword("[%s]", "Password:")) != null) {
 *     ...
 *     java.util.Arrays.fill(passwd, ' ');
 * }
 * }</pre></blockquote>
 *
 * @author  Xueming Shen
 * @since   1.6
 */

public final class Console implements Flushable
{
   /**
    * Retrieves the unique {@link java.io.PrintWriter PrintWriter} object
    * associated with this console.
    * 检索与此控制台关联的唯一 java.io.PrintWriter对象。
    *
    * @return  The printwriter associated with this console
    */
    public PrintWriter writer() {
        return pw;
    }

   /**
    * Retrieves the unique {@link java.io.Reader Reader} object associated
    * with this console.
    * 1.检索与此控制台关联的唯一 java.io.Reader对象
    * <p>
    * This method is intended to be used by sophisticated applications, for
    * example, a {@link java.util.Scanner} object which utilizes the rich
    * parsing/scanning functionality provided by the <tt>Scanner</tt>:
    * <blockquote><pre>
    * Console con = System.console();
    * if (con != null) {
    *     Scanner sc = new Scanner(con.reader());
    *     ...
    * }
    * 2.此方法旨在由复杂的应用程序使用，例如，一个java.util.Scanner对象，
    * 它利用了Scanner提供的丰富的解析扫描功能：Console con = System.console();
    * if (con != null) { Scanner sc = new Scanner(con.reader()); ... }
    * </pre></blockquote>
    * <p>
    * For simple applications requiring only line-oriented reading, use
    * <tt>{@link #readLine}</tt>.
    * <p>
    * The bulk read operations {@link java.io.Reader#read(char[]) read(char[]) },
    * {@link java.io.Reader#read(char[], int, int) read(char[], int, int) } and
    * {@link java.io.Reader#read(java.nio.CharBuffer) read(java.nio.CharBuffer)}
    * on the returned object will not read in characters beyond the line
    * bound for each invocation, even if the destination buffer has space for
    * more characters. The {@code Reader}'s {@code read} methods may block if a
    * line bound has not been entered or reached on the console's input device.
    * A line bound is considered to be any one of a line feed (<tt>'\n'</tt>),
    * a carriage return (<tt>'\r'</tt>), a carriage return followed immediately
    * by a linefeed, or an end of stream.
    * 3.对于只需要面向行读取的简单应用程序，请使用readLine。批量读取操作java.io.Reader.read(char[]) read(char[]),
    * java.io.Reader.read(char[], int, int) 和java.io.Reader.read(java.nio.CharBuffer
    * 不会在每次调用时读取超出行边界的字符，即使目标缓冲区有更多字符的空间。Reader的 read方法可能会阻塞，
    * 如果没有在控制台的输入设备上输入或到达行边界。换行符被认为是换行符 ('\n')、回车符 ('\r')、
    * 回车符和换行符中的任何一个，或流结束
    * @return  The reader associated with this console
    */
    public Reader reader() {
        return reader;
    }

   /**
    * Writes a formatted string to this console's output stream using
    * the specified format string and arguments.
    * 使用指定的格式字符串和参数将格式化字符串写入此控制台的输出流
    * @param  fmt
    *         A format string as described in <a
    *         href="../util/Formatter.html#syntax">Format string syntax</a>
    *
    * @param  args
    *         Arguments referenced by the format specifiers in the format
    *         string.  If there are more arguments than format specifiers, the
    *         extra arguments are ignored.  The number of arguments is
    *         variable and may be zero.  The maximum number of arguments is
    *         limited by the maximum dimension of a Java array as defined by
    *         <cite>The Java&trade; Virtual Machine Specification</cite>.
    *         The behaviour on a
    *         <tt>null</tt> argument depends on the <a
    *         href="../util/Formatter.html#syntax">conversion</a>.
    *
    * @throws  IllegalFormatException
    *          If a format string contains an illegal syntax, a format
    *          specifier that is incompatible with the given arguments,
    *          insufficient arguments given the format string, or other
    *          illegal conditions.  For specification of all possible
    *          formatting errors, see the <a
    *          href="../util/Formatter.html#detail">Details</a> section
    *          of the formatter class specification.
    *
    * @return  This console
    */
    public Console format(String fmt, Object ...args) {
        formatter.format(fmt, args).flush();
        return this;
    }

   /**
    * A convenience method to write a formatted string to this console's
    * output stream using the specified format string and arguments.
    * 1.使用指定的格式字符串和参数将格式化字符串写入此控制台的输出流的便捷方法
    * <p> An invocation of this method of the form <tt>con.printf(format,
    * args)</tt> behaves in exactly the same way as the invocation of
    * <pre>con.format(format, args)</pre>.
    * 2.以con.printf(format, args)形式调用此方法的行为与调用con.format(format, args)完全相同
    * @param  format
    *         A format string as described in <a
    *         href="../util/Formatter.html#syntax">Format string syntax</a>.
    *
    * @param  args
    *         Arguments referenced by the format specifiers in the format
    *         string.  If there are more arguments than format specifiers, the
    *         extra arguments are ignored.  The number of arguments is
    *         variable and may be zero.  The maximum number of arguments is
    *         limited by the maximum dimension of a Java array as defined by
    *         <cite>The Java&trade; Virtual Machine Specification</cite>.
    *         The behaviour on a
    *         <tt>null</tt> argument depends on the <a
    *         href="../util/Formatter.html#syntax">conversion</a>.
    *
    * @throws  IllegalFormatException
    *          If a format string contains an illegal syntax, a format
    *          specifier that is incompatible with the given arguments,
    *          insufficient arguments given the format string, or other
    *          illegal conditions.  For specification of all possible
    *          formatting errors, see the <a
    *          href="../util/Formatter.html#detail">Details</a> section of the
    *          formatter class specification.
    *
    * @return  This console
    */
    public Console printf(String format, Object ... args) {
        return format(format, args);
    }

   /**
    * Provides a formatted prompt, then reads a single line of text from the
    * console.
    * 提供格式化提示，然后从控制台读取单行文本
    * @param  fmt
    *         A format string as described in <a
    *         href="../util/Formatter.html#syntax">Format string syntax</a>.
    *
    * @param  args
    *         Arguments referenced by the format specifiers in the format
    *         string.  If there are more arguments than format specifiers, the
    *         extra arguments are ignored.  The maximum number of arguments is
    *         limited by the maximum dimension of a Java array as defined by
    *         <cite>The Java&trade; Virtual Machine Specification</cite>.
    *
    * @throws  IllegalFormatException
    *          If a format string contains an illegal syntax, a format
    *          specifier that is incompatible with the given arguments,
    *          insufficient arguments given the format string, or other
    *          illegal conditions.  For specification of all possible
    *          formatting errors, see the <a
    *          href="../util/Formatter.html#detail">Details</a> section
    *          of the formatter class specification.
    *
    * @throws IOError
    *         If an I/O error occurs.
    *
    * @return  A string containing the line read from the console, not
    *          including any line-termination characters, or <tt>null</tt>
    *          if an end of stream has been reached.
    */
    public String readLine(String fmt, Object ... args) {
        String line = null;
        synchronized (writeLock) {
            synchronized(readLock) {
                if (fmt.length() != 0)
                    pw.format(fmt, args);
                try {
                    char[] ca = readline(false);
                    if (ca != null)
                        line = new String(ca);
                } catch (IOException x) {
                    throw new IOError(x);
                }
            }
        }
        return line;
    }

   /**
    * Reads a single line of text from the console.
    * 从控制台读取一行文本
    *
    * @throws IOError
    *         If an I/O error occurs.
    *
    * @return  A string containing the line read from the console, not
    *          including any line-termination characters, or <tt>null</tt>
    *          if an end of stream has been reached.
    */
    public String readLine() {
        return readLine("");
    }

   /**
    * Provides a formatted prompt, then reads a password or passphrase from
    * the console with echoing disabled.
    * 提供格式化的提示，然后在禁用回显的情况下从控制台读取密码或密码。
    * @param  fmt
    *         A format string as described in <a
    *         href="../util/Formatter.html#syntax">Format string syntax</a>
    *         for the prompt text.
    *
    * @param  args
    *         Arguments referenced by the format specifiers in the format
    *         string.  If there are more arguments than format specifiers, the
    *         extra arguments are ignored.  The maximum number of arguments is
    *         limited by the maximum dimension of a Java array as defined by
    *         <cite>The Java&trade; Virtual Machine Specification</cite>.
    *
    * @throws  IllegalFormatException
    *          If a format string contains an illegal syntax, a format
    *          specifier that is incompatible with the given arguments,
    *          insufficient arguments given the format string, or other
    *          illegal conditions.  For specification of all possible
    *          formatting errors, see the <a
    *          href="../util/Formatter.html#detail">Details</a>
    *          section of the formatter class specification.
    *
    * @throws IOError
    *         If an I/O error occurs.
    *
    * @return  A character array containing the password or passphrase read
    *          from the console, not including any line-termination characters,
    *          or <tt>null</tt> if an end of stream has been reached.
    */
    public char[] readPassword(String fmt, Object ... args) {
        char[] passwd = null;
        synchronized (writeLock) {
            synchronized(readLock) {
                try {
                    echoOff = echo(false);
                } catch (IOException x) {
                    throw new IOError(x);
                }
                IOError ioe = null;
                try {
                    if (fmt.length() != 0)
                        pw.format(fmt, args);
                    passwd = readline(true);
                } catch (IOException x) {
                    ioe = new IOError(x);
                } finally {
                    try {
                        echoOff = echo(true);
                    } catch (IOException x) {
                        if (ioe == null)
                            ioe = new IOError(x);
                        else
                            ioe.addSuppressed(x);
                    }
                    if (ioe != null)
                        throw ioe;
                }
                pw.println();
            }
        }
        return passwd;
    }

   /**
    * Reads a password or passphrase from the console with echoing disabled
    * 在禁用回显的情况下从控制台读取密码或密码
    * @throws IOError
    *         If an I/O error occurs.
    *
    * @return  A character array containing the password or passphrase read
    *          from the console, not including any line-termination characters,
    *          or <tt>null</tt> if an end of stream has been reached.
    */
    public char[] readPassword() {
        return readPassword("");
    }

    /**
     * Flushes the console and forces any buffered output to be written
     * immediately .
     * 刷新控制台并强制立即写入任何缓冲的输出
     */
    public void flush() {
        pw.flush();
    }

    private Object readLock;
    private Object writeLock;
    private Reader reader;
    private Writer out;
    private PrintWriter pw;
    private Formatter formatter;
    private Charset cs;
    private char[] rcb;
    private static native String encoding();
    private static native boolean echo(boolean on) throws IOException;
    private static boolean echoOff;

    private char[] readline(boolean zeroOut) throws IOException {
        int len = reader.read(rcb, 0, rcb.length);
        if (len < 0)
            return null;  //EOL
        if (rcb[len-1] == '\r')
            len--;        //remove CR at end;
        else if (rcb[len-1] == '\n') {
            len--;        //remove LF at end;
            if (len > 0 && rcb[len-1] == '\r')
                len--;    //remove the CR, if there is one
        }
        char[] b = new char[len];
        if (len > 0) {
            System.arraycopy(rcb, 0, b, 0, len);
            if (zeroOut) {
                Arrays.fill(rcb, 0, len, ' ');
            }
        }
        return b;
    }

    private char[] grow() {
        assert Thread.holdsLock(readLock);
        char[] t = new char[rcb.length * 2];
        System.arraycopy(rcb, 0, t, 0, rcb.length);
        rcb = t;
        return rcb;
    }

    class LineReader extends Reader {
        private Reader in;
        private char[] cb;
        private int nChars, nextChar;
        boolean leftoverLF;
        LineReader(Reader in) {
            this.in = in;
            cb = new char[1024];
            nextChar = nChars = 0;
            leftoverLF = false;
        }
        public void close () {}
        public boolean ready() throws IOException {
            //in.ready synchronizes on readLock already
            //in.ready 已经在 readLock 上同步了
            return in.ready();
        }

        public int read(char cbuf[], int offset, int length)
            throws IOException
        {
            int off = offset;
            int end = offset + length;
            if (offset < 0 || offset > cbuf.length || length < 0 ||
                end < 0 || end > cbuf.length) {
                throw new IndexOutOfBoundsException();
            }
            synchronized(readLock) {
                boolean eof = false;
                char c = 0;
                for (;;) {
                    if (nextChar >= nChars) {   //fill
                        int n = 0;
                        do {
                            n = in.read(cb, 0, cb.length);
                        } while (n == 0);
                        if (n > 0) {
                            nChars = n;
                            nextChar = 0;
                            if (n < cb.length &&
                                cb[n-1] != '\n' && cb[n-1] != '\r') {
                                /*
                                 * we're in canonical mode so each "fill" should
                                 * come back with an eol. if there no lf or nl at
                                 * the end of returned bytes we reached an eof.
                                 * 我们处于规范模式，因此每个“填充”都应该返回一个 eol。
                                 * 如果在返回字节的末尾没有 lf 或 nl，我们就达到了 eof。
                                 */
                                eof = true;
                            }
                        } else { /*EOF*/
                            if (off - offset == 0)
                                return -1;
                            return off - offset;
                        }
                    }
                    if (leftoverLF && cbuf == rcb && cb[nextChar] == '\n') {
                        /*
                         * if invoked by our readline, skip the leftover, otherwise
                         * return the LF.
                         * 如果被我们的 readline 调用，跳过剩余的，否则返回 LF
                         */
                        nextChar++;
                    }
                    leftoverLF = false;
                    while (nextChar < nChars) {
                        c = cbuf[off++] = cb[nextChar];
                        cb[nextChar++] = 0;
                        if (c == '\n') {
                            return off - offset;
                        } else if (c == '\r') {
                            if (off == end) {
                                /* no space left even the next is LF, so return
                                 * whatever we have if the invoker is not our
                                 * readLine()
                                 * 即使下一个是 LF，也没有剩余空间，因此如果调用者不是我们的 readLine()，
                                 * 则返回我们拥有的任何内容
                                 */
                                if (cbuf == rcb) {
                                    cbuf = grow();
                                    end = cbuf.length;
                                } else {
                                    leftoverLF = true;
                                    return off - offset;
                                }
                            }
                            if (nextChar == nChars && in.ready()) {
                                /*
                                 * we have a CR and we reached the end of
                                 * the read in buffer, fill to make sure we
                                 * don't miss a LF, if there is one, it's possible
                                 * that it got cut off during last round reading
                                 * simply because the read in buffer was full.
                                 * 我们有一个 CR 并且我们到达了缓冲区读取的末尾，
                                 * 填充以确保我们不会错过 LF，如果有，它可能在上一轮读取期间被切断，因为缓冲区中的读取是满的
                                 */
                                nChars = in.read(cb, 0, cb.length);
                                nextChar = 0;
                            }
                            if (nextChar < nChars && cb[nextChar] == '\n') {
                                cbuf[off++] = '\n';
                                nextChar++;
                            }
                            return off - offset;
                        } else if (off == end) {
                           if (cbuf == rcb) {
                                cbuf = grow();
                                end = cbuf.length;
                           } else {
                               return off - offset;
                           }
                        }
                    }
                    if (eof)
                        return off - offset;
                }
            }
        }
    }

    // Set up JavaIOAccess in SharedSecrets
    //在 SharedSecrets 中设置 JavaIOAccess
    static {
        try {
            // Add a shutdown hook to restore console's echo state should
            // it be necessary.
            //如果有必要，添加一个关闭钩子来恢复控制台的回声状态
            sun.misc.SharedSecrets.getJavaLangAccess()
                .registerShutdownHook(0 /* shutdown hook invocation order */,
                    false /* only register if shutdown is not in progress */,
                    new Runnable() {
                        public void run() {
                            try {
                                if (echoOff) {
                                    echo(true);
                                }
                            } catch (IOException x) { }
                        }
                    });
        } catch (IllegalStateException e) {
            // shutdown is already in progress and console is first used
            // by a shutdown hook
        }

        sun.misc.SharedSecrets.setJavaIOAccess(new sun.misc.JavaIOAccess() {
            public Console console() {
                if (istty()) {
                    if (cons == null)
                        cons = new Console();
                    return cons;
                }
                return null;
            }

            public Charset charset() {
                // This method is called in sun.security.util.Password,
                // cons already exists when this method is called
                return cons.cs;
            }
        });
    }
    private static Console cons;
    private native static boolean istty();
    private Console() {
        readLock = new Object();
        writeLock = new Object();
        String csname = encoding();
        if (csname != null) {
            try {
                cs = Charset.forName(csname);
            } catch (Exception x) {}
        }
        if (cs == null)
            cs = Charset.defaultCharset();
        out = StreamEncoder.forOutputStreamWriter(
                  new FileOutputStream(FileDescriptor.out),
                  writeLock,
                  cs);
        pw = new PrintWriter(out, true) { public void close() {} };
        formatter = new Formatter(out);
        reader = new LineReader(StreamDecoder.forInputStreamReader(
                     new FileInputStream(FileDescriptor.in),
                     readLock,
                     cs));
        rcb = new char[1024];
    }
}
