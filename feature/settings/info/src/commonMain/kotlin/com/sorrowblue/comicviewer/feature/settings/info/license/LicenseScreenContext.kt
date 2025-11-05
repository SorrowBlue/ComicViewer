package com.sorrowblue.comicviewer.feature.settings.info.license

import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class LicenseScreenScope

@GraphExtension(LicenseScreenScope::class)
interface LicenseScreenContext : ScreenContext {
    val licenseeHelper: LicenseeHelper

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createLicenseScreenContext(): LicenseScreenContext
    }
}
