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
import com.sorrowblue.comicviewer.domain.service.settings.FolderSortSettingsService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

/**
 * コミック閲覧中の前後ブック移動における、探索ポリシー・ソート種別の解決、
 * 親フォルダーパス抽出、モデル検証、およびメモリ上ファイル一覧からの探索を司るドメインサービス。
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

    /**
     * メモリ上のファイル一覧から、ソート条件に従って指定されたパスの次または前の [Book] を探索します。
     *
     * @param files ファイル一覧
     * @param currentPath 現在の本のパス
     * @param isNext 次の本を探索する場合は true、前の本を探索する場合は false
     * @param sortType ソート種別
     * @return 探索された [Book]（見つからない場合や範囲外の場合は null）
     */
    fun findNextBook(
        files: List<File>,
        currentPath: String,
        isNext: Boolean,
        sortType: SortType,
    ): Book?

    companion object {
        /**
         * [BookNavigationService] のインスタンスを生成して返します。
         *
         * @param folderSortSettingsService フォルダ個別ソート設定サービス
         * @return [BookNavigationService] のインスタンス
         */
        operator fun invoke(
            folderSortSettingsService: FolderSortSettingsService = FolderSortSettingsService(),
        ): BookNavigationService = BookNavigationServiceImpl(
            folderSortSettingsService = folderSortSettingsService,
        )
    }
}

@ContributesBinding(AppScope::class)
@Inject
class BookNavigationServiceImpl(private val folderSortSettingsService: FolderSortSettingsService) :
    BookNavigationService {

    override fun resolveFolderSortType(
        settings: FolderDisplaySettings,
        bookshelfId: BookshelfId,
        parentPath: String,
    ): SortType = folderSortSettingsService.resolveSortType(
        settings = settings,
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

    override fun findNextBook(
        files: List<File>,
        currentPath: String,
        isNext: Boolean,
        sortType: SortType,
    ): Book? {
        val comparator = when (sortType) {
            is SortType.Name -> {
                val base = compareBy<Book> {
                    if (it.sortIndex >=
                        0
                    ) {
                        it.sortIndex
                    } else {
                        Int.MAX_VALUE
                    }
                }
                    .thenBy { it.name }
                if (sortType.isAsc) base else base.reversed()
            }

            is SortType.Date -> {
                val base = compareBy<Book> { it.lastModifier }.thenBy { it.name }
                if (sortType.isAsc) base else base.reversed()
            }

            is SortType.Size -> {
                val base = compareBy<Book> { it.size }.thenBy { it.name }
                if (sortType.isAsc) base else base.reversed()
            }
        }

        val books = files.filterIsInstance<Book>().sortedWith(comparator)
        val currentIndex = books.indexOfFirst { it.path == currentPath }
        if (currentIndex == -1) return null

        val targetIndex = if (isNext) currentIndex + 1 else currentIndex - 1
        return books.getOrNull(targetIndex)
    }
}
