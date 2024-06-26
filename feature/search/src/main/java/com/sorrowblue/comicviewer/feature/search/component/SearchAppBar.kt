package com.sorrowblue.comicviewer.feature.search.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.sorrowblue.comicviewer.feature.search.R
import com.sorrowblue.comicviewer.framework.ui.material3.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchAppBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = {
            val skc = LocalSoftwareKeyboardController.current
            TextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text(text = stringResource(R.string.search_label_search)) },
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                keyboardActions = KeyboardActions(onSearch = { skc?.hide() }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            BackButton(onClick = onBackClick)
        },
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior
    )
}
