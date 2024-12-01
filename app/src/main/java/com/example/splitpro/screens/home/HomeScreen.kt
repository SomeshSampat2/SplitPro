package com.example.splitpro.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                            )
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
                            )
                            .padding(top = 16.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Home",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Let's see where your money is dancing!",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        letterSpacing = 0.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Quick Action Buttons Section
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + slideInHorizontally()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionButton(
                            icon = R.drawable.ic_payment,
                            label = "Add\nExpense",
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = R.drawable.ic_split,
                            label = "Split\nBill",
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            icon = R.drawable.ic_stats,
                            label = "View\nStats",
                            onClick = { /* TODO */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
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
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "₹${String.format("%.2f", uiState.monthSummary.totalAmount.absoluteValue)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = if (uiState.monthSummary.totalAmount >= 0)
                                        SettledGreen
                                    else
                                        ExpenseRed
                                )
                                Text(
                                    text = if (uiState.monthSummary.totalAmount >= 0) 
                                        "to receive"
                                    else 
                                        "to pay",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { /* TODO */ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                                    contentDescription = "View details",
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
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
                                .height(220.dp)
                                .padding(start = 8.dp, end = 16.dp)
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
                    SummarySection(
                        weekSummary = uiState.weekSummary,
                        monthSummary = uiState.monthSummary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                    )
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
                            .padding(horizontal = 16.dp)
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
            .padding(start = 8.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(dataPoints) {
                    detectTapGestures { offset ->
                        val chartWidth = size.width - 40.dp.toPx()  // Reduced y-axis space
                        val pointSpacing = chartWidth / (dataPoints.size - 1)
                        val adjustedX = offset.x - 40.dp.toPx()  // Adjusted for new y-axis space
                        val index = (adjustedX / pointSpacing).toInt().coerceIn(0, dataPoints.size - 1)
                        selectedPoint = dataPoints[index]
                    }
                }
        ) {
            val yAxisSpace = 40.dp.toPx() // Reduced from 60.dp
            val xAxisSpace = 24.dp.toPx() // Reduced from 40.dp
            val chartWidth = size.width - yAxisSpace
            val chartHeight = size.height - xAxisSpace
            val pointSpacing = chartWidth / (dataPoints.size - 1)
            val heightRatio = chartHeight * 0.9f / range // Increased from 0.8f

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

                // Draw y-axis labels with smaller text
                val amount = minAmount + (range * i / gridLines)
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "₹${amount.toInt()}",
                        2.dp.toPx(),
                        y + 4.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = onSurfaceColor.toArgb()
                            textSize = 10.sp.toPx() // Reduced from 12.sp
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

                // Draw date labels (only for first, middle, and last points)
                if (index == 0 || index == dataPoints.size - 1 || index == dataPoints.size / 2) {
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            point.date.format(monthFormatter),
                            x,
                            size.height - 2.dp.toPx(),
                            android.graphics.Paint().apply {
                                color = onSurfaceColor.toArgb()
                                textSize = 10.sp.toPx() // Reduced from 12.sp
                                textAlign = android.graphics.Paint.Align.CENTER
                                isAntiAlias = true
                            }
                        )
                    }
                }

                // Draw points
                drawCircle(
                    color = surfaceColor,
                    radius = 4.dp.toPx(), // Reduced from 5.dp
                    center = Offset(x, y)
                )
                drawCircle(
                    color = primaryColor,
                    radius = 3.dp.toPx(), // Reduced from 4.dp
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
private fun QuickActionButton(
    @DrawableRes icon: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(80.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
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
    Surface(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
            )
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_forward),
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "₹${summary.totalAmount}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${summary.numberOfTransactions} transactions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SummarySection(
    weekSummary: ExpenseSummary,
    monthSummary: ExpenseSummary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Expense Summary",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SummaryCard(
                title = "This Week",
                summary = weekSummary,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "This Month",
                summary = monthSummary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
