package com.sorrowblue.comicviewer.feature.settings

import com.sorrowblue.comicviewer.framework.designsystem.locale.AppLocaleIso
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class InAppLanguagePickerScope

@GraphExtension(InAppLanguagePickerScope::class)
interface InAppLanguagePickerScreenContext : ScreenContext {
    val appLocaleIso: AppLocaleIso

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createInAppLanguagePickerScreenContext(): InAppLanguagePickerScreenContext
    }
}
