package com.sorrowblue.comicviewer.feature.bookshelf.delete

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.toRoute
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreen
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenArgs
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenEvent
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenState
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDeleteScreenUiState
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.rememberBookshelfDeleteScreenState
import com.sorrowblue.comicviewer.framework.annotation.DestinationImpl
import com.sorrowblue.comicviewer.framework.navigation.ScreenDestination
import com.sorrowblue.comicviewer.framework.navigation.kSerializableType
import com.sorrowblue.comicviewer.framework.navigation.navResultSender
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewMultiScreen
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.nextLoremIpsum
import kotlin.reflect.typeOf
import org.koin.core.annotation.Singleton

@DestinationImpl
@Singleton
class BookshelfDeleteScreenDestination : ScreenDestination<BookshelfDelete> {
    override val route = BookshelfDelete::class

    override val typeMap = mapOf(typeOf<BookshelfId>() to NavType.kSerializableType<BookshelfId>())

    override val style = com.sorrowblue.comicviewer.framework.navigation.DestinationStyle.Composable

    @Composable
    override fun NavBackStackEntry.Content() {
        val route = toRoute<BookshelfDelete>()
        BookshelfDeleteScreen(
            route = route,
            navResultSender = navController.navResultSender<Boolean>(BookshelfDelete::class)
        )
    }
}

@Destination<ExternalModuleGraph>(
    style = DestinationStyle.Dialog::class,
    navArgs = BookshelfDeleteScreenArgs::class
)
@Composable
internal fun BookshelfDeleteScreen(
    navArgs: BookshelfDeleteScreenArgs,
    destinationsNavigator: ResultBackNavigator<Boolean>,
    state: BookshelfDeleteScreenState = rememberBookshelfDeleteScreenState(navArgs.bookshelfId),
) {
    BookshelfDeleteScreen(
        uiState = state.uiState,
        onDismissRequest = destinationsNavigator::navigateBack,
        onDismissClick = { destinationsNavigator.navigateBack(false) },
        onConfirmClick = state::onConfirmClick
    )
    EventEffect(state.events) {
        when (it) {
            BookshelfDeleteScreenEvent.RemoveSuccess -> destinationsNavigator.navigateBack(true)
        }
    }
}

@PreviewMultiScreen
@Composable
private fun BookshelfDeleteScreenPreview(
    @PreviewParameter(BookshelfDeleteScreenUiStateConfig::class) uiState: BookshelfDeleteScreenUiState,
) {
    PreviewTheme {
        BookshelfDeleteScreen(
            uiState = uiState,
            onDismissRequest = {},
            onDismissClick = {},
            onConfirmClick = {}
        )
    }
}

private class BookshelfDeleteScreenUiStateConfig :
    PreviewParameterProvider<BookshelfDeleteScreenUiState> {
    override val values = sequenceOf(
        BookshelfDeleteScreenUiState(nextLoremIpsum(), false),
        BookshelfDeleteScreenUiState(nextLoremIpsum(), true),
    )
}
