package com.example.splitpro.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitpro.R
import com.example.splitpro.ui.theme.ExpenseRed
import com.example.splitpro.ui.theme.PrimaryLight
import com.example.splitpro.ui.theme.SettledGreen
import com.example.splitpro.viewmodels.ExpenseDataPoint
import com.example.splitpro.viewmodels.ExpenseSummary
import com.example.splitpro.viewmodels.HomeViewModel
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface,
                        PrimaryLight.copy(alpha = 0.15f)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .graphicsLayer {
                            alpha = if (isVisible) 1f else 0f
                            translationY = if (isVisible) 0f else -50f
                        }
                ) {
                    Text(
                        text = "✨ Money Magic",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Let's see where your money is dancing! 💃",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Total Balance Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Total Balance",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (uiState.monthSummary.totalAmount >= 0) "🤑" else "💸",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = if (uiState.monthSummary.totalAmount >= 0) 
                                    "You'll receive" 
                                else 
                                    "You need to pay",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "₹${String.format("%.2f", uiState.monthSummary.totalAmount.absoluteValue)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.monthSummary.totalAmount >= 0)
                                SettledGreen
                            else
                                ExpenseRed
                        )
                        Text(
                            text = if (uiState.monthSummary.totalAmount >= 0)
                                "Great! You're owed money from your friends 🎉"
                            else
                                "Time to settle up with your friends 🤝",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (uiState.monthSummary.percentageChange >= 0)
                                        R.drawable.ic_trending_up
                                    else
                                        R.drawable.ic_trending_down
                                ),
                                contentDescription = "Trend",
                                tint = if (uiState.monthSummary.percentageChange >= 0)
                                    SettledGreen
                                else
                                    ExpenseRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${String.format("%.1f", uiState.monthSummary.percentageChange.absoluteValue)}%",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (uiState.monthSummary.percentageChange >= 0)
                                    SettledGreen
                                else
                                    ExpenseRed
                            )
                            Text(
                                text = "vs last month",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Monthly Expense Chart
            item {
                Text(
                    text = "Your Money Journey 📈",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { 100 })
                ) {
                    if (uiState.monthlyExpenses.isNotEmpty()) {
                        ExpenseChart(
                            dataPoints = uiState.monthlyExpenses,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(380.dp)
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            // Summary Cards
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { 100 })
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SummaryCard(
                            title = "This Week 🎯",
                            summary = uiState.weekSummary,
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = MaterialTheme.shapes.medium,
                                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surface,
                                            PrimaryLight.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                        SummaryCard(
                            title = "This Month 📅",
                            summary = uiState.monthSummary,
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = MaterialTheme.shapes.medium,
                                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                                .clip(MaterialTheme.shapes.medium)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surface,
                                            PrimaryLight.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = MaterialTheme.shapes.medium
                                )
                        )
                    }
                }
            }

            // Top 3 Biggest Expenses
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { 100 })
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "Where did the money go? 🤔",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        uiState.topExpenses.forEachIndexed { index, expense ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = if (index < uiState.topExpenses.size - 1) 16.dp else 0.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = MaterialTheme.shapes.medium,
                                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when(index) {
                                                0 -> "🥇"
                                                1 -> "🥈"
                                                else -> "🥉"
                                            },
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = expense.description,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = when(index) {
                                                    0 -> "Biggest expense"
                                                    1 -> "Second highest"
                                                    else -> "Third highest"
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "•",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = expense.category,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "₹${String.format("%.2f", expense.amount)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = expense.date.format(DateTimeFormatter.ofPattern("MMM d")),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseChart(
    dataPoints: List<ExpenseDataPoint>,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) return

    var selectedPoint by remember { mutableStateOf<ExpenseDataPoint?>(null) }
    val maxAmount = remember(dataPoints) { dataPoints.maxOf { it.amount } }
    val minAmount = remember(dataPoints) { dataPoints.minOf { it.amount } }
    val range = remember(maxAmount, minAmount) { maxAmount - minAmount }
    val monthFormatter = DateTimeFormatter.ofPattern("MMM d")
    val animatedProgress = remember { Animatable(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant

    LaunchedEffect(dataPoints) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dataPoints) {
                    detectTapGestures { offset ->
                        val chartWidth = size.width - 60.dp.toPx()  // Accounting for y-axis space
                        val pointSpacing = chartWidth / (dataPoints.size - 1)
                        val adjustedX = offset.x - 60.dp.toPx()  // Adjusting for y-axis space
                        val index = (adjustedX / pointSpacing).toInt().coerceIn(0, dataPoints.size - 1)
                        selectedPoint = dataPoints[index]
                    }
                }
        ) {
            val yAxisSpace = 60.dp.toPx()
            val xAxisSpace = 40.dp.toPx()
            val chartWidth = size.width - yAxisSpace
            val chartHeight = size.height - xAxisSpace
            val pointSpacing = chartWidth / (dataPoints.size - 1)
            val heightRatio = chartHeight * 0.8f / range

            // Draw grid lines and y-axis labels
            val gridLines = 5
            val gridSpacing = chartHeight / gridLines
            repeat(gridLines + 1) { i ->
                val y = chartHeight - (i * gridSpacing)
                
                // Draw horizontal grid lines
                drawLine(
                    color = onSurfaceColor.copy(alpha = 0.1f),
                    start = Offset(yAxisSpace, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f))
                )

                // Draw y-axis labels
                val amount = minAmount + (range * i / gridLines)
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "₹${amount.toInt()}",
                        4.dp.toPx(),
                        y + 4.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = onSurfaceColor.toArgb()
                            textSize = 12.sp.toPx()
                            textAlign = android.graphics.Paint.Align.LEFT
                            isAntiAlias = true
                        }
                    )
                }
            }

            // Draw y-axis line
            drawLine(
                color = onSurfaceColor.copy(alpha = 0.3f),
                start = Offset(yAxisSpace, 0f),
                end = Offset(yAxisSpace, chartHeight),
                strokeWidth = 1.dp.toPx()
            )

            // Draw x-axis line
            drawLine(
                color = onSurfaceColor.copy(alpha = 0.3f),
                start = Offset(yAxisSpace, chartHeight),
                end = Offset(size.width, chartHeight),
                strokeWidth = 1.dp.toPx()
            )

            // Draw line
            val linePath = Path().apply {
                dataPoints.forEachIndexed { index, point ->
                    val progress = animatedProgress.value
                    val x = yAxisSpace + (index * pointSpacing)
                    val y = chartHeight - ((point.amount - minAmount) * heightRatio * progress)
                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                }
            }
            drawPath(
                path = linePath,
                color = primaryColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw points and date labels
            dataPoints.forEachIndexed { index, point ->
                val progress = animatedProgress.value
                val x = yAxisSpace + (index * pointSpacing)
                val y = chartHeight - ((point.amount - minAmount) * heightRatio * progress)

                // Draw date labels
                if (index == 0 || index == dataPoints.size - 1 || index == dataPoints.size / 2) {
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            point.date.format(monthFormatter),
                            x,
                            size.height - 8.dp.toPx(),
                            android.graphics.Paint().apply {
                                color = onSurfaceColor.toArgb()
                                textSize = 12.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                        )
                    }
                }

                // Draw points
                drawCircle(
                    color = surfaceColor,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        // Tooltip
        selectedPoint?.let { point ->
            Surface(
                modifier = Modifier
                    .padding(4.dp)
                    .shadow(1.dp, RoundedCornerShape(8.dp))
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(8.dp),
                color = surfaceColor
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "₹${String.format("%.2f", point.amount)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = point.date.format(monthFormatter),
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariantColor
                    )
                    if (point.description.isNotEmpty()) {
                        Text(
                            text = point.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = onSurfaceVariantColor
                        )
                    }
                }
            }
        }

        // Trend indicator
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val trend = if (dataPoints.first().amount < dataPoints.last().amount) "📈" else "📉"
            val trendText = if (dataPoints.first().amount < dataPoints.last().amount) {
                "Spending increased"
            } else {
                "Spending decreased"
            }
            Text(
                text = trend,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = trendText,
                style = MaterialTheme.typography.bodySmall,
                color = onSurfaceVariantColor
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    summary: ExpenseSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "₹${String.format("%.2f", summary.totalAmount)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${summary.numberOfTransactions} transactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (summary.percentageChange >= 0)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "${if (summary.percentageChange >= 0) "+" else ""}${summary.percentageChange.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (summary.percentageChange >= 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
