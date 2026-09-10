package com.digitalspeedometer.app.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.digitalspeedometer.app.R

// "DS-Digital" — check its licence permits this app's (ad-monetized) commercial use
// before publishing; swap for a clearly-commercial-licensed font if not.

/** Used for the Home screen's title and "START" text. */
val DigitalFontFamily = FontFamily(Font(R.font.ds_digi))

/** The bold variant — used for the Speedometer screen's actual speed number/unit label. */
val DigitalBoldFontFamily = FontFamily(Font(R.font.ds_digib))
