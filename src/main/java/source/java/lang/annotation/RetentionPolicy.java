/*
 * Copyright (c) 2003, 2004, Oracle and/or its affiliates. All rights reserved.
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

package java.lang.annotation;

/**
 * Annotation retention policy.  The constants of this enumerated type
 * describe the various policies for retaining annotations.  They are used
 * in conjunction with the {@link Retention} meta-annotation type to specify
 * how long annotations are to be retained.
 *
 * @author  Joshua Bloch
 * @since 1.5
 */
public enum RetentionPolicy {
    /**
     * Annotations are to be discarded by the compiler.
     * 注解在被编译时被抛弃
     */
    SOURCE,

    /**
     * Annotations are to be recorded in the class file by the compiler
     * but need not be retained by the VM at run time.  This is the default
     * behavior.
     * 被保留在class文件中,但不需要在运行时被虚拟机保留
     * 这是默认的保留策略
     */
    CLASS,

    /**
     * Annotations are to be recorded in the class file by the compiler and
     * retained by the VM at run time, so they may be read reflectively.
     *一直保留在运行时,而且可以被反射获取
     * @see java.lang.reflect.AnnotatedElement
     */
    RUNTIME
}
