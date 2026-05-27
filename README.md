# SafeCall Recorder

![Visitors](https://komarev.com/ghpvc/?username=Samarpitgupta&repo=recorder&label=Visitors&color=0e75b6&style=flat)

A production-ready Android app (Java) for legal and transparent phone call recording with encrypted local storage and Google Drive backup.

## Features

- 🛠️ **Accessibility Service Exemption**: Leverages a robust, custom `CallRecordingAccessibilityService` to bypass strict Android 12+ background service limits and Android 11+ microphone restrictions, ensuring 100% reliable background service initialization.
- 🎙️ **Automatic Call Recording**: Seamlessly records incoming and outgoing phone calls automatically using modern foreground services.
- 📱 **WhatsApp Call Recording Support**: Built-in support for detecting active WhatsApp VoIP calls via a dynamic notification listener.
- 🎛️ **Advanced Audio Source Selection**: Configurable audio inputs (MIC, VOICE_COMMUNICATION, VOICE_RECOGNITION, etc.) to optimize recording quality across different device manufacturers.
- 📢 **Auto-Speakerphone Mode**: Automatically triggers the device speakerphone during calls to ensure both sides of the conversation are cleanly captured on audio-restricted handsets.
- 🎙️ **Built-in Recording Studio**: Launch a gorgeous, responsive, Material 3 bottom-sheet recorder for dictating on-the-fly local voice memos with full timer, pause, resume, and instant save capabilities.
- 🔒 **AES-256 Keystore Encryption**: Transparent, military-grade local storage security where all recorded audio files are fully encrypted using keys secured by the hardware-backed Android Keystore.
- ☁️ **Secure Google Drive Sync**: Manual or scheduled background backups (using WorkManager) of encrypted recordings to the user's private Google Drive storage, complete with "Wi-Fi Only" bandwidth control.
- 🗑️ **Secure Recycle Bin (Trash system)**: Integrated temporary storage for deleted items, allowing users to easily restore accidental deletions or perform permanent database/file purges.
- 🔑 **Biometric Authentication Lock**: Secure vault protection requiring Fingerprint or Face biometric authentication (using modern Androidx Biometrics) to access the app and its recordings.
- 📇 **Dynamic Contact Resolution**: Seamlessly integrates with the local phonebook to match incoming and outgoing phone numbers with corresponding contact names and pictures.
- 🔍 **Global Search & Filters**: Effortlessly search, filter, and sort recordings by phone number, contact name, duration, date, or favorites.
- ⭐ **Favorites & Annotation Notes**: Mark crucial calls as favorites and attach descriptive text notes directly to any recording metadata.
- 🎵 **Advanced Playback UI**: In-app encrypted audio player with real-time waveform tracking, seekbars, play/pause toggles, and dedicated 10s skip-forward/backward controls.
- 📤 **Secure Share & Export**: Temporarily decrypts and exports call recordings to the standard Android sharesheet for quick, legal, and hassle-free file sharing.

## Overcoming Modern Android Call Recording Barriers

Developing a reliable call recorder for modern Android distributions (Android 10 to Android 14+) is exceptionally difficult due to Google's continuous tightening of security APIs and background task regulations. Below is a breakdown of how **SafeCall Recorder** uniquely resolves these restrictions:

### 1. Bypassing Background Service Restrictions (Android 12+ / 13+ / 14+) — **SOLVED**
- **The restriction**: Modern Android versions prohibit starting foreground services from the background (throwing `ForegroundServiceStartNotAllowedException`).
- **Our solution**: By incorporating `CallRecordingAccessibilityService`, the application operates with system accessibility privileges. Accessibility services are legally exempted from background startup limits, permitting a clean, instant launch of the `CallRecorderService` the moment the dialer state transitions to `OFFHOOK`.

### 2. Capturing Microphone Streams in the Background (Android 11+) — **SOLVED**
- **The restriction**: Background microphone acquisition is restricted by the OS, leading to empty/silent audio streams if recording is initialized in an inactive context.
- **Our solution**: The system accessibility context allows the app to spawn the `CallRecorderService` with the explicit `microphone` foreground service type *prior* to starting audio capture. This cleanly inherits foreground microphone privileges and ensures perfect, rich call audio representation.

### 3. Handling Audio Stream Focus Restrictions
- **The restriction**: Native audio sources like `VOICE_CALL` are completely deprecated and blocked for third-party applications on Android 10+.
- **Our solution**: We utilize high-compatibility streams like `MIC` and `VOICE_COMMUNICATION` in tandem with the **Auto-Speakerphone** system. Toggling the speakerphone programmatically during an active call ensures that both the user's voice and the caller's incoming voice are fed through the active microphone.

### 4. Bypassing Deprecated Call Interceptors
- **The restriction**: The traditional `ACTION_NEW_OUTGOING_CALL` broadcast has been deprecated since API 29 and is no longer delivered.
- **Our solution**: SafeCall leverages `PhoneStateListener` directly inside the connected accessibility service context, which receives immediate, direct, and unthrottled telephony callbacks for all incoming, dialing, answered, and ended calls.


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
