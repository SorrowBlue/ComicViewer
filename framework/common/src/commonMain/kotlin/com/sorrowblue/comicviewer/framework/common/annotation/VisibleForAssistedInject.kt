package com.sorrowblue.comicviewer.framework.common.annotation

@RequiresOptIn(
    message = "Use only for the purpose of collecting",
    level = RequiresOptIn.Level.ERROR
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class VisibleForAssistedInject
