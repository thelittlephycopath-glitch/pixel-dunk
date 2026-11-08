# Pixel Dunk

A compact Android pixel-art basketball game built around drag-to-aim launch physics, gravity, rim rebounds, and progressively harder shots.

## Game direction
- Pixel-art gym, crisp sprites, deliberate shot timing, and readable score feedback.
- Pull the ball back to aim; release to launch. Gravity and collisions determine the shot.
- New obstacle patterns and hoop positions are introduced as the score increases.
- A hidden, brief spooky easter egg can occur only after a rare sequence in later levels. It is a stylized pixel phantom, not graphic horror.

## Android architecture target
- **Target ABI: `arm64-v8a` only** (64-bit ARM).
- 32-bit ARM (`armeabi-v7a`), x86, and x86_64 ABIs are excluded by the Gradle ABI filter.
- The current game is Kotlin/Canvas and has no native libraries, so its DEX/resources are CPU-independent; the ABI filter enforces the intended native-library packaging target for this app and future native dependencies.
- Minimum Android API 23.

## Build
The GitHub Actions workflow builds a debug APK on pushes to `main` and on manual dispatch. The artifact is named `pixel-dunk-debug-apk`.

## APK size target
The design target is exactly 524,288 bytes (512 KiB). This is a strict release validation target, not yet a verified result. APK size must be measured after packaging; any size adjustment must preserve a valid installable APK.

## Development
- Android / Kotlin / custom Canvas renderer.
- No game engine or third-party runtime dependencies.
