package com.sorrowblue.comicviewer.app

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.sorrowblue.comicviewer.ComicViewerUIContext
import com.sorrowblue.comicviewer.data.coil.di.CoilContext
import com.sorrowblue.comicviewer.data.coil.di.DataCoilProviders
import com.sorrowblue.comicviewer.data.coil.startup.CoilProviders
import com.sorrowblue.comicviewer.data.database.di.DatabaseProviders
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.IosDatabaseProviders
import com.sorrowblue.comicviewer.data.datastore.di.DataStoreContext
import com.sorrowblue.comicviewer.data.datastore.di.DataStoreProviders
import com.sorrowblue.comicviewer.data.reader.document.di.ReaderDocumentProviders
import com.sorrowblue.comicviewer.data.reader.zip.di.ReaderZipProviders
import com.sorrowblue.comicviewer.data.storage.client.di.DataStorageClientProviders
import com.sorrowblue.comicviewer.data.storage.device.di.StorageDeviceProviders
import com.sorrowblue.comicviewer.data.storage.smb.di.StorageSmbProviders
import com.sorrowblue.comicviewer.domain.service.di.ServiceProviders
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenContext
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationProviders
import com.sorrowblue.comicviewer.feature.book.BookScreenContext
import com.sorrowblue.comicviewer.feature.book.menu.BookMenuScreenContext
import com.sorrowblue.comicviewer.feature.book.nav.BookNavProviders
import com.sorrowblue.comicviewer.feature.book.navigation.BookProviders
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditProviders
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.navigation.BookshelfInfoProviders
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfProviders
import com.sorrowblue.comicviewer.feature.collection.CollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.add.BasicCollectionAddScreenContext
import com.sorrowblue.comicviewer.feature.collection.add.navigation.CollectionAddProviders
import com.sorrowblue.comicviewer.feature.collection.delete.DeleteCollectionScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.basic.BasicCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.navigation.CollectionEditProviders
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionCreateScreenContext
import com.sorrowblue.comicviewer.feature.collection.editor.smart.SmartCollectionEditScreenContext
import com.sorrowblue.comicviewer.feature.collection.list.CollectionListScreenContext
import com.sorrowblue.comicviewer.feature.collection.nav.CollectionNav
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionProviders
import com.sorrowblue.comicviewer.feature.history.HistoryScreenContext
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryProviders
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenContext
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterProviders
import com.sorrowblue.comicviewer.feature.search.SearchScreenContext
import com.sorrowblue.comicviewer.feature.search.navigation.SearchProviders
import com.sorrowblue.comicviewer.feature.settings.display.DarkModeScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.DisplaySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.display.navigation.DisplaySettingsProviders
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsProviders
import com.sorrowblue.comicviewer.feature.settings.imagecache.ImageCacheScreenContext
import com.sorrowblue.comicviewer.feature.settings.inapp.InAppLanguagePickerScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseScreenContext
import com.sorrowblue.comicviewer.feature.settings.info.license.LicenseeHelper
import com.sorrowblue.comicviewer.feature.settings.info.navigation.InfoSettingsNavigation
import com.sorrowblue.comicviewer.feature.settings.nav.SettingsNavProviders
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsProviders
import com.sorrowblue.comicviewer.feature.settings.plugin.PluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.plugin.navigation.PluginSettingsProviders
import com.sorrowblue.comicviewer.feature.settings.plugin.pdf.PdfPluginScreenContext
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.security.navigation.SecuritySettingsProviders
import com.sorrowblue.comicviewer.feature.settings.viewer.ViewerSettingsScreenContext
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.ViewerSettingsProviders
import com.sorrowblue.comicviewer.feature.tutorial.TutorialScreenContext
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialProviders
import com.sorrowblue.comicviewer.file.FileInfoScreenContext
import com.sorrowblue.comicviewer.file.component.FileAppBarItemContext
import com.sorrowblue.comicviewer.folder.FolderScreenContext
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import com.sorrowblue.comicviewer.framework.common.PlatformGraph
import com.sorrowblue.comicviewer.framework.common.di.FrameworkCommonProviders
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import com.sorrowblue.comicviewer.framework.designsystem.locale.AppLocaleIsoContext
import com.sorrowblue.comicviewer.framework.ui.navigation.Navigator
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(
    scope = AppScope::class,
    additionalScopes = [DataScope::class],
)
actual interface AppGraph :
    ComicViewerUIContext.Factory,
    PreAppScreenContext.Factory,
    CoilContext.Factory,
    DataCoilProviders,
    CoilProviders,
    DatabaseProviders,
    IosDatabaseProviders,
    DataStoreContext.Factory,
    DataStoreProviders,
    ReaderDocumentProviders,
    ReaderZipProviders,
    DataStorageClientProviders,
    StorageDeviceProviders,
    StorageSmbProviders,
    ServiceProviders,
    AuthenticationScreenContext.Factory,
    AuthenticationProviders,
    BookScreenContext.Factory,
    BookMenuScreenContext.Factory,
    BookNavProviders,
    BookProviders,
    BookshelfScreenContext.Factory,
    BookshelfEditScreenContext.Factory,
    BookshelfEditProviders,
    BookshelfInfoScreenContext.Factory,
    BookshelfDeleteScreenContext.Factory,
    BookshelfInfoProviders,
    BookshelfProviders,
    CollectionScreenContext.Factory,
    BasicCollectionAddScreenContext.Factory,
    CollectionAddProviders,
    DeleteCollectionScreenContext.Factory,
    BasicCollectionCreateScreenContext.Factory,
    BasicCollectionEditScreenContext.Factory,
    CollectionEditProviders,
    SmartCollectionCreateScreenContext.Factory,
    SmartCollectionEditScreenContext.Factory,
    CollectionListScreenContext.Factory,
    CollectionNav,
    CollectionProviders,
    HistoryScreenContext.Factory,
    HistoryProviders,
    ReadLaterScreenContext.Factory,
    ReadLaterProviders,
    SearchScreenContext.Factory,
    SearchProviders,
    DarkModeScreenContext.Factory,
    DisplaySettingsScreenContext.Factory,
    DisplaySettingsProviders,
    FolderSettingsScreenContext.Factory,
    FolderSettingsProviders,
    ImageCacheScreenContext.Factory,
    InAppLanguagePickerScreenContext.Factory,
    LicenseScreenContext.Factory,
    InfoSettingsNavigation,
    SettingsNavProviders,
    SettingsProviders,
    PluginScreenContext.Factory,
    PluginSettingsProviders,
    PdfPluginScreenContext.Factory,
    SecuritySettingsScreenContext.Factory,
    SecuritySettingsProviders,
    ViewerSettingsScreenContext.Factory,
    ViewerSettingsProviders,
    TutorialScreenContext.Factory,
    TutorialProviders,
    FileInfoScreenContext.Factory,
    FileAppBarItemContext.Factory,
    FolderScreenContext.Factory,
    AppLocaleIsoContext.Factory,
    PlatformGraph,
    FrameworkCommonProviders {
    actual val entries: Set<EntryProviderScope<NavKey>.(Navigator) -> Unit>
    actual val context: PlatformContext

    @DependencyGraph.Factory
    actual fun interface Factory {
        actual fun createAppGraph(
            @Provides applicationContext: PlatformContext,
            @Provides licenseeHelper: LicenseeHelper,
        ): AppGraph
    }
}
