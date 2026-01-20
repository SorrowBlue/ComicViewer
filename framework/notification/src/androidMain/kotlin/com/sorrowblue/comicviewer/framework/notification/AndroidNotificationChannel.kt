package com.sorrowblue.comicviewer.framework.notification

import android.app.NotificationManager

enum class AndroidNotificationChannel(
    val id: String,
    val nameRes: Int,
    val descriptionRes: Int,
    val important: Int,
    val enable: Boolean = true,
) {
    SCAN_BOOKSHELF(
        id = "bookshelf_scan",
        nameRes = R.string.framework_notification_channel_name_bookshelf_scan,
        descriptionRes = R.string.framework_notification_channel_description_bookshelf_scan,
        important = NotificationManager.IMPORTANCE_LOW,
    ),
    DOWNLOAD(
        id = "download",
        nameRes = R.string.framework_notification_channel_name_bookshelf_scan,
        descriptionRes = R.string.framework_notification_channel_description_bookshelf_scan,
        important = NotificationManager.IMPORTANCE_NONE,
        enable = false,
    ),
}
