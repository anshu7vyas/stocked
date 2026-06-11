package io.github.anshu7vyas.stocked.ui.timeline

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.github.anshu7vyas.stocked.data.Product
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimelineRowTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun product(stocked: Boolean = false, expired: Boolean = false) = Product(
        id = 1, name = "Milk", category = "Dairy products",
        expiryEpochDay = 20_000, stocked = stocked, expired = expired,
    )

    @Test
    fun `stocked item shows STOCKED badge`() {
        composeRule.setContent { TimelineRow(product(stocked = true)) }
        composeRule.onNodeWithText("STOCKED").assertExists()
    }

    @Test
    fun `expired item shows EXPIRED badge`() {
        composeRule.setContent { TimelineRow(product(expired = true)) }
        composeRule.onNodeWithText("EXPIRED").assertExists()
    }

    @Test
    fun `neither stocked nor expired shows CONSUMED badge`() {
        composeRule.setContent { TimelineRow(product()) }
        composeRule.onNodeWithText("CONSUMED").assertExists()
    }
}
