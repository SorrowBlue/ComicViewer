import com.android.build.api.variant.VariantOutput
import com.sorrowblue.comicviewer.ComicBuildType
import java.io.ByteArrayOutputStream
import kotlin.jvm.java
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.application)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.compose)
    alias(libs.plugins.comicviewer.kotlinMultiplatform.koin)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.sorrowblue.comicviewer.app")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.framework.designsystem)
            implementation(projects.framework.ui)
            implementation(projects.data.di)
            implementation(projects.domain.usecase)
            implementation(projects.feature.authentication)
            implementation(projects.feature.bookshelf)
            implementation(projects.feature.bookshelf.info)
            implementation(projects.feature.book)
            implementation(projects.feature.readlater)
            implementation(projects.feature.collection)
            implementation(projects.feature.search)
            implementation(projects.feature.tutorial)
            implementation(projects.feature.folder)
            implementation(projects.feature.settings)
            implementation(projects.feature.settings.info)
            implementation(projects.feature.history)

            // Material3
            implementation(compose.material3)
            implementation(compose.material3AdaptiveNavigationSuite)
            implementation(libs.compose.multiplatform.material3.adaptive)
            // Di
            implementation(libs.koin.composeViewModel)

            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(projects.framework.notification)

            implementation(libs.androidx.core.splashscreen)
            implementation(libs.koin.androidxCompose)
            implementation(libs.koin.androidxStartup)
            implementation(libs.koin.androidxWorkmanager)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.google.android.play.feature.delivery.ktx)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.resources {
    customDirectory(
        sourceSetName = "androidMain",
        directoryProvider = layout.buildDirectory.dir("generated/aboutLibrariesExt/androidMain/composeResources")
    )
    customDirectory(
        sourceSetName = "desktopMain",
        directoryProvider = layout.buildDirectory.dir("generated/aboutLibrariesExt/desktopMain/composeResources")
    )
    customDirectory(
        sourceSetName = "iosMain",
        directoryProvider = layout.buildDirectory.dir("generated/aboutLibrariesExt/iosMain/composeResources")
    )
}

android {
    namespace = "com.sorrowblue.comicviewer.app"
    defaultConfig {
        applicationId = "com.sorrowblue.comicviewer"
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
    }
    androidResources {
        @Suppress("UnstableApiUsage")
        generateLocaleConfig = true
    }

    buildTypes {
        release {
            applicationIdSuffix = ComicBuildType.RELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.RELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.RELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
            ndk.debugSymbolLevel = "SYMBOL_TABLE"
        }
        getByName(ComicBuildType.PRERELEASE.display) {
            initWith(getByName(ComicBuildType.RELEASE.display))
            applicationIdSuffix = ComicBuildType.PRERELEASE.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.PRERELEASE.isMinifyEnabled
            isShrinkResources = ComicBuildType.PRERELEASE.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        getByName(ComicBuildType.INTERNAL.display) {
            initWith(getByName(ComicBuildType.RELEASE.display))
            applicationIdSuffix = ComicBuildType.INTERNAL.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.INTERNAL.isMinifyEnabled
            isShrinkResources = ComicBuildType.INTERNAL.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
        debug {
            applicationIdSuffix = ComicBuildType.DEBUG.applicationIdSuffix
            isMinifyEnabled = ComicBuildType.DEBUG.isMinifyEnabled
            isShrinkResources = ComicBuildType.DEBUG.isShrinkResources
            signingConfig = signingConfigs.findByName(name)
        }
    }

    dynamicFeatures += setOf(projects.data.reader.document.path)

    buildFeatures.buildConfig = true

    packaging {
        resources.excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }

    this.sourceSets {
        this.getByName("main").res.srcDirs("src/commonMain/composeResources")
    }

    lint {
        abortOnError = true
    }
}

compose.desktop {
    application {
        mainClass = "com.sorrowblue.comicviewer.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.sorrowblue.comicviewer.app"
            packageVersion = "1.0.0"
        }
        jvmArgs("-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }
}
// Provider を取得
val gitTagProvider: Provider<String> = providers.of(GitTagValueSource::class) {}

// --- AGP Variant API を使用 ---
androidComponents {
    // onVariants は設定フェーズで実行されるが、中の処理は遅延される
    onVariants(selector().all()) { variant -> // release ビルドのみ対象
        // すべてのバリアントを対象にする場合は selector().all()

        // Variant の Output (通常は1つ) にアクセス
        variant.outputs.forEach { output: VariantOutput -> // 型を明示的に指定

            // versionName にGitタグを設定
            // orElse でタグが見つからない場合のフォールバックを設定
            // map でタグ名を加工 (例: 'v' を削除)
            val vn = gitTagProvider.orElse("0.0.0").get()
            logger.lifecycle("${variant.name} versionName=$vn")
            output.versionName.set(vn)
        }

    }
}

// パラメータは不要だが、インターフェースとして定義が必要
interface GitTagParameters : ValueSourceParameters

// Gitコマンドを実行して最新タグを取得するValueSource
abstract class GitTagValueSource @Inject constructor(
    private val execOperations: ExecOperations
) : ValueSource<String, GitTagParameters> {

    override fun obtain(): String {
        return try {
            // 標準出力をキャプチャするためのByteArrayOutputStream
            val stdout = ByteArrayOutputStream()
            // git describe コマンドを実行
            val result = execOperations.exec {
                // commandLine("git", "tag", "--sort=-creatordate") // もし作成日時順の最新タグが良い場合
                commandLine("git", "describe", "--tags", "--abbrev=1")
                standardOutput = stdout
                // エラーが発生してもGradleビルドを止めないようにし、戻り値で判断
                isIgnoreExitValue = true
                // エラー出力は捨てる (必要ならキャプチャも可能)
                errorOutput = ByteArrayOutputStream()
            }

            if (result.exitValue == 0) {
                // 成功したら標準出力をトリムして返す
                stdout.toString().trim()
            } else {
                // gitコマンド失敗時 (タグがない、gitリポジトリでない等)
                println("Warning: Could not get git tag. (Exit code: ${result.exitValue})")
                "UNKNOWN" // または適切なデフォルト値
            }
        } catch (e: Exception) {
            // その他の予期せぬエラー
            println("Warning: Failed to execute git command: ${e.message}")
            "UNKNOWN" // または適切なデフォルト値
        }
    }
}
