package com.arrterm.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.arrterm.data.settings.ServerConfigRepository
import com.arrterm.ui.common.GlassSurface
import com.arrterm.ui.overseerr.OverseerrScreen
import com.arrterm.ui.overseerr.OverseerrViewModel
import com.arrterm.ui.radarr.MovieDetailScreen
import com.arrterm.ui.radarr.MovieDetailViewModel
import com.arrterm.ui.radarr.RadarrScreen
import com.arrterm.ui.radarr.RadarrViewModel
import com.arrterm.ui.settings.SettingsScreen
import com.arrterm.ui.settings.SettingsViewModel
import com.arrterm.ui.sonarr.SeriesDetailScreen
import com.arrterm.ui.sonarr.SeriesDetailViewModel
import com.arrterm.ui.sonarr.SonarrScreen
import com.arrterm.ui.sonarr.SonarrViewModel
import com.arrterm.ui.theme.SkyBlueDeep
import com.arrterm.ui.theme.SkyGradient

@Composable
fun AppNavHost(repository: ServerConfigRepository) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        modifier = Modifier.background(SkyGradient),
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination
            Box(modifier = Modifier.padding(16.dp)) {
                GlassSurface(shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp), elevation = 14.dp) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        NavDestination.entries.forEach { destination ->
                            val selected = currentRoute?.hierarchy?.any { it.route == destination.route } == true
                            NavBubbleItem(
                                destination = destination,
                                selected = selected,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                            )
                        }
                    }
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
                RadarrScreen(
                    viewModel = vm,
                    onGoToSettings = { navController.navigate(NavDestination.SETTINGS.route) },
                    onMovieClick = { movieId -> navController.navigate("radarr/movie/$movieId") },
                )
            }
            composable(NavDestination.SONARR.route) {
                val vm: SonarrViewModel = viewModel(factory = SonarrViewModel.factory(repository))
                SonarrScreen(
                    viewModel = vm,
                    onGoToSettings = { navController.navigate(NavDestination.SETTINGS.route) },
                    onSeriesClick = { seriesId -> navController.navigate("sonarr/series/$seriesId") },
                )
            }
            composable(NavDestination.OVERSEERR.route) {
                val vm: OverseerrViewModel = viewModel(factory = OverseerrViewModel.factory(repository))
                OverseerrScreen(vm, onGoToSettings = { navController.navigate(NavDestination.SETTINGS.route) })
            }
            composable(NavDestination.SETTINGS.route) {
                val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(repository))
                SettingsScreen(vm)
            }
            composable(
                route = "radarr/movie/{movieId}",
                arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                val vm: MovieDetailViewModel = viewModel(
                    key = "movie_$movieId",
                    factory = MovieDetailViewModel.factory(repository, movieId),
                )
                MovieDetailScreen(viewModel = vm, onBack = { navController.popBackStack() })
            }
            composable(
                route = "sonarr/series/{seriesId}",
                arguments = listOf(navArgument("seriesId") { type = NavType.IntType }),
            ) { backStackEntry ->
                val seriesId = backStackEntry.arguments?.getInt("seriesId") ?: return@composable
                val vm: SeriesDetailViewModel = viewModel(
                    key = "series_$seriesId",
                    factory = SeriesDetailViewModel.factory(repository, seriesId),
                )
                SeriesDetailScreen(viewModel = vm, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun NavBubbleItem(destination: NavDestination, selected: Boolean, onClick: () -> Unit) {
    val content: @Composable () -> Unit = {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center,
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            ) {
                Icon(
                    destination.icon,
                    contentDescription = destination.label,
                    tint = if (selected) SkyBlueDeep else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (selected) {
                    Text(
                        destination.label,
                        color = SkyBlueDeep,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
    if (selected) {
        GlassSurface(
            shape = CircleShape,
            tint = SkyBlueDeep,
            elevation = 6.dp,
            modifier = Modifier.clickable(onClick = onClick),
        ) { content() }
    } else {
        Box(modifier = Modifier.clickable(onClick = onClick)) { content() }
    }
}
