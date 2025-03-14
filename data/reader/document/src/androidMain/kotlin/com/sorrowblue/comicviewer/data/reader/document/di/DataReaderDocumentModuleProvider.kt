package com.sorrowblue.comicviewer.data.reader.document.di

import com.google.auto.service.AutoService
import com.sorrowblue.comicviewer.data.datastore.di.DynamicFeatureModuleProvider
import org.koin.core.annotation.ComponentScan
import org.koin.core.module.Module
import org.koin.ksp.generated.module

@AutoService(DynamicFeatureModuleProvider::class)
internal class DataReaderDocumentModuleProvider : DynamicFeatureModuleProvider {
    override fun module(): Module {
        return DataReaderDocument().module
    }
}

@org.koin.core.annotation.Module
@ComponentScan("com.sorrowblue.comicviewer.data.reader.document")
class DataReaderDocument
