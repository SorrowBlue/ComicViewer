package com.sorrowblue.comicviewer.framework.ui.material3

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import logcat.AndroidLogcatLogger
import logcat.LogcatLogger

@Preview(widthDp = 400, heightDp = 400, showBackground = true)
@Composable
private fun ScrollbarPreview() {
    val state = rememberScrollState()
    Column(
        modifier = Modifier
            .drawVerticalScrollbar(state)
            .verticalScroll(state),
    ) {
        repeat(50) {
            Text(
                text = "Item ${it + 1}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Preview(widthDp = 400, heightDp = 400, showBackground = true)
@Composable
private fun LazyListScrollbarPreview() {
    LogcatLogger.install(AndroidLogcatLogger())
    val state = rememberLazyGridState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.drawVerticalScrollbar(state, 3),
        state = state,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(31) {
            OutlinedCard(
                modifier = Modifier.aspectRatio(1f),
            ) {
                Text(
                    text = "Item ${it + 1}",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Preview(widthDp = 400, showBackground = true)
@Composable
private fun HorizontalScrollbarPreview() {
    val state = rememberScrollState()
    Row(
        modifier = Modifier
            .drawHorizontalScrollbar(state)
            .horizontalScroll(state)
    ) {
        repeat(50) {
            Text(
                text = (it + 1).toString(),
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            )
        }
    }
}

@Preview(widthDp = 400, showBackground = true)
@Composable
private fun LazyListHorizontalScrollbarPreview() {
    val state = rememberLazyListState()
    LazyRow(
        modifier = Modifier.drawHorizontalScrollbar(state),
        state = state
    ) {
        items(50) {
            Text(
                text = (it + 1).toString(),
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            )
        }
    }
}
