package com.sorrowblue.comicviewer.domain.di

import com.sorrowblue.comicviewer.domain.usecase.AddFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.FullScanLibraryUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFavoriteListUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNavigationHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.RegisterServerUseCase
import com.sorrowblue.comicviewer.domain.usecase.RemoveFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.RemoveLibraryUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.interactor.AddFavoriteBookInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.AddReadLaterIteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.CreateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.DeleteFavoriteInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.FullScanLibraryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetBookInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetFavoriteInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetFavoriteListInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetFileInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetNavigationHistoryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetNextBookInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetServerBookInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetServerBookshelfInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetServerFileInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetServerInfoInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.PagingReadLaterInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.RegisterServerInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.RemoveFavoriteBookInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.RemoveLibraryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.UpdateFavoriteInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.UpdateHistoryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.UpdateLastReadPageInteractor
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingServerUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.interactor.PagingFavoriteBookInteractor
import com.sorrowblue.comicviewer.domain.usecase.paging.interactor.PagingFavoriteInteractor
import com.sorrowblue.comicviewer.domain.usecase.paging.interactor.PagingFileInteractor
import com.sorrowblue.comicviewer.domain.usecase.paging.interactor.PagingQueryFileInteractor
import com.sorrowblue.comicviewer.domain.usecase.paging.interactor.PagingServerInteractor
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.interactor.LoadSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.settings.interactor.ManageBookshelfDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.settings.interactor.ManageBookshelfSettings2Interactor
import com.sorrowblue.comicviewer.domain.usecase.settings.interactor.ManageSecuritySettingsInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class ViewModelBindsModule {

    @Binds
    abstract fun bindRegisterServerUseCase(interactor: RegisterServerInteractor): RegisterServerUseCase

    @Binds
    abstract fun bindLoadSettingsUseCase(interactor: LoadSettingsInteractor): LoadSettingsUseCase

    @Binds
    abstract fun bindRemoveLibraryUseCase(interactor: RemoveLibraryInteractor): RemoveLibraryUseCase

    @Binds
    abstract fun bindFullScanLibraryUseCase(interactor: FullScanLibraryInteractor): FullScanLibraryUseCase

    @Binds
    abstract fun bindGetServerBookUseCase(interactor: GetServerBookInteractor): GetServerBookUseCase

    @Binds
    abstract fun bindGetBookUseCase(interactor: GetBookInteractor): GetBookUseCase

    @Binds
    abstract fun bindAddReadLaterUseCase(interactor: AddReadLaterIteractor): AddReadLaterUseCase

    @Binds
    abstract fun bindGetHistoryUseCase(interactor: GetNavigationHistoryInteractor): GetNavigationHistoryUseCase

    @Binds
    abstract fun bindUpdateLastReadPageUseCase(interactor: UpdateLastReadPageInteractor): UpdateLastReadPageUseCase

    @Binds
    abstract fun bindGetServerBookshelfUseCase(interactor: GetServerBookshelfInteractor): GetServerBookshelfUseCase

    @Binds
    abstract fun bindGetServerInfoUseCase(interactor: GetServerInfoInteractor): GetServerInfoUseCase

    @Binds
    abstract fun bindGetNextBookUseCase(interactor: GetNextBookInteractor): GetNextBookUseCase

    @Binds
    abstract fun bindUpdateHistoryUseCase(interactor: UpdateHistoryInteractor): UpdateHistoryUseCase

    @Binds
    abstract fun bindGetFileUseCase(interactor: GetFileInteractor): GetFileUseCase

    @Binds
    abstract fun bindGetServerFileUseCase(interactor: GetServerFileInteractor): GetServerFileUseCase

    @Binds
    abstract fun bindGetFavoriteListUseCase(interactor: GetFavoriteListInteractor): GetFavoriteListUseCase

    @Binds
    abstract fun bindAddFavoriteBookUseCase(interactor: AddFavoriteBookInteractor): AddFavoriteBookUseCase

    @Binds
    abstract fun bindRemoveFavoriteBookUseCase(interactor: RemoveFavoriteBookInteractor): RemoveFavoriteBookUseCase

    @Binds
    abstract fun bindCreateFavoriteBookUseCase(interactor: CreateFavoriteInteractor): CreateFavoriteUseCase

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
    abstract fun bindPagingServerUseCase(interactor: PagingServerInteractor): PagingServerUseCase

    @Binds
    abstract fun bindPagingFavoriteUseCase(interactor: PagingFavoriteInteractor): PagingFavoriteUseCase

    @Binds
    abstract fun bindPagingFavoriteBookUseCase(interactor: PagingFavoriteBookInteractor): PagingFavoriteBookUseCase

    @Binds
    abstract fun bindPagingReadLaterUseCase(interactor: PagingReadLaterInteractor): PagingReadLaterUseCase

    // Settings
    @Binds
    abstract fun bindManageBookshelfDisplaySettingsUseCase(interactor: ManageBookshelfDisplaySettingsInteractor): ManageBookshelfDisplaySettingsUseCase

    @Binds
    abstract fun bindManageBookshelfSettingsUseCase(interactor: ManageBookshelfSettings2Interactor): ManageBookshelfSettingsUseCase

    @Binds
    abstract fun bindManageSecuritySettingsUseCase(interactor: ManageSecuritySettingsInteractor): ManageSecuritySettingsUseCase
}
