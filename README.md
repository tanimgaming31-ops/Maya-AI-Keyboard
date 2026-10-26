# Maya AI Keyboard

Android keyboard preview with Bengali direct and basic phonetic input, English, Tagalog, Indonesian, Unicode emoji groups and an opt-in Gemini writing assistant.

## APK ডাউনলোড

[Download Maya AI Keyboard Preview APK](downloads/Maya-AI-Keyboard-Preview.apk?raw=true)

ইনস্টল → Enable keyboard → Choose keyboard → Maya AI Keyboard।

[বাংলা নির্দেশনা ও সীমাবদ্ধতা](README-BN.md) · [যাচাইয়ের ফল](VALIDATION.md)

## GitHub build

Open **Actions → Build keyboard APK → Run workflow**. After a successful run, download the **Maya-AI-Keyboard-Preview** artifact and extract the APK. Pushes to main also trigger builds. Java 17, Gradle 8.9 and Android SDK 35 are installed by the workflow.

## Preview scope

Offline typing requires no API key. AI requires your own Gemini key, internet and available quota. Only explicitly submitted text is sent to Google. No API key is included. Device runtime and real API tests remain pending. Voice/swipe typing and predictive dictionaries are not implemented.

The committed keystore is a **public development-only signing key**, not a production credential; it preserves preview update compatibility. Do not use it for production distribution.
