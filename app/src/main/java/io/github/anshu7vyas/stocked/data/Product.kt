package io.github.anshu7vyas.stocked.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "product")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    /** Expiry date as [LocalDate.toEpochDay]; null for shopping-list items, which have none. */
    val expiryEpochDay: Long? = null,
    val onShoppingList: Boolean = false,
    val stocked: Boolean = false,
    val consumed: Boolean = false,
    val expired: Boolean = false,
) {
    /**
     * Days until expiry, preserving legacy semantics: the expiry date itself counts
     * as "1 day left"; items count as expired only from the day after.
     * Must stay in sync with the SQL boundaries in ProductDao.expiringSoon and
     * ProductDao.markOverdueAsExpired.
     */
    fun daysLeft(today: LocalDate = LocalDate.now()): Int? {
        val epochDay = expiryEpochDay ?: return null
        val diff = (epochDay - today.toEpochDay()).toInt()
        return if (diff == 0) 1 else diff
    }
}
