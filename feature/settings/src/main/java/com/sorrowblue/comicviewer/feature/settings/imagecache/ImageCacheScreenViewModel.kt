package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookshelfImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetOtherImageCacheInfoUseCase
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class ImageCacheScreenViewModel(
    val getBookshelfImageCacheInfoUseCase: GetBookshelfImageCacheInfoUseCase,
    val getOtherImageCacheInfoUseCase: GetOtherImageCacheInfoUseCase,
    val clearImageCacheUseCase: ClearImageCacheUseCase,
) : ViewModel()
