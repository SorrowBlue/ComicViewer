/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.konture

import io.github.baole.konture.Konture
import io.github.baole.konture.assertNoCycles
import io.github.baole.konture.classes
import io.github.baole.konture.modules
import kotlin.test.Test

/**
 * Architecture guardrail tests verifying module dependencies and class structure conventions.
 */
class ArchitectureGuardrailsTest {

    /**
     * Ensures there are no cyclic dependencies between modules.
     */
    @Test
    fun `no cycles between modules`() {
        Konture.assertNoCycles()
    }

    /**
     * Ensures `:domain:model` does not depend on any other project modules (except `:core:logger`).
     */
    @Test
    fun `domain model does not depend on other layer`() {
        Konture.modules()
            .that().haveNamePath(":domain:model")
            .should().onlyDependOnModules(":core:logger")
            .check()
    }

    /**
     * Ensures `:domain:usecase` only depends on `:domain:model`, `:domain:repository`, and `:domain:service` (plus `:core:logger`).
     */
    @Test
    fun `domain usecase only depends on domain model, repository and service`() {
        Konture.modules()
            .that().haveNamePath(":domain:usecase")
            .should().onlyDependOnModules(
                ":domain:model",
                ":domain:repository",
                ":domain:service",
                ":core:logger",
                ":",
            )
            .check()
    }

    /**
     * Ensures `:domain:service` only depends on `:domain:model` (plus `:core:logger`).
     */
    @Test
    fun `domain service only depends on domain model`() {
        Konture.modules()
            .that().haveNamePath(":domain:service")
            .should().onlyDependOnModules(":domain:model", ":core:logger", ":")
            .check()
    }

    /**
     * Ensures `:domain:repository` only depends on `:domain:model` (plus `:core:logger`).
     */
    @Test
    fun `domain repository only depends on domain model`() {
        Konture.modules()
            .that().haveNamePath(":domain:repository")
            .should().onlyDependOnModules(":domain:model", ":core:logger", ":")
            .check()
    }

    /**
     * Ensures the `feature` layer does not directly depend on the `data` layer.
     */
    @Test
    fun `feature layer does not directly depend on data layer`() {
        Konture.modules()
            .that().haveNamePath { it.startsWith(":feature") }
            .should().notDependOnModule { it.startsWith(":data") }
            .check()
    }

    /**
     * Ensures the `framework` layer does not depend on `data` or `feature` layers,
     * and non-UI `framework` modules do not depend on `:domain:usecase`.
     */
    @Test
    fun `framework layer does not depend on feature layer`() {
        Konture.modules()
            .that().haveNamePath { it.startsWith(":framework") }
            .should().notDependOnModule {
                it.startsWith(":feature")
                    || it.startsWith(":data")
            }
            .check()

        Konture.modules()
            .that().haveNamePath { it.startsWith(":framework") && !it.startsWith(":framework:ui") }
            .should().notDependOnModule { it.startsWith(":domain:usecase") }
            .check()
    }

    /**
     * Ensures all `RepositoryImpl`, `ServiceImpl`, and `UseCaseImpl` classes are `internal`.
     */
    @Test
    fun `repository, service, and usecase implementations are internal`() {
        Konture.classes()
            .allowEmpty()
            .that().nameEndsWith("RepositoryImpl")
            .should().beInternal()
            .check()

        listOf(
            "ServiceImpl",
            "AndroidFileSortService",
            "JvmFileSortService",
            "IosFileSortService",
        ).forEach { suffix ->
            Konture.classes()
                .allowEmpty()
                .that().nameEndsWith(suffix)
                .should().beInternal()
                .check()
        }

        Konture.classes()
            .allowEmpty()
            .that().nameEndsWith("UseCaseImpl")
            .should().beInternal()
            .check()
    }

    /**
     * Ensures all `Repository` declarations in the domain layer are interfaces.
     */
    @Test
    fun `repository interfaces are interfaces`() {
        Konture.classes()
            .allowEmpty()
            .that().inPackage("..domain.repository..")
            .and().nameEndsWith("Repository")
            .should().beInterfaces()
            .check()
    }

    /**
     * Ensures `ViewModel` classes do not reside in `domain` or `data` layers.
     */
    @Test
    fun `viewmodels do not reside in domain or data layers`() {
        Konture.classes()
            .allowEmpty()
            .that().nameEndsWith("ViewModel")
            .and().inPackage("..domain..", "..data..")
            .should().beInterfaces()
            .check()
    }

    /**
     * Ensures the `data` layer does not depend on `feature` or `app` layers.
     */
    @Test
    fun `data layer does not depend on feature and app layers`() {
        Konture.modules()
            .that().haveNamePath { it.startsWith(":data") }
            .should().notDependOnModule { it.startsWith(":feature") || it.startsWith(":app") }
            .check()
    }

    /**
     * Ensures `feature` modules do not directly depend on other `feature` modules,
     * except submodules within the same feature group or `:nav` navigation modules.
     */
    @Test
    fun `feature modules do not directly depend on other feature modules`() {
        val featureNames = listOf(
            "authentication",
            "book",
            "bookshelf",
            "collection",
            "file",
            "folder",
            "history",
            "readlater",
            "search",
            "settings",
            "tutorial",
        )
        featureNames.forEach { feature ->
            Konture.modules()
                .that().haveNamePath { it.startsWith(":feature:$feature") }
                .should().notDependOnModule { target ->
                    target.startsWith(":feature:") &&
                        !target.startsWith(":feature:$feature") &&
                        !target.endsWith(":nav")
                }
                .check()
        }
    }
}

