package com.sorrowblue.comicviewer.feature.book.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.sorrowblue.comicviewer.feature.book.section.PageFormat2
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.comicviewer.framework.ui.layout.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.ExposedDropdownMenu
import comicviewer.feature.book.generated.resources.Res
import comicviewer.feature.book.generated.resources.book_label_display_format
import comicviewer.feature.book.generated.resources.book_label_scale
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
data object BookMenu

internal data class BookMenuScreenUiState(
    val pageFormat2: PageFormat2 = PageFormat2.Default,
    val pageScale: PageScale = PageScale.Fit,
)

@Destination<BookMenu>(style = DestinationStyle.Dialog::class)
@Composable
internal fun BookMenuScreen(navController: NavController = koinInject()) {
    BookMenuScreen(onDismissRequest = navController::navigateUp)
}

@Composable
private fun BookMenuScreen(
    state: BookMenuScreenState = rememberBookMenuScreenState(),
    onDismissRequest: () -> Unit,
) {
    BookMenuScreen(
        uiState = state.uiState,
        onDismissRequest = onDismissRequest,
        onPageFormatChange = state::onPageFormatChange,
        onPageScaleChange = state::onPageScaleChange
    )
}

@Composable
internal fun BookMenuScreen(
    uiState: BookMenuScreenUiState,
    onDismissRequest: () -> Unit,
    onPageFormatChange: (PageFormat2) -> Unit,
    onPageScaleChange: (PageScale) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        contentWindowInsets = { PaddingValues().asWindowInsets() }
    ) {
        ExposedDropdownMenu(
            label = stringResource(Res.string.book_label_display_format),
            value = stringResource(uiState.pageFormat2.label),
            onChangeValue = onPageFormatChange,
            menus = remember { PageFormat2.entries },
        )
        ExposedDropdownMenu(
            label = stringResource(Res.string.book_label_scale),
            value = stringResource(uiState.pageScale.label),
            onChangeValue = onPageScaleChange,
            menus = remember { PageScale.entries },
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}
