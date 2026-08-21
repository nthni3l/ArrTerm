package com.arrterm.ui.nav

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.arrterm.ui.common.AppOverlayHost
import com.arrterm.ui.home.HomeScreen
import com.arrterm.ui.home.HomeViewModel
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
import com.arrterm.ui.theme.AccentGreen
import com.arrterm.ui.theme.AppBackground
import com.arrterm.ui.theme.CardBorder
import com.arrterm.ui.theme.CardSurface
import com.arrterm.ui.theme.JetBrainsMono
import com.arrterm.ui.theme.TextMuted

@Composable
fun AppNavHost(repository: ServerConfigRepository) {
    val navController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = AppBackground,
            // The island bottom bar below handles its own navigationBarsPadding(), so
            // Scaffold shouldn't also reserve bottom system-bar space for content (that
            // would double-pad, leaving a gap above the floating island) — but it still
            // needs to keep clearing the top status bar, so only drop the bottom side.
            contentWindowInsets = WindowInsets.systemBars.only(
                WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
            ),
            bottomBar = {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination
                BottomNavIsland(
                    entries = NavDestination.entries,
                    isSelected = { d -> currentRoute?.hierarchy?.any { it.route == d.route } == true },
                    onSelect = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = NavDestination.HOME.route,
                modifier = Modifier.padding(padding),
            ) {
                composable(NavDestination.HOME.route) {
                    val vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(repository))
                    HomeScreen(
                        viewModel = vm,
                        onServiceClick = { service ->
                            val route = when (service) {
                                com.arrterm.data.settings.ServiceType.RADARR -> NavDestination.RADARR.route
                                com.arrterm.data.settings.ServiceType.SONARR -> NavDestination.SONARR.route
                                com.arrterm.data.settings.ServiceType.OVERSEERR -> NavDestination.OVERSEERR.route
                            }
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onGoToSettings = { navController.navigate(NavDestination.SETTINGS.route) },
                    )
                }
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

        AppOverlayHost()
    }
}

/**
 * Floating "island" nav bar: raised clear of the system gesture/button bar via
 * navigationBarsPadding(), with its own margin and full rounding so it reads as a
 * separate floating surface rather than a bar attached to the screen edge.
 */
@Composable
private fun BottomNavIsland(
    entries: List<NavDestination>,
    isSelected: (NavDestination) -> Boolean,
    onSelect: (NavDestination) -> Unit,
) {
    val shape = RoundedCornerShape(28.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(elevation = 12.dp, shape = shape, ambientColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f))
            .clip(shape)
            .background(CardSurface)
            .border(BorderStroke(1.dp, CardBorder), shape)
            .padding(top = 10.dp, bottom = 10.dp, start = 6.dp, end = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        entries.forEach { destination ->
            val selected = isSelected(destination)
            val color = if (selected) AccentGreen else TextMuted
            Column(
                modifier = Modifier
                    .clickable { onSelect(destination) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (destination) {
                    NavDestination.HOME -> HomeNavIcon(color)
                    NavDestination.RADARR -> RadarrNavIcon(color)
                    NavDestination.SONARR -> SonarrNavIcon(color)
                    NavDestination.OVERSEERR -> OverseerrNavIcon(color)
                    NavDestination.SETTINGS -> SettingsNavIcon(color)
                }
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))
                Text(
                    text = destination.label.uppercase(),
                    color = color,
                    fontFamily = JetBrainsMono,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
