# Validation — 2026-10-26

Performed:
- 16 executable Java phonetic cases passed.
- All application Java sources compiled with JDK 17 compiler targeting Java 8 against Android API 35.
- Android resources and manifest linked with Android build-tools 35 aapt2.
- Bytecode converted with Android D8, min API 26.
- APK aligned and signed; apksigner verifies v2 and v3 signatures.
- Unicode data includes 3,953 fully-qualified emoji/component entries across 10 groups.

Not performed:
- Device/emulator installation or UI tests.
- Actual Google API key validation or AI generation.
- Full Gradle build, Android Lint, GitHub Actions run.
- Complete Bengali phonetic linguistic validation.

The delivered APK was assembled with official Android command-line build tools directly.
The Gradle project and workflow are included for subsequent builds. Do not treat compilation or signature verification as proof of correct runtime behavior.
