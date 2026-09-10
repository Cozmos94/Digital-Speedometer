package com.digitalspeedometer.app.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.digitalspeedometer.app.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/** Google's official test interstitial ad unit ID. Always serves a clearly-labelled test ad. */
const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

/**
 * Real interstitial ad unit ID — create one in AdMob (your app → Ad units → Add ad unit →
 * Interstitial) and paste it here. Deliberately left equal to the test ID for now so a release
 * build still works (just with test ads) if this hasn't been set yet.
 */
const val REAL_INTERSTITIAL_AD_UNIT_ID = TEST_INTERSTITIAL_AD_UNIT_ID

private val interstitialAdUnitId: String
    get() = if (BuildConfig.DEBUG) TEST_INTERSTITIAL_AD_UNIT_ID else REAL_INTERSTITIAL_AD_UNIT_ID

/**
 * Loads one interstitial ad ahead of time and shows it on request. A plain singleton is enough —
 * this app only ever wants at most one interstitial in flight, shown from one place (leaving the
 * Speedometer screen, every 3rd time — see PreferencesManager.speedometerExitCount).
 */
object InterstitialAdManager {
    private const val TAG = "InterstitialAdManager"

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    /** Safe to call repeatedly — no-ops if an ad is already loaded or a load is in flight. */
    fun preload(context: Context) {
        if (interstitialAd != null || isLoading) return
        isLoading = true
        InterstitialAd.load(
            context.applicationContext,
            interstitialAdUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    isLoading = false
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    interstitialAd = null
                    Log.d(TAG, "Failed to load interstitial: ${error.message}")
                }
            },
        )
    }

    /**
     * Shows the preloaded ad if one is ready, then calls [onComplete] once it's dismissed. If no
     * ad is ready yet (e.g. still loading, or the last load failed), calls [onComplete]
     * immediately instead — navigation should never get stuck waiting on an ad.
     */
    fun showIfAvailable(activity: Activity, onComplete: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            onComplete()
            preload(activity)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                preload(activity)
                onComplete()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                interstitialAd = null
                preload(activity)
                onComplete()
            }
        }
        ad.show(activity)
    }
}
