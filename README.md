# ArrTerm

A native Android app (Kotlin + Jetpack Compose) for managing self-hosted Radarr, Sonarr,
and Overseerr servers from your phone. Enter your own server URL and API key per service —
nothing is hardcoded, so anyone can point it at their own instances.

Distributed as a sideloaded debug APK (no Play Store).

## Features

- Light, airy "crystal bubble" UI — translucent glass surfaces on every interactive
  element (buttons, badges, cards, nav bar)
- Per-service (Radarr / Sonarr / Overseerr) settings screen with URL + API key entry and a
  "Test Connection" check
- Radarr: movie library and download queue, tap into a movie to search for a release,
  toggle monitored, or delete it
- Sonarr: series library and download queue, tap into a series for the same actions
- Overseerr: pending requests with Approve / Decline

## Building

Requires a local JDK 17+, the Android SDK, and Gradle — the project ships its own Gradle
Wrapper. Point `local.properties` (`sdk.dir=...`) and `gradle.properties`
(`org.gradle.java.home=...`) at your toolchain, then:

```
./gradlew assembleDebug
```

The output APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

## Security notes

- API keys are stored in `EncryptedSharedPreferences` on-device.
- Cleartext HTTP is allowed (see `network_security_config.xml`) since self-hosted *arr
  servers are commonly plain HTTP on a LAN. HTTPS certificate validation is not overridden.

See [CHANGELOG.md](CHANGELOG.md) for release notes.
