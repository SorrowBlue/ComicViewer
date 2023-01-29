package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.domain.entity.FavoriteBook
import com.sorrowblue.comicviewer.domain.entity.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class GetFavoriteUseCase : FlowUseCase2<GetFavoriteUseCase.Request, Favorite, Unit>() {
    class Request(val favoriteId: FavoriteId) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class UpdateFavoriteUseCase : FlowOneUseCase<UpdateFavoriteUseCase.Request, Favorite, Unit>() {
    class Request(val favorite: Favorite) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class DeleteFavoriteUseCase : FlowOneUseCase<DeleteFavoriteUseCase.Request, Unit, Unit>() {
    class Request(val favoriteId: FavoriteId) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class GetFavoriteListUseCase :
    FlowUseCase<GetFavoriteListUseCase.Request, List<Favorite>, Unit>() {
    class Request(val serverId: ServerId, val filePath: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class AddFavoriteBookUseCase :
    FlowOneUseCase<AddFavoriteBookUseCase.Request, Unit, Unit>() {
    class Request(val favoriteBook: FavoriteBook) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class CreateFavoriteUseCase : FlowOneUseCase<CreateFavoriteUseCase.Request, Unit, Unit>() {
    class Request(val title: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}

abstract class RemoveFavoriteBookUseCase :
    FlowOneUseCase<RemoveFavoriteBookUseCase.Request, Unit, Unit>() {
    class Request(val favoriteBook: FavoriteBook) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
