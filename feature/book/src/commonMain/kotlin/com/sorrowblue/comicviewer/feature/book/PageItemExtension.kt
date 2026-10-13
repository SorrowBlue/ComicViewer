/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.NextPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem

/**
 * [PageItem] に対応する 1-indexed の表示用ページ番号を取得します。
 *
 * @param totalPageCount 総ページ数
 * @return 前巻案内画面の場合は 0、次巻案内画面の場合は [totalPageCount] + 1、コミックページの場合は 1-indexed のページ番号
 */
internal fun PageItem.toDisplayPage(totalPageCount: Int): Int = when (this) {
    is BookPage.Spread.Combine -> maxOf(index, nextIndex) + 1
    is BookPage.Spread.Single -> index + 1
    is BookPage.Spread.Spread2 -> index + 1
    is BookPage.Spread.Unrated -> index + 1
    is BookPage.Split.Left -> index + 1
    is BookPage.Split.Right -> index + 1
    is BookPage.Split.Single -> index + 1
    is BookPage.Split.Unrated -> index + 1
    is BookPage.Default -> index + 1
    is NextPage -> if (isNext) totalPageCount + 1 else 0
}

/**
 * [PageItem] に対応する 0-indexed の読了ページ番号を取得します（保存用）。
 *
 * @param totalPageCount 総ページ数
 * @return 0 から [totalPageCount] - 1 の範囲に収まる 0-indexed のページ番号
 */
internal fun PageItem.toLastReadPage(totalPageCount: Int): Int = when (this) {
    is BookPage.Spread.Combine -> maxOf(index, nextIndex)

    is BookPage.Spread.Single -> index

    is BookPage.Spread.Spread2 -> index

    is BookPage.Spread.Unrated -> index

    is BookPage.Split.Left -> index

    is BookPage.Split.Right -> index

    is BookPage.Split.Single -> index

    is BookPage.Split.Unrated -> index

    is BookPage.Default -> index

    is NextPage -> if (isNext) {
        maxOf(0, totalPageCount - 1)
    } else {
        0
    }
}.coerceIn(0, maxOf(0, totalPageCount - 1))

/**
 * 指定した 1-indexed の目標ページ番号 [targetPage] に対応する Pager のインデックスを検索します。
 *
 * @param targetPage 目標ページ番号（1-indexed。0 以下の場合は前巻、[totalPageCount] より大きい場合は次巻）
 * @param totalPageCount 総ページ数
 * @return Pager のインデックス（0 .. lastIndex）
 */
internal fun List<PageItem>.findPagerIndex(targetPage: Int, totalPageCount: Int): Int {
    if (isEmpty()) return 0
    return when {
        targetPage < 1 -> {
            val index = indexOfFirst { it is NextPage && !it.isNext }
            if (index != -1) index else 0
        }

        targetPage > totalPageCount -> {
            val index = indexOfFirst { it is NextPage && it.isNext }
            if (index != -1) index else lastIndex
        }

        else -> {
            val targetIndex = targetPage - 1
            val exactIndex = indexOfFirst { item ->
                when (item) {
                    is BookPage.Spread.Combine ->
                        item.index == targetIndex ||
                            item.nextIndex == targetIndex

                    is BookPage.Spread.Single -> item.index == targetIndex

                    is BookPage.Spread.Spread2 -> item.index == targetIndex

                    is BookPage.Spread.Unrated -> item.index == targetIndex

                    is BookPage.Split.Left -> item.index == targetIndex

                    is BookPage.Split.Right -> item.index == targetIndex

                    is BookPage.Split.Single -> item.index == targetIndex

                    is BookPage.Split.Unrated -> item.index == targetIndex

                    is BookPage.Default -> item.index == targetIndex

                    is NextPage -> false
                }
            }
            if (exactIndex != -1) {
                exactIndex
            } else {
                val fallback = indexOfFirst { item ->
                    when (item) {
                        is BookPage.Spread.Combine -> maxOf(
                            item.index,
                            item.nextIndex,
                        ) >= targetIndex

                        is BookPage.Spread.Single -> item.index >= targetIndex

                        is BookPage.Spread.Spread2 -> item.index >= targetIndex

                        is BookPage.Spread.Unrated -> item.index >= targetIndex

                        is BookPage.Split.Left -> item.index >= targetIndex

                        is BookPage.Split.Right -> item.index >= targetIndex

                        is BookPage.Split.Single -> item.index >= targetIndex

                        is BookPage.Split.Unrated -> item.index >= targetIndex

                        is BookPage.Default -> item.index >= targetIndex

                        is NextPage -> false
                    }
                }
                if (fallback != -1) fallback else lastIndex
            }
        }
    }
}

/**
 * 現在表示中の [currentItem] に対応する新しいリスト内の Pager インデックスを検索します。
 *
 * @param currentItem 現在表示中の [PageItem]（初回ロード等の場合は null）
 * @param targetDisplayPage [currentItem] が見つからない場合のフォールバック用表示ページ番号 (1-indexed)
 * @param totalPageCount 総ページ数
 * @return 新しいリスト内での Pager インデックス
 */
internal fun List<PageItem>.findPagerIndexFor(
    currentItem: PageItem?,
    targetDisplayPage: Int,
    totalPageCount: Int,
): Int {
    if (isEmpty()) return 0
    val matchedIndex = resolveItemIndex(currentItem)
    return if (matchedIndex != -1) {
        matchedIndex
    } else {
        findPagerIndex(targetDisplayPage, totalPageCount)
    }
}

private fun List<PageItem>.resolveItemIndex(currentItem: PageItem?): Int {
    if (currentItem == null) return -1
    val exactKeyIndex = indexOfFirst { it.key == currentItem.key }
    if (exactKeyIndex != -1) return exactKeyIndex

    return when (currentItem) {
        is BookPage.Split.Unrated -> indexOfFirst { item ->
            item is BookPage.Split && item.index == currentItem.index
        }

        is BookPage.Spread.Unrated -> indexOfFirst { item ->
            when (item) {
                is BookPage.Spread.Combine ->
                    item.index == currentItem.index || item.nextIndex == currentItem.index

                is BookPage.Spread.Single -> item.index == currentItem.index

                is BookPage.Spread.Spread2 -> item.index == currentItem.index

                else -> false
            }
        }

        else -> -1
    }
}
