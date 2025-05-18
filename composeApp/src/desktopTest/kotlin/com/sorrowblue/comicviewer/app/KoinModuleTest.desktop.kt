package com.sorrowblue.comicviewer.app

import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.navigation.NavController
import com.sorrowblue.comicviewer.app.navigation.ComicViewerAppNavigatorImpl
import com.sorrowblue.comicviewer.data.storage.client.SeekableInputStream
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.InternalStorage
import com.sorrowblue.comicviewer.domain.model.bookshelf.ShareContents
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.model.file.File
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.test.verify.verify

@OptIn(KoinExperimentalAPI::class)
internal actual fun Module.verify() {
    verify(
        extraTypes = listOf(
            Bookshelf::class,
            InternalStorage::class,
            SmbServer::class,
            ShareContents::class,
            CoroutineScope::class,
            ThreePaneScaffoldNavigator::class,
            Lazy::class,
            File::class,
            NavController::class,
            Function0::class,
            ComicViewerAppNavigatorImpl::class,
            SeekableInputStream::class
        )
    )
}
