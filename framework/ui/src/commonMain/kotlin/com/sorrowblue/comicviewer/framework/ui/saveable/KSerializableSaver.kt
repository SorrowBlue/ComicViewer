package com.sorrowblue.comicviewer.framework.ui.saveable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> T.encodeToByteArray(): ByteArray {
    return Cbor.encodeToByteArray<T>(this)
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ByteArray.decodeTo(): T {
    return Cbor.decodeFromByteArray<T>(this)
}

@Composable
fun <T : Any> rememberListSaveable(
    vararg inputs: Any?,
    save: (T) -> List<Any?>,
    restore: T.(List<Any?>) -> Unit,
    init: () -> T,
): T {
    return rememberSaveable(
        inputs = inputs,
        saver = Saver(
            save = {
                val list = save(it)
                for (index in list.indices) {
                    val item = list[index]
                    if (item != null) {
                        require(canBeSaved(item)) { "item can't be saved" }
                    }
                }
                if (list.isNotEmpty()) ArrayList(list) else null
            },
            restore = { init().apply { restore(it.toList()) } }
        ),
        init = init
    )
}
