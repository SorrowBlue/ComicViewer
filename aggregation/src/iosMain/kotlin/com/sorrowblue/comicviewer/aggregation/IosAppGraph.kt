package com.sorrowblue.comicviewer.aggregation

import com.sorrowblue.comicviewer.data.coil.di.DataCoilProviders
import com.sorrowblue.comicviewer.data.coil.startup.CoilProviders
import com.sorrowblue.comicviewer.data.database.di.DatabaseProviders
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.IosDatabaseBindings
import com.sorrowblue.comicviewer.data.datastore.di.DataStoreProviders
import com.sorrowblue.comicviewer.data.reader.document.di.ReaderDocumentProviders
import com.sorrowblue.comicviewer.data.reader.zip.di.ReaderZipProviders
import com.sorrowblue.comicviewer.data.storage.client.di.DataStorageClientModule
import com.sorrowblue.comicviewer.data.storage.device.di.StorageDeviceProviders
import com.sorrowblue.comicviewer.data.storage.smb.di.StorageSmbProviders
import com.sorrowblue.comicviewer.domain.service.di.ServiceProviders
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.di.FrameworkCommonProviders
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
interface IosAppGraph : PlatformGraph,
    IosDatabaseBindings,
    DataCoilProviders,
    CoilProviders,
    DatabaseProviders,
    DataStoreProviders,
    ReaderDocumentProviders,
    ReaderZipProviders,
    DataStorageClientModule,
    StorageDeviceProviders,
    StorageSmbProviders,
    FrameworkCommonProviders,
    ServiceProviders {

    @DependencyGraph.Factory
    fun interface Factory {
        fun createDesktopAppGraph(
            @Provides context: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): IosAppGraph
    }
}
