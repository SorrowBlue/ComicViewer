package com.sorrowblue.comicviewer.data.storage.client

import org.koin.core.module.Module

interface DynamicFeatureModuleProvider {
    fun module(): Module
}
