/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.service.book.BookPageLayoutService
import dev.zacsweers.metro.Inject

/**
 * コミックの初期ページ一覧を生成するユースケース。
 *
 * @property service ページレイアウト計算を行うドメインサービス
 */
@Inject
class CreateInitialBookPagesUseCase(
    private val service: BookPageLayoutService = BookPageLayoutService(),
) {

    /**
     * 表示設定と総ページ数に基づき、初期ページリストを生成します。
     *
     * @param totalPageCount 総ページ数
     * @param pageFormat ページフォーマット設定
     * @param isCompactWindow コンパクト画面（スマートフォン等）かどうか
     * @return 初期ページリスト
     */
    operator fun invoke(
        totalPageCount: Int,
        pageFormat: BookSettings.PageFormat,
        isCompactWindow: Boolean,
    ): List<BookPage> = service.createInitialPages(
        totalPageCount = totalPageCount,
        pageFormat = pageFormat,
        isCompactWindow = isCompactWindow,
    )
}
