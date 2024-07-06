package com.sorrowblue.comicviewer.domain.service.di

import com.sorrowblue.comicviewer.domain.service.interactor.AddReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.ClearImageCacheInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.DeleteAllReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.DeleteReadLaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetImageCacheInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetInstalledModulesInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.GetNavigationHistoryInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.DeleteHistoryInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RegisterBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RemoveBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.ScanBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.AddFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.CreateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.DeleteFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.GetFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.RemoveFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.UpdateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.DeleteThumbnailsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.ExistsReadlaterInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetFileAttributeInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetIntentBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetNextBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.UpdateLastReadPageInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingBookshelfFolderInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingHistoryBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingQueryFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.paging.PagingReadLaterFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.LoadSettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageFolderDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageOneTimeFlagInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.settings.ManageSecuritySettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetInstalledModulesUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.DeleteHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetIntentBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingHistoryBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageOneTimeFlagUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface ViewModelBindsModule {

    @Binds
    fun bindManageOneTimeFlagUseCase(interactor: ManageOneTimeFlagInteractor): ManageOneTimeFlagUseCase

    @Binds
    fun bindRegisterBookshelfUseCase(interactor: RegisterBookshelfInteractor): RegisterBookshelfUseCase

    @Binds
    fun bindScanBookshelfUseCase(interactor: ScanBookshelfInteractor): ScanBookshelfUseCase

    @Binds
    fun bindLoadSettingsUseCase(interactor: LoadSettingsInteractor): LoadSettingsUseCase

    @Binds
    fun bindRemoveBookshelfUseCase(interactor: RemoveBookshelfInteractor): RemoveBookshelfUseCase

    @Binds
    fun bindGetBookshelfBookUseCase(interactor: GetBookshelfBookInteractor): GetBookshelfBookUseCase

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
    fun bindDeleteAllReadLaterUseCase(interactor: DeleteAllReadLaterInteractor): DeleteAllReadLaterUseCase

    @Binds
    fun bindGetNavigationHistoryUseCase(
        interactor: GetNavigationHistoryInteractor,
    ): GetNavigationHistoryUseCase

    @Binds
    fun bindUpdateLastReadPageUseCase(interactor: UpdateLastReadPageInteractor): UpdateLastReadPageUseCase

    @Binds
    fun bindDeleteHistoryUseCase(interactor: DeleteHistoryInteractor): DeleteHistoryUseCase

    @Binds
    fun bindGetBookshelfInfoUseCase(interactor: GetBookshelfInfoInteractor): GetBookshelfInfoUseCase

    @Binds
    fun bindGetNextBookUseCase(interactor: GetNextBookInteractor): GetNextBookUseCase

    @Binds
    fun bindGetFileUseCase(interactor: GetFileInteractor): GetFileUseCase

    @Binds
    fun bindDeleteThumbnailsUseCase(interactor: DeleteThumbnailsInteractor): DeleteThumbnailsUseCase

    @Binds
    fun bindGetBookshelfFileUseCase(interactor: GetBookshelfFileInteractor): GetBookshelfFileUseCase

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
    fun bindGetImageCacheInfoUseCase(interactor: GetImageCacheInfoInteractor): GetImageCacheInfoUseCase

    @Binds
    fun bindClearImageCacheUseCase(interactor: ClearImageCacheInteractor): ClearImageCacheUseCase

    // Paging

    @Binds
    fun bindPagingFileUseCase(interactor: PagingFileInteractor): PagingFileUseCase

    @Binds
    fun bindPagingQueryFileUseCase(interactor: PagingQueryFileInteractor): PagingQueryFileUseCase

    @Binds
    fun bindPagingBookshelfFolderUseCase(
        interactor: PagingBookshelfFolderInteractor,
    ): PagingBookshelfFolderUseCase

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
