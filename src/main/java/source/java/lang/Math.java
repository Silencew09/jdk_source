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

package java.lang;
import java.util.Random;

import sun.misc.FloatConsts;
import sun.misc.DoubleConsts;

/**
 * The class {@code Math} contains methods for performing basic
 * numeric operations such as the elementary exponential, logarithm,
 * square root, and trigonometric functions.
 *1. Math类包含用于执行基本数值运算的方法，例如基本指数、对数、平方根和三角函数。
 * <p>Unlike some of the numeric methods of class
 * {@code StrictMath}, all implementations of the equivalent
 * functions of class {@code Math} are not defined to return the
 * bit-for-bit same results.  This relaxation permits
 * better-performing implementations where strict reproducibility is
 * not required.
 * 2.与类 StrictMath的一些数值方法不同，类 Math的等效函数的所有实现都没有定义为返回逐位相同的结果
 * 这种放松允许在不需要严格再现性的情况下实现更好的实现。
 *
 * <p>By default many of the {@code Math} methods simply call
 * the equivalent method in {@code StrictMath} for their
 * implementation.  Code generators are encouraged to use
 * platform-specific native libraries or microprocessor instructions,
 * where available, to provide higher-performance implementations of
 * {@code Math} methods.  Such higher-performance
 * implementations still must conform to the specification for
 * {@code Math}.
 * 3.默认情况下，许多Math方法只是调用StrictMath中的等效方法来实现它们
 * 4.鼓励代码生成器使用特定于平台的本机库或微处理器指令（如果可用）来提供更高性能的Math方法实现。
 * 5.这种更高性能的实现仍然必须符合Math的规范。
 * <p>The quality of implementation specifications concern two
 * properties, accuracy of the returned result and monotonicity of the
 * method.  Accuracy of the floating-point {@code Math} methods is
 * measured in terms of <i>ulps</i>, units in the last place.  For a
 * given floating-point format, an {@linkplain #ulp(double) ulp} of a
 * specific real number value is the distance between the two
 * floating-point values bracketing that numerical value.  When
 * discussing the accuracy of a method as a whole rather than at a
 * specific argument, the number of ulps cited is for the worst-case
 * error at any argument.  If a method always has an error less than
 * 0.5 ulps, the method always returns the floating-point number
 * nearest the exact result; such a method is <i>correctly
 * rounded</i>.  A correctly rounded method is generally the best a
 * floating-point approximation can be; however, it is impractical for
 * many floating-point methods to be correctly rounded.  Instead, for
 * the {@code Math} class, a larger error bound of 1 or 2 ulps is
 * allowed for certain methods.  Informally, with a 1 ulp error bound,
 * when the exact result is a representable number, the exact result
 * should be returned as the computed result; otherwise, either of the
 * two floating-point values which bracket the exact result may be
 * returned.  For exact results large in magnitude, one of the
 * endpoints of the bracket may be infinite.  Besides accuracy at
 * individual arguments, maintaining proper relations between the
 * method at different arguments is also important.  Therefore, most
 * methods with more than 0.5 ulp errors are required to be
 * <i>semi-monotonic</i>: whenever the mathematical function is
 * non-decreasing, so is the floating-point approximation, likewise,
 * whenever the mathematical function is non-increasing, so is the
 * floating-point approximation.  Not all approximations that have 1
 * ulp accuracy will automatically meet the monotonicity requirements.
 * 6.实现规范的质量涉及两个属性，返回结果的准确性和方法的单调性。
 * 浮点 Math方法的准确性是根据 ulps来衡量的，单位在最后。
 * 对于给定的浮点格式，特定实数值的 plain ulp(double) 是包含该数值的两个浮点值之间的距离。
 * 当从整体上而不是在特定参数上讨论方法的准确性时，引用的 ulps 数量是针对任何参数的最坏情况错误。
 * 如果一个方法的错误总是小于 0.5 ulps，则该方法总是返回最接近精确结果的浮点数；
 * 这样的方法正确四舍五入。正确舍入的方法通常是浮点近似的最佳方法；
 * 但是，要正确舍入许多浮点方法是不切实际的。相反，对于 Math类，某些方法允许 1 或 2 ulps 的更大错误界限。
 * 非正式地，在 1 ulp 误差范围内，当精确结果是一个可表示的数字时，精确结果应该作为计算结果返回；
 * 否则，可能会返回包含确切结果的两个浮点值中的任何一个。对于幅度很大的精确结果，括号的端点之一可能是无限的。
 * 除了单个参数的准确性之外，在不同参数的方法之间保持适当的关系也很重要。
 * 因此，大多数误差超过 0.5 ulp 的方法都需要半单调：只要数学函数不减，浮点近似也是如此，
 * 同样，只要数学函数不减增加，浮点近似值也增加。并非所有具有 1 ulp 精度的近似值都会自动满足单调性要求。
 * <p>
 * The platform uses signed two's complement integer arithmetic with
 * int and long primitive types.  The developer should choose
 * the primitive type to ensure that arithmetic operations consistently
 * produce correct results, which in some cases means the operations
 * will not overflow the range of values of the computation.
 * The best practice is to choose the primitive type and algorithm to avoid
 * overflow. In cases where the size is {@code int} or {@code long} and
 * overflow errors need to be detected, the methods {@code addExact},
 * {@code subtractExact}, {@code multiplyExact}, and {@code toIntExact}
 * throw an {@code ArithmeticException} when the results overflow.
 * For other arithmetic operations such as divide, absolute value,
 * increment, decrement, and negation overflow occurs only with
 * a specific minimum or maximum value and should be checked against
 * the minimum or maximum as appropriate.
 * 7.该平台使用带有 int 和 long 原始类型的有符号二进制补码整数算法。
 * 开发人员应选择原始类型以确保算术运算始终产生正确的结果，这在某些情况下意味着运算不会溢出计算值的范围。
 * 最佳实践是选择原始类型和算法以避免溢出。在大小为int或 long且需要检测溢出错误的情况下，
 * 方法addExact、subtractExact、multiplyExact和toIntExact
 * 当结果溢出时抛出一个 ArithmeticException。对于其他算术运算，
 * 例如除法、绝对值、递增、递减和否定溢出仅发生在特定的最小值或最大值时，应根据最小值或最大值进行检查。
 * @author  unascribed
 * @author  Joseph D. Darcy
 * @since   JDK1.0
 */

public final class Math {

    /**
     * Don't let anyone instantiate this class.
     * 不要让任何人实例化这个类。
     */
    private Math() {}

    /**
     * The {@code double} value that is closer than any other to
     * <i>e</i>, the base of the natural logarithms.
     *  一个比任何值都接近e的一个double值（自然对数的底）
     */
    public static final double E = 2.7182818284590452354;

    /**
     * The {@code double} value that is closer than any other to
     * <i>pi</i>, the ratio of the circumference of a circle to its
     * diameter.
     * {@code double} 值比任何其他值都更接近 pi，即圆的周长与其直径的比率。
     */
    public static final double PI = 3.14159265358979323846;

    /**
     * Returns the trigonometric sine of an angle.  Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the
     * result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     * 1.返回角度的三角正弦值。特殊情况：如果参数为 NaN 或无穷大，则结果为 NaN
     * 2. 如果参数为零，则结果为与参数相同符号的零
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的。
     * @param   a   an angle, in radians.
     * @return  the sine of the argument.
     */
    public static double sin(double a) {
        return StrictMath.sin(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the trigonometric cosine of an angle. Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the
     * result is NaN.</ul>
     * 1.返回角度的三角余弦。特殊情况：如果参数为 NaN 或无穷大，则结果为 NaN。
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 2.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的。
     * @param   a   an angle, in radians.
     * @return  the cosine of the argument.
     */
    public static double cos(double a) {
        return StrictMath.cos(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the trigonometric tangent of an angle.  Special cases:
     * <ul><li>If the argument is NaN or an infinity, then the result
     * is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     * 1.返回角度的三角正切值。特殊情况：如果参数为 NaN 或无穷大，则结果为 NaN。
     * 2.如果参数为零，则结果为与参数相同符号的零
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的
     * @param   a   an angle, in radians.
     * @return  the tangent of the argument.
     */
    public static double tan(double a) {
        return StrictMath.tan(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the arc sine of a value; the returned angle is in the
     * range -<i>pi</i>/2 through <i>pi</i>/2.  Special cases:
     * <ul><li>If the argument is NaN or its absolute value is greater
     * than 1, then the result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     * 1.返回一个值的反正弦；返回的角度在 -pi/2 到 pi/2 的范围内
     * 2.特殊情况：<ul><li>如果参数为NaN或其绝对值大于1，则结果为NaN
     * 如果参数为零，则结果为与参数相同符号的零
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的
     * @param   a   the value whose arc sine is to be returned.
     * @return  the arc sine of the argument.
     */
    public static double asin(double a) {
        return StrictMath.asin(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the arc cosine of a value; the returned angle is in the
     * range 0.0 through <i>pi</i>.  Special case:
     * <ul><li>If the argument is NaN or its absolute value is greater
     * than 1, then the result is NaN.</ul>
     * 1.返回一个值的反余弦值；返回的角度在 0.0 到 pi的范围内
     * 2.特殊情况：如果参数为NaN或其绝对值大于1，则结果为NaN
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的
     * @param   a   the value whose arc cosine is to be returned.
     * @return  the arc cosine of the argument.
     */
    public static double acos(double a) {
        return StrictMath.acos(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the arc tangent of a value; the returned angle is in the
     * range -<i>pi</i>/2 through <i>pi</i>/2.  Special cases:
     * <ul><li>If the argument is NaN, then the result is NaN.
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.</ul>
     * 1.返回值的反正切；返回的角度在 -pi/2 到 pi/2 的范围内。
     * 2.特殊情况：如果参数为 NaN，则结果为 NaN。 如果参数为零，则结果为与参数相同符号的零
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的。
     * @param   a   the value whose arc tangent is to be returned.
     * @return  the arc tangent of the argument.
     */
    public static double atan(double a) {
        return StrictMath.atan(a); // default impl. delegates to StrictMath
    }

    /**
     * Converts an angle measured in degrees to an approximately
     * equivalent angle measured in radians.  The conversion from
     * degrees to radians is generally inexact.
     * 1.将以度为单位的角度转换为以弧度为单位的近似等效的角度。从度数到弧度的转换通常是不准确的。
     * @param   angdeg   an angle, in degrees
     * @return  the measurement of the angle {@code angdeg}
     *          in radians.
     * @since   1.2
     */
    public static double toRadians(double angdeg) {
        return angdeg / 180.0 * PI;
    }

    /**
     * Converts an angle measured in radians to an approximately
     * equivalent angle measured in degrees.  The conversion from
     * radians to degrees is generally inexact; users should
     * <i>not</i> expect {@code cos(toRadians(90.0))} to exactly
     * equal {@code 0.0}.
     *将以弧度为单位的角度转换为以度为单位的近似等效的角度。
     * 从弧度到度的转换通常是不准确的；用户应该不期望 cos(toRadians(90.0))完全等于 0.0。
     * @param   angrad   an angle, in radians
     * @return  the measurement of the angle {@code angrad}
     *          in degrees.
     * @since   1.2
     */
    public static double toDegrees(double angrad) {
        return angrad * 180.0 / PI;
    }

    /**
     * Returns Euler's number <i>e</i> raised to the power of a
     * {@code double} value.  Special cases:
     * <ul><li>If the argument is NaN, the result is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is negative infinity, then the result is
     * positive zero.</ul>
     * 1.返回欧拉数 e的 double次幂
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为正无穷大，则结果为正无穷大。
     * 如果参数为负无穷大，则结果为正零。
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的
     * @param   a   the exponent to raise <i>e</i> to.
     * @return  the value <i>e</i><sup>{@code a}</sup>,
     *          where <i>e</i> is the base of the natural logarithms.
     */
    public static double exp(double a) {
        return StrictMath.exp(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the natural logarithm (base <i>e</i>) of a {@code double}
     * value.  Special cases:
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is negative infinity.</ul>
     * 1.返回double值的自然对数（以 e 为底）
     * 2.特殊情况：
     * 如果参数为 NaN 或小于零，则结果为 NaN。
     * 如果参数为正无穷大，则结果为正无穷大。
     * 如果参数为正零或负零，则结果为负无穷大
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的。
     * @param   a   a value
     * @return  the value ln&nbsp;{@code a}, the natural logarithm of
     *          {@code a}.
     */
    public static double log(double a) {
        return StrictMath.log(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the base 10 logarithm of a {@code double} value.
     * Special cases:
     * 1.返回 double值的以 10 为底的对数。
     * 2.特殊情况：
     * 如果参数为 NaN 或小于零，则结果为 NaN。
     * 如果参数为正无穷大，则结果为正无穷大。
     * 如果参数为正零或负零，则结果为负无穷大。
     * 如果整数n的参数等于 10<sup>n<sup>，则结果为 n。
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is negative infinity.
     * <li> If the argument is equal to 10<sup><i>n</i></sup> for
     * integer <i>n</i>, then the result is <i>n</i>.
     * </ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的
     * @param   a   a value
     * @return  the base 10 logarithm of  {@code a}.
     * @since 1.5
     */
    public static double log10(double a) {
        return StrictMath.log10(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the correctly rounded positive square root of a
     * {@code double} value.
     * 1.返回正确舍入的double值的正平方根。
     * 2.特别情况：
     * 如果参数为 NaN 或小于零，则结果为 NaN。
     * 如果参数为正无穷大，则结果为正无穷大。
     * 如果参数为正零或负零，则结果与参数相同。
     * Special cases:
     * <ul><li>If the argument is NaN or less than zero, then the result
     * is NaN.
     * <li>If the argument is positive infinity, then the result is positive
     * infinity.
     * <li>If the argument is positive zero or negative zero, then the
     * result is the same as the argument.</ul>
     * Otherwise, the result is the {@code double} value closest to
     * the true mathematical square root of the argument value.
     *3.否则，结果是最接近参数值的真实数学平方根的 double 值
     * @param   a   a value.
     * @return  the positive square root of {@code a}.
     *          If the argument is NaN or less than zero, the result is NaN.
     */
    public static double sqrt(double a) {
        return StrictMath.sqrt(a); // default impl. delegates to StrictMath
                                   // Note that hardware sqrt instructions
                                   // frequently can be directly used by JITs
                                   // and should be much faster than doing
                                   // Math.sqrt in software.
    }


    /**
     * Returns the cube root of a {@code double} value.  For
     * positive finite {@code x}, {@code cbrt(-x) ==
     * -cbrt(x)}; that is, the cube root of a negative value is
     * the negative of the cube root of that value's magnitude.
     * 1.返回 double值的立方根。对于正有限 x，cbrt(-x) == -cbrt(x);
     * 也就是说，负值的立方根是该值大小的立方根的负数
     * Special cases:
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数是无穷大，则结果是与参数同号的无穷大。
     * 如果参数为零，则结果为与参数相同符号的零。
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is an infinity
     * with the same sign as the argument.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * </ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * 3.计算结果必须在精确结果的 1 ulp 以内。
     * @param   a   a value.
     * @return  the cube root of {@code a}.
     * @since 1.5
     */
    public static double cbrt(double a) {
        return StrictMath.cbrt(a);
    }

    /**
     * Computes the remainder operation on two arguments as prescribed
     * by the IEEE 754 standard.
     * 1.按照 IEEE 754 标准的规定计算两个参数的余数运算
     * The remainder value is mathematically equal to
     * <code>f1&nbsp;-&nbsp;f2</code>&nbsp;&times;&nbsp;<i>n</i>,
     * where <i>n</i> is the mathematical integer closest to the exact
     * mathematical value of the quotient {@code f1/f2}, and if two
     * mathematical integers are equally close to {@code f1/f2},
     * then <i>n</i> is the integer that is even. If the remainder is
     * zero, its sign is the same as the sign of the first argument.
     * 2.余数在数学上等于 f1 - f2× n，其中 n是最接近商  f1/f2 的精确数学值的数学整数，
     * 如果两个数学整数同样接近 f1/f2}，则 n是偶数的整数。
     * 如果余数为零，则其符号与第一个参数的符号相同。
     * Special cases:
     * <ul><li>If either argument is NaN, or the first argument is infinite,
     * or the second argument is positive zero or negative zero, then the
     * result is NaN.
     * <li>If the first argument is finite and the second argument is
     * infinite, then the result is the same as the first argument.</ul>
     *
     * @param   f1   the dividend.
     * @param   f2   the divisor.
     * @return  the remainder when {@code f1} is divided by
     *          {@code f2}.
     */
    public static double IEEEremainder(double f1, double f2) {
        return StrictMath.IEEEremainder(f1, f2); // delegate to StrictMath
    }

    /**
     * Returns the smallest (closest to negative infinity)
     * {@code double} value that is greater than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity or
     * positive zero or negative zero, then the result is the same as
     * the argument.  <li>If the argument value is less than zero but
     * greater than -1.0, then the result is negative zero.</ul> Note
     * that the value of {@code Math.ceil(x)} is exactly the
     * value of {@code -Math.floor(-x)}.
     * 1.返回大于或等于参数且等于数学整数的最小（最接近负无穷大）double值。
     * 2.特殊情况：
     * 如果参数值已经等于一个数学整数，则结果与参数相同。
     * 如果参数是 NaN 或无穷大或正零或负零，则结果与参数相同。
     * 如果参数值小于零但大于-1.0，则结果为负零。
     * 注意Math.ceil(x)的值正是-Math.floor(-x)。
     *
     * @param   a   a value.
     * @return  the smallest (closest to negative infinity)
     *          floating-point value that is greater than or equal to
     *          the argument and is equal to a mathematical integer.
     */
    public static double ceil(double a) {
        return StrictMath.ceil(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the largest (closest to positive infinity)
     * {@code double} value that is less than or equal to the
     * argument and is equal to a mathematical integer. Special cases:
     * <ul><li>If the argument value is already equal to a
     * mathematical integer, then the result is the same as the
     * argument.  <li>If the argument is NaN or an infinity or
     * positive zero or negative zero, then the result is the same as
     * the argument.</ul>
     * 1.返回小于或等于参数且等于数学整数的最大（最接近正无穷大）double值。
     * 2.特殊情况：
     * 如果参数值已经等于一个数学整数，则结果与参数相同。
     * 如果参数是 NaN 或无穷大或正零或负零，则结果与参数相同。
     * @param   a   a value.
     * @return  the largest (closest to positive infinity)
     *          floating-point value that less than or equal to the argument
     *          and is equal to a mathematical integer.
     */
    public static double floor(double a) {
        return StrictMath.floor(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the {@code double} value that is closest in value
     * to the argument and is equal to a mathematical integer. If two
     * {@code double} values that are mathematical integers are
     * equally close, the result is the integer value that is
     * even. Special cases:
     * <ul><li>If the argument value is already equal to a mathematical
     * integer, then the result is the same as the argument.
     * <li>If the argument is NaN or an infinity or positive zero or negative
     * zero, then the result is the same as the argument.</ul>
     * 1.返回值与参数最接近且等于数学整数的 double值。
     * 如果作为数学整数的两个 double值同样接近，则结果是偶数的整数值
     * 2.特殊情况：如果参数值已经等于一个数学整数，则结果与参数相同。
     * 如果参数是 NaN 或无穷大或正零或负零，则结果与参数相同。
     * @param   a   a {@code double} value.
     * @return  the closest floating-point value to {@code a} that is
     *          equal to a mathematical integer.
     */
    public static double rint(double a) {
        return StrictMath.rint(a); // default impl. delegates to StrictMath
    }

    /**
     * Returns the angle <i>theta</i> from the conversion of rectangular
     * coordinates ({@code x},&nbsp;{@code y}) to polar
     * coordinates (r,&nbsp;<i>theta</i>).
     * 1.从直角坐标 x,y到极坐标 (r, theta) 的转换返回角度 theta。
     * This method computes the phase <i>theta</i> by computing an arc tangent
     * of {@code y/x} in the range of -<i>pi</i> to <i>pi</i>.
     * 2.此方法通过计算 y/x在 -pi到pi范围内的反正切来计算相位theta
     * 3.特殊情况：
     *  1）如果任一参数为 NaN，则结果为 NaN
     *  2）如果第一个参数为正零而第二个参数为正，或者第一个参数为正且有限且第二个参数为正无穷大，则结果为正零
     * Special cases:
     * <ul><li>If either argument is NaN, then the result is NaN.
     * <li>If the first argument is positive zero and the second argument
     * is positive, or the first argument is positive and finite and the
     * second argument is positive infinity, then the result is positive
     * zero.
     * <li>If the first argument is negative zero and the second argument
     * is positive, or the first argument is negative and finite and the
     * second argument is positive infinity, then the result is negative zero.
     *  3）如果第一个参数为负零而第二个参数为正，或者第一个参数为负且有限且第二个参数为正无穷大，则结果为负零。
     * <li>If the first argument is positive zero and the second argument
     * is negative, or the first argument is positive and finite and the
     * second argument is negative infinity, then the result is the
     * {@code double} value closest to <i>pi</i>.
     *  4）如果第一个参数为正零而第二个参数为负，或者第一个参数为正且有限且第二个参数为负无穷大，
     *  则结果是最接近 pi的 double值
     * <li>If the first argument is negative zero and the second argument
     * is negative, or the first argument is negative and finite and the
     * second argument is negative infinity, then the result is the
     * {@code double} value closest to -<i>pi</i>.
     * 5）如果第一个参数为负零且第二个参数为负，或者第一个参数为负且有限且第二个参数为负无穷大，
     * 则结果是最接近 -pi的 double值.
     * <li>If the first argument is positive and the second argument is
     * positive zero or negative zero, or the first argument is positive
     * infinity and the second argument is finite, then the result is the
     * {@code double} value closest to <i>pi</i>/2.
     * 6）如果第一个参数为正而第二个参数为正零或负零，或者第一个参数为正无穷大而第二个参数为有限，
     * 则结果为最接近 pi/2的 double} 值.
     * <li>If the first argument is negative and the second argument is
     * positive zero or negative zero, or the first argument is negative
     * infinity and the second argument is finite, then the result is the
     * {@code double} value closest to -<i>pi</i>/2.
     * 7）如果第一个参数为负而第二个参数为正零或负零，或者第一个参数为负无穷大而第二个参数为有限，
     * 则结果为最接近 -pi/2 的 double值
     * <li>If both arguments are positive infinity, then the result is the
     * {@code double} value closest to <i>pi</i>/4.
     * 8）如果两个参数都是正无穷大，则结果是最接近 pi/4 的 double值
     * <li>If the first argument is positive infinity and the second argument
     * is negative infinity, then the result is the {@code double}
     * value closest to 3*<i>pi</i>/4.
     * 9）如果第一个参数是正无穷大，第二个参数是负无穷大，那么结果是最接近 3*pi/4 的double值
     * <li>If the first argument is negative infinity and the second argument
     * is positive infinity, then the result is the {@code double} value
     * closest to -<i>pi</i>/4.
     * 10）如果第一个参数是负无穷大，第二个参数是正无穷大，那么结果是最接近 -pi/4 的 double值。
     * <li>If both arguments are negative infinity, then the result is the
     * {@code double} value closest to -3*<i>pi</i>/4.</ul>
     * 11）如果两个参数都是负无穷大，则结果是最接近 -3*pi/4的 double值
     * <p>The computed result must be within 2 ulps of the exact result.
     * Results must be semi-monotonic.
     * 计算结果必须在准确结果的 2 ul 以内。结果必须是半单调的
     * @param   y   the ordinate coordinate
     * @param   x   the abscissa coordinate
     * @return  the <i>theta</i> component of the point
     *          (<i>r</i>,&nbsp;<i>theta</i>)
     *          in polar coordinates that corresponds to the point
     *          (<i>x</i>,&nbsp;<i>y</i>) in Cartesian coordinates.
     */
    public static double atan2(double y, double x) {
        return StrictMath.atan2(y, x); // default impl. delegates to StrictMath
    }

    /**
     * Returns the value of the first argument raised to the power of the
     * second argument. Special cases:
     * 1.返回第一个参数的第二个参数的幂的值
     * 2.特殊情况：
     *  1）如果第二个参数是正零或负零，则结果为 1.0
     * <ul><li>If the second argument is positive or negative zero, then the
     * result is 1.0.
     * <li>If the second argument is 1.0, then the result is the same as the
     * first argument.
     * 2）如果第二个参数为 1.0，则结果与第一个参数相同
     * <li>If the second argument is NaN, then the result is NaN.
     * 3）如果第二个参数为 NaN，则结果为 NaN。
     * <li>If the first argument is NaN and the second argument is nonzero,
     * then the result is NaN.
     * 4）如果第一个参数为 NaN 而第二个参数非零，则结果为 NaN
     * <li>If
     * <ul>
     * <li>the absolute value of the first argument is greater than 1
     * and the second argument is positive infinity, or
     * <li>the absolute value of the first argument is less than 1 and
     * the second argument is negative infinity,
     * </ul>
     * then the result is positive infinity.
     * 5）如果第一个参数的绝对值大于1且第二个参数为正无穷大，或者第一个参数的绝对值小于1且第二个参数为负无穷大，
     * 那么结果是正无穷大
     * <li>If
     * <ul>
     * <li>the absolute value of the first argument is greater than 1 and
     * the second argument is negative infinity, or
     * <li>the absolute value of the
     * first argument is less than 1 and the second argument is positive
     * infinity,
     * </ul>
     * then the result is positive zero.
     * 6）如果第一个参数的绝对值大于1且第二个参数为负无穷大，或者第一个参数的绝对值小于1且第二个参数为正无穷大，
     * 那么结果是正零。
     * <li>If the absolute value of the first argument equals 1 and the
     * second argument is infinite, then the result is NaN.
     * 7）如果第一个参数的绝对值等于 1 而第二个参数是无穷大，则结果为 NaN。
     * <li>If
     * <ul>
     * <li>the first argument is positive zero and the second argument
     * is greater than zero, or
     * <li>the first argument is positive infinity and the second
     * argument is less than zero,
     * </ul>
     * then the result is positive zero.
     * 8）如果第一个参数为正零且第二个参数大于零，或第一个参数为正无穷大且第二个参数小于零，那么结果为正零.
     * <li>If
     * <ul>
     * <li>the first argument is positive zero and the second argument
     * is less than zero, or
     * <li>the first argument is positive infinity and the second
     * argument is greater than zero,
     * </ul>
     * then the result is positive infinity.
     * 9）如果 第一个参数为正零且第二个参数小于零，或第一个参数为正无穷大且第二个参数大于零，则 那么结果为正无穷大
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is greater than zero but not a finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is less than zero but not a finite odd integer,
     * </ul>
     * then the result is positive zero.
     * 10）如果第一个参数是负零，第二个参数大于零但不是有限奇数，或者第一个参数是负无穷大，第二个参数小于零但不是有限的奇数，那么结果是正零。
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is a positive finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is a negative finite odd integer,
     * </ul>
     * then the result is negative zero.
     * 11）如果第一个参数是负零，第二个参数是正有限奇整数，或者第一个参数是负无穷大，
     * 第二个参数是负有限奇整数，那么结果为负零
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is less than zero but not a finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is greater than zero but not a finite odd integer,
     * </ul>
     * then the result is positive infinity.
     * 12）如果第一个参数为负零且第二个参数小于零但不是有限奇数，
     * 或者第一个参数为负无穷大且第二个参数大于零但不是有限的奇数，那么结果是正无穷大。
     * <li>If
     * <ul>
     * <li>the first argument is negative zero and the second argument
     * is a negative finite odd integer, or
     * <li>the first argument is negative infinity and the second
     * argument is a positive finite odd integer,
     * </ul>
     * then the result is negative infinity.
     * 13）如果第一个参数是负零，第二个参数是负有限奇整数，
     * 或者第一个参数是负无穷大，第二个参数是正有限奇整数，那么结果是负无穷大
     * <li>If the first argument is finite and less than zero
     * <ul>
     * <li> if the second argument is a finite even integer, the
     * result is equal to the result of raising the absolute value of
     * the first argument to the power of the second argument
     * 13）如果第一个参数是有限的且小于零，如果第二个参数是有限偶数，则结果等于第一个参数的绝对值的第二个参数的幂的结果
     * 如果第二个参数是一个有限奇数，则结果等于第一个参数的绝对值的第二个参数的幂的负数，如果第二个参数是有限的而不是整数，则结果为 NaN。
     * <li>if the second argument is a finite odd integer, the result
     * is equal to the negative of the result of raising the absolute
     * value of the first argument to the power of the second
     * argument
     *
     * <li>if the second argument is finite and not an integer, then
     * the result is NaN.
     * </ul>
     *
     * <li>If both arguments are integers, then the result is exactly equal
     * to the mathematical result of raising the first argument to the power
     * of the second argument if that result can in fact be represented
     * exactly as a {@code double} value.</ul>
     * 14）如果两个参数都是整数，那么如果该结果实际上可以完全表示为double值，
     * 则结果完全等于将第一个参数提高到第二个参数的幂的数学结果。
     * <p>(In the foregoing descriptions, a floating-point value is
     * considered to be an integer if and only if it is finite and a
     * fixed point of the method {@link #ceil ceil} or,
     * equivalently, a fixed point of the method {@link #floor
     * floor}. A value is a fixed point of a one-argument
     * method if and only if the result of applying the method to the
     * value is equal to the value.)
     * 15）（在前面的描述中，浮点值被认为是整数当且仅当它是有限的并且是方法  ceil的不动点，
     * 或者等价地，方法的不动点 floor. 当且仅当将该方法应用于该值的结果等于该值时，该值是单参数方法的不动点。
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     * 计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的。
     * @param   a   the base.
     * @param   b   the exponent.
     * @return  the value {@code a}<sup>{@code b}</sup>.
     */
    public static double pow(double a, double b) {
        return StrictMath.pow(a, b); // default impl. delegates to StrictMath
    }

    /**
     * Returns the closest {@code int} to the argument, with ties
     * rounding to positive infinity.
     * 1.返回最接近参数的 int，关系四舍五入到正无穷大
     * <p>
     * Special cases:
     * <ul><li>If the argument is NaN, the result is 0.
     * <li>If the argument is negative infinity or any value less than or
     * equal to the value of {@code Integer.MIN_VALUE}, the result is
     * equal to the value of {@code Integer.MIN_VALUE}.
     * <li>If the argument is positive infinity or any value greater than or
     * equal to the value of {@code Integer.MAX_VALUE}, the result is
     * equal to the value of {@code Integer.MAX_VALUE}.</ul>
     * 2.特别情况：
     *  1）如果参数为 NaN，则结果为 0。
     *  2）如果参数为负无穷大或任何小于或等于Integer.MIN_VALUE的值，则结果等于Integer.MIN_VALUE的值。
     *  3）如果参数为正无穷大或任何大于或等于Integer.MAX_VALUE的值，则结果等于Integer.MAX_VALUE的值。
     * @param   a   a floating-point value to be rounded to an integer.
     * @return  the value of the argument rounded to the nearest
     *          {@code int} value.
     * @see     java.lang.Integer#MAX_VALUE
     * @see     java.lang.Integer#MIN_VALUE
     */
    public static int round(float a) {
        int intBits = Float.floatToRawIntBits(a);
        int biasedExp = (intBits & FloatConsts.EXP_BIT_MASK)
                >> (FloatConsts.SIGNIFICAND_WIDTH - 1);
        int shift = (FloatConsts.SIGNIFICAND_WIDTH - 2
                + FloatConsts.EXP_BIAS) - biasedExp;
        if ((shift & -32) == 0) { // shift >= 0 && shift < 32
            // a is a finite number such that pow(2,-32) <= ulp(a) < 1
            int r = ((intBits & FloatConsts.SIGNIF_BIT_MASK)
                    | (FloatConsts.SIGNIF_BIT_MASK + 1));
            if (intBits < 0) {
                r = -r;
            }
            // In the comments below each Java expression evaluates to the value
            // the corresponding mathematical expression:
            // (r) evaluates to a / ulp(a)
            // (r >> shift) evaluates to floor(a * 2)
            // ((r >> shift) + 1) evaluates to floor((a + 1/2) * 2)
            // (((r >> shift) + 1) >> 1) evaluates to floor(a + 1/2)
            return ((r >> shift) + 1) >> 1;
        } else {
            // a is either
            // - a finite number with abs(a) < exp(2,FloatConsts.SIGNIFICAND_WIDTH-32) < 1/2
            //abs(a) < exp(2,FloatConsts.SIGNIFICAND_WIDTH-32) < 12 的有限数
            // - a finite number with ulp(a) >= 1 and hence a is a mathematical integer
            //ulp(a) >= 1 的有限数，因此 a 是数学整数
            // - an infinity or NaN
            //按infinity or NaN
            return (int) a;
        }
    }

    /**
     * Returns the closest {@code long} to the argument, with ties
     * rounding to positive infinity.
     * 1.返回与参数最接近的long，关系四舍五入到正无穷大
     * <p>Special cases:
     * <ul><li>If the argument is NaN, the result is 0.
     * <li>If the argument is negative infinity or any value less than or
     * equal to the value of {@code Long.MIN_VALUE}, the result is
     * equal to the value of {@code Long.MIN_VALUE}.
     * <li>If the argument is positive infinity or any value greater than or
     * equal to the value of {@code Long.MAX_VALUE}, the result is
     * equal to the value of {@code Long.MAX_VALUE}.</ul>
     * 2.特殊情况：
     *   1)如果参数为 NaN，则结果为 0。
     *   2)如果参数为负无穷大或任何小于或等于Long.MIN_VALUE的值，则结果等于 Long.MIN_VALUE的值。
     *   3)如果参数为正无穷大或任何大于或等于 Long.MAX_VALUE} 的值，则结果等于Long.MAX_VALUE的值
     * @param   a   a floating-point value to be rounded to a
     *          {@code long}.
     * @return  the value of the argument rounded to the nearest
     *          {@code long} value.
     * @see     java.lang.Long#MAX_VALUE
     * @see     java.lang.Long#MIN_VALUE
     */
    public static long round(double a) {
        long longBits = Double.doubleToRawLongBits(a);
        long biasedExp = (longBits & DoubleConsts.EXP_BIT_MASK)
                >> (DoubleConsts.SIGNIFICAND_WIDTH - 1);
        long shift = (DoubleConsts.SIGNIFICAND_WIDTH - 2
                + DoubleConsts.EXP_BIAS) - biasedExp;
        if ((shift & -64) == 0) { // shift >= 0 && shift < 64
            // a is a finite number such that pow(2,-64) <= ulp(a) < 1
            long r = ((longBits & DoubleConsts.SIGNIF_BIT_MASK)
                    | (DoubleConsts.SIGNIF_BIT_MASK + 1));
            if (longBits < 0) {
                r = -r;
            }
            // In the comments below each Java expression evaluates to the value
            // the corresponding mathematical expression:
            // (r) evaluates to a / ulp(a)
            // (r >> shift) evaluates to floor(a * 2)
            // ((r >> shift) + 1) evaluates to floor((a + 1/2) * 2)
            // (((r >> shift) + 1) >> 1) evaluates to floor(a + 1/2)
            return ((r >> shift) + 1) >> 1;
        } else {
            // a is either
            // - a finite number with abs(a) < exp(2,DoubleConsts.SIGNIFICAND_WIDTH-64) < 1/2
            // - a finite number with ulp(a) >= 1 and hence a is a mathematical integer
            // - an infinity or NaN
            return (long) a;
        }
    }

    private static final class RandomNumberGeneratorHolder {
        static final Random randomNumberGenerator = new Random();
    }

    /**
     * Returns a {@code double} value with a positive sign, greater
     * than or equal to {@code 0.0} and less than {@code 1.0}.
     * Returned values are chosen pseudorandomly with (approximately)
     * uniform distribution from that range.
     * 1.返回一个带有正号的 double值，大于或等于 0.0且小于 1.0。
     * 返回值是伪随机选择的，具有该范围内的（近似）均匀分布
     * <p>When this method is first called, it creates a single new
     * pseudorandom-number generator, exactly as if by the expression
     *
     * <blockquote>{@code new java.util.Random()}</blockquote>
     * 2.当这个方法第一次被调用时，它会创建一个新的伪随机数生成器，
     * 就像表达式  new java.util.Random()
     * This new pseudorandom-number generator is used thereafter for
     * all calls to this method and is used nowhere else.
     * 3.此新的伪随机数生成器此后用于对此方法的所有调用，并且不会在其他任何地方使用
     * <p>This method is properly synchronized to allow correct use by
     * more than one thread. However, if many threads need to generate
     * pseudorandom numbers at a great rate, it may reduce contention
     * for each thread to have its own pseudorandom-number generator.
     * 4.此方法已正确同步，以允许多个线程正确使用。但是，如果许多线程需要以很高的速度生成伪随机数，
     * 则可以减少每个线程拥有自己的伪随机数生成器的争用。
     * @return  a pseudorandom {@code double} greater than or equal
     * to {@code 0.0} and less than {@code 1.0}.
     * @see Random#nextDouble()
     */
    public static double random() {
        return RandomNumberGeneratorHolder.randomNumberGenerator.nextDouble();
    }

    /**
     * Returns the sum of its arguments,
     * throwing an exception if the result overflows an {@code int}.
     * 返回其参数的总和，如果结果溢出int，则抛出异常。
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int addExact(int x, int y) {
        int r = x + y;
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }

    /**
     * Returns the sum of its arguments,
     * throwing an exception if the result overflows a {@code long}.
     * 返回其参数的总和，如果结果溢出 long，则抛出异常
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long addExact(long x, long y) {
        long r = x + y;
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }

    /**
     * Returns the difference of the arguments,
     * throwing an exception if the result overflows an {@code int}.
     * 返回参数的差异，如果结果溢出 int，则抛出异常
     * @param x the first value
     * @param y the second value to subtract from the first
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int subtractExact(int x, int y) {
        int r = x - y;
        // HD 2-12 Overflow iff the arguments have different signs and
        // the sign of the result is different than the sign of x
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }

    /**
     * Returns the difference of the arguments,
     * throwing an exception if the result overflows a {@code long}.
     * 返回参数的差异，如果结果溢出 long，则抛出异常
     * @param x the first value
     * @param y the second value to subtract from the first
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long subtractExact(long x, long y) {
        long r = x - y;
        // HD 2-12 Overflow iff the arguments have different signs and
        // the sign of the result is different than the sign of x
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }

    /**
     * Returns the product of the arguments,
     * throwing an exception if the result overflows an {@code int}.
     * 返回参数的乘积，如果结果溢出 int，则抛出异常
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int multiplyExact(int x, int y) {
        long r = (long)x * (long)y;
        if ((int)r != r) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)r;
    }

    /**
     * Returns the product of the arguments,
     * throwing an exception if the result overflows a {@code long}.
     * 返回参数的乘积，如果结果溢出 long，则抛出异常
     * @param x the first value
     * @param y the second value
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long multiplyExact(long x, long y) {
        long r = x * y;
        long ax = Math.abs(x);
        long ay = Math.abs(y);
        if (((ax | ay) >>> 31 != 0)) {
            // Some bits greater than 2^31 that might cause overflow
            // Check the result using the divide operator
            // and check for the special case of Long.MIN_VALUE * -1
           if (((y != 0) && (r / y != x)) ||
               (x == Long.MIN_VALUE && y == -1)) {
                throw new ArithmeticException("long overflow");
            }
        }
        return r;
    }

    /**
     * Returns the argument incremented by one, throwing an exception if the
     * result overflows an {@code int}.
     * 返回递增 1 的参数，如果结果溢出int，则抛出异常
     * @param a the value to increment
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int incrementExact(int a) {
        if (a == Integer.MAX_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return a + 1;
    }

    /**
     * Returns the argument incremented by one, throwing an exception if the
     * result overflows a {@code long}.
     * 返回自增一的参数，如果结果溢出long则抛出异常
     * @param a the value to increment
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long incrementExact(long a) {
        if (a == Long.MAX_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return a + 1L;
    }

    /**
     * Returns the argument decremented by one, throwing an exception if the
     * result overflows an {@code int}.
     * 返回减一的参数，如果结果溢出 int，则抛出异常
     * @param a the value to decrement
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int decrementExact(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return a - 1;
    }

    /**
     * Returns the argument decremented by one, throwing an exception if the
     * result overflows a {@code long}.
     * 返回减一的参数，如果结果溢出long，则抛出异常。
     * @param a the value to decrement
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long decrementExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return a - 1L;
    }

    /**
     * Returns the negation of the argument, throwing an exception if the
     * result overflows an {@code int}.
     * 返回参数的负数，如果结果溢出 int则抛出异常
     * @param a the value to negate
     * @return the result
     * @throws ArithmeticException if the result overflows an int
     * @since 1.8
     */
    public static int negateExact(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return -a;
    }

    /**
     * Returns the negation of the argument, throwing an exception if the
     * result overflows a {@code long}.
     * 返回参数的否定，如果结果溢出 long则抛出异常
     * @param a the value to negate
     * @return the result
     * @throws ArithmeticException if the result overflows a long
     * @since 1.8
     */
    public static long negateExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return -a;
    }

    /**
     * Returns the value of the {@code long} argument;
     * throwing an exception if the value overflows an {@code int}.
     * 返回 long参数的值；如果值溢出int，则抛出异常
     * @param value the long value
     * @return the argument as an int
     * @throws ArithmeticException if the {@code argument} overflows an int
     * @since 1.8
     */
    public static int toIntExact(long value) {
        if ((int)value != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)value;
    }

    /**
     * Returns the largest (closest to positive infinity)
     * {@code int} value that is less than or equal to the algebraic quotient.
     * There is one special case, if the dividend is the
     * {@linkplain Integer#MIN_VALUE Integer.MIN_VALUE} and the divisor is {@code -1},
     * then integer overflow occurs and
     * the result is equal to the {@code Integer.MIN_VALUE}.
     * 1.返回小于或等于代数商的最大（最接近正无穷大）int值。
     * 2.有一种特殊情况，如果被除数为Integer.MIN_VALUE，除数为 -1，则发生整数溢出，结果等于Integer.MIN_VALUE
     * 3.正常整数除法在舍入到零舍入模式（截断）下运行。相反，此操作在向负无穷大（下限）舍入模式的舍入下起作用。
     * 当确切结果为负时，地板舍入模式给出的结果与截断不同
     * <p>
     * Normal integer division operates under the round to zero rounding mode
     * (truncation).  This operation instead acts under the round toward
     * negative infinity (floor) rounding mode.
     * The floor rounding mode gives different results than truncation
     * when the exact result is negative.
     * <ul>
     *   <li>If the signs of the arguments are the same, the results of
     *       {@code floorDiv} and the {@code /} operator are the same.  <br>
     *       For example, {@code floorDiv(4, 3) == 1} and {@code (4 / 3) == 1}.</li>
     *   <li>If the signs of the arguments are different,  the quotient is negative and
     *       {@code floorDiv} returns the integer less than or equal to the quotient
     *       and the {@code /} operator returns the integer closest to zero.<br>
     *       For example, {@code floorDiv(-4, 3) == -2},
     *       whereas {@code (-4 / 3) == -1}.
     *   </li>
     * </ul>
     * <p>
     * 4.如果参数的符号相同，则floorDiv和/运算符的结果相同。
     *例如，floorDiv(4, 3) == 1 和  (4 /3) == 1。
     * 如果参数的符号不同，则商为负数，floorDiv返回小于或等于商的整数，/ 运算符返回最接近零的整数。
     * 例如， floorDiv(-4, 3) == - 2，而(-4 /3) == -1
     * @param x the dividend
     * @param y the divisor
     * @return the largest (closest to positive infinity)
     * {@code int} value that is less than or equal to the algebraic quotient.
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @see #floorMod(int, int)
     * @see #floor(double)
     * @since 1.8
     */
    public static int floorDiv(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    /**
     * Returns the largest (closest to positive infinity)
     * {@code long} value that is less than or equal to the algebraic quotient.
     * 1.返回小于或等于代数商的最大（最接近正无穷大）long 值
     * There is one special case, if the dividend is the
     * {@linkplain Long#MIN_VALUE Long.MIN_VALUE} and the divisor is {@code -1},
     * then integer overflow occurs and
     * the result is equal to the {@code Long.MIN_VALUE}.
     * 2.有一种特殊情况，如果被除数是Long.MIN_VALUE，除数是 -1，则发生整数溢出，结果等于Long.MIN_VALUE
     * <p>
     * Normal integer division operates under the round to zero rounding mode
     * (truncation).  This operation instead acts under the round toward
     * negative infinity (floor) rounding mode.
     * The floor rounding mode gives different results than truncation
     * when the exact result is negative.
     * 3.正常整数除法在舍入到零舍入模式（截断）下运行。相反，
     * 此操作在向负无穷大（下限）舍入模式的舍入下起作用。当确切结果为负时，
     * 地板舍入模式给出的结果与截断不同。
     * <p>
     * For examples, see {@link #floorDiv(int, int)}.
     *
     * @param x the dividend
     * @param y the divisor
     * @return the largest (closest to positive infinity)
     * {@code long} value that is less than or equal to the algebraic quotient.
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @see #floorMod(long, long)
     * @see #floor(double)
     * @since 1.8
     */
    public static long floorDiv(long x, long y) {
        long r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    /**
     * Returns the floor modulus of the {@code int} arguments.
     * 1.返回 int参数的下限模数
     * <p>
     * The floor modulus is {@code x - (floorDiv(x, y) * y)},
     * has the same sign as the divisor {@code y}, and
     * is in the range of {@code -abs(y) < r < +abs(y)}.
     * 2.底模数为x - (floorDiv(x, y)*y)，与除数 y同号，在-abs(y) < r < +abs(y)
     * <p>
     * The relationship between {@code floorDiv} and {@code floorMod} is such that:
     * <ul>
     *   <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}
     * </ul>
     * <p>
     *     3.floorDiv和 floorMod的关系是这样的：floorDiv(x, y)*y + floorMod(x, y) == x
     * The difference in values between {@code floorMod} and
     * the {@code %} operator is due to the difference between
     * {@code floorDiv} that returns the integer less than or equal to the quotient
     * and the {@code /} operator that returns the integer closest to zero.
     * 4.floorMod和 %运算符之间的值差异是由于返回小于或等于商的整数的floorDiv
     * 和返回整数的 /运算符之间的差异最接近零的整数。
     * <p>
     * Examples:
     * <ul>
     *   <li>If the signs of the arguments are the same, the results
     *       of {@code floorMod} and the {@code %} operator are the same.  <br>
     *       <ul>
     *       <li>{@code floorMod(4, 3) == 1}; &nbsp; and {@code (4 % 3) == 1}</li>
     *       </ul>
     *   <li>If the signs of the arguments are different, the results differ from the {@code %} operator.<br>
     *      <ul>
     *      <li>{@code floorMod(+4, -3) == -2}; &nbsp; and {@code (+4 % -3) == +1} </li>
     *      <li>{@code floorMod(-4, +3) == +2}; &nbsp; and {@code (-4 % +3) == -1} </li>
     *      <li>{@code floorMod(-4, -3) == -1}; &nbsp; and {@code (-4 % -3) == -1 } </li>
     *      </ul>
     *   </li>
     * </ul>
     * <p>
     *     5.示例：
     *     如果参数的符号相同，则floorMod和%运算符的结果相同。floorMod(4, 3) == 1;和{@code (4 % 3) == 1}
     *     如果参数的符号不同，则结果与%运算符不同。
     *     floorMod(+4, -3) == -2};和  (+4 % -3) == +1
     *     floorMod(-4, +3) == +2;和  (-4 % +3) == -1}
     *     floorMod(-4, -3) == -1};和  (-4 % -3) == -1
     * If the signs of arguments are unknown and a positive modulus
     * is needed it can be computed as {@code (floorMod(x, y) + abs(y)) % abs(y)}.
     * 6.如果参数的符号未知并且需要正模数，则可以将其计算为 (floorMod(x, y) + abs(y)) % abs(y)
     * @param x the dividend
     * @param y the divisor
     * @return the floor modulus {@code x - (floorDiv(x, y) * y)}
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @see #floorDiv(int, int)
     * @since 1.8
     */
    public static int floorMod(int x, int y) {
        int r = x - floorDiv(x, y) * y;
        return r;
    }

    /**
     * Returns the floor modulus of the {@code long} arguments.
     * 1.返回long参数的下限模数。
     * <p>
     * The floor modulus is {@code x - (floorDiv(x, y) * y)},
     * has the same sign as the divisor {@code y}, and
     * is in the range of {@code -abs(y) < r < +abs(y)}.
     * 2.底模数为 x - (floorDiv(x, y) y)，与除数 y同号，在 -abs(y) < r < +abs(y)
     * <p>
     * The relationship between {@code floorDiv} and {@code floorMod} is such that:
     * <ul>
     *   <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}
     * </ul>
     * <p>
     *     3.floorDiv和 floorMod的关系是这样的：
     *     floorDiv(x, y) y + floorMod(x, y) == x
     * For examples, see {@link #floorMod(int, int)}.
     *
     * @param x the dividend
     * @param y the divisor
     * @return the floor modulus {@code x - (floorDiv(x, y) * y)}
     * @throws ArithmeticException if the divisor {@code y} is zero
     * @see #floorDiv(long, long)
     * @since 1.8
     */
    public static long floorMod(long x, long y) {
        return x - floorDiv(x, y) * y;
    }

    /**
     * Returns the absolute value of an {@code int} value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned.
     * 1.返回 int值的绝对值。如果参数不是负数，则返回该参数。如果参数为负，则返回参数的否定
     * <p>Note that if the argument is equal to the value of
     * {@link Integer#MIN_VALUE}, the most negative representable
     * {@code int} value, the result is that same value, which is
     * negative.
     * 2.请注意，如果参数等于 Integer.MIN_VALUE的值，即最负可表示的int值，则结果是相同的值，即负值。
     * @param   a   the argument whose absolute value is to be determined
     * @return  the absolute value of the argument.
     */
    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }

    /**
     * Returns the absolute value of a {@code long} value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned.
     * 1.返回 long值的绝对值。如果参数不是负数，则返回该参数。如果参数为负，则返回参数的否定
     * <p>Note that if the argument is equal to the value of
     * {@link Long#MIN_VALUE}, the most negative representable
     * {@code long} value, the result is that same value, which
     * is negative.
     * 2.请注意，如果参数等于Long.MIN_VALUE的值，即最负可表示的long值，则结果是相同的值，即负值。
     * @param   a   the argument whose absolute value is to be determined
     * @return  the absolute value of the argument.
     */
    public static long abs(long a) {
        return (a < 0) ? -a : a;
    }

    /**
     * Returns the absolute value of a {@code float} value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned.
     * 1.返回 float值的绝对值。如果参数不是负数，则返回该参数。如果参数为负，则返回参数的否定。
     * Special cases:
     * 2.特别案例：
     *   如果参数为正零或负零，则结果为正零。
     *   如果参数为无穷大，则结果为正无穷大。
     *   如果参数为 NaN，则结果为 NaN。
     *   换句话说，结果与表达式的值相同： Float.intBitsToFloat(0x7fffffff & Float.floatToIntBits(a ))
     * <ul><li>If the argument is positive zero or negative zero, the
     * result is positive zero.
     * <li>If the argument is infinite, the result is positive infinity.
     * <li>If the argument is NaN, the result is NaN.</ul>
     * In other words, the result is the same as the value of the expression:
     * <p>{@code Float.intBitsToFloat(0x7fffffff & Float.floatToIntBits(a))}
     *
     * @param   a   the argument whose absolute value is to be determined
     * @return  the absolute value of the argument.
     */
    public static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }

    /**
     * Returns the absolute value of a {@code double} value.
     * If the argument is not negative, the argument is returned.
     * If the argument is negative, the negation of the argument is returned.
     * 1.返回 double值的绝对值。如果参数不是负数，则返回该参数。如果参数为负，则返回参数的否定。
     * 2.特殊情况：
     * 如果参数为正零或负零，则结果为正零。
     * 如果参数为无穷大，则结果为正无穷大。
     * 如果参数为 NaN，则结果为 NaN。
     * 换句话说，结果与表达式的值相同：Double.longBitsToDouble((Double.doubleToLongBits(a) <<1)>>>1)
     * Special cases:
     * <ul><li>If the argument is positive zero or negative zero, the result
     * is positive zero.
     * <li>If the argument is infinite, the result is positive infinity.
     * <li>If the argument is NaN, the result is NaN.</ul>
     * In other words, the result is the same as the value of the expression:
     * <p>{@code Double.longBitsToDouble((Double.doubleToLongBits(a)<<1)>>>1)}
     *
     * @param   a   the argument whose absolute value is to be determined
     * @return  the absolute value of the argument.
     */
    public static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a;
    }

    /**
     * Returns the greater of two {@code int} values. That is, the
     * result is the argument closer to the value of
     * {@link Integer#MAX_VALUE}. If the arguments have the same value,
     * the result is that same value.
     * 返回两个 int值中的较大者。也就是说，结果是更接近 Integer.MAX_VALUE
     * 值的参数。如果参数具有相同的值，则结果是相同的值。
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the larger of {@code a} and {@code b}.
     */
    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }

    /**
     * Returns the greater of two {@code long} values. That is, the
     * result is the argument closer to the value of
     * {@link Long#MAX_VALUE}. If the arguments have the same value,
     * the result is that same value.
     * 返回两个long值中的较大者。也就是说，结果是更接近 Long.MAX_VALUE值的参数。
     * 如果参数具有相同的值，则结果是相同的值。
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the larger of {@code a} and {@code b}.
     */
    public static long max(long a, long b) {
        return (a >= b) ? a : b;
    }

    // Use raw bit-wise conversions on guaranteed non-NaN arguments.
    //对有保证的非 NaN 参数使用原始的按位转换
    private static long negativeZeroFloatBits  = Float.floatToRawIntBits(-0.0f);
    private static long negativeZeroDoubleBits = Double.doubleToRawLongBits(-0.0d);

    /**
     * Returns the greater of two {@code float} values.  That is,
     * the result is the argument closer to positive infinity. If the
     * arguments have the same value, the result is that same
     * value. If either value is NaN, then the result is NaN.  Unlike
     * the numerical comparison operators, this method considers
     * negative zero to be strictly smaller than positive zero. If one
     * argument is positive zero and the other negative zero, the
     * result is positive zero.
     * 返回两个 float值中的较大者。也就是说，结果是更接近正无穷大的论证。
     * 如果参数具有相同的值，则结果是相同的值。如果任一值为 NaN，则结果为 NaN。
     * 与数值比较运算符不同，此方法认为负零严格小于正零。如果一个参数为正零而另一个为负零，则结果为正零。
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the larger of {@code a} and {@code b}.
     */
    public static float max(float a, float b) {
        if (a != a)
            return a;   // a is NaN
        if ((a == 0.0f) &&
            (b == 0.0f) &&
            (Float.floatToRawIntBits(a) == negativeZeroFloatBits)) {
            // Raw conversion ok since NaN can't map to -0.0.
            return b;
        }
        return (a >= b) ? a : b;
    }

    /**
     * Returns the greater of two {@code double} values.  That
     * is, the result is the argument closer to positive infinity. If
     * the arguments have the same value, the result is that same
     * value. If either value is NaN, then the result is NaN.  Unlike
     * the numerical comparison operators, this method considers
     * negative zero to be strictly smaller than positive zero. If one
     * argument is positive zero and the other negative zero, the
     * result is positive zero.
     * 返回两个 double值中的较大者。也就是说，结果是更接近正无穷大的论证。
     * 如果参数具有相同的值，则结果是相同的值。如果任一值为 NaN，则结果为 NaN。
     * 与数值比较运算符不同，此方法认为负零严格小于正零。如果一个参数为正零而另一个为负零，则结果为正零。
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the larger of {@code a} and {@code b}.
     */
    public static double max(double a, double b) {
        if (a != a)
            return a;   // a is NaN
        if ((a == 0.0d) &&
            (b == 0.0d) &&
            (Double.doubleToRawLongBits(a) == negativeZeroDoubleBits)) {
            // Raw conversion ok since NaN can't map to -0.0.
            return b;
        }
        return (a >= b) ? a : b;
    }

    /**
     * Returns the smaller of two {@code int} values. That is,
     * the result the argument closer to the value of
     * {@link Integer#MIN_VALUE}.  If the arguments have the same
     * value, the result is that same value.
     * 返回两个int值中较小的一个。也就是说，
     * 结果参数更接近于 Integer.MIN_VALUE} 的值。
     * 如果参数具有相同的值，则结果是相同的值
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the smaller of {@code a} and {@code b}.
     */
    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two {@code long} values. That is,
     * the result is the argument closer to the value of
     * {@link Long#MIN_VALUE}. If the arguments have the same
     * value, the result is that same value.
     * 返回两个 long值中较小的一个。也就是说，结果是更接近 Long.MIN_VALUE值的参数。
     * 如果参数具有相同的值，则结果是相同的值
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the smaller of {@code a} and {@code b}.
     */
    public static long min(long a, long b) {
        return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two {@code float} values.  That is,
     * the result is the value closer to negative infinity. If the
     * arguments have the same value, the result is that same
     * value. If either value is NaN, then the result is NaN.  Unlike
     * the numerical comparison operators, this method considers
     * negative zero to be strictly smaller than positive zero.  If
     * one argument is positive zero and the other is negative zero,
     * the result is negative zero.
     * 返回两个 float值中较小的一个。也就是说，结果是更接近负无穷大的值。
     * 如果参数具有相同的值，则结果是相同的值。如果任一值为 NaN，则结果为 NaN。
     * 与数值比较运算符不同，此方法认为负零严格小于正零。如果一个参数为正零，另一个为负零，则结果为负零。
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the smaller of {@code a} and {@code b}.
     */
    public static float min(float a, float b) {
        if (a != a)
            return a;   // a is NaN
        if ((a == 0.0f) &&
            (b == 0.0f) &&
            (Float.floatToRawIntBits(b) == negativeZeroFloatBits)) {
            // Raw conversion ok since NaN can't map to -0.0.
            return b;
        }
        return (a <= b) ? a : b;
    }

    /**
     * Returns the smaller of two {@code double} values.  That
     * is, the result is the value closer to negative infinity. If the
     * arguments have the same value, the result is that same
     * value. If either value is NaN, then the result is NaN.  Unlike
     * the numerical comparison operators, this method considers
     * negative zero to be strictly smaller than positive zero. If one
     * argument is positive zero and the other is negative zero, the
     * result is negative zero.
     *  返回两个 double值中较小的一个。也就是说，结果是更接近负无穷大的值。
     *  如果参数具有相同的值，则结果是相同的值。如果任一值为 NaN，则结果为 NaN。
     *  与数值比较运算符不同，此方法认为负零严格小于正零。如果一个参数为正零，另一个为负零，则结果为负零。
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the smaller of {@code a} and {@code b}.
     */
    public static double min(double a, double b) {
        if (a != a)
            return a;   // a is NaN
        if ((a == 0.0d) &&
            (b == 0.0d) &&
            (Double.doubleToRawLongBits(b) == negativeZeroDoubleBits)) {
            // Raw conversion ok since NaN can't map to -0.0.
            return b;
        }
        return (a <= b) ? a : b;
    }

    /**
     * Returns the size of an ulp of the argument.  An ulp, unit in
     * the last place, of a {@code double} value is the positive
     * distance between this floating-point value and the {@code
     * double} value next larger in magnitude.  Note that for non-NaN
     * <i>x</i>, <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
     * 1.返回参数的 ulp 的大小。double值的最后一个单位 ulp 是此浮点值与数量级次大的 double值之间的正距离。
     * 请注意，对于非 NaN x，ulp(-x) == ulp(x)。
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, then the result is NaN.
     * <li> If the argument is positive or negative infinity, then the
     * result is positive infinity.
     * <li> If the argument is positive or negative zero, then the result is
     * {@code Double.MIN_VALUE}.
     * <li> If the argument is &plusmn;{@code Double.MAX_VALUE}, then
     * the result is equal to 2<sup>971</sup>.
     * </ul>
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为正无穷大或负无穷大，则结果为正无穷大。
     * 如果参数为正零或负零，则结果为 Double.MIN_VALUE。
     * 如果参数是±Double.MAX_VALUE，则结果等于2<sup>971<sup>
     * @param d the floating-point value whose ulp is to be returned
     * @return the size of an ulp of the argument
     * @author Joseph D. Darcy
     * @since 1.5
     */
    public static double ulp(double d) {
        int exp = getExponent(d);

        switch(exp) {
        case DoubleConsts.MAX_EXPONENT+1:       // NaN or infinity
            return Math.abs(d);

        case DoubleConsts.MIN_EXPONENT-1:       // zero or subnormal
            return Double.MIN_VALUE;

        default:
            assert exp <= DoubleConsts.MAX_EXPONENT && exp >= DoubleConsts.MIN_EXPONENT;

            // ulp(x) is usually 2^(SIGNIFICAND_WIDTH-1)*(2^ilogb(x))
           // ulp(x) 通常是 2^(SIGNIFICAND_WIDTH-1)(2^ilogb(x))
            exp = exp - (DoubleConsts.SIGNIFICAND_WIDTH-1);
            if (exp >= DoubleConsts.MIN_EXPONENT) {
                return powerOfTwoD(exp);
            }
            else {
                // return a subnormal result; left shift integer
                // representation of Double.MIN_VALUE appropriate
                // number of positions
                return Double.longBitsToDouble(1L <<
                (exp - (DoubleConsts.MIN_EXPONENT - (DoubleConsts.SIGNIFICAND_WIDTH-1)) ));
            }
        }
    }

    /**
     * Returns the size of an ulp of the argument.  An ulp, unit in
     * the last place, of a {@code float} value is the positive
     * distance between this floating-point value and the {@code
     * float} value next larger in magnitude.  Note that for non-NaN
     * <i>x</i>, <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
     * 1.返回参数的 ulp 的大小。 float值的最后一个单位 ulp 是该浮点值与数量级次大的float值之间的正距离。
     * 请注意，对于非 NaN x，ulp(-x) == ulp(x)。
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, then the result is NaN.
     * <li> If the argument is positive or negative infinity, then the
     * result is positive infinity.
     * <li> If the argument is positive or negative zero, then the result is
     * {@code Float.MIN_VALUE}.
     * <li> If the argument is &plusmn;{@code Float.MAX_VALUE}, then
     * the result is equal to 2<sup>104</sup>.
     * </ul>
     * 2.特殊情况： <ul> <li> 如果参数为 NaN，则结果为 NaN。 <li> 如果参数为正无穷大或负无穷大，则结果为正无穷大。
     * 如果参数为正零或负零，则结果为 Float.MIN_VALUE。
     * 如果参数为±Float.MAX_VALUE，则结果等于2<sup>104<sup>。
     * @param f the floating-point value whose ulp is to be returned
     * @return the size of an ulp of the argument
     * @author Joseph D. Darcy
     * @since 1.5
     */
    public static float ulp(float f) {
        int exp = getExponent(f);

        switch(exp) {
        case FloatConsts.MAX_EXPONENT+1:        // NaN or infinity
            return Math.abs(f);

        case FloatConsts.MIN_EXPONENT-1:        // zero or subnormal
            return FloatConsts.MIN_VALUE;

        default:
            assert exp <= FloatConsts.MAX_EXPONENT && exp >= FloatConsts.MIN_EXPONENT;

            // ulp(x) is usually 2^(SIGNIFICAND_WIDTH-1)*(2^ilogb(x))
            exp = exp - (FloatConsts.SIGNIFICAND_WIDTH-1);
            if (exp >= FloatConsts.MIN_EXPONENT) {
                return powerOfTwoF(exp);
            }
            else {
                // return a subnormal result; left shift integer
                // representation of FloatConsts.MIN_VALUE appropriate
                // number of positions
                return Float.intBitsToFloat(1 <<
                (exp - (FloatConsts.MIN_EXPONENT - (FloatConsts.SIGNIFICAND_WIDTH-1)) ));
            }
        }
    }

    /**
     * Returns the signum function of the argument; zero if the argument
     * is zero, 1.0 if the argument is greater than zero, -1.0 if the
     * argument is less than zero.
     * 1.返回参数的符号函数；如果参数为零，则为零，如果参数大于零，则为 1.0，如果参数小于零，则为 -1.0。
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, then the result is NaN.
     * <li> If the argument is positive zero or negative zero, then the
     *      result is the same as the argument.
     * </ul>
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为正零或负零，则结果与参数相同。
     * @param d the floating-point value whose signum is to be returned
     * @return the signum function of the argument
     * @author Joseph D. Darcy
     * @since 1.5
     */
    public static double signum(double d) {
        return (d == 0.0 || Double.isNaN(d))?d:copySign(1.0, d);
    }

    /**
     * Returns the signum function of the argument; zero if the argument
     * is zero, 1.0f if the argument is greater than zero, -1.0f if the
     * argument is less than zero.
     * 1.返回参数的符号函数；如果参数为零，则为零，如果参数大于零，则为 1.0f，如果参数小于零，则为 -1.0f。
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, then the result is NaN.
     * <li> If the argument is positive zero or negative zero, then the
     *      result is the same as the argument.
     * </ul>
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为正零或负零，则结果与参数相同。
     * @param f the floating-point value whose signum is to be returned
     * @return the signum function of the argument
     * @author Joseph D. Darcy
     * @since 1.5
     */
    public static float signum(float f) {
        return (f == 0.0f || Float.isNaN(f))?f:copySign(1.0f, f);
    }

    /**
     * Returns the hyperbolic sine of a {@code double} value.
     * The hyperbolic sine of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/2
     * where <i>e</i> is {@linkplain Math#E Euler's number}.
     * 1.返回  double 值的双曲正弦值。
     * 的双曲正弦定义为 (e<sup>x<sup> - e<sup>-x<sup>)2 其中 e是 plain MathE Euler 数。
     * <p>Special cases:
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is an infinity
     * with the same sign as the argument.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数是无穷大，则结果是与参数同号的无穷大。
     * 如果参数为零，则结果为与参数相同符号的零。
     * </ul>
     *
     * <p>The computed result must be within 2.5 ulps of the exact result.
     * 3.计算结果必须在准确结果的 2.5 uls 以内。
     * @param   x The number whose hyperbolic sine is to be returned.
     * @return  The hyperbolic sine of {@code x}.
     * @since 1.5
     */
    public static double sinh(double x) {
        return StrictMath.sinh(x);
    }

    /**
     * Returns the hyperbolic cosine of a {@code double} value.
     * The hyperbolic cosine of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>)/2
     * where <i>e</i> is {@linkplain Math#E Euler's number}.
     * 1.返回 double值的双曲余弦。
     * x的双曲余弦定义为 (e<sup>x<sup> + e<sup>-x<sup>)2 其中 e是plain MathE Euler 数。
     * <p>Special cases:
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is infinite, then the result is positive
     * infinity.
     *
     * <li>If the argument is zero, then the result is {@code 1.0}.
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为无穷大，则结果为正无穷大。
     * 如果参数为零，则结果为  1.0。
     * </ul>
     *
     * <p>The computed result must be within 2.5 ulps of the exact result.
     * 3.计算结果必须在准确结果的 2.5 uls 以内。
     * @param   x The number whose hyperbolic cosine is to be returned.
     * @return  The hyperbolic cosine of {@code x}.
     * @since 1.5
     */
    public static double cosh(double x) {
        return StrictMath.cosh(x);
    }

    /**
     * Returns the hyperbolic tangent of a {@code double} value.
     * The hyperbolic tangent of <i>x</i> is defined to be
     * (<i>e<sup>x</sup>&nbsp;-&nbsp;e<sup>-x</sup></i>)/(<i>e<sup>x</sup>&nbsp;+&nbsp;e<sup>-x</sup></i>),
     * in other words, {@linkplain Math#sinh
     * sinh(<i>x</i>)}/{@linkplain Math#cosh cosh(<i>x</i>)}.  Note
     * that the absolute value of the exact tanh is always less than
     * 1.
     * 1.返回 double值的双曲正切值。 x的双曲正切定义为
     * (e<sup>x<sup> - e<sup>-x<sup>)/(e<sup>x <sup> + e<sup>-x<sup>)，
     * 换句话说，plain Math.sinh sinh(x)/plain Math.cosh cosh(x )}。请注意，精确 tanh 的绝对值始终小于 1。
     * <p>Special cases:
     * <ul>
     *
     * <li>If the argument is NaN, then the result is NaN.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     *
     * <li>If the argument is positive infinity, then the result is
     * {@code +1.0}.
     *
     * <li>If the argument is negative infinity, then the result is
     * {@code -1.0}.
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为零，则结果为与参数相同符号的零。
     * 如果参数为正无穷大，则结果为 +1.0。
     * 如果参数为负无穷大，则结果为 -1.0。
     * </ul>
     *
     * <p>The computed result must be within 2.5 ulps of the exact result.
     * The result of {@code tanh} for any finite input must have
     * an absolute value less than or equal to 1.  Note that once the
     * exact result of tanh is within 1/2 of an ulp of the limit value
     * of &plusmn;1, correctly signed &plusmn;{@code 1.0} should
     * be returned.
     * 3.计算结果必须在准确结果的 2.5 uls 以内。 tanh对于任何有限输入的结果必须具有小于或等于 1 的绝对值。
     * 请注意，一旦 tanh 的确切结果在 ±1 的极限值的 ulp 的 12 以内，则正确签名 ±{应该返回 1.0。
     * @param   x The number whose hyperbolic tangent is to be returned.
     * @return  The hyperbolic tangent of {@code x}.
     * @since 1.5
     */
    public static double tanh(double x) {
        return StrictMath.tanh(x);
    }

    /**
     * Returns sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
     * without intermediate overflow or underflow.
     *1.返回 sqrt(x<<sup>2<sup> +y<sup>2<sup>) 没有中间溢出或下溢
     * <p>Special cases:
     * <ul>
     *
     * <li> If either argument is infinite, then the result
     * is positive infinity.
     *
     * <li> If either argument is NaN and neither argument is infinite,
     * then the result is NaN.
     * 2.特殊情况：
     * 如果任一参数为无穷大，则结果为正无穷大。
     * 如果任一参数为 NaN 且两个参数都不是无限的，则结果为 NaN。
     * </ul>
     *
     * <p>The computed result must be within 1 ulp of the exact
     * result.  If one parameter is held constant, the results must be
     * semi-monotonic in the other parameter.
     * 3.计算结果必须在精确结果的 1 ulp 以内。如果一个参数保持不变，则结果在另一个参数中必须是半单调的
     * @param x a value
     * @param y a value
     * @return sqrt(<i>x</i><sup>2</sup>&nbsp;+<i>y</i><sup>2</sup>)
     * without intermediate overflow or underflow
     * @since 1.5
     */
    public static double hypot(double x, double y) {
        return StrictMath.hypot(x, y);
    }

    /**
     * Returns <i>e</i><sup>x</sup>&nbsp;-1.  Note that for values of
     * <i>x</i> near 0, the exact sum of
     * {@code expm1(x)}&nbsp;+&nbsp;1 is much closer to the true
     * result of <i>e</i><sup>x</sup> than {@code exp(x)}.
     * 1.返回 e<sup>x<sup> -1。请注意，对于接近 0 的 x值，
     *  expm1(x) + 1 的精确总和更接近于e<sup>x<sup 的真实结果> 比  exp(x)
     * <p>Special cases:
     * <ul>
     * <li>If the argument is NaN, the result is NaN.
     *
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     *
     * <li>If the argument is negative infinity, then the result is
     * -1.0.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为正无穷大，则结果为正无穷大。
     * 如果参数为负无穷大，则结果为 -1.0。
     * 如果参数为零，则结果为与参数相同符号的零。
     * </ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.  The result of
     * {@code expm1} for any finite input must be greater than or
     * equal to {@code -1.0}.  Note that once the exact result of
     * <i>e</i><sup>{@code x}</sup>&nbsp;-&nbsp;1 is within 1/2
     * ulp of the limit value -1, {@code -1.0} should be
     * returned.
     * 3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的。
     * expm1对于任何有限输入的结果必须大于或等于-1.0。
     * 请注意，一旦 e<sup> x<sup> - 1 的确切结果在限制值 -1 的 12 ulp 内，则应返回  -1.0。
     * @param   x   the exponent to raise <i>e</i> to in the computation of
     *              <i>e</i><sup>{@code x}</sup>&nbsp;-1.
     * @return  the value <i>e</i><sup>{@code x}</sup>&nbsp;-&nbsp;1.
     * @since 1.5
     */
    public static double expm1(double x) {
        return StrictMath.expm1(x);
    }

    /**
     * Returns the natural logarithm of the sum of the argument and 1.
     * Note that for small values {@code x}, the result of
     * {@code log1p(x)} is much closer to the true result of ln(1
     * + x) than the floating-point evaluation of
     * {@code log(1.0+x)}.
     * 1.返回参数和 1 之和的自然对数。请注意，对于小值  x，log1p(x) 的结果 更接近ln(1 + x) 的真实结果)
     * 比 log(1.0+x)}的浮点计算
     * <p>Special cases:
     *
     * <ul>
     *
     * <li>If the argument is NaN or less than -1, then the result is
     * NaN.
     *
     * <li>If the argument is positive infinity, then the result is
     * positive infinity.
     *
     * <li>If the argument is negative one, then the result is
     * negative infinity.
     *
     * <li>If the argument is zero, then the result is a zero with the
     * same sign as the argument.
     * 2.特殊情况：
     * 如果参数为 NaN 或小于 -1，则结果为 NaN。
     * 如果参数为正无穷大，则结果为正无穷大。
     * 如果参数为负一，则结果为负无穷大。
     * 如果参数为零，则结果为与参数相同符号的零。
     * </ul>
     *
     * <p>The computed result must be within 1 ulp of the exact result.
     * Results must be semi-monotonic.
     *3.计算结果必须在精确结果的 1 ulp 以内。结果必须是半单调的。
     * @param   x   a value
     * @return the value ln({@code x}&nbsp;+&nbsp;1), the natural
     * log of {@code x}&nbsp;+&nbsp;1
     * @since 1.5
     */
    public static double log1p(double x) {
        return StrictMath.log1p(x);
    }

    /**
     * Returns the first floating-point argument with the sign of the
     * second floating-point argument.  Note that unlike the {@link
     * StrictMath#copySign(double, double) StrictMath.copySign}
     * method, this method does not require NaN {@code sign}
     * arguments to be treated as positive values; implementations are
     * permitted to treat some NaN arguments as positive and other NaN
     * arguments as negative to allow greater performance.
     * 返回带有第二个浮点参数符号的第一个浮点参数。
     * 请注意，与  StrictMath.copySign(double, double) 方法不同，
     * 此方法不需要将 NaN 的 sign参数视为正值；允许实现将某些 NaN 参数视为正数，将其他 NaN 参数视为负数，以提高性能。
     * @param magnitude  the parameter providing the magnitude of the result
     * @param sign   the parameter providing the sign of the result
     * @return a value with the magnitude of {@code magnitude}
     * and the sign of {@code sign}.
     * @since 1.6
     */
    public static double copySign(double magnitude, double sign) {
        return Double.longBitsToDouble((Double.doubleToRawLongBits(sign) &
                                        (DoubleConsts.SIGN_BIT_MASK)) |
                                       (Double.doubleToRawLongBits(magnitude) &
                                        (DoubleConsts.EXP_BIT_MASK |
                                         DoubleConsts.SIGNIF_BIT_MASK)));
    }

    /**
     * Returns the first floating-point argument with the sign of the
     * second floating-point argument.  Note that unlike the {@link
     * StrictMath#copySign(float, float) StrictMath.copySign}
     * method, this method does not require NaN {@code sign}
     * arguments to be treated as positive values; implementations are
     * permitted to treat some NaN arguments as positive and other NaN
     * arguments as negative to allow greater performance.
     * 返回带有第二个浮点参数符号的第一个浮点参数。
     * 请注意，与 StrictMath.copySign(float, float)方法不同，
     * 此方法不需要将 NaN 的sign参数视为正值；允许实现将某些 NaN 参数视为正数，将其他 NaN 参数视为负数，以提高性能。
     * @param magnitude  the parameter providing the magnitude of the result
     * @param sign   the parameter providing the sign of the result
     * @return a value with the magnitude of {@code magnitude}
     * and the sign of {@code sign}.
     * @since 1.6
     */
    public static float copySign(float magnitude, float sign) {
        return Float.intBitsToFloat((Float.floatToRawIntBits(sign) &
                                     (FloatConsts.SIGN_BIT_MASK)) |
                                    (Float.floatToRawIntBits(magnitude) &
                                     (FloatConsts.EXP_BIT_MASK |
                                      FloatConsts.SIGNIF_BIT_MASK)));
    }

    /**
     * Returns the unbiased exponent used in the representation of a
     * {@code float}.  Special cases:
     * 1.返回用于表示 float的无偏指数。
     * 2.特殊情况:
     *  如果参数为 NaN 或无穷大，则结果为 Float.MAX_EXPONENT} + 1。
     *  如果参数为零或次正规，则结果为  Float.MIN_EXPONENT} -1。
     * <ul>
     * <li>If the argument is NaN or infinite, then the result is
     * {@link Float#MAX_EXPONENT} + 1.
     * <li>If the argument is zero or subnormal, then the result is
     * {@link Float#MIN_EXPONENT} -1.
     * </ul>
     * @param f a {@code float} value
     * @return the unbiased exponent of the argument
     * @since 1.6
     */
    public static int getExponent(float f) {
        /*
         * Bitwise convert f to integer, mask out exponent bits, shift
         * to the right and then subtract out float's bias adjust to
         * get true exponent value
         */
        return ((Float.floatToRawIntBits(f) & FloatConsts.EXP_BIT_MASK) >>
                (FloatConsts.SIGNIFICAND_WIDTH - 1)) - FloatConsts.EXP_BIAS;
    }

    /**
     * Returns the unbiased exponent used in the representation of a
     * {@code double}.  Special cases:
     * 1.返回用于表示 double的无偏指数
     * 2.特殊情况:
     * 如果参数为 NaN 或无穷大，则结果为Double.MAX_EXPONENT + 1。
     * 如果参数为零或次正规，则结果为 Double.MIN_EXPONENT} -1。
     *
     * <ul>
     * <li>If the argument is NaN or infinite, then the result is
     * {@link Double#MAX_EXPONENT} + 1.
     * <li>If the argument is zero or subnormal, then the result is
     * {@link Double#MIN_EXPONENT} -1.
     * </ul>
     * @param d a {@code double} value
     * @return the unbiased exponent of the argument
     * @since 1.6
     */
    public static int getExponent(double d) {
        /*
         * Bitwise convert d to long, mask out exponent bits, shift
         * to the right and then subtract out double's bias adjust to
         * get true exponent value.
         */
        return (int)(((Double.doubleToRawLongBits(d) & DoubleConsts.EXP_BIT_MASK) >>
                      (DoubleConsts.SIGNIFICAND_WIDTH - 1)) - DoubleConsts.EXP_BIAS);
    }

    /**
     * Returns the floating-point number adjacent to the first
     * argument in the direction of the second argument.  If both
     * arguments compare as equal the second argument is returned.
     * 1.返回在第二个参数的方向上与第一个参数相邻的浮点数。如果两个参数比较相等，则返回第二个参数
     * <p>
     * Special cases:
     * <ul>
     * <li> If either argument is a NaN, then NaN is returned.
     * 2.特殊情况:
     *  1)如果任一参数是 NaN，则返回 NaN
     * <li> If both arguments are signed zeros, {@code direction}
     * is returned unchanged (as implied by the requirement of
     * returning the second argument if the arguments compare as
     * equal).
     * 2)如果两个参数都是有符号的零，则 direction原样返回（如参数比较相等时返回第二个参数的要求所暗示）。
     * <li> If {@code start} is
     * &plusmn;{@link Double#MIN_VALUE} and {@code direction}
     * has a value such that the result should have a smaller
     * magnitude, then a zero with the same sign as {@code start}
     * is returned.
     * 3)如果  start是 ± Double.MIN_VALUE并且  direction的值使得结果应该具有较小的幅度，则返回与 start符号相同的零
     * <li> If {@code start} is infinite and
     * {@code direction} has a value such that the result should
     * have a smaller magnitude, {@link Double#MAX_VALUE} with the
     * same sign as {@code start} is returned.
     * 4)如果 start是无限的，并且direction的值使得结果应该具有较小的幅度，
     * 则返回与  start符号相同的 Double.MAX_VALUE
     * <li> If {@code start} is equal to &plusmn;
     * {@link Double#MAX_VALUE} and {@code direction} has a
     * value such that the result should have a larger magnitude, an
     * infinity with same sign as {@code start} is returned.
     * </ul>
     * 5)如果 start等于 ± Double.MAX_VALUE并且 direction的值使得结果应该具有更大的幅度，
     * 则返回与start符号相同的无穷大。
     * @param start  starting floating-point value
     * @param direction value indicating which of
     * {@code start}'s neighbors or {@code start} should
     * be returned
     * @return The floating-point number adjacent to {@code start} in the
     * direction of {@code direction}.
     * @since 1.6
     */
    public static double nextAfter(double start, double direction) {
        /*
         * The cases:
         *
         * nextAfter(+infinity, 0)  == MAX_VALUE
         * nextAfter(+infinity, +infinity)  == +infinity
         * nextAfter(-infinity, 0)  == -MAX_VALUE
         * nextAfter(-infinity, -infinity)  == -infinity
         *
         * are naturally handled without any additional testing
         */

        // First check for NaN values
        if (Double.isNaN(start) || Double.isNaN(direction)) {
            // return a NaN derived from the input NaN(s)
            return start + direction;
        } else if (start == direction) {
            return direction;
        } else {        // start > direction or start < direction
            // Add +0.0 to get rid of a -0.0 (+0.0 + -0.0 => +0.0)
            // then bitwise convert start to integer.
            long transducer = Double.doubleToRawLongBits(start + 0.0d);

            /*
             * IEEE 754 floating-point numbers are lexicographically
             * ordered if treated as signed- magnitude integers .
             * Since Java's integers are two's complement,
             * incrementing" the two's complement representation of a
             * logically negative floating-point value *decrements*
             * the signed-magnitude representation. Therefore, when
             * the integer representation of a floating-point values
             * is less than zero, the adjustment to the representation
             * is in the opposite direction than would be expected at
             * first .
             * 如果将 IEEE 754 浮点数视为有符号整数，则它们按字典顺序排列。
             * 由于Java的整数是二进制补码，增加“逻辑负浮点值的二进制补码表示会减少有符号数表示。
             * 因此，当浮点值的整数表示小于零时，对表示的调整与最初预期的方向相反
             */
            if (direction > start) { // Calculate next greater value
                transducer = transducer + (transducer >= 0L ? 1L:-1L);
            } else  { // Calculate next lesser value
                assert direction < start;
                if (transducer > 0L)
                    --transducer;
                else
                    if (transducer < 0L )
                        ++transducer;
                    /*
                     * transducer==0, the result is -MIN_VALUE
                     *
                     * The transition from zero (implicitly
                     * positive) to the smallest negative
                     * signed magnitude value must be done
                     * explicitly.
                     * 传感器==0，结果为-MIN_VALUE 必须明确完成从零（隐式正）到最小负符号幅度值的转换。
                     */
                    else
                        transducer = DoubleConsts.SIGN_BIT_MASK | 1L;
            }

            return Double.longBitsToDouble(transducer);
        }
    }

    /**
     * Returns the floating-point number adjacent to the first
     * argument in the direction of the second argument.  If both
     * arguments compare as equal a value equivalent to the second argument
     * is returned.
     * 返回在第二个参数的方向上与第一个参数相邻的浮点数。如果两个参数比较相等，则返回与第二个参数等效的值。
     * <p>
     * Special cases:
     * <ul>
     * <li> If either argument is a NaN, then NaN is returned.
     *
     * <li> If both arguments are signed zeros, a value equivalent
     * to {@code direction} is returned.
     *
     * <li> If {@code start} is
     * &plusmn;{@link Float#MIN_VALUE} and {@code direction}
     * has a value such that the result should have a smaller
     * magnitude, then a zero with the same sign as {@code start}
     * is returned.
     *
     * <li> If {@code start} is infinite and
     * {@code direction} has a value such that the result should
     * have a smaller magnitude, {@link Float#MAX_VALUE} with the
     * same sign as {@code start} is returned.
     *
     * <li> If {@code start} is equal to &plusmn;
     * {@link Float#MAX_VALUE} and {@code direction} has a
     * value such that the result should have a larger magnitude, an
     * infinity with same sign as {@code start} is returned.
     * </ul>
     *
     * @param start  starting floating-point value
     * @param direction value indicating which of
     * {@code start}'s neighbors or {@code start} should
     * be returned
     * @return The floating-point number adjacent to {@code start} in the
     * direction of {@code direction}.
     * @since 1.6
     */
    public static float nextAfter(float start, double direction) {
        /*
         * The cases:
         *
         * nextAfter(+infinity, 0)  == MAX_VALUE
         * nextAfter(+infinity, +infinity)  == +infinity
         * nextAfter(-infinity, 0)  == -MAX_VALUE
         * nextAfter(-infinity, -infinity)  == -infinity
         *
         * are naturally handled without any additional testing
         */

        // First check for NaN values
        if (Float.isNaN(start) || Double.isNaN(direction)) {
            // return a NaN derived from the input NaN(s)
            return start + (float)direction;
        } else if (start == direction) {
            return (float)direction;
        } else {        // start > direction or start < direction
            // Add +0.0 to get rid of a -0.0 (+0.0 + -0.0 => +0.0)
            // then bitwise convert start to integer.
            int transducer = Float.floatToRawIntBits(start + 0.0f);

            /*
             * IEEE 754 floating-point numbers are lexicographically
             * ordered if treated as signed- magnitude integers .
             * Since Java's integers are two's complement,
             * incrementing" the two's complement representation of a
             * logically negative floating-point value *decrements*
             * the signed-magnitude representation. Therefore, when
             * the integer representation of a floating-point values
             * is less than zero, the adjustment to the representation
             * is in the opposite direction than would be expected at
             * first.
             */
            if (direction > start) {// Calculate next greater value
                transducer = transducer + (transducer >= 0 ? 1:-1);
            } else  { // Calculate next lesser value
                assert direction < start;
                if (transducer > 0)
                    --transducer;
                else
                    if (transducer < 0 )
                        ++transducer;
                    /*
                     * transducer==0, the result is -MIN_VALUE
                     *
                     * The transition from zero (implicitly
                     * positive) to the smallest negative
                     * signed magnitude value must be done
                     * explicitly.
                     */
                    else
                        transducer = FloatConsts.SIGN_BIT_MASK | 1;
            }

            return Float.intBitsToFloat(transducer);
        }
    }

    /**
     * Returns the floating-point value adjacent to {@code d} in
     * the direction of positive infinity.  This method is
     * semantically equivalent to {@code nextAfter(d,
     * Double.POSITIVE_INFINITY)}; however, a {@code nextUp}
     * implementation may run faster than its equivalent
     * {@code nextAfter} call.
     * 1.返回在正无穷大方向上与 d相邻的浮点值。这个方法在语义上等同于 nextAfter(d, Double.POSITIVE_INFINITY)
     * ;但是，nextUp实现可能比其等效的 nextAfter调用运行得更快。
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, the result is NaN.
     *
     * <li> If the argument is positive infinity, the result is
     * positive infinity.
     *
     * <li> If the argument is zero, the result is
     * {@link Double#MIN_VALUE}
     *2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为正无穷大，则结果为正无穷大。
     * 如果参数为零，则结果为 Double.MIN_VALUE
     * </ul>
     *
     * @param d starting floating-point value
     * @return The adjacent floating-point value closer to positive
     * infinity.
     * @since 1.6
     */
    public static double nextUp(double d) {
        if( Double.isNaN(d) || d == Double.POSITIVE_INFINITY)
            return d;
        else {
            d += 0.0d;
            return Double.longBitsToDouble(Double.doubleToRawLongBits(d) +
                                           ((d >= 0.0d)?+1L:-1L));
        }
    }

    /**
     * Returns the floating-point value adjacent to {@code f} in
     * the direction of positive infinity.  This method is
     * semantically equivalent to {@code nextAfter(f,
     * Float.POSITIVE_INFINITY)}; however, a {@code nextUp}
     * implementation may run faster than its equivalent
     * {@code nextAfter} call.
     * 1.返回在正无穷大方向上与 f相邻的浮点值。这个方法在语义上等价于 nextAfter(f, Float.POSITIVE_INFINITY)};
     * 但是， nextUp实现可能比其等效的nextAfter调用运行得更快。
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, the result is NaN.
     *
     * <li> If the argument is positive infinity, the result is
     * positive infinity.
     *
     * <li> If the argument is zero, the result is
     * {@link Float#MIN_VALUE}
     *
     * </ul>
     *
     * @param f starting floating-point value
     * @return The adjacent floating-point value closer to positive
     * infinity.
     * @since 1.6
     */
    public static float nextUp(float f) {
        if( Float.isNaN(f) || f == FloatConsts.POSITIVE_INFINITY)
            return f;
        else {
            f += 0.0f;
            return Float.intBitsToFloat(Float.floatToRawIntBits(f) +
                                        ((f >= 0.0f)?+1:-1));
        }
    }

    /**
     * Returns the floating-point value adjacent to {@code d} in
     * the direction of negative infinity.  This method is
     * semantically equivalent to {@code nextAfter(d,
     * Double.NEGATIVE_INFINITY)}; however, a
     * {@code nextDown} implementation may run faster than its
     * equivalent {@code nextAfter} call.
     * 1.返回负无穷大方向与 d相邻的浮点值。这个方法在语义上等同于 nextAfter(d, Double.NEGATIVE_INFINITY);
     * 但是，nextDown实现可能比其等效的 nextAfter调用运行得更快。
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, the result is NaN.
     *
     * <li> If the argument is negative infinity, the result is
     * negative infinity.
     *
     * <li> If the argument is zero, the result is
     * {@code -Double.MIN_VALUE}
     *
     * </ul>
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为负无穷大，则结果为负无穷大。
     * 如果参数为零，则结果为 -Double.MIN_VALUE
     * @param d  starting floating-point value
     * @return The adjacent floating-point value closer to negative
     * infinity.
     * @since 1.8
     */
    public static double nextDown(double d) {
        if (Double.isNaN(d) || d == Double.NEGATIVE_INFINITY)
            return d;
        else {
            if (d == 0.0)
                return -Double.MIN_VALUE;
            else
                return Double.longBitsToDouble(Double.doubleToRawLongBits(d) +
                                               ((d > 0.0d)?-1L:+1L));
        }
    }

    /**
     * Returns the floating-point value adjacent to {@code f} in
     * the direction of negative infinity.  This method is
     * semantically equivalent to {@code nextAfter(f,
     * Float.NEGATIVE_INFINITY)}; however, a
     * {@code nextDown} implementation may run faster than its
     * equivalent {@code nextAfter} call.
     * 1.返回负无穷大方向与 f相邻的浮点值。这个方法在语义上等同于 nextAfter(f, Float.NEGATIVE_INFINITY)
     * ;但是，nextDown实现可能比其等效的 nextAfter调用运行得更快。
     * <p>Special Cases:
     * <ul>
     * <li> If the argument is NaN, the result is NaN.
     *
     * <li> If the argument is negative infinity, the result is
     * negative infinity.
     *
     * <li> If the argument is zero, the result is
     * {@code -Float.MIN_VALUE}
     * 2.特殊情况：
     * 如果参数为 NaN，则结果为 NaN。
     * 如果参数为负无穷大，则结果为负无穷大。
     * 如果参数为零，则结果为  -Float.MIN_VALUE
     * </ul>
     *
     * @param f  starting floating-point value
     * @return The adjacent floating-point value closer to negative
     * infinity.
     * @since 1.8
     */
    public static float nextDown(float f) {
        if (Float.isNaN(f) || f == Float.NEGATIVE_INFINITY)
            return f;
        else {
            if (f == 0.0f)
                return -Float.MIN_VALUE;
            else
                return Float.intBitsToFloat(Float.floatToRawIntBits(f) +
                                            ((f > 0.0f)?-1:+1));
        }
    }

    /**
     * Returns {@code d} &times;
     * 2<sup>{@code scaleFactor}</sup> rounded as if performed
     * by a single correctly rounded floating-point multiply to a
     * member of the double value set.  See the Java
     * Language Specification for a discussion of floating-point
     * value sets.  If the exponent of the result is between {@link
     * Double#MIN_EXPONENT} and {@link Double#MAX_EXPONENT}, the
     * answer is calculated exactly.  If the exponent of the result
     * would be larger than {@code Double.MAX_EXPONENT}, an
     * infinity is returned.  Note that if the result is subnormal,
     * precision may be lost; that is, when {@code scalb(x, n)}
     * is subnormal, {@code scalb(scalb(x, n), -n)} may not equal
     * <i>x</i>.  When the result is non-NaN, the result has the same
     * sign as {@code d}.
     * 1.返回d × 2<sup> scaleFactor<sup> 四舍五入，就像通过单个正确四舍五入的浮点乘法执行到 double 值集的成员一样。
     * 有关浮点值集的讨论，请参阅 Java 语言规范。如果结果的指数在 Double.MIN_EXPONENT
     * 和  Double.MAX_EXPONENT之间，则计算结果准确。如果结果的指数大于Double.MAX_EXPONENT，则返回无穷大。
     * 请注意，如果结果不正常，则可能会丢失精度；也就是说，当 scalb(x, n)是次正规时，
     * scalb(scalb(x, n), -n)可能不等于x。当结果为非 NaN 时，结果与d符号相同
     * <p>Special cases:
     * <ul>
     * <li> If the first argument is NaN, NaN is returned.
     * <li> If the first argument is infinite, then an infinity of the
     * same sign is returned.
     * <li> If the first argument is zero, then a zero of the same
     * sign is returned.
     * </ul>
     * 2.特殊情况：
     * 如果第一个参数是 NaN，则返回 NaN。
     * 如果第一个参数是无穷大，则返回相同符号的无穷大。
     * 如果第一个参数为零，则返回相同符号的零
     * @param d number to be scaled by a power of two.
     * @param scaleFactor power of 2 used to scale {@code d}
     * @return {@code d} &times; 2<sup>{@code scaleFactor}</sup>
     * @since 1.6
     */
    public static double scalb(double d, int scaleFactor) {
        /*
         * This method does not need to be declared strictfp to
         * compute the same correct result on all platforms.  When
         * scaling up, it does not matter what order the
         * multiply-store operations are done; the result will be
         * finite or overflow regardless of the operation ordering.
         * However, to get the correct result when scaling down, a
         * particular ordering must be used.
         * 1.此方法不需要声明为 strictfp 即可在所有平台上计算出相同的正确结果。
         * 扩展时，乘法存储操作的执行顺序无关紧要；无论操作顺序如何，结果都将是有限的或溢出。
         * 但是，要在缩小时获得正确的结果，必须使用特定的排序
         * When scaling down, the multiply-store operations are
         * sequenced so that it is not possible for two consecutive
         * multiply-stores to return subnormal results.  If one
         * multiply-store result is subnormal, the next multiply will
         * round it away to zero.  This is done by first multiplying
         * by 2 ^ (scaleFactor % n) and then multiplying several
         * times by by 2^n as needed where n is the exponent of number
         * that is a covenient power of two.  In this way, at most one
         * real rounding error occurs.  If the double value set is
         * being used exclusively, the rounding will occur on a
         * multiply.  If the double-extended-exponent value set is
         * being used, the products will (perhaps) be exact but the
         * stores to d are guaranteed to round to the double value
         * set.
         * 2.当按比例缩小时，乘法存储操作是有序的，因此两个连续的乘法存储不可能返回次正规结果。
         * 如果一个乘法存储结果不正常，则下一个乘法会将其舍入为零。
         * 这是通过首先乘以 2 ^ (scaleFactor % n) 然后根据需要多次乘以 2^n 来完成的，
         * 其中 n 是数字的指数，它是 2 的幂次方。这样，最多出现一个真正的舍入误差。
         * 如果专门使用双精度值集，则乘法会进行舍入。如果使用双扩展指数值集，则乘积（可能）是精确的，
         * 但 d 的存储保证四舍五入到双值集
         * It is _not_ a valid implementation to first multiply d by
         * 2^MIN_EXPONENT and then by 2 ^ (scaleFactor %
         * MIN_EXPONENT) since even in a strictfp program double
         * rounding on underflow could occur; e.g. if the scaleFactor
         * argument was (MIN_EXPONENT - n) and the exponent of d was a
         * little less than -(MIN_EXPONENT - n), meaning the final
         * result would be subnormal.
         * 3.首先将 d 乘以 2^MIN_EXPONENT 然后乘以 2 ^ (scaleFactor % MIN_EXPONENT) _不是_一个有效的实现，
         * 因为即使在严格的程序中也可能发生双舍入下溢；例如如果 scaleFactor 参数为 (MIN_EXPONENT - n)
         * 并且 d 的指数略小于 -(MIN_EXPONENT - n)，则意味着最终结果将低于正常值。
         * Since exact reproducibility of this method can be achieved
         * without any undue performance burden, there is no
         * compelling reason to allow double rounding on underflow in
         * scalb.
         * 由于可以在没有任何过度性能负担的情况下实现此方法的精确再现性，因此没有令人信服的理由允许对 scalb 中的下溢进行双舍入。
         */

        // magnitude of a power of two so large that scaling a finite
        // nonzero value by it would be guaranteed to over or
        // underflow; due to rounding, scaling down takes takes an
        // additional power of two which is reflected here
        //2 的幂的大小如此之大，以至于通过它缩放有限的非零值将保证溢出或下溢；
        // 由于四舍五入，按比例缩小需要额外的 2 次方，这反映在此处
        final int MAX_SCALE = DoubleConsts.MAX_EXPONENT + -DoubleConsts.MIN_EXPONENT +
                              DoubleConsts.SIGNIFICAND_WIDTH + 1;
        int exp_adjust = 0;
        int scale_increment = 0;
        double exp_delta = Double.NaN;

        // Make sure scaling factor is in a reasonable range
        //确保缩放因子在合理范​​围内

        if(scaleFactor < 0) {
            scaleFactor = Math.max(scaleFactor, -MAX_SCALE);
            scale_increment = -512;
            exp_delta = twoToTheDoubleScaleDown;
        }
        else {
            scaleFactor = Math.min(scaleFactor, MAX_SCALE);
            scale_increment = 512;
            exp_delta = twoToTheDoubleScaleUp;
        }

        // Calculate (scaleFactor % +/-512), 512 = 2^9, using
        // technique from "Hacker's Delight" section 10-2.
        //使用“黑客的喜悦”第 10-2 节中的技术计算 (scaleFactor % +-512), 512 = 2^9。
        int t = (scaleFactor >> 9-1) >>> 32 - 9;
        exp_adjust = ((scaleFactor + t) & (512 -1)) - t;

        d *= powerOfTwoD(exp_adjust);
        scaleFactor -= exp_adjust;

        while(scaleFactor != 0) {
            d *= exp_delta;
            scaleFactor -= scale_increment;
        }
        return d;
    }

    /**
     * Returns {@code f} &times;
     * 2<sup>{@code scaleFactor}</sup> rounded as if performed
     * by a single correctly rounded floating-point multiply to a
     * member of the float value set.  See the Java
     * Language Specification for a discussion of floating-point
     * value sets.  If the exponent of the result is between {@link
     * Float#MIN_EXPONENT} and {@link Float#MAX_EXPONENT}, the
     * answer is calculated exactly.  If the exponent of the result
     * would be larger than {@code Float.MAX_EXPONENT}, an
     * infinity is returned.  Note that if the result is subnormal,
     * precision may be lost; that is, when {@code scalb(x, n)}
     * is subnormal, {@code scalb(scalb(x, n), -n)} may not equal
     * <i>x</i>.  When the result is non-NaN, the result has the same
     * sign as {@code f}.
     * 1.返回 f× 2<sup>scaleFactor<sup> 四舍五入，
     * 就像通过单个正确四舍五入的浮点乘法执行到浮点值集的成员一样。
     * 有关浮点值集的讨论，请参阅 Java 语言规范。
     * 如果结果的指数在 Float.MIN_EXPONENT 和 Float.MAX_EXPONENT} 之间，
     * 则准确计算答案。如果结果的指数大于  Float.MAX_EXPONENT，则返回无穷大。
     * 请注意，如果结果不正常，则可能会丢失精度；也就是说，当 scalb(x, n)是次正规时，
     * scalb(scalb(x, n), -n)可能不等于 <i>x<i>。当结果为非 NaN 时，结果与 {@code f 具有相同的符号。
     * <p>Special cases:
     * <ul>
     * <li> If the first argument is NaN, NaN is returned.
     * <li> If the first argument is infinite, then an infinity of the
     * same sign is returned.
     * <li> If the first argument is zero, then a zero of the same
     * sign is returned.
     * </ul>
     *
     * @param f number to be scaled by a power of two.
     * @param scaleFactor power of 2 used to scale {@code f}
     * @return {@code f} &times; 2<sup>{@code scaleFactor}</sup>
     * @since 1.6
     */
    public static float scalb(float f, int scaleFactor) {
        // magnitude of a power of two so large that scaling a finite
        // nonzero value by it would be guaranteed to over or
        // underflow; due to rounding, scaling down takes takes an
        // additional power of two which is reflected here
        //2 的幂的大小如此之大，以至于通过它缩放有限的非零值将保证溢出或下溢；
        // 由于四舍五入，按比例缩小需要额外的 2 次方，这反映在此处
        final int MAX_SCALE = FloatConsts.MAX_EXPONENT + -FloatConsts.MIN_EXPONENT +
                              FloatConsts.SIGNIFICAND_WIDTH + 1;

        // Make sure scaling factor is in a reasonable range
        //确保缩放因子在合理范​​围内
        scaleFactor = Math.max(Math.min(scaleFactor, MAX_SCALE), -MAX_SCALE);

        /*
         * Since + MAX_SCALE for float fits well within the double
         * exponent range and + float -> double conversion is exact
         * the multiplication below will be exact. Therefore, the
         * rounding that occurs when the double product is cast to
         * float will be the correctly rounded float result.  Since
         * all operations other than the final multiply will be exact,
         * it is not necessary to declare this method strictfp.
         * 由于浮点数的 + MAX_SCALE 非常适合双指数范围，并且 + 浮点数 -> 双转换是精确的，
         * 因此下面的乘法将是精确的。因此，将双乘积转换为浮点数时发生的舍入将是正确舍入的浮点数结果。
         * 由于除最终乘法之外的所有操作都是精确的，因此没有必要声明此方法 strictfp
         */
        return (float)((double)f*powerOfTwoD(scaleFactor));
    }

    // Constants used in scalb
    //scalb 中使用的常量
    static double twoToTheDoubleScaleUp = powerOfTwoD(512);
    static double twoToTheDoubleScaleDown = powerOfTwoD(-512);

    /**
     * Returns a floating-point power of two in the normal range.
     * 返回正常范围内 2 的浮点幂
     */
    static double powerOfTwoD(int n) {
        assert(n >= DoubleConsts.MIN_EXPONENT && n <= DoubleConsts.MAX_EXPONENT);
        return Double.longBitsToDouble((((long)n + (long)DoubleConsts.EXP_BIAS) <<
                                        (DoubleConsts.SIGNIFICAND_WIDTH-1))
                                       & DoubleConsts.EXP_BIT_MASK);
    }

    /**
     * Returns a floating-point power of two in the normal range.
     * 返回正常范围内 2 的浮点幂。
     */
    static float powerOfTwoF(int n) {
        assert(n >= FloatConsts.MIN_EXPONENT && n <= FloatConsts.MAX_EXPONENT);
        return Float.intBitsToFloat(((n + FloatConsts.EXP_BIAS) <<
                                     (FloatConsts.SIGNIFICAND_WIDTH-1))
                                    & FloatConsts.EXP_BIT_MASK);
    }
}
