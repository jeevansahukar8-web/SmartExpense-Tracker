package app.expense.tracker.ui.views.expense

import android.icu.lang.UCharacter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.ExpenseListViewModel
import app.expense.presentation.viewStates.ExpenseListState
import app.expense.tracker.R
import app.expense.tracker.ui.theme.AvatarColors
import app.expense.tracker.ui.theme.LocalGlassmorphismColors
import java.util.Locale.getDefault

@Composable
fun ExpenseListView(
    onEditExpense: (expenseId: Long) -> Unit,
    viewModel: ExpenseListViewModel = hiltViewModel()
) {
    val expenseListState =
        viewModel.getExpenseListState().collectAsState(initial = ExpenseListState()).value

    if (expenseListState.dateExpenseMap.isEmpty()) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.empty_expense_message),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    } else {
        ShowExpenseList(expenseListState, onEditExpense)
    }
}

@Composable
private fun ShowExpenseList(
    expenseListState: ExpenseListState,
    onEditExpense: (expenseId: Long) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val glassColors = LocalGlassmorphismColors.current

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.transactions),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 12.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn {
            items(expenseListState.dateExpenseMap.size) { pos ->
                val dateString = expenseListState.dateExpenseMap.keys.toList()[pos]
                val expenseItems = expenseListState.dateExpenseMap[dateString]

                Text(
                    text = dateString,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.5f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = glassColors.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        expenseItems?.forEach { expenseItem ->
                            ListItem(
                                modifier = Modifier.clickable(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onEditExpense(expenseItem.id)
                                }),
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                                headlineContent = {
                                    Text(
                                        text = getFormattedPaidTo(expenseItem),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                supportingContent = {
                                    Text(
                                        text = expenseItem.category ?: "",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingContent = {
                                    ModernAvatar(expenseItem)
                                },
                                trailingContent = {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = expenseItem.amount,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = expenseItem.time,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getFormattedPaidTo(expenseItem: ExpenseListState.Item) =
    UCharacter.toTitleCase(
        getDefault(),
        expenseItem.paidTo ?: stringResource(R.string.unknown_paid_to),
        null,
        0
    )

@Composable
private fun ModernAvatar(expenseItem: ExpenseListState.Item) {
    val name = expenseItem.paidTo ?: "Others"
    val firstChar = name.firstOrNull()?.uppercase() ?: "O"
    val colorIndex = name.length % AvatarColors.size
    val avatarColor = AvatarColors[colorIndex]

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(avatarColor, avatarColor.copy(alpha = 0.6f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = firstChar,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
