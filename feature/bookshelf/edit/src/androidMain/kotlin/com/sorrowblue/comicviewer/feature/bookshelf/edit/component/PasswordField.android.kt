package com.sorrowblue.comicviewer.feature.bookshelf.edit.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics

internal actual fun Modifier.semanticsPasswordForAndroid(): Modifier = semantics {
    contentType = ContentType.Password
}

internal actual fun Modifier.semanticsUsernameForAndroid(): Modifier = semantics {
    contentType = ContentType.Username
}
