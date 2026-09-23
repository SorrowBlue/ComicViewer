/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest

class LimitedCoroutineScopeTest {

    @Test
    fun testLimitedCoroutineScope_concurrencyLimit() = runTest {
        val limit = 3
        val items = (1..10).toList()
        val mutex = Mutex()
        var currentConcurrent = 0
        var maxConcurrent = 0

        val results = limitedCoroutineScope(limit) {
            items.mapParallel { item ->
                mutex.withLock {
                    currentConcurrent++
                    if (currentConcurrent > maxConcurrent) {
                        maxConcurrent = currentConcurrent
                    }
                }
                delay(10.milliseconds)
                mutex.withLock {
                    currentConcurrent--
                }
                item * 2
            }
        }

        assertEquals(items.map { it * 2 }, results)
        assertTrue(
            maxConcurrent <= limit,
            "Max concurrent executions ($maxConcurrent) should not exceed limit ($limit)",
        )
    }
}
