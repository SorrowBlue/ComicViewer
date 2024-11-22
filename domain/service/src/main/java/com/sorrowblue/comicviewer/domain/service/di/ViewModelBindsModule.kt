package com.sorrowblue.comicviewer.domain.service.di

import com.sorrowblue.comicviewer.domain.service.interactor.ClearImageCacheInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetBookshelfImageCacheInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetInstalledModulesInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetNavigationHistoryInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetOtherImageCacheInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.SendFatalErrorInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RegenerateThumbnailsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RegisterBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RemoveBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.ScanBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.AddFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.CreateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.DeleteFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.GetFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.RemoveFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.UpdateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetFileAttributeInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetIntentBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetNextBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.UpdateLastReadPageInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingBookshelfBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingBookshelfFolderInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingFolderBookThumbnailsUseCaseInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingHistoryBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingQueryFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingReadLaterFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.AddReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.DeleteAllReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.DeleteReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.readlater.ExistsReadlaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.LoadSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageFolderDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageSecuritySettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookshelfImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetOtherImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFolderBookThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.PagingReadLaterFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ViewModelBindsModule {

    @Binds
    fun bindRegisterBookshelfUseCase(interactor: RegisterBookshelfInteractor): RegisterBookshelfUseCase

    @Binds
    fun bindScanBookshelfUseCase(interactor: ScanBookshelfInteractor): ScanBookshelfUseCase

    @Binds
    fun bindRegenerateThumbnailsUseCase(interactor: RegenerateThumbnailsInteractor): RegenerateThumbnailsUseCase

    @Binds
    fun bindLoadSettingsUseCase(interactor: LoadSettingsInteractor): LoadSettingsUseCase

    @Binds
    fun bindRemoveBookshelfUseCase(interactor: RemoveBookshelfInteractor): RemoveBookshelfUseCase

    @Binds
    fun bindGetBookUseCase(interactor: GetBookInteractor): GetBookUseCase

    @Binds
    fun bindIsReadLaterUseCase(interactor: ExistsReadlaterInteractor): ExistsReadlaterUseCase

    @Binds
    fun bindGetFileAttributeUseCase(interactor: GetFileAttributeInteractor): GetFileAttributeUseCase

    @Binds
    fun bindAddReadLaterUseCase(interactor: AddReadLaterInteractor): AddReadLaterUseCase

    @Binds
    fun bindDeleteReadLaterUseCase(interactor: DeleteReadLaterInteractor): DeleteReadLaterUseCase

    @Binds
    fun bindSendFatalErrorUseCase(interactor: SendFatalErrorInteractor): SendFatalErrorUseCase

    @Binds
    fun bindDeleteAllReadLaterUseCase(interactor: DeleteAllReadLaterInteractor): DeleteAllReadLaterUseCase

    @Binds
    fun bindGetNavigationHistoryUseCase(
        interactor: GetNavigationHistoryInteractor,
    ): GetNavigationHistoryUseCase

    @Binds
    fun bindUpdateLastReadPageUseCase(interactor: UpdateLastReadPageInteractor): UpdateLastReadPageUseCase

    @Binds
    fun bindGetBookshelfInfoUseCase(interactor: GetBookshelfInfoInteractor): GetBookshelfInfoUseCase

    @Binds
    fun bindGetNextBookUseCase(interactor: GetNextBookInteractor): GetNextBookUseCase

    @Binds
    fun bindGetFileUseCase(interactor: GetFileInteractor): GetFileUseCase

    @Binds
    fun bindAddFavoriteFileUseCase(interactor: AddFavoriteFileInteractor): AddFavoriteFileUseCase

    @Binds
    fun bindRemoveFavoriteFileUseCase(interactor: RemoveFavoriteFileInteractor): RemoveFavoriteFileUseCase

    @Binds
    fun bindCreateFavoriteUseCase(interactor: CreateFavoriteInteractor): CreateFavoriteUseCase

    @Binds
    fun bindGetFavoriteUseCase(interactor: GetFavoriteInteractor): GetFavoriteUseCase

    @Binds
    fun bindDeleteFavoriteUseCase(interactor: DeleteFavoriteInteractor): DeleteFavoriteUseCase

    @Binds
    fun bindUpdateFavoriteUseCase(interactor: UpdateFavoriteInteractor): UpdateFavoriteUseCase

    @Binds
    fun bindGetIntentBookUseCase(interactor: GetIntentBookInteractor): GetIntentBookUseCase

    @Binds
    fun bindGetBookshelfImageCacheInfoUseCase(
        interactor: GetBookshelfImageCacheInfoInteractor,
    ): GetBookshelfImageCacheInfoUseCase

    @Binds
    fun bindGetOtherImageCacheInfoUseCase(interactor: GetOtherImageCacheInfoInteractor): GetOtherImageCacheInfoUseCase

    @Binds
    fun bindClearImageCacheUseCase(interactor: ClearImageCacheInteractor): ClearImageCacheUseCase

    // Paging

    @Binds
    fun bindPagingFileUseCase(interactor: PagingFileInteractor): PagingFileUseCase

    @Binds
    fun bindPagingQueryFileUseCase(interactor: PagingQueryFileInteractor): PagingQueryFileUseCase

    @Binds
    fun bindPagingBookshelfFolderUseCase(interactor: PagingBookshelfFolderInteractor): PagingBookshelfFolderUseCase

    @Binds
    fun bindPagingBookshelfBookUseCase(interactor: PagingBookshelfBookInteractor): PagingBookshelfBookUseCase

    @Binds
    fun bindPagingFolderBookThumbnailsUseCase(
        interactor: PagingFolderBookThumbnailsUseCaseInteractor,
    ): PagingFolderBookThumbnailsUseCase

    @Binds
    fun bindPagingFavoriteUseCase(interactor: PagingFavoriteInteractor): PagingFavoriteUseCase

    @Binds
    fun bindPagingFavoriteFileUseCase(interactor: PagingFavoriteFileInteractor): PagingFavoriteFileUseCase

    @Binds
    fun bindPagingReadLaterFileUseCase(interactor: PagingReadLaterFileInteractor): PagingReadLaterFileUseCase

    @Binds
    fun bindPagingHistoryBookUseCase(interactor: PagingHistoryBookInteractor): PagingHistoryBookUseCase

    // Settings
    @Binds
    fun bindManageFolderDisplaySettingsUseCase(
        interactor: ManageFolderDisplaySettingsInteractor,
    ): ManageFolderDisplaySettingsUseCase

    @Binds
    fun bindManageSecuritySettingsUseCase(
        interactor: ManageSecuritySettingsInteractor,
    ): ManageSecuritySettingsUseCase

    @Binds
    fun bindGetInstalledModulesUseCase(interactor: GetInstalledModulesInteractor): GetInstalledModulesUseCase
}
