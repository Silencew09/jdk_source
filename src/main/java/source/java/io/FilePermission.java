/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.security.*;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Collections;
import sun.security.util.SecurityConstants;

/**
 * This class represents access to a file or directory.  A FilePermission consists
 * of a pathname and a set of actions valid for that pathname.
 * 1.此类表示对文件或目录的访问。 FilePermission 由路径名和对该路径名有效的一组操作组成。
 * <P>
 * Pathname is the pathname of the file or directory granted the specified
 * actions. A pathname that ends in "/*" (where "/" is
 * the file separator character, <code>File.separatorChar</code>) indicates
 * all the files and directories contained in that directory. A pathname
 * that ends with "/-" indicates (recursively) all files
 * and subdirectories contained in that directory. A pathname consisting of
 * the special token "&lt;&lt;ALL FILES&gt;&gt;" matches <b>any</b> file.
 * 2.Pathname 是授予指定操作的文件或目录的路径名。以“/*”结尾的路径名（其中“/”是文件分隔符，
 * File.separatorChar）表示该目录中包含的所有文件和目录。
 * 以“/-”结尾的路径名表示（递归地）包含在该目录中的所有文件和子目录。
 * 由特殊标记“<<ALL FILES>>”组成的路径名匹配any文件
 * <P>
 * Note: A pathname consisting of a single "*" indicates all the files
 * in the current directory, while a pathname consisting of a single "-"
 * indicates all the files in the current directory and
 * (recursively) all files and subdirectories contained in the current
 * directory.
 * 3.注意：由单个“*”组成的路径名表示当前目录中的所有文件，
 * 而由单个“-”组成的路径名表示当前目录中的所有文件以及（递归地）当前目录中包含的所有文件和子目录.
 * <P>
 * The actions to be granted are passed to the constructor in a string containing
 * a list of one or more comma-separated keywords. The possible keywords are
 * "read", "write", "execute", "delete", and "readlink". Their meaning is
 * defined as follows:
 * 4.要授予的操作以包含一个或多个逗号分隔关键字列表的字符串形式传递给构造函数。
 * 可能的关键字是“read”、“write”、“execute”、“delete”和“readlink”。它们的含义定义如下：
 * <DL>
 *    <DT> read <DD> read permission
 *    <DT> write <DD> write permission
 *    <DT> execute
 *    <DD> execute permission. Allows <code>Runtime.exec</code> to
 *         be called. Corresponds to <code>SecurityManager.checkExec</code>.
 *    <DT> delete
 *    <DD> delete permission. Allows <code>File.delete</code> to
 *         be called. Corresponds to <code>SecurityManager.checkDelete</code>.
 *    <DT> readlink
 *    <DD> read link permission. Allows the target of a
 *         <a href="../nio/file/package-summary.html#links">symbolic link</a>
 *         to be read by invoking the {@link java.nio.file.Files#readSymbolicLink
 *         readSymbolicLink } method.
 * </DL>
 * <P>
 * The actions string is converted to lowercase before processing.
 * 5.在处理之前，操作字符串将转换为小写
 * <P>
 * Be careful when granting FilePermissions. Think about the implications
 * of granting read and especially write access to various files and
 * directories. The "&lt;&lt;ALL FILES&gt;&gt;" permission with write action is
 * especially dangerous. This grants permission to write to the entire
 * file system. One thing this effectively allows is replacement of the
 * system binary, including the JVM runtime environment.
 * 6.授予 FilePermissions 时要小心。考虑授予对各种文件和目录的读取和特别是写入访问权限的含义。
 * 带有写操作的“<<ALL FILES>>”权限特别危险。这授予写入整个文件系统的权限。
 * 这有效地允许的一件事是替换系统二进制文件，包括 JVM 运行时环境
 * <p>Please note: Code can always read a file from the same
 * directory it's in (or a subdirectory of that directory); it does not
 * need explicit permission to do so.
 * 7.请注意：代码始终可以从它所在的同一目录（或该目录的子目录）读取文件；这样做不需要明确许可
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 *
 *
 * @author Marianne Mueller
 * @author Roland Schemers
 * @since 1.2
 *
 * @serial exclude
 */

public final class FilePermission extends Permission implements Serializable {

    /**
     * Execute action.
     * 执行动作
     */
    private final static int EXECUTE = 0x1;
    /**
     * Write action.
     * 写动作
     */
    private final static int WRITE   = 0x2;
    /**
     * Read action.
     * 阅读行动
     */
    private final static int READ    = 0x4;
    /**
     * Delete action.
     * 删除操作
     */
    private final static int DELETE  = 0x8;
    /**
     * Read link action.
     * 读取链接操作
     */
    private final static int READLINK    = 0x10;

    /**
     * All actions (read,write,execute,delete,readlink)
     * 所有操作（读取、写入、执行、删除、读取链接）
     */
    private final static int ALL     = READ|WRITE|EXECUTE|DELETE|READLINK;
    /**
     * No actions.
     * 没有任何动作
     */
    private final static int NONE    = 0x0;

    // the actions mask
    //动作掩码
    private transient int mask;

    // does path indicate a directory? (wildcard or recursive)
    //path 是否表示目录？ （通配符或递归
    private transient boolean directory;

    // is it a recursive directory specification?
    //它是递归目录规范吗？
    private transient boolean recursive;

    /**
     * the actions string.
     * 动作字符串
     * @serial
     */
    private String actions; // Left null as long as possible, then
                            // created and re-used in the getAction function.
    //尽量留空，然后在 getAction 函数中创建和重用

    // canonicalized dir path. In the case of
    // directories, it is the name "/blah/*" or "/blah/-" without
    // the last character (the "*" or "-").
    //规范化的目录路径。在目录的情况下，它是没有最后一个字符（“”或“-”）的名称“/blah/*”或“/blah/-

    private transient String cpath;

    // static Strings used by init(int mask)
    //init(int mask) 使用的静态字符串
    private static final char RECURSIVE_CHAR = '-';
    private static final char WILD_CHAR = '*';

/*
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("***\n");
        sb.append("cpath = "+cpath+"\n");
        sb.append("mask = "+mask+"\n");
        sb.append("actions = "+getActions()+"\n");
        sb.append("directory = "+directory+"\n");
        sb.append("recursive = "+recursive+"\n");
        sb.append("***\n");
        return sb.toString();
    }
*/

    private static final long serialVersionUID = 7930732926638008763L;

    /**
     * initialize a FilePermission object. Common to all constructors.
     * Also called during de-serialization.
     * 初始化一个 FilePermission 对象。所有构造函数通用。在反序列化期间也调用
     *
     * @param mask the actions mask to use.
     *
     */
    private void init(int mask) {
        if ((mask & ALL) != mask)
                throw new IllegalArgumentException("invalid actions mask");

        if (mask == NONE)
                throw new IllegalArgumentException("invalid actions mask");

        if ((cpath = getName()) == null)
                throw new NullPointerException("name can't be null");

        this.mask = mask;

        if (cpath.equals("<<ALL FILES>>")) {
            directory = true;
            recursive = true;
            cpath = "";
            return;
        }

        // store only the canonical cpath if possible
        //如果可能，仅存储规范的 cpath
        cpath = AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
                try {
                    String path = cpath;
                    if (cpath.endsWith("*")) {
                        // call getCanonicalPath with a path with wildcard character
                        // replaced to avoid calling it with paths that are
                        // intended to match all entries in a directory
                        //使用替换了通配符的路径调用 getCanonicalPath
                        // 以避免使用旨在匹配目录中所有条目的路径调用它
                        path = path.substring(0, path.length()-1) + "-";
                        path = new File(path).getCanonicalPath();
                        return path.substring(0, path.length()-1) + "*";
                    } else {
                        return new File(path).getCanonicalPath();
                    }
                } catch (IOException ioe) {
                    return cpath;
                }
            }
        });

        int len = cpath.length();
        char last = ((len > 0) ? cpath.charAt(len - 1) : 0);

        if (last == RECURSIVE_CHAR &&
            cpath.charAt(len - 2) == File.separatorChar) {
            directory = true;
            recursive = true;
            cpath = cpath.substring(0, --len);
        } else if (last == WILD_CHAR &&
            cpath.charAt(len - 2) == File.separatorChar) {
            directory = true;
            //recursive = false;
            cpath = cpath.substring(0, --len);
        } else {
            // overkill since they are initialized to false, but
            // commented out here to remind us...
            //矫枉过正，因为它们被初始化为 false，但在这里注释掉是为了提醒我们......
            //directory = false;
            //recursive = false;
        }

        // XXX: at this point the path should be absolute. die if it isn't?
    }

    /**
     * Creates a new FilePermission object with the specified actions.
     * <i>path</i> is the pathname of a file or directory, and <i>actions</i>
     * contains a comma-separated list of the desired actions granted on the
     * file or directory. Possible actions are
     * "read", "write", "execute", "delete", and "readlink".
     * 1.使用指定的操作创建一个新的 FilePermission 对象。path是文件或目录的路径名，
     * actions包含以逗号分隔的对文件或目录授予的所需操作的列表。
     * 可能的操作是“读取”、“写入”、“执行”、“删除”和“读取链接”
     * <p>A pathname that ends in "/*" (where "/" is
     * the file separator character, <code>File.separatorChar</code>)
     * indicates all the files and directories contained in that directory.
     * A pathname that ends with "/-" indicates (recursively) all files and
     * subdirectories contained in that directory. The special pathname
     * "&lt;&lt;ALL FILES&gt;&gt;" matches any file.
     * 2.以“/*”结尾的路径名（其中“/”是文件分隔符，File.separatorChar）
     * 表示该目录中包含的所有文件和目录。以“-”结尾的路径名表示（递归地）包含在该目录中的所有文件和子目录。
     * 特殊路径名“<<ALL FILES>>”匹配任何文件
     * <p>A pathname consisting of a single "*" indicates all the files
     * in the current directory, while a pathname consisting of a single "-"
     * indicates all the files in the current directory and
     * (recursively) all files and subdirectories contained in the current
     * directory.
     * 3.由单个“*”组成的路径名表示当前目录中的所有文件，
     * 而由单个“-”组成的路径名表示当前目录中的所有文件以及（递归地）当前目录中包含的所有文件和子目录。
     * <p>A pathname containing an empty string represents an empty path.
     * 4.包含空字符串的路径名表示空路径
     * @param path the pathname of the file/directory.
     * @param actions the action string.
     *
     * @throws IllegalArgumentException
     *          If actions is <code>null</code>, empty or contains an action
     *          other than the specified possible actions.
     */
    public FilePermission(String path, String actions) {
        super(path);
        init(getMask(actions));
    }

    /**
     * Creates a new FilePermission object using an action mask.
     * More efficient than the FilePermission(String, String) constructor.
     * Can be used from within
     * code that needs to create a FilePermission object to pass into the
     * <code>implies</code> method.
     * 使用操作掩码创建一个新的 FilePermission 对象。比 FilePermission(String, String) 构造函数更有效。
     * 可以在需要创建 FilePermission 对象以传递到implies方法的代码中使用
     * @param path the pathname of the file/directory.
     * @param mask the action mask to use.
     */

    // package private for use by the FilePermissionCollection add method
    FilePermission(String path, int mask) {
        super(path);
        init(mask);
    }

    /**
     * Checks if this FilePermission object "implies" the specified permission.
     * 1.检查此 FilePermission 对象是否“暗示”了指定的权限
     * <P>
     * More specifically, this method returns true if:
     * <ul>
     * <li> <i>p</i> is an instanceof FilePermission,
     * <li> <i>p</i>'s actions are a proper subset of this
     * object's actions, and
     * <li> <i>p</i>'s pathname is implied by this object's
     *      pathname. For example, "/tmp/*" implies "/tmp/foo", since
     *      "/tmp/*" encompasses all files in the "/tmp" directory,
     *      including the one named "foo".
     * </ul>
     * 2.更具体地说，如果：p是 FilePermission 的一个实例，p的操作是此对象操作的适当子集，
     * 并且p的路径名由该对象的路径名隐含。例如，“tmp”暗示“tmpfoo”，因为“tmp”包含“tmp”目录中的所有文件，
     * 包括名为“foo”的文件。
     * @param p the permission to check against.
     *
     * @return <code>true</code> if the specified permission is not
     *                  <code>null</code> and is implied by this object,
     *                  <code>false</code> otherwise.
     */
    public boolean implies(Permission p) {
        if (!(p instanceof FilePermission))
            return false;

        FilePermission that = (FilePermission) p;

        // we get the effective mask. i.e., the "and" of this and that.
        // They must be equal to that.mask for implies to return true.

        return ((this.mask & that.mask) == that.mask) && impliesIgnoreMask(that);
    }

    /**
     * Checks if the Permission's actions are a proper subset of the
     * this object's actions. Returns the effective mask iff the
     * this FilePermission's path also implies that FilePermission's path.
     * 检查权限的操作是否是此对象操作的正确子集。
     * 如果此 FilePermission 的路径也暗示 FilePermission 的路径，则返回有效掩码
     * @param that the FilePermission to check against.
     * @return the effective mask
     */
    boolean impliesIgnoreMask(FilePermission that) {
        if (this.directory) {
            if (this.recursive) {
                // make sure that.path is longer then path so
                // something like /foo/- does not imply /foo
                //确保 that.path 比 path 更长，所以像 foo- 这样的东西并不意味着 foo
                if (that.directory) {
                    return (that.cpath.length() >= this.cpath.length()) &&
                            that.cpath.startsWith(this.cpath);
                }  else {
                    return ((that.cpath.length() > this.cpath.length()) &&
                        that.cpath.startsWith(this.cpath));
                }
            } else {
                if (that.directory) {
                    // if the permission passed in is a directory
                    // specification, make sure that a non-recursive
                    // permission (i.e., this object) can't imply a recursive
                    // permission.
                    //如果传入的权限是目录规范，请确保非递归权限（即此对象）不能暗示递归权限。
                    if (that.recursive)
                        return false;
                    else
                        return (this.cpath.equals(that.cpath));
                } else {
                    int last = that.cpath.lastIndexOf(File.separatorChar);
                    if (last == -1)
                        return false;
                    else {
                        // this.cpath.equals(that.cpath.substring(0, last+1));
                        // Use regionMatches to avoid creating new string
                        //this.cpath.equals(that.cpath.substring(0, last+1));
                        // 使用 regionMatches 避免创建新字符串
                        return (this.cpath.length() == (last + 1)) &&
                            this.cpath.regionMatches(0, that.cpath, 0, last+1);
                    }
                }
            }
        } else if (that.directory) {
            // if this is NOT recursive/wildcarded,
            // do not let it imply a recursive/wildcarded permission
            //如果这不是递归通配符，不要让它暗示递归通配符权限
            return false;
        } else {
            return (this.cpath.equals(that.cpath));
        }
    }

    /**
     * Checks two FilePermission objects for equality. Checks that <i>obj</i> is
     * a FilePermission, and has the same pathname and actions as this object.
     * 检查两个 FilePermission 对象是否相等。
     * 检查obj是否为 FilePermission，并与此对象具有相同的路径名和操作
     * @param obj the object we are testing for equality with this object.
     * @return <code>true</code> if obj is a FilePermission, and has the same
     *          pathname and actions as this FilePermission object,
     *          <code>false</code> otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (! (obj instanceof FilePermission))
            return false;

        FilePermission that = (FilePermission) obj;

        return (this.mask == that.mask) &&
            this.cpath.equals(that.cpath) &&
            (this.directory == that.directory) &&
            (this.recursive == that.recursive);
    }

    /**
     * Returns the hash code value for this object.
     * 返回此对象的哈希码值。
     * @return a hash code value for this object.
     */
    public int hashCode() {
        return 0;
    }

    /**
     * Converts an actions String to an actions mask.
     * 将动作字符串转换为动作掩码。
     * @param actions the action string.
     * @return the actions mask.
     */
    private static int getMask(String actions) {
        int mask = NONE;

        // Null action valid?
        if (actions == null) {
            return mask;
        }

        // Use object identity comparison against known-interned strings for
        // performance benefit (these values are used heavily within the JDK).
        //将对象标识与已知的内部字符串进行比较以提高性能（这些值在 JDK 中被大量使用）
        if (actions == SecurityConstants.FILE_READ_ACTION) {
            return READ;
        } else if (actions == SecurityConstants.FILE_WRITE_ACTION) {
            return WRITE;
        } else if (actions == SecurityConstants.FILE_EXECUTE_ACTION) {
            return EXECUTE;
        } else if (actions == SecurityConstants.FILE_DELETE_ACTION) {
            return DELETE;
        } else if (actions == SecurityConstants.FILE_READLINK_ACTION) {
            return READLINK;
        }

        char[] a = actions.toCharArray();

        int i = a.length - 1;
        if (i < 0)
            return mask;

        while (i != -1) {
            char c;

            // skip whitespace
            while ((i!=-1) && ((c = a[i]) == ' ' ||
                               c == '\r' ||
                               c == '\n' ||
                               c == '\f' ||
                               c == '\t'))
                i--;

            // check for the known strings
            int matchlen;

            if (i >= 3 && (a[i-3] == 'r' || a[i-3] == 'R') &&
                          (a[i-2] == 'e' || a[i-2] == 'E') &&
                          (a[i-1] == 'a' || a[i-1] == 'A') &&
                          (a[i] == 'd' || a[i] == 'D'))
            {
                matchlen = 4;
                mask |= READ;

            } else if (i >= 4 && (a[i-4] == 'w' || a[i-4] == 'W') &&
                                 (a[i-3] == 'r' || a[i-3] == 'R') &&
                                 (a[i-2] == 'i' || a[i-2] == 'I') &&
                                 (a[i-1] == 't' || a[i-1] == 'T') &&
                                 (a[i] == 'e' || a[i] == 'E'))
            {
                matchlen = 5;
                mask |= WRITE;

            } else if (i >= 6 && (a[i-6] == 'e' || a[i-6] == 'E') &&
                                 (a[i-5] == 'x' || a[i-5] == 'X') &&
                                 (a[i-4] == 'e' || a[i-4] == 'E') &&
                                 (a[i-3] == 'c' || a[i-3] == 'C') &&
                                 (a[i-2] == 'u' || a[i-2] == 'U') &&
                                 (a[i-1] == 't' || a[i-1] == 'T') &&
                                 (a[i] == 'e' || a[i] == 'E'))
            {
                matchlen = 7;
                mask |= EXECUTE;

            } else if (i >= 5 && (a[i-5] == 'd' || a[i-5] == 'D') &&
                                 (a[i-4] == 'e' || a[i-4] == 'E') &&
                                 (a[i-3] == 'l' || a[i-3] == 'L') &&
                                 (a[i-2] == 'e' || a[i-2] == 'E') &&
                                 (a[i-1] == 't' || a[i-1] == 'T') &&
                                 (a[i] == 'e' || a[i] == 'E'))
            {
                matchlen = 6;
                mask |= DELETE;

            } else if (i >= 7 && (a[i-7] == 'r' || a[i-7] == 'R') &&
                                 (a[i-6] == 'e' || a[i-6] == 'E') &&
                                 (a[i-5] == 'a' || a[i-5] == 'A') &&
                                 (a[i-4] == 'd' || a[i-4] == 'D') &&
                                 (a[i-3] == 'l' || a[i-3] == 'L') &&
                                 (a[i-2] == 'i' || a[i-2] == 'I') &&
                                 (a[i-1] == 'n' || a[i-1] == 'N') &&
                                 (a[i] == 'k' || a[i] == 'K'))
            {
                matchlen = 8;
                mask |= READLINK;

            } else {
                // parse error
                throw new IllegalArgumentException(
                        "invalid permission: " + actions);
            }

            // make sure we didn't just match the tail of a word
            // like "ackbarfaccept".  Also, skip to the comma.
            boolean seencomma = false;
            while (i >= matchlen && !seencomma) {
                switch(a[i-matchlen]) {
                case ',':
                    seencomma = true;
                    break;
                case ' ': case '\r': case '\n':
                case '\f': case '\t':
                    break;
                default:
                    throw new IllegalArgumentException(
                            "invalid permission: " + actions);
                }
                i--;
            }

            // point i at the location of the comma minus one (or -1).
            i -= matchlen;
        }

        return mask;
    }

    /**
     * Return the current action mask. Used by the FilePermissionCollection.
     * 返回当前的动作掩码。由 FilePermissionCollection 使用
     * @return the actions mask.
     */
    int getMask() {
        return mask;
    }

    /**
     * Return the canonical string representation of the actions.
     * Always returns present actions in the following order:
     * read, write, execute, delete, readlink.
     * 返回操作的规范字符串表示形式。始终按以下顺序返回当前操作：读取、写入、执行、删除、读取链接。
     * @return the canonical string representation of the actions.
     */
    private static String getActions(int mask) {
        StringBuilder sb = new StringBuilder();
        boolean comma = false;

        if ((mask & READ) == READ) {
            comma = true;
            sb.append("read");
        }

        if ((mask & WRITE) == WRITE) {
            if (comma) sb.append(',');
            else comma = true;
            sb.append("write");
        }

        if ((mask & EXECUTE) == EXECUTE) {
            if (comma) sb.append(',');
            else comma = true;
            sb.append("execute");
        }

        if ((mask & DELETE) == DELETE) {
            if (comma) sb.append(',');
            else comma = true;
            sb.append("delete");
        }

        if ((mask & READLINK) == READLINK) {
            if (comma) sb.append(',');
            else comma = true;
            sb.append("readlink");
        }

        return sb.toString();
    }

    /**
     * Returns the "canonical string representation" of the actions.
     * That is, this method always returns present actions in the following order:
     * read, write, execute, delete, readlink. For example, if this FilePermission
     * object allows both write and read actions, a call to <code>getActions</code>
     * will return the string "read,write".
     * 返回操作的“规范字符串表示”。也就是说，此方法始终按以下顺序返回当前操作：读取、写入、执行、删除、读取链接。
     * 例如，如果此 FilePermission 对象允许写入和读取操作，则调用 getActions将返回字符串“read,write”
     * @return the canonical string representation of the actions.
     */
    public String getActions() {
        if (actions == null)
            actions = getActions(this.mask);

        return actions;
    }

    /**
     * Returns a new PermissionCollection object for storing FilePermission
     * objects.
     * 1.返回一个新的 PermissionCollection 对象，用于存储 FilePermission 对象
     * <p>
     * FilePermission objects must be stored in a manner that allows them
     * to be inserted into the collection in any order, but that also enables the
     * PermissionCollection <code>implies</code>
     * method to be implemented in an efficient (and consistent) manner.
     * 2.FilePermission 对象的存储方式必须允许它们以任何顺序插入到集合中，
     * 但也允许 PermissionCollection.implies方法以高效（且一致）的方式实现
     * <p>For example, if you have two FilePermissions:
     * <OL>
     * <LI>  <code>"/tmp/-", "read"</code>
     * <LI>  <code>"/tmp/scratch/foo", "write"</code>
     * </OL>
     *
     * <p>and you are calling the <code>implies</code> method with the FilePermission:
     *
     * <pre>
     *   "/tmp/scratch/foo", "read,write",
     * </pre>
     *
     * then the <code>implies</code> function must
     * take into account both the "/tmp/-" and "/tmp/scratch/foo"
     * permissions, so the effective permission is "read,write",
     * and <code>implies</code> returns true. The "implies" semantics for
     * FilePermissions are handled properly by the PermissionCollection object
     * returned by this <code>newPermissionCollection</code> method.
     * 3.例如，如果您有两个文件权限： "tmp-", "read"
     * "tmpscratchfoo", "write"
     * 并且您正在使用 FilePermission 调用implies方法：
     * "tmpscratchfoo", "read,write", 那么implies
     * 函数必须同时考虑“tmp-”和“tmpscratchfoo”权限，所以有效权限是“读，写”，
     * implies返回true。 FilePermissions 的“隐含”语义由此newPermissionCollection
     * 方法返回的 PermissionCollection 对象正确处理。
     * @return a new PermissionCollection object suitable for storing
     * FilePermissions.
     */
    public PermissionCollection newPermissionCollection() {
        return new FilePermissionCollection();
    }

    /**
     * WriteObject is called to save the state of the FilePermission
     * to a stream. The actions are serialized, and the superclass
     * takes care of the name.
     * 调用 WriteObject 以将 FilePermission 的状态保存到流中。动作被序列化，超类负责名称
     */
    private void writeObject(ObjectOutputStream s)
        throws IOException
    {
        // Write out the actions. The superclass takes care of the name
        // call getActions to make sure actions field is initialized
        //写出动作。超类负责调用 getActions 以确保操作字段已初始化
        if (actions == null)
            getActions();
        s.defaultWriteObject();
    }

    /**
     * readObject is called to restore the state of the FilePermission from
     * a stream.
     * 调用 readObject 以从流中恢复 FilePermission 的状态。
     */
    private void readObject(ObjectInputStream s)
         throws IOException, ClassNotFoundException
    {
        // Read in the actions, then restore everything else by calling init.
        //读入操作，然后通过调用 init 恢复其他所有内容
        s.defaultReadObject();
        init(getMask(actions));
    }
}

/**
 * A FilePermissionCollection stores a set of FilePermission permissions.
 * FilePermission objects
 * must be stored in a manner that allows them to be inserted in any
 * order, but enable the implies function to evaluate the implies
 * method.
 * 1.FilePermissionCollection 存储一组 FilePermission 权限。
 * FilePermission 对象必须以允许它们以任何顺序插入的方式存储，但启用隐含函数来评估隐含方法
 * For example, if you have two FilePermissions:
 * <OL>
 * <LI> "/tmp/-", "read"
 * <LI> "/tmp/scratch/foo", "write"
 * </OL>
 * And you are calling the implies function with the FilePermission:
 * "/tmp/scratch/foo", "read,write", then the implies function must
 * take into account both the /tmp/- and /tmp/scratch/foo
 * permissions, so the effective permission is "read,write".
 * 2.例如，如果您有两个文件权限：
 * "tmp-", "read"
 * "tmpscratchfoo", "write"
 * 并且您正在使用 FilePermission 调用隐含函数："tmpscratchfoo" ,
 * "read,write", 那么隐含函数必须同时考虑 tmp- 和 tmpscratchfoo 权限，所以有效权限是 "read,write"
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 *
 *
 * @author Marianne Mueller
 * @author Roland Schemers
 *
 * @serial include
 *
 */

final class FilePermissionCollection extends PermissionCollection
    implements Serializable
{
    // Not serialized; see serialization section at end of class
    //未连载；请参阅课程末尾的序列化部分
    private transient List<Permission> perms;

    /**
     * Create an empty FilePermissionCollection object.
     * 创建一个空的 FilePermissionCollection 对象
     */
    public FilePermissionCollection() {
        perms = new ArrayList<>();
    }

    /**
     * Adds a permission to the FilePermissionCollection. The key for the hash is
     * permission.path.
     * 向 FilePermissionCollection 添加权限。哈希的关键是permission.path
     *
     * @param permission the Permission object to add.
     *
     * @exception IllegalArgumentException - if the permission is not a
     *                                       FilePermission
     *
     * @exception SecurityException - if this FilePermissionCollection object
     *                                has been marked readonly
     */
    public void add(Permission permission) {
        if (! (permission instanceof FilePermission))
            throw new IllegalArgumentException("invalid permission: "+
                                               permission);
        if (isReadOnly())
            throw new SecurityException(
                "attempt to add a Permission to a readonly PermissionCollection");

        synchronized (this) {
            perms.add(permission);
        }
    }

    /**
     * Check and see if this set of permissions implies the permissions
     * expressed in "permission".
     * 检查并查看这组权限是否暗示了“权限”中表示的权限。
     * @param permission the Permission object to compare
     *
     * @return true if "permission" is a proper subset of a permission in
     * the set, false if not.
     */
    public boolean implies(Permission permission) {
        if (! (permission instanceof FilePermission))
            return false;

        FilePermission fp = (FilePermission) permission;

        int desired = fp.getMask();
        int effective = 0;
        int needed = desired;

        synchronized (this) {
            int len = perms.size();
            for (int i = 0; i < len; i++) {
                FilePermission x = (FilePermission) perms.get(i);
                if (((needed & x.getMask()) != 0) && x.impliesIgnoreMask(fp)) {
                    effective |=  x.getMask();
                    if ((effective & desired) == desired)
                        return true;
                    needed = (desired ^ effective);
                }
            }
        }
        return false;
    }

    /**
     * Returns an enumeration of all the FilePermission objects in the
     * container.
     * 返回容器中所有 FilePermission 对象的枚举。
     * @return an enumeration of all the FilePermission objects.
     */
    public Enumeration<Permission> elements() {
        // Convert Iterator into Enumeration
        synchronized (this) {
            return Collections.enumeration(perms);
        }
    }

    private static final long serialVersionUID = 2202956749081564585L;

    // Need to maintain serialization interoperability with earlier releases,
    // which had the serializable field:
    //    private Vector permissions;
    //需要保持与早期版本的序列化互操作性，这些版本具有可序列化字段：私有向量权限；

    /**
     * @serialField permissions java.util.Vector
     *     A list of FilePermission objects.
     */
    private static final ObjectStreamField[] serialPersistentFields = {
        new ObjectStreamField("permissions", Vector.class),
    };

    /**
     * @serialData "permissions" field (a Vector containing the FilePermissions).
     */
    /*
     * Writes the contents of the perms field out as a Vector for
     * serialization compatibility with earlier releases.
     * 将 perms 字段的内容作为 Vector 写出，以便与早期版本进行序列化兼容
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        // Don't call out.defaultWriteObject()

        // Write out Vector
        //不要调用 out.defaultWriteObject() 写出 Vector
        Vector<Permission> permissions = new Vector<>(perms.size());
        synchronized (this) {
            permissions.addAll(perms);
        }

        ObjectOutputStream.PutField pfields = out.putFields();
        pfields.put("permissions", permissions);
        out.writeFields();
    }

    /*
     * Reads in a Vector of FilePermissions and saves them in the perms field.
     * 读入 FilePermissions 向量并将它们保存在 perms 字段中
     */
    private void readObject(ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        // Don't call defaultReadObject()

        // Read in serialized fields
        ObjectInputStream.GetField gfields = in.readFields();

        // Get the one we want
        @SuppressWarnings("unchecked")
        Vector<Permission> permissions = (Vector<Permission>)gfields.get("permissions", null);
        perms = new ArrayList<>(permissions.size());
        perms.addAll(permissions);
    }
}
