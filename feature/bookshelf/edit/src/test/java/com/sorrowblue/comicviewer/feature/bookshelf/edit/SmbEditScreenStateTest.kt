package com.sorrowblue.comicviewer.feature.bookshelf.edit

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onParent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.common.truth.Truth.assertThat
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegisterBookshelfUseCase
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.LEGACY)
internal class SmbEditScreenStateTest {

    private lateinit var screenState: SmbEditScreenState

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        val context: Context = InstrumentationRegistry.getInstrumentation().context
        screenState = SmbEditScreenState(
            uiState = SmbEditScreenUiState(),
            args = BookshelfEditArgs(),
            context = context,
            snackbarHostState = SnackbarHostState(),
            scope = TestScope(),
            registerBookshelfUseCase = object : RegisterBookshelfUseCase() {
                override fun run(request: Request): Flow<Resource<Bookshelf, Error>> {
                    TODO("Not yet implemented")
                }
            },
            softwareKeyboardController = object : SoftwareKeyboardController {
                override fun hide() {
                    TODO("Not yet implemented")
                }

                override fun show() {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    @Test
    fun screenshot() {
        composeTestRule.setContent {
            ComicTheme {
                SmbEditScreen(state = screenState, onBackClick = { /*TODO*/ }) {
                }
            }
        }
        composeTestRule.onNodeWithTag("DisplayName").onParent()
            .captureRoboImage()
    }

    @Test
    fun onHostChange_empty() {
        screenState.onHostChange("")
        assertThat(screenState.uiState.host.isError).isEqualTo(true)
    }

    @Test
    fun onHostChange_pc_name() {
        screenState.onHostChange("PC_NAME")
        assertThat(screenState.uiState.host.isError).isEqualTo(false)
    }

    @Test
    fun onHostChange_ip() {
        screenState.onHostChange("127.0.0.1")
        assertThat(screenState.uiState.host.isError).isEqualTo(false)
    }

    @Test
    fun onDisplayNameChange_not_empty() {
        screenState.onDisplayNameChange("displayName")
        assertThat(screenState.uiState.displayName.isError).isEqualTo(false)
    }

    @Test
    fun onDisplayNameChange_empty() {
        screenState.onDisplayNameChange("")
        assertThat(screenState.uiState.displayName.isError).isEqualTo(true)
    }
}
