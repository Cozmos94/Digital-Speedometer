package com.digitalspeedometer.app.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.digitalspeedometer.app.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Google's official test banner ad unit ID. Always serves a clearly-labelled
 * test ad and never earns real revenue — used for every non-release build.
 */
const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

/** The real banner ad unit — only actually served in release builds, see [DEFAULT_BANNER_AD_UNIT_ID]. */
const val REAL_BANNER_AD_UNIT_ID = "ca-app-pub-6794927175129725/8905286719"

/**
 * Test ads in debug builds (so tapping around during development never risks
 * AdMob flagging your own real ad unit for invalid traffic), the real ad unit
 * in release builds.
 */
val DEFAULT_BANNER_AD_UNIT_ID: String
    get() = if (BuildConfig.DEBUG) TEST_BANNER_AD_UNIT_ID else REAL_BANNER_AD_UNIT_ID

/** A single AdMob banner, sized to the full width of its container. */
@Composable
fun BannerAd(adUnitId: String = DEFAULT_BANNER_AD_UNIT_ID, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = {
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
