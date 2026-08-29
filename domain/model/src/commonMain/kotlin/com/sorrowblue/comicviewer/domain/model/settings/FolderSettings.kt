/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import com.sorrowblue.comicviewer.domain.model.SupportExtension
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FolderSettings(
    @ProtoNumber(1) val isAutoRefresh: Boolean = true,
    @ProtoNumber(2) val supportExtension: List<SupportExtension> = SupportExtension.Archive.entries,
    @ProtoNumber(3) val resolveImageFolder: Boolean = false,
)
