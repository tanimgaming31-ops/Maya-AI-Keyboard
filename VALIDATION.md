# Validation — 2026-10-26

## GitHub Actions

[Build run 40283537217](https://github.com/tanimgaming31-ops/Maya-AI-Keyboard/actions/runs/40283537217), source commit 7004e0dabcfbc06b74782a0a2662a8e2c9b0e1fb:
- All 16 executable phonetic test cases passed.
- Gradle assembleDebug and lintDebug passed.
- APK artifact Maya-AI-Keyboard-Preview uploaded successfully.
- Fixed SDK setup to avoid the removed legacy tools package.
- Fixed two lint errors using LinearLayout.VERTICAL.

## Initial downloadable preview APK

The APK under downloads/ was built with official Android command-line tools before CI setup. Java sources compiled against API 35; resources linked with aapt2; D8 min API 26; alignment and v2/v3 signatures verified. Its runtime behavior matches the source; the later source changes replace numeric orientation constants with equivalent named constants. The GitHub Actions artifact is the Gradle-built package.

Unicode assets include 3,953 fully-qualified emoji/component entries in 10 groups.

## Not tested

- Device/emulator installation and keyboard UI behavior.
- Real Google API key validation or AI generation.
- Complete Bengali phonetic linguistic validation.

Compilation, lint and signature checks do not prove correct runtime behavior. Follow the phone test checklist in README-BN.md.
