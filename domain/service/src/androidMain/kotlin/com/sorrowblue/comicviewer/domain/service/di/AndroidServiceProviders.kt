package com.sorrowblue.comicviewer.domain.service.di

import com.sorrowblue.comicviewer.domain.service.interactor.GetPdfPluginStateInteractor
import com.sorrowblue.comicviewer.domain.usecase.GetPdfPluginStateUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(DataScope::class)
interface AndroidServiceProviders {
    @Binds
    private val GetPdfPluginStateInteractor.bind: GetPdfPluginStateUseCase get() = this
}
