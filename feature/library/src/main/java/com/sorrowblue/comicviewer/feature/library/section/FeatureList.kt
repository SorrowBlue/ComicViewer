package com.sorrowblue.comicviewer.feature.library.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.library.component.AddOnItem
import com.sorrowblue.comicviewer.feature.library.component.BasicItem
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun FeatureListSheet(
    basicList: PersistentList<Feature.Basic>,
    addOnList: PersistentList<Feature.AddOn>,
    contentPadding: PaddingValues,
    onClick: (Feature) -> Unit,
) {
    Column(Modifier.padding(contentPadding)) {
        basicList.forEach {
            BasicItem(
                label = it.label,
                icon = it.icon,
                onClick = { onClick(it) }
            )
        }

        Spacer(modifier = Modifier.size(8.dp))
        Divider(modifier = Modifier.padding(horizontal = AppMaterialTheme.dimens.margin))
        Spacer(modifier = Modifier.size(8.dp))

        addOnList.forEach {
            AddOnItem(
                label = it.label,
                icon = it.icon,
                state = it.state,
                onClick = { onClick(it) }
            )
        }
    }
}
