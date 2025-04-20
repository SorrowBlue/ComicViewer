package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.framework.ui.FrameworkResString
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBasicCollection
import comicviewer.feature.collection.generated.resources.Res
import comicviewer.feature.collection.generated.resources.collection_label_delete
import comicviewer.feature.collection.generated.resources.collection_label_delete_confirm
import comicviewer.feature.collection.generated.resources.collection_title_delete
import comicviewer.framework.ui.generated.resources.cancel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Serializable
internal data class DeleteCollection(val id: CollectionId, val name: String)

@Destination<DeleteCollection>(style = DestinationStyle.Dialog::class)
@Composable
internal fun DeleteCollectionScreen(
    route: DeleteCollection,
    navController: NavController = koinInject(),
) {
    val state = rememberDeleteCollectionScreenState()
    DeleteCollectionScreen(
        name = route.name,
        onDismissRequest = navController::popBackStack,
        onConfirm = { state.delete(route.id, navController::popBackStack) },
    )
}

@Composable
private fun DeleteCollectionScreen(
    name: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(Res.string.collection_title_delete))
        },
        text = {
            Text(stringResource(Res.string.collection_label_delete_confirm, name))
        },
        confirmButton = {
            FilledTonalButton(onClick = onConfirm) {
                Text(stringResource(Res.string.collection_label_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(FrameworkResString.cancel))
            }
        },
    )
}

@Preview
@Composable
private fun DeleteCollectionScreenPreview() {
    PreviewTheme {
        DeleteCollectionScreen(
            name = fakeBasicCollection().name,
            onDismissRequest = {},
            onConfirm = {},
        )
    }
}
