package com.sorrowblue.comicviewer.framework.navigation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

inline fun <reified D : @Serializable Any> NavType.Companion.kSerializableType(isNullableAllowed: Boolean = false): KSerializableType<D> {
    return KSerializableType(Json.serializersModule.serializer<D>(), isNullableAllowed)
}

@OptIn(ExperimentalSerializationApi::class)
class KSerializableType<D : @Serializable Any?>(
    val serializer: KSerializer<D>,
    isNullableAllowed: Boolean = false,
) : NavType<D>(isNullableAllowed) {
    override val name get() = serializer.descriptor.serialName

    override fun get(bundle: Bundle, key: String): D? {
        return bundle.getByteArray(key)?.let { bytes ->
            Buffer().use {
                it.write(bytes)
                Json.decodeFromBuffer(serializer, it)
            }
        }
    }

    override fun put(bundle: Bundle, key: String, value: D) {
        Buffer().also { Json.encodeToBuffer(serializer, value, it) }.use {
            bundle.putByteArray(key, it.readByteArray())
        }
    }

    override fun serializeAsValue(value: D): String {
        return Json.encodeToString(serializer, value)
    }

    override fun parseValue(value: String): D {
        return Json.decodeFromString(serializer, value)
    }
}
