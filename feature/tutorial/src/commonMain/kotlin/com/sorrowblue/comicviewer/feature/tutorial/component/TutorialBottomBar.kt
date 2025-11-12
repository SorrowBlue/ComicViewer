package com.sorrowblue.comicviewer.feature.tutorial.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.tutorial.TutorialSheet
import com.sorrowblue.comicviewer.feature.tutorial.isLastPage
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.material3.HorizontalPagerIndicator

@Composable
internal fun TutorialBottomBar(pageState: PagerState, onNextClick: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(ComicTheme.dimension.margin),
    ) {
        HorizontalPagerIndicator(
            pagerState = pageState,
            activeColor = MaterialTheme.colorScheme.primary,
            pageCount = TutorialSheet.entries.size,
        )
        TextButton(onClick = onNextClick) {
            if (pageState.isLastPage) {
                Text(text = "Done")
            } else {
                Text(text = "Next")
            }
        }
    }
}
