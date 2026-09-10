package com.digitalspeedometer.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitalspeedometer.app.R
import com.digitalspeedometer.app.ads.BannerAd
import com.digitalspeedometer.app.ui.theme.SpeedoAccent

// "DS-Digital" — check its licence permits this app's (ad-monetized) commercial use
// before publishing; swap for a clearly-commercial-licensed font if not.
private val DigitalFontFamily = FontFamily(Font(R.font.ds_digi))

/**
 * Landing screen: an ad banner top and bottom (this is the only screen with
 * ads — the Speedometer screen stays ad-free so it isn't distracting while
 * mounted on a windshield) and a single "Start Speedometer" button.
 */
@Composable
fun HomeScreen(onStartClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        BannerAd()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Digital Speedometer",
                    style = TextStyle(
                        fontFamily = DigitalFontFamily,
                        fontSize = 34.sp,
                        letterSpacing = 3.sp,
                        color = SpeedoAccent,
                        shadow = Shadow(
                            color = SpeedoAccent.copy(alpha = 0.85f),
                            offset = Offset.Zero,
                            blurRadius = 28f,
                        ),
                    ),
                )
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.padding(top = 32.dp),
                ) {
                    Text("Start Speedometer")
                }
            }
        }

        BannerAd()
    }
}
