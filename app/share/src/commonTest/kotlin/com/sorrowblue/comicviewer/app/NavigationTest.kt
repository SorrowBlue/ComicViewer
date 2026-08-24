/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import androidx.compose.ui.test.waitUntilDoesNotExist
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.framework.ui.core.isCompactWindowClass
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

expect fun setupTest()

expect fun tearDownTest(appGraph: AppGraph)

expect fun createAppGraph(): AppGraph

@Composable
expect fun AppContent(appGraph: AppGraph)

private const val TEST_TIMEOUT = 15000L

@OptIn(ExperimentalTestApi::class)
class NavigationTest {

    private lateinit var appGraph: AppGraph
    private var isCompact = true

    @BeforeTest
    fun setup() {
        appGraph = createAppGraph()
        setupTest()
    }

    @AfterTest
    fun tearDown() {
        tearDownTest(appGraph)
    }

    private fun runComicViewerAppTest(block: suspend ComposeUiTest.() -> Unit) {
        runComposeUiTest {
            setContent {
                isCompact = isCompactWindowClass()
                context(appGraph.context) {
                    MetroContent {
                        ComicViewerApp(finishApp = {})
                        AppContent(appGraph)
                    }
                }
            }
            tutorial()
            block()
        }
    }

    @Test
    fun tabTest() = runComicViewerAppTest {
        // Collection
        onAllNodesWithTag("NavigationSuiteItem")[1].performClick()
        onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
        // Readlater
        onAllNodesWithTag("NavigationSuiteItem")[2].performClick()
        onNodeWithTag("ReadLaterScreenRoot").assertIsDisplayed()
        // History
        onAllNodesWithTag("NavigationSuiteItem")[3].performClick()
        onNodeWithTag("HistoryScreenRoot").assertIsDisplayed()
        // Bookshelf
        onAllNodesWithTag("NavigationSuiteItem")[0].performClick()
        onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()
    }

    @Test
    fun bookshelfTest() = runComicViewerAppTest {
        onAllNodesWithTag("NavigationSuiteItem")[0].performClick()
        onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("BookshelfFab").onFirst().performClick()
        onNodeWithTag("BookshelfSelectionList").assertIsDisplayed()

        if (isCompact) {
            onNodeWithTag("BackButton").performClick()
        } else {
            onNodeWithTag("CancelButton").performClick()
        }
        onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("BookshelfFab").onFirst().performClick()
        onNodeWithTag("BookshelfSelectionList").assertIsDisplayed()

        onNodeWithTag("BookshelfSelectionItem-${BookshelfType.SMB}").performClick()
        onNodeWithTag("BookshelfEditorScreen").assertIsDisplayed()

        onNodeWithTag("BackButton").performClick()
        onNodeWithTag("BookshelfSelectionList").assertIsDisplayed()

        onNodeWithTag("BookshelfSelectionItem-${BookshelfType.DEVICE}")
            .performClick()
        onNodeWithTag("BookshelfEditorScreen").assertIsDisplayed()

        onNodeWithTag("BackButton").performClick()
        onNodeWithTag("BookshelfSelectionList").assertIsDisplayed()

        onNodeWithTag("BookshelfSelectionItem-${BookshelfType.SMB}").performClick()
        onNodeWithTag("BookshelfEditorScreen").assertIsDisplayed()

        onNodeWithTag("DisplayNameField").performTextInput("SMBBookshelf")
        onNodeWithTag("HostField").performTextInput(BuildConfig.SMB_HOST)
        onNodeWithTag("PortField").performTextClearance()
        onNodeWithTag("PortField")
            .performTextInput(BuildConfig.SMB_PORT.toString())
        onNodeWithTag("PathField").performTextInput(BuildConfig.SMB_PATH)
        onNodeWithTag("AuthUserPass").performClick()
        onNodeWithTag("DomainField").performTextInput(BuildConfig.SMB_DOMAIN)
        onNodeWithTag("UsernameField")
            .performTextInput(BuildConfig.SMB_USERNAME)
        onNodeWithTag("PasswordField")
            .performTextInput(BuildConfig.SMB_PASSWORD)
        onNodeWithTag("SaveButton").performClick()
        waitUntilDoesNotExist(hasTestTag("BookshelfEditorScreen"), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("BookshelfListItemMenu").onFirst().performClick()
        onNodeWithTag("BookshelfInfoScreenRoot").assertIsDisplayed()
        waitUntilAtLeastOneExists(hasTestTag("EditButton"), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("EditButton").performClick()
        onNodeWithTag("BookshelfEditorScreen").assertIsDisplayed()
        if (isCompact) {
            onNodeWithTag("BackButton").performClick()
        } else {
            onNodeWithTag("CancelButton").performClick()
        }

        onNodeWithTag("DeleteButton").performClick()
        onNodeWithTag("BookshelfDeleteScreenRoot").assertIsDisplayed()
        onNodeWithTag("DismissButton").performClick()
        onNodeWithTag("BookshelfInfoScreenRoot").assertIsDisplayed()
        onAllNodesWithTag("CloseButton").onLast().performClick()
        onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("BookshelfListItem").onFirst()
            .performTouchInput {
                click(Offset(centerX, 10f))
            }
        onNodeWithTag("FolderScreenRoot").assertIsDisplayed()

        waitUntilAtLeastOneExists(hasTestTag("FileListItemMenu"), timeoutMillis = TEST_TIMEOUT)
        onAllNodesWithTag("FileListItemMenu").onFirst().performClick()
        onNodeWithTag("FileInfoScreenRoot").assertIsDisplayed()

        onNodeWithTag("AddCollectionButton").performClick()
        onNodeWithTag("BasicCollectionAddScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("CloseButton").onLast().performClick()
        onNodeWithTag("FileInfoScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("CloseButton").onLast().performClick()
        onNodeWithTag("FolderScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("FileListItem").onFirst().performClick()
        onNodeWithTag("FolderScreenRoot").assertIsDisplayed()

        onNodeWithTag("SearchButton").performClick()
        onNodeWithTag("SearchScreenRoot").assertIsDisplayed()

        waitUntilAtLeastOneExists(hasTestTag("FileListItemMenu"), timeoutMillis = TEST_TIMEOUT)
        onAllNodesWithTag("FileListItemMenu").onFirst().performClick()

        onNodeWithTag("FileInfoScreenRoot").assertIsDisplayed()

        onNodeWithTag("OpenFolderButton").performClick()
        onNodeWithTag("FolderScreenRoot").assertIsDisplayed()

        onNodeWithTag("BackButton").performClick()
        onNodeWithTag("FileInfoScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("CloseButton").onLast().performClick()
        onNodeWithTag("SearchScreenRoot").assertIsDisplayed()

        onNodeWithTag("SmartCollectionButton").performClick()

        onNodeWithTag("SmartCollectionCreateScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("CloseButton").onLast().performClick()
        onNodeWithTag("SearchScreenRoot").assertIsDisplayed()
    }

    @Test
    fun bookshelfEditTransitionTest() = runComicViewerAppTest {
        // 1. 本棚一覧画面から登録画面（Selection）へ
        onAllNodesWithTag("NavigationSuiteItem")[0].performClick()
        onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()
        onAllNodesWithTag("BookshelfFab").onFirst().performClick()
        onNodeWithTag("BookshelfSelectionList").assertIsDisplayed()

        // 2. SMB本棚を選択してEditor画面へ
        onNodeWithTag("BookshelfSelectionItem-${BookshelfType.SMB}").performClick()
        onNodeWithTag("BookshelfEditorScreen").assertIsDisplayed()

        // 3. 値を入力して変更状態にする (テストのためDisplayNameフィールドに入力)
        onNodeWithTag("DisplayNameField").performTextInput("TransitionTestSMB")

        if (isCompact) {
            onNodeWithTag("BackButton").performClick()
        } else {
            onNodeWithTag("BackButton").performClick()
        }

        // 5. 破棄確認ダイアログが表示されることを検証
        onNodeWithTag("DiscordDialog").assertIsDisplayed()

        // 6. キャンセル（Dismiss）して編集画面に戻る
        onNodeWithTag("DismissButton").performClick()
        onNodeWithTag("BookshelfEditorScreen").assertIsDisplayed()

        // 7. 再度戻るボタンを押して、今度は破棄（Confirm）してSelectionへ戻る
        if (isCompact) {
            onNodeWithTag("BackButton").performClick()
        } else {
            onNodeWithTag("BackButton").performClick()
        }

        onNodeWithTag("DiscordDialog").assertIsDisplayed()

        onNodeWithTag("ConfirmButton").performClick()
        onNodeWithTag("BookshelfSelectionList").assertIsDisplayed()

        // 8. Selectionから一覧画面へ戻る
        if (isCompact) {
            onNodeWithTag("BackButton").performClick()
        } else {
            onNodeWithTag("CancelButton").performClick()
        }
        onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()
    }

    @Test
    fun collectionTest() = runComicViewerAppTest {
        // Add a bookshelf to be able to create a smart collection
        onAllNodesWithTag("BookshelfFab").onFirst().performClick()
        onNodeWithTag("BookshelfSelectionList").assertIsDisplayed()
        onNodeWithTag("BookshelfSelectionItem-${BookshelfType.SMB}").performClick()
        onNodeWithTag("BookshelfEditorScreen").assertIsDisplayed()
        onNodeWithTag("DisplayNameField").performTextInput("SMBBookshelf")
        onNodeWithTag("HostField").performTextInput(BuildConfig.SMB_HOST)
        onNodeWithTag("PortField").performTextClearance()
        onNodeWithTag("PortField").performTextInput(BuildConfig.SMB_PORT.toString())
        onNodeWithTag("PathField").performTextInput(BuildConfig.SMB_PATH)
        onNodeWithTag("AuthUserPass").performClick()
        onNodeWithTag("DomainField").performTextInput(BuildConfig.SMB_DOMAIN)
        onNodeWithTag("UsernameField").performTextInput(BuildConfig.SMB_USERNAME)
        onNodeWithTag("PasswordField").performTextInput(BuildConfig.SMB_PASSWORD)
        onNodeWithTag("SaveButton").performClick()
        waitUntilDoesNotExist(hasTestTag("BookshelfEditorScreen"), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("BookshelfScreenRoot").assertIsDisplayed()

        onAllNodesWithTag("NavigationSuiteItem")[1].performClick()
        onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()

        // Basic collection create
        onNodeWithTag("FloatingActionButton").performClick()
        onNodeWithTag("BasicCollectionCreateButton").performClick()
        onNodeWithTag("BasicCollectionCreateScreenRoot").assertIsDisplayed()
        onNodeWithTag("CollectionNameField").requestFocus()
        onNodeWithTag("CollectionNameField").performTextInput("TestCollectionName")
        waitUntilAtLeastOneExists(hasTestTag("CreateButton") and isEnabled(), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("CreateButton").performClick()
        waitUntilDoesNotExist(hasTestTag("BasicCollectionCreateScreenRoot"), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()

        // Collection
        onAllNodesWithTag("CollectionListItem").onFirst().performClick()
        onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()

        // Basic collection edit
        onNodeWithTag("EditButton").performClick()
        onNodeWithTag("BasicCollectionEditScreenRoot").assertIsDisplayed()
        onNodeWithTag("CloseButton").performClick()
        onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()

        // Basic collection delete
        onNodeWithTag("DeleteButton").performClick()
        onNodeWithTag("DeleteCollectionScreenRoot").assertIsDisplayed()
        onNodeWithTag("ConfirmButton").performClick()
        waitUntilDoesNotExist(hasTestTag("DeleteCollectionScreenRoot"), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()

        // Smart collection create
        onNodeWithTag("FloatingActionButton").performClick()
        onNodeWithTag("SmartCollectionCreateButton").performClick()
        onNodeWithTag("SmartCollectionCreateScreenRoot").assertIsDisplayed()
        onNodeWithTag("CollectionNameField").requestFocus()
        onNodeWithTag("CollectionNameField").performTextInput("TestCollectionName")
        onNodeWithTag("QueryField").performTextInput("Search keyword")
        waitUntilAtLeastOneExists(hasTestTag("CreateButton") and isEnabled(), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("CreateButton").performClick()
        waitUntilDoesNotExist(hasTestTag("SmartCollectionCreateScreenRoot"), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()

        // Collection
        onAllNodesWithTag("CollectionListItem").onFirst().performClick()
        onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()

        // Smart collection edit
        onNodeWithTag("EditButton").performClick()
        onNodeWithTag("SmartCollectionEditScreenRoot").assertIsDisplayed()
        onNodeWithTag("CloseButton").performClick()
        onNodeWithTag("CollectionScreenRoot").assertIsDisplayed()

        // Smart collection delete
        onNodeWithTag("DeleteButton").performClick()
        onNodeWithTag("DeleteCollectionScreenRoot").assertIsDisplayed()
        onNodeWithTag("ConfirmButton").performClick()
        waitUntilDoesNotExist(hasTestTag("DeleteCollectionScreenRoot"), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("CollectionListScreenRoot").assertIsDisplayed()
    }

    @Test
    fun settingsTest() = runComicViewerAppTest {
        onNodeWithTag("SettingsButton").performClick()
        onNodeWithTag("SettingsScreenRoot").assertIsDisplayed()

        checkSettings("DisplaySettings", "DisplaySettingsRoot")
        checkSettings("FolderSettings", "FolderSettingsRoot")
        checkSettings("ViewerSettings", "ViewerSettingsRoot")
        checkSettings("SecuritySettings", "SecuritySettingsRoot")
        checkSettings("ExtensionSettings", "ExtensionSettingsRoot")
        checkSettings("InfoSettings", "InfoSettingsRoot")

        onAllNodesWithTag("CloseButton").onFirst().performClick()
    }

    private fun ComposeUiTest.checkSettings(itemTestTag: String, screenTestTag: String) {
        onNodeWithTag(itemTestTag).performClick()
        onNodeWithTag(screenTestTag).assertIsDisplayed()
        onNodeWithTag("BackButton").performClick()
    }

    private fun ComposeUiTest.tutorial() {
        waitUntilAtLeastOneExists(hasTestTag("TutorialScreen"), timeoutMillis = TEST_TIMEOUT)
        onNodeWithTag("NextButton").performClick()
        onNodeWithTag("NextButton").performClick()
        onNodeWithTag("NextButton").performClick()
        waitUntilDoesNotExist(hasTestTag("TutorialScreen"), timeoutMillis = TEST_TIMEOUT)
    }
}
