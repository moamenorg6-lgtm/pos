# Restaurant POS - نظام نقاط البيع للمطاعم

A complete bilingual (Arabic/English) Restaurant Point of Sale Android application built with Kotlin and Jetpack Compose.

## Project Overview

This is a modern Android POS system designed specifically for restaurants, featuring:
- **Bilingual Support**: Full Arabic and English localization
- **Modern Architecture**: Built with Jetpack Compose, Hilt, and Room
- **Modular Design**: Clean architecture with separation of concerns
- **Real-time Operations**: Optimized for restaurant workflow

## Features (Phase 1 - Foundation)

- ✅ **Modern UI**: Jetpack Compose with Material3 design
- ✅ **Dependency Injection**: Hilt for clean dependency management
- ✅ **Local Database**: Room database with sample entities
- ✅ **Navigation**: Compose Navigation with multiple screens
- ✅ **Bilingual Support**: Arabic and English string resources
- ✅ **CI/CD**: GitHub Actions for automated builds and testing

## Tech Stack

- **Language**: Kotlin 1.9.10
- **UI Framework**: Jetpack Compose (BOM 2023.10.01)
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt 2.48
- **Database**: Room 2.6.1
- **Navigation**: Navigation Compose 2.7.5
- **Build System**: Gradle with Kotlin DSL
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

## Project Structure

```
app/
├── src/main/java/com/company/restaurantpos/
│   ├── data/
│   │   ├── database/          # Room database and DAOs
│   │   └── entity/            # Database entities
│   ├── di/                    # Hilt dependency injection modules
│   ├── ui/
│   │   ├── navigation/        # Navigation components
│   │   ├── screens/           # Compose screens
│   │   └── theme/             # UI theme and styling
│   ├── MainActivity.kt        # Main activity with Compose setup
│   └── RestaurantPOSApplication.kt  # Application class
└── src/main/res/
    ├── values/                # English strings and resources
    ├── values-ar/             # Arabic strings and resources
    └── ...                    # Other Android resources
```

## Setup Instructions

### Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: 17 or later
- **Android SDK**: API level 34
- **Gradle**: 8.2 (included via wrapper)

### Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/moamenorg3-hue/pos.git
   cd pos
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository folder
   - Wait for Gradle sync to complete

3. **Build the project**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run tests**:
   ```bash
   ./gradlew test
   ```

5. **Install on device/emulator**:
   - Connect an Android device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

## Build Commands

| Command | Description |
|---------|-------------|
| `./gradlew assembleDebug` | Build debug APK |
| `./gradlew assembleRelease` | Build release APK |
| `./gradlew test` | Run unit tests |
| `./gradlew connectedAndroidTest` | Run instrumented tests |
| `./gradlew clean` | Clean build artifacts |
| `./gradlew installDebug` | Install debug APK on connected device |

## Running on Emulator

1. **Create an AVD** (Android Virtual Device):
   - Open Android Studio
   - Go to Tools → AVD Manager
   - Create a new virtual device with API level 24 or higher

2. **Start the emulator**:
   - Select your AVD and click "Play"
   - Wait for the emulator to boot completely

3. **Run the app**:
   - In Android Studio, click the "Run" button
   - Or use command line: `./gradlew installDebug`

## Current Screens

The application currently includes placeholder screens for:

- **Home Screen** (`/home`) - Welcome and navigation hub
- **POS Screen** (`/pos`) - Point of sale operations (placeholder)
- **Kitchen Screen** (`/kitchen`) - Kitchen display system (placeholder)
- **Admin Screen** (`/admin`) - Administration panel (placeholder)

## Language Support

The app supports both Arabic and English:
- **English**: Default language with left-to-right layout
- **Arabic**: Full RTL support with localized strings
- **Dynamic**: Automatically switches based on device language settings

To test Arabic language:
1. Change device language to Arabic in Settings
2. Restart the app to see Arabic interface

## Development Workflow

### Adding New Features

1. Create feature branch: `git checkout -b feature/your-feature-name`
2. Implement changes following the existing architecture
3. Add appropriate tests
4. Update documentation if needed
5. Create pull request

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused
- Use dependency injection via Hilt

## CI/CD

The project includes GitHub Actions workflow (`.github/workflows/android-ci.yml`) that:
- Builds debug APK on every push/PR
- Runs unit tests
- Uploads build artifacts
- Caches Gradle dependencies for faster builds

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Roadmap

### Phase 2 - Core POS Features
- Product catalog management
- Order processing
- Payment handling
- Receipt generation

### Phase 3 - Kitchen Integration
- Order queue management
- Kitchen display system
- Order status tracking

### Phase 4 - Advanced Features
- Inventory management
- Reporting and analytics
- Multi-location support
- Cloud synchronization

---

**Note**: This is Phase 1 (foundation) of the Restaurant POS system. Business logic and advanced features will be implemented in subsequent phases.
