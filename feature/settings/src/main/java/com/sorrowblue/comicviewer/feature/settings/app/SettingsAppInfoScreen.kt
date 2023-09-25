package com.sorrowblue.comicviewer.feature.settings.app

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.feature.settings.BuildConfig
import com.sorrowblue.comicviewer.feature.settings.R
import com.sorrowblue.comicviewer.feature.settings.app.component.SettingsAppInfoTopAppBar
import com.sorrowblue.comicviewer.feature.settings.packageInfo
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
internal class SettingsAppInfoViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsAppInfoScreenUiState(
            context.packageInfo.versionName,
            Instant.ofEpochMilli(BuildConfig.TIMESTAMP)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    )

    val uiState = _uiState.asStateFlow()
}

@Composable
internal fun SettingsAppInfoRoute(
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
    onRateAppClick: () -> Unit,
    viewModel: SettingsAppInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SettingsAppInfoScreen(
        uiState,
        onBackClick = onBackClick,
        onLicenceClick = onLicenceClick,
        onRateAppClick = onRateAppClick
    )
}


data class SettingsAppInfoScreenUiState(
    val version: String,
    val build: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsAppInfoScreen(
    uiState: SettingsAppInfoScreenUiState,
    onBackClick: () -> Unit = {},
    onLicenceClick: () -> Unit = {},
    onRateAppClick: () -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SettingsAppInfoTopAppBar(
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
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_app_label_version)) },
                supportingContent = { Text(text = uiState.version) }
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_app_label_build)) },
                supportingContent = { Text(text = uiState.build) }
            )
            val label = stringResource(R.string.settings_app_label_license)
            ListItem(
                headlineContent = { Text(label) },
                modifier = Modifier.clickable(onClick = onLicenceClick)
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings_app_label_rate)) },
                leadingContent = { Icon(ComicIcons.Star, null) },
                modifier = Modifier.clickable(onClick = onRateAppClick)
            )
        }
    }
}
/**
 *                     LibsBuilder().withActivityTitle(label)
 *                         .withSearchEnabled(true).withEdgeToEdge(true).start(context)
 */
