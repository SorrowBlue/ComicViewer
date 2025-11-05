package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import io.github.irgaly.navigation3.resultstate.LocalNavigationResultProducer
import io.github.irgaly.navigation3.resultstate.setResult
import kotlinx.serialization.json.Json

@Composable
fun FolderThumbnailOrderScreenRoot(
    folderThumbnailOrder: FolderThumbnailOrder,
    onDismissRequest: () -> Unit,
) {
    val resultProducer = LocalNavigationResultProducer.current
    FolderThumbnailOrderScreen(
        currentFolderThumbnailOrder = folderThumbnailOrder,
        onFolderThumbnailOrderChange = {
            resultProducer.setResult(
                Json,
                FolderThumbnailOrderScreenResultKey,
                it
            )
            onDismissRequest()
        },
        onDismissRequest = onDismissRequest
    )
}
