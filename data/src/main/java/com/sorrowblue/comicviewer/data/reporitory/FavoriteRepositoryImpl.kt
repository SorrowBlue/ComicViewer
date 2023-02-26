package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.SortType
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.data.datasource.FavoriteBookLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.data.toFavorite
import com.sorrowblue.comicviewer.data.toFavoriteBookModel
import com.sorrowblue.comicviewer.data.toFile
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class FavoriteFileRepositoryImpl @Inject constructor(
    private val favoriteBookLocalDataSource: FavoriteBookLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository
) : FavoriteFileRepository {

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        favoriteId: FavoriteId
    ): Flow<PagingData<File>> {
        return favoriteBookLocalDataSource.pagingSource(
            pagingConfig,
            FavoriteModelId(favoriteId.value)
        ) {
            val settings = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }
            when (settings.sort) {
                FolderDisplaySettings.Sort.NAME -> SortType.NAME(settings.order == FolderDisplaySettings.Order.ASC)
                FolderDisplaySettings.Sort.DATE -> SortType.DATE(settings.order == FolderDisplaySettings.Order.ASC)
                FolderDisplaySettings.Sort.SIZE -> SortType.SIZE(settings.order == FolderDisplaySettings.Order.ASC)
            }
        }.map { it.map(FileModel::toFile) }
    }

}

internal class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource
) : FavoriteRepository {

    override fun get(favoriteId: FavoriteId): Flow<Favorite> {
        return favoriteLocalDataSource.get(FavoriteModelId(favoriteId.value))
            .map(FavoriteModel::toFavorite).flowOn(Dispatchers.IO)
    }

    override suspend fun update(favorite: Favorite): Favorite {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.update(
                FavoriteModel(
                    FavoriteModelId(favorite.id.value),
                    favorite.name,
                    favorite.count
                )
            ).toFavorite()
        }
    }

    override suspend fun delete(favoriteId: FavoriteId) {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.delete(FavoriteModelId(favoriteId.value))
        }
    }

    override suspend fun getFavoriteList(bookshelfId: BookshelfId, filePath: String): List<Favorite> {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.getFavoriteList(BookshelfModelId(bookshelfId.value), filePath)
                .map { it.toFavorite() }
        }
    }

    override suspend fun add(favoriteFile: FavoriteFile) {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.add(favoriteFile.toFavoriteBookModel())
        }
    }

    override suspend fun remove(favoriteFile: FavoriteFile) {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.remove(favoriteFile.toFavoriteBookModel())
        }
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Favorite>> {
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
