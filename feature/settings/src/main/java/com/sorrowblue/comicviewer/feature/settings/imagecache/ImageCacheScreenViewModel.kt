package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookshelfImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetOtherImageCacheInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ImageCacheScreenViewModel @Inject constructor(
    val getBookshelfImageCacheInfoUseCase: GetBookshelfImageCacheInfoUseCase,
    val getOtherImageCacheInfoUseCase: GetOtherImageCacheInfoUseCase,
    val clearImageCacheUseCase: ClearImageCacheUseCase,
) : ViewModel()
