package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FavoriteModel
import com.sorrowblue.comicviewer.data.common.FavoriteModelId
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SortType
import com.sorrowblue.comicviewer.data.datasource.FavoriteBookLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.data.toFavorite
import com.sorrowblue.comicviewer.data.toFavoriteBookModel
import com.sorrowblue.comicviewer.data.toFile
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.domain.entity.FavoriteBook
import com.sorrowblue.comicviewer.domain.entity.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.repository.FavoriteBookRepository
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class FavoriteBookRepositoryImpl @Inject constructor(
    private val favoriteBookLocalDataSource: FavoriteBookLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository
) : FavoriteBookRepository {

    override fun pagingSource(
        pagingConfig: PagingConfig,
        favoriteId: FavoriteId
    ): Flow<PagingData<File>> {
        return favoriteBookLocalDataSource.pagingSource(
            pagingConfig,
            FavoriteModelId(favoriteId.value)
        ) {
            val settings = runBlocking { settingsCommonRepository.bookshelfDisplaySettings.first() }
            when (settings.sort) {
                BookshelfDisplaySettings.Sort.NAME -> SortType.NAME(settings.order == BookshelfDisplaySettings.Order.ASC)
                BookshelfDisplaySettings.Sort.DATE -> SortType.DATE(settings.order == BookshelfDisplaySettings.Order.ASC)
                BookshelfDisplaySettings.Sort.SIZE -> SortType.SIZE(settings.order == BookshelfDisplaySettings.Order.ASC)
            }
        }.map { it.map(FileModel::toFile) }
    }

}

internal class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource
) : FavoriteRepository {

    override suspend fun get(favoriteId: FavoriteId): Favorite {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.get(FavoriteModelId(favoriteId.value)).toFavorite()
        }
    }

    override suspend fun update(favorite: Favorite): Favorite {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.update(FavoriteModel(FavoriteModelId(favorite.id.value), favorite.name, favorite.count)).toFavorite()
        }
    }

    override suspend fun delete(favoriteId: FavoriteId) {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.delete(FavoriteModelId(favoriteId.value))
        }
    }

    override suspend fun getFavoriteList(serverId: ServerId, filePath: String): List<Favorite> {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.getFavoriteList(ServerModelId(serverId.value), filePath)
                .map { it.toFavorite() }
        }
    }

    override suspend fun add(favoriteBook: FavoriteBook) {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.add(favoriteBook.toFavoriteBookModel())
        }
    }

    override suspend fun remove(favoriteBook: FavoriteBook) {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.remove(favoriteBook.toFavoriteBookModel())
        }
    }

    override fun pagingSourceCount(pagingConfig: PagingConfig): Flow<PagingData<Favorite>> {
        return favoriteLocalDataSource.pagingSourceCount(pagingConfig).map {
            it.map {
                Favorite(FavoriteId(it.id.value), it.name, it.count)
            }
        }
    }

    override suspend fun create(title: String) {
        withContext(Dispatchers.IO) {
            favoriteLocalDataSource.create(FavoriteModel(title))
        }
    }
}
