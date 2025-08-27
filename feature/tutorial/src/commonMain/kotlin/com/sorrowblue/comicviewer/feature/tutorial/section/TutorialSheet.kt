package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun TutorialSheet(
    image: ImageVector,
    contentDescription: String?,
    title: @Composable () -> Unit,
    description: @Composable () -> Unit,
    contentPadding: PaddingValues,
) {
    val icon = remember {
        movableContentWithReceiverOf<ColumnScope> {
            Image(
                imageVector = image,
                contentDescription = contentDescription,
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.margin))
            CompositionLocalProvider(LocalTextStyle provides ComicTheme.typography.titleLarge) {
                title()
            }
        }
    }
    val message = remember {
        movableContentWithReceiverOf<ColumnScope> {
            CompositionLocalProvider(LocalTextStyle provides ComicTheme.typography.bodyLarge) {
                description()
            }
        }
    }
    BoxWithConstraints {
        if (maxWidth < maxHeight) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding)
                    .padding(ComicTheme.dimension.margin),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    icon()
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    message()
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(ComicTheme.dimension.margin),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    icon()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.weight(0.8f))
                    message()
                    Spacer(modifier = Modifier.weight(0.2f))
                }
            }
        }
    }
}
