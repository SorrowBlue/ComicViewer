package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

internal interface AppInfoSettingsScreenState {
    fun launchReview()
    var uiState: SettingsAppInfoScreenUiState
}

@Composable
internal fun rememberAppInfoSettingsScreenState(
    urlHandler: UriHandler = LocalUriHandler.current,
): AppInfoSettingsScreenState = remember {
    AppInfoSettingsScreenStateImpl(urlHandler = urlHandler)
}

private class AppInfoSettingsScreenStateImpl(
    private val urlHandler: UriHandler,
) : AppInfoSettingsScreenState {

    override var uiState: SettingsAppInfoScreenUiState by mutableStateOf(
        SettingsAppInfoScreenUiState(
            versionName = "BuildConfig.VERSION_NAME",// TODO
            buildAt = Instant.fromEpochMilliseconds(0L)// TODO BuildConfig.TIMESTAMP
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(
                    LocalDateTime.Format {
                        // TODO
                    }
                )
        )
    )

    override fun launchReview() {
        urlHandler.openUri("http://play.google.com/store/apps/details?id=comicviewr")
    }
}
