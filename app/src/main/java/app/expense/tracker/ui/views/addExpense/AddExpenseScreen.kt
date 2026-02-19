package app.expense.tracker.ui.views.addExpense

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.AddExpenseViewModel
import app.expense.tracker.R
import app.expense.tracker.ui.theme.LocalGlassmorphismColors
import app.expense.tracker.ui.utils.AmountInputDialog
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale.getDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onGoBack: () -> Unit,
    suggestionId: Long? = null,
    expenseId: Long? = null,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val addExpenseViewState = viewModel.addExpenseViewState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val glassColors = LocalGlassmorphismColors.current
    
    val amount =
        rememberSaveable(addExpenseViewState.amount) { mutableStateOf(addExpenseViewState.amount) }
    val amountDialogOpen = remember { mutableStateOf(false) }
    val paidTo =
        rememberSaveable(addExpenseViewState.paidTo) { mutableStateOf(addExpenseViewState.paidTo) }
    val categories =
        rememberSaveable(addExpenseViewState.categories) { mutableStateOf(addExpenseViewState.categories) }
    val time =
        rememberSaveable(addExpenseViewState.time) { mutableStateOf(addExpenseViewState.time) }
    
    val isFormValid = remember(amount.value, paidTo.value) {
        derivedStateOf {
            amount.value > 0.0 && paidTo.value.isNotBlank()
        }
    }
    val context = LocalContext.current

    LaunchedEffect(key1 = "${expenseId ?: ""} ${suggestionId ?: ""}") {
        viewModel.getAddExpenseViewState(expenseId, suggestionId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        text = if (expenseId == null) {
                            stringResource(R.string.add_expense)
                        } else {
                            stringResource(R.string.edit_expense)
                        },
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onGoBack() 
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Go Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    if (suggestionId != null || expenseId != null) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                coroutineScope.launch {
                                    if (suggestionId != null) {
                                        viewModel.deleteSuggestion(suggestionId)
                                    } else if (expenseId != null) {
                                        viewModel.deleteExpense(expenseId)
                                    }
                                    onGoBack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete_expense),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        coroutineScope.launch {
                            viewModel.addExpense(
                                expenseId = expenseId,
                                suggestionId = suggestionId,
                                amount = amount.value,
                                paidTo = paidTo.value,
                                categories = categories.value,
                                time = time.value
                            )
                            onGoBack()
                        }
                    },
                    enabled = isFormValid.value,
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.save),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        if (amountDialogOpen.value) {
            AmountInputDialog(
                amount = amount.value,
                onAmountEntered = { value ->
                    amount.value = value
                    amountDialogOpen.value = false
                },
                onDismiss = {
                    amountDialogOpen.value = false
                }
            )
        }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (addExpenseViewState.suggestionMessage != null) {
                Card(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
                            ),
                            shape = MaterialTheme.shapes.large
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = glassColors.surface.copy(alpha = 0.4f)
                    ),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = addExpenseViewState.suggestionMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Amount Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            amountDialogOpen.value = true
                        },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = glassColors.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.total_amount),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = NumberFormat.getCurrencyInstance().format(amount.value),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.padding(8.dp))
                
                // Date Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(110.dp)
                        .border(
                            width = 1.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.05f))
                            ),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = time.value

                            DatePickerDialog(
                                context,
                                { datePicker, _, _, _ ->
                                    calendar.set(Calendar.YEAR, datePicker.year)
                                    calendar.set(Calendar.MONTH, datePicker.month)
                                    calendar.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)
                                    time.value = calendar.timeInMillis
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(containerColor = glassColors.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.select_date),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = SimpleDateFormat("dd MMM yyyy", getDefault()).format(time.value),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            PaidToView(paidTo = paidTo)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CategoryView(categories = categories)
            
            Spacer(modifier = Modifier.height(100.dp)) // Extra space for button
        }
    }
}
