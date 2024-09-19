package com.sorrowblue.comicviewer.feature.tutorial.section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.tutorial.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.Launcher
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme

@Composable
internal fun WelcomeSheet(contentPadding: PaddingValues) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val icon = remember {
        movableContentWithReceiverOf<ColumnScope> {
            Image(
                imageVector = ComicIcons.Launcher,
                contentDescription = null,
                modifier = Modifier.size(108.dp),
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            Text(
                text = stringResource(id = com.sorrowblue.comicviewer.framework.ui.R.string.app_name),
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
    val message = remember {
        movableContentWithReceiverOf<ColumnScope> {
            Text(
                text = stringResource(R.string.tutorial_title_welcome),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.size(ComicTheme.dimension.padding))
            Text(
                text = stringResource(R.string.tutorial_message_welcome),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
    if (screenWidth < screenHeight) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(ComicTheme.dimension.margin),
            horizontalAlignment = Alignment.CenterHorizontally,
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

@PreviewMultiScreen
@Composable
private fun PreviewWelcomeSheet() {
    PreviewTheme {
        Surface {
            WelcomeSheet(
                contentPadding = PaddingValues()
            )
        }
    }
}
