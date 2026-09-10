package com.digitalspeedometer.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digitalspeedometer.app.ads.BannerAd
import com.digitalspeedometer.app.data.PreferencesManager
import com.digitalspeedometer.app.ui.theme.DigitalFontFamily
import com.digitalspeedometer.app.ui.theme.SpeedoAccent

private val TitleFontSize = 34.sp
private val StartButtonColor = Color(0xFFFF1744) // vivid red, matches the title's green glow treatment

/**
 * Landing screen: an ad banner top and bottom (this is the only screen with
 * ads — the Speedometer screen stays ad-free so it isn't distracting while
 * mounted on a windshield) and a tappable glowing "START" — not a Material
 * button, just clickable text sized twice the title, to fit the digital-clock
 * look.
 */
@Composable
fun HomeScreen(prefs: PreferencesManager, onStartClick: () -> Unit) {
    var showDisclaimer by remember { mutableStateOf(!prefs.hasAcknowledgedDisclaimer) }

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
                        fontSize = TitleFontSize,
                        letterSpacing = 3.sp,
                        color = SpeedoAccent,
                        shadow = Shadow(
                            color = SpeedoAccent.copy(alpha = 0.85f),
                            offset = Offset.Zero,
                            blurRadius = 28f,
                        ),
                    ),
                )
                Text(
                    text = "START",
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .clickable(
                            onClickLabel = "Start Speedometer",
                            role = Role.Button,
                            onClick = onStartClick,
                        ),
                    style = TextStyle(
                        fontFamily = DigitalFontFamily,
                        fontSize = TitleFontSize * 2,
                        letterSpacing = 4.sp,
                        color = StartButtonColor,
                        shadow = Shadow(
                            color = StartButtonColor.copy(alpha = 0.95f),
                            offset = Offset.Zero,
                            blurRadius = 48f,
                        ),
                    ),
                )
            }
        }

        BannerAd()
    }

    if (showDisclaimer) {
        DisclaimerDialog(
            onAcknowledge = {
                prefs.hasAcknowledgedDisclaimer = true
                showDisclaimer = false
            },
        )
    }
}
