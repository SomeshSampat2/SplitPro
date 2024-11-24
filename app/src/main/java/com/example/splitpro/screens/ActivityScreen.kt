package com.example.splitpro.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitpro.R
import com.example.splitpro.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
        ) {
            TopAppBar(
                title = {
                    Text(
                        "Activity",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer {
                            alpha = if (isVisible) 1f else 0f
                            translationY = if (isVisible) 0f else -50f
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.graphicsLayer {
                            alpha = if (isVisible) 1f else 0f
                            translationX = if (isVisible) 0f else -50f
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                itemsIndexed(state.activities) { index, activity ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = index * 50
                            )
                        ) + slideInHorizontally(
                            initialOffsetX = { 100 },
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = index * 50
                            )
                        )
                    ) {
                        Column {
                            ActivityItem(
                                activity = activity,
                                viewModel = viewModel
                            )
                            if (activity != state.activities.last()) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItem(
    activity: ActivityItem,
    viewModel: ActivityViewModel
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with animation
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = getActivityColor(activity.type).copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val infiniteTransition = rememberInfiniteTransition()
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Icon(
                painter = painterResource(id = getActivityIcon(activity.type)),
                contentDescription = null,
                tint = getActivityColor(activity.type),
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
            )
        }

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = "in ${activity.groupName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                activity.amount?.let { amount ->
                    Text(
                        text = "₹%.2f".format(amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPositiveAmount(activity.type)) {
                            SettledGreen
                        } else {
                            ExpenseRed
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = activity.involvedUser,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = viewModel.formatTimestamp(activity.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getActivityIcon(type: ActivityType): Int {
    return when (type) {
        ActivityType.EXPENSE_ADDED -> R.drawable.ic_receipt
        ActivityType.PAYMENT_SENT -> R.drawable.ic_arrow_outward
        ActivityType.PAYMENT_RECEIVED -> R.drawable.ic_arrow_inward
        ActivityType.GROUP_CREATED -> R.drawable.ic_group
        ActivityType.MEMBER_ADDED -> R.drawable.ic_person_add
        ActivityType.EXPENSE_SETTLED -> R.drawable.ic_check_circle
    }
}

private fun getActivityColor(type: ActivityType): Color {
    return when (type) {
        ActivityType.EXPENSE_ADDED -> Warning
        ActivityType.PAYMENT_SENT -> ExpenseRed
        ActivityType.PAYMENT_RECEIVED -> SettledGreen
        ActivityType.GROUP_CREATED -> Primary
        ActivityType.MEMBER_ADDED -> Secondary
        ActivityType.EXPENSE_SETTLED -> Success
    }
}

private fun isPositiveAmount(type: ActivityType): Boolean {
    return when (type) {
        ActivityType.PAYMENT_RECEIVED -> true
        ActivityType.EXPENSE_SETTLED -> true
        else -> false
    }
}
