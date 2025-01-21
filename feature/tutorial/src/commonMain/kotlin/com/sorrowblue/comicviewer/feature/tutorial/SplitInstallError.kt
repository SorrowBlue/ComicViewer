package com.sorrowblue.comicviewer.feature.tutorial

internal sealed interface SplitInstallError {
    val message: String

    data class NotSupport(override val message: String) : SplitInstallError
    data class App(override val message: String) : SplitInstallError

    data class Retryable(override val message: String) : SplitInstallError
    // 再試行可能
}
