package com.sorrowblue.comicviewer.data.coil.startup

import com.sorrowblue.comicviewer.framework.common.BaseInitializerEntryPoint
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface InitializerEntryPoint : BaseInitializerEntryPoint<CoilInitializer> {

    companion object : BaseInitializerEntryPoint.CompanionObject
}
