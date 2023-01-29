package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.AddFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFavoriteListUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.RemoveFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : GetFavoriteUseCase() {

    override fun run(request: Request): Flow<Result<Favorite, Unit>> {
        return favoriteRepository.get(request.favoriteId).map { Result.Success(it) }
    }
}

internal class UpdateFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : UpdateFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Favorite, Unit> {
        return Result.Success(favoriteRepository.update(request.favorite))
    }
}

internal class DeleteFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : DeleteFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(favoriteRepository.delete(request.favoriteId))
    }
}

internal class GetFavoriteListInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : GetFavoriteListUseCase() {
    override suspend fun run(request: Request): Result<List<Favorite>, Unit> {
        return Result.Success(
            favoriteRepository.getFavoriteList(
                request.serverId,
                request.filePath
            )
        )
    }
}

internal class AddFavoriteBookInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : AddFavoriteBookUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteRepository.add(request.favoriteBook)
        )
    }
}

internal class CreateFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : CreateFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteRepository.create(request.title)
        )
    }
}

internal class RemoveFavoriteBookInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : RemoveFavoriteBookUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteRepository.remove(request.favoriteBook)
        )
    }
}
