/*
 * Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;

/**
 * Indicates that the named compiler warnings should be suppressed in the
 * annotated element (and in all program elements contained in the annotated
 * element).  Note that the set of warnings suppressed in a given element is
 * a superset of the warnings suppressed in all containing elements.  For
 * example, if you annotate a class to suppress one warning and annotate a
 * method to suppress another, both warnings will be suppressed in the method.
 * 1.指示应在带注释的元素（以及带注释的元素中包含的所有程序元素）中抑制指定的编译器警告。
 * 请注意，给定元素中被抑制的警告集是所有包含元素中被抑制的警告的超集。
 * 例如，如果您注释一个类来抑制一个警告，并注释一个方法来抑制另一个警告，那么这两个警告都将在该方法中被抑制
 * <p>As a matter of style, programmers should always use this annotation
 * on the most deeply nested element where it is effective.  If you want to
 * suppress a warning in a particular method, you should annotate that
 * method rather than its class.
 * 2.作为风格问题，程序员应该始终在最有效的嵌套最深的元素上使用此注释。
 * 如果您想在特定方法中抑制警告，您应该注释该方法而不是它的类。
 * @author Josh Bloch
 * @since 1.5
 * @jls 4.8 Raw Types
 * @jls 4.12.2 Variables of Reference Type
 * @jls 5.1.9 Unchecked Conversion
 * @jls 5.5.2 Checked Casts and Unchecked Casts
 * @jls 9.6.3.5 @SuppressWarnings
 */
@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE})
@Retention(RetentionPolicy.SOURCE)
public @interface SuppressWarnings {
    /**
     * The set of warnings that are to be suppressed by the compiler in the
     * annotated element.  Duplicate names are permitted.  The second and
     * successive occurrences of a name are ignored.  The presence of
     * unrecognized warning names is <i>not</i> an error: Compilers must
     * ignore any warning names they do not recognize.  They are, however,
     * free to emit a warning if an annotation contains an unrecognized
     * warning name.
     * 1.编译器要在带注解的元素中抑制的警告集。允许重复名称。
     * 名称的第二次和连续出现将被忽略。出现无法识别的警告名称不是一个错误：
     * 编译器必须忽略它们无法识别的任何警告名称。但是，如果注释包含无法识别的警告名称，它们可以自由发出警告。
     * <p> The string {@code "unchecked"} is used to suppress
     * unchecked warnings. Compiler vendors should document the
     * additional warning names they support in conjunction with this
     * annotation type. They are encouraged to cooperate to ensure
     * that the same names work across multiple compilers.
     * @return the set of warnings to be suppressed
     * 2.字符串"unchecked"用于抑制未经检查的警告。
     * 编译器供应商应结合此注释类型记录他们支持的其他警告名称。
     * 鼓励他们合作以确保相同的名称在多个编译器中工作。 @return 要抑制的警告集
     */
    String[] value();
}
