package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_smart_create
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
internal data class SmartCollectionCreate(
    val bookshelfId: BookshelfId = BookshelfId(),
    val searchCondition: SearchCondition = SearchCondition(),
)

@Destination<SmartCollectionCreate>(style = DestinationStyle.Dialog::class)
@Composable
internal fun SmartCollectionCreateScreen(
    route: SmartCollectionCreate,
    navController: NavController = koinInject(),
) {
    SmartCollectionEditorScreen(
        state = rememberSmartCollectionCreateScreenState(route = route),
        title = { Text(text = stringResource(Res.string.collection_editor_title_smart_create)) },
        onCancel = navController::popBackStack,
        onComplete = navController::popBackStack,
    )
}
