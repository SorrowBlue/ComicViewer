package com.sorrowblue.comicviewer.tutorial

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.ktx.bytesDownloaded
import com.google.android.play.core.ktx.errorCode
import com.google.android.play.core.ktx.moduleNames
import com.google.android.play.core.ktx.requestInstall
import com.google.android.play.core.ktx.status
import com.google.android.play.core.ktx.totalBytesToDownload
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.sorrowblue.comicviewer.framework.ui.flow.launchInWithLifecycle
import com.sorrowblue.comicviewer.framework.ui.fragment.FrameworkFragment
import com.sorrowblue.comicviewer.framework.ui.fragment.makeSnackbar
import com.sorrowblue.comicviewer.tutorial.databinding.TutorialFragmentDocumentBinding
import com.sorrowblue.jetpack.binding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TutorialDocumentFragment : FrameworkFragment(R.layout.tutorial_fragment_document) {

    private val binding: TutorialFragmentDocumentBinding by viewBinding()
    private val viewModel: TutorialDocumentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
        binding.install.setOnClickListener {
            binding.install.isEnabled = false
            viewModel.requestInstall()
        }
        viewModel.state.onEach {
            when (it?.status) {
                SplitInstallSessionStatus.PENDING -> {
                    binding.install.text = "PENDING"
                    binding.install.isEnabled = false
                    binding.progress.isVisible = true
                    binding.progress.isIndeterminate = true
                }

                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    binding.install.text = "REQUIRES_USER_CONFIRMATION"
                    binding.install.isEnabled = false
                    binding.progress.isVisible = true
                    binding.progress.isIndeterminate = true
                    viewModel.splitInstallManager.startConfirmationDialogForResult(
                        it,
                        requireActivity(),
                        200
                    )
                }

                SplitInstallSessionStatus.DOWNLOADING -> {
                    binding.install.text = "DOWNLOADING"
                    binding.install.isEnabled = false
                    binding.progress.isVisible = true
                    binding.progress.isIndeterminate = false
                    binding.progress.max = it.totalBytesToDownload.toInt()
                    binding.progress.progress = it.bytesDownloaded.toInt()
                }

                SplitInstallSessionStatus.DOWNLOADED -> {
                    binding.install.text = "DOWNLOADED"
                    binding.install.isEnabled = false
                    binding.progress.isVisible = true
                    binding.progress.isIndeterminate = true
                }

                SplitInstallSessionStatus.INSTALLING -> {
                    binding.install.text = "INSTALLING"
                    binding.install.isEnabled = false
                    binding.progress.isVisible = true
                    binding.progress.isIndeterminate = true
                }

                SplitInstallSessionStatus.INSTALLED -> {
                    binding.install.text = "INSTALLED"
                    binding.install.isEnabled = false
                    binding.progress.isVisible = false
                }

                SplitInstallSessionStatus.FAILED -> {
                    makeSnackbar(it.err.message).show()
                    when (it.err) {
                        is SplitInstallError.App -> {
                            binding.install.text = "DOWNLOAD"
                            binding.install.isEnabled = true
                            binding.progress.isVisible = false
                        }

                        is SplitInstallError.NotSupport -> {
                            binding.install.text = "NOT SUPPORT"
                            binding.install.isEnabled = false
                            binding.progress.isVisible = false
                        }

                        is SplitInstallError.Retryable -> {
                            binding.install.text = "DOWNLOAD"
                            binding.install.isEnabled = true
                            binding.progress.isVisible = false
                        }
                    }
                }

                SplitInstallSessionStatus.CANCELING -> {
                    binding.install.text = "CANCELING"
                    binding.install.isEnabled = false
                    binding.progress.isVisible = true
                    binding.progress.isIndeterminate = true

                }

                SplitInstallSessionStatus.CANCELED -> {
                    makeSnackbar("キャンセルされました。").show()
                    binding.install.text = "DOWNLOAD"
                    binding.install.isVisible = true
                    binding.install.isEnabled = true
                    binding.progress.isVisible = false
                }

                else -> {
                    binding.install.text = "DOWNLOAD"
                    binding.install.isEnabled = true
                    binding.progress.isVisible = false
                }
            }
        }.launchInWithLifecycle()
    }
}

val SplitInstallSessionState.err: SplitInstallError
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
                "A network error has occurred. Please make sure you are connected to a valid network."
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
            SplitInstallErrorCode.SPLITCOMPAT_VERIFICATION_ERROR -> {
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

            SplitInstallErrorCode.SERVICE_DIED -> {
                SplitInstallError.NotSupport("サービスなし")
            }

            else -> throw RuntimeException("想定外")
        }
    }

sealed interface SplitInstallError {
    val message: String

    data class NotSupport(override val message: String) : SplitInstallError
    data class App(override val message: String) : SplitInstallError

    data class Retryable(override val message: String) : SplitInstallError
    // 再試行可能
}

@HiltViewModel
internal class TutorialDocumentViewModel @Inject constructor(val splitInstallManager: SplitInstallManager) :
    ViewModel(), DefaultLifecycleObserver, SplitInstallStateUpdatedListener {

    fun requestInstall() {
        viewModelScope.launch {
            splitInstallManager.requestInstall(listOf("document"))
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        splitInstallManager.registerListener(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        splitInstallManager.unregisterListener(this)
    }

    val state = MutableStateFlow<SplitInstallSessionState?>(
        if (splitInstallManager.installedModules.contains("document"))
            SplitInstallSessionState.create(
                0,
                SplitInstallSessionStatus.INSTALLED,
                SplitInstallErrorCode.NO_ERROR,
                0,
                0,
                emptyList(),
                emptyList()
            )
        else
            null
    )

    override fun onStateUpdate(state: SplitInstallSessionState) {
        if (!state.moduleNames.contains("document")) return
        this.state.value = state
    }
}