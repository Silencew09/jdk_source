/*
 * Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to create operating system processes.
 * 1.此类用于创建操作系统进程。
 * <p>Each {@code ProcessBuilder} instance manages a collection
 * of process attributes.  The {@link #start()} method creates a new
 * {@link Process} instance with those attributes.  The {@link
 * #start()} method can be invoked repeatedly from the same instance
 * to create new subprocesses with identical or related attributes.
 * 2.每个 ProcessBuilder实例管理一组流程属性。start()方法使用这些属性创建一个新的Process实例。
 * 可以从同一实例重复调用start()方法以创建具有相同或相关属性的新子流程。
 * <p>Each process builder manages these process attributes:
 *3.每个流程构建器管理这些流程属性：
 * 一个命令,表示要调用的外部程序文件及其参数（如果有）的字符串列表。哪些字符串列表表示有效的操作系统命令取决于系统。
 * 例如，每个概念参数都是这个列表中的一个元素是很常见的，但是在某些操作系统中，程序需要自己标记命令行字符串——在这样的系统上，
 * Java 实现可能需要命令恰好包含两个元素
 * <ul>
 *
 * <li>a <i>command</i>, a list of strings which signifies the
 * external program file to be invoked and its arguments, if any.
 * Which string lists represent a valid operating system command is
 * system-dependent.  For example, it is common for each conceptual
 * argument to be an element in this list, but there are operating
 * systems where programs are expected to tokenize command line
 * strings themselves - on such a system a Java implementation might
 * require commands to contain exactly two elements.
 *
 * <li>an <i>environment</i>, which is a system-dependent mapping from
 * <i>variables</i> to <i>values</i>.  The initial value is a copy of
 * the environment of the current process (see {@link System#getenv()}).
 * 4.一个环境，它是从变量到值的依赖于系统的映射。初始值是当前进程环境的副本（参见System.getenv()）
 * <li>a <i>working directory</i>.  The default value is the current
 * working directory of the current process, usually the directory
 * named by the system property {@code user.dir}.
 * 5.工作目录。默认值是当前进程的当前工作目录，通常是系统属性user.dir命名的目录
 * <li><a name="redirect-input">a source of <i>standard input</i></a>.
 * By default, the subprocess reads input from a pipe.  Java code
 * can access this pipe via the output stream returned by
 * {@link Process#getOutputStream()}.  However, standard input may
 * be redirected to another source using
 * {@link #redirectInput(Redirect) redirectInput}.
 * 5.标准输入的来源。默认情况下，子进程从管道读取输入。
 * Java 代码可以通过 Process.getOutputStream()返回的输出流访问此管道。
 * 但是，标准输入可以使用 redirectInput(Redirect)重定向到另一个源
 * In this case, {@link Process#getOutputStream()} will return a
 * <i>null output stream</i>, for which:
 *
 * <ul>
 * <li>the {@link OutputStream#write(int) write} methods always
 * throw {@code IOException}
 * <li>the {@link OutputStream#close() close} method does nothing
 * </ul>
 * 6.在这种情况下，Process.getOutputStream()将返回一个 null 输出流，举例：
 * OutputStream.write(int)方法总是抛出 IOException
 * OutputStream.close() 方法什么都不做
 * <li><a name="redirect-output">a destination for <i>standard output</i>
 * and <i>standard error</i></a>.  By default, the subprocess writes standard
 * output and standard error to pipes.  Java code can access these pipes
 * via the input streams returned by {@link Process#getInputStream()} and
 * {@link Process#getErrorStream()}.  However, standard output and
 * standard error may be redirected to other destinations using
 * {@link #redirectOutput(Redirect) redirectOutput} and
 * {@link #redirectError(Redirect) redirectError}.
 * 7.标准输出和标准错误的目的地。默认情况下，子进程将标准输出和标准错误写入管道。
 * Java 代码可以通过Process.getInputStream()和Process.getErrorStream()返回的输入流访问这些管道。
 * 但是，标准输出和标准错误可以使用redirectOutput(Redirect)和 redirectError(Redirect)重定向到其他目的地。
 * In this case, {@link Process#getInputStream()} and/or
 * {@link Process#getErrorStream()} will return a <i>null input
 * stream</i>, for which:
 *
 * <ul>
 * <li>the {@link InputStream#read() read} methods always return
 * {@code -1}
 * <li>the {@link InputStream#available() available} method always returns
 * {@code 0}
 * <li>the {@link InputStream#close() close} method does nothing
 * </ul>
 *  8.在这种情况下，Process.getInputStream()和或Process.getErrorStream()将返回一个空输入流，举例：
 *  InputStream.read()方法总是返回 -1
 *  InputStream.available()方法总是返回 0
 *  InputStream.close()方法什么都不做
 * <li>a <i>redirectErrorStream</i> property.  Initially, this property
 * is {@code false}, meaning that the standard output and error
 * output of a subprocess are sent to two separate streams, which can
 * be accessed using the {@link Process#getInputStream()} and {@link
 * Process#getErrorStream()} methods.
 * 9.重定向错误流属性。最初，这个属性是false，意味着一个子进程的标准输出和错误输出被发送到两个独立的流，
 * 可以使用 Process.getInputStream()和 Process.getErrorStream()访问方法
 * <p>If the value is set to {@code true}, then:
 * 10.如果该值设置为true，则：
 *  1)标准错误与标准输出合并并始终发送到同一目的地（这使得将错误消息与相应输出关联起来更容易）
 * <ul>
 * <li>standard error is merged with the standard output and always sent
 * to the same destination (this makes it easier to correlate error
 * messages with the corresponding output)
 * <li>the common destination of standard error and standard output can be
 * redirected using
 * {@link #redirectOutput(Redirect) redirectOutput}
 *  2)标准错误和标准输出的共同目的地可以使用redirectOutput(Redirect)重定向
 * <li>any redirection set by the
 * {@link #redirectError(Redirect) redirectError}
 * method is ignored when creating a subprocess
 * 3)创建子进程时，将忽略 redirectError(Redirect)方法设置的任何重定向
 * <li>the stream returned from {@link Process#getErrorStream()} will
 * always be a <a href="#redirect-output">null input stream</a>
 * 4)从Process.getErrorStream()返回的流将始终是空输入流
 * </ul>
 *
 * </ul>
 *
 * <p>Modifying a process builder's attributes will affect processes
 * subsequently started by that object's {@link #start()} method, but
 * will never affect previously started processes or the Java process
 * itself.
 * 11.修改进程构建器的属性将影响随后由该对象的start()方法启动的进程，但永远不会影响先前启动的进程或 Java 进程本身。
 * <p>Most error checking is performed by the {@link #start()} method.
 * It is possible to modify the state of an object so that {@link
 * #start()} will fail.  For example, setting the command attribute to
 * an empty list will not throw an exception unless {@link #start()}
 * is invoked.
 * 12.大多数错误检查由start()方法执行。可以修改对象的状态，使start()失败。
 * 例如，将 command 属性设置为空列表不会抛出异常，除非调用start()
 * <p><strong>Note that this class is not synchronized.</strong>
 * If multiple threads access a {@code ProcessBuilder} instance
 * concurrently, and at least one of the threads modifies one of the
 * attributes structurally, it <i>must</i> be synchronized externally.
 * 13.请注意，此类不是同步的。如果多个线程同时访问一个ProcessBuilder实例，
 * 并且至少有一个线程在结构上修改了其中一个属性，则它必须在外部进行同步
 * <p>Starting a new process which uses the default working directory
 * and environment is easy:
 *
 * <pre> {@code
 * Process p = new ProcessBuilder("myCommand", "myArg").start();
 * }</pre>
 *14.启动一个使用默认工作目录和环境的新进程很容易：Process p = new ProcessBuilder("myCommand", "myArg").start();
 * <p>Here is an example that starts a process with a modified working
 * directory and environment, and redirects standard output and error
 * to be appended to a log file:
 * 15.这是一个示例，该示例使用修改后的工作目录和环境启动进程，并将标准输出和错误重定向到附加到日志文件中：
 * <pre> {@code
 * ProcessBuilder pb =
 *   new ProcessBuilder("myCommand", "myArg1", "myArg2");
 * Map<String, String> env = pb.environment();
 * env.put("VAR1", "myValue");
 * env.remove("OTHERVAR");
 * env.put("VAR2", env.get("VAR1") + "suffix");
 * pb.directory(new File("myDir"));
 * File log = new File("log");
 * pb.redirectErrorStream(true);
 * pb.redirectOutput(Redirect.appendTo(log));
 * Process p = pb.start();
 * assert pb.redirectInput() == Redirect.PIPE;
 * assert pb.redirectOutput().file() == log;
 * assert p.getInputStream().read() == -1;
 * }</pre>
 *
 * <p>To start a process with an explicit set of environment
 * variables, first call {@link java.util.Map#clear() Map.clear()}
 * before adding environment variables.
 * 16.要使用一组显式环境变量启动进程，请在添加环境变量之前先调用 java.util.Map.clear()。
 * @author Martin Buchholz
 * @since 1.5
 */

public final class ProcessBuilder
{
    private List<String> command;
    private File directory;
    private Map<String,String> environment;
    private boolean redirectErrorStream;
    private Redirect[] redirects;

    /**
     * Constructs a process builder with the specified operating
     * system program and arguments.  This constructor does <i>not</i>
     * make a copy of the {@code command} list.  Subsequent
     * updates to the list will be reflected in the state of the
     * process builder.  It is not checked whether
     * {@code command} corresponds to a valid operating system
     * command.
     * 使用指定的操作系统程序和参数构造进程构建器。这个构造函数不复制command列表。
     * 对列表的后续更新将反映在流程构建器的状态中。不检查command是否对应于有效的操作系统命令。
     * @param  command the list containing the program and its arguments
     * @throws NullPointerException if the argument is null
     */
    public ProcessBuilder(List<String> command) {
        if (command == null)
            throw new NullPointerException();
        this.command = command;
    }

    /**
     * Constructs a process builder with the specified operating
     * system program and arguments.  This is a convenience
     * constructor that sets the process builder's command to a string
     * list containing the same strings as the {@code command}
     * array, in the same order.  It is not checked whether
     * {@code command} corresponds to a valid operating system
     * command.
     * 使用指定的操作系统程序和参数构造进程构建器。这是一个方便的构造函数，
     * 它将流程构建器的命令设置为一个字符串列表，该列表包含与command数组相同的字符串，顺序相同。
     * 不检查command是否对应于有效的操作系统命令
     * @param command a string array containing the program and its arguments
     */
    public ProcessBuilder(String... command) {
        this.command = new ArrayList<>(command.length);
        for (String arg : command)
            this.command.add(arg);
    }

    /**
     * Sets this process builder's operating system program and
     * arguments.  This method does <i>not</i> make a copy of the
     * {@code command} list.  Subsequent updates to the list will
     * be reflected in the state of the process builder.  It is not
     * checked whether {@code command} corresponds to a valid
     * operating system command.
     * 设置此进程构建器的操作系统程序和参数。此方法不会制作command列表的副本。
     * 对列表的后续更新将反映在流程构建器的状态中。不检查command是否对应于有效的操作系统命令。
     * @param  command the list containing the program and its arguments
     * @return this process builder
     *
     * @throws NullPointerException if the argument is null
     */
    public ProcessBuilder command(List<String> command) {
        if (command == null)
            throw new NullPointerException();
        this.command = command;
        return this;
    }

    /**
     * Sets this process builder's operating system program and
     * arguments.  This is a convenience method that sets the command
     * to a string list containing the same strings as the
     * {@code command} array, in the same order.  It is not
     * checked whether {@code command} corresponds to a valid
     * operating system command.
     * 设置此进程构建器的操作系统程序和参数。这是一种方便的方法，可将命令设置为一个字符串列表，
     * 该列表包含与 command数组相同的字符串，顺序相同。不检查command是否对应于有效的操作系统命令
     * @param  command a string array containing the program and its arguments
     * @return this process builder
     */
    public ProcessBuilder command(String... command) {
        this.command = new ArrayList<>(command.length);
        for (String arg : command)
            this.command.add(arg);
        return this;
    }

    /**
     * Returns this process builder's operating system program and
     * arguments.  The returned list is <i>not</i> a copy.  Subsequent
     * updates to the list will be reflected in the state of this
     * process builder.
     * 返回此进程构建器的操作系统程序和参数。返回的列表不是副本。对列表的后续更新将反映在此流程构建器的状态中。
     * @return this process builder's program and its arguments
     */
    public List<String> command() {
        return command;
    }

    /**
     * Returns a string map view of this process builder's environment.
     * 1.返回此流程构建器环境的字符串映射视图
     * Whenever a process builder is created, the environment is
     * initialized to a copy of the current process environment (see
     * {@link System#getenv()}).  Subprocesses subsequently started by
     * this object's {@link #start()} method will use this map as
     * their environment.
     * 2.每当创建进程构建器时，环境都会初始化为当前进程环境的副本（请参阅 System.getenv()）。
     * 随后由该对象的start()方法启动的子进程将使用该映射作为它们的环境。
     * <p>The returned object may be modified using ordinary {@link
     * java.util.Map Map} operations.  These modifications will be
     * visible to subprocesses started via the {@link #start()}
     * method.  Two {@code ProcessBuilder} instances always
     * contain independent process environments, so changes to the
     * returned map will never be reflected in any other
     * {@code ProcessBuilder} instance or the values returned by
     * {@link System#getenv System.getenv}.
     * 3.可以使用普通的java.util.Map操作修改返回的对象。这些修改对通过start()方法启动的子进程可见。
     * 两个  ProcessBuilder实例始终包含独立的进程环境，因此对返回映射的更改永远不会反映在任何其他ProcessBuilder实例
     * 或 System.getenv返回的值中。
     * <p>If the system does not support environment variables, an
     * empty map is returned.
     * 4.如果系统不支持环境变量，则返回一个空映射
     * <p>The returned map does not permit null keys or values.
     * Attempting to insert or query the presence of a null key or
     * value will throw a {@link NullPointerException}.
     * Attempting to query the presence of a key or value which is not
     * of type {@link String} will throw a {@link ClassCastException}.
     * 5.返回的映射不允许空键或值。尝试插入或查询空键或值的存在将抛出 NullPointerException。
     * 尝试查询不是 String类型的键或值的存在将抛出ClassCastException。
     * <p>The behavior of the returned map is system-dependent.  A
     * system may not allow modifications to environment variables or
     * may forbid certain variable names or values.  For this reason,
     * attempts to modify the map may fail with
     * {@link UnsupportedOperationException} or
     * {@link IllegalArgumentException}
     * if the modification is not permitted by the operating system.
     * 6.返回的地图的行为取决于系统。系统可能不允许修改环境变量或可能禁止某些变量名称或值。因此，
     * 如果操作系统不允许修改，则尝试修改地图可能会失败并显示UnsupportedOperationException或 IllegalArgumentException。
     * <p>Since the external format of environment variable names and
     * values is system-dependent, there may not be a one-to-one
     * mapping between them and Java's Unicode strings.  Nevertheless,
     * the map is implemented in such a way that environment variables
     * which are not modified by Java code will have an unmodified
     * native representation in the subprocess.
     * 7.由于环境变量名称和值的外部格式与系统相关，因此它们与 Java 的 Unicode 字符串之间可能不存在一对一的映射。
     * 尽管如此，该映射的实现方式使得未被 Java 代码修改的环境变量将在子进程中具有未修改的本机表示。
     * <p>The returned map and its collection views may not obey the
     * general contract of the {@link Object#equals} and
     * {@link Object#hashCode} methods.
     * 8.返回的地图及其集合视图可能不遵守  Object.equals和Object.hashCode方法的一般约定。
     * <p>The returned map is typically case-sensitive on all platforms.
     *
     * <p>If a security manager exists, its
     * {@link SecurityManager#checkPermission checkPermission} method
     * is called with a
     * {@link RuntimePermission}{@code ("getenv.*")} permission.
     * This may result in a {@link SecurityException} being thrown.
     * 9.如果存在安全管理器，则使用RuntimePermission("getenv.")权限调用其 SecurityManager.checkPermission方法。
     * 这可能会导致抛出SecurityException。
     * <p>When passing information to a Java subprocess,
     * <a href=System.html#EnvironmentVSSystemProperties>system properties</a>
     * are generally preferred over environment variables.
     * 10.将信息传递给 Java 子进程时，系统属性通常优于环境变量。
     * @return this process builder's environment
     *
     * @throws SecurityException
     *         if a security manager exists and its
     *         {@link SecurityManager#checkPermission checkPermission}
     *         method doesn't allow access to the process environment
     *
     * @see    Runtime#exec(String[],String[],java.io.File)
     * @see    System#getenv()
     */
    public Map<String,String> environment() {
        SecurityManager security = System.getSecurityManager();
        if (security != null)
            security.checkPermission(new RuntimePermission("getenv.*"));

        if (environment == null)
            environment = ProcessEnvironment.environment();

        assert environment != null;

        return environment;
    }

    // Only for use by Runtime.exec(...envp...)
    //仅供 Runtime.exec(...envp...) 使用
    ProcessBuilder environment(String[] envp) {
        assert environment == null;
        if (envp != null) {
            environment = ProcessEnvironment.emptyEnvironment(envp.length);
            assert environment != null;

            for (String envstring : envp) {
                // Before 1.5, we blindly passed invalid envstrings
                // to the child process.
                // We would like to throw an exception, but do not,
                // for compatibility with old broken code.

                // Silently discard any trailing junk.
                //在 1.5 之前，我们盲目地将无效的 envstrings 传递给子进程。
                // 为了与旧的损坏代码兼容，我们想抛出异常，但不要。
                // 默默地丢弃任何尾随的垃圾。
                if (envstring.indexOf((int) '\u0000') != -1)
                    envstring = envstring.replaceFirst("\u0000.*", "");

                int eqlsign =
                    envstring.indexOf('=', ProcessEnvironment.MIN_NAME_LENGTH);
                // Silently ignore envstrings lacking the required `='.
                if (eqlsign != -1)
                    environment.put(envstring.substring(0,eqlsign),
                                    envstring.substring(eqlsign+1));
            }
        }
        return this;
    }

    /**
     * Returns this process builder's working directory.
     * 1.返回此流程构建器的工作目录。
     * Subprocesses subsequently started by this object's {@link
     * #start()} method will use this as their working directory.
     * The returned value may be {@code null} -- this means to use
     * the working directory of the current Java process, usually the
     * directory named by the system property {@code user.dir},
     * as the working directory of the child process.
     * 2.随后由该对象的 start()方法启动的子进程将使用它作为它们的工作目录。
     * 返回值可能为null——表示使用当前Java进程的工作目录，通常是系统属性user.dir命名的目录，作为子进程的工作目录。
     * @return this process builder's working directory
     */
    public File directory() {
        return directory;
    }

    /**
     * Sets this process builder's working directory.
     * 1.设置此进程构建器的工作目录。
     * Subprocesses subsequently started by this object's {@link
     * #start()} method will use this as their working directory.
     * The argument may be {@code null} -- this means to use the
     * working directory of the current Java process, usually the
     * directory named by the system property {@code user.dir},
     * as the working directory of the child process.
     * 2.随后由该对象的start()方法启动的子进程将使用它作为它们的工作目录。
     * 参数可以是null——这意味着使用当前Java进程的工作目录，
     * 通常是系统属性user.dir命名的目录，作为子进程的工作目录
     * @param  directory the new working directory
     * @return this process builder
     */
    public ProcessBuilder directory(File directory) {
        this.directory = directory;
        return this;
    }

    // ---------------- I/O Redirection ----------------

    /**
     * Implements a <a href="#redirect-output">null input stream</a>.
     */
    static class NullInputStream extends InputStream {
        static final NullInputStream INSTANCE = new NullInputStream();
        private NullInputStream() {}
        public int read()      { return -1; }
        public int available() { return 0; }
    }

    /**
     * Implements a <a href="#redirect-input">null output stream</a>.
     */
    static class NullOutputStream extends OutputStream {
        static final NullOutputStream INSTANCE = new NullOutputStream();
        private NullOutputStream() {}
        public void write(int b) throws IOException {
            throw new IOException("Stream closed");
        }
    }

    /**
     * Represents a source of subprocess input or a destination of
     * subprocess output.
     * 1.表示子流程输入的来源或子流程输出的目的地。
     * Each {@code Redirect} instance is one of the following:
     * 2.每个Redirect实例都是以下之一:
     *  1)Redirect.PIPE
     *  2)Redirect.INHERIT
     *  3)从文件读取的重定向，由调用Redirect.from(File)创建
     *  4)写入文件的重定向，由调用 Redirect.to(File)创建
     *  5)附加到文件的重定向，由调用Redirect.appendTo(File)创建
     * <ul>
     * <li>the special value {@link #PIPE Redirect.PIPE}
     * <li>the special value {@link #INHERIT Redirect.INHERIT}
     * <li>a redirection to read from a file, created by an invocation of
     *     {@link Redirect#from Redirect.from(File)}
     * <li>a redirection to write to a file,  created by an invocation of
     *     {@link Redirect#to Redirect.to(File)}
     * <li>a redirection to append to a file, created by an invocation of
     *     {@link Redirect#appendTo Redirect.appendTo(File)}
     * </ul>
     *
     * <p>Each of the above categories has an associated unique
     * {@link Type Type}.
     * 3.上述每个类别都有一个相关的唯一
     * @since 1.7
     */
    public static abstract class Redirect {
        /**
         * The type of a {@link Redirect}.
         */
        public enum Type {
            /**
             * The type of {@link Redirect#PIPE Redirect.PIPE}.
             * 管道
             */
            PIPE,

            /**
             * The type of {@link Redirect#INHERIT Redirect.INHERIT}.
             * 继承
             */
            INHERIT,

            /**
             * The type of redirects returned from
             * {@link Redirect#from Redirect.from(File)}.
             * 读
             */
            READ,

            /**
             * The type of redirects returned from
             * {@link Redirect#to Redirect.to(File)}.
             * 写
             */
            WRITE,

            /**
             * The type of redirects returned from
             * {@link Redirect#appendTo Redirect.appendTo(File)}.
             * 追加
             */
            APPEND
        };

        /**
         * Returns the type of this {@code Redirect}.
         * @return the type of this {@code Redirect}
         */
        public abstract Type type();

        /**
         * Indicates that subprocess I/O will be connected to the
         * current Java process over a pipe.
         * 1.表示子进程 IO 将通过管道连接到当前的 Java 进程
         * This is the default handling of subprocess standard I/O.
         *  2.这是子流程标准 IO 的默认处理方式。
         * <p>It will always be true that
         *  <pre> {@code
         * Redirect.PIPE.file() == null &&
         * Redirect.PIPE.type() == Redirect.Type.PIPE
         * }</pre>
         */
        public static final Redirect PIPE = new Redirect() {
                public Type type() { return Type.PIPE; }
                public String toString() { return type().toString(); }};

        /**
         * Indicates that subprocess I/O source or destination will be the
         * same as those of the current process.  This is the normal
         * behavior of most operating system command interpreters (shells).
         *
         * <p>It will always be true that
         *  <pre> {@code
         * Redirect.INHERIT.file() == null &&
         * Redirect.INHERIT.type() == Redirect.Type.INHERIT
         * }</pre>
         */
        public static final Redirect INHERIT = new Redirect() {
                public Type type() { return Type.INHERIT; }
                public String toString() { return type().toString(); }};

        /**
         * Returns the {@link File} source or destination associated
         * with this redirect, or {@code null} if there is no such file.
         * 返回与此重定向关联的File源或目标，如果没有此类文件，则返回null
         * @return the file associated with this redirect,
         *         or {@code null} if there is no such file
         */
        public File file() { return null; }

        /**
         * When redirected to a destination file, indicates if the output
         * is to be written to the end of the file.
         * 当重定向到目标文件时，指示是否将输出写入文件末尾。
         */
        boolean append() {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns a redirect to read from the specified file.
         * 返回从指定文件读取的重定向
         * <p>It will always be true that
         *  <pre> {@code
         * Redirect.from(file).file() == file &&
         * Redirect.from(file).type() == Redirect.Type.READ
         * }</pre>
         *
         * @param file The {@code File} for the {@code Redirect}.
         * @throws NullPointerException if the specified file is null
         * @return a redirect to read from the specified file
         */
        public static Redirect from(final File file) {
            if (file == null)
                throw new NullPointerException();
            return new Redirect() {
                    public Type type() { return Type.READ; }
                    public File file() { return file; }
                    public String toString() {
                        return "redirect to read from file \"" + file + "\"";
                    }
                };
        }

        /**
         * Returns a redirect to write to the specified file.
         * If the specified file exists when the subprocess is started,
         * its previous contents will be discarded.
         * 返回写入指定文件的重定向。如果子进程启动时指定的文件存在，则其先前的内容将被丢弃。
         * <p>It will always be true that
         *  <pre> {@code
         * Redirect.to(file).file() == file &&
         * Redirect.to(file).type() == Redirect.Type.WRITE
         * }</pre>
         *
         * @param file The {@code File} for the {@code Redirect}.
         * @throws NullPointerException if the specified file is null
         * @return a redirect to write to the specified file
         */
        public static Redirect to(final File file) {
            if (file == null)
                throw new NullPointerException();
            return new Redirect() {
                    public Type type() { return Type.WRITE; }
                    public File file() { return file; }
                    public String toString() {
                        return "redirect to write to file \"" + file + "\"";
                    }
                    boolean append() { return false; }
                };
        }

        /**
         * Returns a redirect to append to the specified file.
         * Each write operation first advances the position to the
         * end of the file and then writes the requested data.
         * Whether the advancement of the position and the writing
         * of the data are done in a single atomic operation is
         * system-dependent and therefore unspecified.
         * 返回附加到指定文件的重定向。每次写入操作首先将位置推进到文件末尾，
         * 然后写入请求的数据。位置的推进和数据的写入是否在单个原子操作中完成取决于系统，因此未指定
         * <p>It will always be true that
         *  <pre> {@code
         * Redirect.appendTo(file).file() == file &&
         * Redirect.appendTo(file).type() == Redirect.Type.APPEND
         * }</pre>
         *
         * @param file The {@code File} for the {@code Redirect}.
         * @throws NullPointerException if the specified file is null
         * @return a redirect to append to the specified file
         */
        public static Redirect appendTo(final File file) {
            if (file == null)
                throw new NullPointerException();
            return new Redirect() {
                    public Type type() { return Type.APPEND; }
                    public File file() { return file; }
                    public String toString() {
                        return "redirect to append to file \"" + file + "\"";
                    }
                    boolean append() { return true; }
                };
        }

        /**
         * Compares the specified object with this {@code Redirect} for
         * equality.  Returns {@code true} if and only if the two
         * objects are identical or both objects are {@code Redirect}
         * instances of the same type associated with non-null equal
         * {@code File} instances.
         * 比较指定的对象与此 Redirect是否相等。当且仅当两个对象相同或两个对象都是与非 null 相等的 File
         * 实例关联的相同类型的Redirect实例时，才返回true。
         */
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (! (obj instanceof Redirect))
                return false;
            Redirect r = (Redirect) obj;
            if (r.type() != this.type())
                return false;
            assert this.file() != null;
            return this.file().equals(r.file());
        }

        /**
         * Returns a hash code value for this {@code Redirect}.
         * 返回此 Redirect的哈希代码值
         * @return a hash code value for this {@code Redirect}
         */
        public int hashCode() {
            File file = file();
            if (file == null)
                return super.hashCode();
            else
                return file.hashCode();
        }

        /**
         * No public constructors.  Clients must use predefined
         * static {@code Redirect} instances or factory methods.
         * 没有公共构造函数。客户端必须使用预定义的静态Redirect实例或工厂方法
         */
        private Redirect() {}
    }

    private Redirect[] redirects() {
        if (redirects == null)
            redirects = new Redirect[] {
                Redirect.PIPE, Redirect.PIPE, Redirect.PIPE
            };
        return redirects;
    }

    /**
     * Sets this process builder's standard input source.
     * 1.设置此流程构建器的标准输入源。
     * Subprocesses subsequently started by this object's {@link #start()}
     * method obtain their standard input from this source.
     * 2.随后由该对象的 start()方法启动的子进程从该源获得它们的标准输入
     * <p>If the source is {@link Redirect#PIPE Redirect.PIPE}
     * (the initial value), then the standard input of a
     * subprocess can be written to using the output stream
     * returned by {@link Process#getOutputStream()}.
     * If the source is set to any other value, then
     * {@link Process#getOutputStream()} will return a
     * <a href="#redirect-input">null output stream</a>.
     * 3.如果源是Redirect.PIPE}（初始值），则可以使用Process.getOutputStream()返回的输出流写入子进程的标准输入。
     * 如果源设置为任何其他值，则 Process.getOutputStream()将返回 null 输出流。
     * @param  source the new standard input source
     * @return this process builder
     * @throws IllegalArgumentException
     *         if the redirect does not correspond to a valid source
     *         of data, that is, has type
     *         {@link Redirect.Type#WRITE WRITE} or
     *         {@link Redirect.Type#APPEND APPEND}
     * @since  1.7
     */
    public ProcessBuilder redirectInput(Redirect source) {
        if (source.type() == Redirect.Type.WRITE ||
            source.type() == Redirect.Type.APPEND)
            throw new IllegalArgumentException(
                "Redirect invalid for reading: " + source);
        redirects()[0] = source;
        return this;
    }

    /**
     * Sets this process builder's standard output destination.
     * 1.设置此流程构建器的标准输出目标
     * Subprocesses subsequently started by this object's {@link #start()}
     * method send their standard output to this destination.
     * 2.随后由该对象的 start()方法启动的子进程将其标准输出发送到该目的地。
     * <p>If the destination is {@link Redirect#PIPE Redirect.PIPE}
     * (the initial value), then the standard output of a subprocess
     * can be read using the input stream returned by {@link
     * Process#getInputStream()}.
     * If the destination is set to any other value, then
     * {@link Process#getInputStream()} will return a
     * <a href="#redirect-output">null input stream</a>.
     * 3.如果目标是Redirect.PIPE}（初始值），则可以使用 Process.getInputStream()返回的输入流读取子进程的标准输出。
     * 如果目标设置为任何其他值，那么Process.getInputStream()将返回一个 空输入流。
     * @param  destination the new standard output destination
     * @return this process builder
     * @throws IllegalArgumentException
     *         if the redirect does not correspond to a valid
     *         destination of data, that is, has type
     *         {@link Redirect.Type#READ READ}
     * @since  1.7
     */
    public ProcessBuilder redirectOutput(Redirect destination) {
        if (destination.type() == Redirect.Type.READ)
            throw new IllegalArgumentException(
                "Redirect invalid for writing: " + destination);
        redirects()[1] = destination;
        return this;
    }

    /**
     * Sets this process builder's standard error destination.
     * 1.设置此流程构建器的标准错误目标。
     * Subprocesses subsequently started by this object's {@link #start()}
     * method send their standard error to this destination.
     * 2.随后由该对象的start()方法启动的子进程将其标准错误发送到该目的地
     * <p>If the destination is {@link Redirect#PIPE Redirect.PIPE}
     * (the initial value), then the error output of a subprocess
     * can be read using the input stream returned by {@link
     * Process#getErrorStream()}.
     * If the destination is set to any other value, then
     * {@link Process#getErrorStream()} will return a
     * <a href="#redirect-output">null input stream</a>.
     * 3.如果目标是Redirect.PIPE（初始值），则可以使用Process.getErrorStream()返回的输入流读取子进程的错误输出。
     * 如果目标设置为任何其他值，则 Process.getErrorStream()} 将返回空输入流。
     * <p>If the {@link #redirectErrorStream redirectErrorStream}
     * attribute has been set {@code true}, then the redirection set
     * by this method has no effect.
     * 4.如果redirectErrorStream 属性已经设置了true，那么这个方法设置的重定向没有效果。
     * @param  destination the new standard error destination
     * @return this process builder
     * @throws IllegalArgumentException
     *         if the redirect does not correspond to a valid
     *         destination of data, that is, has type
     *         {@link Redirect.Type#READ READ}
     * @since  1.7
     */
    public ProcessBuilder redirectError(Redirect destination) {
        if (destination.type() == Redirect.Type.READ)
            throw new IllegalArgumentException(
                "Redirect invalid for writing: " + destination);
        redirects()[2] = destination;
        return this;
    }

    /**
     * Sets this process builder's standard input source to a file.
     * 1.将此流程构建器的标准输入源设置为文件。
     * <p>This is a convenience method.  An invocation of the form
     * {@code redirectInput(file)}
     * behaves in exactly the same way as the invocation
     * {@link #redirectInput(Redirect) redirectInput}
     * {@code (Redirect.from(file))}.
     * 2.这是一种方便的方法。redirectInput(file)形式的调用与调用redirectInput(Redirect)
     * (Redirect.from(file))的行为方式完全相同。
     * @param  file the new standard input source
     * @return this process builder
     * @since  1.7
     */
    public ProcessBuilder redirectInput(File file) {
        return redirectInput(Redirect.from(file));
    }

    /**
     * Sets this process builder's standard output destination to a file.
     * 1.将此流程构建器的标准输出目标设置为文件
     * <p>This is a convenience method.  An invocation of the form
     * {@code redirectOutput(file)}
     * behaves in exactly the same way as the invocation
     * {@link #redirectOutput(Redirect) redirectOutput}
     * {@code (Redirect.to(file))}.
     * 2.这是一种方便的方法。redirectOutput(file)形式的调用与调用redirectOutput(Redirect)
     * (Redirect.to(file))的行为完全相同
     * @param  file the new standard output destination
     * @return this process builder
     * @since  1.7
     */
    public ProcessBuilder redirectOutput(File file) {
        return redirectOutput(Redirect.to(file));
    }

    /**
     * Sets this process builder's standard error destination to a file.
     * 1.将此流程构建器的标准错误目标设置为文件
     * <p>This is a convenience method.  An invocation of the form
     * {@code redirectError(file)}
     * behaves in exactly the same way as the invocation
     * {@link #redirectError(Redirect) redirectError}
     * {@code (Redirect.to(file))}.
     * 2.这是一种方便的方法。redirectError(file)形式的调用与调用redirectError(Redirect)
     * (Redirect.to(file))的行为方式完全相同。
     * @param  file the new standard error destination
     * @return this process builder
     * @since  1.7
     */
    public ProcessBuilder redirectError(File file) {
        return redirectError(Redirect.to(file));
    }

    /**
     * Returns this process builder's standard input source.
     * 1.返回此流程构建器的标准输入源
     * Subprocesses subsequently started by this object's {@link #start()}
     * method obtain their standard input from this source.
     * The initial value is {@link Redirect#PIPE Redirect.PIPE}.
     * 2.随后由该对象的 start()方法启动的子进程从该源获得它们的标准输入。初始值为Redirect.PIPE。
     * @return this process builder's standard input source
     * @since  1.7
     */
    public Redirect redirectInput() {
        return (redirects == null) ? Redirect.PIPE : redirects[0];
    }

    /**
     * Returns this process builder's standard output destination.
     * 1.返回此流程构建器的标准输出目标
     * Subprocesses subsequently started by this object's {@link #start()}
     * method redirect their standard output to this destination.
     * The initial value is {@link Redirect#PIPE Redirect.PIPE}.
     * 2.随后由该对象的start()方法启动的子进程将其标准输出重定向到该目的地。初始值为 Redirect.PIPE。
     * @return this process builder's standard output destination
     * @since  1.7
     */
    public Redirect redirectOutput() {
        return (redirects == null) ? Redirect.PIPE : redirects[1];
    }

    /**
     * Returns this process builder's standard error destination.
     * 1.返回此流程构建器的标准错误目标
     * Subprocesses subsequently started by this object's {@link #start()}
     * method redirect their standard error to this destination.
     * The initial value is {@link Redirect#PIPE Redirect.PIPE}.
     * 2.随后由该对象的 start()方法启动的子进程将它们的标准错误重定向到该目的地。初始值为Redirect.PIPE。
     * @return this process builder's standard error destination
     * @since  1.7
     */
    public Redirect redirectError() {
        return (redirects == null) ? Redirect.PIPE : redirects[2];
    }

    /**
     * Sets the source and destination for subprocess standard I/O
     * to be the same as those of the current Java process.
     * 1.将子进程标准 IO 的源和目标设置为与当前 Java 进程的源和目标相同
     * <p>This is a convenience method.  An invocation of the form
     *  <pre> {@code
     * pb.inheritIO()
     * }</pre>
     * behaves in exactly the same way as the invocation
     *  <pre> {@code
     * pb.redirectInput(Redirect.INHERIT)
     *   .redirectOutput(Redirect.INHERIT)
     *   .redirectError(Redirect.INHERIT)
     * }</pre>
     * 2.这是一种方便的方法。形式pb.inheritIO()的调用行为与调用 pb.redirectInput(Redirect.INHERIT)完全相同.
     * redirectError(Redirect.INHERIT)
     * This gives behavior equivalent to most operating system
     * command interpreters, or the standard C library function
     * {@code system()}.
     * 3.这提供了与大多数操作系统命令解释器或标准 C 库函数system()等效的行为。
     * @return this process builder
     * @since  1.7
     */
    public ProcessBuilder inheritIO() {
        Arrays.fill(redirects(), Redirect.INHERIT);
        return this;
    }

    /**
     * Tells whether this process builder merges standard error and
     * standard output.
     * 1.告诉此流程构建器是否合并标准错误和标准输出。
     * <p>If this property is {@code true}, then any error output
     * generated by subprocesses subsequently started by this object's
     * {@link #start()} method will be merged with the standard
     * output, so that both can be read using the
     * {@link Process#getInputStream()} method.  This makes it easier
     * to correlate error messages with the corresponding output.
     * The initial value is {@code false}.
     * 2.如果此属性为true，则此对象的start()方法随后启动的子进程生成的任何错误输出将与标准输出合并，
     * 以便可以使用  Process.getInputStream 读取两者（）方法。这使得将错误消息与相应的输出关联起来更容易。初始值为 false。
     * @return this process builder's {@code redirectErrorStream} property
     */
    public boolean redirectErrorStream() {
        return redirectErrorStream;
    }

    /**
     * Sets this process builder's {@code redirectErrorStream} property.
     * 1.设置此流程构建器的 redirectErrorStream属性。
     * <p>If this property is {@code true}, then any error output
     * generated by subprocesses subsequently started by this object's
     * {@link #start()} method will be merged with the standard
     * output, so that both can be read using the
     * {@link Process#getInputStream()} method.  This makes it easier
     * to correlate error messages with the corresponding output.
     * The initial value is {@code false}.
     *2.如果此属性为 true，则此对象的 start()方法随后启动的子进程生成的任何错误输出将与标准输出合并，
     * 以便可以使用 ProcessgetInputStream 读取两者（）方法。这使得将错误消息与相应的输出关联起来更容易。初始值为 false。
     * @param  redirectErrorStream the new property value
     * @return this process builder
     */
    public ProcessBuilder redirectErrorStream(boolean redirectErrorStream) {
        this.redirectErrorStream = redirectErrorStream;
        return this;
    }

    /**
     * Starts a new process using the attributes of this process builder.
     * 1.使用此流程构建器的属性启动一个新流程。
     * <p>The new process will
     * invoke the command and arguments given by {@link #command()},
     * in a working directory as given by {@link #directory()},
     * with a process environment as given by {@link #environment()}.
     * 2.新进程将调用 command()提供的命令和参数，在 directory()提供的工作目录中，进程环境由environment()提供。
     * <p>This method checks that the command is a valid operating
     * system command.  Which commands are valid is system-dependent,
     * but at the very least the command must be a non-empty list of
     * non-null strings.
     * 3.此方法检查该命令是否为有效的操作系统命令。哪些命令有效取决于系统，但至少命令必须是非空字符串的非空列表。
     * <p>A minimal set of system dependent environment variables may
     * be required to start a process on some operating systems.
     * As a result, the subprocess may inherit additional environment variable
     * settings beyond those in the process builder's {@link #environment()}.
     * 4.在某些操作系统上启动进程可能需要一组最小的系统相关环境变量。因此，
     * 子流程可能会继承流程构建器的 environment()之外的其他环境变量设置。
     * <p>If there is a security manager, its
     * {@link SecurityManager#checkExec checkExec}
     * method is called with the first component of this object's
     * {@code command} array as its argument. This may result in
     * a {@link SecurityException} being thrown.
     * 5.如果存在安全管理器，则调用其SecurityManager.checkExec方法，
     * 并使用此对象的command数组的第一个组件作为其参数。这可能会导致抛出 SecurityException。
     * <p>Starting an operating system process is highly system-dependent.
     * Among the many things that can go wrong are:
     * <ul>
     * <li>The operating system program file was not found.
     * <li>Access to the program file was denied.
     * <li>The working directory does not exist.
     * </ul>
     * 6.启动操作系统进程高度依赖于系统。可能出错的许多事情包括：
     * 1)找不到操作系统程序文件。
     * 2)对程序文件的访问被拒绝。
     * 3)工作目录不存在。
     * <p>In such cases an exception will be thrown.  The exact nature
     * of the exception is system-dependent, but it will always be a
     * subclass of {@link IOException}.
     * 7.在这种情况下，将抛出异常。异常的确切性质取决于系统，但它始终是 IOException的子类
     * <p>Subsequent modifications to this process builder will not
     * affect the returned {@link Process}.
     * 8.对此流程构建器的后续修改不会影响返回的Process。
     * @return a new {@link Process} object for managing the subprocess
     *
     * @throws NullPointerException
     *         if an element of the command list is null
     *
     * @throws IndexOutOfBoundsException
     *         if the command is an empty list (has size {@code 0})
     *
     * @throws SecurityException
     *         if a security manager exists and
     *         <ul>
     *
     *         <li>its
     *         {@link SecurityManager#checkExec checkExec}
     *         method doesn't allow creation of the subprocess, or
     *
     *         <li>the standard input to the subprocess was
     *         {@linkplain #redirectInput redirected from a file}
     *         and the security manager's
     *         {@link SecurityManager#checkRead checkRead} method
     *         denies read access to the file, or
     *
     *         <li>the standard output or standard error of the
     *         subprocess was
     *         {@linkplain #redirectOutput redirected to a file}
     *         and the security manager's
     *         {@link SecurityManager#checkWrite checkWrite} method
     *         denies write access to the file
     *
     *         </ul>
     *
     * @throws IOException if an I/O error occurs
     *
     * @see Runtime#exec(String[], String[], java.io.File)
     */
    public Process start() throws IOException {
        // Must convert to array first -- a malicious user-supplied
        // list might try to circumvent the security check.
        //必须先转换为数组——恶意用户提供的列表可能会试图绕过安全检查
        String[] cmdarray = command.toArray(new String[command.size()]);
        cmdarray = cmdarray.clone();

        for (String arg : cmdarray)
            if (arg == null)
                throw new NullPointerException();
        // Throws IndexOutOfBoundsException if command is empty
        //如果命令为空，则抛出 IndexOutOfBoundsException
        String prog = cmdarray[0];

        SecurityManager security = System.getSecurityManager();
        if (security != null)
            security.checkExec(prog);

        String dir = directory == null ? null : directory.toString();

        for (int i = 1; i < cmdarray.length; i++) {
            if (cmdarray[i].indexOf('\u0000') >= 0) {
                throw new IOException("invalid null character in command");
            }
        }

        try {
            return ProcessImpl.start(cmdarray,
                                     environment,
                                     dir,
                                     redirects,
                                     redirectErrorStream);
        } catch (IOException | IllegalArgumentException e) {
            String exceptionInfo = ": " + e.getMessage();
            Throwable cause = e;
            if ((e instanceof IOException) && security != null) {
                // Can not disclose the fail reason for read-protected files.
                try {
                    security.checkRead(prog);
                } catch (SecurityException se) {
                    exceptionInfo = "";
                    cause = se;
                }
            }
            // It's much easier for us to create a high-quality error
            // message than the low-level C code which found the problem.
            throw new IOException(
                "Cannot run program \"" + prog + "\""
                + (dir == null ? "" : " (in directory \"" + dir + "\")")
                + exceptionInfo,
                cause);
        }
    }
}
