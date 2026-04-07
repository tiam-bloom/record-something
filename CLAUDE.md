# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run on connected device
./gradlew installDebug

# Clean and build
./gradlew clean assembleDebug

# Sync Gradle (in Android Studio: Ctrl+Shift+S)
./gradlew
```

## Architecture

### Module Structure
- **`app/`** - Application module (UI/presentation layer)
- **`core/`** - Core library module (data/domain layer, shared dependencies)

### Architecture Pattern
Clean Architecture with MVVM:
- **UI Layer** (`app/src/main/java/.../ui/`) - Jetpack Compose screens and components
- **ViewModel Layer** (`app/src/main/java/.../viewmodel/`) - State management, exposes StateFlow
- **Domain Layer** (`core/src/main/java/.../domain/`) - Business logic, use cases, domain models
- **Data Layer** (`core/src/main/java/.../data/`) - Room database, DAOs, repositories

```
app module:
  ui/pages/     - Compose screens (home, assets, statistics, settings)
  ui/navigation/ - Navigation graph
  ui/theme/     - Material 3 theme (Color.kt, Theme.kt, Type.kt)
  viewmodel/    - ViewModels for each feature

core module:
  data/entity/  - Room entities (AssetEntity, CategoryEntity, DepreciationRecordEntity)
  data/dao/     - Room DAOs
  data/repository/ - Repository implementations + mappers
  domain/model/ - Domain models (Asset, Category, etc.)
  domain/usecase/ - Business logic (CalculateDepreciationUseCase, GetAssetStatisticsUseCase)
  di/           - Hilt dependency injection modules
```

### Key Dependencies
- **Jetpack Compose** with Material 3 for UI
- **Room** for local SQLite database
- **Hilt** for dependency injection
- **Navigation Compose** for screen navigation
- **MPAndroidChart** for statistics charts
- **Kotlin Coroutines + Flow** for async operations

### Database Tables
- `assets` - Asset records (name, category_id, purchase_date, purchase_price, current_value, depreciation_method)
- `categories` - Asset categories (name, icon, color, default_depreciation_rate)
- `depreciation_records` - Daily value history for charts

### Depreciation Methods
Supported in `CalculateDepreciationUseCase`: straight-line, double-declining balance, sum-of-years-digits

## Project Configuration
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35
- **JVM Target**: 17
- **Kotlin**: 2.0.21 with Compose plugin
- **Hilt**: 2.51.1 (app and core must use the same version)

## Important Notes

### Multi-module Hilt Configuration
The `core` module contains `DatabaseModule` (Hilt DI module) and `@Inject` annotated classes (`AssetRepository`, `CalculateDepreciationUseCase`). The `app` module's ViewModels depend on these. **All modules must use the same Hilt version** to avoid `NoSuchMethodException` at runtime.

### SDK Versions
This project uses `compileSdk = 35` which requires Android SDK Platform 35 and Build-Tools 35.x. Ensure your Android SDK has these installed.
