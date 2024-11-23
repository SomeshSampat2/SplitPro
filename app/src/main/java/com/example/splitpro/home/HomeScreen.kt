package com.example.splitpro.home

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.sharp.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.splitpro.ui.components.SplitProBottomNavigation
import com.example.splitpro.ui.theme.*
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (state) {
                        is HomeState.Success -> Text(
                            text = "Welcome, ${(state as HomeState.Success).userName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        else -> Text("SplitPro")
                    }
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Box {
                            IconButton(onClick = { /* TODO: Show notifications */ }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications"
                                )
                            }
                            val currentState = state
                            if (currentState is HomeState.Success && currentState.hasNotifications) {
                                Badge(
                                    modifier = Modifier.offset(x = 16.dp, y = (-4).dp),
                                    containerColor = Secondary
                                ) {
                                    Text("!")
                                }
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            SplitProBottomNavigation(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add expense */ },
                containerColor = Primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Expense")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is HomeState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary
                    )
                }
                is HomeState.Success -> {
                    val homeState = state as HomeState.Success
                    val listState = rememberLazyListState()
                    
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface,
                                        PrimaryLight.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp,
                            bottom = 80.dp
                        ),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Total Balance Card
                        item(key = "total_balance") {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = Primary
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Total Balance",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White
                                    )
                                    Text(
                                        formatAmount(homeState.totalBalance),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Quick Actions
                        item(key = "quick_actions") {
                            val rowState = rememberLazyListState()
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                state = rowState
                            ) {
                                item(key = "new_group") {
                                    QuickActionButton(
                                        icon = Icons.Default.AddCircle,
                                        label = "New Group",
                                        onClick = { /* TODO */ }
                                    )
                                }
                                item(key = "settle_up") {
                                    QuickActionButton(
                                        icon = Icons.Filled.Build,
                                        label = "Settle Up",
                                        onClick = { /* TODO */ }
                                    )
                                }
                                item(key = "reports") {
                                    QuickActionButton(
                                        icon = Icons.Sharp.Warning,
                                        label = "Reports",
                                        onClick = { /* TODO */ }
                                    )
                                }
                            }
                        }

                        // Groups Section
                        stickyHeader(key = "groups_header") {
                            Text(
                                "Your Groups",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(vertical = 8.dp)
                            )
                        }
                        
                        items(
                            items = homeState.groups,
                            key = { "group_${it.id}" }
                        ) { group ->
                            key(group.id) {
                                GroupCard(
                                    group = group,
                                    modifier = Modifier.animateItemPlacement()
                                )
                            }
                        }

                        // Recent Expenses Section
                        stickyHeader(key = "expenses_header") {
                            Text(
                                "Recent Expenses",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(vertical = 8.dp)
                            )
                        }
                        
                        items(
                            items = homeState.recentExpenses,
                            key = { "expense_${it.id}" }
                        ) { expense ->
                            key(expense.id) {
                                ExpenseCard(
                                    expense = expense,
                                    modifier = Modifier.animateItemPlacement()
                                )
                            }
                        }
                    }
                }
                is HomeState.Error -> {
                    Text(
                        (state as HomeState.Error).message,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .size(24.dp)
                    .padding(bottom = 4.dp),
                tint = Primary
            )
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun GroupCard(
    group: Group,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { /* TODO: Open group details */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    group.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${group.members.size} members",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Text(
                formatAmount(group.balance),
                style = MaterialTheme.typography.titleMedium,
                color = if (group.balance >= 0) Primary else Secondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ExpenseCard(
    expense: Expense,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { /* TODO: Open expense details */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PrimaryLight.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        tint = Primary
                    )
                }
                Column {
                    Text(
                        expense.description,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Paid by ${expense.paidBy}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Text(
                formatAmount(expense.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatAmount(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(amount)
}
