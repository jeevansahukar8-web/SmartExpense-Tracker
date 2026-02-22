package app.expense.tracker.ui.views.budget

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.BudgetViewModel
import app.expense.presentation.viewStates.BudgetViewState
import app.expense.presentation.viewStates.CategoryBudget
import app.expense.tracker.R
import app.expense.tracker.ui.theme.AccentBlue
import app.expense.tracker.ui.theme.AccentRed
import app.expense.tracker.ui.theme.AvatarColors
import app.expense.tracker.ui.theme.Secondary
import app.expense.tracker.ui.utils.AmountInputDialog
import kotlinx.coroutines.delay
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onGoBack: () -> Unit = {},
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val viewState by viewModel.getBudgetViewState().collectAsState(initial = BudgetViewState())
    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryBudget?>(null) }
    var pendingCategoryLimitUpdate by remember { mutableStateOf<Pair<String, Double>?>(null) }
    val haptic = LocalHapticFeedback.current

    // Entrance Animation Logic
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { 
        isVisible = true 
        haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Subtle feedback when screen loads
    }

    val chartAlpha by animateFloatAsState(if (isVisible) 1f else 0f, tween(500), label = "chartAlpha")
    val chartOffset by animateFloatAsState(if (isVisible) 0f else 20f, tween(500, easing = FastOutSlowInEasing), label = "chartOffset")

    if (showEditBudgetDialog) {
        AmountInputDialog(
            amount = viewState.totalLimit,
            onAmountEntered = { newBudget ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.updateMonthlyBudget(newBudget)
                showEditBudgetDialog = false
            },
            onDismiss = { showEditBudgetDialog = false }
        )
    }

    if (editingCategory != null) {
        AmountInputDialog(
            amount = editingCategory!!.limit,
            onAmountEntered = { newLimit ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                pendingCategoryLimitUpdate = editingCategory!!.name to newLimit
                editingCategory = null
            },
            onDismiss = { editingCategory = null }
        )
    }

    if (pendingCategoryLimitUpdate != null) {
        AlertDialog(
            onDismissRequest = { pendingCategoryLimitUpdate = null },
            title = { Text(stringResource(R.string.update_total_budget_title)) },
            text = { Text(stringResource(R.string.update_total_budget_desc)) },
            confirmButton = {
                TextButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val (category, limit) = pendingCategoryLimitUpdate!!
                    viewModel.updateCategoryBudgetWithChoice(category, limit, updateOverall = true)
                    pendingCategoryLimitUpdate = null
                }) {
                    Text(stringResource(R.string.adjust_total))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val (category, limit) = pendingCategoryLimitUpdate!!
                    viewModel.updateCategoryBudgetWithChoice(category, limit, updateOverall = false)
                    pendingCategoryLimitUpdate = null
                }) {
                    Text(stringResource(R.string.keep_total_fixed))
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detailed Budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onGoBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showEditBudgetDialog = true 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Budget", tint = AccentBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Detailed Pie Chart at the top
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .graphicsLayer {
                            alpha = chartAlpha
                            translationY = chartOffset
                        },
                    contentAlignment = Alignment.Center
                ) {
                    BudgetDonutChart(
                        modifier = Modifier.size(200.dp),
                        categories = viewState.categories,
                        totalLimit = viewState.totalLimit
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val progressPercent = if (viewState.totalLimit > 0) ((viewState.totalSpent / viewState.totalLimit) * 100).toInt() else 0
                        Text(
                            text = "$progressPercent%",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "USED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            letterSpacing = 1.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                // Recommendation Note
                if (viewState.recommendation != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = AccentBlue.copy(alpha = 0.1f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = viewState.recommendation!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Table Legend Section
                Text(
                    text = "Spending Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = Secondary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Table Header
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                            Text("CATEGORY", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("SPENT", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = Color.Gray, textAlign = TextAlign.End)
                            Text("LIMIT", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = Color.Gray, textAlign = TextAlign.End)
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        
                        viewState.categories.forEachIndexed { _, category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { 
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        editingCategory = category 
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(AvatarColors[Math.abs(category.colorIndex) % AvatarColors.size])
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = category.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = NumberFormat.getCurrencyInstance().format(category.spent),
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (category.spent > category.limit) AccentRed else Color.White,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = NumberFormat.getCurrencyInstance().format(category.limit),
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AccentBlue, // Highlight clickable limits
                                    textAlign = TextAlign.End
                                )
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                        }
                        
                        // TOTAL Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL",
                                modifier = Modifier.weight(1.5f),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = NumberFormat.getCurrencyInstance().format(viewState.totalSpent),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (viewState.totalSpent > viewState.totalLimit) AccentRed else Color.White,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = NumberFormat.getCurrencyInstance().format(viewState.totalLimit),
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text("Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(viewState.categories.size) { index ->
                val categoryBudget = viewState.categories[index]
                BudgetCategoryItem(
                    name = categoryBudget.name,
                    spent = categoryBudget.spent,
                    limit = categoryBudget.limit,
                    color = AvatarColors[Math.abs(categoryBudget.colorIndex) % AvatarColors.size],
                    index = index,
                    onEdit = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        editingCategory = categoryBudget 
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun BudgetDonutChart(
    modifier: Modifier = Modifier,
    categories: List<CategoryBudget>,
    totalLimit: Double
) {
    val animationProgress = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(categories) {
        animationProgress.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
        haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Subtle feedback when chart animation finishes
    }

    Canvas(modifier = modifier) {
        val strokeWidth = 16.dp.toPx()
        
        drawArc(
            color = Color.White.copy(alpha = 0.05f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        var startAngle = -90f
        categories.forEach { category ->
            val sweepAngle = if (totalLimit > 0) (category.spent / totalLimit).toFloat() * 360f * animationProgress.value else 0f
            drawArc(
                color = AvatarColors[Math.abs(category.colorIndex) % AvatarColors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun BudgetCategoryItem(name: String, spent: Double, limit: Double, color: Color, index: Int, onEdit: () -> Unit) {
    val isOverLimit = spent > limit
    val progressValue = if (limit > 0) (spent / limit).toFloat().coerceIn(0f, 1f) else 0f
    val haptic = LocalHapticFeedback.current

    // Entrance Animation Logic
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { 
        delay(index * 50L)
        isVisible = true 
        if (index == 0) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val alpha by animateFloatAsState(if (isVisible) 1f else 0f, tween(300), label = "alpha")
    val translationY by animateFloatAsState(if (isVisible) 0f else 20f, tween(300, easing = FastOutSlowInEasing), label = "translationY")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.alpha = alpha
                this.translationY = translationY
            }
            .clickable { 
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onEdit() 
            },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Secondary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(R.drawable.ic_home_custom), contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "${(progressValue * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isOverLimit) AccentRed else Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = if (isOverLimit) AccentRed else color,
                trackColor = Color.White.copy(alpha = 0.05f)
            )
        }
    }
}
