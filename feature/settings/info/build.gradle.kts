import com.android.build.api.variant.VariantOutput
import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.comicviewer.kotlinMultiplatform.feature)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.settings.common)
                implementation(libs.kotlinx.datetime)
                implementation(libs.aboutlibraries.compose) }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.browser)
            }
        }
    }
}

android {
    namespace = "com.sorrowblue.comicviewer.feature.settings.info"
    resourcePrefix("settings_info")
}

// Provider を取得
val gitTagProvider: Provider<String> = providers.of(GitTagValueSource::class) {}

buildkonfig {
    packageName = "om.sorrowblue.comicviewer.feature.settings"
    defaultConfigs {
        buildConfigField(type = FieldSpec.Type.STRING, name = "VERSION_NAME", value = gitTagProvider.orElse("unknown").get(), const = true)
        buildConfigField(FieldSpec.Type.LONG, "TIMESTAMP", System.currentTimeMillis().toString(), const = true)
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
