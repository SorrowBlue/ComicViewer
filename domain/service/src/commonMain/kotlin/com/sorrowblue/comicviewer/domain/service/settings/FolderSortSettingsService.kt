/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.settings

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderScopeOnly
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * フォルダ個別ソート設定の判定・有効ソート種別の解決・更新ルールを司るドメインサービス。
 */
interface FolderSortSettingsService {

    /**
     * 指定されたフォルダーが個別ソート設定を持っているかを判定します。
     *
     * @param settings フォルダ表示設定
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     * @return 個別設定が存在する場合は true
     */
    fun isFolderScopeOnly(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): Boolean

    /**
     * 指定されたフォルダーに適用される有効なソート種別を解決します。
     * 個別設定が存在すればそれを、存在しなければ全体設定を返します。
     *
     * @param settings フォルダ表示設定
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     * @return 適用されるソート種別
     */
    fun resolveSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): SortType

    /**
     * 指定されたフォルダーのソート種別を更新した新しい設定を生成します。
     * 個別設定モードの場合は個別設定を、そうでない場合は全体設定を更新します。
     *
     * @param settings 現在のフォルダ表示設定
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     * @param sortType 新しいソート種別
     * @return 更新後の設定と、値の変更が生じたかどうかの結果
     */
    fun updateSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): UpdateResult

    /**
     * 指定されたフォルダーが子フォルダーへの適用を有効にしているかを判定します。
     *
     * @param settings フォルダ表示設定
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     * @return 子フォルダーへの適用が有効な場合は true
     */
    fun isIncludeSubfolders(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): Boolean

    /**
     * 指定されたフォルダーの個別設定モードをトグルします。
     * 個別設定が存在すれば削除し、存在しなければ現在の全体ソート設定を引き継いで個別設定を追加します。
     *
     * @param settings 現在のフォルダ表示設定
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     * @return 更新後のフォルダ表示設定
     */
    fun toggleFolderScopeOnly(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderDisplaySettings

    /**
     * 指定されたフォルダーの子フォルダー適用フラグをトグルします。
     * 個別設定が存在すればフラグを反転し、存在しなければ子フォルダー適用を有効にした個別設定を追加します。
     *
     * @param settings 現在のフォルダ表示設定
     * @param bookshelfId 本棚ID
     * @param path フォルダーパス
     * @return 更新後のフォルダ表示設定
     */
    fun toggleIncludeSubfolders(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderDisplaySettings

    /**
     * ソート種別更新の結果を表すデータクラス。
     *
     * @property settings 更新後の設定
     * @property isChanged 設定値に変更があった場合は true
     */
    data class UpdateResult(val settings: FolderDisplaySettings, val isChanged: Boolean)
}

@ContributesBinding(AppScope::class)
@Inject
class FolderSortSettingsServiceImpl : FolderSortSettingsService {

    override fun isFolderScopeOnly(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): Boolean = settings.isFolderScopeOnly(bookshelfId, path)

    override fun isIncludeSubfolders(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): Boolean = settings.isIncludeSubfolders(bookshelfId, path)

    override fun resolveSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): SortType = settings.currentSortType(bookshelfId, path)

    override fun updateSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): FolderSortSettingsService.UpdateResult {
        val existing = settings.folderScopeOnlyList.find {
            it.bookshelfId == bookshelfId && it.path == path
        }
        return if (existing != null) {
            if (existing.sortType != sortType) {
                val updatedList = settings.folderScopeOnlyList.map {
                    if (it.bookshelfId == bookshelfId && it.path == path) {
                        it.copy(sortType = sortType)
                    } else {
                        it
                    }
                }
                FolderSortSettingsService.UpdateResult(
                    settings = settings.copy(folderScopeOnlyList = updatedList),
                    isChanged = true,
                )
            } else {
                FolderSortSettingsService.UpdateResult(settings = settings, isChanged = false)
            }
        } else {
            if (settings.sortType != sortType) {
                FolderSortSettingsService.UpdateResult(
                    settings = settings.copy(sortType = sortType),
                    isChanged = true,
                )
            } else {
                FolderSortSettingsService.UpdateResult(settings = settings, isChanged = false)
            }
        }
    }

    override fun toggleFolderScopeOnly(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderDisplaySettings {
        val existing = settings.folderScopeOnlyList.find {
            it.bookshelfId == bookshelfId && it.path == path
        }
        val newList = if (existing != null) {
            settings.folderScopeOnlyList - existing
        } else {
            settings.folderScopeOnlyList + FolderScopeOnly(
                bookshelfId = bookshelfId,
                path = path,
                sortType = resolveSortType(settings, bookshelfId, path),
                includeSubfolders = false,
            )
        }
        return settings.copy(folderScopeOnlyList = newList)
    }

    override fun toggleIncludeSubfolders(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        path: String,
    ): FolderDisplaySettings {
        val existing = settings.folderScopeOnlyList.find {
            it.bookshelfId == bookshelfId && it.path == path
        }
        val newList = if (existing != null) {
            settings.folderScopeOnlyList.map {
                if (it.bookshelfId == bookshelfId && it.path == path) {
                    it.copy(includeSubfolders = !it.includeSubfolders)
                } else {
                    it
                }
            }
        } else {
            settings.folderScopeOnlyList + FolderScopeOnly(
                bookshelfId = bookshelfId,
                path = path,
                sortType = resolveSortType(settings, bookshelfId, path),
                includeSubfolders = true,
            )
        }
        return settings.copy(folderScopeOnlyList = newList)
    }
}
