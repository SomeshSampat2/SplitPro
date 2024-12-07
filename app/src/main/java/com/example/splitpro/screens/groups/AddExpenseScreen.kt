package com.example.splitpro.screens.groups

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitpro.R
import com.example.splitpro.data.models.GroupMember
import com.example.splitpro.ui.theme.*
import com.example.splitpro.utils.getColorForName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    viewModel: AddExpenseViewModel = viewModel()
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var splitEqually by remember { mutableStateOf(true) }
    var selectAll by remember { mutableStateOf(false) }
    var selectedMembers by remember { mutableStateOf(setOf<String>()) }
    var customAmounts by remember { mutableStateOf(mutableMapOf<String, String>()) }
    var isVisible by remember { mutableStateOf(false) }
    
    val expenseState by viewModel.expenseState.collectAsState()
    val members by viewModel.members.collectAsState()
    
    LaunchedEffect(Unit) {
        isVisible = true
        viewModel.loadGroupMembers(groupId)
    }

    LaunchedEffect(expenseState) {
        when (expenseState) {
            is AddExpenseViewModel.ExpenseState.Success -> {
                println("Expense added successfully, navigating back") // Debug log
                onNavigateBack()
            }
            is AddExpenseViewModel.ExpenseState.Error -> {
                println("Error adding expense: ${(expenseState as AddExpenseViewModel.ExpenseState.Error).message}") // Debug log
            }
            else -> {}
        }
    }
    
    LaunchedEffect(selectAll) {
        if (selectAll) {
            selectedMembers = members.map { it.userId }.toSet()
        } else if (selectedMembers.size == members.size) {
            selectedMembers = emptySet()
        }
    }

    // Calculate remaining amount for custom splits
    val totalAmount = amount.toDoubleOrNull() ?: 0.0
    val usedAmount = customAmounts.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
    val remainingAmount = totalAmount - usedAmount

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Text(
                                "New Expense",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                state = rememberLazyListState()
            ) {
                // Main Input Section
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Amount Input
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "Amount",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                ) {
                                    TextField(
                                        value = amount,
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                                amount = newValue
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_payment),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        },
                                        prefix = { 
                                            Text(
                                                "₹",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            ) 
                                        },
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction = ImeAction.Next
                                        ),
                                        singleLine = true,
                                        colors = TextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = MaterialTheme.colorScheme.primary
                                        ),
                                        placeholder = {
                                            Text(
                                                "Enter amount",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        }
                                    )
                                }
                            }
                            
                            // Description Input
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "Description",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                                ) {
                                    TextField(
                                        value = description,
                                        onValueChange = { description = it },
                                        modifier = Modifier.fillMaxWidth(),
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_event),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        },
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        singleLine = true,
                                        colors = TextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = MaterialTheme.colorScheme.primary
                                        ),
                                        placeholder = {
                                            Text(
                                                "What's this expense for?",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Split Options
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it * 2 }),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Split Options",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ElevatedFilterChip(
                                    selected = splitEqually,
                                    onClick = { splitEqually = !splitEqually },
                                    label = { Text("Split Equally") },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(
                                                id = if (splitEqually) R.drawable.ic_check_circle
                                                else R.drawable.ic_circle
                                            ),
                                            contentDescription = null
                                        )
                                    }
                                )
                                
                                ElevatedFilterChip(
                                    selected = selectAll,
                                    onClick = { 
                                        selectAll = !selectAll
                                        if (selectAll) {
                                            selectedMembers = members.map { it.userId }.toSet()
                                        } else {
                                            selectedMembers = emptySet()
                                        }
                                    },
                                    label = { Text("Select All") },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(
                                                id = if (selectAll) R.drawable.ic_check_circle
                                                else R.drawable.ic_circle
                                            ),
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Members Section
                item {
                    Text(
                        "Split With",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                    )
                }
                
                // Members List
                items(
                    items = members,
                    key = { it.userId } // Add key for better performance
                ) { member ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it * 3 }),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (selectedMembers.contains(member.userId))
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedMembers.contains(member.userId),
                                    onCheckedChange = { checked ->
                                        selectedMembers = if (checked) {
                                            selectedMembers + member.userId
                                        } else {
                                            selectedMembers - member.userId
                                        }
                                        selectAll = selectedMembers.size == members.size
                                    }
                                )
                                
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(getColorForName(member.name)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.name.take(1).uppercase(),
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                                
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = member.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    
                                    if (amount.isNotEmpty() && splitEqually && selectedMembers.contains(member.userId)) {
                                        val splitAmount = try {
                                            amount.toDouble() / selectedMembers.size
                                        } catch (e: NumberFormatException) {
                                            0.0
                                        }
                                        Text(
                                            text = "₹%.2f".format(splitAmount),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                
                                if (!splitEqually) {
                                    Column {
                                        TextField(
                                            value = customAmounts[member.userId] ?: "",
                                            onValueChange = { newValue ->
                                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                                    val newAmount = newValue.toDoubleOrNull() ?: 0.0
                                                    val otherAmounts = customAmounts
                                                        .filter { it.key != member.userId }
                                                        .values
                                                        .sumOf { it.toDoubleOrNull() ?: 0.0 }
                                                    
                                                    // Only update if the new total wouldn't exceed the total amount
                                                    if (newAmount + otherAmounts <= totalAmount) {
                                                        customAmounts = customAmounts.toMutableMap().apply {
                                                            put(member.userId, newValue)
                                                        }
                                                    }
                                                }
                                            },
                                            enabled = selectedMembers.contains(member.userId),
                                            prefix = { Text("₹") },
                                            modifier = Modifier.width(100.dp),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = TextFieldDefaults.colors(
                                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                                disabledContainerColor = MaterialTheme.colorScheme.surface
                                            ),
                                            singleLine = true,
                                            placeholder = {
                                                Text(
                                                    "Enter amount",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                                )
                                            }
                                        )
                                        
                                        if (selectedMembers.contains(member.userId)) {
                                            val currentAmount = customAmounts[member.userId]?.toDoubleOrNull() ?: 0.0
                                            if (currentAmount > 0) {
                                                Text(
                                                    "${((currentAmount / totalAmount) * 100).toInt()}% of total",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Add Expense Button
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it * 2 }),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        FilledTonalButton(
                            onClick = {
                                println("Add Expense button clicked") // Debug log
                                val totalAmount = amount.toDoubleOrNull()
                                if (totalAmount == null) {
                                    println("Invalid amount: $amount") // Debug log
                                    return@FilledTonalButton
                                }
                                
                                val contributors = if (splitEqually) {
                                    println("Using equal split for ${selectedMembers.size} members") // Debug log
                                    val splitAmount = totalAmount / selectedMembers.size
                                    selectedMembers.associateWith { splitAmount }
                                } else {
                                    println("Using custom split") // Debug log
                                    selectedMembers.associateWith { userId ->
                                        customAmounts[userId]?.toDoubleOrNull() ?: 0.0
                                    }.filter { (_, amount) -> amount > 0 }
                                }
                                
                                if (contributors.isEmpty()) {
                                    println("No valid contributors found") // Debug log
                                    return@FilledTonalButton
                                }
                                
                                println("Adding expense: amount=$totalAmount, description=$description, contributors=$contributors") // Debug log
                                viewModel.addExpense(
                                    groupId = groupId,
                                    description = description.trim(),
                                    totalAmount = totalAmount,
                                    contributors = contributors
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = description.isNotEmpty() && 
                                     amount.isNotEmpty() && 
                                     selectedMembers.isNotEmpty() &&
                                     (splitEqually || customAmounts.values.any { it.isNotEmpty() }),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                            )
                        ) {
                            if (expenseState is AddExpenseViewModel.ExpenseState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_payment),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Text(
                                        "Add Expense",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Add a summary of split at the bottom
                item {
                    if (!splitEqually && amount.isNotEmpty() && selectedMembers.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Split Summary",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total Amount: ₹$totalAmount",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Remaining: ₹${"%.2f".format(remainingAmount)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (remainingAmount > 0) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            LinearProgressIndicator(
                                progress = if (totalAmount > 0) (usedAmount / totalAmount).toFloat() else 0f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
