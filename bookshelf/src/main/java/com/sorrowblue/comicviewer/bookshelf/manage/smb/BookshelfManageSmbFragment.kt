package com.sorrowblue.comicviewer.bookshelf.manage.smb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.databinding.InverseMethod
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.transition.MaterialSharedAxis
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.domain.entity.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.copy
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import dagger.hilt.android.AndroidEntryPoint
import logcat.logcat

@AndroidEntryPoint
internal class BookshelfManageSmbFragment : FrameworkFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppMaterialTheme {
                    TestScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun TestScreen(viewModel: BookshelfManageSmbViewModel = hiltViewModel()) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    val snackbarHostState = SnackbarHostState()
    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.bookshelf_manage_title_device),
                        modifier = Modifier.basicMarquee()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /*navController.popBackStack()*/ }) {
                        Icon(imageVector = Icons.TwoTone.ArrowBack, contentDescription = "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        val bookshelfFolder by viewModel.bookshelfFolderFlow.collectAsState(initial = null)
        MaiinContent(
            bookshelfFolder?.bookshelf as? SmbServer,
            bookshelfFolder?.folder,
            {},
            modifier = Modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
                .overscroll(ScrollableDefaults.overscrollEffect())
                .padding(paddingValues.copy(all = 16.dp))
        )
    }
}

@Composable
fun MaiinContent(
    smbServer: SmbServer?,
    folder: Folder?,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var host by remember { mutableStateOf(smbServer?.host.orEmpty()) }
    var port by remember { mutableStateOf(smbServer?.port?.toString().orEmpty()) }
    var path by remember { mutableStateOf(folder?.path.orEmpty()) }
    var displayName by remember { mutableStateOf(smbServer?.displayName.orEmpty()) }
    var authMode by remember {
        logcat("TAG") { "cal authMode" }
        mutableStateOf(
            when (smbServer?.auth ?: SmbServer.Auth.Guest) {
                SmbServer.Auth.Guest -> AuthMode.GUEST
                is SmbServer.Auth.UsernamePassword -> AuthMode.USERPASS
            }
        )
    }
    var domain by remember {
        logcat("TAG") { "cal domain" }
        mutableStateOf(
            when (val auth = smbServer?.auth ?: SmbServer.Auth.Guest) {
                SmbServer.Auth.Guest -> ""
                is SmbServer.Auth.UsernamePassword -> auth.domain
            }
        )
    }
    var username by remember {
        mutableStateOf(
            when (val auth = smbServer?.auth ?: SmbServer.Auth.Guest) {
                SmbServer.Auth.Guest -> ""
                is SmbServer.Auth.UsernamePassword -> auth.username
            }
        )
    }
    var password by remember {
        mutableStateOf(
            when (val auth = smbServer?.auth ?: SmbServer.Auth.Guest) {
                SmbServer.Auth.Guest -> ""
                is SmbServer.Auth.UsernamePassword -> auth.password
            }
        )
    }
    Column(modifier) {
        OutlinedTextField(value = host, onValueChange = { host = it }, label = {
            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_host))
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        )
        OutlinedTextField(value = port, onValueChange = { port = it }, label = {
            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_port))
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        )
        OutlinedTextField(value = path, onValueChange = { path = it }, label = {
            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_path))
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        )
        OutlinedTextField(value = displayName, onValueChange = { displayName = it }, label = {
            Text(text = stringResource(id = R.string.bookshelf_manage_hint_display_name))
        }, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        )

        Row {
            OutlinedButton(
                onClick = { /*TODO*/ },
                shape = MaterialTheme.shapes.small.copy(
                    topEnd = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            ) {
                Text(text = "Guest")
            }
            OutlinedButton(
                onClick = { /*TODO*/ },
                shape = MaterialTheme.shapes.small.copy(
                    topStart = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                )
            ) {
                Text(text = "Username/Password")
            }
        }

        when (authMode) {
            AuthMode.GUEST -> {
            }

            AuthMode.USERPASS -> {
                OutlinedTextField(value = domain, onValueChange = { domain = it }, label = {
                    Text(text = stringResource(id = R.string.bookshelf_manage_hint_domain))
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                )
                OutlinedTextField(value = username, onValueChange = { username = it }, label = {
                    Text(text = stringResource(id = R.string.bookshelf_manage_hint_username))
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                )
                OutlinedTextField(value = password, onValueChange = { password = it }, label = {
                    Text(text = stringResource(id = R.string.bookshelf_manage_hint_password))
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                )
            }
        }
        Button(onClick = { }) {
            Text(text = "Save")
        }
    }
}


internal object AuthConverter {

    @JvmStatic
    @InverseMethod("buttonIdToBoolean")
    fun booleanToButtonId(value: Boolean): Int {
        return if (value) R.id.guest else R.id.username_password
    }

    @JvmStatic
    fun buttonIdToBoolean(value: Int): Boolean {
        return value == R.id.guest
    }
}


internal object PortConverter {

    @JvmStatic
    @InverseMethod("portToString")
    fun stringToPort(value: String?) = value?.toIntOrNull()

    @JvmStatic
    fun portToString(value: Int?) = value?.toString()
}
