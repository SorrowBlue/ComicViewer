package com.sorrowblue.comicviewer.domain.service.di

import com.sorrowblue.comicviewer.domain.service.interactor.ClearImageCacheInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetBookshelfImageCacheInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetNavigationHistoryInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetOtherImageCacheInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.SendFatalErrorInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.FlowBookshelfListInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.PagingBookshelfFolderInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RegenerateThumbnailsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RegisterBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RemoveBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.ScanBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.UpdateDeletionFlagInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.AddCollectionFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.CreateCollectionInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.DeleteCollectionInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.GetCollectionInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.PagingCollectionExistInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.PagingCollectionFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.PagingCollectionInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.RemoveCollectionFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.collection.UpdateCollectionInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.ClearAllHistoryInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetFileAttributeInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetIntentBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetNextBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.PagingBookshelfBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.PagingFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.PagingFolderBookThumbnailsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.PagingHistoryBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.PagingQueryFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.UpdateLastReadPageInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.AddReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.DeleteAllReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.DeleteReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.ExistsReadlaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.PagingReadLaterFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.CollectionSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.LoadSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageBookSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageFolderDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageFolderSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManagePdfPluginSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageSecuritySettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageViewerOperationSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageViewerSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookshelfImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetOtherImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.DeleteCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionExistUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ClearAllHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFolderBookThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingHistoryBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManagePdfPluginSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerOperationSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(DataScope::class)
interface ServiceProviders {

    @Binds
    private val FlowBookshelfListInteractor.bind: FlowBookshelfListUseCase get() = this

    @Binds
    private val GetBookshelfInfoInteractor.bind: GetBookshelfInfoUseCase get() = this

    @Binds
    private val PagingBookshelfFolderInteractor.bind: PagingBookshelfFolderUseCase get() = this

    @Binds
    private val RegenerateThumbnailsInteractor.bind: RegenerateThumbnailsUseCase get() = this

    @Binds
    private val RegisterBookshelfInteractor.bind: RegisterBookshelfUseCase get() = this

    @Binds
    private val RemoveBookshelfInteractor.bind: RemoveBookshelfUseCase get() = this

    @Binds
    private val ScanBookshelfInteractor.bind: ScanBookshelfUseCase get() = this

    @Binds
    private fun UpdateDeletionFlagInteractor.bind(): UpdateDeletionFlagUseCase = this

    // Collection
    @Binds
    private val AddCollectionFileInteractor.bind: AddCollectionFileUseCase get() = this

    @Binds
    private val CreateCollectionInteractor.bind: CreateCollectionUseCase get() = this

    @Binds
    private val DeleteCollectionInteractor.bind: DeleteCollectionUseCase get() = this

    @Binds
    private val GetCollectionInteractor.bind: GetCollectionUseCase get() = this

    @Binds
    private val PagingCollectionExistInteractor.bind: PagingCollectionExistUseCase get() = this

    @Binds
    private val PagingCollectionFileInteractor.bind: PagingCollectionFileUseCase get() = this

    @Binds
    private val PagingCollectionInteractor.bind: PagingCollectionUseCase get() = this

    @Binds
    private val RemoveCollectionFileInteractor.bind: RemoveCollectionFileUseCase get() = this

    @Binds
    private val UpdateCollectionInteractor.bind: UpdateCollectionUseCase get() = this

    // File
    @Binds
    private val ClearAllHistoryInteractor.bind: ClearAllHistoryUseCase get() = this

    @Binds
    private val GetBookInteractor.bind: GetBookUseCase get() = this

    @Binds
    private val GetFileAttributeInteractor.bind: GetFileAttributeUseCase get() = this

    @Binds
    private val GetFileInteractor.bind: GetFileUseCase get() = this

    @Binds
    private val GetIntentBookInteractor.bind: GetIntentBookUseCase get() = this

    @Binds
    private val GetNextBookInteractor.bind: GetNextBookUseCase get() = this

    @Binds
    private val PagingBookshelfBookInteractor.bind: PagingBookshelfBookUseCase get() = this

    @Binds
    private val PagingFileInteractor.bind: PagingFileUseCase get() = this

    @Binds
    private val PagingFolderBookThumbnailsInteractor.bind: PagingFolderBookThumbnailsUseCase get() = this

    @Binds
    private val PagingHistoryBookInteractor.bind: PagingHistoryBookUseCase get() = this

    @Binds
    private val PagingQueryFileInteractor.bind: PagingQueryFileUseCase get() = this

    @Binds
    private val UpdateLastReadPageInteractor.bind: UpdateLastReadPageUseCase get() = this

    // Readlater
    @Binds
    private val AddReadLaterInteractor.bind: AddReadLaterUseCase get() = this

    @Binds
    private val DeleteAllReadLaterInteractor.bind: DeleteAllReadLaterUseCase get() = this

    @Binds
    private val DeleteReadLaterInteractor.bind: DeleteReadLaterUseCase get() = this

    @Binds
    private val ExistsReadlaterInteractor.bind: ExistsReadlaterUseCase get() = this

    @Binds
    private val PagingReadLaterFileInteractor.bind: PagingReadLaterFileUseCase get() = this

    // Settings
    @Binds
    private val CollectionSettingsInteractor.bind: CollectionSettingsUseCase get() = this

    @Binds
    private val LoadSettingsInteractor.bind: LoadSettingsUseCase get() = this

    @Binds
    private val ManageBookSettingsInteractor.bind: ManageBookSettingsUseCase get() = this

    @Binds
    private val ManageDisplaySettingsInteractor.bind: ManageDisplaySettingsUseCase get() = this

    @Binds
    private val ManageFolderDisplaySettingsInteractor.bind: ManageFolderDisplaySettingsUseCase get() = this

    @Binds
    private val ManageFolderSettingsInteractor.bind: ManageFolderSettingsUseCase get() = this

    @Binds
    private val ManagePdfPluginSettingsInteractor.bind: ManagePdfPluginSettingsUseCase get() = this

    @Binds
    private val ManageSecuritySettingsInteractor.bind: ManageSecuritySettingsUseCase get() = this

    @Binds
    private val ManageViewerOperationSettingsInteractor.bind: ManageViewerOperationSettingsUseCase get() = this

    //

    @Binds
    private val ClearImageCacheInteractor.bind: ClearImageCacheUseCase get() = this

    @Binds
    private val GetBookshelfImageCacheInfoInteractor.bind: GetBookshelfImageCacheInfoUseCase get() = this

    @Binds
    private val GetNavigationHistoryInteractor.bind: GetNavigationHistoryUseCase get() = this

    @Binds
    private val GetOtherImageCacheInfoInteractor.bind: GetOtherImageCacheInfoUseCase get() = this

    @Binds
    private val SendFatalErrorInteractor.bind: SendFatalErrorUseCase get() = this

    @Binds
    private val ManageViewerSettingsInteractor.bind: ManageViewerSettingsUseCase get() = this

}
