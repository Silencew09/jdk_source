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

import java.io.ObjectStreamClass.WeakClassKey;
import java.lang.ref.ReferenceQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static java.io.ObjectStreamClass.processQueue;
import java.io.SerialCallbackContext;
import sun.reflect.misc.ReflectUtil;

/**
 * An ObjectOutputStream writes primitive data types and graphs of Java objects
 * to an OutputStream.  The objects can be read (reconstituted) using an
 * ObjectInputStream.  Persistent storage of objects can be accomplished by
 * using a file for the stream.  If the stream is a network socket stream, the
 * objects can be reconstituted on another host or in another process.
 * 1.ObjectOutputStream 将 Java 对象的原始数据类型和图形写入 OutputStream。
 * 可以使用 ObjectInputStream 读取（重构）对象。对象的持久存储可以通过对流使用文件来实现。
 * 如果流是网络套接字流，则可以在另一个主机或另一个进程中重构对象
 * <p>Only objects that support the java.io.Serializable interface can be
 * written to streams.  The class of each serializable object is encoded
 * including the class name and signature of the class, the values of the
 * object's fields and arrays, and the closure of any other objects referenced
 * from the initial objects.
 * 2.只有支持 java.io.Serializable 接口的对象才能写入流。
 * 每个可序列化对象的类都被编码，包括类名和类的签名、对象字段和数组的值，以及从初始对象引用的任何其他对象的闭包
 * <p>The method writeObject is used to write an object to the stream.  Any
 * object, including Strings and arrays, is written with writeObject. Multiple
 * objects or primitives can be written to the stream.  The objects must be
 * read back from the corresponding ObjectInputstream with the same types and
 * in the same order as they were written.
 * 3.方法 writeObject 用于将对象写入流。任何对象，包括字符串和数组，都是用 writeObject 编写的。
 * 可以将多个对象或原语写入流。对象必须从相应的 ObjectInputstream 中以相同的类型和与写入它们的顺序相同的顺序读回
 * <p>Primitive data types can also be written to the stream using the
 * appropriate methods from DataOutput. Strings can also be written using the
 * writeUTF method.
 * 4.还可以使用 DataOutput 中的适当方法将原始数据类型写入流。也可以使用 writeUTF 方法编写字符串
 * <p>The default serialization mechanism for an object writes the class of the
 * object, the class signature, and the values of all non-transient and
 * non-static fields.  References to other objects (except in transient or
 * static fields) cause those objects to be written also. Multiple references
 * to a single object are encoded using a reference sharing mechanism so that
 * graphs of objects can be restored to the same shape as when the original was
 * written.
 * 5.对象的默认序列化机制写入对象的类、类签名以及所有非瞬态和非静态字段的值。
 * 对其他对象的引用（瞬态或静态字段除外）会导致这些对象也被写入。
 * 对单个对象的多个引用使用引用共享机制进行编码，以便对象的图形可以恢复到与原始对象相同的形状
 * <p>For example to write an object that can be read by the example in
 * ObjectInputStream:
 * <br>
 * <pre>
 *      FileOutputStream fos = new FileOutputStream("t.tmp");
 *      ObjectOutputStream oos = new ObjectOutputStream(fos);
 *
 *      oos.writeInt(12345);
 *      oos.writeObject("Today");
 *      oos.writeObject(new Date());
 *
 *      oos.close();
 * </pre>
 *6.For example to write an object that can be read by the example in ObjectInputStream:
 *         FileOutputStream fos = new FileOutputStream("t.tmp");
 *         ObjectOutputStream oos = new ObjectOutputStream(fos);
 *
 *         oos.writeInt(12345);
 *         oos.writeObject("Today");
 *         oos.writeObject(new Date());
 *
 *         oos.close();
 * <p>Classes that require special handling during the serialization and
 * deserialization process must implement special methods with these exact
 * signatures:
 * <br>
 * <pre>
 * private void readObject(java.io.ObjectInputStream stream)
 *     throws IOException, ClassNotFoundException;
 * private void writeObject(java.io.ObjectOutputStream stream)
 *     throws IOException
 * private void readObjectNoData()
 *     throws ObjectStreamException;
 * </pre>
 *7.在序列化和反序列化过程中需要特殊处理的类必须实现具有以下确切签名的特殊方法：
 *  private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException;
 *  private void writeObject(java.io.ObjectOutputStream stream) throws IOException
 *  private void readObjectNoData() throws ObjectStreamException;
 * <p>The writeObject method is responsible for writing the state of the object
 * for its particular class so that the corresponding readObject method can
 * restore it.  The method does not need to concern itself with the state
 * belonging to the object's superclasses or subclasses.  State is saved by
 * writing the individual fields to the ObjectOutputStream using the
 * writeObject method or by using the methods for primitive data types
 * supported by DataOutput.
 * 8.writeObject 方法负责为其特定类写入对象的状态，以便相应的 readObject 方法可以恢复它。
 * 该方法不需要关心属于对象的超类或子类的状态。
 * 通过使用 writeObject 方法或使用 DataOutput 支持的原始数据类型的方法将各个字段写入 ObjectOutputStream 来保存状态
 * <p>Serialization does not write out the fields of any object that does not
 * implement the java.io.Serializable interface.  Subclasses of Objects that
 * are not serializable can be serializable. In this case the non-serializable
 * class must have a no-arg constructor to allow its fields to be initialized.
 * In this case it is the responsibility of the subclass to save and restore
 * the state of the non-serializable class. It is frequently the case that the
 * fields of that class are accessible (public, package, or protected) or that
 * there are get and set methods that can be used to restore the state.
 * 9.序列化不会写出任何未实现 java.io.Serializable 接口的对象的字段。
 * 不可序列化的对象的子类可以是可序列化的。在这种情况下，不可序列化的类必须有一个无参数构造函数以允许初始化其字段。
 * 在这种情况下，子类负责保存和恢复不可序列化类的状态。
 * 通常情况下，该类的字段是可访问的（公共的、包的或受保护的），或者存在可用于恢复状态的 get 和 set 方法
 * <p>Serialization of an object can be prevented by implementing writeObject
 * and readObject methods that throw the NotSerializableException.  The
 * exception will be caught by the ObjectOutputStream and abort the
 * serialization process.
 * 10.可以通过实现抛出 NotSerializableException 的 writeObject 和 readObject 方法来防止对象的序列化。
 * 异常将被 ObjectOutputStream 捕获并中止序列化过程
 * <p>Implementing the Externalizable interface allows the object to assume
 * complete control over the contents and format of the object's serialized
 * form.  The methods of the Externalizable interface, writeExternal and
 * readExternal, are called to save and restore the objects state.  When
 * implemented by a class they can write and read their own state using all of
 * the methods of ObjectOutput and ObjectInput.  It is the responsibility of
 * the objects to handle any versioning that occurs.
 * 11.实现 Externalizable 接口允许对象完全控制对象序列化形式的内容和格式。
 * 调用 Externalizable 接口的方法 writeExternal 和 readExternal 来保存和恢复对象状态。当
 * 由类实现时，它们可以使用 ObjectOutput 和 ObjectInput 的所有方法写入和读取自己的状态。
 * 对象有责任处理发生的任何版本控制
 * <p>Enum constants are serialized differently than ordinary serializable or
 * externalizable objects.  The serialized form of an enum constant consists
 * solely of its name; field values of the constant are not transmitted.  To
 * serialize an enum constant, ObjectOutputStream writes the string returned by
 * the constant's name method.  Like other serializable or externalizable
 * objects, enum constants can function as the targets of back references
 * appearing subsequently in the serialization stream.  The process by which
 * enum constants are serialized cannot be customized; any class-specific
 * writeObject and writeReplace methods defined by enum types are ignored
 * during serialization.  Similarly, any serialPersistentFields or
 * serialVersionUID field declarations are also ignored--all enum types have a
 * fixed serialVersionUID of 0L.
 * 12.枚举常量的序列化方式与普通的可序列化或外部化对象不同。枚举常量的序列化形式仅由其名称组成；
 * 不传输常量的字段值。为了序列化一个枚举常量，ObjectOutputStream 写入由常量的 name 方法返回的字符串。
 * 与其他可序列化或可外部化的对象一样，枚举常量可以作为随后出现在序列化流中的反向引用的目标。
 * 枚举常量序列化的过程无法自定义；
 * 在序列化期间，将忽略枚举类型定义的任何特定于类的 writeObject 和 writeReplace 方法。
 * 类似地，任何serialPersistentFields 或serialVersionUID 字段声明也将
 * 被忽略——所有枚举类型都有一个固定的0L 的serialVersionUID
 * <p>Primitive data, excluding serializable fields and externalizable data, is
 * written to the ObjectOutputStream in block-data records. A block data record
 * is composed of a header and data. The block data header consists of a marker
 * and the number of bytes to follow the header.  Consecutive primitive data
 * writes are merged into one block-data record.  The blocking factor used for
 * a block-data record will be 1024 bytes.  Each block-data record will be
 * filled up to 1024 bytes, or be written whenever there is a termination of
 * block-data mode.  Calls to the ObjectOutputStream methods writeObject,
 * defaultWriteObject and writeFields initially terminate any existing
 * block-data record.
 * 13.原始数据（不包括可序列化字段和可外部化数据）以块数据记录的形式写入 ObjectOutputStream。
 * 块数据记录由头和数据组成。块数据头由一个标记和跟在头后面的字节数组成。
 * 连续的原始数据写入合并为一个块数据记录。
 * 用于块数据记录的块因子将为 1024 字节。每个块数据记录最多可填充 1024 个字节，
 * 或者在块数据模式终止时写入。
 * 对 ObjectOutputStream 方法 writeObject、defaultWriteObject 和 writeFields 的调用最初会终止任何现有的块数据记录
 * @author      Mike Warres
 * @author      Roger Riggs
 * @see java.io.DataOutput
 * @see java.io.ObjectInputStream
 * @see java.io.Serializable
 * @see java.io.Externalizable
 * @see <a href="../../../platform/serialization/spec/output.html">Object Serialization Specification, Section 2, Object Output Classes</a>
 * @since       JDK1.1
 */
public class ObjectOutputStream
    extends OutputStream implements ObjectOutput, ObjectStreamConstants
{

    private static class Caches {
        /** cache of subclass security audit results */
        //子类安全审计结果的缓存
        static final ConcurrentMap<WeakClassKey,Boolean> subclassAudits =
            new ConcurrentHashMap<>();

        /** queue for WeakReferences to audited subclasses */
        //WeakReferences 到已审计子类的队列
        static final ReferenceQueue<Class<?>> subclassAuditsQueue =
            new ReferenceQueue<>();
    }

    /** filter stream for handling block data conversion */
    //用于处理块数据转换的过滤流
    private final BlockDataOutputStream bout;
    /** obj -> wire handle map */
    //obj -> 线柄图
    private final HandleTable handles;
    /** obj -> replacement obj map */
    //obj -> 替换 obj 映射
    private final ReplaceTable subs;
    /** stream protocol version */
    //流协议版本
    private int protocol = PROTOCOL_VERSION_2;
    /** recursion depth */
    //递归深度
    private int depth;

    /** buffer for writing primitive field values */
    //用于写入原始字段值的缓冲区
    private byte[] primVals;

    /** if true, invoke writeObjectOverride() instead of writeObject() */
    //如果为 true，则调用 writeObjectOverride() 而不是 writeObject()
    private final boolean enableOverride;
    /** if true, invoke replaceObject() */
    //如果为真，则调用 replaceObject()
    private boolean enableReplace;

    // values below valid only during upcalls to writeObject()/writeExternal()
    /**
     * Context during upcalls to class-defined writeObject methods; holds
     * object currently being serialized and descriptor for current class.
     * Null when not during writeObject upcall.
     * 以下值仅在调用 writeObject()writeExternal() 上下文期间对类定义的 writeObject 方法调用期间有效；
     * 保存当前正在序列化的对象和当前类的描述符。不在 writeObject upcall 期间时为 Null
     */
    private SerialCallbackContext curContext;
    /** current PutField object */
    //当前 PutField 对象
    private PutFieldImpl curPut;

    /** custom storage for debug trace info */
    //用于调试跟踪信息的自定义存储
    private final DebugTraceInfoStack debugInfoStack;

    /**
     * value of "sun.io.serialization.extendedDebugInfo" property,
     * as true or false for extended information about exception's place
     * “sun.io.serialization.extendedDebugInfo”属性的值，对于有关异常位置的扩展信息，为真或假
     */
    private static final boolean extendedDebugInfo =
        java.security.AccessController.doPrivileged(
            new sun.security.action.GetBooleanAction(
                "sun.io.serialization.extendedDebugInfo")).booleanValue();

    /**
     * Creates an ObjectOutputStream that writes to the specified OutputStream.
     * This constructor writes the serialization stream header to the
     * underlying stream; callers may wish to flush the stream immediately to
     * ensure that constructors for receiving ObjectInputStreams will not block
     * when reading the header.
     * 1.创建一个写入指定 OutputStream 的 ObjectOutputStream。
     * 此构造函数将序列化流标头写入底层流；调用者可能希望立即刷新流，以确保接收 ObjectInputStreams 的构造函数在读取标头时不会阻塞
     * <p>If a security manager is installed, this constructor will check for
     * the "enableSubclassImplementation" SerializablePermission when invoked
     * directly or indirectly by the constructor of a subclass which overrides
     * the ObjectOutputStream.putFields or ObjectOutputStream.writeUnshared
     * methods.
     * 2.如果安装了安全管理器，则在由覆盖 ObjectOutputStream.putFields 或
     * ObjectOutputStream.writeUnshared 方法的子类的构造函数直接或间接调用时，
     * 此构造函数将检查“enableSubclassImplementation”SerializablePermission
     * @param   out output stream to write to
     * @throws  IOException if an I/O error occurs while writing stream header
     * @throws  SecurityException if untrusted subclass illegally overrides
     *          security-sensitive methods
     * @throws  NullPointerException if <code>out</code> is <code>null</code>
     * @since   1.4
     * @see     ObjectOutputStream#ObjectOutputStream()
     * @see     ObjectOutputStream#putFields()
     * @see     ObjectInputStream#ObjectInputStream(InputStream)
     */
    public ObjectOutputStream(OutputStream out) throws IOException {
        verifySubclass();
        bout = new BlockDataOutputStream(out);
        handles = new HandleTable(10, (float) 3.00);
        subs = new ReplaceTable(10, (float) 3.00);
        enableOverride = false;
        writeStreamHeader();
        bout.setBlockDataMode(true);
        if (extendedDebugInfo) {
            debugInfoStack = new DebugTraceInfoStack();
        } else {
            debugInfoStack = null;
        }
    }

    /**
     * Provide a way for subclasses that are completely reimplementing
     * ObjectOutputStream to not have to allocate private data just used by
     * this implementation of ObjectOutputStream.
     *
     * <p>If there is a security manager installed, this method first calls the
     * security manager's <code>checkPermission</code> method with a
     * <code>SerializablePermission("enableSubclassImplementation")</code>
     * permission to ensure it's ok to enable subclassing.
     *
     * @throws  SecurityException if a security manager exists and its
     *          <code>checkPermission</code> method denies enabling
     *          subclassing.
     * @throws  IOException if an I/O error occurs while creating this stream
     * @see SecurityManager#checkPermission
     * @see java.io.SerializablePermission
     */
    protected ObjectOutputStream() throws IOException, SecurityException {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
        }
        bout = null;
        handles = null;
        subs = null;
        enableOverride = true;
        debugInfoStack = null;
    }

    /**
     * Specify stream protocol version to use when writing the stream.
     *
     * <p>This routine provides a hook to enable the current version of
     * Serialization to write in a format that is backwards compatible to a
     * previous version of the stream format.
     *
     * <p>Every effort will be made to avoid introducing additional
     * backwards incompatibilities; however, sometimes there is no
     * other alternative.
     *
     * @param   version use ProtocolVersion from java.io.ObjectStreamConstants.
     * @throws  IllegalStateException if called after any objects
     *          have been serialized.
     * @throws  IllegalArgumentException if invalid version is passed in.
     * @throws  IOException if I/O errors occur
     * @see java.io.ObjectStreamConstants#PROTOCOL_VERSION_1
     * @see java.io.ObjectStreamConstants#PROTOCOL_VERSION_2
     * @since   1.2
     */
    public void useProtocolVersion(int version) throws IOException {
        if (handles.size() != 0) {
            // REMIND: implement better check for pristine stream?
            throw new IllegalStateException("stream non-empty");
        }
        switch (version) {
            case PROTOCOL_VERSION_1:
            case PROTOCOL_VERSION_2:
                protocol = version;
                break;

            default:
                throw new IllegalArgumentException(
                    "unknown version: " + version);
        }
    }

    /**
     * Write the specified object to the ObjectOutputStream.  The class of the
     * object, the signature of the class, and the values of the non-transient
     * and non-static fields of the class and all of its supertypes are
     * written.  Default serialization for a class can be overridden using the
     * writeObject and the readObject methods.  Objects referenced by this
     * object are written transitively so that a complete equivalent graph of
     * objects can be reconstructed by an ObjectInputStream.
     *
     * <p>Exceptions are thrown for problems with the OutputStream and for
     * classes that should not be serialized.  All exceptions are fatal to the
     * OutputStream, which is left in an indeterminate state, and it is up to
     * the caller to ignore or recover the stream state.
     *
     * @throws  InvalidClassException Something is wrong with a class used by
     *          serialization.
     * @throws  NotSerializableException Some object to be serialized does not
     *          implement the java.io.Serializable interface.
     * @throws  IOException Any exception thrown by the underlying
     *          OutputStream.
     */
    public final void writeObject(Object obj) throws IOException {
        if (enableOverride) {
            writeObjectOverride(obj);
            return;
        }
        try {
            writeObject0(obj, false);
        } catch (IOException ex) {
            if (depth == 0) {
                writeFatalException(ex);
            }
            throw ex;
        }
    }

    /**
     * Method used by subclasses to override the default writeObject method.
     * This method is called by trusted subclasses of ObjectInputStream that
     * constructed ObjectInputStream using the protected no-arg constructor.
     * The subclass is expected to provide an override method with the modifier
     * "final".
     *
     * @param   obj object to be written to the underlying stream
     * @throws  IOException if there are I/O errors while writing to the
     *          underlying stream
     * @see #ObjectOutputStream()
     * @see #writeObject(Object)
     * @since 1.2
     */
    protected void writeObjectOverride(Object obj) throws IOException {
    }

    /**
     * Writes an "unshared" object to the ObjectOutputStream.  This method is
     * identical to writeObject, except that it always writes the given object
     * as a new, unique object in the stream (as opposed to a back-reference
     * pointing to a previously serialized instance).  Specifically:
     * <ul>
     *   <li>An object written via writeUnshared is always serialized in the
     *       same manner as a newly appearing object (an object that has not
     *       been written to the stream yet), regardless of whether or not the
     *       object has been written previously.
     *
     *   <li>If writeObject is used to write an object that has been previously
     *       written with writeUnshared, the previous writeUnshared operation
     *       is treated as if it were a write of a separate object.  In other
     *       words, ObjectOutputStream will never generate back-references to
     *       object data written by calls to writeUnshared.
     * </ul>
     * While writing an object via writeUnshared does not in itself guarantee a
     * unique reference to the object when it is deserialized, it allows a
     * single object to be defined multiple times in a stream, so that multiple
     * calls to readUnshared by the receiver will not conflict.  Note that the
     * rules described above only apply to the base-level object written with
     * writeUnshared, and not to any transitively referenced sub-objects in the
     * object graph to be serialized.
     *
     * <p>ObjectOutputStream subclasses which override this method can only be
     * constructed in security contexts possessing the
     * "enableSubclassImplementation" SerializablePermission; any attempt to
     * instantiate such a subclass without this permission will cause a
     * SecurityException to be thrown.
     *
     * @param   obj object to write to stream
     * @throws  NotSerializableException if an object in the graph to be
     *          serialized does not implement the Serializable interface
     * @throws  InvalidClassException if a problem exists with the class of an
     *          object to be serialized
     * @throws  IOException if an I/O error occurs during serialization
     * @since 1.4
     */
    public void writeUnshared(Object obj) throws IOException {
        try {
            writeObject0(obj, true);
        } catch (IOException ex) {
            if (depth == 0) {
                writeFatalException(ex);
            }
            throw ex;
        }
    }

    /**
     * Write the non-static and non-transient fields of the current class to
     * this stream.  This may only be called from the writeObject method of the
     * class being serialized. It will throw the NotActiveException if it is
     * called otherwise.
     * 将当前类的非静态和非瞬态字段写入此流。这只能从正在序列化的类的 writeObject 方法中调用。
     * 如果以其他方式调用，它将抛出 NotActiveExceptio
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          <code>OutputStream</code>
     */
    public void defaultWriteObject() throws IOException {
        SerialCallbackContext ctx = curContext;
        if (ctx == null) {
            throw new NotActiveException("not in call to writeObject");
        }
        Object curObj = ctx.getObj();
        ObjectStreamClass curDesc = ctx.getDesc();
        bout.setBlockDataMode(false);
        defaultWriteFields(curObj, curDesc);
        bout.setBlockDataMode(true);
    }

    /**
     * Retrieve the object used to buffer persistent fields to be written to
     * the stream.  The fields will be written to the stream when writeFields
     * method is called.
     *
     * @return  an instance of the class Putfield that holds the serializable
     *          fields
     * @throws  IOException if I/O errors occur
     * @since 1.2
     */
    public ObjectOutputStream.PutField putFields() throws IOException {
        if (curPut == null) {
            SerialCallbackContext ctx = curContext;
            if (ctx == null) {
                throw new NotActiveException("not in call to writeObject");
            }
            Object curObj = ctx.getObj();
            ObjectStreamClass curDesc = ctx.getDesc();
            curPut = new PutFieldImpl(curDesc);
        }
        return curPut;
    }

    /**
     * Write the buffered fields to the stream.
     *
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     * @throws  NotActiveException Called when a classes writeObject method was
     *          not called to write the state of the object.
     * @since 1.2
     */
    public void writeFields() throws IOException {
        if (curPut == null) {
            throw new NotActiveException("no current PutField object");
        }
        bout.setBlockDataMode(false);
        curPut.writeFields();
        bout.setBlockDataMode(true);
    }

    /**
     * Reset will disregard the state of any objects already written to the
     * stream.  The state is reset to be the same as a new ObjectOutputStream.
     * The current point in the stream is marked as reset so the corresponding
     * ObjectInputStream will be reset at the same point.  Objects previously
     * written to the stream will not be referred to as already being in the
     * stream.  They will be written to the stream again.
     *
     * @throws  IOException if reset() is invoked while serializing an object.
     */
    public void reset() throws IOException {
        if (depth != 0) {
            throw new IOException("stream active");
        }
        bout.setBlockDataMode(false);
        bout.writeByte(TC_RESET);
        clear();
        bout.setBlockDataMode(true);
    }

    /**
     * Subclasses may implement this method to allow class data to be stored in
     * the stream. By default this method does nothing.  The corresponding
     * method in ObjectInputStream is resolveClass.  This method is called
     * exactly once for each unique class in the stream.  The class name and
     * signature will have already been written to the stream.  This method may
     * make free use of the ObjectOutputStream to save any representation of
     * the class it deems suitable (for example, the bytes of the class file).
     * The resolveClass method in the corresponding subclass of
     * ObjectInputStream must read and use any data or objects written by
     * annotateClass.
     *
     * @param   cl the class to annotate custom data for
     * @throws  IOException Any exception thrown by the underlying
     *          OutputStream.
     */
    protected void annotateClass(Class<?> cl) throws IOException {
    }

    /**
     * Subclasses may implement this method to store custom data in the stream
     * along with descriptors for dynamic proxy classes.
     *
     * <p>This method is called exactly once for each unique proxy class
     * descriptor in the stream.  The default implementation of this method in
     * <code>ObjectOutputStream</code> does nothing.
     *
     * <p>The corresponding method in <code>ObjectInputStream</code> is
     * <code>resolveProxyClass</code>.  For a given subclass of
     * <code>ObjectOutputStream</code> that overrides this method, the
     * <code>resolveProxyClass</code> method in the corresponding subclass of
     * <code>ObjectInputStream</code> must read any data or objects written by
     * <code>annotateProxyClass</code>.
     *
     * @param   cl the proxy class to annotate custom data for
     * @throws  IOException any exception thrown by the underlying
     *          <code>OutputStream</code>
     * @see ObjectInputStream#resolveProxyClass(String[])
     * @since   1.3
     */
    protected void annotateProxyClass(Class<?> cl) throws IOException {
    }

    /**
     * This method will allow trusted subclasses of ObjectOutputStream to
     * substitute one object for another during serialization. Replacing
     * objects is disabled until enableReplaceObject is called. The
     * enableReplaceObject method checks that the stream requesting to do
     * replacement can be trusted.  The first occurrence of each object written
     * into the serialization stream is passed to replaceObject.  Subsequent
     * references to the object are replaced by the object returned by the
     * original call to replaceObject.  To ensure that the private state of
     * objects is not unintentionally exposed, only trusted streams may use
     * replaceObject.
     * 1.此方法将允许 ObjectOutputStream 的受信任子类在序列化期间用一个对象替换另一个对象。
     * 在调用 enableReplaceObject 之前，替换对象是禁用的。
     * enableReplaceObject 方法检查请求进行替换的流是否可信。
     * 写入序列化流的每个对象的第一次出现被传递给replaceObject。
     * 对该对象的后续引用被替换为原始调用replaceObject 所返回的对象。
     * 为了确保对象的私有状态不会被无意暴露，只有受信任的流才可以使用 replaceObject
     * <p>The ObjectOutputStream.writeObject method takes a parameter of type
     * Object (as opposed to type Serializable) to allow for cases where
     * non-serializable objects are replaced by serializable ones.
     * 2.ObjectOutputStream.writeObject 方法采用 Object 类型（与 Serializable 类型相反）的参数，
     * 以允许将不可序列化对象替换为可序列化对象的情况
     * <p>When a subclass is replacing objects it must insure that either a
     * complementary substitution must be made during deserialization or that
     * the substituted object is compatible with every field where the
     * reference will be stored.  Objects whose type is not a subclass of the
     * type of the field or array element abort the serialization by raising an
     * exception and the object is not be stored.
     * 3.当子类替换对象时，它必须确保在反序列化期间必须进行补充替换，或者替换的对象与将存储引用的每个字段兼容。
     * 类型不是字段或数组元素类型的子类的对象会通过引发异常来中止序列化，并且不会存储该对象
     * <p>This method is called only once when each object is first
     * encountered.  All subsequent references to the object will be redirected
     * to the new object. This method should return the object to be
     * substituted or the original object.
     * 4.此方法仅在第一次遇到每个对象时调用一次。对该对象的所有后续引用都将重定向到新对象。此方法应返回要替换的对象或原始对象
     * <p>Null can be returned as the object to be substituted, but may cause
     * NullReferenceException in classes that contain references to the
     * original object since they may be expecting an object instead of
     * null.
     * 5.Null 可以作为要替换的对象返回，但在包含对原始对象的引用的类中可能会导致 NullReferenceException，
     * 因为它们可能需要一个对象而不是 null
     * @param   obj the object to be replaced
     * @return  the alternate object that replaced the specified one
     * @throws  IOException Any exception thrown by the underlying
     *          OutputStream.
     */
    protected Object replaceObject(Object obj) throws IOException {
        return obj;
    }

    /**
     * Enable the stream to do replacement of objects in the stream.  When
     * enabled, the replaceObject method is called for every object being
     * serialized.
     *
     * <p>If <code>enable</code> is true, and there is a security manager
     * installed, this method first calls the security manager's
     * <code>checkPermission</code> method with a
     * <code>SerializablePermission("enableSubstitution")</code> permission to
     * ensure it's ok to enable the stream to do replacement of objects in the
     * stream.
     *
     * @param   enable boolean parameter to enable replacement of objects
     * @return  the previous setting before this method was invoked
     * @throws  SecurityException if a security manager exists and its
     *          <code>checkPermission</code> method denies enabling the stream
     *          to do replacement of objects in the stream.
     * @see SecurityManager#checkPermission
     * @see java.io.SerializablePermission
     */
    protected boolean enableReplaceObject(boolean enable)
        throws SecurityException
    {
        if (enable == enableReplace) {
            return enable;
        }
        if (enable) {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(SUBSTITUTION_PERMISSION);
            }
        }
        enableReplace = enable;
        return !enableReplace;
    }

    /**
     * The writeStreamHeader method is provided so subclasses can append or
     * prepend their own header to the stream.  It writes the magic number and
     * version to the stream.
     *
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    protected void writeStreamHeader() throws IOException {
        bout.writeShort(STREAM_MAGIC);
        bout.writeShort(STREAM_VERSION);
    }

    /**
     * Write the specified class descriptor to the ObjectOutputStream.  Class
     * descriptors are used to identify the classes of objects written to the
     * stream.  Subclasses of ObjectOutputStream may override this method to
     * customize the way in which class descriptors are written to the
     * serialization stream.  The corresponding method in ObjectInputStream,
     * <code>readClassDescriptor</code>, should then be overridden to
     * reconstitute the class descriptor from its custom stream representation.
     * By default, this method writes class descriptors according to the format
     * defined in the Object Serialization specification.
     *
     * <p>Note that this method will only be called if the ObjectOutputStream
     * is not using the old serialization stream format (set by calling
     * ObjectOutputStream's <code>useProtocolVersion</code> method).  If this
     * serialization stream is using the old format
     * (<code>PROTOCOL_VERSION_1</code>), the class descriptor will be written
     * internally in a manner that cannot be overridden or customized.
     *
     * @param   desc class descriptor to write to the stream
     * @throws  IOException If an I/O error has occurred.
     * @see java.io.ObjectInputStream#readClassDescriptor()
     * @see #useProtocolVersion(int)
     * @see java.io.ObjectStreamConstants#PROTOCOL_VERSION_1
     * @since 1.3
     */
    protected void writeClassDescriptor(ObjectStreamClass desc)
        throws IOException
    {
        desc.writeNonProxy(this);
    }

    /**
     * Writes a byte. This method will block until the byte is actually
     * written.
     *
     * @param   val the byte to be written to the stream
     * @throws  IOException If an I/O error has occurred.
     */
    public void write(int val) throws IOException {
        bout.write(val);
    }

    /**
     * Writes an array of bytes. This method will block until the bytes are
     * actually written.
     *
     * @param   buf the data to be written
     * @throws  IOException If an I/O error has occurred.
     */
    public void write(byte[] buf) throws IOException {
        bout.write(buf, 0, buf.length, false);
    }

    /**
     * Writes a sub array of bytes.
     *
     * @param   buf the data to be written
     * @param   off the start offset in the data
     * @param   len the number of bytes that are written
     * @throws  IOException If an I/O error has occurred.
     */
    public void write(byte[] buf, int off, int len) throws IOException {
        if (buf == null) {
            throw new NullPointerException();
        }
        int endoff = off + len;
        if (off < 0 || len < 0 || endoff > buf.length || endoff < 0) {
            throw new IndexOutOfBoundsException();
        }
        bout.write(buf, off, len, false);
    }

    /**
     * Flushes the stream. This will write any buffered output bytes and flush
     * through to the underlying stream.
     *
     * @throws  IOException If an I/O error has occurred.
     */
    public void flush() throws IOException {
        bout.flush();
    }

    /**
     * Drain any buffered data in ObjectOutputStream.  Similar to flush but
     * does not propagate the flush to the underlying stream.
     *
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    protected void drain() throws IOException {
        bout.drain();
    }

    /**
     * Closes the stream. This method must be called to release any resources
     * associated with the stream.
     *
     * @throws  IOException If an I/O error has occurred.
     */
    public void close() throws IOException {
        flush();
        clear();
        bout.close();
    }

    /**
     * Writes a boolean.
     *
     * @param   val the boolean to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeBoolean(boolean val) throws IOException {
        bout.writeBoolean(val);
    }

    /**
     * Writes an 8 bit byte.
     *
     * @param   val the byte value to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeByte(int val) throws IOException  {
        bout.writeByte(val);
    }

    /**
     * Writes a 16 bit short.
     *
     * @param   val the short value to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeShort(int val)  throws IOException {
        bout.writeShort(val);
    }

    /**
     * Writes a 16 bit char.
     *
     * @param   val the char value to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeChar(int val)  throws IOException {
        bout.writeChar(val);
    }

    /**
     * Writes a 32 bit int.
     * 写入 32 位 int
     * @param   val the integer value to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeInt(int val)  throws IOException {
        bout.writeInt(val);
    }

    /**
     * Writes a 64 bit long.
     *
     * @param   val the long value to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeLong(long val)  throws IOException {
        bout.writeLong(val);
    }

    /**
     * Writes a 32 bit float.
     *
     * @param   val the float value to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeFloat(float val) throws IOException {
        bout.writeFloat(val);
    }

    /**
     * Writes a 64 bit double.
     *
     * @param   val the double value to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeDouble(double val) throws IOException {
        bout.writeDouble(val);
    }

    /**
     * Writes a String as a sequence of bytes.
     *
     * @param   str the String of bytes to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeBytes(String str) throws IOException {
        bout.writeBytes(str);
    }

    /**
     * Writes a String as a sequence of chars.
     *
     * @param   str the String of chars to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeChars(String str) throws IOException {
        bout.writeChars(str);
    }

    /**
     * Primitive data write of this String in
     * <a href="DataInput.html#modified-utf-8">modified UTF-8</a>
     * format.  Note that there is a
     * significant difference between writing a String into the stream as
     * primitive data or as an Object. A String instance written by writeObject
     * is written into the stream as a String initially. Future writeObject()
     * calls write references to the string into the stream.
     *
     * @param   str the String to be written
     * @throws  IOException if I/O errors occur while writing to the underlying
     *          stream
     */
    public void writeUTF(String str) throws IOException {
        bout.writeUTF(str);
    }

    /**
     * Provide programmatic access to the persistent fields to be written
     * to ObjectOutput.
     *
     * @since 1.2
     */
    public static abstract class PutField {

        /**
         * Put the value of the named boolean field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not
         * <code>boolean</code>
         */
        public abstract void put(String name, boolean val);

        /**
         * Put the value of the named byte field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not
         * <code>byte</code>
         */
        public abstract void put(String name, byte val);

        /**
         * Put the value of the named char field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not
         * <code>char</code>
         */
        public abstract void put(String name, char val);

        /**
         * Put the value of the named short field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not
         * <code>short</code>
         */
        public abstract void put(String name, short val);

        /**
         * Put the value of the named int field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not
         * <code>int</code>
         */
        public abstract void put(String name, int val);

        /**
         * Put the value of the named long field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not
         * <code>long</code>
         */
        public abstract void put(String name, long val);

        /**
         * Put the value of the named float field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not
         * <code>float</code>
         */
        public abstract void put(String name, float val);

        /**
         * Put the value of the named double field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not
         * <code>double</code>
         */
        public abstract void put(String name, double val);

        /**
         * Put the value of the named Object field into the persistent field.
         *
         * @param  name the name of the serializable field
         * @param  val the value to assign to the field
         *         (which may be <code>null</code>)
         * @throws IllegalArgumentException if <code>name</code> does not
         * match the name of a serializable field for the class whose fields
         * are being written, or if the type of the named field is not a
         * reference type
         */
        public abstract void put(String name, Object val);

        /**
         * Write the data and fields to the specified ObjectOutput stream,
         * which must be the same stream that produced this
         * <code>PutField</code> object.
         *
         * @param  out the stream to write the data and fields to
         * @throws IOException if I/O errors occur while writing to the
         *         underlying stream
         * @throws IllegalArgumentException if the specified stream is not
         *         the same stream that produced this <code>PutField</code>
         *         object
         * @deprecated This method does not write the values contained by this
         *         <code>PutField</code> object in a proper format, and may
         *         result in corruption of the serialization stream.  The
         *         correct way to write <code>PutField</code> data is by
         *         calling the {@link java.io.ObjectOutputStream#writeFields()}
         *         method.
         */
        @Deprecated
        public abstract void write(ObjectOutput out) throws IOException;
    }


    /**
     * Returns protocol version in use.
     */
    int getProtocolVersion() {
        return protocol;
    }

    /**
     * Writes string without allowing it to be replaced in stream.  Used by
     * ObjectStreamClass to write class descriptor type strings.
     */
    void writeTypeString(String str) throws IOException {
        int handle;
        if (str == null) {
            writeNull();
        } else if ((handle = handles.lookup(str)) != -1) {
            writeHandle(handle);
        } else {
            writeString(str, false);
        }
    }

    /**
     * Verifies that this (possibly subclass) instance can be constructed
     * without violating security constraints: the subclass must not override
     * security-sensitive non-final methods, or else the
     * "enableSubclassImplementation" SerializablePermission is checked.
     * 验证可以在不违反安全约束的情况下构造此（可能是子类）实例：
     * 子类不得覆盖安全敏感的非最终方法，否则检查“enableSubclassImplementation”SerializablePermission
     */
    private void verifySubclass() {
        Class<?> cl = getClass();
        if (cl == ObjectOutputStream.class) {
            return;
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm == null) {
            return;
        }
        processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
        WeakClassKey key = new WeakClassKey(cl, Caches.subclassAuditsQueue);
        Boolean result = Caches.subclassAudits.get(key);
        if (result == null) {
            result = Boolean.valueOf(auditSubclass(cl));
            Caches.subclassAudits.putIfAbsent(key, result);
        }
        if (result.booleanValue()) {
            return;
        }
        sm.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
    }

    /**
     * Performs reflective checks on given subclass to verify that it doesn't
     * override security-sensitive non-final methods.  Returns true if subclass
     * is "safe", false otherwise.
     * 对给定的子类执行反射检查以验证它没有覆盖安全敏感的非最终方法。
     * 如果子类是“安全的”，则返回真，否则返回假
     */
    private static boolean auditSubclass(final Class<?> subcl) {
        Boolean result = AccessController.doPrivileged(
            new PrivilegedAction<Boolean>() {
                public Boolean run() {
                    for (Class<?> cl = subcl;
                         cl != ObjectOutputStream.class;
                         cl = cl.getSuperclass())
                    {
                        try {
                            cl.getDeclaredMethod(
                                "writeUnshared", new Class<?>[] { Object.class });
                            return Boolean.FALSE;
                        } catch (NoSuchMethodException ex) {
                        }
                        try {
                            cl.getDeclaredMethod("putFields", (Class<?>[]) null);
                            return Boolean.FALSE;
                        } catch (NoSuchMethodException ex) {
                        }
                    }
                    return Boolean.TRUE;
                }
            }
        );
        return result.booleanValue();
    }

    /**
     * Clears internal data structures.
     * 清除内部数据结构
     */
    private void clear() {
        subs.clear();
        handles.clear();
    }

    /**
     * Underlying writeObject/writeUnshared implementation.
     * 底层 writeObjectwriteUnshared 实现
     */
    private void writeObject0(Object obj, boolean unshared)
        throws IOException
    {
        boolean oldMode = bout.setBlockDataMode(false);
        depth++;
        try {
            // handle previously written and non-replaceable objects
            //处理以前编写的和不可替换的对象
            int h;
            if ((obj = subs.lookup(obj)) == null) {
                writeNull();
                return;
            } else if (!unshared && (h = handles.lookup(obj)) != -1) {
                writeHandle(h);
                return;
            } else if (obj instanceof Class) {
                writeClass((Class) obj, unshared);
                return;
            } else if (obj instanceof ObjectStreamClass) {
                writeClassDesc((ObjectStreamClass) obj, unshared);
                return;
            }

            // check for replacement object
            //检查替换对象
            Object orig = obj;
            Class<?> cl = obj.getClass();
            ObjectStreamClass desc;
            for (;;) {
                // REMIND: skip this check for strings/arrays?
                //提醒：跳过对字符串数组的检查？
                Class<?> repCl;
                desc = ObjectStreamClass.lookup(cl, true);
                if (!desc.hasWriteReplaceMethod() ||
                    (obj = desc.invokeWriteReplace(obj)) == null ||
                    (repCl = obj.getClass()) == cl)
                {
                    break;
                }
                cl = repCl;
            }
            if (enableReplace) {
                Object rep = replaceObject(obj);
                if (rep != obj && rep != null) {
                    cl = rep.getClass();
                    desc = ObjectStreamClass.lookup(cl, true);
                }
                obj = rep;
            }

            // if object replaced, run through original checks a second time
            //如果对象被替换，则第二次运行原始检查
            if (obj != orig) {
                subs.assign(orig, obj);
                if (obj == null) {
                    writeNull();
                    return;
                } else if (!unshared && (h = handles.lookup(obj)) != -1) {
                    writeHandle(h);
                    return;
                } else if (obj instanceof Class) {
                    writeClass((Class) obj, unshared);
                    return;
                } else if (obj instanceof ObjectStreamClass) {
                    writeClassDesc((ObjectStreamClass) obj, unshared);
                    return;
                }
            }

            // remaining cases
            //剩余案件
            if (obj instanceof String) {
                writeString((String) obj, unshared);
            } else if (cl.isArray()) {
                writeArray(obj, desc, unshared);
            } else if (obj instanceof Enum) {
                writeEnum((Enum<?>) obj, desc, unshared);
            } else if (obj instanceof Serializable) {
                writeOrdinaryObject(obj, desc, unshared);
            } else {
                if (extendedDebugInfo) {
                    throw new NotSerializableException(
                        cl.getName() + "\n" + debugInfoStack.toString());
                } else {
                    throw new NotSerializableException(cl.getName());
                }
            }
        } finally {
            depth--;
            bout.setBlockDataMode(oldMode);
        }
    }

    /**
     * Writes null code to stream.
     * 将空代码写入流
     */
    private void writeNull() throws IOException {
        bout.writeByte(TC_NULL);
    }

    /**
     * Writes given object handle to stream.
     * 将给定的对象句柄写入流
     */
    private void writeHandle(int handle) throws IOException {
        bout.writeByte(TC_REFERENCE);
        bout.writeInt(baseWireHandle + handle);
    }

    /**
     * Writes representation of given class to stream.
     * 将给定类的表示写入流
     */
    private void writeClass(Class<?> cl, boolean unshared) throws IOException {
        bout.writeByte(TC_CLASS);
        writeClassDesc(ObjectStreamClass.lookup(cl, true), false);
        handles.assign(unshared ? null : cl);
    }

    /**
     * Writes representation of given class descriptor to stream.
     * 将给定类描述符的表示写入流
     */
    private void writeClassDesc(ObjectStreamClass desc, boolean unshared)
        throws IOException
    {
        int handle;
        if (desc == null) {
            writeNull();
        } else if (!unshared && (handle = handles.lookup(desc)) != -1) {
            writeHandle(handle);
        } else if (desc.isProxy()) {
            writeProxyDesc(desc, unshared);
        } else {
            writeNonProxyDesc(desc, unshared);
        }
    }

    private boolean isCustomSubclass() {
        // Return true if this class is a custom subclass of ObjectOutputStream
        return getClass().getClassLoader()
                   != ObjectOutputStream.class.getClassLoader();
    }

    /**
     * Writes class descriptor representing a dynamic proxy class to stream.
     */
    private void writeProxyDesc(ObjectStreamClass desc, boolean unshared)
        throws IOException
    {
        bout.writeByte(TC_PROXYCLASSDESC);
        handles.assign(unshared ? null : desc);

        Class<?> cl = desc.forClass();
        Class<?>[] ifaces = cl.getInterfaces();
        bout.writeInt(ifaces.length);
        for (int i = 0; i < ifaces.length; i++) {
            bout.writeUTF(ifaces[i].getName());
        }

        bout.setBlockDataMode(true);
        if (cl != null && isCustomSubclass()) {
            ReflectUtil.checkPackageAccess(cl);
        }
        annotateProxyClass(cl);
        bout.setBlockDataMode(false);
        bout.writeByte(TC_ENDBLOCKDATA);

        writeClassDesc(desc.getSuperDesc(), false);
    }

    /**
     * Writes class descriptor representing a standard (i.e., not a dynamic
     * proxy) class to stream.
     */
    private void writeNonProxyDesc(ObjectStreamClass desc, boolean unshared)
        throws IOException
    {
        bout.writeByte(TC_CLASSDESC);
        handles.assign(unshared ? null : desc);

        if (protocol == PROTOCOL_VERSION_1) {
            // do not invoke class descriptor write hook with old protocol
            desc.writeNonProxy(this);
        } else {
            writeClassDescriptor(desc);
        }

        Class<?> cl = desc.forClass();
        bout.setBlockDataMode(true);
        if (cl != null && isCustomSubclass()) {
            ReflectUtil.checkPackageAccess(cl);
        }
        annotateClass(cl);
        bout.setBlockDataMode(false);
        bout.writeByte(TC_ENDBLOCKDATA);

        writeClassDesc(desc.getSuperDesc(), false);
    }

    /**
     * Writes given string to stream, using standard or long UTF format
     * depending on string length.
     * 使用标准或长 UTF 格式将给定的字符串写入流，具体取决于字符串长度
     */
    private void writeString(String str, boolean unshared) throws IOException {
        handles.assign(unshared ? null : str);
        long utflen = bout.getUTFLength(str);
        if (utflen <= 0xFFFF) {
            bout.writeByte(TC_STRING);
            bout.writeUTF(str, utflen);
        } else {
            bout.writeByte(TC_LONGSTRING);
            bout.writeLongUTF(str, utflen);
        }
    }

    /**
     * Writes given array object to stream.
     * 将给定的数组对象写入流
     */
    private void writeArray(Object array,
                            ObjectStreamClass desc,
                            boolean unshared)
        throws IOException
    {
        bout.writeByte(TC_ARRAY);
        writeClassDesc(desc, false);
        handles.assign(unshared ? null : array);

        Class<?> ccl = desc.forClass().getComponentType();
        if (ccl.isPrimitive()) {
            if (ccl == Integer.TYPE) {
                int[] ia = (int[]) array;
                bout.writeInt(ia.length);
                bout.writeInts(ia, 0, ia.length);
            } else if (ccl == Byte.TYPE) {
                byte[] ba = (byte[]) array;
                bout.writeInt(ba.length);
                bout.write(ba, 0, ba.length, true);
            } else if (ccl == Long.TYPE) {
                long[] ja = (long[]) array;
                bout.writeInt(ja.length);
                bout.writeLongs(ja, 0, ja.length);
            } else if (ccl == Float.TYPE) {
                float[] fa = (float[]) array;
                bout.writeInt(fa.length);
                bout.writeFloats(fa, 0, fa.length);
            } else if (ccl == Double.TYPE) {
                double[] da = (double[]) array;
                bout.writeInt(da.length);
                bout.writeDoubles(da, 0, da.length);
            } else if (ccl == Short.TYPE) {
                short[] sa = (short[]) array;
                bout.writeInt(sa.length);
                bout.writeShorts(sa, 0, sa.length);
            } else if (ccl == Character.TYPE) {
                char[] ca = (char[]) array;
                bout.writeInt(ca.length);
                bout.writeChars(ca, 0, ca.length);
            } else if (ccl == Boolean.TYPE) {
                boolean[] za = (boolean[]) array;
                bout.writeInt(za.length);
                bout.writeBooleans(za, 0, za.length);
            } else {
                throw new InternalError();
            }
        } else {
            Object[] objs = (Object[]) array;
            int len = objs.length;
            bout.writeInt(len);
            if (extendedDebugInfo) {
                debugInfoStack.push(
                    "array (class \"" + array.getClass().getName() +
                    "\", size: " + len  + ")");
            }
            try {
                for (int i = 0; i < len; i++) {
                    if (extendedDebugInfo) {
                        debugInfoStack.push(
                            "element of array (index: " + i + ")");
                    }
                    try {
                        writeObject0(objs[i], false);
                    } finally {
                        if (extendedDebugInfo) {
                            debugInfoStack.pop();
                        }
                    }
                }
            } finally {
                if (extendedDebugInfo) {
                    debugInfoStack.pop();
                }
            }
        }
    }

    /**
     * Writes given enum constant to stream.
     * 将给定的枚举常量写入流
     */
    private void writeEnum(Enum<?> en,
                           ObjectStreamClass desc,
                           boolean unshared)
        throws IOException
    {
        bout.writeByte(TC_ENUM);
        ObjectStreamClass sdesc = desc.getSuperDesc();
        writeClassDesc((sdesc.forClass() == Enum.class) ? desc : sdesc, false);
        handles.assign(unshared ? null : en);
        writeString(en.name(), false);
    }

    /**
     * Writes representation of a "ordinary" (i.e., not a String, Class,
     * ObjectStreamClass, array, or enum constant) serializable object to the
     * stream.
     * 将“普通”（即，不是字符串、类、ObjectStreamClass、数组或枚举常量）可序列化对象的表示形式写入流
     */
    private void writeOrdinaryObject(Object obj,
                                     ObjectStreamClass desc,
                                     boolean unshared)
        throws IOException
    {
        if (extendedDebugInfo) {
            debugInfoStack.push(
                (depth == 1 ? "root " : "") + "object (class \"" +
                obj.getClass().getName() + "\", " + obj.toString() + ")");
        }
        try {
            desc.checkSerialize();

            bout.writeByte(TC_OBJECT);
            writeClassDesc(desc, false);
            handles.assign(unshared ? null : obj);
            if (desc.isExternalizable() && !desc.isProxy()) {
                writeExternalData((Externalizable) obj);
            } else {
                writeSerialData(obj, desc);
            }
        } finally {
            if (extendedDebugInfo) {
                debugInfoStack.pop();
            }
        }
    }

    /**
     * Writes externalizable data of given object by invoking its
     * writeExternal() method.
     * 通过调用其 writeExternal() 方法写入给定对象的可外部化数据
     */
    private void writeExternalData(Externalizable obj) throws IOException {
        PutFieldImpl oldPut = curPut;
        curPut = null;

        if (extendedDebugInfo) {
            debugInfoStack.push("writeExternal data");
        }
        SerialCallbackContext oldContext = curContext;
        try {
            curContext = null;
            if (protocol == PROTOCOL_VERSION_1) {
                obj.writeExternal(this);
            } else {
                bout.setBlockDataMode(true);
                obj.writeExternal(this);
                bout.setBlockDataMode(false);
                bout.writeByte(TC_ENDBLOCKDATA);
            }
        } finally {
            curContext = oldContext;
            if (extendedDebugInfo) {
                debugInfoStack.pop();
            }
        }

        curPut = oldPut;
    }

    /**
     * Writes instance data for each serializable class of given object, from
     * superclass to subclass.
     * 为给定对象的每个可序列化类写入实例数据，从超类到子类
     */
    private void writeSerialData(Object obj, ObjectStreamClass desc)
        throws IOException
    {
        ObjectStreamClass.ClassDataSlot[] slots = desc.getClassDataLayout();
        for (int i = 0; i < slots.length; i++) {
            ObjectStreamClass slotDesc = slots[i].desc;
            if (slotDesc.hasWriteObjectMethod()) {
                PutFieldImpl oldPut = curPut;
                curPut = null;
                SerialCallbackContext oldContext = curContext;

                if (extendedDebugInfo) {
                    debugInfoStack.push(
                        "custom writeObject data (class \"" +
                        slotDesc.getName() + "\")");
                }
                try {
                    curContext = new SerialCallbackContext(obj, slotDesc);
                    bout.setBlockDataMode(true);
                    slotDesc.invokeWriteObject(obj, this);
                    bout.setBlockDataMode(false);
                    bout.writeByte(TC_ENDBLOCKDATA);
                } finally {
                    curContext.setUsed();
                    curContext = oldContext;
                    if (extendedDebugInfo) {
                        debugInfoStack.pop();
                    }
                }

                curPut = oldPut;
            } else {
                defaultWriteFields(obj, slotDesc);
            }
        }
    }

    /**
     * Fetches and writes values of serializable fields of given object to
     * stream.  The given class descriptor specifies which field values to
     * write, and in which order they should be written.
     * 获取给定对象的可序列化字段的值并将其写入流。给定的类描述符指定要写入的字段值，以及它们的写入顺序
     */
    private void defaultWriteFields(Object obj, ObjectStreamClass desc)
        throws IOException
    {
        Class<?> cl = desc.forClass();
        if (cl != null && obj != null && !cl.isInstance(obj)) {
            throw new ClassCastException();
        }

        desc.checkDefaultSerialize();

        int primDataSize = desc.getPrimDataSize();
        if (primVals == null || primVals.length < primDataSize) {
            primVals = new byte[primDataSize];
        }
        desc.getPrimFieldValues(obj, primVals);
        bout.write(primVals, 0, primDataSize, false);

        ObjectStreamField[] fields = desc.getFields(false);
        Object[] objVals = new Object[desc.getNumObjFields()];
        int numPrimFields = fields.length - objVals.length;
        desc.getObjFieldValues(obj, objVals);
        for (int i = 0; i < objVals.length; i++) {
            if (extendedDebugInfo) {
                debugInfoStack.push(
                    "field (class \"" + desc.getName() + "\", name: \"" +
                    fields[numPrimFields + i].getName() + "\", type: \"" +
                    fields[numPrimFields + i].getType() + "\")");
            }
            try {
                writeObject0(objVals[i],
                             fields[numPrimFields + i].isUnshared());
            } finally {
                if (extendedDebugInfo) {
                    debugInfoStack.pop();
                }
            }
        }
    }

    /**
     * Attempts to write to stream fatal IOException that has caused
     * serialization to abort.
     * 尝试写入导致序列化中止的致命 IOException 流
     */
    private void writeFatalException(IOException ex) throws IOException {
        /*
         * Note: the serialization specification states that if a second
         * IOException occurs while attempting to serialize the original fatal
         * exception to the stream, then a StreamCorruptedException should be
         * thrown (section 2.1).  However, due to a bug in previous
         * implementations of serialization, StreamCorruptedExceptions were
         * rarely (if ever) actually thrown--the "root" exceptions from
         * underlying streams were thrown instead.  This historical behavior is
         * followed here for consistency.
         * 注意：序列化规范规定，如果在尝试将原始致命异常序列化到流时发生第二个 IOException，
         * 则应抛出 StreamCorruptedException（第 2.1 节）。
         * 然而，由于之前序列化实现中的一个错误，StreamCorruptedExceptions 很少
         * （如果有的话）实际上被抛出——而是从底层流中抛出“根”异常。
         * 此处遵循此历史行为以保持一致性。
         */
        clear();
        boolean oldMode = bout.setBlockDataMode(false);
        try {
            bout.writeByte(TC_EXCEPTION);
            writeObject0(ex, false);
            clear();
        } finally {
            bout.setBlockDataMode(oldMode);
        }
    }

    /**
     * Converts specified span of float values into byte values.
     */
    // REMIND: remove once hotspot inlines Float.floatToIntBits
    //将指定范围的浮点值转换为字节值。提醒：删除一次热点内联 Float.floatToIntBits
    private static native void floatsToBytes(float[] src, int srcpos,
                                             byte[] dst, int dstpos,
                                             int nfloats);

    /**
     * Converts specified span of double values into byte values.
     */
    // REMIND: remove once hotspot inlines Double.doubleToLongBits
    //将双精度值的指定范围转换为字节值。提醒：删除一次热点内联 Double.doubleToLongBits
    private static native void doublesToBytes(double[] src, int srcpos,
                                              byte[] dst, int dstpos,
                                              int ndoubles);

    /**
     * Default PutField implementation.
     * 默认 PutField 实现
     */
    private class PutFieldImpl extends PutField {

        /** class descriptor describing serializable fields */
        //描述可序列化字段的类描述符
        private final ObjectStreamClass desc;
        /** primitive field values */
        //原始字段值
        private final byte[] primVals;
        /** object field values */
        //对象字段值
        private final Object[] objVals;

        /**
         * Creates PutFieldImpl object for writing fields defined in given
         * class descriptor.
         * 创建 PutFieldImpl 对象，用于写入在给定类描述符中定义的字段
         */
        PutFieldImpl(ObjectStreamClass desc) {
            this.desc = desc;
            primVals = new byte[desc.getPrimDataSize()];
            objVals = new Object[desc.getNumObjFields()];
        }

        public void put(String name, boolean val) {
            Bits.putBoolean(primVals, getFieldOffset(name, Boolean.TYPE), val);
        }

        public void put(String name, byte val) {
            primVals[getFieldOffset(name, Byte.TYPE)] = val;
        }

        public void put(String name, char val) {
            Bits.putChar(primVals, getFieldOffset(name, Character.TYPE), val);
        }

        public void put(String name, short val) {
            Bits.putShort(primVals, getFieldOffset(name, Short.TYPE), val);
        }

        public void put(String name, int val) {
            Bits.putInt(primVals, getFieldOffset(name, Integer.TYPE), val);
        }

        public void put(String name, float val) {
            Bits.putFloat(primVals, getFieldOffset(name, Float.TYPE), val);
        }

        public void put(String name, long val) {
            Bits.putLong(primVals, getFieldOffset(name, Long.TYPE), val);
        }

        public void put(String name, double val) {
            Bits.putDouble(primVals, getFieldOffset(name, Double.TYPE), val);
        }

        public void put(String name, Object val) {
            objVals[getFieldOffset(name, Object.class)] = val;
        }

        // deprecated in ObjectOutputStream.PutField
        //在 ObjectOutputStream.PutField 中弃用
        public void write(ObjectOutput out) throws IOException {
            /*
             * Applications should *not* use this method to write PutField
             * data, as it will lead to stream corruption if the PutField
             * object writes any primitive data (since block data mode is not
             * unset/set properly, as is done in OOS.writeFields()).  This
             * broken implementation is being retained solely for behavioral
             * compatibility, in order to support applications which use
             * OOS.PutField.write() for writing only non-primitive data.
             * 1.应用程序不应使用此方法写入 PutField 数据，因为如果 PutField 对象写入任何原始数据，
             * 它将导致流损坏（因为块数据模式未正确设置，如在 OOS.writeFields() 中所做的那样）。
             * 保留此损坏的实现仅是为了行为兼容性，以支持使用 OOS.PutField.write() 仅写入非原始数据的应用程序
             * Serialization of unshared objects is not implemented here since
             * it is not necessary for backwards compatibility; also, unshared
             * semantics may not be supported by the given ObjectOutput
             * instance.  Applications which write unshared objects using the
             * PutField API must use OOS.writeFields().
             * 2.这里没有实现非共享对象的序列化，因为它不是向后兼容所必需的；
             * 此外，给定的 ObjectOutput 实例可能不支持非共享语义。
             * 使用 PutField API 写入非共享对象的应用程序必须使用 OOS.writeFields()
             */
            if (ObjectOutputStream.this != out) {
                throw new IllegalArgumentException("wrong stream");
            }
            out.write(primVals, 0, primVals.length);

            ObjectStreamField[] fields = desc.getFields(false);
            int numPrimFields = fields.length - objVals.length;
            // REMIND: warn if numPrimFields > 0?
            for (int i = 0; i < objVals.length; i++) {
                if (fields[numPrimFields + i].isUnshared()) {
                    throw new IOException("cannot write unshared object");
                }
                out.writeObject(objVals[i]);
            }
        }

        /**
         * Writes buffered primitive data and object fields to stream.
         * 将缓冲的原始数据和对象字段写入流
         */
        void writeFields() throws IOException {
            bout.write(primVals, 0, primVals.length, false);

            ObjectStreamField[] fields = desc.getFields(false);
            int numPrimFields = fields.length - objVals.length;
            for (int i = 0; i < objVals.length; i++) {
                if (extendedDebugInfo) {
                    debugInfoStack.push(
                        "field (class \"" + desc.getName() + "\", name: \"" +
                        fields[numPrimFields + i].getName() + "\", type: \"" +
                        fields[numPrimFields + i].getType() + "\")");
                }
                try {
                    writeObject0(objVals[i],
                                 fields[numPrimFields + i].isUnshared());
                } finally {
                    if (extendedDebugInfo) {
                        debugInfoStack.pop();
                    }
                }
            }
        }

        /**
         * Returns offset of field with given name and type.  A specified type
         * of null matches all types, Object.class matches all non-primitive
         * types, and any other non-null type matches assignable types only.
         * Throws IllegalArgumentException if no matching field found.
         * 返回具有给定名称和类型的字段的偏移量。
         * 指定类型的 null 匹配所有类型，Object.class 匹配所有非原始类型，任何其他非 null 类型仅匹配可分配类型。
         * 如果找不到匹配的字段，则抛出 IllegalArgumentException
         */
        private int getFieldOffset(String name, Class<?> type) {
            ObjectStreamField field = desc.getField(name, type);
            if (field == null) {
                throw new IllegalArgumentException("no such field " + name +
                                                   " with type " + type);
            }
            return field.getOffset();
        }
    }

    /**
     * Buffered output stream with two modes: in default mode, outputs data in
     * same format as DataOutputStream; in "block data" mode, outputs data
     * bracketed by block data markers (see object serialization specification
     * for details).
     * 有两种模式的缓冲输出流：在默认模式下，以与DataOutputStream相同的格式输出数据；
     * 在“块数据”模式下，输出由块数据标记括起来的数据（有关详细信息，请参阅对象序列化规范）
     */
    private static class BlockDataOutputStream
        extends OutputStream implements DataOutput
    {
        /** maximum data block length */
        //最大数据块长度
        private static final int MAX_BLOCK_SIZE = 1024;
        /** maximum data block header length
         * //最大数据块头长度
         */
        private static final int MAX_HEADER_SIZE = 5;
        /** (tunable) length of char buffer (for writing strings) */
        //（可调）字符缓冲区的长度（用于写入字符串)
        private static final int CHAR_BUF_SIZE = 256;

        /** buffer for writing general/block data */
        //用于写入通用/块数据的缓冲区
        private final byte[] buf = new byte[MAX_BLOCK_SIZE];
        /** buffer for writing block data headers */
        //用于写入块数据头的缓冲区
        private final byte[] hbuf = new byte[MAX_HEADER_SIZE];
        /** char buffer for fast string writes */
        //用于快速字符串写入的字符缓冲区
        private final char[] cbuf = new char[CHAR_BUF_SIZE];

        /** block data mode */
        //块数据模式
        private boolean blkmode = false;
        /** current offset into buf */
        //当前偏移到 buf
        private int pos = 0;

        /** underlying output stream */
        //底层输出流
        private final OutputStream out;
        /** loopback stream (for data writes that span data blocks) */
        //环回流（用于跨越数据块的数据写入）
        private final DataOutputStream dout;

        /**
         * Creates new BlockDataOutputStream on top of given underlying stream.
         * Block data mode is turned off by default.
         * 在给定的底层流之上创建新的 BlockDataOutputStream。 块数据模式默认关闭
         */
        BlockDataOutputStream(OutputStream out) {
            this.out = out;
            dout = new DataOutputStream(this);
        }

        /**
         * Sets block data mode to the given mode (true == on, false == off)
         * and returns the previous mode value.  If the new mode is the same as
         * the old mode, no action is taken.  If the new mode differs from the
         * old mode, any buffered data is flushed before switching to the new
         * mode.
         * 将块数据模式设置为给定模式（true == on，false == off）并返回先前的模式值。
         * 如果新模式与旧模式相同，则不执行任何操作。
         * 如果新模式与旧模式不同，则在切换到新模式之前刷新所有缓冲数据
         */
        boolean setBlockDataMode(boolean mode) throws IOException {
            if (blkmode == mode) {
                return blkmode;
            }
            drain();
            blkmode = mode;
            return !blkmode;
        }

        /**
         * Returns true if the stream is currently in block data mode, false
         * otherwise.
         * 如果流当前处于块数据模式，则返回 true，否则返回 false
         */
        boolean getBlockDataMode() {
            return blkmode;
        }

        /* ----------------- generic output stream methods ----------------- */
        /*
         * The following methods are equivalent to their counterparts in
         * OutputStream, except that they partition written data into data
         * blocks when in block data mode.
         */

        public void write(int b) throws IOException {
            if (pos >= MAX_BLOCK_SIZE) {
                drain();
            }
            buf[pos++] = (byte) b;
        }

        public void write(byte[] b) throws IOException {
            write(b, 0, b.length, false);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            write(b, off, len, false);
        }

        public void flush() throws IOException {
            drain();
            out.flush();
        }

        public void close() throws IOException {
            flush();
            out.close();
        }

        /**
         * Writes specified span of byte values from given array.  If copy is
         * true, copies the values to an intermediate buffer before writing
         * them to underlying stream (to avoid exposing a reference to the
         * original byte array).
         * 从给定数组写入指定范围的字节值。
         * 如果 copy 为真，则在将值写入底层流之前将值复制到中间缓冲区（以避免暴露对原始字节数组的引用）
         */
        void write(byte[] b, int off, int len, boolean copy)
            throws IOException
        {
            if (!(copy || blkmode)) {           // write directly
                drain();
                out.write(b, off, len);
                return;
            }

            while (len > 0) {
                if (pos >= MAX_BLOCK_SIZE) {
                    drain();
                }
                if (len >= MAX_BLOCK_SIZE && !copy && pos == 0) {
                    // avoid unnecessary copy
                    writeBlockHeader(MAX_BLOCK_SIZE);
                    out.write(b, off, MAX_BLOCK_SIZE);
                    off += MAX_BLOCK_SIZE;
                    len -= MAX_BLOCK_SIZE;
                } else {
                    int wlen = Math.min(len, MAX_BLOCK_SIZE - pos);
                    System.arraycopy(b, off, buf, pos, wlen);
                    pos += wlen;
                    off += wlen;
                    len -= wlen;
                }
            }
        }

        /**
         * Writes all buffered data from this stream to the underlying stream,
         * but does not flush underlying stream.
         * 将此流中的所有缓冲数据写入底层流，但不刷新底层流
         */
        void drain() throws IOException {
            if (pos == 0) {
                return;
            }
            if (blkmode) {
                writeBlockHeader(pos);
            }
            out.write(buf, 0, pos);
            pos = 0;
        }

        /**
         * Writes block data header.  Data blocks shorter than 256 bytes are
         * prefixed with a 2-byte header; all others start with a 5-byte
         * header.
         * 写入块数据头。 短于 256 字节的数据块以 2 字节的报头为前缀； 所有其他的都以 5 字节的标头开头
         */
        private void writeBlockHeader(int len) throws IOException {
            if (len <= 0xFF) {
                hbuf[0] = TC_BLOCKDATA;
                hbuf[1] = (byte) len;
                out.write(hbuf, 0, 2);
            } else {
                hbuf[0] = TC_BLOCKDATALONG;
                Bits.putInt(hbuf, 1, len);
                out.write(hbuf, 0, 5);
            }
        }


        /* ----------------- primitive data output methods ----------------- */
        /*
         * The following methods are equivalent to their counterparts in
         * DataOutputStream, except that they partition written data into data
         * blocks when in block data mode.
         */
        public void writeBoolean(boolean v) throws IOException {
            if (pos >= MAX_BLOCK_SIZE) {
                drain();
            }
            Bits.putBoolean(buf, pos++, v);
        }

        public void writeByte(int v) throws IOException {
            if (pos >= MAX_BLOCK_SIZE) {
                drain();
            }
            buf[pos++] = (byte) v;
        }

        public void writeChar(int v) throws IOException {
            if (pos + 2 <= MAX_BLOCK_SIZE) {
                Bits.putChar(buf, pos, (char) v);
                pos += 2;
            } else {
                dout.writeChar(v);
            }
        }

        public void writeShort(int v) throws IOException {
            if (pos + 2 <= MAX_BLOCK_SIZE) {
                Bits.putShort(buf, pos, (short) v);
                pos += 2;
            } else {
                dout.writeShort(v);
            }
        }

        public void writeInt(int v) throws IOException {
            if (pos + 4 <= MAX_BLOCK_SIZE) {
                Bits.putInt(buf, pos, v);
                pos += 4;
            } else {
                dout.writeInt(v);
            }
        }

        public void writeFloat(float v) throws IOException {
            if (pos + 4 <= MAX_BLOCK_SIZE) {
                Bits.putFloat(buf, pos, v);
                pos += 4;
            } else {
                dout.writeFloat(v);
            }
        }

        public void writeLong(long v) throws IOException {
            if (pos + 8 <= MAX_BLOCK_SIZE) {
                Bits.putLong(buf, pos, v);
                pos += 8;
            } else {
                dout.writeLong(v);
            }
        }

        public void writeDouble(double v) throws IOException {
            if (pos + 8 <= MAX_BLOCK_SIZE) {
                Bits.putDouble(buf, pos, v);
                pos += 8;
            } else {
                dout.writeDouble(v);
            }
        }

        public void writeBytes(String s) throws IOException {
            int endoff = s.length();
            int cpos = 0;
            int csize = 0;
            for (int off = 0; off < endoff; ) {
                if (cpos >= csize) {
                    cpos = 0;
                    csize = Math.min(endoff - off, CHAR_BUF_SIZE);
                    s.getChars(off, off + csize, cbuf, 0);
                }
                if (pos >= MAX_BLOCK_SIZE) {
                    drain();
                }
                int n = Math.min(csize - cpos, MAX_BLOCK_SIZE - pos);
                int stop = pos + n;
                while (pos < stop) {
                    buf[pos++] = (byte) cbuf[cpos++];
                }
                off += n;
            }
        }

        public void writeChars(String s) throws IOException {
            int endoff = s.length();
            for (int off = 0; off < endoff; ) {
                int csize = Math.min(endoff - off, CHAR_BUF_SIZE);
                s.getChars(off, off + csize, cbuf, 0);
                writeChars(cbuf, 0, csize);
                off += csize;
            }
        }

        public void writeUTF(String s) throws IOException {
            writeUTF(s, getUTFLength(s));
        }


        /* -------------- primitive data array output methods -------------- */
        /*
         * The following methods write out spans of primitive data values.
         * Though equivalent to calling the corresponding primitive write
         * methods repeatedly, these methods are optimized for writing groups
         * of primitive data values more efficiently.
         */

        void writeBooleans(boolean[] v, int off, int len) throws IOException {
            int endoff = off + len;
            while (off < endoff) {
                if (pos >= MAX_BLOCK_SIZE) {
                    drain();
                }
                int stop = Math.min(endoff, off + (MAX_BLOCK_SIZE - pos));
                while (off < stop) {
                    Bits.putBoolean(buf, pos++, v[off++]);
                }
            }
        }

        void writeChars(char[] v, int off, int len) throws IOException {
            int limit = MAX_BLOCK_SIZE - 2;
            int endoff = off + len;
            while (off < endoff) {
                if (pos <= limit) {
                    int avail = (MAX_BLOCK_SIZE - pos) >> 1;
                    int stop = Math.min(endoff, off + avail);
                    while (off < stop) {
                        Bits.putChar(buf, pos, v[off++]);
                        pos += 2;
                    }
                } else {
                    dout.writeChar(v[off++]);
                }
            }
        }

        void writeShorts(short[] v, int off, int len) throws IOException {
            int limit = MAX_BLOCK_SIZE - 2;
            int endoff = off + len;
            while (off < endoff) {
                if (pos <= limit) {
                    int avail = (MAX_BLOCK_SIZE - pos) >> 1;
                    int stop = Math.min(endoff, off + avail);
                    while (off < stop) {
                        Bits.putShort(buf, pos, v[off++]);
                        pos += 2;
                    }
                } else {
                    dout.writeShort(v[off++]);
                }
            }
        }

        void writeInts(int[] v, int off, int len) throws IOException {
            int limit = MAX_BLOCK_SIZE - 4;
            int endoff = off + len;
            while (off < endoff) {
                if (pos <= limit) {
                    int avail = (MAX_BLOCK_SIZE - pos) >> 2;
                    int stop = Math.min(endoff, off + avail);
                    while (off < stop) {
                        Bits.putInt(buf, pos, v[off++]);
                        pos += 4;
                    }
                } else {
                    dout.writeInt(v[off++]);
                }
            }
        }

        void writeFloats(float[] v, int off, int len) throws IOException {
            int limit = MAX_BLOCK_SIZE - 4;
            int endoff = off + len;
            while (off < endoff) {
                if (pos <= limit) {
                    int avail = (MAX_BLOCK_SIZE - pos) >> 2;
                    int chunklen = Math.min(endoff - off, avail);
                    floatsToBytes(v, off, buf, pos, chunklen);
                    off += chunklen;
                    pos += chunklen << 2;
                } else {
                    dout.writeFloat(v[off++]);
                }
            }
        }

        void writeLongs(long[] v, int off, int len) throws IOException {
            int limit = MAX_BLOCK_SIZE - 8;
            int endoff = off + len;
            while (off < endoff) {
                if (pos <= limit) {
                    int avail = (MAX_BLOCK_SIZE - pos) >> 3;
                    int stop = Math.min(endoff, off + avail);
                    while (off < stop) {
                        Bits.putLong(buf, pos, v[off++]);
                        pos += 8;
                    }
                } else {
                    dout.writeLong(v[off++]);
                }
            }
        }

        void writeDoubles(double[] v, int off, int len) throws IOException {
            int limit = MAX_BLOCK_SIZE - 8;
            int endoff = off + len;
            while (off < endoff) {
                if (pos <= limit) {
                    int avail = (MAX_BLOCK_SIZE - pos) >> 3;
                    int chunklen = Math.min(endoff - off, avail);
                    doublesToBytes(v, off, buf, pos, chunklen);
                    off += chunklen;
                    pos += chunklen << 3;
                } else {
                    dout.writeDouble(v[off++]);
                }
            }
        }

        /**
         * Returns the length in bytes of the UTF encoding of the given string.
         * 返回给定字符串的 UTF 编码的字节长度。
         */
        long getUTFLength(String s) {
            int len = s.length();
            long utflen = 0;
            for (int off = 0; off < len; ) {
                int csize = Math.min(len - off, CHAR_BUF_SIZE);
                s.getChars(off, off + csize, cbuf, 0);
                for (int cpos = 0; cpos < csize; cpos++) {
                    char c = cbuf[cpos];
                    if (c >= 0x0001 && c <= 0x007F) {
                        utflen++;
                    } else if (c > 0x07FF) {
                        utflen += 3;
                    } else {
                        utflen += 2;
                    }
                }
                off += csize;
            }
            return utflen;
        }

        /**
         * Writes the given string in UTF format.  This method is used in
         * situations where the UTF encoding length of the string is already
         * known; specifying it explicitly avoids a prescan of the string to
         * determine its UTF length.
         * 以 UTF 格式写入给定的字符串。
         * 该方法用于字符串的UTF编码长度已知的情况； 明确指定它可以避免对字符串进行预扫描以确定其 UTF 长度。
         */
        void writeUTF(String s, long utflen) throws IOException {
            if (utflen > 0xFFFFL) {
                throw new UTFDataFormatException();
            }
            writeShort((int) utflen);
            if (utflen == (long) s.length()) {
                writeBytes(s);
            } else {
                writeUTFBody(s);
            }
        }

        /**
         * Writes given string in "long" UTF format.  "Long" UTF format is
         * identical to standard UTF, except that it uses an 8 byte header
         * (instead of the standard 2 bytes) to convey the UTF encoding length.
         * 以“长”UTF 格式写入给定字符串。
         * “长”UTF 格式与标准 UTF 相同，不同之处在于它使用 8 字节的标头（而不是标准的 2 字节）来传达 UTF 编码长度
         */
        void writeLongUTF(String s) throws IOException {
            writeLongUTF(s, getUTFLength(s));
        }

        /**
         * Writes given string in "long" UTF format, where the UTF encoding
         * length of the string is already known.
         * 以“长”UTF 格式写入给定字符串，其中字符串的 UTF 编码长度已知
         */
        void writeLongUTF(String s, long utflen) throws IOException {
            writeLong(utflen);
            if (utflen == (long) s.length()) {
                writeBytes(s);
            } else {
                writeUTFBody(s);
            }
        }

        /**
         * Writes the "body" (i.e., the UTF representation minus the 2-byte or
         * 8-byte length header) of the UTF encoding for the given string.
         * 写入给定字符串的 UTF 编码的“主体”（即 UTF 表示减去 2 字节或 8 字节长度的标头）
         */
        private void writeUTFBody(String s) throws IOException {
            int limit = MAX_BLOCK_SIZE - 3;
            int len = s.length();
            for (int off = 0; off < len; ) {
                int csize = Math.min(len - off, CHAR_BUF_SIZE);
                s.getChars(off, off + csize, cbuf, 0);
                for (int cpos = 0; cpos < csize; cpos++) {
                    char c = cbuf[cpos];
                    if (pos <= limit) {
                        if (c <= 0x007F && c != 0) {
                            buf[pos++] = (byte) c;
                        } else if (c > 0x07FF) {
                            buf[pos + 2] = (byte) (0x80 | ((c >> 0) & 0x3F));
                            buf[pos + 1] = (byte) (0x80 | ((c >> 6) & 0x3F));
                            buf[pos + 0] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                            pos += 3;
                        } else {
                            buf[pos + 1] = (byte) (0x80 | ((c >> 0) & 0x3F));
                            buf[pos + 0] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                            pos += 2;
                        }
                    } else {    // write one byte at a time to normalize block
                        if (c <= 0x007F && c != 0) {
                            write(c);
                        } else if (c > 0x07FF) {
                            write(0xE0 | ((c >> 12) & 0x0F));
                            write(0x80 | ((c >> 6) & 0x3F));
                            write(0x80 | ((c >> 0) & 0x3F));
                        } else {
                            write(0xC0 | ((c >> 6) & 0x1F));
                            write(0x80 | ((c >> 0) & 0x3F));
                        }
                    }
                }
                off += csize;
            }
        }
    }

    /**
     * Lightweight identity hash table which maps objects to integer handles,
     * assigned in ascending order.
     * 将对象映射到整数句柄的轻量级身份哈希表，按升序分配
     */
    private static class HandleTable {

        /* number of mappings in table/next available handle */
        private int size;
        /* size threshold determining when to expand hash spine */
        private int threshold;
        /* factor for computing size threshold */
        private final float loadFactor;
        /* maps hash value -> candidate handle value */
        private int[] spine;
        /* maps handle value -> next candidate handle value */
        private int[] next;
        /* maps handle value -> associated object */
        private Object[] objs;

        /**
         * Creates new HandleTable with given capacity and load factor.
         * 使用给定的容量和负载因子创建新的 HandleTable
         */
        HandleTable(int initialCapacity, float loadFactor) {
            this.loadFactor = loadFactor;
            spine = new int[initialCapacity];
            next = new int[initialCapacity];
            objs = new Object[initialCapacity];
            threshold = (int) (initialCapacity * loadFactor);
            clear();
        }

        /**
         * Assigns next available handle to given object, and returns handle
         * value.  Handles are assigned in ascending order starting at 0.
         * 将下一个可用句柄分配给给定对象，并返回句柄值。 句柄从 0 开始按升序分配
         */
        int assign(Object obj) {
            if (size >= next.length) {
                growEntries();
            }
            if (size >= threshold) {
                growSpine();
            }
            insert(obj, size);
            return size++;
        }

        /**
         * Looks up and returns handle associated with given object, or -1 if
         * no mapping found.
         * 查找并返回与给定对象关联的句柄，如果未找到映射，则返回 -1。
         */
        int lookup(Object obj) {
            if (size == 0) {
                return -1;
            }
            int index = hash(obj) % spine.length;
            for (int i = spine[index]; i >= 0; i = next[i]) {
                if (objs[i] == obj) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Resets table to its initial (empty) state.
         * 将表重置为其初始（空）状态。
         */
        void clear() {
            Arrays.fill(spine, -1);
            Arrays.fill(objs, 0, size, null);
            size = 0;
        }

        /**
         * Returns the number of mappings currently in table.
         * 返回表中当前的映射数。
         */
        int size() {
            return size;
        }

        /**
         * Inserts mapping object -> handle mapping into table.  Assumes table
         * is large enough to accommodate new mapping.
         * 插入映射对象 -> 处理映射到表中。 假设表足够大以容纳新映射
         */
        private void insert(Object obj, int handle) {
            int index = hash(obj) % spine.length;
            objs[handle] = obj;
            next[handle] = spine[index];
            spine[index] = handle;
        }

        /**
         * Expands the hash "spine" -- equivalent to increasing the number of
         * buckets in a conventional hash table.
         * 扩展散列“脊椎”——相当于增加传统散列表中的桶数
         */
        private void growSpine() {
            spine = new int[(spine.length << 1) + 1];
            threshold = (int) (spine.length * loadFactor);
            Arrays.fill(spine, -1);
            for (int i = 0; i < size; i++) {
                insert(objs[i], i);
            }
        }

        /**
         * Increases hash table capacity by lengthening entry arrays.
         * 通过加长条目数组来增加哈希表容量。
         */
        private void growEntries() {
            int newLength = (next.length << 1) + 1;
            int[] newNext = new int[newLength];
            System.arraycopy(next, 0, newNext, 0, size);
            next = newNext;

            Object[] newObjs = new Object[newLength];
            System.arraycopy(objs, 0, newObjs, 0, size);
            objs = newObjs;
        }

        /**
         * Returns hash value for given object.
         * 返回给定对象的哈希值。
         */
        private int hash(Object obj) {
            return System.identityHashCode(obj) & 0x7FFFFFFF;
        }
    }

    /**
     * Lightweight identity hash table which maps objects to replacement
     * objects.
     * 将对象映射到替换对象的轻量级身份哈希表
     */
    private static class ReplaceTable {

        /* maps object -> index */
        private final HandleTable htab;
        /* maps index -> replacement object */
        private Object[] reps;

        /**
         * Creates new ReplaceTable with given capacity and load factor.
         * 使用给定的容量和负载因子创建新的 ReplaceTable
         */
        ReplaceTable(int initialCapacity, float loadFactor) {
            htab = new HandleTable(initialCapacity, loadFactor);
            reps = new Object[initialCapacity];
        }

        /**
         * Enters mapping from object to replacement object.
         * 输入从对象到替换对象的映射。
         */
        void assign(Object obj, Object rep) {
            int index = htab.assign(obj);
            while (index >= reps.length) {
                grow();
            }
            reps[index] = rep;
        }

        /**
         * Looks up and returns replacement for given object.  If no
         * replacement is found, returns the lookup object itself.
         * 查找并返回给定对象的替换。 如果未找到替换，则返回查找对象本身。
         */
        Object lookup(Object obj) {
            int index = htab.lookup(obj);
            return (index >= 0) ? reps[index] : obj;
        }

        /**
         * Resets table to its initial (empty) state.
         * 将表重置为其初始（空）状态
         */
        void clear() {
            Arrays.fill(reps, 0, htab.size(), null);
            htab.clear();
        }

        /**
         * Returns the number of mappings currently in table.
         * 返回表中当前的映射数
         */
        int size() {
            return htab.size();
        }

        /**
         * Increases table capacity.
         * 增加桌子容量。
         */
        private void grow() {
            Object[] newReps = new Object[(reps.length << 1) + 1];
            System.arraycopy(reps, 0, newReps, 0, reps.length);
            reps = newReps;
        }
    }

    /**
     * Stack to keep debug information about the state of the
     * serialization process, for embedding in exception messages.
     * 堆栈以保留有关序列化过程状态的调试信息，用于嵌入异常消息。
     */
    private static class DebugTraceInfoStack {
        private final List<String> stack;

        DebugTraceInfoStack() {
            stack = new ArrayList<>();
        }

        /**
         * Removes all of the elements from enclosed list.
         * 从封闭列表中删除所有元素。
         */
        void clear() {
            stack.clear();
        }

        /**
         * Removes the object at the top of enclosed list.
         * 删除封闭列表顶部的对象
         */
        void pop() {
            stack.remove(stack.size()-1);
        }

        /**
         * Pushes a String onto the top of enclosed list.
         * 将字符串推送到封闭列表的顶部。
         */
        void push(String entry) {
            stack.add("\t- " + entry);
        }

        /**
         * Returns a string representation of this object
         * 返回此对象的字符串表示形式
         */
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            if (!stack.isEmpty()) {
                for(int i = stack.size(); i > 0; i-- ) {
                    buffer.append(stack.get(i-1) + ((i != 1) ? "\n" : ""));
                }
            }
            return buffer.toString();
        }
    }

}
