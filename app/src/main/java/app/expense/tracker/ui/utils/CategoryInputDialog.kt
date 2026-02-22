package app.expense.tracker.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.AddExpenseViewModel
import app.expense.tracker.R
import app.expense.tracker.ui.theme.AccentBlue
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryInputDialog(
    onCategoryEntered: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val viewState by viewModel.addExpenseViewState.collectAsState()
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var customCategory by rememberSaveable { mutableStateOf("") }
    
    val predefinedCategories = listOf(
        CategoryItem("Food & Dining", R.drawable.ic_food_custom),
        CategoryItem("Transport", R.drawable.ic_transport_custom),
        CategoryItem("Shopping", R.drawable.ic_shopping_custom),
        CategoryItem("Entertainment", R.drawable.ic_entertainment_custom),
        CategoryItem("Health", R.drawable.ic_health_custom),
        CategoryItem("Bills", R.drawable.ic_bills_custom),
        CategoryItem("Other", R.drawable.ic_other_custom)
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Category",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance().format(viewState.amount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue
                    )
                }
                
                if (viewState.paidTo.isNotBlank()) {
                    Text(
                        text = viewState.paidTo,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(180.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(predefinedCategories) { item ->
                        val isSelected = selectedCategory == item.name
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(if (isSelected) AccentBlue.copy(alpha = 0.1f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) AccentBlue else Color.Gray.copy(alpha = 0.2f),
                                    MaterialTheme.shapes.medium
                                )
                                .clickable { 
                                    selectedCategory = item.name
                                    if (item.name != "Other") {
                                        customCategory = ""
                                    }
                                }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(item.icon),
                                    contentDescription = null,
                                    tint = if (isSelected) AccentBlue else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) AccentBlue else Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                
                AnimatedVisibility(
                    visible = selectedCategory == "Other",
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Text(
                            text = "CUSTOM CATEGORY",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customCategory,
                            onValueChange = { customCategory = it },
                            placeholder = { Text("Enter category name...", color = Color.Gray, fontSize = 14.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Gray.copy(alpha = 0.2f),
                                focusedBorderColor = AccentBlue
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { 
                            val result = if (selectedCategory == "Other") customCategory else selectedCategory
                            if (result.isNotBlank()) {
                                onCategoryEntered(result)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = selectedCategory.isNotBlank() && (selectedCategory != "Other" || customCategory.isNotBlank()),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

data class CategoryItem(val name: String, val icon: Int)
