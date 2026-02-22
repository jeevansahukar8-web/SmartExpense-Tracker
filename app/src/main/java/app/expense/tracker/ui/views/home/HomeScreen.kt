package app.expense.tracker.ui.views.home

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.expense.presentation.viewModels.HomeScreenViewModel
import app.expense.presentation.viewModels.SettingsViewModel
import app.expense.tracker.R
import app.expense.tracker.ui.theme.AccentBlue
import app.expense.tracker.ui.utils.ScreenRoute
import app.expense.tracker.ui.utils.replace
import app.expense.tracker.ui.views.budget.BudgetScreen
import app.expense.tracker.ui.views.expense.ExpenseScreen
import app.expense.tracker.ui.views.profile.ProfileScreen
import app.expense.tracker.ui.views.reports.ReportsScreen
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
    viewModel: HomeScreenViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val navController = rememberNavController()
    val currentSelectedRoute = rememberSaveable { mutableStateOf(ScreenRoute.Expense.TEMPLATE) }
    val smsPermissionState = rememberPermissionState(Manifest.permission.READ_SMS)
    val haptic = LocalHapticFeedback.current

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
                containerColor = AccentBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .offset(y = 50.dp)
                    .size(56.dp)
                    .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus_custom), 
                    contentDescription = "Add Expense",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .height(80.dp),
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    label = { Text("Home") },
                    icon = { Icon(painterResource(R.drawable.ic_home_custom), null) },
                    selected = currentSelectedRoute.value == ScreenRoute.Expense.TEMPLATE,
                    onClick = {
                        currentSelectedRoute.value = ScreenRoute.Expense.TEMPLATE
                        navController.replace(currentSelectedRoute.value)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = AccentBlue,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    label = { Text("Reports") },
                    icon = { Icon(painterResource(R.drawable.ic_bell_custom), null) },
                    selected = currentSelectedRoute.value == ScreenRoute.Reports.TEMPLATE,
                    onClick = {
                        currentSelectedRoute.value = ScreenRoute.Reports.TEMPLATE
                        navController.replace(currentSelectedRoute.value)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = AccentBlue,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                
                Spacer(Modifier.weight(0.1f))

                NavigationBarItem(
                    label = { Text("Budget") },
                    icon = { Icon(painterResource(R.drawable.ic_money_custom), null) },
                    selected = currentSelectedRoute.value == ScreenRoute.Budget.TEMPLATE,
                    onClick = {
                        currentSelectedRoute.value = ScreenRoute.Budget.TEMPLATE
                        navController.replace(currentSelectedRoute.value)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = AccentBlue,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    label = { Text("Profile") },
                    icon = { Icon(painterResource(R.drawable.ic_usersettings_custom), null) },
                    selected = currentSelectedRoute.value == ScreenRoute.Profile.TEMPLATE,
                    onClick = {
                        currentSelectedRoute.value = ScreenRoute.Profile.TEMPLATE
                        navController.replace(currentSelectedRoute.value)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentBlue,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = AccentBlue,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
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
            composable(ScreenRoute.Expense.TEMPLATE) { 
                ExpenseScreen(
                    onEditExpense = onEditExpense,
                    onBudgetClick = {
                        currentSelectedRoute.value = ScreenRoute.Budget.TEMPLATE
                        navController.replace(currentSelectedRoute.value)
                    }
                ) 
            }
            composable(ScreenRoute.Reports.TEMPLATE) { ReportsScreen(onAddSuggestion = onAddSuggestion) }
            composable(ScreenRoute.Budget.TEMPLATE) { 
                BudgetScreen(onGoBack = {
                    currentSelectedRoute.value = ScreenRoute.Expense.TEMPLATE
                    navController.replace(currentSelectedRoute.value)
                }) 
            }
            composable(ScreenRoute.Profile.TEMPLATE) { 
                ProfileScreen(onLogout = { settingsViewModel.logout() })
            }
        }
    }
}
