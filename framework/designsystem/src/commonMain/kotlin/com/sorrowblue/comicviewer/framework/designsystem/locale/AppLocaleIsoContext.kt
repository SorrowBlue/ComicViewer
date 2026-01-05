package com.sorrowblue.comicviewer.framework.designsystem.locale

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class AppLocaleIsoScope

@GraphExtension(AppLocaleIsoScope::class)
interface AppLocaleIsoContext {
    val appLocaleIso: AppLocaleIso

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createAppLocaleIsoContext(): AppLocaleIsoContext
    }
}
