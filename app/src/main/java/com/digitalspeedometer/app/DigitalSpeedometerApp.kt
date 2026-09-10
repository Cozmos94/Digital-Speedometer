package com.digitalspeedometer.app

import android.app.Application
import com.digitalspeedometer.app.ads.InterstitialAdManager
import com.google.android.gms.ads.MobileAds

class DigitalSpeedometerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {
            // Kick off the first interstitial load as soon as the SDK is ready, so one's
            // likely already available the first time the user leaves the Speedometer screen.
            InterstitialAdManager.preload(this@DigitalSpeedometerApp)
        }
    }
}
