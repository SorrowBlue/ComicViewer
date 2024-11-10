package com.sorrowblue.comicviewer.domain.model

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "There is no need for the user to specify the ID."
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CONSTRUCTOR)
@MustBeDocumented
annotation class ExperimentalIdValue
