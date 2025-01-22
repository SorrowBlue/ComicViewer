package com.sorrowblue.comicviewer.feature.settings.donation

import android.app.Activity
import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.android.billingclient.api.Purchase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logcat.logcat

interface DonationScreenState {
    val snackbarHostState: SnackbarHostState
    val uiState: DonationScreenUiState

    fun onItemClick(item: InAppItem)
}

@Composable
fun rememberDonationScreenState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): DonationScreenState = remember {
    DonationScreenStateImpl(
        context = context,
        scope = scope,
        snackbarHostState = snackbarHostState
    )
}

private class DonationScreenStateImpl(
    private val context: Context,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    override val snackbarHostState: SnackbarHostState,
) : DonationScreenState {

    override var uiState by mutableStateOf(DonationScreenUiState())
        private set

    var billingClient = BillingClientWrapper(context)

    init {
        billingClient.onCancel = {
            scope.launch {
                snackbarHostState.showSnackbar("購入がキャンセルされました。")
            }
        }
        billingClient.onError = {
            scope.launch {
                snackbarHostState.showSnackbar("購入時にエラーが発生しました。${it.debugMessage}")
            }
        }
        billingClient.startBillingConnection {
            scope.launch(dispatcher) {
                val list = billingClient.queryProductDetails(
                    Product.entries.map { it.productId }
                )
                uiState =
                    uiState.copy(
                        items = list.mapNotNull { productDetails ->
                            Product.productIdOf(productDetails.productId)?.let {
                                InAppItem(
                                    it,
                                    productDetails.name,
                                    productDetails.description,
                                    productDetails.oneTimePurchaseOfferDetails?.formattedPrice.orEmpty()
                                )
                            }
                        }
                    )
                billingClient.queryPurchases().also {
                    logcat { "queryPurchases ${it.billingResult} ${it.purchasesList}" }
                }.purchasesList.forEach { purchase ->
                    logcat {
                        "${purchase.products.firstOrNull()}, purchaseState=${purchase.purchaseState}, isAcknowledged=${purchase.isAcknowledged}"
                    }
                    // 購入海、かつ未承認の場合
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        execPurchased(purchase)
                    }
                }
                billingClient.queryPurchaseHistory().also {
                    logcat { "queryPurchaseHistory ${it.billingResult}" }
                }.purchaseHistoryRecordList?.forEach {
                    logcat { it.originalJson }
                }
            }
        }
    }

    private suspend fun execPurchased(purchase: Purchase) {
        purchase.products.forEach {
            when (Product.productIdOf(it)) {
                is ConsumableProduct -> billingClient.consume(purchase)
                is NonConsumableProduct -> billingClient.acknowledgePurchase(
                    purchase.purchaseToken
                )

                is TestProduct -> billingClient.consume(purchase)
                null -> Unit
            }
        }
    }

    override fun onItemClick(item: InAppItem) {
        scope.launch {
            billingClient.queryProductDetail(item.product.productId)?.let {
                billingClient.launchBillingFlow(context as Activity, it)
            }
        }
    }
}
