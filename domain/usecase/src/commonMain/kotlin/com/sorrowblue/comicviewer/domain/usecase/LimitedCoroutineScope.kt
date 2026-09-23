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
 * [Semaphore] により同時に実行されるコルーチン数を制限するカスタム [CoroutineScope]。
 *
 * @param underlying ラップ元の [CoroutineScope]
 * @param limit 最大同時実行数
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
     * コレクションの各要素に対して、最大同時実行数を保ちながら並行実行し、全タスクの完了を待機する。
     *
     * @param transform 各要素に対して並行実行する処理
     * @return 変換結果のリスト
     */
    suspend fun <T, R> Iterable<T>.mapParallel(transform: suspend (T) -> R): List<R> = map { item ->
        async { transform(item) }
    }.awaitAll()
}

/**
 * 同時実行数を [limit] に制限した [LimitedCoroutineScope] で [block] を実行する。
 *
 * @param limit 最大同時実行数
 * @param context 実行コンテキスト
 * @param block スコープ内で実行する処理
 */
internal suspend fun <T> limitedCoroutineScope(
    limit: Int,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend LimitedCoroutineScope.() -> T,
): T = withContext(context) {
    LimitedCoroutineScope(this, limit).block()
}
