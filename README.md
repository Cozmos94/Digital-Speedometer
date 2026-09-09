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

## Before publishing to Google Play

Right now the app uses **Google's official test AdMob IDs** (app ID and
banner ad unit ID), so it builds and shows clearly-labelled test ads
immediately with zero setup. Swap these out before you publish, or you won't
earn anything and may risk your AdMob account for invalid-traffic policy
reasons if real users see "test ad" banners:

1. Create an AdMob account and register the app to get a **real AdMob App ID**.
   Replace the value in [`app/src/main/AndroidManifest.xml`](app/src/main/AndroidManifest.xml)
   (the `com.google.android.gms.ads.APPLICATION_ID` meta-data).
2. Create a **real banner ad unit** and replace `TEST_BANNER_AD_UNIT_ID` in
   [`app/src/main/java/com/digitalspeedometer/app/ads/BannerAd.kt`](app/src/main/java/com/digitalspeedometer/app/ads/BannerAd.kt)
   with it (or pass your real ad unit ID into `BannerAd(adUnitId = ...)` at
   each call site in `HomeScreen.kt`).
3. Replace the placeholder launcher icon
   ([`ic_launcher_foreground.xml`](app/src/main/res/drawable/ic_launcher_foreground.xml))
   with real artwork — easiest via Android Studio's
   **Right-click `res` → New → Image Asset**.
4. Fill in a real Play Store listing (screenshots, privacy policy — required
   since the app requests location permission and shows ads/uses an
   advertising ID).

## Permissions

- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — required to read GPS
  speed. Requested at runtime when the user opens the Speedometer screen, not
  at install time.
- `INTERNET` / `ACCESS_NETWORK_STATE` — required by the Google Mobile Ads SDK
  to fetch and display banner ads.
