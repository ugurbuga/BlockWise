# BlockWise

BlockWise is a **Compose Multiplatform** puzzle game targeting Android, iOS, and Desktop (JVM).

## Gameplay

- Place **colored pieces with different shapes** onto a square grid.
- When a **row or column becomes full**, it is cleared (except locked cells) and you score points.
- Difficulty can enable additional constraints like color limits, adjacency limits, variety requirements, and move limits.

## Rules & Difficulty System

The core rule set is represented by `GameRules` and is resolved per mode via `resolveGameConfig(gridSize, difficulty)`.

Depending on the selected difficulty, the game can enable or tune:

- `maxSameColorPerRow` / `maxSameColorPerCol`
  - Limits how many cells of the same color can appear in any full/partial row or column.
  - Computed from a difficulty ratio and the selected `gridSize`.
- `maxAdjacentSameColor`
  - Limits consecutive adjacent cells of the same color in rows and columns.
- `minDistinctColorsInFullLine`
  - Requires a minimum number of distinct colors in a fully filled row/column.
- `moveLimit`
  - A maximum number of moves for the session (enabled in harder difficulties).

The difficulty configuration also affects the starting board:

- `preFilledRatio`
  - Some cells are pre-filled to shape the opening.
- `lockedCellsRatio`
  - A portion of pre-filled cells can be locked (they cannot be cleared).

In addition, the piece pool is constrained by difficulty (e.g., maximum shape dimension / complexity).

## Features

- **Multi-platform**
  - Android
  - iOS
  - Desktop (JVM)
- **Game modes**
  - Grid sizes: `8x8`, `10x10`, `12x12`, `14x14`
  - Difficulty: `Easy`, `Normal`, `Hard`, `VeryHard`
- **Visual customization**
  - Theme mode: `System`, `Light`, `Dark`
  - Theme color palettes: `Classic`, `Aurora`, `Sunset`
  - Block color palettes: `Classic`, `Candy`, `Neon`, `Earth`
  - Block styles: `Flat`, `Bubble`, `Outline`, `Sharp 3D`, `Wood`, `Liquid Glass`, `Neon`
  - **Animated Neon border** with adjustable pulse speed (`Slow`, `Normal`, `Fast`)
  - **Block gap spacing**: `None`, `Low`, `High`
  - **Board block style modes**: `Always Flat`, `Match Selected Block Style`
- **Interaction customization**
  - **Invalid placement feedback**: `While Dragging`, `On Drop`
  - **Drag finger offset levels**: `None`, `Low`, `Medium`, `High`
- **Rules screen**
  - Shows active rules (limits) for the currently selected mode.
- **Scores**
  - Stores best scores per mode.
- **Settings**
  - Language selection across English, Turkish, Spanish, French, German, Russian, and Arabic
  - Polished chip-based previews for theme palettes, block palettes, and block styles
  - Real-time preview of all visual customizations
- **Controls**
  - Piece selection, drag & drop placement, and invalid-move feedback.

## Tech Stack

- **Kotlin Multiplatform (KMP)** with Kotlin 2.3.20
- **Compose Multiplatform 1.10.3** + **Material 3**
- **Decompose 3.5.0** (navigation)
- **kotlinx.serialization 1.10.0**
- **Android Gradle Plugin 9.1.0**
- **AndroidX Lifecycle 2.10.0**, **AndroidX Core 1.18.0**, **AndroidX Activity 1.13.0**

## Project Structure

- `composeApp/`
  - `src/commonMain/` shared UI and game logic
  - `src/androidMain/`, `src/iosMain/`, `src/jvmMain/` platform-specific adapters
- `iosApp/` iOS entry point (Xcode project)
- `screenshots/` screenshots used in the README

## Run

### Android

```sh
./gradlew :composeApp:assembleDebug
```

### Desktop (JVM)

```sh
./gradlew :composeApp:run
```

### iOS

- Open `iosApp/` in Xcode
- Run on a simulator / device

Alternatively, you can build from the terminal:

Build the KMP iOS framework (shared module):

```sh
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

Build the iOS app with Xcode tooling:

```sh
xcodebuild \
  -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  build
```

## Screenshots

> Note: screenshot assets are stored in `screenshots/`. If visuals change, the README images should be recaptured manually.

### Android

| Screen | Light | Dark |
| --- | --- | --- |
| Selection | <img src="./screenshots/android-light-selection.png" alt="android-light-selection" width="180" /> | <img src="./screenshots/android-dark-selection.png" alt="android-dark-selection" width="180" /> |
| Game | <img src="./screenshots/android-light-game.png" alt="android-light-game" width="180" /> | <img src="./screenshots/android-dark-game.png" alt="android-dark-game" width="180" /> |
| Rules | <img src="./screenshots/android-light-rules.png" alt="android-light-rules" width="180" /> | <img src="./screenshots/android-dark-rules.png" alt="android-dark-rules" width="180" /> |
| Scores | <img src="./screenshots/android-light-scores.png" alt="android-light-scores" width="180" /> | <img src="./screenshots/android-dark-scores.png" alt="android-dark-scores" width="180" /> |
| Settings | <img src="./screenshots/android-light-settings.png" alt="android-light-settings" width="180" /> | <img src="./screenshots/android-dark-settings.png" alt="android-dark-settings" width="180" /> |

### iOS

| Screen | Light | Dark |
| --- | --- | --- |
| Selection | <img src="./screenshots/ios-light-selection.png" alt="ios-light-selection" width="180" /> | <img src="./screenshots/ios-dark-selection.png" alt="ios-dark-selection" width="180" /> |
| Game | <img src="./screenshots/ios-light-game.png" alt="ios-light-game" width="180" /> | <img src="./screenshots/ios-dark-game.png" alt="ios-dark-game" width="180" /> |
| Rules | <img src="./screenshots/ios-light-rules.png" alt="ios-light-rules" width="180" /> | <img src="./screenshots/ios-dark-rules.png" alt="ios-dark-rules" width="180" /> |
| Scores | <img src="./screenshots/ios-light-scores.png" alt="ios-light-scores" width="180" /> | <img src="./screenshots/ios-dark-scores.png" alt="ios-dark-scores" width="180" /> |
| Settings | <img src="./screenshots/ios-light-settings.png" alt="ios-light-settings" width="180" /> | <img src="./screenshots/ios-dark-settings.png" alt="ios-dark-settings" width="180" /> |

### Desktop

| Screen | Light | Dark |
| --- | --- | --- |
| Selection | <img src="./screenshots/desktop-light-selection.png" alt="desktop-light-selection" width="240" /> | <img src="./screenshots/desktop-dark-selection.png" alt="desktop-dark-selection" width="240" /> |
| Game | <img src="./screenshots/desktop-light-game.png" alt="desktop-light-game" width="240" /> | <img src="./screenshots/desktop-dark-game.png" alt="desktop-dark-game" width="240" /> |
| Rules | <img src="./screenshots/desktop-light-rules.png" alt="desktop-light-rules" width="240" /> | <img src="./screenshots/desktop-dark-rules.png" alt="desktop-dark-rules" width="240" /> |
| Scores | <img src="./screenshots/desktop-light-scores.png" alt="desktop-light-scores" width="240" /> | <img src="./screenshots/desktop-dark-scores.png" alt="desktop-dark-scores" width="240" /> |
| Settings | <img src="./screenshots/desktop-light-settings.png" alt="desktop-light-settings" width="240" /> | <img src="./screenshots/desktop-dark-settings.png" alt="desktop-dark-settings" width="240" /> |
