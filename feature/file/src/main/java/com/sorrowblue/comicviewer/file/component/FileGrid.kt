package com.sorrowblue.comicviewer.file.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.placeholder.placeholder3

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileGrid(
    file: File?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier) {
        var expand by remember { mutableStateOf(false) }
        Column(
            Modifier.combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onLongClick = onLongClick,
                onClick = onClick
            )
        ) {
            AsyncImage(
                model = file,
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = CardDefaults.elevatedShape
                    )
                    .clip(CardDefaults.elevatedShape)
                    .placeholder3(file == null)
            )
            if (file is Book && 0 < file.lastPageRead) {
                LinearProgressIndicator(
                    progress = file.lastPageRead.toFloat() / file.totalPageCount,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Text(
                file?.name.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .placeholder3(file == null)
            )
        }
    }
}

@Preview
@Composable
fun PreviewFileGrid() {
    AppMaterialTheme {
        FileGrid(
            file = FakeFile,
            onClick = {},
            onLongClick = {}
        )
    }
}
