# Digital Speedometer

An Android app that turns your phone into a digital speedometer, designed to be
mounted on your dashboard and read via its reflection in the windshield.

## Features

- **Mirrored by default** — the readout is flipped so its windshield reflection
  reads correctly. Tap the flip icon (top-right on the speedometer screen) to
  un-mirror it if you'd rather read the phone screen directly.
- **GPS-based speed** — uses the device's fused location provider (Google Play
  services), not a simulation.
- **KM/H ⇄ MPH** toggle button, bottom-right of the speedometer screen.
- **Custom colours** for both the number and the background, via the palette
  button next to the units toggle.
- Settings (mirror state, units, colours) persist between launches.
- **Two screens:**
  1. **Home** — banner ads top and bottom, single "Start Speedometer" button.
  2. **Speedometer** — full-screen, completely ad-free.

## Tech stack

- Kotlin + Jetpack Compose (Material 3)
- Google Play services Location (`FusedLocationProviderClient`) for speed
- Google Mobile Ads SDK (AdMob) for the home-screen banners
- Plain `SharedPreferences` for settings — no extra persistence dependency
- minSdk 26 (Android 8.0+), compileSdk/targetSdk 34

## Opening the project

This project was authored outside Android Studio, so it doesn't ship a
`gradlew`/Gradle wrapper jar. Just open the folder in Android Studio
(**File → Open**, pick `DigitalSpeedometer`) — it will detect the Gradle
project and offer to generate the wrapper / sync using its bundled Gradle.
Then **Run ▶** on a device or emulator as normal.

> Note: an emulator won't have real GPS movement — test actual speed readings
> on a physical device, or use the emulator's Extended Controls → Location tab
> to simulate a moving route.

## AdMob: real ads vs test ads

The real AdMob App ID and banner ad unit ID are already wired in, but only
for **release** builds:

- **Debug builds** (what Android Studio's Run ▶ button gives you) always use
  Google's official test IDs — real, clearly-labelled test ads, zero risk of
  your own testing taps getting flagged as invalid traffic on the real
  account.
- **Release builds** (Build → Generate Signed Bundle/APK, or any build with
  `buildType = "release"`) use the real IDs and earn real revenue.

This is controlled by `manifestPlaceholders["admobAppId"]` per build type in
[`app/build.gradle.kts`](app/build.gradle.kts) (App ID) and
`DEFAULT_BANNER_AD_UNIT_ID` in
[`BannerAd.kt`](app/src/main/java/com/digitalspeedometer/app/ads/BannerAd.kt)
(ad unit ID, switches on `BuildConfig.DEBUG`). If you ever change ad units or
create new ones (e.g. an interstitial), update the `REAL_...` constants
there — never test by tapping a release build's ads.

## Before publishing to Google Play

1. Replace the placeholder launcher icon
   ([`ic_launcher_foreground.xml`](app/src/main/res/drawable/ic_launcher_foreground.xml))
   with real artwork — easiest via Android Studio's
   **Right-click `res` → New → Image Asset**.
2. Fill in a real Play Store listing (screenshots, privacy policy — required
   since the app requests location permission and shows ads/uses an
   advertising ID).
3. Build a signed release build (Build → Generate Signed Bundle/APK) — that's
   the build that will actually serve real ads and earn revenue.

## Permissions

- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — required to read GPS
  speed. Requested at runtime when the user opens the Speedometer screen, not
  at install time.
- `INTERNET` / `ACCESS_NETWORK_STATE` — required by the Google Mobile Ads SDK
  to fetch and display banner ads.
