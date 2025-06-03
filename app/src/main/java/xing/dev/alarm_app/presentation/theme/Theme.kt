package xing.dev.alarm_app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import br.ufc.insight.ai4wellness.presentation.theme.SuperAppColors.colorBlue
import br.ufc.insight.ai4wellness.presentation.theme.SuperAppColors.colorPink

private val DarkColorScheme = darkColorScheme(
    primary = Color(0XFF096B68),
    primaryContainer = Color(0XFF129990),
    onPrimary = Color(0xFF66ffc7),
    secondary = Color(0XFFFFFBDE),
    secondaryContainer = colorBlue,
    onSecondaryContainer = Color(0XFFcbc8b9),
    surface = Color.Black,
    onSurface = Color(0XFFDDDDDD),
    error = colorPink,
    onSecondary = Color.Black,
    onError = Color.Black,
    background = Color.Black,
    onPrimaryFixed = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0XFF096B68),
    primaryContainer = Color(0XFF129990),
    onPrimary = Color(0xFF66ffc7),
    secondary = Color(0XFFFFFBDE),
    secondaryContainer = colorBlue,
    onSecondaryContainer = Color(0XFFcbc8b9),
    surface = Color.Black,
    onSurface = Color(0XFFDDDDDD),
    error = colorPink,
    onSecondary = Color.White,
    onError = Color.Black,
    background = Color.White,
    onPrimaryFixed = Color.Black
)


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlarmAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        shapes = AlarmAppShapes,
        typography = Typography,
        content = content
    )
}