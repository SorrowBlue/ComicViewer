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
import kotlinx.coroutines.withContext
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
        billingClient.onCancell = {
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
                    ConsumableProduct.entries.map { it.productId }
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
                }.purchasesList.forEach {
                    it.isAcknowledged
                    if (it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged) {
                        withContext(dispatcher) {
                            billingClient.acknowledgePurchase(it.purchaseToken)
                        }
                    }
                }
                billingClient.queryPurchaseHistory().also {
                    logcat { "queryPurchaseHistory ${it.billingResult}" }
                }.purchaseHistoryRecordList?.forEach {
                    logcat { "${it.originalJson}" }
                }
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
