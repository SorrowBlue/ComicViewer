package com.sorrowblue.comicviewer.app

import android.content.Context
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkAppBarConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FrameworkAppBarConfigurationModule {

    @FrameworkAppBarConfiguration
    @Provides
    fun bindFrameworkAppBarConfiguration() = AppBarConfiguration(
        setOf(
            com.sorrowblue.comicviewer.bookshelf.R.id.bookshelf_list_fragment,
            com.sorrowblue.comicviewer.favorite.R.id.favorite_list_fragment,
            com.sorrowblue.comicviewer.readlater.R.id.readlater_fragment,
            com.sorrowblue.comicviewer.library.R.id.library_list_fragment,
        )
    )

    @Singleton
    @Provides
    fun splitInstallManager(@ApplicationContext context: Context) =
        SplitInstallManagerFactory.create(context)
}
