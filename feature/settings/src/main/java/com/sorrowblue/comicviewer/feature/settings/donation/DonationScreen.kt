package com.sorrowblue.comicviewer.feature.settings.donation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.info.BuildConfig
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

sealed interface Product {

    val productId: String

    companion object {
        fun productIdOf(productId: String): Product? =
            kotlin.runCatching { ConsumableProduct.entries.firstOrNull { it.productId == productId } }
                .getOrNull()
                ?: kotlin.runCatching { NonConsumableProduct.entries.firstOrNull { it.productId == productId } }
                    .getOrNull()

        val entries = ConsumableProduct.entries + NonConsumableProduct.entries +
            if (BuildConfig.DEBUG) {
                TestProduct.entries
            } else {
                emptyList()
            }
    }
}

enum class ConsumableProduct(override val productId: String) : Product {
    Coffee("consumable_coffee"),
    Cake("consumable_cake"),
    ;
}

enum class TestProduct(override val productId: String) : Product {
    Purchased("android.test.purchased"),
    Canceled("android.test.canceled"),
    Refunded("android.test.refunded"),
    ItemUnavailable("android.test.item_unavailable"),
    ;
}

enum class NonConsumableProduct(override val productId: String) : Product {
    EndlessBookshelf("non_consumable_bookshelf_infinite"),
    ;
}

data class InAppItem(
    val product: Product,
    val name: String,
    val description: String,
    val formattedPrice: String,
)

data class DonationScreenUiState(
    val items: List<InAppItem> = emptyList(),
)

@Destination
@Composable
internal fun DonationScreen(
    contentPadding: PaddingValues,
    navigator: SettingsDetailNavigator,
) {
    DonationScreen(
        contentPadding = contentPadding,
        onBackClick = navigator::navigateBack
    )
}

@Composable
internal fun DonationScreen(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    state: DonationScreenState = rememberDonationScreenState(),
) {
    DonationScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onItemClick = state::onItemClick,
        contentPadding = contentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DonationScreen(
    uiState: DonationScreenUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onItemClick: (InAppItem) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    SettingsDetailPane(
        title = { Text(text = "Donate") },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        onBackClick = onBackClick,
        contentPadding = contentPadding,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            uiState.items.forEach {
                OutlinedCard {
                    ListItem(
                        headlineContent = { Text(text = it.name) },
                        supportingContent = { Text(text = it.description) },
                        trailingContent = { Text(text = it.formattedPrice) },
                        modifier = Modifier.clickable {
                            onItemClick(it)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(ComicTheme.dimension.padding))
            }
        }
    }
}
