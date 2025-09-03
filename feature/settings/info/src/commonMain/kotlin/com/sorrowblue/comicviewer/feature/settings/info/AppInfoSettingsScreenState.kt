package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import om.sorrowblue.comicviewer.feature.settings.BuildKonfig

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

    @OptIn(ExperimentalTime::class)
    override var uiState: SettingsAppInfoScreenUiState by mutableStateOf(
        SettingsAppInfoScreenUiState(
            versionName = BuildKonfig.VERSION_NAME,
            buildAt = Instant.fromEpochMilliseconds(BuildKonfig.TIMESTAMP)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(
                    LocalDateTime.Format {
                        date(LocalDate.Formats.ISO)
                        char(' ')
                        time(LocalTime.Formats.ISO)
                    }
                )
        )
    )

    override fun launchReview() {
        urlHandler.openUri("http://play.google.com/store/apps/details?id=com.sorrowblue.comicviewer")
    }
}
