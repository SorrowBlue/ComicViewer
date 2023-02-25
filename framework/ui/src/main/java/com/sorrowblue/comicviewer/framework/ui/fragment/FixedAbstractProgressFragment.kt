package com.sorrowblue.comicviewer.framework.ui.fragment

import android.app.Activity
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.dynamicfeatures.Constants
import androidx.navigation.dynamicfeatures.DynamicExtras
import androidx.navigation.dynamicfeatures.DynamicInstallMonitor
import androidx.navigation.dynamicfeatures.fragment.ui.AbstractProgressFragment
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.ktx.moduleNames
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

abstract class FixedAbstractProgressFragment : AbstractProgressFragment {

    internal companion object {
        private const val INSTALL_REQUEST_CODE = 1
        private const val TAG = "FixedAbstractProgressFragment"
    }

    private val installViewModel: FixedInstallViewModel by viewModels()
    private val destinationId by lazy {
        requireArguments().getInt(Constants.DESTINATION_ID)
    }
    private val destinationArgs: Bundle? by lazy {
        requireArguments().getBundle(Constants.DESTINATION_ARGS)
    }
    private var navigated = false

    public constructor()

    public constructor(contentLayoutId: Int) : super(contentLayoutId)

    private val intentSenderLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_CANCELED) {
            onCancelled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            navigated = savedInstanceState.getBoolean(Constants.KEY_NAVIGATED, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (navigated) {
            findNavController().popBackStack()
            return
        }
        var monitor = installViewModel.installMonitor
        if (monitor == null) {
            Log.i(TAG, "onViewCreated: monitor is null, navigating")
            navigate()
            monitor = installViewModel.installMonitor
        }
        if (monitor != null) {
            Log.i(TAG, "onViewCreated: monitor is now not null, observing")
            monitor.status.observe(viewLifecycleOwner, StateObserver(monitor))
        }
    }
    /**
     * Navigates to an installed dynamic feature module or kicks off installation.
     */
    internal fun navigate() {
        Log.i(TAG, "navigate: ")
        val installMonitor = DynamicInstallMonitor()
        val extras = DynamicExtras(installMonitor)
         findNavController().navigate(destinationId, destinationArgs, null, extras)
        if (!installMonitor.isInstallRequired) {
            Log.i(TAG, "navigate: install not required")
            navigated = true
        } else {
            Log.i(TAG, "navigate: setting install monitor")
            installViewModel.installMonitor = installMonitor
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(Constants.KEY_NAVIGATED, navigated)
    }

    abstract fun bindModule(modules: List<String>)

    private inner class StateObserver constructor(private val monitor: DynamicInstallMonitor) :
        Observer<SplitInstallSessionState> {

        override fun onChanged(sessionState: SplitInstallSessionState?) {
            if (sessionState != null) {
                if (sessionState.hasTerminalStatus()) {
                    monitor.status.removeObserver(this)
                }
                bindModule(sessionState.moduleNames)
                when (sessionState.status()) {
                    SplitInstallSessionStatus.INSTALLED -> {
                        onInstalled()
                        navigate()
                    }
                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION ->
                        try {
                            val splitInstallManager = monitor.splitInstallManager
                            if (splitInstallManager == null) {
                                onFailed(SplitInstallErrorCode.INTERNAL_ERROR)
                                return
                            }
                            splitInstallManager.startConfirmationDialogForResult(
                                sessionState,
                                IntentSenderForResultStarter { intent,
                                                               _,
                                                               fillInIntent,
                                                               flagsMask,
                                                               flagsValues,
                                                               _,
                                                               _ ->
                                    intentSenderLauncher.launch(
                                        IntentSenderRequest.Builder(intent)
                                            .setFillInIntent(fillInIntent)
                                            .setFlags(flagsValues, flagsMask)
                                            .build()
                                    )
                                },
                                INSTALL_REQUEST_CODE
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            onFailed(SplitInstallErrorCode.INTERNAL_ERROR)
                        }
                    SplitInstallSessionStatus.CANCELED -> onCancelled()
                    SplitInstallSessionStatus.FAILED -> onFailed(sessionState.errorCode())
                    SplitInstallSessionStatus.UNKNOWN ->
                        onFailed(SplitInstallErrorCode.INTERNAL_ERROR)
                    SplitInstallSessionStatus.CANCELING,
                    SplitInstallSessionStatus.DOWNLOADED,
                    SplitInstallSessionStatus.DOWNLOADING,
                    SplitInstallSessionStatus.INSTALLING,
                    SplitInstallSessionStatus.PENDING -> {
                        onProgress(
                            sessionState.status(),
                            sessionState.bytesDownloaded(),
                            sessionState.totalBytesToDownload()
                        )
                    }
                }
            }
        }
    }
}
