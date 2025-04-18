package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import comicviewer.feature.collection.editor.generated.resources.Res
import comicviewer.feature.collection.editor.generated.resources.collection_editor_title_smart_edit
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
internal data class SmartCollectionEdit(val collectionId: CollectionId)

@Destination<SmartCollectionEdit>(style = DestinationStyle.Dialog::class)
@Composable
internal fun SmartCollectionEditScreen(
    route: SmartCollectionEdit,
    navController: NavController = koinInject(),
) {
    SmartCollectionEditorScreen(
        state = rememberSmartCollectionEditScreenState(route = route),
        title = { Text(text = stringResource(Res.string.collection_editor_title_smart_edit)) },
        onCancel = navController::popBackStack,
        onComplete = navController::popBackStack,
    )
}
