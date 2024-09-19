package com.sorrowblue.comicviewer.feature.library.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.library.component.AddOnItem
import com.sorrowblue.comicviewer.feature.library.component.BasicItem
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

@Composable
internal fun FeatureListSheet(
    basicList: List<Feature.Basic>,
    addOnList: List<Feature.AddOn>,
    contentPadding: PaddingValues,
    onClick: (Feature) -> Unit,
    state: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        state = state,
        contentPadding = contentPadding,
        modifier = Modifier.drawVerticalScrollbar(state)
    ) {
        items(basicList) {
            BasicItem(
                label = it.label,
                icon = it.icon,
                onClick = { onClick(it) }
            )
        }

        item {
            Spacer(modifier = Modifier.size(8.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = ComicTheme.dimension.margin))
            Spacer(modifier = Modifier.size(8.dp))
        }

        items(addOnList) {
            AddOnItem(
                label = it.label,
                icon = it.icon,
                state = it.state,
                onClick = { onClick(it) }
            )
        }
    }
}
