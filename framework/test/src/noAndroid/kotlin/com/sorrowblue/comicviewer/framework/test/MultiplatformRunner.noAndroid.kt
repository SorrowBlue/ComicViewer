package com.sorrowblue.comicviewer.framework.test

import kotlin.reflect.KClass

@Suppress("AbstractClassCanBeInterface")
actual abstract class MultiplatformRunner

actual annotation class MultiplatformRunWith(actual val value: KClass<out MultiplatformRunner>)

actual class MultiplatformAndroidJUnit4 : MultiplatformRunner()
