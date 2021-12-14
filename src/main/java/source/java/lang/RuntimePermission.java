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

package java.lang;

import java.security.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * This class is for runtime permissions. A RuntimePermission
 * contains a name (also referred to as a "target name") but
 * no actions list; you either have the named permission
 * or you don't.
 * 1.此类用于运行时权限。 RuntimePermission 包含名称（也称为“目标名称”）但不包含操作列表；您要么拥有指定权限，要么没有。
 * <P>
 * The target name is the name of the runtime permission (see below). The
 * naming convention follows the  hierarchical property naming convention.
 * Also, an asterisk
 * may appear at the end of the name, following a ".", or by itself, to
 * signify a wildcard match. For example: "loadLibrary.*" and "*" signify a
 * wildcard match, while "*loadLibrary" and "a*b" do not.
 * 2.目标名称是运行时权限的名称（见下文）。命名约定遵循分层属性命名约定。
 * 此外，星号可能出现在名称的末尾，跟在“.”之后，或单独出现，以表示通配符匹配。
 * 例如：“loadLibrary.*”。和 "*" 表示通配符匹配，而 "*loadLibrary" 和 "a*b" 不表示
 * <P>
 * The following table lists all the possible RuntimePermission target names,
 * and for each provides a description of what the permission allows
 * and a discussion of the risks of granting code the permission.
 * 3.下表列出了所有可能的 RuntimePermission 目标名称，
 * 并为每个目标名称提供了权限所允许的内容的描述以及对授予代码权限的风险的讨论
 * <table border=1 cellpadding=5 summary="permission target name,
 *  what the target allows,and associated risks">
 *  4.border=1 cellpadding=5 summary="权限目标名称、目标允许的内容以及相关风险
 * <tr>
 * <th>Permission Target Name</th>
 * <th>What the Permission Allows</th>
 * <th>Risks of Allowing this Permission</th>
 * </tr>
 *
 * <tr>
 *   <td>createClassLoader</td>
 *   <td>Creation of a class loader</td>
 *   <td>This is an extremely dangerous permission to grant.
 * Malicious applications that can instantiate their own class
 * loaders could then load their own rogue classes into the system.
 * These newly loaded classes could be placed into any protection
 * domain by the class loader, thereby automatically granting the
 * classes the permissions for that domain.</td>
 * 5.可以实例化自己的类加载器的恶意应用程序然后可以将自己的流氓类加载到系统中。
 * 这些新加载的类可以被类加载器放置到任何保护域中，从而自动授予这些类对该域的权限
 * </tr>
 *
 * <tr>
 *   <td>getClassLoader</td>
 *   <td>Retrieval of a class loader (e.g., the class loader for the calling
 * class)</td>
 *   <td>This would grant an attacker permission to get the
 * class loader for a particular class. This is dangerous because
 * having access to a class's class loader allows the attacker to
 * load other classes available to that class loader. The attacker
 * would typically otherwise not have access to those classes.</td>
 * 6.这将授予攻击者获取特定类的类加载器的权限。这是危险的，因为访问类的类加载器允许攻击者加载该类加载器可用的其他类。
 * 否则攻击者通常无法访问这些类
 * </tr>
 *
 * <tr>
 *   <td>setContextClassLoader</td>
 *   <td>Setting of the context class loader used by a thread</td>
 *   <td>The context class loader is used by system code and extensions
 * when they need to lookup resources that might not exist in the system
 * class loader. Granting setContextClassLoader permission would allow
 * code to change which context class loader is used
 * for a particular thread, including system threads.</td>
 * 7.当他们需要查找系统类加载器中可能不存在的资源时。授予 setContextClassLoader
 * 权限将允许代码更改用于特定线程（包括系统线程）的上下文类加载器。
 * </tr>
 *
 * <tr>
 *   <td>enableContextClassLoaderOverride</td>
 *   <td>Subclass implementation of the thread context class loader methods</td>
 *   <td>The context class loader is used by system code and extensions
 * when they need to lookup resources that might not exist in the system
 * class loader. Granting enableContextClassLoaderOverride permission would allow
 * a subclass of Thread to override the methods that are used
 * to get or set the context class loader for a particular thread.</td>
 * 8.当他们需要查找系统类加载器中可能不存在的资源时。授予 enableContextClassLoaderOverride
 * 权限将允许 Thread 的子类覆盖用于获取或设置特定线程的上下文类加载器的方法
 * </tr>
 *
 * <tr>
 *   <td>closeClassLoader</td>
 *   <td>Closing of a ClassLoader</td>
 *   <td>Granting this permission allows code to close any URLClassLoader
 * that it has a reference to.</td>
 * </tr>
 *
 * <tr>
 *   <td>setSecurityManager</td>
 *   <td>Setting of the security manager (possibly replacing an existing one)
 * </td>
 *   <td>The security manager is a class that allows
 * applications to implement a security policy. Granting the setSecurityManager
 * permission would allow code to change which security manager is used by
 * installing a different, possibly less restrictive security manager,
 * thereby bypassing checks that would have been enforced by the original
 * security manager.</td>
 * </tr>
 * 9.安全管理器是一个允许应用程序实现安全策略的类。授予 setSecurityManager 权限将允许代码通过安装不同的、
 * 可能限制较少的安全管理器来更改使用的安全管理器，从而绕过本应由原始安全管理器强制执行的检查。
 * <tr>
 *   <td>createSecurityManager</td>
 *   <td>Creation of a new security manager</td>
 *   <td>This gives code access to protected, sensitive methods that may
 * disclose information about other classes or the execution stack.</td>
 * </tr>
 *
 * <tr>
 *   <td>getenv.{variable name}</td>
 *   <td>Reading of the value of the specified environment variable</td>
 *   <td>This would allow code to read the value, or determine the
 *       existence, of a particular environment variable.  This is
 *       dangerous if the variable contains confidential data.</td>
 * </tr>
 *
 * <tr>
 *   <td>exitVM.{exit status}</td>
 *   <td>Halting of the Java Virtual Machine with the specified exit status</td>
 *   <td>This allows an attacker to mount a denial-of-service attack
 * by automatically forcing the virtual machine to halt.
 * Note: The "exitVM.*" permission is automatically granted to all code
 * loaded from the application class path, thus enabling applications
 * to terminate themselves. Also, the "exitVM" permission is equivalent to
 * "exitVM.*".</td>
 * </tr>
 *
 * <tr>
 *   <td>shutdownHooks</td>
 *   <td>Registration and cancellation of virtual-machine shutdown hooks</td>
 *   <td>This allows an attacker to register a malicious shutdown
 * hook that interferes with the clean shutdown of the virtual machine.</td>
 * </tr>
 *
 * <tr>
 *   <td>setFactory</td>
 *   <td>Setting of the socket factory used by ServerSocket or Socket,
 * or of the stream handler factory used by URL</td>
 *   <td>This allows code to set the actual implementation
 * for the socket, server socket, stream handler, or RMI socket factory.
 * An attacker may set a faulty implementation which mangles the data
 * stream.</td>
 * </tr>
 *
 * <tr>
 *   <td>setIO</td>
 *   <td>Setting of System.out, System.in, and System.err</td>
 *   <td>This allows changing the value of the standard system streams.
 * An attacker may change System.in to monitor and
 * steal user input, or may set System.err to a "null" OutputStream,
 * which would hide any error messages sent to System.err. </td>
 * </tr>
 *
 * <tr>
 *   <td>modifyThread</td>
 *   <td>Modification of threads, e.g., via calls to Thread
 * <tt>interrupt</tt>, <tt>stop</tt>, <tt>suspend</tt>,
 * <tt>resume</tt>, <tt>setDaemon</tt>, <tt>setPriority</tt>,
 * <tt>setName</tt> and <tt>setUncaughtExceptionHandler</tt>
 * methods</td>
 * <td>This allows an attacker to modify the behaviour of
 * any thread in the system.</td>
 * </tr>
 *
 * <tr>
 *   <td>stopThread</td>
 *   <td>Stopping of threads via calls to the Thread <code>stop</code>
 * method</td>
 *   <td>This allows code to stop any thread in the system provided that it is
 * already granted permission to access that thread.
 * This poses as a threat, because that code may corrupt the system by
 * killing existing threads.</td>
 * </tr>
 *
 * <tr>
 *   <td>modifyThreadGroup</td>
 *   <td>modification of thread groups, e.g., via calls to ThreadGroup
 * <code>destroy</code>, <code>getParent</code>, <code>resume</code>,
 * <code>setDaemon</code>, <code>setMaxPriority</code>, <code>stop</code>,
 * and <code>suspend</code> methods</td>
 *   <td>This allows an attacker to create thread groups and
 * set their run priority.</td>
 * </tr>
 *
 * <tr>
 *   <td>getProtectionDomain</td>
 *   <td>Retrieval of the ProtectionDomain for a class</td>
 *   <td>This allows code to obtain policy information
 * for a particular code source. While obtaining policy information
 * does not compromise the security of the system, it does give
 * attackers additional information, such as local file names for
 * example, to better aim an attack.</td>
 * </tr>
 *
 * <tr>
 *   <td>getFileSystemAttributes</td>
 *   <td>Retrieval of file system attributes</td>
 *   <td>This allows code to obtain file system information such as disk usage
 *       or disk space available to the caller.  This is potentially dangerous
 *       because it discloses information about the system hardware
 *       configuration and some information about the caller's privilege to
 *       write files.</td>
 * </tr>
 *
 * <tr>
 *   <td>readFileDescriptor</td>
 *   <td>Reading of file descriptors</td>
 *   <td>This would allow code to read the particular file associated
 *       with the file descriptor read. This is dangerous if the file
 *       contains confidential data.</td>
 * </tr>
 *
 * <tr>
 *   <td>writeFileDescriptor</td>
 *   <td>Writing to file descriptors</td>
 *   <td>This allows code to write to a particular file associated
 *       with the descriptor. This is dangerous because it may allow
 *       malicious code to plant viruses or at the very least, fill up
 *       your entire disk.</td>
 * </tr>
 *
 * <tr>
 *   <td>loadLibrary.{library name}</td>
 *   <td>Dynamic linking of the specified library</td>
 *   <td>It is dangerous to allow an applet permission to load native code
 * libraries, because the Java security architecture is not designed to and
 * does not prevent malicious behavior at the level of native code.</td>
 * </tr>
 *
 * <tr>
 *   <td>accessClassInPackage.{package name}</td>
 *   <td>Access to the specified package via a class loader's
 * <code>loadClass</code> method when that class loader calls
 * the SecurityManager <code>checkPackageAccess</code> method</td>
 *   <td>This gives code access to classes in packages
 * to which it normally does not have access. Malicious code
 * may use these classes to help in its attempt to compromise
 * security in the system.</td>
 * </tr>
 *
 * <tr>
 *   <td>defineClassInPackage.{package name}</td>
 *   <td>Definition of classes in the specified package, via a class
 * loader's <code>defineClass</code> method when that class loader calls
 * the SecurityManager <code>checkPackageDefinition</code> method.</td>
 *   <td>This grants code permission to define a class
 * in a particular package. This is dangerous because malicious
 * code with this permission may define rogue classes in
 * trusted packages like <code>java.security</code> or <code>java.lang</code>,
 * for example.</td>
 * </tr>
 *
 * <tr>
 *   <td>accessDeclaredMembers</td>
 *   <td>Access to the declared members of a class</td>
 *   <td>This grants code permission to query a class for its public,
 * protected, default (package) access, and private fields and/or
 * methods. Although the code would have
 * access to the private and protected field and method names, it would not
 * have access to the private/protected field data and would not be able
 * to invoke any private methods. Nevertheless, malicious code
 * may use this information to better aim an attack.
 * Additionally, it may invoke any public methods and/or access public fields
 * in the class.  This could be dangerous if
 * the code would normally not be able to invoke those methods and/or
 * access the fields  because
 * it can't cast the object to the class/interface with those methods
 * and fields.
</td>
 * </tr>
 * <tr>
 *   <td>queuePrintJob</td>
 *   <td>Initiation of a print job request</td>
 *   <td>This could print sensitive information to a printer,
 * or simply waste paper.</td>
 * </tr>
 *
 * <tr>
 *   <td>getStackTrace</td>
 *   <td>Retrieval of the stack trace information of another thread.</td>
 *   <td>This allows retrieval of the stack trace information of
 * another thread.  This might allow malicious code to monitor the
 * execution of threads and discover vulnerabilities in applications.</td>
 * </tr>
 *
 * <tr>
 *   <td>setDefaultUncaughtExceptionHandler</td>
 *   <td>Setting the default handler to be used when a thread
 *   terminates abruptly due to an uncaught exception</td>
 *   <td>This allows an attacker to register a malicious
 *   uncaught exception handler that could interfere with termination
 *   of a thread</td>
 * </tr>
 *
 * <tr>
 *   <td>preferences</td>
 *   <td>Represents the permission required to get access to the
 *   java.util.prefs.Preferences implementations user or system root
 *   which in turn allows retrieval or update operations within the
 *   Preferences persistent backing store.) </td>
 *   <td>This permission allows the user to read from or write to the
 *   preferences backing store if the user running the code has
 *   sufficient OS privileges to read/write to that backing store.
 *   The actual backing store may reside within a traditional filesystem
 *   directory or within a registry depending on the platform OS</td>
 * </tr>
 *
 * <tr>
 *   <td>usePolicy</td>
 *   <td>Granting this permission disables the Java Plug-In's default
 *   security prompting behavior.</td>
 *   <td>For more information, refer to Java Plug-In's guides, <a href=
 *   "../../../technotes/guides/plugin/developer_guide/security.html">
 *   Applet Security Basics</a> and <a href=
 *   "../../../technotes/guides/plugin/developer_guide/rsa_how.html#use">
 *   usePolicy Permission</a>.</td>
 * </tr>
 * </table>
 *
 * @see java.security.BasicPermission
 * @see java.security.Permission
 * @see java.security.Permissions
 * @see java.security.PermissionCollection
 * @see java.lang.SecurityManager
 *
 *
 * @author Marianne Mueller
 * @author Roland Schemers
 */

public final class RuntimePermission extends BasicPermission {

    private static final long serialVersionUID = 7399184964622342223L;

    /**
     * Creates a new RuntimePermission with the specified name.
     * The name is the symbolic name of the RuntimePermission, such as
     * "exit", "setFactory", etc. An asterisk
     * may appear at the end of the name, following a ".", or by itself, to
     * signify a wildcard match.
     * 1.创建具有指定名称的新 RuntimePermission。该名称是 RuntimePermission 的符号名称，
     * 例如“exit”、“setFactory”等。星号可能出现在名称的末尾，跟在“.”之后，或者单独出现，以表示通配符匹配
     * @param name the name of the RuntimePermission.
     *
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     */

    public RuntimePermission(String name)
    {
        super(name);
    }

    /**
     * Creates a new RuntimePermission object with the specified name.
     * The name is the symbolic name of the RuntimePermission, and the
     * actions String is currently unused and should be null.
     * 2.创建具有指定名称的新 RuntimePermission 对象。 name 是 RuntimePermission 的符号名称，
     * actions String 当前未使用，应为 null。
     * @param name the name of the RuntimePermission.
     * @param actions should be null.
     *
     * @throws NullPointerException if <code>name</code> is <code>null</code>.
     * @throws IllegalArgumentException if <code>name</code> is empty.
     */

    public RuntimePermission(String name, String actions)
    {
        super(name, actions);
    }
}
