package com.sorrowblue.comicviewer.feature.bookshelf.info.navigation

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfInfoNavKey(val id: BookshelfId) : ScreenKey

@Serializable
internal data class BookshelfDeleteNavKey(val id: BookshelfId) : ScreenKey

@Serializable
internal data class BookshelfNotificationNavKey(val scanType: ScanType) : ScreenKey
