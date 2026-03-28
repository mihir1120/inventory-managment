package com.ashokvatika.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    background = BackgroundWhite,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark
)

private val DarkColors = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color.White,
    background = BackgroundWhite,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark
)

@Immutable
data class AshokvatikaColors(
    val background: Color,
    val card: Color,
    val title: Color,
    val text: Color,
    val hint: Color,
    val link: Color,
    val shadow: Color,
    val accent: Color
)

private val LocalExtraColors = staticCompositionLocalOf {
    AshokvatikaColors(
        background = BackgroundWhite,
        card = Color.White,
        title = OrangePrimary,
        text = TextDark,
        hint = HintGray,
        link = LinkBlue,
        shadow = SoftShadow,
        accent = OrangePrimary
    )
}

object AshokvatikaPalette {
    val colors: AshokvatikaColors
        @Composable
        get() = LocalExtraColors.current
}

@Composable
fun AshokvatikaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val materialScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = materialScheme,
        typography = AppTypography
    ) {
        CompositionLocalProvider(
            LocalExtraColors provides AshokvatikaColors(
                background = BackgroundWhite,
                card = Color.White,
                title = OrangePrimary,
                text = TextDark,
                hint = HintGray,
                link = LinkBlue,
                shadow = SoftShadow,
                accent = OrangePrimary
            ),
            content = content
        )
    }
}
