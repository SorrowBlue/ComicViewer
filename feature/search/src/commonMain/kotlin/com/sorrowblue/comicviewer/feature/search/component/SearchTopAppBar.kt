package com.sorrowblue.comicviewer.feature.search.component

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.CanonicalTopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.BackIconButton
import com.sorrowblue.comicviewer.framework.ui.material3.SettingsIconButton
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_date_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_date_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_name_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_name_desc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_size_asc
import comicviewer.feature.folder.generated.resources.folder_sorttype_label_size_desc
import comicviewer.feature.search.generated.resources.Res
import comicviewer.feature.search.generated.resources.search_label_bookshelf
import comicviewer.feature.search.generated.resources.search_label_hour24
import comicviewer.feature.search.generated.resources.search_label_in_folder
import comicviewer.feature.search.generated.resources.search_label_month1
import comicviewer.feature.search.generated.resources.search_label_none
import comicviewer.feature.search.generated.resources.search_label_search
import comicviewer.feature.search.generated.resources.search_label_show_hidden_files
import comicviewer.feature.search.generated.resources.search_label_sub_folder
import comicviewer.feature.search.generated.resources.search_label_week1
import org.jetbrains.compose.resources.stringResource
import comicviewer.feature.folder.generated.resources.Res as FolderRes

internal sealed interface SearchTopAppBarAction {

    /** 戻るボタン */
    data object BackClick : SearchTopAppBarAction

    /** 設定 */
    data object Settings : SearchTopAppBarAction

    /**
     * 検索クエリ
     *
     * @property value
     */
    data class QueryChange(val value: String) : SearchTopAppBarAction

    /**
     * 検索範囲
     *
     * @property range 検索範囲
     */
    data class RangeClick(val range: SearchCondition.Range) :
        SearchTopAppBarAction

    /**
     * 検索期間
     *
     * @property period 検索期間
     */
    data class PeriodClick(val period: SearchCondition.Period) :
        SearchTopAppBarAction

    /**
     * ソート
     *
     * @property sortType ソート
     */
    data class SortTypeClick(val sortType: SortType) :
        SearchTopAppBarAction

    /**
     * 隠しファイルを表示するか
     *
     * @property value 隠しファイルを表示するか
     */
    data class ShowHiddenClick(val value: Boolean) : SearchTopAppBarAction
}

@Composable
internal fun SearchTopAppBar(
    searchCondition: SearchCondition,
    onAction: (SearchTopAppBarAction) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    scrollableState: ScrollableState,
) {
    CanonicalTopAppBar(
        title = {
            val skc = LocalSoftwareKeyboardController.current
            TextField(
                value = searchCondition.query,
                onValueChange = { onAction(SearchTopAppBarAction.QueryChange(it)) },
                placeholder = { Text(text = stringResource(Res.string.search_label_search)) },
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                keyboardActions = KeyboardActions(onSearch = { skc?.hide() }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth()
            )
        },
        navigationIcon = {
            BackIconButton(onClick = { onAction(SearchTopAppBarAction.BackClick) })
        },
        actions = {
            SettingsIconButton(onClick = { onAction(SearchTopAppBarAction.Settings) })
        },
        bottomComponent = {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = ComicTheme.dimension.margin),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DropdownMenuChip(
                    text = stringResource(searchCondition.range.displayText),
                    onChangeSelect = { onAction(SearchTopAppBarAction.RangeClick(it)) },
                    menus = remember { SearchCondition.Range.entries }
                ) {
                    Text(stringResource(it.displayText))
                }
                DropdownMenuChip(
                    text = stringResource(searchCondition.period.displayText),
                    onChangeSelect = { onAction(SearchTopAppBarAction.PeriodClick(it)) },
                    menus = remember { SearchCondition.Period.entries }
                ) {
                    Text(stringResource(it.displayText))
                }
                DropdownMenuChip(
                    text = stringResource(searchCondition.sortType.displayText),
                    onChangeSelect = { onAction(SearchTopAppBarAction.SortTypeClick(it)) },
                    menus = remember { SortType.entries }
                ) {
                    // Text(stringResource(it.displayText))
                }
                FilterChip(
                    selected = searchCondition.showHidden,
                    onClick = { onAction(SearchTopAppBarAction.ShowHiddenClick(!searchCondition.showHidden)) },
                    label = { Text(text = stringResource(Res.string.search_label_show_hidden_files)) },
                    leadingIcon = {
                        if (searchCondition.showHidden) {
                            Icon(imageVector = ComicIcons.Check, contentDescription = null)
                        }
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior,
        scrollableState = scrollableState
    )
}

private val SearchCondition.Period.displayText
    get() = when (this) {
        SearchCondition.Period.None -> Res.string.search_label_none
        SearchCondition.Period.Hour24 -> Res.string.search_label_hour24
        SearchCondition.Period.Week1 -> Res.string.search_label_week1
        SearchCondition.Period.Month1 -> Res.string.search_label_month1
    }
private val SearchCondition.Range.displayText
    get() = when (this) {
        SearchCondition.Range.Bookshelf -> Res.string.search_label_bookshelf
        is SearchCondition.Range.InFolder -> Res.string.search_label_in_folder
        is SearchCondition.Range.SubFolder -> Res.string.search_label_sub_folder
    }
private val SortType.displayText
    get() = when (this) {
        is SortType.Date -> if (isAsc) FolderRes.string.folder_sorttype_label_date_asc else FolderRes.string.folder_sorttype_label_date_desc
        is SortType.Name -> if (isAsc) FolderRes.string.folder_sorttype_label_name_asc else FolderRes.string.folder_sorttype_label_name_desc
        is SortType.Size -> if (isAsc) FolderRes.string.folder_sorttype_label_size_asc else FolderRes.string.folder_sorttype_label_size_desc
    }
