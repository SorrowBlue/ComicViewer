package com.sorrowblue.comicviewer.feature.settings.display

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.DarkMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.entity.settings.DarkMode
import com.sorrowblue.comicviewer.feature.settings.display.section.SettingsDisplayTopAppBar
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

data class SettingsDisplayScreenUiState(
    val darkMode: DarkMode = DarkMode.DEVICE
)

@Composable
internal fun SettingsDisplayRoute(
    onBackClick: () -> Unit,
    viewModel: SettingsDisplayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SettingsDisplayScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onDarkModeChange = viewModel::updateDarkMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsDisplayScreen(
    uiState: SettingsDisplayScreenUiState = SettingsDisplayScreenUiState(),
    onBackClick: () -> Unit = {},
    onDarkModeChange: (DarkMode) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SettingsDisplayTopAppBar(
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
        ) {
            Box {
                var expanded by remember { mutableStateOf(false) }
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(id = R.string.settings_display_label_design))
                    },
                    supportingContent = {
                        Text(text = uiState.darkMode.label())
                    },
                    leadingContent = { Icon(Icons.TwoTone.DarkMode, null) },
                    modifier = Modifier.clickable { expanded = !expanded },
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(48.dp, 0.dp)
                ) {
                    DarkMode.entries.forEach { darkMode ->
                        DropdownMenuItem(
                            onClick = {
                                expanded = false
                                onDarkModeChange(darkMode)
                            },
                            text = {
                                Text(text = darkMode.label())
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkMode.label() = when (this) {
    DarkMode.DEVICE -> stringResource(id = R.string.settings_display_label_system_default)
    DarkMode.DARK -> stringResource(id = R.string.settings_display_label_dark_mode)
    DarkMode.LIGHT -> stringResource(id = R.string.settings_display_label_light_mode)
}

@MultiThemePreviews
@Composable
private fun PreviewSettingsDisplayScreen() {
    AppMaterialTheme {
        Surface {
            SettingsDisplayScreen()
        }
    }
}

@Preview(name = "Light Mode", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class MultiThemePreviews
