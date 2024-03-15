package com.sorrowblue.comicviewer.app

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.app.navigation.RootNavGraph
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import logcat.logcat

@AndroidEntryPoint
internal class MainActivity : AppCompatActivity() {

    private val viewModel: ComicViewerAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            )
            super.onCreate(savedInstanceState)
            setKeepOnScreenCondition(viewModel::shouldKeepSplash)
            setOnExitAnimationListener(SplashScreenViewProvider::startSlideUpAnime)
        }

        setContent {
            val navController = rememberNavController()
            ComicViewerApp(
                onTutorial = {
                    navController.navigate(TutorialNavGraph) {
                        popUpTo(RootNavGraph) {
                            inclusive = true
                        }
                    }
                },
                navController = navController
            )
        }


        val purchasesUpdatedListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                // To be implemented in a later section.
            }

        var billingClient = BillingClient.newBuilder(application.applicationContext)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    logcat { "接続OK" }
                    val productList = listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("donation_caffee")
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                    val params = QueryProductDetailsParams.newBuilder()
                    params.setProductList(productList)

                    // leverage queryProductDetails Kotlin extension function
                    lifecycleScope.launch {
                    val productDetailsResult = withContext(Dispatchers.IO) {
                        billingClient.queryProductDetails(params.build())
                    }
                        logcat { productDetailsResult.billingResult.toString() }
                        productDetailsResult.productDetailsList?.forEach {
                            logcat { it.name }
                            logcat { it.description }
                        }
                    }
                }
            }
            override fun onBillingServiceDisconnected() {
                logcat { "onBillingServiceDisconnected" }
            }
        })

    }
}

private fun SplashScreenViewProvider.startSlideUpAnime() {
    kotlin.runCatching {
        val slideUp = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -iconView.height * 2f)
        slideUp.interpolator = AnticipateInterpolator()
        slideUp.doOnEnd { remove() }
        slideUp.duration =
            if (iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis < 0) 300 else iconAnimationDurationMillis - System.currentTimeMillis() + iconAnimationStartMillis
        slideUp.start()
    }.onFailure { remove() }
}
