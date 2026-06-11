package io.github.anshu7vyas.stocked.data

import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ProductRepository @Inject constructor(
    private val dao: ProductDao,
) {
    fun stockedProducts(): Flow<List<Product>> = dao.stockedProducts()

    fun shoppingListProducts(): Flow<List<Product>> = dao.shoppingListProducts()

    fun timelineProducts(): Flow<List<Product>> = dao.timelineProducts()

    suspend fun addStockedProduct(name: String, category: String, expiry: LocalDate) {
        dao.insert(
            Product(
                name = name,
                category = category,
                expiryEpochDay = expiry.toEpochDay(),
                stocked = true,
            )
        )
    }

    suspend fun addShoppingItem(name: String) {
        dao.insert(Product(name = name, category = "", onShoppingList = true))
    }

    suspend fun markConsumed(product: Product) {
        dao.update(product.copy(consumed = true, stocked = false, expired = false))
    }

    suspend fun markExpired(product: Product) {
        dao.update(product.copy(expired = true, stocked = false, consumed = false))
    }

    suspend fun delete(product: Product) = dao.delete(product)

    /** Sweep stocked items whose expiry date has passed; returns rows flipped. */
    suspend fun expireOverdue(today: LocalDate = LocalDate.now()): Int =
        dao.markOverdueAsExpired(today.toEpochDay())

    suspend fun expiringSoon(today: LocalDate = LocalDate.now()): List<Product> =
        dao.expiringSoon(today.toEpochDay())
}
