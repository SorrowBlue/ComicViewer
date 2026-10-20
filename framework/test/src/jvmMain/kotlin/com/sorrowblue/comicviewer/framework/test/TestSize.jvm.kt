/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.test

import org.junit.jupiter.api.Tag

/**
 * Annotation to assign a small test size qualifier to a test. This annotation can be used at the
 * method or class level.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("small")
actual annotation class SmallTest

/**
 * Annotation to assign a medium test size qualifier to a test. This annotation can be used at the
 * method or class level.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("medium")
actual annotation class MediumTest

/**
 * Annotation to assign a large test size qualifier to a test. This annotation can be used at the
 * method or class level.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Tag("large")
actual annotation class LargeTest
