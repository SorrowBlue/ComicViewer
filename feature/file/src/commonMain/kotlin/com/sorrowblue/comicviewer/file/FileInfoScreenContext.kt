package com.sorrowblue.comicviewer.file

import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFolderBookThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenContext
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Scope

@Scope
annotation class FileInfoScreenScope

@GraphExtension(FileInfoScreenScope::class)
interface FileInfoScreenContext : ScreenContext {

    val getFileUseCase: GetFileUseCase
    val getFileAttributeUseCase: GetFileAttributeUseCase
    val existsReadlaterUseCase: ExistsReadlaterUseCase
    val pagingFolderBookThumbnailsUseCase: PagingFolderBookThumbnailsUseCase
    val addReadLaterUseCase: AddReadLaterUseCase
    val deleteReadLaterUseCase: DeleteReadLaterUseCase

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createFileInfoScreenContext(): FileInfoScreenContext
    }
}
