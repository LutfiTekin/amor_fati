# AmorFati

[![Kotlin](https://img.shields.io/badge/Kotlin-1.8-blue.svg)](https://kotlinlang.org) [![Jetpack%20Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-green.svg)](https://developer.android.com/jetpack/compose) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**AmorFati** is a Kotlin-based Android app built with Jetpack Compose (Material 3) for crafting and emailing customized Tarot readings.

---

## 📋 Table of Contents

* [Features](#features)
* [Tech Stack](#tech-stack)
* [Prerequisites](#prerequisites)
* [Getting Started](#getting-started)
* [Configuration](#configuration)
* [Usage](#usage)
* [JSON Schema](#json-schema)
* [Project Structure](#project-structure)
* [Customization](#customization)
* [Contributing](#contributing)
* [License](#license)

---

## ✨ Features

* **JSON-driven templates**: Paste or clear your JSON block, with runtime validation and preview.
* **Card selection & animations**: Select up to four Tarot cards (plus an optional location card) via a carousel or grid; tap to flip, long-press to spin, with 3D animations.
* **Adaptive layouts**: Single-pane on phones; split grid + compose pane on tablets.
* **Metaphor image support**: Upload from gallery or share into the app via Android’s SEND intent; images are cached locally.
* **Email Delivery**: Send readings via SendGrid (test/prod buckets); toggle CC and choose from multiple templates.
* **Automatic timestamp footer**: Readings include a “Day Month Year HH\:MM” timestamp auto-injected.
* **Preloading & caching**: All card images preload into Coil’s cache for smooth UX.

---

## 🛠 Tech Stack

* **Language**: Kotlin 1.8+
* **UI**: Jetpack Compose (Material 3)
* **DI**: Hilt
* **Networking**: Retrofit + Moshi, OkHttp
* **Image Loading**: Coil
* **Storage**: Firebase Storage (for metaphor images)
* **Email**: SendGrid (via `SendEmailUseCase`)
* **Concurrency**: Kotlin Coroutines & Flow
* **Parcelization**: `@Parcelize` models

---

## 🔧 Prerequisites

* Android Studio Arctic Fox (2020.3.1) or later
* JDK 11 or higher
* Gradle 7.0+
* Android SDK:

   * `compileSdkVersion 34`
   * `minSdkVersion 21`
   * `targetSdkVersion 34`

---

## 🚀 Getting Started

1. **Clone** the repository:

   ```bash
   git clone https://github.com/yourusername/amorfati.git
   cd amorfati
   ```
2. **Configure** your environment (see [Configuration](#configuration)).
3. **Build & Run** on an emulator or device:

   * In Android Studio, click **Run**.
   * Or use Gradle:

     ```bash
     ./gradlew clean assembleDebug && ./gradlew installDebug
     ```

---

## ⚙️ Configuration

1. **SendGrid**

   * Obtain your SendGrid API key.
   * Store it securely, e.g., in `local.properties`:

     ```properties
     SENDGRID_API_KEY=your_key_here
     ```
2. **Firebase Storage** (optional)

   * Create a Storage bucket and add its URL to `Defaults.mainLoreUrl` in `Defaults.kt`.
3. **Defaults.kt**

   * Toggle buckets and email behavior:

     ```kotlin
     object Defaults {
       var useTestBucket by mutableStateOf(false)
       var sendEmail     by mutableStateOf(true)
       var shouldSendCC  by mutableStateOf(true)
       var selectedTemplate by mutableStateOf(Template.list.first())
       // …
     }
     ```

---

## 📖 Usage

1. **Select Cards**: Swipe through the deck and pick up to four cards. Optionally add a location card.
2. **Compose Reading**: Fill or paste a JSON template to drive the narrative.
3. **Add Metaphor**: Upload or share an image; add a custom quote.
4. **Send**: Tap the email icon to send your reading; preview it in-app or in your inbox.

> ⚠️ **Tip**: Toggle **Test Bucket** in Settings to preview emails without sending.

---

## 🗂 JSON Schema

Your template JSON must include the following keys (all strings unless noted):

```json
{
  "name": "USER_NAME",
  "email": "USER_EMAIL",
  "date_time": "READING_TIMESTAMP",
  "first_card_url": "CARD_CODE_1",
  "second_card_url": "CARD_CODE_2",
  "third_card_url": "CARD_CODE_3",
  "fourth_card_url": "CARD_CODE_4",
  "location_card_url": "LOCATION_CARD_CODE",
  "first_card_quote": "…",
  "location_card_quote": "…",
  "card_influence_summary": "…",
  "act1": "…",
  "act2": "…",
  "act3": "…",
  "future_holds": ["…", "…", "…", "…"],
  "metaphor_image_url": "METAPHOR_IMAGE_URL",
  "metaphor_image_quote": "…"
}
```

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/tekin/luetfi/amorfati/
│   │   ├── MainActivity.kt        # Entry point, toggles screens
│   │   ├── di/                    # Hilt modules (NetworkModule, LoreModule…)
│   │   ├── domain/
│   │   │   ├── model/             # Data classes: TarotCard, CardLore…
│   │   │   ├── use_case/          # Business logic: GetLoreUseCase…
│   │   │   └── repository/        # MailComposerRepository
│   │   ├── data/remote/dto/       # JSON DTOs
│   │   ├── ui/
│   │   │   ├── screens/           # ComposeScreen, SettingsScreen…
│   │   │   └── theme/             # Colors, Typography, Shapes
│   │   └── utils/                 # Defaults, Deck, formatters
├── assets/                        # Local card images, lore.json
└── LICENSE                        # MIT License
```

---

## 🎨 Customization

* **Animations**: Modify `FlippableCard.kt` for flip/spin effects.
* **Layouts**: Update Compose screens in `ui/screens/`.
* **Lore Service**: Change the Retrofit setup in `LoreNetworkModule.kt`.
* **Email Templates**: Edit or add IDs in `Template.list`.

---

## 🤝 Contributing

1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/YourFeature`.
3. Commit your changes: `git commit -m "Add feature"`.
4. Push: `git push origin feature/YourFeature`.
5. Create a Pull Request.

Please follow the [Code of Conduct](CODE_OF_CONDUCT.md).

---

## 📄 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
