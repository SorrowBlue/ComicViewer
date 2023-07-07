package com.sorrowblue.comicviewer.bookshelf.manage.device

import androidx.lifecycle.SavedStateHandle
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeBookshelfDeviceEditViewModel :
    BookshelfDeviceEditViewModel(
        object : GetBookshelfInfoUseCase() {
            override fun run(request: Request): Flow<Result<BookshelfFolder, GetLibraryInfoError>> {
                return flowOf(
                    Result.Success(
                        BookshelfFolder(
                            InternalStorage(BookshelfId(0), "Fake Display Name", 0) to Folder(
                                BookshelfId(0),
                                "",
                                "",
                                "",
                                0,
                                0
                            )
                        )
                    )
                )
            }
        },
        object : RegisterBookshelfUseCase() {
            override suspend fun run(request: Request): Result<Bookshelf, RegisterBookshelfError> {
                TODO("Not yet implemented")
            }
        },
        SavedStateHandle()
    )
