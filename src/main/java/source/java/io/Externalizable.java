/*
 * Copyright (c) 1996, 2004, Oracle and/or its affiliates. All rights reserved.
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

import java.io.ObjectOutput;
import java.io.ObjectInput;

/**
 * Only the identity of the class of an Externalizable instance is
 * written in the serialization stream and it is the responsibility
 * of the class to save and restore the contents of its instances.
 * 1.序列化流中仅写入 Externalizable 实例的类的标识，并且类负责保存和恢复其实例的内容
 * The writeExternal and readExternal methods of the Externalizable
 * interface are implemented by a class to give the class complete
 * control over the format and contents of the stream for an object
 * and its supertypes. These methods must explicitly
 * coordinate with the supertype to save its state. These methods supersede
 * customized implementations of writeObject and readObject methods.<br>
 * 2.Externalizable 接口的 writeExternal 和 readExternal 方法由类实现，
 * 以使类完全控制对象及其超类型的流的格式和内容。这些方法必须显式地与超类型协调以保存其状态。
 * 这些方法取代了 writeObject 和 readObject 方法的自定义实现。
 * Object Serialization uses the Serializable and Externalizable
 * interfaces.  Object persistence mechanisms can use them as well.  Each
 * object to be stored is tested for the Externalizable interface. If
 * the object supports Externalizable, the writeExternal method is called. If the
 * object does not support Externalizable and does implement
 * Serializable, the object is saved using
 * ObjectOutputStream. <br> When an Externalizable object is
 * reconstructed, an instance is created using the public no-arg
 * constructor, then the readExternal method called.  Serializable
 * objects are restored by reading them from an ObjectInputStream.<br>
 * 3.对象序列化使用 Serializable 和 Externalizable 接口。对象持久性机制也可以使用它们。
 * 每个要存储的对象都针对 Externalizable 接口进行了测试。如果对象支持 Externalizable，
 * 则调用 writeExternal 方法。如果对象不支持 Externalizable 并且实现了 Serializable，
 * 则使用 ObjectOutputStream 保存该对象。  重构 Externalizable 对象时，
 * 会使用公共无参数构造函数创建一个实例，然后调用 readExternal 方法。
 * 可序列化的对象通过从 ObjectInputStream 读取它们来恢复
 * An Externalizable instance can designate a substitution object via
 * the writeReplace and readResolve methods documented in the Serializable
 * interface.<br>
 * 4.Externalizable 实例可以通过 Serializable 接口中记录的 writeReplace 和 readResolve 方法指定替换对象。
 * @author  unascribed
 * @see java.io.ObjectOutputStream
 * @see java.io.ObjectInputStream
 * @see java.io.ObjectOutput
 * @see java.io.ObjectInput
 * @see java.io.Serializable
 * @since   JDK1.1
 */
public interface Externalizable extends java.io.Serializable {
    /**
     * The object implements the writeExternal method to save its contents
     * by calling the methods of DataOutput for its primitive values or
     * calling the writeObject method of ObjectOutput for objects, strings,
     * and arrays.
     * 该对象实现了writeExternal 方法来保存其内容，方法是调用DataOutput 的方法为其原始值或对象、字符串和数组调用
     * ObjectOutput 的writeObject 方法。
     *
     * @serialData Overriding methods should use this tag to describe
     *             the data layout of this Externalizable object.
     *             List the sequence of element types and, if possible,
     *             relate the element to a public/protected field and/or
     *             method of this Externalizable class.
     *
     * @param out the stream to write the object to
     * @exception IOException Includes any I/O exceptions that may occur
     */
    void writeExternal(ObjectOutput out) throws IOException;

    /**
     * The object implements the readExternal method to restore its
     * contents by calling the methods of DataInput for primitive
     * types and readObject for objects, strings and arrays.  The
     * readExternal method must read the values in the same sequence
     * and with the same types as were written by writeExternal.
     * 该对象实现了 readExternal 方法以通过调用原始类型的 DataInput
     * 和对象、字符串和数组的 readObject 方法来恢复其内容。
     * readExternal 方法必须以与 writeExternal 写入的相同序列和相同类型读取值
     * @param in the stream to read data from in order to restore the object
     * @exception IOException if I/O errors occur
     * @exception ClassNotFoundException If the class for an object being
     *              restored cannot be found.
     */
    void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;
}
