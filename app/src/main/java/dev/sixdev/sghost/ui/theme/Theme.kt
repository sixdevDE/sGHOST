package dev.sixdev.sghost.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BrandColor = Color(0xFF2B104E)
private val OnBrand = Color(0xFFFFFFFF)

private val SGhostDarkColors = darkColorScheme(
    primary = BrandColor,
    onPrimary = OnBrand,
    surface = BrandColor,
    onSurface = OnBrand,
    background = BrandColor,
    onBackground = OnBrand,
    secondary = BrandColor,
    onSecondary = OnBrand,
    tertiary = BrandColor,
    onTertiary = OnBrand,
)

@Composable
fun SGhostTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SGhostDarkColors,
        typography = Typography(),
        content = {
            Surface(color = SGhostDarkColors.background, contentColor = SGhostDarkColors.onBackground) {
                content()
            }
        }
    )
}
