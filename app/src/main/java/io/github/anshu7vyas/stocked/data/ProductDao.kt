package io.github.anshu7vyas.stocked.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    /** Pantry: stocked items, soonest expiry first; null expiries (schema-legal) last. */
    @Query(
        "SELECT * FROM product WHERE stocked = 1 AND onShoppingList = 0 " +
            "ORDER BY expiryEpochDay IS NULL, expiryEpochDay ASC"
    )
    fun stockedProducts(): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE onShoppingList = 1 ORDER BY id DESC")
    fun shoppingListProducts(): Flow<List<Product>>

    /** Timeline: every tracked (non-shopping) item, soonest expiry first. */
    @Query(
        "SELECT * FROM product WHERE onShoppingList = 0 " +
            "ORDER BY expiryEpochDay IS NULL, expiryEpochDay ASC"
    )
    fun timelineProducts(): Flow<List<Product>>

    /**
     * Stocked items expiring today or tomorrow — the notification set.
     * Boundary semantics must stay in sync with [Product.daysLeft] and
     * [markOverdueAsExpired]: an item expires the day AFTER its expiry date.
     */
    @Query(
        "SELECT * FROM product WHERE stocked = 1 AND onShoppingList = 0 " +
            "AND expiryEpochDay BETWEEN :todayEpochDay AND :todayEpochDay + 1"
    )
    suspend fun expiringSoon(todayEpochDay: Long): List<Product>

    @Insert
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    /** Flip overdue stocked items to expired. Returns number of rows changed. */
    @Query(
        "UPDATE product SET expired = 1, stocked = 0, consumed = 0 " +
            "WHERE stocked = 1 AND onShoppingList = 0 AND expiryEpochDay < :todayEpochDay"
    )
    suspend fun markOverdueAsExpired(todayEpochDay: Long): Int
}
