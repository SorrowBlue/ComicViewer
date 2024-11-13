package com.sorrowblue.comicviewer.feature.library.onedrive

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.sorrowblue.comicviewer.feature.library.onedrive.data.OneDriveApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

internal interface OneDriveSignInDialogState {
    fun onSignInClick()
}

@Composable
internal fun rememberOneDriveSignInDialogState(
    scope: CoroutineScope = rememberCoroutineScope(),
    context: Context = LocalContext.current,
    repository: OneDriveApiRepository = koinInject(),
): OneDriveSignInDialogState {
    return remember {
        OneDriveSignInDialogStateImpl(
            scope = scope,
            activity = context as ComponentActivity,
            repository = repository
        )
    }
}

private class OneDriveSignInDialogStateImpl(
    private val scope: CoroutineScope,
    private val activity: ComponentActivity,
    private val repository: OneDriveApiRepository,
) : OneDriveSignInDialogState {

    override fun onSignInClick() {
        scope.launch {
            repository.login(activity)
        }
    }
}
