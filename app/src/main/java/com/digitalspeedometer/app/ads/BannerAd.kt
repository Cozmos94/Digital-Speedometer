package com.digitalspeedometer.app.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * Google's official test banner ad unit ID. Safe to ship while developing/testing —
 * it always serves a clearly-labelled test ad and never earns real revenue.
 * Replace with your real AdMob banner ad unit ID before publishing (see README.md).
 */
const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

/** A single AdMob banner, sized to the full width of its container. */
@Composable
fun BannerAd(adUnitId: String = TEST_BANNER_AD_UNIT_ID, modifier: Modifier = Modifier) {
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
