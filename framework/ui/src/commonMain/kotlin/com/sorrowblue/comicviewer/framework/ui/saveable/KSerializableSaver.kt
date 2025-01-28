package com.sorrowblue.comicviewer.framework.ui.saveable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.sorrowblue.comicviewer.framework.ui.byteArrayToBufferedSource
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import okio.Buffer
import okio.use

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> T.encodeToByteArray(): ByteArray {
    return Buffer().also {
        Json.encodeToBufferedSink<T>(this, it)
    }.use { it.readByteArray() }
}


@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Any> ByteArray.decodeTo(): T {
    return Json.decodeFromBufferedSource<T>(byteArrayToBufferedSource(this))
}

@Composable
fun <T : Any> rememberListSaveable(
    vararg inputs: Any?,
    save: (T) -> List<Any?>,
    restore: T.(List<Any?>) -> Unit,
    key: String? = null,
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
        key = key,
        init = init
    )
}
