package com.sorrowblue.comicviewer.domain.model

val String.extension get() = substringAfterLast('.', "").lowercase()
