/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.model.settings

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

/**
 * Settings for displaying collections.
 *
 * @property recent Whether to sort collections by recently updated by default.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CollectionSettings(@ProtoNumber(1) val recent: Boolean = true)
