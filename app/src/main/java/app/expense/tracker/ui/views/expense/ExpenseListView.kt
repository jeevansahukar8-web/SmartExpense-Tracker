package app.expense.tracker.ui.views.expense

import android.icu.lang.UCharacter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.ExpenseListViewModel
import app.expense.presentation.viewStates.ExpenseListState
import app.expense.tracker.R
import app.expense.tracker.ui.theme.AccentBlue
import app.expense.tracker.ui.theme.AvatarColors
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
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "See All",
                style = MaterialTheme.typography.labelLarge,
                color = AccentBlue,
                fontWeight = FontWeight.SemiBold
            )
        }

        LazyColumn {
            expenseListState.dateExpenseMap.forEach { (dateString, expenseItems) ->
                item {
                    Text(
                        text = dateString.uppercase(),
                        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        letterSpacing = 1.sp
                    )
                }

                items(expenseItems.size) { index ->
                    val expenseItem = expenseItems[index]
                    TransactionItem(expenseItem, onEditExpense)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(
    expenseItem: ExpenseListState.Item,
    onEditExpense: (expenseId: Long) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onEditExpense(expenseItem.id)
            },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModernAvatar(expenseItem)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getFormattedPaidTo(expenseItem),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${expenseItem.category} • ${expenseItem.time}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "-${expenseItem.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "AUTO-DETECTED",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
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
    val colorIndex = name.length % AvatarColors.size
    val avatarColor = AvatarColors[colorIndex]

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(avatarColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_home_custom), // Placeholder
            contentDescription = null,
            tint = avatarColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
