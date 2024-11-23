package com.example.splitpro.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Group(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val balance: Double = 0.0,
    val lastActivity: Long = System.currentTimeMillis()
)

data class Expense(
    val id: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val paidBy: String = "",
    val date: Long = System.currentTimeMillis(),
    val groupId: String? = null
)

sealed class HomeState {
    object Loading : HomeState()
    data class Success(
        val userName: String = "",
        val groups: List<Group> = emptyList(),
        val recentExpenses: List<Expense> = emptyList(),
        val totalBalance: Double = 0.0,
        val hasNotifications: Boolean = false
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                val userDoc = firestore.collection("Users").document(userId).get().await()
                
                val userName = userDoc.getString("name") ?: ""
                
                // Load groups (mock data for now)
                val groups = listOf(
                    Group("1", "Weekend Trip", listOf("user1", "user2"), 150.0),
                    Group("2", "Roommates", listOf("user1", "user3"), -75.0),
                    Group("3", "Lunch Group", listOf("user1", "user4"), 30.0)
                )
                
                // Load expenses (mock data for now)
                val expenses = listOf(
                    Expense("1", "Groceries", 50.0, "user1"),
                    Expense("2", "Movie tickets", 30.0, "user2"),
                    Expense("3", "Dinner", 75.0, "user1")
                )
                
                _state.value = HomeState.Success(
                    userName = userName,
                    groups = groups,
                    recentExpenses = expenses,
                    totalBalance = 105.0,
                    hasNotifications = true
                )
            } catch (e: Exception) {
                _state.value = HomeState.Error("Failed to load data: ${e.message}")
            }
        }
    }
}
