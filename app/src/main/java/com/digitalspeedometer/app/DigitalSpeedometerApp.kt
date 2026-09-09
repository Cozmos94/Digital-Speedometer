package com.digitalspeedometer.app

import android.app.Application
import com.google.android.gms.ads.MobileAds

class DigitalSpeedometerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}
