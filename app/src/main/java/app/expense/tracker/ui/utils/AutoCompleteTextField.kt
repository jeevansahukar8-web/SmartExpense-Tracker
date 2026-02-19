package app.expense.tracker.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.expense.tracker.ui.theme.LocalGlassmorphismColors

@ExperimentalMaterial3Api
@Composable
fun AutoCompleteTextField(
    label: String,
    value: String,
    suggestions: List<String>,
    onCategoryEntered: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val glassColors = LocalGlassmorphismColors.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            label = { 
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                ) 
            },
            value = value,
            onValueChange = { newValue ->
                expanded = true
                onCategoryEntered(newValue)
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = glassColors.surface.copy(alpha = 0.5f),
                unfocusedContainerColor = glassColors.surface.copy(alpha = 0.5f)
            ),
            shape = MaterialTheme.shapes.large,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(glassColors.surface)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
                    ),
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            suggestions.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = selectionOption,
                            style = MaterialTheme.typography.bodyLarge
                        ) 
                    },
                    onClick = {
                        expanded = false
                        onCategorySelect(selectionOption)
                    },
                )
            }
        }
    }
}
