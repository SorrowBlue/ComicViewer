/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.book

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * コミック閲覧中の前後ブック移動における、探索ポリシー・ソート種別の解決、
 * 親フォルダーパス抽出、およびモデル検証を司るドメインサービス。
 */
interface BookNavigationService {

    /**
     * フォルダー閲覧時において、親フォルダーに適用される有効なソート種別を解決します。
     * 個別設定や子フォルダーへの継承設定が存在する場合はそれを優先し、なければ全体設定を返します。
     *
     * @param settings フォルダ表示設定
     * @param bookshelfId 本棚ID
     * @param parentPath 親フォルダーパス
     * @return 適用されるソート種別
     */
    fun resolveFolderSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        parentPath: String,
    ): SortType

    /**
     * コレクション閲覧時において、コレクションの種類に応じた有効なソート種別を解決します。
     * [SmartCollection] の場合は検索条件のソート種別を、[BasicCollection] の場合は表示設定のソート種別を返します。
     *
     * @param collection コレクション
     * @param settings フォルダ表示設定
     * @return 適用されるソート種別
     */
    fun resolveCollectionSortType(
        collection: Collection,
        settings: FolderDisplaySettings,
    ): SortType

    /**
     * ファイルパスから親フォルダーのパスを抽出します。
     *
     * @param path 対象のファイルまたはフォルダーパス
     * @return 親フォルダーパス（ルート直下または親がない場合は空文字列）
     */
    fun extractParentPath(path: String): String

    /**
     * 取得されたファイルが有効な [Book] であるかを検証します。
     *
     * @param file 検証対象のファイル
     * @return 有効な [Book] の場合はそのインスタンス、それ以外または null の場合は null
     */
    fun validateBook(file: File?): Book?

    companion object {
        /**
         * [BookNavigationService] のインスタンスを生成して返します。
         *
         * @return [BookNavigationService] のインスタンス
         */
        operator fun invoke(): BookNavigationService = BookNavigationServiceImpl()
    }
}

@ContributesBinding(AppScope::class)
@Inject
class BookNavigationServiceImpl : BookNavigationService {

    override fun resolveFolderSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        parentPath: String,
    ): SortType = settings.currentSortType(
        bookshelfId = bookshelfId,
        path = parentPath,
    )

    override fun resolveCollectionSortType(
        collection: Collection,
        settings: FolderDisplaySettings,
    ): SortType = when (collection) {
        is SmartCollection -> collection.searchCondition.sortType
        is BasicCollection -> settings.sortType
    }

    override fun extractParentPath(path: String): String {
        val trimmed = path.removeSuffix("/")
        val lastIndex = trimmed.lastIndexOf('/')
        return if (lastIndex > 0) {
            trimmed.substring(0, lastIndex)
        } else {
            ""
        }
    }

    override fun validateBook(file: File?): Book? = file as? Book
}
