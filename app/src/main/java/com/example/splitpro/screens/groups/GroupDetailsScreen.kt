package com.example.splitpro.screens.groups

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitpro.R
import com.example.splitpro.data.models.Expense
import com.example.splitpro.data.models.GroupMember
import com.example.splitpro.firebase.FirebaseManager
import com.example.splitpro.utils.getColorForName
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailsScreen(
    groupId: String,
    viewModel: GroupDetailsViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onAddMember: () -> Unit,
    onAddExpense: () -> Unit,
    onSettleUpExpense: (String) -> Unit
) {
    val details by viewModel.groupDetails.collectAsState()
    val expenses by viewModel.expenses.collectAsState()
    val currentUserId = FirebaseManager.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(groupId)
//        viewModel.loadExpenses(groupId)
    }

    details?.let { groupDetails ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = groupDetails.name) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onAddMember) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person_add),
                                contentDescription = "Add Member"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ExtendedFloatingActionButton(
                        modifier = Modifier
                            .padding(bottom = 32.dp),
                        onClick = onAddExpense,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_payment),
                                contentDescription = null
                            )
                        },
                        text = { Text("Add Expense") }
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Group Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = groupDetails.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${groupDetails.members.size} members",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Members Section
                item {
                    if (groupDetails.members.isEmpty()) {
                        EmptyMembersSection(onAddMember)
                    } else {
                        MembersSection(
                            members = groupDetails.members,
                            onAddMember = onAddMember
                        )
                    }
                }

                // Expenses List
                item {
                    if (expenses.isNotEmpty()) {
                        Text(
                            text = "Recent Expenses",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                
                items(expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        currentUserId = currentUserId,
                        members = groupDetails.members,
                        onClick = { onSettleUpExpense(expense.id) }
                    )
                }
                
                if (expenses.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No expenses yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupMembersRow(
    members: List<GroupMember>,
    onAddMember: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Members",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(members) { member ->
                MemberAvatar(member = member)
            }
            
            item {
                AddMemberButton(onClick = onAddMember)
            }
        }
    }
}

@Composable
fun MemberAvatar(member: GroupMember) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(getColorForName(member.name))
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = member.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AddMemberButton(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_person_add),
                contentDescription = "Add Member",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Add",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptyEntriesSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Expenses Yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your first expense to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun EmptyMembersSection(onAddMember: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Members Yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Invite members to your group",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(
                onClick = { onAddMember() },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_add),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Members")
            }
        }
    }
}

@Composable
private fun EntriesSection(entries: List<Expense>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Recent Expenses",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        val groupedEntries = entries.groupBy {
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it.createdAt)
        }

        groupedEntries.forEach { (date, dateEntries) ->
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            dateEntries.forEach { entry ->
                EntryItem(entry = entry)
                if (dateEntries.last() != entry) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 88.dp, end = 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun EntryItem(entry: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            ) { /* TODO: Handle entry click */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(getColorForName(entry.createdBy)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.createdBy.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.description,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_payment),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "₹${String.format("%.2f", entry.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = " • ${entry.createdAt}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when {
//                    entry.yourShare > 0 -> "You get back ₹${String.format("%.2f", entry.yourShare)}"
//                    entry.yourShare < 0 -> "You owe ₹${String.format("%.2f", -entry.yourShare)}"
                    //todo
                    else -> "No share"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when {
//                    entry.yourShare > 0 -> MaterialTheme.colorScheme.primary
//                    entry.yourShare < 0 -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MembersSection(members: List<GroupMember>, onAddMember: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Members",
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = { onAddMember() },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_add),
                    contentDescription = "Add Members",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        members.firstOrNull()?.let { firstMember ->
            MemberItem(
                member = firstMember,
                expanded = expanded,
                onExpandClick = { expanded = !expanded }
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp, top = 8.dp)
            ) {
                members.drop(1).forEach { member ->
                    MemberItem(member = member)
                }
            }
        }
    }
}

@Composable
private fun MemberItem(
    member: GroupMember,
    expanded: Boolean? = null,
    onExpandClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onExpandClick != null) {
                    Modifier.clickable { onExpandClick() }
                } else {
                    Modifier
                }
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(getColorForName(member.name)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.name.first().toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.surface,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = when {
                    member.balance > 0 -> "You get back ₹${String.format("%.2f", member.balance)}"
                    member.balance < 0 -> "You owe ${member.name} ₹${String.format("%.2f", -member.balance)}"
                    else -> "You are settled up with ${member.name}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    member.balance > 0 -> MaterialTheme.colorScheme.primary
                    member.balance < 0 -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
        expanded?.let {
            Icon(
                painter = painterResource(
                    id = if (it) R.drawable.ic_expand_up else R.drawable.ic_expand_down
                ),
                contentDescription = if (it) "Collapse" else "Expand",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExpenseItem(
    expense: Expense,
    currentUserId: String,
    members: List<GroupMember>,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
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
                Column {
                    Text(
                        text = expense.description,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Paid by ${members.find { it.userId == expense.createdBy }?.name ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Text(
                    text = "₹${expense.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (expense.createdBy == currentUserId) {
                    val contribution = expense.contributors.find { it.userId == currentUserId }?.amount ?: 0.0
                    "You'll receive ₹${expense.amount - contribution}"
                } else {
                    val contribution = expense.contributors.find { it.userId == currentUserId }?.amount ?: 0.0
                    "Your share: ₹$contribution"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (expense.createdBy == currentUserId)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
            
            Text(
                text = expense.createdAt.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
