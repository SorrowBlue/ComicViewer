package com.sorrowblue.comicviewer.feature.collection.add.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.collection.add.generated.resources.Res
import comicviewer.feature.collection.add.generated.resources.collection_add_label_file_count
import org.jetbrains.compose.resources.pluralStringResource

@Composable
internal fun CollectionListItem(collection: Collection, exist: Boolean, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                text = collection.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = pluralStringResource(
                    Res.plurals.collection_add_label_file_count,
                    collection.count,
                    collection.count
                )
            )
        },
        leadingContent = {
            SubcomposeAsyncImage(
                model = collection,
                contentDescription = null,
                loading = {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.wrapContentSize()
                    )
                },
                error = {
                    Icon(
                        imageVector = ComicIcons.BrokenImage,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )
                },
                modifier = Modifier.size(56.dp)
            )
        },
        trailingContent = {
            if (exist) {
                Icon(imageVector = ComicIcons.Check, contentDescription = null)
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
