package com.sorrowblue.comicviewer.feature.book.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings.PageFormat
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageBookSettingsUseCase
import com.sorrowblue.comicviewer.feature.book.section.PageFormat2
import com.sorrowblue.comicviewer.feature.book.section.PageScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
context(context: BookMenuScreenContext)
internal fun rememberBookMenuScreenState(): BookMenuScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        BookMenuScreenStateImpl(
            coroutineScope = coroutineScope,
            manageBookSettingsUseCase = context.manageBookSettingsUseCase,
        )
    }
}

internal interface BookMenuScreenState {
    val uiState: BookMenuScreenUiState

    fun onPageFormatChange(pageFormat2: PageFormat2)

    fun onPageScaleChange(pageScale: PageScale)
}

private class BookMenuScreenStateImpl(
    private val coroutineScope: CoroutineScope,
    private val manageBookSettingsUseCase: ManageBookSettingsUseCase,
) : BookMenuScreenState {
    override var uiState by mutableStateOf(BookMenuScreenUiState())
        private set

    init {
        manageBookSettingsUseCase.settings
            .onEach {
                uiState = uiState.copy(
                    pageFormat2 = when (it.pageFormat) {
                        PageFormat.Default -> PageFormat2.Default
                        PageFormat.Spread -> PageFormat2.Spread
                        PageFormat.Split -> PageFormat2.Split
                        PageFormat.Auto -> PageFormat2.SplitSpread
                    },
                    pageScale = when (it.pageScale) {
                        BookSettings.PageScale.Fit -> PageScale.Fit
                        BookSettings.PageScale.FillWidth -> PageScale.FillWidth
                        BookSettings.PageScale.FillHeight -> PageScale.FillHeight
                        BookSettings.PageScale.Inside -> PageScale.Inside
                        BookSettings.PageScale.None -> PageScale.None
                        BookSettings.PageScale.FillBounds -> PageScale.FillBounds
                    },
                )
            }.launchIn(coroutineScope)
    }

    override fun onPageFormatChange(pageFormat2: PageFormat2) {
        coroutineScope.launch {
            manageBookSettingsUseCase.edit {
                it.copy(
                    pageFormat = when (pageFormat2) {
                        PageFormat2.Default -> PageFormat.Default
                        PageFormat2.Split -> PageFormat.Split
                        PageFormat2.Spread -> PageFormat.Spread
                        PageFormat2.SplitSpread -> PageFormat.Auto
                    },
                )
            }
        }
    }

    override fun onPageScaleChange(pageScale: PageScale) {
        coroutineScope.launch {
            manageBookSettingsUseCase.edit {
                it.copy(
                    pageScale = when (pageScale) {
                        PageScale.Fit -> BookSettings.PageScale.Fit
                        PageScale.FillHeight -> BookSettings.PageScale.FillHeight
                        PageScale.FillWidth -> BookSettings.PageScale.FillWidth
                        PageScale.Inside -> BookSettings.PageScale.Inside
                        PageScale.None -> BookSettings.PageScale.None
                        PageScale.FillBounds -> BookSettings.PageScale.FillBounds
                    },
                )
            }
        }
    }
}
