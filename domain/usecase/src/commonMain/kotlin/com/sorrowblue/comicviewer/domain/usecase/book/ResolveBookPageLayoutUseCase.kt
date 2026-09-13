/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.book

import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.service.book.BookPageLayoutService
import dev.zacsweers.metro.Inject

/**
 * 画像サイズ（縦横比）判定完了時に、コミックページの結合・分割レイアウトを再計算するユースケース。
 *
 * @property service ページレイアウト計算を行うドメインサービス
 */
@Inject
class ResolveBookPageLayoutUseCase(
    private val service: BookPageLayoutService = BookPageLayoutService(),
) {

    /**
     * 未評価ページの縦横比に基づき、ページリストを更新して返します。
     *
     * @param currentList 現在のページアイテム一覧
     * @param unratedPage 縦横比が確定した未評価ページ
     * @param isPortrait 画像が縦長の場合は true、横長の場合は false
     * @return 更新後のページアイテム一覧
     */
    operator fun invoke(
        currentList: List<PageItem>,
        unratedPage: UnratedPage,
        isPortrait: Boolean,
    ): List<PageItem> = service.resolvePage(
        currentList = currentList,
        unratedPage = unratedPage,
        isPortrait = isPortrait,
    )
}
