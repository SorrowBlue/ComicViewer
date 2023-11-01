package com.sorrowblue.comicviewer.domain.service.di

import com.sorrowblue.comicviewer.domain.service.interactor.ScanBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.UpdateHistoryInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.DeleteHistoryInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfFolderInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.GetBookshelfInfoInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RegisterBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.bookshelf.RemoveBookshelfInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.AddFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.CreateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.DeleteFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.GetFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.GetNextFavoriteBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.RemoveFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.favorite.UpdateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.DeleteThumbnailsInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetBookInteractor
import com.sorrowblue.comicviewer.domain.service.interactor.file.GetFileInteractor
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
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.DeleteHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFolderUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetNextFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
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
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class ViewModelBindsModule {

    @Binds
    abstract fun bindManageOneTimeFlagUseCase(interactor: ManageOneTimeFlagInteractor): ManageOneTimeFlagUseCase

    @Binds
    abstract fun bindRegisterBookshelfUseCase(interactor: RegisterBookshelfInteractor): RegisterBookshelfUseCase

    @Binds
    abstract fun bindLoadSettingsUseCase(interactor: LoadSettingsInteractor): LoadSettingsUseCase

    @Binds
    abstract fun bindRemoveBookshelfUseCase(interactor: RemoveBookshelfInteractor): RemoveBookshelfUseCase

    @Binds
    abstract fun bindScanBookshelfUseCase(interactor: ScanBookshelfInteractor): ScanBookshelfUseCase

    @Binds
    abstract fun bindGetBookshelfBookUseCase(interactor: GetBookshelfBookInteractor): GetBookshelfBookUseCase

    @Binds
    abstract fun bindGetBookUseCase(interactor: GetBookInteractor): GetBookUseCase

    @Binds
    abstract fun bindAddReadLaterUseCase(interactor: com.sorrowblue.comicviewer.domain.service.interactor.AddReadLaterInteractor): AddReadLaterUseCase

    @Binds
    abstract fun bindDeleteReadLaterUseCase(interactor: com.sorrowblue.comicviewer.domain.service.interactor.DeleteReadLaterInteractor): DeleteReadLaterUseCase

    @Binds
    abstract fun bindDeleteAllReadLaterUseCase(interactor: com.sorrowblue.comicviewer.domain.service.interactor.DeleteAllReadLaterInteractor): DeleteAllReadLaterUseCase

    @Binds
    abstract fun bindGetNavigationHistoryUseCase(interactor: com.sorrowblue.comicviewer.domain.service.interactor.GetNavigationHistoryInteractor): GetNavigationHistoryUseCase

    @Binds
    abstract fun bindUpdateLastReadPageUseCase(interactor: UpdateLastReadPageInteractor): UpdateLastReadPageUseCase

    @Binds
    abstract fun bindGetBookshelfFolderUseCase(interactor: GetBookshelfFolderInteractor): GetBookshelfFolderUseCase

    @Binds
    abstract fun bindDeleteHistoryUseCase(interactor: DeleteHistoryInteractor): DeleteHistoryUseCase

    @Binds
    abstract fun bindGetBookshelfInfoUseCase(interactor: GetBookshelfInfoInteractor): GetBookshelfInfoUseCase

    @Binds
    abstract fun bindGetNextBookUseCase(interactor: GetNextBookInteractor): GetNextBookUseCase

    @Binds
    abstract fun bindGetNextFavoriteBookUseCase(interactor: GetNextFavoriteBookInteractor): GetNextFavoriteBookUseCase

    @Binds
    abstract fun bindUpdateHistoryUseCase(interactor: UpdateHistoryInteractor): UpdateHistoryUseCase

    @Binds
    abstract fun bindGetFileUseCase(interactor: GetFileInteractor): GetFileUseCase

    @Binds
    abstract fun bindDeleteThumbnailsUseCase(interactor: DeleteThumbnailsInteractor): DeleteThumbnailsUseCase

    @Binds
    abstract fun bindGetBookshelfFileUseCase(interactor: GetBookshelfFileInteractor): GetBookshelfFileUseCase

    @Binds
    abstract fun bindAddFavoriteFileUseCase(interactor: AddFavoriteFileInteractor): AddFavoriteFileUseCase

    @Binds
    abstract fun bindRemoveFavoriteFileUseCase(interactor: RemoveFavoriteFileInteractor): RemoveFavoriteFileUseCase

    @Binds
    abstract fun bindCreateFavoriteUseCase(interactor: CreateFavoriteInteractor): CreateFavoriteUseCase

    @Binds
    abstract fun bindGetFavoriteUseCase(interactor: GetFavoriteInteractor): GetFavoriteUseCase

    @Binds
    abstract fun bindDeleteFavoriteUseCase(interactor: DeleteFavoriteInteractor): DeleteFavoriteUseCase

    @Binds
    abstract fun bindUpdateFavoriteUseCase(interactor: UpdateFavoriteInteractor): UpdateFavoriteUseCase

    // Paging

    @Binds
    abstract fun bindPagingFileUseCase(interactor: PagingFileInteractor): PagingFileUseCase

    @Binds
    abstract fun bindPagingQueryFileUseCase(interactor: PagingQueryFileInteractor): PagingQueryFileUseCase

    @Binds
    abstract fun bindPagingBookshelfFolderUseCase(interactor: PagingBookshelfFolderInteractor): PagingBookshelfFolderUseCase

    @Binds
    abstract fun bindPagingFavoriteUseCase(interactor: PagingFavoriteInteractor): PagingFavoriteUseCase

    @Binds
    abstract fun bindPagingFavoriteFileUseCase(interactor: PagingFavoriteFileInteractor): PagingFavoriteFileUseCase

    @Binds
    abstract fun bindPagingReadLaterFileUseCase(interactor: PagingReadLaterFileInteractor): PagingReadLaterFileUseCase

    @Binds
    abstract fun bindPagingHistoryBookUseCase(interactor: PagingHistoryBookInteractor): PagingHistoryBookUseCase

    // Settings
    @Binds
    abstract fun bindManageFolderDisplaySettingsUseCase(interactor: ManageFolderDisplaySettingsInteractor): ManageFolderDisplaySettingsUseCase

    @Binds
    abstract fun bindManageSecuritySettingsUseCase(interactor: ManageSecuritySettingsInteractor): ManageSecuritySettingsUseCase
}