package com.sorrowblue.comicviewer.domain.di

import com.sorrowblue.comicviewer.domain.interactor.AddReadLaterInteractor
import com.sorrowblue.comicviewer.domain.interactor.GetNavigationHistoryInteractor
import com.sorrowblue.comicviewer.domain.interactor.ScanBookshelfInteractor
import com.sorrowblue.comicviewer.domain.interactor.UpdateHistoryInteractor
import com.sorrowblue.comicviewer.domain.interactor.bookshelf.GetBookshelfBookInteractor
import com.sorrowblue.comicviewer.domain.interactor.bookshelf.GetBookshelfFileInteractor
import com.sorrowblue.comicviewer.domain.interactor.bookshelf.GetBookshelfFolderInteractor
import com.sorrowblue.comicviewer.domain.interactor.bookshelf.GetBookshelfInfoInteractor
import com.sorrowblue.comicviewer.domain.interactor.bookshelf.RegisterBookshelfInteractor
import com.sorrowblue.comicviewer.domain.interactor.bookshelf.RemoveBookshelfInteractor
import com.sorrowblue.comicviewer.domain.interactor.favorite.AddFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.interactor.favorite.CreateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.interactor.favorite.DeleteFavoriteInteractor
import com.sorrowblue.comicviewer.domain.interactor.favorite.GetFavoriteInteractor
import com.sorrowblue.comicviewer.domain.interactor.favorite.GetFavoriteListInteractor
import com.sorrowblue.comicviewer.domain.interactor.favorite.RemoveFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.interactor.favorite.UpdateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.interactor.file.GetBookInteractor
import com.sorrowblue.comicviewer.domain.interactor.file.GetFileInteractor
import com.sorrowblue.comicviewer.domain.interactor.file.GetNextBookInteractor
import com.sorrowblue.comicviewer.domain.interactor.file.UpdateLastReadPageInteractor
import com.sorrowblue.comicviewer.domain.interactor.paging.PagingBookshelfFolderInteractor
import com.sorrowblue.comicviewer.domain.interactor.paging.PagingFavoriteFileInteractor
import com.sorrowblue.comicviewer.domain.interactor.paging.PagingFavoriteInteractor
import com.sorrowblue.comicviewer.domain.interactor.paging.PagingFileInteractor
import com.sorrowblue.comicviewer.domain.interactor.paging.PagingQueryFileInteractor
import com.sorrowblue.comicviewer.domain.interactor.paging.PagingReadLaterFileInteractor
import com.sorrowblue.comicviewer.domain.interactor.settings.LoadSettingsInteractor
import com.sorrowblue.comicviewer.domain.interactor.settings.ManageFolderDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.interactor.settings.ManageFolderSettingsInteractor
import com.sorrowblue.comicviewer.domain.interactor.settings.ManageSecuritySettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfFolderUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteListUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class ViewModelBindsModule {

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
    abstract fun bindAddReadLaterUseCase(interactor: AddReadLaterInteractor): AddReadLaterUseCase

    @Binds
    abstract fun bindGetNavigationHistoryUseCase(interactor: GetNavigationHistoryInteractor): GetNavigationHistoryUseCase

    @Binds
    abstract fun bindUpdateLastReadPageUseCase(interactor: UpdateLastReadPageInteractor): UpdateLastReadPageUseCase

    @Binds
    abstract fun bindGetBookshelfFolderUseCase(interactor: GetBookshelfFolderInteractor): GetBookshelfFolderUseCase

    @Binds
    abstract fun bindGetBookshelfInfoUseCase(interactor: GetBookshelfInfoInteractor): GetBookshelfInfoUseCase

    @Binds
    abstract fun bindGetNextBookUseCase(interactor: GetNextBookInteractor): GetNextBookUseCase

    @Binds
    abstract fun bindUpdateHistoryUseCase(interactor: UpdateHistoryInteractor): UpdateHistoryUseCase

    @Binds
    abstract fun bindGetFileUseCase(interactor: GetFileInteractor): GetFileUseCase

    @Binds
    abstract fun bindGetBookshelfFileUseCase(interactor: GetBookshelfFileInteractor): GetBookshelfFileUseCase

    @Binds
    abstract fun bindGetFavoriteListUseCase(interactor: GetFavoriteListInteractor): GetFavoriteListUseCase

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

    // Settings
    @Binds
    abstract fun bindManageFolderDisplaySettingsUseCase(interactor: ManageFolderDisplaySettingsInteractor): ManageFolderDisplaySettingsUseCase

    @Binds
    abstract fun bindManageFolderSettingsUseCase(interactor: ManageFolderSettingsInteractor): ManageFolderSettingsUseCase

    @Binds
    abstract fun bindManageSecuritySettingsUseCase(interactor: ManageSecuritySettingsInteractor): ManageSecuritySettingsUseCase
}
