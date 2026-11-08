# Pixel Dunk

A compact Android pixel-art basketball game built around drag-to-aim launch physics, gravity, rim rebounds, and progressively harder shots.

## Game direction
- Pixel-art gym, crisp sprites, deliberate shot timing, and readable score feedback.
- Pull the ball back to aim; release to launch. Gravity and collisions determine the shot.
- New obstacle patterns and hoop positions are introduced as the score increases.
- A hidden, brief spooky easter egg can occur only after a rare sequence in later levels. It is a stylized pixel phantom, not graphic horror.

## Build
The GitHub Actions workflow builds a debug APK on pushes to `main` and manual dispatch. The artifact is named `pixel-dunk-debug-apk`.

## APK size target
The design target is exactly 524,288 bytes (512 KiB). This is a strict release validation target, not yet a verified result. APK size must be measured after packaging; any size adjustment must preserve a valid installable APK.

## Development
- Android / Kotlin / custom Canvas renderer.
- No game engine or third-party runtime dependencies.
- Minimum Android API 23.
