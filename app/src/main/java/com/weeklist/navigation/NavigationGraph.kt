package com.weeklist.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.weeklist.LogicAndData.Constants.Companion.navigationGraphEnterDelay
import com.weeklist.LogicAndData.Constants.Companion.navigationGraphEnterDur
import com.weeklist.LogicAndData.Constants.Companion.navigationGraphExitDur
import com.weeklist.LogicAndData.Constants.Companion.settingsEnterDur
import com.weeklist.database.Task
import com.weeklist.screens.GridScreen
import com.weeklist.screens.SettingsScreen
import com.weeklist.screens.TotalsScreen
import com.weeklist.screens.utils.MainViewModel


@Composable
fun NavigationGraph(context : Context,
                    navController: NavHostController,
                    tasks : List<Task>,
                    dispDates : List<String>,
                    //sharedPreferences : SharedPreferences,
                    modifier: Modifier = Modifier,
                    viewModel: MainViewModel = hiltViewModel(),
                    onDarkThemeChanged : (Boolean) -> Unit)
{
    NavHost(
        navController,
        startDestination = Destinations.GridDestination.route,
        enterTransition = { fadeIn(
            animationSpec = tween(
                durationMillis = navigationGraphEnterDur,
                delayMillis = navigationGraphEnterDelay,
                easing = EaseInOut
            )
        ) },
        exitTransition = { fadeOut(
            animationSpec = tween(
                durationMillis = navigationGraphExitDur,
                delayMillis = 0,
                easing = EaseIn
            )
        ) }
    ) {
        composable(
            Destinations.GridDestination.route,
        ) {
            GridScreen(
                viewModel = viewModel,
                tasks,
                dispDates
            )
        }
        composable(
            Destinations.TotalsDestination.route,
        ) {
            TotalsScreen(
                viewModel = viewModel,
                tasks = tasks,
                dispDates = dispDates
                //sharedPreferences = sharedPreferences
            )
        }
        composable(
            Destinations.NextWeekDestination.route,
        ){
            viewModel.nextWeek()
        }
        composable(Destinations.PrevWeekDestination.route){
            viewModel.prevWeek()
        }
        composable(
            Destinations.SettingsDestination.route,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(settingsEnterDur, easing = EaseOutSine),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 0,
                        easing = EaseOutSine)
                )
            },
        ) {
            SettingsScreen(
                viewModel = viewModel,
                onDarkThemeChanged = {
                    onDarkThemeChanged(it)
                    /*
                    navController.navigate(Destinations.SettingsDestinationDark.route){
                    popUpTo(navController.graph.findStartDestination().id){
                        saveState=true

                    }
                    launchSingleTop = true
                    restoreState = true
                }
*/
                }
            )
        }
        composable(
            Destinations.SettingsDestinationDark.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 300,
                        easing = EaseOutSine)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 0,
                        easing = EaseOutSine)
                )
            }
        ) {
            /*
            Log.v("navigated to ", "settings dark")
            SettingsScreen(
                viewModel = viewModel,
                onDarkThemeChanged = {navController.navigate(Destinations.SettingsDestination.route){
                    popUpTo(navController.graph.findStartDestination().id){
                        saveState=true

                    }
                    launchSingleTop = true
                    restoreState = true
                }
                }
            )
            */
        }

    }
}