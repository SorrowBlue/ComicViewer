package com.sorrowblue.comicviewer.app

import kotlin.test.Test
import org.koin.core.module.Module
import org.koin.dsl.module

internal class KoinModuleTest {
    @Test
    fun checkKoinModule() {

        module {
            includes(allModules())
        }.verify()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect fun Module.verify()
