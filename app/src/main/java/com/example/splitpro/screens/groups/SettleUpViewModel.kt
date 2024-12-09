package com.example.splitpro.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitpro.data.models.Expense
import com.example.splitpro.data.models.GroupMember
import com.example.splitpro.firebase.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettleUpViewModel : ViewModel() {
    private val firebaseManager = FirebaseManager.getInstance()
    
    private val _expense = MutableStateFlow<Expense?>(null)
    val expense: StateFlow<Expense?> = _expense.asStateFlow()
    
    private val _groupMembers = MutableStateFlow<List<GroupMember>>(emptyList())
    val groupMembers: StateFlow<List<GroupMember>> = _groupMembers.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadExpenseDetails(expenseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get expense details
                val expenses = firebaseManager.getGroupExpenses(listOf(expenseId))
                val expense = expenses.firstOrNull()
                _expense.value = expense
                
                // Load group members if expense exists
                if (expense != null) {
                    val groupDetails = firebaseManager.getGroupDetails(expense.groupId)
                    _groupMembers.value = groupDetails?.members ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun settleUpExpense(memberId: String) {
        viewModelScope.launch {
            val currentExpense = _expense.value
            if (currentExpense == null) {
                return@launch
            }
            
            try {
                // TODO: Implement settle up logic
                // This will be implemented in the next step
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
