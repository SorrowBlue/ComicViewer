package com.sorrowblue.comicviewer.framework.common.di

import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.common.starup.LogcatInitializer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides

@ContributesTo(DataScope::class)
interface FrameworkCommonProviders {

    @Provides
    @IntoSet
    private fun provideCoilInitializer(): Initializer<*> = LogcatInitializer()
}
