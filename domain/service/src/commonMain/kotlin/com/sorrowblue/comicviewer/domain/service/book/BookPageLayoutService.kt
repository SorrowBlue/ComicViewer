/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings

/**
 * コミックページの展開、見開き結合、および分割レイアウトの計算を行うドメインサービス。
 */
interface BookPageLayoutService {

    /**
     * 表示設定と総ページ数に基づき、初期ページリストを生成します。
     *
     * @param totalPageCount 総ページ数
     * @param pageFormat ページフォーマット設定
     * @param isCompactWindow コンパクト画面（スマートフォン等）かどうか（Auto 判定で使用）
     * @return 初期ページリスト
     */
    fun createInitialPages(
        totalPageCount: Int,
        pageFormat: BookSettings.PageFormat,
        isCompactWindow: Boolean,
    ): List<BookPage>

    /**
     * 画像読み込み完了時に未評価ページの縦横比を判定し、ページリストを更新して返します。
     *
     * @param currentList 現在のページアイテム一覧
     * @param unratedPage 縦横比が確定した未評価ページ
     * @param isPortrait 画像が縦長の場合は true、横長の場合は false
     * @return 更新後のページアイテム一覧
     */
    fun resolvePage(
        currentList: List<PageItem>,
        unratedPage: UnratedPage,
        isPortrait: Boolean,
    ): List<PageItem>

    companion object {
        /**
         * [BookPageLayoutService] のインスタンスを生成して返します。
         */
        operator fun invoke(): BookPageLayoutService = BookPageLayoutServiceImpl()
    }
}

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
        val index = currentList.indexOf(spread)
        if (index < 0) return currentList

        val workingList = currentList.toMutableList()
        workingList[index] = if (isPortrait) {
            BookPage.Spread.Single(spread.index)
        } else {
            BookPage.Spread.Spread2(spread.index)
        }

        return updateSpreadPageList(workingList)
    }

    private fun updateSpreadPageList(currentList: List<PageItem>): List<PageItem> {
        val skipIndex = mutableSetOf<Int>()
        val newList = mutableListOf<PageItem>()
        var nextSingle: BookPage.Spread.Single? = null

        currentList.forEachIndexed { index, bookItem ->
            if (index in skipIndex) return@forEachIndexed

            when (val item = nextSingle ?: bookItem) {
                is BookPage.Spread.Combine -> {
                    newList.add(item)
                    nextSingle = null
                }

                is BookPage.Spread.Single -> {
                    if (item.index == 0) {
                        newList.add(item)
                        nextSingle = null
                    } else {
                        nextSingle = processSingleSpreadPage(
                            item = item,
                            currentIndex = index,
                            currentList = currentList,
                            newList = newList,
                            skipIndex = skipIndex,
                        )
                    }
                }

                is BookPage.Spread.Spread2 -> {
                    newList.add(item)
                    nextSingle = null
                }

                is BookPage.Spread.Unrated -> {
                    newList.add(item)
                    nextSingle = null
                }

                else -> {
                    newList.add(item)
                    nextSingle = null
                }
            }
        }
        return newList
    }

    private fun processSingleSpreadPage(
        item: BookPage.Spread.Single,
        currentIndex: Int,
        currentList: List<PageItem>,
        newList: MutableList<PageItem>,
        skipIndex: MutableSet<Int>,
    ): BookPage.Spread.Single? {
        if (currentIndex + 1 >= currentList.size) {
            newList.add(item)
            return null
        }

        return when (val nextItem = currentList[currentIndex + 1]) {
            is BookPage.Spread.Single -> {
                newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                skipIndex.add(currentIndex + 1)
                null
            }

            is BookPage.Spread.Combine -> {
                newList.add(BookPage.Spread.Combine(item.index, nextItem.index))
                BookPage.Spread.Single(nextItem.nextIndex)
            }

            else -> {
                newList.add(item)
                null
            }
        }
    }
}
