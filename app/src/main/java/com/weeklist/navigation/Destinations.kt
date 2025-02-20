package com.weeklist.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destinations(
    val route: String,
    val title: String? = null,
    val icon: ImageVector? = null
) {
    object GridDestination : Destinations(
        route = "grid_screen",
        title = "Main",
        icon = Icons.Outlined.List
    )

    object TotalsDestination : Destinations(
        route = "totals_screen",
        title = "Summary",
        icon = Icons.Outlined.CheckCircle
    )

    object PrevWeekDestination : Destinations(
        route = "prev_week",
        title = "Previous Week",
        icon = Icons.Outlined.ArrowBack
    )

    object NextWeekDestination : Destinations(
        route = "next_week",
        title = "Next Week",
        icon = Icons.Outlined.ArrowForward
    )

    object SettingsDestination : Destinations(
        route = "settings_screen",
        title = "Settings",
        icon = Icons.Outlined.Add
    )

    object SettingsDestinationDark : Destinations(
        route = "settings_screen_dark",
        title = "Settings",
        icon = Icons.Outlined.Add
    )

}

