package com.sorrowblue.comicviewer.domain.service.di

import com.sorrowblue.comicviewer.domain.service.interactor.RegisterPdfPluginInteractor
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.scomicviewer.domain.usecase.RegisterPdfPluginUseCase
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(DataScope::class)
interface DesktopServiceProviders {
    @Binds
    private val RegisterPdfPluginInteractor.bind: RegisterPdfPluginUseCase get() = this
}
