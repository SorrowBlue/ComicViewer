package com.sorrowblue.comicviewer.feature.settings.folder

import com.sorrowblue.comicviewer.framework.common.BaseInitializerEntryPoint
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface InitializerEntryPoint : BaseInitializerEntryPoint<ExtensionInitializer> {

    companion object : BaseInitializerEntryPoint.CompanionObject
}
