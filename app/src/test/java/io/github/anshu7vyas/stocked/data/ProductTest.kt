package io.github.anshu7vyas.stocked.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductTest {

    private val today: LocalDate = LocalDate.of(2026, 6, 10)

    private fun productExpiring(date: LocalDate?) = Product(
        name = "Milk",
        category = "Dairy products",
        expiryEpochDay = date?.toEpochDay(),
        stocked = true,
    )

    @Test
    fun `future expiry is plain calendar-day difference`() {
        val product = productExpiring(today.plusDays(8))
        assertEquals(8, product.daysLeft(today))
    }

    @Test
    fun `expiry day counts as one day left - legacy semantics`() {
        val product = productExpiring(today)
        assertEquals(1, product.daysLeft(today))
    }

    @Test
    fun `tomorrow also counts as one day left`() {
        val product = productExpiring(today.plusDays(1))
        assertEquals(1, product.daysLeft(today))
    }

    @Test
    fun `past expiry is negative`() {
        val product = productExpiring(today.minusDays(3))
        assertEquals(-3, product.daysLeft(today))
    }

    @Test
    fun `cross-year boundary is exact`() {
        val newYearsEve = LocalDate.of(2026, 12, 31)
        val product = productExpiring(LocalDate.of(2027, 1, 2))
        assertEquals(2, product.daysLeft(newYearsEve))
    }

    @Test
    fun `shopping items have no expiry`() {
        assertNull(productExpiring(null).daysLeft(today))
    }
}
