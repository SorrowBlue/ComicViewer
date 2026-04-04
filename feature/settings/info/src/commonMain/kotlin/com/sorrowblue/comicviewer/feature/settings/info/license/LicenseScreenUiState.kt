package com.sorrowblue.comicviewer.feature.settings.info.license

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library

internal data class LicenseScreenUiState(val libs: Libs? = null, val openDialog: Library? = null)
