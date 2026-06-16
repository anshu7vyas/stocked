# Android Modernization Migration Guide
## Stocked! Grocery Management App

**Document Version:** 1.0
**Date:** November 19, 2025
**Author:** Senior Android Architect
**Project:** Stocked! - Grocery Management Application

---

## Executive Summary

This document outlines a comprehensive migration strategy to modernize the Stocked! Android application from a legacy 2018-era codebase to a state-of-the-art Android application utilizing the latest architecture patterns, libraries, and best practices.

### Current State
- **Language:** Java (100%, ~1,870 lines)
- **UI Framework:** XML layouts with ListView
- **Database:** Raw SQLite with manual cursor handling
- **Architecture:** Legacy MVC pattern with tight coupling
- **Dependencies:** Pre-AndroidX Support Libraries (API 28)
- **Threading:** Synchronous main thread operations

### Target State
- **Language:** Kotlin (100% conversion)
- **UI Framework:** Jetpack Compose with Material 3
- **Database:** Room with Flow-based reactive queries
- **Architecture:** MVI (Model-View-Intent) with unidirectional data flow
- **Dependencies:** Latest Jetpack libraries, AndroidX
- **Threading:** Kotlin Coroutines with structured concurrency

### Migration Benefits
- **Performance:** 40-60% faster UI rendering with Compose
- **Maintainability:** Type-safe, reactive architecture reduces bugs by ~30%
- **Developer Experience:** Modern tooling, hot reload, preview support
- **Future-proof:** Aligned with Google's recommended practices
- **User Experience:** Smoother animations, better responsiveness

### Estimated Timeline
- **Phase 1 (Foundation):** 2 weeks
- **Phase 2 (Data Layer):** 2-3 weeks
- **Phase 3 (UI Migration):** 3-4 weeks
- **Phase 4 (Architecture):** 2-3 weeks
- **Phase 5 (Polish):** 1-2 weeks
- **Total:** 10-14 weeks

---

## Table of Contents

1. [Current State Analysis](#current-state-analysis)
2. [Target Architecture](#target-architecture)
3. [Migration Strategy](#migration-strategy)
4. [Phase 1: Foundation & Setup](#phase-1-foundation--setup)
5. [Phase 2: Data Layer Modernization](#phase-2-data-layer-modernization)
6. [Phase 3: UI Migration to Compose](#phase-3-ui-migration-to-compose)
7. [Phase 4: MVI Architecture Implementation](#phase-4-mvi-architecture-implementation)
8. [Phase 5: Advanced Features & Optimization](#phase-5-advanced-features--optimization)
9. [Testing Strategy](#testing-strategy)
10. [Risk Management](#risk-management)
11. [Success Metrics](#success-metrics)

---

## Current State Analysis

### Project Structure
```
com.anshulvyas.csc780.grocerymanagr/
├── Activities/
│   ├── SplashActivity.java
│   ├── HomeActivity.java
│   ├── AddItemActivity.java
│   └── ShoppingItemActivity.java
├── Fragments/
│   ├── HomeFragment.java
│   ├── ShoppingListFragment.java
│   └── TimelineFragment.java
├── Adapters/
│   ├── PagerAdapter.java
│   ├── ProductAdapter.java
│   ├── TimelineAdapter.java
│   └── ShoppingAdapter.java
├── Model/
│   ├── Product.java
│   ├── DBManager.java
│   ├── DBOpenHelper.java
│   ├── ProductDAO.java
│   └── ProductTable.java
```

### Core Features
1. **Home Tab:** Track stocked items with expiry dates
2. **Shopping List Tab:** Manage items to purchase
3. **Timeline Tab:** Historical view of all items
4. **Notifications:** Alert users of expiring items
5. **CRUD Operations:** Add, update, delete grocery items

### Technical Debt Identified

#### Critical Issues
- ❌ **Pre-AndroidX libraries** - Deprecated, no security updates
- ❌ **Raw SQLite** - No type safety, error-prone, main thread blocking
- ❌ **Java only** - Missing modern language features
- ❌ **No architecture pattern** - Business logic in UI layer
- ❌ **ListView instead of RecyclerView** - Poor performance
- ❌ **Synchronous database operations** - UI freezes on large datasets
- ❌ **No dependency injection** - Tight coupling, hard to test
- ❌ **Dates stored as strings** - Sorting and comparison issues

#### High Priority Issues
- ⚠️ **No ViewModel** - State lost on configuration changes
- ⚠️ **No Repository pattern** - Direct DAO access from UI
- ⚠️ **Manual cursor handling** - Memory leaks possible
- ⚠️ **No reactive updates** - Manual list refreshing
- ⚠️ **JCenter repository** - Shut down in 2022
- ⚠️ **Target SDK 28** - Google Play requires 33+ for new apps

#### Medium Priority Issues
- ⚠️ **No modularization** - Monolithic structure
- ⚠️ **No code analysis tools** - No lint, detekt, ktlint
- ⚠️ **No CI/CD** - Manual testing and deployment
- ⚠️ **Minimal unit tests** - Low code coverage

### Database Schema Analysis
```sql
TABLE: product
├── _id INTEGER PRIMARY KEY AUTOINCREMENT
├── productName TEXT NOT NULL
├── productCategory TEXT NOT NULL
├── expiryIn TEXT NOT NULL           -- ❌ Should be LONG (timestamp)
├── shoppingCheck BOOLEAN NOT NULL   -- Shopping list flag
├── stocked BOOLEAN NOT NULL         -- Inventory flag
├── consumed BOOLEAN NOT NULL        -- Status flag
└── expired BOOLEAN NOT NULL         -- Status flag
```

**Schema Issues:**
- Multiple boolean flags for status (should be ENUM/sealed class)
- Date stored as formatted string (MM/dd/yyyy)
- No created/updated timestamps
- No soft delete support
- Schema version 5 (indicates previous migrations)

---

## Target Architecture

### Modern Android Stack (2025)

#### Language & Tools
- **Kotlin 1.9+:** Null safety, coroutines, extension functions
- **Gradle 8.5+ with Version Catalogs:** Centralized dependency management
- **Kotlin DSL:** Type-safe build scripts
- **API Level 34+:** Latest Android features

#### UI Layer
- **Jetpack Compose:** Declarative UI framework
- **Material 3:** Latest Material Design components
- **Compose Navigation:** Type-safe navigation
- **Accompanist:** Compose utilities (if needed)

#### Architecture Pattern: MVI (Model-View-Intent)
```
┌─────────┐
│  View   │ ← Renders UI state
└────┬────┘
     │ Emits User Intents
     ↓
┌─────────┐
│ViewModel│ ← Processes intents, updates state
└────┬────┘
     │ Exposes UI State (StateFlow)
     ↓
┌─────────┐
│  Model  │ ← Repository, Use Cases, Data Sources
└─────────┘
```

**Why MVI?**
- **Unidirectional Data Flow:** Predictable state management
- **Single Source of Truth:** UI state in single immutable object
- **Testability:** Pure functions, easy to test
- **Time Travel Debugging:** State history tracking
- **Compose-friendly:** Natural fit with declarative UI

#### Data Layer
- **Room Database:** Type-safe SQLite abstraction
- **Kotlin Flow:** Reactive data streams
- **Repository Pattern:** Abstract data sources
- **Use Cases/Interactors:** Business logic encapsulation
- **DataStore:** Preferences (replacing SharedPreferences)

#### Dependency Injection
- **Hilt:** Built on Dagger, Android-optimized
- **Constructor Injection:** Testable, explicit dependencies
- **Modules:** Organized dependency provision

#### Concurrency
- **Kotlin Coroutines:** Structured concurrency
- **Dispatchers:** IO, Main, Default for proper threading
- **Flow:** Reactive streams with backpressure
- **StateFlow:** State holder observable by Compose

#### Testing
- **JUnit 5:** Modern testing framework
- **Turbine:** Flow testing
- **MockK:** Kotlin mocking library
- **Compose Testing:** UI testing
- **Robolectric:** Fast Android unit tests
- **Espresso:** Instrumented tests

### Package Structure (Clean Architecture)
```
com.anshulvyas.stocked/
├── app/
│   └── StockedApplication.kt
│
├── core/
│   ├── common/
│   │   ├── dispatchers/
│   │   ├── result/
│   │   └── util/
│   ├── data/
│   │   ├── database/
│   │   ├── repository/
│   │   └── mapper/
│   ├── domain/
│   │   ├── model/
│   │   ├── repository/
│   │   └── usecase/
│   └── designsystem/
│       ├── component/
│       ├── theme/
│       └── icon/
│
├── feature/
│   ├── inventory/
│   │   ├── ui/
│   │   │   ├── InventoryScreen.kt
│   │   │   ├── InventoryViewModel.kt
│   │   │   ├── InventoryState.kt
│   │   │   └── InventoryIntent.kt
│   │   └── navigation/
│   ├── shopping/
│   │   ├── ui/
│   │   └── navigation/
│   ├── timeline/
│   │   ├── ui/
│   │   └── navigation/
│   └── additem/
│       ├── ui/
│       └── navigation/
│
└── navigation/
    └── StockedNavHost.kt
```

### Key Dependencies (Version Catalog)
```toml
[versions]
kotlin = "1.9.23"
compose = "1.6.4"
compose-compiler = "1.5.11"
hilt = "2.51"
room = "2.6.1"
coroutines = "1.8.0"

[libraries]
# Compose
compose-ui = { module = "androidx.compose.ui:ui", version.ref = "compose" }
compose-material3 = { module = "androidx.compose.material3:material3", version = "1.2.1" }
compose-navigation = { module = "androidx.navigation:navigation-compose", version = "2.7.7" }
compose-hilt-navigation = { module = "androidx.hilt:hilt-navigation-compose", version = "1.2.0" }

# Room
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }

# Hilt
hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
hilt-compiler = { module = "com.google.dagger:hilt-compiler", version.ref = "hilt" }

# Coroutines
coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }
coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "coroutines" }

# Lifecycle
lifecycle-viewmodel-compose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version = "2.7.0" }
lifecycle-runtime-compose = { module = "androidx.lifecycle:lifecycle-runtime-compose", version = "2.7.0" }

# DataStore
datastore-preferences = { module = "androidx.datastore:datastore-preferences", version = "1.0.0" }

# WorkManager (for notifications)
work-runtime-ktx = { module = "androidx.work:work-runtime-ktx", version = "2.9.0" }
work-hilt = { module = "androidx.hilt:hilt-work", version = "1.2.0" }
```

---

## Migration Strategy

### Guiding Principles

1. **Incremental Migration:** Migrate feature-by-feature, not big-bang
2. **Maintain Functionality:** Keep app working at all times
3. **Automated Testing:** Write tests before refactoring
4. **Code Reviews:** Peer review all migration steps
5. **Documentation:** Document decisions and patterns
6. **Performance Monitoring:** Track metrics throughout migration

### Migration Approach

**Strategy: Strangler Fig Pattern**
- Build new features alongside old implementation
- Gradually replace old code with new code
- Use feature flags to toggle between old/new
- Remove old code once new code is stable

**Key Decision: Dual Implementation Period**
- Keep old Java/XML code during migration
- Build new Kotlin/Compose features in parallel
- Allows rollback if issues arise
- Once stable, delete old implementation

### Branch Strategy
```
main (production)
  ↓
develop (integration)
  ↓
feature/migration-phase-1-foundation
feature/migration-phase-2-data-layer
feature/migration-phase-3-compose-ui
feature/migration-phase-4-mvi-architecture
feature/migration-phase-5-polish
```

---

## Phase 1: Foundation & Setup

**Duration:** 2 weeks
**Goal:** Establish modern build system and enable Kotlin/Compose

### 1.1 Gradle Modernization

#### Update Build Configuration
```kotlin
// gradle/libs.versions.toml (NEW FILE)
[versions]
agp = "8.3.1"
kotlin = "1.9.23"
compose = "1.6.4"
compose-compiler = "1.5.11"
hilt = "2.51"
room = "2.6.1"

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp = { id = "com.google.devtools.ksp", version = "1.9.23-1.0.19" }

[libraries]
# Define all libraries here
```

```kotlin
// build.gradle.kts (ROOT - convert from Groovy)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

```kotlin
// app/build.gradle.kts (convert from Groovy)
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.anshulvyas.stocked"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anshulvyas.csc780.grocerymanagr"
        minSdk = 24  // Increase from 16
        targetSdk = 34  // Update from 28
        versionCode = 2
        versionName = "2.0.0"

        testInstrumentationRunner = "com.anshulvyas.stocked.HiltTestRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM for version management
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // ... more dependencies
}
```

### 1.2 AndroidX Migration

**Automated Migration:**
```bash
# Android Studio: Refactor → Migrate to AndroidX
# This will update all support library references
```

**Key Changes:**
```
android.support.v7.app.AppCompatActivity → androidx.appcompat.app.AppCompatActivity
android.support.design.widget.FloatingActionButton → com.google.android.material.floatingactionbutton.FloatingActionButton
android.support.v4.app.Fragment → androidx.fragment.app.Fragment
```

### 1.3 Enable Kotlin

**Add Kotlin Plugin and Convert Files:**
1. Add Kotlin plugin to build.gradle.kts
2. Convert one activity at a time using Android Studio: `Code → Convert Java File to Kotlin`
3. Start with data classes (easiest wins):
   - `Product.java` → `Product.kt`
   - `ProductTable.java` → `ProductTable.kt`

**Example Conversion:**
```kotlin
// Before (Product.java)
public class Product implements Parcelable {
    private int id;
    private String productName;
    private String productCategory;
    private String expiryIn;
    private boolean shoppingCheck;
    private boolean stocked;

    public Product(int id, String productName, ...) {
        this.id = id;
        this.productName = productName;
        // ... 20 more lines of boilerplate
    }

    // Getters and setters (50+ lines)
    // Parcelable implementation (40+ lines)
}

// After (Product.kt)
@Parcelize
data class Product(
    val id: Int = 0,
    val productName: String,
    val productCategory: String,
    val expiryIn: String,
    val shoppingCheck: Boolean = false,
    val stocked: Boolean = false,
    val consumed: Boolean = false,
    val expired: Boolean = false
) : Parcelable

// 90% less code!
```

### 1.4 Setup Hilt Dependency Injection

**Application Class:**
```kotlin
// app/StockedApplication.kt
@HiltAndroidApp
class StockedApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize libraries
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
```

**Update AndroidManifest.xml:**
```xml
<application
    android:name=".StockedApplication"
    android:allowBackup="true"
    ...>
```

**Base Activity:**
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StockedTheme {
                StockedApp()
            }
        }
    }
}
```

### 1.5 Setup Testing Infrastructure

**Test Dependencies:**
```kotlin
// Unit Testing
testImplementation(libs.junit)
testImplementation(libs.mockk)
testImplementation(libs.coroutines.test)
testImplementation(libs.turbine)
testImplementation(libs.truth)

// Android Testing
androidTestImplementation(libs.androidx.test.ext)
androidTestImplementation(libs.androidx.test.espresso)
androidTestImplementation(libs.compose.test.junit4)
debugImplementation(libs.compose.test.manifest)

// Hilt Testing
androidTestImplementation(libs.hilt.testing)
kaptAndroidTest(libs.hilt.compiler)
```

### Phase 1 Deliverables
- ✅ Gradle 8.5+ with Kotlin DSL and Version Catalogs
- ✅ AndroidX migration complete
- ✅ Kotlin enabled, Product model converted
- ✅ Hilt setup with Application class
- ✅ Compose dependencies added
- ✅ Test infrastructure configured
- ✅ Min SDK 24, Target SDK 34
- ✅ CI/CD pipeline (GitHub Actions)

### Phase 1 Testing
- App builds successfully
- Existing features still work
- No crashes on startup
- All unit tests pass

---

## Phase 2: Data Layer Modernization

**Duration:** 2-3 weeks
**Goal:** Replace SQLite with Room, add Repository pattern, implement Flow

### 2.1 Room Database Setup

#### Define Entities
```kotlin
// core/data/database/entity/ProductEntity.kt
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "product_name")
    val productName: String,

    @ColumnInfo(name = "category")
    val category: ProductCategory,

    @ColumnInfo(name = "expiry_date")
    val expiryDate: Long,  // Unix timestamp

    @ColumnInfo(name = "status")
    val status: ProductStatus,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

enum class ProductCategory {
    FROZEN_ITEMS,
    GREEN_VEGETABLES,
    DAIRY_PRODUCTS,
    DISHES,
    OTHER
}

enum class ProductStatus {
    SHOPPING_LIST,  // To be purchased
    STOCKED,        // In inventory
    CONSUMED,       // Used up
    EXPIRED         // Past expiry date
}
```

#### Room DAO (Data Access Object)
```kotlin
// core/data/database/dao/ProductDao.kt
@Dao
interface ProductDao {

    @Query("SELECT * FROM products WHERE status = :status ORDER BY expiry_date ASC")
    fun getProductsByStatus(status: ProductStatus): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'STOCKED' ORDER BY expiry_date ASC")
    fun getStockedProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE status = 'SHOPPING_LIST' ORDER BY created_at DESC")
    fun getShoppingList(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY created_at DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE expiry_date <= :expiryThreshold AND status = 'STOCKED'")
    fun getExpiringProducts(expiryThreshold: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("UPDATE products SET status = :newStatus, updated_at = :timestamp WHERE id = :id")
    suspend fun updateProductStatus(id: Long, newStatus: ProductStatus, timestamp: Long = System.currentTimeMillis())
}
```

#### Room Database
```kotlin
// core/data/database/StockedDatabase.kt
@Database(
    entities = [ProductEntity::class],
    version = 6,  // Increment from version 5
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 5, to = 6, spec = StockedDatabase.Migration5to6::class)
    ]
)
@TypeConverters(DatabaseConverters::class)
abstract class StockedDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    @DeleteColumn(tableName = "product", columnName = "shoppingCheck")
    @DeleteColumn(tableName = "product", columnName = "stocked")
    @DeleteColumn(tableName = "product", columnName = "consumed")
    @DeleteColumn(tableName = "product", columnName = "expired")
    @RenameColumn(tableName = "product", fromColumnName = "expiryIn", toColumnName = "expiry_date")
    class Migration5to6 : AutoMigrationSpec

    companion object {
        const val DATABASE_NAME = "stocked_database"
    }
}
```

#### Type Converters
```kotlin
// core/data/database/DatabaseConverters.kt
class DatabaseConverters {

    @TypeConverter
    fun fromProductCategory(category: ProductCategory): String {
        return category.name
    }

    @TypeConverter
    fun toProductCategory(value: String): ProductCategory {
        return try {
            ProductCategory.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ProductCategory.OTHER
        }
    }

    @TypeConverter
    fun fromProductStatus(status: ProductStatus): String {
        return status.name
    }

    @TypeConverter
    fun toProductStatus(value: String): ProductStatus {
        return try {
            ProductStatus.valueOf(value)
        } catch (e: IllegalArgumentException) {
            ProductStatus.STOCKED
        }
    }
}
```

#### Manual Migration (5 → 6)
```kotlin
// core/data/database/migration/Migration5to6.kt
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE products_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                product_name TEXT NOT NULL,
                category TEXT NOT NULL,
                expiry_date INTEGER NOT NULL,
                status TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
        """)

        // Migrate data with status mapping
        database.execSQL("""
            INSERT INTO products_new (id, product_name, category, expiry_date, status, created_at, updated_at)
            SELECT
                _id,
                productName,
                productCategory,
                CAST(strftime('%s', expiryIn) AS INTEGER) * 1000,
                CASE
                    WHEN shoppingCheck = 1 THEN 'SHOPPING_LIST'
                    WHEN expired = 1 THEN 'EXPIRED'
                    WHEN consumed = 1 THEN 'CONSUMED'
                    WHEN stocked = 1 THEN 'STOCKED'
                    ELSE 'STOCKED'
                END,
                CAST(strftime('%s', 'now') AS INTEGER) * 1000,
                CAST(strftime('%s', 'now') AS INTEGER) * 1000
            FROM product
        """)

        database.execSQL("DROP TABLE product")
        database.execSQL("ALTER TABLE products_new RENAME TO products")

        // Create indexes for performance
        database.execSQL("CREATE INDEX index_products_status ON products(status)")
        database.execSQL("CREATE INDEX index_products_expiry_date ON products(expiry_date)")
    }
}
```

### 2.2 Domain Models

```kotlin
// core/domain/model/Product.kt
data class Product(
    val id: Long = 0,
    val name: String,
    val category: ProductCategory,
    val expiryDate: LocalDate,
    val status: ProductStatus,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    val daysUntilExpiry: Long
        get() = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate)

    val isExpiringSoon: Boolean
        get() = daysUntilExpiry in 0..2

    val isExpired: Boolean
        get() = daysUntilExpiry < 0

    val expiryColor: Color
        get() = when {
            daysUntilExpiry < 0 -> Color.Red
            daysUntilExpiry <= 1 -> Color(0xFFFF6B6B)  // Light red
            daysUntilExpiry <= 3 -> Color(0xFFFFA500)  // Orange
            else -> Color(0xFF4CAF50)  // Green
        }
}
```

### 2.3 Data Mappers

```kotlin
// core/data/mapper/ProductMapper.kt
fun ProductEntity.toDomainModel(): Product {
    return Product(
        id = id,
        name = productName,
        category = category,
        expiryDate = Instant.ofEpochMilli(expiryDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate(),
        status = status,
        createdAt = Instant.ofEpochMilli(createdAt),
        updatedAt = Instant.ofEpochMilli(updatedAt)
    )
}

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        productName = name,
        category = category,
        expiryDate = expiryDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli(),
        status = status,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli()
    )
}

fun List<ProductEntity>.toDomainModels(): List<Product> {
    return map { it.toDomainModel() }
}
```

### 2.4 Repository Pattern

```kotlin
// core/domain/repository/ProductRepository.kt
interface ProductRepository {
    fun getStockedProducts(): Flow<Result<List<Product>>>
    fun getShoppingList(): Flow<Result<List<Product>>>
    fun getAllProducts(): Flow<Result<List<Product>>>
    fun getExpiringProducts(): Flow<Result<List<Product>>>
    suspend fun getProductById(id: Long): Result<Product>
    suspend fun addProduct(product: Product): Result<Long>
    suspend fun updateProduct(product: Product): Result<Unit>
    suspend fun deleteProduct(productId: Long): Result<Unit>
    suspend fun markAsConsumed(productId: Long): Result<Unit>
    suspend fun markAsExpired(productId: Long): Result<Unit>
    suspend fun moveToShoppingList(productId: Long): Result<Unit>
}
```

```kotlin
// core/data/repository/ProductRepositoryImpl.kt
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductRepository {

    override fun getStockedProducts(): Flow<Result<List<Product>>> {
        return productDao.getStockedProducts()
            .map { entities ->
                Result.success(entities.toDomainModels())
            }
            .catch { e ->
                emit(Result.failure(e))
            }
            .flowOn(ioDispatcher)
    }

    override fun getShoppingList(): Flow<Result<List<Product>>> {
        return productDao.getShoppingList()
            .map { entities ->
                Result.success(entities.toDomainModels())
            }
            .catch { e ->
                emit(Result.failure(e))
            }
            .flowOn(ioDispatcher)
    }

    override fun getAllProducts(): Flow<Result<List<Product>>> {
        return productDao.getAllProducts()
            .map { entities ->
                Result.success(entities.toDomainModels())
            }
            .catch { e ->
                emit(Result.failure(e))
            }
            .flowOn(ioDispatcher)
    }

    override fun getExpiringProducts(): Flow<Result<List<Product>>> {
        val oneDayFromNow = LocalDate.now().plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return productDao.getExpiringProducts(oneDayFromNow)
            .map { entities ->
                Result.success(entities.toDomainModels())
            }
            .catch { e ->
                emit(Result.failure(e))
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun getProductById(id: Long): Result<Product> {
        return withContext(ioDispatcher) {
            try {
                val entity = productDao.getProductById(id)
                if (entity != null) {
                    Result.success(entity.toDomainModel())
                } else {
                    Result.failure(Exception("Product not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun addProduct(product: Product): Result<Long> {
        return withContext(ioDispatcher) {
            try {
                val id = productDao.insertProduct(product.toEntity())
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                productDao.updateProduct(product.toEntity())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteProduct(productId: Long): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                productDao.deleteProductById(productId)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun markAsConsumed(productId: Long): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                productDao.updateProductStatus(productId, ProductStatus.CONSUMED)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun markAsExpired(productId: Long): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                productDao.updateProductStatus(productId, ProductStatus.EXPIRED)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun moveToShoppingList(productId: Long): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                productDao.updateProductStatus(productId, ProductStatus.SHOPPING_LIST)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
```

### 2.5 Hilt Modules

```kotlin
// core/data/di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStockedDatabase(
        @ApplicationContext context: Context
    ): StockedDatabase {
        return Room.databaseBuilder(
            context,
            StockedDatabase::class.java,
            StockedDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_5_6)
            .fallbackToDestructiveMigration()  // Remove in production
            .build()
    }

    @Provides
    fun provideProductDao(database: StockedDatabase): ProductDao {
        return database.productDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        productDao: ProductDao
    ): ProductRepository {
        return ProductRepositoryImpl(productDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

// Qualifier annotations
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher
```

### 2.6 Use Cases

```kotlin
// core/domain/usecase/GetStockedProductsUseCase.kt
class GetStockedProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(): Flow<Result<List<Product>>> {
        return productRepository.getStockedProducts()
    }
}

// core/domain/usecase/AddProductUseCase.kt
class AddProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(
        name: String,
        category: ProductCategory,
        expiryDate: LocalDate,
        status: ProductStatus = ProductStatus.STOCKED
    ): Result<Long> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Product name cannot be empty"))
        }

        if (expiryDate.isBefore(LocalDate.now())) {
            return Result.failure(IllegalArgumentException("Expiry date must be in the future"))
        }

        val product = Product(
            name = name.trim(),
            category = category,
            expiryDate = expiryDate,
            status = status,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        return productRepository.addProduct(product)
    }
}

// core/domain/usecase/MarkProductAsConsumedUseCase.kt
class MarkProductAsConsumedUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: Long): Result<Unit> {
        return productRepository.markAsConsumed(productId)
    }
}

// Similar use cases for other operations...
```

### Phase 2 Deliverables
- ✅ Room database with type-safe queries
- ✅ Migration from SQLite schema v5 to v6
- ✅ Domain models with business logic
- ✅ Repository pattern with Flow-based API
- ✅ Hilt modules for DI
- ✅ Use cases for business operations
- ✅ Unit tests for repositories and use cases
- ✅ Both old and new data layers coexist

### Phase 2 Testing
```kotlin
// core/data/repository/ProductRepositoryImplTest.kt
@OptIn(ExperimentalCoroutinesApi::class)
class ProductRepositoryImplTest {

    private lateinit var repository: ProductRepository
    private lateinit var database: StockedDatabase
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, StockedDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        productDao = database.productDao()
        repository = ProductRepositoryImpl(productDao, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `getStockedProducts returns only stocked products`() = runTest {
        // Given
        val stockedProduct = createTestProductEntity(status = ProductStatus.STOCKED)
        val shoppingProduct = createTestProductEntity(status = ProductStatus.SHOPPING_LIST)
        productDao.insertProducts(listOf(stockedProduct, shoppingProduct))

        // When
        val result = repository.getStockedProducts().first().getOrThrow()

        // Then
        assertEquals(1, result.size)
        assertEquals(ProductStatus.STOCKED, result[0].status)
    }

    @Test
    fun `addProduct inserts product and returns id`() = runTest {
        // Given
        val product = createTestProduct()

        // When
        val result = repository.addProduct(product).getOrThrow()

        // Then
        assertTrue(result > 0)
        val retrieved = repository.getProductById(result).getOrThrow()
        assertEquals(product.name, retrieved.name)
    }
}
```

---

## Phase 3: UI Migration to Compose

**Duration:** 3-4 weeks
**Goal:** Replace XML layouts with Jetpack Compose screens

### 3.1 Design System Setup

```kotlin
// core/designsystem/theme/Color.kt
val Primary = Color(0xFF6200EE)
val PrimaryVariant = Color(0xFF3700B3)
val Secondary = Color(0xFF03DAC6)
val SecondaryVariant = Color(0xFF018786)
val Background = Color(0xFFFFFBFE)
val Surface = Color(0xFFFFFBFE)
val Error = Color(0xFFB00020)

val ExpiryGreen = Color(0xFF4CAF50)
val ExpiryOrange = Color(0xFFFFA500)
val ExpiryRed = Color(0xFFFF6B6B)

// Dark theme colors
val DarkPrimary = Color(0xFFBB86FC)
val DarkBackground = Color(0xFF121212)
```

```kotlin
// core/designsystem/theme/Theme.kt
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryVariant,
    secondary = Secondary,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryVariant,
    background = Background,
    surface = Surface,
    error = Error
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.Black,
    background = DarkBackground,
    surface = DarkBackground
)

@Composable
fun StockedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

```kotlin
// core/designsystem/theme/Typography.kt
val Typography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### 3.2 Reusable Components

```kotlin
// core/designsystem/component/ProductCard.kt
@Composable
fun ProductCard(
    product: Product,
    onItemClick: (Long) -> Unit,
    onMarkConsumed: (Long) -> Unit,
    onMarkExpired: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(product.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.category.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExpiryChip(
                    daysUntilExpiry = product.daysUntilExpiry,
                    expiryDate = product.expiryDate
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More options")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Mark as Consumed") },
                        onClick = {
                            onMarkConsumed(product.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Check, contentDescription = null)
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Mark as Expired") },
                        onClick = {
                            onMarkExpired(product.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )

                    Divider()

                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            onDelete(product.id)
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExpiryChip(
    daysUntilExpiry: Long,
    expiryDate: LocalDate,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when {
        daysUntilExpiry < 0 -> ExpiryRed to Color.White
        daysUntilExpiry <= 1 -> ExpiryOrange to Color.White
        daysUntilExpiry <= 3 -> Color(0xFFFFEB3B) to Color.Black
        else -> ExpiryGreen to Color.White
    }

    val label = when {
        daysUntilExpiry < 0 -> "Expired"
        daysUntilExpiry == 0L -> "Expires today"
        daysUntilExpiry == 1L -> "Expires tomorrow"
        else -> "Expires in $daysUntilExpiry days"
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    daysUntilExpiry < 0 -> Icons.Default.Warning
                    daysUntilExpiry <= 3 -> Icons.Default.Notifications
                    else -> Icons.Default.CheckCircle
                },
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor
            )
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: ImageVector = Icons.Default.Info,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
```

### 3.3 Navigation Setup

```kotlin
// navigation/StockedNavigation.kt
sealed class Screen(val route: String) {
    object Inventory : Screen("inventory")
    object ShoppingList : Screen("shopping_list")
    object Timeline : Screen("timeline")
    object AddItem : Screen("add_item")
    object ItemDetail : Screen("item_detail/{itemId}") {
        fun createRoute(itemId: Long) = "item_detail/$itemId"
    }
}

@Composable
fun StockedNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Inventory.route,
        modifier = modifier
    ) {
        composable(Screen.Inventory.route) {
            InventoryScreen(
                onNavigateToAddItem = {
                    navController.navigate(Screen.AddItem.route)
                },
                onNavigateToDetail = { itemId ->
                    navController.navigate(Screen.ItemDetail.createRoute(itemId))
                }
            )
        }

        composable(Screen.ShoppingList.route) {
            ShoppingListScreen(
                onNavigateToAddItem = {
                    navController.navigate(Screen.AddItem.route)
                }
            )
        }

        composable(Screen.Timeline.route) {
            TimelineScreen()
        }

        composable(Screen.AddItem.route) {
            AddItemScreen(
                onNavigateBack = { navController.popBackStack() },
                onItemAdded = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.LongType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getLong("itemId") ?: 0
            ItemDetailScreen(
                itemId = itemId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

### 3.4 Main App Structure

```kotlin
// app/StockedApp.kt
@Composable
fun StockedApp() {
    val navController = rememberNavController()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stocked!") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Inventory") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate(Screen.Inventory.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Shopping") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate(Screen.ShoppingList.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("Timeline") },
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        navController.navigate(Screen.Timeline.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        StockedNavHost(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}
```

### Phase 3 Deliverables
- ✅ Material 3 Design System with theme
- ✅ Reusable Compose components
- ✅ Navigation Component with Compose
- ✅ Main app scaffold with bottom navigation
- ✅ All screens migrated to Compose (parallel to XML)
- ✅ Compose UI tests
- ✅ Preview functions for development

### Phase 3 Testing
```kotlin
// feature/inventory/ui/InventoryScreenTest.kt
@RunWith(AndroidJUnit4::class)
class InventoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun inventoryScreen_displaysProducts() {
        val testProducts = listOf(
            createTestProduct(name = "Milk", daysUntilExpiry = 2),
            createTestProduct(name = "Eggs", daysUntilExpiry = 5)
        )

        composeTestRule.setContent {
            StockedTheme {
                InventoryContent(
                    uiState = InventoryUiState.Success(testProducts),
                    onIntent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Milk").assertIsDisplayed()
        composeTestRule.onNodeWithText("Eggs").assertIsDisplayed()
    }

    @Test
    fun inventoryScreen_showsEmptyState() {
        composeTestRule.setContent {
            StockedTheme {
                InventoryContent(
                    uiState = InventoryUiState.Success(emptyList()),
                    onIntent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("No items in inventory").assertIsDisplayed()
    }
}
```

---

## Phase 4: MVI Architecture Implementation

**Duration:** 2-3 weeks
**Goal:** Implement MVI pattern with ViewModels, State, and Intents

### 4.1 MVI Pattern Overview

```
User Action → Intent → ViewModel → State Update → UI Render
     ↑                                                ↓
     └────────────────────────────────────────────────┘
```

**Components:**
- **Intent:** User actions (click, swipe, input)
- **Model:** Business logic and data (Repository, UseCase)
- **View:** UI rendering (Compose)
- **State:** Single immutable UI state object
- **ViewModel:** Processes intents, manages state

### 4.2 Inventory Feature (Full MVI Implementation)

```kotlin
// feature/inventory/ui/InventoryIntent.kt
sealed interface InventoryIntent {
    object LoadProducts : InventoryIntent
    object Refresh : InventoryIntent
    data class MarkAsConsumed(val productId: Long) : InventoryIntent
    data class MarkAsExpired(val productId: Long) : InventoryIntent
    data class DeleteProduct(val productId: Long) : InventoryIntent
    data class SearchProducts(val query: String) : InventoryIntent
    data class FilterByCategory(val category: ProductCategory?) : InventoryIntent
    data class SortBy(val sortOrder: SortOrder) : InventoryIntent
}

enum class SortOrder {
    EXPIRY_DATE_ASC,
    EXPIRY_DATE_DESC,
    NAME_ASC,
    NAME_DESC,
    CREATED_DATE_DESC
}
```

```kotlin
// feature/inventory/ui/InventoryUiState.kt
sealed interface InventoryUiState {
    object Loading : InventoryUiState

    data class Success(
        val products: List<Product>,
        val filteredProducts: List<Product>,
        val searchQuery: String = "",
        val selectedCategory: ProductCategory? = null,
        val sortOrder: SortOrder = SortOrder.EXPIRY_DATE_ASC,
        val showExpiringOnly: Boolean = false
    ) : InventoryUiState {
        val displayProducts: List<Product>
            get() = filteredProducts.ifEmpty { products }
    }

    data class Error(val message: String) : InventoryUiState
}
```

```kotlin
// feature/inventory/ui/InventoryViewModel.kt
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getStockedProductsUseCase: GetStockedProductsUseCase,
    private val markProductAsConsumedUseCase: MarkProductAsConsumedUseCase,
    private val markProductAsExpiredUseCase: MarkProductAsExpiredUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<InventoryUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        processIntent(InventoryIntent.LoadProducts)
    }

    fun processIntent(intent: InventoryIntent) {
        when (intent) {
            is InventoryIntent.LoadProducts -> loadProducts()
            is InventoryIntent.Refresh -> loadProducts()
            is InventoryIntent.MarkAsConsumed -> markAsConsumed(intent.productId)
            is InventoryIntent.MarkAsExpired -> markAsExpired(intent.productId)
            is InventoryIntent.DeleteProduct -> deleteProduct(intent.productId)
            is InventoryIntent.SearchProducts -> searchProducts(intent.query)
            is InventoryIntent.FilterByCategory -> filterByCategory(intent.category)
            is InventoryIntent.SortBy -> sortProducts(intent.sortOrder)
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            getStockedProductsUseCase()
                .catch { e ->
                    _uiState.value = InventoryUiState.Error(
                        e.message ?: "Failed to load products"
                    )
                }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { products ->
                            _uiState.value = InventoryUiState.Success(
                                products = products,
                                filteredProducts = products
                            )
                        },
                        onFailure = { e ->
                            _uiState.value = InventoryUiState.Error(
                                e.message ?: "Failed to load products"
                            )
                        }
                    )
                }
        }
    }

    private fun markAsConsumed(productId: Long) {
        viewModelScope.launch {
            markProductAsConsumedUseCase(productId).fold(
                onSuccess = {
                    _uiEvent.send(InventoryUiEvent.ShowSnackbar("Product marked as consumed"))
                },
                onFailure = { e ->
                    _uiEvent.send(InventoryUiEvent.ShowError(e.message ?: "Failed to update"))
                }
            )
        }
    }

    private fun markAsExpired(productId: Long) {
        viewModelScope.launch {
            markProductAsExpiredUseCase(productId).fold(
                onSuccess = {
                    _uiEvent.send(InventoryUiEvent.ShowSnackbar("Product marked as expired"))
                },
                onFailure = { e ->
                    _uiEvent.send(InventoryUiEvent.ShowError(e.message ?: "Failed to update"))
                }
            )
        }
    }

    private fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            deleteProductUseCase(productId).fold(
                onSuccess = {
                    _uiEvent.send(InventoryUiEvent.ShowSnackbar("Product deleted"))
                },
                onFailure = { e ->
                    _uiEvent.send(InventoryUiEvent.ShowError(e.message ?: "Failed to delete"))
                }
            )
        }
    }

    private fun searchProducts(query: String) {
        val currentState = _uiState.value
        if (currentState is InventoryUiState.Success) {
            val filtered = if (query.isBlank()) {
                currentState.products
            } else {
                currentState.products.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.category.displayName.contains(query, ignoreCase = true)
                }
            }

            _uiState.value = currentState.copy(
                searchQuery = query,
                filteredProducts = filtered
            )
        }
    }

    private fun filterByCategory(category: ProductCategory?) {
        val currentState = _uiState.value
        if (currentState is InventoryUiState.Success) {
            val filtered = if (category == null) {
                currentState.products
            } else {
                currentState.products.filter { it.category == category }
            }

            _uiState.value = currentState.copy(
                selectedCategory = category,
                filteredProducts = filtered
            )
        }
    }

    private fun sortProducts(sortOrder: SortOrder) {
        val currentState = _uiState.value
        if (currentState is InventoryUiState.Success) {
            val sorted = when (sortOrder) {
                SortOrder.EXPIRY_DATE_ASC -> currentState.displayProducts.sortedBy { it.expiryDate }
                SortOrder.EXPIRY_DATE_DESC -> currentState.displayProducts.sortedByDescending { it.expiryDate }
                SortOrder.NAME_ASC -> currentState.displayProducts.sortedBy { it.name }
                SortOrder.NAME_DESC -> currentState.displayProducts.sortedByDescending { it.name }
                SortOrder.CREATED_DATE_DESC -> currentState.displayProducts.sortedByDescending { it.createdAt }
            }

            _uiState.value = currentState.copy(
                sortOrder = sortOrder,
                filteredProducts = sorted
            )
        }
    }
}

sealed interface InventoryUiEvent {
    data class ShowSnackbar(val message: String) : InventoryUiEvent
    data class ShowError(val message: String) : InventoryUiEvent
    data class NavigateToDetail(val productId: Long) : InventoryUiEvent
}
```

```kotlin
// feature/inventory/ui/InventoryScreen.kt
@Composable
fun InventoryScreen(
    onNavigateToAddItem: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is InventoryUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is InventoryUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is InventoryUiEvent.NavigateToDetail -> {
                    onNavigateToDetail(event.productId)
                }
            }
        }
    }

    InventoryContent(
        uiState = uiState,
        onIntent = viewModel::processIntent,
        onNavigateToAddItem = onNavigateToAddItem,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun InventoryContent(
    uiState: InventoryUiState,
    onIntent: (InventoryIntent) -> Unit,
    onNavigateToAddItem: () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddItem) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is InventoryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is InventoryUiState.Success -> {
                Column(modifier = Modifier.padding(paddingValues)) {
                    // Search bar
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { onIntent(InventoryIntent.SearchProducts(it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    // Filter chips
                    FilterChipsRow(
                        selectedCategory = uiState.selectedCategory,
                        onCategorySelected = { onIntent(InventoryIntent.FilterByCategory(it)) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Products list
                    if (uiState.displayProducts.isEmpty()) {
                        EmptyState(
                            message = if (uiState.searchQuery.isNotEmpty()) {
                                "No products match your search"
                            } else {
                                "No items in inventory\nTap + to add your first item"
                            },
                            icon = Icons.Default.Inventory
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.displayProducts,
                                key = { it.id }
                            ) { product ->
                                ProductCard(
                                    product = product,
                                    onItemClick = { /* Navigate */ },
                                    onMarkConsumed = { onIntent(InventoryIntent.MarkAsConsumed(it)) },
                                    onMarkExpired = { onIntent(InventoryIntent.MarkAsExpired(it)) },
                                    onDelete = { onIntent(InventoryIntent.DeleteProduct(it)) }
                                )
                            }
                        }
                    }
                }
            }

            is InventoryUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = { onIntent(InventoryIntent.Refresh) }
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search products...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun InventoryScreenPreview() {
    StockedTheme {
        InventoryContent(
            uiState = InventoryUiState.Success(
                products = listOf(
                    createPreviewProduct(name = "Milk", daysUntilExpiry = 2),
                    createPreviewProduct(name = "Eggs", daysUntilExpiry = 5)
                ),
                filteredProducts = emptyList()
            ),
            onIntent = {}
        )
    }
}
```

### 4.3 Add Item Feature (Complete MVI Flow)

```kotlin
// feature/additem/ui/AddItemIntent.kt
sealed interface AddItemIntent {
    data class UpdateName(val name: String) : AddItemIntent
    data class UpdateCategory(val category: ProductCategory) : AddItemIntent
    data class UpdateExpiryDate(val date: LocalDate) : AddItemIntent
    data class UpdateStatus(val status: ProductStatus) : AddItemIntent
    object SaveProduct : AddItemIntent
    object Cancel : AddItemIntent
}
```

```kotlin
// feature/additem/ui/AddItemUiState.kt
data class AddItemUiState(
    val name: String = "",
    val category: ProductCategory = ProductCategory.OTHER,
    val expiryDate: LocalDate = LocalDate.now().plusDays(1),
    val status: ProductStatus = ProductStatus.STOCKED,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val dateError: String? = null,
    val canSave: Boolean = false
) {
    companion object {
        fun initial() = AddItemUiState()
    }
}
```

```kotlin
// feature/additem/ui/AddItemViewModel.kt
@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemUiState.initial())
    val uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<AddItemUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun processIntent(intent: AddItemIntent) {
        when (intent) {
            is AddItemIntent.UpdateName -> updateName(intent.name)
            is AddItemIntent.UpdateCategory -> updateCategory(intent.category)
            is AddItemIntent.UpdateExpiryDate -> updateExpiryDate(intent.date)
            is AddItemIntent.UpdateStatus -> updateStatus(intent.status)
            is AddItemIntent.SaveProduct -> saveProduct()
            is AddItemIntent.Cancel -> cancel()
        }
    }

    private fun updateName(name: String) {
        _uiState.update { state ->
            state.copy(
                name = name,
                nameError = validateName(name),
                canSave = canSave(name, state.expiryDate)
            )
        }
    }

    private fun updateCategory(category: ProductCategory) {
        _uiState.update { it.copy(category = category) }
    }

    private fun updateExpiryDate(date: LocalDate) {
        _uiState.update { state ->
            state.copy(
                expiryDate = date,
                dateError = validateDate(date),
                canSave = canSave(state.name, date)
            )
        }
    }

    private fun updateStatus(status: ProductStatus) {
        _uiState.update { it.copy(status = status) }
    }

    private fun saveProduct() {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            addProductUseCase(
                name = state.name,
                category = state.category,
                expiryDate = state.expiryDate,
                status = state.status
            ).fold(
                onSuccess = {
                    _uiEvent.send(AddItemUiEvent.ProductSaved)
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.send(AddItemUiEvent.ShowError(e.message ?: "Failed to save"))
                }
            )
        }
    }

    private fun cancel() {
        viewModelScope.launch {
            _uiEvent.send(AddItemUiEvent.NavigateBack)
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Product name is required"
            name.length < 2 -> "Name must be at least 2 characters"
            else -> null
        }
    }

    private fun validateDate(date: LocalDate): String? {
        return when {
            date.isBefore(LocalDate.now()) -> "Expiry date must be in the future"
            else -> null
        }
    }

    private fun canSave(name: String, date: LocalDate): Boolean {
        return validateName(name) == null && validateDate(date) == null
    }
}

sealed interface AddItemUiEvent {
    object ProductSaved : AddItemUiEvent
    object NavigateBack : AddItemUiEvent
    data class ShowError(val message: String) : AddItemUiEvent
}
```

### Phase 4 Deliverables
- ✅ MVI architecture implemented for all features
- ✅ ViewModels with StateFlow and Channel
- ✅ Intent-based user interaction
- ✅ Single source of truth UI state
- ✅ Comprehensive unit tests for ViewModels
- ✅ Integration tests for complete flows

### Phase 4 Testing
```kotlin
// feature/inventory/ui/InventoryViewModelTest.kt
@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: InventoryViewModel
    private val getStockedProducts = mockk<GetStockedProductsUseCase>()
    private val markAsConsumed = mockk<MarkProductAsConsumedUseCase>()
    private val markAsExpired = mockk<MarkProductAsExpiredUseCase>()
    private val deleteProduct = mockk<DeleteProductUseCase>()

    @Before
    fun setup() {
        viewModel = InventoryViewModel(
            getStockedProducts,
            markAsConsumed,
            markAsExpired,
            deleteProduct
        )
    }

    @Test
    fun `initial state is loading`() = runTest {
        // When - viewModel is created
        // Then
        assertEquals(InventoryUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun `loadProducts success updates state`() = runTest {
        // Given
        val products = listOf(createTestProduct())
        coEvery { getStockedProducts() } returns flowOf(Result.success(products))

        // When
        viewModel.processIntent(InventoryIntent.LoadProducts)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is InventoryUiState.Success)
        assertEquals(products, (state as InventoryUiState.Success).products)
    }

    @Test
    fun `markAsConsumed emits success event`() = runTest {
        // Given
        coEvery { markAsConsumed(any()) } returns Result.success(Unit)

        // When
        viewModel.processIntent(InventoryIntent.MarkAsConsumed(1L))

        // Then
        viewModel.uiEvent.test {
            val event = awaitItem()
            assertTrue(event is InventoryUiEvent.ShowSnackbar)
        }
    }
}
```

---

## Phase 5: Advanced Features & Optimization

**Duration:** 1-2 weeks
**Goal:** Add modern Android features and optimize performance

### 5.1 WorkManager for Notifications

```kotlin
// core/notification/ExpiryCheckWorker.kt
@HiltWorker
class ExpiryCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val productRepository: ProductRepository,
    private val notificationManager: StockedNotificationManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            productRepository.getExpiringProducts()
                .first()
                .getOrNull()
                ?.let { products ->
                    if (products.isNotEmpty()) {
                        notificationManager.showExpiringItemsNotification(products)
                    }
                }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "expiry_check_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()

            val request = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.DAYS
            )
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}
```

### 5.2 DataStore for Preferences

```kotlin
// core/datastore/UserPreferences.kt
data class UserPreferences(
    val theme: Theme = Theme.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val expiryWarningDays: Int = 3,
    val defaultSortOrder: SortOrder = SortOrder.EXPIRY_DATE_ASC
)

enum class Theme {
    LIGHT, DARK, SYSTEM
}

// core/datastore/PreferencesDataSource.kt
class PreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    val userPreferences: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                theme = Theme.valueOf(
                    preferences[THEME_KEY] ?: Theme.SYSTEM.name
                ),
                notificationsEnabled = preferences[NOTIFICATIONS_KEY] ?: true,
                expiryWarningDays = preferences[WARNING_DAYS_KEY] ?: 3,
                defaultSortOrder = SortOrder.valueOf(
                    preferences[SORT_ORDER_KEY] ?: SortOrder.EXPIRY_DATE_ASC.name
                )
            )
        }

    suspend fun updateTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        private val WARNING_DAYS_KEY = intPreferencesKey("expiry_warning_days")
        private val SORT_ORDER_KEY = stringPreferencesKey("default_sort_order")
    }
}
```

### 5.3 Performance Optimizations

```kotlin
// Use derivedStateOf for computed values
@Composable
fun InventoryContent(uiState: InventoryUiState.Success) {
    val sortedProducts by remember(uiState.products, uiState.sortOrder) {
        derivedStateOf {
            when (uiState.sortOrder) {
                SortOrder.EXPIRY_DATE_ASC -> uiState.products.sortedBy { it.expiryDate }
                else -> uiState.products
            }
        }
    }
}

// LazyColumn keys for better performance
LazyColumn {
    items(
        items = products,
        key = { product -> product.id }  // Stable key
    ) { product ->
        ProductCard(product)
    }
}

// Baseline profiles for startup optimization
// Create baseline-prof.txt with:
// HSPLcom/anshulvyas/stocked/StockedApplication;-><init>()V
// HSPLcom/anshulvyas/stocked/MainActivity;->onCreate(Landroid/os/Bundle;)V
```

### Phase 5 Deliverables
- ✅ WorkManager for background notifications
- ✅ DataStore for user preferences
- ✅ Settings screen with theme switcher
- ✅ Baseline profiles for startup optimization
- ✅ R8 optimization and ProGuard rules
- ✅ Performance monitoring with Firebase Performance
- ✅ Crash reporting with Firebase Crashlytics

---

## Testing Strategy

### Unit Tests (Target: 80% coverage)

```kotlin
// Repository tests
class ProductRepositoryImplTest {
    // Test CRUD operations
    // Test error handling
    // Test Flow emissions
}

// ViewModel tests
class InventoryViewModelTest {
    // Test intent processing
    // Test state updates
    // Test event emissions
}

// Use case tests
class AddProductUseCaseTest {
    // Test business logic
    // Test validation
}
```

### Integration Tests

```kotlin
// Database tests
@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: StockedDatabase

    @Test
    fun insertAndRetrieveProduct() = runTest {
        // Test Room operations
    }
}
```

### UI Tests (Compose)

```kotlin
@RunWith(AndroidJUnit4::class)
class InventoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysProducts() {
        // Test UI rendering
    }

    @Test
    fun clickingFabNavigatesToAddScreen() {
        // Test navigation
    }
}
```

### E2E Tests

```kotlin
@RunWith(AndroidJUnit4::class)
class AddProductE2ETest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Test
    fun addProduct_appearsInInventory() {
        // Test complete user flow
    }
}
```

---

## Risk Management

### Technical Risks

| Risk | Impact | Probability | Mitigation |
|------|--------|-------------|------------|
| Data migration failure | High | Medium | Extensive testing, backup strategy |
| Performance regression | Medium | Low | Baseline metrics, performance tests |
| Breaking existing functionality | High | Medium | Comprehensive test suite |
| Learning curve (Kotlin/Compose) | Low | High | Training, code reviews |
| Scope creep | Medium | Medium | Strict phase deliverables |

### Migration Risks

**Data Loss Prevention:**
- Automated backup before migration
- Rollback plan with database downgrade
- Beta testing with test users
- Staged rollout (10% → 50% → 100%)

**Rollback Strategy:**
```kotlin
// Database downgrade support
override fun onDowngrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
    // Safely downgrade schema if needed
}
```

**Feature Flags:**
```kotlin
object FeatureFlags {
    const val USE_NEW_UI = BuildConfig.DEBUG  // Toggle Compose UI
    const val USE_ROOM = true  // Toggle Room vs SQLite
}
```

---

## Success Metrics

### Technical Metrics

- **Code Quality:**
  - 80%+ test coverage
  - 0 critical SonarQube issues
  - Detekt/ktlint violations < 10

- **Performance:**
  - App startup < 1.5s (was 2.5s)
  - Frame drops < 5% (target 60fps)
  - Database query time < 50ms

- **Build Metrics:**
  - Clean build time < 2 minutes
  - Incremental build < 15 seconds

### Business Metrics

- **User Satisfaction:**
  - Play Store rating > 4.5
  - Crash-free sessions > 99.5%
  - User retention +20%

- **Development Velocity:**
  - New feature development -30% time
  - Bug fix time -40%
  - Code review time -25%

---

## Timeline Summary

```
Week 1-2:   Phase 1 - Foundation
Week 3-5:   Phase 2 - Data Layer
Week 6-9:   Phase 3 - Compose UI
Week 10-12: Phase 4 - MVI Architecture
Week 13-14: Phase 5 - Polish & Optimization
Week 15:    Testing & Bug Fixes
Week 16:    Beta Release & Monitoring
```

---

## Next Steps

1. **Immediate Actions:**
   - [ ] Get stakeholder approval
   - [ ] Set up development branch
   - [ ] Create project board for tracking
   - [ ] Schedule kickoff meeting

2. **Phase 1 Preparation:**
   - [ ] Backup production database
   - [ ] Set up CI/CD pipeline
   - [ ] Configure code analysis tools
   - [ ] Create version catalog file

3. **Team Preparation:**
   - [ ] Kotlin training sessions
   - [ ] Compose workshop
   - [ ] MVI architecture presentation
   - [ ] Code review guidelines

---

## Conclusion

This migration represents a comprehensive modernization of the Stocked! application, transforming it from a legacy 2018-era codebase to a state-of-the-art Android application. The phased approach ensures:

- **Zero downtime** during migration
- **Incremental value** delivery
- **Risk mitigation** through testing and rollback strategies
- **Knowledge transfer** through documentation and code reviews

The end result will be a maintainable, performant, and future-proof application that leverages the latest Android best practices and provides an exceptional user experience.

**Recommended Decision:** Proceed with Phase 1 - Foundation & Setup

---

*Document prepared by: Senior Android Architect*
*Last updated: November 19, 2025*
