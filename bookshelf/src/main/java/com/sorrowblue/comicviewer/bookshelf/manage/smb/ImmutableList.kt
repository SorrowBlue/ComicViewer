package com.sorrowblue.comicviewer.bookshelf.manage.smb

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableList<T>(val l: List<T>)

fun <T> immutableList(vararg value: T) = ImmutableList(value.asList())

fun <T> List<T>.asImmutable() = ImmutableList(this)
