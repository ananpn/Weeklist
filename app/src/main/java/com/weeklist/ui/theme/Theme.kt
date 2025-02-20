package com.weeklist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.weeklist.LogicAndData.Constants.Companion.defaultColorFloat
import com.weeklist.LogicAndData.transformFloatToColor
import com.weeklist.screens.utils.MainViewModel

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    secondary = secondaryLight,
    tertiary = tertiaryLight

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AppMainTheme(
    darkTheme: Boolean =
        isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    //dynamicColor: Boolean = true,
    //hyviä: Rainbow, TonalSpot, Neutral
    paletteStyle : PaletteStyle = PaletteStyle.TonalSpot,
    viewModel : MainViewModel,
    content: @Composable () -> Unit,
) {
    val seedColorFloat = viewModel.seedColorSlider.collectAsStateWithLifecycle(initialValue = defaultColorFloat)
    var seedColorValue by rememberSaveable{ mutableStateOf(defaultColorFloat)}
    seedColorValue = seedColorFloat.value

    DynamicMaterialTheme(
        seedColor = transformFloatToColor(
            float = seedColorValue,
            defaultColorFloat = defaultColorFloat
        ),
        useDarkTheme = darkTheme,
        content = content,
        style = paletteStyle
    )

    //val colorScheme = dynamicColorScheme(seedColor, darkTheme)
    /*
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    */
    //Dont know what this is
    /*
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
*/
//default theme

/*

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
*/



}

