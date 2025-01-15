package com.sorrowblue.comicviewer.feature.bookshelf.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_btn_add
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfFab(expanded: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        expanded = expanded,
        onClick = onClick,
        text = { Text(text = stringResource(Res.string.bookshelf_btn_add)) },
        icon = { Icon(imageVector = ComicIcons.Add, contentDescription = null) },
        modifier = modifier
    )
}
