package com.sorrowblue.comicviewer.feature.collection.add.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.collection.add.generated.resources.Res
import comicviewer.feature.collection.add.generated.resources.collection_add_label_file_count
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun RecentCollection(
    collection: Collection,
    exist: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
    ) {
        Box {
            SubcomposeAsyncImage(
                model = collection,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(72.dp),
                error = {
                    Icon(
                        imageVector = ComicIcons.Image,
                        contentDescription = null,
                        modifier = Modifier.wrapContentSize()
                    )
                }
            )
            if (exist) {
                Icon(
                    imageVector = ComicIcons.CheckCircle,
                    contentDescription = null,
                    tint = ComicTheme.colorScheme.primaryContainer,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
        Text(
            text = collection.name,
            style = ComicTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text =
            pluralStringResource(
                Res.plurals.collection_add_label_file_count,
                collection.count,
                collection.count
            ),
            style = ComicTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun PreviewRecentCollection() {
    PreviewTheme {
        RecentCollection(
            collection = SmartCollection(
                name = "Collection",
                bookshelfId = BookshelfId(),
                searchCondition = SearchCondition()
            ),
            exist = true,
            onClick = {}
        )
    }
}
