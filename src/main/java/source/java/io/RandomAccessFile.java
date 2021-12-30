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

import java.nio.channels.FileChannel;
import sun.nio.ch.FileChannelImpl;


/**
 * Instances of this class support both reading and writing to a
 * random access file. A random access file behaves like a large
 * array of bytes stored in the file system. There is a kind of cursor,
 * or index into the implied array, called the <em>file pointer</em>;
 * input operations read bytes starting at the file pointer and advance
 * the file pointer past the bytes read. If the random access file is
 * created in read/write mode, then output operations are also available;
 * output operations write bytes starting at the file pointer and advance
 * the file pointer past the bytes written. Output operations that write
 * past the current end of the implied array cause the array to be
 * extended. The file pointer can be read by the
 * {@code getFilePointer} method and set by the {@code seek}
 * method.
 * <p>
 * It is generally true of all the reading routines in this class that
 * if end-of-file is reached before the desired number of bytes has been
 * read, an {@code EOFException} (which is a kind of
 * {@code IOException}) is thrown. If any byte cannot be read for
 * any reason other than end-of-file, an {@code IOException} other
 * than {@code EOFException} is thrown. In particular, an
 * {@code IOException} may be thrown if the stream has been closed.
 * 1.此类的实例支持读取和写入随机访问文件。
 * 2.随机访问文件的行为类似于存储在文件系统中的大量字节。
 * 3.有一种游标，或隐含数组的索引，称为文件指针； 输入操作从文件指针开始读取字节，并将文件指针前进到读取的字节之后。
 * 4.如果随机存取文件是以读/写方式创建的，那么也可以进行输出操作；
 * 输出操作从文件指针开始写入字节，并将文件指针前进到写入的字节之后。
 * 5.写入越过隐含数组当前末尾的输出操作会导致数组被扩展。 文件指针可以通过getFilePointer方法读取并通过seek方法设置。
 * 此类中的所有读取例程通常都是如此，如果在读取所需字节数之前到达文件尾，则会抛出EOFException （这是一种IOException ）。 如果由于文件结束以外的任何原因无法读取任何字节，则抛出EOFException以外的IOException 。 特别是，如果流已关闭，则可能会抛出IOException
 * @author  unascribed
 * @since   JDK1.0
 */

public class RandomAccessFile implements DataOutput, DataInput, Closeable {

    private FileDescriptor fd;
    private FileChannel channel = null;
    private boolean rw;

    /**
     * The path of the referenced file
     * (null if the stream is created with a file descriptor)
     * 引用文件的路径（如果流是用文件描述符创建的，则为 null）
     */
    private final String path;

    private Object closeLock = new Object();
    private volatile boolean closed = false;

    private static final int O_RDONLY = 1;
    private static final int O_RDWR =   2;
    private static final int O_SYNC =   4;
    private static final int O_DSYNC =  8;

    /**
     * Creates a random access file stream to read from, and optionally
     * to write to, a file with the specified name. A new
     * {@link FileDescriptor} object is created to represent the
     * connection to the file.
     *
     * <p> The <tt>mode</tt> argument specifies the access mode with which the
     * file is to be opened.  The permitted values and their meanings are as
     * specified for the <a
     * href="#mode"><tt>RandomAccessFile(File,String)</tt></a> constructor.
     *
     * <p>
     * If there is a security manager, its {@code checkRead} method
     * is called with the {@code name} argument
     * as its argument to see if read access to the file is allowed.
     * If the mode allows writing, the security manager's
     * {@code checkWrite} method
     * is also called with the {@code name} argument
     * as its argument to see if write access to the file is allowed.
     * 1.创建一个随机访问文件流，以从具有指定名称的文件中读取和选择性地写入。
     * 2.创建一个新的FileDescriptor对象来表示与文件的连接。
     * 3.mode参数指定打开文件的访问模式。 允许的值及其含义与RandomAccessFile(File,String)构造函数的指定相同。
     * 4.如果有安全管理器，则调用其checkRead方法，并使用name参数作为其参数，以查看是否允许对文件进行读取访问。
     * 5.如果模式允许写入，还会调用安全管理器的checkWrite方法，并将name参数作为其参数，以查看是否允许对文件进行写访问。
     * @param      name   the system-dependent filename
     * @param      mode   the access <a href="#mode">mode</a>
     * @exception  IllegalArgumentException  if the mode argument is not equal
     *               to one of <tt>"r"</tt>, <tt>"rw"</tt>, <tt>"rws"</tt>, or
     *               <tt>"rwd"</tt>
     * @exception FileNotFoundException
     *            if the mode is <tt>"r"</tt> but the given string does not
     *            denote an existing regular file, or if the mode begins with
     *            <tt>"rw"</tt> but the given string does not denote an
     *            existing, writable regular file and a new regular file of
     *            that name cannot be created, or if some other error occurs
     *            while opening or creating the file
     * @exception  SecurityException         if a security manager exists and its
     *               {@code checkRead} method denies read access to the file
     *               or the mode is "rw" and the security manager's
     *               {@code checkWrite} method denies write access to the file
     * @see        java.lang.SecurityException
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @revised 1.4
     * @spec JSR-51
     */
    public RandomAccessFile(String name, String mode)
        throws FileNotFoundException
    {
        this(name != null ? new File(name) : null, mode);
    }

    /**
     * Creates a random access file stream to read from, and optionally to
     * write to, the file specified by the {@link File} argument.  A new {@link
     * FileDescriptor} object is created to represent this file connection.
     *
     * <p>The <a name="mode"><tt>mode</tt></a> argument specifies the access mode
     * in which the file is to be opened.  The permitted values and their
     * meanings are:
     *
     * <table summary="Access mode permitted values and meanings">
     * <tr><th align="left">Value</th><th align="left">Meaning</th></tr>
     * <tr><td valign="top"><tt>"r"</tt></td>
     *     <td> Open for reading only.  Invoking any of the <tt>write</tt>
     *     methods of the resulting object will cause an {@link
     *     java.io.IOException} to be thrown. </td></tr>
     * <tr><td valign="top"><tt>"rw"</tt></td>
     *     <td> Open for reading and writing.  If the file does not already
     *     exist then an attempt will be made to create it. </td></tr>
     * <tr><td valign="top"><tt>"rws"</tt></td>
     *     <td> Open for reading and writing, as with <tt>"rw"</tt>, and also
     *     require that every update to the file's content or metadata be
     *     written synchronously to the underlying storage device.  </td></tr>
     * <tr><td valign="top"><tt>"rwd"&nbsp;&nbsp;</tt></td>
     *     <td> Open for reading and writing, as with <tt>"rw"</tt>, and also
     *     require that every update to the file's content be written
     *     synchronously to the underlying storage device. </td></tr>
     * </table>
     *
     * The <tt>"rws"</tt> and <tt>"rwd"</tt> modes work much like the {@link
     * java.nio.channels.FileChannel#force(boolean) force(boolean)} method of
     * the {@link java.nio.channels.FileChannel} class, passing arguments of
     * <tt>true</tt> and <tt>false</tt>, respectively, except that they always
     * apply to every I/O operation and are therefore often more efficient.  If
     * the file resides on a local storage device then when an invocation of a
     * method of this class returns it is guaranteed that all changes made to
     * the file by that invocation will have been written to that device.  This
     * is useful for ensuring that critical information is not lost in the
     * event of a system crash.  If the file does not reside on a local device
     * then no such guarantee is made.
     *
     * <p>The <tt>"rwd"</tt> mode can be used to reduce the number of I/O
     * operations performed.  Using <tt>"rwd"</tt> only requires updates to the
     * file's content to be written to storage; using <tt>"rws"</tt> requires
     * updates to both the file's content and its metadata to be written, which
     * generally requires at least one more low-level I/O operation.
     *
     * <p>If there is a security manager, its {@code checkRead} method is
     * called with the pathname of the {@code file} argument as its
     * argument to see if read access to the file is allowed.  If the mode
     * allows writing, the security manager's {@code checkWrite} method is
     * also called with the path argument to see if write access to the file is
     * allowed.
     *1.创建一个随机访问文件流以从File参数指定的File读取，并可选择写入。
     * 2.创建一个新的FileDescriptor对象来表示此文件连接。
     * mode参数指定打开文件的访问模式。 允许的值及其含义是：
     * 价值
     * 意义
     * “r”
     * 仅供阅读。 调用结果对象的任何写入方法都会导致抛出IOException 。
     * “rw”
     * 开放阅读和写作。 如果该文件尚不存在，则会尝试创建它。
     * “rws”
     * 与"rw"一样可读写，并且还要求对文件内容或元数据的每次更新都同步写入底层存储设备。
     * “rwd”
     * 与"rw"一样可读写，并且还要求对文件内容的每次更新都同步写入底层存储设备。
     * 3."rws"和"rwd"模式的工作方式与FileChannel类的force(boolean)方法非常相似，
     * 分别传递true和false参数，但它们始终适用于每个 I/O 操作，因此通常更有效。
     *4.如果文件驻留在本地存储设备上，则当此类方法的调用返回时，可以保证该调用对文件所做的所有更改都将写入该设备。
     *  这对于确保在系统崩溃时不会丢失关键信息很有用。 如果文件不在本地设备上，则不提供此类保证。
     * 5.“rwd”模式可用于减少执行的 I/O 操作的数量。
     * 使用“rwd”只需要更新要写入存储的文件内容；
     * 使用“rws”需要更新要写入的文件内容及其元数据，这通常至少需要再进行一次低级 I/O 操作。
     * 6.如果有安全管理器，则调用其checkRead方法，并使用file参数的路径名作为其参数，以查看是否允许对文件进行读访问。
     * 如果模式允许写入，还会使用路径参数调用安全管理器的checkWrite方法，以查看是否允许对文件进行写访问
     * @param      file   the file object
     * @param      mode   the access mode, as described
     *                    <a href="#mode">above</a>
     * @exception  IllegalArgumentException  if the mode argument is not equal
     *               to one of <tt>"r"</tt>, <tt>"rw"</tt>, <tt>"rws"</tt>, or
     *               <tt>"rwd"</tt>
     * @exception FileNotFoundException
     *            if the mode is <tt>"r"</tt> but the given file object does
     *            not denote an existing regular file, or if the mode begins
     *            with <tt>"rw"</tt> but the given file object does not denote
     *            an existing, writable regular file and a new regular file of
     *            that name cannot be created, or if some other error occurs
     *            while opening or creating the file
     * @exception  SecurityException         if a security manager exists and its
     *               {@code checkRead} method denies read access to the file
     *               or the mode is "rw" and the security manager's
     *               {@code checkWrite} method denies write access to the file
     * @see        java.lang.SecurityManager#checkRead(java.lang.String)
     * @see        java.lang.SecurityManager#checkWrite(java.lang.String)
     * @see        java.nio.channels.FileChannel#force(boolean)
     * @revised 1.4
     * @spec JSR-51
     */
    public RandomAccessFile(File file, String mode)
        throws FileNotFoundException
    {
        String name = (file != null ? file.getPath() : null);
        int imode = -1;
        if (mode.equals("r"))
            imode = O_RDONLY;
        else if (mode.startsWith("rw")) {
            imode = O_RDWR;
            rw = true;
            if (mode.length() > 2) {
                if (mode.equals("rws"))
                    imode |= O_SYNC;
                else if (mode.equals("rwd"))
                    imode |= O_DSYNC;
                else
                    imode = -1;
            }
        }
        if (imode < 0)
            throw new IllegalArgumentException("Illegal mode \"" + mode
                                               + "\" must be one of "
                                               + "\"r\", \"rw\", \"rws\","
                                               + " or \"rwd\"");
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(name);
            if (rw) {
                security.checkWrite(name);
            }
        }
        if (name == null) {
            throw new NullPointerException();
        }
        if (file.isInvalid()) {
            throw new FileNotFoundException("Invalid file path");
        }
        fd = new FileDescriptor();
        fd.attach(this);
        path = name;
        open(name, imode);
    }

    /**
     * Returns the opaque file descriptor object associated with this
     * stream.
     * 返回与此流关联的不透明文件描述符对象
     * @return     the file descriptor object associated with this stream.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FileDescriptor
     */
    public final FileDescriptor getFD() throws IOException {
        if (fd != null) {
            return fd;
        }
        throw new IOException();
    }

    /**
     * Returns the unique {@link java.nio.channels.FileChannel FileChannel}
     * object associated with this file.
     *
     * <p> The {@link java.nio.channels.FileChannel#position()
     * position} of the returned channel will always be equal to
     * this object's file-pointer offset as returned by the {@link
     * #getFilePointer getFilePointer} method.  Changing this object's
     * file-pointer offset, whether explicitly or by reading or writing bytes,
     * will change the position of the channel, and vice versa.  Changing the
     * file's length via this object will change the length seen via the file
     * channel, and vice versa.
     * 1.返回与此文件关联的唯一FileChannel对象。
     * 2.返回的通道的position将始终等于getFilePointer方法返回的该对象的文件指针偏移量。
     * 更改此对象的文件指针偏移量，无论是显式还是通过读取或写入字节，都将更改通道的位置，反之亦然。
     * 3.通过此对象更改文件的长度将更改通过文件通道看到的长度，反之亦然
     * @return  the file channel associated with this file
     *
     * @since 1.4
     * @spec JSR-51
     */
    public final FileChannel getChannel() {
        synchronized (this) {
            if (channel == null) {
                channel = FileChannelImpl.open(fd, path, true, rw, this);
            }
            return channel;
        }
    }

    /**
     * Opens a file and returns the file descriptor.  The file is
     * opened in read-write mode if the O_RDWR bit in {@code mode}
     * is true, else the file is opened as read-only.
     * If the {@code name} refers to a directory, an IOException
     * is thrown.
     * 1.打开一个文件并返回文件描述符。 如果模式中的 O_RDWR 位为真，则文件以读写mode打开，否则文件以只读方式打开。
     * 2.如果name引用目录，则抛出 IOException
     * @param name the name of the file
     * @param mode the mode flags, a combination of the O_ constants
     *             defined above
     */
    private native void open0(String name, int mode)
        throws FileNotFoundException;

    // wrap native call to allow instrumentation
    /**
     * Opens a file and returns the file descriptor.  The file is
     * opened in read-write mode if the O_RDWR bit in {@code mode}
     * is true, else the file is opened as read-only.
     * If the {@code name} refers to a directory, an IOException
     * is thrown.
     * 打开一个文件并返回文件描述符。 如果模式中的 O_RDWR 位为真，则文件以读写mode打开，否则文件以只读方式打开。
     * 如果name引用目录，则抛出 IOException
     * @param name the name of the file
     * @param mode the mode flags, a combination of the O_ constants
     *             defined above
     */
    private void open(String name, int mode)
        throws FileNotFoundException {
        open0(name, mode);
    }

    // 'Read' primitives

    /**
     * Reads a byte of data from this file. The byte is returned as an
     * integer in the range 0 to 255 ({@code 0x00-0x0ff}). This
     * method blocks if no input is yet available.
     * <p>
     * Although {@code RandomAccessFile} is not a subclass of
     * {@code InputStream}, this method behaves in exactly the same
     * way as the {@link InputStream#read()} method of
     * {@code InputStream}.
     * 从此文件中读取一个字节的数据。 该字节作为 0 到 255 ( 0x00-0x0ff ) 范围内的整数返回。
     * 如果尚无可用输入，则此方法会阻塞。
     * 虽然RandomAccessFile不是的一个子类InputStream ，
     * 以完全相同的方式与此方法的行为InputStream.read()的方法InputStream 。
     * @return     the next byte of data, or {@code -1} if the end of the
     *             file has been reached.
     * @exception  IOException  if an I/O error occurs. Not thrown if
     *                          end-of-file has been reached.
     */
    public int read() throws IOException {
        return read0();
    }

    private native int read0() throws IOException;

    /**
     * Reads a sub array as a sequence of bytes.
     * 将子数组作为字节序列读取。
     * @param b the buffer into which the data is read.
     * @param off the start offset of the data.
     * @param len the number of bytes to read.
     * @exception IOException If an I/O error has occurred.
     */
    private native int readBytes(byte b[], int off, int len) throws IOException;

    /**
     * Reads up to {@code len} bytes of data from this file into an
     * array of bytes. This method blocks until at least one byte of input
     * is available.
     * <p>
     * Although {@code RandomAccessFile} is not a subclass of
     * {@code InputStream}, this method behaves in exactly the
     * same way as the {@link InputStream#read(byte[], int, int)} method of
     * {@code InputStream}.
     * 从此文件中读取最多len个字节的数据到一个字节数组中。 此方法会阻塞，直到至少有一个字节的输入可用。
     * 虽然RandomAccessFile不是的一个子类InputStream ，
     * 以完全相同的方式与此方法的行为InputStream.read(byte[], int, int)的方法InputStream
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in array {@code b}
     *                   at which the data is written.
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             {@code -1} if there is no more data because the end of
     *             the file has been reached.
     * @exception  IOException If the first byte cannot be read for any reason
     * other than end of file, or if the random access file has been closed, or if
     * some other I/O error occurs.
     * @exception  NullPointerException If {@code b} is {@code null}.
     * @exception  IndexOutOfBoundsException If {@code off} is negative,
     * {@code len} is negative, or {@code len} is greater than
     * {@code b.length - off}
     */
    public int read(byte b[], int off, int len) throws IOException {
        return readBytes(b, off, len);
    }

    /**
     * Reads up to {@code b.length} bytes of data from this file
     * into an array of bytes. This method blocks until at least one byte
     * of input is available.
     * <p>
     * Although {@code RandomAccessFile} is not a subclass of
     * {@code InputStream}, this method behaves in exactly the
     * same way as the {@link InputStream#read(byte[])} method of
     * {@code InputStream}.
     * 从此文件中读取最多b.length个字节的数据到一个字节数组中。 此方法会阻塞，直到至少有一个字节的输入可用。
     * 虽然RandomAccessFile不是的一个子类InputStream ，
     * 以完全相同的方式与此方法的行为InputStream.read(byte[])的方法InputStream 。
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             {@code -1} if there is no more data because the end of
     *             this file has been reached.
     * @exception  IOException If the first byte cannot be read for any reason
     * other than end of file, or if the random access file has been closed, or if
     * some other I/O error occurs.
     * @exception  NullPointerException If {@code b} is {@code null}.
     */
    public int read(byte b[]) throws IOException {
        return readBytes(b, 0, b.length);
    }

    /**
     * Reads {@code b.length} bytes from this file into the byte
     * array, starting at the current file pointer. This method reads
     * repeatedly from the file until the requested number of bytes are
     * read. This method blocks until the requested number of bytes are
     * read, the end of the stream is detected, or an exception is thrown.
     * 从此文件中读取b.length个字节到字节数组中，从当前文件指针开始。 此方法从文件中重复读取，直到读取了请求的字节数。
     * 此方法会阻塞，直到读取了请求的字节数、检测到流的结尾或抛出异常。
     * @param      b   the buffer into which the data is read.
     * @exception  EOFException  if this file reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public final void readFully(byte b[]) throws IOException {
        readFully(b, 0, b.length);
    }

    /**
     * Reads exactly {@code len} bytes from this file into the byte
     * array, starting at the current file pointer. This method reads
     * repeatedly from the file until the requested number of bytes are
     * read. This method blocks until the requested number of bytes are
     * read, the end of the stream is detected, or an exception is thrown.
     * 从当前文件指针开始，从该文件中准确读取len个字节到字节数组中。 此方法从文件中重复读取，直到读取了请求的字节数。
     * 此方法会阻塞，直到读取了请求的字节数、检测到流的结尾或抛出异常
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset of the data.
     * @param      len   the number of bytes to read.
     * @exception  EOFException  if this file reaches the end before reading
     *               all the bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public final void readFully(byte b[], int off, int len) throws IOException {
        int n = 0;
        do {
            int count = this.read(b, off + n, len - n);
            if (count < 0)
                throw new EOFException();
            n += count;
        } while (n < len);
    }

    /**
     * Attempts to skip over {@code n} bytes of input discarding the
     * skipped bytes.
     * <p>
     *
     * This method may skip over some smaller number of bytes, possibly zero.
     * This may result from any of a number of conditions; reaching end of
     * file before {@code n} bytes have been skipped is only one
     * possibility. This method never throws an {@code EOFException}.
     * The actual number of bytes skipped is returned.  If {@code n}
     * is negative, no bytes are skipped.
     * 尝试跳过n字节的输入并丢弃跳过的字节。
     * 此方法可能会跳过一些较小的字节数，可能为零。 这可能由多种情况中的任何一种引起；
     * 在跳过n个字节之前到达文件末尾只是一种可能性。
     * 此方法从不抛出EOFException 。 返回实际跳过的字节数。 如果n为负，则不跳过任何字节
     * @param      n   the number of bytes to be skipped.
     * @return     the actual number of bytes skipped.
     * @exception  IOException  if an I/O error occurs.
     */
    public int skipBytes(int n) throws IOException {
        long pos;
        long len;
        long newpos;

        if (n <= 0) {
            return 0;
        }
        pos = getFilePointer();
        len = length();
        newpos = pos + n;
        if (newpos > len) {
            newpos = len;
        }
        seek(newpos);

        /* return the actual number of bytes skipped */
        return (int) (newpos - pos);
    }

    // 'Write' primitives

    /**
     * Writes the specified byte to this file. The write starts at
     * the current file pointer.
     * 将指定的字节写入此文件。 写入从当前文件指针开始。
     * @param      b   the {@code byte} to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public void write(int b) throws IOException {
        write0(b);
    }

    private native void write0(int b) throws IOException;

    /**
     * Writes a sub array as a sequence of bytes.
     * @param b the data to be written
    将子数组写入为字节序列。
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    private native void writeBytes(byte b[], int off, int len) throws IOException;

    /**
     * Writes {@code b.length} bytes from the specified byte array
     * to this file, starting at the current file pointer.
     * 从当前文件指针开始，将指定字节数组中的b.length个字节写入此文件。
     * @param      b   the data.
     * @exception  IOException  if an I/O error occurs.
     */
    public void write(byte b[]) throws IOException {
        writeBytes(b, 0, b.length);
    }

    /**
     * Writes {@code len} bytes from the specified byte array
     * starting at offset {@code off} to this file.
     * 将指定字节数组中的len个字节从 offset off写入此文件。
     * @param      b     the data.
     * @param      off   the start offset in the data.
     * @param      len   the number of bytes to write.
     * @exception  IOException  if an I/O error occurs.
     */
    public void write(byte b[], int off, int len) throws IOException {
        writeBytes(b, off, len);
    }

    // 'Random access' stuff

    /**
     * Returns the current offset in this file.
     * 返回此文件中的当前偏移量。
     * @return     the offset from the beginning of the file, in bytes,
     *             at which the next read or write occurs.
     * @exception  IOException  if an I/O error occurs.
     */
    public native long getFilePointer() throws IOException;

    /**
     * Sets the file-pointer offset, measured from the beginning of this
     * file, at which the next read or write occurs.  The offset may be
     * set beyond the end of the file. Setting the offset beyond the end
     * of the file does not change the file length.  The file length will
     * change only by writing after the offset has been set beyond the end
     * of the file.
     * 设置文件指针偏移量，从该文件的开头开始测量，在该位置发生下一次读取或写入。 偏移量可能设置在文件末尾之外。
     * 设置超出文件末尾的偏移量不会更改文件长度。 只有在将偏移量设置为超出文件末尾之后，文件长度才会更改。
     * @param      pos   the offset position, measured in bytes from the
     *                   beginning of the file, at which to set the file
     *                   pointer.
     * @exception  IOException  if {@code pos} is less than
     *                          {@code 0} or if an I/O error occurs.
     */
    public void seek(long pos) throws IOException {
        if (pos < 0) {
            throw new IOException("Negative seek offset");
        } else {
            seek0(pos);
        }
    }

    private native void seek0(long pos) throws IOException;

    /**
     * Returns the length of this file.
     * 返回此文件的长度。
     * @return     the length of this file, measured in bytes.
     * @exception  IOException  if an I/O error occurs.
     */
    public native long length() throws IOException;

    /**
     * Sets the length of this file.
     *
     * <p> If the present length of the file as returned by the
     * {@code length} method is greater than the {@code newLength}
     * argument then the file will be truncated.  In this case, if the file
     * offset as returned by the {@code getFilePointer} method is greater
     * than {@code newLength} then after this method returns the offset
     * will be equal to {@code newLength}.
     *
     * <p> If the present length of the file as returned by the
     * {@code length} method is smaller than the {@code newLength}
     * argument then the file will be extended.  In this case, the contents of
     * the extended portion of the file are not defined.
     * 1.设置此文件的长度。
     * 2.如果length方法返回的文件的当前长度大于newLength参数，则文件将被截断。
     * 在这种情况下，如果getFilePointer方法返回的文件偏移量大于newLength则在此方法返回后偏移量将等于newLength 。
     * 3.如果length方法返回的文件的当前长度小于newLength参数，则文件将被扩展。 在这种情况下，未定义文件扩展部分的内容。
     * @param      newLength    The desired length of the file
     * @exception  IOException  If an I/O error occurs
     * @since      1.2
     */
    public native void setLength(long newLength) throws IOException;

    /**
     * Closes this random access file stream and releases any system
     * resources associated with the stream. A closed random access
     * file cannot perform input or output operations and cannot be
     * reopened.
     *
     * <p> If this file has an associated channel then the channel is closed
     * as well.
     * 关闭此随机访问文件流并释放与该流关联的所有系统资源。 关闭的随机访问文件不能执行输入或输出操作，也不能重新打开。
     * 如果此文件具有关联的频道，则该频道也将关闭。
     * @exception  IOException  if an I/O error occurs.
     *
     * @revised 1.4
     * @spec JSR-51
     */
    public void close() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        if (channel != null) {
            channel.close();
        }

        fd.closeAll(new Closeable() {
            public void close() throws IOException {
               close0();
           }
        });
    }

    //
    //  Some "reading/writing Java data types" methods stolen from
    //  DataInputStream and DataOutputStream.
    //

    /**
     * Reads a {@code boolean} from this file. This method reads a
     * single byte from the file, starting at the current file pointer.
     * A value of {@code 0} represents
     * {@code false}. Any other value represents {@code true}.
     * This method blocks until the byte is read, the end of the stream
     * is detected, or an exception is thrown.
     * 从此文件中读取一个boolean 。 此方法从文件中读取单个字节，从当前文件指针开始。 值0表示false 。
     * 任何其他值都表示true 。 此方法会阻塞，直到读取字节、检测到流的结尾或抛出异常。
     * @return     the {@code boolean} value read.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     */
    public final boolean readBoolean() throws IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return (ch != 0);
    }

    /**
     * Reads a signed eight-bit value from this file. This method reads a
     * byte from the file, starting from the current file pointer.
     * If the byte read is {@code b}, where
     * <code>0&nbsp;&lt;=&nbsp;b&nbsp;&lt;=&nbsp;255</code>,
     * then the result is:
     * <blockquote><pre>
     *     (byte)(b)
     * </pre></blockquote>
     * <p>
     * This method blocks until the byte is read, the end of the stream
     * is detected, or an exception is thrown.
     * 从此文件中读取一个有符号的八位值。 此方法从文件中读取一个字节，从当前文件指针开始。
     * 如果读取的字节为b ，其中0 <= b <= 255 ，则结果为：
     *            (byte)(b)
     *
     * 此方法会阻塞，直到读取字节、检测到流的结尾或抛出异常
     * @return     the next byte of this file as a signed eight-bit
     *             {@code byte}.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     */
    public final byte readByte() throws IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    /**
     * Reads an unsigned eight-bit number from this file. This method reads
     * a byte from this file, starting at the current file pointer,
     * and returns that byte.
     * <p>
     * This method blocks until the byte is read, the end of the stream
     * is detected, or an exception is thrown.
     * 从此文件中读取一个无符号的八位数字。 此方法从此文件读取一个字节，从当前文件指针开始，并返回该字节。
     * 此方法会阻塞，直到读取字节、检测到流的结尾或抛出异常
     * @return     the next byte of this file, interpreted as an unsigned
     *             eight-bit number.
     * @exception  EOFException  if this file has reached the end.
     * @exception  IOException   if an I/O error occurs.
     */
    public final int readUnsignedByte() throws IOException {
        int ch = this.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    /**
     * Reads a signed 16-bit number from this file. The method reads two
     * bytes from this file, starting at the current file pointer.
     * If the two bytes read, in order, are
     * {@code b1} and {@code b2}, where each of the two values is
     * between {@code 0} and {@code 255}, inclusive, then the
     * result is equal to:
     * <blockquote><pre>
     *     (short)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     * 从此文件中读取一个带符号的 16 位数字。 该方法从此文件读取两个字节，从当前文件指针开始。
     * 如果按顺序读取的两个字节是b1和b2 ，其中两个值中的每一个都在0和255之间（包括0和255 ），则结果等于：
     *            (short)((b1 << 8) | b2)
     *
     * 此方法会阻塞，直到读取了两个字节、检测到流的结尾或抛出异常。
     * @return     the next two bytes of this file, interpreted as a signed
     *             16-bit number.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public final short readShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads an unsigned 16-bit number from this file. This method reads
     * two bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 8) | b2
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *从此文件中读取一个无符号的 16 位数字。 此方法从文件中读取两个字节，从当前文件指针开始。
     * 如果按顺序读取的字节为b1和b2 ，其中0 <= b1, b2 <= 255 ，则结果等于：
     *            (b1 << 8) | b2
     *
     * 此方法会阻塞，直到读取了两个字节、检测到流的结尾或抛出异常
     * @return     the next two bytes of this file, interpreted as an unsigned
     *             16-bit integer.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public final int readUnsignedShort() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }

    /**
     * Reads a character from this file. This method reads two
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1} and {@code b2}, where
     * <code>0&nbsp;&lt;=&nbsp;b1,&nbsp;b2&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (char)((b1 &lt;&lt; 8) | b2)
     * </pre></blockquote>
     * <p>
     * This method blocks until the two bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     * 从此文件中读取一个字符。 此方法从文件中读取两个字节，从当前文件指针开始。
     * 如果按顺序读取的字节为b1和b2 ，其中0 <= b1, b2 <= 255 ，则结果等于：
     *            (char)((b1 << 8) | b2)
     *
     * 此方法会阻塞，直到读取了两个字节、检测到流的结尾或抛出异常。
     * @return     the next two bytes of this file, interpreted as a
     *                  {@code char}.
     * @exception  EOFException  if this file reaches the end before reading
     *               two bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public final char readChar() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch1 << 8) + (ch2 << 0));
    }

    /**
     * Reads a signed 32-bit integer from this file. This method reads 4
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are {@code b1},
     * {@code b2}, {@code b3}, and {@code b4}, where
     * <code>0&nbsp;&lt;=&nbsp;b1, b2, b3, b4&nbsp;&lt;=&nbsp;255</code>,
     * then the result is equal to:
     * <blockquote><pre>
     *     (b1 &lt;&lt; 24) | (b2 &lt;&lt; 16) + (b3 &lt;&lt; 8) + b4
     * </pre></blockquote>
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *从此文件中读取一个有符号的 32 位整数。 此方法从文件中读取 4 个字节，从当前文件指针开始。
     *  如果读取的字节依次为b1 、 b2 、 b3和b4 ，其中0 <= b1, b2, b3, b4 <= 255 ，则结果等于：
     *            (b1 << 24) | (b2 << 16) + (b3 << 8) + b4
     *
     * 此方法会阻塞，直到读取了四个字节、检测到流的结尾或抛出异常。
     * @return     the next four bytes of this file, interpreted as an
     *             {@code int}.
     * @exception  EOFException  if this file reaches the end before reading
     *               four bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public final int readInt() throws IOException {
        int ch1 = this.read();
        int ch2 = this.read();
        int ch3 = this.read();
        int ch4 = this.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    /**
     * Reads a signed 64-bit integer from this file. This method reads eight
     * bytes from the file, starting at the current file pointer.
     * If the bytes read, in order, are
     * {@code b1}, {@code b2}, {@code b3},
     * {@code b4}, {@code b5}, {@code b6},
     * {@code b7}, and {@code b8,} where:
     * <blockquote><pre>
     *     0 &lt;= b1, b2, b3, b4, b5, b6, b7, b8 &lt;=255,
     * </pre></blockquote>
     * <p>
     * then the result is equal to:
     * <blockquote><pre>
     *     ((long)b1 &lt;&lt; 56) + ((long)b2 &lt;&lt; 48)
     *     + ((long)b3 &lt;&lt; 40) + ((long)b4 &lt;&lt; 32)
     *     + ((long)b5 &lt;&lt; 24) + ((long)b6 &lt;&lt; 16)
     *     + ((long)b7 &lt;&lt; 8) + b8
     * </pre></blockquote>
     * <p>
     * This method blocks until the eight bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     * 从此文件中读取一个有符号的 64 位整数。 此方法从文件中读取八个字节，从当前文件指针开始。
     * 如果读取的字节依次为b1 、 b2 、 b3 、 b4 、 b5 、 b6 、 b7和b8,其中：
     *            0 <= b1, b2, b3, b4, b5, b6, b7, b8 <=255,
     *
     * 那么结果等于：
     *            ((long)b1 << 56) + ((long)b2 << 48)
     *            + ((long)b3 << 40) + ((long)b4 << 32)
     *            + ((long)b5 << 24) + ((long)b6 << 16)
     *            + ((long)b7 << 8) + b8
     *
     * 此方法会阻塞，直到读取了 8 个字节、检测到流的结尾或抛出异常。
     * @return     the next eight bytes of this file, interpreted as a
     *             {@code long}.
     * @exception  EOFException  if this file reaches the end before reading
     *               eight bytes.
     * @exception  IOException   if an I/O error occurs.
     */
    public final long readLong() throws IOException {
        return ((long)(readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
    }

    /**
     * Reads a {@code float} from this file. This method reads an
     * {@code int} value, starting at the current file pointer,
     * as if by the {@code readInt} method
     * and then converts that {@code int} to a {@code float}
     * using the {@code intBitsToFloat} method in class
     * {@code Float}.
     * <p>
     * This method blocks until the four bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     * 从此文件中读取float 。 此方法读取的int值，并从当前文件指针，仿佛通过readInt方法，
     * 然后，其将int到float使用intBitsToFloat在类方法Float 。
     * 此方法会阻塞，直到读取了四个字节、检测到流的结尾或抛出异常。
     * @return     the next four bytes of this file, interpreted as a
     *             {@code float}.
     * @exception  EOFException  if this file reaches the end before reading
     *             four bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.RandomAccessFile#readInt()
     * @see        java.lang.Float#intBitsToFloat(int)
     */
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    /**
     * Reads a {@code double} from this file. This method reads a
     * {@code long} value, starting at the current file pointer,
     * as if by the {@code readLong} method
     * and then converts that {@code long} to a {@code double}
     * using the {@code longBitsToDouble} method in
     * class {@code Double}.
     * <p>
     * This method blocks until the eight bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     * 从此文件中读取double精度值。 此方法读取一个long值，从当前文件指针开始，
     * 就像通过readLong方法一样，然后使用Double类中的longBitsToDouble方法将该long转换为double 。
     * 此方法会阻塞，直到读取了 8 个字节、检测到流的结尾或抛出异常。
     * @return     the next eight bytes of this file, interpreted as a
     *             {@code double}.
     * @exception  EOFException  if this file reaches the end before reading
     *             eight bytes.
     * @exception  IOException   if an I/O error occurs.
     * @see        java.io.RandomAccessFile#readLong()
     * @see        java.lang.Double#longBitsToDouble(long)
     */
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    /**
     * Reads the next line of text from this file.  This method successively
     * reads bytes from the file, starting at the current file pointer,
     * until it reaches a line terminator or the end
     * of the file.  Each byte is converted into a character by taking the
     * byte's value for the lower eight bits of the character and setting the
     * high eight bits of the character to zero.  This method does not,
     * therefore, support the full Unicode character set.
     *
     * <p> A line of text is terminated by a carriage-return character
     * ({@code '\u005Cr'}), a newline character ({@code '\u005Cn'}), a
     * carriage-return character immediately followed by a newline character,
     * or the end of the file.  Line-terminating characters are discarded and
     * are not included as part of the string returned.
     *
     * <p> This method blocks until a newline character is read, a carriage
     * return and the byte following it are read (to see if it is a newline),
     * the end of the file is reached, or an exception is thrown.
     *1.从此文件中读取下一行文本。 此方法从文件中连续读取字节，从当前文件指针开始，直到到达行终止符或文件末尾。
     * 2.通过取字符低八位的字节值并将字符的高八位设置为零，将每个字节转换为字符。 因此，此方法不支持完整的 Unicode 字符集。
     * 3.一行文本以回车符 ( '\r' )、换行符 ( '\n' )、紧跟换行符的回车符或文件结尾结束。
     * 行终止字符被丢弃并且不作为返回字符串的一部分包含在内。
     * 4.此方法会阻塞，直到读取换行符、读取回车符及其后的字节（以查看它是否为换行符）、到达文件末尾或引发异常
     * @return     the next line of text from this file, or null if end
     *             of file is encountered before even one byte is read.
     * @exception  IOException  if an I/O error occurs.
     */

    public final String readLine() throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        boolean eol = false;

        while (!eol) {
            switch (c = read()) {
            case -1:
            case '\n':
                eol = true;
                break;
            case '\r':
                eol = true;
                long cur = getFilePointer();
                if ((read()) != '\n') {
                    seek(cur);
                }
                break;
            default:
                input.append((char)c);
                break;
            }
        }

        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }

    /**
     * Reads in a string from this file. The string has been encoded
     * using a
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * format.
     * <p>
     * The first two bytes are read, starting from the current file
     * pointer, as if by
     * {@code readUnsignedShort}. This value gives the number of
     * following bytes that are in the encoded string, not
     * the length of the resulting string. The following bytes are then
     * interpreted as bytes encoding characters in the modified UTF-8 format
     * and are converted into characters.
     * <p>
     * This method blocks until all the bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     * 1.从此文件中读入一个字符串。 该字符串已使用修改后的 UTF-8格式进行编码。
     * 2.从当前文件指针开始读取前两个字节，就像readUnsignedShort 。
     * 3.该值给出了编码字符串中的后续字节数，而不是结果字符串的长度。
     * 然后将以下字节解释为修改后的 UTF-8 格式中的字节编码字符并转换为字符。
     * 此方法会阻塞，直到读取所有字节、检测到流的结尾或引发异常
     * @return     a Unicode string.
     * @exception  EOFException            if this file reaches the end before
     *               reading all the bytes.
     * @exception  IOException             if an I/O error occurs.
     * @exception  UTFDataFormatException  if the bytes do not represent
     *               valid modified UTF-8 encoding of a Unicode string.
     * @see        java.io.RandomAccessFile#readUnsignedShort()
     */
    public final String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    /**
     * Writes a {@code boolean} to the file as a one-byte value. The
     * value {@code true} is written out as the value
     * {@code (byte)1}; the value {@code false} is written out
     * as the value {@code (byte)0}. The write starts at
     * the current position of the file pointer.
     * 将boolean作为一字节值写入文件。 值true作为值(byte)1写出； 值false作为值(byte)0写出。 写入从文件指针的当前位置开始
     * @param      v   a {@code boolean} value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public final void writeBoolean(boolean v) throws IOException {
        write(v ? 1 : 0);
        //written++;
    }

    /**
     * Writes a {@code byte} to the file as a one-byte value. The
     * write starts at the current position of the file pointer.
     * 将一个byte作为一字节值写入文件。 写入从文件指针的当前位置开始。
     * @param      v   a {@code byte} value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public final void writeByte(int v) throws IOException {
        write(v);
        //written++;
    }

    /**
     * Writes a {@code short} to the file as two bytes, high byte first.
     * The write starts at the current position of the file pointer.
     * 以两个字节的形式将short写入文件，高字节在前。 写入从文件指针的当前位置开始
     * @param      v   a {@code short} to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public final void writeShort(int v) throws IOException {
        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);
        //written += 2;
    }

    /**
     * Writes a {@code char} to the file as a two-byte value, high
     * byte first. The write starts at the current position of the
     * file pointer.
     * 将一个char作为两字节值写入文件，高字节在前。 写入从文件指针的当前位置开始
     * @param      v   a {@code char} value to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public final void writeChar(int v) throws IOException {
        write((v >>> 8) & 0xFF);
        write((v >>> 0) & 0xFF);
        //written += 2;
    }

    /**
     * Writes an {@code int} to the file as four bytes, high byte first.
     * The write starts at the current position of the file pointer.
     * 将int作为四个字节写入文件，高字节在前。 写入从文件指针的当前位置开始。
     * @param      v   an {@code int} to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public final void writeInt(int v) throws IOException {
        write((v >>> 24) & 0xFF);
        write((v >>> 16) & 0xFF);
        write((v >>>  8) & 0xFF);
        write((v >>>  0) & 0xFF);
        //written += 4;
    }

    /**
     * Writes a {@code long} to the file as eight bytes, high byte first.
     * The write starts at the current position of the file pointer.
     * 将long写入文件为 8 个字节，高字节在前。 写入从文件指针的当前位置开始。
     * @param      v   a {@code long} to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public final void writeLong(long v) throws IOException {
        write((int)(v >>> 56) & 0xFF);
        write((int)(v >>> 48) & 0xFF);
        write((int)(v >>> 40) & 0xFF);
        write((int)(v >>> 32) & 0xFF);
        write((int)(v >>> 24) & 0xFF);
        write((int)(v >>> 16) & 0xFF);
        write((int)(v >>>  8) & 0xFF);
        write((int)(v >>>  0) & 0xFF);
        //written += 8;
    }

    /**
     * Converts the float argument to an {@code int} using the
     * {@code floatToIntBits} method in class {@code Float},
     * and then writes that {@code int} value to the file as a
     * four-byte quantity, high byte first. The write starts at the
     * current position of the file pointer.
     * 使用Float类中的floatToIntBits方法将 float 参数转换为int ，
     * 然后将该int值作为四字节数量写入文件，高字节在前。 写入从文件指针的当前位置开始。
     * @param      v   a {@code float} value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.lang.Float#floatToIntBits(float)
     */
    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    /**
     * Converts the double argument to a {@code long} using the
     * {@code doubleToLongBits} method in class {@code Double},
     * and then writes that {@code long} value to the file as an
     * eight-byte quantity, high byte first. The write starts at the current
     * position of the file pointer.
     * 使用Double类中的doubleToLongBits方法将 double 参数转换为long ，
     * 然后将该long值作为八字节数量写入文件，高字节在前。 写入从文件指针的当前位置开始。
     * @param      v   a {@code double} value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.lang.Double#doubleToLongBits(double)
     */
    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    /**
     * Writes the string to the file as a sequence of bytes. Each
     * character in the string is written out, in sequence, by discarding
     * its high eight bits. The write starts at the current position of
     * the file pointer.
     * 将字符串作为字节序列写入文件。 通过丢弃其高八位，按顺序写出字符串中的每个字符。 写入从文件指针的当前位置开始
     * @param      s   a string of bytes to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    @SuppressWarnings("deprecation")
    public final void writeBytes(String s) throws IOException {
        int len = s.length();
        byte[] b = new byte[len];
        s.getBytes(0, len, b, 0);
        writeBytes(b, 0, len);
    }

    /**
     * Writes a string to the file as a sequence of characters. Each
     * character is written to the data output stream as if by the
     * {@code writeChar} method. The write starts at the current
     * position of the file pointer.
     * 将字符串作为字符序列写入文件。 每个字符都被写入数据输出流，就像通过writeChar方法一样。
     * 写入从文件指针的当前位置开始
     * @param      s   a {@code String} value to be written.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.RandomAccessFile#writeChar(int)
     */
    public final void writeChars(String s) throws IOException {
        int clen = s.length();
        int blen = 2*clen;
        byte[] b = new byte[blen];
        char[] c = new char[clen];
        s.getChars(0, clen, c, 0);
        for (int i = 0, j = 0; i < clen; i++) {
            b[j++] = (byte)(c[i] >>> 8);
            b[j++] = (byte)(c[i] >>> 0);
        }
        writeBytes(b, 0, blen);
    }

    /**
     * Writes a string to the file using
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * encoding in a machine-independent manner.
     * <p>
     * First, two bytes are written to the file, starting at the
     * current file pointer, as if by the
     * {@code writeShort} method giving the number of bytes to
     * follow. This value is the number of bytes actually written out,
     * not the length of the string. Following the length, each character
     * of the string is output, in sequence, using the modified UTF-8 encoding
     * for each character.
     * 以独立于机器的方式使用修改后的 UTF-8编码将字符串写入文件。
     * 首先，从当前文件指针开始，将两个字节写入文件，就像通过writeShort方法给出要跟随的字节数一样。
     * 该值是实际写出的字节数，而不是字符串的长度。 按照长度，字符串的每个字符按顺序输出，对每个字符使用修改后的 UTF-8 编码。
     * @param      str   a string to be written.
     * @exception  IOException  if an I/O error occurs.
     */
    public final void writeUTF(String str) throws IOException {
        DataOutputStream.writeUTF(str, this);
    }

    private static native void initIDs();

    private native void close0() throws IOException;

    static {
        initIDs();
    }
}
