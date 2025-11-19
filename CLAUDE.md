# CLAUDE.md - AI Assistant Guide for Stocked!

## Project Overview

**Stocked!** is an Android native mobile application for grocery and food inventory management. It helps users track grocery items, monitor expiration dates, manage shopping lists, and receive notifications for items about to expire.

- **Package:** `com.anshulvyas.csc780.grocerymanagr`
- **Platform:** Android (Min SDK 16, Target SDK 28, Compile SDK 28)
- **Language:** Java
- **Database:** SQLite (Fooded.db, version 5)
- **Status:** Published on [Google Play Store](https://play.google.com/store/apps/details?id=com.anshulvyas.csc780.grocerymanagr&hl=en)

---

## Repository Structure

```
/home/user/stocked/
├── app/                              # Main application module
│   ├── build.gradle                  # App-level build configuration
│   ├── proguard-rules.pro           # ProGuard obfuscation rules
│   └── src/main/
│       ├── java/com/anshulvyas/csc780/grocerymanagr/
│       │   ├── Activities/          # 4 Activity classes
│       │   ├── Fragments/           # 3 Fragment classes
│       │   ├── Adapters/            # 4 Adapter classes
│       │   ├── Model/               # Data layer (DAO, DBManager, etc.)
│       │   ├── Product.java         # Core data model
│       │   └── StockedApp.java      # Application class
│       ├── res/                     # Android resources
│       │   ├── layout/              # 11 XML layout files
│       │   ├── values/              # Strings, colors, styles, arrays, dimensions
│       │   ├── drawable-*/          # Multi-density drawables
│       │   └── mipmap-*/            # App icons
│       └── AndroidManifest.xml      # App manifest
├── build.gradle                      # Project-level build configuration
├── settings.gradle                   # Gradle settings
├── gradle.properties                 # Gradle properties
├── fastlane/                        # CI/CD automation
│   ├── Appfile
│   └── Fastfile                     # Deployment lanes (test, beta, deploy)
├── Documents/DesignDocument/        # Design documentation
├── Screens/                         # App screenshots (6 images)
├── README.md                        # Project documentation
└── LICENSE                          # Project license
```

---

## Architecture & Design Patterns

### MVC Architecture
The app follows a **Model-View-Controller** pattern with clear separation of concerns:

- **Model Layer** (`app/src/main/java/.../Model/`)
  - `Product.java` - Core data model (implements Parcelable)
  - `ProductDAO.java` - Data Access Object for database queries
  - `DBManager.java` - Database operations facade
  - `DBOpenHelper.java` - SQLite database helper
  - `ProductTable.java` - Database schema definitions

- **View Layer**
  - Activities: SplashActivity, HomeActivity, AddItemActivity, ShoppingItemActivity
  - Fragments: HomeFragment, ShoppingListFragment, TimelineFragment
  - Layouts: 11 XML layout files in `app/src/main/res/layout/`

- **Controller Layer**
  - Activities and Fragments handle UI logic
  - Adapters: PagerAdapter, ProductAdapter, ShoppingAdapter, TimelineAdapter

### Key Components

#### Activities (Entry Points)
1. **SplashActivity** (`app/src/main/java/.../Activities/SplashActivity.java:1`) - Launch screen
2. **HomeActivity** (`app/src/main/java/.../Activities/HomeActivity.java:1`) - Main container with TabLayout navigation
3. **AddItemActivity** (`app/src/main/java/.../Activities/AddItemActivity.java:1`) - Add/edit grocery items
4. **ShoppingItemActivity** (`app/src/main/java/.../Activities/ShoppingItemActivity.java:1`) - Shopping list item management

#### Fragments (UI Screens)
1. **HomeFragment** (`app/src/main/java/.../Fragments/HomeFragment.java:1`) - Track stocked items with expiration countdown
2. **ShoppingListFragment** (`app/src/main/java/.../Fragments/ShoppingListFragment.java:1`) - Manage shopping list
3. **TimelineFragment** (`app/src/main/java/.../Fragments/TimelineFragment.java:1`) - View item history

---

## Database Schema

### Product Table
**Database:** `Fooded.db` (SQLite, version 5)
**Table Name:** `product`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `_id` | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique identifier |
| `productName` | TEXT | NOT NULL | Name of the grocery item |
| `productCategory` | TEXT | NOT NULL | Category (Frozen, Vegetables, Dairy, etc.) |
| `expiryIn` | TEXT | NOT NULL | Expiry date/days remaining |
| `shoppingCheck` | BOOLEAN | - | Flag for shopping list items |
| `stocked` | BOOLEAN | - | Flag for stocked items |
| `consumed` | BOOLEAN | - | Flag for consumed items |
| `expired` | BOOLEAN | - | Flag for expired items |

**Note:** The database uses boolean flags to track item states across different views (stocked, shopping list, timeline).

---

## Dependencies & Libraries

### Core Dependencies (from `app/build.gradle`)
```gradle
// Android Support Libraries (API 28 - Legacy)
implementation 'com.android.support:appcompat-v7:28.0.0'
implementation 'com.android.support:design:28.0.0'
implementation 'com.android.support:cardview-v7:28.0.0'
implementation 'com.android.support:palette-v7:28.0.0'

// Date/Time Library
implementation 'net.danlew:android.joda:2.9.0'
```

### Key Libraries
- **Android Support Library v28** - Legacy support library (pre-AndroidX)
- **Joda Time** - Date/time calculations for expiry date countdown
- **Material Design Components** - TabLayout, FloatingActionButton, CardView

**⚠️ Important:** This project uses **legacy Android Support Libraries** (not AndroidX). When adding new dependencies or making changes, ensure compatibility with API level 28 and avoid AndroidX libraries.

---

## Development Setup

### Prerequisites
1. **Android Studio** (Arctic Fox or later recommended)
2. **Android SDK 28** (Android 9.0 Pie)
3. **JDK 8** or later
4. **Gradle 4.x+** (managed via Gradle wrapper)

### Installation Steps
```bash
# Clone the repository
git clone https://github.com/av-7/stocked.git
cd stocked

# Open in Android Studio
# File > Open > Select /home/user/stocked

# Sync Gradle dependencies
# Android Studio will prompt to sync

# Build and run
./gradlew assembleDebug
# Or use Android Studio's Run button
```

### Build Configuration
- **Build Tools Version:** 28.0.3
- **compileSdkVersion:** 28
- **minSdkVersion:** 16 (Android 4.1 Jelly Bean)
- **targetSdkVersion:** 28 (Android 9.0 Pie)
- **Android Gradle Plugin:** 3.2.1

---

## Key Conventions & Patterns

### 1. Code Organization
- **Package structure:** `com.anshulvyas.csc780.grocerymanagr.<component>`
- **Component folders:** Activities, Fragments, Adapters, Model
- **Naming conventions:**
  - Activities: `*Activity.java` (e.g., `HomeActivity.java`)
  - Fragments: `*Fragment.java` (e.g., `HomeFragment.java`)
  - Adapters: `*Adapter.java` (e.g., `ProductAdapter.java`)
  - Layouts: `activity_*.xml`, `fragment_*.xml`, `item_*.xml`

### 2. Data Flow Pattern
```
UI (Activity/Fragment)
  ↓
Adapter (for ListView display)
  ↓
DBManager (facade)
  ↓
ProductDAO (database operations)
  ↓
SQLite Database
```

### 3. Product Model (Parcelable)
The `Product` class implements `Parcelable` for efficient data passing between Activities:
```java
// Pass product data between activities
Intent intent = new Intent(context, TargetActivity.class);
intent.putExtra("product", productObject);
startActivity(intent);
```

### 4. Database Operations
Always use `DBManager` or `ProductDAO` for database operations:
```java
// Get database manager instance
DBManager dbManager = new DBManager(context);
dbManager.open();

// Perform operations via ProductDAO
ProductDAO productDAO = new ProductDAO(context);
List<Product> products = productDAO.getAllStockedProducts();

// Close when done
dbManager.close();
```

### 5. Boolean Flags System
The app uses boolean flags to track product states:
- `stocked = true` → Item appears in Home tab
- `shoppingCheck = true` → Item appears in Shopping List tab
- `consumed = true` or `expired = true` → Item appears in Timeline tab

**When modifying items, update flags appropriately to ensure correct view filtering.**

---

## Common Development Workflows

### Adding a New Activity
1. Create new Java class extending `AppCompatActivity` in `Activities/` folder
2. Create corresponding layout XML in `res/layout/`
3. Register activity in `AndroidManifest.xml`
4. Follow naming convention: `NewFeatureActivity.java` + `activity_new_feature.xml`

### Adding a New Fragment
1. Create new Java class extending `Fragment` in `Fragments/` folder
2. Create corresponding layout XML in `res/layout/`
3. Add fragment to parent Activity (usually via ViewPager or FragmentTransaction)
4. Follow naming convention: `NewFeatureFragment.java` + `fragment_new_feature.xml`

### Modifying Database Schema
1. Update `ProductTable.java` with new column definitions
2. Increment database version in `DBOpenHelper.java`
3. Implement `onUpgrade()` migration logic in `DBOpenHelper.java`
4. Update `ProductDAO.java` with new query methods
5. Update `Product.java` model class with new fields

### Adding New String Resources
Add to `app/src/main/res/values/strings.xml`:
```xml
<string name="new_string_key">String value</string>
```
Access via: `getString(R.string.new_string_key)`

### Testing & Building
```bash
# Run tests
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install on connected device
./gradlew installDebug
```

---

## CI/CD with Fastlane

### Available Lanes
Located in `fastlane/Fastfile`:

```bash
# Run tests
fastlane test

# Deploy to beta (internal testing)
fastlane beta

# Deploy to production (Play Store)
fastlane deploy
```

### Configuration Files
- `fastlane/Appfile` - App package configuration
- `fastlane/Fastfile` - Lane definitions
- `Gemfile` - Ruby dependencies for Fastlane

---

## Git Workflow & Branching

### Branch Naming Convention
- Feature branches: `feature/<feature-name>`
- Bug fixes: `bugfix/<bug-description>`
- Releases: `release/<version>`
- AI-assisted development: `claude/<session-id>`

### Current Development Branch
**Active Branch:** `claude/claude-md-mi59ktaq8eysdo2r-01Vi6YQH9CvmkeEyR5HSBGem`

### Commit Message Guidelines
- Use conventional commits format:
  - `feat:` - New feature
  - `fix:` - Bug fix
  - `refactor:` - Code refactoring
  - `docs:` - Documentation changes
  - `test:` - Test additions/modifications
  - `chore:` - Build process or auxiliary tool changes

Example: `feat: Add notification settings screen`

---

## AI Assistant Guidelines

### When Making Code Changes

1. **Preserve Legacy Code Style**
   - This is a Java-based Android project (not Kotlin)
   - Uses legacy Support Libraries, not AndroidX
   - Maintain existing code formatting and style

2. **Database Changes Require Caution**
   - Always increment database version
   - Implement proper migration in `onUpgrade()`
   - Test on emulator before deploying

3. **Resource Management**
   - Add new strings to `strings.xml`, not hardcoded
   - Use existing colors from `colors.xml`
   - Follow existing drawable naming conventions

4. **Testing**
   - Run `./gradlew assembleDebug` to verify builds
   - Test on emulator with API 28 or lower
   - Verify database migrations work correctly

5. **Dependencies**
   - Use Support Library versions, not AndroidX
   - Check compatibility with minSdkVersion 16
   - Avoid adding heavy dependencies (app size consideration)

### Common Tasks

#### Task: Add a new feature to track nutrition info
```
1. Update ProductTable.java with new columns
2. Modify Product.java model to include nutrition fields
3. Update ProductDAO.java with new query methods
4. Create/update UI layouts for nutrition input
5. Update Adapters to display nutrition info
6. Increment database version and handle migration
7. Test thoroughly on emulator
```

#### Task: Fix a bug in expiry date calculation
```
1. Locate the issue (likely in HomeFragment.java or ProductDAO.java)
2. Check Joda Time usage for date calculations
3. Fix the calculation logic
4. Test with various date scenarios
5. Verify notifications still work correctly
```

#### Task: Update UI styling
```
1. Modify res/values/colors.xml for color changes
2. Update res/values/styles.xml for style changes
3. Modify individual layout XMLs as needed
4. Test on multiple screen densities
5. Ensure Material Design guidelines are followed
```

### Files to Avoid Modifying (Unless Necessary)
- `gradle/wrapper/` - Gradle wrapper configuration
- `.gitignore` - Git ignore rules
- `LICENSE` - Project license
- `proguard-rules.pro` - Unless adding new library requiring ProGuard rules

### Important File Paths Reference
- Main source: `app/src/main/java/com/anshulvyas/csc780/grocerymanagr/`
- Layouts: `app/src/main/res/layout/`
- Resources: `app/src/main/res/values/`
- Manifest: `app/src/main/AndroidManifest.xml`
- App Gradle: `app/build.gradle`
- Project Gradle: `build.gradle`

---

## Resources & Documentation

### Internal Documentation
- **README.md** - Project overview and setup instructions
- **Design Document** - `Documents/DesignDocument/DesignDocumentCSC_780.pdf`
- **Screenshots** - `Screens/Screen1.jpg` through `Screen6.jpg`

### External Resources
- [GitHub Wiki](https://github.com/av-7/stocked/wiki) - JOURNAL and QA Test Plans
- [Google Play Store](https://play.google.com/store/apps/details?id=com.anshulvyas.csc780.grocerymanagr)
- [Privacy Policy](http://av-7.github.io/stocked/)

### Android API Documentation
- [Android SDK 28 Reference](https://developer.android.com/sdk/api_level/28)
- [Support Library 28.0.0](https://developer.android.com/topic/libraries/support-library)
- [SQLite in Android](https://developer.android.com/training/data-storage/sqlite)
- [Joda Time for Android](https://github.com/dlew/joda-time-android)

---

## Project Statistics

- **Total Lines of Code:** ~1,870 (Java)
- **Number of Classes:** 17
- **Activities:** 4
- **Fragments:** 3
- **Adapters:** 4
- **Layout Files:** 11
- **Supported Android Versions:** 4.1 (API 16) to 9.0 (API 28)+
- **App Size:** Optimized for minimal footprint

---

## Troubleshooting

### Build Errors
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug

# If Gradle sync fails, try:
# File > Invalidate Caches / Restart in Android Studio
```

### Database Issues
- Database location: `/data/data/com.anshulvyas.csc780.grocerymanagr/databases/Fooded.db`
- Use Android Studio Database Inspector to view database
- Clear app data to reset database: `adb shell pm clear com.anshulvyas.csc780.grocerymanagr`

### Dependency Resolution
- Ensure you're using jcenter() and google() repositories (defined in project `build.gradle`)
- Check that Android SDK 28 is installed via SDK Manager

---

## Version History

| Version | Changes | Date |
|---------|---------|------|
| Current | Added Fastlane CI/CD, updated .gitignore | Recent |
| Previous | Restructured project, updated targetSDK to 28 | Recent |
| v1.0 | Initial Play Store release | Original |

---

## Contact & Support

- **GitHub Repository:** https://github.com/av-7/stocked
- **Issues:** https://github.com/av-7/stocked/issues
- **Original Author:** Anshul Vyas
- **Package:** com.anshulvyas.csc780.grocerymanagr

---

**Last Updated:** 2025-11-19
**CLAUDE.md Version:** 1.0
**Target Audience:** AI Assistants (Claude Code), Developers

---

## Quick Reference Commands

```bash
# Build commands
./gradlew clean assembleDebug
./gradlew assembleRelease
./gradlew test

# Fastlane commands
fastlane test
fastlane beta
fastlane deploy

# Git workflow
git checkout -b feature/new-feature
git add .
git commit -m "feat: Add new feature"
git push -u origin feature/new-feature

# ADB commands
adb devices
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb logcat | grep "GroceryManagr"
```

---

*This document is maintained for AI assistants to effectively understand and work with the Stocked! Android application codebase.*
