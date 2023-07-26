package com.sorrowblue.comicviewer.bookshelf.manage.smb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialSharedAxis
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.copy
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
internal class BookshelfManageSmbFragment : FrameworkFragment() {
    private val args: BookshelfManageSmbFragmentArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppMaterialTheme {
                    BookshelfEditSmbScreen(
                        navigateUp = findNavController()::navigateUp,
                        saveComplete = {
                            if (0 < args.bookshelfId) {
                                findNavController().popBackStack()
                            } else {
                                findNavController().popBackStack(
                                    R.id.bookshelf_manage_list_fragment,
                                    true
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun BookshelfEditSmbScreen(
    navigateUp: () -> Unit,
    saveComplete: () -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfSmbEditViewModel = hiltViewModel()
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    val snackbarHostState = SnackbarHostState()
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest {
            when (it) {
                is BookshelfSmbEditViewModel.UiEvent.Error ->
                    snackbarHostState.showSnackbar(it.e, duration = SnackbarDuration.Short)

                BookshelfSmbEditViewModel.UiEvent.SaveComplete -> saveComplete()
            }
        }
    }
    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.bookshelf_manage_title_device)) },
                navigationIcon = {
                    IconButton(onClick = { navigateUp() }) {
                        Icon(
                            imageVector = Icons.TwoTone.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        MainContent(
            viewModel,
            modifier = Modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
                .overscroll(ScrollableDefaults.overscrollEffect())
                .padding(paddingValues.copy(all = 16.dp))
        )
        val uiState by viewModel.uiState.collectAsState()
        if (uiState == BookshelfSmbEditViewModel.UiState.CONNECTING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .background(MaterialTheme.colorScheme.scrim)
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
internal fun MainContent(
    viewModel: BookshelfSmbEditViewModel,
    modifier: Modifier = Modifier
) {
    val host by viewModel.host.flow.collectAsState()
    val isInvalidHost by viewModel.host.isError.collectAsState(false)
    val port by viewModel.port.flow.collectAsState()
    val isInvalidPort by viewModel.port.isError.collectAsState(false)
    val path by viewModel.path.flow.collectAsState()
    val isInvalidPath by viewModel.path.isError.collectAsState(false)
    val displayName by viewModel.displayName.flow.collectAsState()
    val isInvalidDisplayName by viewModel.displayName.isError.collectAsState(false)
    val isGuest by viewModel.isGuest.flow.collectAsState()
    val domain by viewModel.domain.flow.collectAsState()
    val username by viewModel.username.flow.collectAsState()
    val isInvalidUsername by viewModel.username.isError.collectAsState(false)
    val password by viewModel.password.flow.collectAsState()
    val isInvalidPassword by viewModel.password.isError.collectAsState(false)
    Column(modifier) {
        OutlinedTextField(
            value = host,
            onValueChange = remember { viewModel.host::edit },
            label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_host)) },
            isError = isInvalidHost,
            supportingText = {
                if (isInvalidHost) {
                    Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_host))
                }
            },
            singleLine = viewModel.host.singleLine,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next

            ),
        )
        OutlinedTextField(
            value = port,
            onValueChange = remember { viewModel.port::edit },
            label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_port)) },
            isError = isInvalidPort,
            supportingText = {
                if (isInvalidPort) {
                    Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_port))
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = path,
            onValueChange = remember { viewModel.path::edit },
            label = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_path)) },
            isError = isInvalidPath,
            supportingText = {
                if (isInvalidPath) {
                    Text(text = "エラー")
                }
            },
            prefix = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_prefix_path)) },
            suffix = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_suffix_path)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            value = displayName,
            onValueChange = remember { viewModel.displayName::edit },
            label = {
                Text(text = stringResource(id = R.string.bookshelf_manage_hint_display_name))
            },
            isError = isInvalidDisplayName,
            supportingText = {
                if (isInvalidDisplayName) {
                    Text(text = stringResource(id = R.string.bookshelf_manage_hint_display_name))
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = if (isGuest) ImeAction.Done else ImeAction.Next
            )
        )
        val selectedIndex by remember(isGuest) { derivedStateOf { if (isGuest) 0 else 1 } }
        MaterialButtons(
            size = remember { 2 },
            label = {
                Text(text = stringResource(id = if (it == 0) R.string.bookshelf_manage_label_guest else R.string.bookshelf_manage_label_username_password))
            },
            selectedIndex = selectedIndex,
            onChange = remember { { viewModel.isGuest.edit(it == 0) } },
            modifier = Modifier.padding(top = 16.dp)
        )
        if (!isGuest) {
            OutlinedTextField(
                value = domain,
                onValueChange = remember { viewModel.domain::edit },
                label = { Text(text = stringResource(id = R.string.bookshelf_manage_hint_domain)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = username,
                onValueChange = remember { viewModel.username::edit },
                label = { Text(text = stringResource(id = R.string.bookshelf_manage_hint_username)) },
                isError = isInvalidUsername,
                supportingText = {
                    if (isInvalidUsername) {
                        Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_username))
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = remember { viewModel.password::edit },
                isError = isInvalidPassword,
                supportingText = {
                    if (isInvalidPassword) {
                        Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_error_password))
                    }
                },
                label = { Text(text = stringResource(id = R.string.bookshelf_manage_hint_password)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
        }
        Button(
            onClick = { viewModel.save() },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(text = "Save")
        }
    }
}
