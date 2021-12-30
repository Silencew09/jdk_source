/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.Native;

/**
 * Package-private abstract class for the local filesystem abstraction.
 * 本地文件系统抽象的包私有抽象类
 */

abstract class FileSystem {

    /* -- Normalization and construction -- */
    //规范化建设

    /**
     * Return the local filesystem's name-separator character.
     * 返回本地文件系统的名称分隔符。
     */
    public abstract char getSeparator();

    /**
     * Return the local filesystem's path-separator character.
     * 返回本地文件系统的路径分隔符
     */
    public abstract char getPathSeparator();

    /**
     * Convert the given pathname string to normal form.  If the string is
     * already in normal form then it is simply returned.
     * 将给定的路径名字符串转换为正常形式。如果字符串已经是正常形式，则简单地返回它
     */
    public abstract String normalize(String path);

    /**
     * Compute the length of this pathname string's prefix.  The pathname
     * string must be in normal form.
     * 计算此路径名字符串前缀的长度。路径名字符串必须是正常格式
     */
    public abstract int prefixLength(String path);

    /**
     * Resolve the child pathname string against the parent.
     * Both strings must be in normal form, and the result
     * will be in normal form.
     * 针对父级解析子路径名字符串。两个字符串都必须是范式，结果将是范式
     */
    public abstract String resolve(String parent, String child);

    /**
     * Return the parent pathname string to be used when the parent-directory
     * argument in one of the two-argument File constructors is the empty
     * pathname.
     * 当双参数 File 构造函数之一中的 parent-directory 参数是空路径名时，返回要使用的父路径名字符串
     */
    public abstract String getDefaultParent();

    /**
     * Post-process the given URI path string if necessary.  This is used on
     * win32, e.g., to transform "/c:/foo" into "c:/foo".  The path string
     * still has slash separators; code in the File class will translate them
     * after this method returns.
     * 如有必要，对给定的 URI 路径字符串进行后处理。
     * 这用于 win32，例如，将“c:foo”转换为“c:foo”。路径字符串仍然有斜线分隔符；
     * File 类中的代码将在此方法返回后转换它们
     */
    public abstract String fromURIPath(String path);


    /* -- Path operations -- */
    //路径操作

    /**
     * Tell whether or not the given abstract pathname is absolute.
     * 判断给定的抽象路径名是否是绝对的
     */
    public abstract boolean isAbsolute(File f);

    /**
     * Resolve the given abstract pathname into absolute form.  Invoked by the
     * getAbsolutePath and getCanonicalPath methods in the File class.
     * 将给定的抽象路径名解析为绝对形式。由 File 类中的 getAbsolutePath 和 getCanonicalPath 方法调用
     */
    public abstract String resolve(File f);

    public abstract String canonicalize(String path) throws IOException;


    /* -- Attribute accessors -- */
    //属性访问器

    /* Constants for simple boolean attributes */
    //简单布尔属性的常量
    @Native public static final int BA_EXISTS    = 0x01;
    @Native public static final int BA_REGULAR   = 0x02;
    @Native public static final int BA_DIRECTORY = 0x04;
    @Native public static final int BA_HIDDEN    = 0x08;

    /**
     * Return the simple boolean attributes for the file or directory denoted
     * by the given abstract pathname, or zero if it does not exist or some
     * other I/O error occurs.
     * 返回由给定抽象路径名表示的文件或目录的简单布尔属性，如果不存在或发生其他一些 IO 错误，则返回零。
     */
    public abstract int getBooleanAttributes(File f);

    @Native public static final int ACCESS_READ    = 0x04;
    @Native public static final int ACCESS_WRITE   = 0x02;
    @Native public static final int ACCESS_EXECUTE = 0x01;

    /**
     * Check whether the file or directory denoted by the given abstract
     * pathname may be accessed by this process.  The second argument specifies
     * which access, ACCESS_READ, ACCESS_WRITE or ACCESS_EXECUTE, to check.
     * Return false if access is denied or an I/O error occurs
     * 检查此进程是否可以访问由给定抽象路径名表示的文件或目录。第二个参数指定要检查的访问权限，
     * ACCESS_READ、ACCESS_WRITE 或 ACCESS_EXECUTE。如果访问被拒绝或发生 IO 错误，则返回 false
     */
    public abstract boolean checkAccess(File f, int access);
    /**
     * Set on or off the access permission (to owner only or to all) to the file
     * or directory denoted by the given abstract pathname, based on the parameters
     * enable, access and oweronly.
     * 根据参数 enable、access 和oweronly，设置或关闭对给定抽象路径名表示的文件或目录的访问权限
     * （仅限所有者或所有人）
     */
    public abstract boolean setPermission(File f, int access, boolean enable, boolean owneronly);

    /**
     * Return the time at which the file or directory denoted by the given
     * abstract pathname was last modified, or zero if it does not exist or
     * some other I/O error occurs.
     * 返回给定抽象路径名表示的文件或目录上次修改的时间，如果不存在或发生其他一些 IO 错误，则返回零
     */
    public abstract long getLastModifiedTime(File f);

    /**
     * Return the length in bytes of the file denoted by the given abstract
     * pathname, or zero if it does not exist, is a directory, or some other
     * I/O error occurs.
     * 返回由给定抽象路径名表示的文件的长度（以字节为单位），如果不存在、是目录或发生其他一些 IO 错误，则返回零
     */
    public abstract long getLength(File f);


    /* -- File operations -- */
    //- 文件操作

    /**
     * Create a new empty file with the given pathname.  Return
     * <code>true</code> if the file was created and <code>false</code> if a
     * file or directory with the given pathname already exists.  Throw an
     * IOException if an I/O error occurs.
     * 使用给定的路径名创建一个新的空文件。如果文件已创建，则返回true，
     * 如果具有给定路径名的文件或目录已存在，则返回false。如果发生 IO 错误，则抛出 IOException
     */
    public abstract boolean createFileExclusively(String pathname)
        throws IOException;

    /**
     * Delete the file or directory denoted by the given abstract pathname,
     * returning <code>true</code> if and only if the operation succeeds.
     * 删除由给定抽象路径名表示的文件或目录，当且仅当操作成功时返回true
     */
    public abstract boolean delete(File f);

    /**
     * List the elements of the directory denoted by the given abstract
     * pathname.  Return an array of strings naming the elements of the
     * directory if successful; otherwise, return <code>null</code>.
     * 列出由给定抽象路径名表示的目录的元素。如果成功，则返回命名目录元素的字符串数组；
     * 否则，返回null
     */
    public abstract String[] list(File f);

    /**
     * Create a new directory denoted by the given abstract pathname,
     * returning <code>true</code> if and only if the operation succeeds.
     * 创建一个由给定抽象路径名表示的新目录，当且仅当操作成功时返回true
     */
    public abstract boolean createDirectory(File f);

    /**
     * Rename the file or directory denoted by the first abstract pathname to
     * the second abstract pathname, returning <code>true</code> if and only if
     * the operation succeeds.
     * 将第一个抽象路径名表示的文件或目录重命名为第二个抽象路径名，当且仅当操作成功时返回true
     */
    public abstract boolean rename(File f1, File f2);

    /**
     * Set the last-modified time of the file or directory denoted by the
     * given abstract pathname, returning <code>true</code> if and only if the
     * operation succeeds.
     * 设置由给定抽象路径名表示的文件或目录的最后修改时间，当且仅当操作成功时返回true
     */
    public abstract boolean setLastModifiedTime(File f, long time);

    /**
     * Mark the file or directory denoted by the given abstract pathname as
     * read-only, returning <code>true</code> if and only if the operation
     * succeeds.
     * 将给定抽象路径名表示的文件或目录标记为只读，当且仅当操作成功时返回true
     */
    public abstract boolean setReadOnly(File f);


    /* -- Filesystem interface -- */
    //文件系统接口

    /**
     * List the available filesystem roots.
     * 列出可用的文件系统根
     */
    public abstract File[] listRoots();

    /* -- Disk usage -- */
    @Native public static final int SPACE_TOTAL  = 0;
    @Native public static final int SPACE_FREE   = 1;
    @Native public static final int SPACE_USABLE = 2;

    public abstract long getSpace(File f, int t);

    /* -- Basic infrastructure -- */
    //基础设施

    /**
     * Compare two abstract pathnames lexicographically.
     * 按字典顺序比较两个抽象路径名
     */
    public abstract int compare(File f1, File f2);

    /**
     * Compute the hash code of an abstract pathname.
     * 计算抽象路径名的哈希码
     */
    public abstract int hashCode(File f);

    // Flags for enabling/disabling performance optimizations for file
    // name canonicalization
    //启用禁用文件名规范化性能优化的标志
    static boolean useCanonCaches      = true;
    static boolean useCanonPrefixCache = true;

    private static boolean getBooleanProperty(String prop, boolean defaultVal) {
        String val = System.getProperty(prop);
        if (val == null) return defaultVal;
        if (val.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    static {
        useCanonCaches      = getBooleanProperty("sun.io.useCanonCaches",
                                                 useCanonCaches);
        useCanonPrefixCache = getBooleanProperty("sun.io.useCanonPrefixCache",
                                                 useCanonPrefixCache);
    }
}
