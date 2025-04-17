package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
internal class LimitedCoroutineScope(
    private val underlying: CoroutineScope,
    limit: Int,
) : CoroutineScope by underlying {

    private val semaphore: Semaphore = Semaphore(limit)

    private fun <T> withPermit(block: suspend CoroutineScope.() -> T): suspend CoroutineScope.() -> T =
        {
            semaphore.withPermit {
                block()
            }
        }

    fun <T> async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T> = underlying.async(context, start, withPermit(block))
}

internal suspend fun <T> limitedCoroutineScope(
    limit: Int,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend LimitedCoroutineScope.() -> T,
): T =
    withContext(context) {
        LimitedCoroutineScope(this, limit).block()
    }
