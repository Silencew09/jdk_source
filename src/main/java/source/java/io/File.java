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

import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.security.AccessController;
import java.security.SecureRandom;
import java.nio.file.Path;
import java.nio.file.FileSystems;
import sun.security.action.GetPropertyAction;

/**
 * An abstract representation of file and directory pathnames.
 * 1.文件和目录路径名的抽象表示
 * <p> User interfaces and operating systems use system-dependent <em>pathname
 * strings</em> to name files and directories.  This class presents an
 * abstract, system-independent view of hierarchical pathnames.  An
 * <em>abstract pathname</em> has two components:
 *
 * <ol>
 * <li> An optional system-dependent <em>prefix</em> string,
 *      such as a disk-drive specifier, <code>"/"</code>&nbsp;for the UNIX root
 *      directory, or <code>"\\\\"</code>&nbsp;for a Microsoft Windows UNC pathname, and
 * <li> A sequence of zero or more string <em>names</em>.
 * </ol>
 * 2.用户界面和操作系统使用依赖于系统的路径名字符串来命名文件和目录。此类提供分层路径名的抽象的、独立于系统的视图。
 * 抽象路径名有两个组成部分：一个可选的依赖于系统的 prefix字符串，例如磁盘驱动器说明符 "/";
 * UNIX 根目录，或"\\\\"代表 Microsoft Windows UNC 路径名，以及零个或多个字符串names的序列。
 * The first name in an abstract pathname may be a directory name or, in the
 * case of Microsoft Windows UNC pathnames, a hostname.  Each subsequent name
 * in an abstract pathname denotes a directory; the last name may denote
 * either a directory or a file.  The <em>empty</em> abstract pathname has no
 * prefix and an empty name sequence.
 * 3.抽象路径名中的第一个名称可以是目录名，或者在 Microsoft Windows UNC 路径名的情况下是主机名。
 * 抽象路径名中的每个后续名称表示一个目录；姓氏可以表示目录或文件。 empty抽象路径名没有前缀和空名称序列
 * <p> The conversion of a pathname string to or from an abstract pathname is
 * inherently system-dependent.  When an abstract pathname is converted into a
 * pathname string, each name is separated from the next by a single copy of
 * the default <em>separator character</em>.  The default name-separator
 * character is defined by the system property <code>file.separator</code>, and
 * is made available in the public static fields <code>{@link
 * #separator}</code> and <code>{@link #separatorChar}</code> of this class.
 * When a pathname string is converted into an abstract pathname, the names
 * within it may be separated by the default name-separator character or by any
 * other name-separator character that is supported by the underlying system.
 * 4.路径名字符串与抽象路径名的转换本质上是依赖于系统的。当抽象路径名转换为路径名字符串时，
 * 每个名称与下一个名称由默认分隔符的单个副本分隔。默认名称分隔符由系统属性file.separator定义，
 * 并在公共静态字段separator和  separatorChar中可用 这个类。当路径名字符串转换为抽象路径名时，
 * 其中的名称可以由默认名称分隔符或底层系统支持的任何其他名称分隔符分隔
 * <p> A pathname, whether abstract or in string form, may be either
 * <em>absolute</em> or <em>relative</em>.  An absolute pathname is complete in
 * that no other information is required in order to locate the file that it
 * denotes.  A relative pathname, in contrast, must be interpreted in terms of
 * information taken from some other pathname.  By default the classes in the
 * <code>java.io</code> package always resolve relative pathnames against the
 * current user directory.  This directory is named by the system property
 * <code>user.dir</code>, and is typically the directory in which the Java
 * virtual machine was invoked.
 * 5.路径名，无论是抽象的还是字符串形式的，都可以是absolute或relative。绝对路径名是完整的，
 * 因为不需要其他信息来定位它表示的文件。相反，相对路径名必须根据取自其他路径名的信息进行解释。
 * 默认情况下，java.io包中的类总是针对当前用户目录解析相对路径名。该目录由系统属性user.dir命名，
 * 通常是调用 Java 虚拟机的目录
 * <p> The <em>parent</em> of an abstract pathname may be obtained by invoking
 * the {@link #getParent} method of this class and consists of the pathname's
 * prefix and each name in the pathname's name sequence except for the last.
 * Each directory's absolute pathname is an ancestor of any <tt>File</tt>
 * object with an absolute abstract pathname which begins with the directory's
 * absolute pathname.  For example, the directory denoted by the abstract
 * pathname <tt>"/usr"</tt> is an ancestor of the directory denoted by the
 * pathname <tt>"/usr/local/bin"</tt>.
 * 6.抽象路径名的parent可以通过调用该类的getParent方法获得，它由路径名的前缀和路径名名称序列中
 * 除最后一个之外的每个名称组成。每个目录的绝对路径名是任何具有以目录的绝对路径名开头的
 * 绝对抽象路径名的File对象的祖先。例如，由抽象路径名"/usr"表示的目录是由路径名"/usr/local/bin"表示的目录的祖先。
 * <p> The prefix concept is used to handle root directories on UNIX platforms,
 * and drive specifiers, root directories and UNC pathnames on Microsoft Windows platforms,
 * as follows:
 * 7.前缀概念用于处理 UNIX 平台上的根目录，以及 Microsoft Windows 平台上的驱动器说明符、根目录和 UNC 路径名，如下所示：
 * <ul>
 *
 * <li> For UNIX platforms, the prefix of an absolute pathname is always
 * <code>"/"</code>.  Relative pathnames have no prefix.  The abstract pathname
 * denoting the root directory has the prefix <code>"/"</code> and an empty
 * name sequence.
 * 1).对于 UNIX 平台，绝对路径名的前缀始终是"/"。相对路径名没有前缀。
 * 表示根目录的抽象路径名具有前缀 "/"和空名称序列
 * <li> For Microsoft Windows platforms, the prefix of a pathname that contains a drive
 * specifier consists of the drive letter followed by <code>":"</code> and
 * possibly followed by <code>"\\"</code> if the pathname is absolute.  The
 * prefix of a UNC pathname is <code>"\\\\"</code>; the hostname and the share
 * name are the first two names in the name sequence.  A relative pathname that
 * does not specify a drive has no prefix.
 * 2).对于 Microsoft Windows 平台，包含驱动器说明符的路径名的前缀由驱动器号后跟":"组成，
 * 如果路径名是绝对路径，则可能后跟"\\". UNC 路径名的前缀是 "\\\\";
 * 主机名和共享名是名称序列中的前两个名称。未指定驱动器的相对路径名没有前缀。
 * </ul>
 *
 * <p> Instances of this class may or may not denote an actual file-system
 * object such as a file or a directory.  If it does denote such an object
 * then that object resides in a <i>partition</i>.  A partition is an
 * operating system-specific portion of storage for a file system.  A single
 * storage device (e.g. a physical disk-drive, flash memory, CD-ROM) may
 * contain multiple partitions.  The object, if any, will reside on the
 * partition <a name="partName">named</a> by some ancestor of the absolute
 * form of this pathname.
 *8.此类的实例可能表示也可能不表示实际的文件系统对象，例如文件或目录。如果它确实表示这样的对象，
 * 则该对象驻留在partition中。分区是用于文件系统的特定于操作系统的存储部分。
 * 单个存储设备（例如物理磁盘驱动器、闪存、CD-ROM）可能包含多个分区。
 * 该对象（如果有）将驻留在分区 <a name="partName">named上，该分区由该路径名的绝对形式的某个祖先命名
 * <p> A file system may implement restrictions to certain operations on the
 * actual file-system object, such as reading, writing, and executing.  These
 * restrictions are collectively known as <i>access permissions</i>.  The file
 * system may have multiple sets of access permissions on a single object.
 * For example, one set may apply to the object's <i>owner</i>, and another
 * may apply to all other users.  The access permissions on an object may
 * cause some methods in this class to fail.
 * 9.文件系统可以对实际文件系统对象上的某些操作实施限制，例如读取、写入和执行。
 * 这些限制统称为访问权限。文件系统可能对单个对象具有多组访问权限。例如，一个集合可能适用于对象的owner，
 * 另一个可能适用于所有其他用户。对象的访问权限可能会导致该类中的某些方法失败
 * <p> Instances of the <code>File</code> class are immutable; that is, once
 * created, the abstract pathname represented by a <code>File</code> object
 * will never change.
 * 10.File类的实例是不可变的；也就是说，一旦创建，由File对象表示的抽象路径名将永远不会改变
 * <h3>Interoperability with {@code java.nio.file} package</h3>
 * 11.与 {@code java.nio.file} 包的互操作性
 * <p> The <a href="../../java/nio/file/package-summary.html">{@code java.nio.file}</a>
 * package defines interfaces and classes for the Java virtual machine to access
 * files, file attributes, and file systems. This API may be used to overcome
 * many of the limitations of the {@code java.io.File} class.
 * The {@link #toPath toPath} method may be used to obtain a {@link
 * Path} that uses the abstract path represented by a {@code File} object to
 * locate a file. The resulting {@code Path} may be used with the {@link
 * java.nio.file.Files} class to provide more efficient and extensive access to
 * additional file operations, file attributes, and I/O exceptions to help
 * diagnose errors when an operation on a file fails.
 * 12.<a href="....javaniofilepackage-summary.html">java.nio.file
 * 包定义了 Java 虚拟机访问文件、文件属性和文件系统的接口和类。
 * 此 API 可用于克服java.io.File类的许多限制。toPath方法可用于获取使用File对象表示的抽象路径来定位文件的Path。
 * 生成的Path可以与java.nio.file.Files类一起使用，以提供对其他文件操作、文件属性和 IO 异常的更有效和广泛的访问，
 * 以帮助诊断错误一个文件失败
 * @author  unascribed
 * @since   JDK1.0
 */

public class File
    implements Serializable, Comparable<File>
{

    /**
     * The FileSystem object representing the platform's local file system.
     * 代表平台本地文件系统的 FileSystem 对象
     */
    private static final FileSystem fs = DefaultFileSystem.getFileSystem();

    /**
     * This abstract pathname's normalized pathname string. A normalized
     * pathname string uses the default name-separator character and does not
     * contain any duplicate or redundant separators.
     * 此抽象路径名的规范化路径名字符串。规范化的路径名字符串使用默认的名称分隔符，并且不包含任何重复或冗余的分隔符
     * @serial
     */
    private final String path;

    /**
     * Enum type that indicates the status of a file path.
     * 指示文件路径状态的枚举类型。
     */
    private static enum PathStatus { INVALID, CHECKED };

    /**
     * The flag indicating whether the file path is invalid.
     * 指示文件路径是否无效的标志
     */
    private transient PathStatus status = null;

    /**
     * Check if the file has an invalid path. Currently, the inspection of
     * a file path is very limited, and it only covers Nul character check.
     * Returning true means the path is definitely invalid/garbage. But
     * returning false does not guarantee that the path is valid.
     * 检查文件是否有无效路径。目前对文件路径的检查非常有限，只包括Nul字符检查。
     * 返回 true 意味着路径肯定是 invalidgarbage。但是返回 false 并不能保证路径有效
     * @return true if the file path is invalid.
     */
    final boolean isInvalid() {
        if (status == null) {
            status = (this.path.indexOf('\u0000') < 0) ? PathStatus.CHECKED
                                                       : PathStatus.INVALID;
        }
        return status == PathStatus.INVALID;
    }

    /**
     * The length of this abstract pathname's prefix, or zero if it has no
     * prefix.
     * 此抽象路径名前缀的长度，如果没有前缀，则为零
     */
    private final transient int prefixLength;

    /**
     * Returns the length of this abstract pathname's prefix.
     * For use by FileSystem classes.
     * 返回此抽象路径名前缀的长度。供文件系统类使用。
     */
    int getPrefixLength() {
        return prefixLength;
    }

    /**
     * The system-dependent default name-separator character.  This field is
     * initialized to contain the first character of the value of the system
     * property <code>file.separator</code>.  On UNIX systems the value of this
     * field is <code>'/'</code>; on Microsoft Windows systems it is <code>'\\'</code>.
     * 系统相关的默认名称分隔符。该字段被初始化为包含系统属性file.separator值的第一个字符。
     * 在 UNIX 系统上，该字段的值为'/'；在 Microsoft Windows 系统上，它是'\\'
     * @see     java.lang.System#getProperty(java.lang.String)
     */
    public static final char separatorChar = fs.getSeparator();

    /**
     * The system-dependent default name-separator character, represented as a
     * string for convenience.  This string contains a single character, namely
     * <code>{@link #separatorChar}</code>.
     * 系统相关的默认名称分隔符，为方便起见表示为字符串。该字符串包含单个字符，即separatorChar
     */
    public static final String separator = "" + separatorChar;

    /**
     * The system-dependent path-separator character.  This field is
     * initialized to contain the first character of the value of the system
     * property <code>path.separator</code>.  This character is used to
     * separate filenames in a sequence of files given as a <em>path list</em>.
     * On UNIX systems, this character is <code>':'</code>; on Microsoft Windows systems it
     * is <code>';'</code>.
     * 系统相关的路径分隔符。该字段被初始化为包含系统属性path.separator值的第一个字符。
     * 该字符用于分隔作为路径列表给出的一系列文件中的文件名。在 UNIX 系统上，这个字符是':;
     * 在 Microsoft Windows 系统上，它是 ';'
     *
     * @see     java.lang.System#getProperty(java.lang.String)
     */
    public static final char pathSeparatorChar = fs.getPathSeparator();

    /**
     * The system-dependent path-separator character, represented as a string
     * for convenience.  This string contains a single character, namely
     * <code>{@link #pathSeparatorChar}</code>.
     * 系统相关的路径分隔符，为方便起见表示为字符串。该字符串包含单个字符，即pathSeparatorChar
     */
    public static final String pathSeparator = "" + pathSeparatorChar;


    /* -- Constructors -- */

    /**
     * Internal constructor for already-normalized pathname strings.
     * 已经规范化的路径名字符串的内部构造函数。
     */
    private File(String pathname, int prefixLength) {
        this.path = pathname;
        this.prefixLength = prefixLength;
    }

    /**
     * Internal constructor for already-normalized pathname strings.
     * The parameter order is used to disambiguate this method from the
     * public(File, String) constructor.
     * 已经规范化的路径名字符串的内部构造函数。参数顺序用于消除此方法与 public(File, String) 构造函数的歧义
     */
    private File(String child, File parent) {
        assert parent.path != null;
        assert (!parent.path.equals(""));
        this.path = fs.resolve(parent.path, child);
        this.prefixLength = parent.prefixLength;
    }

    /**
     * Creates a new <code>File</code> instance by converting the given
     * pathname string into an abstract pathname.  If the given string is
     * the empty string, then the result is the empty abstract pathname.
     * 通过将给定的路径名字符串转换为抽象路径名来创建一个新的File实例。
     * 如果给定的字符串是空字符串，则结果是空的抽象路径名
     * @param   pathname  A pathname string
     * @throws  NullPointerException
     *          If the <code>pathname</code> argument is <code>null</code>
     */
    public File(String pathname) {
        if (pathname == null) {
            throw new NullPointerException();
        }
        this.path = fs.normalize(pathname);
        this.prefixLength = fs.prefixLength(this.path);
    }

    /* Note: The two-argument File constructors do not interpret an empty
       parent abstract pathname as the current user directory.  An empty parent
       instead causes the child to be resolved against the system-dependent
       directory defined by the FileSystem.getDefaultParent method.  On Unix
       this default is "/", while on Microsoft Windows it is "\\".  This is required for
       compatibility with the original behavior of this class. */
    //注意：两个参数的 File 构造函数不会将空的父抽象路径名解释为当前用户目录。
    // 相反，空父项会导致根据 FileSystem.getDefaultParent 方法定义的系统相关目录解析子项。
    // 在 Unix 上这个默认值是 "/"，而在 Microsoft Windows 上它是 "\\"。这是与此类的原始行为兼容所必需的

    /**
     * Creates a new <code>File</code> instance from a parent pathname string
     * and a child pathname string.
     * 1.从父路径名字符串和子路径名字符串创建一个新的File实例
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     * 2.如果parent是null则创建新的File实例，就像通过在给定的上调用单参数File构造函数一样child路径名字符串
     * <p> Otherwise the <code>parent</code> pathname string is taken to denote
     * a directory, and the <code>child</code> pathname string is taken to
     * denote either a directory or a file.  If the <code>child</code> pathname
     * string is absolute then it is converted into a relative pathname in a
     * system-dependent way.  If <code>parent</code> is the empty string then
     * the new <code>File</code> instance is created by converting
     * <code>child</code> into an abstract pathname and resolving the result
     * against a system-dependent default directory.  Otherwise each pathname
     * string is converted into an abstract pathname and the child abstract
     * pathname is resolved against the parent.
     * 3.否则，parent路径名字符串表示目录，child路径名字符串表示目录或文件。
     * 如果child路径名字符串是绝对的，那么它会以系统相关的方式转换为相对路径名。
     * 如果parent是空字符串，那么新的File实例是通过将child转换为抽象路径名并根据
     * 系统相关的默认目录解析结果来创建的。否则，每个路径名字符串都将转换为抽象路径名，
     * 并且根据父级解析子抽象路径名
     * @param   parent  The parent pathname string
     * @param   child   The child pathname string
     * @throws  NullPointerException
     *          If <code>child</code> is <code>null</code>
     */
    public File(String parent, String child) {
        if (child == null) {
            throw new NullPointerException();
        }
        if (parent != null) {
            if (parent.equals("")) {
                this.path = fs.resolve(fs.getDefaultParent(),
                                       fs.normalize(child));
            } else {
                this.path = fs.resolve(fs.normalize(parent),
                                       fs.normalize(child));
            }
        } else {
            this.path = fs.normalize(child);
        }
        this.prefixLength = fs.prefixLength(this.path);
    }

    /**
     * Creates a new <code>File</code> instance from a parent abstract
     * pathname and a child pathname string.
     * 1.从父抽象路径名和子路径名字符串创建一个新的File实例
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     * 2.如果parent是null则创建新的File实例，就像通过在给定的上调用单参数File构造函数一样child路径名字符串
     * <p> Otherwise the <code>parent</code> abstract pathname is taken to
     * denote a directory, and the <code>child</code> pathname string is taken
     * to denote either a directory or a file.  If the <code>child</code>
     * pathname string is absolute then it is converted into a relative
     * pathname in a system-dependent way.  If <code>parent</code> is the empty
     * abstract pathname then the new <code>File</code> instance is created by
     * converting <code>child</code> into an abstract pathname and resolving
     * the result against a system-dependent default directory.  Otherwise each
     * pathname string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
     * 3.否则parent抽象路径名被用来表示一个目录，而child路径名字符串被用来表示一个目录或一个文件。
     * 如果child路径名字符串是绝对的，那么它会以系统相关的方式转换为相对路径名。如果parent是空的抽象路径名，
     * 那么新的File实例是通过将child转换为抽象路径名并根据系统相关的默认目录解析结果来创建的.否则，
     * 每个路径名字符串都将转换为抽象路径名，并且根据父级解析子抽象路径名。
     * @param   parent  The parent abstract pathname
     * @param   child   The child pathname string
     * @throws  NullPointerException
     *          If <code>child</code> is <code>null</code>
     */
    public File(File parent, String child) {
        if (child == null) {
            throw new NullPointerException();
        }
        if (parent != null) {
            if (parent.path.equals("")) {
                this.path = fs.resolve(fs.getDefaultParent(),
                                       fs.normalize(child));
            } else {
                this.path = fs.resolve(parent.path,
                                       fs.normalize(child));
            }
        } else {
            this.path = fs.normalize(child);
        }
        this.prefixLength = fs.prefixLength(this.path);
    }

    /**
     * Creates a new <tt>File</tt> instance by converting the given
     * <tt>file:</tt> URI into an abstract pathname.
     * 1.通过将给定的file:URI 转换为抽象路径名来创建一个新的File实例
     * <p> The exact form of a <tt>file:</tt> URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     * 2.file: URI 的确切形式与系统有关，因此此构造函数执行的转换也与系统有关。
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     *
     * <blockquote><tt>
     * new File(</tt><i>&nbsp;f</i><tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </tt></blockquote>
     * 3.对于给定的抽象路径名f保证 :
     * new File(f.toURI()).equals(f.getAbsoluteFile())
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a <tt>file:</tt> URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     * 4.只要原始抽象路径名、URI 和新抽象路径名都是在（可能是不同调用）同一个 Java 虚拟机中创建的。
     * 然而，当在一个操作系统上的虚拟机中创建的file:URI 被转换为不同操作系统上的虚拟机中的抽象路径名时，这种关系通常不成立
     * @param  uri
     *         An absolute, hierarchical URI with a scheme equal to
     *         <tt>"file"</tt>, a non-empty path component, and undefined
     *         authority, query, and fragment components
     *
     * @throws  NullPointerException
     *          If <tt>uri</tt> is <tt>null</tt>
     *
     * @throws  IllegalArgumentException
     *          If the preconditions on the parameter do not hold
     *
     * @see #toURI()
     * @see java.net.URI
     * @since 1.4
     */
    public File(URI uri) {

        // Check our many preconditions
        if (!uri.isAbsolute())
            throw new IllegalArgumentException("URI is not absolute");
        if (uri.isOpaque())
            throw new IllegalArgumentException("URI is not hierarchical");
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equalsIgnoreCase("file"))
            throw new IllegalArgumentException("URI scheme is not \"file\"");
        if (uri.getAuthority() != null)
            throw new IllegalArgumentException("URI has an authority component");
        if (uri.getFragment() != null)
            throw new IllegalArgumentException("URI has a fragment component");
        if (uri.getQuery() != null)
            throw new IllegalArgumentException("URI has a query component");
        String p = uri.getPath();
        if (p.equals(""))
            throw new IllegalArgumentException("URI path component is empty");

        // Okay, now initialize
        p = fs.fromURIPath(p);
        if (File.separatorChar != '/')
            p = p.replace('/', File.separatorChar);
        this.path = fs.normalize(p);
        this.prefixLength = fs.prefixLength(this.path);
    }


    /* -- Path-component accessors -- */
    //路径组件访问器

    /**
     * Returns the name of the file or directory denoted by this abstract
     * pathname.  This is just the last name in the pathname's name
     * sequence.  If the pathname's name sequence is empty, then the empty
     * string is returned.
     * 返回此抽象路径名表示的文件或目录的名称。这只是路径名名称序列中的最后一个名称。
     * 如果路径名的名称序列为空，则返回空字符串
     * @return  The name of the file or directory denoted by this abstract
     *          pathname, or the empty string if this pathname's name sequence
     *          is empty
     */
    public String getName() {
        int index = path.lastIndexOf(separatorChar);
        if (index < prefixLength) return path.substring(prefixLength);
        return path.substring(index + 1);
    }

    /**
     * Returns the pathname string of this abstract pathname's parent, or
     * <code>null</code> if this pathname does not name a parent directory.
     * 1.返回此抽象路径名的父目录的路径名字符串，如果此路径名未命名父目录，则返回null。
     * <p> The <em>parent</em> of an abstract pathname consists of the
     * pathname's prefix, if any, and each name in the pathname's name
     * sequence except for the last.  If the name sequence is empty then
     * the pathname does not name a parent directory.
     * 2.抽象路径名的parent由路径名的前缀（如果有）和路径名的名称序列中除最后一个名称之外的每个名称组成。
     * 如果名称序列为空，则路径名不会命名父目录
     * @return  The pathname string of the parent directory named by this
     *          abstract pathname, or <code>null</code> if this pathname
     *          does not name a parent
     */
    public String getParent() {
        int index = path.lastIndexOf(separatorChar);
        if (index < prefixLength) {
            if ((prefixLength > 0) && (path.length() > prefixLength))
                return path.substring(0, prefixLength);
            return null;
        }
        return path.substring(0, index);
    }

    /**
     * Returns the abstract pathname of this abstract pathname's parent,
     * or <code>null</code> if this pathname does not name a parent
     * directory.
     * 1.返回此抽象路径名的父目录的抽象路径名，如果此路径名未命名父目录，则返回null
     * <p> The <em>parent</em> of an abstract pathname consists of the
     * pathname's prefix, if any, and each name in the pathname's name
     * sequence except for the last.  If the name sequence is empty then
     * the pathname does not name a parent directory.
     * 2.抽象路径名的parent由路径名的前缀（如果有）和路径名的名称序列中除最后一个名称之外的每个名称组成。
     * 如果名称序列为空，则路径名不会命名父目录
     * @return  The abstract pathname of the parent directory named by this
     *          abstract pathname, or <code>null</code> if this pathname
     *          does not name a parent
     *
     * @since 1.2
     */
    public File getParentFile() {
        String p = this.getParent();
        if (p == null) return null;
        return new File(p, this.prefixLength);
    }

    /**
     * Converts this abstract pathname into a pathname string.  The resulting
     * string uses the {@link #separator default name-separator character} to
     * separate the names in the name sequence.
     * 将此抽象路径名转换为路径名字符串。结果字符串使用 separator default name-separator character
     * 分隔名称序列中的名称
     * @return  The string form of this abstract pathname
     */
    public String getPath() {
        return path;
    }


    /* -- Path operations -- */

    /**
     * Tests whether this abstract pathname is absolute.  The definition of
     * absolute pathname is system dependent.  On UNIX systems, a pathname is
     * absolute if its prefix is <code>"/"</code>.  On Microsoft Windows systems, a
     * pathname is absolute if its prefix is a drive specifier followed by
     * <code>"\\"</code>, or if its prefix is <code>"\\\\"</code>.
     * 测试这个抽象路径名是否是绝对的。绝对路径名的定义取决于系统。在 UNIX 系统上，
     * 如果路径名的前缀为"/"，则该路径名是绝对路径名。在 Microsoft Windows 系统上，
     * 如果路径名的前缀是驱动器说明符后跟"\\"，或者它的前缀是"\\\\"，则路径名是绝对路径名。
     * @return  <code>true</code> if this abstract pathname is absolute,
     *          <code>false</code> otherwise
     */
    public boolean isAbsolute() {
        return fs.isAbsolute(this);
    }

    /**
     * Returns the absolute pathname string of this abstract pathname.
     * 1.返回此抽象路径名的绝对路径名字符串。
     * <p> If this abstract pathname is already absolute, then the pathname
     * string is simply returned as if by the <code>{@link #getPath}</code>
     * method.  If this abstract pathname is the empty abstract pathname then
     * the pathname string of the current user directory, which is named by the
     * system property <code>user.dir</code>, is returned.  Otherwise this
     * pathname is resolved in a system-dependent way.  On UNIX systems, a
     * relative pathname is made absolute by resolving it against the current
     * user directory.  On Microsoft Windows systems, a relative pathname is made absolute
     * by resolving it against the current directory of the drive named by the
     * pathname, if any; if not, it is resolved against the current user
     * directory.
     * 2.如果这个抽象路径名已经是绝对路径名，那么路径名字符串就像由getPath方法一样简单地返回。
     * 如果此抽象路径名是空抽象路径名，则返回当前用户目录的路径名字符串，由系统属性user.dir命名。
     * 否则，此路径名将以系统相关的方式解析。在 UNIX 系统上，
     * 相对路径名通过针对当前用户目录进行解析而成为绝对路径名。在 Microsoft Windows 系统上，
     * 相对路径名通过针对由路径名命名的驱动器的当前目录（如果有）进行解析而成为绝对路径名；如果不是，则针对当前用户目录进行解析
     * @return  The absolute pathname string denoting the same file or
     *          directory as this abstract pathname
     *
     * @throws  SecurityException
     *          If a required system property value cannot be accessed.
     *
     * @see     java.io.File#isAbsolute()
     */
    public String getAbsolutePath() {
        return fs.resolve(this);
    }

    /**
     * Returns the absolute form of this abstract pathname.  Equivalent to
     * <code>new&nbsp;File(this.{@link #getAbsolutePath})</code>.
     * 返回此抽象路径名的绝对形式。等效于new File(this.getAbsolutePath)
     * @return  The absolute abstract pathname denoting the same file or
     *          directory as this abstract pathname
     *
     * @throws  SecurityException
     *          If a required system property value cannot be accessed.
     *
     * @since 1.2
     */
    public File getAbsoluteFile() {
        String absPath = getAbsolutePath();
        return new File(absPath, fs.prefixLength(absPath));
    }

    /**
     * Returns the canonical pathname string of this abstract pathname.
     * 1.返回此抽象路径名的规范路径名字符串。
     * <p> A canonical pathname is both absolute and unique.  The precise
     * definition of canonical form is system-dependent.  This method first
     * converts this pathname to absolute form if necessary, as if by invoking the
     * {@link #getAbsolutePath} method, and then maps it to its unique form in a
     * system-dependent way.  This typically involves removing redundant names
     * such as <tt>"."</tt> and <tt>".."</tt> from the pathname, resolving
     * symbolic links (on UNIX platforms), and converting drive letters to a
     * standard case (on Microsoft Windows platforms).
     * 2.规范路径名既是绝对的又是唯一的。规范形式的精确定义取决于系统。如有必要，
     * 此方法首先将此路径名转换为绝对形式，就像调用getAbsolutePath方法一样，
     * 然后以依赖于系统的方式将其映射到其唯一形式。这通常涉及从路径名中删除诸如 "."和 ".."之类的冗余名称、
     * 解析符号链接（在 UNIX 平台上）以及将驱动器号转换为标准大小写（在 Microsoft Windows 平台上）
     * <p> Every pathname that denotes an existing file or directory has a
     * unique canonical form.  Every pathname that denotes a nonexistent file
     * or directory also has a unique canonical form.  The canonical form of
     * the pathname of a nonexistent file or directory may be different from
     * the canonical form of the same pathname after the file or directory is
     * created.  Similarly, the canonical form of the pathname of an existing
     * file or directory may be different from the canonical form of the same
     * pathname after the file or directory is deleted.
     * 3.每个表示现有文件或目录的路径名都有唯一的规范形式。每个表示不存在的文件或目录的路径名也具有唯一的规范形式。
     * 创建文件或目录后，不存在的文件或目录的路径名的规范形式可能与相同路径名的规范形式不同。
     * 同样，删除文件或目录后，现有文件或目录的路径名的规范形式可能与相同路径名的规范形式不同。
     * @return  The canonical pathname string denoting the same file or
     *          directory as this abstract pathname
     *
     * @throws  IOException
     *          If an I/O error occurs, which is possible because the
     *          construction of the canonical pathname may require
     *          filesystem queries
     *
     * @throws  SecurityException
     *          If a required system property value cannot be accessed, or
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead}</code> method denies
     *          read access to the file
     *
     * @since   JDK1.1
     * @see     Path#toRealPath
     */
    public String getCanonicalPath() throws IOException {
        if (isInvalid()) {
            throw new IOException("Invalid file path");
        }
        return fs.canonicalize(fs.resolve(this));
    }

    /**
     * Returns the canonical form of this abstract pathname.  Equivalent to
     * <code>new&nbsp;File(this.{@link #getCanonicalPath})</code>.
     * 返回此抽象路径名的规范形式。等效于new File(this.getCanonicalPath)
     * @return  The canonical pathname string denoting the same file or
     *          directory as this abstract pathname
     *
     * @throws  IOException
     *          If an I/O error occurs, which is possible because the
     *          construction of the canonical pathname may require
     *          filesystem queries
     *
     * @throws  SecurityException
     *          If a required system property value cannot be accessed, or
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead}</code> method denies
     *          read access to the file
     *
     * @since 1.2
     * @see     Path#toRealPath
     */
    public File getCanonicalFile() throws IOException {
        String canonPath = getCanonicalPath();
        return new File(canonPath, fs.prefixLength(canonPath));
    }

    private static String slashify(String path, boolean isDirectory) {
        String p = path;
        if (File.separatorChar != '/')
            p = p.replace(File.separatorChar, '/');
        if (!p.startsWith("/"))
            p = "/" + p;
        if (!p.endsWith("/") && isDirectory)
            p = p + "/";
        return p;
    }

    /**
     * Converts this abstract pathname into a <code>file:</code> URL.  The
     * exact form of the URL is system-dependent.  If it can be determined that
     * the file denoted by this abstract pathname is a directory, then the
     * resulting URL will end with a slash.
     *
     * @return  A URL object representing the equivalent file URL
     *
     * @throws  MalformedURLException
     *          If the path cannot be parsed as a URL
     *
     * @see     #toURI()
     * @see     java.net.URI
     * @see     java.net.URI#toURL()
     * @see     java.net.URL
     * @since   1.2
     *
     * @deprecated This method does not automatically escape characters that
     * are illegal in URLs.  It is recommended that new code convert an
     * abstract pathname into a URL by first converting it into a URI, via the
     * {@link #toURI() toURI} method, and then converting the URI into a URL
     * via the {@link java.net.URI#toURL() URI.toURL} method.
     */
    @Deprecated
    public URL toURL() throws MalformedURLException {
        if (isInvalid()) {
            throw new MalformedURLException("Invalid file path");
        }
        return new URL("file", "", slashify(getAbsolutePath(), isDirectory()));
    }

    /**
     * Constructs a <tt>file:</tt> URI that represents this abstract pathname.
     * 1.构造表示此抽象路径名的file:URI
     * <p> The exact form of the URI is system-dependent.  If it can be
     * determined that the file denoted by this abstract pathname is a
     * directory, then the resulting URI will end with a slash.
     * 2.URI 的确切形式取决于系统。如果可以确定这个抽象路径名表示的文件是一个目录，那么生成的 URI 将以斜杠结尾。
     * <p> For a given abstract pathname <i>f</i>, it is guaranteed that
     *
     * <blockquote><tt>
     * new {@link #File(java.net.URI) File}(</tt><i>&nbsp;f</i><tt>.toURI()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </tt></blockquote>
     * 3.对于给定的抽象路径名f，保证 new  File(java.net.URI) File(f.toURI()).equals(f.getAbsoluteFile())
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  Due to the system-dependent nature of abstract
     * pathnames, however, this relationship typically does not hold when a
     * <tt>file:</tt> URI that is created in a virtual machine on one operating
     * system is converted into an abstract pathname in a virtual machine on a
     * different operating system.
     * 4.只要原始抽象路径名、URI 和新抽象路径名都是在（可能是不同调用）同一个 Java 虚拟机中创建的。
     * 然而，由于抽象路径名的系统相关性质，当在一个操作系统上的虚拟机中创建的file:URI
     * 被转换为虚拟机中的抽象路径名时，这种关系通常不成立。机器在不同的操作系统上
     * <p> Note that when this abstract pathname represents a UNC pathname then
     * all components of the UNC (including the server name component) are encoded
     * in the {@code URI} path. The authority component is undefined, meaning
     * that it is represented as {@code null}. The {@link Path} class defines the
     * {@link Path#toUri toUri} method to encode the server name in the authority
     * component of the resulting {@code URI}. The {@link #toPath toPath} method
     * may be used to obtain a {@code Path} representing this abstract pathname.
     * 5.请注意，当此抽象路径名表示 UNC 路径名时，UNC 的所有组件（包括服务器名称组件）都在URI路径中进行编码。
     * 权限组件未定义，这意味着它表示为null。 Path类定义了Path.toUri方法，以在结果URI的授权组件中对服务器名称进行编码。
     * toPath方法可用于获取表示此抽象路径名的Path
     * @return  An absolute, hierarchical URI with a scheme equal to
     *          <tt>"file"</tt>, a path representing this abstract pathname,
     *          and undefined authority, query, and fragment components
     * @throws SecurityException If a required system property value cannot
     * be accessed.
     *
     * @see #File(java.net.URI)
     * @see java.net.URI
     * @see java.net.URI#toURL()
     * @since 1.4
     */
    public URI toURI() {
        try {
            File f = getAbsoluteFile();
            String sp = slashify(f.getPath(), f.isDirectory());
            if (sp.startsWith("//"))
                sp = "//" + sp;
            return new URI("file", null, sp, null);
        } catch (URISyntaxException x) {
            throw new Error(x);         // Can't happen
        }
    }


    /* -- Attribute accessors -- */
    //属性访问器

    /**
     * Tests whether the application can read the file denoted by this
     * abstract pathname. On some platforms it may be possible to start the
     * Java virtual machine with special privileges that allow it to read
     * files that are marked as unreadable. Consequently this method may return
     * {@code true} even though the file does not have read permissions.
     * 测试应用程序是否可以读取此抽象路径名表示的文件。在某些平台上，
     * 可以使用特殊权限启动 Java 虚拟机，允许它读取标记为不可读的文件。
     * 因此，即使文件没有读取权限，此方法也可能返回 true
     * @return  <code>true</code> if and only if the file specified by this
     *          abstract pathname exists <em>and</em> can be read by the
     *          application; <code>false</code> otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *          method denies read access to the file
     */
    public boolean canRead() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.checkAccess(this, FileSystem.ACCESS_READ);
    }

    /**
     * Tests whether the application can modify the file denoted by this
     * abstract pathname. On some platforms it may be possible to start the
     * Java virtual machine with special privileges that allow it to modify
     * files that are marked read-only. Consequently this method may return
     * {@code true} even though the file is marked read-only.
     * 1.测试应用程序是否可以修改此抽象路径名表示的文件。在某些平台上，
     * 可以使用特殊权限启动 Java 虚拟机，允许它修改标记为只读的文件。
     * 因此，即使文件被标记为只读，此方法也可能返回 true
     * @return  <code>true</code> if and only if the file system actually
     *          contains a file denoted by this abstract pathname <em>and</em>
     *          the application is allowed to write to the file;
     *          <code>false</code> otherwise.
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the file
     */
    public boolean canWrite() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.checkAccess(this, FileSystem.ACCESS_WRITE);
    }

    /**
     * Tests whether the file or directory denoted by this abstract pathname
     * exists.
     * 测试此抽象路径名表示的文件或目录是否存在
     *
     * @return  <code>true</code> if and only if the file or directory denoted
     *          by this abstract pathname exists; <code>false</code> otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *          method denies read access to the file or directory
     */
    public boolean exists() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return ((fs.getBooleanAttributes(this) & FileSystem.BA_EXISTS) != 0);
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a
     * directory.
     * 1.测试此抽象路径名表示的文件是否为目录
     *
     * <p> Where it is required to distinguish an I/O exception from the case
     * that the file is not a directory, or where several attributes of the
     * same file are required at the same time, then the {@link
     * java.nio.file.Files#readAttributes(Path,Class,LinkOption[])
     * Files.readAttributes} method may be used.
     * 2.如果需要区分IO异常和文件不是目录的情况，或者同时需要同一个文件的几个属性，
     * 那么java.nio.file.Files.readAttributes(Path, Class,LinkOption[]) 方法可以使用
     * @return <code>true</code> if and only if the file denoted by this
     *          abstract pathname exists <em>and</em> is a directory;
     *          <code>false</code> otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *          method denies read access to the file
     */
    public boolean isDirectory() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return ((fs.getBooleanAttributes(this) & FileSystem.BA_DIRECTORY)
                != 0);
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a normal
     * file.  A file is <em>normal</em> if it is not a directory and, in
     * addition, satisfies other system-dependent criteria.  Any non-directory
     * file created by a Java application is guaranteed to be a normal file.
     * 1.测试此抽象路径名表示的文件是否为普通文件。如果文件不是目录，并且还满足其他依赖于系统的标准，
     * 则该文件是普通。 Java 应用程序创建的任何非目录文件都保证是普通文件
     * <p> Where it is required to distinguish an I/O exception from the case
     * that the file is not a normal file, or where several attributes of the
     * same file are required at the same time, then the {@link
     * java.nio.file.Files#readAttributes(Path,Class,LinkOption[])
     * Files.readAttributes} method may be used.
     * 2.如果需要区分IO异常和文件不是普通文件的情况，或者同时需要同一个文件的几个属性，
     * 那么java.nio.file.Files.readAttributes(Path ,Class,LinkOption[]) 方法可以使用
     * @return  <code>true</code> if and only if the file denoted by this
     *          abstract pathname exists <em>and</em> is a normal file;
     *          <code>false</code> otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *          method denies read access to the file
     */
    public boolean isFile() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return ((fs.getBooleanAttributes(this) & FileSystem.BA_REGULAR) != 0);
    }

    /**
     * Tests whether the file named by this abstract pathname is a hidden
     * file.  The exact definition of <em>hidden</em> is system-dependent.  On
     * UNIX systems, a file is considered to be hidden if its name begins with
     * a period character (<code>'.'</code>).  On Microsoft Windows systems, a file is
     * considered to be hidden if it has been marked as such in the filesystem.
     * 测试由此抽象路径名命名的文件是否为隐藏文件。hidden的确切定义取决于系统。
     * 在 UNIX 系统上，如果文件名称以句点字符 ('.') 开头，则该文件被认为是隐藏的。
     * 在 Microsoft Windows 系统上，如果文件在文件系统中被标记为隐藏文件，则该文件被认为是隐藏的
     * @return  <code>true</code> if and only if the file denoted by this
     *          abstract pathname is hidden according to the conventions of the
     *          underlying platform
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *          method denies read access to the file
     *
     * @since 1.2
     */
    public boolean isHidden() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return ((fs.getBooleanAttributes(this) & FileSystem.BA_HIDDEN) != 0);
    }

    /**
     * Returns the time that the file denoted by this abstract pathname was
     * last modified.
     * 1.返回此抽象路径名表示的文件上次修改的时间。
     * <p> Where it is required to distinguish an I/O exception from the case
     * where {@code 0L} is returned, or where several attributes of the
     * same file are required at the same time, or where the time of last
     * access or the creation time are required, then the {@link
     * java.nio.file.Files#readAttributes(Path,Class,LinkOption[])
     * Files.readAttributes} method may be used.
     * 2.需要区分IO异常和返回0L的情况，或者同时需要同一个文件的多个属性的情况，
     * 或者需要上次访问时间或创建时间的情况，然后可以使用java.nio.file.Files.readAttributes(Path,Class,LinkOption[])
     * 方法
     * @return  A <code>long</code> value representing the time the file was
     *          last modified, measured in milliseconds since the epoch
     *          (00:00:00 GMT, January 1, 1970), or <code>0L</code> if the
     *          file does not exist or if an I/O error occurs
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *          method denies read access to the file
     */
    public long lastModified() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getLastModifiedTime(this);
    }

    /**
     * Returns the length of the file denoted by this abstract pathname.
     * The return value is unspecified if this pathname denotes a directory.
     * 1.返回由此抽象路径名表示的文件的长度。如果此路径名表示目录，则返回值未指定
     * <p> Where it is required to distinguish an I/O exception from the case
     * that {@code 0L} is returned, or where several attributes of the same file
     * are required at the same time, then the {@link
     * java.nio.file.Files#readAttributes(Path,Class,LinkOption[])
     * Files.readAttributes} method may be used.
     * 2.如果需要区分IO异常和返回0L的情况，或者同时需要同一个文件的多个属性，
     * 那么java.nio.file.Files.readAttributes(Path ,Class,LinkOption[])方法可以使用
     * @return  The length, in bytes, of the file denoted by this abstract
     *          pathname, or <code>0L</code> if the file does not exist.  Some
     *          operating systems may return <code>0L</code> for pathnames
     *          denoting system-dependent entities such as devices or pipes.
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *          method denies read access to the file
     */
    public long length() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getLength(this);
    }


    /* -- File operations -- */
    //文件操作

    /**
     * Atomically creates a new, empty file named by this abstract pathname if
     * and only if a file with this name does not yet exist.  The check for the
     * existence of the file and the creation of the file if it does not exist
     * are a single operation that is atomic with respect to all other
     * filesystem activities that might affect the file.
     * 1.当且仅当具有此名称的文件尚不存在时，以原子方式创建以该抽象路径名命名的新的空文件。
     * 检查文件是否存在以及如果文件不存在则创建文件是单个操作，对于可能影响该文件的所有其他文件系统活动而言是原子操作。
     * <P>
     * Note: this method should <i>not</i> be used for file-locking, as
     * the resulting protocol cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock}
     * facility should be used instead.
     * 2.注意：此方法应该不用于文件锁定，因为无法使生成的协议可靠地工作。应改用java.nio.channels.FileLock工具。
     * @return  <code>true</code> if the named file does not exist and was
     *          successfully created; <code>false</code> if the named file
     *          already exists
     *
     * @throws  IOException
     *          If an I/O error occurred
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the file
     *
     * @since 1.2
     */
    public boolean createNewFile() throws IOException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) security.checkWrite(path);
        if (isInvalid()) {
            throw new IOException("Invalid file path");
        }
        return fs.createFileExclusively(path);
    }

    /**
     * Deletes the file or directory denoted by this abstract pathname.  If
     * this pathname denotes a directory, then the directory must be empty in
     * order to be deleted.
     * 1.删除此抽象路径名表示的文件或目录。如果此路径名表示一个目录，则该目录必须为空才能被删除
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#delete(Path) delete} method to throw an {@link IOException}
     * when a file cannot be deleted. This is useful for error reporting and to
     * diagnose why a file cannot be deleted.
     * 2.请注意，java.nio.file.Files类定义了java.nio.file.Files.delete(Path)
     * 方法以在无法删除文件时抛出IOException。这对于错误报告和诊断无法删除文件的原因很有用
     * @return  <code>true</code> if and only if the file or directory is
     *          successfully deleted; <code>false</code> otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkDelete}</code> method denies
     *          delete access to the file
     */
    public boolean delete() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkDelete(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.delete(this);
    }

    /**
     * Requests that the file or directory denoted by this abstract
     * pathname be deleted when the virtual machine terminates.
     * Files (or directories) are deleted in the reverse order that
     * they are registered. Invoking this method to delete a file or
     * directory that is already registered for deletion has no effect.
     * Deletion will be attempted only for normal termination of the
     * virtual machine, as defined by the Java Language Specification.
     * 1.请求在虚拟机终止时删除此抽象路径名表示的文件或目录。文件（或目录）的删除顺序与它们注册的顺序相反。
     * 调用此方法删除已注册删除的文件或目录无效。仅在虚拟机正常终止时才会尝试删除，如 Java 语言规范所定义
     * <p> Once deletion has been requested, it is not possible to cancel the
     * request.  This method should therefore be used with care.
     * 2.一旦请求删除，就无法取消请求。因此，应谨慎使用此方法
     * <P>
     * Note: this method should <i>not</i> be used for file-locking, as
     * the resulting protocol cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock}
     * facility should be used instead.
     * 3.注意：此方法应该不用于文件锁定，因为无法使生成的协议可靠地工作。应改用java.nio.channels.FileLock工具
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkDelete}</code> method denies
     *          delete access to the file
     *
     * @see #delete
     *
     * @since 1.2
     */
    public void deleteOnExit() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkDelete(path);
        }
        if (isInvalid()) {
            return;
        }
        DeleteOnExitHook.add(path);
    }

    /**
     * Returns an array of strings naming the files and directories in the
     * directory denoted by this abstract pathname.
     * 1.返回命名此抽象路径名表示的目录中的文件和目录的字符串数组。
     * <p> If this abstract pathname does not denote a directory, then this
     * method returns {@code null}.  Otherwise an array of strings is
     * returned, one for each file or directory in the directory.  Names
     * denoting the directory itself and the directory's parent directory are
     * not included in the result.  Each string is a file name rather than a
     * complete path.
     * 2.如果此抽象路径名不表示目录，则此方法返回null。否则返回一个字符串数组，目录中的每个文件或目录一个。
     * 结果中不包括表示目录本身和目录的父目录的名称。每个字符串都是一个文件名，而不是一个完整的路径
     * <p> There is no guarantee that the name strings in the resulting array
     * will appear in any specific order; they are not, in particular,
     * guaranteed to appear in alphabetical order.
     * 3.无法保证结果数组中的名称字符串会以任何特定顺序出现；特别是，它们不能保证按字母顺序出现
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#newDirectoryStream(Path) newDirectoryStream} method to
     * open a directory and iterate over the names of the files in the directory.
     * This may use less resources when working with very large directories, and
     * may be more responsive when working with remote directories.
     * 4.请注意，java.nio.file.Files类定义了java.nio.file.Files.newDirectoryStream(Path)
     * 方法以打开目录并遍历目录中的文件名。这在处理非常大的目录时可能会使用较少的资源，而在处理远程目录时可能会更灵敏
     * @return  An array of strings naming the files and directories in the
     *          directory denoted by this abstract pathname.  The array will be
     *          empty if the directory is empty.  Returns {@code null} if
     *          this abstract pathname does not denote a directory, or if an
     *          I/O error occurs.
     *
     * @throws  SecurityException
     *          If a security manager exists and its {@link
     *          SecurityManager#checkRead(String)} method denies read access to
     *          the directory
     */
    public String[] list() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return null;
        }
        return fs.list(this);
    }

    /**
     * Returns an array of strings naming the files and directories in the
     * directory denoted by this abstract pathname that satisfy the specified
     * filter.  The behavior of this method is the same as that of the
     * {@link #list()} method, except that the strings in the returned array
     * must satisfy the filter.  If the given {@code filter} is {@code null}
     * then all names are accepted.  Otherwise, a name satisfies the filter if
     * and only if the value {@code true} results when the {@link
     * FilenameFilter#accept FilenameFilter.accept(File,&nbsp;String)} method
     * of the filter is invoked on this abstract pathname and the name of a
     * file or directory in the directory that it denotes.
     *返回命名由该抽象路径名表示的目录中满足指定过滤器的文件和目录的字符串数组。
     * 此方法的行为与 list()方法相同，只是返回数组中的字符串必须满足过滤器。
     * 如果给定的filter是 null，则接受所有名称。否则，当且仅当在此抽象路径名和文件名上调用过滤器的
     * FilenameFilter.accept(File, String)} 方法时产生值true时，名称满足过滤器或它表示的目录中的目录
     * @param  filter
     *         A filename filter
     *
     * @return  An array of strings naming the files and directories in the
     *          directory denoted by this abstract pathname that were accepted
     *          by the given {@code filter}.  The array will be empty if the
     *          directory is empty or if no names were accepted by the filter.
     *          Returns {@code null} if this abstract pathname does not denote
     *          a directory, or if an I/O error occurs.
     *
     * @throws  SecurityException
     *          If a security manager exists and its {@link
     *          SecurityManager#checkRead(String)} method denies read access to
     *          the directory
     *
     * @see java.nio.file.Files#newDirectoryStream(Path,String)
     */
    public String[] list(FilenameFilter filter) {
        String names[] = list();
        if ((names == null) || (filter == null)) {
            return names;
        }
        List<String> v = new ArrayList<>();
        for (int i = 0 ; i < names.length ; i++) {
            if (filter.accept(this, names[i])) {
                v.add(names[i]);
            }
        }
        return v.toArray(new String[v.size()]);
    }

    /**
     * Returns an array of abstract pathnames denoting the files in the
     * directory denoted by this abstract pathname.
     * 1.返回一组抽象路径名，表示此抽象路径名所表示的目录中的文件
     * <p> If this abstract pathname does not denote a directory, then this
     * method returns {@code null}.  Otherwise an array of {@code File} objects
     * is returned, one for each file or directory in the directory.  Pathnames
     * denoting the directory itself and the directory's parent directory are
     * not included in the result.  Each resulting abstract pathname is
     * constructed from this abstract pathname using the {@link #File(File,
     * String) File(File,&nbsp;String)} constructor.  Therefore if this
     * pathname is absolute then each resulting pathname is absolute; if this
     * pathname is relative then each resulting pathname will be relative to
     * the same directory.
     * 2.如果此抽象路径名不表示目录，则此方法返回null。否则将返回一组File对象，
     * 目录中的每个文件或目录对应一个。结果中不包含表示目录本身和目录的父目录的路径名。
     * 每个生成的抽象路径名都是使用File(File, String)构造函数从此抽象路径名构造的。
     * 因此，如果这个路径名是绝对的，那么每个结果路径名都是绝对的；
     * 如果此路径名是相对的，则每个结果路径名都将相对于同一目录。
     * <p> There is no guarantee that the name strings in the resulting array
     * will appear in any specific order; they are not, in particular,
     * guaranteed to appear in alphabetical order.
     * 3.无法保证结果数组中的名称字符串会以任何特定顺序出现；特别是，它们不能保证按字母顺序出现
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#newDirectoryStream(Path) newDirectoryStream} method
     * to open a directory and iterate over the names of the files in the
     * directory. This may use less resources when working with very large
     * directories.
     * 4.请注意，java.nio.file.Files类定义了java.nio.file.Files.newDirectoryStream(Path)
     * 方法以打开目录并遍历目录中的文件名。在处理非常大的目录时，这可能会使用较少的资源。
     * @return  An array of abstract pathnames denoting the files and
     *          directories in the directory denoted by this abstract pathname.
     *          The array will be empty if the directory is empty.  Returns
     *          {@code null} if this abstract pathname does not denote a
     *          directory, or if an I/O error occurs.
     *
     * @throws  SecurityException
     *          If a security manager exists and its {@link
     *          SecurityManager#checkRead(String)} method denies read access to
     *          the directory
     *
     * @since  1.2
     */
    public File[] listFiles() {
        String[] ss = list();
        if (ss == null) return null;
        int n = ss.length;
        File[] fs = new File[n];
        for (int i = 0; i < n; i++) {
            fs[i] = new File(ss[i], this);
        }
        return fs;
    }

    /**
     * Returns an array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname that
     * satisfy the specified filter.  The behavior of this method is the same
     * as that of the {@link #listFiles()} method, except that the pathnames in
     * the returned array must satisfy the filter.  If the given {@code filter}
     * is {@code null} then all pathnames are accepted.  Otherwise, a pathname
     * satisfies the filter if and only if the value {@code true} results when
     * the {@link FilenameFilter#accept
     * FilenameFilter.accept(File,&nbsp;String)} method of the filter is
     * invoked on this abstract pathname and the name of a file or directory in
     * the directory that it denotes.
     * 返回一组抽象路径名，表示此抽象路径名所表示的目录中满足指定过滤器的文件和目录。
     * 此方法的行为与 listFiles()方法的行为相同，只是返回数组中的路径名必须满足过滤器。
     * 如果给定的 filter是 null，则接受所有路径名。否则，当且仅当在此抽象路径名和文件名上调用过滤器的
     * FilenameFilter.accept(File, String)方法时结果值true时，路径名满足过滤器或它表示的目录中的目录
     * @param  filter
     *         A filename filter
     *
     * @return  An array of abstract pathnames denoting the files and
     *          directories in the directory denoted by this abstract pathname.
     *          The array will be empty if the directory is empty.  Returns
     *          {@code null} if this abstract pathname does not denote a
     *          directory, or if an I/O error occurs.
     *
     * @throws  SecurityException
     *          If a security manager exists and its {@link
     *          SecurityManager#checkRead(String)} method denies read access to
     *          the directory
     *
     * @since  1.2
     * @see java.nio.file.Files#newDirectoryStream(Path,String)
     */
    public File[] listFiles(FilenameFilter filter) {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<File> files = new ArrayList<>();
        for (String s : ss)
            if ((filter == null) || filter.accept(this, s))
                files.add(new File(s, this));
        return files.toArray(new File[files.size()]);
    }

    /**
     * Returns an array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname that
     * satisfy the specified filter.  The behavior of this method is the same
     * as that of the {@link #listFiles()} method, except that the pathnames in
     * the returned array must satisfy the filter.  If the given {@code filter}
     * is {@code null} then all pathnames are accepted.  Otherwise, a pathname
     * satisfies the filter if and only if the value {@code true} results when
     * the {@link FileFilter#accept FileFilter.accept(File)} method of the
     * filter is invoked on the pathname.
     * 返回一组抽象路径名，表示此抽象路径名所表示的目录中满足指定过滤器的文件和目录。
     * 此方法的行为与listFiles()方法的行为相同，只是返回数组中的路径名必须满足过滤器。
     * 如果给定的filter是null，则接受所有路径名。否则，当且仅当在路径名上调用过滤器的
     * FileFilter.accept(File)方法时产生值true时，路径名才满足过滤器
     * @param  filter
     *         A file filter
     *
     * @return  An array of abstract pathnames denoting the files and
     *          directories in the directory denoted by this abstract pathname.
     *          The array will be empty if the directory is empty.  Returns
     *          {@code null} if this abstract pathname does not denote a
     *          directory, or if an I/O error occurs.
     *
     * @throws  SecurityException
     *          If a security manager exists and its {@link
     *          SecurityManager#checkRead(String)} method denies read access to
     *          the directory
     *
     * @since  1.2
     * @see java.nio.file.Files#newDirectoryStream(Path,java.nio.file.DirectoryStream.Filter)
     */
    public File[] listFiles(FileFilter filter) {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<File> files = new ArrayList<>();
        for (String s : ss) {
            File f = new File(s, this);
            if ((filter == null) || filter.accept(f))
                files.add(f);
        }
        return files.toArray(new File[files.size()]);
    }

    /**
     * Creates the directory named by this abstract pathname.
     * 创建由此抽象路径名命名的目录
     * @return  <code>true</code> if and only if the directory was
     *          created; <code>false</code> otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method does not permit the named directory to be created
     */
    public boolean mkdir() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.createDirectory(this);
    }

    /**
     * Creates the directory named by this abstract pathname, including any
     * necessary but nonexistent parent directories.  Note that if this
     * operation fails it may have succeeded in creating some of the necessary
     * parent directories.
     * 创建由此抽象路径名命名的目录，包括任何必要但不存在的父目录。
     * 请注意，如果此操作失败，则它可能已成功创建了一些必要的父目录。
     * @return  <code>true</code> if and only if the directory was created,
     *          along with all necessary parent directories; <code>false</code>
     *          otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *          method does not permit verification of the existence of the
     *          named directory and all necessary parent directories; or if
     *          the <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method does not permit the named directory and all necessary
     *          parent directories to be created
     */
    public boolean mkdirs() {
        if (exists()) {
            return false;
        }
        if (mkdir()) {
            return true;
        }
        File canonFile = null;
        try {
            canonFile = getCanonicalFile();
        } catch (IOException e) {
            return false;
        }

        File parent = canonFile.getParentFile();
        return (parent != null && (parent.mkdirs() || parent.exists()) &&
                canonFile.mkdir());
    }

    /**
     * Renames the file denoted by this abstract pathname.
     * 1重命名由此抽象路径名表示的文件。
     * <p> Many aspects of the behavior of this method are inherently
     * platform-dependent: The rename operation might not be able to move a
     * file from one filesystem to another, it might not be atomic, and it
     * might not succeed if a file with the destination abstract pathname
     * already exists.  The return value should always be checked to make sure
     * that the rename operation was successful.
     * 2.此方法的行为的许多方面本质上是平台相关的：重命名操作可能无法将文件从一个文件系统移动到另一个文件系统，
     * 它可能不是原子的，如果具有目标抽象路径名的文件可能不会成功已经存在。应始终检查返回值以确保重命名操作成功
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#move move} method to move or rename a file in a
     * platform independent manner.
     * 3.请注意，java.nio.file.Files类定义了java.nio.file.Files.move方法以独立于平台的方式移动或重命名文件
     * @param  dest  The new abstract pathname for the named file
     *
     * @return  <code>true</code> if and only if the renaming succeeded;
     *          <code>false</code> otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to either the old or new pathnames
     *
     * @throws  NullPointerException
     *          If parameter <code>dest</code> is <code>null</code>
     */
    public boolean renameTo(File dest) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
            security.checkWrite(dest.path);
        }
        if (dest == null) {
            throw new NullPointerException();
        }
        if (this.isInvalid() || dest.isInvalid()) {
            return false;
        }
        return fs.rename(this, dest);
    }

    /**
     * Sets the last-modified time of the file or directory named by this
     * abstract pathname.
     * 1.设置由此抽象路径名命名的文件或目录的最后修改时间。
     * <p> All platforms support file-modification times to the nearest second,
     * but some provide more precision.  The argument will be truncated to fit
     * the supported precision.  If the operation succeeds and no intervening
     * operations on the file take place, then the next invocation of the
     * <code>{@link #lastModified}</code> method will return the (possibly
     * truncated) <code>time</code> argument that was passed to this method.
     * 2.所有平台都支持精确到秒的文件修改时间，但有些平台提供更高的精度。参数将被截断以适合支持的精度。
     * 如果操作成功并且没有对文件进行中间操作，则lastModified方法的下一次调用将返回（可能被截断的）time传递的参数到这个方法
     * @param  time  The new last-modified time, measured in milliseconds since
     *               the epoch (00:00:00 GMT, January 1, 1970)
     *
     * @return <code>true</code> if and only if the operation succeeded;
     *          <code>false</code> otherwise
     *
     * @throws  IllegalArgumentException  If the argument is negative
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the named file
     *
     * @since 1.2
     */
    public boolean setLastModified(long time) {
        if (time < 0) throw new IllegalArgumentException("Negative time");
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setLastModifiedTime(this, time);
    }

    /**
     * Marks the file or directory named by this abstract pathname so that
     * only read operations are allowed. After invoking this method the file
     * or directory will not change until it is either deleted or marked
     * to allow write access. On some platforms it may be possible to start the
     * Java virtual machine with special privileges that allow it to modify
     * files that are marked read-only. Whether or not a read-only file or
     * directory may be deleted depends upon the underlying system.
     * 1.标记由此抽象路径名命名的文件或目录，以便只允许读取操作。调用此方法后，文件或目录将不会更改，
     * 直到将其删除或标记为允许写访问。在某些平台上，可以使用特殊权限启动 Java 虚拟机，
     * 允许它修改标记为只读的文件。是否可以删除只读文件或目录取决于底层系统
     * @return <code>true</code> if and only if the operation succeeded;
     *          <code>false</code> otherwise
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the named file
     *
     * @since 1.2
     */
    public boolean setReadOnly() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setReadOnly(this);
    }

    /**
     * Sets the owner's or everybody's write permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to modify files that
     * disallow write operations.
     * 1.设置所有者或每个人对此抽象路径名的写权限。在某些平台上，可以使用特殊权限启动 Java 虚拟机，允许它修改禁止写操作的文件。
     * <p> The {@link java.nio.file.Files} class defines methods that operate on
     * file attributes including file permissions. This may be used when finer
     * manipulation of file permissions is required.
     * 2.java.nio.file.Files类定义了对文件属性（包括文件权限）进行操作的方法。这可以在需要更精细地操作文件权限时使用
     * @param   writable
     *          If <code>true</code>, sets the access permission to allow write
     *          operations; if <code>false</code> to disallow write operations
     *
     * @param   ownerOnly
     *          If <code>true</code>, the write permission applies only to the
     *          owner's write permission; otherwise, it applies to everybody.  If
     *          the underlying file system can not distinguish the owner's write
     *          permission from that of others, then the permission will apply to
     *          everybody, regardless of this value.
     *
     * @return  <code>true</code> if and only if the operation succeeded. The
     *          operation will fail if the user does not have permission to change
     *          the access permissions of this abstract pathname.
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the named file
     *
     * @since 1.6
     */
    public boolean setWritable(boolean writable, boolean ownerOnly) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setPermission(this, FileSystem.ACCESS_WRITE, writable, ownerOnly);
    }

    /**
     * A convenience method to set the owner's write permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to modify files that
     * disallow write operations.
     * 1.设置所有者对此抽象路径名的写权限的便捷方法。在某些平台上，可以使用特殊权限启动 Java 虚拟机，允许它修改禁止写操作的文件
     * <p> An invocation of this method of the form <tt>file.setWritable(arg)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     file.setWritable(arg, true) </pre>
     * 2.以file.setWritable(arg)形式调用此方法的行为与调用file.setWritable(arg, true) 完全相同
     * @param   writable
     *          If <code>true</code>, sets the access permission to allow write
     *          operations; if <code>false</code> to disallow write operations
     *
     * @return  <code>true</code> if and only if the operation succeeded.  The
     *          operation will fail if the user does not have permission to
     *          change the access permissions of this abstract pathname.
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the file
     *
     * @since 1.6
     */
    public boolean setWritable(boolean writable) {
        return setWritable(writable, true);
    }

    /**
     * Sets the owner's or everybody's read permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to read files that are
     * marked as unreadable.
     * 1.设置所有者或每个人对此抽象路径名的读取权限。在某些平台上，可以使用特殊权限启动 Java 虚拟机，允许它读取标记为不可读的文件
     * <p> The {@link java.nio.file.Files} class defines methods that operate on
     * file attributes including file permissions. This may be used when finer
     * manipulation of file permissions is required.
     * 2.java.nio.file.Files类定义了对文件属性（包括文件权限）进行操作的方法。这可以在需要更精细地操作文件权限时使用
     * @param   readable
     *          If <code>true</code>, sets the access permission to allow read
     *          operations; if <code>false</code> to disallow read operations
     *
     * @param   ownerOnly
     *          If <code>true</code>, the read permission applies only to the
     *          owner's read permission; otherwise, it applies to everybody.  If
     *          the underlying file system can not distinguish the owner's read
     *          permission from that of others, then the permission will apply to
     *          everybody, regardless of this value.
     *
     * @return  <code>true</code> if and only if the operation succeeded.  The
     *          operation will fail if the user does not have permission to
     *          change the access permissions of this abstract pathname.  If
     *          <code>readable</code> is <code>false</code> and the underlying
     *          file system does not implement a read permission, then the
     *          operation will fail.
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the file
     *
     * @since 1.6
     */
    public boolean setReadable(boolean readable, boolean ownerOnly) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setPermission(this, FileSystem.ACCESS_READ, readable, ownerOnly);
    }

    /**
     * A convenience method to set the owner's read permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to read files that that are
     * marked as unreadable.
     * 1.设置所有者对此抽象路径名的读取权限的便捷方法。在某些平台上，可以使用特殊权限启动 Java 虚拟机，允许它读取标记为不可读的文件
     * <p>An invocation of this method of the form <tt>file.setReadable(arg)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     file.setReadable(arg, true) </pre>
     * 2.以file.setReadable(arg)形式调用此方法的行为与调用file.setReadable(arg, true)完全相同
     * @param  readable
     *          If <code>true</code>, sets the access permission to allow read
     *          operations; if <code>false</code> to disallow read operations
     *
     * @return  <code>true</code> if and only if the operation succeeded.  The
     *          operation will fail if the user does not have permission to
     *          change the access permissions of this abstract pathname.  If
     *          <code>readable</code> is <code>false</code> and the underlying
     *          file system does not implement a read permission, then the
     *          operation will fail.
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the file
     *
     * @since 1.6
     */
    public boolean setReadable(boolean readable) {
        return setReadable(readable, true);
    }

    /**
     * Sets the owner's or everybody's execute permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to execute files that are
     * not marked executable.
     * 1.为这个抽象路径名设置所有者或每个人的执行权限。在某些平台上，可以使用特殊权限启动 Java 虚拟机，允许它执行未标记为可执行文件的文件
     * <p> The {@link java.nio.file.Files} class defines methods that operate on
     * file attributes including file permissions. This may be used when finer
     * manipulation of file permissions is required.
     * 2.java.nio.file.Files类定义了对文件属性（包括文件权限）进行操作的方法。这可以在需要更精细地操作文件权限时使用
     * @param   executable
     *          If <code>true</code>, sets the access permission to allow execute
     *          operations; if <code>false</code> to disallow execute operations
     *
     * @param   ownerOnly
     *          If <code>true</code>, the execute permission applies only to the
     *          owner's execute permission; otherwise, it applies to everybody.
     *          If the underlying file system can not distinguish the owner's
     *          execute permission from that of others, then the permission will
     *          apply to everybody, regardless of this value.
     *
     * @return  <code>true</code> if and only if the operation succeeded.  The
     *          operation will fail if the user does not have permission to
     *          change the access permissions of this abstract pathname.  If
     *          <code>executable</code> is <code>false</code> and the underlying
     *          file system does not implement an execute permission, then the
     *          operation will fail.
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the file
     *
     * @since 1.6
     */
    public boolean setExecutable(boolean executable, boolean ownerOnly) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setPermission(this, FileSystem.ACCESS_EXECUTE, executable, ownerOnly);
    }

    /**
     * A convenience method to set the owner's execute permission for this
     * abstract pathname. On some platforms it may be possible to start the Java
     * virtual machine with special privileges that allow it to execute files
     * that are not marked executable.
     * 1.设置所有者对此抽象路径名的执行权限的便捷方法。在某些平台上，可以使用特殊权限启动 Java 虚拟机，
     * 允许它执行未标记为可执行文件的文件。
     * <p>An invocation of this method of the form <tt>file.setExcutable(arg)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     file.setExecutable(arg, true) </pre>
     * 2.以file.setExcutable(arg)形式调用此方法的行为与调用file.setExecutable(arg, true)完全相同
     * @param   executable
     *          If <code>true</code>, sets the access permission to allow execute
     *          operations; if <code>false</code> to disallow execute operations
     *
     * @return   <code>true</code> if and only if the operation succeeded.  The
     *           operation will fail if the user does not have permission to
     *           change the access permissions of this abstract pathname.  If
     *           <code>executable</code> is <code>false</code> and the underlying
     *           file system does not implement an execute permission, then the
     *           operation will fail.
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method denies write access to the file
     *
     * @since 1.6
     */
    public boolean setExecutable(boolean executable) {
        return setExecutable(executable, true);
    }

    /**
     * Tests whether the application can execute the file denoted by this
     * abstract pathname. On some platforms it may be possible to start the
     * Java virtual machine with special privileges that allow it to execute
     * files that are not marked executable. Consequently this method may return
     * {@code true} even though the file does not have execute permissions.
     * 测试应用程序是否可以执行此抽象路径名表示的文件。
     * 在某些平台上，可以使用特殊权限启动 Java 虚拟机，允许它执行未标记为可执行文件的文件。
     * 因此，即使文件没有执行权限，此方法也可能返回true。
     * @return  <code>true</code> if and only if the abstract pathname exists
     *          <em>and</em> the application is allowed to execute the file
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkExec(java.lang.String)}</code>
     *          method denies execute access to the file
     *
     * @since 1.6
     */
    public boolean canExecute() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkExec(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.checkAccess(this, FileSystem.ACCESS_EXECUTE);
    }


    /* -- Filesystem interface -- */
    //文件系统接口

    /**
     * List the available filesystem roots.
     * 1.列出可用的文件系统根
     * <p> A particular Java platform may support zero or more
     * hierarchically-organized file systems.  Each file system has a
     * {@code root} directory from which all other files in that file system
     * can be reached.  Windows platforms, for example, have a root directory
     * for each active drive; UNIX platforms have a single root directory,
     * namely {@code "/"}.  The set of available filesystem roots is affected
     * by various system-level operations such as the insertion or ejection of
     * removable media and the disconnecting or unmounting of physical or
     * virtual disk drives.
     * 2.一个特定的 Java 平台可能支持零个或多个分层组织的文件系统。每个文件系统都有一个root目录，
     * 可以从中访问该文件系统中的所有其他文件。例如，Windows 平台为每个活动驱动器都有一个根目录；
     * UNIX 平台有一个根目录，即 "/"。可用的文件系统根集会受到各种系统级操作的影响，
     * 例如可移动媒体的插入或弹出以及物理或虚拟磁盘驱动器的断开或卸载
     * <p> This method returns an array of {@code File} objects that denote the
     * root directories of the available filesystem roots.  It is guaranteed
     * that the canonical pathname of any file physically present on the local
     * machine will begin with one of the roots returned by this method.
     * 3.此方法返回一个File对象数组，这些对象表示可用文件系统根的根目录。
     * 可以保证本地机器上物理存在的任何文件的规范路径名都以该方法返回的根之一开始
     * <p> The canonical pathname of a file that resides on some other machine
     * and is accessed via a remote-filesystem protocol such as SMB or NFS may
     * or may not begin with one of the roots returned by this method.  If the
     * pathname of a remote file is syntactically indistinguishable from the
     * pathname of a local file then it will begin with one of the roots
     * returned by this method.  Thus, for example, {@code File} objects
     * denoting the root directories of the mapped network drives of a Windows
     * platform will be returned by this method, while {@code File} objects
     * containing UNC pathnames will not be returned by this method.
     * 4.驻留在其他机器上并通过远程文件系统协议（如 SMB 或 NFS）访问的文件的规范路径名
     * 可能会或可能不会以此方法返回的根之一开头。如果远程文件的路径名与本地文件的路径名在语法上无法区分，
     * 那么它将以该方法返回的根之一开始。因此，例如，此方法将返回表示 Windows 平台映射网络驱动器根目录的File对象，
     * 而此方法将不返回包含 UNC 路径名的File对象
     * <p> Unlike most methods in this class, this method does not throw
     * security exceptions.  If a security manager exists and its {@link
     * SecurityManager#checkRead(String)} method denies read access to a
     * particular root directory, then that directory will not appear in the
     * result.
     * 5.与此类中的大多数方法不同，此方法不会引发安全异常。
     * 如果安全管理器存在且其SecurityManager.checkRead(String)方法拒绝对特定根目录的读取访问，
     * 则该目录将不会出现在结果中
     * @return  An array of {@code File} objects denoting the available
     *          filesystem roots, or {@code null} if the set of roots could not
     *          be determined.  The array will be empty if there are no
     *          filesystem roots.
     *
     * @since  1.2
     * @see java.nio.file.FileStore
     */
    public static File[] listRoots() {
        return fs.listRoots();
    }


    /* -- Disk usage -- */
    //磁盘使用情况

    /**
     * Returns the size of the partition <a href="#partName">named</a> by this
     * abstract pathname.
     * 返回此抽象路径名 <a href="partName">named的分区大小。
     * @return  The size, in bytes, of the partition or <tt>0L</tt> if this
     *          abstract pathname does not name a partition
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     *          {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *          or its {@link SecurityManager#checkRead(String)} method denies
     *          read access to the file named by this abstract pathname
     *
     * @since  1.6
     */
    public long getTotalSpace() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getFileSystemAttributes"));
            sm.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getSpace(this, FileSystem.SPACE_TOTAL);
    }

    /**
     * Returns the number of unallocated bytes in the partition <a
     * href="#partName">named</a> by this abstract path name.
     * 1.返回此抽象路径名 <a href="partName">named中未分配的字节数
     * <p> The returned number of unallocated bytes is a hint, but not
     * a guarantee, that it is possible to use most or any of these
     * bytes.  The number of unallocated bytes is most likely to be
     * accurate immediately after this call.  It is likely to be made
     * inaccurate by any external I/O operations including those made
     * on the system outside of this virtual machine.  This method
     * makes no guarantee that write operations to this file system
     * will succeed.
     * 2.返回的未分配字节数是一个提示，但不能保证可以使用大多数或任何这些字节。在
     * 此调用之后，未分配的字节数最有可能是准确的。任何外部 IO 操作（包括在此虚拟机之外的系统上进行的操作）
     * 都可能导致它不准确。此方法不保证对该文件系统的写入操作会成功
     * @return  The number of unallocated bytes on the partition or <tt>0L</tt>
     *          if the abstract pathname does not name a partition.  This
     *          value will be less than or equal to the total file system size
     *          returned by {@link #getTotalSpace}.
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     *          {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *          or its {@link SecurityManager#checkRead(String)} method denies
     *          read access to the file named by this abstract pathname
     *
     * @since  1.6
     */
    public long getFreeSpace() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getFileSystemAttributes"));
            sm.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getSpace(this, FileSystem.SPACE_FREE);
    }

    /**
     * Returns the number of bytes available to this virtual machine on the
     * partition <a href="#partName">named</a> by this abstract pathname.  When
     * possible, this method checks for write permissions and other operating
     * system restrictions and will therefore usually provide a more accurate
     * estimate of how much new data can actually be written than {@link
     * #getFreeSpace}.
     * 1.返回此抽象路径名 <a href="partName">named上此虚拟机可用的字节数。
     * 如果可能，此方法会检查写入权限和其他操作系统限制，因此通常会比getFreeSpace更准确地估计实际可以写入多少新数据。
     * <p> The returned number of available bytes is a hint, but not a
     * guarantee, that it is possible to use most or any of these bytes.  The
     * number of unallocated bytes is most likely to be accurate immediately
     * after this call.  It is likely to be made inaccurate by any external
     * I/O operations including those made on the system outside of this
     * virtual machine.  This method makes no guarantee that write operations
     * to this file system will succeed.
     * 2.返回的可用字节数是一个提示，但不能保证可以使用大多数或任何这些字节。
     * 在此调用之后，未分配的字节数最有可能是准确的。任何外部 IO 操作（
     * 包括在此虚拟机之外的系统上进行的操作）都可能导致它不准确。此方法不保证对该文件系统的写入操作会成功
     * @return  The number of available bytes on the partition or <tt>0L</tt>
     *          if the abstract pathname does not name a partition.  On
     *          systems where this information is not available, this method
     *          will be equivalent to a call to {@link #getFreeSpace}.
     *
     * @throws  SecurityException
     *          If a security manager has been installed and it denies
     *          {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *          or its {@link SecurityManager#checkRead(String)} method denies
     *          read access to the file named by this abstract pathname
     *
     * @since  1.6
     */
    public long getUsableSpace() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getFileSystemAttributes"));
            sm.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getSpace(this, FileSystem.SPACE_USABLE);
    }

    /* -- Temporary files -- */
    //临时文件

    private static class TempDirectory {
        private TempDirectory() { }

        // temporary directory location
        //临时目录位置
        private static final File tmpdir = new File(AccessController
            .doPrivileged(new GetPropertyAction("java.io.tmpdir")));
        static File location() {
            return tmpdir;
        }

        // file name generation
        //文件名生成
        private static final SecureRandom random = new SecureRandom();
        static File generateFile(String prefix, String suffix, File dir)
            throws IOException
        {
            long n = random.nextLong();
            if (n == Long.MIN_VALUE) {
                n = 0;      // corner case
            } else {
                n = Math.abs(n);
            }

            // Use only the file name from the supplied prefix
            //仅使用提供的前缀中的文件名
            prefix = (new File(prefix)).getName();

            String name = prefix + Long.toString(n) + suffix;
            File f = new File(dir, name);
            if (!name.equals(f.getName()) || f.isInvalid()) {
                if (System.getSecurityManager() != null)
                    throw new IOException("Unable to create temporary file");
                else
                    throw new IOException("Unable to create temporary file, " + f);
            }
            return f;
        }
    }

    /**
     * <p> Creates a new empty file in the specified directory, using the
     * given prefix and suffix strings to generate its name.  If this method
     * returns successfully then it is guaranteed that:
     * 1.在指定目录中创建一个新的空文件，使用给定的前缀和后缀字符串生成其名称。如果此方法成功返回，则可以保证：
     * <ol>
     * <li> The file denoted by the returned abstract pathname did not exist
     *      before this method was invoked, and
     * <li> Neither this method nor any of its variants will return the same
     *      abstract pathname again in the current invocation of the virtual
     *      machine.
     * </ol>
     * 2.在调用此方法之前，返回的抽象路径名表示的文件不存在，并且在虚拟机的当前调用中，
     * 此方法及其任何变体都不会再次返回相同的抽象路径名。
     * This method provides only part of a temporary-file facility.  To arrange
     * for a file created by this method to be deleted automatically, use the
     * <code>{@link #deleteOnExit}</code> method.
     * 3.此方法仅提供临时文件工具的一部分。要安排自动删除此方法创建的文件，请使用deleteOnExit方法。
     * <p> The <code>prefix</code> argument must be at least three characters
     * long.  It is recommended that the prefix be a short, meaningful string
     * such as <code>"hjb"</code> or <code>"mail"</code>.  The
     * <code>suffix</code> argument may be <code>null</code>, in which case the
     * suffix <code>".tmp"</code> will be used.
     * 4.prefix参数的长度必须至少为三个字符。建议前缀是一个简短的、有意义的字符串，
     * 例如"hjb"或"mail"。 suffix参数可能是null，在这种情况下，将使用后缀".tmp"
     * <p> To create the new file, the prefix and the suffix may first be
     * adjusted to fit the limitations of the underlying platform.  If the
     * prefix is too long then it will be truncated, but its first three
     * characters will always be preserved.  If the suffix is too long then it
     * too will be truncated, but if it begins with a period character
     * (<code>'.'</code>) then the period and the first three characters
     * following it will always be preserved.  Once these adjustments have been
     * made the name of the new file will be generated by concatenating the
     * prefix, five or more internally-generated characters, and the suffix.
     * 5.要创建新文件，可以首先调整前缀和后缀以适应底层平台的限制。
     * 如果前缀太长，它将被截断，但它的前三个字符将始终保留。
     * 如果后缀太长，那么它也会被截断，但如果它以句点字符 ('.') 开头，那么句点及其后的前三个字符将始终保留。
     * 完成这些调整后，将通过连接前缀、五个或更多内部生成的字符和后缀来生成新文件的名称
     * <p> If the <code>directory</code> argument is <code>null</code> then the
     * system-dependent default temporary-file directory will be used.  The
     * default temporary-file directory is specified by the system property
     * <code>java.io.tmpdir</code>.  On UNIX systems the default value of this
     * property is typically <code>"/tmp"</code> or <code>"/var/tmp"</code>; on
     * Microsoft Windows systems it is typically <code>"C:\\WINNT\\TEMP"</code>.  A different
     * value may be given to this system property when the Java virtual machine
     * is invoked, but programmatic changes to this property are not guaranteed
     * to have any effect upon the temporary directory used by this method.
     * 6.如果directory参数为null，则将使用系统相关的默认临时文件目录。
     * 默认的临时文件目录由系统属性java.io.tmpdir指定。在 UNIX 系统上，
     * 此属性的默认值通常是"tmp"或"vartmp"；在 Microsoft Windows 系统上，它通常是"C:\\WINNT\\TEMP"。
     * 调用 Java 虚拟机时，可能会为此系统属性指定不同的值，但不能保证对此属性的编程更改对此方法使用的临时目录有任何影响
     * @param  prefix     The prefix string to be used in generating the file's
     *                    name; must be at least three characters long
     *
     * @param  suffix     The suffix string to be used in generating the file's
     *                    name; may be <code>null</code>, in which case the
     *                    suffix <code>".tmp"</code> will be used
     *
     * @param  directory  The directory in which the file is to be created, or
     *                    <code>null</code> if the default temporary-file
     *                    directory is to be used
     *
     * @return  An abstract pathname denoting a newly-created empty file
     *
     * @throws  IllegalArgumentException
     *          If the <code>prefix</code> argument contains fewer than three
     *          characters
     *
     * @throws  IOException  If a file could not be created
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method does not allow a file to be created
     *
     * @since 1.2
     */
    public static File createTempFile(String prefix, String suffix,
                                      File directory)
        throws IOException
    {
        if (prefix.length() < 3)
            throw new IllegalArgumentException("Prefix string too short");
        if (suffix == null)
            suffix = ".tmp";

        File tmpdir = (directory != null) ? directory
                                          : TempDirectory.location();
        SecurityManager sm = System.getSecurityManager();
        File f;
        do {
            f = TempDirectory.generateFile(prefix, suffix, tmpdir);

            if (sm != null) {
                try {
                    sm.checkWrite(f.getPath());
                } catch (SecurityException se) {
                    // don't reveal temporary directory location
                    if (directory == null)
                        throw new SecurityException("Unable to create temporary file");
                    throw se;
                }
            }
        } while ((fs.getBooleanAttributes(f) & FileSystem.BA_EXISTS) != 0);

        if (!fs.createFileExclusively(f.getPath()))
            throw new IOException("Unable to create temporary file");

        return f;
    }

    /**
     * Creates an empty file in the default temporary-file directory, using
     * the given prefix and suffix to generate its name. Invoking this method
     * is equivalent to invoking <code>{@link #createTempFile(java.lang.String,
     * java.lang.String, java.io.File)
     * createTempFile(prefix,&nbsp;suffix,&nbsp;null)}</code>.
     * 1.在默认临时文件目录中创建一个空文件，使用给定的前缀和后缀生成其名称。
     * 调用这个方法相当于调用createTempFile(java.lang.String, java.lang.String, java.io.File)
     *
     * <p> The {@link
     * java.nio.file.Files#createTempFile(String,String,java.nio.file.attribute.FileAttribute[])
     * Files.createTempFile} method provides an alternative method to create an
     * empty file in the temporary-file directory. Files created by that method
     * may have more restrictive access permissions to files created by this
     * method and so may be more suited to security-sensitive applications.
     * 2.java.nio.file.FilescreateTempFile(String,String,java.nio.file.attribute.FileAttribute[])
     * 方法提供了一种在临时文件目录中创建空文件的替代方法。通过该方法创建的文件可能对通过该方法创建的文件具有更严格的访问权限，
     * 因此可能更适合对安全敏感的应用程序
     * @param  prefix     The prefix string to be used in generating the file's
     *                    name; must be at least three characters long
     *
     * @param  suffix     The suffix string to be used in generating the file's
     *                    name; may be <code>null</code>, in which case the
     *                    suffix <code>".tmp"</code> will be used
     *
     * @return  An abstract pathname denoting a newly-created empty file
     *
     * @throws  IllegalArgumentException
     *          If the <code>prefix</code> argument contains fewer than three
     *          characters
     *
     * @throws  IOException  If a file could not be created
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method does not allow a file to be created
     *
     * @since 1.2
     * @see java.nio.file.Files#createTempDirectory(String,FileAttribute[])
     */
    public static File createTempFile(String prefix, String suffix)
        throws IOException
    {
        return createTempFile(prefix, suffix, null);
    }

    /* -- Basic infrastructure -- */
    //基础设施

    /**
     * Compares two abstract pathnames lexicographically.  The ordering
     * defined by this method depends upon the underlying system.  On UNIX
     * systems, alphabetic case is significant in comparing pathnames; on Microsoft Windows
     * systems it is not.
     * 1.按字典顺序比较两个抽象路径名。此方法定义的排序取决于底层系统。
     * 在 UNIX 系统上，字母大小写在比较路径名时很重要；在 Microsoft Windows 系统上不是
     * @param   pathname  The abstract pathname to be compared to this abstract
     *                    pathname
     *
     * @return  Zero if the argument is equal to this abstract pathname, a
     *          value less than zero if this abstract pathname is
     *          lexicographically less than the argument, or a value greater
     *          than zero if this abstract pathname is lexicographically
     *          greater than the argument
     *
     * @since   1.2
     */
    public int compareTo(File pathname) {
        return fs.compare(this, pathname);
    }

    /**
     * Tests this abstract pathname for equality with the given object.
     * Returns <code>true</code> if and only if the argument is not
     * <code>null</code> and is an abstract pathname that denotes the same file
     * or directory as this abstract pathname.  Whether or not two abstract
     * pathnames are equal depends upon the underlying system.  On UNIX
     * systems, alphabetic case is significant in comparing pathnames; on Microsoft Windows
     * systems it is not.
     * 测试此抽象路径名是否与给定对象相等。当且仅当参数不是null并且是表示与此抽象路径名相同的文件或目录的抽象路径名时，
     * 才返回true。两个抽象路径名是否相等取决于底层系统。在 UNIX 系统上，
     * 字母大小写在比较路径名时很重要；在 Microsoft Windows 系统上不是
     * @param   obj   The object to be compared with this abstract pathname
     *
     * @return  <code>true</code> if and only if the objects are the same;
     *          <code>false</code> otherwise
     */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof File)) {
            return compareTo((File)obj) == 0;
        }
        return false;
    }

    /**
     * Computes a hash code for this abstract pathname.  Because equality of
     * abstract pathnames is inherently system-dependent, so is the computation
     * of their hash codes.  On UNIX systems, the hash code of an abstract
     * pathname is equal to the exclusive <em>or</em> of the hash code
     * of its pathname string and the decimal value
     * <code>1234321</code>.  On Microsoft Windows systems, the hash
     * code is equal to the exclusive <em>or</em> of the hash code of
     * its pathname string converted to lower case and the decimal
     * value <code>1234321</code>.  Locale is not taken into account on
     * lowercasing the pathname string.
     * 计算此抽象路径名的哈希码。因为抽象路径名的相等性本质上是依赖于系统的，所以它们的哈希码的计算也是如此。
     * 在 UNIX 系统上，抽象路径名的哈希码等于其路径名字符串的哈希码与十进制值1234321的互斥或。
     * 在 Microsoft Windows 系统上，哈希码等于其路径名字符串的哈希码转换为小写和十进制值1234321的互斥或。
     * 小写路径名字符串时不考虑语言环境。
     * @return  A hash code for this abstract pathname
     */
    public int hashCode() {
        return fs.hashCode(this);
    }

    /**
     * Returns the pathname string of this abstract pathname.  This is just the
     * string returned by the <code>{@link #getPath}</code> method.
     * 返回此抽象路径名的路径名字符串。这只是 getPath方法返回的字符串。
     * @return  The string form of this abstract pathname
     */
    public String toString() {
        return getPath();
    }

    /**
     * WriteObject is called to save this filename.
     * The separator character is saved also so it can be replaced
     * in case the path is reconstituted on a different host type.
     * <p>
     *     调用 WriteObject 以保存此文件名。分隔符也被保存，以便在路径在不同的主机类型上重构时可以替换它。
     * @serialData  Default fields followed by separator character.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException
    {
        s.defaultWriteObject();
        s.writeChar(separatorChar); // Add the separator character
    }

    /**
     * readObject is called to restore this filename.
     * The original separator character is read.  If it is different
     * than the separator character on this system, then the old separator
     * is replaced by the local separator.
     * 调用 readObject 来恢复这个文件名。读取原始分隔符。如果它与此系统上的分隔符不同，则旧分隔符将替换为本地分隔符。
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
        ObjectInputStream.GetField fields = s.readFields();
        String pathField = (String)fields.get("path", null);
        char sep = s.readChar(); // read the previous separator char
        if (sep != separatorChar)
            pathField = pathField.replace(sep, separatorChar);
        String path = fs.normalize(pathField);
        UNSAFE.putObject(this, PATH_OFFSET, path);
        UNSAFE.putIntVolatile(this, PREFIX_LENGTH_OFFSET, fs.prefixLength(path));
    }

    private static final long PATH_OFFSET;
    private static final long PREFIX_LENGTH_OFFSET;
    private static final sun.misc.Unsafe UNSAFE;
    static {
        try {
            sun.misc.Unsafe unsafe = sun.misc.Unsafe.getUnsafe();
            PATH_OFFSET = unsafe.objectFieldOffset(
                    File.class.getDeclaredField("path"));
            PREFIX_LENGTH_OFFSET = unsafe.objectFieldOffset(
                    File.class.getDeclaredField("prefixLength"));
            UNSAFE = unsafe;
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }


    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 301077366599181567L;

    // -- Integration with java.nio.file --

    //- 与 java.nio.file 集成 -
    private volatile transient Path filePath;

    /**
     * Returns a {@link Path java.nio.file.Path} object constructed from the
     * this abstract path. The resulting {@code Path} is associated with the
     * {@link java.nio.file.FileSystems#getDefault default-filesystem}.
     * 1.返回从这个抽象路径构造的java.nio.file.Path对象。生成的Path与 java.nio.file.FileSystems.getDefault
     * default-filesystem相关联
     * <p> The first invocation of this method works as if invoking it were
     * equivalent to evaluating the expression:
     * <blockquote><pre>
     * {@link java.nio.file.FileSystems#getDefault FileSystems.getDefault}().{@link
     * java.nio.file.FileSystem#getPath getPath}(this.{@link #getPath getPath}());
     * </pre></blockquote>
     * 2.此方法的第一次调用就好像调用它等效于评估表达式：
     * java.nio.file.FileSystems.getDefault().java.nio.file. FileSystem.getPath
     * (this.getPath ())
     * Subsequent invocations of this method return the same {@code Path}.
     * 3.此方法的后续调用返回相同的 Path。
     * <p> If this abstract pathname is the empty abstract pathname then this
     * method returns a {@code Path} that may be used to access the current
     * user directory.
     * 4.如果此抽象路径名是空抽象路径名，则此方法返回可用于访问当前用户目录的Path
     * @return  a {@code Path} constructed from this abstract path
     *
     * @throws  java.nio.file.InvalidPathException
     *          if a {@code Path} object cannot be constructed from the abstract
     *          path (see {@link java.nio.file.FileSystem#getPath FileSystem.getPath})
     *
     * @since   1.7
     * @see Path#toFile
     */
    public Path toPath() {
        Path result = filePath;
        if (result == null) {
            synchronized (this) {
                result = filePath;
                if (result == null) {
                    result = FileSystems.getDefault().getPath(path);
                    filePath = result;
                }
            }
        }
        return result;
    }
}
