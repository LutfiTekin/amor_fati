# AmorFati

**AmorFati** is a Jetpack Compose Android app for crafting and emailing Tarot readings. It supports phone and tablet layouts, card-flipping animations, dynamic JSON templates, image uploads, and SendGrid integration.

---

## 🛠️ Technology Stack

- **Kotlin** (1.8+)
- **Jetpack Compose** (Material 3)
- **Hilt** for dependency injection
- **Retrofit** + **Moshi** for networking
- **OkHttp** for HTTP client
- **Coil** for image loading & caching
- **Firebase Storage** for metaphor image uploads
- **SendGrid** (via `SendEmailUseCase`) for sending emails
- **Parcelize** for easy model passing
- **Coroutines** + **Flow** for asynchronous work

---

## 🚀 Features

- **JSON-driven templates**: paste or clear your JSON block, with runtime validation  
- **Card carousel & grid**: select up to four Tarot cards (plus an optional location card), animated entry & flips  
- **Metaphor image selection**: pick from gallery or share into the app via Android’s SEND intent  
- **Animated flips & spins**: tap to flip, long-press to spin, 3D card animations  
- **Device-specific layouts**: single-pane on phones, split grid + compose pane on tablets  
- **Settings screen**: toggle test bucket, email/CC on-off, choose among multiple SendGrid templates  
- **Footer with timestamp**: “21st of April 2025 11:11”-style date, auto-injected  
- **Preloading**: ViewModel preloads all card images into Coil’s cache

---

## 📁 Project Structure

src/ └── main/ ├── java/tekin/luetfi/amorfati/ │   ├── MainActivity.kt         # entry point, toggles settings/compose │   ├── di/                     # Hilt modules (NetworkModule, LoreModule, etc.) │   ├── domain/ │   │   ├── model/              # TarotCard, CardLore, EmailTemplate, ReadingProgress │   │   ├── use_case/           # GetLoreUseCase, SendEmailUseCase │   │   └── repository/         # MailComposerRepository │   ├── data/ │   │   └── remote/dto/         # JSON DTOs (TarotReadingJson, EmailAddress) │   ├── ui/ │   │   ├── screens/            # Compose, Settings, TabletMainScreen, CardBottomSheet… │   │   └── theme/              # Colors, Typography, Shapes │   └── utils/                  # Defaults, Deck, formatting helpers └── assets/                     # (optional) local card images, lore.json

---

## ⚙️ Setup & Configuration

1. **Clone** the repository.  
2. **SendGrid**  
   - Store your SendGrid key securely (e.g. in `local.properties` or environment variables).  
3. **Firebase Storage** (optional)  
   - Enable a storage bucket and set its URL in `Defaults.mainLoreUrl`.  
4. **Defaults object** (`Defaults.kt`)  
   ```kotlin
   object Defaults {
     var useTestBucket     by mutableStateOf(false)
     var sendEmail         by mutableStateOf(true)
     var shouldSendCC      by mutableStateOf(true)
     var selectedTemplate  by mutableStateOf(Template.list.first())
     const val IMAGE_HOST_DIR = "https://lutfitek.in/assets/"
     // …
   }

5. JSON schema
Your dynamic template must include:

{
  "name":               "USER_NAME",
  "email":              "USER_EMAIL",
  "date_time":          "READING_TIME",
  "first_card_url":     "CODE_OF_FIRST_CARD",
  "second_card_url":    "…",
  "third_card_url":     "…",
  "fourth_card_url":    "…",
  "location_card_url":  "CODE_OF_LOCATION_CARD",
  "first_card_quote":   "…",
  "location_card_quote":"…",
  "card_influence_summary":"…",
  "act1":               "…",
  "act2":               "…",
  "act3":               "…",
  "future_holds":       ["…","…","…","…"],
  "metaphor_image_url": "METAPHOR_IMAGE",
  "metaphor_image_quote":"…"
}


6. Build & Run

Open in Android Studio.

Let Hilt generate code.

Run on a phone (full-screen compose) or tablet (split grid + compose pane).





---

🎨 Customization

Card animations live in FlippableCard.kt.

Compose layouts under ui/screens/.

Retrofit service for lore JSON in LoreNetworkModule.kt.

Email templates configured in Template.list (two default SendGrid IDs).



---

📄 License

This project is MIT-licensed. See LICENSE for details.

