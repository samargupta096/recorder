# SafeCall Recorder

![Visitors](https://komarev.com/ghpvc/?username=Samarpitgupta&repo=recorder&label=Visitors&color=0e75b6&style=flat)

A production-ready Android app (Java) for legal and transparent phone call recording with encrypted local storage and Google Drive backup.

## Features

- **Automatic Call Recording**: Records incoming and outgoing phone calls automatically.
- **WhatsApp Call Recording Support**: Initial support for detecting WhatsApp call notifications.
- **Advanced Audio Source Selection**: Configurable audio sources (MIC, VOICE_COMMUNICATION, VOICE_RECOGNITION, etc.) to handle manufacturer-specific restrictions.
- **Auto-Speakerphone**: Automatically enables speakerphone during calls to capture audio on restricted devices.
- **Encrypted Storage**: AES-256 encryption using Android Keystore for all recorded audio files.
- **Google Drive Backup**: Manual and scheduled backup with Wi-Fi only option.
- **Favorites & Notes**: Star important recordings and add text notes.
- **App Lock**: Secure access with Biometric Authentication (Fingerprint/Face).
- **Contact Resolution**: Displays contact names for recorded calls by reading local contacts.
- **Search & Filter**: Find recordings by contact name or phone number.
- **Playback Controls**: Built-in audio player with seek functionality.
- **Share & Export**: Share decrypted recordings temporarily via any supported app.

## Internal Development Details & Call Recording Analysis

Developing a reliable Call Recorder for modern Android versions (Android 10 to 14+) is exceptionally challenging due to Google's continuous deprecation of call recording APIs and aggressive background execution limits. Below is an analysis of the internal development hurdles and why call recording features may fail on newer devices.

### 1. Android Background Execution Limits (Android 12+)
- **The Issue**: The app uses `PhoneCallReceiver` to detect phone state changes (`ACTION_PHONE_STATE`) and attempts to start the `CallRecorderService` as a Foreground Service from the background using `ContextCompat.startForegroundService()`.
- **The Restriction**: Android 12+ strictly prohibits starting Foreground Services from the background unless the app holds special permissions, is a default app (like Default Dialer), or is actively visible to the user.
- **The Result**: Attempting to start the service throws a `ForegroundServiceStartNotAllowedException`, causing the recording initialization to fail silently before the service even spins up.

### 2. Microphone Access Restrictions in Background (Android 11+)
- **The Issue**: To prevent background eavesdropping, Android 11+ restricts background apps from accessing the microphone.
- **The Restriction**: An app must be in the foreground or already running a foreground service with the `microphone` type *before* the microphone is accessed. Because `CallRecorderService` attempts to start and access the `AudioRecord` simultaneously while the app UI is typically closed, the OS may deny the audio capture.
- **The Result**: The `AudioRecord` object may read only zeroes, resulting in completely silent audio files.

### 3. Audio Source Restrictions (Android 10+)
- **The Issue**: In Android 10, Google explicitly blocked the `VOICE_CALL`, `VOICE_DOWNLINK`, and `VOICE_UPLINK` audio sources for third-party apps to protect user privacy.
- **The Workaround**: The app defaults to `MediaRecorder.AudioSource.MIC` or `VOICE_COMMUNICATION`. We introduced an "Auto-Speakerphone" feature to force the call audio through the external speaker, allowing the device microphone to pick up the other party.
- **The Current Limitation**: Even with `MIC`, many OEMs (Samsung, Pixel, Xiaomi) grant exclusive audio focus to the native dialer app during an active call, completely muting the microphone stream for third-party apps. Currently, the app's `findBestAudioSource()` method is unimplemented in the recording flow, relying solely on the user-selected preference.

### 4. Deprecation of `ACTION_NEW_OUTGOING_CALL`
- **The Issue**: The `PhoneCallReceiver` listens for `ACTION_NEW_OUTGOING_CALL` to detect outgoing calls.
- **The Limitation**: This broadcast was deprecated in API 29 (Android 10) and is no longer reliably delivered. To properly intercept and manage outgoing calls on modern devices, apps are required to implement `CallScreeningService` or `InCallService`.

### 5. Lack of AccessibilityService Implementation
- **The State of the Art**: Almost all functioning third-party call recorders on the Play Store today bypass the above restrictions by utilizing an `AccessibilityService`. This allows the app to capture global device audio or intercept UI events. This project does not currently implement an `AccessibilityService`, which remains the biggest hurdle to achieving 100% recording reliability on Android 11+.

## Screenshots

| Recordings List | Recording Details | Settings | App Lock | Empty State |
|:---------------:|:-----------------:|:--------:|:--------:|:-----------:|
| ![Recordings](screenshots/recordings_list.png) | ![Details](screenshots/recording_details.png) | ![Settings](screenshots/settings.png) | ![App Lock](screenshots/app_lock.png) | ![Empty](screenshots/empty_state.png) |

## Requirements

- Android 8.0 (API 26) or higher
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17

## Project Structure

```
app/src/main/java/com/safecall/recorder/
├── SafeCallApp.java              # Application class with Hilt
├── MainActivity.java             # Main recordings list
├── RecordingDetailsActivity.java # Recording playback
├── SettingsActivity.java         # App settings
├── data/
│   ├── local/
│   │   ├── db/                   # Room database
│   │   │   ├── AppDatabase.java
│   │   │   ├── RecordingDao.java
│   │   │   └── RecordingEntity.java
│   │   └── prefs/
│   │       └── PreferencesManager.java
│   ├── repository/
│   │   └── RecordingRepository.java
│   └── encryption/
│       └── EncryptionManager.java
├── service/
│   ├── CallRecorderService.java  # Foreground service for recording
│   ├── PhoneCallReceiver.java    # BroadcastReceiver for call detection
│   ├── WhatsAppCallListenerService.java # NotificationListenerService for WhatsApp
│   └── BootReceiver.java         # Boot completed receiver
├── backup/
│   ├── GoogleSignInHelper.java   # Google Sign-In integration
│   ├── DriveBackupManager.java   # Google Drive API operations
│   └── BackupWorker.java         # WorkManager scheduled backups
├── ui/
│   └── RecordingsAdapter.java    # RecyclerView adapter
└── di/
    └── AppModule.java            # Hilt dependency injection
```

## Setup Instructions

### 1. Open in Android Studio
- File → Open → Navigate to the project root directory
- Wait for Gradle sync to complete

### 2. Configure Google Drive API (Optional)
To enable Google Drive backup:
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the **Google Drive API**
4. Create OAuth 2.0 credentials:
   - Application type: Android
   - Package name: `com.safecall.recorder`
   - SHA-1 fingerprint: Use `./gradlew signingReport`
5. Download `google-services.json` (if using Firebase) or note the client ID

### 3. Build the App
In Android Studio:
- Build → Make Project (Ctrl+F9)
- Run → Run 'app' (Shift+F10)

Or via command line (requires Gradle installation):
```bash
./gradlew assembleDebug
```

### 4. Install on Device
Connect an Android device via USB and run from Android Studio, or:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Permissions Required

| Permission | Purpose |
|------------|---------|
| `READ_PHONE_STATE` | Detect incoming/outgoing calls |
| `RECORD_AUDIO` | Record audio during calls |
| `READ_CONTACTS` | Display contact names |
| `READ_CALL_LOG` | Get call details and incoming numbers |
| `FOREGROUND_SERVICE` | Run recording in background |
| `INTERNET` | Google Drive backup |

## Legal Disclaimer

⚠️ **Important**: Call recording laws vary by jurisdiction. This app implements user consent UI, but users are responsible for complying with local laws (one-party vs two-party consent states/countries).

## Tech Stack

- **Language**: Java
- **Architecture**: MVVM with Repository pattern
- **DI**: Hilt (Dagger)
- **Database**: Room
- **Encryption**: AES-256-GCM via Android Keystore
- **UI**: Material 3 with ViewBinding
- **Background Work**: WorkManager
- **Cloud**: Google Drive API
