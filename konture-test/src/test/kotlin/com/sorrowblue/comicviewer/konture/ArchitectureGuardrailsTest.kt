/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.konture

import io.github.baole.konture.Konture
import io.github.baole.konture.assertNoCycles
import io.github.baole.konture.classes
import io.github.baole.konture.modules
import kotlin.test.Test

class ArchitectureGuardrailsTest {

    /**
     * モジュール間の循環依存がないことを保証する。
     * 循環依存はビルドエラーの原因となり、モジュールの独立性を損なう。
     */
    @Test
    fun `no cycles between modules`() {
        Konture.assertNoCycles()
    }

    /**
     * :domain:model が他のモジュールに依存していないことを保証する。
     * domain:model はプロジェクトの最下位レイヤーであり、純粋なKotlinモデルのみを含む。
     */
    @Test
    fun `domain model does not depend on other layer`() {
        Konture.modules()
            .that().haveNamePath(":domain:model")
            .should().onlyDependOnModules("")
            .check()
    }

    /**
     * :domain:usecase が :domain:model 以外に依存しないことを保証する。
     * usecase はビジネスルール(インターフェース)を定義し、実装詳細を知ってはならない。
     */
    @Test
    fun `domain usecase only depends on domain model`() {
        Konture.modules()
            .that().haveNamePath(":domain:usecase")
            .should().onlyDependOnModules(":domain:model")
            .check()
    }

    /**
     * domain:service が domain:model と domain:usecase 以外に依存しないことを保証する。
     * service はビジネスロジックを実装する。
     */
    @Test
    fun `domain service only depend on domain model and domain usecase`() {
        Konture.modules()
            .that().haveNamePath(":domain:service")
            .should().onlyDependOnModules(":domain:model", ":domain:usecase")
            .check()
    }

    /**
     * feature層がdata層に直接依存しないことを保証する。
     */
    @Test
    fun `feature layer does not directly depend on data layer`() {
        Konture.modules()
            .that().haveNamePath { it.startsWith(":feature") }
            .should().notDependOnModule { it.startsWith(":data") }
            .check()
    }

    /**
     * framework層が data層、feature層、domain:usecase、domain:serviceに依存しないことを保証する。
     */
    @Test
    fun `framework layer does not depend on feature layer`() {
        Konture.modules()
            .that().haveNamePath { it.startsWith(":framework") }
            .should().notDependOnModule {
                it.startsWith(":feature")
                    || it.startsWith(":data")
                    || it.startsWith(":domain:usecase")
                    || it.startsWith(":domain:service")
            }
            .check()
    }

    /**
     * すべての Interactor クラス（ユースケースの実装）が internal であることを保証する。
     */
    @Test
    fun `interactors are internal`() {
        Konture.classes()
            .that().haveNameEndingWith("Interactor")
            .should().beInternal()
            .check()
    }

    /**
     * すべての DataSourceImpl クラス（データソースの実装）が internal であることを保証する。
     */
    @Test
    fun `datasource implementations are internal`() {
        Konture.classes()
            .that().haveNameEndingWith("DataSourceImpl")
            .should().beInternal()
            .check()
    }

    /**
     * ドメインサービスの DataSource インターフェースがインターフェースであることを保証する。
     */
    @Test
    fun `datasource interfaces are interfaces`() {
        Konture.classes()
            .that().resideInAPackage("..domain.service.datasource..")
            .and().haveNameEndingWith("DataSource")
            .should().beInterfaces()
            .check()
    }

    /**
     * ViewModel クラスが domain, data レイヤーに存在しないことを保証する。
     */
    @Test
    fun `viewmodels do not reside in domain or data layers`() {
        Konture.classes()
            .allowEmpty()
            .that().haveNameEndingWith("ViewModel")
            .and().resideInAPackage("..domain..", "..data..")
            .should().beInterfaces()
            .check()
    }

    /**
     * data層が feature層、app層に依存しないことを保証する。
     */
    @Test
    fun `data layer does not depend on feature and app layers`() {
        Konture.modules()
            .that().haveNamePath { it.startsWith(":data") }
            .should().notDependOnModule { it.startsWith(":feature") || it.startsWith(":app") }
            .check()
    }
}
