package com.sorrowblue.comicviewer.domain.service

import dev.zacsweers.metro.Qualifier

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher
