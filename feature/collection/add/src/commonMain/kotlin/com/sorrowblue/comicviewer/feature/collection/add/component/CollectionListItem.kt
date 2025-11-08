package com.sorrowblue.comicviewer.feature.collection.add.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.imageBackground
import com.sorrowblue.comicviewer.framework.ui.AsyncImage3
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBasicCollection
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
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = pluralStringResource(
                    Res.plurals.collection_add_label_file_count,
                    collection.count,
                    collection.count,
                ),
            )
        },
        leadingContent = {
            AsyncImage3(
                model = collection,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                error = {
                    Icon(
                        imageVector = ComicIcons.BrokenImage,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize(),
                    )
                },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CardDefaults.shape)
                    .background(
                        ComicTheme.colorScheme.imageBackground(ListItemDefaults.containerColor),
                    ),
            )
        },
        trailingContent = {
            if (exist) {
                Icon(imageVector = ComicIcons.Check, contentDescription = null)
            }
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Preview
@Composable
private fun CollectionListItemPreview() {
    PreviewTheme {
        Column {
            CollectionListItem(
                collection = fakeBasicCollection(),
                exist = true,
                onClick = {},
            )
            Spacer(modifier = Modifier.size(8.dp))
            CollectionListItem(
                collection = fakeBasicCollection(),
                exist = false,
                onClick = {},
            )
        }
    }
}
