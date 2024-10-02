package com.sorrowblue.comicviewer.feature.book.menu

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.feature.book.R
import com.sorrowblue.comicviewer.feature.book.navigation.BookGraph
import com.sorrowblue.comicviewer.feature.book.section.PageFormat2
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.ExposedDropdownMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

internal data class BookMenuScreenUiState(
    val pageFormat2: PageFormat2 = PageFormat2.Default,
    val pageScale: PageScale = PageScale.Fit,
)

@HiltViewModel
internal class BookMenuViewModel @Inject constructor(
    val manageBookSettingsUseCase: ManageBookSettingsUseCase,
) : ViewModel()

@Destination<BookGraph>(
    style = DestinationStyle.Dialog::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun BookMenuScreen(destinationsNavigator: DestinationsNavigator) {
    BookMenuScreen(onDismissRequest = destinationsNavigator::navigateUp)
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
            label = stringResource(id = R.string.book_label_display_format),
            value = stringResource(id = uiState.pageFormat2.label),
            onChangeValue = onPageFormatChange,
            menus = remember { PageFormat2.entries },
        )
        ExposedDropdownMenu(
            label = stringResource(id = R.string.book_label_scale),
            value = stringResource(id = uiState.pageScale.label),
            onChangeValue = onPageScaleChange,
            menus = remember { PageScale.entries },
        )
        Spacer(modifier = Modifier.navigationBarsPadding())
    }
}
