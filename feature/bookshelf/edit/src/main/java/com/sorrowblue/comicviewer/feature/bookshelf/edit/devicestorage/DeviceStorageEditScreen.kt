package com.sorrowblue.comicviewer.feature.bookshelf.edit.devicestorage

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.feature.bookshelf.edit.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.responsive.FullScreenTopAppBar

enum class EditType {
    Register,
    Edit
}

data class DeviceStorageEditScreenUiState(
    val editType: EditType,
    val displayName: String,
    val isDisplayNameError: Boolean,
    val dir: String,
    val isError: Boolean,
    val isProgress: Boolean,
)

interface DeviceStorageEditScreenState {

    var uiState: DeviceStorageEditScreenUiState

    val snackbarHostState: SnackbarHostState

    val activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    val openDocumentTreeIntent
        get() = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }

    fun onSelectFolderClick() {
        activityResultLauncher.launch(openDocumentTreeIntent)
    }

    fun onDisplayNameChange(text: String) {
        uiState = uiState.copy(displayName = text)
    }

    fun onSaveClick() {
        TODO("Not yet implemented")
    }
}

class DeviceStorageEditScreenStateImpl(
    override val snackbarHostState: SnackbarHostState,
    override val activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) : DeviceStorageEditScreenState {

    override var uiState: DeviceStorageEditScreenUiState by mutableStateOf(
        DeviceStorageEditScreenUiState(
            editType = EditType.Register,
            displayName = "",
            isDisplayNameError = false,
            dir = "",
            isError = false,
            isProgress = false
        )
    )

    fun onFolderSelect(uri: Uri) {

    }
}

@Composable
fun rememberDeviceStorageEditScreen(
    activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.data?.let { uri ->
//                viewModel.updateUri(uri)
            } ?: run {
//                scope.launch {
//                    snackbarHostState.showSnackbar("フォルダを選択してください")
//                }
            }
        },
): DeviceStorageEditScreenState = remember {
    DeviceStorageEditScreenStateImpl(
        snackbarHostState = SnackbarHostState(),
        activityResultLauncher = activityResultLauncher
    )
}

@Composable
fun DeviceStorageEditScreen2(onBackClick: () -> Unit) {
    val state = rememberDeviceStorageEditScreen()
    DeviceStorageEditScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onDisplayNameChange = state::onDisplayNameChange,
        onSelectFolderClick = state::onSelectFolderClick,
        onSaveClick = state::onSaveClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceStorageEditScreen(
    uiState: DeviceStorageEditScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSelectFolderClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            FullScreenTopAppBar(
                title = {
                    Text(
                        text = when (uiState.editType) {
                            EditType.Register -> stringResource(id = R.string.bookshelf_edit_title_register)
                            EditType.Edit -> stringResource(id = R.string.bookshelf_edit_title_edit)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        Box {
            Column(Modifier.padding(contentPadding)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.displayName,
                    label = { Text(text = "表示名") },
                    isError = uiState.isDisplayNameError,
                    onValueChange = { onDisplayNameChange(it) },
                    supportingText = {
                        if (uiState.isDisplayNameError) {
                            Text(text = "ERROR")
                        }
                    }
                )
                Spacer(modifier = Modifier.size(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(text = "フォルダを選択")
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTrailingIconColor = ComicTheme.colorScheme.primary,
                        disabledContainerColor = Color.Transparent,
                        disabledBorderColor = ComicTheme.colorScheme.outline,
                        disabledLabelColor = ComicTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = ComicTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = ComicTheme.colorScheme.onSurfaceVariant,
                        disabledPrefixColor = ComicTheme.colorScheme.onSurfaceVariant,
                        disabledSuffixColor = ComicTheme.colorScheme.onSurfaceVariant,
                        disabledSupportingTextColor = ComicTheme.colorScheme.onSurfaceVariant,
                        disabledTextColor = ComicTheme.colorScheme.onSurface
                    ),
                    value = uiState.dir,
                    onValueChange = {},
                    trailingIcon = {
                        IconButton(onClick = onSelectFolderClick) {
                            Icon(ComicIcons.Folder, null)
                        }
                    },
                    enabled = false
                )
                Spacer(modifier = Modifier.weight(1f))
                FilledTonalButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = onSaveClick,
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                    enabled = !uiState.isError
                ) {
                    Icon(
                        modifier = Modifier.size(ButtonDefaults.IconSize),
                        imageVector = ComicIcons.Save,
                        contentDescription = null
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Save")
                }
            }
            if (uiState.isProgress) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ComicTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f))
                        .clickable {}
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
