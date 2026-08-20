package com.arrterm.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavDestination(val route: String, val label: String, val icon: ImageVector) {
    RADARR("radarr", "Radarr", Icons.Filled.Movie),
    SONARR("sonarr", "Sonarr", Icons.Filled.Tv),
    OVERSEERR("overseerr", "Overseerr", Icons.Filled.CloudQueue),
    SETTINGS("settings", "Settings", Icons.Filled.Settings),
}
