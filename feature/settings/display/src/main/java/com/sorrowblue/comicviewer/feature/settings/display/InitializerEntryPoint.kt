package com.sorrowblue.comicviewer.feature.settings.display

import com.sorrowblue.comicviewer.framework.common.BaseInitializerEntryPoint
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface InitializerEntryPoint : BaseInitializerEntryPoint<DarkModeInitializer> {

    companion object : BaseInitializerEntryPoint.CompanionObject
}
