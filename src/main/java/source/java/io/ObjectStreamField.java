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

import java.lang.reflect.Field;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

/**
 * A description of a Serializable field from a Serializable class.  An array
 * of ObjectStreamFields is used to declare the Serializable fields of a class.
 * 1.Serializable 类中的 Serializable 字段的描述。 ObjectStreamFields 数组用于声明类的 Serializable 字段。
 * @author      Mike Warres
 * @author      Roger Riggs
 * @see ObjectStreamClass
 * @since 1.2
 */
public class ObjectStreamField
    implements Comparable<Object>
{

    /** field name */
    //字段名称
    private final String name;
    /** canonical JVM signature of field type */
    //字段类型的规范 JVM 签名
    private final String signature;
    /** field type (Object.class if unknown non-primitive type) */
    //字段类型（Object.class 如果未知的非原始类型）
    private final Class<?> type;
    /** whether or not to (de)serialize field values as unshared */
    //是否将字段值（反）序列化为非共享
    private final boolean unshared;
    /** corresponding reflective field object, if any */
    //对应的反射场对象，如果有的话
    private final Field field;
    /** offset of field value in enclosing field group */
    //封闭字段组中字段值的偏移量
    private int offset = 0;

    /**
     * Create a Serializable field with the specified type.  This field should
     * be documented with a <code>serialField</code> tag.
     * 创建具有指定类型的 Serializable 字段。 这个字段应该用一个serialField标签来记录
     * @param   name the name of the serializable field
     * @param   type the <code>Class</code> object of the serializable field
     */
    public ObjectStreamField(String name, Class<?> type) {
        this(name, type, false);
    }

    /**
     * Creates an ObjectStreamField representing a serializable field with the
     * given name and type.  If unshared is false, values of the represented
     * field are serialized and deserialized in the default manner--if the
     * field is non-primitive, object values are serialized and deserialized as
     * if they had been written and read by calls to writeObject and
     * readObject.  If unshared is true, values of the represented field are
     * serialized and deserialized as if they had been written and read by
     * calls to writeUnshared and readUnshared.
     * 创建一个 ObjectStreamField 表示具有给定名称和类型的可序列化字段。
     * 如果 unshared 为 false，则表示字段的值以默认方式进行序列化和反序列化——如果该字段是非原始的，
     * 则对象值将被序列化和反序列化，就好像它们是通过调用 writeObject 和 readObject 写入和读取的一样。
     * 如果 unshared 为真，则表示字段的值将被序列化和反序列化，
     * 就好像它们已通过调用 writeUnshared 和 readUnshared 写入和读取一样
     * @param   name field name
     * @param   type field type
     * @param   unshared if false, write/read field values in the same manner
     *          as writeObject/readObject; if true, write/read in the same
     *          manner as writeUnshared/readUnshared
     * @since   1.4
     */
    public ObjectStreamField(String name, Class<?> type, boolean unshared) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.type = type;
        this.unshared = unshared;
        signature = getClassSignature(type).intern();
        field = null;
    }

    /**
     * Creates an ObjectStreamField representing a field with the given name,
     * signature and unshared setting.
     * 创建一个 ObjectStreamField 表示具有给定名称、签名和非共享设置的字段
     */
    ObjectStreamField(String name, String signature, boolean unshared) {
        if (name == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.signature = signature.intern();
        this.unshared = unshared;
        field = null;

        switch (signature.charAt(0)) {
            case 'Z': type = Boolean.TYPE; break;
            case 'B': type = Byte.TYPE; break;
            case 'C': type = Character.TYPE; break;
            case 'S': type = Short.TYPE; break;
            case 'I': type = Integer.TYPE; break;
            case 'J': type = Long.TYPE; break;
            case 'F': type = Float.TYPE; break;
            case 'D': type = Double.TYPE; break;
            case 'L':
            case '[': type = Object.class; break;
            default: throw new IllegalArgumentException("illegal signature");
        }
    }

    /**
     * Creates an ObjectStreamField representing the given field with the
     * specified unshared setting.  For compatibility with the behavior of
     * earlier serialization implementations, a "showType" parameter is
     * necessary to govern whether or not a getType() call on this
     * ObjectStreamField (if non-primitive) will return Object.class (as
     * opposed to a more specific reference type).
     * 创建一个 ObjectStreamField 表示具有指定非共享设置的给定字段。
     * 为了与早期序列化实现的行为兼容，需要一个“showType”参数来控制对此 ObjectStreamField
     * （如果非原始）的 getType() 调用是否将返回 Object.class（与更具体的引用类型相反） ）
     */
    ObjectStreamField(Field field, boolean unshared, boolean showType) {
        this.field = field;
        this.unshared = unshared;
        name = field.getName();
        Class<?> ftype = field.getType();
        type = (showType || ftype.isPrimitive()) ? ftype : Object.class;
        signature = getClassSignature(ftype).intern();
    }

    /**
     * Get the name of this field.
     * 获取该字段的名称
     * @return  a <code>String</code> representing the name of the serializable
     *          field
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of the field.  If the type is non-primitive and this
     * <code>ObjectStreamField</code> was obtained from a deserialized {@link
     * ObjectStreamClass} instance, then <code>Object.class</code> is returned.
     * Otherwise, the <code>Class</code> object for the type of the field is
     * returned.
     * 获取字段的类型。
     * 如果类型是非原始类型，并且此ObjectStreamField是从反序列化的
     * ObjectStreamClass实例中获得的，则返回Object.class 。 否则，返回该字段类型的Class对象
     * @return  a <code>Class</code> object representing the type of the
     *          serializable field
     */
    @CallerSensitive
    public Class<?> getType() {
        if (System.getSecurityManager() != null) {
            Class<?> caller = Reflection.getCallerClass();
            if (ReflectUtil.needsPackageAccessCheck(caller.getClassLoader(), type.getClassLoader())) {
                ReflectUtil.checkPackageAccess(type);
            }
        }
        return type;
    }

    /**
     * Returns character encoding of field type.  The encoding is as follows:
     * <blockquote><pre>
     * B            byte
     * C            char
     * D            double
     * F            float
     * I            int
     * J            long
     * L            class or interface
     * S            short
     * Z            boolean
     * [            array
     * </pre></blockquote>
     * 返回字段类型的字符编码。 编码如下：
     *        B            byte
     *        C            char
     *        D            double
     *        F            float
     *        I            int
     *        J            long
     *        L            class or interface
     *        S            short
     *        Z            boolean
     *        [            array
     *
     * @return  the typecode of the serializable field
     */
    // REMIND: deprecate?
    public char getTypeCode() {
        return signature.charAt(0);
    }

    /**
     * Return the JVM type signature.
     * 返回 JVM 类型签名
     * @return  null if this field has a primitive type.
     */
    // REMIND: deprecate?
    public String getTypeString() {
        return isPrimitive() ? null : signature;
    }

    /**
     * Offset of field within instance data.
     * 实例数据中字段的偏移量
     * @return  the offset of this field
     * @see #setOffset
     */
    // REMIND: deprecate?
    public int getOffset() {
        return offset;
    }

    /**
     * Offset within instance data.
     * 实例数据中的偏移量
     * @param   offset the offset of the field
     * @see #getOffset
     */
    // REMIND: deprecate?
    protected void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Return true if this field has a primitive type.
     * 如果此字段具有原始类型，则返回 true
     * @return  true if and only if this field corresponds to a primitive type
     */
    // REMIND: deprecate?
    public boolean isPrimitive() {
        char tcode = signature.charAt(0);
        return ((tcode != 'L') && (tcode != '['));
    }

    /**
     * Returns boolean value indicating whether or not the serializable field
     * represented by this ObjectStreamField instance is unshared.
     * 返回布尔值，指示由此 ObjectStreamField 实例表示的可序列化字段是否未共享
     * @return {@code true} if this field is unshared
     *
     * @since 1.4
     */
    public boolean isUnshared() {
        return unshared;
    }

    /**
     * Compare this field with another <code>ObjectStreamField</code>.  Return
     * -1 if this is smaller, 0 if equal, 1 if greater.  Types that are
     * primitives are "smaller" than object types.  If equal, the field names
     * are compared.
     * 将此字段与另一个ObjectStreamField进行比较。
     * 如果较小则返回 -1，如果相等则返回 0，如果较大则返回 1。
     * 原始类型比对象类型“小”。 如果相等，则比较字段名称
     */
    // REMIND: deprecate?
    public int compareTo(Object obj) {
        ObjectStreamField other = (ObjectStreamField) obj;
        boolean isPrim = isPrimitive();
        if (isPrim != other.isPrimitive()) {
            return isPrim ? -1 : 1;
        }
        return name.compareTo(other.name);
    }

    /**
     * Return a string that describes this field.
     * 返回描述此字段的字符串
     */
    public String toString() {
        return signature + ' ' + name;
    }

    /**
     * Returns field represented by this ObjectStreamField, or null if
     * ObjectStreamField is not associated with an actual field.
     * 返回由此 ObjectStreamField 表示的字段，如果 ObjectStreamField 未与实际字段关联，则返回 null
     */
    Field getField() {
        return field;
    }

    /**
     * Returns JVM type signature of field (similar to getTypeString, except
     * that signature strings are returned for primitive fields as well).
     * 返回字段的JVM 类型签名（类似于getTypeString，除了原始字段也返回签名字符串）
     */
    String getSignature() {
        return signature;
    }

    /**
     * Returns JVM type signature for given class.
     * 返回给定类的 JVM 类型签名
     */
    private static String getClassSignature(Class<?> cl) {
        StringBuilder sbuf = new StringBuilder();
        while (cl.isArray()) {
            sbuf.append('[');
            cl = cl.getComponentType();
        }
        if (cl.isPrimitive()) {
            if (cl == Integer.TYPE) {
                sbuf.append('I');
            } else if (cl == Byte.TYPE) {
                sbuf.append('B');
            } else if (cl == Long.TYPE) {
                sbuf.append('J');
            } else if (cl == Float.TYPE) {
                sbuf.append('F');
            } else if (cl == Double.TYPE) {
                sbuf.append('D');
            } else if (cl == Short.TYPE) {
                sbuf.append('S');
            } else if (cl == Character.TYPE) {
                sbuf.append('C');
            } else if (cl == Boolean.TYPE) {
                sbuf.append('Z');
            } else if (cl == Void.TYPE) {
                sbuf.append('V');
            } else {
                throw new InternalError();
            }
        } else {
            sbuf.append('L' + cl.getName().replace('.', '/') + ';');
        }
        return sbuf.toString();
    }
}
