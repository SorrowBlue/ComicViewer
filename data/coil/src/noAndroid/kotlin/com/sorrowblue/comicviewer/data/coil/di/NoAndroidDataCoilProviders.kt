package com.sorrowblue.comicviewer.data.coil.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface NoAndroidDataCoilProviders {

    @Provides
    fun provideCoilPlatformContext(): coil3.PlatformContext = coil3.PlatformContext.INSTANCE
}
