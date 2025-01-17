package com.sorrowblue.comicviewer.data.datastore.di

import org.koin.core.module.Module

interface DynamicFeatureModuleProvider {
    fun module(): Module
}
