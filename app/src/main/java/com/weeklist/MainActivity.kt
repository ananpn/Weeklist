package com.weeklist

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.materialkolor.PaletteStyle
import com.weeklist.LogicAndData.Constants.Companion.appDarkThemeGetDelay
import com.weeklist.LogicAndData.Constants.Companion.appInitializedDelay
import com.weeklist.LogicAndData.transformIntToPaletteStyle
import com.weeklist.screens.utils.MainViewModel
import com.weeklist.ui.theme.AppMainTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val context : Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            /*
            LaunchedEffect(Unit){
                viewModel.darkTheme.collect {
                    val mode = when (it) {
                        true -> AppCompatDelegate.MODE_NIGHT_YES
                        false -> AppCompatDelegate.MODE_NIGHT_NO
                    }
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
            */
            lifecycleScope.launchWhenStarted {

            }


            var palette by remember{mutableStateOf(PaletteStyle.TonalSpot)}
            palette = transformIntToPaletteStyle(viewModel.paletteIn.collectAsState(initial = 0).value)
            var initialized by rememberSaveable{ mutableStateOf(false) }
            var darkTheme by rememberSaveable{ mutableStateOf(false) }

            //to stop flashing at start
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.DarkGray
            ){}
            LaunchedEffect(Unit){
                delay(appInitializedDelay)
                initialized=true
                delay(appDarkThemeGetDelay)
                darkTheme = viewModel.darkThemeGet()
            }

            AppMainTheme(
                paletteStyle = palette,
                viewModel = viewModel,
                darkTheme = darkTheme
            )
            {
                AnimatedVisibility(
                    visible = initialized,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = 0,
                            easing = EaseIn
                        )
                    ),
                    exit = fadeOut(
                        animationSpec = tween(
                            durationMillis = 100,
                            delayMillis = 0,
                            easing = EaseIn
                        )
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        MainApp(
                            context = context,
                            viewModel = viewModel,
                            onDarkThemeChanged = {
                                darkTheme = it
                            }
                        )

                        //this just updates the current date viewModel.currentDate after resuming
                        val lifecycleOwner = LocalLifecycleOwner.current
                        DisposableEffect(lifecycleOwner) {
                            val callback = object : LifecycleEventObserver {
                                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                                    if (event == Lifecycle.Event.ON_RESUME) {
                                        viewModel.currentDateSet()
                                    }
                                }
                            }
                            lifecycleOwner.lifecycle.addObserver(callback)

                            onDispose {
                                lifecycleOwner.lifecycle.removeObserver(callback)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

/*

    private fun observeThemeMode() {
        lifecycleScope.launchWhenStarted {
            viewModel.darkTheme.collect {
                val mode = when (it) {
                    true -> AppCompatDelegate.MODE_NIGHT_YES
                    false -> AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }
*/

    //how to set Theme from AppCompatDelegate..... defaultNightMode???
}




/*

@Composable
fun NavigationGraph(navController: NavHostController, taskViewModel : TaskViewModel) {
    NavHost(navController, startDestination = Destinations.TaskScreen.route) {
        composable(Destinations.TaskScreen.route) {
            TaskScreen(taskViewModel)
        }
        composable(Destinations.Favourite.route) {
            FavouriteScreen()
        }
        composable(Destinations.Notification.route) {
            NotificationScreen()
        }
        composable(Destinations.AddNew.route) {
        }
    }
}*/
