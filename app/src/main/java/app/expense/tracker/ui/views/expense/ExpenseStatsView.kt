package app.expense.tracker.ui.views.expense

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.ExpenseStatsViewModel
import app.expense.presentation.viewStates.ExpenseStats
import app.expense.tracker.R
import app.expense.tracker.ui.theme.AccentBlue
import app.expense.tracker.ui.theme.AvatarColors

@Composable
fun ExpenseStatsView(
    onBudgetClick: () -> Unit,
    viewModel: ExpenseStatsViewModel = hiltViewModel()
) {
    val expenseStats = viewModel.getExpenseStats().collectAsState(initial = ExpenseStats()).value
    val haptic = LocalHapticFeedback.current

    // Entrance Animation Logic
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { 
        isVisible = true 
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    val headerAlpha by animateFloatAsState(if (isVisible) 1f else 0f, tween(500), label = "headerAlpha")
    val headerOffset by animateFloatAsState(if (isVisible) 0f else -50f, tween(500, easing = FastOutSlowInEasing), label = "headerOffset")

    val cardAlpha by animateFloatAsState(if (isVisible) 1f else 0f, tween(500, delayMillis = 100), label = "cardAlpha")
    val cardOffset by animateFloatAsState(if (isVisible) 0f else 50f, tween(500, delayMillis = 100, easing = FastOutSlowInEasing), label = "cardOffset")

    val widgetsAlpha by animateFloatAsState(if (isVisible) 1f else 0f, tween(500, delayMillis = 200), label = "widgetsAlpha")
    val widgetsOffset by animateFloatAsState(if (isVisible) 0f else 50f, tween(500, delayMillis = 200, easing = FastOutSlowInEasing), label = "widgetsOffset")

    Column(modifier = Modifier.padding(16.dp)) {
        // Welcome Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = headerAlpha
                    translationY = headerOffset
                }
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_home_custom),
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "WELCOME BACK",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = expenseStats.userName.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bell_custom),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Monthly Spending Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = cardAlpha
                    translationY = cardOffset
                }
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    shape = MaterialTheme.shapes.extraLarge
                ),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = AccentBlue,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Monthly Spending",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = expenseStats.totalSpent,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_home_custom),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tracked from your SMS alerts",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Activity Widgets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = widgetsAlpha
                    translationY = widgetsOffset
                }
        ) {
            Card(
                modifier = Modifier.weight(1f).height(160.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ACTIVITY", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("Weekly", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    
                    WeeklyBarChart(
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        data = expenseStats.weeklySpent
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp)
                    .clickable { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onBudgetClick() 
                    },
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BUDGET", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(contentAlignment = Alignment.Center) {
                        MultiSegmentDonutChart(
                            modifier = Modifier.size(70.dp),
                            categorySpent = expenseStats.categorySpent,
                            totalBudget = expenseStats.totalBudget
                        )
                        Text(
                            text = "${(expenseStats.budgetProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "VIEW DETAILS",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyBarChart(
    modifier: Modifier = Modifier,
    data: List<Double>
) {
    val days = listOf("S", "M", "T", "W", "T", "F", "S")
    val haptic = LocalHapticFeedback.current
    
    LaunchedEffect(data) {
        if (data.isNotEmpty()) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val maxVal = (data.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
            data.forEach { value ->
                val heightPercentage = (value / maxVal).toFloat().coerceIn(0.05f, 1f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(heightPercentage)
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        .background(AccentBlue)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            days.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MultiSegmentDonutChart(
    modifier: Modifier = Modifier,
    categorySpent: Map<String, Double>,
    totalBudget: Double
) {
    val sweepProgress = remember { Animatable(0f) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(categorySpent) {
        sweepProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Canvas(modifier = modifier) {
        val strokeWidth = 10.dp.toPx() 
        
        drawArc(
            color = Color.Gray.copy(alpha = 0.15f),
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )

        var startAngle = -90f
        categorySpent.entries.forEach { entry ->
            val targetSweepAngle = if (totalBudget > 0) {
                (entry.value / totalBudget).toFloat() * 360f
            } else 0f
            
            val sweepAngle = targetSweepAngle * sweepProgress.value
            
            val color = AvatarColors[Math.abs(entry.key.hashCode()) % AvatarColors.size]
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            startAngle += targetSweepAngle
        }
    }
}
