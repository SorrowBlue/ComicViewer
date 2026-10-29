/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import com.sorrowblue.comicviewer.domain.model.file.SupportExtension
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Settings controlling folder scanning and file recognition behavior.
 *
 * @property isAutoRefresh Whether folders automatically refresh when opened.
 * @property supportExtension List of archive and document extensions recognized as books.
 * @property resolveImageFolder Whether folders directly containing images are treated as books.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FolderSettings(
    @ProtoNumber(1) val isAutoRefresh: Boolean = true,
    @ProtoNumber(2) val supportExtension: List<SupportExtension> = SupportExtension.Archive.entries,
    @ProtoNumber(3) val resolveImageFolder: Boolean = false,
)
