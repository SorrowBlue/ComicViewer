package com.sorrowblue.comicviewer.feature.readlater.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.sorrowblue.comicviewer.feature.readlater.R
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme

@Composable
internal fun EmptyContent(modifier: Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painterResource(id = com.sorrowblue.comicviewer.framework.resource.R.drawable.ic_undraw_no_data_re_kwbl),
            null
        )
        Spacer(modifier = Modifier.size(AppMaterialTheme.dimens.spacer))
        Text(
            text = stringResource(id = R.string.readlater_label_nothing_to_read_later),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
