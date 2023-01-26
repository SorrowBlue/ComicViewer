package com.sorrowblue.comicviewer.data.database.entity

import androidx.room.Embedded

internal class ServerFile(
    @Embedded val server: Server,
    @Embedded val file: File
)
