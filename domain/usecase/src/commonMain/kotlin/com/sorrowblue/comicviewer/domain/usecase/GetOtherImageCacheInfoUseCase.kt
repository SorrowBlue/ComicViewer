package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.OtherImageCache

abstract class GetOtherImageCacheInfoUseCase :
    OneShotUseCase<GetOtherImageCacheInfoUseCase.Request, OtherImageCache, Unit>() {
    data object Request : OneShotUseCase.Request
}
