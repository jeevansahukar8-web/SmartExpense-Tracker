package app.expense.tracker.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import app.expense.tracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountInputDialog(
    amount: Double,
    onAmountEntered: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val amountState = remember { mutableStateOf(if (amount > 0) amount.toString() else "") }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = stringResource(id = R.string.total_amount)) },
        text = {
            Column {
                OutlinedTextField(
                    label = { Text(text = stringResource(R.string.amount)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = amountState.value,
                    onValueChange = { value ->
                        amountState.value = value
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = amountState.value.toDoubleOrNull() != null,
                onClick = {
                    onAmountEntered(amountState.value.toDoubleOrNull() ?: 0.0)
                }
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}
