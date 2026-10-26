/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

/**
 * Custom [CoroutineScope] that limits the number of concurrently executing coroutines using a [Semaphore].
 *
 * @param underlying The wrapped [CoroutineScope].
 * @param limit Maximum number of concurrent executions.
 */
internal class LimitedCoroutineScope(private val underlying: CoroutineScope, limit: Int) :
    CoroutineScope by underlying {

    private val semaphore = Semaphore(limit)

    fun <T> async(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T,
    ): Deferred<T> = underlying.async(context, start) {
        semaphore.withPermit { block() }
    }

    /**
     * Executes [transform] in parallel for each element in the collection while maintaining the concurrency limit,
     * and awaits completion of all tasks.
     *
     * @param transform Suspending transformation to run in parallel for each element.
     * @return List of transformed results.
     */
    suspend fun <T, R> Iterable<T>.mapParallel(transform: suspend (T) -> R): List<R> = map { item ->
        async { transform(item) }
    }.awaitAll()
}

/**
 * Executes [block] within a [LimitedCoroutineScope] whose concurrency is limited to [limit].
 *
 * @param limit Maximum number of concurrent executions.
 * @param context Execution coroutine context.
 * @param block Suspending block to execute within the scope.
 */
internal suspend fun <T> limitedCoroutineScope(
    limit: Int,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend LimitedCoroutineScope.() -> T,
): T = withContext(context) {
    LimitedCoroutineScope(this, limit).block()
}
