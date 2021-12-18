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

package java.lang.reflect;

/**
 * The {@code Array} class provides static methods to dynamically create and
 * access Java arrays.
 *  1.Array类提供静态方法来动态创建和访问 Java数组
 *
 * <p>{@code Array} permits widening conversions to occur during a get or set
 * operation, but throws an {@code IllegalArgumentException} if a narrowing
 * conversion would occur.
 * 2.Array允许在 get 或 set 操作期间发生扩大转换，但如果发生缩小转换，
 * 则会抛出 {@code IllegalArgumentException}。
 * @author Nakul Saraiya
 */
public final
class Array {

    /**
     * Constructor.  Class Array is not instantiable.
     * 构造函数。类 Array 不可实例化。
     */
    private Array() {}

    /**
     * Creates a new array with the specified component type and
     * length.
     * 1.创建具有指定组件类型和长度的新数组
     * Invoking this method is equivalent to creating an array
     * as follows:
     * <blockquote>
     * <pre>
     * int[] x = {length};
     * Array.newInstance(componentType, x);
     * </pre>
     * </blockquote>
     * 2.调用这个方法相当于创建一个数组如下：
     * int[] x = {length};
     * Array.newInstance(componentType, x);
     * <p>The number of dimensions of the new array must not
     * exceed 255.
     *3.新数组的维数不得超过 255。
     * @param componentType the {@code Class} object representing the
     * component type of the new array
     * @param length the length of the new array
     * @return the new array
     * @exception NullPointerException if the specified
     * {@code componentType} parameter is null
     * @exception IllegalArgumentException if componentType is {@link
     * Void#TYPE} or if the number of dimensions of the requested array
     * instance exceed 255.
     * @exception NegativeArraySizeException if the specified {@code length}
     * is negative
     */
    public static Object newInstance(Class<?> componentType, int length)
        throws NegativeArraySizeException {
        return newArray(componentType, length);
    }

    /**
     * Creates a new array
     * with the specified component type and dimensions.
     * If {@code componentType}
     * represents a non-array class or interface, the new array
     * has {@code dimensions.length} dimensions and
     * {@code componentType} as its component type. If
     * {@code componentType} represents an array class, the
     * number of dimensions of the new array is equal to the sum
     * of {@code dimensions.length} and the number of
     * dimensions of {@code componentType}. In this case, the
     * component type of the new array is the component type of
     * {@code componentType}.
     * 1.创建具有指定组件类型和尺寸的新数组。如果componentType表示非数组类或接口，
     * 则新数组的组件类型为dimensions.length维度和componentType。如果componentType代表一个数组类，
     * 则新数组的维数等于dimension.length与componentType的维数之和。在这种情况下，
     * 新数组的组件类型为componentType的组件类型。
     * <p>The number of dimensions of the new array must not
     * exceed 255.
     * 2.新数组的维数不得超过 255。
     * @param componentType the {@code Class} object representing the component
     * type of the new array
     * @param dimensions an array of {@code int} representing the dimensions of
     * the new array
     * @return the new array
     * @exception NullPointerException if the specified
     * {@code componentType} argument is null
     * @exception IllegalArgumentException if the specified {@code dimensions}
     * argument is a zero-dimensional array, if componentType is {@link
     * Void#TYPE}, or if the number of dimensions of the requested array
     * instance exceed 255.
     * @exception NegativeArraySizeException if any of the components in
     * the specified {@code dimensions} argument is negative.
     */
    public static Object newInstance(Class<?> componentType, int... dimensions)
        throws IllegalArgumentException, NegativeArraySizeException {
        return multiNewArray(componentType, dimensions);
    }

    /**
     * Returns the length of the specified array object, as an {@code int}.
     * 以 int形式返回指定数组对象的长度。
     * @param array the array
     * @return the length of the array
     * @exception IllegalArgumentException if the object argument is not
     * an array
     */
    public static native int getLength(Object array)
        throws IllegalArgumentException;

    /**
     * Returns the value of the indexed component in the specified
     * array object.  The value is automatically wrapped in an object
     * if it has a primitive type.
     * 返回指定数组对象中索引组件的值。如果该值具有原始类型，则该值会自动包装在一个对象中
     * @param array the array
     * @param index the index
     * @return the (possibly wrapped) value of the indexed component in
     * the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     */
    public static native Object get(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code boolean}.
     * 返回指定数组对象中索引组件的值，作为boolean。
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native boolean getBoolean(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code byte}.
     * 返回指定数组对象中索引组件的值，作为byte
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native byte getByte(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code char}.
     * 返回指定数组对象中索引组件的值，作为char。
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native char getChar(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code short}.
     * 返回指定数组对象中索引组件的值，作为 short
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native short getShort(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as an {@code int}.
     * 返回指定数组对象中索引组件的值，作为int。
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native int getInt(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code long}.
     * 返回指定数组对象中索引组件的值，作为 long。
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native long getLong(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code float}.
     * 返回指定数组对象中索引组件的值，作为float
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native float getFloat(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the value of the indexed component in the specified
     * array object, as a {@code double}.
     * 返回指定数组对象中索引组件的值，作为double。
     * @param array the array
     * @param index the index
     * @return the value of the indexed component in the specified array
     * @exception NullPointerException If the specified object is null
     * @exception IllegalArgumentException If the specified object is not
     * an array, or if the indexed element cannot be converted to the
     * return type by an identity or widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to the
     * length of the specified array
     * @see Array#get
     */
    public static native double getDouble(Object array, int index)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified new value.  The new value is first
     * automatically unwrapped if the array has a primitive component
     * type.
     * 将指定数组对象的索引组件的值设置为指定的新值。如果数组具有原始组件类型，则首先自动解包新值
     * @param array the array
     * @param index the index into the array
     * @param value the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the array component type is primitive and
     * an unwrapping conversion fails
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     */
    public static native void set(Object array, int index, Object value)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code boolean} value.
     * 将指定数组对象的索引组件的值设置为指定的boolean值。
     * @param array the array
     * @param index the index into the array
     * @param z the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setBoolean(Object array, int index, boolean z)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code byte} value.
     * 将指定数组对象的索引组件的值设置为指定的byte值
     * @param array the array
     * @param index the index into the array
     * @param b the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setByte(Object array, int index, byte b)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code char} value.
     * 将指定数组对象的索引组件的值设置为指定的char值
     * @param array the array
     * @param index the index into the array
     * @param c the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setChar(Object array, int index, char c)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code short} value.
     * 将指定数组对象的索引组件的值设置为指定的short值
     * @param array the array
     * @param index the index into the array
     * @param s the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setShort(Object array, int index, short s)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code int} value.
     * 将指定数组对象的索引组件的值设置为指定的 int 值
     * @param array the array
     * @param index the index into the array
     * @param i the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setInt(Object array, int index, int i)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code long} value.
     * 将指定数组对象的索引组件的值设置为指定的 long值
     * @param array the array
     * @param index the index into the array
     * @param l the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setLong(Object array, int index, long l)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code float} value.
     * 将指定数组对象的索引组件的值设置为指定的 float值。
     * @param array the array
     * @param index the index into the array
     * @param f the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setFloat(Object array, int index, float f)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /**
     * Sets the value of the indexed component of the specified array
     * object to the specified {@code double} value.
     * 将指定数组对象的索引组件的值设置为指定的 double值
     * @param array the array
     * @param index the index into the array
     * @param d the new value of the indexed component
     * @exception NullPointerException If the specified object argument
     * is null
     * @exception IllegalArgumentException If the specified object argument
     * is not an array, or if the specified value cannot be converted
     * to the underlying array's component type by an identity or a
     * primitive widening conversion
     * @exception ArrayIndexOutOfBoundsException If the specified {@code index}
     * argument is negative, or if it is greater than or equal to
     * the length of the specified array
     * @see Array#set
     */
    public static native void setDouble(Object array, int index, double d)
        throws IllegalArgumentException, ArrayIndexOutOfBoundsException;

    /*
     * Private
     */

    private static native Object newArray(Class<?> componentType, int length)
        throws NegativeArraySizeException;

    private static native Object multiNewArray(Class<?> componentType,
        int[] dimensions)
        throws IllegalArgumentException, NegativeArraySizeException;


}
