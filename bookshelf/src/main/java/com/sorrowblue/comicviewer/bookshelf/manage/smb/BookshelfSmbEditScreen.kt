package com.sorrowblue.comicviewer.bookshelf.manage.smb

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Save
import androidx.compose.material.icons.twotone.Visibility
import androidx.compose.material.icons.twotone.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sorrowblue.comicviewer.bookshelf.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import com.sorrowblue.comicviewer.framework.compose.copy
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
internal fun BookshelfSmbEditScreen(
    navController: NavController,
    viewModel: BookshelfSmbEditViewModel = hiltViewModel()
) {
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
    val snackbarHostState = SnackbarHostState()
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .overscroll(ScrollableDefaults.overscrollEffect())
                .padding(paddingValues.copy(all = 16.dp))
        ) {
            val hostValidatorState =
                stringResource(id = R.string.bookshelf_edit_smb_input_error_host).let { message ->
                    rememberValidatorState { if (it.isNotEmpty()) null else message }
                }
            val isInvalidHost = viewModel.isInvalidHost.collectAsState(false)
            OutlinedTextField(
                value = viewModel.host.collectAsState().value,
                onValueChange = { viewModel.host.value = it },
                isError = isInvalidHost.value,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                )
            )
            val portValidatorState =
                stringResource(id = R.string.bookshelf_edit_smb_input_error_port).let { message ->
                    rememberValidatorState {
                        if (it.isNotEmpty() && it.matches("^((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))\$".toRegex())) null else message
                    }
                }
            val port by viewModel.port.collectAsState()
            OutlinedTextField(
                value = port,
                onValueChange = { viewModel.port.value = it },
                label = {
                    Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_port))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
            )
            val path by viewModel.path.collectAsState()
            OutlinedTextField(
                value = path,
                onValueChange = { viewModel.path.value = it },
                label = {
                    Text(text = stringResource(id = R.string.bookshelf_manage_hint_path))
                },
                prefix = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_prefix_path)) },
                suffix = { Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_suffix_path)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                )
            )
            val displayNameValidatorState =
                rememberValidatorState { if (it.isNotEmpty()) null else "なにか入力してください。" }
            val authMode by viewModel.authMode.collectAsState()
            val displayName by viewModel.displayName.collectAsState()
            ValidatorTextField(
                value = displayName,
                onValueChange = {  },
                label = {
                    Text(text = stringResource(id = R.string.bookshelf_manage_hint_display_name))
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = if (authMode == AuthMode.USERPASS) ImeAction.Next else ImeAction.Done
                ),
                validatorState = displayNameValidatorState
            )

            Spacer(modifier = Modifier.padding(top = 8.dp))

            ToggleGroup(
                listOf(
                    stringResource(id = R.string.bookshelf_manage_label_guest),
                    stringResource(id = R.string.bookshelf_manage_label_username_password)
                ),
                AuthMode.entries.indexOf(authMode)
            ) {
                viewModel.authMode.value = AuthMode.entries[it]
            }
            val usernameValidator =
                stringResource(id = R.string.bookshelf_edit_smb_input_error_username).let { message ->
                    rememberValidatorState { if (it.isNotEmpty()) null else message }
                }
            val passwordValidator =
                stringResource(id = R.string.bookshelf_edit_smb_input_error_password).let { message ->
                    rememberValidatorState { if (it.isNotEmpty()) null else message }
                }
            when (authMode) {
                AuthMode.GUEST -> {
                    usernameValidator.isError = false
                    passwordValidator.isError = false
                }

                AuthMode.USERPASS -> {
                    val domain by viewModel.domain.collectAsState()
                    OutlinedTextField(
                        value = domain,
                        onValueChange = { viewModel.domain.value = it },
                        label = {
                            Text(text = stringResource(id = R.string.bookshelf_manage_hint_domain))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )
                    )
                    val username by viewModel.username.collectAsState()
                    ValidatorTextField(
                        value = username,
                        onValueChange = { viewModel.username.value = it },
                        label = {
                            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_username))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        validatorState = usernameValidator
                    )
                    val password by viewModel.password.collectAsState()
                    ValidatorTextField(
                        value = password,
                        onValueChange = { viewModel.password.value = it },
                        label = {
                            Text(text = stringResource(id = R.string.bookshelf_edit_smb_input_label_password))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        validatorState = passwordValidator
                    )
                }
            }
            Spacer(modifier = Modifier.padding(top = 8.dp))
            val keyboardController = LocalSoftwareKeyboardController.current
            val errorMessage = stringResource(id = R.string.bookshelf_edit_smb_message_error)
            val scope = rememberCoroutineScope()
            TextButton(
                onClick = {
                    hostValidatorState.revalidate()
                    portValidatorState.revalidate()
                    displayNameValidatorState.revalidate()
                    when (authMode) {
                        AuthMode.GUEST -> {
                            if (hostValidatorState.isError || portValidatorState.isError || displayNameValidatorState.isError) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = errorMessage,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "GUEST OK",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }

                        AuthMode.USERPASS -> {
                            usernameValidator.revalidate()
                            passwordValidator.revalidate()
                            if (hostValidatorState.isError || portValidatorState.isError || displayNameValidatorState.isError || usernameValidator.isError || passwordValidator.isError) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = errorMessage,
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "USER OK",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        }
                    }
                    keyboardController?.hide()
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
}

enum class AuthMode(val index: Int) {
    GUEST(0),
    USERPASS(1)
}

@Composable
fun ToggleGroup(
    items: List<String>,
    selectedIndex: Int,
    indexChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            OutlinedButton(
                onClick = { indexChanged(index) },
                modifier = when (index) {
                    0 ->
                        Modifier
                            .offset(0.dp, 0.dp)
                            .zIndex(if (selectedIndex == index) 1f else 0f)

                    else ->
                        Modifier
                            .offset((-1 * index).dp, 0.dp)
                            .zIndex(if (selectedIndex == index) 1f else 0f)
                },
                shape = when (index) {
                    // left outer button
                    0 -> RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 0.dp,
                        bottomStart = 8.dp,
                        bottomEnd = 0.dp
                    )
                    // right outer button
                    items.size - 1 -> RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 8.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 8.dp
                    )
                    // middle button
                    else -> RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                },
                border = BorderStroke(
                    1.dp, if (selectedIndex == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.DarkGray.copy(alpha = 0.75f)
                    }
                ),
                colors = if (selectedIndex == index) {
                    ButtonDefaults.outlinedButtonColors()
                    // selected colors
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    // not selected colors
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                },
            ) {
                Text(
                    text = item,
                    color = if (selectedIndex == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.DarkGray.copy(alpha = 0.9f)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreivewPasswordInput() {
    AppMaterialTheme {
        Surface {
            val password = remember { mutableStateOf("") }
            PasswordInput(password)
        }
    }
}

@Composable
fun PasswordInput(password: MutableState<String>, initPasswordVisibility: Boolean = false) {
    var passwordVisibility by remember { mutableStateOf(initPasswordVisibility) }
    OutlinedTextField(
        value = password.value,
        label = {
            Text(text = stringResource(id = R.string.bookshelf_manage_hint_password))
        },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        trailingIcon = {
            IconButton(onClick = {
                passwordVisibility = !passwordVisibility
            }) {
                Icon(
                    imageVector = if (passwordVisibility) Icons.TwoTone.Visibility else Icons.TwoTone.VisibilityOff,
                    contentDescription = "Visibility Icon"
                )
            }
        },
        onValueChange = { password.value = it },
        modifier = Modifier.fillMaxWidth(),
        supportingText = {
        }
    )
}
