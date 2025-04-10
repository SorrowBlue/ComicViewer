package com.sorrowblue.comicviewer.feature.collection.add.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId.Companion.invoke
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.feature.collection.add.component.RecentCollection
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData
import comicviewer.feature.collection.add.generated.resources.Res
import comicviewer.feature.collection.add.generated.resources.collection_add_label_recent
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun RecentCollectionSheet(
    lazyPagingItems: LazyPagingItems<Pair<Collection, Boolean>>,
    onClick: (Collection, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider()
        Text(
            text = stringResource(Res.string.collection_add_label_recent),
            style = ComicTheme.typography.labelSmall,
            modifier = Modifier.padding(ComicTheme.dimension.padding)
        )
        HorizontalMultiBrowseCarousel(
            state = rememberCarouselState { lazyPagingItems.itemCount },
            preferredItemWidth = 72.dp,
            itemSpacing = ComicTheme.dimension.padding,
            contentPadding = PaddingValues(ComicTheme.dimension.padding)
        ) {
            if (lazyPagingItems.itemCount <= 0) return@HorizontalMultiBrowseCarousel
            lazyPagingItems[it]?.let { item ->
                RecentCollection(
                    collection = item.first,
                    exist = item.second,
                    onClick = { onClick(item.first, item.second) }
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(top = ComicTheme.dimension.padding)
                .padding(horizontal = ComicTheme.dimension.margin)
        )
    }
}

@Preview
@Composable
private fun RecentCollectionSheetPreview() {
    PreviewTheme {
        RecentCollectionSheet(
            lazyPagingItems = PagingData.flowData {
                SmartCollection(
                    name = "Collection",
                    bookshelfId = BookshelfId(),
                    searchCondition = SearchCondition()
                ) as Collection to true
            }.collectAsLazyPagingItems(),
            onClick = { _, _ -> }
        )
    }
}
