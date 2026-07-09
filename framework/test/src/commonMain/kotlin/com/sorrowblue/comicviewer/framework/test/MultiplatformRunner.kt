/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.framework.test

import kotlin.reflect.KClass

@Suppress("AbstractClassCanBeInterface")
expect abstract class MultiplatformRunner

expect annotation class MultiplatformRunWith(val value: KClass<out MultiplatformRunner>)

expect class MultiplatformAndroidJUnit4 : MultiplatformRunner
