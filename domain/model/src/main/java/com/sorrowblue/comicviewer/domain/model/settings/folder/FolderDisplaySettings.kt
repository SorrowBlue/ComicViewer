package com.sorrowblue.comicviewer.domain.model.settings.folder

import kotlinx.serialization.Serializable

/**
 * フォルダ表示設定
 *
 * @property fileListDisplay ファイル/フォルダの表示方式。
 * @property isSavedThumbnail サムネイルを表示するか
 * @property gridColumnSize グリッド表示時のカラムサイズ
 * @property sortType ソート順所
 * @property showHiddenFiles 隠しファイルを表示するか
 */
@Serializable
data class FolderDisplaySettings(
    val fileListDisplay: FileListDisplay = FolderDisplaySettingsDefaults.fileListDisplay,
    val gridColumnSize: GridColumnSize = FolderDisplaySettingsDefaults.gridColumnSize,
    val sortType: SortType = FolderDisplaySettingsDefaults.sortType,
    val showHiddenFiles: Boolean = FolderDisplaySettingsDefaults.isDisplayHiddenFile,
    val showFilesExtension: Boolean = FolderDisplaySettingsDefaults.isDisplayFileExtension,
    val showThumbnails: Boolean = FolderDisplaySettingsDefaults.isDisplayThumbnail,
    val isSavedThumbnail: Boolean = FolderDisplaySettingsDefaults.isSavedThumbnail,
    val fontSize: Int = FolderDisplaySettingsDefaults.fontSize,
    val thumbnailQuality: Int = FolderDisplaySettingsDefaults.thumbnailQuality,
    val imageFormat: ImageFormat = FolderDisplaySettingsDefaults.imageFormat,
    val imageScale: ImageScale = FolderDisplaySettingsDefaults.imageScale,
    val imageFilterQuality: ImageFilterQuality = FolderDisplaySettingsDefaults.imageFilterQuality,
    val folderThumbnailOrder: FolderThumbnailOrder = FolderDisplaySettingsDefaults.folderThumbnailOrder,
)

object FolderDisplaySettingsDefaults {

    /** ソートタイプ */
    val sortType = SortType.Name(true)

    /** ファイル一覧の表示方法 */
    val fileListDisplay = FileListDisplay.Grid

    /** グリッドリストのカラムサイズ */
    val gridColumnSize = GridColumnSize.Medium

    const val fontSize = 16

    /** 拡張子を表示するか */
    const val isDisplayFileExtension = true

    /** 隠しファイルを表示するか */
    const val isDisplayHiddenFile = false

    /** サムネイルを表示するか */
    const val isDisplayThumbnail = true

    /** サムネイルを保存するか */
    const val isSavedThumbnail = true

    /** 画像フォーマット */
    val imageFormat = ImageFormat.WEBP

    /** サムネイル品質 */
    const val thumbnailQuality = 75

    /** サムネイルの拡大縮小 */
    val imageScale = ImageScale.Fit

    /** サムネイルフィルターの品質 */
    val imageFilterQuality = ImageFilterQuality.Medium

    /** フォルダーサムネイルの順序 */
    val folderThumbnailOrder = FolderThumbnailOrder.NAME
}
