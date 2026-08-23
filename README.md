# Minimal Launcher

A minimalistic Android home-screen launcher inspired by the Light Phone: a plain black
screen, text-only app lists (no icons), a user-curated multi-page dock, a full
alphabetical app list, and a dashboard with weather, location, and notifications.

## Navigation

- **Dock** (center screen) — your chosen apps, in text form, across one or more
  vertical pages (swipe up/down).
- **Swipe right** from the dock → weather, current location, and notifications.
  Tapping a notification opens the app it came from.
- **Swipe left** from the dock → the alphabetical list of every installed app.
- **Long-press** anywhere on an empty area of the dock to edit it: rename an
  app (tap its name), remove it (tap "−"), drag "≡" to reorder, "+ Add app" to
  add one from the full app list, or add/remove dock pages.

## Building

Requires a JDK the bundled Gradle wrapper can run on (Android Studio's own JBR
works well) and the Android SDK. From the project root:

```
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`. This is a
debug-signed build — no signing keys are required.

A GitHub Actions workflow (`.github/workflows/build.yml`) builds the same APK
on every push and pull request, uploads it as a workflow artifact, and — on
pushes to `main` — publishes it to a rolling "latest" GitHub Release.

## Installing on a device

1. Enable Developer Options and USB debugging on the phone, then connect it
   over USB, or just download the APK from the GitHub Release directly on
   the phone.
2. Install it: `adb install -r app-debug.apk`, or tap the APK on-device
   (you'll need to allow "install unknown apps" for whichever app you used
   to open it).
3. Set it as your home app: **Settings → Apps → Default apps → Home app**.
4. The dashboard screen will prompt you in-app to grant location access
   (for weather) and notification access — the latter opens Android's
   notification-listener settings, since that permission can't be granted
   from within the app itself.

## Tech notes

- Kotlin + Jetpack Compose, single `:app` module, no DI framework (a small
  manual `AppContainer` service locator instead).
- Dock configuration (pages, apps, custom names) is stored locally via
  Jetpack DataStore.
- Weather comes from the free, no-API-key
  [Open-Meteo](https://open-meteo.com/) API; location comes from
  `FusedLocationProviderClient`, with on-device `Geocoder` for the city name.
- Notifications are read via a `NotificationListenerService`, which requires
  a one-time manual permission grant in Android's Settings.
