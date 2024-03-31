package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.SmbServer
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SmbEditScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var state: SmbEditScreenState

    @Before
    fun setup() {
        state = SmbEditScreenState(
            uiState = SmbEditScreenUiState(),
            args = BookshelfEditArgs(),
            context = InstrumentationRegistry.getInstrumentation().context,
            snackbarHostState = SnackbarHostState(),
            scope = TestScope(),
            registerBookshelfUseCase = object : RegisterBookshelfUseCase() {
                override fun run(request: Request): Flow<Resource<Bookshelf, Error>> {
                    return flowOf(Resource.Success(SmbServer("", "", 445, SmbServer.Auth.Guest)))
                }
            },
            softwareKeyboardController = object : SoftwareKeyboardController {
                override fun hide() = Unit

                override fun show() = Unit
            }
        )
        composeTestRule.setContent {
            SmbEditScreen(state = state, onBackClick = {}, onComplete = {})
        }
    }

    @Test
    fun smbEditScreen() {
        composeTestRule.onNodeWithTag("DisplayName").performTextInput("Test")
        assertEquals(state.uiState.displayName.value, "Test")

        composeTestRule.onNodeWithTag("Host").performTextInput("127.0.0.1")
        assertEquals(state.uiState.host.value, "127.0.0.1")

        composeTestRule.onNodeWithTag("Port").performTextClearance()
        composeTestRule.onNodeWithTag("Port").performTextInput("123")
        assertEquals(state.uiState.port.value, "123")
    }
}
