package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.compose.serialization.serializers.SnapshotStateListSerializer
import com.sorrowblue.comicviewer.feature.authentication.navigation.AuthenticationKeySerializersModule
import com.sorrowblue.comicviewer.feature.book.navigation.BookKeySerializersModule
import com.sorrowblue.comicviewer.feature.bookshelf.navigation.BookshelfKeySerializersModule
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionKeySerializersModule
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryKeySerializersModule
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterKeySerializersModule
import com.sorrowblue.comicviewer.feature.search.navigation.SearchKeySerializersModule
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsKeySerializersModule
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialKeySerializersModule
import com.sorrowblue.comicviewer.folder.navigation.SortTypeSelectKey
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.polymorphic

internal val AppSerializersModule
    get() = SerializersModule {
        contextual(SnapshotStateListSerializer<NavKey>())
        polymorphic(NavKey::class) {
            subclass(SortTypeSelectKey::class, SortTypeSelectKey.serializer())

            include(BookshelfKeySerializersModule)
            include(CollectionKeySerializersModule)
            include(ReadLaterKeySerializersModule)
            include(HistoryKeySerializersModule)

            include(SearchKeySerializersModule)
            include(SettingsKeySerializersModule)
            include(BookKeySerializersModule)

            include(AuthenticationKeySerializersModule)
            include(TutorialKeySerializersModule)
        }
    }
