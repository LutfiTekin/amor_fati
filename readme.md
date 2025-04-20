# TarotApp Companion README

## Overview
This Android application interfaces with a custom 40‑card Tarot deck and an AI‑driven storytelling engine. It allows a single user to:

- Select four cards for a reading
- Paste a ChatGPT‑generated JSON payload defining the reading
- Upload a metaphorical image to Firebase Storage
- Send the completed reading via email using SendGrid

## Features & Flow
1. **Card Bar**: Displays all 40 cards as a horizontal scroll (`LazyRow`).  
2. **Recipient Email**: Input field for the email address.  
3. **JSON Input**: Text area for pasting the reading JSON.  
4. **Metaphor Image Picker**: Launch system image picker, preview thumbnail.  
5. **Submit**: Upload image → replace placeholder in JSON → send email → show progress & Snackbar → clear form.

## Setup Instructions
1. Add `google-services.json` for Firebase in `app/`. Enable Cloud Storage.  
2. Create a SendGrid Dynamic Template (blank + code editor). Note the `template_id` and store your API key securely.  
3. Add dependencies in `build.gradle`:
   ```groovy
   // Firebase
   implementation "com.google.firebase:firebase-storage-ktx"
   // Retrofit & JSON
   implementation "com.squareup.retrofit2:retrofit:2.9.0"
   implementation "com.squareup.retrofit2:converter-moshi:2.9.0"
   implementation "com.squareup.moshi:moshi:1.13.0"
   // Coil for images
   implementation "io.coil-kt:coil-compose:2.2.2"
   ```
4. Initialize Firebase in `Application` class and configure Retrofit for SendGrid.

## Project Structure
```
app/
├─ src/main/
│  ├─ java/.../ui/screens/EmailComposeScreen.kt
│  ├─ java/.../viewmodel/EmailComposerViewModel.kt
│  └─ res/
│     └─ drawable/ic_card_placeholder.xml
├─ assets/tarot-schema.json
└─ docs/card_rules.md
```

---
Happy mystical coding! Combine modern Android tech with ancient Tarot lore.

