package com.arrterm.ui.nav

enum class NavDestination(val route: String, val label: String) {
    HOME("home", "Home"),
    RADARR("radarr", "Radarr"),
    SONARR("sonarr", "Sonarr"),
    OVERSEERR("overseerr", "Overseerr"),
    SETTINGS("settings", "Settings"),
}
