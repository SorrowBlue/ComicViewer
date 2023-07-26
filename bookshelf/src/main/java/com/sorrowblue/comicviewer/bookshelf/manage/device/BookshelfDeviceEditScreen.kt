package com.sorrowblue.comicviewer.bookshelf.manage.device

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Folder
import androidx.compose.material.icons.twotone.Save
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.copy

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
internal fun BookshelfDeviceEditScreen(
    navController: NavController,
    openDocumentTree: () -> Unit,
    viewModel: BookshelfDeviceEditViewModel = hiltViewModel()
) {
    val dir = viewModel.dir.collectAsState()
    val state = viewModel.state.collectAsState()
    val message = viewModel.message.collectAsState()
    val displayName = viewModel.displayName.collectAsState()
    val errorDisplayName = viewModel.errorDisplayName.collectAsState()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    val snackbarHostState = SnackbarHostState()
    if (message.value.isNotEmpty()) {
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = message.value,
                duration = SnackbarDuration.Short
            )
        }
    }
    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.bookshelf_manage_title_device),
                        modifier = Modifier.basicMarquee()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.TwoTone.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .overscroll(ScrollableDefaults.overscrollEffect())
                .padding(paddingValues.copy(all = 16.dp))
        ) {
            OutlinedTextField(
                value = displayName.value,
                label = {
                    Text(text = "表示名")
                },
                isError = errorDisplayName.value.isNotEmpty(),
                onValueChange = { viewModel.updateDisplayName(it) },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    if (errorDisplayName.value.isNotEmpty()) {
                        Text(text = errorDisplayName.value)
                    }
                }
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            OutlinedTextField(
                value = dir.value,
                onValueChange = { },
                trailingIcon = {
                    IconButton(onClick = {
                        openDocumentTree.invoke()
                    }) {
                        Icon(imageVector = Icons.TwoTone.Folder, contentDescription = "")
                    }
                },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            val keyboardController = LocalSoftwareKeyboardController.current
            TextButton(
                onClick = {
                    keyboardController?.hide()
                    viewModel.connect {
                        navController.popBackStack(R.id.bookshelf_manage_list_fragment, true)
                    }
                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    Icons.TwoTone.Save,
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Save")
            }
        }
    }
    if (state.value == BookshelfEditState.LOADING) {
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.scrim)
                .fillMaxSize()
                .clickable { }
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
