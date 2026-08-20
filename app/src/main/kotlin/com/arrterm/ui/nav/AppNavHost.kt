package com.arrterm.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.ui.overseerr.OverseerrScreen
import com.arrterm.ui.overseerr.OverseerrViewModel
import com.arrterm.ui.radarr.RadarrScreen
import com.arrterm.ui.radarr.RadarrViewModel
import com.arrterm.ui.settings.SettingsScreen
import com.arrterm.ui.settings.SettingsViewModel
import com.arrterm.ui.sonarr.SonarrScreen
import com.arrterm.ui.sonarr.SonarrViewModel

@Composable
fun AppNavHost(repository: ServerConfigRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination
            NavigationBar {
                NavDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.RADARR.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(NavDestination.RADARR.route) {
                val vm: RadarrViewModel = viewModel(factory = RadarrViewModel.factory(repository))
                RadarrScreen(vm, onGoToSettings = { navController.navigate(NavDestination.SETTINGS.route) })
            }
            composable(NavDestination.SONARR.route) {
                val vm: SonarrViewModel = viewModel(factory = SonarrViewModel.factory(repository))
                SonarrScreen(vm, onGoToSettings = { navController.navigate(NavDestination.SETTINGS.route) })
            }
            composable(NavDestination.OVERSEERR.route) {
                val vm: OverseerrViewModel = viewModel(factory = OverseerrViewModel.factory(repository))
                OverseerrScreen(vm, onGoToSettings = { navController.navigate(NavDestination.SETTINGS.route) })
            }
            composable(NavDestination.SETTINGS.route) {
                val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(repository))
                SettingsScreen(vm)
            }
        }
    }
}
