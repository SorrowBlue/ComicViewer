package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.component.SaveButton
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditArgs
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditContentState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.BookshelfEditScreenLoadingState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceStorageEditScreenState2
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.DeviceStorageEditScreenState2Impl
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.MobileSmbEditContent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.MobileStorageEditContent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditContentState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditContentStateImpl
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.SmbEditScreenUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.StorageEditContentState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.StorageEditContentUiState
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.TabletSmbEditContent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.TabletStorageEditContent
import com.sorrowblue.comicviewer.feature.bookshelf.edit.section.rememberStorageEditContentState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.PreviewMobile
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.rememberMobile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun BookshelfEditRoute(
    args: BookshelfEditArgs,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val state = rememberBookshelfEditScreenState(args = args)
    if (rememberMobile()) {
        BookshelfEditScreen(state = state, onBackClick = onBackClick, onComplete = onComplete)
    } else {
        BookshelfEditDialog(state = state, onBackClick = onBackClick, onComplete = onComplete)
    }
}

internal abstract class BookshelfEditScreenState {

    abstract var editType: EditType

    abstract val snackbarHostState: SnackbarHostState

    abstract var contentState: BookshelfEditContentState
        protected set
}

@Composable
internal fun rememberBookshelfEditScreenState(
    args: BookshelfEditArgs,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfEditViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
    storageEditContentState: StorageEditContentState = rememberStorageEditContentState(
        args = args,
        snackbarHostState = snackbarHostState,
        viewModel = viewModel,
        scope = scope,
        context = context
    ),
    activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult> =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            storageEditContentState.onResult(it)
        },
): BookshelfEditScreenState = remember {
    BookshelfEditScreenStateImpl(
        args = args,
        snackbarHostState = snackbarHostState,
        viewModel = viewModel,
        scope = scope,
        activityResultLauncher = activityResultLauncher,
        storageEditContentState = storageEditContentState
    )
}

private class BookshelfEditScreenStateImpl(
    private val args: BookshelfEditArgs,
    override val snackbarHostState: SnackbarHostState,
    private val viewModel: BookshelfEditViewModel,
    private val scope: CoroutineScope,
    activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    storageEditContentState: StorageEditContentState,
) : BookshelfEditScreenState() {

    override var contentState: BookshelfEditContentState by mutableStateOf(
        BookshelfEditScreenLoadingState
    )

    init {
        if (0 < args.bookshelfId.value) {
            scope.launch {
                val bf = viewModel.fetch(args.bookshelfId)!!
                val bookshelf = bf.bookshelf
                val folder = bf.folder
                contentState = when (bookshelf) {
                    is InternalStorage -> DeviceStorageEditScreenState2Impl(
                        state = storageEditContentState.init(bookshelf, folder),
                        activityResultLauncher = activityResultLauncher
                    )

                    is SmbServer -> {
                        SmbEditContentStateImpl(
                            uiState = SmbEditScreenUiState(bookshelf, folder),
                            args = args,
                            snackbarHostState = snackbarHostState,
                            viewModel = viewModel,
                            scope = scope
                        )
                    }
                }
            }
        } else {
            contentState = when (args.bookshelfType) {
                BookshelfType.SMB -> SmbEditContentStateImpl(
                    uiState = SmbEditScreenUiState(),
                    args = args,
                    snackbarHostState = snackbarHostState,
                    viewModel = viewModel,
                    scope = scope
                )

                BookshelfType.DEVICE -> DeviceStorageEditScreenState2Impl(
                    state = storageEditContentState,
                    activityResultLauncher = activityResultLauncher
                )
            }
        }
    }

    override var editType: EditType =
        if (0 < args.bookshelfId.value) EditType.Edit else EditType.Register
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfEditScreen(
    state: BookshelfEditScreenState,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val altState = state.contentState
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = state.editType.title)) },
                navigationIcon = {
                    IconButton(onBackClick) {
                        Icon(imageVector = ComicIcons.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = state.snackbarHostState)
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val modifier = if (rememberMobile()) {
            Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(
                    start = ComicTheme.dimension.margin,
                    end = ComicTheme.dimension.margin,
                    bottom = ComicTheme.dimension.margin
                )
                .verticalScroll(rememberScrollState())
        } else {
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(ComicTheme.dimension.margin)
                .padding(contentPadding)
                .clip(ComicTheme.shapes.large)
                .background(ComicTheme.colorScheme.surface)
                .padding(ComicTheme.dimension.margin)
        }
        when (altState) {
            is SmbEditContentState -> MobileSmbEditContent(
                state = altState,
                onComplete = onComplete,
                modifier = modifier
            )

            BookshelfEditScreenLoadingState -> Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                CircularProgressIndicator()
            }

            is DeviceStorageEditScreenState2 -> {
                MobileStorageEditContent(
                    contentState = altState,
                    onComplete = onComplete,
                    modifier = modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookshelfEditDialog(
    state: BookshelfEditScreenState,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val contentState = state.contentState
    AlertDialog(
        onDismissRequest = onBackClick,
    ) {
        Surface(
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier
            ) {
                val scrollState = rememberScrollState()
                Text(
                    text = stringResource(id = state.editType.title),
                    style = ComicTheme.typography.headlineSmall,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        top = 24.dp,
                        end = 24.dp,
                        bottom = 16.dp
                    )

                )
                if (scrollState.canScrollBackward) {
                    HorizontalDivider()
                }
                when (contentState) {
                    is SmbEditContentState -> TabletSmbEditContent(
                        state = contentState,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .padding(horizontal = 24.dp)
                            .verticalScroll(scrollState)
                    )

                    BookshelfEditScreenLoadingState -> Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }

                    is DeviceStorageEditScreenState2 -> {
                        TabletStorageEditContent(
                            contentState = contentState,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .padding(horizontal = 24.dp)
                                .verticalScroll(scrollState)
                        )
                    }
                }
                if (scrollState.canScrollForward) {
                    HorizontalDivider()
                }

                SaveButton(
                    onClick = {
                        when (contentState) {
                            BookshelfEditScreenLoadingState -> Unit
                            is DeviceStorageEditScreenState2 -> contentState.state.onSaveClick(
                                onComplete
                            )

                            is SmbEditContentState -> contentState.onSaveClick(onComplete)
                        }
                    },
                    modifier = Modifier
                        .padding(24.dp)
                        .align(Alignment.End)
                )
            }
        }
    }
}

@PreviewMobile
@Composable
private fun PreviewBookshelfEditScreen() {
    PreviewTheme {
        BookshelfEditScreen(
            state = rememberPreviewSmb(),
            onComplete = {},
            onBackClick = {}
        )
    }
}

@Composable
private fun rememberPreviewSmb() = remember {
    val snackbarHostState = SnackbarHostState()
    object : BookshelfEditScreenState() {
        override var editType = EditType.Register
        override val snackbarHostState: SnackbarHostState = snackbarHostState
        override var contentState: BookshelfEditContentState = object : SmbEditContentState {
            override var uiState = SmbEditScreenUiState()
            override val snackbarHostState: SnackbarHostState = snackbarHostState
        }
    }
}

@Composable
private fun rememberPreviewStorageState() = remember {
    val snackbarHostState = SnackbarHostState()
    object : BookshelfEditScreenState() {
        override var editType = EditType.Register
        override val snackbarHostState: SnackbarHostState = snackbarHostState
        override var contentState: BookshelfEditContentState =
            object : DeviceStorageEditScreenState2 {
                override val state: StorageEditContentState = object : StorageEditContentState {
                    override fun init(
                        internalStorage: InternalStorage,
                        folder: Folder,
                    ): StorageEditContentState {
                        TODO("Not yet implemented")
                    }

                    override var uiState: StorageEditContentUiState =
                        StorageEditContentUiState()
                    override val snackbarHostState: SnackbarHostState = snackbarHostState

                    override fun onResult(it: ActivityResult) = Unit
                }
                override val activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
                    get() = TODO("Not yet implemented")
            }
    }
}

@PreviewMobile
@Composable
private fun PreviewBookshelfDeviceEditScreen() {
    PreviewTheme {
        BookshelfEditScreen(
            state = rememberPreviewStorageState(),
            onComplete = {},
            onBackClick = {}
        )
    }
}
