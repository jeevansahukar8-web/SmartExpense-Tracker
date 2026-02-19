package app.expense.tracker.ui.views.expense

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.expense.presentation.viewModels.ExpenseStatsViewModel
import app.expense.presentation.viewStates.ExpenseStats
import app.expense.tracker.R
import app.expense.tracker.ui.theme.ChartBlue
import app.expense.tracker.ui.theme.ChartGreen
import app.expense.tracker.ui.theme.ChartPurple
import app.expense.tracker.ui.theme.ChartRed
import app.expense.tracker.ui.theme.ChartYellow
import app.expense.tracker.ui.theme.GlassmorphismSurface

@Composable
fun ExpenseStatsView(
    viewModel: ExpenseStatsViewModel = hiltViewModel()
) {
    val expenseStats = viewModel.getExpenseStats().collectAsState(initial = ExpenseStats()).value
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.default_padding))
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.1f))
                ),
                shape = MaterialTheme.shapes.extraLarge
            ),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = GlassmorphismSurface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.default_padding))) {
            if (expenseStats.monthlySpent.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_expenses),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 32.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                val lastMonthName = expenseStats.monthlySpent.keys.last()
                val lastMonthExpense = expenseStats.monthlySpent.values.last()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.monthly_expenses),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lastMonthName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = lastMonthExpense,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedDonutChart(
                            modifier = Modifier.size(80.dp),
                            categorySpent = expenseStats.categorySpent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedDonutChart(
    modifier: Modifier = Modifier,
    categorySpent: Map<String, Double>
) {
    val chartColors = listOf(ChartBlue, ChartGreen, ChartYellow, ChartRed, ChartPurple)
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(categorySpent) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    Canvas(modifier = modifier) {
        val total = categorySpent.values.sum()
        if (total == 0.0) return@Canvas

        val strokeWidth = 12.dp.toPx()
        var startAngle = -90f

        categorySpent.values.forEachIndexed { index, amount ->
            val sweepAngle = (amount / total).toFloat() * 360f * animationProgress.value
            drawArc(
                color = chartColors[index % chartColors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweepAngle
        }
    }
}
