# Maya AI Keyboard — 0.1 Preview

এটি Maya companion থেকে আলাদা Android keyboard application। আগের Maya অ্যাপ বা রিপোজিটরি বদলানো হয়নি। Android 8.0 বা পরের সংস্করণ প্রয়োজন।

## এখন ইনস্টল

1. Maya-AI-Keyboard-Preview.apk ফোনে ডাউনলোড করে খুলুন।
2. Android চাইলে যেখান থেকে APK খুলছেন, সেই অ্যাপের জন্য Install unknown apps অনুমতি দিন।
3. Maya AI Keyboard খুলুন → Enable keyboard → Maya AI Keyboard চালু করুন। Android-এর keyboard সতর্কতা পড়ুন।
4. Choose keyboard → Maya AI Keyboard নির্বাচন করুন।
5. WhatsApp/Messenger বা নোটে লিখে দেখুন। 🌐 চাপলে ভাষা বদলাবে; ধরে রাখলে অন্য কিবোর্ড বেছে নিতে পারবেন।

## কী তৈরি হয়েছে

- বাংলা সরাসরি অক্ষর: consonants এবং vowels/marks-এর দুটি পাতা, হসন্তসহ।
- বাংলা phonetic: Latin লিখে বাংলা preview; ami → আমি, tumi → তুমি, bangla → বাংলা। মূল Latin শব্দ রাখতে preview-র Latin অংশ চাপুন।
- English, Tagalog, Indonesian QWERTY; Tagalog-এ ñ ও accent অক্ষর।
- Shift, সংখ্যা, চিহ্ন, space, delete, cursor arrows, editor action/Enter।
- Unicode 17.0: ৩,৯৫৩ fully-qualified emoji এবং components; ১০টি গ্রুপ, skin-tone/ZWJ/flag variants, পৃষ্ঠা বদলানো, সাম্প্রতিক ব্যবহৃত ইমোজি। Emoji appearance ফোনের font-এর ওপর নির্ভর করে।
- AI assistant: অনুবাদ, grammar correction, reply draft, polite/affectionate rewrite, shorten, meaning, সাধারণ প্রশ্ন।
- নিজের Gemini API key: Save/Replace, Remove, Test/load models। Android Keystore দিয়ে encrypt করা।
- সাধারণ টাইপিং সম্পূর্ণ offline। AI পাঠানোর আগে লেখা দেখা যায়; Send চাপলেই Google-এ যায়। ফল নিজে Copy ও Paste করতে হবে।

## AI চালাবেন

1. App settings-এ নিজের Gemini API key paste করে Save চাপুন।
2. Test saved key / Load available models চাপুন। List সফল হলে generation-এর উপযোগী text model নির্বাচন করুন।
3. Chat-এর লেখা select করে keyboard-এর AI চাপুন; অথবা AI panel-এ নিজে paste/type করুন।
4. কাজ ও output language নির্বাচন করুন। নিচের যে লেখাটি দেখছেন সেটিই Send this text to Google চাপলে পাঠানো হবে।
5. ফল দেখে Copy AI result → Return to chat → Paste। অ্যাপ নিজে message send করে না।

API key test শুধু model-list access যাচাই করে। Generation request দিয়ে quota ও model compatibility আলাদাভাবে পরীক্ষা হবে। AI internet/API quota-নির্ভর; সব account/model-এ বিনামূল্যে generation পাওয়া নিশ্চিত নয়। এই অ্যাপে কোনো paid service বাধ্যতামূলক করা হয়নি।

## সীমাবদ্ধতা — লুকানো হয়নি

- এটি প্রথম preview, পূর্ণ Gboard/Avro বিকল্প নয়। ফোনেটিকের নিজস্ব ছোট rule engine; সব শব্দের বানান সঠিক হয় না, পূর্ণ Avro rules নেই। সরাসরি বাংলা দিয়ে সংশোধন করা যায়।
- Predictive dictionary, automatic correction, swipe typing, voice typing, voice assistant, custom themes, clipboard history, offline AI model এখনো তৈরি হয়নি।
- AI ফোনের অ্যাপ নিয়ন্ত্রণ বা নিজে বার্তা পাঠায় না।
- Recent emoji শুধু session memory-তে থাকে। Keyboard restart হলে মুছে যায়।
- Android password/private-marked fields-এ AI, clipboard button ও phonetic বন্ধ; অ্যাপ নিজে কোনো typing log রাখে না।
- ফোনে runtime/keyboard layout test এবং আসল API দিয়ে generation এখনও করা হয়নি।

## ফোন দিয়ে GitHub Actions build

1. আলাদা নতুন GitHub repository তৈরি করুন, যেমন maya-ai-keyboard। আগের Maya repository ব্যবহার করার প্রয়োজন নেই।
2. এই source ZIP ফোনের file manager দিয়ে extract করুন। ZIP-টিকেই repository-তে upload করলে build হবে না: ভেতরের project files/folders দিতে হবে।
3. Repository root-এ settings.gradle, build.gradle, gradle.properties, app/, tests/, preview-signing/ এবং .github/workflows/build-apk.yml রাখুন। Hidden .github folder upload না হলে GitHub-এর Add file → Create new file দিয়ে একই path তৈরি করে workflow text paste করুন।
4. Actions → Build keyboard APK → Run workflow।
5. সফল হলে Maya-AI-Keyboard-Preview artifact download করে ZIP খুলুন; app-debug.apk ইনস্টল করুন।

Public repository-তে standard Actions runners ব্যবহার করার ক্ষেত্রে সাধারণত free build পাওয়া যায়; account-এর বর্তমান limits/terms প্রযোজ্য। Workflow এই পরিবেশে চালানো হয়নি।

preview-signing-এর development keystore ইচ্ছাকৃতভাবে source-এর সঙ্গে রাখা হয়েছে, যাতে নিজের preview update-এ একই signature থাকে। Password/alias standard Android development values। এটি production signing key নয়; public distribution-এর জন্য আলাদা private release key লাগবে। নিজের Gemini key কখনো repository-তে লিখবেন না।

## ফোনে যাচাইয়ের তালিকা

1. Notes-এ English/বাংলা/Tagalog/Indonesia লিখুন।
2. Phonetic mode: ami, tumi, bangla লিখুন; delete এবং cursor move করে দেখুন।
3. Direct বাংলা: ক + ্ + ষ; ক + ি; আ লিখে দেখুন।
4. 😀 দিয়ে faces, people, animals, food, travel, activities, objects, symbols, flags, component groups ঘুরে দেখুন।
5. Skin tone/flag/family emoji insert ও delete পরীক্ষা করুন।
6. AI key load, একটি অনুবাদ, reply draft, remove key এবং offline error পরীক্ষা করুন।
7. Password field-এ Private দেখায় ও AI বন্ধ থাকে কি না পরীক্ষা করুন।
8. Portrait/landscape, gesture navigation, font size, WhatsApp/Messenger-এ layout পরীক্ষা করুন।

## Data sources

Emoji data: https://www.unicode.org/Public/17.0.0/emoji/emoji-test.txt
Unicode license included in app/src/main/assets/UNICODE-LICENSE.txt.
Android IME: https://developer.android.com/develop/ui/views/touch-and-input/creating-input-method
Gemini: https://ai.google.dev/api/generate-content and https://ai.google.dev/api/models
