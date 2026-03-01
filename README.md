# X Video Downloader

A fully functional Android application designed to extract and archive MP4 video data streams directly from X (formerly Twitter). Built with a custom OkHttp network engine to bypass system restrictions, featuring a neon-infused UI, glitch animations, and an integrated media player.

## 🚀 Features

* **Custom Extraction Engine:** Uses `OkHttp` to pull raw video data directly, bypassing the unreliable native Android `DownloadManager`.
* **Cyberpunk Terminal UI:** Dark mode by default with glowing neon cyan/pink accents, terminal-style text, and glitch animations.
* **In-App Media Player:** Instantly play downloaded archives directly from the terminal interface without opening an external gallery.
* **Persistent Local Archive:** Maintains a timestamped history of all extractions saved to a custom `/Downloads/X_Downloader` directory.
* **Secure Vault Architecture:** API credentials are decoupled from the main logic for safe repository sharing.

---

## 🔑 Required API Setup

To prevent rate-limiting and keep this project open-source, the API key has been removed from this repository. **You must generate your own free API key to use this app.**

This project runs on the **Twitter Downloader API** by JustMobi via RapidAPI.

### Step 1: Get Your API Key
1. Go to the API page: [Twitter Downloader on RapidAPI](https://rapidapi.com/JustMobi/api/twitter-downloader-download-twitter-videos-gifs-and-images)
2. Log in or create a free RapidAPI account.
3. Click the **Subscribe to Test** button (there is a free tier available).
4. Once subscribed, go to the **Endpoints** tab.
5. Look at the code snippet block on the right side and find the `x-rapidapi-key` string. Copy that key.

### Step 2: Configure the App Vault
Once you have cloned this repository into Android Studio, you need to create the secure configuration file to hold your key.

1. Navigate to your main code directory: `app/src/main/java/com/example/x_video_downloader/`
2. Create a new Kotlin Object file named `ApiConfig.kt`.
3. Paste the following code into the file and insert your copied key:

```kotlin
package com.example.x_video_downloader

object ApiConfig {
    // Insert your RapidAPI key here:
    const val RAPID_API_KEY = "YOUR_API_KEY_HERE"
    const val RAPID_API_HOST = "twitter-video-downloader2.p.rapidapi.com"
}
```

---

## 🛠️ Installation & Compilation

1. Clone this repository to your local machine.
2. Open the project in **Android Studio**.
3. Complete the API Setup steps above.
4. Sync the Gradle project files.
5. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)** to generate a local `app-debug.apk`.
6. Transfer the `.apk` file to your Android device and install (ensure "Install from Unknown Sources" is permitted on your device).

## ⚠️ Disclaimer

This application is built for educational and personal archiving purposes. Please respect copyright laws and the terms of service of X when downloading media. 

---

## 🤖 Credits & Architecture

* **Project Lead & Visionary:** [Your Name / Handle]
* **AI Co-Pilot & Lead Architect:** Gemini
* **Core Engine:** Custom OkHttp Pipeline
* **Design Language:** Neon Cyberpunk / Terminal Dark Mode

*"Built collaboratively by human intuition and artificial intelligence."*
