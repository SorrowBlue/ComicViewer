import com.android.build.api.dsl.AndroidSourceSet
import com.android.build.api.dsl.ApplicationBuildType
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinSourceSetConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun NamedDomainObjectContainer<out AndroidSourceSet>.debug(action: AndroidSourceSet.() -> Unit) {
    action(getByName("debug"))
}

fun NamedDomainObjectContainer<ApplicationBuildType>.prerelease(action: ApplicationBuildType.() -> Unit) {
    action(getByName("prerelease"))
}

fun NamedDomainObjectContainer<ApplicationBuildType>.internal(action: ApplicationBuildType.() -> Unit) {
    action(getByName("internal"))
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
val NamedDomainObjectContainer<KotlinSourceSet>.desktopMain: NamedDomainObjectProvider<KotlinSourceSet> by KotlinSourceSetConvention

@OptIn(ExperimentalKotlinGradlePluginApi::class)
val NamedDomainObjectContainer<KotlinSourceSet>.desktopTest: NamedDomainObjectProvider<KotlinSourceSet> by KotlinSourceSetConvention

fun KotlinDependencyHandler.implementation(
    dependencyNotation: Provider<MinimalExternalModuleDependency>,
    configure: ExternalModuleDependency.() -> Unit,
) {
    implementation(dependencyNotation.get().run { "$group:$name:$version" }) {
        configure()
    }
}
