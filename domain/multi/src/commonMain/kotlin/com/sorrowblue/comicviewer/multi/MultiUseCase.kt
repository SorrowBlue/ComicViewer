package com.sorrowblue.comicviewer.multi

import kotlin.random.Random

interface MultiUseCase {

    fun getUuid(): String
}


internal class MultiUseCaseImpl @Inject constructor(): MultiUseCase {
    override fun getUuid(): String {
        return Random.nextLong().toString()
    }
}

@OptIn(ExperimentalMultiplatform::class)
@OptionalExpectation
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.BINARY)
@Repeatable
expect annotation class Inject()
// Declaration annotated with '@OptionalExpectation' can only be used in common module sources
