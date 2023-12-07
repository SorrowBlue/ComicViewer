package com.sorrowblue.comicviewer.feature.search.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.search.R
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveTopAppBar

@Composable
internal fun SearchAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    ResponsiveTopAppBar(
        title = {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(text = stringResource(R.string.search_label_search)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        onBackClick = onBackClick,
        windowInsets = contentPadding.asWindowInsets(),
        scrollBehavior = scrollBehavior
    )
}
