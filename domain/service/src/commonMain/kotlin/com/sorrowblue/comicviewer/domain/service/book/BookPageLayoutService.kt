/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.NextPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * Domain service that calculates initial book page items, spread combinations, and split layouts.
 */
interface BookPageLayoutService {

    /**
     * Creates the initial list of book pages based on the display format and total page count.
     *
     * @param totalPageCount Total number of pages in the book.
     * @param pageFormat Page display format setting.
     * @param isCompactWindow Whether the current window is compact (used to resolve [BookSettings.PageFormat.Auto]).
     * @return Initial list of [BookPage] items.
     */
    fun createInitialPages(
        totalPageCount: Int,
        pageFormat: BookSettings.PageFormat,
        isCompactWindow: Boolean,
    ): List<BookPage>

    /**
     * Resolves an unrated page once its aspect ratio is determined and returns the updated page list.
     *
     * @param currentList Current list of page items.
     * @param unratedPage The unrated page whose aspect ratio has been resolved.
     * @param isPortrait True if the page image is portrait or square, false if landscape.
     * @return Updated list of [PageItem]s.
     */
    fun resolvePage(
        currentList: List<PageItem>,
        unratedPage: UnratedPage,
        isPortrait: Boolean,
    ): List<PageItem>
}

@Inject
@ContributesBinding(AppScope::class)
internal class BookPageLayoutServiceImpl : BookPageLayoutService {

    override fun createInitialPages(
        totalPageCount: Int,
        pageFormat: BookSettings.PageFormat,
        isCompactWindow: Boolean,
    ): List<BookPage> {
        if (totalPageCount <= 0) return emptyList()

        val format = when (pageFormat) {
            BookSettings.PageFormat.Default -> BookSettings.PageFormat.Default

            BookSettings.PageFormat.Spread -> BookSettings.PageFormat.Spread

            BookSettings.PageFormat.Split -> BookSettings.PageFormat.Split

            BookSettings.PageFormat.Auto -> if (isCompactWindow) {
                BookSettings.PageFormat.Split
            } else {
                BookSettings.PageFormat.Spread
            }
        }

        return (0 until totalPageCount).map { index ->
            when (format) {
                BookSettings.PageFormat.Default -> BookPage.Default(index)
                BookSettings.PageFormat.Spread -> BookPage.Spread.Unrated(index)
                BookSettings.PageFormat.Split -> BookPage.Split.Unrated(index)
                BookSettings.PageFormat.Auto -> error("Resolved above")
            }
        }
    }

    override fun resolvePage(
        currentList: List<PageItem>,
        unratedPage: UnratedPage,
        isPortrait: Boolean,
    ): List<PageItem> = when (unratedPage) {
        is BookPage.Spread.Unrated -> resolveSpreadPage(currentList, unratedPage, isPortrait)
        is BookPage.Split.Unrated -> resolveSplitPage(currentList, unratedPage, isPortrait)
    }

    private fun resolveSplitPage(
        currentList: List<PageItem>,
        split: BookPage.Split.Unrated,
        isPortrait: Boolean,
    ): List<PageItem> {
        val index = currentList.indexOf(split)
        if (index < 0) return currentList

        val mutableList = currentList.toMutableList()
        if (isPortrait) {
            mutableList[index] = BookPage.Split.Single(split.index)
        } else {
            // 右開きに合わせて右ページを先、左ページを後に配置
            mutableList[index] = BookPage.Split.Right(split.index)
            mutableList.add(index + 1, BookPage.Split.Left(split.index))
        }
        return mutableList
    }

    private fun resolveSpreadPage(
        currentList: List<PageItem>,
        spread: BookPage.Spread.Unrated,
        isPortrait: Boolean,
    ): List<PageItem> {
        val orientations = mutableMapOf<Int, PageOrientation>()
        currentList.forEach { item ->
            when (item) {
                is BookPage.Spread.Unrated -> orientations[item.index] = PageOrientation.Unrated

                is BookPage.Spread.Single -> orientations[item.index] = PageOrientation.Portrait

                is BookPage.Spread.Spread2 -> orientations[item.index] = PageOrientation.Landscape

                is BookPage.Spread.Combine -> {
                    orientations[item.index] = PageOrientation.Portrait
                    orientations[item.nextIndex] = PageOrientation.Portrait
                }

                else -> Unit
            }
        }
        orientations[spread.index] =
            if (isPortrait) PageOrientation.Portrait else PageOrientation.Landscape
        if (orientations.isEmpty()) return currentList

        val minIndex = orientations.keys.min()
        val maxIndex = orientations.keys.max()

        val prefix = currentList.takeWhile { it is NextPage }
        val suffix = currentList.takeLastWhile { it is NextPage }

        val spreadPages = mutableListOf<PageItem>()
        var page = minIndex
        while (page <= maxIndex) {
            page += processSpreadSlot(page, maxIndex, orientations, spreadPages)
        }

        return prefix + spreadPages + suffix
    }

    private fun processSpreadSlot(
        page: Int,
        maxIndex: Int,
        orientations: Map<Int, PageOrientation>,
        destination: MutableList<PageItem>,
    ): Int {
        val orientation1 = orientations[page] ?: PageOrientation.Unrated
        return when {
            page == 0 -> {
                val coverPage = when (orientation1) {
                    PageOrientation.Unrated -> BookPage.Spread.Unrated(0)
                    PageOrientation.Portrait -> BookPage.Spread.Single(0)
                    PageOrientation.Landscape -> BookPage.Spread.Spread2(0)
                }
                destination.add(coverPage)
                1
            }

            orientation1 == PageOrientation.Landscape -> {
                destination.add(BookPage.Spread.Spread2(page))
                1
            }

            page + 1 > maxIndex -> {
                val lastPage = if (orientation1 == PageOrientation.Portrait) {
                    BookPage.Spread.Single(page)
                } else {
                    BookPage.Spread.Unrated(page)
                }
                destination.add(lastPage)
                1
            }

            else -> processPairSlot(
                page = page,
                orientation1 = orientation1,
                orientation2 = orientations[page + 1] ?: PageOrientation.Unrated,
                destination = destination,
            )
        }
    }

    private fun processPairSlot(
        page: Int,
        orientation1: PageOrientation,
        orientation2: PageOrientation,
        destination: MutableList<PageItem>,
    ): Int {
        if (orientation2 == PageOrientation.Landscape) {
            val p1 = if (orientation1 == PageOrientation.Portrait) {
                BookPage.Spread.Single(page)
            } else {
                BookPage.Spread.Unrated(page)
            }
            destination.add(p1)
            destination.add(BookPage.Spread.Spread2(page + 1))
        } else if (orientation1 == PageOrientation.Portrait &&
            orientation2 == PageOrientation.Portrait
        ) {
            destination.add(BookPage.Spread.Combine(page, page + 1))
        } else {
            val p1 = if (orientation1 == PageOrientation.Portrait) {
                BookPage.Spread.Single(page)
            } else {
                BookPage.Spread.Unrated(page)
            }
            val p2 = if (orientation2 == PageOrientation.Portrait) {
                BookPage.Spread.Single(page + 1)
            } else {
                BookPage.Spread.Unrated(page + 1)
            }
            destination.add(p1)
            destination.add(p2)
        }
        return 2
    }

    private sealed interface PageOrientation {
        data object Unrated : PageOrientation
        data object Portrait : PageOrientation
        data object Landscape : PageOrientation
    }
}
