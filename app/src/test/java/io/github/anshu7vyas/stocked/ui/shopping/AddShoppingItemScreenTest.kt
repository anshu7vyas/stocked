package io.github.anshu7vyas.stocked.ui.shopping

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import io.github.anshu7vyas.stocked.data.ProductRepository
import io.github.anshu7vyas.stocked.ui.home.FakeProductDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddShoppingItemScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun viewModel() = ShoppingViewModel(
        ProductRepository(FakeProductDao()),
        CoroutineScope(SupervisorJob() + Dispatchers.Main),
    )

    @Test
    fun `blank name shows error and does not finish`() {
        var done = false
        composeRule.setContent {
            AddShoppingItemScreen(onDone = { done = true }, viewModel = viewModel())
        }

        composeRule.onNodeWithTag("save_shopping_fab").performClick()

        composeRule.onNodeWithText("Please enter name of an item.").assertExists()
        assertFalse(done)
    }

    @Test
    fun `valid name saves and finishes`() {
        var done = false
        composeRule.setContent {
            AddShoppingItemScreen(onDone = { done = true }, viewModel = viewModel())
        }

        composeRule.onNodeWithTag("shopping_name_field").performTextInput("Eggs")
        composeRule.onNodeWithTag("save_shopping_fab").performClick()

        assertTrue(done)
    }
}
