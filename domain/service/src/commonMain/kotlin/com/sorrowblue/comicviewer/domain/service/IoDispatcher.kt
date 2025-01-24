package com.sorrowblue.comicviewer.domain.service

import org.koin.core.annotation.Named

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
@Named
annotation class IoDispatcher
