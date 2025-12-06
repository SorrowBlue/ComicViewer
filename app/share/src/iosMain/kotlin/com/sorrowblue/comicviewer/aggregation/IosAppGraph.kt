package com.sorrowblue.comicviewer.aggregation

import com.sorrowblue.comicviewer.data.coil.di.CoilGraph
import com.sorrowblue.comicviewer.data.coil.di.DataCoilProviders
import com.sorrowblue.comicviewer.data.coil.startup.CoilProviders
import com.sorrowblue.comicviewer.data.database.di.DatabaseProviders
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.IosDatabaseProviders
import com.sorrowblue.comicviewer.data.datastore.di.DataStoreProviders
import com.sorrowblue.comicviewer.data.reader.document.di.ReaderDocumentProviders
import com.sorrowblue.comicviewer.data.reader.zip.di.ReaderZipProviders
import com.sorrowblue.comicviewer.data.storage.client.di.DataStorageClientProviders
import com.sorrowblue.comicviewer.data.storage.device.di.StorageDeviceProviders
import com.sorrowblue.comicviewer.data.storage.smb.di.StorageSmbProviders
import com.sorrowblue.comicviewer.domain.service.di.ServiceProviders
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.feature.book.BookScreenContext
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenContext
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenContext
import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
import com.sorrowblue.comicviewer.feature.settings.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.component.GridSizeItemGraph
import com.sorrowblue.comicviewer.file.component.HiddenFilesToggleableItemGraph
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.di.FrameworkCommonProviders
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.designsystem.locale.AppLocaleIsoGraph
import com.sorrowblue.comicviewer.framework.designsystem.theme.ThemeContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
interface IosAppGraph :
    PlatformGraph,
    DataStoreProviders,
    IosDatabaseProviders,
    DataCoilProviders,
    CoilProviders,
    DatabaseProviders,
    ReaderDocumentProviders,
    ReaderZipProviders,
    DataStorageClientProviders,
    StorageDeviceProviders,
    StorageSmbProviders,
    FrameworkCommonProviders,
    ServiceProviders,
    ThemeContext.Factory,
    AppLocaleIsoGraph.Factory,
    CoilGraph.Factory,
    GridSizeItemGraph.Factory,
    HiddenFilesToggleableItemGraph.Factory,
    AuthenticationScreenContext.Factory,
    BasicCollectionCreateScreenContext.Factory,
    BasicCollectionEditScreenContext.Factory,
    BookMenuScreenContext.Factory,
    BookScreenContext.Factory,
    BookshelfDeleteScreenContext.Factory,
    BookshelfEditScreenContext.Factory,
    BookshelfScreenContext.Factory,
    BookshelfInfoScreenContext.Factory,
    CollectionListScreenContext.Factory,
    CollectionScreenContext.Factory,
    DarkModeScreenContext.Factory,
    DeleteCollectionScreenContext.Factory,
    DisplaySettingsScreenContext.Factory,
    FileInfoScreenContext.Factory,
    FolderScreenContext.Factory,
    HistoryScreenContext.Factory,
    ImageCacheScreenContext.Factory,
    InAppLanguagePickerScreenContext.Factory,
    LicenseScreenContext.Factory,
    PdfPluginScreenContext.Factory,
    PluginScreenContext.Factory,
    ReadLaterScreenContext.Factory,
    SecuritySettingsScreenContext.Factory,
    SmartCollectionCreateScreenContext.Factory,
    SmartCollectionEditScreenContext.Factory,
    TutorialScreenContext.Factory,
    ViewerSettingsScreenContext.Factory {
    @DependencyGraph.Factory
    fun interface Factory {
        fun createDesktopAppGraph(
            @Provides context: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): IosAppGraph
    }
}
