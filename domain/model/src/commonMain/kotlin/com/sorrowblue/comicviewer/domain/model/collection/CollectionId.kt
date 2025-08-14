package com.sorrowblue.comicviewer.domain.model.collection

import com.sorrowblue.comicviewer.domain.model.InternalDataApi
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class CollectionId @InternalDataApi constructor(val value: Int) {

    companion object {

        @OptIn(InternalDataApi::class)
        operator fun invoke() = CollectionId(0)
    }
}
