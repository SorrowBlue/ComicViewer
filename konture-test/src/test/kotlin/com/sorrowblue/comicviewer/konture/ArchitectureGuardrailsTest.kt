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
            .should().onlyDependOnModules()
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
            .should().onlyDependOnModules(":domain:model", ":")
            .check()
    }

    /**
     * :domain:repository が :domain:model 以外に依存しないことを保証する。
     * repository はデータアクセスの抽象インターフェースを定義し、実装詳細を知ってはならない。
     */
    @Test
    fun `domain repository only depends on domain model`() {
        Konture.modules()
            .that().haveNamePath(":domain:repository")
            .should().onlyDependOnModules(":domain:model", ":")
            .check()
    }

    /**
     * domain:service が domain:model, domain:usecase, domain:repository 以外に依存しないことを保証する。
     * service はビジネスロジックを実装する。
     */
    @Test
    fun `domain service only depend on domain model and domain usecase and domain repository`() {
        Konture.modules()
            .that().haveNamePath(":domain:service")
            .should().onlyDependOnModules(":domain:model", ":domain:usecase", ":domain:repository")
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
     * framework層が data層、feature層、domain:serviceに依存しないことを保証する。
     * また、UI以外のframework層は domain:usecase にも依存しない。
     */
    @Test
    fun `framework layer does not depend on feature layer`() {
        Konture.modules()
            .that().haveNamePath { it.startsWith(":framework") }
            .should().notDependOnModule {
                it.startsWith(":feature")
                    || it.startsWith(":data")
                    || it.startsWith(":domain:service")
            }
            .check()

        Konture.modules()
            .that().haveNamePath { it.startsWith(":framework") && !it.startsWith(":framework:ui") }
            .should().notDependOnModule { it.startsWith(":domain:usecase") }
            .check()
    }

    /**
     * すべての Interactor クラス（ユースケースの実装）が internal であることを保証する。
     */
    @Test
    fun `interactors are internal`() {
        Konture.classes()
            .that().nameEndsWith("Interactor")
            .should().beInternal()
            .check()
    }

    /**
     * すべての RepositoryImpl クラス（リポジトリの実装）が internal であることを保証する。
     */
    @Test
    fun `repository implementations are internal`() {
        Konture.classes()
            .that().nameEndsWith("RepositoryImpl")
            .should().beInternal()
            .check()
    }

    /**
     * ドメイン層の Repository インターフェースがインターフェースであることを保証する。
     */
    @Test
    fun `repository interfaces are interfaces`() {
        Konture.classes()
            .that().inPackage("..domain.repository..")
            .and().nameEndsWith("Repository")
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
            .that().nameEndsWith("ViewModel")
            .and().inPackage("..domain..", "..data..")
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
