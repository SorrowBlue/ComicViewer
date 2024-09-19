package com.sorrowblue.comicviewer.feature.settings.donation

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryResult
import com.android.billingclient.api.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchaseHistory
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class BillingClientWrapper(
    context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) :
    PurchasesUpdatedListener {

    var onCancell: (() -> Unit)? = null
    var onError: ((BillingResult) -> Unit)? = null

    // Initialize the BillingClient.
    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    /** Google Playへの接続を確立します。 */
    fun startBillingConnection(onConnected: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    logcat { "Billing response OK" }
                    // BillingClient の準備ができました。製品の購入をクエリできるようになりました。
                    onConnected()
                } else {
                    logcat(LogPriority.ERROR) { billingResult.debugMessage }
                    retryBillingServiceConnection()
                }
            }

            override fun onBillingServiceDisconnected() {
                logcat(LogPriority.ERROR) { "GBPL Service disconnected" }
                retryBillingServiceConnection()
            }
        })
    }

    /** 請求接続の再試行ロジック。これは単純な最大再試行パターンです */
    private fun retryBillingServiceConnection() {
        val maxTries = 3
        var tries = 1
        var isConnectionEstablished = false
        do {
            try {
                billingClient.startConnection(object : BillingClientStateListener {
                    override fun onBillingServiceDisconnected() {
//                        TODO("Not yet implemented")
                    }

                    override fun onBillingSetupFinished(billingResult: BillingResult) {
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            isConnectionEstablished = true
                            logcat { "Billing connection retry succeeded." }
                        } else {
                            logcat(
                                LogPriority.ERROR
                            ) { "Billing connection retry failed: ${billingResult.debugMessage}" }
                        }
                    }
                })
            } catch (e: Exception) {
                logcat(LogPriority.ERROR) { e.asLog() }
                tries++
            }
        } while (tries <= maxTries && !isConnectionEstablished)
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        logcat { "result=$result, purchases=$purchases" }
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                logcat { "onPurchasesUpdated result=$result, purchases=$purchases, json=${purchase.originalJson}" }
                purchase.products.forEach {
                    Product.productIdOf(it)?.let {
                        runBlocking {
                            when (it) {
                                is ConsumableProduct -> consume(purchase)
                                is NonConsumableProduct -> acknowledgePurchase(purchase.purchaseToken)
                                is TestProduct -> consume(purchase)
                            }
                        }
                    }
                }
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            onCancell?.invoke()
        } else {
            onError?.invoke(result)
            // Handle any other error codes.
        }
    }

    suspend fun queryProductDetail(productId: String): ProductDetails? {
        val retryDelayMs = 2000L
        val retryFactor = 2
        val maxTries = 3

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()

        val productDetailsResult = billingClient.queryProductDetails(queryProductDetailsParams)

        val billingResult = productDetailsResult.billingResult

        val playBillingResponseCode = billingResult.responseCode
        when (playBillingResponseCode) {
            BillingClient.BillingResponseCode.OK -> {
                logcat(LogPriority.INFO) { "queryProductDetails was successful" }
                return productDetailsResult.productDetailsList?.firstOrNull()
            }

            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                // これは、古い Play キャッシュに関連している可能性があります。
                // 購入を再度クエリします。
                logcat { "queryProductDetails failed with ITEM_NOT_OWNED" }
                val productDetailsResult1 =
                    billingClient.queryProductDetails(queryProductDetailsParams)
                when (productDetailsResult1.billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        return productDetailsResult1.productDetailsList?.firstOrNull()
                    }
                }
            }

            in setOf(
                BillingClient.BillingResponseCode.ERROR,
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            ),
            -> {
                logcat {
                    "Acknowledgement failed, but can be retried -- Response Code: ${billingResult.responseCode} -- Debug Message: ${billingResult.debugMessage}"
                }
                return runBlocking {
                    exponentialRetry(
                        maxTries = maxTries,
                        initialDelay = retryDelayMs,
                        retryFactor = retryFactor
                    ) { queryProductDetail(productId) }
                }
            }

            in setOf(
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
                BillingClient.BillingResponseCode.DEVELOPER_ERROR,
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            ),
            -> {
                logcat(
                    LogPriority.ERROR
                ) {
                    "Acknowledgement failed and cannot be retried -- Response Code: ${billingResult.responseCode} -- Debug Message: ${billingResult.debugMessage}"
                }
                throw BillingException("Failed to acknowledge the purchase!")
            }
        }
        return null
    }

    suspend fun queryProductDetails(productIds: List<String>): List<ProductDetails> {
        val retryDelayMs = 2000L
        val retryFactor = 2
        val maxTries = 3

        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    productIds.map {
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(it)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    }
                )
                .build()

        val productDetailsResult = billingClient.queryProductDetails(queryProductDetailsParams)

        val billingResult = productDetailsResult.billingResult

        val playBillingResponseCode = billingResult.responseCode
        when (playBillingResponseCode) {
            BillingClient.BillingResponseCode.OK -> {
                logcat(
                    LogPriority.INFO
                ) { "queryProductDetails was successful, ${productDetailsResult.productDetailsList}" }
                return productDetailsResult.productDetailsList.orEmpty()
            }

            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                // これは、古い Play キャッシュに関連している可能性があります。
                // 購入を再度クエリします。
                logcat { "queryProductDetails failed with ITEM_NOT_OWNED" }
                val productDetailsResult1 =
                    billingClient.queryProductDetails(queryProductDetailsParams)
                when (productDetailsResult1.billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        return productDetailsResult1.productDetailsList.orEmpty()
                    }
                }
            }

            in setOf(
                BillingClient.BillingResponseCode.ERROR,
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            ),
            -> {
                logcat {
                    "Acknowledgement failed, but can be retried -- Response Code: ${billingResult.responseCode} -- Debug Message: ${billingResult.debugMessage}"
                }
                return runBlocking {
                    exponentialRetry(
                        maxTries = maxTries,
                        initialDelay = retryDelayMs,
                        retryFactor = retryFactor
                    ) { queryProductDetails(productIds) }.orEmpty()
                }
            }

            in setOf(
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
                BillingClient.BillingResponseCode.DEVELOPER_ERROR,
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            ),
            -> {
                logcat(
                    LogPriority.ERROR
                ) {
                    "Acknowledgement failed and cannot be retried -- Response Code: ${billingResult.responseCode} -- Debug Message: ${billingResult.debugMessage}"
                }
                throw BillingException("Failed to acknowledge the purchase!")
            }
        }
        return emptyList()
    }

    fun launchBillingFlow(activity: Activity, productDetails: ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private suspend fun acknowledge(purchaseToken: String): BillingResult {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        return billingClient.acknowledgePurchase(params)
    }

    suspend fun consume(purchase: Purchase): ConsumeResult {
        return withContext(dispatcher) {
            val consumeParams =
                ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
            billingClient.consumePurchase(consumeParams)
        }
    }

    suspend fun queryPurchases(): PurchasesResult {
        val purchasesParams =
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        return billingClient.queryPurchasesAsync(purchasesParams)
    }

    suspend fun queryPurchaseHistory(): PurchaseHistoryResult {
        val purchasesParams =
            QueryPurchaseHistoryParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        return billingClient.queryPurchaseHistory(purchasesParams)
    }

    suspend fun acknowledgePurchase(purchaseToken: String) {
        val retryDelayMs = 2000L
        val retryFactor = 2
        val maxTries = 3

        val acknowledgePurchaseResult = withContext(dispatcher) {
            acknowledge(purchaseToken)
        }

        val playBillingResponseCode = acknowledgePurchaseResult.responseCode
        when (playBillingResponseCode) {
            BillingClient.BillingResponseCode.OK -> {
                logcat(LogPriority.INFO) { "Acknowledgement was successful" }
            }

            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                // これは、古い Play キャッシュに関連している可能性があります。
                // 購入を再度クエリします。
                logcat { "Acknowledgement failed with ITEM_NOT_OWNED" }
                val purchaseResult = billingClient.queryPurchasesAsync(
                    QueryPurchasesParams.newBuilder()
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
                when (purchaseResult.billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        purchaseResult.purchasesList.forEach { purchase ->
                            acknowledge(purchase.purchaseToken)
                        }
                    }
                }
            }

            in setOf(
                BillingClient.BillingResponseCode.ERROR,
                BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
                BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            ),
            -> {
                logcat {
                    "Acknowledgement failed, but can be retried -- Response Code: ${acknowledgePurchaseResult.responseCode} -- Debug Message: ${acknowledgePurchaseResult.debugMessage}"
                }
                runBlocking {
                    exponentialRetry(
                        maxTries = maxTries,
                        initialDelay = retryDelayMs,
                        retryFactor = retryFactor
                    ) { acknowledge(purchaseToken) }
                }
            }

            in setOf(
                BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
                BillingClient.BillingResponseCode.DEVELOPER_ERROR,
                BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            ),
            -> {
                logcat(
                    LogPriority.ERROR
                ) {
                    "Acknowledgement failed and cannot be retried -- Response Code: ${acknowledgePurchaseResult.responseCode} -- Debug Message: ${acknowledgePurchaseResult.debugMessage}"
                }
                throw BillingException("Failed to acknowledge the purchase!")
            }
        }
    }

    private suspend fun <T> exponentialRetry(
        maxTries: Int = Int.MAX_VALUE,
        initialDelay: Long = Long.MAX_VALUE,
        retryFactor: Int = Int.MAX_VALUE,
        block: suspend () -> T,
    ): T? {
        var currentDelay = initialDelay
        var retryAttempt = 1
        do {
            runCatching {
                delay(currentDelay)
                block()
            }
                .onSuccess {
                    logcat { "Retry succeeded" }
                    return@onSuccess
                }
                .onFailure { throwable ->
                    logcat(
                        LogPriority.ERROR
                    ) { "Retry Failed -- Cause: ${throwable.cause} -- Message: ${throwable.message}" }
                }
            currentDelay *= retryFactor
            retryAttempt++
        } while (retryAttempt < maxTries)

        return block() // last attempt
    }
}

class BillingException(message: String?) : RuntimeException(message)
