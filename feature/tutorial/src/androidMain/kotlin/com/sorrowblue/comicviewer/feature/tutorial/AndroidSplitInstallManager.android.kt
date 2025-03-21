package com.sorrowblue.comicviewer.feature.tutorial

import android.content.Context
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.errorCode
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.status
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.feature.tutorial.section.DocumentSheetUiState
import logcat.logcat
import org.koin.core.annotation.Factory

@Factory
internal actual class AndroidSplitInstallManager(
    private val context: Context,
    private val splitInstallManager: SplitInstallManager = SplitInstallManagerFactory.create(context),
) : SplitInstallManager by splitInstallManager {

    actual val installedModulesSet: Set<String>
        get() = splitInstallManager.installedModules

    private var listener: ((DocumentSheetUiState) -> Unit)? = null

    private val stateUpdatedListener = SplitInstallStateUpdatedListener {
        if (installedModulesSet.contains(DocumentModule)) {
            logcat("AndroidSplitInstallManager") { "SplitInstallStateUpdatedListener status=${it.status}" }
            val uiState = when (it.status) {
                SplitInstallSessionStatus.CANCELED -> DocumentSheetUiState.CANCELED
                SplitInstallSessionStatus.CANCELING -> DocumentSheetUiState.CANCELING
                SplitInstallSessionStatus.DOWNLOADED -> DocumentSheetUiState.DOWNLOADED
                SplitInstallSessionStatus.DOWNLOADING -> DocumentSheetUiState.DOWNLOADING(
                    (it.bytesDownloaded.toDouble() / it.totalBytesToDownload).toFloat()
                )

                SplitInstallSessionStatus.FAILED -> DocumentSheetUiState.FAILED(it.err)
                SplitInstallSessionStatus.INSTALLED -> DocumentSheetUiState.INSTALLED
                SplitInstallSessionStatus.INSTALLING -> DocumentSheetUiState.INSTALLING
                SplitInstallSessionStatus.PENDING -> DocumentSheetUiState.Pending
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> DocumentSheetUiState.RequestsUserConfirmation
                SplitInstallSessionStatus.UNKNOWN -> DocumentSheetUiState.NONE
                else -> DocumentSheetUiState.NONE
            }
            listener?.invoke(uiState)
        }
    }

    actual fun setStateListener(function: (DocumentSheetUiState) -> Unit) {
        listener = function
    }

    actual fun registerListener() {
        splitInstallManager.registerListener(stateUpdatedListener)
    }

    actual fun unregisterListener() {
        splitInstallManager.unregisterListener(stateUpdatedListener)
    }

    actual suspend fun requestInstall(module: String) {
        splitInstallManager.requestInstall(listOf(DocumentModule))
    }

    private val SplitInstallSessionState.err: SplitInstallError
        get() {
            return when (errorCode) {

                SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED -> {
                    // 現在ダウンロード中の既存のリクエストが少なくとも 1 つ存在するため、このリクエストは拒否されました。
                    // 現在のアプリで実行されているセッションが多すぎます。最初に既存のセッションを解決する必要があります。
                    SplitInstallError.Retryable("他の機能をダウンロード中です。しばらくしてから再試行してください。")
                }

                SplitInstallErrorCode.MODULE_UNAVAILABLE -> {
                    // Google Play は、現在インストールされているアプリのバージョン、デバイス、ユーザーの Google Play アカウントに基づいて、リクエストされたモジュールを見つけることができませんでした。
                    // ユーザーがモジュールに対するアクセス権限を持っていない場合は、ユーザーに通知します。
                    SplitInstallError.App("エラーが発生しました。エラーコード=$errorCode")
                }

                SplitInstallErrorCode.INVALID_REQUEST -> {
                    // Google Play はリクエストを受信しましたが、リクエストが無効です。
                    // リクエストに含まれている情報が完全で正確であることを確認します。
                    SplitInstallError.App("エラーが発生しました。エラーコード=$errorCode")
                }

                SplitInstallErrorCode.SESSION_NOT_FOUND -> {
                    // 指定されたセッション ID のセッションが見つかりませんでした。
                    // セッション ID でリクエストの状態をモニタリングする場合は、セッション ID が正しいことを確認します。
                    SplitInstallError.App("エラーが発生しました。エラーコード=$errorCode")
                }

                SplitInstallErrorCode.API_NOT_AVAILABLE -> {
                    // 現在のデバイスでは、Play Feature Delivery Library がサポートされていません。そのため、このデバイスはオンデマンドで機能をダウンロードしてインストールすることができません。
                    // Android 4.4（API レベル 20）以前を搭載しているデバイスの場合、dist:fusing マニフェスト プロパティを使用して、インストール時に機能モジュールを組み込む必要があります。詳しくは、機能モジュールのマニフェストをご覧ください。
                    // 利用不可
                    SplitInstallError.NotSupport("エラーが発生しました。この端末のAndroidバージョンはサポートしていません。")
                }

                SplitInstallErrorCode.NETWORK_ERROR -> {
                    // ネットワーク エラーのため、リクエストが失敗しました。
                    // ネットワーク接続を確立するか、別のネットワークに変更することをユーザーに求めます。
                    // 再試行
                    SplitInstallError.Retryable("ネットワークエラーが発生しました。有効なネットワークに接続しているか確認してください。")
                }

                SplitInstallErrorCode.ACCESS_DENIED -> {
                    // 権限が不十分なため、アプリがリクエストを登録できませんでした。
                    // 通常、このエラーはアプリがバックグラウンドで動作しているときに発生します。アプリがフォアグラウンドに戻ってから、リクエストを試行するようにします。
                    SplitInstallError.Retryable("ダウンロードに失敗しました。しばらくしてから再試行してください。")
                }

                SplitInstallErrorCode.INCOMPATIBLE_WITH_EXISTING_SESSION -> {
                    // リクエスト内に、すでにリクエスト済みだがまだインストールされていないモジュールが 1 つまたは複数含まれています。
                    // リクエスト済みのモジュールを含まない新しいリクエストを作成するか、現在リクエスト中のすべてのモジュールのインストールが完了するのを待ってからリクエストを再試行します。
                    // なお、インストール済みのモジュールをリクエストしてもエラーにはなりません。
                    // 再試行
                    SplitInstallError.Retryable("他の機能をインストール中です。しばらくしてから再試行してください。")
                }

                SplitInstallErrorCode.INSUFFICIENT_STORAGE -> {
                    // 機能モジュールをインストールするにはデバイスの空き容量が不足しています。
                    // この機能をインストールするには空き容量が不足していることをユーザーに通知します。
                    SplitInstallError.Retryable("このデバイスの空き容量が不足しているためダウンロードできませんでした。")
                }

                SplitInstallErrorCode.SPLITCOMPAT_COPY_ERROR,
                SplitInstallErrorCode.SPLITCOMPAT_EMULATION_ERROR,
                SplitInstallErrorCode.SPLITCOMPAT_VERIFICATION_ERROR,
                -> {
                    // SplitCompat が機能モジュールを読み込めませんでした。
                    // これらのエラーは、次回のアプリの再起動後に自動的に解決されます。
                    SplitInstallError.Retryable("〇〇機能を読み込めませんでした。アプリを再起動してください。")
                }

                SplitInstallErrorCode.PLAY_STORE_NOT_FOUND -> {
                    // Play ストア アプリがデバイスにインストールされていません。
                    // この機能をダウンロードするには Play ストア アプリが必要であることをユーザーに伝えます。
                    SplitInstallError.NotSupport("GooglePlayストアが見つかりませんでした。〇〇機能はダウンロードできません。")
                }

                SplitInstallErrorCode.APP_NOT_OWNED -> {
                    // このアプリは Google Play によってインストールされたものではないため、この機能をダウンロードできません。このエラーは、遅延インストールの場合にのみ発生する可能性があります。
                    // Google Play からアプリを入手するには startInstall() を使用します。これにより、必要なユーザーの同意を得ることができます。
                    SplitInstallError.NotSupport("このアプリは正規のアプリではありません。GooglePlayストアからインストールしてください。")
                }

                SplitInstallErrorCode.INTERNAL_ERROR -> {
                    // Play ストアで内部エラーが発生しました。
                    // リクエストを再試行します。
                    SplitInstallError.Retryable("エラーが発生しました。しばらくしてから再試行してください。")
                }

                SplitInstallErrorCode.NO_ERROR -> {
                    SplitInstallError.NotSupport("エラーなし")
                }

                else -> {
                    SplitInstallError.NotSupport("サービスなし")
                }
            }
        }
}
