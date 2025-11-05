package com.sorrowblue.comicviewer.aggregation

import android.content.Context
import com.sorrowblue.comicviewer.data.coil.di.CoilGraph
import com.sorrowblue.comicviewer.data.reader.document.di.ReaderDocumentContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.designsystem.locale.LocaleGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
interface AndroidAppGraph :
    PlatformGraph,
    CoilGraph.Factory,
    ReaderDocumentContext,
    LocaleGraph {

    @DependencyGraph.Factory
    fun interface Factory {
        fun createAndroidAppGraph(
            @Provides applicationContext: Context,
            @Provides licenseeHelper: LicenseeHelper,
        ): AndroidAppGraph
    }
}
