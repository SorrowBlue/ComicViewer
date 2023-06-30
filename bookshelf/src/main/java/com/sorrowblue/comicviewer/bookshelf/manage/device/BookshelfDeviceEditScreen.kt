package com.sorrowblue.comicviewer.bookshelf.manage.device

import android.net.Uri
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfError
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.copy
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BookshelfEditState {
    NONE,
    LOADING,
}

@HiltViewModel
open class BookshelfDeviceEditViewModel @Inject constructor(
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val registerBookshelfUseCase: RegisterBookshelfUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args: BookshelfManageDeviceFragmentArgs by navArgs()
    private val bookshelfFolderFlow =
        getBookshelfInfoUseCase.execute(GetBookshelfInfoUseCase.Request(BookshelfId(args.bookshelfId)))
            .map { it.dataOrNull }

    private val internalStorageFlow =
        bookshelfFolderFlow.map { it?.bookshelf as? InternalStorage }.stateIn { null }

    val state = MutableStateFlow(BookshelfEditState.NONE)
    val errorDisplayName = MutableStateFlow("")
    val displayName = MutableStateFlow("")
    val message = MutableStateFlow("aaaaaaaaaaaaaaaaaaa")
    val data = MutableStateFlow<Uri?>(null)
    val dir: StateFlow<String> = data.mapNotNull {
        it?.lastPathSegment?.split(":")?.lastOrNull()
    }.stateIn(viewModelScope, SharingStarted.Lazily, "")

    init {
        viewModelScope.launch {
            bookshelfFolderFlow.filterNotNull().onEach {
                displayName.value = it.bookshelf.displayName
            }.collect()
        }
    }

    fun updateDisplayName(str: String) {
        displayName.value = str
        if (str.isEmpty()) {
            errorDisplayName.value = "なにか入力してください"
        } else {
            errorDisplayName.value = ""
        }
    }


    fun connect(function: () -> Unit) {
        val internalStorage = internalStorageFlow.value ?: kotlin.run {
            if (args.bookshelfId == -1) null else return
        }

        state.value = BookshelfEditState.LOADING

        updateDisplayName(displayName.value)

        if (errorDisplayName.value.isNotEmpty()) {
            state.value = BookshelfEditState.NONE
            return
        }
        val storage = internalStorage?.copy(displayName = displayName.value)
            ?: InternalStorage(displayName.value)
        viewModelScope.launch {
            delay(5000)
//            when (val res = registerBookshelfUseCase.execute(
//                RegisterBookshelfUseCase.Request(
//                    storage,
//                    data.value?.toString().orEmpty()
//                )
//            ).first()) {
//                is Result.Error -> when (res.error) {
//                    RegisterBookshelfError.InvalidAuth -> message.emit("アクセス権限がありません。")
//                    RegisterBookshelfError.InvalidPath -> message.emit("フォルダが存在しません。")
//                    RegisterBookshelfError.InvalidBookshelfInfo -> Unit
//                }
//
//                is Result.Exception -> {
//                    if (res.cause is Unknown) {
//                        logcat { "Error: ${(res.cause as Unknown).throws}" }
//                    }
//                }
//
//                is Result.Success -> {
//                    function.invoke()
//                }
//            }
            message.emit("アクセス権限がありません。")
            state.value = BookshelfEditState.NONE
        }
    }
}

internal class FakeBookshelfDeviceEditViewModel :
    BookshelfDeviceEditViewModel(object : GetBookshelfInfoUseCase() {
        override fun run(request: Request): Flow<Result<BookshelfFolder, GetLibraryInfoError>> {
            return flowOf(
                Result.Success(
                    BookshelfFolder(
                        InternalStorage(BookshelfId(0), "Fake Display Name", 0) to Folder(
                            BookshelfId(0),
                            "",
                            "",
                            "",
                            0,
                            0
                        )
                    )
                )
            )
        }
    }, object : RegisterBookshelfUseCase() {
        override suspend fun run(request: Request): Result<Bookshelf, RegisterBookshelfError> {
            TODO("Not yet implemented")
        }
    }, SavedStateHandle())

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    val nestedScrollConnection = rememberNestedScrollInteropConnection()
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
                .overscroll(ScrollableDefaults.overscrollEffect())
                .verticalScroll(rememberScrollState())
                .nestedScroll(nestedScrollConnection)
                .height(2000.dp)
                .padding(paddingValues.copy(all = 16.dp))
        ) {
            OutlinedTextField(
                value = displayName.value.orEmpty(),
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
            TextButton(
                onClick = {
                    viewModel.connect { }
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

@Preview(showSystemUi = true)
@Composable
internal fun PreviewBookshelfDeviceEditScreen() {
    AppMaterialTheme {
        val navController = rememberNavController()
        BookshelfDeviceEditScreen(navController, {}, FakeBookshelfDeviceEditViewModel())
    }
}
