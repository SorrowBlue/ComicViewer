/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.book

import com.sorrowblue.comicviewer.domain.model.book.PageItem
import com.sorrowblue.comicviewer.domain.model.book.UnratedPage
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.service.book.BookPageLayoutService
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * 画像サイズ（縦横比）判定完了時に、コミックページの結合・分割レイアウトを再計算するユースケース。
 */
abstract class ResolveBookPageLayoutUseCase :
    OneShotUseCase<ResolveBookPageLayoutUseCase.Request, List<PageItem>, Unit>() {

    /**
     * ページリスト更新リクエスト
     *
     * @property currentList 現在のページアイテム一覧
     * @property unratedPage 縦横比が確定した未評価ページ
     * @property isPortrait 画像が縦長の場合は true、横長の場合は false
     */
    data class Request(
        val currentList: List<PageItem>,
        val unratedPage: UnratedPage,
        val isPortrait: Boolean,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class ResolveBookPageLayoutUseCaseImpl(private val service: BookPageLayoutService) :
    ResolveBookPageLayoutUseCase() {

    override suspend fun run(request: Request): Resource<List<PageItem>, Unit> {
        val pageItems = service.resolvePage(
            currentList = request.currentList,
            unratedPage = request.unratedPage,
            isPortrait = request.isPortrait,
        )
        return Resource.Success(pageItems)
    }
}
