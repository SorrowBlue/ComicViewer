package com.sorrowblue.comicviewer.data.reader.zip.di

import com.sorrowblue.comicviewer.data.reader.zip.startup.SevenZipInitializer
import com.sorrowblue.comicviewer.framework.common.Initializer
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet

@ContributesTo(DataScope::class)
interface DesktopReaderZipProviders {
    @Binds
    @IntoSet
    private fun SevenZipInitializer.bind(): Initializer<*> = this
}
