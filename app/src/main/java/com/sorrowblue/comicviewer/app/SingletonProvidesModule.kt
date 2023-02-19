package com.sorrowblue.comicviewer.app

import androidx.navigation.ui.AppBarConfiguration
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkAppBarConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @FrameworkAppBarConfiguration
    @Provides
    fun bindFrameworkAppBarConfiguration() = AppBarConfiguration(
        setOf(
            com.sorrowblue.comicviewer.bookshelf.R.id.bookshelf_list_fragment,
            com.sorrowblue.comicviewer.favorite.R.id.favorite_list_fragment,
            com.sorrowblue.comicviewer.readlater.R.id.readlater_fragment,
//            com.sorrowblue.comicviewer.library.R.id.library_list_fragment,
        )
    )
}
