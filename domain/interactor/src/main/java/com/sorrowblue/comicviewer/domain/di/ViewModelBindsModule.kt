package com.sorrowblue.comicviewer.domain.di

import com.sorrowblue.comicviewer.domain.usecase.FullScanLibraryUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.ServerBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetServerFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.LoadFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.PagingQueryFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.RegisterLibraryUseCase
import com.sorrowblue.comicviewer.domain.usecase.RemoveLibraryUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.interactor.FullScanLibraryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetFileInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetHistoryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.ServerBookshelfInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetNextComicInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetServerBookInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetServerBookshelfInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.GetServerFileInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.LoadFileInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.LoadSettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.ManageBookshelfDisplaySettingsInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.ManageBookshelfSettings2Interactor
import com.sorrowblue.comicviewer.domain.usecase.interactor.PagingQueryFileUseCaseInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.RegisterLibraryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.RemoveLibraryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.UpdateHistoryInteractor
import com.sorrowblue.comicviewer.domain.usecase.interactor.UpdateLastReadPageInteractor
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookshelfSettingsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class ViewModelBindsModule {

    @Binds
    abstract fun bindLoadFileUseCase(interactor: LoadFileInteractor): LoadFileUseCase

    @Binds
    abstract fun bindPagingQueryFileUseCaseUseCase(interactor: PagingQueryFileUseCaseInteractor): PagingQueryFileUseCase

    @Binds
    abstract fun bindRegisterLibraryUseCase(interactor: RegisterLibraryInteractor): RegisterLibraryUseCase

    @Binds
    abstract fun bindManageBookshelfSettings(interactor: ManageBookshelfDisplaySettingsInteractor): ManageBookshelfDisplaySettingsUseCase

    @Binds
    abstract fun bindManageBookshelfSettings2UseCase(interactor: ManageBookshelfSettings2Interactor): ManageBookshelfSettingsUseCase

    @Binds
    abstract fun bindLoadSettings(interactor: LoadSettingsInteractor): LoadSettingsUseCase

    @Binds
    abstract fun bindRemoveLibraryUseCase(interactor: RemoveLibraryInteractor): RemoveLibraryUseCase

    @Binds
    abstract fun bindFullScanLibraryUseCase(interactor: FullScanLibraryInteractor): FullScanLibraryUseCase

    @Binds
    abstract fun bindGetLibraryFileUseCase(interactor: GetServerBookInteractor): GetServerBookUseCase

    @Binds
    abstract fun bindGetHistoryUseCase(interactor: GetHistoryInteractor): GetHistoryUseCase

    @Binds
    abstract fun bindUpdateLastReadPageUseCase(interactor: UpdateLastReadPageInteractor): UpdateLastReadPageUseCase

    @Binds
    abstract fun bindServerBookshelfUseCase(interactor: ServerBookshelfInteractor): ServerBookshelfUseCase

    @Binds
    abstract fun bindGetNextComicUseCase(interactor: GetNextComicInteractor): GetNextComicUseCase

    @Binds
    abstract fun bindUpdateHistoryUseCase(interactor: UpdateHistoryInteractor): UpdateHistoryUseCase

    @Binds
    abstract fun bindGetFileUseCase(interactor: GetFileInteractor): GetFileUseCase

    @Binds
    abstract fun bindGetServerBookshelfUseCase(interactor: GetServerBookshelfInteractor): GetServerBookshelfUseCase

    @Binds
    abstract fun bindGetServerFileUseCase(interactor: GetServerFileInteractor): GetServerFileUseCase
}
