package com.sorrowblue.comicviewer.domain.model

fun String.extension() = substringAfterLast('.', "").lowercase()
