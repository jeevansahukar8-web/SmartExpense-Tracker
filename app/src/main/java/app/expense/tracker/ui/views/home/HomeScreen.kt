package app.expense.tracker.ui.views.home

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.expense.presentation.viewModels.HomeScreenViewModel
import app.expense.tracker.R
import app.expense.tracker.ui.theme.GlassmorphismSurface
import app.expense.tracker.ui.utils.ScreenRoute
import app.expense.tracker.ui.utils.replace
import app.expense.tracker.ui.views.expense.ExpenseScreen
import app.expense.tracker.ui.views.suggestions.SuggestionsScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onAddExpense: () -> Unit,
    onEditExpense: (expenseId: Long) -> Unit,
    onAddSuggestion: (suggestionId: Long) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val navController = rememberNavController()
    val currentSelectedRoute = rememberSaveable { mutableStateOf(ScreenRoute.Expense.TEMPLATE) }
    val smsPermissionState = rememberPermissionState(Manifest.permission.READ_SMS)
    val haptic = LocalHapticFeedback.current

    val suggestionCount = viewModel.getSuggestionsCount().collectAsState(initial = 0).value
    val hasNotification = if (smsPermissionState.status != PermissionStatus.Granted) {
        true
    } else {
        suggestionCount > 0
    }
    LaunchedEffect(key1 = smsPermissionState.status) {
        if (smsPermissionState.status == PermissionStatus.Granted) {
            viewModel.syncSuggestions()
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onAddExpense() 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, 
                    contentDescription = "Add Expense",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
                        ),
                        shape = MaterialTheme.shapes.extraLarge
                    ),
                containerColor = GlassmorphismSurface,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    label = { 
                        Text(
                            text = stringResource(R.string.home),
                            style = MaterialTheme.typography.labelSmall
                        ) 
                    },
                    icon = { 
                        Icon(
                            painter = painterResource(id = R.drawable.ic_home_custom), 
                            contentDescription = null 
                        ) 
                    },
                    selected = currentSelectedRoute.value == ScreenRoute.Expense.TEMPLATE,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        currentSelectedRoute.value = ScreenRoute.Expense.TEMPLATE
                        navController.replace(currentSelectedRoute.value)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
                NavigationBarItem(
                    label = { 
                        Text(
                            text = stringResource(R.string.notifications),
                            style = MaterialTheme.typography.labelSmall
                        ) 
                    },
                    icon = {
                        BadgedBox(
                            badge = { 
                                if (hasNotification) {
                                    Badge(
                                        modifier = Modifier.size(8.dp),
                                        containerColor = Color.Red
                                    )
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_bell_custom), 
                                contentDescription = null 
                            )
                        }
                    },
                    selected = currentSelectedRoute.value == ScreenRoute.Suggestions.TEMPLATE,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        currentSelectedRoute.value = ScreenRoute.Suggestions.TEMPLATE
                        navController.replace(currentSelectedRoute.value)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            navController = navController,
            startDestination = ScreenRoute.Expense.TEMPLATE,
        ) {
            composable(ScreenRoute.Expense.TEMPLATE) { ExpenseScreen(onEditExpense = onEditExpense) }
            composable(ScreenRoute.Suggestions.TEMPLATE) { SuggestionsScreen(onAddSuggestion = onAddSuggestion) }
        }
    }
}

/**
 * Currently preview does not support view model provided by hilt as it expects AndroidEntryPoint
 */
@Preview
@Composable
fun PreviewDashBoardView() {
    HomeScreen({}, {}, {})
}
