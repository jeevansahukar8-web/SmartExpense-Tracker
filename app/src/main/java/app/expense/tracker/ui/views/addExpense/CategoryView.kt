package app.expense.tracker.ui.views.addExpense

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.expense.tracker.R
import app.expense.tracker.ui.utils.CategoryInputDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryView(
    categories: MutableState<List<String>>
) {
    val categoryDialogOpen = remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Column {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.category),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                categoryDialogOpen.value = true 
            }) {
                Icon(
                    imageVector = Icons.Outlined.AddCircle,
                    contentDescription = stringResource(R.string.add_category),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            if (categoryDialogOpen.value) {
                CategoryInputDialog(onCategoryEntered = { category ->
                    categories.value = categories.value.toMutableList().apply {
                        add(0, category)
                    }
                    categoryDialogOpen.value = false
                }, onDismiss = { categoryDialogOpen.value = false })
            }
        }

        if (categories.value.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                categories.value.filter { it.isNotBlank() }.forEach { item ->
                    InputChip(
                        modifier = Modifier.padding(end = 8.dp),
                        selected = true,
                        enabled = true,
                        shape = MaterialTheme.shapes.medium,
                        colors = InputChipDefaults.inputChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = InputChipDefaults.inputChipBorder(
                            enabled = true,
                            selected = true,
                            selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            selectedBorderWidth = 1.dp
                        ),
                        label = { 
                            Text(
                                text = item, 
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            ) 
                        },
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            categories.value =
                                categories.value.toMutableList().apply { remove(item) }
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = stringResource(id = R.string.remove_category),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}
