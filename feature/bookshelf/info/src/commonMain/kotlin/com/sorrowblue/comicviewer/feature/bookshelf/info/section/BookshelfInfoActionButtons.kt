package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_delete
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_btn_edit
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BottomActions(enabled: Boolean, onRemoveClick: () -> Unit, onEditClick: () -> Unit) {
    val scaffoldDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfo())
    val singlePane by remember(scaffoldDirective.maxHorizontalPartitions) {
        mutableStateOf(scaffoldDirective.maxHorizontalPartitions == 1)
    }
    val containerColor by animateColorAsState(
        if (singlePane) ComicTheme.colorScheme.surface else ComicTheme.colorScheme.surfaceContainerHigh,
    )
    Surface(color = containerColor) {
        Column {
            HorizontalDivider()
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                    ),
                )
                    .padding(vertical = 8.dp, horizontal = 24.dp),
            ) {
                OutlinedButton(
                    onClick = onRemoveClick,
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    enabled = enabled,
                    modifier = Modifier.testTag("DeleteButton"),
                ) {
                    Icon(imageVector = ComicIcons.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(Res.string.bookshelf_info_btn_delete))
                }
                FilledTonalButton(
                    onClick = onEditClick,
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    enabled = enabled,
                    modifier = Modifier.testTag("EditButton"),
                ) {
                    Icon(imageVector = ComicIcons.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(Res.string.bookshelf_info_btn_edit))
                }
            }
        }
    }
}
