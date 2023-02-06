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
            com.sorrowblue.comicviewer.server.R.id.server_list_fragment,
            com.sorrowblue.comicviewer.favorite.R.id.favorite_list_fragment,
            com.sorrowblue.comicviewer.readlater.R.id.readlater_fragment
        )
    )
}
