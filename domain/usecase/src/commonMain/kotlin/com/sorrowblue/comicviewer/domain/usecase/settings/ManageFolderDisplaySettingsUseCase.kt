/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.service.settings.FolderSortSettingsService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

/**
 * フォルダ表示設定の取得・編集およびフォルダー別ソート設定の操作を行うユースケース。
 */
interface ManageFolderDisplaySettingsUseCase : ManageSettingsUseCase<FolderDisplaySettings> {

    /**
     * 指定されたフォルダーのソート種別を更新します。
     * 個別設定モードの場合は個別設定を、そうでない場合は全体設定を更新します。
     *
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     * @param sortType 新しいソート種別
     * @return 値の変更が生じた場合は true、変更がなかった場合は false
     */
    suspend fun updateSortType(bookshelfId: BookshelfId, path: String, sortType: SortType): Boolean

    /**
     * 指定されたフォルダーの個別設定モードをトグルします。
     * 個別設定が存在すれば削除し、存在しなければ現在の全体ソート設定を引き継いで個別設定を追加します。
     *
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     */
    suspend fun toggleFolderScopeOnly(bookshelfId: BookshelfId, path: String)

    /**
     * 指定されたフォルダーの子フォルダー適用フラグをトグルします。
     *
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     */
    suspend fun toggleIncludeSubfolders(bookshelfId: BookshelfId, path: String)
}

@Inject
@ContributesBinding(AppScope::class)
internal class ManageFolderDisplaySettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
    private val folderSortSettingsService: FolderSortSettingsService,
) : ManageFolderDisplaySettingsUseCase {

    override val settings: Flow<FolderDisplaySettings> = settingsRepository.folderDisplaySettings

    override suspend fun edit(action: (FolderDisplaySettings) -> FolderDisplaySettings) {
        settingsRepository.updateFolderDisplaySettings(action::invoke)
    }

    override suspend fun updateSortType(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Boolean {
        var isChanged = false
        edit { current ->
            val result = folderSortSettingsService.updateSortType(
                settings = current,
                bookshelfId = bookshelfId,
                path = path,
                sortType = sortType,
            )
            isChanged = result.isChanged
            result.settings
        }
        return isChanged
    }

    override suspend fun toggleFolderScopeOnly(bookshelfId: BookshelfId, path: String) {
        edit { current ->
            folderSortSettingsService.toggleFolderScopeOnly(
                settings = current,
                bookshelfId = bookshelfId,
                path = path,
            )
        }
    }

    override suspend fun toggleIncludeSubfolders(bookshelfId: BookshelfId, path: String) {
        edit { current ->
            folderSortSettingsService.toggleIncludeSubfolders(
                settings = current,
                bookshelfId = bookshelfId,
                path = path,
            )
        }
    }
}
