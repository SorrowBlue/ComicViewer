package com.sorrowblue.comicviewer.framework.common

import dev.zacsweers.metro.Qualifier

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher
