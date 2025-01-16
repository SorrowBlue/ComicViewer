package com.sorrowblue.comicviewer.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.app.component.ComicViewerScaffold
import kotlinx.serialization.Serializable

@Serializable
data object First

@Serializable
data object Second

@Composable
internal fun ComicViewerApp(state: ComicViewerAppState = rememberComicViewerAppState()) {
    ComicViewerScaffold(
        uiState = state.uiState,
        onTabSelect = { tab -> state.onTabSelect(tab) },
    ) {
        NavHost(state.navController, startDestination = First) {
            composable<First> {
                Scaffold {
                    Column(
                        Modifier.fillMaxSize().padding(it),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(First::class.qualifiedName.orEmpty())
                        Button(onClick = { state.navController.navigate(Second) }) {
                            Text("To second")
                        }
                    }
                }
            }
            composable<Second> {
                Scaffold {
                    Column(
                        Modifier.fillMaxSize().padding(it),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(Second::class.qualifiedName.orEmpty())
                        Button(onClick = { state.navController.popBackStack() }) {
                            Text("Back")
                        }
                    }
                }
            }
        }
    }
}
