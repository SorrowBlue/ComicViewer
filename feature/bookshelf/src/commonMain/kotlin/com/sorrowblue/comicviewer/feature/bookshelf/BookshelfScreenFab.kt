/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.feature.bookshelf.nav.BookshelfWizardNavKey
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.FloatingActionContent
import com.sorrowblue.comicviewer.framework.ui.adaptive.FloatingActionButtonState
import com.sorrowblue.comicviewer.framework.ui.navigation3.LocalNavigator
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_btn_add
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoSet
import org.jetbrains.compose.resources.stringResource

@ContributesIntoSet(AppScope::class)
internal class BookshelfScreenFab : FloatingActionContent {
    override val navKey = BookshelfNavKey::class

    override val allowRail = true

    @Composable
    override fun Content(
        state: FloatingActionButtonState,
        modifier: Modifier,
        elevation: FloatingActionButtonElevation,
        expanded: Boolean,
    ) {
        val navigator = LocalNavigator.current
        SmallExtendedFloatingActionButton(
            onClick = {
                navigator.navigate(BookshelfWizardNavKey.Selection)
            },
            modifier = modifier.testTag("BookshelfFab"),
            elevation = elevation,
            text = { Text(text = stringResource(Res.string.bookshelf_btn_add)) },
            icon = {
                Icon(imageVector = ComicIcons.Add, contentDescription = null)
            },
            expanded = expanded,
        )
    }
}
