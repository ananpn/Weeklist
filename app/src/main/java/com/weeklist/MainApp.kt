package com.weeklist

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.weeklist.LogicAndData.Constants.Companion.NEXT_WEEK
import com.weeklist.LogicAndData.Constants.Companion.PREVIOUS_WEEK
import com.weeklist.LogicAndData.Constants.Companion.SETTINGS_BUTTON
import com.weeklist.LogicAndData.TimeFunctions
import com.weeklist.LogicAndData.TimeFunctions.Companion.obtainCurrentWeek
import com.weeklist.LogicAndData.transformBGColor
import com.weeklist.navigation.Destinations
import com.weeklist.navigation.NavigationGraph
import com.weeklist.screens.utils.MainViewModel
import kotlinx.coroutines.delay

/*
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainApp(navController: NavHostController = rememberNavController()) {
    NavigationGraph(navController = navController)
}

 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(context : Context,
            //sharedPreferences : SharedPreferences,
            viewModel: MainViewModel,
            onDarkThemeChanged : (Boolean) -> Unit) {
    val navController: NavHostController = rememberNavController()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = remember(configuration){with(density) {configuration.screenWidthDp.dp}}
    val screenWidthPx = remember(configuration){with(density) {configuration.screenWidthDp.dp.toPx()}}
    val screenHeightDp = remember(configuration){with(density) {configuration.screenHeightDp.dp}}

    viewModel.setBGColors(
        newDef = MaterialTheme.colorScheme.background,
        newMod = transformBGColor(color = MaterialTheme.colorScheme.background)
    )

    //easiest way to get the last opened group from DataStore
    LaunchedEffect(Unit){
        viewModel.updateDispGroupId()
        viewModel.setUiState(dispDates = obtainCurrentWeek())
        viewModel.updateScreenSize(screenWidthDp, screenWidthPx, screenHeightDp)
    }


    //this might not be right but I dont care
    LaunchedEffect(Unit){
        while(true){
            viewModel.currentDateSet()
            delay(15000)
        }
    }


    LaunchedEffect(configuration){
        viewModel.updateScreenSize(screenWidthDp, screenWidthPx, screenHeightDp)
    }

    var dispDates by rememberSaveable{ mutableStateOf(viewModel.uiState.dispDates.map { date -> TimeFunctions.formatToString(date, "yyyyMMdd") }) }
    LaunchedEffect(viewModel.uiState.dispDates) {
        dispDates = viewModel.uiState.dispDates.map { date -> TimeFunctions.formatToString(date, "yyyyMMdd") }
    }
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = listOf())
    if (tasks.isEmpty()){
        viewModel.letNoTasks(true)
        val groups = viewModel.groups.collectAsState(initial = listOf())
        if (groups.value.isEmpty()){
            viewModel.letNoGroups(true)
        }
        else viewModel.letNoGroups(false)
    }
    else{
        viewModel.letNoGroups(false)
        viewModel.letNoTasks(false)
    }


    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                viewModel = viewModel
            )
        },
        content= { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){
                NavigationGraph(
                    context = context,
                    tasks = tasks,
                    dispDates = dispDates,
                    navController = navController,
                    viewModel = viewModel,
                    onDarkThemeChanged = {onDarkThemeChanged(it)}
                )
            }
        }

    )
}


@Composable
fun BottomBar(
    navController: NavHostController, viewModel: MainViewModel
) {
    val screens = listOf(
        Destinations.GridDestination, Destinations.TotalsDestination
    )

    NavigationBar(
        modifier = Modifier
            //.horizontalScroll(rememberScrollState())
        ,
        //containerColor = Color.LightGray,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->

            NavigationBarItem(
                label = {
                    Text(text = screen.title!!, maxLines = 1)
                },
                icon = {
                    Icon(imageVector = screen.icon!!, contentDescription = "")
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState=true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedTextColor = Color.Gray, selectedTextColor = Color.White
                ),
            )
        }
        NavigationBarItem(
            label = {Text(text = PREVIOUS_WEEK, maxLines = 1)
            },
            icon = {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "")
            },
            enabled = (currentRoute!=Destinations.SettingsDestination.route),
            onClick = {
                viewModel.prevWeek()
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedTextColor = Color.Gray, selectedTextColor = Color.White
            ),
            selected = false
        )
        NavigationBarItem(
            label = {Text(text = NEXT_WEEK, maxLines = 1)
            },
            icon = {
                Icon(Icons.Outlined.ArrowForward, contentDescription = "")
            },
            enabled = (currentRoute!=Destinations.SettingsDestination.route),
            onClick = {
                viewModel.nextWeek()
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedTextColor = Color.Gray, selectedTextColor = Color.White
            ),
            selected = false
        )
        NavigationBarItem(
            label = {Text(text = SETTINGS_BUTTON, maxLines = 1)
            },
            icon = {
                Icon(Icons.Outlined.Settings, contentDescription = "")
            },
            onClick = {
                navController.navigate(Destinations.SettingsDestination.route) {
                    popUpTo(navController.graph.findStartDestination().id){
                        saveState=true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedTextColor = Color.Gray, selectedTextColor = Color.White
            ),
            selected = currentRoute == Destinations.SettingsDestination.route

        )

    }
}