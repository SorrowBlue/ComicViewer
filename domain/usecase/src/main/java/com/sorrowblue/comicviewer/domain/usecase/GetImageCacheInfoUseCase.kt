package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.ImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.Resource

abstract class GetImageCacheInfoUseCase :
    UseCase<GetImageCacheInfoUseCase.Request, List<ImageCacheInfo>, GetImageCacheInfoUseCase.Error>() {

    object Request : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}
