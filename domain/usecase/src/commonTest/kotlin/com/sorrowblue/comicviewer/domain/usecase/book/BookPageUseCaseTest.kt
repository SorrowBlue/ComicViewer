/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.coroutines.test.runTest

@DependencyGraph(scope = AppScope::class)
internal interface TestGraph {

    val createInitialBookPagesUseCase: CreateInitialBookPagesUseCase
    val resolveBookPageLayoutUseCase: ResolveBookPageLayoutUseCase
}

class BookPageUseCaseTest {

    private lateinit var createInitialBookPagesUseCase: CreateInitialBookPagesUseCase
    private lateinit var resolveBookPageLayoutUseCase: ResolveBookPageLayoutUseCase

    @BeforeTest
    fun setup() {
        val graph = createGraph<TestGraph>()
        createInitialBookPagesUseCase = graph.createInitialBookPagesUseCase
        resolveBookPageLayoutUseCase = graph.resolveBookPageLayoutUseCase
    }

    @Test
    fun createInitialBookPagesUseCase_delegatesToService() = runTest {
        val resource = createInitialBookPagesUseCase(
            CreateInitialBookPagesUseCase.Request(
                totalPageCount = 2,
                pageFormat = BookSettings.PageFormat.Spread,
                isCompactWindow = false,
            ),
        )
        assertIs<Resource.Success<List<BookPage>>>(resource)
        assertEquals(
            listOf(
                BookPage.Spread.Unrated(0),
                BookPage.Spread.Unrated(1),
            ),
            resource.data,
        )
    }

    @Test
    fun resolveBookPageLayoutUseCase_delegatesToService() = runTest {
        val initialList: List<PageItem> = listOf(
            BookPage.Spread.Unrated(0),
            BookPage.Spread.Unrated(1),
        )

        val resource = resolveBookPageLayoutUseCase(
            ResolveBookPageLayoutUseCase.Request(
                currentList = initialList,
                unratedPage = BookPage.Spread.Unrated(0),
                isPortrait = true,
            ),
        )
        assertIs<Resource.Success<List<PageItem>>>(resource)
        assertEquals(
            listOf(
                BookPage.Spread.Single(0),
                BookPage.Spread.Unrated(1),
            ),
            resource.data,
        )
    }
}
