package com.sorrowblue.comicviewer.feature.readlater

import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class ReadLaterScreenScope

@GraphExtension(ReadLaterScreenScope::class)
interface ReadLaterScreenContext : ScreenContext {

    val pagingReadLaterFileUseCase: PagingReadLaterFileUseCase
    val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createReadLaterScreenContext(): ReadLaterScreenContext
    }
}
