/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.book

import com.sorrowblue.comicviewer.domain.model.book.BookPage
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.service.book.BookPageLayoutService
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * コミックの初期ページ一覧を生成するユースケース。
 */
abstract class CreateInitialBookPagesUseCase :
    OneShotUseCase<CreateInitialBookPagesUseCase.Request, List<BookPage>, Unit>() {

    /**
     * ページリスト生成リクエスト
     *
     * @property totalPageCount 総ページ数
     * @property pageFormat ページフォーマット設定
     * @property isCompactWindow コンパクト画面（スマートフォン等）かどうか
     */
    data class Request(
        val totalPageCount: Int,
        val pageFormat: BookSettings.PageFormat,
        val isCompactWindow: Boolean,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class CreateInitialBookPagesUseCaseImpl(private val service: BookPageLayoutService) :
    CreateInitialBookPagesUseCase() {

    override suspend fun run(request: Request): Resource<List<BookPage>, Unit> {
        val bookPages = service.createInitialPages(
            totalPageCount = request.totalPageCount,
            pageFormat = request.pageFormat,
            isCompactWindow = request.isCompactWindow,
        )
        return Resource.Success(bookPages)
    }
}
