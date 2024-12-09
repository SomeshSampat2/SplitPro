package com.example.splitpro.screens.groups

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitpro.R
import com.example.splitpro.data.models.ExpenseContributor
import com.example.splitpro.data.models.GroupMember
import com.example.splitpro.utils.getColorForName
import com.example.splitpro.ui.theme.Primary
import com.example.splitpro.ui.theme.PrimaryLight
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleUpScreen(
    expenseId: String,
    viewModel: SettleUpViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val expense by viewModel.expense.collectAsState()
    val groupMembers by viewModel.groupMembers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(expenseId) {
        viewModel.loadExpenseDetails(expenseId)
    }
    
    Box(
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
            )
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("Settle Up", maxLines = 1) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Primary,
                        navigationIconContentColor = Primary
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                expense?.let { currentExpense ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Expense Details Card
                        item {
                            ExpenseDetailsCard(
                                amount = currentExpense.amount,
                                description = currentExpense.description,
                                paidBy = groupMembers.find { it.userId == currentExpense.createdBy }?.name ?: ""
                            )
                        }
                        
                        // Section Title with subtle background
                        item {
                            Text(
                                text = "Contributors",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Primary,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        
                        // Contributors List
                        items(currentExpense.contributors) { contributor ->
                            ContributorSettleCard(
                                contributor = contributor,
                                member = groupMembers.find { it.userId == contributor.userId },
                                paidByUserId = currentExpense.createdBy,
                                onSettleUp = { viewModel.settleUpExpense(contributor.userId) }
                            )
                        }
                        
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseDetailsCard(
    amount: Double,
    description: String,
    paidBy: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
            ),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Amount with larger text
                Text(
                    text = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                        .format(amount),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary
                )
                
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .alpha(0.1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_money_send),
                        contentDescription = null,
                        tint = Primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Paid by $paidBy",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ContributorSettleCard(
    contributor: ExpenseContributor,
    member: GroupMember?,
    paidByUserId: String,
    onSettleUp: () -> Unit
) {
    if (member == null) return
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Member Avatar
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .shadow(4.dp, CircleShape),
                    color = getColorForName(member.name)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = member.name.first().toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = member.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Primary
                    )
                    Text(
                        text = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
                            .format(contributor.amount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            if (member.userId != paidByUserId) {
                Button(
                    onClick = onSettleUp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = Primary.copy(alpha = 0.25f)
                        )
                ) {
                    Text(
                        text = "Settle",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
