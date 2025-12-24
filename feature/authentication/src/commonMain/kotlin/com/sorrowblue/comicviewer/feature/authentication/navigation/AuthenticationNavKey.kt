package com.sorrowblue.comicviewer.feature.authentication.navigation

import com.sorrowblue.comicviewer.feature.authentication.ScreenType
import com.sorrowblue.comicviewer.framework.ui.navigation.ScreenKey
import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationNavKey(val type: ScreenType) : ScreenKey
