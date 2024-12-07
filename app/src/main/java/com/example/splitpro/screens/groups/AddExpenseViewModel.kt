package com.example.splitpro.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitpro.data.models.GroupMember
import com.example.splitpro.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddExpenseViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()
    private val _members = MutableStateFlow<List<GroupMember>>(emptyList())
    val members: StateFlow<List<GroupMember>> = _members.asStateFlow()

    private val _expenseState = MutableStateFlow<ExpenseState>(ExpenseState.Idle)
    val expenseState: StateFlow<ExpenseState> = _expenseState.asStateFlow()

    sealed class ExpenseState {
        object Idle : ExpenseState()
        object Loading : ExpenseState()
        data class Success(val message: String) : ExpenseState()
        data class Error(val message: String) : ExpenseState()
    }

    fun loadGroupMembers(groupId: String) {
        viewModelScope.launch {
            try {
                val groupDetails = firebaseManager.getGroupDetails(groupId)
                if (groupDetails != null) {
                    _members.value = groupDetails.members
                }
            } catch (e: Exception) {
                println("Error loading group members: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun addExpense(
        groupId: String,
        description: String,
        totalAmount: Double,
        contributors: Map<String, Double>
    ) {
        viewModelScope.launch {
            try {
                _expenseState.value = ExpenseState.Loading
                
                val result = firebaseManager.addExpense(
                    groupId = groupId,
                    description = description,
                    totalAmount = totalAmount,
                    contributors = contributors
                )
                
                result.fold(
                    onSuccess = { expenseId ->
                        println("Expense added successfully with ID: $expenseId")
                        _expenseState.value = ExpenseState.Success("Expense added successfully")
                    },
                    onFailure = { exception ->
                        println("Failed to add expense: ${exception.message}")
                        exception.printStackTrace()
                        _expenseState.value = ExpenseState.Error(exception.message ?: "Failed to add expense")
                    }
                )
            } catch (e: Exception) {
                println("Exception in addExpense: ${e.message}")
                e.printStackTrace()
                _expenseState.value = ExpenseState.Error(e.message ?: "Failed to add expense")
            }
        }
    }
}
