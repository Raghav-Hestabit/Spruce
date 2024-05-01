package com.example.spruce.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


val SpruceThemeColor: () -> Color = { Color(0xFF872BD5) }

val TransparentBlack: () -> Color = { Color(0x80000000) }
val ThemeColor: () -> Color = { Color(0xFF872BD5) }
val GradientCardBrush: () -> Brush =
    {
        Brush.verticalGradient(
            listOf(
                TransparentBlack(),
                Color.Transparent,
                TransparentBlack()
            )
        )
    }

val BottomCardBrush: () -> Brush =
    {
        Brush.verticalGradient(
            listOf(Color.Transparent, TransparentBlack())
        )
    }