package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.OtherImageCache

abstract class GetOtherImageCacheInfoUseCase :
    OneShotUseCase<GetOtherImageCacheInfoUseCase.Request, OtherImageCache, Unit>() {
    object Request : OneShotUseCase.Request
}
