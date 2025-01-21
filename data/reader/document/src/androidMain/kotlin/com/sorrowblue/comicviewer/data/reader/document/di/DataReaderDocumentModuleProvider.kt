package com.sorrowblue.comicviewer.data.reader.document.di

import com.google.auto.service.AutoService
import com.sorrowblue.comicviewer.data.datastore.di.DynamicFeatureModuleProvider
import org.koin.core.module.Module
import org.koin.ksp.generated.module

@AutoService(DynamicFeatureModuleProvider::class)
class DataReaderDocumentModuleProvider : DynamicFeatureModuleProvider {
    override fun module(): Module {
        return DataReaderDocumentModule().module
    }
}
