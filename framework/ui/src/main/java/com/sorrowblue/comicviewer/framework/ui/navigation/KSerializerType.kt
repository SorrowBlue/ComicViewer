package com.sorrowblue.comicviewer.framework.ui.navigation

import android.os.Bundle
import androidx.navigation.NavType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import kotlinx.serialization.serializer

inline fun <reified T> kSerializerType(json: Json = Json) =
    KSerializerType(json.serializersModule.serializer<T>())

@OptIn(ExperimentalSerializationApi::class)
class KSerializerType<T>(val serializer: KSerializer<T>) {

    fun toByteArray(value: T): ByteArray =
        ByteArrayOutputStream().use {
            Json.encodeToStream(serializer, value, it)
            it.toByteArray()
        }

    fun fromByteArray(bytes: ByteArray): T =
        ByteArrayInputStream(bytes).use {
            Json.decodeFromStream(serializer, it)
        }
}

inline fun <reified T : @Serializable Any> typePair(isNullableAllowed: Boolean): Pair<KType, NavType<T>> =
    typeOf<T>() to navType<T>(isNullableAllowed = isNullableAllowed)

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : @Serializable Any> navType(isNullableAllowed: Boolean) =
    object : NavType<T>(isNullableAllowed) {
        override val name = T::class.qualifiedName ?: T::class.java.name

        override fun get(bundle: Bundle, key: String): T? {
            return bundle.getByteArray(key)?.let {
                ByteArrayInputStream(it).use(Json::decodeFromStream)
            }
        }

        override fun put(bundle: Bundle, key: String, value: T) {
            val byteArray = ByteArrayOutputStream().use {
                Json.encodeToStream(value, it)
                it.toByteArray()
            }
            bundle.putByteArray(key, byteArray)
        }

        override fun parseValue(value: String): T = Json.decodeFromString(value)

        override fun serializeAsValue(value: T) = Json.encodeToString(value)
    }
