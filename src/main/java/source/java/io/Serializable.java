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
 * Serializability of a class is enabled by the class implementing the
 * java.io.Serializable interface. Classes that do not implement this
 * interface will not have any of their state serialized or
 * deserialized.  All subtypes of a serializable class are themselves
 * serializable.  The serialization interface has no methods or fields
 * and serves only to identify the semantics of being serializable. <p>
 *
 * To allow subtypes of non-serializable classes to be serialized, the
 * subtype may assume responsibility for saving and restoring the
 * state of the supertype's public, protected, and (if accessible)
 * package fields.  The subtype may assume this responsibility only if
 * the class it extends has an accessible no-arg constructor to
 * initialize the class's state.  It is an error to declare a class
 * Serializable if this is not the case.  The error will be detected at
 * runtime. <p>
 *
 * During deserialization, the fields of non-serializable classes will
 * be initialized using the public or protected no-arg constructor of
 * the class.  A no-arg constructor must be accessible to the subclass
 * that is serializable.  The fields of serializable subclasses will
 * be restored from the stream. <p>
 *
 * When traversing a graph, an object may be encountered that does not
 * support the Serializable interface. In this case the
 * NotSerializableException will be thrown and will identify the class
 * of the non-serializable object. <p>
 *
 * Classes that require special handling during the serialization and
 * deserialization process must implement special methods with these exact
 * signatures:
 *
 * <PRE>
 * private void writeObject(java.io.ObjectOutputStream out)
 *     throws IOException
 * private void readObject(java.io.ObjectInputStream in)
 *     throws IOException, ClassNotFoundException;
 * private void readObjectNoData()
 *     throws ObjectStreamException;
 * </PRE>
 *
 * <p>The writeObject method is responsible for writing the state of the
 * object for its particular class so that the corresponding
 * readObject method can restore it.  The default mechanism for saving
 * the Object's fields can be invoked by calling
 * out.defaultWriteObject. The method does not need to concern
 * itself with the state belonging to its superclasses or subclasses.
 * State is saved by writing the individual fields to the
 * ObjectOutputStream using the writeObject method or by using the
 * methods for primitive data types supported by DataOutput.
 *
 * <p>The readObject method is responsible for reading from the stream and
 * restoring the classes fields. It may call in.defaultReadObject to invoke
 * the default mechanism for restoring the object's non-static and
 * non-transient fields.  The defaultReadObject method uses information in
 * the stream to assign the fields of the object saved in the stream with the
 * correspondingly named fields in the current object.  This handles the case
 * when the class has evolved to add new fields. The method does not need to
 * concern itself with the state belonging to its superclasses or subclasses.
 * State is saved by writing the individual fields to the
 * ObjectOutputStream using the writeObject method or by using the
 * methods for primitive data types supported by DataOutput.
 *
 * <p>The readObjectNoData method is responsible for initializing the state of
 * the object for its particular class in the event that the serialization
 * stream does not list the given class as a superclass of the object being
 * deserialized.  This may occur in cases where the receiving party uses a
 * different version of the deserialized instance's class than the sending
 * party, and the receiver's version extends classes that are not extended by
 * the sender's version.  This may also occur if the serialization stream has
 * been tampered; hence, readObjectNoData is useful for initializing
 * deserialized objects properly despite a "hostile" or incomplete source
 * stream.
 *
 * <p>Serializable classes that need to designate an alternative object to be
 * used when writing an object to the stream should implement this
 * special method with the exact signature:
 *
 * <PRE>
 * ANY-ACCESS-MODIFIER Object writeReplace() throws ObjectStreamException;
 * </PRE><p>
 *
 * This writeReplace method is invoked by serialization if the method
 * exists and it would be accessible from a method defined within the
 * class of the object being serialized. Thus, the method can have private,
 * protected and package-private access. Subclass access to this method
 * follows java accessibility rules. <p>
 *
 * Classes that need to designate a replacement when an instance of it
 * is read from the stream should implement this special method with the
 * exact signature.
 *
 * <PRE>
 * ANY-ACCESS-MODIFIER Object readResolve() throws ObjectStreamException;
 * </PRE><p>
 *
 * This readResolve method follows the same invocation rules and
 * accessibility rules as writeReplace.<p>
 *
 * The serialization runtime associates with each serializable class a version
 * number, called a serialVersionUID, which is used during deserialization to
 * verify that the sender and receiver of a serialized object have loaded
 * classes for that object that are compatible with respect to serialization.
 * If the receiver has loaded a class for the object that has a different
 * serialVersionUID than that of the corresponding sender's class, then
 * deserialization will result in an {@link InvalidClassException}.  A
 * serializable class can declare its own serialVersionUID explicitly by
 * declaring a field named <code>"serialVersionUID"</code> that must be static,
 * final, and of type <code>long</code>:
 *
 * <PRE>
 * ANY-ACCESS-MODIFIER static final long serialVersionUID = 42L;
 * </PRE>
 *
 * If a serializable class does not explicitly declare a serialVersionUID, then
 * the serialization runtime will calculate a default serialVersionUID value
 * for that class based on various aspects of the class, as described in the
 * Java(TM) Object Serialization Specification.  However, it is <em>strongly
 * recommended</em> that all serializable classes explicitly declare
 * serialVersionUID values, since the default serialVersionUID computation is
 * highly sensitive to class details that may vary depending on compiler
 * implementations, and can thus result in unexpected
 * <code>InvalidClassException</code>s during deserialization.  Therefore, to
 * guarantee a consistent serialVersionUID value across different java compiler
 * implementations, a serializable class must declare an explicit
 * serialVersionUID value.  It is also strongly advised that explicit
 * serialVersionUID declarations use the <code>private</code> modifier where
 * possible, since such declarations apply only to the immediately declaring
 * class--serialVersionUID fields are not useful as inherited members. Array
 * classes cannot declare an explicit serialVersionUID, so they always have
 * the default computed value, but the requirement for matching
 * serialVersionUID values is waived for array classes.
 * 1.类的可序列化由实现 java.io.Serializable 接口的类启用。
 * 2.未实现此接口的类将不会对其任何状态进行序列化或反序列化。
 * 3.可序列化类的所有子类型本身都是可序列化的。 序列化接口没有方法或字段，仅用于标识可序列化的语义。
 * 4.为了允许不可序列化的类的子类型被序列化，子类型可能负责保存和恢复超类型的公共、受保护和（如果可访问）包字段的状态。
 * 仅当它扩展的类具有可访问的无参数构造函数来初始化类的状态时，子类型才可能承担此责任。
 * 如果不是这种情况，则声明类 Serializable 是错误的。 该错误将在运行时被检测到。
 * 5.在反序列化期间，不可序列化类的字段将使用类的公共或受保护的无参数构造函数进行初始化。
 * 可序列化的子类必须可以访问无参数构造函数。 可序列化子类的字段将从流中恢复。
 * 6.遍历图时，可能会遇到不支持Serializable接口的对象。
 * 在这种情况下，将抛出 NotSerializableException 并标识不可序列化对象的类。
 * 7.在序列化和反序列化过程中需要特殊处理的类必须实现具有以下确切签名的特殊方法：
 *    private void writeObject(java.io.ObjectOutputStream out)
 *        throws IOException
 *    private void readObject(java.io.ObjectInputStream in)
 *        throws IOException, ClassNotFoundException;
 *    private void readObjectNoData()
 *        throws ObjectStreamException;
 *
 * 8.writeObject 方法负责为其特定类写入对象的状态，以便相应的 readObject 方法可以恢复它。
 * 保存对象字段的默认机制可以通过调用 out.defaultWriteObject 来调用。
 * 该方法不需要关心属于它的超类或子类的状态。
 * 通过使用 writeObject 方法或使用 DataOutput 支持的原始数据类型的方法将各个字段写入 ObjectOutputStream 来保存状态。
 * 9.readObject 方法负责从流中读取并恢复类字段。
 * 它可以调用 in.defaultReadObject 来调用默认机制来恢复对象的非静态和非瞬态字段。
 * defaultReadObject 方法使用流中的信息将保存在流中的对象的字段分配给当前对象中相应命名的字段。
 * 这可以处理类已经演变为添加新字段的情况。 该方法不需要关心属于它的超类或子类的状态。
 * 通过使用 writeObject 方法或使用 DataOutput 支持的原始数据类型的方法将各个字段写入 ObjectOutputStream 来保存状态。
 * 10.readObjectNoData 方法负责在序列化流未将给定类列为被反序列化对象的超类的情况下为其特定类初始化对象的状态。
 * 在接收方使用与发送方不同版本的反序列化实例类的情况下，可能会发生这种情况，并且接收方的版本扩展了发送方版本未扩展的类。
 * 如果序列化流被篡改，也可能发生这种情况； 因此，尽管存在“敌对”或不完整的源流，
 * 但 readObjectNoData 可用于正确初始化反序列化对象。
 * 在将对象写入流时需要指定要使用的替代对象的可序列化类应使用确切的签名实现此特殊方法：
 *    ANY-ACCESS-MODIFIER Object writeReplace() throws ObjectStreamException;
 *
 *11. 如果该方法存在并且可以从被序列化的对象的类中定义的方法访问，则该 writeReplace 方法由序列化调用。
 * 因此，该方法可以具有私有的、受保护的和包私有的访问。 对该方法的子类访问遵循 java 可访问性规则。
 *12.当从流中读取它的实例时需要指定替换的类应该使用确切的签名实现这个特殊的方法。
 *    ANY-ACCESS-MODIFIER Object readResolve() throws ObjectStreamException;
 *
 * 13.此 readResolve 方法遵循与 writeReplace 相同的调用规则和可访问性规则。
 * 序列化运行时将每个可序列化类与一个称为 serialVersionUID 的版本号相关联，
 * 在反序列化期间使用该版本号来验证序列化对象的发送方和接收方是否已为该对象加载了与序列化兼容的类。
 * 如果接收方为对象加载了一个类，该类的 serialVersionUID 与相应发送方的类不同，
 * 则反序列化将导致InvalidClassException 。
 * 一个可序列化的类可以通过声明一个名为"serialVersionUID"的字段来显式声明它自己的serialVersionUID，
 * 该字段必须是静态的、最终的并且类型为long ：
 *    ANY-ACCESS-MODIFIER static final long serialVersionUID = 42L;
 *
 * 14.如果可序列化类未显式声明 serialVersionUID，
 * 则序列化运行时将根据该类的各个方面计算该类的默认 serialVersionUID 值，如 Java(TM) 对象序列化规范中所述。
 * 但是，强烈建议所有可序列化的类显式声明 serialVersionUID 值，
 * 因为默认的 serialVersionUID 计算对可能因编译器实现而异的类详细信息高度敏感，
 * 因此可能会在反序列化期间导致意外的InvalidClassException 。
 * 因此，为了保证在不同的 Java 编译器实现中具有一致的 serialVersionUID 值，
 * 可序列化类必须声明一个显式的 serialVersionUID 值。
 * 还强烈建议显式 serialVersionUID 声明尽可能使用private修饰符，
 * 因为此类声明仅适用于立即声明的类——serialVersionUID 字段作为继承成员没有用。
 * 数组类无法声明显式的 serialVersionUID，因此它们始终具有默认的计算值，
 * 但数组类放弃了匹配 serialVersionUID 值的要求
 * @author  unascribed
 * @see java.io.ObjectOutputStream
 * @see java.io.ObjectInputStream
 * @see java.io.ObjectOutput
 * @see java.io.ObjectInput
 * @see java.io.Externalizable
 * @since   JDK1.1
 */
public interface Serializable {
}
